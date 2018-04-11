package com.wanniu.game.hookSet;

import java.util.List;

import com.wanniu.core.util.StringUtil;
import com.wanniu.game.common.ConstsTR;
import com.wanniu.game.player.PlayerUtil;
import com.wanniu.game.player.WNPlayer;
import com.wanniu.game.poes.HookSetPO;
import com.wanniu.redis.PlayerPOManager;

import pomelo.area.HookSetHandler.HookSetData;
import pomelo.player.PlayerOuterClass;

public class HookSetManager {

	private final HookSetPO po;

	public HookSetManager(WNPlayer player, HookSetPO data) {
		if (data == null) {
			player.allBlobData.hookSetData = data = PlayerUtil.createHookSetManager(player.getId());
			PlayerPOManager.put(ConstsTR.hookSetTR, player.getId(), data);
		}
		this.po = data;
	}

	public void changeHookSet(HookSetData hsd) {
		po.hpPercent = (hsd.getHpPercent());
		po.mpPercent = (hsd.getMpPercent());
		po.hpItemCode = (hsd.getHpItemCode());
		po.mpItemCode = (hsd.getMpItemCode());
		po.pkSet = (hsd.getPkSet());
		po.meltQcolor = (hsd.getMeltQcolorList());
		po.autoBuyHpItem = (hsd.getAutoBuyHpItem());
		po.autoBuyMpItem = (hsd.getAutoBuyMpItem());
		po.fieldMaphook = (hsd.getFieldMaphook());
		po.areaMaphook = (hsd.getAreaMaphook());
	}

	public PlayerOuterClass.HookSetData.Builder toJson4Payload() {
		PlayerOuterClass.HookSetData.Builder data = PlayerOuterClass.HookSetData.newBuilder();
		data.setHpPercent(this.po.hpPercent);
		data.setMpPercent(this.po.mpPercent);
		if (StringUtil.isNotEmpty(this.po.hpItemCode)) {
			data.setHpItemCode(this.po.hpItemCode);
		}

		if (StringUtil.isNotEmpty(this.po.mpItemCode)) {
			data.setMpItemCode(this.po.mpItemCode);
		} else {
			data.setMpItemCode("");
		}

		data.setPkSet(this.po.pkSet);
		data.addAllMeltQcolor(this.po.meltQcolor);
		data.setAutoBuyHpItem(this.po.autoBuyHpItem);
		data.setAutoBuyMpItem(this.po.autoBuyMpItem);
		data.setFieldMaphook(this.po.fieldMaphook);
		data.setAreaMaphook(this.po.areaMaphook);
		return data;
	};

	/**
	 * 自动熔炼装备颜色
	 * 
	 * @returns [qcolor1, qcolor2, qcolor3]
	 */
	public List<Integer> getMeltQcolor() {
		return this.po.meltQcolor;
	};
}
