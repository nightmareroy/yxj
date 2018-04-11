package com.wanniu.game.request.player;

import java.io.IOException;
import java.util.List;

import com.wanniu.core.game.LangService;
import com.wanniu.core.game.entity.GClientEvent;
import com.wanniu.core.game.protocol.PomeloRequest;
import com.wanniu.core.game.protocol.PomeloResponse;
import com.wanniu.game.GWorld;
import com.wanniu.game.common.Const;
import com.wanniu.game.common.msg.ErrorResponse;
import com.wanniu.game.data.GameData;
import com.wanniu.game.data.ext.TreasureClassExt;
import com.wanniu.game.item.ItemUtil;
import com.wanniu.game.item.NormalItem;
import com.wanniu.game.player.WNPlayer;

import pomelo.area.PlayerHandler.SimulateDataPush;
import pomelo.area.PlayerHandler.SimulateDropByTcRequest;
import pomelo.area.PlayerHandler.SimulateDropByTcResponse;
import pomelo.item.ItemOuterClass.MiniItem;

/**
 * 获取角色坐标
 * 
 * @author agui
 *
 */
@GClientEvent("area.playerHandler.getSimulateDropByTcRequest")
public class GetSimulateDropByTcHandler extends PomeloRequest {

	public PomeloResponse request() throws Exception {

		if (!GWorld.DEBUG) {
			pak.getSession().close();
			return null;
		}

		WNPlayer player = (WNPlayer) pak.getPlayer();

		SimulateDropByTcRequest req = SimulateDropByTcRequest.parseFrom(pak.getRemaingBytes());
		String tcCode = req.getC2STcCode();
		int tcCount = req.getC2STcCount();
		int tcLevel = req.getC2STcLevel();

		if (tcCount > 10000) {
			return new ErrorResponse(LangService.getValue("SIMULATE_TOO_LARGE"));
		}
		TreasureClassExt prop = GameData.TreasureClasss.get(tcCode);
		if (prop == null) {
			return new ErrorResponse(LangService.getValue("SIMULATE_PROP_FIND"));
		}
		if (prop.tcLevel < tcLevel) {
			return new ErrorResponse(LangService.getValue("SIMULATE_LEVEL_LOW"));
		}

		SimulateDropByTcResponse.Builder res = SimulateDropByTcResponse.newBuilder();
		int runCount = 0;
		for (int i = 0; i < tcCount; ++i) {
			List<NormalItem> tcItems = ItemUtil.createItemsByTcCode(tcCode);
			if (tcItems.size() > 0) {
				runCount++;
				SimulateDataPush.Builder data = SimulateDataPush.newBuilder();
				tcItems.forEach(v -> {
					if (v.prop.itemSecondType == Const.ItemSecondType.virtual.getValue()) {
						MiniItem.Builder propTmp = ItemUtil.getMiniItemData(v.itemCode(), v.getWorth());
						data.addS2CItems(propTmp);

					} else {
						MiniItem.Builder propTmp = ItemUtil.getMiniItemData(v.itemCode(), v.getNum());
						data.addS2CItems(propTmp);
					}
				});
				player.receive("area.playerPush.simulateDropPush", data.build());
			}
		}

		res.setS2CRunNum(runCount);
		res.setS2CSuccPer((runCount / tcCount) * 100);

		return new PomeloResponse() {
			@Override
			protected void write() throws IOException {
				res.setS2CCode(OK);
				body.writeBytes(res.build().toByteArray());
			}
		};

	}
}