package com.wanniu.game.money;

import java.util.Date;
import java.util.HashMap;
import java.util.List;

import com.wanniu.core.logfs.Out;
import com.wanniu.game.activity.RechargeActivityService;
import com.wanniu.game.common.Const;
import com.wanniu.game.common.Const.GOODS_CHANGE_TYPE;
import com.wanniu.game.common.HackerException;
import com.wanniu.game.daoyou.AllyConfiguration;
import com.wanniu.game.daoyou.DaoYouService;
import com.wanniu.game.item.VirtualItemType;
import com.wanniu.game.player.WNPlayer;
import com.wanniu.game.player.bi.LogReportService;
import com.wanniu.game.poes.PlayerPO;
import com.wanniu.game.poes.XianYuanPO;
import com.wanniu.game.rank.RankType;
import com.wanniu.game.sevengoal.SevenGoalManager.SevenGoalTaskType;

import pomelo.Common.KeyValueStruct;

/**
 * 货币相关的业务管理类.
 *
 * @author 小流氓(zhoumingkai@qeng.cn)
 */
public class MoneyManager {
	private final WNPlayer player;
	/** 今天最大收益阀值 */
	public static int today_max_diamond_threshold = 10_0000;
	public static int today_max_kicket_threshold = 2_0000;
	public static int today_max_gold_threshold = 2_0000_0000;

	public MoneyManager(WNPlayer player) {
		this.player = player;
	}

	// 获取基本数据的接口.
	private PlayerPO getBaseData() {
		return player.player;
	}

	/**
	 * 判定玩家身上的银两是否足够.
	 * 
	 * @param num 需要消耗的银两值
	 * @return 如果足够返回true，否则返回false.
	 */
	public boolean enoughGold(int num) {
		if (num < 0) {
			throw new HackerException("判定玩家身上的银两是否足够时参数小于0.");
		}
		return getBaseData().gold >= num;
	}

	/**
	 * 消耗银两接口.
	 * <p>
	 * 1.此方法会自动推送更新协议<br>
	 * 2.此方法会自动重新计算强化打造红点<br>
	 * 3.刷新公会捐献红点<br>
	 * 4.BI系统接入...
	 * 
	 * @param num 需要消耗的银两值
	 * @param origin 消耗来源(必填) 参考{@link Const.GOODS_CHANGE_TYPE}
	 * @return 消耗成功返回true,否则返回false.
	 */
	public boolean costGold(int num, Const.GOODS_CHANGE_TYPE origin) {
		if (num == 0) {
			return true;
		}
		if (!enoughGold(num)) {
			return false;
		}
		int before = getBaseData().gold;
		getBaseData().gold -= num;// 扣钱
		int after = getBaseData().gold;
		Out.info("cost gold. id=", player.getId(), ",name=", player.getName(), ",before=", before, ",value=", num, ",after=", after, ",origin=", origin.value);
		LogReportService.getInstance().ansycReportMoneyFlow(player.getPlayer(), VirtualItemType.GOLD, before, LogReportService.OPERATE_COST, num, after, origin.value);

		// 推送更新协议给客户端
		this.pushDynamicDataByGold(origin);

		this.updateScript();
		return true;
	}

	/**
	 * 根据万分比扣钱
	 * 
	 * @param percent
	 * @param origin
	 * @return
	 */
	public int costGoldByPercent(int percent, Const.GOODS_CHANGE_TYPE origin) {
		if (percent <= 0 || getBaseData().gold <= 0) {
			return 0;
		}
		float fc = (1.0f * getBaseData().gold / 10000) * percent;
		int cost = Math.round(fc);
		cost = cost <= 0 ? 1 : cost;
		boolean success = costGold(cost, origin);
		if (!success) {
			Out.warn("cost gold not enough并发!!!!!. id=", player.getId(), ",name=", player.getName(), "origin=", origin.value, ",cost:", cost);
		}
		return success ? cost : 0;
	}

	/**
	 * 根据万分比扣钱
	 * 
	 * @param percent
	 * @param origin
	 * @return
	 */
	public int costGoldOnPk(int goldNum, Const.GOODS_CHANGE_TYPE origin) {
		if (goldNum <= 0 || getBaseData().gold <= 0) {
			return 0;
		}

		boolean success = costGold(goldNum, origin);
		if (!success) {
			Out.warn("cost gold not enough并发!!!!!. id=", player.getId(), ",name=", player.getName(), "origin=", origin.value, ",cost:", goldNum);
		}
		return success ? goldNum : 0;
	}

	/**
	 * 增加银两接口.
	 * <p>
	 * 1.此方法会自动推送更新协议<br>
	 * 2.成就计算.<br>
	 * 3.此方法会自动重新计算强化打造红点(非打怪掉落)<br>
	 * 4.刷新公会捐献红点(非打怪掉落)<br>
	 * 5.BI系统接入...
	 * 
	 * @param num 需要增加的银两值
	 * @param origin 增加来源(必填) 参考{@link Const.GOODS_CHANGE_TYPE}
	 */
	public void addGold(int num, Const.GOODS_CHANGE_TYPE origin) {
		if (num == 0) {// 正常逻辑，任务奖励有可能设计为0，然后附加发一笔奖励
			return;
		}
		if (num < 0) {
			throw new HackerException("增加银两时参数小于0.");
		}
		int before = getBaseData().gold;
		// 溢出判定
		if (0L + this.getBaseData().gold + num > Integer.MAX_VALUE) {
			this.getBaseData().gold = Integer.MAX_VALUE;
		} else {
			this.getBaseData().gold += num;
		}
		int after = getBaseData().gold;

		// 怪物掉落不记录日志，太多了...
		if (origin != Const.GOODS_CHANGE_TYPE.monsterdrop) {
			Out.info("add gold. id=", player.getId(), ",name=", player.getName(), ",before=", before, ",value=", num, ",after=", after, ",origin=", origin.value);
			LogReportService.getInstance().ansycReportMoneyFlow(player.getPlayer(), VirtualItemType.GOLD, before, LogReportService.OPERATE_ADD, num, after, origin.value);
		}

		// 推送更新协议给客户端
		this.pushDynamicDataByGold(origin);
		player.achievementManager.onGetGold(num);
		if (origin != GOODS_CHANGE_TYPE.monsterdrop) {
			this.updateScript();
		}

		// 今日
		this.getBaseData().todayGold += num;
		if (this.getBaseData().todayGold >= today_max_gold_threshold) {
			Out.warn("今日银两收益超过预期值,playerId:", player.getId(), ",name:", player.getName(), ",todayGold:", this.getBaseData().todayGold, ",max:", today_max_gold_threshold);
			LogReportService.getInstance().ansycReportMoneyMonitor(player, VirtualItemType.GOLD, this.getBaseData().todayGold, today_max_gold_threshold);
		}
	}

	/**
	 * 获取当前所拥有的银两值
	 * 
	 * @return 当前所拥有的银两值
	 */
	public int getGold() {
		return this.getBaseData().gold;
	}

	// 推送更新协议给客户端
	private void pushDynamicDataByGold(Const.GOODS_CHANGE_TYPE origin) {
		player.pushDynamicData("gold", this.getBaseData().gold, origin);
	}

	// 更新红点
	private void updateScript() {
		// 刷新强化红点
		player.equipManager.updateStrengthScript(null);
		player.equipManager.updateMakeScript(null);
		// 属性公会捐献红点
		player.guildManager.pushRedPoint();
	}

	/**
	 * 判定玩家身上的充值元宝是否足够.
	 * 
	 * @param num 需要消耗的充值元宝值
	 * @return 如果足够返回true，否则返回false.
	 */
	public boolean enoughDiamond(int num) {
		if (num < 0) {
			throw new HackerException("判定玩家身上的充值元宝是否足够时参数小于0.");
		}
		return getBaseData().diamond >= num;
	}

	/**
	 * 消耗充值元宝接口.
	 * <p>
	 * 1.此方法会自动推送更新协议<br>
	 * 2.道友返利...<br>
	 * 3.BI系统接入...
	 * 
	 * @param num 需要消耗的充值元宝值
	 * @param origin 消耗来源(必填) 参考{@link Const.GOODS_CHANGE_TYPE}
	 * @return 消耗成功返回true,否则返回false.
	 */
	public boolean costDiamond(int num, Const.GOODS_CHANGE_TYPE origin) {
		return costDiamond(num, origin, null);
	}

	/**
	 * 消耗充值元宝接口.
	 * <p>
	 * 1.此方法会自动推送更新协议<br>
	 * 2.道友返利...<br>
	 * 3.BI系统接入...
	 * 
	 * @param num 需要消耗的充值元宝值
	 * @param origin 消耗来源(必填) 参考{@link Const.GOODS_CHANGE_TYPE}
	 * @param itemChange 涉及道具变更
	 * @return 消耗成功返回true,否则返回false.
	 */
	public boolean costDiamond(int num, Const.GOODS_CHANGE_TYPE origin, List<KeyValueStruct> itemChange) {
		if (num == 0) {
			return true;
		}
		if (!enoughDiamond(num)) {
			return false;
		}
		int before = getBaseData().diamond;
		this.getBaseData().diamond -= num;
		int after = getBaseData().diamond;
		Out.info("cost diamond. id=", player.getId(), ",name=", player.getName(), ",before=", before, ",value=", num, ",after=", after, ",origin=", origin.value);
		LogReportService.getInstance().ansycReportMoneyFlow(player.getPlayer(), VirtualItemType.DIAMOND, before, LogReportService.OPERATE_COST, num, after, origin.value);

		// 竞拍消费是不参数累计的
		if (!Const.GOODS_CHANGE_TYPE.AUCTION.equals(origin)&&!Const.GOODS_CHANGE_TYPE.RedPacket.equals(origin)) {
			this.getBaseData().totalCostDiamond += num;
			RechargeActivityService.getInstance().onConsumeEvent(player.getId(), num);

			// 道友返利...
			if (num > AllyConfiguration.getInstance().getConfigI("MinCostDiamond")) {
				int value = num * AllyConfiguration.getInstance().getConfigI("FeeBackRate") / 100;
				DaoYouService.getInstance().calDaoYouRebate(player, value);
			}
		}

		player.pushDynamicData("diamond", this.getBaseData().diamond, origin, itemChange);

		player.sevenGoalManager.processGoal(SevenGoalTaskType.COST_DIAMOND_COUNT);
		player.sevenGoalManager.processGoal(SevenGoalTaskType.COST_DIAMOND_OR_BINDDIAMOND_COUNT, num);
		return true;
	}

	/**
	 * 增加充值元宝接口.
	 * <p>
	 * 1.此方法会自动推送更新协议<br>
	 * 2.BI系统接入...
	 * 
	 * @param num 需要增加充值元宝值
	 * @param origin 增加来源(必填) 参考{@link Const.GOODS_CHANGE_TYPE}
	 */
	public void addDiamond(int num, Const.GOODS_CHANGE_TYPE origin) {
		if (num == 0) {// 正常逻辑，邮件领取...
			return;
		}
		if (num < 0) {
			throw new HackerException("增加充值元宝时参数小于0.");
		}
		int before = getBaseData().diamond;
		// 溢出判定
		if (0L + this.getBaseData().diamond + num > Integer.MAX_VALUE) {
			this.getBaseData().diamond = Integer.MAX_VALUE;
		} else {
			this.getBaseData().diamond += num;
		}
		int after = getBaseData().diamond;
		Out.info("add diamond. id=", player.getId(), ",name=", player.getName(), ",before=", before, ",value=", num, ",after=", after, ",origin=", origin.value);
		LogReportService.getInstance().ansycReportMoneyFlow(player.getPlayer(), VirtualItemType.DIAMOND, before, LogReportService.OPERATE_ADD, num, after, origin.value);

		player.pushDynamicData("diamond", this.getBaseData().diamond, origin);

		// 今日
		this.getBaseData().todayDiamond += num;
		if (this.getBaseData().todayDiamond >= today_max_diamond_threshold) {
			Out.warn("今日充值元宝收益超过预期值,playerId:", player.getId(), ",name:", player.getName(), ",todayDiamond:", this.getBaseData().todayDiamond, ",max:", today_max_diamond_threshold);
			LogReportService.getInstance().ansycReportMoneyMonitor(player, VirtualItemType.DIAMOND, this.getBaseData().todayDiamond, today_max_diamond_threshold);
		}
	}

	/**
	 * 获取当前所拥有的充值元宝
	 * 
	 * @return 当前所拥有的充值元宝
	 */
	public int getDiamond() {
		return this.getBaseData().diamond;
	}

	/**
	 * 判定玩家身上的绑定元宝是否足够.
	 * 
	 * @param num 需要消耗的绑定元宝值
	 * @return 如果足够返回true，否则返回false.
	 */
	public boolean enoughTicket(int num) {
		if (num < 0) {
			throw new HackerException("判定玩家身上的绑定元宝是否足够时参数小于0.");
		}
		return getBaseData().ticket >= num;
	}

	/**
	 * 消耗绑定元宝接口.
	 * <p>
	 * 1.此方法会自动推送更新协议<br>
	 * 2.BI系统接入...
	 * 
	 * @param num 需要消耗的绑定元宝值
	 * @param origin 消耗来源(必填) 参考{@link Const.GOODS_CHANGE_TYPE}
	 * @return 消耗成功返回true,否则返回false.
	 */
	public boolean costTicket(int num, GOODS_CHANGE_TYPE origin) {
		return costTicket(num, origin, null);
	}

	/**
	 * 消耗绑定元宝接口.
	 * <p>
	 * 1.此方法会自动推送更新协议<br>
	 * 2.BI系统接入...
	 * 
	 * @param num 需要消耗的绑定元宝值
	 * @param origin 消耗来源(必填) 参考{@link Const.GOODS_CHANGE_TYPE}
	 * @param itemChange 涉及道具变更
	 * @return 消耗成功返回true,否则返回false.
	 */
	public boolean costTicket(int num, GOODS_CHANGE_TYPE origin, List<KeyValueStruct> itemChange) {
		if (num == 0) {
			return true;
		}
		if (!enoughTicket(num)) {
			return false;
		}
		int before = getBaseData().ticket;
		getBaseData().ticket -= num;
		int after = getBaseData().ticket;
		Out.info("cost ticket. id=", player.getId(), ",name=", player.getName(), ",before=", before, ",value=", num, ",after=", after, ",origin=", origin.value);
		LogReportService.getInstance().ansycReportMoneyFlow(player.getPlayer(), VirtualItemType.CASH, before, LogReportService.OPERATE_COST, num, after, origin.value);

		player.pushDynamicData("ticket", getBaseData().ticket, origin, itemChange);
		player.sevenGoalManager.processGoal(SevenGoalTaskType.COST_DIAMOND_OR_BINDDIAMOND_COUNT, num);
		return true;
	}

	/**
	 * 增加绑定元宝接口.
	 * <p>
	 * 1.此方法会自动推送更新协议<br>
	 * 2.BI系统接入...
	 * 
	 * @param num 需要增加绑定元宝值
	 * @param origin 增加来源(必填) 参考{@link Const.GOODS_CHANGE_TYPE}
	 */
	public void addTicket(int num, Const.GOODS_CHANGE_TYPE origin) {
		if (num == 0) {// 正常逻辑，任务奖励有可能设计为0，然后附加发一笔奖励
			return;
		}
		if (num < 0) {
			throw new HackerException("增加绑定元宝时参数小于0.");
		}
		int before = getBaseData().ticket;
		// 溢出判定
		if (0L + this.getBaseData().ticket + num > Integer.MAX_VALUE) {
			this.getBaseData().ticket = Integer.MAX_VALUE;
		} else {
			this.getBaseData().ticket += num;
		}
		int after = getBaseData().ticket;
		Out.info("add ticket. id=", player.getId(), ",name=", player.getName(), ",before=", before, ",value=", num, ",after=", after, ",origin=", origin.value);
		LogReportService.getInstance().ansycReportMoneyFlow(player.getPlayer(), VirtualItemType.CASH, before, LogReportService.OPERATE_ADD, num, after, origin.value);

		if (origin != Const.GOODS_CHANGE_TYPE.monsterdrop) {
			player.customTip(Const.CUSTOMTIPTYPE.TICKET, num);
		}
		player.pushDynamicData("ticket", this.getBaseData().ticket, origin);

		// 今日
		this.getBaseData().todayTicket += num;
		if (this.getBaseData().todayTicket >= today_max_kicket_threshold) {
			Out.warn("今日绑定元宝收益超过预期值,playerId:", player.getId(), ",name:", player.getName(), ",todayTicket:", this.getBaseData().todayTicket, ",max:", today_max_kicket_threshold);
			LogReportService.getInstance().ansycReportMoneyMonitor(player, VirtualItemType.CASH, this.getBaseData().todayTicket, today_max_kicket_threshold);
		}
	}

	/**
	 * 获取当前所拥有的绑定元宝
	 * 
	 * @return 当前所拥有的绑定元宝
	 */
	public int getTicket() {
		return this.getBaseData().ticket;
	}

	// 组合元宝
	/**
	 * 判定玩家身上的（绑定元宝+充值元宝）总和是否足够.
	 * 
	 * @param num 需要消耗的绑定元宝值
	 * @return 如果足够返回true，否则返回false.
	 */
	public boolean enoughTicketAndDiamond(int num) {
		if (num < 0) {
			throw new HackerException("判定玩家身上的（绑定元宝+充值元宝）总和是否足够时参数小于0.");
		}
		return 0L + getBaseData().ticket + getBaseData().diamond >= num;
	}

	/**
	 * 消耗（绑定元宝+充值元宝）<b>绑定元宝优先扣除</b>接口.
	 * <p>
	 * <b>绑定元宝优先扣除</b><br>
	 * 等同于分别扣除逻辑...
	 * 
	 * @param num 需要消耗的绑定元宝值
	 * @param origin 消耗来源(必填) 参考{@link Const.GOODS_CHANGE_TYPE}
	 * @return 返回操作结果
	 */
	public CostResult costTicketAndDiamond(int num, GOODS_CHANGE_TYPE origin) {
		return costTicketAndDiamond(num, origin, null);
	}

	/**
	 * 消耗（绑定元宝+充值元宝）<b>绑定元宝优先扣除</b>接口.
	 * <p>
	 * <b>绑定元宝优先扣除</b><br>
	 * 等同于分别扣除逻辑...
	 * 
	 * @param num 需要消耗的绑定元宝值
	 * @param origin 消耗来源(必填) 参考{@link Const.GOODS_CHANGE_TYPE}
	 * @param itemChange 涉及道具变更
	 * @return 返回操作结果
	 */
	public CostResult costTicketAndDiamond(int num, GOODS_CHANGE_TYPE origin, List<KeyValueStruct> itemChange) {
		if (num == 0) {
			return new CostResult(true);
		}
		// 如果绑定元宝直接够了就结束了.
		if (costTicket(num, origin, itemChange)) {
			return new CostResult(true).addValue(VirtualItemType.CASH, num);
		}

		// 两种元宝都不够也结束了
		if (!enoughTicketAndDiamond(num)) {
			return new CostResult(false);
		}

		// 扣完所有绑定元宝，再去扣充值元宝
		int ticket = this.getTicket();
		if (ticket > 0) {
			costTicket(ticket, origin, itemChange);
		}

		int diamond = num - ticket;
		costDiamond(diamond, origin, itemChange);

		return new CostResult(true).addValue(VirtualItemType.CASH, ticket).addValue(VirtualItemType.DIAMOND, diamond);
	}

	// 每日收入监控清0.
	public void refreshNewDay() {
		Out.info("每日监控收入值清零，roleId=", player.getId(), ",todayDiamond=", getBaseData().todayDiamond, ",todayTicket=", getBaseData().todayTicket, ",todayGold=", getBaseData().todayGold);
		this.getBaseData().todayDiamond = 0;
		this.getBaseData().todayTicket = 0;
		this.getBaseData().todayGold = 0;
	}

	/**
	 * 增加消费积分
	 * 
	 * @param num 消费积分值
	 * @param origin 增加来源(必填) 参考{@link Const.GOODS_CHANGE_TYPE}
	 */
	public void addConsumePoint(int num, Const.GOODS_CHANGE_TYPE origin) {
		if (num == 0) {// 正常逻辑
			return;
		}
		if (num < 0) {
			throw new HackerException("增加消费积分时参数小于0.");
		}

		int before = getBaseData().consumePoint;
		// 溢出判定
		if (0L + this.getBaseData().consumePoint + num > Integer.MAX_VALUE) {
			this.getBaseData().consumePoint = Integer.MAX_VALUE;
		} else {
			this.getBaseData().consumePoint += num;
		}
		int after = getBaseData().consumePoint;
		Out.info("add consumePoint. id=", player.getId(), ",name=", player.getName(), ",before=", before, ",value=", num, ",after=", after, ",origin=", origin.value);
		LogReportService.getInstance().ansycReportMoneyFlow(player.getPlayer(), VirtualItemType.CONSUMEPOINT, before, LogReportService.OPERATE_ADD, num, after, origin.value);

		this.pushDynamicDataByConsumePoint(origin);
	}

	/**
	 * 判断是否有足够的消耗积分
	 */
	public boolean enoughConsumePoint(int num) {
		if (num < 0) {
			throw new HackerException("判定玩家身上的消费积分是否足够时参数小于0.");
		}
		return getConsumePoint() >= num;
	}

	/**
	 * 消耗消费积分
	 */
	public boolean costConsumePoint(int num, Const.GOODS_CHANGE_TYPE origin) {
		if (num == 0) {
			return false;
		}
		if (!enoughConsumePoint(num)) {
			return false;
		}

		int before = getBaseData().consumePoint;
		getBaseData().consumePoint -= num;// 扣钱
		int after = getBaseData().consumePoint;
		Out.info("cost consumePoint. id=", player.getId(), ",name=", player.getName(), ",before=", before, ",value=", num, ",after=", after, ",origin=", origin.value);
		LogReportService.getInstance().ansycReportMoneyFlow(player.getPlayer(), VirtualItemType.CONSUMEPOINT, before, LogReportService.OPERATE_COST, num, after, origin.value);

		// 推送更新协议给客户端
		this.pushDynamicDataByConsumePoint(origin);
		return true;
	}

	private void pushDynamicDataByConsumePoint(GOODS_CHANGE_TYPE origin) {
		this.player.pushDynamicData("consumePoint", getBaseData().consumePoint);
	}

	/**
	 * 获取当前所拥有的消费积分
	 * 
	 * @return 当前所拥有的消费积分
	 */
	public int getConsumePoint() {
		return this.getBaseData().consumePoint;
	}

	/**
	 * 获取存着仙缘值PO
	 * 
	 * @return 仙缘值PO
	 */
	private XianYuanPO getXianYuanPo() {
		return player.allBlobData.xianYuan;
	}

	/**
	 * 增加仙缘值.
	 * <p>
	 * 有来源的方式添加仙缘值
	 * 
	 * @param num 消费仙缘值
	 * @param origin 增加来源(必填) 参考{@link Const.GOODS_CHANGE_TYPE}
	 */
	public void addXianYuan(int num, GOODS_CHANGE_TYPE origin) {
		this.addXianYuan(num, origin, -1);
	}

	public void addXianYuan(int num, GOODS_CHANGE_TYPE origin, int from) {
		if (num == 0) {// 正常逻辑
			return;
		}
		if (num < 0) {
			throw new HackerException("增加仙缘时参数小于0.");
		}

		final XianYuanPO po = this.getXianYuanPo();

		int before = po.xianYuanNum;
		// 溢出判定
		if (0L + po.xianYuanNum + num > Integer.MAX_VALUE) {
			po.xianYuanNum = Integer.MAX_VALUE;
		} else {
			po.xianYuanNum += num;
		}
		int after = po.xianYuanNum;

		Out.info("add xianyuan. id=", player.getId(), ",name=", player.getName(), ",before=", before, ",value=", num, ",after=", after, ",origin=", origin.value, ",from=", from);
		LogReportService.getInstance().ansycReportMoneyFlow(player.getPlayer(), VirtualItemType.FATE, before, LogReportService.OPERATE_ADD, num, after, origin.value);

		// 统计历史累计仙缘值
		po.sumXianYuan += num;
		po.updateTime = new Date();

		player.rankManager.onEvent(RankType.XIANYUAN, po.sumXianYuan);

		// 只推增加的值
		player.pushDynamicData("fate", num);
		// 成就
		player.achievementManager.onXianyuanChange(num);

		player.sevenGoalManager.processGoal(SevenGoalTaskType.XIANYUAN_TO, num);
	}

	/**
	 * 增加仙缘值.
	 * <p>
	 * 有上限的添加，做法
	 * 
	 * @param num 消费仙缘值
	 * @param from 做法
	 */
	public void addXianYuan(int num, int from) {
		this.addXianYuan(num, GOODS_CHANGE_TYPE.def, from);

		final XianYuanPO xianYuanPo = this.getXianYuanPo();
		if (xianYuanPo.reviceNumbers == null) {
			xianYuanPo.reviceNumbers = new HashMap<Integer, Integer>();
		}
		xianYuanPo.reviceNumbers.compute(from, (k, v) -> (v == null) ? num : v + num);
	}

	/**
	 * 判断是否有足够的仙缘值
	 */
	public boolean enoughXianYuan(int num) {
		if (num < 0) {
			throw new HackerException("判定玩家身上的仙缘值是否足够时参数小于0.");
		}
		return getXianYuan() >= num;
	}

	public int getXianYuan() {
		return this.getXianYuanPo().xianYuanNum;
	}

	/**
	 * 消耗仙缘值
	 */
	public boolean costXianYuan(int num, Const.GOODS_CHANGE_TYPE origin) {
		if (num == 0) {
			return false;
		}
		if (!enoughXianYuan(num)) {
			return false;
		}
		final XianYuanPO po = this.getXianYuanPo();
		int before = po.xianYuanNum;
		po.xianYuanNum -= num;// 扣钱
		int after = po.xianYuanNum;
		Out.info("cost xianyuan. id=", player.getId(), ",name=", player.getName(), ",before=", before, ",value=", num, ",after=", after, ",origin=", origin.value);
		LogReportService.getInstance().ansycReportMoneyFlow(player.getPlayer(), VirtualItemType.FATE, before, LogReportService.OPERATE_COST, num, after, origin.value);

		return true;
	}
}