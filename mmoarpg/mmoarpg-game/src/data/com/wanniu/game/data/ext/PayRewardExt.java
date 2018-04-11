package com.wanniu.game.data.ext;

import java.util.ArrayList;

import com.wanniu.core.util.StringUtil;
import com.wanniu.game.data.PayRewardCO;
import com.wanniu.game.mail.data.MailData.Attachment;

public class PayRewardExt extends PayRewardCO {

	public RefreshTime RefreshTime;
	public ArrayList<Attachment> RankReward;

	/** 属性构造 */
	@Override
	public void initProperty() {
		this.RankReward = new ArrayList<>();

		if (StringUtil.isNotEmpty(this.payReward)) {
			String[] rewards = this.payReward.split(";");
			for (int i = 0; i < rewards.length; i++) {
				String[] rw = rewards[i].split(":");
				if (rw.length == 2) {
					Attachment item = new Attachment();
					item.itemCode = rw[0];
					item.itemNum = Integer.parseInt(rw[1]);
					RankReward.add(item);
				}
			}
		}

		if (StringUtil.isNotEmpty(payRefreshDay)) {
			String[] day = this.payRefreshDay.split("-");
			if (day.length == 2) {
				RefreshTime = new RefreshTime();
				RefreshTime.Year = Integer.parseInt(day[0]);
				RefreshTime.Month = Integer.parseInt(day[1]);
			}
		}
	}

	public static class RefreshTime {
		public int Year;
		public int Month;
	}
}