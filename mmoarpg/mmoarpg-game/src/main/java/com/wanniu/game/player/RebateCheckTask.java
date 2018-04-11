/*
 * Copyright © 2017 qeng.cn All Rights Reserved.
 * 
 * 感谢您加入清源科技，不用多久，您就会升职加薪、当上总经理、出任CEO、迎娶白富美、从此走上人生巅峰
 * 除非符合本公司的商业许可协议，否则不得使用或传播此源码，您可以下载许可协议文件：
 * 
 * 		http://www.noark.xyz/qeng/LICENSE
 *
 * 1、未经许可，任何公司及个人不得以任何方式或理由来修改、使用或传播此源码;
 * 2、禁止在本源码或其他相关源码的基础上发展任何派生版本、修改版本或第三方版本;
 * 3、无论你对源代码做出任何修改和优化，版权都归清源科技所有，我们将保留所有权利;
 * 4、凡侵犯清源科技相关版权或著作权等知识产权者，必依法追究其法律责任，特此郑重法律声明！
 */
package com.wanniu.game.player;

import java.util.ArrayList;
import java.util.HashMap;

import org.apache.commons.lang3.StringUtils;

import com.wanniu.core.logfs.Out;
import com.wanniu.game.common.Const.GOODS_CHANGE_TYPE;
import com.wanniu.game.item.VirtualItemType;
import com.wanniu.game.mail.MailUtil;
import com.wanniu.game.mail.SysMailConst;
import com.wanniu.game.mail.data.MailData.Attachment;
import com.wanniu.game.mail.data.MailSysData;
import com.wanniu.redis.GlobalDao;

import cn.qeng.common.gm.RedisKeyConst;

/**
 * 充值返利任务.
 *
 * @author 周明凯(zhoumingkai@qeng.cn)
 */
public class RebateCheckTask implements Runnable {
	private static int rate = 20;// 充值返回倍率

	private final String uid;
	private final String palyerId;
	private final String palyerName;

	public RebateCheckTask(WNPlayer player) {
		this.uid = player.getUid();
		this.palyerId = player.getId();
		this.palyerName = player.getName();
	}

	@Override
	public void run() {
		try {
			String value = GlobalDao.hget(RedisKeyConst.REDIS_KEY_REBATE, uid);
			if (StringUtils.isNotEmpty(value)) {
				if (GlobalDao.hremove(RedisKeyConst.REDIS_KEY_REBATE, uid) > 0) {
					int diamond = Integer.parseInt(value) / 100 * rate;
					Out.info("充值返利.uid=", uid, ",playerId=", palyerId, ",name=", palyerName, ",rmb=", value, " 分, diamond=", diamond);

					MailSysData mail = new MailSysData(SysMailConst.CCBReward1);
					mail.attachments = new ArrayList<>();

					// 充值元宝
					Attachment att = new Attachment();
					att.itemCode = VirtualItemType.DIAMOND.getItemcode();
					att.itemNum = diamond;
					mail.attachments.add(att);

					mail.replace = new HashMap<>();
					MailUtil.getInstance().sendMailToOnePlayer(palyerId, mail, GOODS_CHANGE_TYPE.PAY_REBATE);
				}
			}
		} catch (Exception e) {
			Out.info("充值返利异常.uid=", uid, ",playerId=", palyerId, ",name=", palyerName, e);
		}
	}
}