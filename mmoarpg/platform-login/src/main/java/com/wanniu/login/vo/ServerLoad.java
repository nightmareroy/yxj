package com.wanniu.login.vo;

public enum ServerLoad {

	MAINTAIN(0, "维护"), 
	SMOOTH(1, "流畅"), 
	BUSY(2, "繁忙"), 
	FULL(3, "爆满");

	public final int value;

	private ServerLoad(int value, String desc) {
		this.value = value;
	}

}
