package com.wanniu.core.pay;

/**
 * 充值服务器通信协议
 * 
 * @author lxm
 */
public class PayType {

	public static final short JOIN = 0xf1;

	public static final short PING = 0xf2;

	public static final short CREATE_ORDER = 0xf3;

	public static final short VALID_ORDER = 0xf4;

	public static final short PAY_SUCCESS = 0xf5;
}
