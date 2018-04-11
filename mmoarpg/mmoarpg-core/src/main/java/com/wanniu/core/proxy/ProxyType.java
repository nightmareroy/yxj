package com.wanniu.core.proxy;

/**
 * proxy通信协议
 * @author agui
 */
public interface ProxyType {

	short JOIN 				=  0xff;
	short PING 				=  0xf2;
	
	short REQUEST 			=  0x101;
	short ENTER 			=  0x102;
	short LEAVE 			=  0x103;
	short AREA_RECEIVE 		=  0x1f1;
	short DISPONSE 			=  0x1f2;
	short DIE 				=  0x1f3;

	short QUERY 			=  0x201;
	short RESULT 			=  0x302;

	short PLAYER_DATA 		=  0x401;
	short PLAYER_EVENT 		=  0x402;

	short TEAM 				=  0x4F1;
	
	short CHANGE_AREA 		=  0x4F2;
	short PLAYER_RECEIVE 	=  0x4F3;

	short SOLO_MATCH 		=  0x501;
	short SOLO_QUIT 		=  0x502;
	short SOLO_MATCH_OK		=  0x503;
	short SOLO_JOIN			=  0x504;
	
	enum ProxyMethod {
		
		M_DISPATCHER(10),
		M_CREATE(11),
		M_DISPONSE(12),
		M_LINES(13),
		M_TRANS_LINE(14);
		
		public final int value;
		private ProxyMethod(int value) {
			this.value = value;
		}
		
	}
	
}
