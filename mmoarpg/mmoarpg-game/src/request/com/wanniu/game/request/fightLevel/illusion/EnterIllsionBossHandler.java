package com.wanniu.game.request.fightLevel.illusion;

import java.io.IOException;

import com.wanniu.core.game.LangService;
import com.wanniu.core.game.entity.GClientEvent;
import com.wanniu.core.game.protocol.PomeloResponse;
import com.wanniu.core.logfs.Out;
import com.wanniu.game.GWorld;
import com.wanniu.game.area.AreaUtil;
import com.wanniu.game.common.msg.ErrorResponse;
import com.wanniu.game.data.GameData;
import com.wanniu.game.data.TypeNameCO;
import com.wanniu.game.data.ext.MonsterRefreshExt;
import com.wanniu.game.player.WNPlayer;
import com.wanniu.game.request.fightLevel.FightLevelLine;

import pomelo.area.FightLevelHandler.EnterLllsionBossRequest;
import pomelo.area.FightLevelHandler.EnterLllsionBossResponse;

/**
 * 进入幻境-领主
 * 
 * @author Yangzz
 */
@GClientEvent("area.fightLevelHandler.enterLllsionBossRequest")
public class EnterIllsionBossHandler extends FightLevelLine {

	public PomeloResponse request(WNPlayer player) throws Exception {

		EnterLllsionBossRequest req = EnterLllsionBossRequest.parseFrom(pak.getRemaingBytes());

		int monsterRereshId = req.getC2SId();
		Out.info(this.getClass().getName(), " : ", monsterRereshId);

		MonsterRefreshExt refreshCO = GameData.MonsterRefreshs.get(monsterRereshId);
		TypeNameCO refreshTypeName = GameData.TypeNames.get(refreshCO.type);

		if (refreshCO == null || player.getLevel() < refreshTypeName.minLv || player.getLevel() > refreshTypeName.maxLv) { //secionCO.minLv
			return new ErrorResponse(LangService.getValue("LEVEL_NOT_LIMIT_ENTER"));
		}
		
		if(refreshCO.pointX>0 && refreshCO.pointY>0){
			int dstId = refreshCO.mapID; float dstX = refreshCO.pointX, dstY = refreshCO.pointY;
			GWorld.getInstance().ansycExec(() -> {
				String instanceId = player.getInstanceId();
				int oldAreaId = player.getAreaId();
				AreaUtil.enterArea(player, dstId, dstX, dstY);
				if (AreaUtil.needCreateArea(oldAreaId)) {
					AreaUtil.closeAreaNoPlayer(instanceId);
				}
			});
		}else{
			AreaUtil.enterArea(player, refreshCO.mapID);
		}

		return new PomeloResponse() {
			@Override
			protected void write() throws IOException {
				EnterLllsionBossResponse.Builder res = EnterLllsionBossResponse.newBuilder();
				res.setS2CCode(OK);
				body.writeBytes(res.build().toByteArray());

			}
		};
	}

}