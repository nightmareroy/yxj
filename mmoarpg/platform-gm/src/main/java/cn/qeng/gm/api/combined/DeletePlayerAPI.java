package cn.qeng.gm.api.combined;

import java.util.Date;

import com.alibaba.fastjson.JSON;
import com.wanniu.util.DateUtil;

import cn.qeng.common.gm.RpcOpcode;
import cn.qeng.gm.api.GmAPI;

/**
 * 删除小号.
 *
 * @author 小流氓(176543888@qq.com)
 */
public class DeletePlayerAPI extends GmAPI {

	private String openDate;

	public DeletePlayerAPI(Date openDate) {
		this.openDate = DateUtil.format(openDate, DateUtil.F_yyyyMMdd);
	}

	@Override
	protected short getOp() {
		return RpcOpcode.OPCODE_DELETE_PLAYER;
	}

	@Override
	protected String getArgs() {
		return JSON.toJSONString(new Object[] { openDate });
	}
}