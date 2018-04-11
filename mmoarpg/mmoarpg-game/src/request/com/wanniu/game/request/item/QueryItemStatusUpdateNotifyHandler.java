package com.wanniu.game.request.item;

import com.wanniu.core.game.entity.GClientEvent;
import com.wanniu.core.game.protocol.PomeloRequest;
import com.wanniu.core.game.protocol.PomeloResponse;
import com.wanniu.game.bag.WNBag;
import com.wanniu.game.player.WNPlayer;

import pomelo.area.ItemHandler.QueryItemStatusUpdateNotify;

/**
 * 获取所有装备详情
 * @author Yangzz
 *
 */
@GClientEvent("area.itemHandler.queryItemStatusUpdateNotify")
public class QueryItemStatusUpdateNotifyHandler extends PomeloRequest {

	public PomeloResponse request() throws Exception {
		
		WNPlayer player = (WNPlayer) pak.getPlayer();
		WNBag wnBag = player.getWnBag();
		
		QueryItemStatusUpdateNotify req = QueryItemStatusUpdateNotify.parseFrom(pak.getRemaingBytes());
		wnBag.ItemStatusUpdate(req.getIndex());
		
		return null;
	}
}
