package com.wanniu.game.request.equip;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.wanniu.core.game.LangService;
import com.wanniu.core.game.entity.GClientEvent;
import com.wanniu.core.game.protocol.PomeloRequest;
import com.wanniu.core.game.protocol.PomeloResponse;
import com.wanniu.game.common.Const;
import com.wanniu.game.equip.NormalEquip;
import com.wanniu.game.item.ItemUtil;
import com.wanniu.game.player.GlobalConfig;
import com.wanniu.game.player.WNPlayer;

import pomelo.area.EquipHandler.EquipLevelUpRequest;
import pomelo.area.EquipHandler.EquipLevelUpResponse;
import pomelo.area.EquipHandler.EquipPos;

/**
 * 装备升级(等级)
 * 
 * @author Yangzz
 *
 */
@GClientEvent("area.equipHandler.equipLevelUpRequest")
public class EquipLevelUpHandler extends PomeloRequest {

	public PomeloResponse request() throws Exception {

		WNPlayer player = (WNPlayer) pak.getPlayer();

		EquipLevelUpRequest req = EquipLevelUpRequest.parseFrom(pak.getRemaingBytes());
		final EquipPos equipPos = req.getC2SEquipPos();
		final int _mateType = req.getC2SMateType(); // //0:装备和材料都需要消耗，根据配置设置 1：选择消耗装备 2：消耗材料

		return new PomeloResponse() {
			@Override
			protected void write() throws IOException {
				EquipLevelUpResponse.Builder res = EquipLevelUpResponse.newBuilder();

				int mateType = _mateType;
			    if(mateType != 1 && mateType != 2){
			    	res.setS2CCode(FAIL);
			    	res.setS2CMsg(LangService.getValue("PARAM_ERROR"));
					body.writeBytes(res.build().toByteArray());
			        return;
		        }

			    NormalEquip oldEquip = (NormalEquip) ItemUtil.getEquip(player, equipPos);
		        if(oldEquip == null){
		        	res.setS2CCode(FAIL);
			    	res.setS2CMsg(LangService.getValue("EQUIP_NOT_EXIST"));
					body.writeBytes(res.build().toByteArray());
			        return;
		        }
		        if(!oldEquip.isEquip()){
		        	res.setS2CCode(FAIL);
			    	res.setS2CMsg(LangService.getValue("ITEM_NOT_EQUIP"));
					body.writeBytes(res.build().toByteArray());
			        return;
		        }
		        if(oldEquip.prop.itemType != Const.ItemType.Weapon.getValue() && oldEquip.prop.itemType != Const.ItemType.Armor.getValue()){
		        	res.setS2CCode(FAIL);
			    	res.setS2CMsg(LangService.getValue("EQUIP_CAN_NOT_LEVEL_UP"));
					body.writeBytes(res.build().toByteArray());
			        return;
		        }

		        int minColor = GlobalConfig.Equipment_LvUp_QColor;
		        if(oldEquip.prop.qcolor < minColor){
		        	res.setS2CCode(FAIL);
			    	res.setS2CMsg(LangService.getValue("EQUIP_LEVEL_UP_QUALITY_TO_LOW"));
					body.writeBytes(res.build().toByteArray());
			        return;
		        }


		    //删除源装备
		    if(equipPos.getBagOrBody() == Const.EquipPos.BODY.value){//删除身上的装备
		        player.equipManager.unEquip(oldEquip.getPosition());
		    }else{//删除背包中的装备
		        player.getWnBag().removeItemByPos(equipPos.getPosOrGrid(), true, Const.GOODS_CHANGE_TYPE.equipLevelUp);
		    }
		    Map<String, Integer> biItems = new HashMap<String, Integer>();
		    biItems.put(oldEquip.itemDb.code, 1);

			res.setS2CCode(OK);
			body.writeBytes(res.build().toByteArray());
			}
		};
	}
}