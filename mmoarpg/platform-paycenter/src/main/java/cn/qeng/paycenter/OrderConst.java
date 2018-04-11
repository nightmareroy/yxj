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
package cn.qeng.paycenter;

/**
 * 订单常量类
 *
 * @author 周明凯(zhoumingkai@qeng.cn)
 */
public class OrderConst {
	/**
	 * 订单状态：0=待支付
	 */
	public static final int STATE_UNPAID = 0;

	/**
	 * 订单状态：1=待发货
	 */
	public static final int STATE_UNSHIPPED = 1;

	/**
	 * 订单状态：2=交易完成
	 */
	public static final int STATE_COMPLETE = 2;

	/**
	 * 订单状态：3=交易失败
	 */
	public static final int STATE_FAIL = 3;

	/**
	 * 订单状态：4=交易异常
	 */
	public static final int STATE_EXCEPTION = 4;

}