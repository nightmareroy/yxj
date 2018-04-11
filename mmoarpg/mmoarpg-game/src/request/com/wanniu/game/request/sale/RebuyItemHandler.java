package com.wanniu.game.request.sale;

import java.io.IOException;

import com.wanniu.core.game.LangService;
import com.wanniu.core.game.entity.GClientEvent;
import com.wanniu.core.game.protocol.PomeloRequest;
import com.wanniu.core.game.protocol.PomeloResponse;
import com.wanniu.game.player.WNPlayer;
import com.wanniu.game.sale.SaleManager;

import pomelo.area.SaleHandler.RebuyItemRequest;
import pomelo.area.SaleHandler.RebuyItemResponse;

@GClientEvent("area.saleHandler.rebuyItemRequest")
public class RebuyItemHandler extends PomeloRequest{

	@Override
	public PomeloResponse request() throws Exception {
		WNPlayer player = (WNPlayer)pak.getPlayer();
		RebuyItemRequest msg = RebuyItemRequest.parseFrom(pak.getRemaingBytes());
		int gridIndex = msg.getC2SGridIndex();
		int num = msg.getC2SNum();
		
		return new PomeloResponse() {
			@Override
			protected void write() throws IOException {
				RebuyItemResponse.Builder res = RebuyItemResponse.newBuilder();
				int result = player.saleManager.handleRebuyItem(gridIndex, num);

			    if (result == SaleManager.ERR_CODE.ERR_CODE_OK.getValue()) {
//			        Out.debug('Player ', player.uid, " rebuyItemRequest: ', JSON.stringify({s2c_code: consts.CODE.OK}));
			    	res.setS2CCode(OK);
			    } else if (result == SaleManager.ERR_CODE.ERR_CODE_ITEM_NOT_EXIST.getValue()) {
//			        Out.debug('Player ', player.uid, " rebuyItemRequest: ', JSON.stringify({s2c_code: consts.CODE.FAIL, s2c_msg: strList.SALE_ITEM_NOT_EXIST}));
			    	res.setS2CCode(FAIL);
			    	res.setS2CMsg(LangService.getValue("SALE_ITEM_NOT_EXIST"));
			    } else if (result == SaleManager.ERR_CODE.ERR_CODE_PARAM_ERROR.getValue()) {
//			        Out.debug('Player ', player.uid, " rebuyItemRequest: ', JSON.stringify({s2c_code: consts.CODE.FAIL, s2c_msg: strList.PARAM_ERROR}));
			    	res.setS2CCode(FAIL);
			    	res.setS2CMsg(LangService.getValue("PARAM_ERROR"));
			    } else if (result == SaleManager.ERR_CODE.ERR_CODE_GOLD_NOT_ENOUGH.getValue()) {
//			        Out.debug('Player ', player.uid, " rebuyItemRequest: ', JSON.stringify({s2c_code: consts.CODE.FAIL, s2c_msg: strList.GOLD_NOT_ENOUGH}));
			    	res.setS2CCode(FAIL);
			    	res.setS2CMsg(LangService.getValue("GOLD_NOT_ENOUGH"));
			    } else if (result == SaleManager.ERR_CODE.ERR_CODE_BAG_NOT_ENOUGH_POS.getValue()) {
//			        Out.debug('Player ', player.uid, " rebuyItemRequest: ', JSON.stringify({s2c_code: consts.CODE.FAIL, s2c_msg: strList.BAG_NOT_ENOUGH_POS}));
			    	res.setS2CCode(FAIL);
			    	res.setS2CMsg(LangService.getValue("BAG_NOT_ENOUGH_POS"));
			    } else {
//			        Out.debug('Player ', player.uid, " rebuyItemRequest: ', JSON.stringify({s2c_code: consts.CODE.FAIL, s2c_msg: strList.SOMETHING_ERR}));
			    	res.setS2CCode(FAIL);
			    	res.setS2CMsg(LangService.getValue("SOMETHING_ERR"));
			    }
			    body.writeBytes(res.build().toByteArray());
			}
		};
	}

}
