package com.wanniu.game.guild.guildFort;

import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.ScheduledFuture;

import com.wanniu.core.game.JobFactory;
import com.wanniu.core.game.LangService;
import com.wanniu.core.game.entity.GPlayer;
import com.wanniu.core.logfs.Out;
import com.wanniu.core.util.DateUtil;
import com.wanniu.game.GWorld;
import com.wanniu.game.common.Const;
import com.wanniu.game.common.Const.CHAT_SCOPE;
import com.wanniu.game.common.Const.TipsType;
import com.wanniu.game.common.msg.MessageUtil;
import com.wanniu.game.player.GlobalConfig;
import com.wanniu.game.player.PlayerUtil;
import com.wanniu.game.player.WNPlayer;

/**
 * @author fangyue
 *
 */
public class GuildFortService {
	public final static int PREPARE_AREA_ID = 54001;
	public final static int PVE_AREA_ID = 54002;
	public final static int PVP_AREA_ID = 54003;
	
	private final static int DAYS_PER_ROUND = GlobalConfig.GuildFort_RoundDays;
	private long MILLISECONDS_PER_ROUND = DAYS_PER_ROUND * 24 * 3600 * 1000;
	private long BID_END_PRE_NOTICE_TIME = 30*DateUtil.ONE_MINUTE_MILLS;
	private long BATTLE_BEGIN_PRE_NOTICE_TIME = 10*DateUtil.ONE_MINUTE_MILLS;
	private long BATTLE_END_PRE_NOTICE_TIME5 = 5*DateUtil.ONE_MINUTE_MILLS;
	private long BATTLE_END_PRE_NOTICE_TIME1 = 1*DateUtil.ONE_MINUTE_MILLS;
	
	private final static String[] WEEKDAY = {"周日","周一","周二","周三","周四","周五","周六"};
	
	private long bidBeginTime;
	private long bidEndTime;
	private long battlePrepareTime;
	private long battleBeginTime;
	private long battleEndTime;
	
	private ScheduledFuture<?> bidBeginNoticeFuture = null;
	private ScheduledFuture<?> bidPreEndNoticeFuture = null;
	private ScheduledFuture<?> battlePreNoticeFuture = null;
	
	private ScheduledFuture<?> f1;
	private ScheduledFuture<?> f2;
	private ScheduledFuture<?> f3;
	private ScheduledFuture<?> f4;
	private ScheduledFuture<?> f5;
	
	
	public static void main(String[] args) throws InterruptedException {
		ScheduledFuture<?> f =JobFactory.addScheduleJob(new Runnable() {
			@Override
			public void run() {
				System.err.println("test future");
			}
		}, 1000, 5*1000);

		Thread.sleep(2000);
		while(!f.cancel(true)) {
			System.err.println("f cancelled:" + f.isCancelled() + " isDone:" + f.isDone());
			Thread.sleep(1000);
		}
		
		System.out.println("f cancelled:" + f.isCancelled() + " isDone:" + f.isDone());
	}
	
	private GuildFortService() {
		initDebugConfig();
		initTimes();
		startTimers();
	}
	
	/**
	 * Only run in debug environment
	 */
	private void initDebugConfig() {
		if(GWorld.DEBUG) {//TODO test by minutes
			MILLISECONDS_PER_ROUND = DAYS_PER_ROUND *1000*60;	
			BID_END_PRE_NOTICE_TIME = 30*DateUtil.ONE_SECOND_MILLS;
			BATTLE_BEGIN_PRE_NOTICE_TIME = 10*DateUtil.ONE_SECOND_MILLS;
			BATTLE_END_PRE_NOTICE_TIME5 = 5*DateUtil.ONE_SECOND_MILLS;
			BATTLE_END_PRE_NOTICE_TIME1 = 1*DateUtil.ONE_SECOND_MILLS;
		}
	}
	
	/**
	 * Only for test
	 */
	public void reload() {
		if(!GWorld.DEBUG) {
			return;
		}
				
//		MessageUtil.sendRollChat(GWorld.__SERVER_ID, "test sendROLLChat.......", Const.CHAT_SCOPE.SYSTEM);
//		MessageUtil.sendRollTipsToAllAnsy("test sendRollToAllAnsy WORLD  ROLL.......");
//		MessageUtil.sendRollTipsToAllAsyn("test sendRollToAllAnsy SYSTEM ROLL...",Const.CHAT_SCOPE.SYSTEM); 
		f1.cancel(true);
		f2.cancel(true);
		f3.cancel(true);
		f4.cancel(true);
		f5.cancel(true);
		cancelBidNotice();
		
		this.initTimes();
		this.startTimers();
		
		System.err.println("init guild service reloaded......");
	}
	
	private void initTimes() {	
		long lastTime = getTimeFromString(GlobalConfig.GuildFort_BattleEndTime);
		long nowTime = System.currentTimeMillis();
		this.battleEndTime = getRealTime(GlobalConfig.GuildFort_BattleEndTime,lastTime,nowTime);
		
		this.bidBeginTime = getRealTime(GlobalConfig.GuildFort_BidStartTime,battleEndTime,nowTime);
		this.bidEndTime = getRealTime(GlobalConfig.GuildFort_BidEndTime,battleEndTime,nowTime);
		this.battleBeginTime = getRealTime(GlobalConfig.GuildFort_BattleStartTime,battleEndTime,nowTime);
		this.battlePrepareTime = battleBeginTime - GlobalConfig.GuildFort_PreStart*60*1000;
		
		logTimeInfo();
	}
	
	private long getRealTime(String timeString,long lastTime,long nowTime) {
		long configTime = getTimeFromString(timeString);

		if(nowTime>lastTime) {//If configured battleEndTime is overed,then get latest next round
			return this.getRecentNextRound(configTime, nowTime);
		}
		return this.getCurrentRound(configTime, lastTime);
	}
	
	
	/**
	 * Return milliseconds of the defined strTime.
	 * strTime's format is like "5,8:25:00",before "," is number of day of week,after "," is time.
	 * @param strTime 
	 * @return
	 */
	private long getTimeFromString(String strTime) {
		String[] strs =  strTime.trim().split(",");
		int dayOfWeek = Integer.parseInt(strs[0])+1;
		Date date = DateUtil.format(strs[1]);
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		c.set(Calendar.DAY_OF_WEEK, dayOfWeek);
		if(dayOfWeek == 1) {//If configure time is Sunday, that's mean is next Sundy for Chinese people 
			c.add(Calendar.DAY_OF_WEEK, 7);
		}
		return c.getTimeInMillis();
	}
	
	private long getNextRoundTime(long currentRoundTime) {
		Date date = new Date(currentRoundTime);
		if(GWorld.DEBUG) {//TODO return next round by minutes while in debug period
			return getDateAfterMinute(date,DAYS_PER_ROUND).getTimeInMillis();
		}
		
		return DateUtil.getDateAfter(date, DAYS_PER_ROUND).getTimeInMillis();	
	}
	
	private static Calendar getDateAfterMinute(Date date, int minute) {
		Calendar now = Calendar.getInstance();
		now.setTime(date);
		now.add(Calendar.MINUTE, minute);
		return now;
	}
	
	public void print(long timeLong) {
		System.err.println(new Date(timeLong).toString());
	}
	
	private long getCurrentRound(long configTime, long lastTime) {
		while(configTime<lastTime && lastTime-configTime > MILLISECONDS_PER_ROUND) {
			configTime = getNextRoundTime(configTime);		
		}
		return configTime;
	}
	
	private long getRecentNextRound(long configTime,long nowTime) {			
		do{
			configTime = getNextRoundTime(configTime);
		}while(configTime<=nowTime);
		return configTime;
	}
	
	private long getDelayTime(long configTime) {
		long now = System.currentTimeMillis();
		if(configTime<=now) {//if configTime less than current time,the timer will be start at next round time 
			configTime = this.getNextRoundTime(configTime);
		}
		long mills = configTime - now;
		return mills;
	}
	
	private String getLogString(String title,long time) {
		Date date = new Date(time);
		return title+"\t" + date.toString();
	}
	
	private void logTimeInfo() {
		StringBuilder sb = new StringBuilder("Guild Fort Times:\r\n");
		sb.append(this.getLogString("bidBeginTime\t", bidBeginTime)).append("\r\n");
		sb.append(this.getLogString("bidEndTime\t", bidEndTime)).append("\r\n");
		sb.append(this.getLogString("battlePrepareTime", battlePrepareTime)).append("\r\n");
		sb.append(this.getLogString("battleBeginTime\t", battleBeginTime)).append("\r\n");
		sb.append(this.getLogString("battleEndTime\t", battleEndTime)).append("\r\n");
		sb.append("-------------------------------------------------------------------");
		Out.error(sb.toString());
	}
	
	private String getBidBeginNoticeMsg() {
		String msgStr = LangService.getValue("GUILDFORT_INBIDDING_NOTICE"); 	
		msgStr = msgStr.replace("{beginTime}", getTimeString(bidBeginTime));
		msgStr = msgStr.replace("{endTime}", getTimeString(bidEndTime));

		return msgStr;
	}


	private static String getTimeString(long time) {
		Date d = new Date(time);
		Calendar c = Calendar.getInstance();
		c.setTime(d);
		String timeStr = DateUtil.format(d,DateUtil.F_HHmmss);
		int index = c.get(Calendar.DAY_OF_WEEK)-1;
		return WEEKDAY[index] + " " + timeStr;
	}
	
	public String getBattleBeginTimeString() {
		return getTimeString(this.battleBeginTime);
	}
	
	public String getBattleEndTimeString() {
		return getTimeString(this.battleEndTime);
	}	
	
	/**
	 * Send roll message to all online players every day when bid time begin
	 */
	private void startBidBeginNotice(long delay) {
		if(bidBeginNoticeFuture!=null) {
			return;
		}
		
		String msgStr = this.getBidBeginNoticeMsg();
		this.bidBeginNoticeFuture = JobFactory.addScheduleJob(new Runnable() {
			@Override
			public void run() {
				GuildFortUtil.sendRollTipsToAllAnsy( msgStr);
			}
		}, delay, DateUtil.ONE_DAY_MILLS);
	}
	
	private long getBidBeginNoticeDelay() {
		long delay = 0;
		long now = System.currentTimeMillis();
		long begin = getTimeFromString(GlobalConfig.GuildFort_BidStartTime);
		
		if(begin > now) {//if beginTime is behinde nowTime
			delay = begin - now;
		}else {
			Date date = new Date(begin);
			delay = DateUtil.getDateAfter(date, 1).getTimeInMillis() - now;
		}
		return delay;
	}
	
	private void startBidPreEndNotice() {
		if(bidPreEndNoticeFuture!=null) {
			return;
		}
		String msgStr = LangService.getValue("GUILDFORT_PREBIDEND_NOTICE");
		bidPreEndNoticeFuture = JobFactory.addDelayJob(new Runnable() {
			@Override
			public void run() {
				GuildFortUtil.sendRollTipsToAllAnsy(msgStr);
			}
		}, this.getBidEndRemainMills()-BID_END_PRE_NOTICE_TIME);
	}
	
	private void startBattlePreNoticeFuture() {
		if(battlePreNoticeFuture!=null) {
			return;
		}
		String msgStr = LangService.getValue("GUILDFORT_PREBATTLE_NOTICE");
		battlePreNoticeFuture = JobFactory.addDelayJob(new Runnable() {
			@Override
			public void run() {
				GuildFortUtil.sendRollTipsToAllAnsy(msgStr);
			}
		}, this.battlePrepareTime-System.currentTimeMillis()-BATTLE_BEGIN_PRE_NOTICE_TIME);
	}
	
	private void startBattleEndPreFiveMinute() {
		JobFactory.addDelayJob(new Runnable() {
			@Override
			public void run() {
				String msgStr = LangService.getValue("GUILDFORT_ENDBATTLE_FIVEMINUTE_NOTICE");
				GuildFortUtil.sendRollTipsToAllAnsy(msgStr);
				startBattleEndPreOneMinute();
			}
		}, battleEndTime-System.currentTimeMillis()-BATTLE_END_PRE_NOTICE_TIME5);
	}
	
	private void startBattleEndPreOneMinute() {
		JobFactory.addDelayJob(new Runnable() {
			@Override
			public void run() {
				String msgStr = LangService.getValue("GUILDFORT_ENDBATTLE_ONEMINUTE_NOTICE");
				GuildFortUtil.sendRollTipsToAllAnsy(msgStr);
			}
		}, battleEndTime-System.currentTimeMillis()-BATTLE_END_PRE_NOTICE_TIME1);
	}
	
	private void cancelBidNotice() {
		if(bidBeginNoticeFuture!=null) {
			bidBeginNoticeFuture.cancel(true);
			bidBeginNoticeFuture = null;
		}
		
		if(bidPreEndNoticeFuture!=null) {
			bidPreEndNoticeFuture.cancel(true);
			bidPreEndNoticeFuture = null;
		}
	}
	
	private void cancelBattleNotice() {
		if(this.battlePreNoticeFuture!=null) {
			battlePreNoticeFuture.cancel(true);
			battlePreNoticeFuture = null;
		}
	}

	private void startTimers() {
		if(this.isInBidTime()) {
			startBidBeginNotice(this.getBidBeginNoticeDelay());
			startBidPreEndNotice();
			startBattlePreNoticeFuture();
		}
		
		long delay = this.getDelayTime(bidBeginTime);
		f1 = JobFactory.addScheduleJob(new Runnable() {
			@Override
			public void run() {
				startBidBeginNotice(0);
				startBidPreEndNotice();
				GuildFortCenter.getInstance().onBidTimeBegin();
			}
		}, delay, MILLISECONDS_PER_ROUND);
		
		delay = this.getDelayTime(bidEndTime);
		f2=JobFactory.addScheduleJob(new Runnable() {
			@Override
			public void run() {
				cancelBidNotice();
				GuildFortCenter.getInstance().onBidTimeEnd();
				startBattlePreNoticeFuture();
			}
		}, delay, MILLISECONDS_PER_ROUND);
		
		delay = this.getDelayTime(battlePrepareTime);
		f3=JobFactory.addScheduleJob(new Runnable() {
			@Override
			public void run() {
				cancelBattleNotice();
				GuildFortCenter.getInstance().onPrepareBattle();
			}
		}, delay, MILLISECONDS_PER_ROUND);
		
		delay = this.getDelayTime(battleBeginTime);
		f4 = JobFactory.addScheduleJob(new Runnable() {
			@Override
			public void run() {
				GuildFortCenter.getInstance().onBattleTimeBegin();
				startBattleEndPreFiveMinute();
			}
		}, delay, MILLISECONDS_PER_ROUND);
		
		delay = this.getDelayTime(battleEndTime);
		f5 = JobFactory.addScheduleJob(new Runnable() {
			@Override
			public void run() {
				GuildFortCenter.getInstance().onBattleTimeEnd();
				initTimes();
			}
		}, delay, MILLISECONDS_PER_ROUND);
		
	}
	

	
	private boolean isIn(long begin,long end) {
		long now = System.currentTimeMillis();		
		return now>begin && now<end ? true : false;
	}
	
	private static class GuildFortServiceHolder {
		public final static GuildFortService INSTANCE = new GuildFortService();
	}

	public static GuildFortService getInstance() {
		return GuildFortServiceHolder.INSTANCE;
	}
	
	public boolean isInBidTime() {
		return isIn(this.bidBeginTime,this.bidEndTime);
	}
	
	public boolean isInBidEndedTime() {
		return isIn(this.bidEndTime,this.battlePrepareTime);
	}
	
	public boolean isInEnterFortTime() {
		return isIn(this.battlePrepareTime,this.battleEndTime);
	}
	
	public boolean isInPrepareBattleTime() {
		return isIn(this.battlePrepareTime,this.battleBeginTime);
	}
	
	public boolean isInBattleTime() {
		return isIn(this.battleBeginTime,this.battleEndTime);
	}
	
	/**
	 * @return whether the current time is between bid begin time and battle end time
	 */
	public boolean isInOpen() {
		return isIn(bidBeginTime,battleEndTime);
	}
	
	private long getBidEndRemainMills() {
		return bidEndTime-System.currentTimeMillis();
	}
	
	public int getBidEndRemainSecond() {
		if(isInBidTime()) {
			return (int)(getBidEndRemainMills()/1000);
		}
		return 0;
	}
	
	public int getBidBeginRemainSecond() {
		if(!isInOpen()) {
			return (int) (bidBeginTime-System.currentTimeMillis())/1000;
		}
		return 0;
	}

}
