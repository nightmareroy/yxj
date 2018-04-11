package com.wanniu.game.data.ext;

import java.util.HashMap;
import java.util.Map;

import com.wanniu.core.logfs.Out;
import com.wanniu.core.util.StringUtil;
import com.wanniu.game.data.TeamPropCO;
import com.wanniu.game.player.AttributeUtil;

public class TeamPropExt extends TeamPropCO{

	public Map<String, Integer> attrs;
	@Override
	public void initProperty() {
		super.initProperty();
		
		 this.attrs = new HashMap<>();
		    for(int i = 1; i <= 8; i ++){
		    	String attrName = "";
		    	int attrValue = 0;
		    	switch(i){
		    	case 1:
		    		attrName = this.prop1;
		    		attrValue = this.min1;
		    		break;
		    	case 2:
		    		attrName = this.prop2;
		    		attrValue = this.min2;
		    		break;
		    	case 3:
		    		attrName = this.prop3;
		    		attrValue = this.min3;
		    		break;
		    	case 4:
		    		attrName = this.prop4;
		    		attrValue = this.min4;
		    		break;
		    	case 5:
		    		attrName = this.prop5;
		    		attrValue = this.min5;
		    		break;
		    	case 6:
		    		attrName = this.prop6;
		    		attrValue = this.min6;
		    		break;
		    	case 7:
		    		attrName = this.prop7;
		    		attrValue = this.min7;
		    		break;
		    	case 8:
		    		attrName = this.prop8;
		    		attrValue = this.min8;
		    		break;
		    	}
		    	if(StringUtil.isNotEmpty(attrName)){
		    		String key = AttributeUtil.getKeyByName(attrName);
		    		if(key != null){
		    			this.attrs.put(key, attrValue);
		    		}
		    	}
		    	else{
		    		Out.debug("EquipProp attName is space");
		    	}
		    }
		
	}
	
}
