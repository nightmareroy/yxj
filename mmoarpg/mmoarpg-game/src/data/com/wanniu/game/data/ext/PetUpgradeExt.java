package com.wanniu.game.data.ext;

import java.util.HashMap;
import java.util.Map;

import com.wanniu.core.logfs.Out;
import com.wanniu.core.util.ClassUtil;
import com.wanniu.game.common.Const.PlayerBtlData;
import com.wanniu.game.data.PetUpgradeCO;

public class PetUpgradeExt extends PetUpgradeCO {
	
	public Map<PlayerBtlData,Integer> upLevelAttrs = new HashMap<>();
	
	
	@Override
	public void initProperty() {
		for (int i = 1; i <= propCount; ++i) {
			String propName = "petProp" + i;
	        String attrMax = "petMax" + i;
	        String key;
	        try {
	        	Object obj = ClassUtil.getProperty(this, propName) ;
	        	if (obj != null) {
	        		key = (String) obj;
					PlayerBtlData pbd = PlayerBtlData.getE(key);
					
					if(pbd!=null){
						upLevelAttrs.put(pbd, (int) ClassUtil.getProperty(this, attrMax));
					}
	        	}
			} catch (Exception e) {
				Out.error("Exception in PetUpgradeExt: ", e);
			}
		}
	}
}
