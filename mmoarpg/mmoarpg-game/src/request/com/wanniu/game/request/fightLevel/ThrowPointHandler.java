package com.wanniu.game.request.fightLevel;

import java.io.IOException;

import com.wanniu.core.game.entity.GClientEvent;
import com.wanniu.core.game.protocol.PomeloResponse;
import com.wanniu.game.area.Area;
import com.wanniu.game.player.WNPlayer;

import pomelo.area.BattleHandler.ThrowPointRequest;
import pomelo.area.BattleHandler.ThrowPointResponse;

/**
 * 购买进入次数
 * @author agui
 */
@GClientEvent("area.battleHandler.throwPointRequest")
public class ThrowPointHandler extends FightLevelLine {

	public PomeloResponse request(WNPlayer player) throws Exception {

		ThrowPointRequest req = ThrowPointRequest.parseFrom(pak.getRemaingBytes());
		String id = req.getId();
		return new PomeloResponse() {
			@Override
			protected void write() throws IOException {
				ThrowPointResponse.Builder res = ThrowPointResponse.newBuilder();

				Area area = player.getArea();

				res.setS2CCode(OK);
				res.setPoint(area.randomPoint(player, id));

				body.writeBytes(res.build().toByteArray());

			}
		};
	}

}