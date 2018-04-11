package com.wanniu.game.data.ext;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import com.wanniu.core.GGlobal;
import com.wanniu.core.game.JobFactory;
import com.wanniu.core.game.LangService;
import com.wanniu.core.game.entity.GPlayer;
import com.wanniu.core.logfs.Out;
import com.wanniu.core.util.DateUtil;
import com.wanniu.game.GWorld;
import com.wanniu.game.common.Const;
import com.wanniu.game.common.Const.TipsType;
import com.wanniu.game.common.msg.MessageUtil;
import com.wanniu.game.data.NoticeSendCO;
import com.wanniu.game.player.PlayerUtil;
import com.wanniu.game.player.WNPlayer;

/**
 * 活动开启通知
 * 
 * @author agui
 *
 */
public class NoticeSendExt extends NoticeSendCO {

	public int[] weeks;

	private List<TimeRectangle> times;

	@Override
	public void initProperty() {
		String[] weeks = this.startDay.split(",");
		this.weeks = new int[weeks.length];
		for (int i = 0; i < weeks.length; ++i) {
			this.weeks[i] = Integer.parseInt(weeks[i]);
			if (this.weeks[i] == 7) {
				this.weeks[i] = 1;
			} else {
				this.weeks[i]++;
			}
		}
		String[] sTimes = this.startTime.split(",");
		String[] eTimes = this.endTime.split(",");
		if (sTimes.length != eTimes.length) {
			Out.error(getClass(), " 时间格式不匹配...");
		}
		times = new ArrayList<>(sTimes.length);
		for (int i = 0; i < sTimes.length; i++) {
			String[] sTime = sTimes[i].split(":");
			String[] eTime = eTimes[i].split(":");
			TimeRectangle timeRect = new TimeRectangle();
			timeRect.sHour = Integer.parseInt(sTime[0]);
			timeRect.sMinute = Integer.parseInt(sTime[1]);
			timeRect.eHour = Integer.parseInt(eTime[0]);
			timeRect.eMinute = Integer.parseInt(eTime[1]);
			times.add(timeRect);
		}
		schedeule();
	}

	public boolean isWeek(int week) {
		for (int i = 0; i < weeks.length; ++i) {
			if (weeks[i] == week) {
				return true;
			}
		}
		return false;
	}

	class TimeRectangle {
		int sHour;
		int sMinute;
		int eHour;
		int eMinute;

		boolean isDuration(Calendar cal) {
			int hour = cal.get(Calendar.HOUR_OF_DAY);
			int minute = cal.get(Calendar.MINUTE);
			return ((hour > sHour || hour == sHour && minute >= sMinute) && (hour < eHour || hour == eHour && minute < eMinute));
		}
	}

	boolean isDuration(Calendar cal) {
		for (TimeRectangle rect : times) {
			if (rect.isDuration(cal)) {
				return true;
			}
		}
		return false;
	}

	void noticeOnlinePlayers(String msg) {
		Out.debug("notice msg : ", msg);
		for (GPlayer p : PlayerUtil.getAllOnlinePlayer()) {
			WNPlayer wp = (WNPlayer) p;
			wp.sendSysTip(msg, TipsType.ROLL);
		}
		
		MessageUtil.sendRollChat(GWorld.__SERVER_ID, msg, Const.CHAT_SCOPE.SYSTEM);
	}

	void addNoticeJob(Calendar cal, int time, String msg) {
		cal.add(Calendar.MINUTE, time);
		long delay = cal.getTimeInMillis() - System.currentTimeMillis();
		if (delay > 0) {
			Out.info(DateUtil.format(cal.getTime()), " add notice job : ", msg);
			JobFactory.addDelayJob(() -> {
				noticeOnlinePlayers(msg);
			}, delay);
		}
		cal.add(Calendar.MINUTE, -time);
	}

	// 定期活动通知
	public void schedeule() {
		Calendar cal = Calendar.getInstance();
		if (isWeek(cal.get(Calendar.DAY_OF_WEEK))) {
			long currTime = cal.getTimeInMillis();
			Out.info(this.schName, " schedeule notice send job...");
			cal.set(Calendar.MILLISECOND, 0);
			cal.set(Calendar.SECOND, 0);
			for (TimeRectangle tr : times) {
				
				// 活动开始前
				{
					cal.set(Calendar.HOUR_OF_DAY, tr.sHour);
					cal.set(Calendar.MINUTE, tr.sMinute);
					addNoticeJob(cal, -firstTime, String.format(showNotice, firstTime));
					addNoticeJob(cal, -secondTime, String.format(showNotice, secondTime));
				}

				// 活动期间
				{
					Runnable durationRun = new Runnable() {
						@Override
						public void run() {
							Calendar newCal = Calendar.getInstance();
							if (tr.isDuration(newCal)) {
								noticeOnlinePlayers(showNotice2);
							}
							newCal.set(Calendar.SECOND, 0);
							newCal.add(Calendar.MINUTE, spaceTime2);
							if (tr.isDuration(newCal)) {
								JobFactory.addDelayJob(this, newCal.getTimeInMillis() - System.currentTimeMillis());
							}
						}
					};
					long actTime = cal.getTimeInMillis();
					if (currTime > actTime) {
						int spaceMinute = spaceTime2 * GGlobal.TIME_MINUTE;
						actTime += (((currTime - actTime) / spaceMinute + 1) * spaceMinute);
					}
					JobFactory.addDelayJob(durationRun, actTime - currTime);
				}

				// 活动结束前
				{
					cal.set(Calendar.HOUR_OF_DAY, tr.eHour);
					cal.set(Calendar.MINUTE, tr.eMinute);
					addNoticeJob(cal, -firstTime2, String.format(showNotice3, firstTime2));
					addNoticeJob(cal, -secondTime2, String.format(showNotice3, secondTime2));
				}

				// 活动结束
				addNoticeJob(cal, 0, LangService.format("ACTIVITY_OVER_SEND", schName));
			}
		}
	}

}
