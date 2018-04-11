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
package com.wanniu.game.auction;

import java.util.ArrayList;
import java.util.List;

import com.wanniu.core.util.StringUtil;
import com.wanniu.game.common.Const;
import com.wanniu.game.common.Const.ManagerType;
import com.wanniu.game.common.Const.PlayerEventType;
import com.wanniu.game.common.ModuleManager;
import com.wanniu.game.player.WNPlayer;
import com.wanniu.game.poes.GuildBossPo;

import pomelo.area.PlayerHandler.SuperScriptPush;
import pomelo.area.PlayerHandler.SuperScriptType;

/**
 * 
 *
 * @author Feiling(feiling@qeng.cn)
 */
public class AuctionManager extends ModuleManager {
	private WNPlayer player;

	public AuctionManager(WNPlayer player) {
		this.player = player;
	}

	/**
	 * 推送工会小红点
	 */
	public void pushScript() {
		SuperScriptPush.Builder data = SuperScriptPush.newBuilder();
		List<SuperScriptType> list = getSuperScript();
		if (list != null && !list.isEmpty()) {
			data.addAllS2CData(list);
			player.receive("area.playerPush.onSuperScriptPush", data.build());
		}
		this.player.guildManager.pushRedPoint();
	}

	@Override
	public List<SuperScriptType> getSuperScript() {
		List<SuperScriptType> list = new ArrayList<>();
		list.add(getGuildScript().build());
		list.add(getWorldScript().build());
		return list;
	}

	private SuperScriptType.Builder getGuildScript() {
		SuperScriptType.Builder data = SuperScriptType.newBuilder();
		data.setType(Const.SUPERSCRIPT_TYPE.GUILD_AUCTION.getValue());
		String guildId = player.guildManager.getGuildId();
		if (StringUtil.isEmpty(guildId)) {
			data.setNumber(0);
			return data;
		}
		boolean hasGuildAuction = AuctionDataManager.getInstance().hasGuildItem(guildId);
		if (hasGuildAuction) {
			data.setNumber(1);
		} else {
			data.setNumber(0);
		}
		return data;
	}

	public boolean canGuildPush() {
		String guildId = player.guildManager.getGuildId();
		if (StringUtil.isEmpty(guildId)) {
			return false;
		}
		boolean hasGuildAuction = AuctionDataManager.getInstance().hasGuildItem(guildId);
		return hasGuildAuction ? true : false;
	}

	public boolean needUpdateRedPoint() {
		GuildBossPo guildBossPO = player.guildBossManager.getAndCheckUpdateGuildBossPo(player.player.id);
		boolean hasWorldAuction = AuctionDataManager.getInstance().hasWorldItem();
		return (hasWorldAuction && guildBossPO.aucpoint == 0) ? true : false;
	}

	private SuperScriptType.Builder getWorldScript() {
		SuperScriptType.Builder data = SuperScriptType.newBuilder();
		data.setType(Const.SUPERSCRIPT_TYPE.WORLD_AUCTION.getValue());
		if (needUpdateRedPoint()) {
			data.setNumber(1);
		} else {
			data.setNumber(0);
		}
		return data;
	}

	@Override
	public void onPlayerEvent(PlayerEventType eventType) {

	}

	@Override
	public ManagerType getManagerType() {
		return ManagerType.AUCTION;
	}

}
