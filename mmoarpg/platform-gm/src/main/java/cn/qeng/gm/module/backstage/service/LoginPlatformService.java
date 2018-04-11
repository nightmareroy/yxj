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

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import cn.qeng.gm.module.backstage.domain.LoginPlatform;
import cn.qeng.gm.module.backstage.domain.LoginPlatformRepository;

/**
 * 登录平台业务逻辑处理类.
 *
 * @author 小流氓(mingkai.zhou@qeng.net)
 */
@Service
public class LoginPlatformService {
	@Autowired
	private LoginPlatformRepository loginPlatformRepository;

	/**
	 * 初始化登录平台，并开始的时候默认创建一个.
	 */
	String initDefaultLoginPlatform() {
		// 开始加载所有的列表
		Page<LoginPlatform> loginPlatform = loginPlatformRepository.findAll(new PageRequest(1, 1, new Sort(Sort.Direction.ASC, "id")));
		// 我们首先判断一下列表是否为空，如果是空的，那么就创建一个吧
		if (!loginPlatform.getContent().isEmpty()) {
			// 不为空，那就按id返回列表信息
			return loginPlatform.getContent().get(0).getId();
		}
		// 当是空值，那么开始创建吧
		LoginPlatform platform = new LoginPlatform();
		platform.setId("default");
		platform.setName("密码登录");
		platform.setSecretkey(UUID.randomUUID().toString());
		platform.setPassword(true);
		platform.setCreateTime(new Date());
		platform.setModifyTime(platform.getCreateTime());
		// 保存
		loginPlatformRepository.save(platform);
		// 把创建的返回
		return platform.getId();
	}

	/**
	 * 获取全部的登录平台信息
	 */
	public Page<LoginPlatform> getPlatforms(int page, int size) {
		// 获取库里面所有的登录平台信息
		List<LoginPlatform> list = loginPlatformRepository.findAll();
		// 按照时间的先后顺序进行排序
		Collections.sort(list, (o2, o1) -> o1.getCreateTime().compareTo(o2.getCreateTime()));
		// 返回信息
		return new PageImpl<>(list, new PageRequest(page, size), list.size());
	}

	/**
	 * 获取全部的登录平台组信息
	 */
	public List<LoginPlatform> getAllPlatforms() {
		return loginPlatformRepository.findAll();
	}

	/**
	 * 获取全部的登录平台组信息(这里是用作页面列表，加载速度优化进行修改方案)
	 */
	public Map<String, LoginPlatform> getAllPlatformes() {
		return loginPlatformRepository.findAll().stream().collect(Collectors.toMap(LoginPlatform::getId, Function.identity()));
	}

	/**
	 * 获得某个登录平台的信息
	 * 
	 */
	public LoginPlatform getLoginPlatform(String id) {
		return loginPlatformRepository.findOne(id);
	}

	/**
	 * 添加一条登录平台信息
	 */
	public void add(String secretkey, String loginPlatformName, String[] platfroms, String loginPlatformId) {
		LoginPlatform loginPlatform = new LoginPlatform();
		loginPlatform.setCreateTime(new Date());
		loginPlatform.setModifyTime(loginPlatform.getCreateTime());
		loginPlatform.setName(loginPlatformName);
		loginPlatform.setId(loginPlatformId);
		loginPlatform.setSecretkey(secretkey);
		loginPlatformRepository.save(loginPlatform);
	}

	/**
	 * 编辑登录平台
	 * 
	 */
	public void edit(String secretkey, String loginPlatformName, String loginPlatformId, String[] platfroms) {
		// 拿loginPlatformId（id）去库里面检索是否存在
		LoginPlatform loginPlatform = this.getLoginPlatform(loginPlatformId);
		if (loginPlatform == null) {
			// 做添加动作
			this.add(secretkey, loginPlatformName, platfroms, loginPlatformId);
			// 做编辑动作
		} else {
			loginPlatform.setModifyTime(new Date());
			loginPlatform.setName(loginPlatformName);
			loginPlatform.setId(loginPlatformId);
			loginPlatform.setSecretkey(secretkey);
			loginPlatformRepository.save(loginPlatform);
		}
	}

	/**
	 * 删除登录平台
	 */
	public void delete(String id) {
		loginPlatformRepository.delete(id);
	}
}