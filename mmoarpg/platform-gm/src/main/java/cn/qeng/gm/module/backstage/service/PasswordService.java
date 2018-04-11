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
package cn.qeng.gm.module.backstage.service;

import org.springframework.stereotype.Service;

import cn.qeng.gm.util.Md5Utils;

/**
 * 密码服务类.
 *
 * @author 小流氓(mingkai.zhou@qeng.net)
 */
@Service
public class PasswordService {
	// 固态盐值：娉娉袅袅十三余，豆蔻梢头二月初
	private static final String salt = "ppnn13%dkstFeb.1st";

	/**
	 * 加密指定密码.
	 * <p>
	 * 账号充当动态盐，然后加入固态盐，最后才是Md5的密码.<br>
	 * md5(randomSalt+salt+password)
	 */
	public String encrypt(String password, String randomSalt) {
		return Md5Utils.encrypt(new StringBuilder(randomSalt.length() + salt.length() + password.length()).append(randomSalt).append(salt).append(password).toString());
	}
}