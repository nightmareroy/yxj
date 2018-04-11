package com.wanniu.game.poes;

import com.wanniu.core.game.entity.GEntity;
import com.wanniu.game.DBTable;
import com.wanniu.game.common.Table;

/**
 * 竞技场原数据类
 * 
 * @author WFY
 *
 */
@DBTable(Table.player_arena_data)
public class ArenaDataPO extends GEntity {
	public int usedDefTimes = 0;// 记录当天参与的场次
	public int singleReward = 0;// 单场奖励，0-不可领取 1-可领取 2-已领取
	public int totalReward = 0; // 赛季奖励，0-不可领取 1-可领取 2-已领取

	public int score = 0;// 单场mvp分值
	public int combo = 0;// 连杀人数
	public int comboDaily = 0;// 最高连杀
	public int scoreMonth = 0;// 赛季累积分数

	public int killDaily = 0;// 单场杀人总数
	public int killMonth = 0;// 赛季累积杀人数量
	public int deadMonth = 0;// 单场死亡总次数

	public int singleWinTimes = 0;// 单场第一次数
	public long firstSingleWinTime = 0;// 首次获得单场第一的时间戳毫秒数

	public long activityTime;// 记录上次参与竞技场的时间

	public int season;// 当前参与的赛季
	
	public String arenaInstanceId;// 最后加入的场景实例ID

	public ArenaDataPO() {
	}

}
