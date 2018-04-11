package com.wanniu.gm.handler;

import java.util.ArrayList;

import com.alibaba.fastjson.JSONArray;
import com.wanniu.core.logfs.Out;
import com.wanniu.core.util.DateUtil;
import com.wanniu.core.util.StringUtil;
import com.wanniu.game.common.Const.ForceType;
import com.wanniu.game.common.Const.GOODS_CHANGE_TYPE;
import com.wanniu.game.mail.MailCenter;
import com.wanniu.game.mail.MailUtil;
import com.wanniu.game.mail.data.MailData.Attachment;
import com.wanniu.game.mail.data.MailGmData;
import com.wanniu.game.player.PlayerDao;
import com.wanniu.game.player.PlayerUtil;
import com.wanniu.gm.GMEvent;
import com.wanniu.gm.GMResponse;
import com.wanniu.gm.GMStateResponse;

import cn.qeng.common.gm.RpcOpcode;

/**
 * GM邮件处理器
 * 
 * @author lxm
 *
 */
@GMEvent
public class MailHandler extends GMBaseHandler {

	public GMResponse execute(JSONArray arr) {
		int type = arr.getIntValue(0);// 0-指定玩家 1-全服
		String rids = arr.getString(1);
		String title = arr.getString(2);
		String content = arr.getString(3);
		String sender = arr.getString(4);
		String createRoleDate = arr.getString(5);// 最后创角时间
		String minLevel = arr.getString(6);
		String atta = arr.getString(7);
		String mailId = null;
		if (type == 1) {
			mailId = arr.getString(8);
		}

		ArrayList<Attachment> list = new ArrayList<>();
		if (StringUtil.isNotEmpty(atta)) {
			for (String its : atta.split(";")) {
				Attachment attachment = new Attachment();
				attachment.itemCode = its.split(":")[0];
				attachment.itemNum = Integer.parseInt(its.split(":")[1]);
				// GM发送的道具都是绑定的
				attachment.isBind = ForceType.BIND.getValue();
				list.add(attachment);
			}
		}
		MailGmData mail = new MailGmData();
		mail.mailSender = sender;
		mail.mailTitle = title;
		mail.mailText = content;
		if (StringUtil.isNotEmpty(minLevel)) {
			mail.minLevel = Integer.parseInt(minLevel);
		}
		mail.attachments = list;

		if (type == 0) {
			String[] ids = rids.split(";");
			for (int i = 0; i < ids.length; i++) {
				String id = ids[i];
				if (PlayerUtil.getPlayerBaseData(id) == null) {
					// ID不存在根据名字找
					String rid = PlayerDao.getIdByName(id);
					if (rid == null) {
						return new GMStateResponse(-2);
					}
					ids[i] = rid;
				}
			}
			MailUtil.getInstance().sendMailToSomePlayer(ids, mail, GOODS_CHANGE_TYPE.GMT);
		} else {
			// 全局邮件
			if (StringUtil.isNotEmpty(createRoleDate)) {
				mail.createRoleDate = DateUtil.format(createRoleDate);
			}
			MailCenter.getInstance().addServerMail(mailId, mail, GOODS_CHANGE_TYPE.GMT);
		}
		Out.info("GM邮件记录 json=", arr.toJSONString());
		return new GMStateResponse(1);
	}

	public short getType() {
		return RpcOpcode.OPCODE_SEND_MAIL;
	}
}
