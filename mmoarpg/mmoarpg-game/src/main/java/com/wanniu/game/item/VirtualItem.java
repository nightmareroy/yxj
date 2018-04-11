package com.wanniu.game.item;

import com.wanniu.game.data.base.DItemEquipBase;
import com.wanniu.game.item.po.PlayerItemPO;

/**
 * 虚拟道具
 * 
 * @author Yangzz
 *
 */
public class VirtualItem extends NormalItem {

	public VirtualItem(PlayerItemPO itemDb, DItemEquipBase prop) {
		super(itemDb, prop);

	}

	public int getWorth() {
		return this.itemDb.speData.worth;
	};

	public void addWorth(int num) {
		this.itemDb.speData.worth += num;
	}

	public void setWorth(int num) {
		this.itemDb.speData.worth = num;
	};

	/**
	 * 怪物掉落二次运算 怪物掉落Tc生成的道具是，部分虚拟道具进行二次计算 怪物等级|怪物金币掉落增幅 怪物等级|经验万分比|玩家当前等级升级需要的经验
	 */
	public void dropResetWorth(int args0, int args1, int args2) {
		int oldWorth = this.itemDb.speData.worth;
		int newWorth = this.itemDb.speData.worth;
		if (this.itemDb.code.equals("gold")) {
			int monsterLevel = args0;// 怪物等级
			int goldPerMonLv = args1;// 怪物金币掉落增幅
			int playerNum = args2 > 1 ? args2 : 1;

			// 二次计算公式
			newWorth = (oldWorth + (monsterLevel - 1) * goldPerMonLv) / playerNum;
			// 取整方式
			// newWorth = (int) Math.ceil(newWorth);
		} else if (this.itemDb.code.equals("exp")) {
			int monsterLevel = args0; // 怪物等级
			int expRatio = args1; // 经验万分比
			int playerLevelUpExp = args2; // 玩家当前等级升级需要的经验
			// 二次计算公式
			newWorth = oldWorth + playerLevelUpExp * expRatio / 10000;
			// 取整方式
			// newWorth = (int) Math.ceil(newWorth);
		}
		this.setWorth(newWorth);
	};

	// public void toJSON4MiniItem (){
	// var data = {
	// code: this.prop.Code,
	// groupCount: this.getWorth(),
	// icon: this.prop.Icon,
	// qColor: this.prop.Qcolor,
	// name: this.prop.Name,
	// star : this.prop.Star || 0,
	// bindType : this.isBind
	// };
	// return data;
	// };
}
