package com.wanniu.game.request.farm;

import java.io.IOException;

import com.wanniu.core.game.LangService;
import com.wanniu.core.game.entity.GClientEvent;
import com.wanniu.core.game.protocol.PomeloRequest;
import com.wanniu.core.game.protocol.PomeloResponse;
import com.wanniu.core.logfs.Out;
import com.wanniu.game.data.GameData;
import com.wanniu.game.data.GetLandCO;
import com.wanniu.game.farm.FarmMgr;
import com.wanniu.game.farm.FarmMgr.BLOCK_STATE;
import com.wanniu.game.farm.FarmMgr.Block;
import com.wanniu.game.farm.FarmMgr.OPEN_BLOCK;
import com.wanniu.game.player.WNPlayer;
import com.wanniu.game.poes.FarmPO;

import pomelo.farm.FarmHandler.OpenRequest;
import pomelo.farm.FarmHandler.OpenResponse;

@GClientEvent("farm.farmHandler.openRequest")
public class OpenHandler extends PomeloRequest {
	@Override
	public PomeloResponse request() throws Exception {
		WNPlayer player = (WNPlayer) pak.getPlayer();
		OpenRequest msg = OpenRequest.parseFrom(pak.getRemaingBytes());
		int blockId=msg.getBlockId();
		return new PomeloResponse() {
			@Override
			protected void write() throws IOException {
				OpenResponse.Builder res = OpenResponse.newBuilder();
				FarmMgr farmMgr = player.getFarmMgr();
				FarmPO farmPO=farmMgr.myPO;
				
				GetLandCO getLandCO = GameData.GetLands.get(blockId);
				Block block=farmPO.blockMap.get(blockId);
				
				//只有元宝开启需要手动开启
				if(getLandCO.getType!=OPEN_BLOCK.DIAMOND.value)
				{
					res.setS2CCode(FAIL);
					res.setS2CMsg(LangService.getValue("FARM_NO_NEED_TO_OPEN"));
					body.writeBytes(res.build().toByteArray());
					return;
				}
				
				// 元宝不足
				if (!player.moneyManager.enoughDiamond(getLandCO.value)) {
					res.setS2CCode(FAIL);
					res.setS2CMsg(String.format(LangService.getValue("FARM_DIAMOND_NEED"), getLandCO.value));
					body.writeBytes(res.build().toByteArray());
					return;
				}
				
				//地块已开垦
				if (block.blockState!=BLOCK_STATE.CLOSED) {
					res.setS2CCode(FAIL);
					res.setS2CMsg(LangService.getValue("FARM_BLOCK_OPENED"));
					body.writeBytes(res.build().toByteArray());
					return;
				}
				
				boolean openRes=farmMgr.open(blockId);

				if(openRes)
					res.setS2CCode(OK);
				else
				{
					Out.error("不会走到这里");
					res.setS2CCode(FAIL);
					return;
				}
				
				body.writeBytes(res.build().toByteArray());
			}
		};
	}

}