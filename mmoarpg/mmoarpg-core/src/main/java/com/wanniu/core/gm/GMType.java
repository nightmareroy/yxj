package com.wanniu.core.gm;

import com.wanniu.core.GConfig;

/**
 * 登录服务器通信协议
 * @author agui
 */
public class GMType {

	public static final short JOIN 						=  GConfig.getInstance().getShort("gm.type.join", (short)0xff);

	public static final short PING 					=  GConfig.getInstance().getShort("gm.type.ping", (short)0xf2);

}
