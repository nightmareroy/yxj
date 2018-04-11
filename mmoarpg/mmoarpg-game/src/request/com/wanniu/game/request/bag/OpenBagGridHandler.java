package com.wanniu.game.request.bag;

import java.io.IOException;

import com.wanniu.core.game.LangService;
import com.wanniu.core.game.entity.GClientEvent;
import com.wanniu.core.game.protocol.PomeloRequest;
import com.wanniu.core.game.protocol.PomeloResponse;
import com.wanniu.game.bag.BagUtil;
import com.wanniu.game.bag.WNBag;
import com.wanniu.game.common.Const;
import com.wanniu.game.player.WNPlayer;

import pomelo.area.BagHandler.OpenBagGridRequest;
import pomelo.area.BagHandler.OpenBagGridResponse;

/**
 * 背包开格子
 * @author Yangzz
 *
 */
@GClientEvent("area.bagHandler.openBagGridRequest")
public class OpenBagGridHandler extends PomeloRequest {

	public PomeloResponse request() throws Exception {
		
		WNPlayer player = (WNPlayer) pak.getPlayer();
		
		OpenBagGridRequest req = OpenBagGridRequest.parseFrom(pak.getRemaingBytes());
		int type_from = req.getC2SType();
		int num = req.getC2SNumber();
		
		return new PomeloResponse() {
			@Override
			protected void write() throws IOException {
				OpenBagGridResponse.Builder res = OpenBagGridResponse.newBuilder();

				if(num <= 0) {
					res.setS2CCode(FAIL);
					res.setS2CMsg(LangService.getValue("PARAM_ERROR"));
					body.writeBytes(res.build().toByteArray());
					return;
				}
				
				WNBag store = BagUtil.getStoreByType(player, type_from);
			    if(store == null){
			    	res.setS2CCode(FAIL);
					res.setS2CMsg(LangService.getValue("PARAM_ERROR"));
					body.writeBytes(res.build().toByteArray());
					return;
			    }
				
				int needDiamond = num * BagUtil.getGridPrice(type_from);
//			    if(!player.moneyManager.enoughDiamond(needDiamond)) {
				if(!player.moneyManager.enoughTicketAndDiamond(needDiamond)) {
			    	res.setS2CCode(PomeloRequest.FAIL);
			    	res.setS2CMsg(LangService.getValue("DIAMAND_NOT_ENOUGH"));
			    	body.writeBytes(res.build().toByteArray());
			    	return;
			    }
				
				if(store.openGrid(num)) {
//					player.moneyManager.costDiamond(needDiamond, Const.GOODS_CHANGE_TYPE.openbag);
					player.moneyManager.costTicketAndDiamond(needDiamond, Const.GOODS_CHANGE_TYPE.openbag);
				}
				
				res.setS2CCode(PomeloRequest.OK);
			    res.setS2CBagGridCount(store.bagPO.bagGridCount);
				body.writeBytes(res.build().toByteArray());
			}
		};
	}
}
