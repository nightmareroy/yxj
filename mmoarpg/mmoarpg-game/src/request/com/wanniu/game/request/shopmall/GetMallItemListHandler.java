package com.wanniu.game.request.shopmall;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import com.wanniu.core.game.LangService;
import com.wanniu.core.game.entity.GClientEvent;
import com.wanniu.core.game.entity.GPlayer;
import com.wanniu.core.game.protocol.PomeloRequest;
import com.wanniu.core.game.protocol.PomeloResponse;
import com.wanniu.game.GWorld;
import com.wanniu.game.common.Const;
import com.wanniu.game.common.msg.ErrorResponse;
import com.wanniu.game.data.GameData;
import com.wanniu.game.data.ShopMallConfigCO;
import com.wanniu.game.player.GlobalConfig;
import com.wanniu.game.player.WNPlayer;

import pomelo.area.ShopMallHandler.GetMallItemListRequest;
import pomelo.area.ShopMallHandler.GetMallItemListResponse;
import pomelo.area.ShopMallHandler.MallItem;

@GClientEvent("area.shopMallHandler.getMallItemListRequest")
public class GetMallItemListHandler extends PomeloRequest {

	@Override
	public PomeloResponse request() throws Exception {
		GetMallItemListRequest req = GetMallItemListRequest.parseFrom(pak.getRemaingBytes());
		GPlayer player = pak.getPlayer();
		WNPlayer wPlayer = (WNPlayer)player;

	    int itemType = req.getC2SItemType();
	    
	    
	    
	    ShopMallConfigCO shopMallConfigCO=GameData.ShopMallConfigs.get(itemType);
	    if(shopMallConfigCO==null)
	    {
	    	return new ErrorResponse(LangService.getValue("PARAM_ERROR"));
	    }

	    
//	    int limitType = .;
//	    if(moneyType == Const.SHOP_MALL_CONSUME_TYPE.TICKET.getValue()){
//	        limitType = Const.SHOP_MALL_ITEM_TYPE.TICKET_LIMIT.getValue();
//	    }
	    
//	    switch (moneyType) {
//		case 1:
//			
//			break;
//
//		default:
//			break;
//		}
	    
	    
	    
		return new PomeloResponse(){
			@Override
			protected void write() throws IOException {
				GetMallItemListResponse.Builder res = GetMallItemListResponse.newBuilder();

				ArrayList<MallItem.Builder> items = wPlayer.shopMallManager.getMallItemList(itemType);
			    int endTime = 0;
			    
			    if(wPlayer.shopMallManager.isOpenOfMallTab(itemType)){
			    	Date now = new Date();
			    	Calendar c = Calendar.getInstance();
			    	c.setTime(now);
			    	int nowHour = c.get(Calendar.HOUR_OF_DAY);
			    	int onSaleTime = 1;
			        int shelfTime = 1;
			        if(shopMallConfigCO.consumeType == Const.SHOP_MALL_CONSUME_TYPE.DIAMOND.getValue()){
			        	onSaleTime = GlobalConfig.Shop_OnSaleTime_Diamond;
				        shelfTime = GlobalConfig.Shop_ShelfTime_Diamond;
			        }else{
			        	onSaleTime = GlobalConfig.Shop_OnSaleTime_Ticket;
				        shelfTime = GlobalConfig.Shop_ShelfTime_Ticket;
			        }
			        if(nowHour >= onSaleTime && nowHour < shelfTime){
			        	c.set(Calendar.HOUR_OF_DAY, shelfTime);
			        	c.set(Calendar.MINUTE, 0);
			        	c.set(Calendar.SECOND, 0);
			            endTime = (int)Math.ceil(now.getTime()/1000);
			        }
			    }
			    else{
			    	endTime = -1;
			    }
			    res.setS2CCode(OK);
			    res.setS2CEndTime(endTime);
			    for(int i = 0;i<items.size();i++){
			    	res.addS2CItems(items.get(i).build());
			    }
			    body.writeBytes(res.build().toByteArray());
			}
		};
	}
}
