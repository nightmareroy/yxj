package com.wanniu.game.data.ext;

import java.util.HashMap;
import java.util.Map;

import com.wanniu.core.logfs.Out;
import com.wanniu.game.data.VitBonusCO;

public class VitBonusExt extends VitBonusCO{
	public Map<String,Integer> rewards;
	
	public void initRewards(Map<String,Integer> map) {
		String[] rewards = super.chestCode.split(",");
		for (int i = 0; i < rewards.length; i++) {
			String[] _elem = rewards[i].split(":");
			if(2 != _elem.length){
				Out.error("the config err in VitBonus.json");
				continue;
			}
			
			map.put(_elem[0],Integer.valueOf(_elem[1]));
		}
	}
	
	public void initProperty(){
		super.initProperty();
		rewards = new HashMap<String,Integer>();
		initRewards(rewards);
	}
}
