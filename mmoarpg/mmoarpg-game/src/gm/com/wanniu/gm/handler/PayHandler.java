package com.wanniu.gm.handler;

import com.alibaba.fastjson.JSONArray;
import com.wanniu.core.logfs.Out;
import com.wanniu.game.player.PlayerDao;
import com.wanniu.game.player.PlayerUtil;
import com.wanniu.game.prepaid.PrepaidCenter;
import com.wanniu.game.prepaid.PrepaidManager;
import com.wanniu.gm.GMEvent;
import com.wanniu.gm.GMResponse;
import com.wanniu.gm.GMStateResponse;

import cn.qeng.common.gm.RpcOpcode;

/**
 * 后台GM补单充值.
 *
 * @author 小流氓(176543888@qq.com)
 */
@GMEvent
public class PayHandler extends GMBaseHandler {

	public GMResponse execute(JSONArray arr) {
		String roleType = arr.getString(0);
		String roleId = arr.getString(1);
		int productId = arr.getIntValue(2);
		boolean logBI = true;// 记录BI...
		if (arr.size() > 3) {
			logBI = arr.getIntValue(3) == 1;
		}

		if (roleType.equals("id")) {
			if (PlayerUtil.getPlayerBaseData(roleId) == null) {
				return new GMStateResponse(-2);
			}
		} else {
			String id = PlayerDao.getIdByName(roleId);
			if (id == null) {
				return new GMStateResponse(-2);
			}
			roleId = id;
		}

		boolean isCard = false;
		// 月惠卡--尊享卡
		if (productId == 1 || productId == 2) {
			isCard = true;
		}

		// 301，302，303 为超级礼包
		boolean isSuperPackage = false;
		if (productId > 300) {
			isSuperPackage = true;
		}

		PrepaidManager manager = PrepaidCenter.getInstance().findPrepaid(roleId);
		manager.onCharge(productId, isCard, isSuperPackage, logBI);
		PrepaidCenter.getInstance().update(roleId, manager);
		Out.info("GM补单记录 roleId=", roleId, ",productId=", productId, ",isCard=", isCard, ",isSuperPackage=", isSuperPackage, ",logBI=", logBI);
		return new GMStateResponse(1);
	}

	public short getType() {
		return RpcOpcode.OPCODE_RECHARGE;
	}
}