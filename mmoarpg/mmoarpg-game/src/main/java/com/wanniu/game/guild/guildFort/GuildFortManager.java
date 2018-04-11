package com.wanniu.game.guild.guildFort;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.wanniu.core.game.LangService;
import com.wanniu.core.logfs.Out;
import com.wanniu.core.util.DateUtil;
import com.wanniu.game.area.Area;
import com.wanniu.game.common.Const;
import com.wanniu.game.common.Const.CHAT_SCOPE;
import com.wanniu.game.common.Const.GOODS_CHANGE_TYPE;
import com.wanniu.game.common.Const.GuildJob;
import com.wanniu.game.common.Const.ManagerType;
import com.wanniu.game.common.Const.PlayerEventType;
import com.wanniu.game.common.ModuleManager;
import com.wanniu.game.data.GuildLevelCO;
import com.wanniu.game.guild.GuildServiceCenter;
import com.wanniu.game.guild.GuildUtil;
import com.wanniu.game.guild.guildFort.dao.GuildFortAwardPO;
import com.wanniu.game.guild.guildFort.dao.GuildFortAwardPO.AwardFlag;
import com.wanniu.game.guild.guildFort.dao.GuildFortBattleReportPO;
import com.wanniu.game.guild.guildFort.dao.GuildFortBidderPO;
import com.wanniu.game.guild.guildFort.dao.GuildFortContenderPO;
import com.wanniu.game.guild.guildFort.dao.GuildFortMemberPO;
import com.wanniu.game.guild.guildFort.dao.GuildFortReportPO;
import com.wanniu.game.item.NormalItem;
import com.wanniu.game.mail.MailUtil;
import com.wanniu.game.mail.SysMailConst;
import com.wanniu.game.mail.data.MailSysData;
import com.wanniu.game.player.GlobalConfig;
import com.wanniu.game.player.WNPlayer;
import com.wanniu.game.poes.GuildPO;
import com.wanniu.game.team.TeamData.TeamMemberData;

import pomelo.area.GuildFortHandler.ApplyAllReportListResponse;
import pomelo.area.GuildFortHandler.ApplyDailyAwardListResponse;
import pomelo.area.GuildFortHandler.ApplyFortGuildInfoResponse;
import pomelo.area.GuildFortHandler.ApplyFundResponse;
import pomelo.area.GuildFortHandler.ApplyReportDetailResponse;
import pomelo.area.GuildFortHandler.ApplyReportStatisticsResponse;
import pomelo.area.GuildFortHandler.AreaAward;
import pomelo.area.GuildFortHandler.FortGuildInfo;
import pomelo.area.GuildFortHandler.GetGuildAreaApplyListResponse;
import pomelo.area.GuildFortHandler.GetGuildAreaDetailResponse;
import pomelo.area.GuildFortHandler.GetGuildAreaListResponse;
import pomelo.area.GuildFortHandler.GuildAreaApplyInfo;
import pomelo.area.GuildFortHandler.GuildAreaDetail;
import pomelo.area.GuildFortHandler.GuildAreaInfo;
import pomelo.area.GuildFortHandler.ReportDetail;
import pomelo.area.GuildFortHandler.ReportGuildDetail;
import pomelo.area.GuildFortHandler.ReportList;
import pomelo.area.GuildFortHandler.ReportListInfo;
import pomelo.area.GuildFortHandler.ReportStatisticsDetail;
import pomelo.area.PlayerHandler.SuperScriptType;

public class GuildFortManager extends ModuleManager {
	private WNPlayer player;
	private GuildFortAwardPO dailyAwards = null;
	private int fortId = 0;

	private String getGuildId() {
		return this.player.guildManager.getGuildId();
	}
	
	private String getJobName() {
		return this.player.guildManager.getJobName();
	}
	
	
	public GuildFortManager(WNPlayer wnPlayer) {
		this.player = wnPlayer;
		if (player.playerAttachPO.guildFortDailyAwards == null) {
			player.playerAttachPO.guildFortDailyAwards = new GuildFortAwardPO();
		}
		this.dailyAwards = player.playerAttachPO.guildFortDailyAwards;//reference assignment
	}
	
	private boolean isSameDay(long date) {
		return DateUtil.isSameDay(new Date(), new Date(date));
	}

	/* (non-Javadoc)
	 * @see com.wanniu.game.common.ModuleManager#onPlayerEvent(com.wanniu.game.common.Const.PlayerEventType)
	 */
	public void onPlayerEvent(PlayerEventType eventType) {
		if (eventType == PlayerEventType.REFRESH_NEWDAY) {
			recalcDailyAwards();
		} else if (eventType == PlayerEventType.OFFLINE) {
			setFortId(0);
		} else if(eventType == PlayerEventType.AFTER_LOGIN) {
			if(!isSameDay(this.dailyAwards.updateDate)) {//Update time is expired, need reset
				recalcDailyAwards();
			}
		}
		return;
	}

	/* (non-Javadoc)
	 * @see com.wanniu.game.common.ModuleManager#getManagerType()
	 */
	public ManagerType getManagerType() {
		return ManagerType.GUILD_FORT;
	}

	/**
	 * Set daily award's flag into HAS_AWARD
	 */
	public void recalcDailyAwards() {
		synchronized (dailyAwards.awardStatus) {
			dailyAwards.awardStatus.clear();
			List<GuildFort> list = GuildFortCenter.getInstance().getOccupiedForts(getGuildId());
			for (GuildFort fort : list) {
				dailyAwards.awardStatus.put(fort.getId(), AwardFlag.HAS_AWARD);// 0: 不可领取 1: 可领取 2：已领取
			}
			dailyAwards.updateDate = System.currentTimeMillis();
		}
	}
	
	/**
	 * Clear this player guild fort daily awards status
	 */
	public void clearDailyAwards() {
		synchronized (dailyAwards.awardStatus) {
			dailyAwards.awardStatus.clear();
		}
	}

	private AwardFlag getAwardFlag(int fortId) {
		synchronized (dailyAwards.awardStatus) {
			if (dailyAwards.awardStatus.containsKey(fortId)) {
				return dailyAwards.awardStatus.get(fortId);
			}
			return AwardFlag.NO_AWARD;// 0: 不可领取 1: 可领取 2：已领取
		}
	}

	private List<Integer> getAwardFortIds() {
		List<Integer> fortIds = new ArrayList<>();
		synchronized (dailyAwards.awardStatus) {
			for (Integer fortId : dailyAwards.awardStatus.keySet()) {
				if (dailyAwards.awardStatus.get(fortId) == AwardFlag.HAS_AWARD) {
					fortIds.add(fortId);
				}
			}
		}
		return fortIds;
	}

	private boolean hasAward() {
		return getAwardFortIds().size() > 0;
	}

	private boolean isBidded(GuildFort fort) {
		if(GuildFortService.getInstance().isInBidTime()) {
			if(fort.isInBidders(getGuildId())) {
				return true;
			}
		}
		
		return false;
	}
	
	public String handleGetGuildAreaList(GetGuildAreaListResponse.Builder res) {
		for (GuildFort fort : GuildFortCenter.getInstance().getAllGuildFort()) {
			GuildAreaInfo.Builder data = GuildAreaInfo.newBuilder();
			data.setAreaId(fort.getId());

			String occupyId = fort.getOccupyGuildId();
			if (occupyId != null) {
				GuildPO guild = GuildServiceCenter.getInstance().getGuild(occupyId);
				data.setApplied(isBidded(fort)?1:0);
				
				data.setGuildId(occupyId);
				data.setGuildName(guild.name);
				data.setGuildName1(fort.getDefenserName());
				data.setGuildName2(fort.getAttackerName());
			} else {
				data.setApplied(0);
				
				data.setGuildId("");
				data.setGuildName("");
				data.setGuildName1("");
				data.setGuildName2("");
			}
			res.addAreaList(data);
		}

		return null;
	}

	public String handleGetGuildAreaDetail(GetGuildAreaDetailResponse.Builder res, int fortId) {
		GuildFort fort = GuildFortCenter.getInstance().getFort(fortId);
		if (fort == null) {
			return LangService.getValue("GUILDFORT_FORTID_NOT_EXIST");
		}
		GuildAreaDetail.Builder data = GuildAreaDetail.newBuilder();
		String occupyId = fort.getOccupyGuildId();
		if (occupyId != null) {
			GuildPO guild = GuildServiceCenter.getInstance().getGuild(occupyId);
			data.setGuildId(occupyId);
			data.setGuildName(guild.name);
			data.setGuildLevel(guild.level);
			data.setGuildLeaderId(guild.presidentId);
			data.setGuildLeaderName(guild.presidentName);
			data.setGuildNumberCount(GuildUtil.getGuildMemberCount(occupyId));
			GuildLevelCO prop = GuildUtil.getGuildLevelPropByLevel(guild.level);
			data.setGuildNumberTotalCount(prop.member);
		} else {
			data.setGuildId("");
		}
		data.addAllWinnerAwardList(fort.getWinnerReward());
		data.addAllDailyAwardList(fort.getDailyAward());
		data.setDailyAwardFlag(this.getAwardFlag(fortId).value);
		int status = GuildFortCenter.getInstance().getStatus(isBidded(fort));
		data.setAreaStatus(status);
		if(status == GuildFortCenter.Status.NOT_BEGIN.value) {//据点战报名倒计时，areaStatus为0时，必传
			data.setCountDown(GuildFortService.getInstance().getBidBeginRemainSecond());
		}else if(status == GuildFortCenter.Status.INTIME_NOTBID.value) {
			data.setCountDown(GuildFortService.getInstance().getBidEndRemainSecond());
		}

		res.setAreaDetail(data.build());
		return null;
	}

	public String handleGetGuildAreaApplyList(GetGuildAreaApplyListResponse.Builder res, int fortId) {
		GuildFort fort = GuildFortCenter.getInstance().getFort(fortId);
		if (fort == null) {
			return LangService.getValue("GUILDFORT_FORTID_NOT_EXIST");
		}
		GuildFortService service = GuildFortService.getInstance();
		if (!service.isInOpen()) {
			return LangService.getValue("GUILDFORT_NOT_OPENED");
		}

		for (GuildFortBidderPO bidder : fort.getBidders()) {
			GuildAreaApplyInfo.Builder data = GuildAreaApplyInfo.newBuilder();
			GuildPO guild = GuildServiceCenter.getInstance().getGuild(bidder.guildId);
			data.setGuildIcon(guild.icon);
			data.setGuildId(bidder.guildId);
			data.setGuildName(guild.name);
			data.setGuildLevel(guild.level);
			data.setGuildLeaderId(guild.presidentId);
			data.setGuildLeaderName(guild.presidentName);
			data.setGuildNumberCount(GuildUtil.getGuildMemberCount(bidder.guildId));
			GuildLevelCO prop = GuildUtil.getGuildLevelPropByLevel(guild.level);
			data.setGuildNumberTotalCount(prop.member);
			if (GuildFortService.getInstance().isInBidTime()) {
				if (player.guildManager.getGuildId().equals(bidder.guildId)) {//the player who is in his own guild
					data.setApplyFund(bidder.fund);
				} else {
					data.setApplyFund(0);// the value 0 means to hide fund money
				}
			} else {
				data.setApplyFund(bidder.fund);
			}
			data.setIsWinner(fort.isBidWinner(bidder.guildId));
			res.addApplyList(data);
		}
		if (service.isInBidTime()) {
			res.setCountDown(GuildFortService.getInstance().getBidEndRemainSecond());
		}
		return null;
	}

	
	private void bidCommitNotify(GuildPO guild,String playerName,String fortName,int fund,long sumFund) {
		String msgStr = LangService.getValue("GUILDFORT_FUNDCHANGED_NOTICE2"); 	
		msgStr = msgStr.replace("{position}", getJobName())
				.replace("{name}", playerName)
				.replace("{fortname}", fortName)
				.replace("{fund}", String.valueOf(fund))
				.replace("{sumfund}", String.valueOf(sumFund));
		Set<String> playerIds =GuildUtil.getGuildMemberIdList(guild.id);
		GuildFortUtil.sendRollTipsAsyn(playerIds, msgStr, CHAT_SCOPE.GUILD);
		
		MailSysData mailData = new MailSysData(SysMailConst.GuildFortBidFundCommit);
		mailData.replace = new HashMap<>();
		mailData.replace.put("position",  getJobName());
		mailData.replace.put("name",  playerName);
		mailData.replace.put("fortname",  fortName);
		mailData.replace.put("fund",  String.valueOf(fund));
		mailData.replace.put("sumfund",  String.valueOf(sumFund));
		List<String> ids = GuildServiceCenter.getInstance().getGuildMemberIdList(guild.id,GuildJob.PRESIDENT,GuildJob.VICE_PRESIDENT);
		MailUtil.getInstance().sendMailToSomePlayer(ids.toArray(new String[ids.size()]), mailData, GOODS_CHANGE_TYPE.guild_mail);
	}
	
	private void bidCancelNotify(GuildPO guild,String playerName,String fortName,int fund) {
		String msgStr = LangService.getValue("GUILDFORT_FUNDCHANGED_NOTICE1"); 	
		msgStr = msgStr.replace("{position}", getJobName()).replace("{name}", playerName)
				.replace("{fortname}", fortName).replace("{fund}", String.valueOf(fund));
		Set<String> playerIds =GuildUtil.getGuildMemberIdList(guild.id);
		GuildFortUtil.sendRollTipsAsyn(playerIds, msgStr, CHAT_SCOPE.GUILD);
		
		MailSysData mailData = new MailSysData(SysMailConst.GuildFortBidFundCancel);
		mailData.replace = new HashMap<>();
		mailData.replace.put("position",  getJobName());
		mailData.replace.put("name",  playerName);
		mailData.replace.put("fortname",  fortName);
		mailData.replace.put("fund",  String.valueOf(fund));
		List<String> ids = GuildServiceCenter.getInstance().getGuildMemberIdList(guild.id,GuildJob.PRESIDENT,GuildJob.VICE_PRESIDENT);
		MailUtil.getInstance().sendMailToSomePlayer(ids.toArray(new String[ids.size()]), mailData, GOODS_CHANGE_TYPE.guild_mail);
	}
	
	public String handleApplyFund(ApplyFundResponse.Builder res, int fortId, int fund) {
		if (fund <= 0) {
			return LangService.getValue("GUILD_FUND_NOT_ENOUGH");
		}

		if(fund< GlobalConfig.GuildFort_MinBetMoney) {//In normal,this condition will not happen,because client has already filtered.
			return LangService.getValue("SOMETHING_ERR");
		}
		
		if (!GuildFortService.getInstance().isInBidTime()) {
			return LangService.getValue("GUILDFORT_NOTIN_BIDTIME");
		}

		GuildFort fort = GuildFortCenter.getInstance().getFort(fortId);
		if (fort == null) {
			return LangService.getValue("GUILDFORT_FORTID_NOT_EXIST");
		}
		if (!player.guildManager.isInGuild()) {
			return LangService.getValue("GUILD_NOT_JOIN");
		}
		if (!player.guildManager.isPresident() && !player.guildManager.isVicePresident()) {
			return LangService.getValue("GUILDFORT_NOT_PRISIDENT_ONBID");
		}
		GuildPO guild = player.guildManager.getGuildInfo();
		if (guild.fund < fund) {
			return LangService.getValue("GUILD_FUND_NOT_ENOUGH");
		}
		if (!fort.isInBidders(guild.id) && GuildFortCenter.getInstance().isBitFortExceeded(guild.id)) {
			return LangService.getValue("GUILDFORT_BITFORT_EXCEEDED");
		}

		int afterCommitFund = fort.commitBidFund(guild.id, fund);
		if(afterCommitFund>0) {
			bidCommitNotify(guild,player.getName(),fort.getName(),fund,afterCommitFund);
			GuildFortCenter.getInstance().onBidOperation();
		}else {
			return LangService.getValue("SOMETHING_ERR");
		}
		

		return null;
	}

	public String handleApplyCancelFund(int fortId) {
		if (!GuildFortService.getInstance().isInBidTime()) {
			return LangService.getValue("GUILDFORT_NOTIN_BIDTIME");
		}

		GuildFort fort = GuildFortCenter.getInstance().getFort(fortId);
		if (fort == null) {
			return LangService.getValue("GUILDFORT_FORTID_NOT_EXIST");
		}

		if (!player.guildManager.isInGuild()) {
			return LangService.getValue("GUILD_NOT_JOIN");
		}
		if (!player.guildManager.isPresident() && !player.guildManager.isVicePresident()) {
			return LangService.getValue("GUILDFORT_NOT_PRISIDENT_ONBID");
		}
		GuildPO guild = player.guildManager.getGuildInfo();
		if (!fort.isInBidders(guild.id)) {
			return LangService.getValue("GUILDFORT_NOT_BIDDED");
		}

		int fund = fort.extractBidFund(guild.id);
		if(fund>0) {
			Out.info("Apply cancel fund guild id:", guild.id, " original fund:", guild.fund, " to caneled fund:" + fund);
			bidCancelNotify(guild, player.getName(), fort.getName(), fund);
			GuildFortCenter.getInstance().onBidOperation();
		}else {
			return LangService.getValue("SOMETHING_ERR");
		}

		return null;
	}

	public String handleEnterPrepareArea(int fortId) {
		GuildFort fort = GuildFortCenter.getInstance().getFort(fortId);
		if (fort == null) {
			return LangService.getValue("GUILDFORT_FORTID_NOT_EXIST");
		}
		
		if (!GuildFortService.getInstance().isInEnterFortTime()) {
			return LangService.getValue("GUILDFORT_NOT_IN_BATTLETIME");
		}else {
			if (GuildFortService.getInstance().isInBattleTime() && fort.isBattleOver()) {
				return LangService.getValue("GUILDFORT_BATTLE_ENDED");
			}
		}


		
		Area area = fort.requestEnterPrepareArea(player);
		if (area == null) {
			return LangService.getValue("GUILDFORT_NOT_IN_BATTLEGUILD");
		}
		setFortId(fort.getId());

		return null;
	}

	private String handleEnterPveArea(int fortId) {
		GuildFort fort = GuildFortCenter.getInstance().getFort(fortId);
		if (fort == null) {
			return LangService.getValue("GUILDFORT_FORTID_NOT_EXIST");
		}
		if (!GuildFortService.getInstance().isInBattleTime()) {
			return LangService.getValue("GUILDFORT_NOT_IN_BATTLETIME");
		}

		if(fort.isBattleOver()) {
			return LangService.getValue("GUILDFORT_BATTLE_ENDED");
		}
		
		Area area = fort.requestEnterPveArea(player);
		if (area == null) {
			return LangService.getValue("GUILDFORT_NOT_IN_BATTLEGUILD");
		}

		return null;
	}

	private String handleEnterPvpArea(int fortId) {
		GuildFort fort = GuildFortCenter.getInstance().getFort(fortId);
		if (fort == null) {
			return LangService.getValue("GUILDFORT_FORTID_NOT_EXIST");
		}
		if (!GuildFortService.getInstance().isInBattleTime()) {
			return LangService.getValue("GUILDFORT_NOT_IN_BATTLETIME");
		}

		if(fort.isBattleOver()) {
			return LangService.getValue("GUILDFORT_BATTLE_ENDED");
		}
		
		Area area = fort.requestEnterPvpArea(player);
		if (area == null) {
			return LangService.getValue("GUILDFORT_NOT_IN_BATTLEGUILD");
		}

		return null;
	}

	public String handleApplyDailyAwardList(ApplyDailyAwardListResponse.Builder res) {	
		for(GuildFort fort : GuildFortCenter.getInstance().getAllGuildFort()) {
			AreaAward.Builder data = AreaAward.newBuilder();
			data.setAreaId(fort.getId());
			data.addAllDailyAwardList(fort.getDailyAward());
			String occupyId = fort.getOccupyGuildId();
			if(occupyId==null) {//No one occupied
				data.setGuildName("");
				data.setStatus(AwardFlag.NO_AWARD.value);
				res.addAreaAwardList(data);
				continue;
			}
						
			if(fort.isOccupier(getGuildId())) {//I'm in occupier's part
				AwardFlag flag = this.getAwardFlag(fort.getId());
				data.setGuildName(player.guildManager.getGuildName());
				data.setStatus(flag.value);
			}else {//Other guild occupied
				GuildPO guild = GuildServiceCenter.getInstance().getGuild(occupyId);
				if(guild!=null) {
					data.setGuildName(guild.name);
				}else {//something error occured
					data.setGuildName("");
				}
				data.setStatus(AwardFlag.NO_AWARD.value);
			}
			res.addAreaAwardList(data);
		}		
		
		return null;
	}

	
	private String getDailyAward(GuildFort fort) {
		List<NormalItem> dailyAward = fort.generateDailyAward();

		if (!this.player.getWnBag().testAddEntityItems(dailyAward, true)) {
			return LangService.getValue("BAG_NOT_ENOUGH_POS");
		}

		this.player.getWnBag().addEntityItems(dailyAward, Const.GOODS_CHANGE_TYPE.guildfort_daily_award);
		dailyAwards.awardStatus.put(fortId, AwardFlag.AWARDED);// mark the award has drew
		pushRedPoint();// update red point status

		return null;
	}
	
	public String handleApplyDailyAward(int fortId) {
		GuildFort fort = GuildFortCenter.getInstance().getFort(fortId);
		if (fort == null) {
			return LangService.getValue("GUILDFORT_FORTID_NOT_EXIST");
		}
		
		if(!fort.isOccupier(getGuildId())) {
			return LangService.getValue("GUILDFORT_NO_AWARD");
		}
		
		AwardFlag flag = this.getAwardFlag(fortId);
		switch (flag) {
		case NO_AWARD:
			return LangService.getValue("GUILDFORT_NO_AWARD");
		case AWARDED:
			return LangService.getValue("GUILDFORT_ERR_AWARDED");
		case HAS_AWARD: {
			return getDailyAward(fort);
		}
		default:
			Out.error("something error occured in handleApplyDailyAward with fortId:", fortId,
					" playerId:" + player.getId());
			return LangService.getValue("SOMETHING_ERR");
		}
	}

	private FortGuildInfo buildStatInfo(GuildFortContenderPO stat) {
		FortGuildInfo.Builder data = FortGuildInfo.newBuilder();
		GuildPO guild = GuildServiceCenter.getInstance().getGuild(stat.getGuildId());
		if(guild!=null) {
			data.setGuildIcon(guild.icon);
			data.setGuildName(guild.name);
			data.setGuildLevel(guild.level);
		}
		data.setGuildId(stat.getGuildId());
		data.setArmyFlag(stat.killFlagNum);
		data.setKill(stat.killPlayerNum);
		data.setDefenseSoul(stat.defBuffScore);
		data.setAttackSoul(stat.attBuffScore);
		data.setMumber(stat.memberNumber);
		data.setDefense(stat.defBuff);
		data.setAttack(stat.attBuff);
		data.setScore(stat.getScore());

		return data.build();
	}

	public String handleApplyFortGuildInfo(ApplyFortGuildInfoResponse.Builder res) {
		if (player.getAreaId() != GuildFortService.PVE_AREA_ID && player.getAreaId() != GuildFortService.PVP_AREA_ID && player.getAreaId() != GuildFortService.PREPARE_AREA_ID ) {
			return LangService.getValue("GUILDFORT_NOT_IN_BATTLEGUILD");
		}
		if (!GuildFortService.getInstance().isInEnterFortTime()) {
			return LangService.getValue("GUILDFORT_NOT_IN_BATTLETIME");
		}

		GuildFort fort = GuildFortCenter.getInstance().getFort(this.fortId);
		if (fort == null) {
			return LangService.getValue("GUILDFORT_FORTID_NOT_EXIST");
		}

		GuildFortContenderPO me = fort.getStatByPlayer(player, false);
		GuildFortContenderPO opponent = fort.getStatByPlayer(player, true);
		res.setOwnGuild(this.buildStatInfo(me));
		res.setEnemyGuild(this.buildStatInfo(opponent));

		return null;
	}

	public String handleChangeArea(int areaId) {
		if (this.fortId == 0) {
			return LangService.getValue("GUILDFORT_FORTID_NOT_EXIST");
		}
		String result = checkTeam(this.player);
		if (result != null) {
			return result;
		}
		if (areaId == GuildFortService.PVE_AREA_ID) {
			return handleEnterPveArea(fortId);
		} else if (areaId == GuildFortService.PVP_AREA_ID) {
			return handleEnterPvpArea(fortId);
		} else if (areaId == GuildFortService.PREPARE_AREA_ID) {
			return handleEnterPrepareArea(this.fortId);
		}

		return LangService.getValue("AREA_ID_NULL");
	}

	public void handleLeaveArea(int areaId) {
		if (areaId == GuildFortService.PREPARE_AREA_ID) {
			setFortId(0);
		}
	}

	private String checkTeam(WNPlayer player) {
		Map<String, TeamMemberData> members = player.getTeamManager().getTeamMembers();
		if (members == null || members.isEmpty()) {
			return null;
		}
		if (!player.getTeamManager().isTeamLeader() && player.getTeamManager().isFollowLeader()) {
			return LangService.getValue("TEAM_FOLLOW_CHANGE_AREA");
		}
		String guildId = getGuildId();

		for (TeamMemberData member : members.values()) {
			if (member.getPlayer() == null || member.getPlayer().guildManager == null
					|| member.getPlayer().guildManager.guild == null) {
				return LangService.getValue("GUILDFORT_NOT_SAME_GUILD");
			} else if (!member.getPlayer().guildManager.getGuildId().equals(guildId)) {
				return LangService.getValue("GUILDFORT_NOT_SAME_GUILD");
			}
		}

		return null;
	}

	public String handleApplyAllReportList(ApplyAllReportListResponse.Builder res) {
		List<GuildFortReportPO> reports = GuildFortCenter.getInstance().getReports();
		for (GuildFortReportPO report : reports) {
			ReportList.Builder data = ReportList.newBuilder();
			data.setDate(report.date);
			Map<Integer, GuildFortBattleReportPO> battleReports = report.battleReports;
			for (Integer fId : battleReports.keySet()) {
				GuildFortBattleReportPO br = battleReports.get(fId);
				if(br.defenser.guildId.equals("") && br.attacker.guildId.equals("")) {
					continue;
				}
				
				ReportListInfo.Builder subData = ReportListInfo.newBuilder();
				subData.setAreaId(fId);
				subData.setDefenseGuildName(br.defenser.guildName);
				subData.setDefenseGuildIcon(br.defenser.guildIcon);
				subData.setAttackGuildName(br.attacker.guildName);
				subData.setAttackGuildIcon(br.attacker.guildIcon);
				
				if (br.defenser.isWinner()) {// 0:防守方胜利，1:进攻方胜利，2：没有胜者
					subData.setResult(0);
				} else if(br.attacker.isWinner()){
					subData.setResult(1);
				}else {
					subData.setResult(2);
				}
				data.addReportListInfo(subData);
			}
			
			if(data.getReportListInfoCount()>0) {
				res.addReportList(data);
			}
			
		}

		return null;
	}

	private void buildReportGuildDetail(ReportGuildDetail.Builder res, GuildFortContenderPO contender) {
		res.setGuildIcon(contender.guildIcon);
		res.setGuildId(contender.guildId);
		res.setGuildName(contender.guildName);
		res.setGuildLevel(contender.guildLevel);
		res.setIsWinner(contender.isWinner() ? 1 : 0);
		
		res.setCollect(contender.pickItemNum);
		res.setDefense(contender.defBuff);
		res.setSoul(contender.killMonsterNum);
		res.setAttack(contender.attBuff);
		res.setKill(contender.killPlayerNum);
		res.setKillScore(contender.killPlayerScore);
		res.setDestroyFlag(contender.killFlagNum);
		res.setDestroyFlagScore(contender.killFlagScore);
		
		res.setTotalScore(contender.getScore());
	}

	public String handleApplyReportDetail(ApplyReportDetailResponse.Builder res, String date, int fortId) {
		List<GuildFortReportPO> reports = GuildFortCenter.getInstance().getReports();
		for (GuildFortReportPO report : reports) {
			if (report.date.equals(date)) {
				GuildFortBattleReportPO br = report.battleReports.get(fortId);
				if (br != null) {
					ReportDetail.Builder data = ReportDetail.newBuilder();
					data.setAreaId(fortId);
					ReportGuildDetail.Builder subData = ReportGuildDetail.newBuilder();
					buildReportGuildDetail(subData, br.defenser);
					data.setDetail1(subData);
					subData = ReportGuildDetail.newBuilder();
					buildReportGuildDetail(subData, br.attacker);
					data.setDetail2(subData);
					res.setReportDetail(data);
					return null;
				}
			}
		}

		return LangService.getValue("GUILDFORT_REPORT_NOT_FOUND");// can't find your request report Details
	}

	public String handleApplyReportStatistics(ApplyReportStatisticsResponse.Builder res, String date, int fortId,
			String guildId) {
		List<GuildFortReportPO> reports = GuildFortCenter.getInstance().getReports();
		for (GuildFortReportPO report : reports) {
			if (report.date.equals(date)) {
				GuildFortBattleReportPO br = report.battleReports.get(fortId);
				if (br != null) {
					GuildFortContenderPO ct = br.getContender(guildId);
					if (ct != null) {
						for (GuildFortMemberPO m : ct.getMembers()) {
							ReportStatisticsDetail.Builder data = ReportStatisticsDetail.newBuilder();
							data.setName(m.playerName);
							data.setLevel(m.playerLevel);
							data.setJob(m.guildJob);
	
							data.setKill(m.getKilledPlayerNum());
							data.setDestroyFlag(m.getKilledFlagNum());
							
							data.setDamage(m.getFightHurt());
							data.setCure(m.getFightCure());
							data.setDefenseScore(m.getDefBuffScore());
							data.setAttackSoul(m.getAttBuffScore());
							data.setTotalScore(m.getScore());
							
							
							res.addStatisticsDetail(data);
						}
						return null;
					}
				}
			}
		}

		return LangService.getValue("GUILDFORT_REPORT_NOT_FOUND");// can't find your request report Details
	}

	private void setFortId(int fortId) {
		this.fortId = fortId;
	}

	@Override
	public List<SuperScriptType> getSuperScript() {
		List<SuperScriptType> list = new ArrayList<>();
		boolean isInBid = GuildFortService.getInstance().isInBidTime();
		boolean isInEnterFort = GuildFortService.getInstance().isInEnterFortTime();
		boolean isCanAward = this.hasAward();

		SuperScriptType.Builder data = null;

		data = SuperScriptType.newBuilder();
		data.setType(Const.SUPERSCRIPT_TYPE.GUILDFORT_INBID.getValue());
		if (isInBid) {
			data.setNumber(1);
		} else {
			data.setNumber(0);
		}
		list.add(data.build());

		data = SuperScriptType.newBuilder();
		data.setType(Const.SUPERSCRIPT_TYPE.GUILDFORT_INBATTLE.getValue());
		if (isInEnterFort) {
			data.setNumber(1);
		} else {
			data.setNumber(0);
		}
		list.add(data.build());

		data = SuperScriptType.newBuilder();
		data.setType(Const.SUPERSCRIPT_TYPE.GUILDFORT_INAWARD.getValue());
		if (isCanAward) {
			data.setNumber(1);
		} else {
			data.setNumber(0);
		}
		list.add(data.build());

		return list;
	}

	public void pushRedPoint() {
		this.player.updateSuperScriptList(this.getSuperScript());
		this.player.guildManager.pushRedPoint();
	}

	public boolean needUpdateRedPoint() {
		return GuildFortService.getInstance().isInBidTime() || GuildFortService.getInstance().isInEnterFortTime()
				|| hasAward();
	}
}
