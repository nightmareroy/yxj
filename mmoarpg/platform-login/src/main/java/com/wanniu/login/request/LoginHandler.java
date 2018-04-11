package com.wanniu.login.request;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.wanniu.core.GConfig;
import com.wanniu.core.GGlobal;
import com.wanniu.core.game.protocol.ErrorResponse;
import com.wanniu.core.game.protocol.PomeloRequest;
import com.wanniu.core.game.protocol.PomeloResponse;
import com.wanniu.core.game.request.GClientEvent;
import com.wanniu.core.logfs.Out;
import com.wanniu.core.redis.GCache;
import com.wanniu.core.util.StringUtil;
import com.wanniu.core.util.http.HttpRequester;
import com.wanniu.core.util.http.HttpRespons;
import com.wanniu.login.LoginServer;
import com.wanniu.login.proto.LoginHandler.LoginRequest;
import com.wanniu.login.proto.LoginHandler.LoginResponse;
import com.wanniu.login.vo.AppVO;
import com.wanniu.login.whitelist.WhitelistManager;

import cn.qeng.common.login.LoginConst;
import cn.qeng.common.login.LoginResult;
import cn.qeng.common.login.TokenInfo;

/**
 * 客户端登录请求.
 *
 * @author 小流氓(176543888@qq.com)
 */
@GClientEvent("login.loginHandler.loginRequest")
public class LoginHandler extends PomeloRequest {
	private static final HttpRequester httpReq = new HttpRequester("UTF-8");

	@Override
	public PomeloResponse request() throws Exception {
		LoginRequest req = LoginRequest.parseFrom(pak.getRemaingBytes());

		String uid = req.getUid();
		String token = req.getToken();
		Out.debug("尝试登录 uid=", uid, ",token=", token);
		if (StringUtil.isEmpty(uid) || StringUtil.isEmpty(token)) {
			return new ErrorResponse("用户名和密码不能为空！");
		}

		// 是否在IP黑名单的判定.
		if (WhitelistManager.getInstance().isBlackListByIP(pak.getIp())) {
			Out.warn("IP黑名单拦截命中：ip=", pak.getIp());
			return new ErrorResponse("系统繁忙，请稍后再次尝试...");
		}

		String subchannelUid = uid;
		AppVO app = null;
		int appId = LoginServer.__APP_ID;
		if (StringUtil.isNotEmpty(req.getChannel())) {// 走第三方sdk，连usercenter验证
			Map<String, String> params = new HashMap<>();
			params.put(LoginConst.PARAM_NAME_UID, req.getUid());
			params.put(LoginConst.PARAM_NAME_CHANNEL, req.getChannel());
			params.put(LoginConst.PARAM_NAME_SUBCHANNEL, req.getChannelUid());
			params.put(LoginConst.PARAM_NAME_ACCESS_TOKEN, req.getToken());
			params.put(LoginConst.PARAM_NAME_PRODUCT_ID, req.getAppId());
			params.put(LoginConst.PARAM_NAME_MAC, req.getImei());
			params.put(LoginConst.PARAM_NAME_OS, String.valueOf(req.getOs()));
			params.put(LoginConst.PARAM_NAME_IP, pak.getIp());
			String loginUrl = GConfig.getInstance().get("usercenter.url");
			Out.debug("走第三方SDK验证 url=", loginUrl, ",params=", params);

			try {
				HttpRespons respons = httpReq.sendPost(loginUrl, params);
				Out.debug("走第三方SDK验证 result=", respons.getContent());

				if (respons.getCode() == 200 && StringUtil.isNotEmpty(respons.getContent())) {
					LoginResult loginResult = JSON.parseObject(respons.getContent(), LoginResult.class);
					if (!loginResult.isSuccess()) {
						return new ErrorResponse(loginResult.getDesc());
					}
					app = LoginServer.getInstance().getApp(appId);
					uid = loginResult.getUsername();
					subchannelUid = loginResult.getSubchannelUid();
				} else {
					return new ErrorResponse("登录验证失败！");
				}
			} catch (Exception e) {
				return new ErrorResponse("登录服务器正在维护中，请稍后再次尝试登录！");
			}
		} else {// 走自己的帐号系统验证
			String pwd = GCache.get("/account/" + uid);
			if (!token.equals(pwd)) {
				return new ErrorResponse("用户名或密码错误！");
			}
			if (StringUtil.isNumeric(req.getAppId())) {
				appId = Integer.parseInt(req.getAppId());
			} else {
				Out.warn(pak.getIp(), " login error app ", req.getAppId());
			}
			app = LoginServer.getInstance().getApp(appId);
		}
		if (app == null) {
			return new ErrorResponse("app id not exists : " + appId);
		}

		// --------------登录成功---------------------------------
		String newUid;
		// 第三方的账号，需要重新Build一个账号...
		if (StringUtil.isNotEmpty(req.getChannel())) {
			String channel = req.getChannel();
			int length = channel.length() + 1 + uid.length();
			newUid = new StringBuilder(length).append(channel).append('_').append(uid).toString();
		}
		// 研发就使用输入的账号
		else {
			newUid = uid;
		}
		Out.debug("登录成功 uid=", uid, ",newUid=", newUid);

		// 是否在UID黑名单的判定.
		if (WhitelistManager.getInstance().isBlackListByUID(newUid)) {
			Out.warn("UID黑名单拦截命中：uid=", newUid);
			return new ErrorResponse("系统繁忙，请稍后再次尝试...");
		}

		// 已有角色
		String playerServers = GCache.get("/player/servers/" + newUid);
		Map<Integer, Integer> histories = new HashMap<Integer, Integer>();
		if (!StringUtil.isEmpty(playerServers)) {
			try {
				JSONArray arr = JSON.parseArray(playerServers);
				for (int i = 0; i < arr.size(); i++) {
					JSONObject json = arr.getJSONObject(i);
					int sid = json.getIntValue("sid");
					int count = json.getIntValue("count");
					if (count > 0) {
						histories.put(sid, count);
					}
				}
			} catch (Exception e) {
				Out.error(e);
			}
		}
		Out.debug("已有角色 uid=", newUid, ",histories=", histories);

		// 构建登录Token
		TokenInfo tokenInfo = new TokenInfo();
		tokenInfo.setUid(newUid);// 游戏的用户ID（uid=channel-channelUid）

		tokenInfo.setChannel(req.getChannel());// 渠道
		tokenInfo.setChannelUid(uid);// 渠道的用户ID

		tokenInfo.setSubchannel(req.getChannelUid());// 这个参数用来充当子渠道了
		tokenInfo.setSubchannelUid(subchannelUid);

		tokenInfo.setMac(req.getImei());
		tokenInfo.setOs(String.valueOf(req.getOs()));//
		tokenInfo.setAccessToken(req.getToken());// 登录Token

		final String uuid = UUID.randomUUID().toString();
		GCache.put("/token/" + uuid, JSON.toJSONString(tokenInfo), 600);// 存储token到redis，供游戏服验证用

		pak.setAttr(GGlobal._KEY_USER_ID, newUid);
		JSONArray board = app.getServerList(histories, pak, newUid);

		return new PomeloResponse() {// 给游戏客户端返回token以便拿到游戏服验证
			@Override
			protected void write() throws IOException {
				LoginResponse.Builder res = LoginResponse.newBuilder();
				res.setS2CCode(OK);
				res.setToken(uuid);
				res.setNewUid(newUid);
				res.setBoard(board.toJSONString());
				body.writeBytes(res.build().toByteArray());
			}
		};
	}
}