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

import pomelo.area.FashionHandler.GetFashionsResponse;
import pomelo.area.PlayerHandler.SuperScriptType;

/**
 * 获取已有时装列表
 * 
 * @author Liyue
 *
 */
@GClientEvent("area.fashionHandler.getFashionsRequest")
public class GetFashionsHandler extends PomeloRequest {

	public PomeloResponse request() throws Exception {

		WNPlayer player = (WNPlayer) pak.getPlayer();

		return new PomeloResponse() {
			@Override
			protected void write() throws IOException {
				GetFashionsResponse.Builder res = GetFashionsResponse.newBuilder();

				for (Object[] temp_fashion : player.playerBasePO.fashions_get) {
					FashionExt fashionExt = GameData.Fashions.get(temp_fashion[0]);
					switch (Const.FASHION_TYPE.valueOf(fashionExt.type)) {
					case WEPON:
						res.addCode1((String)temp_fashion[0]);
						if(!(boolean)temp_fashion[1]) {
							res.addFlagcode1((String)temp_fashion[0]);
						}
						break;
					case CLOTH:
						res.addCode2((String)temp_fashion[0]);
						if(!(boolean)temp_fashion[1]) {
							res.addFlagcode2((String)temp_fashion[0]);
						}
						break;
					case WING:
						res.addCode3((String)temp_fashion[0]);
						if(!(boolean)temp_fashion[1]) {
							res.addFlagcode3((String)temp_fashion[0]);
						}
						break;
					}
					
				}
				String equipedCode1=player.playerBasePO.fashions_equiped.get(Const.FASHION_TYPE.WEPON.value);
				if(equipedCode1!=null)
				{
					res.setEquipedCode1(equipedCode1);
				}
				String equipedCode2=player.playerBasePO.fashions_equiped.get(Const.FASHION_TYPE.CLOTH.value);
				if(equipedCode2!=null)
				{
					res.setEquipedCode2(equipedCode2);
				}
				String equipedCode3=player.playerBasePO.fashions_equiped.get(Const.FASHION_TYPE.WING.value);
				if(equipedCode3!=null)
				{
					res.setEquipedCode3(equipedCode3);
				}
				
//				player.playerBasePO.fashion_get_spot=0;
				List<SuperScriptType> ls = player.fashionManager.getSuperScriptList();
				player.updateSuperScriptList(ls); // 红点推送给玩家

				res.setS2CCode(OK);
				body.writeBytes(res.build().toByteArray());
				return;
			}
		};
	}
}