package com.wanniu.game.data.ext;

import java.util.HashMap;
import java.util.Map;

import com.wanniu.core.logfs.Out;
import com.wanniu.core.util.ClassUtil;
import com.wanniu.core.util.StringUtil;
import com.wanniu.game.data.RankListCO;
import com.wanniu.game.player.AttributeUtil;

/**
 * @since 2017/1/22 14:42:40
 * @author auto generate
 */
public class RankListExt extends RankListCO {

	public Map<String, Integer> rankAttrs;

	@Override
	public void initProperty() {
		rankAttrs = new HashMap<>();
		for (int i = 1; i <= 4; i++) {
			String propName = "prop" + i;
			String maxValue = "max" + i;
			String key;
			try {
				if (ClassUtil.getProperty(this, propName) != null && StringUtil.isNotEmpty((String) ClassUtil.getProperty(this, propName))) {
					key = AttributeUtil.getKeyByName((String) ClassUtil.getProperty(this, propName));
					if (StringUtil.isNotEmpty(key) && ClassUtil.getProperty(this, maxValue) != null) {
						rankAttrs.put(key, (int) ClassUtil.getProperty(this, maxValue));
					}
				}
			} catch (Exception e) {
				Out.error(e);
			}
		}
	}

}