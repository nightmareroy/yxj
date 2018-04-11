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
import com.wanniu.game.common.msg.ErrorResponse;
import com.wanniu.game.data.BloodListCO;
import com.wanniu.game.data.GameData;
import com.wanniu.game.data.OpenLvCO;
import com.wanniu.game.functionOpen.FunctionOpenUtil;
import com.wanniu.game.item.NormalItem;
import com.wanniu.game.item.po.PlayerItemPO;
import com.wanniu.game.player.WNPlayer;


import pomelo.area.BloodHandler.EquipBloodRequest;
import pomelo.area.BloodHandler.EquipBloodResponse;

/**
 * 装备血脉
 * @author liyue
 *
 */
@GClientEvent("area.bloodHandler.equipBloodRequest")
public class EquipBloodHandler extends PomeloRequest {

	public PomeloResponse request() throws Exception {
		
		WNPlayer player = (WNPlayer) pak.getPlayer();
		
		EquipBloodRequest req = EquipBloodRequest.parseFrom(pak.getRemaingBytes());
		String itemId=req.getItemId();
		
		if(!player.functionOpenManager.isOpen(Const.FunctionType.BloodLineage.getValue()))
		{
			OpenLvCO prop = FunctionOpenUtil.getPropByName(Const.FunctionType.BloodLineage.getValue());
			return new ErrorResponse(LangService.format("BLOOD_NOT_OPEN", prop.openLv));
		}
		
		int result = player.bloodManager.equipBlood(itemId);
		switch (result) {
		case 0:
			break;
		case 1:
			return new ErrorResponse(LangService.getValue("BLOOD_NOT_HAVE"));
		case 2:
			return new ErrorResponse(LangService.getValue("BLOOD_NOT_BLOOD"));
		case 3:
			return new ErrorResponse(LangService.getValue("BLOOD_PARAM_ERROR"));
		default:
			return new ErrorResponse(LangService.getValue("BLOOD_PARAM_ERROR"));
		}
		return new PomeloResponse() {
			@Override
			protected void write() throws IOException {
				EquipBloodResponse.Builder res = EquipBloodResponse.newBuilder();
				res.setS2CCode(OK);
				
				
				body.writeBytes(res.build().toByteArray());
			}
		};
	}
}
