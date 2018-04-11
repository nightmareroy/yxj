package com.wanniu.game.data.ext;

import com.wanniu.game.common.Const.PlayerBtlData;
import com.wanniu.game.data.AttributeCO;

public class AttributeExt extends AttributeCO{
	
	public PlayerBtlData btlProp;
	
	@Override
	public void initProperty() {
		btlProp = PlayerBtlData.getE(iD);
	}
}
