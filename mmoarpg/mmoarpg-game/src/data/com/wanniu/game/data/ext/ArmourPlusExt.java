package com.wanniu.game.data.ext;

import java.util.HashMap;
import java.util.Map;

import com.wanniu.core.util.StringUtil;
import com.wanniu.game.common.Const;

import com.wanniu.game.common.Const.PlayerBtlData;

import com.wanniu.game.data.ArmourPlusCO;


public class ArmourPlusExt extends ArmourPlusCO {

	
	public Map<PlayerBtlData, Integer> atts;

	@Override
	public void initProperty() {
		atts=new HashMap<>();
		
		String[] propStrs=this.prop.split(";");
		
		for (String string : propStrs) {
			String[] subPropStrs=string.split(":");
			this.atts.put(Const.PlayerBtlData.getE(Integer.parseInt(subPropStrs[0])), Integer.parseInt(subPropStrs[1]));
			
		}
		
		
	}

	@Override
	public int getKey() {
		return this.iD;
	}
	
	
}
