package com.wanniu.game.area;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import com.alibaba.fastjson.JSONObject;
import com.wanniu.core.logfs.Out;
import com.wanniu.game.common.Const;
import com.wanniu.game.data.base.MapBase;
import com.wanniu.game.player.WNPlayer;

/**
 * 幻境
 * 
 * @author Yangzz
 */
public class IllusionArea extends Area {

	public MapBase flProp;

	public final ConcurrentMap<String, Long> playerHookTimeMap;

	public IllusionArea(JSONObject opts) {
		super(opts);

		flProp = AreaDataConfig.getInstance().get(this.areaId);
		if (this.flProp == null) {
			Out.error("there is no IllusionArea prop   id:", this.areaId);
		}

		this.playerHookTimeMap = new ConcurrentHashMap<>();
	}

	/**
	 * 角色成功进入场景
	 */
	public void onPlayerEntered(WNPlayer player) {
		super.onPlayerEntered(player);
		/** 开始挂机时间 */
		long hookStartTime = System.currentTimeMillis();
		playerHookTimeMap.put(player.getId(), hookStartTime);
		Out.info("幻境计时开始 playerId=", player.getId(), ",hookStartTime=", hookStartTime);
		player.illusionManager.pushChage();
	}

	/**
	 * 角色成功离开场景
	 */
	public void onPlayerLeaved(WNPlayer player) {
		super.onPlayerLeaved(player);
		// Out.debug("leave...-----");
		// 更新累计挂机时间
		Long hookStartTime = playerHookTimeMap.remove(player.getId());
		if (hookStartTime != null) {
			recordHookTime(player, hookStartTime);
		} else {
			Out.warn("幻境计时结算异常，没有找到进入时间 playerId=", player.getId());
		}
	}

	/**
	 * 更新挂机时间
	 */
	private void recordHookTime(WNPlayer player, long hookStartTime) {
		long now = System.currentTimeMillis();
		long hookTime = now - hookStartTime;
		Out.info("幻境计时结束 playerId=", player.getId(), ",now=", now, ",hookStartTime=", hookStartTime, ",hookTime=", hookTime);
		player.achievementManager.onIllusionTimeChange((int) (hookTime / Const.Time.Minute.getValue()));
	}
}