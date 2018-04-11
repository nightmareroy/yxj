package com.wanniu.core.db.handler;


/**
 * 保存或更新的回调函数
 * @author agui
 */
public interface ISaveOrUpdate {

	String getSaveSQL();
	Object[] getSaveParams();
	
	String getUpdateSQL();
	Object[] getUpdateParams();
}
