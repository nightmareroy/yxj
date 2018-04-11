package com.wanniu.game.data.ext;

import java.util.ArrayList;
import java.util.List;

import com.wanniu.core.util.StringUtil;
import com.wanniu.game.data.ScheduleCO;

public class ScheduleExt extends ScheduleCO {
	public List<String> mapIDArray;
	public List<String> targetArray;
	public List<Integer> weekArray;
	public List<TimeCond> timeArray;
	public List<TimeCond> periodInCalendarArray;

	public void initArray(List<String> arr, String str,boolean isWeek) {
		if (StringUtil.isEmpty(str)) {
			return;
		}
		
		if(isWeek){
			if(str.equals("0")){
				return;
			}
		}
		
		String[] tempData = str.split(";");

		for (int i = 0; i < tempData.length; i++) {

			arr.add(tempData[i]);
		}
	}

	public void initTime(List<TimeCond> arr, String str) {
		if (StringUtil.isEmpty(str) || "0" == str) {
			return;
		}
		String[] timeStr = str.split(";");
		for (int i = 0; i < timeStr.length; i++) {
			String elemStr = timeStr[i];
			if (StringUtil.isEmpty(elemStr)) {
				continue;
			}

			String[] tmp = elemStr.split("-");
			
			if (tmp.length != 2) {
				continue;
			}
			
			TimeCond timeCond = new TimeCond();
			timeCond.beginTime = tmp[0];
			timeCond.endTime = tmp[1];
			arr.add(timeCond);
		}
	}

	/** 属性构造 */
	public void initProperty() {
		this.mapIDArray = new ArrayList<>();
		this.targetArray = new ArrayList<>();
		this.weekArray = new ArrayList<>();
		this.timeArray = new ArrayList<>();
		this.periodInCalendarArray = new ArrayList<>();
		
		
		initArray(this.mapIDArray, this.mapID,false);
		initArray(this.targetArray, this.target,false);
		List<String> tmpWeek  = new ArrayList<>();
		initArray(tmpWeek, this.openday,true);
		
		for(int i = 0;i < tmpWeek.size();i++){
			this.weekArray.add(Integer.parseInt(tmpWeek.get(i)));
		}
		
		initTime(this.timeArray,this.openPeriod);
		initTime(this.periodInCalendarArray,this.periodInCalendar);
	}

	public static class TimeCond {
		public String beginTime;
		public String endTime;
	}

}