package com.etheller.warsmash.viewer5.handlers.w3x.rendersim.ability;

import java.util.List;
// Buff资源：图标、特效、声音等
public class BuffUI {
	// 开启状态图标的UI组件
	private final IconUI onIconUI;
	
	// 目标特效的UI组件
	private final List<EffectAttachmentUI> targetArt;
	
	// 特殊特效的UI组件
	private final List<EffectAttachmentUI> specialArt;
	
	// 普通特效的UI组件
	private final List<EffectAttachmentUI> effectArt;
	
	// 导弹特效的UI组件
	private final List<EffectAttachmentUI> missileArt;
	
	// 特效播放的单次声音
	private final String effectSound;
	
	// 特效播放的循环声音
	private final String effectSoundLooped;


	public BuffUI(final IconUI onIconUI, final List<EffectAttachmentUI> targetArt,
			final List<EffectAttachmentUI> specialArt, final List<EffectAttachmentUI> effectArt,
			final List<EffectAttachmentUI> missileArt, final String effectSound, final String effectSoundLooped) {
		this.onIconUI = onIconUI;
		this.targetArt = targetArt;
		this.specialArt = specialArt;
		this.effectArt = effectArt;
		this.missileArt = missileArt;
		this.effectSound = effectSound;
		this.effectSoundLooped = effectSoundLooped;
	}

	public IconUI getOnIconUI() {
		return this.onIconUI;
	}

	public EffectAttachmentUI getTargetArt(final int index) {
		return AbilityUI.tryGet(this.targetArt, index);
	}

	public EffectAttachmentUI getSpecialArt(final int index) {
		return AbilityUI.tryGet(this.specialArt, index);
	}

	public EffectAttachmentUI getEffectArt(final int index) {
		return AbilityUI.tryGet(this.effectArt, index);
	}

	public EffectAttachmentUI getMissileArt(final int index) {
		return AbilityUI.tryGet(this.missileArt, index);
	}

	public String getEffectSound() {
		return this.effectSound;
	}

	public String getEffectSoundLooped() {
		return this.effectSoundLooped;
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

	public List<EffectAttachmentUI> getMissileArt() {
		return missileArt;
	}

}
