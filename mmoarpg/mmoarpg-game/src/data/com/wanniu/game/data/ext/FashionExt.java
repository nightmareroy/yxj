package com.wanniu.game.data.ext;

import java.util.HashMap;
import java.util.Map;

import com.wanniu.core.util.StringUtil;
import com.wanniu.game.common.Const;
import com.wanniu.game.common.Const.FASHION_TYPE;
import com.wanniu.game.common.Const.PlayerBtlData;
import com.wanniu.game.data.FashionCO;
import com.wanniu.game.player.AttributeUtil;

public class FashionExt extends FashionCO {

	
	
	/**Const.AVATAR_TYPE*/
	public int avatarTag;
	
	public Map<PlayerBtlData, Integer> atts;

	@Override
	public void initProperty() {
		
		if (this.type == FASHION_TYPE.WEPON.value) {
			this.avatarTag = Const.AVATAR_TYPE.R_HAND_WEAPON.value;
		} else if (this.type == FASHION_TYPE.CLOTH.value) {
			this.avatarTag = Const.AVATAR_TYPE.AVATAR_BODY.value;
		} else if (this.type == FASHION_TYPE.WING.value) {
			this.avatarTag = Const.AVATAR_TYPE.REAR_EQUIPMENT.value;
		}
		this.atts = new HashMap<>();
		
		if (StringUtil.isNotEmpty(this.prop1)) {
			
			this.atts.put(Const.PlayerBtlData.getE(prop1), num1);
		}
		if (StringUtil.isNotEmpty(this.prop2)) {
			this.atts.put(Const.PlayerBtlData.getE(prop2), num2);
		}
		if (StringUtil.isNotEmpty(this.prop3)) {
			this.atts.put(Const.PlayerBtlData.getE(prop3), num3);
		}
		if (StringUtil.isNotEmpty(this.prop4)) {
			this.atts.put(Const.PlayerBtlData.getE(prop4), num4);
		}
	}

	@Override
	public String getKey() {
		return this.code;
	}
	
	
}
