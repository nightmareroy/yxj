package com.wanniu.game.request.blood;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import com.wanniu.core.game.LangService;
import com.wanniu.core.game.entity.GClientEvent;
import com.wanniu.core.game.protocol.PomeloRequest;
import com.wanniu.core.game.protocol.PomeloResponse;
import com.wanniu.core.logfs.Out;
import com.wanniu.game.bag.BagUtil;
import com.wanniu.game.bag.WNBag;
import com.wanniu.game.blood.BloodManager;
import com.wanniu.game.common.Const;
import com.wanniu.game.common.Const.PlayerBtlData;
import com.wanniu.game.common.ConstsTR;
import com.wanniu.game.common.msg.ErrorResponse;
import com.wanniu.game.data.BloodListCO;
import com.wanniu.game.data.GameData;
import com.wanniu.game.data.OpenLvCO;
import com.wanniu.game.functionOpen.FunctionOpenUtil;
import com.wanniu.game.item.NormalItem;
import com.wanniu.game.item.po.PlayerItemPO;
import com.wanniu.game.player.WNPlayer;
import com.wanniu.game.poes.BloodPO;
import com.wanniu.redis.PlayerPOManager;

import pomelo.area.BloodHandler.GetEquipedBloodsRequest;
import pomelo.area.BloodHandler.GetEquipedBloodsResponse;

/**
 * 获取血脉信息
 * @author liyue
 *
 */
@GClientEvent("area.bloodHandler.getEquipedBloodsRequest")
public class GetEquipedBloodsHandler extends PomeloRequest {

	public PomeloResponse request() throws Exception {
		
		WNPlayer player = (WNPlayer) pak.getPlayer();
		
		GetEquipedBloodsRequest req = GetEquipedBloodsRequest.parseFrom(pak.getRemaingBytes());
		String playerId = req.getPlayerId();
		
		if(!player.functionOpenManager.isOpen(Const.FunctionType.BloodLineage.getValue()))
		{
			OpenLvCO prop = FunctionOpenUtil.getPropByName(Const.FunctionType.BloodLineage.getValue());
			return new ErrorResponse(LangService.format("BLOOD_NOT_OPEN", prop.openLv));
		}
		
		BloodPO bloodPO=PlayerPOManager.findPO(ConstsTR.player_blood, playerId, BloodPO.class);
		if(bloodPO==null) {
			return new ErrorResponse(LangService.getValue("PLAYER_NULL"));
		}
		
		Map<Integer, Integer> equipedBloods = bloodPO.equipedMap;
		
		return new PomeloResponse() {
			@Override
			protected void write() throws IOException {
				GetEquipedBloodsResponse.Builder res = GetEquipedBloodsResponse.newBuilder();
				
				
				
				//bloodEquiped
				for (int bloodId : equipedBloods.values()) {

					
					
					res.addBloodIds(bloodId);
				}
				res.setS2CCode(OK);
				
				
				body.writeBytes(res.build().toByteArray());
			}
		};
	}
}
