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
package com.wanniu.game.auction;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.RandomUtils;
import org.apache.commons.lang3.StringUtils;

import com.wanniu.core.GGame;
import com.wanniu.core.game.JobFactory;
import com.wanniu.core.game.entity.GPlayer;
import com.wanniu.core.logfs.Out;
import com.wanniu.core.util.DateUtils;
import com.wanniu.game.GWorld;
import com.wanniu.game.activity.RechargeActivityService;
import com.wanniu.game.common.Const.ForceType;
import com.wanniu.game.common.Const.GOODS_CHANGE_TYPE;
import com.wanniu.game.common.ConstsTR;
import com.wanniu.game.common.msg.MessageUtil;
import com.wanniu.game.data.GameData;
import com.wanniu.game.data.RewardListCO;
import com.wanniu.game.data.base.DItemEquipBase;
import com.wanniu.game.guild.GuildUtil;
import com.wanniu.game.guild.ItemRecordInfo;
import com.wanniu.game.guild.dao.GuildDao;
import com.wanniu.game.guild.guidDepot.GuildAuctionLog;
import com.wanniu.game.item.ItemUtil;
import com.wanniu.game.item.NormalItem;
import com.wanniu.game.item.VirtualItemType;
import com.wanniu.game.mail.MailUtil;
import com.wanniu.game.mail.SysMailConst;
import com.wanniu.game.mail.data.MailData.Attachment;
import com.wanniu.game.mail.data.MailSysData;
import com.wanniu.game.player.GlobalConfig;
import com.wanniu.game.player.PlayerUtil;
import com.wanniu.game.player.WNPlayer;
import com.wanniu.game.poes.AuctionItemPO;
import com.wanniu.game.poes.GuildBossPo;
import com.wanniu.game.poes.GuildMemberPO;
import com.wanniu.game.poes.GuildPO;
import com.wanniu.game.poes.PlayerPO;
import com.wanniu.redis.PlayerPOManager;

import pomelo.auction.AuctionHandler.AddAuctionItemPush;
import pomelo.auction.AuctionHandler.AuctionItem;
import pomelo.auction.AuctionHandler.AuctionItemPush;
import pomelo.auction.AuctionHandler.RemoveAuctionItemPush;

/**
 * 竞拍服务类.
 *
 * @author 周明凯(zhoumingkai@qeng.cn)
 */
public class AuctionService {
	private static final AuctionService instance = new AuctionService();
	// 同步竞拍的列表.角色ID->同步时间
	private static final Map<String, Long> syncs = new ConcurrentHashMap<>();
	// 竟拍物品的定时任务
	private static final Map<String, ScheduledFuture<?>> futures = new ConcurrentHashMap<>();

	public static AuctionService getInstance() {
		return instance;
	}

	/**
	 * 添加同步请求.
	 */
	public void addSyncRequest(String playerId) {
		syncs.put(playerId, System.currentTimeMillis());
	}

	/**
	 * 取消同步请求.
	 */
	public void cancelSyncRequest(String playerId) {
		syncs.remove(playerId);
	}

	/**
	 * 添加一批道具进竟拍
	 */
	public void addAuctionItem(List<NormalItem> items, String guildId, String rewardSource) {
		LocalDateTime now = LocalDateTime.now();

		List<AuctionItemPO> newItemList = new ArrayList<>();
		for (NormalItem item : items) {
			AuctionItemPO aitem = create(item, guildId, rewardSource);
			AuctionDataManager.getInstance().addAuctionItem(aitem);
			// 展示结束定时任务
			this.addDelayJob(now, aitem);
			newItemList.add(aitem);
		}

		// 同步添加推送
		GWorld.getInstance().ansycExec(() -> syncAddAuctionItemInfo(newItemList, AuctionConst.TYPE_GUILD_AUCTION));
	}

	/**
	 * 处理仙盟竞拍小红点
	 */
	public void processGuildAuctionsPoint(String guildId) {
		List<GuildMemberPO> memberList = GuildUtil.getGuildMemberList(guildId);
		if (memberList != null && !memberList.isEmpty()) {
			for (GuildMemberPO po : memberList) {
				WNPlayer player = PlayerUtil.getOnlinePlayer(po.playerId);
				if (player != null) {
					player.auctionManager.pushScript();
				}
			}
		}
	}

	/**
	 * 处理世界竞拍小红点
	 */
	public void processWorldAuctionsPoint() {
		Map<String, GPlayer> map = GGame.getInstance().getOnlinePlayers();
		if (map != null && !map.isEmpty()) {
			Collection<GPlayer> playerIds = map.values();
			for (GPlayer player : playerIds) {
				((WNPlayer) player).auctionManager.pushScript();
			}
		}
	}

	// add
	public void syncAddAuctionItemInfo(List<AuctionItemPO> newItemList, int type) {
		for (String playerId : syncs.keySet()) {
			WNPlayer player = GWorld.getInstance().getPlayer(playerId);
			if (player == null) {
				continue;
			}

			AddAuctionItemPush.Builder push = AddAuctionItemPush.newBuilder();
			for (AuctionItemPO item : newItemList) {
				// 判定是否可以跳过.
				if (hasContinue(item, player)) {
					continue;
				}
				push.addS2CItem(toAuctionItem(player, item));
			}
			if (push.getS2CItemCount() > 0) {
				push.setS2CType(type);
				player.receive("auction.auctionPush.addAuctionItemPush", push.build());
			}
		}
	}

	// update
	public void syncAuctionItemInfo(AuctionItemPO item) {
		for (String playerId : syncs.keySet()) {
			WNPlayer player = GWorld.getInstance().getPlayer(playerId);
			if (player == null) {
				continue;
			}

			// 判定是否可以跳过.
			if (hasContinue(item, player)) {
				continue;
			}

			AuctionItemPush.Builder push = AuctionItemPush.newBuilder();
			push.setS2CItem(toAuctionItem(player, item));
			player.receive("auction.auctionPush.auctionItemPush", push.build());
		}
	}

	// remove
	public void syncRemoveAuctionItem(AuctionItemPO item) {
		RemoveAuctionItemPush push = RemoveAuctionItemPush.newBuilder().setId(item.id).build();
		for (String playerId : syncs.keySet()) {
			WNPlayer player = GWorld.getInstance().getPlayer(playerId);
			if (player == null) {
				continue;
			}

			// 判定是否可以跳过.
			if (hasContinue(item, player)) {
				continue;
			}

			player.receive("auction.auctionPush.removeAuctionItemPush", push);
		}
	}

	private boolean hasContinue(AuctionItemPO item, WNPlayer player) {
		// 公会竞拍，不是本公会的也可以不推送.
		if (StringUtils.isNotEmpty(item.guildId)) {
			if (!item.guildId.equals(player.guildManager.getGuildId())) {
				// 且不在参与列表中.
				if (!(item.participant != null && item.participant.contains(player.getId()))) {
					return true;
				}
			}
		}
		return false;
	}

	public AuctionItem toAuctionItem(WNPlayer player, AuctionItemPO item) {
		NormalItem normalItem = ItemUtil.createItemByDbOpts(item.db);
		AuctionItem.Builder builder = AuctionItem.newBuilder();
		builder.setId(item.id);
		builder.setDetail(normalItem.getItemDetail(player.playerBasePO));
		builder.setState(item.state);// 竞拍状态（1=展示，2=竞拍中，3=已结束）
		builder.setTimeleft((int) Duration.between(LocalDateTime.now(), item.stateOverTime).getSeconds());
		builder.setCurPrice(item.nextPrice);
		builder.setMaxPrice(item.maxPrice);
		builder.setSelf(player.getId().equals(item.playerId));
		builder.setNum(normalItem.getNum());
		builder.setSource(item.source == null ? "" : item.source);
		return builder.build();
	}

	private AuctionItemPO create(NormalItem item, String guildId, String rewardSource) {
		AuctionItemPO data = new AuctionItemPO();
		data.id = UUID.randomUUID().toString();
		data.db = item.itemDb;
		// 强制绑定
		data.db.isBind = ForceType.BIND.getValue();

		// 新建状态为展示5分钟
		data.state = AuctionConst.STATE_SHOW;
		data.stateOverTime = LocalDateTime.now().plusMinutes(GlobalConfig.Auction_GuildShowTime).plusSeconds(RandomUtils.nextInt(1, 60));
		data.guildId = guildId;
		data.source = rewardSource;

		Optional<RewardListCO> template = GameData.RewardLists.values().stream().filter(v -> data.db.code.equals(v.code)).findFirst();
		if (template.isPresent()) {
			data.curPrice = template.get().auctionMin * item.getNum();// 竞价
			data.maxPrice = template.get().auctionMax * item.getNum();// 一口价
		} else {
			Out.warn("竞拍物品找不到竞价模板，code=", data.db.code);
			data.curPrice = 100 * item.getNum();// 竞价
			data.maxPrice = 10_0000 * item.getNum();// 一口价
		}
		data.nextPrice = data.curPrice;
		return data;
	}

	/**
	 * 获取自己仙盟的竞拍
	 */
	public List<AuctionItemPO> getGuildAuctionItemList(WNPlayer player) {
		String guildId = player.guildManager.getGuildId();
		if (StringUtils.isEmpty(guildId)) {
			return Collections.emptyList();
		}
		return AuctionDataManager.getInstance().getItemByPredicate(v -> guildId.equals(v.guildId));
	}

	/**
	 * 获取自己参与的竟拍物品.
	 */
	public List<AuctionItemPO> getSelfAuctionItemList(WNPlayer player) {
		return AuctionDataManager.getInstance().getItemByPredicate(v -> v.participant != null && v.participant.contains(player.getId()));
	}

	/**
	 * 获取世界竟拍物品.
	 */
	public List<AuctionItemPO> getWorldAuctionItemList(WNPlayer player) {
		List<AuctionItemPO> list = AuctionDataManager.getInstance().getItemByPredicate(v -> v.guildId == null);
		if (list != null && !list.isEmpty()) {// 只有当有拍卖物品的时候才把红点干掉
			GuildBossPo guildBossPO = player.guildBossManager.getAndCheckUpdateGuildBossPo(player.player.id);
			guildBossPO.aucpoint = 1;

			player.auctionManager.pushScript();
		}
		return list;
	}

	public void addDelayJob(LocalDateTime now, AuctionItemPO item) {
		long timeleft = Duration.between(now, item.stateOverTime).getSeconds();
		futures.put(item.id, JobFactory.addDelayJob(() -> {
			removeSchedule(item.id);
			GWorld.getInstance().ansycExec(new AuctionTimeoutHandler(item.id));
		}, timeleft, TimeUnit.SECONDS));
	}

	public void resetDelayJob(LocalDateTime now, AuctionItemPO item) {
		ScheduledFuture<?> future = futures.remove(item.id);
		if (future != null) {
			future.cancel(false);
			if (!future.isCancelled()) {
				Out.warn("记录一个非法状态, itemId=", item.id);
				return;
			}
		}

		// 重新添加
		this.addDelayJob(now, item);
	}

	public void removeSchedule(String id) {
		futures.remove(id);// 移除掉这个任务
	}

	/**
	 * 结算归属.
	 */
	public void settlementAttribution(AuctionItemPO item) {
		String playerId = item.playerId;
		Out.info("结算竟拍归属，id=", item.id, ",code=", item.db.code, ",playerId=", playerId, ",price=", item.curPrice);
		// 从仓库里删除
		AuctionDataManager.getInstance().removeAuctionItem(item.id);
		ScheduledFuture<?> future = futures.remove(item.id);
		if (future != null) {
			future.cancel(false);
		}
		boolean isPrice = item.curPrice >= item.maxPrice;
		if (StringUtils.isNotEmpty(playerId)) {
			try {
				String mailKey = isPrice ? SysMailConst.AuctionGetReward2 : SysMailConst.AuctionGetReward1;
				MailSysData mail = new MailSysData(mailKey);
				mail.entityItems = new ArrayList<>();
				mail.entityItems.add(item.db);

				mail.replace = new HashMap<>();
				mail.replace.put("Price", String.valueOf(item.curPrice));
				DItemEquipBase itemBase = ItemUtil.getPropByCode(item.db.code);
				mail.replace.put("Item", MessageUtil.itemColorName(itemBase.qcolor, itemBase.name));

				MailUtil.getInstance().sendMailToOnePlayer(playerId, mail, GOODS_CHANGE_TYPE.AUCTION);

				// 如果花元宝了，要添加要总消耗里去...
				if (item.diamond > 0) {
					PlayerPO playerPo = PlayerUtil.getPlayerBaseData(playerId);
					if (playerPo != null) {
						playerPo.totalCostDiamond += item.diamond;
						RechargeActivityService.getInstance().onConsumeEvent(playerId, item.diamond);

						Out.info("add totalCostDiamond playerId=", playerId, ",num=", item.diamond);
					} else {
						Out.warn("add totalCostDiamond error. playerId=", playerId, ",num=", item.diamond);
					}
				}
			} catch (Exception e) {
				Out.warn("竞拍结算归属异常.playerId=", playerId, ",diamond=", item.diamond, ",ticket=", item.ticket, ",itemcode=", item.db.code);
			}
		}

		// 有仙盟，记录日志
		String guildId = item.guildId;
		if (StringUtils.isNotEmpty(guildId)) {
			int type = isPrice ? AuctionConst.LOG_TYPE_AUCTION_MAX : AuctionConst.LOG_TYPE_AUCTION_CUR;
			GWorld.getInstance().ansycExec(() -> log(guildId, playerId, type, item.db.code, item.curPrice));
		}

		// 推送移除。
		GWorld.getInstance().ansycExec(() -> AuctionService.getInstance().syncRemoveAuctionItem(item));

		// 尝试分红
		this.trySendAuctionBonus(guildId);

		// 推送红点
		this.processWorldAuctionsPoint();
	}

	public void trySendAuctionBonus(String guildId) {
		// 没有公会的不管
		if (StringUtils.isEmpty(guildId)) {
			return;
		}

		// 还有此公会的物品，跳过
		if (AuctionDataManager.getInstance().hasGuildItem(guildId)) {
			return;
		}

		// 公会PO
		GuildPO guild = GuildUtil.getGuild(guildId);
		if (guild == null) {
			return;
		}

		// 公会人数
		Set<String> ids = GuildUtil.getGuildMemberIdList(guildId);
		if (ids == null || ids.isEmpty()) {
			return;
		}

		final int totalBonus;
		final int bonus;
		synchronized (guild) {
			totalBonus = guild.auctionBonus;
			bonus = Math.min(GlobalConfig.Auction_MaxBonus, totalBonus / ids.size());
			if (bonus <= 0) {
				return;
			}
			guild.auctionBonus = 0;
		}

		// 发邮件，分红
		for (String playerId : ids) {
			try {
				MailSysData mail = new MailSysData(SysMailConst.AuctionShareBonus);
				mail.attachments = new ArrayList<>();

				// 绑定元宝
				Attachment att = new Attachment();
				att.itemCode = VirtualItemType.CASH.getItemcode();
				att.itemNum = bonus;
				mail.attachments.add(att);

				mail.replace = new HashMap<>();
				mail.replace.put("Price", String.valueOf(totalBonus));
				mail.replace.put("Num", String.valueOf(bonus));

				MailUtil.getInstance().sendMailToOnePlayer(playerId, mail, GOODS_CHANGE_TYPE.AUCTION_BONUS);
			} catch (Exception e) {
				Out.warn("竞拍分红异常.playerId=", playerId, ",ticket=", bonus);
			}
		}
	}

	/**
	 * 归还竞价.
	 */
	public void restitution(AuctionItemPO item) {
		try {
			MailSysData mail = new MailSysData(SysMailConst.AuctionReturn);
			mail.attachments = new ArrayList<>();

			if (item.diamond > 0) {// 充值元宝
				Attachment att = new Attachment();
				att.itemCode = VirtualItemType.DIAMOND.getItemcode();
				att.itemNum = item.diamond;
				mail.attachments.add(att);
			}
			if (item.ticket > 0) {// 绑定元宝
				Attachment att = new Attachment();
				att.itemCode = VirtualItemType.CASH.getItemcode();
				att.itemNum = item.ticket;
				mail.attachments.add(att);
			}

			mail.replace = new HashMap<>();
			DItemEquipBase itemBase = ItemUtil.getPropByCode(item.db.code);
			mail.replace.put("Item", MessageUtil.itemColorName(itemBase.qcolor, itemBase.name));
			MailUtil.getInstance().sendMailToOnePlayer(item.playerId, mail, GOODS_CHANGE_TYPE.AUCTION_RESTITUTION);
		} catch (Exception e) {
			Out.warn("竞拍归还竞价异常.playerId=", item.playerId, ",diamond=", item.diamond, ",ticket=", item.ticket);
		}
	}

	/**
	 * 记录日志.
	 * 
	 * @param guildId 公会ID
	 * @param playerId 竞拍玩家ID
	 * @param type 日志类型
	 * @param itemcode 物品编号
	 * @param price 成交价格
	 */
	public void log(String guildId, String playerId, int type, String itemcode, int price) {
		GuildAuctionLog log = new GuildAuctionLog();
		log.type = type;
		DItemEquipBase itemBase = ItemUtil.getPropByCode(itemcode);
		log.item = new ItemRecordInfo();
		log.item.name = itemBase.name;
		log.item.qColor = itemBase.qcolor;
		log.price = price;
		log.time = LocalDateTime.now().format(DateUtils.F_YYYYMMDDHHMMSS);
		if (StringUtils.isNotEmpty(playerId)) {
			PlayerPO baseData = PlayerPOManager.findPO(ConstsTR.playerTR, playerId, PlayerPO.class);
			if (baseData != null) {
				log.player = baseData.name;
			}
		}
		this.addGuildAuctionLog(guildId, log);
	}

	public void addGuildAuctionLog(String guildId, GuildAuctionLog record) {
		List<GuildAuctionLog> logs = GuildDao.getGuildAuctionLog(guildId);
		synchronized (logs) {
			logs.add(0, record);
			while (logs.size() > 50) {
				logs.remove(logs.size() - 1);
			}
		}
		GuildDao.saveGuildAuctionLog(guildId);
	}
}