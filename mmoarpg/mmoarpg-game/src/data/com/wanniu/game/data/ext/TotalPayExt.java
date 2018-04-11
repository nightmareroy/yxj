package com.wanniu.game.data.ext;

import java.util.ArrayList;

import com.wanniu.game.data.TotalPayCO;
import com.wanniu.game.data.ext.DailyPayExt.AwardInfo;

public class TotalPayExt extends TotalPayCO {
	public ArrayList<AwardInfo> awards;

	@Override
	public void initProperty() {
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
	}
}
