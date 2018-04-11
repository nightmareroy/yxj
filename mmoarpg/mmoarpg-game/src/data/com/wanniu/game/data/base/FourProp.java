package com.wanniu.game.data.base;

/**
 * 对应配置表中4个属性的数据
 * @author Yangzz
 *
 */
public class FourProp {

	public String prop;
	
	public int par;
	
	public int min;
	
	public int max;

	public FourProp() {
		
	}
	
	public FourProp(String prop, int par, int min, int max) {
		this.prop = prop;
		this.par = par;
		this.min = min;
		this.max = max;
	}
	
}
