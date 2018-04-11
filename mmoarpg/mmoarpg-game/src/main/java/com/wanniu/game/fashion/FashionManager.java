package com.wanniu.game.fashion;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.wanniu.core.logfs.Out;
import com.wanniu.game.common.Const;
import com.wanniu.game.common.Const.GOODS_CHANGE_TYPE;
import com.wanniu.game.common.Const.ManagerType;
import com.wanniu.game.common.Const.PlayerBtlData;
import com.wanniu.game.common.Const.PlayerEventType;
import com.wanniu.game.common.ModuleManager;
import com.wanniu.game.data.GameData;
import com.wanniu.game.data.ext.FashSuitConfigExt;
import com.wanniu.game.data.ext.FashionExt;
import com.wanniu.game.player.WNPlayer;
import com.wanniu.game.player.bi.LogReportService;

import pomelo.area.PlayerHandler.SuperScriptType;
import pomelo.item.ItemOuterClass.EquipFashionInfo;

/**
 * 时装
 * 
 * @author Yangzz
 *
 */
public class FashionManager extends ModuleManager {

	public WNPlayer player;

	public FashionManager(WNPlayer player) {
		this.player = player;

		// if (GWorld.DEBUG) {
		// if (player.playerBasePO.fashions == null) {
		// player.playerBasePO.fashions = new HashMap<>();
		// }
		// }

		
	}

	// private List<String> getFashionsOfType(Const.FASHION_TYPE type)
	// {
	// FashionExt fashionExt=GameData.Fashions.get(key)
	// List<String> result=new LinkedList<>();
	// for (String code : player.playerBasePO.fashions_get) {
	// if()
	// }
	// }

	// 判断是否已获取该时装
	private boolean getFashionGet(String code) {
		boolean have = false;
		for (Object[] fashion : player.playerBasePO.fashions_get) {
			if (((String)fashion[0]).equals(code)) {
				have = true;
				break;
			}
		}
		if (!have) {
			return false;
		}
		return true;
	}
	
	private SuperScriptType createSuperScriptType(int type, int num) {
		SuperScriptType.Builder data = SuperScriptType.newBuilder();
		data.setType(type);
		data.setNumber(num);
		return data.build();
	}
	
	public List<SuperScriptType> getSuperScriptList() {
		List<SuperScriptType> ls = new ArrayList<SuperScriptType>();
		int count=0;
		for(Object[] fashionObj:player.playerBasePO.fashions_get) {
			if(!(boolean)fashionObj[1]) {
				count++;
			}
		}

		ls.add(createSuperScriptType(Const.SUPERSCRIPT_TYPE.FLAG_FASHION.getValue(), count));


		return ls;
	}
	

	/**
	 * 激活时装
	 */
	public boolean activiateFashion(String code) {

		if (getFashionGet(code)) {
			return false;
		}
		
		FashionExt fashionExt = GameData.Fashions.get(code);
		if(player.getPro()!=fashionExt.pro)
		{
			return false;
		}
		player.playerBasePO.fashions_get.add(new Object[] {code,false});
		
		LogReportService.getInstance().ansycReportFashion(player, code);
		
//		player.playerBasePO.fashion_get_spot=1;
		List<SuperScriptType> ls = getSuperScriptList();
		this.player.updateSuperScriptList(ls); // 红点推送给玩家
		
		return true;
	}

	/**
	 * 穿脱时装 isOn true:穿上 false:脱下
	 */
	public boolean equipFashion(String code, boolean isOn) {
		FashionExt fashionExt = GameData.Fashions.get(code);
		
		if (!getFashionGet(code)) {
			return false;
		}
		if (isOn) {
			player.playerBasePO.fashions_equiped.put(fashionExt.type, fashionExt.code);
		} else {
			player.playerBasePO.fashions_equiped.put(fashionExt.type, null);
		}

		fashionSync();
		return true;

	}

	/**
	 * 由装备的穿脱的属性变化以及处理
	 */
	public void fashionSync() {
		this.player.btlDataManager.data_fashion = calAllInfluence();
		this.player.btlDataManager.calFinalData();
		this.player.onFashionChange();
		this.player.refreshBattlerServerAvatar();
	};
	
	/**
	 * 查看时装后消除红点
	 * @param code
	 */
	public void checkFashion(String code) {
		for(Object[] fashionObj:player.playerBasePO.fashions_get) {
			if(fashionObj[0].equals(code)) {
				fashionObj[1] = true;
			}
		}
		List<SuperScriptType> ls = getSuperScriptList();
		player.updateSuperScriptList(ls); // 红点推送给玩家
	}

	/**
	 * 计算时装属性
	 */
	public Map<PlayerBtlData, Integer> calAllInfluence() {
		Map<PlayerBtlData, Integer> data = new ConcurrentHashMap<>();

		// 时装单独的属性
		for (Map.Entry<Integer, String> entry : player.playerBasePO.fashions_equiped.entrySet()) {
			if (entry.getValue() == null) {
				continue;
			}
			String code = entry.getValue();

			FashionExt fashion = GameData.Fashions.get(code);

			deepCopy(data, fashion.atts);
		}

		// 时装 套装属性
		// 先计算除 每套 套装集齐了几件
		Map<Integer, List<String>> suits = new HashMap<>();
		for (FashionExt fashion : GameData.Fashions.values()) {
			List<String> list = suits.get(fashion.fashionID);
			if (list == null) {
				list = new ArrayList<>();
				suits.put(fashion.fashionID, list);
			}
			if (list.contains(fashion.code)) {
				Out.debug(list);
				Out.error("居然会走到这里？");
				continue;
			}
			
			for (Object[] fashionObj : player.playerBasePO.fashions_get) {
				if(fashionObj[0].equals(fashion.code)) {
					list.add(fashion.code);
				}
			}

//			for (String temp_code : player.playerBasePO.fashions_get) {
//				list.add(temp_code);
//			}
			
			// if (player.bag.findItemByProp(fashion.code).size() > 0) {
			// list.add(fashion.code);
			// }
		}
		// 再计算每个n件套的属性
		for (FashSuitConfigExt suit : GameData.FashSuitConfigs.values()) {
			for (int suitID : suits.keySet()) {
				int count = suits.get(suitID).size();
				if (count == 2) {
					deepCopy(data, suit.Attr2Map);
				}
				if (count == 3) {
					deepCopy(data, suit.Attr3Map);
				}
			}
		}
		return data;
	}

	/**
	 * 复制Map对象
	 */
	private void deepCopy(Map<PlayerBtlData, Integer> data, Map<PlayerBtlData, Integer> source) {
		if (source == null) {
			return;
		}
		for (Map.Entry<PlayerBtlData, Integer> entry : source.entrySet()) {
			if (data.get(entry.getKey()) != null && data.get(entry.getKey()) > 0) {
				data.put(entry.getKey(), data.get(entry.getKey()) + entry.getValue());
			} else {
				data.put(entry.getKey(), entry.getValue());
			}
		}
	};

	public List<EquipFashionInfo> toJson4Fashion() {
		List<EquipFashionInfo> list = new ArrayList<>();
		for (Object[] tempFashion : player.playerBasePO.fashions_get) {
			FashionExt fashion = GameData.Fashions.get(tempFashion[0]);

			EquipFashionInfo.Builder fashionInfo = EquipFashionInfo.newBuilder();
			fashionInfo.setPos(fashion.type);
			fashionInfo.setItemcode((String)tempFashion[0]);
			list.add(fashionInfo.build());
		}
		return list;
	}

	@Override
	public void onPlayerEvent(PlayerEventType eventType) {
		switch (eventType) {
		case AFTER_LOGIN:
			List<SuperScriptType> ls = getSuperScriptList();
			player.updateSuperScriptList(ls); // 红点推送给玩家
			break;

		default:
			break;
		}
	}

	@Override
	public ManagerType getManagerType() {
		// TODO Auto-generated method stub
		return ManagerType.FASHION;
	}
}
