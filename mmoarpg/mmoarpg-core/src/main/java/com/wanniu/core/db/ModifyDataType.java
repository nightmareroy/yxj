package com.wanniu.core.db;

/**
 * @author agui
 *
 */
public enum ModifyDataType {

	STRING(1), MAP(2);

	public int value;

	private ModifyDataType(int tr) {
		this.value = tr;
	}
	
}
