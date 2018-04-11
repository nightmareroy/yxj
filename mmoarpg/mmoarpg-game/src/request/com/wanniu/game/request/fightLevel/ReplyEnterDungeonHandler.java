package com.wanniu.game.request.fightLevel;

import java.io.IOException;

import com.wanniu.core.game.entity.GClientEvent;
import com.wanniu.core.game.protocol.PomeloResponse;
import com.wanniu.core.logfs.Out;
import com.wanniu.game.fightLevel.FightLevelManager;
import com.wanniu.game.player.WNPlayer;

import pomelo.area.FightLevelHandler.ReplyEnterDungeonRequest;
import pomelo.area.FightLevelHandler.ReplyEnterDungeonResponse;

/**
 * 确认是否同意进入副本
 * @author agui
 *
 */
@GClientEvent("area.fightLevelHandler.replyEnterDungeonRequest")
public class ReplyEnterDungeonHandler extends FightLevelLine {

	public PomeloResponse request(WNPlayer player) throws Exception {

		ReplyEnterDungeonRequest req = ReplyEnterDungeonRequest.parseFrom(pak.getRemaingBytes());

		int dungeonId = req.getC2SDungeonId();
		int c2sType = req.getC2SType();	//1：接受，2：拒绝
		
		String data = null;
//			Area area = player.getArea();
//			if (area.getSceneType() == Const.SCENE_TYPE.CROSS_SERVER.getValue()) {
//				c2sType = 2;
//			}
		FightLevelManager fightLevelManager = player.fightLevelManager;
		data = fightLevelManager.replyEnterDungeon(player, c2sType, dungeonId);

		Out.debug("c2sType:", c2sType, " - ", data);
		
		ReplyEnterDungeonResponse.Builder res = ReplyEnterDungeonResponse.newBuilder();
		if (data == null) {
			res.setS2CCode(OK);
		} else {
			res.setS2CCode(FAIL);
			res.setS2CMsg(data);
		}
		return new PomeloResponse() {
			@Override
			protected void write() throws IOException {
				body.writeBytes(res.build().toByteArray());
			}
		};
	}
	
}