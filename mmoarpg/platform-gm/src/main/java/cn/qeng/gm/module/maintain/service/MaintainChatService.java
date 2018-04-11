/*
 * Copyright © 2016 qeng.cn All Rights Reserved.
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
package cn.qeng.gm.module.maintain.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cn.qeng.gm.core.dfa.DFAScanner;
import cn.qeng.gm.core.session.SessionUser;
import cn.qeng.gm.module.maintain.domain.ChatConfig;
import cn.qeng.gm.module.maintain.domain.ChatConfigRepository;
import cn.qeng.gm.module.maintain.domain.ChatConfigStatus;
import cn.qeng.gm.module.maintain.domain.ChatConfigStatusRepository;

/**
 * 反广告配置控制器
 *
 * @author 小流氓(176543888@qq.com)
 */
@Service
public class MaintainChatService {
	@Autowired
	private ChatConfigRepository configRepository;
	@Autowired
	private ChatConfigStatusRepository statusRepository;

	private DFAScanner dFAScanner;

	@PostConstruct
	public void refCache() {
		int status = getStatus();
		if (status == 0) {
			dFAScanner = null;
			return;
		}
		;
		List<ChatConfig> list = configRepository.findAll();
		if (list == null || list.isEmpty()) {
			dFAScanner = null;
			return;
		}
		List<String> values = new ArrayList<>();
		for (ChatConfig cc : list) {
			values.add(cc.getValue());
		}
		dFAScanner = new DFAScanner(" `~!@#$%^&*()_-+={[}]|\\:;\"'<,>.?/！￥%……｛｝【】", values);
	}

	public boolean findSensitiveWord(String text) {
		if (dFAScanner == null)
			return false;
		return dFAScanner.findSensitiveWord(text);
	}

	public List<ChatConfig> getForbidConfig() {
		return configRepository.findAll();
	}

	public String addForbidChat(SessionUser user, String text) {
		if (text == null || text.isEmpty())
			return "0";
		ChatConfig config = configRepository.findOneByValue(text);
		if (config != null)
			return "0";
		config = new ChatConfig();
		config.setName(user.getName());
		config.setUsername(user.getUsername());
		config.setValue(text);
		config.setCreateTime(new Date());
		configRepository.save(config);
		refCache();
		return "1";
	}

	public int getStatus() {
		List<ChatConfigStatus> list = statusRepository.findAll();
		if (list == null || list.isEmpty()) {
			ChatConfigStatus status = new ChatConfigStatus();
			status.setId(1);
			status.setStatus(0);
			statusRepository.saveAndFlush(status);
			list = statusRepository.findAll();
		}
		ChatConfigStatus status = list.get(0);
		return status.getStatus();
	}

	public String statusUpdate(int state) {
		List<ChatConfigStatus> list = statusRepository.findAll();
		if (list == null || list.isEmpty()) {
			ChatConfigStatus status = new ChatConfigStatus();
			status.setId(1);
			status.setStatus(0);
			statusRepository.saveAndFlush(status);
			list = statusRepository.findAll();
		}
		ChatConfigStatus status = list.get(0);
		status.setStatus(state);
		statusRepository.save(status);
		refCache();
		return "success";
	}

	public String delete(int id) {
		configRepository.delete(id);
		refCache();
		return "1";
	}
}