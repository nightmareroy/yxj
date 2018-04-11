package com.wanniu.login.vo;

public enum ServerShow {

	HIDE(0),	// 隐藏
	INNER(1),	// 对内
	OUTER(2);	// 对外
	
	public final int value;
	
	private ServerShow(int value) {
		this.value = value;
	}

}
