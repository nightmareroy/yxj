package com.wanniu.game.request.equip;

import java.io.IOException;
import java.util.List;

import com.wanniu.core.game.LangService;
import com.wanniu.core.game.entity.GClientEvent;
import com.wanniu.core.game.protocol.PomeloRequest;
import com.wanniu.core.game.protocol.PomeloResponse;
import com.wanniu.core.util.StringUtil;
import com.wanniu.game.common.Const;
import com.wanniu.game.common.Const.GOODS_CHANGE_TYPE;
import com.wanniu.game.common.msg.ErrorResponse;
import com.wanniu.game.data.ext.SmritiExt;
import com.wanniu.game.equip.EquipManager;
import com.wanniu.game.equip.NormalEquip;
import com.wanniu.game.item.NormalItem;
import com.wanniu.game.player.WNPlayer;

import pomelo.area.EquipHandler.SmritiRequest;
import pomelo.area.EquipHandler.SmritiResponse;

/**
 * 继承装备
 * 
 * @author Feil
 *
 */
@GClientEvent("area.equipHandler.smritiRequest")
public class EquipSmritiHandler extends PomeloRequest {

	public PomeloResponse request() throws Exception {

		WNPlayer player = (WNPlayer) pak.getPlayer();
		EquipManager equipManager = player.equipManager;

		SmritiRequest req = SmritiRequest.parseFrom(pak.getRemaingBytes());
		String leftEquipId = req.getLetfEquipId();
		String rightEquipId = req.getRightEquipId();
		if (StringUtil.isEmpty(leftEquipId) || StringUtil.isEmpty(rightEquipId) || leftEquipId.endsWith(rightEquipId)) {
			return new ErrorResponse(LangService.getValue("DATA_ERR"));
		}
		Object[] leftFlag = checkEquipExists(leftEquipId, player);
		boolean isExists = (boolean) leftFlag[0];
		if (!isExists) {
			return new ErrorResponse(LangService.getValue("BAG_STACKINDEX_ILLEGALITY"));
		}
		Object[] rightFlag = checkEquipExists(rightEquipId, player);
		isExists = (boolean) rightFlag[0];
		if (!isExists) {
			return new ErrorResponse(LangService.getValue("BAG_STACKINDEX_ILLEGALITY"));
		}
		NormalEquip leftEquip = (NormalEquip) leftFlag[3];
		NormalEquip rightEquip = (NormalEquip) rightFlag[3];
		if (rightEquip.prop.tcLevel < leftEquip.prop.tcLevel) {// 右边的装备的等级必须大于等于左边装备的等级
			return new ErrorResponse(LangService.getValue("EQUIP_SMRITI_LEVEL"));
		}
		if (rightEquip.prop.qcolor < Const.EQUIP_QCOLOR.GREEN.value) {// 必须大于绿色品质的才能传承
			return new ErrorResponse(LangService.getValue("EQUIP_SMRITI_QULITY"));
		}
		if (leftEquip.speData.extAtts == null || leftEquip.speData.extAtts.isEmpty()) {// 空属性的肯定不能作为覆盖的
			return new ErrorResponse(LangService.getValue("EQUIP_SMRITI_CANNOT"));
		}

		return new PomeloResponse() {
			@Override
			protected void write() throws IOException {
				SmritiResponse.Builder res = SmritiResponse.newBuilder();
				if (!player.functionOpenManager.isOpen(Const.FunctionType.Inherit.getValue())) {
					res.setS2CCode(Const.CODE.FAIL);
					res.setS2CMsg(LangService.getValue("FUNC_SET_PLAYED_NOT_OPEN"));
					body.writeBytes(res.build().toByteArray());
					return;
				}
				List<SmritiExt> coList = equipManager.findSmritiCO(leftEquip.prop.tcLevel);
				if (coList == null || coList.isEmpty()) {
					res.setS2CCode(Const.CODE.FAIL);
					res.setS2CMsg(LangService.getValue("CONFIG_ERR"));
					body.writeBytes(res.build().toByteArray());
					return;
				}
				SmritiExt smritiCo = coList.get(0);

				// 材料1/材料2/材料3
				for (String mateCode : smritiCo.getNeedItems().keySet()) {
					int mateCount = smritiCo.getNeedItems().get(mateCode);
					// 增加锁定消耗
					if (player.bag.findItemNumByCode(mateCode) < mateCount) {
						res.setS2CCode(FAIL);
						res.setS2CMsg(LangService.getValue("ITEM_NOT_ENOUGH"));
						body.writeBytes(res.build().toByteArray());
						return;
					}
				}
				boolean leftIsDressed = (boolean) leftFlag[1];
				int leftGridIndex = (int) leftFlag[2];

				boolean rightIsDressed = (boolean) rightFlag[1];
				int rightGridIndex = (int) rightFlag[2];

				if (player.player.gold < smritiCo.costGold) {
					res.setS2CCode(FAIL);
					res.setS2CMsg(LangService.getValue("NOT_ENOUGH_GOLD_LEARN"));
					body.writeBytes(res.build().toByteArray());
					return;
				}

				// 扣除金币
				player.moneyManager.costGold(smritiCo.costGold, GOODS_CHANGE_TYPE.inherit);

				// 扣除材料
				for (String mateCode : smritiCo.getNeedItems().keySet()) {
					int mateCount = smritiCo.getNeedItems().get(mateCode);
					player.bag.discardItem(mateCode, mateCount, GOODS_CHANGE_TYPE.inherit);
				}
				equipManager.smritiEquip(leftEquip, leftGridIndex, leftIsDressed, rightEquip, rightGridIndex, rightIsDressed);
				res.setS2CCode(OK);
				body.writeBytes(res.build().toByteArray());
				return;
			}
		};
	}

	private Object[] checkEquipExists(String equipId, WNPlayer player) {
		Object[] obj = new Object[4];
		obj[0] = true;
		obj[1] = true;
		obj[2] = 0;
		EquipManager equipManager = player.equipManager;
		NormalEquip equip = null;
		int gridIndex = equipManager.getEquipmentById(equipId);
		if (gridIndex > 0) {
			equip = equipManager.getEquipment(gridIndex);
		}
		if (equip == null) {
			obj[1] = false;
			NormalItem item = player.bag.findItemById(equipId);
			if (item != null) {
				equip = (NormalEquip) item;
				gridIndex = player.bag.findPosById(equipId);
			}
		}
		if (equip == null) {
			obj[0] = false;
		}
		obj[2] = gridIndex;
		obj[3] = equip;
		return obj;
	}
}