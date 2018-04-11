package com.wanniu.game.request.equip;

import java.io.IOException;

import com.wanniu.core.game.LangService;
import com.wanniu.core.game.entity.GClientEvent;
import com.wanniu.core.game.protocol.PomeloRequest;
import com.wanniu.core.game.protocol.PomeloResponse;
import com.wanniu.game.bag.WNBag;
import com.wanniu.game.common.Const;
import com.wanniu.game.data.ext.EnchantExt;
import com.wanniu.game.equip.EquipManager;
import com.wanniu.game.equip.EquipUtil;
import com.wanniu.game.player.WNPlayer;
import com.wanniu.game.poes.PlayerBasePO.EquipStrengthPos;

import pomelo.area.EquipHandler.EquipStrengthenRequest;
import pomelo.area.EquipHandler.EquipStrengthenResponse;

/**
 * 装备强化
 * 
 * @author Yangzz
 *
 */
@GClientEvent("area.equipHandler.equipStrengthenRequest")
public class EquipStrengthenHandler extends PomeloRequest {

	public PomeloResponse request() throws Exception {

		WNPlayer player = (WNPlayer) pak.getPlayer();
		WNBag wnBag = player.getWnBag();
		EquipManager wnEquip = player.equipManager;

		EquipStrengthenRequest req = EquipStrengthenRequest.parseFrom(pak.getRemaingBytes());
		int pos = req.getC2SPos();

		return new PomeloResponse() {
			@Override
			protected void write() throws IOException {
				EquipStrengthenResponse.Builder res = EquipStrengthenResponse.newBuilder();

				if (!player.functionOpenManager.isOpen(Const.FunctionType.STRENGTHEN.getValue())) {
					res.setS2CCode(Const.CODE.FAIL);
					res.setS2CMsg(LangService.getValue("FUNC_SET_PLAYED_NOT_OPEN"));
					body.writeBytes(res.build().toByteArray());
					return;
				}
				
				EquipStrengthPos strengthPos = player.equipManager.strengthPos.get(pos);
			    if(strengthPos == null){
			    	res.setS2CCode(FAIL);
			    	res.setS2CMsg(LangService.getValue("EQUIP_POS_NOT_EXIST"));
					body.writeBytes(res.build().toByteArray());
			        return;
			    }

			    if(EquipUtil.isMaxStrengthLevel(strengthPos.enSection, strengthPos.enLevel)){
			    	res.setS2CCode(FAIL);
			    	res.setS2CMsg(LangService.getValue("EQUIP_MAX_STRENGTH_LEVEL"));
					body.writeBytes(res.build().toByteArray());
			        return;
			    }

			    EnchantExt prop = EquipUtil.getStrengthConfig(strengthPos.enSection, strengthPos.enLevel);
			    if(prop == null){
			    	res.setS2CCode(FAIL);
			    	res.setS2CMsg(LangService.getValue("SOMETHING_ERR"));
					body.writeBytes(res.build().toByteArray());
			        return;
			    }

			    if(player.moneyManager.getGold() < prop.costGold){
			    	res.setS2CCode(FAIL);
			    	res.setS2CMsg(LangService.getValue("GOLD_NOT_ENOUGH"));
					body.writeBytes(res.build().toByteArray());
			        return;
			    }
			    
			    // 检测材料是否足够
				for (String mateCode : prop.mates.keySet()) {
					int haveNum = wnBag.findItemNumByCode(mateCode);
					int needNum = prop.mates.getIntValue(mateCode);
					if (haveNum < needNum) {
						res.setS2CCode(FAIL);
						res.setS2CMsg(LangService.getValue("ITEM_NOT_ENOUGH"));
						body.writeBytes(res.build().toByteArray());
						return;
					}
				}
				
				// 扣除材料
				for (String mateCode : prop.mates.keySet()) {
					int needNum = prop.mates.getIntValue(mateCode);
					wnBag.discardItem(mateCode, needNum, Const.GOODS_CHANGE_TYPE.equipstrengh, null, false, false);
				}
				
			    player.moneyManager.costGold(prop.costGold, Const.GOODS_CHANGE_TYPE.equipstrengh);

			    Object[] result = wnEquip.equipStrengthen(pos);
			    
			    res.setS2CStrengthenData(EquipUtil.getStrengthInfo(player, pos));
			    if((boolean) result[0]){
			    	res.setS2CSuccess(1);
			    }else{
			    	res.setS2CSuccess(1);
			    }

				res.setS2CCode(OK);
				body.writeBytes(res.build().toByteArray());
			}
		};
	}
}