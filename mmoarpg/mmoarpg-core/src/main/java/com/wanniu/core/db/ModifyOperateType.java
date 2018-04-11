package com.wanniu.core.db;

/**
 * 操作类型
 * @author agui
 */
public enum ModifyOperateType {

	UPDATE(1), INSERT(2), DELETE(0);

	public int value;

	private ModifyOperateType(int tr) {
		this.value = tr;
	}
}
