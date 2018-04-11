package com.wanniu.game.data.ext;

import java.util.HashMap;
import java.util.Map;

import com.wanniu.core.logfs.Out;
import com.wanniu.core.util.StringUtil;
import com.wanniu.game.common.Const;
import com.wanniu.game.common.Const.FASHION_TYPE;
import com.wanniu.game.common.Const.PlayerBtlData;
import com.wanniu.game.data.ArmourAttributeCO;
import com.wanniu.game.data.FashionCO;
import com.wanniu.game.player.AttributeUtil;

public class ArmourAttributeExt extends ArmourAttributeCO {

	
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
