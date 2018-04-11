package com.wanniu.game.request.player;

import java.io.IOException;

import com.wanniu.core.game.LangService;
import com.wanniu.core.game.entity.GClientEvent;
import com.wanniu.core.game.protocol.PomeloRequest;
import com.wanniu.core.game.protocol.PomeloResponse;
import com.wanniu.core.logfs.Out;
import com.wanniu.game.common.Const;
import com.wanniu.game.common.msg.ErrorResponse;
import com.wanniu.game.data.base.MapBase;
import com.wanniu.game.player.GlobalConfig;
import com.wanniu.game.player.WNPlayer;

import pomelo.area.PlayerHandler.ChangPkModelRequest;
import pomelo.area.PlayerHandler.ChangPkModelRespone;

/**
 * 请求改变pk模式
 * 
 * @author agui
 *
 */
@GClientEvent("area.playerHandler.ChangePkModelRequest")
public class ChangePkModelHandler extends PomeloRequest {

	public PomeloResponse request() throws Exception {

		WNPlayer player = (WNPlayer) pak.getPlayer();

		ChangPkModelRequest req = ChangPkModelRequest.parseFrom(pak.getRemaingBytes());
		int reqModel = req.getC2SModel();

		// 获取场景类型
		MapBase sceneProp = player.getArea().prop;
		if (sceneProp.changePKtype == 0) {
			return new ErrorResponse(LangService.getValue("AREA_CANNOT_CHANG_PKMODE"));
		}

		int levelReq = GlobalConfig.PK_LevelReq;

		if (levelReq > 0 && player.getLevel() < levelReq) {
			return new ErrorResponse(LangService.getValue("PK_LEVEL_REQ").replace("{0}", String.valueOf(levelReq)));
		}

		if (reqModel == Const.PkModel.Team.value && !player.getTeamManager().isInTeam()) {
			return new ErrorResponse(LangService.getValue("TEAM_NO_TEAM"));
		}
		if (reqModel == Const.PkModel.Guild.value && player.guildManager.guild == null) {
			return new ErrorResponse(LangService.getValue("NOTHAVE_GUILD"));
		}

		if (reqModel != player.pkRuleManager.pkData.pkModel) {
			player.pkRuleManager.pkData.pkModel = reqModel;
			Out.debug("ChangePkModelRequest: " , reqModel);
			player.getXmdsManager().refreshPlayerPKMode(player.getId(), reqModel);
		}

		return new PomeloResponse() {
			@Override
			protected void write() throws IOException {
				ChangPkModelRespone.Builder res = ChangPkModelRespone.newBuilder();
				res.setS2CCode(OK);
				res.setS2CCurrentModel(reqModel);
				body.writeBytes(res.build().toByteArray());
			}
		};
	}
}