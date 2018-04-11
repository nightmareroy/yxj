package com.wanniu.game.revelry;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;

import org.apache.commons.lang3.StringUtils;

import com.wanniu.core.game.JobFactory;
import com.wanniu.core.logfs.Out;
import com.wanniu.game.GWorld;
import com.wanniu.game.common.Const;
import com.wanniu.game.common.Const.GOODS_CHANGE_TYPE;
import com.wanniu.game.data.GameData;
import com.wanniu.game.data.RevelryCO;
import com.wanniu.game.data.RevelryConfigCO;
import com.wanniu.game.guild.GuildServiceCenter;
import com.wanniu.game.mail.MailUtil;
import com.wanniu.game.mail.SysMailConst;
import com.wanniu.game.mail.data.MailData.Attachment;
import com.wanniu.game.mail.data.MailSysData;
import com.wanniu.game.player.WNPlayer;
import com.wanniu.game.poes.GuildMemberPO;
import com.wanniu.game.rank.RankType;
import com.wanniu.game.rank.SimpleRankData;

/**
 * 冲榜管理类.
 *
 * @author 小流氓(176543888@qq.com)
 */
public class RevelryManager {
	private static final RevelryManager instance = new RevelryManager();
	/**
	 * 结算任务的存根.
	 */
	private static final Map<String, ScheduledFuture<?>> settlementFutureMap = new ConcurrentHashMap<>();

	public static RevelryManager getInstance() {
		return instance;
	}

	/**
	 * 冲榜中的类型转化为排行榜枚举.
	 * <p>
	 * 1=属性-生命榜<br>
	 * 2=属性-物攻榜<br>
	 * 3=属性-魔攻榜<br>
	 * 4=仙缘榜<br>
	 * 5=镇妖榜<br>
	 * 6=等级榜<br>
	 * 7=战力榜<br>
	 * 8=仙盟榜<br>
	 * 9=灵宠榜<br>
	 * 10=仙骑榜<br>
	 * 11=问道榜<br>
	 * 12=试炼榜<br>
	 */
	public RankType toRankType(int activityKey) {
		switch (activityKey) {
		case 1:
			return RankType.HP;
		case 2:
			return RankType.PHY;
		case 3:
			return RankType.MAGIC;
		case 4:
			return RankType.XIANYUAN;
		case 5:
			return RankType.DEMON_TOWER;
		case 6:
			return RankType.LEVEL;
		case 7:
			return RankType.FIGHTPOWER;
		case 8:
			return RankType.GUILD_LEVEL;
		case 9:
			return RankType.PET;
		case 10:
			return RankType.Mount;
		case 11:
			return RankType.ARENA_SCORE;
		case 12:
			return RankType.PVP_5V5;
		default:
			return RankType.FIGHTPOWER;
		}
	}

	public boolean isGuildRankKey(int activityKey) {
		return activityKey == 8;
	}

	public void onLogin(WNPlayer player) {
		LocalDate openServerDate = GWorld.OPEN_SERVER_DATE;
		LocalDate now = LocalDate.now();

		boolean isOpen = GameData.Revelrys.values().stream().filter(v -> v.isOpen == 1 && now.isBefore(openServerDate.plusDays(v.endDays2))).findFirst().isPresent();
		player.updateSuperScript(Const.SUPERSCRIPT_TYPE.ACTIVITY_REVELRY, isOpen ? 1 : 0);
	}
	
	/**
	 * 获取所有冲榜大类.
	 */
	public List<RevelryClass> getRevelryClassList() {
		LocalDate openServerDate = GWorld.OPEN_SERVER_DATE;
		LocalDate now = LocalDate.now();

		// 未关闭的CO都要取出来.
		List<RevelryCO> templates = GameData.findRevelrys(v -> v.isOpen == 1 && now.isBefore(openServerDate.plusDays(v.endDays2)));
		templates.sort((o1, o2) -> o1.endDays1 - o2.endDays1);

		// 大类
		Map<String, RevelryClass> tabCaches = new TreeMap<>();

		for (RevelryCO template : templates) {
			// 大类.
			RevelryClass revelryClass = tabCaches.computeIfAbsent(template.activityTab, key -> new RevelryClass(template.activityName1));

			// 天数
			RevelryToday today = revelryClass.getTodays().computeIfAbsent(template.activityID, key -> new RevelryToday(template.activityName2));

			// 栏目
			RevelryColumn column = new RevelryColumn();
			column.setId(template.tabID);
			column.setName(template.tabName);
			column.setLabel(template.activityKey2);
			column.setGoto1(template.goTo1);
			column.setGoto2(template.goTo2);
			column.setTip(template.activityDesc);
			today.getColumns().add(column);

			// 计算这个栏目的结束时间
			LocalDateTime endTime = GWorld.OPEN_SERVER_DATE.plusDays(template.endDays1).atTime(0, 0, 0, 0);
			long timeleft = Duration.between(LocalDateTime.now(), endTime).getSeconds();
			if (timeleft > 0 && timeleft < today.getTimeleft()) {
				today.setTimeleft(timeleft);
			}
		}

		return new ArrayList<>(tabCaches.values());
	}

	/**
	 * 重置开服时间.
	 */
	public void onResetOpenServerDate(LocalDate openServerDate) {
		LocalDateTime now = LocalDateTime.now();
		GameData.Revelrys.values().stream().filter(v -> v.isOpen == 1).forEach(v -> {
			ScheduledFuture<?> future = settlementFutureMap.get(v.tabID);
			if (future != null) {
				future.cancel(true);
			}

			LocalDateTime endTime = openServerDate.plusDays(v.endDays1).atTime(0, 0, 0, 0);
			if (now.isBefore(endTime)) {
				long timeleft = Duration.between(now, endTime).getSeconds();
				// 这里的定时任务只是充当调度任务，具体任务由异步线程池去跑
				settlementFutureMap.put(v.tabID, JobFactory.addDelayJob(() -> {
					GWorld.getInstance().ansycExec(() -> settlementResult(v.tabID));
				}, timeleft * 1000));
				Out.info("冲榜活动添加结果任务. tabID=", v.tabID, ",timeleft=", timeleft);
			}
		});
	}

	/**
	 * 结算活动结果
	 */
	public void settlementResult(String tabID) {
		Out.info("冲榜活动结算。tabID=", tabID);
		// 拉取排行榜，存档，发邮件奖励
		RevelryCO template = GameData.Revelrys.get(tabID);
		RankType rankType = RevelryManager.getInstance().toRankType(template.activityKey);
		Map<Integer, SimpleRankData> rankMap = rankType.getHandler().copyRankToKey(tabID);

		List<RevelryConfigCO> configs = GameData.findRevelryConfigs(v -> tabID.equals(v.type));
		for (RevelryConfigCO config : configs) {
			for (int rank = config.parameter1; rank <= config.parameter2; rank++) {
				SimpleRankData rankData = rankMap.get(rank);
				if (rankData == null) {
					continue;
				}

				// 仙盟榜
				if (isGuildRankKey(template.activityKey)) {
					this.sendGuildRankReward(config, template.tabName, rankData.getId(), rank);
				}
				// 玩家榜
				else {

					final List<Attachment> attachments = new ArrayList<>();
					this.buildAttachment(attachments, config.item1code, config.num1);
					this.buildAttachment(attachments, config.item2code, config.num2);
					this.buildAttachment(attachments, config.item3code, config.num3);
					this.buildAttachment(attachments, config.item4code, config.num4);

					this.sendPlayerRankReward(template.tabName, rankData.getId(), rank, attachments);
				}

			}
		}
		Out.info("冲榜活动结算结束。tabID=", tabID);
	}

	// a61,仙盟榜特殊处理
	// 资源1和资源2为盟主奖励
	// 资源3和资源4为成员奖励
	private void sendGuildRankReward(RevelryConfigCO config, String rankName, String guildId, int rank) {
		ArrayList<GuildMemberPO> members = GuildServiceCenter.getInstance().getGuildMemberList(guildId);
		for (GuildMemberPO member : members) {
			final List<Attachment> attachments = new ArrayList<>();

			// 会长奖励
			if (member.job == Const.GuildJob.PRESIDENT.getValue()) {
				this.buildAttachment(attachments, config.item1code, config.num1);
				this.buildAttachment(attachments, config.item2code, config.num2);
			}
			// 成员
			else {
				this.buildAttachment(attachments, config.item3code, config.num3);
				this.buildAttachment(attachments, config.item4code, config.num4);
			}

			this.sendPlayerRankReward(rankName, member.playerId, rank, attachments);
		}
	}

	private void sendPlayerRankReward(String rankName, String playerId, int rank, List<Attachment> attachments) {
		try {
			MailSysData mailData = new MailSysData(SysMailConst.RankRevelryReward);
			mailData.attachments = attachments;

			// 替换字符
			mailData.replace = new HashMap<>();
			mailData.replace.put("rankName", rankName);
			mailData.replace.put("rank", String.valueOf(rank));

			MailUtil.getInstance().sendMailToOnePlayer(playerId, mailData, GOODS_CHANGE_TYPE.REVELRY);
		} catch (Exception e) {
			Out.warn("冲榜活动结算时发放奖励异常.rank=", rank, ",playerId=", playerId);
		}
	}

	private void buildAttachment(List<Attachment> attachments, String itemCode, int itemNum) {
		if (StringUtils.isNotEmpty(itemCode)) {
			Attachment item = new Attachment();
			item.itemCode = itemCode;
			item.itemNum = itemNum;
			attachments.add(item);
		}
	}
}