package com.wanniu.game.request.sale;

import java.io.IOException;
import java.util.List;

import com.wanniu.core.game.LangService;
import com.wanniu.core.game.entity.GClientEvent;
import com.wanniu.core.game.protocol.PomeloRequest;
import com.wanniu.core.game.protocol.PomeloResponse;
import com.wanniu.game.player.WNPlayer;
import com.wanniu.game.sale.SaleManager;

import pomelo.area.SaleHandler.AutoBuyItemByCodeRequest;
import pomelo.area.SaleHandler.AutoBuyItemByCodeResponse;

@GClientEvent("area.saleHandler.autoBuyItemByCodeRequest")
public class AutoBuyItemByCodeHandler extends PomeloRequest{

	@Override
	public PomeloResponse request() throws Exception {
		WNPlayer player = (WNPlayer)pak.getPlayer();
		AutoBuyItemByCodeRequest msg = AutoBuyItemByCodeRequest.parseFrom(pak.getRemaingBytes());
		List<Integer> typeIds = msg.getC2STypeIdList();
		String itemCode = msg.getC2SItemCode();
		int num = msg.getC2SNum();
		return new PomeloResponse() {
			@Override
			protected void write() throws IOException {
				AutoBuyItemByCodeResponse.Builder result = player.saleManager.handleAutoBuyItemByTypeCode(typeIds, itemCode, num);
				
			    if (result.getS2CCode() == SaleManager.ERR_CODE.ERR_CODE_OK.getValue()) {
//			        logger.debug('Player ', player.uid, ' autoBuyItemByCodeRequest: ', JSON.stringify({s2c_code: consts.CODE.OK}));
			        result.setS2CCode(OK);
			        body.writeBytes(result.build().toByteArray());
			        return;
			    } else if (result.getS2CCode() == SaleManager.ERR_CODE.ERR_CODE_GOLD_NOT_ENOUGH.getValue()) {
//			        logger.debug('Player ', player.uid, ' autoBuyItemByCodeRequest: ', JSON.stringify({s2c_code: consts.CODE.OK, s2c_msg: strList.GOLD_NOT_ENOUGH, s2c_notEnoughGold:1, s2c_needGold: result.needMoney}));
			    	result.setS2CCode(OK);
			    	result.setS2CMsg(LangService.getValue("GOLD_NOT_ENOUGH"));
			    	result.setS2CNotEnoughGold(1);
			    	int needValue = result.getS2CNeedGold();
			    	result.setS2CNeedGold(needValue);
			    	body.writeBytes(result.build().toByteArray());
				    return;
			    }
			    result.setS2CCode(OK);
			    body.writeBytes(result.build().toByteArray());
		        return;				
			}
		};
	}

}
