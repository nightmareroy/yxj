package com.wanniu.game.request.area;

import com.wanniu.core.game.entity.GClientEvent;
import com.wanniu.core.game.protocol.PomeloRequest;
import com.wanniu.core.game.protocol.PomeloResponse;

/**
 * 战斗事件通知
 * @author c
 *
 */
@GClientEvent("area.playerHandler.battleEventNotify")
public class BattleEventNotifyHandler extends PomeloRequest {

	public PomeloResponse request() throws Exception {
//		BattleEventNotify req = BattleEventNotify.parseFrom(pak.getRemaingBytes());
//		ByteString s2c_data = req.getC2SData();

		return null;
	}

}
