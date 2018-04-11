package cn.qeng.gm.api;

import java.util.Date;

import com.alibaba.fastjson.JSON;

import cn.qeng.common.gm.RpcOpcode;
import cn.qeng.gm.util.DateUtils;

/**
 * 发送邮件.
 *
 * @author 小流氓(176543888@qq.com)
 */
public class MailSendAPI extends GmAPI {
	private int type;// 0-指定玩家 1-全服
	private String rids;
	private String title;
	private String content;
	private String sender = "系统";
	private String createRoleDate;// 最后创角时间
	private String minLevel;
	private String atta;
	private String mailId;

	public MailSendAPI(int type, String rids, String title, String content, Date createRoleDate, int minLevel, String atta, int mailId) {
		this.type = type;
		this.rids = rids;
		this.title = title;
		this.content = content;
		this.createRoleDate = DateUtils.formatyyyyMMddHHmmss(createRoleDate);
		this.minLevel = String.valueOf(minLevel);
		this.atta = atta;
		this.mailId = String.valueOf(mailId);
	}

	@Override
	protected short getOp() {
		return RpcOpcode.OPCODE_SEND_MAIL;
	}

	@Override
	protected String getArgs() {
		return JSON.toJSONString(new Object[] { type, rids, title, content, sender, createRoleDate, minLevel, atta, mailId });
	}
}