package cn.qeng.gm.api;

import com.alibaba.fastjson.JSON;

import cn.qeng.common.gm.RpcOpcode;

/**
 * 充值补单的接口.
 *
 * @author 小流氓(176543888@qq.com)
 */
public class RechargeAPI extends GmAPI {
	private String roleType;
	private String roleId;
	private int productId;
	private int flag = 0;// 0=模拟充值，1=充值补单

	public RechargeAPI(int type, String roleId, int productId, int flag) {
		this.roleType = type == 0 ? "id" : "name";
		this.roleId = roleId;
		this.productId = productId;
		this.flag = flag;
	}

	@Override
	protected short getOp() {
		return RpcOpcode.OPCODE_RECHARGE;
	}

	@Override
	protected String getArgs() {
		return JSON.toJSONString(new Object[] { roleType, roleId, productId, flag });
	}
}