package com.wanniu.game.data.ext;

import com.wanniu.core.logfs.Out;
import com.wanniu.core.util.RandomUtil;
import com.wanniu.game.data.NpcListCO;

public class NpcListExt extends NpcListCO {
	public int id;
	public String type;
	public Limit successArray;
	public Limit failScoldArray;
	public Limit failBattleArray;
	public int totalDrop;

	public static class Limit {
		public int min;
		public int max;
	}

	/** 属性构造 */
	public void initProperty() {
		this.id = super.npcID;
		this.successArray = new Limit();
		this.failScoldArray = new Limit();
		this.failBattleArray = new Limit();
		if (this.type.indexOf("1") == 0) {
			this.totalDrop = super.sucessDrop + super.failScold + super.failBattle;

			if (super.sucessDrop >= 0) {
				this.successArray.min = 0;
				this.successArray.max = super.sucessDrop;
			} else {
				Out.error("NpcDataProp 策划数据错误 SucessDrop 配的概率<0,id:", this.id, ",SucessDrop值:", super.sucessDrop, this);
			}

			if (super.failScold >= 0) {
				this.failScoldArray.min = this.successArray.max;
				this.failScoldArray.max = super.failScold;
			} else {
				Out.error("NpcDataProp 策划数据错误 FailScold 配的概率<0,id:", this.id, ",FailScold值:", super.failScold, this);
			}

			if (super.failBattle >= 0) {
				this.failBattleArray.min = this.failScoldArray.max;
				this.failBattleArray.max = super.failBattle;
			} else {
				Out.error("NpcDataProp 策划数据错误 FailBattle 配的概率<0 ,id:", this.id, ",FailBattle值:", super.failBattle, this);
			}
		}
	}

	public int steal() {
		int temp = RandomUtil.random(this.totalDrop);
		if (temp >= this.successArray.min && temp < this.successArray.max) {
			return 1;
		} else if (temp >= this.failScoldArray.min && temp < this.failScoldArray.max) {
			return 2;
		} else if (temp >= this.failBattleArray.min && temp < this.failBattleArray.max) {
			return 3;
		} else {
			return 2;
		}
	}
}
