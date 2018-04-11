// **********************************************************************
//
// Copyright (c) 2003-2015 ZeroC, Inc. All rights reserved.
//
// **********************************************************************

#pragma once

module Xmds
{
	/**onFinishPickItem*/
	class FinishPickItem {
		string itemIcon;
		int quality;
		int num;
	};
	
	/**getPlayerPKInfoData*/
	class PlayerPKInfoData {
		int pkMode;
		int pkValue;
		int pkLevel;
	};
	/**canTalkWithNpc*/
	class CanTalkWithNpc {
		int templateId;
		bool canTalk;
	};
	
	/**refreshPlayerPropertyChange*/
	class RefreshPlayerPropertyChange {
		string key;
		int changeType;
		int valueType;
		int value;
		int duration;
		long timestamp;
	};
	
	/**getPlayerData*/
	class SkillDataICE {
		int skillId;
		long skillTime;
	};
	sequence<SkillDataICE> SkillDataSeq;
	class GetPlayerData {
		float x;
        float y;
        float direction;
        int hp;
        int mp;
        int pkMode;
        int pkValue;
        int pkLevel;
        SkillDataSeq skillData;
        int combatState;
	};
	
	/**canTalkWithNpc*/
	class CanTalkWithNpcResult{
		int templateId;
		bool canTalk;
	};

	["amd"] interface XmdsManager
	{
		//游戏服通知战斗服数据
		void notifyBattleServer(string instanceId, string name, string data);

		// 获取跨场景出生坐标
		string getBornPlace(string instanceId, int areaId);

		//获取场景副本所有的单位信息
		string getAllUnitInfo(string instanceId);

		//获取场景副本所有的npc信息
		string getAllNpcInfo(string instanceId);

		// 获取玩家数据
		string getPlayerData(string playerId,bool changeArea);

		// 怪物死亡掉落
		void onMonsterDiedDrops(string instanceId,string data);	
	
		// 复活角色
		void revivePlayer(string playerId, string data);
	
		// 角色坐骑
		void refreshSummonMount(string playerId, int time, int isUp);
	
		// 玩家战斗信息同步
		void refreshPlayerBasicData(string playerId, string basic);
		
		// 玩家组队信息同步
		void refreshPlayerTeamData(string playerId, string uuidList);
	
		// 玩家背包剩余格子数量信息同步
		void refreshPlayerRemainBagCountData(string playerId, int remainCount);

		// 玩家背包剩余格子数量信息同步
		void refreshPlayerRemainTeamBagCountData(string playerId, int remainCount);

		// 玩家宠物全部信息变更
		void refreshPlayerPetDataChange(string playerId, int type, string data);

		// 玩家战斗信息同步
		void refreshPlayerBattleEffect(string playerId, string effects);
	
		// 添加玩家属性
		void addPlayerAtt(string playerId, string notifyPlayerIds,  string key, int value);

		// 玩家技能信息同步
		void refreshPlayerSkill(string playerId, int operateID , string skills);
	
		// 玩家时装信息同步
		void refreshPlayerAvatar(string playerId, string avatars);

		// 获取玩家PK信息
		string getPlayerPKInfoData(string playerId);   

		// 获取玩家技能cd信息
		string getPlayerSkillCDTime(string playerId);
		
		// 获取玩家技能cd信息
		string canTalkWithNpc(string playerId, int npcId);   

		// 角色关联属性变更
		void refreshPlayerPropertyChange(string playerId, string data);

		// 玩家pk模式同步 
		void refreshPlayerPKMode(string playerId, int mode);  

		// 玩家pk值同步
		void refreshPlayerPKValue(string playerId, int value);   

		// 拾取道具 data{itemIcon, quality, num}
		void onFinishPickItem(string playerId, string data);	

		// 宠物技能信息同步
        void refreshPlayerPetSkillChange(string playerId, int operateID , string skills);

		// 宠物加血
		void refreshPlayerPetPropertyChange(string playerId, string data);

		// 往场景内种怪
		void addUnits(string instanceId, string data);

		// 切换宠物pk模式
		void refreshPlayerPetFollowModeChange(string playerId, int mode);

		// 玩家准备就绪
		void playerReady(string playerId);

		// 刷新队伍数据
		void refreshTeamData(string playerId, string data);

		// 设置自动战斗
		void autoBattle(string instanceId, string playerId, bool enable);
		
		// 获取场景统计
		string getInstanceStatistic(string instanceId);
		
		//刷NPC
		int addUnit(string instanceId, int unitTemplateID, string data);

		//移除NPC
		void removeUnit(string instanceId, int unitId);
		

		//================游戏服没用到的协议===============
		//获取场景副本实例静态数据
		string getZoneStaticData(string instanceId);
		//获取场景副本中有效的刷新区域
		string getZoneRegions(string instanceId);
		//获取所有玩家UUID信息
		string getAllPlayerUUID();
		// 获取玩家宠物数据
		string getPlayerPetData(string playerId);
		// 获取玩家统计
		string getPlayerStatistic(string playerId);		
		// 宠物加血判断
		bool canUseItem(string playerId);	
		
		// 帮助复活角色
		void helpRevivePlayer(string playerId, string revivePlayerId, int time);
		// 拾取道具 data{itemIcon, quality, num}
		//void onPickItem(string playerId,string data);	
		// 玩家宠物基础信息变更
		void refreshPlayerPetBaseInfoChange(string playerId, string data);
		// 触发特殊战斗事件
		void triggrBattleFunction(string playerId, int eventId, string data);   
		// 玩家pk等级同步   
		void refreshPlayerPKLevel(string playerId, int level);
	}; 
};

