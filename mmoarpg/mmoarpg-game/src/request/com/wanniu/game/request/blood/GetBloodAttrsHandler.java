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

import pomelo.area.BloodHandler;
import pomelo.area.BloodHandler.BloodAttr;
import pomelo.area.BloodHandler.GetBloodAttrsRequest;
import pomelo.area.BloodHandler.GetBloodAttrsResponse;

/**
 * 获取血脉属性
 * @author liyue
 *
 */
@GClientEvent("area.bloodHandler.getBloodAttrsRequest")
public class GetBloodAttrsHandler extends PomeloRequest {

	public PomeloResponse request() throws Exception {
		
		WNPlayer player = (WNPlayer) pak.getPlayer();
		
//		GetEquipedBloodsRequest req = GetEquipedBloodsRequest.parseFrom(pak.getRemaingBytes());
		if(!player.functionOpenManager.isOpen(Const.FunctionType.BloodLineage.getValue()))
		{
			OpenLvCO prop = FunctionOpenUtil.getPropByName(Const.FunctionType.BloodLineage.getValue());
			return new ErrorResponse(LangService.format(LangService.getValue("BLOOD_NOT_OPEN"), prop.openLv));
		}
//		Map<Integer, Integer> equipedBloods = player.bloodManager.bloodPO.equipedMap;
		Map<PlayerBtlData, Integer> attrs = player.bloodManager.calAllInfluence();
		
		return new PomeloResponse() {
			@Override
			protected void write() throws IOException {
				GetBloodAttrsResponse.Builder res = GetBloodAttrsResponse.newBuilder();
				
				
				
				//bloodEquiped
				for (Map.Entry<PlayerBtlData, Integer> entry : attrs.entrySet()) {
					BloodAttr.Builder ab=BloodAttr.newBuilder();
					ab.setId(entry.getKey().id);
					ab.setValue(entry.getValue());
					res.addBloodAttrs(ab.build());
				}
				res.setS2CCode(OK);
				
				
				body.writeBytes(res.build().toByteArray());
			}
		};
	}
}
