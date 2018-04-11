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
package com.wanniu.game.request.guild.guildBoss;

import java.io.IOException;

import com.wanniu.core.game.entity.GClientEvent;
import com.wanniu.core.game.protocol.PomeloRequest;
import com.wanniu.core.game.protocol.PomeloResponse;
import com.wanniu.game.common.Const;
import com.wanniu.game.player.WNPlayer;

import pomelo.area.GuildBossHandler.EnterGuildBossAreaResponse;
import pomelo.area.GuildBossHandler.GuildBossInspireRequest;

/**
 * 
 * 鼓舞
 * 
 * @author Feiling(feiling@qeng.cn)
 */
@GClientEvent("area.guildBossHandler.guildBossInspireRequest")
public class GuildBossInspireHanler extends PomeloRequest {
	@Override
	public PomeloResponse request() throws Exception {
		WNPlayer player = (WNPlayer) pak.getPlayer();
		GuildBossInspireRequest req = GuildBossInspireRequest.parseFrom(pak.getRemaingBytes());
		int index = req.getC2SIndex();// 1:个人2：仙盟 3:仙盟防御鼓舞
		return new PomeloResponse() {
			@Override
			protected void write() throws IOException {
				EnterGuildBossAreaResponse.Builder res = EnterGuildBossAreaResponse.newBuilder();
				String msg = player.guildBossManager.handlerInspireGuildBoss(index);
				if (msg != null) {
					res.setS2CCode(Const.CODE.FAIL);
					res.setS2CMsg(msg);
				} else {
					res.setS2CCode(Const.CODE.OK);
				}
				body.writeBytes(res.build().toByteArray());
			}
		};
	}

}
