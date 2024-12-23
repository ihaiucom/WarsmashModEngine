package com.etheller.warsmash.viewer5.handlers.w3x.rendersim.ability;

import java.util.List;

import com.etheller.warsmash.util.War3ID;

// 技能资源：图标、特效、声音等
public class AbilityUI {
	// 学习技能的图标UI
	private final IconUI learnIconUI;

	// 技能激活状态下的图标UI列表
	private final List<IconUI> onIconUIs;

	// 技能未激活状态下的图标UI列表
	private final List<IconUI> offIconUIs;

	// 施法者相关的特效附件UI列表
	private final List<EffectAttachmentUI> casterArt;

	// 目标相关的特效附件UI列表
	private final List<EffectAttachmentUI> targetArt;

	// 特殊效果相关的特效附件UI列表
	private final List<EffectAttachmentUI> specialArt;

	// 技能效果相关的特效附件UI列表
	private final List<EffectAttachmentUI> effectArt;

	// 范围效果相关的特效附件UI列表
	private final List<EffectAttachmentUI> areaEffectArt;

	// 导弹效果相关的特效附件UIMissile列表
	private final List<EffectAttachmentUIMissile> missileArt;

	// 闪电效果相关的War3ID列表
	private final List<War3ID> lightningEffects;

	// 技能效果声音
	private final String effectSound;

	// 循环播放的技能效果声音
	private final String effectSoundLooped;


	public AbilityUI(IconUI learnIconUI, List<IconUI> onIconUIs, List<IconUI> offIconUIs,
			List<EffectAttachmentUI> casterArt, List<EffectAttachmentUI> targetArt, List<EffectAttachmentUI> specialArt,
			List<EffectAttachmentUI> effectArt, List<EffectAttachmentUI> areaEffectArt,
			List<EffectAttachmentUIMissile> missileArt, List<War3ID> lightningEffects, String effectSound, String effectSoundLooped) {
		this.learnIconUI = learnIconUI;
		this.onIconUIs = onIconUIs;
		this.offIconUIs = offIconUIs;
		this.casterArt = casterArt;
		this.targetArt = targetArt;
		this.specialArt = specialArt;
		this.effectArt = effectArt;
		this.areaEffectArt = areaEffectArt;
		this.missileArt = missileArt;
		this.lightningEffects = lightningEffects;
		this.effectSound = effectSound;
		this.effectSoundLooped = effectSoundLooped;
	}

	public IconUI getLearnIconUI() {
		return this.learnIconUI;
	}

	public IconUI getOnIconUI(int index) {
		return tryGet(this.onIconUIs, index);
	}

	public IconUI getOffIconUI(int index) {
		return tryGet(this.offIconUIs, index);
	}

	public EffectAttachmentUI getCasterArt(final int index) {
		return tryGet(this.casterArt, index);
	}

	public EffectAttachmentUI getTargetArt(final int index) {
		return tryGet(this.targetArt, index);
	}

	public EffectAttachmentUI getSpecialArt(final int index) {
		return tryGet(this.specialArt, index);
	}

	public EffectAttachmentUI getEffectArt(final int index) {
		return tryGet(this.effectArt, index);
	}

	public EffectAttachmentUI getAreaEffectArt(final int index) {
		return tryGet(this.areaEffectArt, index);
	}

	public EffectAttachmentUIMissile getMissileArt(final int index) {
		return tryGet(this.missileArt, index);
	}

	public String getEffectSound() {
		return this.effectSound;
	}

	public String getEffectSoundLooped() {
		return this.effectSoundLooped;
	}

	protected static <T> T tryGet(final List<T> items, final int index) {
		if (items.isEmpty()) {
			return null;
		}
		if (index < items.size()) {
			return items.get(index);
		}
		return items.get(items.size() - 1);
	}

	public List<EffectAttachmentUI> getCasterArt() {
		return casterArt;
	}

	public List<EffectAttachmentUI> getTargetArt() {
		return targetArt;
	}

	public List<EffectAttachmentUI> getSpecialArt() {
		return specialArt;
	}

	public List<EffectAttachmentUI> getEffectArt() {
		return effectArt;
	}

	public List<EffectAttachmentUI> getAreaEffectArt() {
		return areaEffectArt;
	}

	public List<EffectAttachmentUIMissile> getMissileArt() {
		return missileArt;
	}

	public List<War3ID> getLightningEffects() {
		return lightningEffects;
	}
}
