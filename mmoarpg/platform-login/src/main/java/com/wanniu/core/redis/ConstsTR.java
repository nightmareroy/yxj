package com.wanniu.core.redis;

/**
 * redis key 
 * @author Yangzz
 */
public enum ConstsTR {
	// 登录公告
  	announcement("announcement");
	
	public String value;
	private ConstsTR(String value) {
		this.value = value;
	}
}
