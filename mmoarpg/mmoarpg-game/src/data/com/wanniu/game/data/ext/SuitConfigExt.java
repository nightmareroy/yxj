package com.wanniu.game.data.ext;

import com.wanniu.game.data.SuitConfigCO;
import com.wanniu.game.player.AttributeUtil;

/**
 * @since 2017/1/24 15:48:09
 * @author auto generate
 */
public class SuitConfigExt extends SuitConfigCO {
	
	public String _prop;

	public void initProperty() {
		_prop = AttributeUtil.getKeyByName(this.prop);
	}

}