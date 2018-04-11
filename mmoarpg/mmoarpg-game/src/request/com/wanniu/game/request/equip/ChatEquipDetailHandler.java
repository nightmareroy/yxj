package com.wanniu.game.request.equip;

import java.io.IOException;

import com.alibaba.fastjson.JSON;
import com.wanniu.core.db.GCache;
import com.wanniu.core.game.LangService;
import com.wanniu.core.game.entity.GClientEvent;
import com.wanniu.core.game.protocol.PomeloRequest;
import com.wanniu.core.game.protocol.PomeloResponse;
import com.wanniu.core.util.StringUtil;
import com.wanniu.game.common.ConstsTR;
import com.wanniu.game.item.ItemUtil;
import com.wanniu.game.item.NormalItem;
import com.wanniu.game.item.po.PlayerItemPO;
import com.wanniu.game.player.WNPlayer;

import pomelo.area.EquipHandler.ChatEquipDetailRequest;
import pomelo.area.EquipHandler.ChatEquipDetailResponse;
import pomelo.item.ItemOuterClass.ItemDetail;

/**
 * 聊天获取装备详情
 * 
 * @author Yangzz
 *
 */
@GClientEvent("area.equipHandler.chatEquipDetailRequest")
public class ChatEquipDetailHandler extends PomeloRequest {

	public PomeloResponse request() throws Exception {

		WNPlayer player = (WNPlayer) pak.getPlayer();
		
		ChatEquipDetailRequest req = ChatEquipDetailRequest.parseFrom(pak.getRemaingBytes());
		String itemId = req.getC2SId();
		
		return new PomeloResponse() {
			@Override
			protected void write() throws IOException {
				ChatEquipDetailResponse.Builder res = ChatEquipDetailResponse.newBuilder();
				String showItem = GCache.get(ConstsTR.chat_item_tr.value + "/" + itemId);
				if (StringUtil.isNotEmpty(showItem)) {
					NormalItem item = ItemUtil.createItemByDbOpts(JSON.parseObject(showItem, PlayerItemPO.class));
					ItemDetail.Builder itemDetail = item.getItemDetail(player.playerBasePO);
					res.setS2CCode(OK);
					res.setS2CData(itemDetail);
				} else {
					res.setS2CCode(FAIL);
					res.setS2CMsg(LangService.getValue("LINK_HAS_FAILED"));
				}
				body.writeBytes(res.build().toByteArray());

			}
		};
	}
}