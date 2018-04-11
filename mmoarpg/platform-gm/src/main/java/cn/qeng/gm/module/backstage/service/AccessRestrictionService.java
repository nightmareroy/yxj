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
package cn.qeng.gm.module.backstage.service;

import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import cn.qeng.gm.module.backstage.domain.AccessRestriction;
import cn.qeng.gm.module.backstage.domain.AccessRestrictionRepository;
import cn.qeng.gm.util.IpUtils;

/**
 * 访问权限数据处理类
 *
 * @author 小流氓(mingkai.zhou@qeng.net)
 */
@Service
public class AccessRestrictionService {
	@Autowired
	private AccessRestrictionRepository accessRestrictionRepository;
	// 缓存配置...
	private ConcurrentHashMap<String, Set<String>> caches = new ConcurrentHashMap<>();

	@PostConstruct
	private void RefreshCache() {
		ConcurrentHashMap<String, Set<String>> caches = new ConcurrentHashMap<>();
		accessRestrictionRepository.findAll().forEach(v -> {
			Set<String> result = caches.computeIfAbsent(v.getIpUsername(), key -> new HashSet<>());
			Set<String> avaliIpList = IpUtils.getAvaliIpList(v.getIp());
			if (!avaliIpList.isEmpty()) {
				result.addAll(avaliIpList);
			}
		});
		this.caches = caches;
	}

	public boolean checkLoginIP(String pf, String ipAddress) {
		// 如果是内网IP直接放行...
		if (IpUtils.isInnerIP(ipAddress)) {
			return true;
		}

		Set<String> cache = caches.getOrDefault(pf, Collections.emptySet());
		if (cache.isEmpty()) {
			return true;
		}

		return IpUtils.checkLoginIP(ipAddress, cache);
	}

	/**
	 * 用来做后台展示所有的访问限制的内容
	 */
	public Page<AccessRestriction> getAllAccessibleRelations(int page, int size) {
		// 获取库里面所有的访问限制信息
		List<AccessRestriction> accessRestrictions = accessRestrictionRepository.findAll();
		// 按照时间的先后顺序进行排序
		Collections.sort(accessRestrictions, (o2, o1) -> o1.getCreateTime().compareTo(o2.getCreateTime()));
		// 返回信息
		return new PageImpl<>(accessRestrictions, new PageRequest(page, size), accessRestrictions.size());
	}

	/**
	 * 访问一个已知id的访问限制
	 */
	public AccessRestriction getAccessibleRelation(int id) {
		return accessRestrictionRepository.findOne(id);
	}

	/**
	 * 后台添加新的访问限制信息
	 */
	public void add(String name, String ip, String ipUserName) {
		AccessRestriction accessRestriction = new AccessRestriction();
		accessRestriction.setCreateTime(new Date());
		accessRestriction.setModifyTime(accessRestriction.getCreateTime());
		accessRestriction.setName(name);
		accessRestriction.setIpUsername(ipUserName);
		accessRestriction.setIp(ip);
		// 保存一下
		accessRestrictionRepository.save(accessRestriction);

		this.RefreshCache();
	}

	/**
	 * 编辑范文限制的内容
	 */
	public void edit(int id, String name, String ip, String ipUserName) {
		// 拿id到库里面去检索，看是否存在
		AccessRestriction accessRestriction = this.getAccessibleRelation(id);
		// 如果不存在
		if (accessRestriction == null) {
			this.add(name, ip, ipUserName);
			// 存在就开始修改吧
		} else {
			accessRestriction.setModifyTime(new Date());
			accessRestriction.setIpUsername(ipUserName);
			accessRestriction.setIp(ip);
			accessRestriction.setName(name);
			// 保存一下
			accessRestrictionRepository.save(accessRestriction);

			this.RefreshCache();
		}
	}

	/**
	 * 删除
	 */
	public void delete(int id) {
		accessRestrictionRepository.delete(id);
		this.RefreshCache();
	}
}