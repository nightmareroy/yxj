package cn.qeng.gm.api;

import com.alibaba.fastjson.JSON;

import cn.qeng.common.gm.RpcOpcode;

/**
 * 查询道友信息.
 *
 * @author 小流氓(176543888@qq.com)
 */
public class QueryDaoYouInfoAPI extends GmAPI {
	private String name;

	public QueryDaoYouInfoAPI(String name) {
		this.name = name;
	}

	@Override
	protected short getOp() {
		return RpcOpcode.OPCODE_QUERY_DAOYOU_INFO;
	}

	@Override
	protected String getArgs() {
		return JSON.toJSONString(new Object[] { name });
	}
}