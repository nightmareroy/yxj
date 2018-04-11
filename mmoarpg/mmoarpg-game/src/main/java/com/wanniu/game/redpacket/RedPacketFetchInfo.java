package com.wanniu.game.redpacket;

import java.util.Date;

public class RedPacketFetchInfo {
//	public int id;
	public int num;
	public String fetcherId;
	public String fetcherName;
	public Date fetchDate;
	
	public RedPacketFetchInfo() {
//		id=-1;
		num=0;
		fetcherId=null;
		fetcherName=null;
		fetchDate=null;
	}
	public RedPacketFetchInfo(int num) {
//		this.id=id;
		this.num=num;
	}
	
	public void SetFetcher(String fetcherId,String fetcherName) {
		this.fetcherId=fetcherId;
		this.fetcherName=fetcherName;
		this.fetchDate=new Date();
	}
}
