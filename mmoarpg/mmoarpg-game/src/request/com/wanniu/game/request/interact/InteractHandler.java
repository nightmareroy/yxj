package com.wanniu.game.request.interact;

import java.io.IOException;

import com.wanniu.core.game.LangService;
import com.wanniu.core.game.entity.GClientEvent;
import com.wanniu.core.game.protocol.PomeloRequest;
import com.wanniu.core.game.protocol.PomeloResponse;
import com.wanniu.core.logfs.Out;
import com.wanniu.game.common.Const;
import com.wanniu.game.common.msg.ErrorResponse;
import com.wanniu.game.data.InteractionCO;
import com.wanniu.game.interact.PlayerInteract;
import com.wanniu.game.player.WNPlayer;

import pomelo.area.InteractHandler.InteractRequest;
import pomelo.area.InteractHandler.InteractResponse;

/**
 * 交互：如送鲜花
 * @author agui
 */
@GClientEvent("area.interactHandler.interactRequest")
public class InteractHandler extends PomeloRequest {

	@Override
	public PomeloResponse request() throws Exception {
		WNPlayer player = (WNPlayer)pak.getPlayer();
		InteractRequest msg = InteractRequest.parseFrom(pak.getRemaingBytes());
		int id = msg.getC2SId();
		InteractionCO prop = PlayerInteract.getPropById(id);
		if (prop == null) {
			return new ErrorResponse("interact error!!!");
		}

	    if(!player.getInteractManager().enoughTimes(id)){
			return new ErrorResponse(LangService.getValue("TIMES_NOT_ENOUGH"));
	    }

	    String playerId = msg.getC2SPlayerId();
	    String playerName = msg.getC2SPlayerName();
		Out.debug("interactRequest id:", id, "  playerId:", playerId, "  playerName:", playerName);
		return new PomeloResponse() {
			@Override
			protected void write() throws IOException {
				InteractResponse.Builder res = InteractResponse.newBuilder();

				int gold = prop.gold;
				int diamond = prop.diamond;
				if (gold > 0) {
					if (player.moneyManager.getGold() > gold) {
						player.moneyManager.costGold(gold, Const.GOODS_CHANGE_TYPE.interact);
						player.getInteractManager().send(id, playerId, playerName);
						res.setS2CCode(OK);
					} else {
						res.setS2CCode(FAIL);
						res.setS2CMsg(LangService.getValue("GOLD_NOT_ENOUGH"));
					}
				} else {
					if (player.moneyManager.enoughDiamond(diamond)) {
						player.moneyManager.costDiamond(diamond, Const.GOODS_CHANGE_TYPE.interact);
//						player.pushDynamicData("diamond", player.player.diamond);
						player.getInteractManager().send(id, playerId, playerName);
						res.setS2CCode(OK);
					} else {
						res.setS2CCode(FAIL);
						res.setS2CMsg(LangService.getValue("DIAMAND_NOT_ENOUGH"));
					}
				}
				body.writeBytes(res.build().toByteArray());
			}
		};
	}

}
