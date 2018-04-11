package com.wanniu.game.poes;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.wanniu.core.game.entity.GEntity;
import com.wanniu.core.logfs.Out;
import com.wanniu.game.DBTable;
import com.wanniu.game.common.Const;
import com.wanniu.game.common.Table;
import com.wanniu.game.player.GlobalConfig;
import com.wanniu.game.solo.SoloService;
import com.wanniu.game.solo.po.BattleRecordPO;

/**
 * 单挑存档原数据类
 * @author WFY
 *
 */
@DBTable(Table.player_solo_data)
public class SoloDataPO extends GEntity {
	public boolean havePlayed;//是否玩过单挑王

	public int score; // 积分 新增
	public int curRank;//当前排名
	public int soloPoint; // 新增 当前宗师币
	public int soloPointToday; // 新增 今日收益宗师币
	
	public int rankId;// 段位ID 根据积分换算的段位
	public Date rankGotTime;// 段位获得时间
	/**
	 * rankRewards:{ $rankId1:$status1,$rankId2:$status2,...}
	 */
	public Map<Integer,RankStatus> rankRewards;// 段位奖励 {$rankId1:$status1,$rankId2:$status2,...} 0-不可领取(不存档) 1-可领取 2-已领取

	/**
	 * dailyRewards: {$rankId1:$status1,$rankId2:$status2,...} 0-不可领取(不存档) 1-可领取 2-已领取
	 */
	public List<RankStatus> dailyRewards;// 每日奖励 [$rankId1,$rankId2,...]
	public int dailyReceived;// 当日已领取次数
	public int dailyBattleTimes;//获得每日奖励所需战斗次数
	public int dailyRewardRound;//最后生成奖励是第几轮
	
	public Date dailyResetTime; // 每日重置时间
	public Date lastJoinedTime; // 最后一次参加时间

//==========================================================	
	public int contWinTimes;// 连胜次数
	public int extrWinTimes;// 额外奖励连胜次数
	public int maxContWinTimes;//历史最高连胜次数	
	public int contLoseTimes;//连败次数
	public int maxContLoseTimes;//历史最高连败次数

	public int term;//当前所在赛季期数
	public int battleTimes; // 新增 当前赛季单挑总场次
	public int winTotalTimes; // 新增 当前赛季胜利场次
	public int loseTotalTimes; // 新增 当前赛季失败场次

	public int winTimes_canglang;
	public int winTimes_yujian;
	public int winTimes_yixian;
	public int winTimes_shenjian;
	public int winTimes_linghu;

	
	public int battleTimes_canglang;
	public int battleTimes_yujian;
	public int battleTimes_yixian;
	public int battleTimes_shenjian;
	public int battleTimes_linghu;
	public List<BattleRecordPO> battleRecords ;// 最新的战斗记录

	public SoloDataPO() {
		
	}

	public SoloDataPO(String playerId){
		this.havePlayed = false; // 是否玩过单挑王
		
		this.score = 0;
		this.curRank = 0;
		this.soloPoint = Const.PLAYER.initSolopoint;
		this.soloPointToday = 0;
		
		this.rankId = 1; // 段位ID
		this.rankGotTime = null; // 段位获得时间
		this.rankRewards = new HashMap<>(); // 段位奖励{$rankId1:$status1,$rankId2:$status2,...}
											// 0-不可领取(不存档) 1-可领取 2-已领取
		
		this.dailyRewards = new ArrayList<>(); // 每日奖励 [$rankId1,$rankId2,...]
		this.dailyReceived = 0; // 当日已领取次数
		this.dailyBattleTimes = 0;//获得每日奖励所需战斗次数记录
		this.dailyRewardRound = 0;
		
		this.dailyResetTime = null; // 每日重置时间
		this.lastJoinedTime = null; // 最后一次参加时间
		
		this.contWinTimes = 0; // 连胜次数
		this.extrWinTimes = 0; // 额外奖励连胜次数
		this.maxContWinTimes = 0;//		
		this.contLoseTimes = 0;
		this.maxContLoseTimes = 0;
		
		
		this.term = SoloService.getInstance().getTerm();//当前所在赛季期数
		this.battleTimes=0; // 新增 当前赛季单挑总场次
		this.winTotalTimes=0; // 新增 当前赛季胜利场次
		this.loseTotalTimes=0; // 新增 当前赛季失败场次

		this.winTimes_canglang=0;
		this.winTimes_yujian=0;
		this.winTimes_yixian=0;
		this.winTimes_shenjian=0;
		this.winTimes_linghu=0;

		
		this.battleTimes_canglang=0;
		this.battleTimes_yujian=0;
		this.battleTimes_yixian=0;
		this.battleTimes_shenjian=0;
		this.battleTimes_linghu=0;
		this.battleRecords = new ArrayList<>();
	}

	
	
	private int _getWinRate(int winTimes, int battleTimes){
		if(battleTimes==0){
			battleTimes = 1;
		}
		float a=winTimes;
		float b = battleTimes;
		return Math.round(a/b*10000); 
	}
	public static void main(String[] args) {
		float a=2;
		float b = 3;
		int c = Math.round(a/b*10000); 
		System.out.println(c);
	}
	
	/**
	 * 根据职业获取对应胜率 
	 * @param pro 通用0|狂战士=>苍狼1|刺客=>御剑2|魔法师=>逸仙3|猎人=>神剑4|牧师=>灵狐5|
	 * @return
	 */
	public int getWinRate(Const.Profession pro){
		switch(pro.getValue()){
		case 0:
			return _getWinRate(winTotalTimes,battleTimes);
		case 1:
			return _getWinRate(winTimes_canglang,battleTimes_canglang);
		case 2:
			return _getWinRate(winTimes_yujian,battleTimes_yujian);
		case 3:
			return _getWinRate(winTimes_yixian,battleTimes_yixian);
		case 4:
			return _getWinRate(winTimes_shenjian,battleTimes_shenjian);
		case 5:
			return _getWinRate(winTimes_linghu,battleTimes_linghu);
		default:
			return 0;
		}
	}
	
	
	/**
	 * 记录战斗数据
	 * @param battleRecord
	 */
	public void recordBattle(BattleRecordPO battleRecord){//战斗结果 1-胜 2-负 3-平
		battleTimes++;
		switch(battleRecord.result){
		case 1:
			winTotalTimes++;
			addBattleTimesByPro(battleRecord.vsPro,true);
			break;
		case 2:
			loseTotalTimes++;
			addBattleTimesByPro(battleRecord.vsPro,false);
			break;
		case 3:
			addBattleTimesByPro(battleRecord.vsPro,false);
			break;
		default:
			Out.error("错误的战斗记录",this.getClass());
		}
		addBattleRecord(battleRecord);
	}
	
	//|狂战士=>苍狼1|刺客=>御剑2|魔法师=>逸仙3|猎人=>神剑4|牧师=>灵狐5|
	private void addBattleTimesByPro(int pro,boolean isWin){

			switch(pro){
			case 1:
				battleTimes_canglang++;
				if(isWin){
					winTimes_canglang++;
				}
				break;
			case 2:
				battleTimes_yujian++;
				if(isWin){
					winTimes_yujian++;
				}
				break;
			case 3:
				battleTimes_yixian++;
				if(isWin){
					winTimes_yixian++;
				}
				break;
			case 4:
				battleTimes_shenjian++;
				if(isWin){
					winTimes_shenjian++;
				}
				break;
			case 5:
				battleTimes_linghu++;
				if(isWin){
					winTimes_linghu++;
				}
				break;
			}
	
	}
	
	private boolean isExpired(Date oldDate, Date newDate,long validMillis){
		return oldDate.getTime()+validMillis < newDate.getTime();
	}
	
	/**
	 * 增加一条战斗记录，超过记录上限就删除最旧的记录
	 * 
	 * @param battleRecord
	 */
	private void addBattleRecord(BattleRecordPO battleRecord) {
		final long validDay = GlobalConfig.Solo_ReportTime*3600*1000;//小时换算成毫秒数
		Iterator<BattleRecordPO> iter = battleRecords.iterator();
		Date now = new Date();
		while(iter.hasNext()){
			if(isExpired(iter.next().battleTime,now,validDay)){
				iter.remove();//移除过期的战斗信息
			}
		}
		
		if(battleRecords.size()<GlobalConfig.Solo_ReportCount){//小于记录上限就直接加到后面
			battleRecords.add(battleRecord);
		}else{
			battleRecords.remove(0);
			battleRecords.add(battleRecord);
		}

	}

	public static class RankStatus {
		public int rankId;
		public int status;

		public int getRankId() {
			return rankId;
		}

		public void setRankId(int rankId) {
			this.rankId = rankId;
		}

		public int getStatus() {
			return status;
		}

		public void setStatus(int status) {
			this.status = status;
		}
	}
}
