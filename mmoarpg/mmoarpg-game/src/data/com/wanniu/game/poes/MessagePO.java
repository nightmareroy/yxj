package com.wanniu.game.poes;

import java.util.Date;
import java.util.Map;

import com.wanniu.game.message.MessageData.MessageData_Data;

public class MessagePO {

	public String id;
	
	public Date createTime;
	public String createPlayerId;
	public int messageType;
	public Map<String,String> strMsg;
	public MessageData_Data data;
	
	public MessagePO() {
		
	}
}
