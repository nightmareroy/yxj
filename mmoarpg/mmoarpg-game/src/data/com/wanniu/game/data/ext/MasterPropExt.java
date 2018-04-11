package com.wanniu.game.data.ext;

import java.util.HashMap;
import java.util.Map;

import com.wanniu.core.logfs.Out;
import com.wanniu.core.util.ClassUtil;
import com.wanniu.game.common.Const.PlayerBtlData;
import com.wanniu.game.data.MasterPropCO;

public class MasterPropExt extends MasterPropCO {
	public Map<PlayerBtlData, Integer> attr_master = new HashMap<>();
	public Map<PlayerBtlData, Float>  attr_grow = new HashMap<>();
	@Override
	public void initProperty() {
		for (int i = 1; i <= propCount; ++i) {
			String propName = "prop" + i;
	        String attrMax = "max" + i;
	        String propName_grow = "grow"+i;
	        String key;
	        try {
	        	Object obj = ClassUtil.getProperty(this, propName) ;
	        	if (obj != null) {
	        		key = (String) obj;
					PlayerBtlData pbd = PlayerBtlData.getE(key);
					
					if(pbd!=null){
						attr_master.put(pbd, (int) ClassUtil.getProperty(this, attrMax));
					}
					
					attr_grow.put(pbd, (float) ClassUtil.getProperty(this, propName_grow));
	        	}
			} catch (Exception e) {
				Out.error("Exception in MasterPropExt: ", e);
			}
		}
	}
	
}
