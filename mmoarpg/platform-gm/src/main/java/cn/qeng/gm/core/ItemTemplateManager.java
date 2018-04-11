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
package cn.qeng.gm.core;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.annotation.PostConstruct;

import org.springframework.stereotype.Component;

/**
 * 所有道具模板管理类.
 *
 * @author 小流氓(mingkai.zhou@qeng.net)
 */
@Component
public class ItemTemplateManager {
	private final Map<String, String> items = new HashMap<>();

	@PostConstruct
	public void init() throws IOException {
		ClassLoader cl = ItemTemplateManager.class.getClassLoader();
		Properties props = new Properties();
		try (InputStream is = cl.getResourceAsStream("template-item.properties"); InputStreamReader isr = new InputStreamReader(is, "UTF-8")) {
			props.load(isr);
		}
		props.forEach((k, v) -> items.put(k.toString().substring(10), v.toString()));
	}

	public Map<String, String> getItems() {
		return items;
	}
}