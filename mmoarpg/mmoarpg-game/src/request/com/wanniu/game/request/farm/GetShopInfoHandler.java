package com.wanniu.game.request.farm;

import java.io.IOException;
import java.util.List;

import com.wanniu.core.game.entity.GClientEvent;
import com.wanniu.core.game.protocol.PomeloRequest;
import com.wanniu.core.game.protocol.PomeloResponse;
import com.wanniu.game.bag.WNBag.SimpleItemInfo;
import com.wanniu.game.data.GameData;
import com.wanniu.game.data.PlantShopCO;
import com.wanniu.game.farm.FarmMgr;
import com.wanniu.game.item.ItemUtil;
import com.wanniu.game.player.WNPlayer;

import pomelo.Common.KeyValueStruct;
import pomelo.farm.Farm.ShopItemInfo;
import pomelo.farm.FarmHandler.GetShopInfoRequest;
import pomelo.farm.FarmHandler.GetShopInfoResponse;

@GClientEvent("farm.farmHandler.getShopInfoRequest")
public class GetShopInfoHandler extends PomeloRequest {
	@Override
	public PomeloResponse request() throws Exception {
		WNPlayer player = (WNPlayer) pak.getPlayer();
		GetShopInfoRequest msg = GetShopInfoRequest.parseFrom(pak.getRemaingBytes());
		return new PomeloResponse() {
			@Override
			protected void write() throws IOException {
				GetShopInfoResponse.Builder res = GetShopInfoResponse.newBuilder();
				FarmMgr farmMgr = player.farmMgr;
				for (PlantShopCO plantShopCO : GameData.PlantShops.values()) {
					ShopItemInfo.Builder sb=ShopItemInfo.newBuilder();
					sb.setItemId(plantShopCO.iD);
					if(farmMgr.myPO.shopToday.containsKey(plantShopCO.iD))
					{
						sb.setChangeNumCurrent(farmMgr.myPO.shopToday.get(plantShopCO.iD));
					}
					else
					{
						sb.setChangeNumCurrent(0);
					}
					sb.setChangeNumMax(plantShopCO.changeNum);
					List<SimpleItemInfo> simpleItemInfos=ItemUtil.parseString(plantShopCO.parameter);
					for (SimpleItemInfo simpleItemInfo : simpleItemInfos) {
						KeyValueStruct.Builder kvBuilder=KeyValueStruct.newBuilder();
						kvBuilder.setKey(simpleItemInfo.itemCode);
						kvBuilder.setValue(String.valueOf(simpleItemInfo.itemNum));
						sb.addNeed(kvBuilder.build());
					}
					
					simpleItemInfos=ItemUtil.parseString(plantShopCO.itemCode);
					for (SimpleItemInfo simpleItemInfo : simpleItemInfos) {
						KeyValueStruct.Builder kvBuilder=KeyValueStruct.newBuilder();
						kvBuilder.setKey(simpleItemInfo.itemCode);
						kvBuilder.setValue(String.valueOf(simpleItemInfo.itemNum));
						sb.addAward(kvBuilder.build());
					}
					
				}
				
				res.setS2CCode(OK);
				body.writeBytes(res.build().toByteArray());
			}
		};
	}

}