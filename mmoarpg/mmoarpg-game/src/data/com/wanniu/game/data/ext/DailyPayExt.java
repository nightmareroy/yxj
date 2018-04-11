package com.wanniu.game.data.ext;

import java.util.ArrayList;

import com.wanniu.game.common.Const;
import com.wanniu.game.data.DailyPayCO;

public class DailyPayExt extends DailyPayCO {
	public int Job;
	public ArrayList<AwardInfo> awards;

	@Override
	public void initProperty() {
		this.Job = Const.PlayerPro.Value(this.job);
		awards = new ArrayList<>();
		if (this.rewardCode1 != null && this.rewardNum1 != 0) {
			AwardInfo awardInfo = new AwardInfo();
			awardInfo.itemCode = this.rewardCode1;
			awardInfo.itemNum = this.rewardNum1;
			this.awards.add(awardInfo);
		}
		if (this.rewardCode2 != null && this.rewardNum2 != 0) {
			AwardInfo awardInfo = new AwardInfo();
			awardInfo.itemCode = this.rewardCode2;
			awardInfo.itemNum = this.rewardNum2;
			this.awards.add(awardInfo);
		}
		if (this.rewardCode3 != null && this.rewardNum3 != 0) {
			AwardInfo awardInfo = new AwardInfo();
			awardInfo.itemCode = this.rewardCode3;
			awardInfo.itemNum = this.rewardNum3;
			this.awards.add(awardInfo);
		}
		if (this.rewardCode4 != null && this.rewardNum4 != 0) {
			AwardInfo awardInfo = new AwardInfo();
			awardInfo.itemCode = this.rewardCode4;
			awardInfo.itemNum = this.rewardNum4;
			this.awards.add(awardInfo);
		}

	}

	/*
	 * PayGiftProp.prototype.initProperty = function() { var playerPro =
	 * consts.PlayerPro; this.Job = playerPro[this.Job];
	 * 
	 * this.awards = []; if (this.WeaponCode) { var awardInfo = {};
	 * awardInfo.itemCode = this.WeaponCode; awardInfo.itemNum = 1;
	 * awardInfo.enchantLv = this.EnchantLv; this.awards.push(awardInfo); } if
	 * (this.RewardCode1 && this.RewardNum1) { var awardInfo = {};
	 * awardInfo.itemCode = this.RewardCode1; awardInfo.itemNum =
	 * this.RewardNum1; this.awards.push(awardInfo); } if (this.RewardCode2 &&
	 * this.RewardNum2) { var awardInfo = {}; awardInfo.itemCode =
	 * this.RewardCode2; awardInfo.itemNum = this.RewardNum2;
	 * this.awards.push(awardInfo); } if (this.RewardCode3 && this.RewardNum3) {
	 * var awardInfo = {}; awardInfo.itemCode = this.RewardCode3;
	 * awardInfo.itemNum = this.RewardNum3; this.awards.push(awardInfo); } if
	 * (this.RewardCode4 && this.RewardNum4) { var awardInfo = {};
	 * awardInfo.itemCode = this.RewardCode4; awardInfo.itemNum =
	 * this.RewardNum4; this.awards.push(awardInfo); } };
	 * 
	 */

	public static class AwardInfo {
		public String itemCode;
		public int itemNum;
		public int enchantLv;

		public AwardInfo() {

		}

		public AwardInfo(String itemCode, int itemNum, int enchantLv) {
			super();
			this.itemCode = itemCode;
			this.itemNum = itemNum;
			this.enchantLv = enchantLv;
		}

	}
}
