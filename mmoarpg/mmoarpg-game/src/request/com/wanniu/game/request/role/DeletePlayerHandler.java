package com.wanniu.game.request.role;

import java.io.IOException;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.wanniu.core.GGlobal;
import com.wanniu.core.game.entity.GClientEvent;
import com.wanniu.core.game.protocol.PomeloRequest;
import com.wanniu.core.game.protocol.PomeloResponse;
import com.wanniu.core.logfs.Out;
import com.wanniu.core.util.StringUtil;
import com.wanniu.game.common.ConstsTR;
import com.wanniu.game.daoyou.DaoYouCenter;
import com.wanniu.game.guild.GuildService;
import com.wanniu.game.player.PlayerDao;
import com.wanniu.game.player.PlayerUtil;
import com.wanniu.game.poes.PlayerPO;
import com.wanniu.game.rank.RankCenter;
import com.wanniu.redis.GameDao;
import com.wanniu.redis.GlobalDao;
import com.wanniu.redis.PlayerPOManager;

import pomelo.connector.RoleHandler.DeletePlayerRequest;
import pomelo.connector.RoleHandler.DeletePlayerResponse;

/**
 * 删除角色
 * 
 * @author agui
 */
@GClientEvent("connector.roleHandler.deletePlayerRequest")
public class DeletePlayerHandler extends PomeloRequest {

	public PomeloResponse request() throws Exception {
		DeletePlayerRequest req = DeletePlayerRequest.parseFrom(pak.getRemaingBytes());
		final String playerId = req.getC2SPlayerId();

		PlayerPO player = PlayerPOManager.findPO(ConstsTR.playerTR, playerId, PlayerPO.class);
		if (!player.uid.equals(pak.getUid())) {
			Out.warn("玩家竟然尝试删除不是自己的角色 uid=", pak.getUid(), ",playerId=", playerId, ",name=", player.name);
			return new PomeloResponse() {
				@Override
				protected void write() throws IOException {
					DeletePlayerResponse.Builder res = DeletePlayerResponse.newBuilder();
					res.setS2CCode(OK);
					body.writeBytes(res.build().toByteArray());
				}
			};
		}

		player.isDelete = 1;
		Out.info("玩家删除角色 uid=", pak.getUid(), ",playerId=", playerId, ",name=", player.name);

		Integer count = pak.getAttr(GGlobal.__KEY_ROLE_COUNT);
		if (count != null && count > 0) {
			pak.setAttr(GGlobal.__KEY_ROLE_COUNT, count - 1);
		}

		// 删除角色，删除公会相应数据
		GuildService.delRole(playerId);

		// 删除角色，清理角色相关排行榜
		RankCenter.delRoleClear(playerId);

		// 退出道友.
		DaoYouCenter.getInstance().removeDaoYouMember(playerId);

		String s_players = GlobalDao.hget(String.valueOf(player.logicServerId), player.uid);
		if (StringUtil.isNotEmpty(s_players)) {
			JSONObject players = JSON.parseObject(s_players);
			players.remove(player.id);
			GlobalDao.hset(String.valueOf(player.logicServerId), player.uid, players.toJSONString());
			PlayerUtil.addLoginServer(player.uid, player.logicServerId, players.size());
		}

		PlayerDao.freeName(player.name);
		GameDao.freeName(player.name);

		PlayerPOManager.clearOfflinePO(playerId);

		return new PomeloResponse() {
			@Override
			protected void write() throws IOException {
				DeletePlayerResponse.Builder res = DeletePlayerResponse.newBuilder();
				res.setS2CCode(OK);
				body.writeBytes(res.build().toByteArray());
			}
		};
	}
}