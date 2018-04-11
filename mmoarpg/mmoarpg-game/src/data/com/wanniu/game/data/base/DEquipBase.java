/**
 * XSanGoGreen ©2016 美峰数码 http://www.morefuntek.com
 */
package com.wanniu.game.data.base;

import java.util.HashMap;
import java.util.Map;

import com.wanniu.core.logfs.Out;
import com.wanniu.core.util.ClassUtil;
import com.wanniu.core.util.StringUtil;
import com.wanniu.game.common.Const;
import com.wanniu.game.data.ItemIdConfigCO;
import com.wanniu.game.data.ItemTypeConfigCO;
import com.wanniu.game.item.ItemConfig;
import com.wanniu.game.player.AttributeUtil;

/**
 * 物品模板抽象类
 * 
 * @author Yangzz
 *
 */
public abstract class DEquipBase extends DItemEquipBase {
	/** 是否双手武器 */
	public int isBothHand;
	/** 占用护符空间 */
	public int space;
	/** 是否出现 */
	public int isValid;
	/** 是否已经鉴定过 */
	public int isIdentfied;
	/** 稀有度 */
	public int rare;
	/** TC等级 */
	public int tcLevel;
	/** 品质等级 */
	public int qLevel;
	/** 进阶需求 */
	public int upReq;
	/** 熔炼等级 */
	public int meltLevel;
	/** 装备孔数 */
	public int socksNum;
	/** 是否可升级 */
	public int canLvUp;
	/** 是否可进阶 */
	public int canUpgrade;
	/** 随机属性来源 */
	public String affixSheet;
	/** 随机属性数量 */
	public String affixCount;
	/** 基础属性1 */
	public String prop1;
	/** 参数1 */
	public int par1;
	/** 最小值1 */
	public int min1;
	/** 最大值1 */
	public int max1;
	/** 基础属性2 */
	public String prop2;
	/** 参数2 */
	public int par2;
	/** 最小值2 */
	public int min2;
	/** 最大值2 */
	public int max2;
	/** 基础属性3 */
	public String prop3;
	/** 参数3 */
	public int par3;
	/** 最小值3 */
	public int min3;
	/** 最大值3 */
	public int max3;

	/** 不可熔炼 */
	public int noMelt;
	/** 工会存入资金 */
	public int wareHouseValue;
	/** 工会取出资金 */
	public int wareHouseCost;
	
	/**基类装备代码:::固定属性装备独有的*****************************************************************************/
	public String baseCode;
// 固定属性装备取 固定值
//	public String prop1;
//	public int par1;
//	public int min1;
//	public int max1;
//
//	public String prop2;
//	public int par2;
//	public int min2;
//	public int max2;
//
//	public String prop3;
//	public int par3;
//	public int min3;
//	public int max3;

	public int rPropCount;
	
	/**
	 * 固定装备基础属性
	 */
	public Map<String, Integer> fixedAtts; 

	public String rProp1;
	public int rPar1;
	public int rMin1;
	public int rMax1;

	public String rProp2;
	public int rPar2;
	public int rMin2;
	public int rMax2;

	public String rProp3;
	public int rPar3;
	public int rMin3;
	public int rMax3;

	public String rProp4;
	public int rPar4;
	public int rMin4;
	public int rMax4;

	public String rProp5;
	public int rPar5;
	public int rMin5;
	public int rMax5;

	public String rProp6;
	public int rPar6;
	public int rMin6;
	public int rMax6;
	/**固定属性End******************************************************************************************************/

	public int uPar1;
	public String uProp1;
	public int uMin1;
	public int uMax1;
	public String uProp2;
	public int uPar2;
	public int uMin2;
	public int uMax2;
	public int suitID;
	public int starLevel;

	public int SocksNum;

	public int itemOrder;
	public String orderRule;
	public int orderID;
	public Map<String, FourProp> baseAtts;
	public int minAffixCount;
	public int maxAffixCount;
	public String waysID;
	/**
	 * csz:
		独有属性，用来区别本装备和其他装备的不同，一般都是一些比较好的属性
		独有属性最多有2条，这些属性不能被洗练、精炼，但是可以通过重铸来重新生成其属性值
		为空就表示没有，无需处理
	 */
	public Map<String, FourProp> uniqueAtts;

	public void initProperty() {
		this.initProp();
	}
	public void initProp() {
		if(StringUtil.isEmpty(this.type)) {
			Out.error(this.code , " type is nil");
			return;
		}
		ItemIdConfigCO itemIdConfigProp = ItemConfig.getInstance().getIdConfig(this.type);
		if (itemIdConfigProp != null) {
			this.itemTypeId = itemIdConfigProp.typeID;
			this.itemOrder = itemIdConfigProp.order;
			this.orderRule = itemIdConfigProp.orderRule;
		} else {
			Out.error(this.getClass().getName(),
					" equip no prop in itemIdConfig :" , this.type , ", name:" , this.name);
		}

		ItemTypeConfigCO itemTypeConfigProp = ItemConfig.getInstance().getTypeConfig(this.type);
		if (itemTypeConfigProp != null) {
			int itemType = Const.ItemType.getV(itemTypeConfigProp.parentCode);
			if (itemType == 0) {
				Out.error(this.getClass().getName(),
						" equip no element in itemType:" , itemTypeConfigProp.parentCode , ", name:" , this.name);
			}
			this.itemType = itemType;
			this.orderID = itemTypeConfigProp.order;
		} else {
			Out.error(this.getClass().getName(),
					" equip no prop in itemTypeConfig :" , this.type , ", name:" , this.name);
		}
		int itemSecondType = Const.ItemSecondType.getV(this.type);
		if (itemSecondType == 0) {
			Out.error(this.getClass().getName(), " equip no itemSecondType : " , this.type , ", name:" , this.name);
		}
		this.itemSecondType = itemSecondType;

		this.groupCount = 1;

		this.Pro = Const.PlayerPro.Value(this.pro);

		// 基础属性
		this.baseAtts = new HashMap<>();
		for (int i = 1; i <= 3; i++) {
			String attrName = "prop" + i;
			String attrPar = "par" + i;
			String attrMin = "min" + i;
			String attrMax=  "max" + i;
			String key = null;

			try {
				if (ClassUtil.getProperty(this, attrName) == null || StringUtil.isEmpty((String) ClassUtil.getProperty(this, attrName))) {
					Out.debug(this.getClass().getName(), " EquipProp attName is space");
					continue;
				}
				key = AttributeUtil.getKeyByName((String) ClassUtil.getProperty(this, attrName));
				if (StringUtil.isNotEmpty(key)) {
					FourProp minMax = new FourProp(key, 
							(int) ClassUtil.getProperty(this, attrPar), 
							(int) ClassUtil.getProperty(this, attrMin), 
							(int) ClassUtil.getProperty(this, attrMax));
					this.baseAtts.put(key, minMax);
				} else {
					Out.error(this.getClass().getName(), " EquipProp attrName not exist " , attrName);
				}
			} catch (Exception e) {
				Out.error(e);
			}
		}
		
		// 独有属性
		this.uniqueAtts = new HashMap<>();
		for (int i = 1; i <= 2; i++) {
			String attrName = "uProp" + i;
			String par = "uPar" + i;
			String minValue = "uMin" + i;
			String maxValue = "uMax" + i;

			try {
				Object attrNameObj = ClassUtil.getProperty(this, attrName);
				if (attrNameObj == null || StringUtil.isEmpty((String) attrNameObj)) {
					continue;
				}
				this.uniqueAtts.put(AttributeUtil.getKeyByName((String) attrNameObj), 
						new FourProp(AttributeUtil.getKeyByName((String) attrNameObj), 
								(int) ClassUtil.getProperty(this, par), 
								(int) ClassUtil.getProperty(this, minValue),
								(int) ClassUtil.getProperty(this, maxValue))
						);
			} catch (Exception e) {
				Out.error(e);
			}
		}

		if(StringUtil.isNotEmpty(this.affixCount)) {
			String[] countData = this.affixCount.substring(1, this.affixCount.length() - 1).split(",");
			this.minAffixCount = Integer.parseInt(countData[0]);
			this.maxAffixCount = Integer.parseInt(countData[1]);
		}
	}
	
}
