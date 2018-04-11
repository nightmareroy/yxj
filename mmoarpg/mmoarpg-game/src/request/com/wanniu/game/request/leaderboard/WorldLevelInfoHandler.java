package com.wanniu.game.request.leaderboard;

import java.io.IOException;

import com.wanniu.core.game.LangService;
import com.wanniu.core.game.entity.GClientEvent;
import com.wanniu.core.game.protocol.PomeloRequest;
import com.wanniu.core.game.protocol.PomeloResponse;
import com.wanniu.game.common.Const;
import com.wanniu.game.common.msg.ErrorResponse;
import com.wanniu.game.player.WNPlayer;

import pomelo.area.LeaderBoardHandler.WorldLevelInfo;
import pomelo.area.LeaderBoardHandler.WorldLevelInfoResponse;

/**
 * 世界等级信息
 */
@GClientEvent("area.leaderBoardHandler.worldLevelInfoRequest")
public class WorldLevelInfoHandler extends PomeloRequest {

	public PomeloResponse request() throws Exception {
		WNPlayer player = (WNPlayer) pak.getPlayer();

		if (!player.functionOpenManager.isOpen(Const.FunctionType.WORLD_EXP.getValue())) {
			return new ErrorResponse(LangService.getValue("FUNC_SET_PLAYED_NOT_OPEN"));
		}

		WorldLevelInfo data = player.leaderBoardManager.worldLevelInfo(player);

		return new PomeloResponse() {
			@Override
			protected void write() throws IOException {
				WorldLevelInfoResponse.Builder res = WorldLevelInfoResponse.newBuilder();
				res.setS2CCode(OK);
				res.setS2CData(data);
				body.writeBytes(res.build().toByteArray());
			}
		};
	}
}