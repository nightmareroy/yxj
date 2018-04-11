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

import java.util.Date;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;

import cn.qeng.common.login.LoginConst;
import cn.qeng.common.login.LoginResult;
import cn.qeng.usercenter.ChannelHander;
import cn.qeng.usercenter.domain.Account;
import cn.qeng.usercenter.domain.AccountRepository;

/**
 * 抽象的处理逻辑.
 *
 * @author 周明凯(zhoumingkai@qeng.cn)
 */
public abstract class AbstractChannelHander implements ChannelHander {
	private final static Logger logger = LogManager.getLogger(AbstractChannelHander.class);

	@Autowired
	private AccountRepository accountRepository;

	/**
	 * 尝试保存这个登录者的账号信息.
	 * <p>
	 * 这个方法是异步的噢.
	 */
	@Async
	protected void tryRecordAccount(Map<String, String> params, LoginResult result) {
		String channel = params.getOrDefault(LoginConst.PARAM_NAME_CHANNEL, "");
		String uid = result.getUsername();
		int length = channel.length() + 1 + uid.length();
		String newUid = new StringBuilder(length).append(channel).append('_').append(uid).toString();

		// 不存在需要创建
		if (!accountRepository.exists(newUid)) {
			accountRepository.save(createAccount(newUid, params, result));
			logger.info("新账号：account={}", newUid);
		}
	}

	private Account createAccount(String username, Map<String, String> params, LoginResult result) {
		Account account = new Account();
		account.setUsername(username);
		account.setCreateTime(new Date());
		account.setSdkUid(result.getUsername());
		account.setSubchannelUid(result.getSubchannelUid());

		account.setChannel(params.getOrDefault(LoginConst.PARAM_NAME_CHANNEL, "-")); // 大渠道编号
		account.setSubchannel(params.getOrDefault(LoginConst.PARAM_NAME_SUBCHANNEL, "-"));// 子渠道名称
		account.setAppId(params.getOrDefault(LoginConst.PARAM_NAME_PRODUCT_ID, "-"));
		account.setIp(params.getOrDefault(LoginConst.PARAM_NAME_IP, "-"));
		account.setMac(params.getOrDefault(LoginConst.PARAM_NAME_MAC, "-"));
		account.setOs(params.getOrDefault(LoginConst.PARAM_NAME_OS, "-"));
		return account;
	}
}