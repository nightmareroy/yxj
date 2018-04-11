package com.wanniu.game.data.ext;

import com.alibaba.fastjson.JSONObject;
import com.wanniu.core.logfs.Out;
import com.wanniu.game.data.BlessBuffCO;
import com.wanniu.game.player.AttributeUtil;

public class BlessBuffExt extends BlessBuffCO {
	public JSONObject attr;

	@Override
	public void initProperty() {
		super.initProperty();
		this.attr = new JSONObject();

		String key = AttributeUtil.getKeyByName(super.buffAttribute1);
		if (key == null) {
			Out.error("BlessBuffExt attrName not exist : ", super.buffAttribute1);
		}
		this.attr.put("attrKey", key);
		this.attr.put("attrValue", super.buffValue1);
	}
}
