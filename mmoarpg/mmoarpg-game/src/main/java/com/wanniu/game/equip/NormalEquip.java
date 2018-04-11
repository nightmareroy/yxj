package com.wanniu.game.equip;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.wanniu.core.logfs.Out;
import com.wanniu.core.util.StringUtil;
import com.wanniu.game.common.CommonUtil;
import com.wanniu.game.common.Const;
import com.wanniu.game.common.Utils;
import com.wanniu.game.data.GameData;
import com.wanniu.game.data.base.DEquipBase;
import com.wanniu.game.data.base.DItemEquipBase;
import com.wanniu.game.data.base.FourProp;
import com.wanniu.game.data.ext.AffixExt;
import com.wanniu.game.data.ext.EnchantExt;
import com.wanniu.game.item.ItemConfig;
import com.wanniu.game.item.ItemUtil;
import com.wanniu.game.item.NormalItem;
import com.wanniu.game.item.data.AttsObj;
import com.wanniu.game.item.po.ItemSpeData;
import com.wanniu.game.item.po.PlayerItemPO;
import com.wanniu.game.player.AttributeUtil;
import com.wanniu.game.poes.PlayerBasePO;
import com.wanniu.game.poes.PlayerBasePO.EquipStrengthPos;

import pomelo.item.ItemOuterClass.EquipmentDetail;
import pomelo.item.ItemOuterClass.ItemDetail;

/**
 * 单个物品对象类
 * 
 * @author Yangzz
 *
 */
public class NormalEquip extends NormalItem {

	public DEquipBase prop;

	public ItemSpeData speData;

	public NormalEquip(PlayerItemPO itemDb, DItemEquipBase prop) {
		super(itemDb, prop);

		this.prop = (DEquipBase) prop;
		this.speData = itemDb.speData;

		this._init();
	}

	private void _init() {

	};

	public ItemSpeData getSpeData() {
		return this.speData;
	};

	/**
	 * 获取装备的品质
	 * 
	 * @returns {number}
	 */
	public int getQColor() {
		return this.prop.qcolor;
	};

	public int getEquipScore(PlayerBasePO basePO) {
		return getEquipScore(this._calInfluence(basePO));
	}

	public int getEquipScore(Map<String, Integer> attrs) {
		float score = 0;
		for (Map.Entry<String, Integer> entry : attrs.entrySet()) {
			score += AttributeUtil.getScoreRatioByKey(entry.getKey()) * Math.abs(entry.getValue()) ;
		}
		Out.debug(getClass(), " getEquipScore:", attrs.toString(), " = ", score);
		return (int) score;
	};

	/**
	 * 原 toJson4EquipDetailPush 使用这个函数代替
	 * 
	 * @returns {{}}
	 */
	public ItemDetail.Builder getItemDetail(PlayerBasePO basePO) {
		ItemDetail.Builder data = ItemDetail.newBuilder();
		data.setId(this.itemDb.id);
		data.setCode(this.itemDb.code);
		data.setBindType(this.getBind());
		data.setCanTrade(this.canTrade() ? 1 : 0);
		data.setCanAuction((this.canAuction() && this.getBind() != 1) ? 1 : 0);
		data.setCanDepotRole(this.canDepotRole() ? 1 : 0);
		data.setCanDepotGuild(this.canDepotGuild() ? 1 : 0);

		EquipmentDetail.Builder equipDetail = EquipmentDetail.newBuilder();
		equipDetail.setIsIdentfied(0);
		equipDetail.setScore(getEquipScore(this._calInfluence(basePO)));
		// equipDetail.setBaseScore(getEquipScore(this._calInfluence(basePO)));
		equipDetail.setBaseScore(equipDetail.getScore());// 一样的代码为什么要调2次

		// 获取min，max用的prop
		DEquipBase attsProp = ItemConfig.getInstance().getEquipProp(this.prop.code);
		if (StringUtil.isNotEmpty(this.prop.baseCode)) {
			attsProp = ItemConfig.getInstance().getEquipProp(this.prop.baseCode);
		}
		int remakeScore = 0;
		int tempremakeScore = 0;
		int seniortempremakeScore = 0;
		List<AttsObj> list_ext = new ArrayList<>();
		if (this.speData.extAtts == null) {
			if (this.prop.fixedAtts != null) {
				for (String key : this.prop.fixedAtts.keySet()) {
					int value = this.prop.fixedAtts.get(key);
					list_ext.add(new AttsObj(key, value, 0, value, value));// TODO XXX FIXME 这里重铸范围 最大和最小一样的,
					remakeScore += CommonUtil.calOneAttributeFightScroreByStr(key, value); // 固定属性无法对应词缀的最大最小值
				}
			}
		} else {
			for (RepeatKeyMap.Pair<Integer, Integer> rp : this.speData.extAtts.entrySet()) {
				AffixExt affix = GameData.Affixs.get(rp.k);
				if(affix == null) {
					Out.warn("key="+rp.k + " val=" + rp.v);
					continue;
				}
				FourProp pair = affix.props.get(this.getQColor());
				if(pair==null) {
					continue;
				}
				list_ext.add(new AttsObj(pair.prop, rp.v, pair.par, ItemUtil.calcRebuildPropMin(pair.min), pair.max));
				remakeScore += CommonUtil.calOneAttributeFightScroreById(rp.k, rp.v, this.prop.qcolor);
			}

		}
		equipDetail.addAllRandomAtts(EquipUtil.getAttsAttributeBase(list_ext));

		List<AttsObj> list_base = new ArrayList<>();
		if (this.speData.baseAtts != null) {
			for (String key : this.speData.baseAtts.keySet()) {
				FourProp pair = attsProp.baseAtts.get(key);
				if(pair==null) {
					continue;
				}
				list_base.add(new AttsObj(key, this.speData.baseAtts.get(key), pair.par, pair.min, pair.max));
			}
		}
		equipDetail.addAllBaseAtts(EquipUtil.getAttsAttributeBase(list_base));

		List<AttsObj> star_base = new ArrayList<>();
		if (this.speData.extAttsAdd != null) {
			int add = 0;
			for (String key : this.speData.extAttsAdd.keySet()) {
				Integer vl = this.speData.extAttsAdd.get(key);
				int ivl = vl == null ? 0 : vl;
				star_base.add(new AttsObj(key, ivl, 0, ivl, ivl));
				add = CommonUtil.calOneAttributeFightScroreByStr(key, ivl);
				remakeScore += add;
			}
			if(this.speData.tempExtAtts_senior != null) {//高级重铸是把左边的属性种类拷过来的,直接拿取左边的就行
				seniortempremakeScore+=add;
			}
		}
		equipDetail.addAllStarAttr(EquipUtil.getAttsAttributeBase(star_base));
		
		

		List<AttsObj> temp_star_base = new ArrayList<>();
		if (this.speData.tempExtAttsAdd != null) {
			for (String key : this.speData.tempExtAttsAdd.keySet()) {
				Integer vl = this.speData.tempExtAttsAdd.get(key);
				int ivl = vl == null ? 0 : vl;
				temp_star_base.add(new AttsObj(key, ivl, 0, ivl, ivl));
				tempremakeScore += CommonUtil.calOneAttributeFightScroreByStr(key, ivl);
			}
		}
		equipDetail.addAllTempstarAttr(EquipUtil.getAttsAttributeBase(temp_star_base));
		if (this.speData.legendAtts != null) {
			for (int affixId : this.speData.legendAtts.keySet()) {
				AffixExt affix = GameData.Affixs.get(affixId);
				if(affix == null) {
					continue;
				}
				FourProp pair = affix.props.get(this.getQColor());
				if(pair==null) {
					continue;
				}
				equipDetail.addUniqueAtts(EquipUtil.getAttributeBase(new AttsObj(pair.prop, this.speData.legendAtts.get(affixId), pair.par, pair.min, pair.max)));
			}
		}

		if (this.speData.tempBaseAtts != null) {
			List<AttsObj> list_temp_base = new ArrayList<>();
			for (String key : this.speData.tempBaseAtts.keySet()) {
				FourProp pair = attsProp.baseAtts.get(key);
				if(pair==null) {
					continue;
				}
				list_temp_base.add(new AttsObj(key, this.speData.tempBaseAtts.get(key), pair.par, pair.min, pair.max));
			}
			equipDetail.addAllTempBaseAtts(EquipUtil.getAttsAttributeBase(list_temp_base));
		}

		if (this.speData.tempExtAtts != null) {
			List<AttsObj> list__temp_ext = new ArrayList<>();
			for (RepeatKeyMap.Pair<Integer, Integer> rp : this.speData.tempExtAtts.entrySet()) {
				AffixExt affix = GameData.Affixs.get(rp.k);
				if(affix==null) {
					Out.error("tempExtAtts null exception, id=" + this.getId() + " code=" +this.itemDb.code + " name=" + this.getName() + " k="+rp.k+" v="+rp.v);
					continue;
				}
				FourProp pair = affix.props.get(this.getQColor());
				if(pair==null) {
					continue;
				}
				list__temp_ext.add(new AttsObj(pair.prop, rp.v, pair.par, ItemUtil.calcRebuildPropMin(pair.min), pair.max));
				tempremakeScore += CommonUtil.calOneAttributeFightScroreByStr(pair.prop, rp.v);
			}
			equipDetail.addAllTempExtAtts(EquipUtil.getAttsAttributeBase(list__temp_ext));
		}

		if (this.speData.tempExtAtts_senior != null) {
			List<AttsObj> list__temp_ext = new ArrayList<>();
			for (RepeatKeyMap.Pair<Integer, Integer> rp : this.speData.tempExtAtts_senior.entrySet()) {
				AffixExt affix = GameData.Affixs.get(rp.k);
				if(affix == null) {
					Out.warn("key="+rp.k + " val=" + rp.v);
					continue;
				}
				FourProp pair = affix.props.get(this.getQColor());
				if(pair==null) {
					continue;
				}
				list__temp_ext.add(new AttsObj(pair.prop, rp.v, pair.par, ItemUtil.calcRebuildPropMin(pair.min), pair.max));
				seniortempremakeScore += CommonUtil.calOneAttributeFightScroreByStr(pair.prop, rp.v);
			}

			equipDetail.addAllTempExtAttsSenior(EquipUtil.getAttsAttributeBase(list__temp_ext));
		}

		if (this.speData.tempUniqueAtts != null) {
			for (int affixId : this.speData.tempUniqueAtts.keySet()) {
				AffixExt affix = GameData.Affixs.get(affixId);
				if(affix == null) {
					continue;
				}
				FourProp pair = affix.props.get(this.getQColor());
				if(pair==null) {
					continue;
				}
				equipDetail.addTempUniqueAtts(EquipUtil.getAttributeBase(new AttsObj(pair.prop, this.speData.tempUniqueAtts.get(affixId), pair.par, pair.min, pair.max)));
			}
		}
		equipDetail.setRemakeScore(remakeScore);
		equipDetail.setTempRemakeScore(tempremakeScore);
		equipDetail.setSeniorTempRemakeScore(seniortempremakeScore);
		data.setEquipDetail(equipDetail);

		return data;
	};

	public Map<String, Integer> _calInfluence(PlayerBasePO basePO) {
		EquipStrengthPos strengthInfo = basePO.strengthPos.get(getPosition());

		Map<String, Integer> data = new HashMap<>();
		// 基础属性 修改为随机属性+ (扩展属性)随机词条

		if (strengthInfo == null || (strengthInfo.enSection == 0 && strengthInfo.enLevel == 0)) {
			Utils.deepCopy(data, speData.baseAtts);
		} else {
			EnchantExt enchantExt = GameData.Enchants.get(strengthInfo.enSection * 100 + strengthInfo.enLevel);
			if (enchantExt != null) {
				for (String key : this.speData.baseAtts.keySet()) {
					int value = this.speData.baseAtts.get(key);
					value = value * (10000 + enchantExt.propPer) / 10000;
					if (data.containsKey(key)) {
						data.put(key, data.get(key) + value);
					} else {
						data.put(key, value);
					}
				}
			}
		}

		if (speData.extAtts == null) {
			Utils.deepCopy(data, this.prop.fixedAtts);
		} else {
			Utils.deepCopyAffix(data, speData.extAtts, this.getQColor());
		}
		
		if (speData.extAttsAdd != null) {// 重铸带来的额外属性
			Utils.deepCopy(data, speData.extAttsAdd);
		}
		// 传奇属性
		Utils.deepCopyAffix(data, speData.legendAtts, this.getQColor());

		return data;
	};

	public int getPosition() {
		return Const.ItemSecondType.getV(this.prop.type);
	};

}
