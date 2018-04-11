package com.wanniu.game.request.intergalmall;

import java.io.IOException;
import java.util.List;

import com.wanniu.core.game.entity.GClientEvent;
import com.wanniu.core.game.entity.GPlayer;
import com.wanniu.core.game.protocol.PomeloRequest;
import com.wanniu.core.game.protocol.PomeloResponse;
import com.wanniu.game.player.WNPlayer;

import pomelo.area.IntergalMallHandler.GetIntergalMallListRequest;
import pomelo.area.IntergalMallHandler.GetIntergalMallListResponse;
import pomelo.area.IntergalMallHandler.IntergalMallTab;

/**
 * 获取积分商城数据
 * @author Yangzz
 *
 */
@GClientEvent("area.intergalMallHandler.getMallScoreItemListRequest")
public class GetIntergalMallListHandler extends PomeloRequest {

	@Override
	public PomeloResponse request() throws Exception {
		GPlayer player = pak.getPlayer();
		WNPlayer wPlayer = (WNPlayer)player;
		
		GetIntergalMallListRequest req = GetIntergalMallListRequest.parseFrom(pak.getRemaingBytes());
		
		int shopType = req.getC2SType();
		
		return new PomeloResponse(){
			@Override
			protected void write() throws IOException {
				GetIntergalMallListResponse.Builder res = GetIntergalMallListResponse.newBuilder();
				
				List<IntergalMallTab> tabItems = wPlayer.getIntergalMallManager().getIntergalMallItemList(shopType);
				res.setS2CCode(OK);
				res.addAllS2CTabitems(tabItems);
				body.writeBytes(res.build().toByteArray());
			}
		};
	}
}
