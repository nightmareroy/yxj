package com.wanniu.game.request.equip;

import java.io.IOException;

import com.wanniu.core.game.LangService;
import com.wanniu.core.game.entity.GClientEvent;
import com.wanniu.core.game.protocol.PomeloRequest;
import com.wanniu.core.game.protocol.PomeloResponse;
import com.wanniu.game.bag.WNBag;
import com.wanniu.game.common.Const;
import com.wanniu.game.common.Const.GOODS_CHANGE_TYPE;
import com.wanniu.game.common.msg.ErrorResponse;
import com.wanniu.game.equip.EquipManager;
import com.wanniu.game.equip.NormalEquip;
import com.wanniu.game.item.NormalItem;
import com.wanniu.game.player.WNPlayer;

import pomelo.area.EquipHandler.EquipRequest;
import pomelo.area.EquipHandler.EquipResponse;

/**
 * 穿戴装备
 * 
 * @author Yangzz
 *
 */
@GClientEvent("area.equipHandler.equipRequest")
public class EquipHandler extends PomeloRequest {

	public PomeloResponse request() throws Exception {

		WNPlayer player = (WNPlayer) pak.getPlayer();
		WNBag wnBag = player.getWnBag();
		EquipManager wnEquip = player.equipManager;

		EquipRequest req = EquipRequest.parseFrom(pak.getRemaingBytes());
		int gridIndex = req.getC2SGridIndex();
		NormalItem item = wnBag.getItem(gridIndex);

		if (item == null) {
			return new ErrorResponse(LangService.getValue("SOMETHING_ERR"));
		}
		if (!item.isEquip()) {
			return new ErrorResponse(LangService.getValue("ITEM_NOT_EQUIP"));
		}
		NormalEquip equip = (NormalEquip) item;
		if (equip.prop.Pro != 0) {
			if (equip.prop.Pro != player.getPro()) {
				return new ErrorResponse(LangService.getValue("OCCUPATION_WRONG"));
			}
		}

		// 等级和进阶判断
		if (equip.getUpLevel() != 0) {
			if (equip.getUpLevel() > player.getPlayer().upLevel) {
				return new ErrorResponse(LangService.getValue("UP_NOT_ENOUGH"));
			}
		} else {
			if (equip.getLevel() > player.getLevel()) {
				return new ErrorResponse(LangService.getValue("LEVEL_NOT_ENOUGH"));
			}
		}

		return new PomeloResponse() {
			@Override
			protected void write() throws IOException {
				EquipResponse.Builder res = EquipResponse.newBuilder();

				if (equip.prop.itemType == Const.ItemType.Weapon.getValue() && (equip.prop.isBothHand == 1 || wnEquip.haveBothHandWeapon())) {
					// 双手武器
					int num = player.equipManager.weaponNum();
					num--;
					if (wnBag.emptyGridNum() < num) {
						res.setS2CCode(FAIL);
						res.setS2CMsg(LangService.getValue("BAG_NOT_ENOUGH_POS"));
						body.writeBytes(res.build().toByteArray());
						return;
					}

					wnBag.removeItemByPos(gridIndex, false, GOODS_CHANGE_TYPE.equip);
					NormalEquip mainH = wnEquip.getEquipment(Const.EquipType.MAIN_HAND.getValue());
					if (mainH != null) {
						wnBag.addEntityItem(mainH, null, null, true, true);
						wnEquip.unEquip(Const.EquipType.MAIN_HAND.getValue());
					}
					// NormalEquip secondH = wnEquip.getEquipment(Const.EquipType.SECOND_HAND.getValue());
					// if (secondH != null) {
					// wnBag.addEntityItem(secondH, null, null, true, true);
					// wnEquip.unEquip(Const.EquipType.SECOND_HAND.getValue());
					// }
					wnEquip.equip(equip);
					// ItemUtil.checkEquipInheritPush(player, gridIndex, item.getPosition()); 传承没有了

					res.setS2CCode(OK);
					body.writeBytes(res.build().toByteArray());
					return;
				} else {
					NormalEquip oldEquip = player.equipManager.getEquipment(equip.getPosition());
					wnBag.removeItemByPos(gridIndex, false, GOODS_CHANGE_TYPE.equip);
					if (oldEquip != null) {
						wnBag.addItemToPos(gridIndex, oldEquip, GOODS_CHANGE_TYPE.equip);
					}
					wnEquip.equip(equip);
					// ItemUtil.checkEquipInheritPush(player, gridIndex, item.getPosition());传承没了

					res.setS2CCode(OK);
					body.writeBytes(res.build().toByteArray());
					return;
				}
			}
		};
	}
}