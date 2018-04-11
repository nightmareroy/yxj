package com.wanniu.core.db;

import com.wanniu.core.GConfig;

/**
 * @author agui
 *
 */
public class DBType {
	public static final short JOIN = GConfig.getInstance().getShort("db.type.join", (short) 0x33f1);

	public static final short PING = GConfig.getInstance().getShort("db.type.ping", (short) 0x33f2);

	public static final short UPDATE = GConfig.getInstance().getShort("db.type.update", (short) 0x33f3);

	public static final short QUERY = GConfig.getInstance().getShort("db.type.query", (short) 0x33f4);
	
	public static final short NOTIFY = GConfig.getInstance().getShort("db.type.notify", (short) 0x33f5);
}
