package com.wanniu.game.leaderBoard;

import java.util.ArrayList;
import java.util.List;

import pomelo.area.LeaderBoardHandler.LeaderBoardData;

public class LeaderBoardProto {
	public List<LeaderBoardData> s2c_lists; // 排行榜列表
	public LeaderBoardData s2c_myData; // 自己的排行信息

	public LeaderBoardProto() {
		s2c_lists = new ArrayList<LeaderBoardData>();
		s2c_myData = LeaderBoardData.newBuilder().build();
	}
}
