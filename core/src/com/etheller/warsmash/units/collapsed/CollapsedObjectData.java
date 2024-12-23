package com.etheller.warsmash.units.collapsed;

import java.util.List;
import java.util.Map;

import com.etheller.warsmash.units.GameObject;
import com.etheller.warsmash.units.ObjectData;
import com.etheller.warsmash.units.custom.Change;
import com.etheller.warsmash.units.custom.ObjectDataChangeEntry;
import com.etheller.warsmash.units.custom.War3ObjectDataChangeset;
import com.etheller.warsmash.units.manager.MutableObjectData.WorldEditorDataType;
import com.etheller.warsmash.util.War3ID;
import com.etheller.warsmash.util.WorldEditStrings;

public class CollapsedObjectData {
	/**
	 * 应用一组更改到游戏对象的数据中。
	 *
	 * @param worldEditStrings    用于字符串转换的 WorldEditStrings 对象。
	 * @param worldEditorDataType 编辑器数据的类型。
	 * @param sourceSLKData       源 SLK 数据对象。
	 * @param sourceSLKMetaData   源 SLK 元数据对象。
	 * @param editorData          包含更改信息的 War3ObjectDataChangeset 对象。
	 */
	public static void apply(final WorldEditStrings worldEditStrings, final WorldEditorDataType worldEditorDataType,
							 final ObjectData sourceSLKData, final ObjectData sourceSLKMetaData,
							 final War3ObjectDataChangeset editorData) {
		// 如果编辑器数据类型是 ABILITIES
		if (worldEditorDataType == WorldEditorDataType.ABILITIES) {
			// 遍历源 SLK 数据中的每个键
			for (final String originalKey : sourceSLKData.keySet()) {
				// 获取原始键对应的游戏对象
				final GameObject originalObject = sourceSLKData.get(originalKey);
				// 获取游戏对象的 "code" 字段值
				final String code = originalObject.getFieldAsString("code", 0);
				// 如果 "code" 字段值不为空
				if (code != null) {
					// 从源 SLK 数据中获取 "code" 字段值对应的游戏对象
					final GameObject codeObject = sourceSLKData.get(code);
					// 如果找到了对应的游戏对象
					if (codeObject != null) {
						// 将原始游戏对象的属性继承自 "code" 游戏对象
						sourceSLKData.inheritFrom(originalKey, code);
					}
				}
			}
		}

		// 遍历编辑器数据中的每个自定义更改项
		for (final Map.Entry<War3ID, ObjectDataChangeEntry> entry : editorData.getCustom()) {
			// 获取更改项的单位 ID
			final War3ID unitId = entry.getKey();
			// 获取更改项的详细信息
			final ObjectDataChangeEntry unitChanges = entry.getValue();
			// 获取更改项的旧 ID
			final War3ID oldId = unitChanges.getOldId();
			// 获取更改项的新 ID
			final War3ID newId = unitChanges.getNewId();
			// 将新 ID 转换为字符串
			final String unitIdString = newId.toString();
			// 在源 SLK 数据中克隆一个单位，从旧 ID 到新 ID
			sourceSLKData.cloneUnit(oldId.asStringValue(), newId.asStringValue());
			// 从源 SLK 数据中获取新 ID 对应的游戏对象
			final GameObject gameObject = sourceSLKData.get(unitIdString);
			// 如果找到了对应的游戏对象
			if (gameObject != null) {
				// 遍历更改项中的每个元数据更改
				for (final Map.Entry<War3ID, List<Change>> changeEntry : unitChanges.getChanges()) {
					// 获取元数据更改的键
					final War3ID metaKey = changeEntry.getKey();
					// 获取元数据更改的列表
					final List<Change> changes = changeEntry.getValue();
					// 从源 SLK 元数据中获取元数据键对应的游戏对象
					final GameObject metaDataField = sourceSLKMetaData.get(metaKey.asStringValue());
					// 如果没有找到对应的元数据游戏对象
					if (metaDataField == null) {
						// 打印错误信息
						System.err.println("UNKNOWN META DATA FIELD: " + metaKey + " on " + unitId);
						// 继续下一个更改项
						continue;
					}
					// 应用更改到游戏对象
					applyChange(gameObject, changes, metaDataField);
				}
			}
		}

		// 遍历编辑器数据中的每个原始更改项
		for (final Map.Entry<War3ID, ObjectDataChangeEntry> entry : editorData.getOriginal()) {
			// 获取更改项的单位 ID
			final War3ID unitId = entry.getKey();
			// 获取更改项的详细信息
			final ObjectDataChangeEntry unitChanges = entry.getValue();
			// 获取更改项的旧 ID
			final War3ID oldId = unitChanges.getOldId();
			// 获取更改项的新 ID
			final War3ID newId = unitChanges.getNewId();
			// 将旧 ID 转换为字符串
			final String unitIdString = oldId.toString();
			// 从源 SLK 数据中获取旧 ID 对应的游戏对象
			final GameObject gameObject = sourceSLKData.get(unitIdString);
			// 如果找到了对应的游戏对象
			if (gameObject != null) {
				// 遍历更改项中的每个元数据更改
				for (final Map.Entry<War3ID, List<Change>> changeEntry : unitChanges.getChanges()) {
					// 获取元数据更改的键
					final War3ID metaKey = changeEntry.getKey();
					// 获取元数据更改的列表
					final List<Change> changes = changeEntry.getValue();
					// 从源 SLK 元数据中获取元数据键对应的游戏对象
					final GameObject metaDataField = sourceSLKMetaData.get(metaKey.asStringValue());
					// 如果没有找到对应的元数据游戏对象
					if (metaDataField == null) {
						// 打印错误信息
						System.err.println("UNKNOWN META DATA FIELD: " + metaKey + " on " + unitId);
						// 继续下一个更改项
						continue;
					}
					// 应用更改到游戏对象
					applyChange(gameObject, changes, metaDataField);
				}
			}
		}

		// 解析源 SLK 数据中名称字段的字符串引用
		resolveStringReferencesInNames(worldEditStrings, sourceSLKData);
	}


	private static void applyChange(final GameObject gameObject, final List<Change> changes,
			final GameObject metaDataField) {
		for (final Change change : changes) {
			int level = change.getLevel();
			final String slk = metaDataField.getField("slk");
			int index = metaDataField.getFieldValue("index");
			String metaDataName = metaDataField.getField("field");
			final int repeatCount = metaDataField.getFieldValue("repeat");
			final String appendIndexMode = metaDataField.getField("appendIndex");
			final int data = metaDataField.getFieldValue("data");
			if (data > 0) {
				metaDataName += (char) ('A' + (data - 1));
			}
			if (repeatCount > 0) {
				switch (appendIndexMode) {
				case "0": {
					index = level - 1;
					break;
				}
				case "1": {
					final int upgradeExtensionLevel = level - 1;
					if (upgradeExtensionLevel > 0) {
						metaDataName += Integer.toString(upgradeExtensionLevel);
					}
					break;
				}
				default:
				case "": {
					if ((index == -1) || (repeatCount >= 10)) {
						if (level == 0) {
							level = 1;
						}
						if (repeatCount >= 10) {
							metaDataName += String.format("%2d", level).replace(' ', '0');
						}
						else {
							metaDataName += Integer.toString(level);
						}
					}
					else {
						index = level - 1;
					}
					break;
				}
				}
			}
			final String slkKey = metaDataName;
			switch (change.getVartype()) {
			case War3ObjectDataChangeset.VAR_TYPE_BOOLEAN: {
				gameObject.setField(slk, slkKey, Integer.toString(change.getLongval()), index);
				break;
			}
			case War3ObjectDataChangeset.VAR_TYPE_INT: {
				gameObject.setField(slk, slkKey, Integer.toString(change.getLongval()), index);
				break;
			}
			case War3ObjectDataChangeset.VAR_TYPE_REAL: {
				gameObject.setField(slk, slkKey, Float.toString(change.getRealval()), index);
				break;
			}
			case War3ObjectDataChangeset.VAR_TYPE_STRING: {
				final String fieldValue = change.getStrval();
				if (index == -1) {
					gameObject.clearFieldList(slk, slkKey);
					final int indexOfComma = fieldValue.indexOf(",");
					if (indexOfComma != -1) {
						final String[] splitLine = fieldValue.split(",");
						for (int splitChunkId = 0; splitChunkId < splitLine.length; splitChunkId++) {
							gameObject.setField(slk, slkKey, splitLine[splitChunkId], splitChunkId);
						}
					}
					else {
						gameObject.setField(slk, slkKey, fieldValue, index);
					}
				}
				else {
					gameObject.setField(slk, slkKey, fieldValue, index);
				}
				break;
			}
			case War3ObjectDataChangeset.VAR_TYPE_UNREAL: {
				gameObject.setField(slk, slkKey, Float.toString(change.getRealval()), index);
				break;
			}
			default:
				throw new IllegalStateException("Unsupported type: " + change.getVartype());
			}
		}
	}

	private static void resolveStringReferencesInNames(final WorldEditStrings worldEditStrings,
			final ObjectData sourceSLKData) {
		for (final String key : sourceSLKData.keySet()) {
			final GameObject gameObject = sourceSLKData.get(key);
			String name = gameObject.getField("Name");
			final String suffix = gameObject.getField("EditorSuffix");
			if (name.startsWith("WESTRING")) {
				if (!name.contains(" ")) {
					name = worldEditStrings.getString(name);
				}
				else {
					final String[] names = name.split(" ");
					name = "";
					for (final String subName : names) {
						if (name.length() > 0) {
							name += " ";
						}
						if (subName.startsWith("WESTRING")) {
							name += worldEditStrings.getString(subName);
						}
						else {
							name += subName;
						}
					}
				}
				if (name.startsWith("\"") && name.endsWith("\"")) {
					name = name.substring(1, name.length() - 1);
				}
				gameObject.setField("Name", name);
			}
			if (suffix.startsWith("WESTRING")) {
				gameObject.setField("EditorSuffix", worldEditStrings.getString(suffix));
			}
		}
	}
}
