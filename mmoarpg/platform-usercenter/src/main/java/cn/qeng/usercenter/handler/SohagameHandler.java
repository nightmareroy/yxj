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
import cn.qeng.usercenter.util.HttpUtil;
import cn.qeng.usercenter.util.RsaUtil;

/**
 * 越南登录.
 *
 * @author 周明凯(zhoumingkai@qeng.cn)
 */
@Component("channel:2002")
public class SohagameHandler extends AbstractChannelHander {
	// 清源分配的应用ID
	@Value("${android.soha.app.id}")
	private String APP_ID;
	// 清源分配的登录地址
	@Value("${android.soha.check.url}")
	private String CHECK_URL;
	// 清源分配的私钥
	@Value("${android.soha.privatekey}")
	private String PRIVATE_KEY;

	@Override
	public LoginResult channelLogin(Map<String, String> params) throws Exception {
		String token = params.get(LoginConst.PARAM_NAME_ACCESS_TOKEN);

		Map<String, String> qengParam = new TreeMap<>();
		qengParam.put("appid", APP_ID);
		qengParam.put("model", "");
		qengParam.put("os", "android");
		qengParam.put("platform", "1");
		qengParam.put("udid", "");
		qengParam.put("auth", token);

		StringBuilder sb = new StringBuilder(512);
		String content = buildParams(sb, qengParam).toString();
		sb.append("sign=").append(RsaUtil.sign(content, PRIVATE_KEY));// 签名+base64编码

		String json = HttpUtil.sendPost(CHECK_URL, sb.toString());
		if (StringUtils.isEmpty(json)) {
			return new LoginResult(false, ErrorCode.RESULT_IS_EMPTY);
		}

		// {"code":0,"msg":"","data":{"openid":"f5aab3eff1b117ceebaf2b89c446ed7a","username":"游客34b77cf1"}}
		JSONObject resultObject = JSON.parseObject(json);
		int code = resultObject.getIntValue("code");

		// 登录成功
		if (code == 0) {
			LoginResult result = new LoginResult();
			result.setSuccess(true);

			// 取出openid
			JSONObject dataObject = resultObject.getJSONObject("data");
			result.setUsername(dataObject.getString("openid"));
			result.setSubchannelUid(dataObject.getString("accountId"));

			// 登录成功尝试记录账号
			tryRecordAccount(params, result);
			return result;
		}

		// 登录失败，取出消息返回给调用者
		return new LoginResult(false, resultObject.getString("msg"));
	}

	/**
	 * 将待签名参数按照字典顺序（字母升序）排列。
	 */
	private StringBuilder buildParams(StringBuilder sb, Map<String, String> params) {
		TreeSet<String> set = new TreeSet<String>(params.keySet());
		for (String key : set) {
			sb.append(key + "=" + params.get(key) + "&");
		}
		sb.substring(0, sb.length() - 1);// 去除掉末尾的那个“&”号
		return sb;
	}
}