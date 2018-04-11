/**
 * XSanGoGreen ©2016 美峰数码 http://www.morefuntek.com
 */
package com.wanniu.game.item;

import java.util.Collection;
import java.util.UUID;

import com.wanniu.core.logfs.Out;
import com.wanniu.core.util.DateUtil;
import com.wanniu.game.GWorld;
import com.wanniu.game.blood.BloodManager;
import com.wanniu.game.common.Const;
import com.wanniu.game.common.Utils;
import com.wanniu.game.data.base.DItemBase;
import com.wanniu.game.data.base.DItemEquipBase;
import com.wanniu.game.item.data.ItemToBtlServerData;
import com.wanniu.game.item.po.PlayerItemPO;
import com.wanniu.game.player.GlobalConfig;
import com.wanniu.game.poes.ConsignmentItemsPO;
import com.wanniu.game.poes.PlayerBasePO;

import Xmds.FinishPickItem;
import pomelo.area.BattleHandler.ItemDrop;
import pomelo.item.ItemOuterClass.Item;
import pomelo.item.ItemOuterClass.ItemDetail;
import pomelo.item.ItemOuterClass.MiniItem;

/**
 * 单个普通物品对象
 * 
 * @author Yangzz
 * 
 */
public class NormalItem {

	/** 数据库对象 */
	public PlayerItemPO itemDb;
	/** 模版对象 */
	public DItemEquipBase prop;

	/**
	 * 构造函数
	 *
	 * @param db
	 * @param template
	 */
	public NormalItem(PlayerItemPO itemDb, DItemEquipBase prop) {
		this.itemDb = itemDb;
		this.prop = prop;
	}

	public String itemCode() {
		return this.prop.code;
	}

	public Item.Builder getView() {
		Item.Builder item = Item.newBuilder();
		item.setId(itemDb.id);
		// item.setCode(itemDb.code);
		// item.setItemType(Const.ItemType.Item)
		// item.setItemSecondType(value)
		// item.setQColor(template.)
		return item;
	}

	public final DItemEquipBase getTemplate() {
		return this.prop;
	}

	public String getId() {
		return this.itemDb.id;
	};

	public void old() {
		itemDb.isNew = 0;
		itemDb.gotTime = DateUtil.getZeroDate();
	};

	public String getName() {
		return this.prop.name;
	};

	public int getNum() {
		if (this.isVirtual()) {
			return this.getWorth();
		}
		return itemDb.groupCount;
	}

	public void setNum(int num) {
		synchronized (lock) {
			itemDb.groupCount = num;
		}
	}

	public boolean isFullGroup() {
		if (this.itemDb.groupCount >= this.prop.groupCount) {
			return true;
		}
		return false;
	};

	private Object lock = new Object();

	public boolean addGroupNum(int num) {
		synchronized (lock) {
			if (itemDb.groupCount + num <= this.prop.groupCount) {
				this.itemDb.groupCount += num;
				return true;
			}
			return false;
		}
	};

	public boolean addGroup(NormalItem item) {
		if (this.isFullGroup()) {
			return false;
		}
		int result = item.itemDb.groupCount + this.itemDb.groupCount;
		if (result > this.prop.groupCount) {
			return false;
		}
		synchronized (lock) {
			this.itemDb.groupCount = result;
		}
		return true;
	};

	public void setGroup(int num) {
		if (num > this.prop.groupCount) {
			num = this.prop.groupCount;
		}
		synchronized (lock) {
			this.itemDb.groupCount = num;
		}
	};

	public boolean deleteGroup(int num) {
		if (num > this.itemDb.groupCount) {
			return false;
		}
		synchronized (lock) {
			this.itemDb.groupCount = this.itemDb.groupCount - num;
		}
		return true;
	};

	public int leftGroup() {
		return this.prop.groupCount - this.itemDb.groupCount;
	};

	public boolean isInvalid() {
		if (this.itemDb.groupCount <= 0) {
			return true;
		}
		return false;
	};

	public boolean setCD() {
		DItemBase itemProp = (DItemBase) this.prop;
		if (itemProp.useCD > 0) {
			this.itemDb.cdTime = System.currentTimeMillis() + itemProp.useCD;
			return true;
		}
		return false;
	};

	public long getCD() {
		return itemDb.cdTime;
	};

	/**
	 * 获取绑定状态
	 * 
	 * @returns {*}
	 */
	public int getBind() {
		if(this.itemDb.isBindFilter==-1) {
			return this.itemDb.isBind;
		}
		else {
			return this.itemDb.isBindFilter;
		}
		
	};

	/**
	 * 设置绑定状态
	 * 
	 * @param bind
	 */
	public void setBind(int bind) {
		this.itemDb.isBind = bind;
		// nodejs 里面 改变了 模板值，这里做法应该是 模板值不变，结合bind使用
		// if(bind == 1){
		// this.prop.noTrade() = 1;
		// this.prop.noAuction = 1;
		// this.prop.noDepotAcc = 1;
		// this.prop.noDepotGuild = 1;
		// }else if(bind == 0 || bind == 2){
		// this.prop.noTrade = 0;
		// this.prop.noAuction = 0;
		// this.prop.noDepotAcc = 0;
		// this.prop.noDepotGuild = 0;
		// }
	}
	
	/**
	 * 设置用于后置过滤的绑定
	 * 
	 * @param bind
	 */
	public void setBindFilter(int bind) {
		this.itemDb.isBindFilter=bind;
	}

	public boolean isBinding() {
		if(this.itemDb.isBindFilter==-1) {
			if (this.itemDb.isBind == 1) {
				return true;
			} else {
				return false;
			}
		}
		else {
			return this.itemDb.isBindFilter==1;
		}
	};

	/**
	 * 设置用于后置过滤的是否可寄卖
	 */
	public void setNoAuction(int noAuction) {
		this.itemDb.noAuctionFilter = noAuction;
	}

	/**
	 * 能否交易 1 可以
	 * 
	 * @returns {number}
	 */
	public boolean canTrade() {
		return this.prop.noTrade == 0 && this.itemDb.isBind != 1;
	};

	public boolean canAuction() {
		if (this.itemDb.isBindFilter == -1) {
			return this.prop.noAuction == 0 && this.itemDb.noAuction != 1;
		} else {
			return this.itemDb.noAuctionFilter != 1;
		}
	};

	public boolean canDepotRole() {
		return this.prop.noDepotRole == 0;
	};

	public boolean canDepotAcc() {
		return this.prop.noDepotAcc == 0 && this.itemDb.isBind != 1;
	};

	public boolean canDepotGuild() {
		return this.prop.noDepotGuild == 0 && this.itemDb.isBind != 1;
	};

	/**
	 * 判断是否是虚拟道具
	 */
	public boolean isVirtual() {
		return ItemConfig.getInstance().getSecondType(this.prop.type) == Const.ItemSecondType.virtual.getValue();
	};

	/**
	 * 是否不进背包的道具
	 */
	public boolean isVirtQuest() {
		return false;//ItemConfig.getInstance().getSecondType(this.prop.type) == Const.ItemSecondType.virtQuest.getValue();
	};

	public int getLevel() {
		return this.prop.levelReq;
	};

	// FIXME 返回 0 ？？？？
	public int getUpLevel() {
		return 0; // this.prop.UpReq;
	}

	public int getQLevel() {
		return this.prop.qcolor;
	};

	public boolean isEquip() {
		return ItemUtil.isEquipByItemType(this.prop.itemType);
	};
	
	public boolean isBlood() {
		if(this.prop.itemSecondType!=Const.ItemSecondType.virtQuest.getValue())
		{
			return false;
		}
		if(BloodManager.itemFilter.contains(this.itemCode()))
		{
			return false;
		}
		return true;
	};

	/**
	 * 能否出售
	 * 
	 * @returns {boolean}
	 */
	public boolean canSell() {
		return this.prop.noSell == 0;
	};

	public int price() {
		return this.prop.price;
	};

	public int getScore() {
		return 0;
	};

	public int getPrice() {
		return prop.price;
	};

	public boolean canUse() {
		if (((DItemBase) this.prop).isApply == 1) {
			return true;
		}
		return false;
	};

	public int getWorth() {
		return this.itemDb.speData.worth;
	}

	/**
	 * 道具是否在cd时间内，是 true 否 false
	 * 
	 * @returns {boolean}
	 */
	public boolean isCD() {
		if (this.itemDb.cdTime < GWorld.APP_TIME) {
			return false;
		} else {
			return true;
		}
	};

	public ItemDetail.Builder getItemDetail(PlayerBasePO basePO) {
		ItemDetail.Builder data = ItemDetail.newBuilder();
		data.setId(getId());
		data.setCode(itemDb.code);
		data.setBindType(this.getBind());
		data.setCanTrade(this.canTrade() ? 1 : 0);
		data.setCanAuction((this.canAuction() && this.getBind() != 1) ? 1 : 0);
		data.setCanDepotRole(this.canDepotRole() ? 1 : 0);
		data.setCanDepotGuild(canDepotGuild() ? 1 : 0);
		return data;
	};

	public PlayerItemPO cloneItemDB() {
		return Utils.clone(itemDb);
	}

	public Item.Builder toJSON4GridPayload() {
		Item.Builder data = Item.newBuilder();
		data.setId(getId());
		data.setCode(itemDb.code);
		data.setItemType(ItemConfig.getInstance().getTypeConfig(prop.type).iD);
		data.setItemSecondType(ItemConfig.getInstance().getIdConfig(prop.type).typeID);
		data.setGroupCount(itemDb.groupCount);
		data.setMaxGroupCount(prop.groupCount);
		data.setIsNew(itemDb.isNew);
		data.setIcon(prop.icon);
		data.setQColor(prop.qcolor);
		data.setBindType(itemDb.isBind);

		// 目前只有勋章有星级
		data.setStar(0);
		data.setCdTime(String.valueOf(this.getCD()));
		return data;
	};

	public final ItemToBtlServerData toJSON4BatterServer(Collection<String> belongPlayerIds, Const.TEAM_DISTRIBUTE_TYPE distributeType, boolean isPlayerDead) {
		ItemToBtlServerData data = new ItemToBtlServerData();
		data.id = this.getId();
		data.name = this.prop.name;
		data.showId = this.prop.showId;
		data.groupCount = this.itemDb.groupCount;
		data.qColor = this.prop.qcolor;
		data.itemTypeId = this.prop.itemTypeId;
		data.IconName = this.prop.icon;
		data.freezeTime = GlobalConfig.itemdrop_lock_freezeTime;
		data.protectTime = GlobalConfig.itemdrop_lock_protectTime;
		data.lifeTime = GlobalConfig.itemdrop_lock_lifeTime;

		data.PlayerUUID = belongPlayerIds;
		data.distributeType = distributeType.value;
		if (!isPlayerDead) {
			if (!this.isBinding()) {
				if (GlobalConfig.getBindByQcolor(this.prop.qcolor) > 0) {
					this.setBind(1);
				}
			}
		}
		return data;
	}

	public final ItemDrop.Builder toProto4Client() {
		ItemDrop.Builder data = ItemDrop.newBuilder();
		data.setId(this.getId());
		data.setName(this.prop.name);
		data.setShowId(this.prop.showId);
		data.setGroupCount(this.itemDb.groupCount);
		data.setQColor(this.prop.qcolor);
		data.setItemTypeId(this.prop.itemTypeId);
		data.setIconName(this.prop.icon);
		data.setFreezeTime(GlobalConfig.itemdrop_lock_freezeTime);
		data.setProtectTime(GlobalConfig.itemdrop_lock_protectTime);
		data.setLifeTime(GlobalConfig.itemdrop_lock_lifeTime);

		if (!this.isBinding() && GlobalConfig.getBindByQcolor(this.prop.qcolor) > 0) {
			this.setBind(1);
		}
		return data;
	}

	public FinishPickItem toJSON4PickItemBatterServer() {
		FinishPickItem data = new FinishPickItem();
		data.itemIcon = this.prop.icon;
		data.quality = this.prop.qcolor;
		data.num = this.itemDb.groupCount;
		return data;
	};

	/**
	 * 寄卖行所有的数据
	 */
	public final ConsignmentItemsPO toJSON4ConsignmentLine(int salePrice, String playerName, int pro, String playerId, int effectiveTime, int num, int lateMinutes) {
		ConsignmentItemsPO data = new ConsignmentItemsPO();
		if (this.prop.groupCount != 1) {
			data.id = UUID.randomUUID().toString();
			Out.debug("toJSON4ConsignmentLine generate id  source id:", this.itemDb.id, "targetId:", data.id);
		} else {
			data.id = this.itemDb.id;
		}

		// data.logicServerId = logicServerId;
		data.itemType = this.prop.itemType;
		data.itemSecondType = this.prop.itemSecondType;
		data.pro = this.prop.Pro;
		data.level = this.getLevel() + this.getUpLevel() * 1000;
		data.consignmentPrice = salePrice;
		data.consignmentTime = System.currentTimeMillis() + effectiveTime + lateMinutes * Const.Time.Minute.getValue();// DateUtil.getAfterHour(hours);
		data.lateMinutes = lateMinutes;
		data.consignmentPlayerName = playerName;
		data.consignmentPlayerPro = pro;
		data.consignmentPlayerId = playerId;
		data.groupCount = num;
		// ItemDetail.Builder detail = this.getItemDetail();
		// detail.setId(data.id); //客户端要用这个id,所以要统一
		// data.detail = detail.build(); TODO
		data.db = this.cloneItemDB();
		data.db.id = data.id;
		data.db.groupCount = num;

		return data;
	};

	public MiniItem toJSON4MiniItem() {
		MiniItem.Builder data = MiniItem.newBuilder();
		data.setCode(this.prop.code);
		data.setGroupCount(this.itemDb.groupCount);
		data.setIcon(this.prop.icon);
		data.setQColor(this.prop.qcolor);
		data.setName(this.prop.name);
		data.setStar(this.prop.star);
		data.setBindType(this.getBind());
		return data.build();
	}

}