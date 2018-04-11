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
package cn.qeng.usercenter;

import java.util.Map;

import cn.qeng.common.login.LoginResult;

/**
 * 渠道登录处理接口.
 *
 * @author 周明凯(zhoumingkai@qeng.cn)
 */
public interface ChannelHander {

	/**
	 * 渠道登录.
	 * 
	 * @param params 参数
	 * @return 返回登录结果
	 * @throws Exception 可能会有异常需要判定
	 */
	LoginResult channelLogin(Map<String, String> params) throws Exception;
}