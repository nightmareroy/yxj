package com.wanniu.game.data.ext;

import java.util.HashMap;
import java.util.Map;

import com.wanniu.game.data.GBuffCO;
import com.wanniu.game.player.AttributeUtil;

import io.netty.util.internal.StringUtil;

public class GBuffExt extends GBuffCO {
	public Map<String, Integer> attrs;

	public void putAttr(String str, int value) {
		if (null == attrs) {
			attrs = new HashMap<String, Integer>();
		}

		if (StringUtil.isNullOrEmpty(str)) {
			return;
		}

		String attr = AttributeUtil.getKeyByName(str);

		if (StringUtil.isNullOrEmpty(attr)) {
			return;
		}

		attrs.put(attr, value);
	}

	public void initProperty() {
		putAttr(super.buffAttribute1, super.buffValue1);
		putAttr(super.buffAttribute2, super.buffValue2);
		putAttr(super.buffAttribute3, super.buffValue3);
		putAttr(super.buffAttribute4, super.buffValue4);
		putAttr(super.buffAttribute5, super.buffValue5);
	};

}
