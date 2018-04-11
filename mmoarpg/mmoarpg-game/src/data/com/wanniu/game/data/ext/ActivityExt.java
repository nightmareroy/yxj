package com.wanniu.game.data.ext;

import java.util.Date;

import com.wanniu.core.util.DateUtil;
import com.wanniu.game.data.ActivityCO;

public class ActivityExt extends ActivityCO {	
	public long beginTime;
	public long endTime;

	@Override
	public void initProperty() {
		Date dateBegin = DateUtil.format(this.openTime);
		this.beginTime = dateBegin.getTime();
		Date dateEnd = DateUtil.format(this.closeTime);
		this.endTime = dateEnd.getTime();
	}
}
