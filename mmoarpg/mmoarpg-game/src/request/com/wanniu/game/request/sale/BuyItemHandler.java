package com.wanniu.game.request.sale;

import java.io.IOException;

import com.wanniu.core.game.LangService;
import com.wanniu.core.game.entity.GClientEvent;
import com.wanniu.core.game.protocol.PomeloRequest;
import com.wanniu.core.game.protocol.PomeloResponse;
import com.wanniu.game.player.WNPlayer;
import com.wanniu.game.sale.SaleManager;

import pomelo.area.SaleHandler.BuyItemRequest;
import pomelo.area.SaleHandler.BuyItemResponse;

@GClientEvent("area.saleHandler.buyItemRequest")
public class BuyItemHandler extends PomeloRequest{

	@Override
	public PomeloResponse request() throws Exception {
		WNPlayer player = (WNPlayer)pak.getPlayer();
		BuyItemRequest msg = BuyItemRequest.parseFrom(pak.getRemaingBytes());
		int typeId = msg.getC2STypeId();
	    int itemId = msg.getC2SItemId();
	    int num = msg.getC2SNum();
		return new PomeloResponse() {
			@Override
			protected void write() throws IOException {
				BuyItemResponse.Builder res = BuyItemResponse.newBuilder();
				int result = player.saleManager.handleBuyItem(typeId, itemId, num,false);
			    if (result == SaleManager.ERR_CODE.ERR_CODE_OK.getValue()) {
			    	res.setS2CCode(OK);
			    } else if (result == SaleManager.ERR_CODE.ERR_CODE_ITEM_NOT_EXIST.getValue()) {
			    	res.setS2CCode(FAIL);
			    	res.setS2CMsg(LangService.getValue("SALE_ITEM_NOT_EXIST"));
			    } else if (result == SaleManager.ERR_CODE.ERR_CODE_GOLD_NOT_ENOUGH.getValue()) {
			    	res.setS2CCode(FAIL);
			    	res.setS2CMsg(LangService.getValue("GOLD_NOT_ENOUGH"));
			    } else if (result == SaleManager.ERR_CODE.ERR_CODE_TICKET_NOT_ENOUGH.getValue()) {
			    	res.setS2CCode(FAIL);
			    	res.setS2CMsg(LangService.getValue("TICKET_NOT_ENOUGH"));
			    } else if (result == SaleManager.ERR_CODE.ERR_CODE_DIAMAND_NOT_ENOUGH.getValue()) {
			    	res.setS2CCode(FAIL);
			    	res.setS2CMsg("");
			    } else if (result == SaleManager.ERR_CODE.ERR_CODE_BAG_NOT_ENOUGH_POS.getValue()) {
//			        Out.debug("Player ", player.uid, " buyItemRequest: ", JSON.stringify({s2c_code: Const.CODE.FAIL, s2c_msg: strList.BAG_NOT_ENOUGH_POS}));
			    	res.setS2CCode(FAIL);
			    	res.setS2CMsg(LangService.getValue("BAG_NOT_ENOUGH_POS"));
			    } else {
			    	res.setS2CCode(FAIL);
			    	res.setS2CMsg(LangService.getValue("SOMETHING_ERR"));
			    }
			    body.writeBytes(res.build().toByteArray());
			}
		};
	}

}
