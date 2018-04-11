package com.wanniu.game.data.ext;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.wanniu.game.data.DropListCO;
import com.wanniu.game.item.ItemConfig;
import com.wanniu.game.item.ItemUtil;
import com.wanniu.game.item.NormalItem;

/**
 * 镇妖塔掉落
 * @author agui
 *
 */
public class DropListExt extends DropListCO {

	public int[] weeks;
	
//	public class Reward
//	{
//		public String code;
//		public int count;
//		
//		public Reward (String code,int count) {
//			this.code=code;
//			this.count=count;
//		}
//	}
	public Map<String, Integer> rewardPreview;
	public Map<String, Integer> firstRewardPreview;
	public Map<String, Integer> weekRewardPreview;

	@Override
	public void initProperty() {
		String[] weeks = this.weekDay.split(",");
		this.weeks = new int[weeks.length];
		for (int i = 0; i < weeks.length; ++i) {
			this.weeks[i] = Integer.parseInt(weeks[i]);
			if (this.weeks[i] == 7) {
				this.weeks[i] = 1;
			} else {
				this.weeks[i]++;
			}
		}
		
		String[] rewardPreviewStrs=this.itemView.split(";");
		this.rewardPreview=new HashMap<>();
		for(int i=0;i<rewardPreviewStrs.length;i++)
		{
			String str=rewardPreviewStrs[i];
			String[] params=str.split(":");
			String code=params[0];
			int count=Integer.parseInt(params[1]);
			this.rewardPreview.put(code, count);
			
		}
		
		String[] firstRewardPreviewStrs=this.firstReward.split(";");
		this.firstRewardPreview=new HashMap<>();
		for(int i=0;i<rewardPreviewStrs.length;i++)
		{
			String str=firstRewardPreviewStrs[i];
			String[] params=str.split(":");
			String code=params[0];
			int count=Integer.parseInt(params[1]);
			this.firstRewardPreview.put(code, count);
			
		}
		
		String[] weekRewardPreviewStrs=this.weekReward.split(";");
		this.weekRewardPreview=new HashMap<>();
		for(int i=0;i<weekRewardPreviewStrs.length;i++)
		{
			String str=weekRewardPreviewStrs[i];
			String[] params=str.split(":");
			String code=params[0];
			int count=Integer.parseInt(params[1]);
			this.weekRewardPreview.put(code, count);
			
		}
	}

	public boolean isWeek(int week) {
		for (int i = 0; i < weeks.length; ++i) {
			if (weeks[i] == week) {
				return true;
			}
		}
		return false;
	}

//	public List<NormalItem> randomTC(int playerLevel) {
////		String realTC = ItemConfig.getInstance().getRealTC(tc, playerLevel);
////		return ItemUtil.createItemsByTcCode(realTC);
//		return ItemUtil.createItemsByRealTC(tc, playerLevel);
//	}

}
