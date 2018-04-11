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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import cn.qeng.gm.core.PageConstant;
import cn.qeng.gm.core.log.LogClassify;
import cn.qeng.gm.core.log.OperationType;
import cn.qeng.gm.module.backstage.domain.Log;
import cn.qeng.gm.module.backstage.domain.LogRepository;

/**
 * 日志服务类.
 *
 * @author 小流氓(mingkai.zhou@qeng.net)
 */
@Service
public class LogService {
	@Autowired
	private LogRepository logRepository;

	/**
	 * 记录一条日志.
	 */
	public void log(String username, String name, OperationType operationType, String ip, long cost) {
		this.log(username, name, operationType, "", ip, cost);
	}

	public void log(String username, String name, OperationType operationType, String arguments, String ip, long cost) {
		Log log = new Log();
		log.setUsername(username);
		log.setName(name);
		log.setIp(ip);
		log.setCost(cost);
		log.setArguments(arguments);
		log.setOperation(operationType.name());
		log.setCreateTime(new Date());
		logRepository.save(log);
	}

	/**
	 * 查询日志
	 */
	public Page<Log> getLogs(String username, String operation, String ip, Date startTime, Date endTime, int page) {
		return logRepository.findAll(where(username, operation, ip, startTime, endTime), new PageRequest(page, PageConstant.MAX_SIZE, new Sort(Sort.Direction.DESC, "id")));
	}

	/**
	 * 条件查询时动态组装条件
	 */
	Specification<Log> where(String username, String operation, String ip, Date startTime, Date endTime) {
		return new Specification<Log>() {
			@Override
			public Predicate toPredicate(Root<Log> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				List<Predicate> predicates = new ArrayList<Predicate>();
				// 账号
				if (!StringUtils.isEmpty(username)) {
					predicates.add(cb.equal(root.<String> get("username"), username));
				}

				// 操作类型
				if (!StringUtils.isEmpty(operation)) {
					final LogClassify classify = LogClassify.valueOf(operation);
					predicates.add(cb.in(root.get("operation")).value(Arrays.stream(OperationType.values()).filter(v -> v.getClassify() == classify).map(v -> v.name()).collect(Collectors.toList())));
				}

				// IP
				if (!StringUtils.isEmpty(ip)) {
					predicates.add(cb.equal(root.<String> get("ip"), ip));
				}

				// 日期
				if (startTime != null && endTime != null) {
					predicates.add(cb.between(root.<Date> get("createTime"), startTime, endTime));
				}

				return query.where(predicates.toArray(new Predicate[predicates.size()])).getRestriction();
			}
		};
	}
}