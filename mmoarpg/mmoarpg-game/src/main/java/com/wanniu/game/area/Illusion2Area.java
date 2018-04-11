package com.wanniu.game.area;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import com.alibaba.fastjson.JSONObject;
import com.wanniu.core.game.JobFactory;
import com.wanniu.core.logfs.Out;
import com.wanniu.core.util.StringUtil;
import com.wanniu.game.area.Area.Actor;
import com.wanniu.game.area.Area.AreaItem;
import com.wanniu.game.common.Const;
import com.wanniu.game.common.Const.GOODS_CHANGE_TYPE;
import com.wanniu.game.daily.DailyActivityMgr;
import com.wanniu.game.data.CharacterLevelCO;
import com.wanniu.game.data.GameData;
import com.wanniu.game.data.ext.RandomBoxExt;
import com.wanniu.game.data.ext.RandomBoxExt.Point;
import com.wanniu.game.item.ItemUtil;
import com.wanniu.game.item.NormalItem;
import com.wanniu.game.mail.SysMailConst;
import com.wanniu.game.player.GlobalConfig;
import com.wanniu.game.player.WNPlayer;
import com.wanniu.game.sevengoal.SevenGoalManager.SevenGoalTaskType;

/**
 * 幻境2玩法.
 * 
 * @author 周明凯(zhoumingkai@qeng.cn)
 */
public class Illusion2Area extends Area {
	public static final int DEFAULT_ID = 53001;

	public Illusion2Area(JSONObject opts) {
		super(opts);

		// 计算出自动关闭的时间。
		JobFactory.addDelayJob(() -> {
			Out.info("幻境2玩法清场.", this.instanceId);
			AreaManager.getInstance().closeArea(instanceId);
		}, DailyActivityMgr.getCloseIllusion2Second() + 2, TimeUnit.SECONDS);
	}

	/**
	 * 角色成功进入场景
	 */
	@Override
	public void onPlayerEntered(WNPlayer player) {
		super.onPlayerEntered(player);
		player.illusionManager.pushChageBy2();
		player.dailyActivityMgr.onEvent(Const.DailyType.ILLUSION2, "0", 1);
		player.sevenGoalManager.processGoal(SevenGoalTaskType.ILLUSION2_COUNT);
	}

	/* (non-Javadoc)
	 * @see com.wanniu.game.area.Area#canPickInterActiveItem(java.lang.String, int)
	 */
	@Override
	protected boolean canPickInterActiveItem(WNPlayer player,String itemCode,int itemNum) {
		return  player.illusionManager.addItemNum(this, itemCode, itemNum);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.wanniu.game.area.Area#onPickItem(java.lang.String, java.lang.String,
	 * boolean)
	 */
	@Override
	public AreaItem onPickItem(String playerId, String itemId, boolean isGuard) {
		AreaItem areaItem = this.items.get(itemId);
		if (areaItem != null && (areaItem.bindPlayers == null || areaItem.bindPlayers.containsKey(playerId)
				|| System.currentTimeMillis() - areaItem.createTime > GlobalConfig.itemdrop_lock_lifeTime)) {
			Actor actor = getActor(playerId);
			if (actor == null || !actor.profitable) {
				Out.debug(playerId, "当前不可拾取", itemId);
				return null;
			}
			WNPlayer player = getPlayer(playerId);

			if (!player.illusionManager.addItemNum(this, areaItem.item.itemCode(), areaItem.item.getNum())) {
				Out.debug(playerId, "秘境夺宝达到宝箱上限，当前不可拾取", itemId);
				return null;
			}

			this.items.remove(itemId);
			Out.debug(getClass(), player.getName(), " onPickItem itemId:", itemId, ",isGuard:", isGuard);
			int groupCount = areaItem.item.itemDb.groupCount;
			this.onFreedomPickItem(player, areaItem.item, isGuard);
			areaItem.item.setGroup(groupCount);
			if (areaItem.dropPlayer != null) {
				this.onPickPlayerDropItem(player, areaItem);
			} else {
				this.onPickMonsterDropItem(player, areaItem);
			}
			return areaItem;
		}
		return null;
	}
}