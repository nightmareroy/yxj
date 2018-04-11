package com.wanniu.game.data.ext;

import java.util.HashMap;
import java.util.Map;

import com.wanniu.core.util.StringUtil;
import com.wanniu.game.data.SmritiCO;

public class SmritiExt extends SmritiCO {
	private Map<String, Integer> needItems;

	@Override
	public void initProperty() {
		Map<String, Integer> tpneedItems = new HashMap<>();
		if (!StringUtil.isEmpty(this.mateCode1) && this.mateCount1 > 0) {
			tpneedItems.put(this.mateCode1, this.mateCount1);
		}
		if (!StringUtil.isEmpty(this.mateCode2) && this.mateCount2 > 0) {
			tpneedItems.put(this.mateCode2, this.mateCount2);
		}
		if (!StringUtil.isEmpty(this.mateCode3) && this.mateCount3 > 0) {
			tpneedItems.put(this.mateCode3, this.mateCount3);
		}
		needItems = tpneedItems;
	}

	public Map<String, Integer> getNeedItems() {
		return needItems;
	}

}
