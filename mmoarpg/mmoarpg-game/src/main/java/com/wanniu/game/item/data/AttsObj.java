package com.wanniu.game.item.data;

/**
 * 装备发送给客户端的数据对象
 * 
 * @author Yangzz
 *
 */
public class AttsObj {
	public String key;
	public int value;
	public int par;
	public int min;
	public int max;

	public AttsObj() {

	}

	public AttsObj(String key, int value, int par, int min, int max) {
		this.key = key;
		this.value = value;
		this.par = par;
		this.min = min;
		this.max = max;
	}
}