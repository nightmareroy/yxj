package com.wanniu.core.db.handler;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * 读取行回调
 * @author agui
 */
public abstract class RowReader {

	public void setParams(PreparedStatement pstmt)  { }
	
	public abstract void mapRow(ResultSet rs) throws SQLException;
	
}
