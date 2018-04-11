package cn.qeng.gm.api;

import com.alibaba.fastjson.JSON;

import cn.qeng.common.gm.RpcOpcode;

/**
 * 回收邮件.
 *
 * @author 小流氓(176543888@qq.com)
 */
public class MailDeleteAPI extends GmAPI {
	private String mailId;

	public MailDeleteAPI(int mailId) {
		this.mailId = String.valueOf(mailId);
	}

	@Override
	protected short getOp() {
		return RpcOpcode.OPCODE_DELETE_MAIL;
	}

	@Override
	protected String getArgs() {
		return JSON.toJSONString(new Object[] { mailId });
	}
}