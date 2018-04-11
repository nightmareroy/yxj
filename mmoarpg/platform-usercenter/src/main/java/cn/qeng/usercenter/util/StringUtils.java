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
package cn.qeng.usercenter.util;

/**
 * 
 *
 * @author 周明凯(zhoumingkai@qeng.cn)
 */
public class StringUtils {
	/**
	 * The empty String {@code ""}.
	 * 
	 * @since 2.4.2
	 */
	public static final String EMPTY = "";

	/**
	 * <p>
	 * 检测字符串是否为 null或"".
	 * </p>
	 *
	 * <pre>
	 * StringUtils.isEmpty(null)      = true
	 * StringUtils.isEmpty("")        = true
	 * StringUtils.isEmpty(" ")       = false
	 * StringUtils.isEmpty("test")     = false
	 * StringUtils.isEmpty("  test  ") = false
	 * </pre>
	 *
	 * @param text 被检测字符串
	 * @return 如果字符为null或""则返回true,否则返回false.
	 */
	public static boolean isEmpty(final String text) {
		return text == null || text.length() == 0;
	}

	/**
	 * <p>
	 * 检测字符串是否不为 null且不为"".
	 * </p>
	 *
	 * <pre>
	 * StringUtils.isNotEmpty(null)      = false
	 * StringUtils.isNotEmpty("")        = false
	 * StringUtils.isNotEmpty(" ")       = true
	 * StringUtils.isNotEmpty("test")    = true
	 * StringUtils.isNotEmpty("  test ") = true
	 * </pre>
	 *
	 * @param text 被检测字符串
	 * @return 如果字符不为 null且不为""则返回true,否则返回false.
	 */
	public static boolean isNotEmpty(final String text) {
		return !isEmpty(text);
	}
}
