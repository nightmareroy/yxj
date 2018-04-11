package com.wanniu.game.mail;

import java.util.Date;
import java.util.List;

import com.wanniu.core.logfs.Out;
import com.wanniu.game.common.Const;
import com.wanniu.game.common.Utils;
import com.wanniu.game.data.base.DItemEquipBase;
import com.wanniu.game.item.ItemConfig;
import com.wanniu.game.item.ItemUtil;
import com.wanniu.game.item.po.PlayerItemPO;
import com.wanniu.game.player.GlobalConfig;
import com.wanniu.game.poes.PlayerBasePO;

import pomelo.area.MailHandler.Mail;
import pomelo.item.ItemOuterClass.MiniItem;

public class WNMail {
	public String id;
	public String playerId;
	public Date createTime;
	public List<PlayerItemPO> attachment;
	public String orderId;
	public String mailSender;
	public String mailSenderId;
	public String mailTitle;
	public String mailText;
	public int mailRead;
	public int mailIcon;
	public int mailId;
	public int mailType;
	public String mailSubType; // 邮件子类型(寄卖行,背包...)
	public int hadAttach;
	public int status;
	public int origin;// 邮件的正式来源

	public WNMail() {

	}

	public int compareTo(WNMail mail) {
		int flag = 0;
		if (this.attachment != null && this.attachment.size() > 0) {
			if (mail.attachment == null || mail.attachment.size() < 1) {
				return -1;
			}
		} else {
			if (mail.attachment != null && mail.attachment.size() > 0) {
				return 1;
			}
		}

		if (this.status != mail.status) {
			return this.status - mail.status;
		}

		if (this.createTime.after(mail.createTime))
			return -1;
		if (this.createTime.before(mail.createTime))
			return 1;
		return flag;

	}

	public Mail.Builder toMailBuilder(PlayerBasePO basePO) {
		Mail.Builder builder = Mail.newBuilder();
		builder.setId(this.id);
		builder.setMailId(0);
		builder.setMailType(this.mailType);
		builder.setMailIcon(this.mailIcon);
		builder.setMailSender(this.mailSender);
		builder.setMailSenderId(this.mailSenderId);
		builder.setMailTitle(this.mailTitle);
		builder.setMailText(this.mailText);
		String time = String.valueOf(this.createTime.getTime());
		builder.setCreateTime(time);
		builder.setStatus(this.status);
		builder.setMailRead(this.mailRead);
		builder.setHadAttach(this.hadAttach);
		if (this.hadAttach == Const.MailAttach.MAIL_ATTACH_CARRY.getValue()) {
			// 添加附件物品
			for (PlayerItemPO att : attachment) {
				{// Mini物品数据也不能删除，客户端还在用
					int groupCount = att.groupCount;
					DItemEquipBase prop = ItemConfig.getInstance().getItemProp(att.code);
					if (ItemConfig.getInstance().getSecondType(prop.type) == Const.ItemSecondType.virtual.getValue()) {
						if (att.speData != null) {
							groupCount = att.speData.worth;
						}
					}
					MiniItem item = ItemUtil.getMiniItemData(att.code, groupCount, Const.ForceType.getE(att.isBind)).build();
					builder.addAttachment(item);
				}

				// 详情
				builder.addNewAttachment(ItemUtil.createItemByDbOpts(att).getItemDetail(basePO));
			}
		}
		return builder;
	}

	public void addAttach(PlayerItemPO item) {
		if (attachment.size() <= GlobalConfig.Mail_Attach_Size) {
			attachment.add(item);
		} else {
			Out.warn("mail attach too many:", Utils.serialize(item));
		}
	}

}
