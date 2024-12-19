package com.etheller.warsmash.viewer5.handlers.w3x;

public class AnimationTokens {
	public static enum PrimaryTag {
		ATTACK, // 攻击
		BIRTH, // 出生或生成状态
//		CINEMATIC, // 电视动画
		DEATH, // 死亡
		DECAY, // 衰变状态的动画，可能用于物体或角色的消失。
		DISSIPATE, // 消散
		MORPH, // 变身
		PORTRAIT, // 肖像
		SLEEP, // 睡眠
//		SPELL, // 施法
		STAND, // 站立
		WALK; // 行走
	}

	public static enum SecondaryTag {
		ALTERNATE, ALTERNATEEX, //代表动画的替代状态或变体，可以用于不同的动画效果。
		BONE, //可能与骨骼动画或骨架相关的状态，通常用于角色或生物体的动画。
		CHAIN, //表示链状动作，可能与多个对象的连接或一个接一个的动画效果有关。
		CHANNEL, //可能用于控制动画播放的通道。
		COMPLETE, //表示动画的完成状态，可能用于表示某个动作完成了。
		CRITICAL, //可能表示关键时刻的动画，通常用于游戏中特殊的攻击或效果。
		DEFEND, //表示防御状态的动画，通常是角色或单位处于防御姿态。
		DRAIN, //可能与耗尽、消耗资源或生命值相关的动画。
		EATTREE, //可能表示一种特定的动作，例如某个角色或单位在吃树。
		FAST, //表示快速的动画动作。
		FILL, //可能与填充某种状态或效果有关的动画。
		FLAIL, //表示无序地摆动，通常用于模拟疯狂或挣扎的动态。
		FLESH, //可能指与肉体相关的动画状态。
		FIRST, SECOND,  THIRD, FOURTH, FIFTH,  FIVE,       FOUR,  TWO, //表示数字相关的状态或顺序，可以用于不同的动作或效果标识。
		FIRE, //与火焰相关的动画效果，可能用于攻击或技能。
		GOLD, //可能与金钱或资源相关的动画效果。
		HIT, //表示受击状态，通常用于角色被攻击后的动画。
		LARGE, MEDIUM, SMALL, //表示动作的大小分类，可能影响动画的表现形式。
		LEFT, RIGHT, //表示方向，与角色朝向或动作方向相关。
		LIGHT, //可能表示轻盈或弱小的状态或效果。
		LOOPING, //表示动画循环播放的状态。
		LUMBER, //可能与砍树或木材相关的动画。
		MODERATE, //表示中等程度的状态或效果。
		OFF, //表示关闭或非活动状态。
		PUKE, //可能表示呕吐相关的动画动作。
		READY, //表示准备状态，通常用于角色准备施法或攻击。
		SEVERE, //表示严重状态，可能用于表示危急或者重伤。
		SLAM, //代表猛烈的撞击或攻击动画。
		SPIKED, //表示带刺的状态，常用于某些特定的角色或生物体的动画。
		SPIN, //代表旋转的动作。
		SPELL, //与施法动作相关的动画。
		CINEMATIC, //表示电影级别的动画，通常用于过场动画。
		SWIM, //代表游泳状态。
		TALK, //表示说话的动画状态。
		TURN, //表示转向的动作。
		THROW, //表示投掷的动作。
		VICTORY, //表示胜利状态的动画。
		WORK, //代表工作状态的动作。
		WOUNDED, //表示受伤状态的动画，通常用于表示角色受伤后的动作。
		UPGRADE; //表示升级状态的动画，常用于角色或单位的提升。

		
		public static SecondaryTag fromCount(int count) {
			switch(count) {
			case 1:
				return SecondaryTag.FIRST;
			case 2:
				return SecondaryTag.SECOND;
			case 3:
				return SecondaryTag.THIRD;
			case 4:
				return SecondaryTag.FOURTH;
			case 5:
				return SecondaryTag.FIFTH;
			}
			return null;
		}
	}
}
