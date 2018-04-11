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
package cn.qeng.gm.module.maintain.service;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import cn.qeng.gm.core.PageConstant;
import cn.qeng.gm.module.maintain.domain.Email;
import cn.qeng.gm.module.maintain.domain.EmailRepository;

/**
 * 运维工具，接收异常邮件用的邮箱管理.
 *
 * @author 小流氓(mingkai.zhou@qeng.net)
 */
@Service
public class EmailService {

	@Autowired
	private EmailRepository emailRepository;

	public Page<Email> getAll(int page) {
		return emailRepository.findAll(new PageRequest(page, PageConstant.MAX_SIZE));
	}

	/**
	 * 获取接收监控异常接收者邮箱...
	 */
	public List<Email> getEmailByMonitor() {
		return emailRepository.findAll();
	}

	public void edit(String addr, String name, String remarks) {
		Email email = this.getEmail(addr);
		if (email == null) {
			email = new Email();
			email.setAddr(addr);
			email.setCreateTime(new Date());
		}
		email.setName(name);
		email.setRemarks(remarks);
		email.setModifyTime(new Date());
		emailRepository.save(email);
	}

	public Email getEmail(String addr) {
		return emailRepository.findOne(addr);
	}

	public void delete(String addr) {
		emailRepository.delete(addr);
	}
}