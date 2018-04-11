package com.wanniu.game.data.base;

import java.util.HashMap;
import java.util.Map;

import com.wanniu.core.util.StringUtil;
import com.wanniu.game.common.Const;

public abstract class MonsterBase {

	public Map<Integer, String> firstTcMap = new HashMap<>();
	public Map<Integer, String> personTcMap = new HashMap<>();
	
	/** 怪物ID */
	public int iD;
	/** 怪物名字 */
	public String name;
	/** 副名字/称号 */
	public String title;
	/** 生物类型 */
	public int atype;
	/** 性别 */
	public int sex;
	/** 等级 */
	public int level;
	/** 名字颜色 */
	public int qcolor;
	/** 怪物类型 */
	public int type;
	public int fight_type;
	public int fight_count;
	public int aliveTime;
	public int killPlayerTime;
	/** 变成普通怪的概率 */
	public int prob1;
	/** 普通怪ID */
	public int reviveID1;
	/** 变成稀有怪的概率 */
	public int prob2;
	/** 稀有怪ID */
	public int reviveID2;
	/** 变成首脑怪的概率 */
	public int prob3;
	/** 首脑怪ID */
	public int reviveID3;
	/** 变成精英怪的概率 */
	public int prob4;
	/** 精英怪ID */
	public int reviveID4;
	/** 显示个性语言概率 */
	public int dialogChance;
	/** 个性语言 */
	public String dialogWords;
	/** 战死时语言概率 */
	public int deadDialogChance;
	/** 战死时语言内容 */
	public String deadDialogWords;
	/** 非战斗状态时语音概率 */
	public int idleSpeakChance;
	/** 非战斗状态时语音内容 */
	public String idleSpeakWords;
	/** 非战斗状态时语音播放间隔 */
	public String idleSpeakCoolDown;
	/** 战斗状态时语音概率 */
	public int fightSpeakChance;
	/** 战斗状态时语音内容 */
	public String fightSpeakWords;
	/** 战斗状态时语音播放间隔 */
	public String fightSpeakCoolDown;
	/** 死亡时播放的语音 */
	public String deadSpeakWords;
	/** 生命 */
	public int maxHP;
	/** 法力 */
	public int maxMP;
	/** 物攻 */
	public int phy;
	/** 魔攻 */
	public int mag;
	/** 命中 */
	public int hit;
	/** 闪避 */
	public int dodge;
	/** 暴击 */
	public int cirt;
	/** 抗暴 */
	public int resCirt;
	/** 物防 */
	public int ac;
	/** 魔防 */
	public int resist;
	/** 无视敌人物防 */
	public int ignoreAc;
	/** 无视敌人魔防 */
	public int ignoreResist;
	/** 初始治疗效果 */
	public int healEffect;
	/** 初始被治疗效果 */
	public int healedEffect;
	/** 基础经验 */
	public int baseExp;
	/** 是否主动攻击 */
	public int isAttack;
	/** 身负特殊技能 */
	public String ability;
	/** 触发特殊技能血量万分比 */
	public String callAbilityPerHP;
	/** 特殊技能参数 */
	public String abilityPar;
	/** 灵气 */
	public int wingsReiki;
	/** 是否任务共享 */
	public int shareType;
	/** 首杀TC */
	public String firstTc;
	/** 常规TC */
	public String tc;
	/** 预览TC */
	public String showTc;
	/** 队伍模式TC */
	public String teamTc;
	/** 个人TC */
	public String personTc;
	/** 对应任务 */
	public int exdTask;
	/** 基础荣誉 */
	public int baseHornor;
	/** 图标文件 */
	public String icon;
	/** 刷新时间 */
	public int refresh;
	/** 死亡后可复活时间 */
	public int deadTimeMs;
	/** 出生事件信息 */
	public String birthInfo;
	/** 死亡事件信息 */
	public String deathInfo;
	/** 怪物描述 */
	public String monsterDes;
	/** 怪物攻略 */
	public String monsterRaid;
	/** 金币增幅 */
	public int goldPerMonLv;
	/** 经验万分比 */
	public int expRatio;
	
	public void initProperty() {
		if (StringUtil.isNotEmpty(firstTc)) {
			if (firstTc.indexOf(":") != -1) {
				String[] tcStrs = firstTc.split(";");
				for (int i = 0; i < tcStrs.length; i++) {
					String tcStr = tcStrs[i];
					if (StringUtil.isNotEmpty(tcStr)) {
						String[] tcData = tcStr.split(":");
						convertTcData(tcData, firstTcMap);
					}
				}
			} else {
				convertTcData(new String[] { "0", firstTc }, personTcMap);
			}
		}
		
		if (StringUtil.isNotEmpty(personTc)) {
			if (personTc.indexOf(":") != -1) {
				String[] tcStrs = personTc.split(";");
				for (int i = 0; i < tcStrs.length; i++) {
					String tcStr = tcStrs[i];
					if (StringUtil.isNotEmpty(tcStr)) {
						String[] tcData = tcStr.split(":");
						convertTcData(tcData, personTcMap);
					}
				}
			} else {
				convertTcData(new String[] { "0", personTc }, personTcMap);
			}
		}
	}

	public void beforeProperty() { }
	
	public Integer getKey() {
		return this.iD;
	}
	
	public void convertTcData (String[] tcData, Map<Integer, String> tcMap){
	    int pro = Integer.parseInt(tcData[0]);
	    String tc = tcData[1];
	    if(pro == Const.PlayerPro.COMMON.value){
	        tcMap.put(Const.PlayerPro.YU_JIAN.value, tc);
	        tcMap.put(Const.PlayerPro.SHEN_JIAN.value, tc);
	        tcMap.put(Const.PlayerPro.CANG_LANG.value, tc);
	        tcMap.put(Const.PlayerPro.LI_NHU.value, tc);
	        tcMap.put(Const.PlayerPro.YI_XIAN.value, tc);
	    }else{
	        tcMap.put(pro, tc);
	    }
	};
}
