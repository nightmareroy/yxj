package com.wanniu.game.request.petNew;

import java.io.IOException;

import com.wanniu.core.game.entity.GClientEvent;
import com.wanniu.core.game.protocol.PomeloRequest;
import com.wanniu.core.game.protocol.PomeloResponse;
import com.wanniu.game.player.WNPlayer;

import pomelo.area.PetNewHandler.AddExpByItemRequest;
import pomelo.area.PetNewHandler.AddExpByItemResponse;

/**
 * 升级宠物请求.
 *
 * @author 小流氓(176543888@qq.com)
 */
@GClientEvent("area.petNewHandler.addExpByItemRequest")
public class AddExpByItemHandler extends PomeloRequest {

	@Override
	public PomeloResponse request() throws Exception {
		WNPlayer player = (WNPlayer) pak.getPlayer();
		AddExpByItemRequest req = AddExpByItemRequest.parseFrom(pak.getRemaingBytes());
		int id = req.getC2SId();
		String itemCode = req.getC2SItemCode();

		AddExpByItemResponse.Builder res = player.petNewManager.addExpByItem(id, itemCode, 1);

		return new PomeloResponse() {
			protected void write() throws IOException {
				body.writeBytes(res.build().toByteArray());
			}
		};
	}
}