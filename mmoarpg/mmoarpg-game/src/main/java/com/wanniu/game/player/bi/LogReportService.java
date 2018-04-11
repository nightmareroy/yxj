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
package com.wanniu.game.player.bi;

import com.wanniu.core.logfs.Out;
import com.wanniu.game.common.Const;
import com.wanniu.game.item.VirtualItemType;
import com.wanniu.game.player.BILogService;
import com.wanniu.game.player.WNPlayer;
import com.wanniu.game.player.bi.jsfunny.ConsignmentData;
import com.wanniu.game.player.bi.jsfunny.CreatePlayerData;
import com.wanniu.game.player.bi.jsfunny.FashionData;
import com.wanniu.game.player.bi.jsfunny.ItemFlowData;
import com.wanniu.game.player.bi.jsfunny.LoginData;
import com.wanniu.game.player.bi.jsfunny.LuckDrawData;
import com.wanniu.game.player.bi.jsfunny.MoneyFlowData;
import com.wanniu.game.player.bi.jsfunny.MoneyMonitorData;
import com.wanniu.game.player.bi.jsfunny.MountSkinData;
import com.wanniu.game.player.bi.jsfunny.MountUpgradeData;
import com.wanniu.game.player.bi.jsfunny.OnlineCountData;
import com.wanniu.game.player.bi.jsfunny.PacketMonitorData;
import com.wanniu.game.player.bi.jsfunny.PetSkinData;
import com.wanniu.game.player.bi.jsfunny.PetUpgradeData;
import com.wanniu.game.player.bi.jsfunny.RechargeData;
import com.wanniu.game.player.bi.jsfunny.RoleUpgradeData;
import com.wanniu.game.player.bi.jsfunny.ShopData;
import com.wanniu.game.poes.PlayerPO;

/**
 * 日志上报服务.
 *
 * @author 周明凯(zhoumingkai@qeng.cn)
 */
public class LogReportService {
	private static final LogReportService instance = new LogReportService();

	public static final int OPERATE_ADD = 1;// 增加
	public static final int OPERATE_COST = 2;// 减少

	public static LogReportService getInstance() {
		return instance;
	}

	/**
	 * 异步上报在线数据...
	 * 
	 * @param onlineCount 玩家在线数量
	 * @param robotOnlineCount 机器人在线数量
	 */
	public void ansycReportOnline(int onlineCount, int robotOnlineCount) {
		try {
			new OnlineCountData(onlineCount, robotOnlineCount).publishing();
		} catch (Exception e) {
			Out.warn("ansycReportOnline Exception", e);
		}
	}

	/**
	 * 异步上报升级数据...
	 */
	public void ansycReportUpgrade(WNPlayer player) {
		try {
			new RoleUpgradeData(player).publishing();
		} catch (Exception e) {
			Out.warn("ansycReportUpgrade Exception", e);
		}
	}

	/**
	 * 异步上报货币流数据...
	 */
	public void ansycReportMoneyFlow(PlayerPO player, VirtualItemType type, int before, int operate, int value, int after, int origin) {
		try {
			new MoneyFlowData(player, type, before, operate, value, after, origin).publishing();

			// 给清源一份货币变更数据...
			switch (type) {
			case DIAMOND:
				BILogService.getInstance().ansycReportEconomy(player, "yb", operate == OPERATE_ADD, value, origin);
				break;
			case CASH:
				BILogService.getInstance().ansycReportEconomy(player, "bdyb", operate == OPERATE_ADD, value, origin);
				break;
			case GOLD:
				BILogService.getInstance().ansycReportEconomy(player, "yl", operate == OPERATE_ADD, value, origin);
				break;
			default:
				break;
			}
		} catch (Exception e) {
			Out.warn("ansycReportMoneyFlow Exception", e);
		}
	}

	/**
	 * 异步上报道具流数据...
	 * 
	 * @param name
	 */
	public void ansycReportItemFlow(PlayerPO player, int operate, String itemcode, int count, boolean bind, Const.GOODS_CHANGE_TYPE origin, String name) {
		if (origin == null) {
			origin = Const.GOODS_CHANGE_TYPE.def;
		}
		try {
			new ItemFlowData(player, operate, itemcode, count, bind, origin.value).publishing();

			// 给清源一份上报
			BILogService.getInstance().ansycReportItem(player, operate == OPERATE_ADD, itemcode, count, origin, name);
		} catch (Exception e) {
			Out.warn("ansycReportItemFlow Exception", e);
		}
	}

	/**
	 * 异步上报坐骑升级数据.
	 */
	public void ansycReportMountUpgrade(WNPlayer player, int rideLevel, int starLv) {
		try {
			new MountUpgradeData(player.getPlayer(), rideLevel, starLv).publishing();
		} catch (Exception e) {
			Out.warn("ansycReportMount Exception", e);
		}
	}

	/**
	 * 异步上报宠物升级数据.
	 */
	public void ansycReportPetUpgrade(WNPlayer player, int petId, String petName, int upLevel, int level, long exp) {
		try {
			new PetUpgradeData(player.getPlayer(), petId, petName, upLevel, level, exp).publishing();
		} catch (Exception e) {
			Out.warn("ansycReportMount Exception", e);
		}
	}

	/**
	 * 异步上报充值数据.
	 */
	public void ansycReportRecharge(PlayerPO player, int productId, int type, int money) {
		try {
			new RechargeData(player, productId, type, money).publishing();
		} catch (Exception e) {
			Out.warn("ansycReportRecharge Exception", e);
		}
	}

	/**
	 * 异步上报商城购买信息.
	 */
	public void ansycReportShop(WNPlayer player, String itemcode, int itemnum, int consumeType, int costMoney) {
		try {
			new ShopData(player.getPlayer(), itemcode, itemnum, consumeType, costMoney).publishing();
		} catch (Exception e) {
			Out.warn("ansycReportShop Exception", e);
		}
	}

	/**
	 * 异步上报创建角色信息.
	 */
	public void ansycReportCreatePlayer(WNPlayer player) {
		try {
			new CreatePlayerData(player.getPlayer()).publishing();
		} catch (Exception e) {
			Out.warn("ansycReportCreatePlayer Exception", e);
		}
	}

	public void ansycReportMountSkin(WNPlayer player, int skinId) {
		try {
			new MountSkinData(player.getPlayer(), skinId).publishing();
		} catch (Exception e) {
			Out.warn("ansycReportMountSkin Exception", e);
		}
	}

	/**
	 * 宠物种类.
	 */
	public void ansycReportPetSkin(WNPlayer player, int petId) {
		try {
			new PetSkinData(player.getPlayer(), petId).publishing();
		} catch (Exception e) {
			Out.warn("ansycReportPetSkin Exception", e);
		}
	}

	/**
	 * 上报时装.
	 */
	public void ansycReportFashion(WNPlayer player, String code) {
		try {
			new FashionData(player.getPlayer(), code).publishing();
		} catch (Exception e) {
			Out.warn("ansycReportFashion Exception", e);
		}
	}

	/**
	 * 上报抽奖数据.
	 */
	public void ansycReportLuckDraw(WNPlayer player, int type, int count, int money, int itemcount) {
		try {
			new LuckDrawData(player.getPlayer(), type, count, money, itemcount).publishing();
		} catch (Exception e) {
			Out.warn("ansycReportLuckDraw Exception", e);
		}
	}

	/**
	 * 异步上报拍卖交易
	 */
	public void ansycReportConsignment(WNPlayer player, String buyerId, String buyerName, String itemcode, int itemcount) {
		try {
			new ConsignmentData(player.getPlayer(), buyerId, buyerName, itemcode, itemcount).publishing();
		} catch (Exception e) {
			Out.warn("ansycReportConsignment Exception", e);
		}
	}

	/**
	 * 异步上报货币监控.
	 */
	public void ansycReportMoneyMonitor(WNPlayer player, VirtualItemType type, int today, int threshold) {
		try {
			new MoneyMonitorData(player.getPlayer(), type, today, threshold).publishing();
		} catch (Exception e) {
			Out.warn("ansycReportMoneyMonitor Exception", e);
		}
	}

	/**
	 * 异步上报封包监控.
	 */
	public void ansycReportPacketMonitor(PlayerPO player, Integer count, short type, String route) {
		try {
			new PacketMonitorData(player, count, type, route).publishing();
		} catch (Exception e) {
			Out.warn("ansycReportPacketMonitor Exception", e);
		}
	}

	/**
	 * 登录
	 */
	public void ansycReportLogin(PlayerPO player) {
		try {
			new LoginData(player).publishing();
		} catch (Exception e) {
			Out.warn("ansycReportLogin Exception", e);
		}
	}
}