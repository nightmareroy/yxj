package com.wanniu.game.mail.data;

import java.util.Date;
import java.util.List;

import com.wanniu.game.item.po.PlayerItemPO;

public class MailData {

	public static class Attachment {
		public String itemCode;
		public int itemNum;
		public int isBind;
	}

	public int mailId;
	/**
	 * mailType: //邮件类型
	 */
	public int mailType;

	public MailData() {

	}

	/**
	 * attachments : [{ itemCode:'scr01', itemNum:2, isBind:undefined, forceType:0
	 * }, { itemCode: 'ridesk01', itemNum:1, isBind:0, forceType:1 }]
	 */
	public List<Attachment> attachments;
	/**
	 * entityItems:[] //实体道具二进制数据列表
	 */
	public List<PlayerItemPO> entityItems;
	/**
	 * tcCode : 通过tcCode随机生成
	 */
	public String tcCode;

	/**
	 * 批次id，维护时可用来对GM邮件批量删除
	 */
	public String orderId;

	public Date createTime;
}
