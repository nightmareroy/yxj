package com.wanniu.gm.handler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.wanniu.game.common.Const.PlayerBtlData;
import com.wanniu.game.data.GameData;
import com.wanniu.game.player.PlayerDao;
import com.wanniu.game.player.PlayerUtil;
import com.wanniu.game.player.WNPlayer;
import com.wanniu.game.player.po.AllBlobPO;
import com.wanniu.game.poes.MountPO;
import com.wanniu.gm.GMEvent;
import com.wanniu.gm.GMJsonResponse;
import com.wanniu.gm.GMResponse;

import cn.qeng.common.gm.RpcOpcode;
import cn.qeng.common.gm.vo.GmMountVO;

/**
 * 玩家坐骑查询
 * 
 * @author lxm
 *
 */
@GMEvent
public class PlayerMountHandler extends GMBaseHandler {

	public GMResponse execute(JSONArray arr) {
		String id = arr.getString(0);
		WNPlayer wnPlayer = PlayerUtil.getOnlinePlayer(id);
		if (wnPlayer == null) {
			AllBlobPO allBlobData = PlayerDao.getAllBlobData(id);
			wnPlayer = new WNPlayer(allBlobData);
		}
		List<GmMountVO> list = new ArrayList<>();
		MountPO mount = wnPlayer.mountManager.mount;
		StringBuilder attr = new StringBuilder();
		for (Entry<PlayerBtlData, Integer> entry : wnPlayer.mountManager.data_mount_final.entrySet()) {
			attr.append(entry.getKey().chName + "+" + entry.getValue() + "<br />");
		}
		if (mount != null) {
			list.add(new GmMountVO(GameData.SkinLists.get(mount.usingSkinId).skinName, String.valueOf(mount.rideLevel), mount.starLv, attr.toString()));
		}
		Map<String, Object> data = new HashMap<String, Object>();
		data.put("total", 1);
		data.put("rows", list);
		JSONObject jo = new JSONObject(data);

		return new GMJsonResponse(jo);
	}

	public short getType() {
		return RpcOpcode.OPCODE_QUERY_PLAYER_MOUNT;
	}
}