package com.wanniu.game.data.ext;

import java.util.HashMap;
import java.util.Map;

import com.wanniu.core.logfs.Out;
import com.wanniu.game.data.BlesslibaoCO;

public class BlesslibaoExt extends BlesslibaoCO {	
	public Map<String,Integer> itemCode30;
	public Map<String,Integer> itemCode60;
	public Map<String,Integer> itemCode100;
	
	public BlesslibaoExt(){
		itemCode30 = new HashMap<String,Integer>();
		itemCode60 = new HashMap<String,Integer>();
		itemCode100 = new HashMap<String,Integer>();
	}
	
	public void initLs(String code,Map<String,Integer> map) {
		String[] rewards = code.split(",");
		for (int i = 0; i < rewards.length; i++) {
			String[] _elem = rewards[i].split(":");
			if(2 != _elem.length){
				Out.error("the config err in Blesslibao.json");
				continue;
			}
			
			map.put(_elem[0],Integer.valueOf(_elem[1]));
		}
	}

	public void initProperty() {
		super.initProperty();
		initLs(super.blessAward30,itemCode30);
		initLs(super.blessAward60,itemCode60);
		initLs(super.blessAward100,itemCode100);
	};
}
