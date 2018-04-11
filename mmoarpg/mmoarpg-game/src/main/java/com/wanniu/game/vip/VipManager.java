package com.wanniu.game.vip;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import com.wanniu.game.common.Const;
import com.wanniu.game.common.Const.GOODS_CHANGE_TYPE;
import com.wanniu.game.common.Const.ManagerType;
import com.wanniu.game.common.Const.PlayerEventType;
import com.wanniu.game.common.Const.VipType;
import com.wanniu.game.common.DateUtils;
import com.wanniu.game.common.ModuleManager;
import com.wanniu.game.data.CardCO;
import com.wanniu.game.data.GameData;
import com.wanniu.game.player.WNPlayer;
import com.wanniu.game.poes.VipPO;

import pomelo.area.PlayerHandler.SuperScriptType;

public class VipManager extends ModuleManager {
	private static final float Tenthousand = 10000;
	private final WNPlayer player;

	public VipPO po;

	public VipManager(WNPlayer player, VipPO po) {
		this.player = player;
		if (po == null) {
			player.playerAttachPO.vipData = po = new VipPO();
		}
		this.po = po;
	}

	/**
	 * 领取vip每日奖励
	 * 
	 * @return 0成功，-1不是月卡vip，-2不是终身卡vip，-3已经领过了,-4未知异常
	 */
	public int takeDailyReward(int type) {
		CardCO cardProp = GameData.Cards.get(type);
		if (cardProp == null) {
			return -4;
		}
		VipType vt = VipType.getE(type);
		if (vt == VipType.month) {
			if (player.baseDataManager.getVip() != VipType.month.value && player.baseDataManager.getVip() != VipType.sb_double.value) {
				return -1;
			}
			if (po.lastMonthRewardDate != null && DateUtils.isToday(po.lastMonthRewardDate)) {
				return -3;
			}
			po.lastMonthRewardDate = Calendar.getInstance().getTime();
		} else if (vt == VipType.forever) {
			if (player.baseDataManager.getVip() != VipType.forever.value && player.baseDataManager.getVip() != VipType.sb_double.value) {
				return -2;
			}
			if (po.lastForeverRewardDate != null && DateUtils.isToday(po.lastForeverRewardDate)) {
				return -3;
			}
			po.lastForeverRewardDate = Calendar.getInstance().getTime();
		}
		player.moneyManager.addTicket(cardProp.dailyDW, GOODS_CHANGE_TYPE.dailypay_gift);
		updateSuperScript();
		return 0;
	}

	public float getVipExpRatio() {
		if (player.baseDataManager.getVip() != VipType.none.value) {
			CardCO card = GameData.Cards.get(player.baseDataManager.getVip());
			return card.prv2 / Tenthousand;
		}
		return 0;
	}

	public float getExtGoldRatio() {
		if (player.baseDataManager.getVip() != VipType.none.value) {
			CardCO card = GameData.Cards.get(player.baseDataManager.getVip());
			return card.prv3 / Tenthousand;
		}
		return 0;
	}

	@Override
	public void onPlayerEvent(PlayerEventType eventType) {
		if (eventType == PlayerEventType.AFTER_LOGIN) {
			updateSuperScript();
		}
	}

	@Override
	public ManagerType getManagerType() {
		return ManagerType.VIP;
	}

	public long getVipRemainTime() {
		long remain = 0;
		if (player.baseDataManager.getVip() == VipType.none.value || player.baseDataManager.getVip() == VipType.forever.value)
			return 0;
		else {
			Calendar c_end = Calendar.getInstance();
			c_end.setTime(player.player.vipEndTime);
			remain = (c_end.getTimeInMillis() - System.currentTimeMillis()) / 1000;
			if (remain < 0)
				remain = 0;
		}
		return remain;
	}

	public final List<SuperScriptType> getSuperScript() {
		List<SuperScriptType> list = new ArrayList<>();
		SuperScriptType.Builder script = SuperScriptType.newBuilder();
		script.setType(Const.SUPERSCRIPT_TYPE.VIP.getValue());

		int vipType = player.baseDataManager.getVip();
		int num = 0;
		if (vipType == VipType.month.value || vipType == VipType.sb_double.value) {
			if (po.lastMonthRewardDate == null || !DateUtils.isToday(po.lastMonthRewardDate)) {
				num++;
			}
		}
		if (vipType == VipType.forever.value || vipType == VipType.sb_double.value) {
			if (po.lastForeverRewardDate == null || !DateUtils.isToday(po.lastForeverRewardDate)) {
				num++;
			}
		}
		script.setNumber(num);

		list.add(script.build());
		return list;
	}

	public final void updateSuperScript() {
		this.player.updateSuperScriptList(this.getSuperScript());
	}

	/**
	 * 获取每日VIP增加的复活次数.
	 */
	public int getReliveNum() {
		if (player.baseDataManager.getVip() != VipType.none.value) {
			CardCO card = GameData.Cards.get(player.baseDataManager.getVip());
			if (card != null) {
				return card.prv10;
			}
		}
		return 0;
	}
}