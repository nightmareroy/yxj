package com.wanniu.game.poes;

public class GuildFortInfoPO {
	public int attendTimes;
	public int winTimes;
	
	public float getWinRate() {
		if(attendTimes <=0) {
			return 0f;
		}
		
		return winTimes/attendTimes*100;
	}
	
	public void onStat(boolean isWin) {
		this.attendTimes++;
		if(isWin) {
			this.winTimes++;
		}
	}
}
