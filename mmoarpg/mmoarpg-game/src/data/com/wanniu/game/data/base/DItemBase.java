/**
 * XSanGoGreen ©2016 美峰数码 http://www.morefuntek.com
 */
package com.wanniu.game.data.base;

import java.util.HashMap;
import java.util.Map;

import com.wanniu.core.logfs.Out;
import com.wanniu.game.common.Const;
import com.wanniu.game.data.ext.ItemIdConfigExt;
import com.wanniu.game.data.ext.ItemTypeConfigExt;
import com.wanniu.game.item.ItemConfig;

/**
 * 物品模板抽象类
 * 
 * @author Yangzz
 *
 */
public abstract class DItemBase extends DItemEquipBase {

	/** 帮助说明 */
	public String tips;
	/** 获得说明 */
	public String ways;
	/** 使用间隔 */
	public int useCD;
	/** 属性1 */
	public String prop;
	/** 参数1 */
	public int par;
	/** 最小值1 */
	public int min;
	/** 最大值1 */
	public int max;
	/** 小图标文件 */
	public String smallIcon;

	/** 合成公式 */
	public int destID;
	/** 可使用 */
	public int isApply;
	/** 可立即使用 */
	public int isApplyNow;
	/** 使用说明 */
	public String applyTips;

	/** 途径功能ID */
	public String waysID;

	public int itemOrder;
	public String orderRule;

	public int orderID;
	public int minAffixCount;
	public int maxAffixCount;
	
	public Map<Integer, String> chestTC;

	// 客户端用的显示红点，服务器只是添加一个引用，这样不会输出警告日志.
	public int redPoint;
	
	public void initProperty() {
		ItemIdConfigExt itemIdConfigProp = ItemConfig.getInstance().getIdConfig(this.type);
		if (itemIdConfigProp != null) {
			this.itemTypeId = itemIdConfigProp.typeID;
			this.itemOrder = itemIdConfigProp.order;
			this.orderRule = itemIdConfigProp.orderRule;
		} else {
			Out.error(this.getClass().getName(), " item no prop in itemIdConfig :" , this.type , ", name:" , this.name);
		}

		ItemTypeConfigExt itemTypeConfigProp = ItemConfig.getInstance().getTypeConfig(this.type);
		if (itemTypeConfigProp != null) {
			int itemType = Const.ItemType.getV(itemTypeConfigProp.parentCode);
			if (itemType == 0) {
				Out.error(this.getClass().getName(),
						" item no element in itemType:" , itemTypeConfigProp.parentCode , ", name:" , this.name);
			}
			this.itemType = itemType;
			this.orderID = itemTypeConfigProp.order;
		} else {
			Out.error(this.getClass().getName(),
					" item no prop in itemTypeConfig :" , this.type , ", name:" , this.name);
		}

		int itemSecondType = Const.ItemSecondType.getV(this.type);
		if (itemSecondType == 0) {
			Out.error(this.getClass().getName(), " item no itemSecondType : " , this.type , ", name:" , this.name);
		}
		this.itemSecondType = itemSecondType;
		
		if (itemType == Const.ItemType.Chest.getValue()) {
			chestTC = new HashMap<>();
			if (prop.indexOf(":") != -1) {
				String[] tcs = prop.split(";");
				for (String str : tcs) {
					String[] proTC = str.split(":");
					if (proTC.length == 0) {
						continue;
					}
					int pro = Integer.parseInt(proTC[0]);
					String tc = proTC[1];
					if (pro == 0) {
						chestTC.put(1, tc);
						chestTC.put(2, tc);
						chestTC.put(3, tc);
						chestTC.put(4, tc);
						chestTC.put(5, tc);
					} else {
						chestTC.put(pro, tc);
					}
				}
			} else {
				chestTC.put(1, prop);
				chestTC.put(2, prop);
				chestTC.put(3, prop);
				chestTC.put(4, prop);
				chestTC.put(5, prop);
			}
		}
	}

	/** 主键 */
	public String getKey() {
		return this.code;
	}
}
