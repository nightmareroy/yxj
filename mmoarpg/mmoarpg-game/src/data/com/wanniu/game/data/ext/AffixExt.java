package com.wanniu.game.data.ext;

import java.util.HashMap;
import java.util.Map;

import com.wanniu.core.logfs.Out;
import com.wanniu.core.util.ClassUtil;
import com.wanniu.core.util.StringUtil;
import com.wanniu.game.data.AffixCO;
import com.wanniu.game.data.base.FourProp;
import com.wanniu.game.player.AttributeUtil;

/**
 * 扩展属性词条
 * 
 * @author Yangzz
 *
 */
public class AffixExt extends AffixCO {

	public Map<Integer, FourProp> props;

	public String attName;

	@Override
	public int getKey() {
		return super.getKey();
	}

	@Override
	public void initProperty() {
		props = new HashMap<>();

		try {
			for (int i = 0; i < 5; i++) {
				String propName = (String) ClassUtil.getProperty(this, "prop" + i);
				if (StringUtil.isNotEmpty(propName)) {
					FourProp _prop = new FourProp(AttributeUtil.getKeyByName(propName),
							(int) ClassUtil.getProperty(this, "par" + i), (int) ClassUtil.getProperty(this, "min" + i),
							(int) ClassUtil.getProperty(this, "max" + i));
					props.put(i, _prop);
					if(!StringUtil.isEmpty(_prop.prop)) {
						attName = _prop.prop;
					}
				}
			}
		} catch (Exception e) {
			Out.error(e);
		}
	}

}
