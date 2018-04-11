package com.wanniu.game.request.fightLevel.illusion;

import java.io.IOException;

import com.wanniu.core.game.entity.GClientEvent;
import com.wanniu.core.game.protocol.PomeloResponse;
import com.wanniu.game.player.WNPlayer;
import com.wanniu.game.poes.IllusionPO;
import com.wanniu.game.request.fightLevel.FightLevelLine;

import pomelo.area.FightLevelHandler.GetLllsionInfoResponse;

/**
 * 获取幻境界面信息
 * 
 * @author Yangzz
 */
@GClientEvent("area.fightLevelHandler.getLllsionInfoRequest")
public class GetIllsionInfoHandler extends FightLevelLine {

	public PomeloResponse request(WNPlayer player) throws Exception {
		IllusionPO illusion = player.illusionManager.illusionPO;

		GetLllsionInfoResponse.Builder res = GetLllsionInfoResponse.newBuilder();
		res.setS2CCode(OK);
		res.setS2CMaxExp(0);
		res.setS2CMaxClassexp(0);
		res.setS2CMaxGold(0);

		res.setS2CTodayExp(illusion.todayExp);
		res.setS2CTodayClassexp(illusion.todayClassExp);
		res.setS2CTodayGold(illusion.todayGold);

		return new PomeloResponse() {
			@Override
			protected void write() throws IOException {
				body.writeBytes(res.build().toByteArray());
			}
		};
	}
}