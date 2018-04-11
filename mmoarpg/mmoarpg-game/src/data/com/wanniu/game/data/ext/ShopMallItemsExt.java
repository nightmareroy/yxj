package com.wanniu.game.data.ext;

import java.util.Date;

import com.wanniu.core.util.DateUtil;
import com.wanniu.core.util.StringUtil;
import com.wanniu.game.data.ShopMallItemsCO;

/**
 * @since 2017/1/19 14:01:11
 * @author auto generate
 */
public class ShopMallItemsExt extends ShopMallItemsCO{
	
	public Date startTime;
	
	public Date endTime;

	@Override
	public void initProperty() {
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