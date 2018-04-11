package com.wanniu.game.guild.guildFort;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.wanniu.core.game.LangService;
import com.wanniu.core.game.entity.GPlayer;
import com.wanniu.core.logfs.Out;
import com.wanniu.core.util.DateUtil;
import com.wanniu.game.GWorld;
import com.wanniu.game.common.Const;
import com.wanniu.game.common.msg.MessageUtil;
import com.wanniu.game.data.GameData;
import com.wanniu.game.data.ext.GuildFortExt;
import com.wanniu.game.guild.guildFort.dao.GuildFortBattleReportPO;
import com.wanniu.game.guild.guildFort.dao.GuildFortDao;
import com.wanniu.game.guild.guildFort.dao.GuildFortPO;
import com.wanniu.game.guild.guildFort.dao.GuildFortReportPO;
import com.wanniu.game.player.GlobalConfig;
import com.wanniu.game.player.PlayerUtil;
import com.wanniu.game.player.WNPlayer;

public class GuildFortCenter {	
	public static enum Status {
		NOT_BEGIN(0), 	// 未开始
		INTIME_NOTBID(1), 	// 报名阶段，还未下注
		BID_ENDED(2),	//报名结束，等待开始
		IN_PREPARE(3),	//战斗准备中
		IN_BATTLE(4), //战斗进行中
		INTIME_BIDDED(5),//报名阶段，已下注
		;	// 进行中
		public int value;

		/**
		 * 0: 未开始 1：报名阶段，可以下注，2:报名结束，等待开始, 3：准备中 4：进行中
		 * @param value
		 */
		private Status(int value) {
			this.value = value;
		}
	}
	
	private final static int MAX_BID_FORT_NUM = 3;
	private Map<Integer,GuildFort> forts = new HashMap<>();
	private List<GuildFortReportPO> reports = new ArrayList<>();
	
	
	private GuildFortCenter() {
		init();
	}
	
	private void init() {	
		for(GuildFortExt fortExt : GameData.GuildForts.values()) { 
			GuildFort fort = new GuildFort(fortExt);
			GuildFortPO po = GuildFortDao.getFort(fortExt.iD);
			if(po!=null) {
				fort.setPo(po);				
			}
			forts.put(fortExt.iD, new GuildFort(fortExt));
		}	
		reports = GuildFortDao.getReports();
	}
	
	
	private static class GuildFortCenterHolder {
		public final static GuildFortCenter INSTANCE = new GuildFortCenter();
	}

	public static GuildFortCenter getInstance() {
		return GuildFortCenterHolder.INSTANCE;
	}
	
	public int getStatus(boolean isBidded) {
		GuildFortService service = GuildFortService.getInstance();
		if(service.isInBidTime()) {
			if(isBidded) {
				return Status.INTIME_BIDDED.value;
			}else {
				return Status.INTIME_NOTBID.value;
			}
		}else if(service.isInBidEndedTime()) {
			return Status.BID_ENDED.value;
		}else if(service.isInPrepareBattleTime()) {
			return Status.IN_PREPARE.value;
		}else if(service.isInBattleTime()) {
			return Status.IN_BATTLE.value;
		}else {
			return Status.NOT_BEGIN.value;
		}
	}
	
	public void onBidTimeBegin() {
		GuildFortUtil.mailToAllGuildManagerWhenBidBegin();
		GuildFortUtil.pushRedPointToAll();
	}
	
	
	
	public void onBidOperation() {
		saveForts();
	}
	
	private String getBidOverReportMessage() {
		StringBuilder title = new StringBuilder(LangService.getValue("GUILDFORT_BIDRESULT_TITILE_NOTICE"));		
		String msgStr = LangService.getValue("GUILDFORT_BIDRESULT_LOOP_NOTICE");
		String noBidderStr = LangService.getValue("GUILDFORT_BIDRESULT_NOBIDDER_NOTICE");
		for(GuildFort fort: forts.values()) {
			String str = msgStr.replace("{fortname}", fort.getName());
			String defName = fort.getDefenserName();
			String attName = fort.getAttackerName();
			if(defName.equals("") && attName.equals("")) {
				str = noBidderStr.replace("{fortname}", fort.getName());
			}else if(!defName.equals("") && attName.equals("")) {
				str  = str.replace("{defguildname}", defName).replace("{attguildname}", "据点守卫军");
			}else {
				str = str.replace("{defguildname}", defName).replace("{attguildname}", attName);
			}
			
			title.append(str);
		}
		
		return title.toString();
	}
		
	public void onBidTimeEnd() {
		for(GuildFort fort: forts.values()) {
			fort.onBidOver();
		}	
		saveForts();
		GuildFortUtil.sendRollTipsToAllAsyn(getBidOverReportMessage(), Const.CHAT_SCOPE.SYSTEM);
	}
	
	public void onPrepareBattle() {
		Map<String,Integer> members = new HashMap<>();
		for(GuildFort fort: forts.values()) {
			List<String> ids = fort.getAllMemberIds();
			for(String id: ids) {
				members.put(id, 0);//Filter out the repeated player ids
			}
		}
		
		GuildFortUtil.pushRedPoint(members.keySet());
	}
	
	public void onBattleTimeBegin() {	
		for(GuildFort fort: forts.values()) {
			fort.onBattleBegin();
		}
		clearAllDailyAwards();
	}
	
	
	public void onBattleTimeEnd() {
		for(GuildFort fort: forts.values()) {
			fort.onBattleOver();
		}		
		saveForts();
		GuildFortUtil.pushRedPointToAll();
		resetAllDailyAwards();
	}
	
	
	private void resetAllDailyAwards() {
		GWorld.getInstance().ansycExec(() -> {//update every online player's daily awards' status
			for (GPlayer p : PlayerUtil.getAllOnlinePlayer()) {
				WNPlayer wp = (WNPlayer) p;
				wp.guildFortManager.recalcDailyAwards();
			}
		});
		Out.info("Guildfort daily awards recalc completed...");
	}
	
	private void clearAllDailyAwards() {
		GWorld.getInstance().ansycExec(() -> {//update every online player's daily awards' status
			for (GPlayer p : PlayerUtil.getAllOnlinePlayer()) {
				WNPlayer wp = (WNPlayer) p;
				wp.guildFortManager.clearDailyAwards();
			}
		});
		Out.info("Guildfort daily awards cleared...");
	}

		
	public void addBattleReport(GuildFortBattleReportPO battleReport) {
		String date = DateUtil.getDate();
		if(GWorld.DEBUG) {//TODO 测试期间每10分钟一轮，战报被反复覆盖，所以key取分钟时间
			date = DateUtil.getDateTime();
			date = date.substring(0, date.length()-3);
		}
		
		GuildFortReportPO report = null;		
		for(GuildFortReportPO rep : reports) {
			if(rep.date.equals(date)) {
				report = rep;
				break;
			}
		}
		
		if(report==null) {
			report = new GuildFortReportPO();
			reports.add(report);
		}
		report.date = date;
		report.battleReports.put(battleReport.fortId, battleReport);
		
		if(reports.size()>=GlobalConfig.GuildFort_BattleRecordNum) {
			reports.remove(0);
		}
		
		saveReports();
	}
	
	public GuildFort getFort(int fortId) {
		return forts.get(fortId);
	}
	
	public List<GuildFort> getOccupiedForts(String guildId){
		List<GuildFort> list = new ArrayList<>();
		for(GuildFort fort : forts.values()) {
			if(fort.isOccupier(guildId)) {
				list.add(fort);
			}
		}
		
		return list;
	}
	
	public boolean isBitFortExceeded(String guildId) {
		int num = 0;
		for(GuildFort fort : forts.values()) {
			if(fort.isInBidders(guildId)) {
				num++;
			}
		}
		
		return num >= MAX_BID_FORT_NUM ;
	}
	
	public Collection<GuildFort> getAllGuildFort(){
		return forts.values();
	}
	
	public List<GuildFortReportPO> getReports(){
		return this.reports;
	}
	
	private void saveForts() {
		for(GuildFort fort : forts.values()) {
			GuildFortDao.saveFort(fort.getPo());
		}
	}
	
	private void saveReports() {
		GuildFortDao.saveReports(reports);
	}
	
	public void test() {
		//TODO some testing...
//		GuildFortMemberPO a = new GuildFortMemberPO();
//		a.playerId = "a1";
//		GuildFortContenderPO b1 = new GuildFortContenderPO();
//		GuildFortContenderPO b2 = new GuildFortContenderPO();
//		GuildFortBattleReportPO c = new GuildFortBattleReportPO();
//		c.defenser = b1;
//		b1.members.put(a.playerId, a);
//		
//		c.attacker = b2;
//		this.addBattleReport(c);
//		try {
//			Thread.sleep(1000);
//		} catch (InterruptedException e) {
//			e.printStackTrace();
//		}
////		saveForts();
//		saveReports();
//		List<GuildFortReportPO> p = GuildFortDao.getReports();
	}
	
	public void onCloseGame() {
		saveForts();
		saveReports();
	}

}
