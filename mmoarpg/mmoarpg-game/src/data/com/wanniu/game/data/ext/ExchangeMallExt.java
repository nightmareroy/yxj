package com.wanniu.game.data.ext;

import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSONObject;
import com.wanniu.core.util.DateUtil;
import com.wanniu.core.util.StringUtil;
import com.wanniu.game.data.EnchantCO;
import com.wanniu.game.data.ExchangeMallCO;

/**
 * 兑换商城
 * @author liyue
 *
 */
public class ExchangeMallExt extends ExchangeMallCO {

	public Map<String, Integer> exchangeNeedMap; 
	
	public Date startTime;
	
	public Date endTime;

	@Override
	public void initProperty() {
		exchangeNeedMap=new HashMap<>();
		
		String[] strs1=exchangeNeed.split(";");
		for (String strItem : strs1) {

			String[] strs2=strItem.split(":");
			exchangeNeedMap.put(strs2[0], Integer.parseInt(strs2[1]));

		}
		
		
		String periodStart = this.periodStart;
	    if(StringUtil.isNotEmpty(periodStart)){
	        this.startTime = DateUtil.format(periodStart);
	    }

	    String periodEnd = this.periodStart;
	    if(StringUtil.isNotEmpty(periodEnd)){
	        this.endTime = DateUtil.format(periodEnd);
	    }
	}
	
	
}
