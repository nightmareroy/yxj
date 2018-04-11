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
package com.wanniu.game.hotupdate;

import java.lang.instrument.ClassDefinition;
import java.lang.instrument.UnmodifiableClassException;

/**
 * 
 *
 * @author Feiling(feiling@qeng.cn)
 */
public class HotUpdateService {

	@SuppressWarnings("rawtypes")
	public static String changeClass(String className, byte[] classBt) {

		try {
			Class cls = Class.forName(className);
			if (JavaAgent.INST == null) {
				return "启动命令行没有写上正确的格式:java -javaagent:agent.jar -jar xx.jar,或者没有找到agent.jar";
			}
			JavaAgent.INST.redefineClasses(new ClassDefinition(cls, classBt));
		} catch (ClassNotFoundException e) {
			return "请写上完整的类名.例如com.wanniu.game.hotupdate.HotUpdateService";
		} catch (UnmodifiableClassException e) {
			return "热更的时候发生未知错误,请检查:" + e.getMessage();
		}
		return "OK";
	}
}