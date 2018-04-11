package com.wanniu.gm.handler;

import com.alibaba.fastjson.JSONArray;
import com.wanniu.game.player.PlayerDao;
import com.wanniu.game.player.PlayerUtil;
import com.wanniu.game.player.WNPlayer;
import com.wanniu.game.player.po.AllBlobPO;
import com.wanniu.gm.GMErrorResponse;
import com.wanniu.gm.GMEvent;
import com.wanniu.gm.GMResponse;
import com.wanniu.gm.GMStateResponse;

/**
 * 删除玩家物品
 * 
 * @author lxm
 *
 */
@GMEvent
public class DeleteItemHandler extends GMBaseHandler {

	public GMResponse execute(JSONArray arr) {
		String id = arr.getString(0);
		String itemId = arr.getString(1);
		int num = arr.getIntValue(2);
		int deleteType = arr.getIntValue(3);
		if (num < 0) {
			return new GMErrorResponse();
		}
		WNPlayer player = PlayerUtil.findPlayer(id);
		if (player == null) {
			AllBlobPO allBlobData = PlayerDao.getAllBlobData(id);
			player = new WNPlayer(allBlobData);
		}
		if (deleteType == 0) {
			player.bag.gmDeleteItemById(itemId, num);
			player.bag.update();
		} else if (deleteType == 1) {
			player.wareHouse.gmDeleteItemById(itemId, num);
			player.wareHouse.update();
		}
		return new GMStateResponse(1);
	}

	public short getType() {
		return 0x3022;
	}

}
