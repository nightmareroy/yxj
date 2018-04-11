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
package cn.qeng.common.login;

/**
 * 登录相关的常量类.
 *
 * @author 周明凯(zhoumingkai@qeng.cn)
 */
public class LoginConst {

	// 登录时HTTP所用的参数名称.
	/**
	 * UID
	 */
	public final static String PARAM_NAME_UID = "uid";
	/**
	 * 渠道
	 */
	public final static String PARAM_NAME_CHANNEL = "channel";
	/**
	 * 子渠道
	 * <p>
	 * channelUid改为子渠道参数名称
	 */
	public final static String PARAM_NAME_SUBCHANNEL = "channelUid";
	/**
	 * IP
	 */
	public final static String PARAM_NAME_IP = "ip";
	/**
	 * Mac地址
	 */
	public final static String PARAM_NAME_MAC = "mac";
	/**
	 * 登录时用的验证Token
	 */
	public final static String PARAM_NAME_ACCESS_TOKEN = "accessToken";
	/**
	 * 应用ID
	 */
	public final static String PARAM_NAME_PRODUCT_ID = "productId";
	/**
	 * 系统
	 */
	public final static String PARAM_NAME_OS = "os";

	// 平台类型--------------------
	/**
	 * 1=其他平台
	 */
	public final static int OS_TYPE_OTHER = 0;
	/**
	 * 1=IOS平台
	 */
	public final static int OS_TYPE_IOS = 1;
	/**
	 * 1=安卓平台
	 */
	public final static int OS_TYPE_ANDROID = 2;
}