package com.wanniu.game.request.farm;

import java.io.IOException;

import com.wanniu.core.game.LangService;
import com.wanniu.core.game.entity.GClientEvent;
import com.wanniu.core.game.protocol.PomeloRequest;
import com.wanniu.core.game.protocol.PomeloResponse;
import com.wanniu.core.logfs.Out;
import com.wanniu.game.data.PlantingCO;
import com.wanniu.game.farm.FarmMgr;
import com.wanniu.game.farm.FarmMgr.BLOCK_STATE;
import com.wanniu.game.farm.FarmMgr.Block;
import com.wanniu.game.item.ItemConfig;
import com.wanniu.game.item.NormalItem;
import com.wanniu.game.player.WNPlayer;
import com.wanniu.game.poes.FarmPO;

import pomelo.farm.FarmHandler.SowRequest;
import pomelo.farm.FarmHandler.SowResponse;

@GClientEvent("farm.farmHandler.sowRequest")
public class SowHandler extends PomeloRequest {
	@Override
	public PomeloResponse request() throws Exception {
		WNPlayer player = (WNPlayer) pak.getPlayer();
		SowRequest msg = SowRequest.parseFrom(pak.getRemaingBytes());
		int blockId = msg.getBlockId();
		String seedCode = msg.getSeedCode();
		return new PomeloResponse() {
			@Override
			protected void write() throws IOException {
				SowResponse.Builder res = SowResponse.newBuilder();
				FarmMgr farmMgr = player.getFarmMgr();
				FarmPO farmPO = farmMgr.myPO;

				Block block = farmPO.blockMap.get(blockId);

				// 地块不可播种
				if (block.blockState != BLOCK_STATE.OPENED) {
					res.setS2CCode(FAIL);
					res.setS2CMsg(LangService.getValue("FARM_BLOCK_CANNOT_SOW"));
					body.writeBytes(res.build().toByteArray());
					return;
				}

				// 种植等级不足
				PlantingCO plantingCO = ItemConfig.plantingMap.get(block.seedCode);
				if (farmPO.lv < plantingCO.plantLevel) {
					res.setS2CCode(FAIL);
					res.setS2CMsg(LangService.getValue("FARM_NOT_ENOUGH_LV"));
					body.writeBytes(res.build().toByteArray());
					return;
				}

				int seedNum = player.bag.findItemNumByCode(seedCode);

				// 种子不足
				if (seedNum < 1) {
					res.setS2CCode(FAIL);
					res.setS2CMsg(LangService.getValue("FARM_NO_SEED"));
					body.writeBytes(res.build().toByteArray());
					return;
				}

				boolean sowRes = farmMgr.sow(blockId, seedCode);

				if (sowRes)
					res.setS2CCode(OK);
				else {
					Out.error("不会走到这里");
					res.setS2CCode(FAIL);
					return;
				}

				body.writeBytes(res.build().toByteArray());
			}
		};
	}

}