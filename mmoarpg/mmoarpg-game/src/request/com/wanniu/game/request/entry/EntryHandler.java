package com.wanniu.game.request.entry;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.alibaba.fastjson.JSON;
import com.wanniu.core.GGlobal;
import com.wanniu.core.game.LangService;
import com.wanniu.core.game.entity.GClientEvent;
import com.wanniu.core.game.protocol.PomeloRequest;
import com.wanniu.core.game.protocol.PomeloResponse;
import com.wanniu.core.logfs.Out;
import com.wanniu.core.login.AuthServer;
import com.wanniu.core.util.DateUtils;
import com.wanniu.core.util.StringUtil;
import com.wanniu.game.GWorld;
import com.wanniu.game.area.PlayerRemote;
import com.wanniu.game.common.msg.ErrorResponse;

import cn.qeng.common.gm.RedisKeyConst;
import cn.qeng.common.login.TokenInfo;
import pomelo.connector.EntryHandler.EntryRequest;
import pomelo.connector.EntryHandler.EntryResponse;
import pomelo.player.PlayerOuterClass.PlayerBasic;

/**
 * @author agui
 */
@GClientEvent("connector.entryHandler.entryRequest")
public class EntryHandler extends PomeloRequest {

	public PomeloResponse request() throws Exception {
		EntryRequest req = EntryRequest.parseFrom(pak.getRemaingBytes());

		if (!GWorld.DEBUG && !"1002".equals(req.getC2SClientVersion())) {
			return new ErrorResponse("1月30号才开始公测噢~");
		}

		String uid = req.getC2SUid();
		// 检测输入参数
		if (StringUtil.isEmpty(uid)) {
			return new ErrorResponse(LangService.getValue("PLAYER_UID_NULL"));
		}
		String token = req.getC2SToken();
		if (StringUtil.isEmpty(token)) {
			return new ErrorResponse(LangService.getValue("PLAYER_TOKEN_NULL"));
		}

		int logicServerId = req.getC2SLogicServerId();
		if (!GWorld.sids.contains(logicServerId)) {
			Out.warn("登录异常 C2SLogicServerId=", logicServerId, ",SERVER_ID=", GWorld.__SERVER_ID);
			return new ErrorResponse(LangService.getValue("PARAM_ERROR"));
		}

		// 对外时间判定
		if (LocalDateTime.now().isBefore(GWorld.__EXTERNAL_TIME)) {
			// 白名单判定
			if (!isWhiteList(pak.getIp(), uid)) {
				return new ErrorResponse(LangService.getValue("SERVER_EXTERNAL_TIME") + GWorld.__EXTERNAL_TIME.format(DateUtils.F_YYYYMMDDHHMMSS));
			}
		}

		// 验证身份
		String auth_token = AuthServer.K_TOKEN + token;
		String authJson = AuthServer.get(auth_token);
		if (StringUtils.isEmpty(authJson)) {
			Out.warn("登录已超时,Token信息为空,uid=", uid);
			return new ErrorResponse(LangService.getValue("VERIFY_FAIL"));
		}

		TokenInfo tokenInfo = JSON.parseObject(authJson, TokenInfo.class);
		if (StringUtils.isEmpty(tokenInfo.getUid()) || !tokenInfo.getUid().equals(uid)) {
			Out.warn("登录异常,UID不匹配 auth_uid=", tokenInfo.getUid(), ",uid=", uid);
			return new ErrorResponse(LangService.getValue("VERIFY_FAIL"));
		}

		// 账号保存到连接会话中
		pak.setAttr(GGlobal.__KEY_USER_ID, uid);
		pak.setAttr(GGlobal.__KEY_TOKEN, auth_token);
		pak.setAttr(GGlobal.__KEY_TOKEN_INFO, tokenInfo);// 登录Token整个存起来

		if (!LoginQueue.checkQueue(pak)) {
			return new ErrorResponse("服务器爆满，请稍后...");
		}

		// session相关
		pak.setAttr(GGlobal.__KEY_LOGIC_SERVERID, logicServerId);
		List<PlayerBasic> players = PlayerRemote.getPlayersByUidAndLogicServerId(pak.getSession(), uid, logicServerId, pak.getIp());
		pak.setAttr(GGlobal.__KEY_ROLE_COUNT, players.size());
		Out.info("玩家进入选角界面uid=", uid, ",playerSize=", players.size());

		// 登录成功后，给此Token的时间多加些，2小时吧...
		AuthServer.expire(auth_token, 2 * 60 * 60);

		return new PomeloResponse() {
			@Override
			protected void write() throws IOException {
				EntryResponse.Builder res = EntryResponse.newBuilder();

				res.addAllS2CPlayers(players);

				res.setS2CCode(OK);
				body.writeBytes(res.build().toByteArray());
			}
		};
	}

	public boolean isWhiteList(String ip, String uid) {
		try {
			String ltime = AuthServer.hget(RedisKeyConst.REDIS_KEY_WHITELIST_IP, ip);
			if (StringUtil.isEmpty(ltime)) {
				ltime = AuthServer.hget(RedisKeyConst.REDIS_KEY_WHITELIST_UID, uid);
			}
			if (StringUtil.isEmpty(ltime)) {
				return false;
			} else {
				long time = Long.parseLong(ltime);
				if (time > 0 && System.currentTimeMillis() > time) {
					return false;
				}
			}
		} catch (Exception e) {
			Out.error("判定白名单异常啦.", e);
			return false;
		}
		return true;
	}

	public short getType() {
		return 0x101;
	}
}