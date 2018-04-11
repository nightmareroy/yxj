// **********************************************************************
//
// Copyright (c) 2003-2015 ZeroC, Inc. All rights reserved.
//
// **********************************************************************

#pragma once

#include <Ice/Identity.ice>

module Pomelo
{ 

	//场景管理事件回调
	interface ZoneManagerCallback
	{
	   //事件通知
	   void  eventNotify(string eventType,string msg);
	};

	//场景管理
	interface ZoneManager
	{
		/************************************场景管理器相关协议********************************************/
		//设置事件回调
		void  setCallback(Ice::Identity ident);

		/************************************场景副本相关协议********************************************/

		//创建场景副本实例
		["amd"] void createZoneRequest(string gameServerId, int mapTemplateId, string instanceId, string data);

		//删除场景副本实例
		["amd"] void destroyZoneRequest(string instanceId);

		/************************************玩家相关协议********************************************/

		// 清空所有玩家 
		["amd"] void clearAllPlayersRequest();

		// 获取总玩家数量 
		["amd"] int getAllPlayerCountRequest();

		// 玩家进入副本 
		["amd"] void playerEnterRequest(string playerId, string instanceId, string data);

		//玩家离开副本 
		["amd"] void playerLeaveRequest(string playerId, string instanceId, bool keepObject);

		//玩家网络状况改变 {connected, disconnected, }
		["amd"] void playerNetStateChanged(string playerId, string state);

		["amd"] string getServerState();
	};

};

