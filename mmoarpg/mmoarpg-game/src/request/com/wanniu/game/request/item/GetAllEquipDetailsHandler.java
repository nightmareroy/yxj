package com.wanniu.game.request.item;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.wanniu.core.game.entity.GClientEvent;
import com.wanniu.core.game.protocol.PomeloRequest;
import com.wanniu.core.game.protocol.PomeloResponse;
import com.wanniu.game.bag.WNBag;
import com.wanniu.game.player.WNPlayer;

import pomelo.area.ItemHandler.GetAllEquipDetailsRequest;
import pomelo.area.ItemHandler.GetAllEquipDetailsResponse;
import pomelo.item.ItemOuterClass.ItemDetail;

/**
 * 获取所有装备详情
 * @author Yangzz
 *
 */
@GClientEvent("area.itemHandler.getAllEquipDetailsRequest")
public class GetAllEquipDetailsHandler extends PomeloRequest {

	public PomeloResponse request() throws Exception {
		
		WNPlayer player = (WNPlayer) pak.getPlayer();
		WNBag wnBag = player.getWnBag();
		
		GetAllEquipDetailsRequest req = GetAllEquipDetailsRequest.parseFrom(pak.getRemaingBytes());
		
		
		return new PomeloResponse() {
			@Override
			protected void write() throws IOException {
				GetAllEquipDetailsResponse.Builder res = GetAllEquipDetailsResponse.newBuilder();

				List<ItemDetail> data = new ArrayList<ItemDetail>();
			    data.addAll(wnBag.getAllEquipDetails4PayLoad());
			    data.addAll(player.equipManager.getAllEquipDetails4PayLoad());
			    data.addAll(player.wareHouse.getAllEquipDetails4PayLoad());
				
			    res.setS2CCode(OK);
			    res.addAllS2CItems(data);
				body.writeBytes(res.build().toByteArray());
			}
		};
	}
}
