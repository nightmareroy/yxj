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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import cn.qeng.common.login.LoginConst;
import cn.qeng.common.login.LoginResult;
import cn.qeng.usercenter.ErrorCode;
import cn.qeng.usercenter.SockpuppetConfig;
import cn.qeng.usercenter.util.HttpUtil;
import cn.qeng.usercenter.util.RsaUtil;

/**
 * 清源IOS登录.
 *
 * @author 周明凯(zhoumingkai@qeng.cn)
 */
@Component("channel:1002")
public class QengIOSHandler extends AbstractChannelHander {
	private final static String SIGN_TYPE = "RSA";
	private final static String SDK_VERSION = "1.1";

	@Autowired
	private SockpuppetConfig sockpuppetConfig;

	// 清源分配的应用ID
	@Value("${ios.qeng.app.id}")
	private String APP_ID;
	// 清源分配的登录地址
	@Value("${ios.qeng.check.url}")
	private String CHECK_URL;
	// 清源分配的私钥
	@Value("${ios.qeng.privatekey}")
	private String PRIVATE_KEY;

	@Override
	public LoginResult channelLogin(Map<String, String> params) throws Exception {
		String uid = params.get(LoginConst.PARAM_NAME_UID);

		String appId = APP_ID;
		String privatekey = PRIVATE_KEY;

		// 如果是马甲包，需要修正上面两个参数
		String subchannel = params.get(LoginConst.PARAM_NAME_SUBCHANNEL);
		if (!"1002".equals(subchannel) && !StringUtils.isEmpty(subchannel)) {
			appId = subchannel;// 这个就是马甲APPID
			privatekey = sockpuppetConfig.getSockpuppet().getOrDefault(subchannel, PRIVATE_KEY);
		}

		Map<String, String> qengParam = new TreeMap<>();// 将待签名参数按照字典顺序（字母升序）排列。
		qengParam.put("AppId", appId);
		qengParam.put("OpenId", uid);
		qengParam.put("SignType", SIGN_TYPE);
		qengParam.put("SdkVersion", SDK_VERSION);

		StringBuilder sb = new StringBuilder(512);
		String content = buildParams(sb, qengParam);
		sb.append("Sign=").append(RsaUtil.sign(content, privatekey));// 签名+base64编码

		String json = HttpUtil.sendPost(CHECK_URL, sb.toString());
		if (StringUtils.isEmpty(json)) {
			return new LoginResult(false, ErrorCode.RESULT_IS_EMPTY);
		}

		// {"OpenId":"8455c2b97cf60a8594816b8d0b19d29a","CreateAt":"2018-01-15
		// 17:39:18","code":0,"msg":""}
		JSONObject resultObject = JSON.parseObject(json);
		int code = resultObject.getIntValue("code");

		// 登录成功
		if (code == 0) {
			LoginResult result = new LoginResult();
			result.setSuccess(true);

			// 取出openid
			result.setUsername(uid);
			result.setSubchannelUid(uid);

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
	private String buildParams(StringBuilder sb, Map<String, String> params) {
		TreeSet<String> set = new TreeSet<String>(params.keySet());
		for (String key : set) {
			sb.append(key + "=" + params.get(key) + "&");
		}
		return sb.substring(0, sb.length() - 1);// 去除掉末尾的那个“&”号
	}
}