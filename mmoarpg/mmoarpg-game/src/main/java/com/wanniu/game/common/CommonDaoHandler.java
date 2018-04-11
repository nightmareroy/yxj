package com.wanniu.game.common;

import com.wanniu.core.db.handler.DBHandler;

/**
 * 数据库通用操作对象
 * 
 * @author Yangzz
 */
public class CommonDaoHandler extends DBHandler {

	private String sql;

	@Override
	public void run() {
		execute(sql);
	}

	public CommonDaoHandler(String sql) {
		this.sql = sql;
	}
}
