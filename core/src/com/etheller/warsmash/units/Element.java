package com.etheller.warsmash.units;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
// Element类，用于表示游戏中的元素，继承自HashedGameObject
public class Element extends HashedGameObject {

	// 构造函数，初始化Element对象
	public Element(final String id, final DataTable table) {
		super(id, table);
	}

	// 获取元素可以制造的游戏对象列表
	public List<GameObject> builds() {
		return getFieldAsList("Builds", this.parentTable);
	}

	// 获取元素所需的游戏对象列表
	public List<GameObject> requires() {
		final List<GameObject> requirements = getFieldAsList("Requires", this.parentTable);
		final List<Integer> reqLvls = requiresLevels();
		return requirements;
	}

	// 获取元素所需的等级列表
	public List<Integer> requiresLevels() {
		final String stringList = getField("Requiresamount");
		final String[] listAsArray = stringList.split(",");
		final LinkedList<Integer> output = new LinkedList<>();
		if ((listAsArray != null) && (listAsArray.length > 0) && !listAsArray[0].equals("")) {
			for (final String levelString : listAsArray) {
				final Integer level = Integer.parseInt(levelString);
				if (level != null) {
					output.add(level);
				}
			}
		}
		return output;
	}

	// 获取元素的父对象列表
	public List<GameObject> parents() {
		return getFieldAsList("Parents", this.parentTable);
	}

	// 获取元素的子对象列表
	public List<GameObject> children() {
		return getFieldAsList("Children", this.parentTable);
	}

	// 获取被当前元素所需的游戏对象列表
	public List<GameObject> requiredBy() {
		return getFieldAsList("RequiredBy", this.parentTable);
	}

	// 获取当前元素的训练对象列表
	public List<GameObject> trains() {
		return getFieldAsList("Trains", this.parentTable);
	}

	// 获取当前元素的升级对象列表
	public List<GameObject> upgrades() {
		return getFieldAsList("Upgrade", this.parentTable);
	}

	// 获取当前元素的研究对象列表
	public List<GameObject> researches() {
		return getFieldAsList("Researches", this.parentTable);
	}

	// 获取当前元素的或依赖对象列表
	public List<GameObject> dependencyOr() {
		return getFieldAsList("DependencyOr", this.parentTable);
	}

	// 获取当前元素的能力列表
	public List<GameObject> abilities() {
		return getFieldAsList("abilList", this.parentTable);
	}

	HashMap<String, List<Element>> hashedLists = new HashMap<>();

	// 返回元素的名称
	@Override
	public String toString() {
		return getField("Name");
	}

	// 获取当前元素的科技等级
	public int getTechTier() {
		final String tier = getField("Custom Field: TechTier");
		if (tier == null) {
			return -1;
		}
		return Integer.parseInt(tier);
	}

	// 设置当前元素的科技等级
	public void setTechTier(final int i) {
		setField("Custom Field: TechTier", i + "");
	}

	// 获取当前元素的科技深度
	public int getTechDepth() {
		final String tier = getField("Custom Field: TechDepth");
		if (tier == null) {
			return -1;
		}
		return Integer.parseInt(tier);
	}

	// 设置当前元素的科技深度
	public void setTechDepth(final int i) {
		setField("Custom Field: TechDepth", i + "");
	}

	// 获取当前元素的图标路径
	public String getIconPath() {
		String artField = getField("Art");
		if (artField.indexOf(',') != -1) {
			artField = artField.substring(0, artField.indexOf(','));
		}
		return artField;
	}

	// 获取当前元素的单位ID
	public String getUnitId() {
		return this.id;
	}

	// 获取当前元素的名称
	@Override
	public String getName() {
		String name = getField("Name");
		boolean nameKnown = name.length() >= 1;
		if (!nameKnown && !getField("code").equals(this.id) && (getField("code").length() >= 4)) {
			final Element other = (Element) this.parentTable.get(getField("code").substring(0, 4));
			if (other != null) {
				name = other.getName();
				nameKnown = true;
			}
		}
		if (!nameKnown && (getField("EditorName").length() > 1)) {
			name = getField("EditorName");
			nameKnown = true;
		}
		if (!nameKnown && (getField("Editorname").length() > 1)) {
			name = getField("Editorname");
			nameKnown = true;
		}
		if (!nameKnown && (getField("BuffTip").length() > 1)) {
			name = getField("BuffTip");
			nameKnown = true;
		}
		if (!nameKnown && (getField("Bufftip").length() > 1)) {
			name = getField("Bufftip");
			nameKnown = true;
		}
		if (nameKnown && name.startsWith("WESTRING")) {
			if (!name.contains(" ")) {
				name = this.parentTable.getLocalizedString(name);
			}
			else {
				final String[] names = name.split(" ");
				name = "";
				for (final String subName : names) {
					if (name.length() > 0) {
						name += " ";
					}
					if (subName.startsWith("WESTRING")) {
						name += this.parentTable.getLocalizedString(subName);
					}
					else {
						name += subName;
					}
				}
			}
			if (name.startsWith("\"") && name.endsWith("\"")) {
				name = name.substring(1, name.length() - 1);
			}
			setField("Name", name);
		}
		if (!nameKnown) {
			name = this.parentTable.getLocalizedString("WESTRING_UNKNOWN") + " '" + getUnitId() + "'";
		}
		if (getField("campaign").startsWith("1") && Character.isUpperCase(getUnitId().charAt(0))) {
			name = getField("Propernames");
			if (name.contains(",")) {
				name = name.split(",")[0];
			}
		}
		String suf = getField("EditorSuffix");
		if ((suf.length() > 0) && !suf.equals("_")) {
			if (suf.startsWith("WESTRING")) {
				suf = this.parentTable.getLocalizedString(suf);
			}
			if (!suf.startsWith(" ")) {
				name += " ";
			}
			name += suf;
		}
		return name;
	}

	// 获取元素的遗留名称
	@Override
	public String getLegacyName() {
		return null;
	}

	// 添加父元素ID
	public void addParent(final String parentId) {
		String parentField = getField("Parents");
		if (!parentField.contains(parentId)) {
			parentField = parentField + "," + parentId;
			setField("Parents", parentField);
		}
	}

	// 添加子元素ID
	public void addChild(final String parentId) {
		String parentField = getField("Children");
		if (!parentField.contains(parentId)) {
			parentField = parentField + "," + parentId;
			setField("Children", parentField);
		}
	}

	// 添加被当前元素需要的元素ID
	public void addRequiredBy(final String parentId) {
		String parentField = getField("RequiredBy");
		if (!parentField.contains(parentId)) {
			parentField = parentField + "," + parentId;
			setField("RequiredBy", parentField);
		}
	}

	// 添加当前元素的研究元素ID
	public void addResearches(final String parentId) {
		String parentField = getField("Researches");
		if (!parentField.contains(parentId)) {
			parentField = parentField + "," + parentId;
			setField("Researches", parentField);
		}
	}
}
