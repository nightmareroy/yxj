package com.wanniu.game.request.sale;

import java.io.IOException;
import java.util.List;

import com.wanniu.core.game.LangService;
import com.wanniu.core.game.entity.GClientEvent;
import com.wanniu.core.game.protocol.PomeloRequest;
import com.wanniu.core.game.protocol.PomeloResponse;
import com.wanniu.game.player.WNPlayer;
import com.wanniu.game.sale.SaleManager;

import pomelo.area.SaleHandler.SellGrid;
import pomelo.area.SaleHandler.SellItemsRequest;
import pomelo.area.SaleHandler.SellItemsResponse;

@GClientEvent("area.saleHandler.sellItemsRequest")
public class SellItemsHandler extends PomeloRequest{

	@Override
	public PomeloResponse request() throws Exception {
		WNPlayer player = (WNPlayer)pak.getPlayer();
		SellItemsRequest msg = SellItemsRequest.parseFrom(pak.getRemaingBytes());
		List<SellGrid> sellGrids = msg.getC2SSellGridsList();
		return new PomeloResponse() {
			@Override
			protected void write() throws IOException {
				SellItemsResponse.Builder res = SellItemsResponse.newBuilder();
				int result = player.saleManager.handleSellItems(sellGrids);

				if (result == SaleManager.ERR_CODE.ERR_CODE_OK.getValue()) {
//			        Out.debug("Player ", player.uid, " sellItemsRequest: ", JSON.stringify({s2c_code: consts.CODE.OK}));
					res.setS2CCode(OK);
			    } else if (result == SaleManager.ERR_CODE.ERR_CODE_SELL_ITEMS_EMPTY.getValue()) {
//			        Out.debug("Player ", player.uid, " sellItemsRequest: ", JSON.stringify({s2c_code: consts.CODE.FAIL, s2c_msg: strList.SALE_SELL_ITEMS_EMPTY}));
			        res.setS2CCode(FAIL);
			        res.setS2CMsg(LangService.getValue("SALE_SELL_ITEMS_EMPTY"));
			    } else if (result == SaleManager.ERR_CODE.ERR_CODE_ITEM_NOT_EXIST.getValue()) {
//			        Out.debug("Player ", player.uid, " sellItemsRequest: ", JSON.stringify({s2c_code: consts.CODE.FAIL, s2c_msg: strList.SALE_ITEM_NOT_EXIST}));
			    	res.setS2CCode(FAIL);
			        res.setS2CMsg(LangService.getValue("SALE_ITEM_NOT_EXIST"));
			    } else if (result == SaleManager.ERR_CODE.ERR_CODE_ITEM_NOSELL.getValue()) {
//			        Out.debug("Player ", player.uid, " sellItemsRequest: ", JSON.stringify({s2c_code: consts.CODE.FAIL, s2c_msg: strList.SALE_ITEM_NOSELL}));
			    	res.setS2CCode(FAIL);
			        res.setS2CMsg(LangService.getValue("SALE_ITEM_NOSELL"));
			    } else if (result == SaleManager.ERR_CODE.ERR_CODE_PARAM_ERROR.getValue()) {
//			        Out.debug("Player ", player.uid, " sellItemsRequest: ", JSON.stringify({s2c_code: consts.CODE.FAIL, s2c_msg: strList.PARAM_ERROR}));
			    	res.setS2CCode(FAIL);
			        res.setS2CMsg(LangService.getValue("PARAM_ERROR"));
			    } else {
//			        Out.debug("Player ", player.uid, " sellItemsRequest: ", JSON.stringify({s2c_code: consts.CODE.FAIL, s2c_msg: strList.SOMETHING_ERR}));
			    	res.setS2CCode(FAIL);
			        res.setS2CMsg(LangService.getValue("SOMETHING_ERR"));
			    }
				body.writeBytes(res.build().toByteArray());
			}
		};
	}

}
