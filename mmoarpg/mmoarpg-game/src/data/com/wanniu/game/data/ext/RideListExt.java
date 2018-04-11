package com.wanniu.game.data.ext;

import java.util.HashMap;
import java.util.Map;

import com.wanniu.core.logfs.Out;
import com.wanniu.core.util.ClassUtil;
import com.wanniu.game.common.Const.PlayerBtlData;
import com.wanniu.game.data.GameData;
import com.wanniu.game.data.RideListCO;

public class RideListExt extends RideListCO {
	public Map<PlayerBtlData,Integer> levelAttrs = new HashMap<>();
	public Map<PlayerBtlData,Integer> starAttrs = new HashMap<>();
	/**
	 * 这个是存前置所有星级属性的集合
	 */
	public Map<PlayerBtlData,Integer> totalPreStarAttrs = new HashMap<>();
	
	
	/** 属性构造 */
	@Override
	public void initProperty() { 
	    for (int i = 1; i <= 7; ++i) {
	        String propName = "prop" + i;
	        String attrMax = "max" + i;
	        String starPropName = "starProp" + i;
	        String starAttrMax = "starMax" + i;
	        String key;
	        
	        try {
	        	Object obj = ClassUtil.getProperty(this, propName) ;
	        	if (obj != null) {
	        		key = (String) obj;
					PlayerBtlData pbd = PlayerBtlData.getE(key);
					
					if(pbd!=null){
						levelAttrs.put(pbd, (int) ClassUtil.getProperty(this, attrMax));
					}
	        	}
	        	obj = ClassUtil.getProperty(this, starPropName) ;
	        	if (obj != null) {
	        		key = (String) obj;
					PlayerBtlData pbd = PlayerBtlData.getE(key);
					
					if(pbd!=null){
						starAttrs.put(pbd, (int) ClassUtil.getProperty(this, starAttrMax));
						totalPreStarAttrs.put(pbd, 0);
					}
	        	}
	        	
			} catch (Exception e) {
				Out.error("Exception in RidelistExt: ", e);
			}
	    }
	    
	    Map<Integer, RideListExt> map = GameData.RideLists;
	    for(int level : map.keySet()){
	    	if(level<this.rideLevel){
	    		RideListExt prop_pre = map.get(level);
	    		Map<PlayerBtlData,Integer> starAttrs_pre = prop_pre.starAttrs;
	    		for(PlayerBtlData pbd: starAttrs_pre.keySet()){
	    			int value = totalPreStarAttrs.get(pbd);
	    			value += (starAttrs_pre.get(pbd)*10);
	    			totalPreStarAttrs.put(pbd, value);
	    		}
	    	}
	    }
	}
}
