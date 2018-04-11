package com.wanniu.game.bag;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import com.wanniu.game.common.Const;
import com.wanniu.game.player.GlobalConfig;
import com.wanniu.game.player.WNPlayer;

public class BagUtil {

	/**
	 * 获取打开背包格子花费
	 */
	public static int getGridPrice(int type) {
		if (type == Const.BAG_TYPE.BAG.getValue()) {
			return GlobalConfig.Package_PricePer;
		} else if (type == Const.BAG_TYPE.RECYCLE.getValue()) {
			return 0;
		} else {
			return GlobalConfig.PersonalWarehouse_PricePer;
		}
	}

	/**
	 * @param costs <itemCode, itemNum>
	 */
	public static boolean checkCost(WNPlayer player, List<Object[]> costs) {
		boolean flag = true;
		if (costs.size() == 0) {
			flag = false;
		}
		for (Object[] intString : costs) {
			if (player.getWnBag().findItemNumByCode((String) intString[0]) < (int) intString[1]) {
				flag = false;
			}
		}

		return flag;
	}

	/**
	 * @param costs {itemCode, itemNum, price}
	 */
	public static void cost(WNPlayer player, List<Object[]> costs, Const.GOODS_CHANGE_TYPE costFrom) {
		// 扣除材料
		costs.forEach(v -> {
			player.getWnBag().discardItem((String) v[0], (int) v[1], costFrom, null, false, false);
		});
	};

	/**
	 * 有就扣
	 * 
	 * @param player
	 * @param costs
	 */
	public static void costJustHave(WNPlayer player, List<Object[]> costs) {
		// 扣除材料
		costs.forEach(v -> {
			int num = player.getWnBag().findItemNumByCode((String) v[0]);
			player.getWnBag().discardItem((String) v[0], num, null, null, false, false);
		});
	};

	/**
	 * 容器单个格子价格 (元宝)
	 * 
	 * @param type
	 * @returns {*}
	 */
	public static int getGridPrice(Const.BAG_TYPE type) {
		if (type == Const.BAG_TYPE.BAG) {
			return GlobalConfig.Package_PricePer;
		} else if (type == Const.BAG_TYPE.RECYCLE) {
			return 0;
		} else {
			return GlobalConfig.PersonalWarehouse_PricePer;
		}
	}

	/**
	 * 容器总格子数
	 * 
	 * @param type
	 * @returns {*}
	 */
	public static int getTotalCount(Const.BAG_TYPE type) {
		if (type == Const.BAG_TYPE.BAG) {
			return GlobalConfig.Package_MaxSize;
		} else if (type == Const.BAG_TYPE.RECYCLE) {
			return GlobalConfig.Package_MaxSize;
		} else {
			return GlobalConfig.PersonalWarehouse_MaxSize;
		}
	};

	/**
	 * 物品出售价格
	 * 
	 * @param price
	 * @param num
	 * @returns {number}
	 */
	public static int getSellPrice(int price, int num) {
		return Math.round(price * GlobalConfig.NpcShop_ItemPrice_CutRate / 100) * num;
	}

	/**
	 * 容器类型
	 * 
	 * @param player
	 * @param type
	 * @returns {*}
	 */
	public static WNBag getStoreByType(WNPlayer player, int type) {
		if (type == Const.BAG_TYPE.BAG.getValue()) {
			return player.bag;
		} else if (type == Const.BAG_TYPE.WAREHOUSE.getValue()) {
			return player.wareHouse;
		} else {
			return null;
		}
	}

	public static void main(String[] args) {
		Lock lock = new ReentrantLock();
		lock.lock();
		try {
			Condition condition = lock.newCondition();
			condition.await(15000, TimeUnit.MILLISECONDS);
			System.out.println("xxx");
		} catch (InterruptedException e) {

			e.printStackTrace();
		}finally {
			lock.unlock();
		}
	}
}
