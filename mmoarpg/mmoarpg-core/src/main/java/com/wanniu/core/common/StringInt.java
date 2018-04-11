package com.wanniu.core.common;

/** int2string模版 */
public class StringInt implements java.io.Serializable {
	private static final long serialVersionUID = 1L;

	public int intValue;
	public String strValue;

	public StringInt() {

	}

	public StringInt(int intValue, String strValue) {
		this.intValue = intValue;
		this.strValue = strValue;
	}
	
	public StringInt(String strValue, int intValue) {
		this.intValue = intValue;
		this.strValue = strValue;
	}
}
