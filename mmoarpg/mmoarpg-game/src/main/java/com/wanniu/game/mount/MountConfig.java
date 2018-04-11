package com.wanniu.game.mount;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import com.wanniu.game.data.GameData;
import com.wanniu.game.data.ext.RideListExt;
import com.wanniu.game.data.ext.SkinListExt;

public class MountConfig {

	private static MountConfig instance;

	public static MountConfig getInstance() {
		if (instance == null) {
			instance = new MountConfig();
		}
		return instance;
	}

	private Map<Integer, RideListExt> mountTable = GameData.RideLists;// new TreeMap<>();
	private Map<Integer, SkinListExt> mountSkinTable = GameData.SkinLists;// new TreeMap<>();

	/*** 坐骑列表 */
	public List<RideListExt> getMountPropList() {
		return new ArrayList<RideListExt>(GameData.RideLists.values());
	}

	/*** 皮肤列表 */
	public List<SkinListExt> getMountSkinPropList() {
		return new ArrayList<SkinListExt>(GameData.SkinLists.values());
	}

	/*** 查找坐骑 */
	public RideListExt getMountPropByID(int mountId) {
		return (RideListExt) this.mountTable.get(mountId);
	}

	/*** 查找皮肤 */
	public SkinListExt getMountSkinPropByID(int skinId) {
		return (SkinListExt) this.mountSkinTable.get(skinId);
	}

	public Map<String, Integer> getSkinTotalAttribute(List<Integer> skinAry) {
		Map<String, Integer> attrTable = new TreeMap<>();
		for (Entry<Integer, SkinListExt> node : mountSkinTable.entrySet()) {
			SkinListExt skin = node.getValue();
			if (skinAry.indexOf(skin.skinID) != -1) {
				// Map<String,Integer> skillAttr = skin.attrs;
				// for(Map.Entry<String, Integer> data : skillAttr.entrySet()){
				// int value = data.getValue();
				// if(attrTable.containsKey(data.getKey())){
				// value += attrTable.get(data.getKey());
				// }
				// attrTable.put(data.getKey(), value);
				// }
			}
		}
		return attrTable;
	}

}
