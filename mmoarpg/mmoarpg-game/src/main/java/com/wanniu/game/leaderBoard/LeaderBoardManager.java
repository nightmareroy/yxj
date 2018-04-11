package com.wanniu.game.leaderBoard;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.wanniu.core.game.LangService;
import com.wanniu.core.logfs.Out;
import com.wanniu.core.util.DateUtil;
import com.wanniu.core.util.RandomUtil;
import com.wanniu.core.util.StringUtil;
import com.wanniu.game.GWorld;
import com.wanniu.game.common.Const;
import com.wanniu.game.common.ConstsTR;
import com.wanniu.game.common.msg.MessageUtil;
import com.wanniu.game.guild.GuildUtil;
import com.wanniu.game.mail.MailUtil;
import com.wanniu.game.mail.SysMailConst;
import com.wanniu.game.mail.data.MailData.Attachment;
import com.wanniu.game.mail.data.MailSysData;
import com.wanniu.game.player.BILogService;
import com.wanniu.game.player.GlobalConfig;
import com.wanniu.game.player.PlayerUtil;
import com.wanniu.game.player.WNPlayer;
import com.wanniu.game.poes.GuildPO;
import com.wanniu.game.poes.LeaderBoardPlayerPO;
import com.wanniu.game.poes.PlayerPO;
import com.wanniu.game.rank.RankType;
import com.wanniu.game.request.leaderboard.WorShipHandler.WorshipRes;
import com.wanniu.redis.PlayerPOManager;

import pomelo.area.LeaderBoardHandler.WorldLevelInfo;
import pomelo.area.PlayerHandler.SuperScriptType;

/**
 * 世界等级/膜拜
 * 
 * @author Yangzz
 *
 */
public class LeaderBoardManager {
	public WNPlayer player;
	public LeaderBoardPlayerPO po;

	public LeaderBoardManager(WNPlayer player) {
		this.player = player;
		this.po = PlayerPOManager.findPO(ConstsTR.player_leaderboardTR, this.player.getId(), LeaderBoardPlayerPO.class);
	};

	public LeaderBoardPlayerPO toJson4Serialize() {
		return po;
	};

	public WorshipRes worShip(WNPlayer player, int type) {
		List<String> awards = new ArrayList<>();
		WorshipRes data = new WorshipRes(true, "", awards);

		int reqLevel = GlobalConfig.WorldExp_ReqLevel;

		if (player.getLevel() < reqLevel) {

			data.result = false;
			data.info = LangService.getValue("REWARD_LEVEL_NOT_ENOUGH");
			return data;
		}

		if (this.po.worShipTime == null || DateUtil.isNeedFiveRefresh(po.worShipTime)) {

			po.worShipTimes = 0;
			po.worShipDiamondTimes = 0;
		}

		int worShipTimeMax = GlobalConfig.WorldExp_Admire_MaxCount;

		if (po.worShipTimes >= worShipTimeMax) {

			data.result = false;
			data.info = LangService.getValue("WORLDEXP_NOT_TIME");
			return data;
		}

		int addGold = 0;

		if (type == 0) { // ��ͨĤ��

			addGold = GlobalConfig.WorldExp_Admire_AddGold;
		} else {
			addGold = GlobalConfig.WorldExp_DiamondAdmire_AddGold;

			int costDiamond = GlobalConfig.WorldExp_DiamondAdmire_Price;

			if (player.moneyManager.getDiamond() < costDiamond) {
				player.onFunctionGoTo(Const.FUNCTION_GOTO_TYPE.PREPAID, null, null, null);
				data.result = false;
				return data;
			}

			player.moneyManager.costDiamond(costDiamond, Const.GOODS_CHANGE_TYPE.leaderBoard);
			// player.pushDynamicData("diamond" ,player.player.diamond);

			this.po.worShipDiamondTimes++;
		}

		this.po.worShipTimes++;
		this.po.worShipTime = new Date();
		Out.info("膜拜大神 playerId=", player.getId(), ",type=", type);
		player.moneyManager.addGold(addGold, Const.GOODS_CHANGE_TYPE.leaderBoard);
		BILogService.getInstance().ansycReportLeaderBoard(player.getPlayer(), type);

		if (this.po.worShipTimes == worShipTimeMax) {

			String itemCode = GlobalConfig.WorldExp_NormalChest_ItemCode;

			data.awards.add(itemCode);

			if (this.player.bag.testAddCodeItem(itemCode, 1, Const.ForceType.DEFAULT, true)) {

				this.player.bag.addCodeItem(itemCode, 1, Const.ForceType.DEFAULT, Const.GOODS_CHANGE_TYPE.leaderBoard);
			} else {
				MailSysData mailData = new MailSysData(SysMailConst.WORLDEXP_REWARD);
				mailData.attachments = new ArrayList<>();
				Attachment attachment = new Attachment();
				attachment.itemCode = itemCode;
				attachment.itemNum = 1;
				mailData.attachments.add(attachment);
				MailUtil.getInstance().sendMailToOnePlayer(player.getId(), mailData, Const.GOODS_CHANGE_TYPE.leaderBoard);
			}

			if (this.po.worShipDiamondTimes == worShipTimeMax) {

				int randValue = RandomUtil.getInt(1, 10000);

				if (randValue <= GlobalConfig.WorldExp_SuperChest_Chance) {

					itemCode = GlobalConfig.WorldExp_SuperChest_ItemCode;

					data.awards.add(itemCode);

					if (this.player.bag.testAddCodeItem(itemCode, 1, Const.ForceType.DEFAULT, true)) {

						this.player.bag.addCodeItem(itemCode, 1, Const.ForceType.DEFAULT, Const.GOODS_CHANGE_TYPE.leaderBoard);
					} else {
						MailSysData mailData = new MailSysData(SysMailConst.WORLDEXP_REWARD);
						mailData.attachments = new ArrayList<>();
						Attachment attachment = new Attachment();
						attachment.itemCode = itemCode;
						attachment.itemNum = 1;
						mailData.attachments.add(attachment);
						MailUtil.getInstance().sendMailToOnePlayer(player.getId(), mailData, Const.GOODS_CHANGE_TYPE.leaderBoard);
					}

					String playerName = MessageUtil.getPlayerNameColor(this.player.getName(), this.player.getPro());
					String msg = LangService.getValue("WORLDEXP_NEWS");

					msg = msg.replace("{playerName}", playerName);

					MessageUtil.sendRollChat(player.getLogicServerId(), msg, Const.CHAT_SCOPE.SYSTEM);
				}
			}
		}

		this.updateSuperScript();

		// 更新活跃度
		this.player.dailyActivityMgr.onEvent(Const.DailyType.WORLD_LEVEL, "0", 1);
		// 成就
		this.player.achievementManager.onWorldLevelTimes();
		return data;
	};

	public WorldLevelInfo worldLevelInfo(WNPlayer player) {
		WorldLevelInfo.Builder data = WorldLevelInfo.newBuilder();

		if (this.po.worShipTime == null || DateUtil.isNeedFiveRefresh(this.po.worShipTime)) {
			this.po.worShipTimes = 0;
			this.po.worShipDiamondTimes = 0;
		}

		data.setWorShipTimes(this.po.worShipTimes);
		data.setMaxWorShipTimes(GlobalConfig.WorldExp_Admire_MaxCount);

		String playerId = RankType.LEVEL.getHandler().getFirstRankMemberId(GWorld.__SERVER_ID);
		if (StringUtil.isEmpty(playerId)) {
			playerId = player.getId();
			Out.warn("获取排行榜第一名时没有值");
		}
		PlayerPO result = PlayerPOManager.findPO(ConstsTR.playerTR, playerId, PlayerPO.class);

		data.setWorldLevelId(playerId);
		data.setWorldLevelName(result.name);
		data.setWorldLevel(result.level);
		data.setWorldUpLevel(result.upOrder);

		data.addAllAvatars(PlayerUtil.getBattlerServerAvatar(playerId));
		data.setWorldLevelPro(result.pro);

		data.setAddExp(getExpAdd(result));

		GuildPO guild = GuildUtil.getPlayerGuild(playerId);
		data.setRank1StGuildName(guild != null ? guild.name : "");
		data.setRank1StFight(result.fightPower);
		return data.build();
	}

	/**
	 * 获取经验加成
	 */
	public int getExpAdd(PlayerPO result) {
		if (result == null) {
			return 0;
		}
		int expAdd = 0;

		int ratioPerLevel = GlobalConfig.WorldExp_RatioPerLevel;
		int maxExpRatio = GlobalConfig.WorldExp_MaxExpRatio;

		int levelDiff = result.level - player.getLevel();

		if (levelDiff > GlobalConfig.WorldExp_MaxLevelValue) {
			expAdd += ratioPerLevel * levelDiff;
		}

		if (levelDiff != 0) {
			if (expAdd > maxExpRatio) {
				expAdd = maxExpRatio;
			}
		}

		return expAdd;
	}

	public void updateSuperScript() {
		List<SuperScriptType> data = this.getSuperScript();
		this.player.updateSuperScriptList(data);
	};

	public List<SuperScriptType> getSuperScript() {

		List<SuperScriptType> data = new ArrayList<>();

		if (!this.player.functionOpenManager.isOpen(Const.FunctionType.WORLD_EXP.getValue())) {
			SuperScriptType.Builder script = SuperScriptType.newBuilder();
			script.setType(Const.SUPERSCRIPT_TYPE.WORLD_LEVEL.getValue());
			script.setNumber(0);
			data.add(script.build());
		} else {
			if (this.po.worShipTime == null || DateUtil.isNeedFiveRefresh(this.po.worShipTime)) {

				this.po.worShipTimes = 0;
				this.po.worShipDiamondTimes = 0;
			}

			int count = GlobalConfig.WorldExp_Admire_MaxCount > this.po.worShipTimes ? 1 : 0;

			SuperScriptType.Builder script = SuperScriptType.newBuilder();
			script.setType(Const.SUPERSCRIPT_TYPE.WORLD_LEVEL.getValue());
			script.setNumber(count);
			data.add(script.build());
		}

		return data;
	}
}
