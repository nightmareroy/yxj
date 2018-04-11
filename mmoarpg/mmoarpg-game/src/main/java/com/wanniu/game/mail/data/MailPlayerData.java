package com.wanniu.game.mail.data;

import com.wanniu.game.common.Const;

/**
 * 玩家邮件 mailData: { mailType //邮件类型 mailSender //发送这名字 mailSenderId //发送这名字id
 * mailTitle //标题 mailText //内容 mailRead //读取处理方式 mailIcon //发送这职业 Code附件物品,
 * isBind:走配置不用填，0:未绑定 1：绑定 2：装绑,
 * forceType:强制绑定类型，参考consts.ForceType类型,不传则为consts.ForceType.DEFAULT
 * attachments : [{ itemCode:'scr01', itemNum:2, isBind:undefined }, { itemCode:
 * 'ridesk01', itemNum:1, isBind:undefined }] tcCode : 通过tcCode随机生成，没有则不填
 * entityItems:[] //实体道具二进制数据列表 } eg:newMail = createMail({mailType:1,
 * mailSender:'tom', msilSenderId:'16777ab0-10db-11e6-962e-2bfa29d62541',
 * maildTitle:'快来主城'， mailText:'我要跟你单挑'， mailRead:1, mailIcon:1);
 */
public class MailPlayerData extends MailData {
	public String mailSender;
	public String mailSenderId;
	public String mailTitle;
	public String mailText;
	public int mailRead;
	public int mailIcon;

	public MailPlayerData() {
		this.mailType = Const.MailType.MAIL_PLAYER_TYPE.getValue();
	}

}
