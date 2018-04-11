package com.wanniu.game.request.fashion;

import java.io.IOException;
import java.util.List;

import com.wanniu.core.game.LangService;
import com.wanniu.core.game.entity.GClientEvent;
import com.wanniu.core.game.protocol.PomeloRequest;
import com.wanniu.core.game.protocol.PomeloResponse;
import com.wanniu.core.logfs.Out;
import com.wanniu.game.common.Const;
import com.wanniu.game.player.PlayerUtil;
import com.wanniu.game.player.WNPlayer;

import pomelo.Common.Avatar;
import pomelo.area.FashionHandler.EquipFashionRequest;
import pomelo.area.FashionHandler.EquipFashionResponse;

/**
 * 穿脱装备
 * 
 * @author Liyue
 *
 */
@GClientEvent("area.fashionHandler.equipFashionRequest")
public class EquipFashionHandler extends PomeloRequest {

	public PomeloResponse request() throws Exception {

		WNPlayer player = (WNPlayer) pak.getPlayer();

		EquipFashionRequest req = EquipFashionRequest.parseFrom(pak.getRemaingBytes());
		String code = req.getCode();
		boolean ison = req.getIson();


		return new PomeloResponse() {
			@Override
			protected void write() throws IOException {
				EquipFashionResponse.Builder res = EquipFashionResponse.newBuilder();

				boolean result = player.fashionManager.equipFashion(code, ison);
				
				if(!result)
				{
					res.setS2CCode(FAIL);
					res.setS2CMsg(LangService.getValue("FASHION_DONT_HAVE"));
				}
				res.setS2CCode(OK);

				List<Avatar> list = PlayerUtil.getBattlerServerAvatar(player, true);
				for (Avatar avatar : list) {
					if(avatar.getTag()==Const.AVATAR_TYPE.R_HAND_WEAPON.value)
					{
						res.setEquipedCode1(avatar);
					}
					if(avatar.getTag()==Const.AVATAR_TYPE.AVATAR_BODY.value)
					{
						res.setEquipedCode2(avatar);
					}
					if(avatar.getTag()==Const.AVATAR_TYPE.REAR_EQUIPMENT.value)
					{
						res.setEquipedCode3(avatar);
					}
				}

				
				
				body.writeBytes(res.build().toByteArray());
				return;
			}
		};
	}
}