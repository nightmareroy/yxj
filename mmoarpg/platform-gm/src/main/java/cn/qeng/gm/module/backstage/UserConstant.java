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
package cn.qeng.gm.module.backstage;

/**
 * 用户常量类.
 *
 * @author 小流氓(mingkai.zhou@qeng.net)
 */
public class UserConstant {
	/**
	 * 用户状态： 0=正常
	 */
	public static final int STATUS_NORMAL = 0;
	/**
	 * 用户状态： 1=异常
	 * <p>
	 * 异常登录，指定时间后才可以再次登录...
	 */
	public static final int STATUS_EXCEPTION = 1;
	/**
	 * 用户状态： 2=锁定
	 * <p>
	 * 此账号封存，禁止登录使用了...
	 */
	public static final int STATUS_LOCK = 2;

	/**
	 * 连续登录错误10次，此账号为异常状态，锁5分钟...
	 */
	public static final int MAX_LOGIN_ERROR_COUNT = 10;

	/**
	 * 添加用户成功
	 */
	public static final int USER_ADD_SUCCESS = 11;

	/**
	 * 添加用户失败
	 */
	public static final int USER_ADD_FAIL = 12;
}