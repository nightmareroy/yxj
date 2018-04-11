package com.wanniu.game.request.solo;

import java.io.IOException;

import com.wanniu.core.game.entity.GClientEvent;
import com.wanniu.core.game.protocol.PomeloResponse;
import com.wanniu.game.area.Area;
import com.wanniu.game.common.Const;
import com.wanniu.game.player.PlayerUtil;
import com.wanniu.game.player.WNPlayer;

import pomelo.area.SoloHandler.GetRivalInfoResponse;

/**
 * 获取对手信息
 */
@GClientEvent("area.soloHandler.getRivalInfoRequest")
public class GetRivalInfoHandler extends SoloRequestFilter {

	public PomeloResponse request(WNPlayer player) throws Exception {

		return new PomeloResponse() {

			@Override
			protected void write() throws IOException {

				GetRivalInfoResponse.Builder res = GetRivalInfoResponse.newBuilder();
				res.setS2CCode(Const.CODE.OK);
				res.setS2CVsPlayerName("");
				res.setS2CVsPlayerPro(1);
				res.setS2CVsPlayerLevel(0);
				
				Area area = player.getArea();
				for (String id : area.actors.keySet()) {
					if (id.equals(player.getId())) {
						continue;
					}
					WNPlayer rival = PlayerUtil.getOnlinePlayer(id);
					res.setS2CVsPlayerName(rival.getName());
					res.setS2CVsPlayerPro(rival.getPro());
					res.setS2CVsPlayerLevel(rival.getLevel());
				}
				body.writeBytes(res.build().toByteArray());
			}

		};
	}
}