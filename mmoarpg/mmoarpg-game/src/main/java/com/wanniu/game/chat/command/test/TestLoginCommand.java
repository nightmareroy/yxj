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
package com.wanniu.game.chat.command.test;

import com.wanniu.core.logfs.Out;
import com.wanniu.core.util.StringUtil;
import com.wanniu.game.chat.command.AbsCommand;
import com.wanniu.game.chat.command.Command;
import com.wanniu.game.common.Const.KickReason;
import com.wanniu.game.common.ConstsTR;
import com.wanniu.game.common.msg.MessagePush;
import com.wanniu.game.player.PlayerDao;
import com.wanniu.game.player.WNPlayer;
import com.wanniu.game.poes.PlayerPO;
import com.wanniu.redis.GameDao;
import com.wanniu.redis.PlayerPOManager;

import pomelo.area.PlayerHandler.KickPlayerPush;

/**
 * 测试登录任何角色.
 *
 * @author 周明凯(zhoumingkai@qeng.cn)
 */
@Command("@gm test login")
public class TestLoginCommand extends AbsCommand {

	@Override
	public String help() {
		return "@gm test login <name> 测试登录任何角色";
	}

	@Override
	protected String exec(WNPlayer player, String... args) {
		String name = args[3];
		String id = GameDao.getIdByName(name);
		if (!StringUtil.isEmpty(id)) {
			Out.info("拉取目标角色 name=", name, ",id=", id);
			PlayerPO baseData = PlayerPOManager.findPO(ConstsTR.playerTR, id, PlayerPO.class);
			PlayerDao.insertPlayerId(baseData, player.allBlobData);

			// 该玩家状态不是sessionClosed，是异常状态，返回错误
			KickPlayerPush.Builder data = KickPlayerPush.newBuilder();
			data.setS2CReasonType(KickReason.GM_KICK.value);
			try {
				player.getSession().write(new MessagePush("area.playerPush.kickPlayerPush", data.build()).getContent()).await(2000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		} else {
			return "目标角色名称未找到...";
		}
		return "OK";
	}
}
