package com.wanniu.core.db;

import com.wanniu.core.util.StringUtil;

/**
 * @author agui
 */
public class QueryVo {
	
	/** REDIS表的KEY(也是mysql中的表名) */
	private String table;
	/** 条件(主键值) */
	private String conVal;
	
	public int type;

	public QueryVo(String tr, String conVal) {
		this.table = tr;
		this.conVal = conVal;
		this.type = StringUtil.isEmpty(conVal) ? 0 : 1;
	}

	/**
	 * @return the queryTR
	 */
	public String getQueryTR() {
		return table;
	}

	/**
	 * @return the queryPKey
	 */
	public String getConVal() {
		return conVal;
	}
}
