package com.wanniu.game.request.player;

import java.io.IOException;

import com.wanniu.core.game.LangService;
import com.wanniu.core.game.entity.GClientEvent;
import com.wanniu.core.game.protocol.PomeloRequest;
import com.wanniu.core.game.protocol.PomeloResponse;
import com.wanniu.core.logfs.Out;
import com.wanniu.game.area.Area;
import com.wanniu.game.area.Area.Actor;
import com.wanniu.game.area.Area.ReliveType;
import com.wanniu.game.common.Const;
import com.wanniu.game.common.msg.ErrorResponse;
import com.wanniu.game.data.GameData;
import com.wanniu.game.data.ResurrectionCO;
import com.wanniu.game.player.ReliveManager;
import com.wanniu.game.player.WNPlayer;
import com.wanniu.game.poes.PlayerPO;

import pomelo.area.PlayerHandler.ReliveRequest;
import pomelo.area.PlayerHandler.ReliveResponse;

/**
 * 复活请求
 * 
 * @author agui
 */
@GClientEvent("area.playerHandler.reliveRequest")
public class ReliveHandler extends PomeloRequest {

	public PomeloResponse request() throws Exception {

		WNPlayer player = (WNPlayer) pak.getPlayer();

		ReliveRequest req = ReliveRequest.parseFrom(pak.getRemaingBytes());
		int c2s_type = req.getType();
		int payConfirm = req.getAutoPay();
		Out.debug("reliveRequest :: ", c2s_type, " && ", payConfirm);
		Area area = player.getArea();
		ReliveManager reliveManager = player.getReliveManager();
		int total = reliveManager.getReliveTotal(area.areaId);

		ReliveType reliveType = area.getReliveType();
		int cost = Math.max(Const.RELIVE_DIAMOND, reliveManager.payCost);
		int reliveNum = Const.RELIVE_NUM + player.vipManager.getReliveNum();
		ResurrectionCO resurrection = GameData.Resurrections.get(area.areaId);
		if (resurrection != null) {
			if (resurrection.freeResurrect > 0) {
				reliveNum = 0;
			}
		}
		if (c2s_type == 1) {
			reliveType = ReliveType.NOW;
			if (reliveManager.nowReliveNum >= reliveNum) {
				if (payConfirm == 1) {
					reliveManager.payConfirm = 0;
				}
				PlayerPO po = player.player;
				int diamond = po.diamond + po.ticket;
				if (cost > diamond) {
					return new ErrorResponse(LangService.getValue("NOT_ENOUGH_DIAMOND"));
				}
				if (po.ticket > cost) {
					player.moneyManager.costTicket(cost, Const.GOODS_CHANGE_TYPE.relive);
				} else {
					int ticket = po.ticket;
					if (ticket > 0) {
						player.moneyManager.costTicket(ticket, Const.GOODS_CHANGE_TYPE.relive);
					}
					player.moneyManager.costDiamond(cost, Const.GOODS_CHANGE_TYPE.relive);
					// player.pushDynamicData("diamond", player.player.diamond);
				}
			}
			reliveManager.nowReliveNum++;
		} else {
			Actor actor = area.getActor(player.getId());
			if (actor != null) {
				if (System.currentTimeMillis() < actor.reliveCoolTime) {
					return new ErrorResponse(LangService.format("RELIVE_COOL_TIME", Math.max(1, (actor.reliveCoolTime - System.currentTimeMillis()) / 1000)));
				}
			} else {
				return new ErrorResponse("");
			}
		}
		if (!area.isNormal() && total > 0) {
			int count = reliveManager.getReliveCount(area.areaId);
			if (count >= total) {
				return new ErrorResponse(LangService.getValue("RELIVE_FULL"));
			}
			reliveManager.addRelive(area.areaId);
		}

		ReliveResponse.Builder res = area.relive(player.getId(), reliveType);

		return new PomeloResponse() {
			@Override
			protected void write() throws IOException {
				body.writeBytes(res.build().toByteArray());
			}
		};
	}
}