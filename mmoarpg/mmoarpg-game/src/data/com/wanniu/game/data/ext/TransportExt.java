package com.wanniu.game.data.ext;

import java.util.HashMap;
import java.util.Map;

import com.wanniu.core.util.StringUtil;
import com.wanniu.game.data.TransportCO;

public class TransportExt extends TransportCO { 

	public int targetX;
	public int targetY;
	public Map<String, Integer> targetPoint_;
	
	/** 属性构造 */
	public void initProperty() { 
		  // 初始化新场景坐标
	    if(!StringUtil.isEmpty(super.targetPoint)){
	        String[] targetPointStr = super.targetPoint.split(",");
	        int targetX = (int) Float.parseFloat(targetPointStr[0]);
	        int targetY = (int) Float.parseFloat(targetPointStr[1]);
	        this.targetPoint_ = new HashMap<>();
	        targetPoint_.put("targetX", targetX);
	        targetPoint_.put("targetY", targetY);
	    }
	}

}