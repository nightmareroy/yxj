package com.wanniu.game.data.ext;

import java.util.HashMap;
import java.util.Map;

import com.wanniu.core.util.StringUtil;
import com.wanniu.game.common.Const;
import com.wanniu.game.common.Const.PlayerBtlData;
import com.wanniu.game.data.FashSuitConfigCO;
import com.wanniu.game.player.AttributeUtil;

public class FashSuitConfigExt extends FashSuitConfigCO {

	public Map<PlayerBtlData, Integer> Attr2Map;
	public Map<PlayerBtlData, Integer> Attr3Map;
	
	@Override
	public void initProperty() {
		this.Attr2Map = new HashMap<>();
		this.Attr3Map = new HashMap<>();
		
		String[] attr2strs=attr2.split(";");
		String[] attr3strs=attr3.split(";");
		
		for (String str : attr2strs) {
			String[] sub_str=str.split(":");
			Attr2Map.put(Const.PlayerBtlData.getE(Integer.parseInt(sub_str[0])), Integer.parseInt(sub_str[1]));
		}
		
		for (String str : attr3strs) {
			String[] sub_str=str.split(":");
			Attr2Map.put(Const.PlayerBtlData.getE(Integer.parseInt(sub_str[0])), Integer.parseInt(sub_str[1]));
		}

	}
}
