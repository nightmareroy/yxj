package com.wanniu.game.data.ext;

import com.alibaba.fastjson.JSONObject;
import com.wanniu.game.data.EnchantCO;

/**
 * 装备强化配置
 * @author Yangzz
 *
 */
public class EnchantExt extends EnchantCO {

	public JSONObject mates = null;

	@Override
	public void initProperty() {
		mates = new JSONObject();
		mates.put(mateCode1, mateCount1);
		mates.put(mateCode2, mateCount2);
	}
	
	
}
