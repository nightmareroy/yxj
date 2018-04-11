package com.wanniu.core.common;

public class StringString implements java.io.Serializable {
	private static final long serialVersionUID = 1L;

	public String key;
	public String value;

	public StringString() {

	}

	public StringString(String key, String value) {
		this.key = key;
		this.value = value;
	}

}
