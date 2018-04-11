package com.wanniu.game.data.ext;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.wanniu.game.data.GTechnologyCO;
import com.wanniu.game.player.AttributeUtil;

import io.netty.util.internal.StringUtil;

public class GTechnologyExt extends GTechnologyCO {
	public Map<String, Integer> attrs;
	public List<Integer> recommendPros;

	public void putAttr(String str, int value) {
		if (null == attrs) {
			attrs = new HashMap<String, Integer>();
		}

		if (!StringUtil.isNullOrEmpty(str)) {
			String attr = AttributeUtil.getKeyByName(str);
			attrs.put(attr, value);
		}
	}

	public void initProperty() {
		putAttr(super.techAttribute1, super.techValue1);
		putAttr(super.techAttribute2, super.techValue2);
		recommendPros = new ArrayList<Integer>();
		if (StringUtil.isNullOrEmpty(super.recommend)) {
			String[] proArr = super.recommend.split(",");
			for (String pro : proArr) {
				recommendPros.add(Integer.parseInt(pro));
			}
		}
	}
}
