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
import java.util.TreeMap;
import java.util.TreeSet;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import cn.qeng.common.login.LoginConst;
import cn.qeng.common.login.LoginResult;
import cn.qeng.usercenter.ErrorCode;
import cn.qeng.usercenter.util.HttpsUtils;

/**
 * 魅族安卓登录.
 *
 * @author 周明凯(zhoumingkai@qeng.cn)
 */
@Component("channel:1005")
public class MeizuAndroidHandler extends AbstractChannelHander {
	// 魅族分配的应用ID
	@Value("${android.meizu.app.id}")
	private int appId;
	@Value("${android.meizu.app.key}")
	private String appKey;
	@Value("${android.meizu.app.secret}")
	private String appSecret;
	@Value("${android.meizu.check.url}")
	private String checkUrl;

	@Override
	public LoginResult channelLogin(Map<String, String> params) throws Exception {
		String token = params.get(LoginConst.PARAM_NAME_ACCESS_TOKEN);
		long uid = Long.parseLong(params.get(LoginConst.PARAM_NAME_UID));// meizu sdk 用户 id
		long ts = System.currentTimeMillis();

		Map<String, Object> qengParam = new TreeMap<>();
		qengParam.put("app_id", appId);
		qengParam.put("token", token);
		qengParam.put("ts", ts);
		qengParam.put("uid", uid);//

		StringBuilder sb = new StringBuilder(512);
		sb.append(checkUrl).append("?");
		buildParams(sb, qengParam);
		String json = HttpsUtils.get(sb.toString());
		if (StringUtils.isEmpty(json)) {
			return new LoginResult(false, ErrorCode.RESULT_IS_EMPTY);
		}

		// {"code":198004,"message":"","value":{"uid": 163}}
		JSONObject resultObject = JSON.parseObject(json);
		int code = resultObject.getIntValue("code");
		// 登录成功
		if (code == 200) {
			LoginResult result = new LoginResult();
			result.setSuccess(true);

			// 取出openid
			JSONObject dataObject = resultObject.getJSONObject("value");
			result.setUsername(dataObject.getString("uid"));
			result.setSubchannelUid(result.getUsername());// FIXME 不知道他家有没有这方面需求

			// 登录成功尝试记录账号
			tryRecordAccount(params, result);
			return result;
		}
		// 测试，1234567强制登录成功
		else if ("1234567".equals(token)) {
			LoginResult result = new LoginResult();
			result.setSuccess(true);
			result.setUsername("1234567");
			return result;
		}

		String message = resultObject.getString("message");
		if (StringUtils.isEmpty(message)) {
			switch (code) {
			case 110003:// 请求参数错误
				message = "请求参数错误";
				break;
			case 198001:// 游戏不存在
				message = "游戏不存在";
				break;
			case 198002:// 签名错误
				message = "签名错误";
				break;
			case 198004:// 签名错误
				message = "token 无效";
				break;
			case 120015:// 签名错误
				message = "订单不存在";
				break;
			default:
				message = "登录异常：" + code;
				break;
			}
		}
		// 登录失败，取出消息返回给调用者
		return new LoginResult(false, message);
	}

	/**
	 * 将待签名参数按照字典顺序（字母升序）排列。
	 */
	private StringBuilder buildParams(StringBuilder sb, Map<String, Object> params) {
		TreeSet<String> set = new TreeSet<String>(params.keySet());
		for (String key : set) {
			sb.append(key + "=" + params.get(key) + "&");
		}
		sb.substring(0, sb.length() - 1);// 去除掉末尾的那个“&”号
		return sb;
	}
}