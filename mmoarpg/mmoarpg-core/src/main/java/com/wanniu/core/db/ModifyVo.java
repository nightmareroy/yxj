package com.wanniu.core.db;

/**
 * @author agui
 */
public class ModifyVo {
	
	/** REDIS表的KEY(也是mysql中的表名) */
	private String modifyTR;

	/** KEY */
	private String modifyPKey;

	/** 操作(ModifyOperateType) */
	private int modifyOperate;

	/** 修改的数据类型 (ModifyDataType) */
	private int modifyDataType;

	public ModifyVo(String modifyTR, String modifyPKey, ModifyOperateType modifyOperate) {
		this.modifyTR = modifyTR;
		this.modifyPKey = modifyPKey;
		this.modifyOperate = modifyOperate.value;
	}

	public ModifyVo(String modifyTR, String modifyPKey, ModifyOperateType modifyOperate, ModifyDataType modifyDataType) {
		this(modifyTR, modifyPKey, modifyOperate);
		this.modifyDataType = modifyDataType.value;
	}

	/**
	 * @return the modifyTR
	 */
	public String getModifyTR() {
		return modifyTR;
	}

	/**
	 * @return the modifyPKey
	 */
	public String getModifyPKey() {
		return modifyPKey;
	}

	/**
	 * @return the modifyOperate
	 */
	public int getModifyOperate() {
		return modifyOperate;
	}

	/**
	 * @return the modifyDataType
	 */
	public int getModifyDataType() {
		return modifyDataType;
	}

}
