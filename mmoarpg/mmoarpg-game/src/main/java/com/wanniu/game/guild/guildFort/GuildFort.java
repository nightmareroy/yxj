package com.wanniu.game.guild.guildFort;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.wanniu.core.game.JobFactory;
import com.wanniu.core.game.LangService;
import com.wanniu.core.logfs.Out;
import com.wanniu.core.util.DateUtil;
import com.wanniu.game.area.Area;
import com.wanniu.game.area.AreaUtil;
import com.wanniu.game.area.DamageHealVO;
import com.wanniu.game.common.Const;
import com.wanniu.game.common.Const.GOODS_CHANGE_TYPE;
import com.wanniu.game.common.Const.TipsType;
import com.wanniu.game.common.Utils;
import com.wanniu.game.common.msg.MessagePush;
import com.wanniu.game.data.ext.GuildFortExt;
import com.wanniu.game.guild.GuildServiceCenter;
import com.wanniu.game.guild.GuildUtil;
import com.wanniu.game.guild.guildFort.dao.GuildFortBattleReportPO;
import com.wanniu.game.guild.guildFort.dao.GuildFortBidderPO;
import com.wanniu.game.guild.guildFort.dao.GuildFortContenderPO;
import com.wanniu.game.guild.guildFort.dao.GuildFortMemberPO;
import com.wanniu.game.guild.guildFort.dao.GuildFortPO;
import com.wanniu.game.item.ItemUtil;
import com.wanniu.game.item.NormalItem;
import com.wanniu.game.mail.MailUtil;
import com.wanniu.game.mail.SysMailConst;
import com.wanniu.game.mail.data.MailData;
import com.wanniu.game.mail.data.MailData.Attachment;
import com.wanniu.game.mail.data.MailSysData;
import com.wanniu.game.player.GlobalConfig;
import com.wanniu.game.player.PlayerUtil;
import com.wanniu.game.player.WNPlayer;
import com.wanniu.game.poes.GuildPO;
import com.wanniu.game.rank.RankType;

import pomelo.area.GuildFortHandler.FortGuildStatistics;
import pomelo.area.GuildFortHandler.OnGuildFortPush;
import pomelo.item.ItemOuterClass.MiniItem;

/**
 * @author fangyue
 * Each GuildFort object represent a fort,which manage all actions of this fort
 */
public class GuildFort {
	private GuildFortExt fortConfig = null;
	private GuildFortPO po = null;
	
	private boolean battleOver = false;

	
	public GuildFortContenderPO defenser = null;
	public GuildFortContenderPO attacker = null;
	
	private GuildFortPveArea defPrepareArea = null;
	private GuildFortPveArea attPrepareArea = null;
	private GuildFortPveArea defPveArea = null;
	private GuildFortPveArea attPveArea = null;
	private GuildFortPvpArea pvpArea = null;
	
	
	public GuildFort() {
		
	}
	
	public GuildFort(GuildFortExt fortConfig) {
		this.fortConfig = fortConfig;
		this.po = new GuildFortPO();
		this.po.fortId = fortConfig.iD;
	}
	
	public void setPo(GuildFortPO guildFortPO) {
		this.po = guildFortPO;
	}
	
	public GuildFortPO getPo() {
		return this.po;
	}
	
	/**
	 * @return 
	 */
	public List<MiniItem> getWinnerReward(){
		List<MiniItem> res = new ArrayList<>();
		for(String key : this.fortConfig.winnerReward.keySet()) {
			MiniItem.Builder data = ItemUtil.getMiniItemData(key, fortConfig.winnerReward.get(key));
			res.add(data.build());
		}
		
		return res;
	}
	
	public List<MiniItem> getDailyAward(){
		List<MiniItem> res = new ArrayList<>();
		for(String key : this.fortConfig.dailyReward.keySet()) {
			MiniItem.Builder data = ItemUtil.getMiniItemData(key, fortConfig.dailyReward.get(key));
			res.add(data.build());
		}
		
		return res;
	}
	
	public List<NormalItem> generateDailyAward(){
		return ItemUtil.createItemsByItemCode(fortConfig.dailyReward);
	}

	
	/**
	 * @return This fort's id which the producer configured in file
	 */
	public int getId() {
		return this.fortConfig.iD;
	}
	
	/**
	 * Get occupied guild's guildId
	 * @return If no occupier, the result will be null
	 */
	public String getOccupyGuildId() {
		return po.occupyGuildId;
	}
	
	
	public boolean isOccupier(String guildId) {
		if(po.occupyGuildId!=null 
				&& po.occupyGuildId.equals(guildId)) {
			return true;
		}
		return false;
	}
	

	
	/**
	 * When guild managers commit bidding funds
	 * @param guildId
	 * @param fund
	 */
	public int commitBidFund(String guildId,int fund) {
		GuildPO guild = GuildServiceCenter.getInstance().getGuild(guildId);
		int sumFund = 0;
		if(guild!=null) {
			Out.info("Apply fund guild id:",guild.id," original fund:",guild.fund," to deduct fund:" +fund);
			guild.fund -= fund;
			
			boolean isIn = false; 			
			for(GuildFortBidderPO bidder : po.bidders) {
				if(bidder.guildId.equals(guildId)) {
					if(bidder.fund + fund > Integer.MAX_VALUE) {
						Out.error("Bid fund overflowed, guildId:"+guildId," biddedFund:",bidder.fund," toAdd fund:",fund);
						return sumFund;
					}
					bidder.fund += fund;
					sumFund = bidder.fund;				
					isIn = true;
					break;
				}
			}
			
			if(!isIn) {
				GuildFortBidderPO bidder = new GuildFortBidderPO(guildId,fund);
				po.bidders.add(bidder);
				sumFund = bidder.fund;	
			}
		}else {
			Out.warn("Can't find the guild when commitBidFund by guildId=",guildId," and fund=",fund);
		}
		
		return sumFund;
	}
	
	/**
	 * Extract all bidded funds and remove from bidder list and return deducted fund to the guild
	 * @param guildId
	 * @return the extracted funds after deducting 10 percent commission fee
	 * 	if cant't find the guild ,return 0
	 */
	public int extractBidFund(String guildId) {
		int index = this.getInBiddersIndex(guildId);
		if(index !=-1 && index<po.bidders.size()) {
			GuildFortBidderPO bidder = po.bidders.remove(index);
			Out.info("Apply cancel all bidded fund, guild id=",guildId,"  fund=",bidder.fund);

			return returnBidFund(guildId, bidder.fund);//deduct 10% commission fee
		}
		return 0;
	}
	
	private int getInBiddersIndex(String guildId) {
		int index = 0;
		for(GuildFortBidderPO bidder : po.bidders) {
			if(bidder.guildId.equals(guildId)) {
				return index;
			}
			index++;
		}	
		return -1;
	}
	

	/**
	 * 【{guildname}】以{fund}下注资金获得{fortname}据点的对战资格，开战时间为{fighttime}
	 * @param guildId
	 * @param fund
	 */
	private void sendBidSucMail(String guildId,int fund) {
		GuildPO guild = GuildServiceCenter.getInstance().getGuild(guildId);
		
		MailSysData mailData = new MailSysData(SysMailConst.GuildFortBidSuccess);
		mailData.replace = new HashMap<>();
		mailData.replace.put("guildname",  guild.name);
		mailData.replace.put("fund",  String.valueOf(fund));
		mailData.replace.put("fortname",  this.getName());
		mailData.replace.put("fighttime",  GuildFortService.getInstance().getBattleBeginTimeString());
		Set<String> ids = GuildUtil.getGuildMemberIdList(guildId);
		MailUtil.getInstance().sendMailToSomePlayer(ids.toArray(new String[ids.size()]), mailData, GOODS_CHANGE_TYPE.guild_mail);
	}
	

	/**
	 * 你所在仙盟【{guildname}】以{fund}资金下注{fortname}据点
	 * @param guildId
	 * @param fund
	 */
	private void sendBidFailMail(String guildId,int fund) {
		GuildPO guild = GuildServiceCenter.getInstance().getGuild(guildId);
		
		MailSysData mailData = new MailSysData(SysMailConst.GuildFortBidFailed);
		mailData.replace = new HashMap<>();
		mailData.replace.put("guildname",  guild.name);
		mailData.replace.put("fund",  String.valueOf(fund));
		mailData.replace.put("fortname",  this.getName());

		Set<String> ids = GuildUtil.getGuildMemberIdList(guildId);
		MailUtil.getInstance().sendMailToSomePlayer(ids.toArray(new String[ids.size()]), mailData, GOODS_CHANGE_TYPE.guild_mail);
	}
	

	/**
	 * 【{fortname}】据点的争夺战中以{point}积分战胜【{guildname}】
	 * @param guild
	 * @param score
	 * @param opponent
	 */
	private void sendBattleWinMail(GuildPO guild,int score,GuildFortContenderPO opponent) {
		String opponentName = "";
		if(opponent!=null) {
			opponentName = opponent.guildName;
		}
		
		MailSysData mailData = new MailSysData(SysMailConst.GuildFortNotifyBattleEndForWin);
		mailData.replace = new HashMap<>();
		mailData.replace.put("fortname",  this.getName());
		mailData.replace.put("point",  String.valueOf(score));
		mailData.replace.put("guildname",  opponentName);

		ArrayList<Attachment> list = new ArrayList<>();
		for (String itemCode : this.fortConfig.winnerReward.keySet()) {
			MailData.Attachment attach = new MailData.Attachment();
			attach.itemCode = itemCode;
			attach.itemNum = fortConfig.winnerReward.get(itemCode);
			list.add(attach);
		}
		mailData.attachments = list;
		
		Set<String> ids = GuildUtil.getGuildMemberIdList(guild.id);
		MailUtil.getInstance().sendMailToSomePlayer(ids.toArray(new String[ids.size()]), mailData, GOODS_CHANGE_TYPE.guild_mail);
	}

	
	/**
	 * 在仙盟在【{fortname}】据点的争夺战中以{point}积分惜败【{guildname}】
	 * @param guild
	 * @param score
	 * @param opponent
	 */
	private void sendBattleFailMail(GuildPO guild,int score,GuildFortContenderPO opponent) {
		String opponentName = "";
		if(opponent!=null) {
			opponentName = opponent.guildName;
		}
		
		MailSysData mailData = new MailSysData(SysMailConst.GuildFortNotifyBattleEndForLose);
		mailData.replace = new HashMap<>();
		mailData.replace.put("fortname",  this.getName());
		mailData.replace.put("point",  String.valueOf(score));
		mailData.replace.put("guildname",  opponentName);

		ArrayList<Attachment> list = new ArrayList<>();
		for (String itemCode : this.fortConfig.loserReward.keySet()) {
			MailData.Attachment attach = new MailData.Attachment();
			attach.itemCode = itemCode;
			attach.itemNum = fortConfig.loserReward.get(itemCode);
			list.add(attach);
		}
		mailData.attachments = list;
		
		Set<String> ids = GuildUtil.getGuildMemberIdList(guild.id);
		MailUtil.getInstance().sendMailToSomePlayer(ids.toArray(new String[ids.size()]), mailData, GOODS_CHANGE_TYPE.guild_mail);
	}
	
	/**
	 * whether the specified guildId is already bidded in this fort
	 * @param guildId
	 * @return
	 */
	public boolean isInBidders(String guildId) {
		return getInBiddersIndex(guildId) != -1;
	}
	
	/**
	 * When bidding overed
	 */
	public void onBidOver() {
		calcBidResult();
	}
	
	
	private void buildReport() {
		GuildFortBattleReportPO report = new GuildFortBattleReportPO();
		report.fortId = this.fortConfig.iD;
		report.fortName = this.fortConfig.name;
		if(this.defenser!=null) {
			this.defenser.build();
			report.defenser = this.defenser;
		}else {
			report.defenser = new GuildFortContenderPO();
		}
		
		if(this.attacker!=null) {
			this.attacker.build();
			report.attacker = this.attacker;
		}else {
			report.attacker = new GuildFortContenderPO();
		}

		GuildFortCenter.getInstance().addBattleReport(report);
	}
	
	/**
	 * Count winning battle times and send paticipant a mail that's contained the battle result
	 * @param po
	 * @param isWin
	 * @param opponent
	 */
	private void updateWinTimes(GuildFortContenderPO po,boolean isWin,GuildFortContenderPO opponent) {
		GuildPO guild = GuildServiceCenter.getInstance().getGuild(po.guildId);
		if(guild!=null) {
			guild.fortInfo.onStat(isWin);
			if(isWin) {
				sendBattleWinMail(guild, po.getScore(),opponent);
				po.setWinner(true);
			}else {
				sendBattleFailMail(guild, po.getScore(),opponent);
			}
		}else {
			Out.error("can't find the guild when updateWinTimes, guildId=" ,po.guildId, " isWin=" ,isWin);
		}
	}
	
	private void setNobodyOccupied() {
		po.occupyGuildId = null; //occupy status is null
	}
	
	private void calcBidResult() {
		List<GuildFortBidderPO> toSort = po.bidders;
		toSort.sort(new Comparator<GuildFortBidderPO>() {
			@Override
			public int compare(GuildFortBidderPO o1, GuildFortBidderPO o2) {	
				if(o1.fund<o2.fund) {
					return 1;
				}else if(o1.fund>o2.fund) {
					return -1;
				}				
				return 0;
			}
		});
				
		int size = toSort.size();
		if(size==0) {//no contender
			this.defenser = null;
			this.attacker = null;
		}else if(size==1) {//only one contender
			this.defenser = new GuildFortContenderPO(toSort.get(0).guildId);
			this.attacker = null;
			sendBidSucMail(defenser.guildId,toSort.get(0).fund);
		}else {//first contender is defenser, the second one is attacker
			this.defenser = new GuildFortContenderPO(toSort.get(0).guildId);
			this.attacker = new GuildFortContenderPO(toSort.get(1).guildId);
			sendBidSucMail(defenser.guildId,toSort.get(0).fund);
			sendBidSucMail(attacker.guildId,toSort.get(1).fund);
		}
		
		for(int i=2; i< size;i++) {
			GuildFortBidderPO bidder = toSort.get(i);
			returnBidFund(bidder.guildId, bidder.fund);
			sendBidFailMail(bidder.guildId, bidder.fund);
		}		
	}
	
	private void calcBattleResult() {
		po.occupyGuildId = null; 
		if(this.defenser==null && this.attacker==null) {//No any guild bidded
			po.occupyGuildId = null; 
			return;
		}
		if(defenser!=null && attacker==null) {//Only the defenser's show
			if(this.defenser.getScore()>=GlobalConfig.GuildFort_MinWinPoint) {
				po.occupyGuildId = defenser.getGuildId();
				updateWinTimes( defenser, true,attacker);
			}else {
				po.occupyGuildId = null; 
				this.updateWinTimes( defenser, false,attacker);
			}
			return;
		}
		if(defenser!=null && attacker!=null) {		
			if(defenser.getScore()<attacker.getScore()) {
				if(attacker.getScore()>=GlobalConfig.GuildFort_MinWinPoint) {//The winner is attacker
					po.occupyGuildId = attacker.getGuildId();
					this.updateWinTimes( attacker, true,defenser);
				}else {
					this.updateWinTimes( attacker, false,defenser);
				}
				this.updateWinTimes( defenser, false,attacker);
			}else {
				if(this.defenser.getScore()>=GlobalConfig.GuildFort_MinWinPoint) {//The winner is defenser
					po.occupyGuildId = defenser.getGuildId();
					this.updateWinTimes( defenser, true,attacker);
				}else {
					this.updateWinTimes( defenser, false,attacker);
				}
				this.updateWinTimes( attacker, false,defenser);
			}
			return;
		}
		
		po.occupyGuildId = null; 
	}
	
	
	private int returnBidFund(String guildId,int fund) {
		GuildPO guild = GuildServiceCenter.getInstance().getGuild(guildId);
		if(guild!=null) {
			int deductedFund = fund*(100-GlobalConfig.GuildFort_BackRate)/100;//deduct 10% commission fee
			guild.fund += deductedFund;
			Out.info("return Bid Fund guild id=",guildId,"  fund=",fund);
			return deductedFund;
		}else {
			Out.warn("Can't find the guild when return bidFund by guildId=",guildId," return fund=",fund);
		}
		
		return 0;
	}

	private void broadcastBattleOver() {
		String guildId = getOccupyGuildId();
		if(guildId != null) {
			GuildPO guild = GuildServiceCenter.getInstance().getGuild(guildId);
			if(guild!=null) {
				String msgStr = LangService.getValue("GUILDFORT_BATTLEEND_NOTICE");				
				msgStr = msgStr.replace("{fortname}", getName()).replace("{guildname}", guild.name);
				GuildFortUtil.sendRollTipsToAllAnsy(msgStr);
			}else {
				Out.warn("Can't find guild by guildId = ",guildId);
			}
		}else {
			Out.info(getName()," no one occupied...");
		}
	}
	
	/**
	 * @return the ids of all members of the two warring parties
	 */
	public List<String> getAllMemberIds(){
		List<String> ids = new ArrayList<>();		
		if(defenser!=null) {
			ids.addAll(GuildUtil.getGuildMemberIdList(defenser.guildId));
		}		
		if(attacker!=null) {
			ids.addAll(GuildUtil.getGuildMemberIdList(attacker.guildId));
		}
		
		return ids;
	}
	
	/**
	 * When battle begined
	 */
	public void onBattleBegin() {
		setNobodyOccupied();
		if(this.defenser==null && this.attacker==null) {//no guild bid fund
			setBattleOver(true);
			dispose();
			return;
		}
		setBattleOver(false);
	}
	
	/**
	 * When battle overed
	 */
	public synchronized void onBattleOver() {
		if(isBattleOver()) {
			Out.warn("guild fort battle has already overed!!!");
			return;
		}
		setBattleOver(true);
		calcBattleResult();
		pushBattleResult();
		updateRank();
		buildReport();
		broadcastBattleOver();
		dispose();
	}
	
	private void closeAllArea() {
		if(this.pvpArea!=null) {
			AreaUtil.closeArea(pvpArea.instanceId);
			pvpArea = null;
		}
		if(this.defPveArea!=null) {
			AreaUtil.closeArea(defPveArea.instanceId);
			defPveArea = null;
		}
		if(this.attPveArea!=null) {
			AreaUtil.closeArea(attPveArea.instanceId);
			attPveArea = null;
		}
		if(this.defPrepareArea!=null) {
			AreaUtil.closeArea(defPrepareArea.instanceId);
			defPrepareArea = null;
		}
		if(this.attPrepareArea!=null) {
			AreaUtil.closeArea(attPrepareArea.instanceId);
			attPrepareArea = null;
		}
	}
	
	private void dispose() {
		po.bidders.clear();
		JobFactory.addDelayJob(new Runnable() {		
			@Override
			public void run() {
				closeAllArea();				
			}
		}, 5*DateUtil.ONE_SECOND_MILLS);		
		this.defenser=null;
		this.attacker=null;
	}
	
	public boolean isBattleOver() {
		return battleOver;
	}
	
	private void setBattleOver(boolean isOver) {
		this.battleOver = isOver;
	}
	
	/**
	 * whether the specified player is a member of the two engagement
	 * @param player
	 * @return
	 */
	public boolean isBattleMember(WNPlayer player) {
		return getContenderByPlayer(player)==null? false:true;
	}
	
	/**
	 * When player request to enter prepare area
	 * @param player
	 * @return
	 */
	public Area requestEnterPrepareArea(WNPlayer player) {
		String guildId = player.guildManager.getGuildId();
		if(isDefenserMember(guildId)) {
			if(this.defPrepareArea==null) {
				Map<String, Object> userData = Utils.ofMap("fortId", this.getId());
				this.defPrepareArea = (GuildFortPveArea) AreaUtil.dispatchByAreaId(player, GuildFortService.PREPARE_AREA_ID,userData);
			}else {
				AreaUtil.changeTeamArea(player, defPrepareArea.getAreaData());
			}
			return this.defPrepareArea;
		}
		
		if(isAttackerMember(guildId)) {
			if(this.attPrepareArea==null) {
				Map<String, Object> userData = Utils.ofMap("fortId", this.getId());
				this.attPrepareArea = (GuildFortPveArea) AreaUtil.dispatchByAreaId(player, GuildFortService.PREPARE_AREA_ID, userData);
			}else {
				AreaUtil.changeTeamArea(player, attPrepareArea.getAreaData());
			}
			return this.attPrepareArea;
		}
		
		return null;
	}
	
	public Area requestEnterPveArea(WNPlayer player) {
		if(isDefenserMember(player.guildManager.getGuildId())) {
			if(this.defPveArea==null) {
				Map<String, Object> userData = Utils.ofMap("fortId", this.getId());
				this.defPveArea = (GuildFortPveArea) AreaUtil.dispatchByAreaId(player, GuildFortService.PVE_AREA_ID, userData);
			}else {
				AreaUtil.changeTeamArea(player, defPveArea.getAreaData());
			}
			return this.defPveArea;
		}
		
		if(isAttackerMember(player.guildManager.getGuildId())) {
			if(this.attPveArea==null) {
				Map<String, Object> userData = Utils.ofMap("fortId", this.getId());
				this.attPveArea = (GuildFortPveArea) AreaUtil.dispatchByAreaId(player, GuildFortService.PVE_AREA_ID, userData);
			}else {
				AreaUtil.changeTeamArea(player, attPveArea.getAreaData());
			}
			return this.attPveArea;
		}
		
		return null;
	}
	
	public Area requestEnterPvpArea(WNPlayer player) {
		if(this.pvpArea==null) {
			Map<String, Object> userData = Utils.ofMap("fortId", this.getId());
			this.pvpArea = (GuildFortPvpArea) AreaUtil.dispatchByAreaId(player, GuildFortService.PVP_AREA_ID,userData);
		}else {
			AreaUtil.changeTeamArea(player, pvpArea.getAreaData());		
		}	
		
		return this.pvpArea;
	}
	
	public GuildFortContenderPO getContenderByPlayer(WNPlayer player) {
		return getContenderByGuildId(player.guildManager.getGuildId());
	} 
	

	private MessagePush generateDefenserReport(FortGuildStatistics.Builder defBuilder,FortGuildStatistics.Builder attBuilder) {
		OnGuildFortPush.Builder res = OnGuildFortPush.newBuilder();
		res.setS2CCode(Const.CODE.OK);
		
		if(defBuilder!=null) {
			res.setOwnGuild(defBuilder);
		}
		if(attBuilder!=null) {
			res.setEnemyGuild(attBuilder);
		}
		
		return new MessagePush("area.guildFortPush.onGuildFortPush", res.build());
	}
	
	private MessagePush generateAttackerReport(FortGuildStatistics.Builder defBuilder,FortGuildStatistics.Builder attBuilder) {
		OnGuildFortPush.Builder res = OnGuildFortPush.newBuilder();
		res.setS2CCode(Const.CODE.OK);
		
		if(defBuilder!=null) {
			res.setEnemyGuild(defBuilder);
		}
		if(attBuilder!=null) {		
			res.setOwnGuild(attBuilder);
		}
		
		return new MessagePush("area.guildFortPush.onGuildFortPush", res.build());
	}
	
	private FortGuildStatistics.Builder getStatistics(GuildFortContenderPO contender){
		if(contender==null) {
			return null;
		}
		FortGuildStatistics.Builder def = FortGuildStatistics.newBuilder();
		GuildFortStatPush stat = contender.getPush();
		def.setArmyFlag(stat.killFlagNum);
		def.setMumber(stat.memberNumber);
		def.setDefenseSoul(stat.defBuffScore);
		def.setAttackSoul(stat.attBuffScore);
		def.setKill(stat.killPlayerNum);
		def.setAttack(stat.attBuff);
		def.setDefense(stat.defBuff);
		
		def.setScore(stat.score);
		return def;
	}
	
	/**
	 * 
	 * @param player
	 * @param isOpponent identify if need opponent's stat info,false is player's stat
	 * @return if no attacker,return the default stat info
	 */
	public GuildFortContenderPO getStatByPlayer(WNPlayer player,boolean isOpponent) {
		String gid = player.guildManager.getGuildId();
		if(isDefenserMember(gid)) {
			if(isOpponent) {
				if(this.attacker!=null) {
					return this.attacker.getContenderPO();
				}
			}else {
				if(this.defenser!= null) {
					return this.defenser.getContenderPO();
				}
			}
		}else if(isAttackerMember(gid)) {
			if(isOpponent) {
				if(this.defenser!= null) {
					return this.defenser.getContenderPO();
				}
			}else {
				if(this.attacker!=null) {
					return this.attacker.getContenderPO();
				}
			}			
		}
		
		return new GuildFortContenderPO();
	}
		
	public GuildFortContenderPO getContenderByGuildId(String guildId) {		
		if(isDefenserMember(guildId)) {
			return this.defenser;
		}else if(isAttackerMember(guildId)) {
			return this.attacker;
		}else {
			return null;
		}
	}
	
	private void addDefBuffScore(GuildFortContenderPO contender,int score) {
		contender.defBuffScore += score;
		int afterAddDiv = contender.defBuffScore/GlobalConfig.GuildFort_PickAddDefense;
		if(afterAddDiv > contender.defBuff) {
			contender.defBuff = afterAddDiv;
			pushReport();
			pushBuffChanged(contender);
		}
	}
	
	private void addAttBuffScore(GuildFortContenderPO contender,int score) {
		contender.attBuffScore += score;
		int afterAddDiv = contender.attBuffScore/GlobalConfig.GuildFort_KillMonAddAttack;
		if(afterAddDiv > contender.attBuff) {
			contender.attBuff = afterAddDiv;
			pushReport();
			pushBuffChanged(contender);
		}
	}
	
	private void pushReport() {
		FortGuildStatistics.Builder defBuilder = this.getStatistics(defenser);
		FortGuildStatistics.Builder attBuilder = this.getStatistics(attacker);
		
		MessagePush defMsg = generateDefenserReport(defBuilder,attBuilder);
		MessagePush attMsg = generateAttackerReport(defBuilder,attBuilder);
		
		if(this.defPrepareArea!=null) {
			defPrepareArea.pushReport(defMsg,attMsg);
		}
		if(this.defPveArea!=null) {
			defPveArea.pushReport(defMsg,attMsg);
		}
		if(this.attPrepareArea!=null) {
			attPrepareArea.pushReport(defMsg,attMsg);
		}
		if(this.attPveArea!=null) {
			attPveArea.pushReport(defMsg,attMsg);
		}
		if(this.pvpArea!=null) {
			pvpArea.pushReport(defMsg,attMsg);
		}
	}
	
	private void pushBattleResult() {
		if(defenser!=null) {
			for(GuildFortMemberPO member: defenser.getMembers()) {
				WNPlayer player = PlayerUtil.getOnlinePlayer(member.playerId);
				if(player!=null) {//TODO
					player.sendSysTip("据点战结果在这里。。。", TipsType.NORMAL);
				}
			}
		}
		
		if(attacker!=null) {
			for(GuildFortMemberPO member: attacker.getMembers()) {
				WNPlayer player = PlayerUtil.getOnlinePlayer(member.playerId);
				if(player!=null) {//TODO 
					player.sendSysTip("据点战结果在这里。。。", TipsType.NORMAL);
				}
			}
		}
	}
	
	private void pushBuffChanged(GuildFortContenderPO contender) {
		if(this.pvpArea!=null) {
			pvpArea.onAddBuff(contender);
		}
	}
	
	private void addScore(GuildFortContenderPO contender,int score) {
		contender.score += score;
		pushReport();
		if(contender.score >= GlobalConfig.GuildFort_MaxWinPoint) {//Who is the first to exceed the upper limit socre who wins first and game is over
			onBattleOver();
		}
		
	}
	
	public void onPlayerEntered(WNPlayer player) {
		GuildFortContenderPO contender = this.getContenderByPlayer(player);
		contender.getMemberAndPut(player);
		pushReport();
	}
	
	public void onPickedItem(WNPlayer player,int itemId) {
		if (isBattleOver()) {
			Out.warn("guild fort battle is overed while on stat");
			return;
		}
		
		GuildFortContenderPO contender = this.getContenderByPlayer(player);
		GuildFortMemberPO stat = contender.getMemberAndPut(player);
		Integer times = GlobalConfig.guildFortPickPoint.get(itemId);
		if(times == null) {
			Out.warn("guildFortPickPoint not contains the itemId,itemId: ",itemId," playerId: " + player.getId());
			return;
		}
		stat.onPickedItem(itemId,times);
		
		addDefBuffScore(contender,times);
	}
	
	public void onKilledMonster(WNPlayer player,int monsterId) {
		if (isBattleOver()) {
			Out.warn("guild fort battle is overed while on stat");
			return;
		}
		GuildFortContenderPO contender = this.getContenderByPlayer(player);
		GuildFortMemberPO stat = contender.getMemberAndPut(player);
		Integer times = GlobalConfig.guildFortKillMonPoint.get(monsterId);
		if(times == null) {
			Out.warn("guildFortKillMonPoint not contains the monster,monsterId: ",monsterId," playerId: " + player.getId());
			return;
		}
		
		stat.onKilledMonster(monsterId,times);
		addAttBuffScore(contender,times);
	}
	
	public void onKilledFlag(WNPlayer player,int monsterId) {
		if (isBattleOver()) {
			Out.warn("guild fort battle is overed while on stat");
			return;
		}
		GuildFortContenderPO contender = this.getContenderByPlayer(player);
		GuildFortMemberPO stat = contender.getMemberAndPut(player);
		stat.onKilledFlag(GlobalConfig.GuildFort_DestroyPoint);
		
		addScore(contender,GlobalConfig.GuildFort_DestroyPoint);	
	}
	
	public void onKilledPlayer(WNPlayer player) {
		if (isBattleOver()) {
			Out.warn("guild fort battle is overed while on stat");
			return;
		}
		
		GuildFortContenderPO contender = this.getContenderByPlayer(player);
		GuildFortMemberPO stat = contender.getMemberAndPut(player);
		stat.onKilledPlayer(GlobalConfig.GuildFort_KillPoint);
		
		addScore(contender,GlobalConfig.GuildFort_KillPoint);
	}
	
	public void onBattleReport(WNPlayer player, DamageHealVO report,int count) {
		if (isBattleOver()) {
			Out.warn("guild fort battle is overed while on stat");
			return;
		}
		GuildFortContenderPO contender = this.getContenderByPlayer(player);
		GuildFortMemberPO stat = contender.getMemberAndPut(player);
		stat.onFightHurt(report.TotalDamage,count);
		stat.onFightHurt(report.TotalHealing, count);
	}
	
	
	public boolean isDefenserMember(String guildId) {
		return this.defenser!=null && this.defenser.isMember(guildId);
	}
	
	public boolean isAttackerMember(String guildId) {
		return this.attacker!=null && this.attacker.isMember(guildId);
	}
	
	public List<GuildFortBidderPO> getBidders(){
		return po.bidders;
	}
	
	public boolean isBidWinner(String guildId) {
		if(this.defenser!=null && this.defenser.getGuildId().equals(guildId)){
			return true;
		}
		
		return false;
	}
	
	public String getName() {
		return this.fortConfig.name;
	}
	
	public String getDefenserName() {
		if(defenser==null) {
			return "";
		}
		
		return defenser.guildName;
	}
	
	public String getAttackerName() {
		if(attacker==null) {
			return "";
		}
		
		return attacker.guildName;
	}
	
	/**
	 * 
	 * @param guildId
	 */
	public void updateRank() {
		String guildId = getOccupyGuildId();
		if(guildId == null) {
			Out.error("occupied guild id null occured in update rank");
			return;
		}
		GuildPO guild = GuildServiceCenter.getInstance().getGuild(guildId);
		if(guild!=null) {
			RankType.GUILD_FORT.getHandler().handle(guild);
		}
	}
}
