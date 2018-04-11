package com.wanniu.game.data.ext;

import java.util.ArrayList;
import java.util.List;

import com.wanniu.core.logfs.Out;
import com.wanniu.core.util.ClassUtil;
import com.wanniu.core.util.StringUtil;
import com.wanniu.game.bag.WNBag.SimpleItemInfo;
import com.wanniu.game.data.CombineCO;

/**
 * @since 2017/1/24 15:48:08
 * @author auto generate
 */
public class CombineExt extends CombineCO {

	public List<SimpleItemInfo> material = null;

	@Override
	public void initProperty() {
		material = new ArrayList<>();
		for (int i = 1; i <= 3; ++i) {
			String key = "srcCode" + i;
			String value = "srcCount" + i;

			try {
				String keyCode = (String) ClassUtil.getProperty(this, key);
				if (StringUtil.isNotEmpty(keyCode)) {
					SimpleItemInfo item = new SimpleItemInfo();
					item.itemCode = (String) ClassUtil.getProperty(this, key);
					item.itemNum = (int) ClassUtil.getProperty(this, value);
					material.add(item);
				}
			} catch (Exception e) {
				Out.error(e);
			}
		}

	}

}