package com.wanniu.game.data.ext;

import com.wanniu.game.data.EnchantBonusCO;
import com.wanniu.game.item.ItemConfig;
import com.wanniu.game.player.AttributeUtil;

/**
 * 强化段位奖励属性
 * @author Yangzz
 *
 */
public class EnchantBonusExt extends EnchantBonusCO{
	
	public int _type;
	public String _prop;

	@Override
	public void initProperty() {
		_type = ItemConfig.getInstance().getIdConfig(type).typeID;
		
		_prop = AttributeUtil.getKeyByName(prop);
	}

	
}
