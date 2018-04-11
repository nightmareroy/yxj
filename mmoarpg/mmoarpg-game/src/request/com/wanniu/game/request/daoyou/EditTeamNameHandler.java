package com.wanniu.game.request.daoyou;

import java.io.IOException;

import com.wanniu.core.game.LangService;
import com.wanniu.core.game.entity.GClientEvent;
import com.wanniu.core.game.protocol.PomeloRequest;
import com.wanniu.core.game.protocol.PomeloResponse;
import com.wanniu.core.util.StringUtil;
import com.wanniu.game.common.msg.ErrorResponse;
import com.wanniu.game.daoyou.DaoYouService;
import com.wanniu.game.player.WNPlayer;
import com.wanniu.game.util.BlackWordUtil;

import pomelo.daoyou.DaoYouHandler.DaoYouEditTeamNameRequest;
import pomelo.daoyou.DaoYouHandler.DaoYouResponse;

/**
 * @author wanghaitao
 *
 */
@GClientEvent("daoyou.daoYouHandler.daoYouEditTeamNameRequest")
public class EditTeamNameHandler extends PomeloRequest {

	@Override
	public PomeloResponse request() throws Exception {
		WNPlayer player = (WNPlayer) pak.getPlayer();

		DaoYouEditTeamNameRequest req = DaoYouEditTeamNameRequest.parseFrom(pak.getRemaingBytes());

		String teamName = req.getTeamName();
		
		if (StringUtil.isEmpty(teamName)) {
			return new ErrorResponse(LangService.getValue("DAO_YOU_NAME_NOT_EMPTY"));
		}
		
		if(BlackWordUtil.isIncludeBlackString(teamName)) {
			return new ErrorResponse(LangService.getValue("PLAYER_ID_SENSITIVE"));
		}

		return new PomeloResponse() {
			@Override
			protected void write() throws IOException {
				DaoYouResponse.Builder res = DaoYouResponse.newBuilder();

				String playerId = player.getId();
				String playerName = player.getName();
				String msg = DaoYouService.getInstance().editDaoYouName(playerId, playerName, teamName);
				if (!msg.equals("")) {
					res.setS2CCode(FAIL);
					res.setS2CMsg(msg);
					body.writeBytes(res.build().toByteArray());
				} else {
					res.setS2CCode(OK);
					body.writeBytes(res.build().toByteArray());
				}

			}
		};
	}

}
