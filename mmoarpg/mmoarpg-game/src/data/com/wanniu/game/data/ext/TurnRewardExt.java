package com.wanniu.game.data.ext;

import java.util.ArrayList;
import java.util.HashMap;

import com.wanniu.core.logfs.Out;
import com.wanniu.core.util.StringUtil;
import com.wanniu.game.bag.WNBag.SimpleItemInfo;
import com.wanniu.game.common.Const;
import com.wanniu.game.common.Const.ForceType;
import com.wanniu.game.data.ActivityConfigCO;
import com.wanniu.game.data.SevDayActivityCO;
import com.wanniu.game.data.TurnRewardCO;

public class TurnRewardExt extends TurnRewardCO{
	public HashMap<String, Integer> getRewardMap;


	@Override
	public void initProperty() {
		getRewardMap=new HashMap<>();
		
		String[] getRewardStrs=getReward.split(";");
		for (String getRewardSubStr : getRewardStrs) {
			String[] getRewardSubStrs = getRewardSubStr.split(":");
			
			getRewardMap.put(getRewardSubStrs[0], Integer.parseInt(getRewardSubStrs[1]));
		}
	}
	
	
}














