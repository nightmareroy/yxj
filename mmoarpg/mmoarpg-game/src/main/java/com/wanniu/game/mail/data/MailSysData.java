package com.wanniu.game.mail.data;

import java.util.Map;

import com.wanniu.game.common.Const;

/**
 * GM，系统邮件 mailData: { mailType: //邮件类型 mailId://配置id Code附件物品,
 * isBind:走配置不用填，0:未绑定 1：绑定 2：装绑,
 * forceType:强制绑定类型，参考consts.ForceType类型,不传则为consts.ForceType.DEFAULT
 * attachments : [{ itemCode:'scr01', itemNum:2, isBind:undefined, forceType:0
 * }, { itemCode: 'ridesk01', itemNum:1, isBind:0, forceType:1 }] tcCode :
 * 通过tcCode随机生成 entityItems:[] //实体道具二进制数据列表 replace:{guildName:'我来自天朝', level:
 * 20} //邮件内容替换结构，会根据{key}来替换值 } eg: var newMail = createMail({mailType:2,
 * mailId: 20205, attachments:[{ itemCode:'scr01', itemNum:2 }],
 * replace:{guildName:'天朝'} });
 *
 */
public class MailSysData extends MailData {

	public Map<String, String> replace;
	public String key;
	// public MailSysData(MailID mailId) {
	// this.mailType = Const.MailType.MAIL_SYSTEM_TYPE.getValue();
	// this.mailId = mailId.getValue();
	// }

	// public MailSysData(int mailId) {
	// this.mailType = Const.MailType.MAIL_SYSTEM_TYPE.getValue();
	// this.mailId = mailId;
	// }

	public MailSysData(String key) {
		this.key = key;
		this.mailType = Const.MailType.MAIL_SYSTEM_TYPE.getValue();
	}
}
