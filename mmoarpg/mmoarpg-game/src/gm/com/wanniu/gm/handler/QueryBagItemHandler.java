package com.wanniu.gm.handler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.wanniu.core.logfs.Out;
import com.wanniu.game.data.GameData;
import com.wanniu.game.data.base.DEquipBase;
import com.wanniu.game.data.base.DItemBase;
import com.wanniu.game.data.base.DItemEquipBase;
import com.wanniu.game.data.base.FourProp;
import com.wanniu.game.data.ext.AffixExt;
import com.wanniu.game.equip.RepeatKeyMap;
import com.wanniu.game.item.ItemConfig;
import com.wanniu.game.item.ItemUtil;
import com.wanniu.game.item.NormalItem;
import com.wanniu.game.item.po.PlayerItemPO;
import com.wanniu.game.player.AttributeUtil;
import com.wanniu.game.player.PlayerDao;
import com.wanniu.game.player.PlayerUtil;
import com.wanniu.game.player.WNPlayer;
import com.wanniu.game.player.po.AllBlobPO;
import com.wanniu.game.poes.PlayerBasePO;
import com.wanniu.gm.GMEvent;
import com.wanniu.gm.GMJsonResponse;
import com.wanniu.gm.GMResponse;

import cn.qeng.common.gm.RpcOpcode;
import cn.qeng.common.gm.vo.GmItemVO;

/**
 * 查询玩家物品信息
 * 
 * @author lxm
 *
 */
@GMEvent
public class QueryBagItemHandler extends GMBaseHandler {

	public GMResponse execute(JSONArray arr) {
		String id = arr.getString(0);
		int type = arr.getIntValue(1);
		WNPlayer player = PlayerUtil.findPlayer(id);
		if (player == null) {
			AllBlobPO allBlobData = PlayerDao.getAllBlobData(id);
			player = new WNPlayer(allBlobData);
		}
		Map<String, Object> data = new HashMap<String, Object>();
		if (type == 0) {// 背包物品
			List<GmItemVO> items = new ArrayList<>();
			for (NormalItem it : player.bag.bagGrids.values()) {
				if (it == null) {
					continue;
				}
				boolean isEquip = ItemUtil.isEquipByItemType(it.prop.itemType);
				GmItemVO vo = new GmItemVO(it.itemDb.id, it.prop.code, it.prop.name, it.itemDb.groupCount, isEquip ? "是" : "否");
				if (isEquip) {
					initEquipAttr(vo, it.itemDb);
				}
				items.add(vo);
			}
			data.put("total", items.size());
			data.put("rows", items);
		} else if (type == 1) {// 身上装备
			PlayerBasePO basePo = player.playerBasePO;
			List<GmItemVO> items = new ArrayList<>();
			for (Entry<Integer, PlayerItemPO> entry : basePo.equipGrids.entrySet()) {
				PlayerItemPO it = entry.getValue();
				DItemEquipBase tm = ItemConfig.getInstance().getItemProp(it.code);
				GmItemVO vo = new GmItemVO(it.id, it.code, tm.name, it.groupCount, "");
				vo.setLevel(basePo.strengthPos.get(entry.getKey()).enSection + "段" + basePo.strengthPos.get(entry.getKey()).enLevel + "级");

				StringBuilder sb = new StringBuilder();
				// 处理宝石属性
				for (String code : basePo.strengthPos.get(entry.getKey()).gems.values()) {
					DItemBase prop = ItemUtil.getUnEquipPropByCode(code);
					sb.append(prop.name + "：" + prop.prop + "+" + prop.max);
					sb.append("<br />");
				}
				vo.setGemAttr(sb.toString());
				initEquipAttr(vo, it);
				items.add(vo);
			}
			data.put("total", items.size());
			data.put("rows", items);
		} else if (type == 2) {// 仓库物品
			List<GmItemVO> items = new ArrayList<>();
			for (NormalItem it : player.wareHouse.bagGrids.values()) {
				if (it == null) {
					continue;
				}
				boolean isEquip = ItemUtil.isEquipByItemType(it.prop.itemType);
				GmItemVO vo = new GmItemVO(it.itemDb.id, it.prop.code, it.prop.name, it.itemDb.groupCount, isEquip ? "是" : "否");
				if (isEquip) {
					initEquipAttr(vo, it.itemDb);
				}
				items.add(vo);
			}
			data.put("total", items.size());
			data.put("rows", items);
		}

		JSONObject jo = new JSONObject(data);
		return new GMJsonResponse(jo);
	}

	private void initEquipAttr(GmItemVO vo, PlayerItemPO it) {
		DEquipBase prop = ItemConfig.getInstance().getEquipProp(it.code);
		// 基础属性
		StringBuilder sb = new StringBuilder();
		for (String key : it.speData.baseAtts.keySet()) {
			sb.append(AttributeUtil.getNameByKey(key));
			sb.append("+" + it.speData.baseAtts.get(key));
			sb.append("<br />");
		}
		vo.setBaseAttr(sb.toString());
		// 扩展属性
		sb = new StringBuilder();
		if (it.speData.extAtts == null) {
			for (String key : prop.fixedAtts.keySet()) {
				sb.append(AttributeUtil.getNameByKey(key));
				sb.append("+" + prop.fixedAtts.get(key));
				sb.append("<br />");
			}
		} else {
			for (RepeatKeyMap.Pair<Integer, Integer> rp : it.speData.extAtts.entrySet()) {
				AffixExt affix = GameData.Affixs.get(rp.k);
				if(affix == null) {
					Out.warn("key="+rp.k + " val=" + rp.v);
					continue;
				}
				FourProp pair = affix.props.get(prop.qcolor);
				if(pair==null) {
					continue;
				}
				sb.append(AttributeUtil.getNameByKey(pair.prop));
				sb.append("+" + rp.v);
				sb.append("<br />");
			}

		}
		vo.setExtAttr(sb.toString());
		// 传奇属性
		if (it.speData.legendAtts != null) {
			sb = new StringBuilder();
			for (int affixId : it.speData.legendAtts.keySet()) {
				AffixExt affix = GameData.Affixs.get(affixId);
				if(affix == null) {
					continue;
				}
				FourProp pair = affix.props.get(prop.qcolor);
				if(pair==null) {
					continue;
				}
				sb.append(AttributeUtil.getNameByKey(pair.prop));
				sb.append("+" + it.speData.legendAtts.get(affixId));
			}
			vo.setLegendAttr(sb.toString());
		}
	}

	public short getType() {
		return RpcOpcode.OPCODE_QUERY_BAG_ITEM_INFO;
	}
}