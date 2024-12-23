package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.action.structural;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.etheller.warsmash.parsers.jass.JassTextGenerator;
import com.etheller.warsmash.parsers.jass.JassTextGeneratorCallStmt;
import com.etheller.warsmash.parsers.jass.JassTextGeneratorStmt;
import com.etheller.warsmash.parsers.jass.JassTextGeneratorType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.unitgroupcallbacks.ABUnitGroupCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.core.ABAction;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.core.ABLocalStoreKeys;

public class ABActionIterateUnitsInGroup implements ABAction {
	private static final boolean ALLOW_BREAK_JASS = true;

	private ABUnitGroupCallback unitGroup;
	private List<ABAction> iterationActions;

	@Override
	public void runAction(final CSimulation game, final CUnit caster, final Map<String, Object> localStore,
			final int castId) {
		final Set<CUnit> unitSet = this.unitGroup.callback(game, caster, localStore, castId);
		final List<CUnit> unitList = new ArrayList<>(unitSet);
		for (final CUnit enumUnit : unitList) {
			localStore.put(ABLocalStoreKeys.ENUMUNIT + castId, enumUnit);
			for (final ABAction iterationAction : this.iterationActions) {
				iterationAction.runAction(game, caster, localStore, castId);
			}
			final Boolean brk = (Boolean) localStore.remove(ABLocalStoreKeys.BREAK);
			if ((brk != null) && brk) {
				break;
			}
		}
		localStore.remove(ABLocalStoreKeys.ENUMUNIT + castId);
	}

	@Override
	public void generateJassEquivalent(int indent, JassTextGenerator jassTextGenerator) {
		if (ALLOW_BREAK_JASS) {
			final String loopIndexVarName = jassTextGenerator.declareLocal("integer", "idx");

			final String groupExpression = this.unitGroup.generateJassEquivalent(jassTextGenerator);

			final StringBuilder sb = new StringBuilder();
			JassTextGenerator.Util.indent(indent, sb);
			sb.append("set ");
			sb.append(loopIndexVarName);
			sb.append("= 0");
			jassTextGenerator.println(sb.toString());

			JassTextGenerator.Util.indent(indent, sb);
			sb.append("loop");
			jassTextGenerator.println(sb.toString());
			final int childIndent = indent + 1;
			sb.setLength(0);
			JassTextGenerator.Util.indent(childIndent, sb);
			sb.append("exitwhen ");
			sb.append(loopIndexVarName);
			sb.append(" >= GroupGetSize(");
			sb.append(groupExpression);
			sb.append(")");
			jassTextGenerator.println(sb.toString());

			sb.setLength(0);
			JassTextGenerator.Util.indent(childIndent, sb);
			sb.append("call SetLocalStoreUnitHandle(" + jassTextGenerator.getTriggerLocalStore()
					+ ", AB_LOCAL_STORE_KEY_ENUMUNIT + I2S(" + jassTextGenerator.getCastId() + "), " + "GroupGetUnitAt("
					+ loopIndexVarName + ")" + ")");

			for (final ABAction action : this.iterationActions) {
				action.generateJassEquivalent(childIndent, jassTextGenerator);
			}

			sb.setLength(0);
			JassTextGenerator.Util.indent(childIndent, sb);
			sb.append("if ");
			sb.append(jassTextGenerator.getUserDataExpr("AB_LOCAL_STORE_KEY_BREAK", JassTextGeneratorType.Boolean));
			sb.append(" then");
			jassTextGenerator.println(sb.toString());

			final int breakBranchIndent = childIndent + 1;
			sb.setLength(0);
			JassTextGenerator.Util.indent(breakBranchIndent, sb);
			sb.append("call FlushChildLocalStore(" + jassTextGenerator.getTriggerLocalStore()
					+ ", AB_LOCAL_STORE_KEY_BREAK)");
			jassTextGenerator.println(sb.toString());
			sb.setLength(0);
			JassTextGenerator.Util.indent(breakBranchIndent, sb);
			sb.append("exitwhen true // break");
			jassTextGenerator.println(sb.toString());

			sb.setLength(0);
			JassTextGenerator.Util.indent(childIndent, sb);
			sb.append("endif");
			jassTextGenerator.println(sb.toString());

			sb.setLength(0);
			JassTextGenerator.Util.indent(childIndent, sb);
			sb.append("set ");
			sb.append(loopIndexVarName);
			sb.append(" = ");
			sb.append(loopIndexVarName);
			sb.append(" + 1");
			jassTextGenerator.println(sb.toString());

			sb.setLength(0);
			JassTextGenerator.Util.indent(indent, sb);
			sb.append("endloop");
			jassTextGenerator.println(sb.toString());

			sb.setLength(0);
			JassTextGenerator.Util.indent(indent, sb);
			sb.append("call FlushChildLocalStore(" + jassTextGenerator.getTriggerLocalStore()
					+ ", AB_LOCAL_STORE_KEY_ENUMUNIT + I2S(" + jassTextGenerator.getCastId() + "))");
			jassTextGenerator.println(sb.toString());
		}
		else {
			// else use ForGroup
			final List<JassTextGeneratorStmt> modifiedActionList = new ArrayList<>(this.iterationActions);
			modifiedActionList.add(0, new JassTextGeneratorCallStmt() {
				@Override
				public String generateJassEquivalent(final JassTextGenerator jassTextGenerator) {
					return "SetLocalStoreUnitHandle(" + jassTextGenerator.getTriggerLocalStore()
							+ ", AB_LOCAL_STORE_KEY_ENUMUNIT + I2S(" + jassTextGenerator.getCastId() + "), "
							+ "GetEnumUnit()" + ")";
				}
			});
			final String iterationActionsName = jassTextGenerator.createAnonymousFunction(modifiedActionList,
					"UnitsInGroupEnumActions");

			final StringBuilder sb = new StringBuilder();
			JassTextGenerator.Util.indent(indent, sb);
			sb.append("call ForGroup(" + this.unitGroup.generateJassEquivalent(jassTextGenerator) + ", "
					+ jassTextGenerator.functionPointerByName(iterationActionsName) + ")");
			jassTextGenerator.println(sb.toString());

			sb.setLength(0);
			JassTextGenerator.Util.indent(indent, sb);
			sb.append("call FlushChildLocalStore(" + jassTextGenerator.getTriggerLocalStore()
					+ ", AB_LOCAL_STORE_KEY_ENUMUNIT + I2S(" + jassTextGenerator.getCastId() + "))");
			jassTextGenerator.println(sb.toString());
		}
	}
}
