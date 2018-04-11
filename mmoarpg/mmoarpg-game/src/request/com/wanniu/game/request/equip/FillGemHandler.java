package com.wanniu.game.request.equip;

import java.io.IOException;

import com.wanniu.core.game.LangService;
import com.wanniu.core.game.entity.GClientEvent;
import com.wanniu.core.game.protocol.PomeloRequest;
import com.wanniu.core.game.protocol.PomeloResponse;
import com.wanniu.game.bag.WNBag;
import com.wanniu.game.common.Const;
import com.wanniu.game.data.GameData;
import com.wanniu.game.item.NormalItem;
import com.wanniu.game.player.WNPlayer;
import com.wanniu.game.poes.PlayerBasePO.EquipStrengthPos;

import pomelo.area.EquipHandler.FillGemRequest;
import pomelo.area.EquipHandler.FillGemResponse;

/**
 * 装备强化
 * 
 * @author Yangzz
 *
 */
@GClientEvent("area.equipHandler.fillGemRequest")
public class FillGemHandler extends PomeloRequest {

	public PomeloResponse request() throws Exception {

		WNPlayer player = (WNPlayer) pak.getPlayer();
		WNBag wnBag = player.getWnBag();

		FillGemRequest req = FillGemRequest.parseFrom(pak.getRemaingBytes());
		int pos = req.getC2SPos();
//		int index = req.getC2SIndex();
		int gridIndex = req.getC2SGridIndex();

		return new PomeloResponse() {
			@Override
			protected void write() throws IOException {
				FillGemResponse.Builder res = FillGemResponse.newBuilder();

				if (!player.functionOpenManager.isOpen(Const.FunctionType.SetNew.getValue())) {
					res.setS2CCode(Const.CODE.FAIL);
					res.setS2CMsg(LangService.getValue("FUNC_SET_PLAYED_NOT_OPEN"));
					body.writeBytes(res.build().toByteArray());
					return;
				}
				
				NormalItem item = wnBag.getItem(gridIndex);
				if (item == null || item.prop.itemSecondType != Const.ItemSecondType.gem.getValue()) {
					res.setS2CCode(FAIL);
					res.setS2CMsg(LangService.getValue("SOMETHING_ERR"));
					body.writeBytes(res.build().toByteArray());
					return;
				}

				// 判断部位宝石类型是否匹配
				if (!GameData.EquipSocks.get(pos).typeList.contains(item.itemDb.code)) {
					res.setS2CCode(FAIL);
					res.setS2CMsg(LangService.getValue("EQUIP_CANNOT_USE_GEM"));
					body.writeBytes(res.build().toByteArray());
					return;
				}


				EquipStrengthPos posInfo = player.equipManager.strengthPos.get(pos);
				if (posInfo == null) {
					res.setS2CCode(FAIL);
					res.setS2CMsg(LangService.getValue("EQUIP_POS_NOT_EXIST"));
					body.writeBytes(res.build().toByteArray());
					return;
				}



				int[] index = new int[] {-1};
				if (player.equipManager.fillGem(pos, index, item.prop.code, player)) {
					wnBag.discardItemByPos(gridIndex, 1, false, Const.GOODS_CHANGE_TYPE.equipmosaic);
					if (index[0] != -1) {
						res.setS2CIndex(index[0]);
					}
					res.setS2CCode(OK);
					body.writeBytes(res.build().toByteArray());
				} else {
					res.setS2CCode(FAIL);
					res.setS2CMsg(LangService.getValue("EQUIP_FILL_ERROR"));
					body.writeBytes(res.build().toByteArray());
					return;
				}
			}
		};
	}
}