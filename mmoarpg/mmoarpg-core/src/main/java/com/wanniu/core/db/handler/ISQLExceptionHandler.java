package com.wanniu.core.db.handler;

import java.sql.SQLException;

public interface ISQLExceptionHandler {

	void exceptionSQL(SQLException e);
	
	void exceptionSQL(String logName, Exception e);
	
}
