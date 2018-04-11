package com.wanniu.game.request.fightLevel.illusion;

import java.io.IOException;

import com.wanniu.core.game.LangService;
import com.wanniu.core.game.entity.GClientEvent;
import com.wanniu.core.game.protocol.PomeloResponse;
import com.wanniu.core.logfs.Out;
import com.wanniu.game.area.AreaUtil;
import com.wanniu.game.common.msg.ErrorResponse;
import com.wanniu.game.data.GameData;
import com.wanniu.game.data.SectionCO;
import com.wanniu.game.player.WNPlayer;
import com.wanniu.game.request.fightLevel.FightLevelLine;

import pomelo.area.FightLevelHandler.EnterLllsionRequest;
import pomelo.area.FightLevelHandler.EnterLllsionResponse;

/**
 * 进入幻境
 * 
 * @author Yangzz
 */
@GClientEvent("area.fightLevelHandler.enterLllsionRequest")
public class EnterIllsionHandler extends FightLevelLine {

	public PomeloResponse request(WNPlayer player) throws Exception {

		EnterLllsionRequest req = EnterLllsionRequest.parseFrom(pak.getRemaingBytes());

		int lllusionId = req.getC2SId();
		Out.debug(this.getClass().getName(), " : ", lllusionId);

		SectionCO secionCO = GameData.Sections.get(lllusionId);

		if (secionCO == null || player.getLevel() < secionCO.minLv) {
			return new ErrorResponse(LangService.getValue("PARAM_ERROR"));
		}

		return new PomeloResponse() {
			@Override
			protected void write() throws IOException {
				EnterLllsionResponse.Builder res = EnterLllsionResponse.newBuilder();

				AreaUtil.enterArea(player, secionCO.dungeonID);

				res.setS2CCode(OK);
				body.writeBytes(res.build().toByteArray());

			}
		};
	}

}