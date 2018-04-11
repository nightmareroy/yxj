package com.wanniu.game.data.ext;

import java.util.HashMap;

import com.wanniu.core.logfs.Out;
import com.wanniu.core.util.ClassUtil;
import com.wanniu.core.util.StringUtil;
import com.wanniu.game.data.base.DEquipBase;
import com.wanniu.game.player.AttributeUtil;

/**
 * 固定属性装备，只在生成的时候起作用，重铸、洗练都用 BaseCode随机属性的数值计算
 * @author Yangzz
 *
 */
public class UniqueEquipExt extends DEquipBase {

	public String getKey() {
		return this.code;
	}

	@Override
	public void initProperty() {
		super.initProp();

		fixedAtts = new HashMap<>();
		try {
			for (int i = 1; i <= 6; i++) {
				String attrName = "rProp" + i;
				String par = "rPar" + i;
				String minValue = "rMin" + i;
				String maxValue = "rMax" + i;
				
				String key = (String) ClassUtil.getProperty(this, attrName);
				if(StringUtil.isEmpty(key))	continue;
				
				fixedAtts.put(AttributeUtil.getKeyByName(key), 
						(int) ClassUtil.getProperty(this, minValue));
			}
		} catch (Exception e) {
			Out.error(e);
		}
	}

	
}
