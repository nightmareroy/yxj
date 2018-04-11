package com.wanniu.core.db.handler;

import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * 读取行回调
 * @author agui
 */
public interface ParamMapper {

	public void setParams(PreparedStatement pstmt, int row) throws SQLException;
	
}
