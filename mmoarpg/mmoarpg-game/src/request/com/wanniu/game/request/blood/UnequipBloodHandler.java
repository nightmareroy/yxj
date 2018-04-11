package com.wanniu.game.request.blood;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import com.wanniu.core.game.LangService;
import com.wanniu.core.game.entity.GClientEvent;
import com.wanniu.core.game.protocol.PomeloRequest;
import com.wanniu.core.game.protocol.PomeloResponse;
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


import pomelo.area.BloodHandler.UnequipBloodRequest;
import pomelo.area.BloodHandler.UnequipBloodResponse;

/**
 * 卸载血脉
 * @author liyue
 *
 */
@GClientEvent("area.bloodHandler.unequipBloodRequest")
public class UnequipBloodHandler extends PomeloRequest {

	public PomeloResponse request() throws Exception {
		
		WNPlayer player = (WNPlayer) pak.getPlayer();
		
		if(!player.functionOpenManager.isOpen(Const.FunctionType.BloodLineage.getValue()))
		{
			OpenLvCO prop = FunctionOpenUtil.getPropByName(Const.FunctionType.BloodLineage.getValue());
			return new ErrorResponse(LangService.format("BLOOD_NOT_OPEN", prop.openLv));
		}
		
		UnequipBloodRequest req = UnequipBloodRequest.parseFrom(pak.getRemaingBytes());
		int sortId=req.getSortId();
		
		boolean result = player.bloodManager.unequipBlood(sortId);
		if(!result)
		{
			return new ErrorResponse(LangService.getValue("BLOOD_UNEQUIP_FAIL"));
		}
		
		return new PomeloResponse() {
			@Override
			protected void write() throws IOException {
				UnequipBloodResponse.Builder res = UnequipBloodResponse.newBuilder();
				res.setS2CCode(OK);
				
				
				body.writeBytes(res.build().toByteArray());
			}
		};
	}
}
