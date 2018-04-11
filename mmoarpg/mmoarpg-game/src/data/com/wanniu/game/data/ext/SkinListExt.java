package com.wanniu.game.data.ext;

import java.util.HashMap;
import java.util.Map;

import com.wanniu.core.logfs.Out;
import com.wanniu.core.util.ClassUtil;
import com.wanniu.game.common.Const.PlayerBtlData;
import com.wanniu.game.data.SkinListCO;

public class SkinListExt extends SkinListCO {
	public Map<PlayerBtlData,Integer> skinAttrs = new HashMap<>();
//	public Map<String,Integer> attrs = new HashMap<>();
//	public Map<String,Integer> minAttrs = new HashMap<>();
//	public Map<String,Integer> maxAttrs = new HashMap<>();
	/** 属性构造 */
	@Override
	public void initProperty() {
		for (int i = 1; i <= 6; ++i) {
			String propName = "prop" + i;
			String attrMax = "max" + i;
			String key;

			try {
				Object obj = ClassUtil.getProperty(this, propName);
				if (obj != null) {
					key = (String) obj;
					PlayerBtlData pbd = PlayerBtlData.getE(key);

					if (pbd != null) {
						skinAttrs.put(pbd, (int) ClassUtil.getProperty(this, attrMax));
					}
				}

			} catch (Exception e) {
				Out.error("Exception in SkinListExt: ", e);
			}
		}
	}
}
