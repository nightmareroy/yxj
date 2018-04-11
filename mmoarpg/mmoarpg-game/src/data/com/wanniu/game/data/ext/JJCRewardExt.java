package com.wanniu.game.data.ext;

import java.util.HashMap;
import java.util.Map;

import com.wanniu.core.util.StringUtil;
import com.wanniu.game.data.JJCRewardCO;

public class JJCRewardExt extends JJCRewardCO {
	/**
	 * itemCode:rw[0],itemNum:Number(rw[1])
	 */
	public Map<String,Integer> _rankReward ;
	
	@Override
	public void initProperty() { 
	    this._rankReward =  new HashMap<>();
	    if(StringUtil.isEmpty(super.rankReward)) {
	    	return;
	    }
	    String[] rewards = super.rankReward.split(";");
	    for (String s : rewards) {
	        String[] rw = s.split(":");
	        if(rw.length>=2){
	        	this._rankReward.put(rw[0], Integer.parseInt(rw[1]));
	        }
	    }
	}
}
