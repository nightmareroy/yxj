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
package cn.qeng.usercenter.handler;

import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import cn.qeng.common.login.LoginConst;
import cn.qeng.common.login.LoginResult;
import cn.qeng.usercenter.ErrorCode;
import cn.qeng.usercenter.util.HttpUtil;
import cn.qeng.usercenter.util.Md5;
import cn.qeng.usercenter.util.StringUtils;

/**
 * 当乐登录.
 *
 * @author 周明凯(zhoumingkai@qeng.cn)
 */
@Component("channel:1003")
public class DownjoyAndroidHandler extends AbstractChannelHander {

	@Value("${android.downjoy.app.id}")
	private String APP_ID;
	@Value("${android.downjoy.app.key}")
	private String APP_KEY;
	@Value("${android.downjoy.check.url}")
	private String CHECK_URL;
	@Value("${android.downjoy.check.backup.url}")
	private String CHECK_BACKUP_URL;

	@Override
	public LoginResult channelLogin(Map<String, String> params) throws Exception {
		final String uid = params.get(LoginConst.PARAM_NAME_UID);
		String accessToken = params.get(LoginConst.PARAM_NAME_ACCESS_TOKEN);

		String sig = getSignString(APP_ID, APP_KEY, accessToken, uid);
		String param = "appid=" + APP_ID + "&token=" + accessToken + "&umid=" + uid + "&sig=" + sig;
		String url = accessToken.startsWith("ZB_") ? CHECK_BACKUP_URL : CHECK_URL;
		url = new StringBuilder(url).append("?").append(param).toString();

		String json = HttpUtil.sendGet(url);

		if (StringUtils.isEmpty(json)) {
			return new LoginResult(false, ErrorCode.RESULT_IS_EMPTY);
		}

		JSONObject resultObject = JSON.parseObject(json);
		int code = resultObject.getIntValue("msg_code");
		int valid = resultObject.getIntValue("valid");

		// 登录成功
		if (code == 2000 && valid == 1) {
			LoginResult result = new LoginResult();
			result.setSuccess(true);
			result.setUsername(uid);
			result.setSubchannelUid(uid);

			// 登录成功尝试记录账号
			tryRecordAccount(params, result);
			return result;
		}

		// 登录失败，取出消息返回给调用者
		return new LoginResult(false, resultObject.getString("msg_desc"));
	}

	private String getSignString(String appId, String appKey, String token, String umid) throws Exception {
		StringBuilder sb = new StringBuilder(appId);
		sb.append("|").append(appKey).append("|").append(token).append("|").append(umid);
		return Md5.getMD5(sb.toString());
	}
}
