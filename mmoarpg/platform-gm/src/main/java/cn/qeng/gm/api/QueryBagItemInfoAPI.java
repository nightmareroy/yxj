package cn.qeng.gm.api;

import com.alibaba.fastjson.JSON;

import cn.qeng.common.gm.RpcOpcode;

/**
 * 查询背包物品信息.
 *
 * @author 小流氓(176543888@qq.com)
 */
public class QueryBagItemInfoAPI extends GmAPI {
	private String playerId;
	private int type;// 0=背包物品,1=身上装备,2=仓库物品

	public QueryBagItemInfoAPI(String playerId, int type) {
		this.type = type;
		this.playerId = playerId;
	}

	@Override
	protected short getOp() {
		return RpcOpcode.OPCODE_QUERY_BAG_ITEM_INFO;
	}

	@Override
	protected String getArgs() {
		return JSON.toJSONString(new Object[] { playerId, type });
	}
}