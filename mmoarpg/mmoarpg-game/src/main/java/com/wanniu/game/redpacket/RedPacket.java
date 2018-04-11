package com.wanniu.game.redpacket;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

import com.wanniu.core.logfs.Out;
import com.wanniu.core.util.RandomUtil;
import com.wanniu.game.player.GlobalConfig;


public class RedPacket {
	public String id;
	public String providerId;
	public String providerName;
	public String providerGuildId;
	public Date dispatchDate;
	public int totalNum;
	public int count;
//	public int channelType;//0世界红包 1仙盟红包
	public int fetchType;//0口令红包 1普通红包
	public int benifitType;//0元宝红包 1银币红包
	public String msg;
	public List<RedPacketFetchInfo> redPacketFetchInfoList;
//	public Map<String, Date> 
	public RedPacket() {
		redPacketFetchInfoList=new LinkedList<>();
	}
	
	public RedPacket(String providerId,String providerName,String providerGuildId,int totalNum,
			int count,int fetchType,int benifitType,String msg) {
		this();
		if(totalNum<=0||count<=0) {
			Out.error("参数错误！");
			return;
		}
		
		id=UUID.randomUUID().toString();
		this.providerId=providerId;
		this.providerName=providerName;
		this.providerGuildId=providerGuildId;
		this.dispatchDate=new Date();
		this.totalNum=totalNum;
		this.count=count;
//		this.channelType=channelType;
		this.fetchType=fetchType;
		this.benifitType=benifitType;
		this.msg=msg;

		int scale=benifitType==0?1:GlobalConfig.Red_HongbaoRatio;
		int per=totalNum*scale/count;
		int left=totalNum*scale%count;

		
		
		

		
		for(int i=0;i<count;i++) {
			RedPacketFetchInfo redPacketFetchInfo=new RedPacketFetchInfo(per+(left-->0?1:0));
			redPacketFetchInfoList.add(redPacketFetchInfo);
		}
		for(int i=0;i<count;i++) {
			RedPacketFetchInfo redPacketFetchInfo1=redPacketFetchInfoList.get(i);
			RedPacketFetchInfo redPacketFetchInfo2=redPacketFetchInfoList.get((i+1)%count);
			int randomValue=RandomUtil.random(per);
			redPacketFetchInfo1.num+=randomValue;
			redPacketFetchInfo2.num-=randomValue;
		}
	}
}
