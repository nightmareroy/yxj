package com.wanniu.game.cross;

import com.alibaba.fastjson.JSONObject;
import com.wanniu.game.area.Area;

/**
 * 连服场景
 * 
 * @author agui
 *
 */
public class CrossServerLocalArea extends Area {

	public CrossServerLocalArea(JSONObject opts) {
		super(opts);
	};

	public void init() {
		super.init();
		// TODO
		// int mapId = GlobalConfig.CROSS_SERVER_ENTER_SCENE;
		// if (this.areaId != mapId) {
		// return ;
		// }
		// var data = crossUtil.getCrossTreasureConfig();
		// var zeroDate = utils.getZeroDate();
		// var timeOpenCloseList = [];
		// for(var i = 0; i < data.openTimeList.length; ++i){
		// var openInfo = data.openTimeList[i];
		// var startTime = openInfo.start.getTime() - zeroDate.getTime();
		// var endTime = openInfo.end.getTime() - zeroDate.getTime();
		// timeOpenCloseList.push(startTime);
		// timeOpenCloseList.push(endTime);
		// }
		//
		// var monsterIdProbabilityList = [];
		// for(var i = 0; i < data.refreshList.length; ++i){
		// var boxInfo = data.refreshList[i];
		// monsterIdProbabilityList.push(boxInfo.id);
		// monsterIdProbabilityList.push(boxInfo.weight);
		// }
		//
		// var createBossIntervalMS = data.coolDown * Const.Time.Second;
		// var maxMonsterCount = data.maxCount;
		//
		// var param = {};
		// param.timeOpenCloseList = timeOpenCloseList;
		// param.monsterIdProbabilityList = monsterIdProbabilityList;
		// param.createBossIntervalMS = createBossIntervalMS;
		// param.maxMonsterCount = maxMonsterCount;
		// Out.debug("TreasureActivityInfo areaId:" + this.areaId + ", instanceId:" +
		// this.instanceId + ", param:", param);
		// XmdsManager.getInstance().notifyBattleServer("TreasureActivityInfoR2B",
		// param, this.instanceId);
	}

}
