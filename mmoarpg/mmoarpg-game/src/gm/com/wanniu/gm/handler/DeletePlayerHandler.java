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
package com.wanniu.gm.handler;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.alibaba.fastjson.JSONArray;
import com.wanniu.core.db.GCache;
import com.wanniu.core.logfs.Out;
import com.wanniu.core.util.DateUtil;
import com.wanniu.game.common.Const.GOODS_CHANGE_TYPE;
import com.wanniu.game.common.Const;
import com.wanniu.game.common.ConstsTR;
import com.wanniu.game.item.VirtualItemType;
import com.wanniu.game.mail.MailUtil;
import com.wanniu.game.mail.SysMailConst;
import com.wanniu.game.mail.data.MailData.Attachment;
import com.wanniu.game.mail.data.MailSysData;
import com.wanniu.game.player.GlobalConfig;
import com.wanniu.game.poes.PlayerPO;
import com.wanniu.game.prepaid.po.PrepaidPO;
import com.wanniu.gm.GMEvent;
import com.wanniu.gm.GMResponse;
import com.wanniu.gm.GMStateResponse;
import com.wanniu.redis.GameDao;
import com.wanniu.redis.PlayerPOManager;

import cn.qeng.common.gm.RpcOpcode;

/**
 * 删除小号.
 *
 * @author 周明凯(zhoumingkai@qeng.cn)
 */
@GMEvent
public class DeletePlayerHandler extends GMBaseHandler {
	private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DateUtil.F_yyyyMMdd);

	@Override
	public GMResponse execute(JSONArray arr) {
		Out.warn("开始删除小号...");

		//
		Out.warn("发补偿...");
		LocalDate openDate = LocalDate.parse(arr.getString(0), formatter);
		Map<String, String> names = GCache.hgetAll("NAME_MODULE");
		for (String playerId : names.values()) {
			try {
				// 首充双倍
				{
					PrepaidPO po = PlayerPOManager.findPO(ConstsTR.prepaidNewTR, playerId, PrepaidPO.class);
					po.first_buy_record.clear();
				}

				// 合服补偿...
				PlayerPO player = GameDao.get(playerId, ConstsTR.playerTR, PlayerPO.class);
				LocalDate createTime = LocalDate.parse(DateUtil.format(player.createTime, DateUtil.F_yyyyMMdd), formatter);
				int daysDiff = (int) Math.min(Math.max(0, ChronoUnit.DAYS.between(openDate, createTime)), GlobalConfig.Combine_MaxDay);
				Out.info("合服补偿 playerId=", playerId, ",day=", daysDiff);
				if (daysDiff > 0) {
					String mailKey = SysMailConst.CombineCompensate;
					MailSysData mail = new MailSysData(mailKey);
					mail.attachments = new ArrayList<>();
					{// 补偿经验
						Attachment item = new Attachment();
						item.itemCode = VirtualItemType.EXP.getItemcode();
						item.itemNum = daysDiff * GlobalConfig.Combine_Exp;
						item.isBind = Const.BindType.BIND.getValue();
						mail.attachments.add(item);
					}
					{// 补偿修为
						Attachment item = new Attachment();
						item.itemCode = VirtualItemType.UPEXP.getItemcode();
						item.itemNum = daysDiff * GlobalConfig.Combine_UpExp;
						item.isBind = Const.BindType.BIND.getValue();
						mail.attachments.add(item);
					}
					{// 补偿物品mjbx-1:10,mjbx-2:10,mjbx-3:10
						for (String is : GlobalConfig.Combine_Items.split(",")) {
							String[] i = is.split(":");
							Attachment item = new Attachment();
							item.itemCode = i[0];
							item.itemNum = daysDiff * Integer.parseInt(i[1]);
							item.isBind = Const.BindType.BIND.getValue();
							mail.attachments.add(item);
						}
					}
					mail.replace = new HashMap<>();
					mail.replace.put("day", String.valueOf(daysDiff));
					MailUtil.getInstance().sendMailToOnePlayer(playerId, mail, GOODS_CHANGE_TYPE.CombineCompensate);
				}
			} catch (Exception e) {
				Out.error(e);
			}
		}
		PlayerPOManager.clearOfflinePO();
		return new GMStateResponse(0);
	}

	@Override
	public short getType() {
		return RpcOpcode.OPCODE_DELETE_PLAYER;
	}
}