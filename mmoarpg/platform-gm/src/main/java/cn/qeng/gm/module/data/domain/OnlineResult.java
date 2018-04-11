/*
 * Copyright © 2017 qeng.cn All Rights Reserved.
 * 
 * 感谢您加入清源科技，不用多久，您就会升职加薪、当上总经理、出任CEO、迎娶白富美、从此走上人生巅峰
 * 除非符合本公司的商业许可协议，否则不得使用或传播此源码，您可以下载许可协议文件：
 * 
 * 		http://www.noark.xyz/qeng/LICENSE
 *
 * 1、未经许可，任何公司及个人不得以任何方式或理由来修改、使用或传播此源码;
 * 2、禁止在本源码或其他相关源码的基础上发展任何派生版本、修改版本或第三方版本;
 * 3、无论你对源代码做出任何修改和优化，版权都归清源科技所有，我们将保留所有权利;
 * 4、凡侵犯清源科技相关版权或著作权等知识产权者，必依法追究其法律责任，特此郑重法律声明！
 */
package cn.qeng.gm.module.data.domain;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * 
 *
 * @author 周明凯(zhoumingkai@qeng.cn)
 */
public class OnlineResult {
	private LocalDate date;
	private List<Online> onlineData = new ArrayList<>();

	public LocalDate getDate() {
		return date;
	}

	public void setDate(LocalDate date) {
		this.date = date;
	}

	public OnlineResult(LocalDate today) {
		this.date = today;
	}

	public List<Online> getOnlineData() {
		return onlineData;
	}

	public void setOnlineData(List<Online> onlineData) {
		this.onlineData = onlineData;
	}

	public static class Online {
		private String localDate;// 具体日期
		private String localTime;// 具体时间
		private int todayData;// 今日数据
		private int yesterData;// 昨日数据

		public int getTodayData() {
			return todayData;
		}

		public void setTodayData(int todayData) {
			this.todayData = todayData;
		}

		public int getYesterData() {
			return yesterData;
		}

		public void setYesterData(int yesterData) {
			this.yesterData = yesterData;
		}

		public String getLocalDate() {
			return localDate;
		}

		public void setLocalDate(String localDate) {
			this.localDate = localDate;
		}

		public String getLocalTime() {
			return localTime;
		}

		public void setLocalTime(String localTime) {
			this.localTime = localTime;
		}

		public Online(String localDate, String localTime, int todayData, int yesterData) {
			this.localDate = localDate;
			this.localTime = localTime;
			this.todayData = todayData;
			this.yesterData = yesterData;
		}
	}
}
