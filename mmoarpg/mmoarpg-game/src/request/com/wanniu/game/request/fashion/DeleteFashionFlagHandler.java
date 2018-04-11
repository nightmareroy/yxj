package com.wanniu.game.request.fashion;

import java.io.IOException;
import java.util.List;

import com.wanniu.core.game.entity.GClientEvent;
import com.wanniu.core.game.protocol.PomeloRequest;
import com.wanniu.core.game.protocol.PomeloResponse;
import com.wanniu.game.common.Const;
import com.wanniu.game.data.GameData;
import com.wanniu.game.data.ext.FashionExt;
import com.wanniu.game.player.WNPlayer;

import pomelo.area.FashionHandler.DeleteFashionFlagRequest;
import pomelo.area.FashionHandler.DeleteFashionFlagResponse;
import pomelo.area.PlayerHandler.SuperScriptType;

/**
 * 删除时装红点
 * 
 * @author Liyue
 *
 */
@GClientEvent("area.fashionHandler.deleteFashionFlagRequest")
public class DeleteFashionFlagHandler extends PomeloRequest {

	public PomeloResponse request() throws Exception {

		WNPlayer player = (WNPlayer) pak.getPlayer();
		
		DeleteFashionFlagRequest request=DeleteFashionFlagRequest.parseFrom(pak.getRemaingBytes());
		String code=request.getCode();
		player.fashionManager.checkFashion(code);
		
		return new PomeloResponse() {
			@Override
			protected void write() throws IOException {
				DeleteFashionFlagResponse.Builder res = DeleteFashionFlagResponse.newBuilder();
				res.setS2CCode(OK);
				body.writeBytes(res.build().toByteArray());
				return;
			}
		};
	}
}