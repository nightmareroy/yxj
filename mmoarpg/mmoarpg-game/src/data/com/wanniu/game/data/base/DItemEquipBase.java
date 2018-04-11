package com.wanniu.game.data.base;

/**
 * 物品/装备 父类
 * 
 * @author Yangzz
 *
 */
public class DItemEquipBase {

	/** 名称 */
	public String name;
	/** 类型 */
	public String type;
	/** 代码 */
	public String code;

	/** 描述 */
	public String desc;

	/** 基础价格 */
	public int price;
	public int salePrice;
	/** 等级需求 */
	public int levelReq;
	/** 成色 */
	public int qcolor;
	/** 图标文件 */
	public String icon;
	/** 重叠数量 */
	public int groupCount;
	/** 购买上限 */
	public int purchaseCount;
	/** 职业 */
	public String pro;

	/** 不可销毁 */
	public int noDestory;
	/** 不可出售 */
	public int noSell;
	/** 不可交易 */
	public int noTrade;
	/** 不可寄卖 */
	public int noAuction;
	/** 不可存入个人仓库 */
	public int noDepotRole;
	/** 不可存入账号仓库 */
	public int noDepotAcc;
	/** 不可存入公会仓库 */
	public int noDepotGuild;
	/** 模型ID */
	public String showId;
	/** 造型ID */
	public String avatarId;
	/** 星级 */
	public int star;
	/** 绑定规则 */
	public int bindType;

	/** initProperty */
	public int itemType;
	public int itemSecondType;
	public int itemTypeId;
	public int Pro;

	public DItemEquipBase copy() {
		DItemEquipBase base = new DItemEquipBase();
		base.name = name;
		base.type = type;
		base.code = code;
		base.desc = desc;
		base.price = price;
		base.salePrice = salePrice;
		base.levelReq = levelReq;
		base.qcolor = qcolor;
		base.icon = icon;
		base.groupCount = groupCount;
		base.purchaseCount = purchaseCount;
		base.pro = pro;
		base.noDestory = noDestory;
		base.noSell = noSell;
		base.noTrade = noTrade;
		base.noAuction = noAuction;
		base.noDepotRole = noDepotRole;
		base.noDepotAcc = noDepotAcc;		
		base.noDepotGuild=noDepotGuild;		
		base.showId=showId;		
		base.avatarId=avatarId;		
		base.star=star;		
		base.bindType=bindType;
		
		base.itemType=itemType;
		base.itemSecondType=itemSecondType;
		base.itemTypeId=itemTypeId;
		base.Pro=Pro;

		return base;
	}

	public void beforeProperty() {
	}

}
