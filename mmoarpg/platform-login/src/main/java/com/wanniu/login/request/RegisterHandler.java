package com.wanniu.login.request;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import com.alibaba.fastjson.JSON;
import com.wanniu.core.GGlobal;
import com.wanniu.core.game.protocol.ErrorResponse;
import com.wanniu.core.game.protocol.PomeloRequest;
import com.wanniu.core.game.protocol.PomeloResponse;
import com.wanniu.core.game.request.GClientEvent;
import com.wanniu.core.logfs.Out;
import com.wanniu.core.redis.GCache;
import com.wanniu.core.util.StringUtil;
import com.wanniu.login.LoginServer;
import com.wanniu.login.proto.LoginHandler.RegisterRequest;
import com.wanniu.login.proto.LoginHandler.RegisterResponse;
import com.wanniu.login.vo.AppVO;
import com.wanniu.login.whitelist.WhitelistManager;

import cn.qeng.common.login.TokenInfo;

@GClientEvent("login.loginHandler.registerRequest")
public class RegisterHandler extends PomeloRequest {

	private Map<Integer, Integer> DEFAULT = new HashMap<Integer, Integer>();

	@Override
	public PomeloResponse request() throws Exception {

		if (WhitelistManager.getInstance().isBlackListByIP(pak.getIp())) {
			pak.close();
			return null;
		}

		RegisterRequest req = RegisterRequest.parseFrom(pak.getRemaingBytes());
		String accout = req.getAccount();
		if (StringUtil.isEmpty(accout)) {
			return new ErrorResponse("账号不能为空！");
		}
		if (!StringUtil.isAlphaNumeric_(accout)) {
			return new ErrorResponse("账号只能由字母数字和_构成！");
		}
		if (accout.length() < 6) {
			return new ErrorResponse("账号长度不能少于6位！");
		}
		if (GCache.exists("/account/" + accout)) {
			return new ErrorResponse("账号已存在！");
		}

		String pwd = req.getPassword();
		if (StringUtil.isEmpty(pwd)) {
			return new ErrorResponse("密码不能为空！");
		}
		final AppVO app = LoginServer.getInstance().getApp(req.getAppId());
		if (app == null) {
			return new ErrorResponse("app id not exists : " + req.getAppId());
		}
		// String imei = req.getImei();
		// String version = req.getVersion();
		// String tel = req.getTel();
		// int channel = req.getChannel();
		// int os = req.getOs();
		final String uuid = UUID.randomUUID().toString();
		GCache.put("/account/" + accout, pwd);

		// 构建登录Token
		TokenInfo tokenInfo = new TokenInfo();
		tokenInfo.setChannel("");// 渠道
		tokenInfo.setSubchannel("");// 这个参数用来充当子渠道了
		tokenInfo.setSubchannelUid(accout);
		tokenInfo.setUid(accout);// 游戏的用户ID（uid=channel-channelUid）
		tokenInfo.setChannelUid(accout);// 渠道的用户ID
		tokenInfo.setMac(req.getImei());
		tokenInfo.setOs(String.valueOf(req.getOs()));//
		tokenInfo.setAccessToken("");// 登录Token
		GCache.put("/token/" + uuid, JSON.toJSONString(tokenInfo), 600);// 存储token到redis，供游戏服验证用

		Out.info("新增账号：", accout, " = ", pwd, pak.getIp());
		pak.setAttr(GGlobal._KEY_USER_ID, accout);
		return new PomeloResponse() {
			@Override
			protected void write() throws IOException {
				RegisterResponse.Builder res = RegisterResponse.newBuilder();
				res.setS2CCode(OK);

				res.setToken(uuid);
				res.setBoard(app.getServerList(DEFAULT, pak, accout).toJSONString());

				body.writeBytes(res.build().toByteArray());
			}
		};
	}

}
