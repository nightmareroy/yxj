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
package cn.qeng.gm.module.game.service;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;

import cn.qeng.common.gm.vo.GmManagerVO;
import cn.qeng.common.gm.vo.GmResult;
import cn.qeng.gm.api.ChatBacklistAPI;
import cn.qeng.gm.api.ForbidAPI;
import cn.qeng.gm.api.QueryPublishAPI;
import cn.qeng.gm.core.ErrorCode;
import cn.qeng.gm.core.PageConstant;
import cn.qeng.gm.core.session.SessionUser;
import cn.qeng.gm.module.game.domain.Publish;
import cn.qeng.gm.module.game.domain.PublishRepository;
import cn.qeng.gm.util.DateUtils;

/**
 * 惩罚实现类
 *
 * @author 小流氓(mingkai.zhou@qeng.net)
 */
@Service
public class PublishService {

	private final static Logger logger = LogManager.getLogger(PublishService.class);
	// 聊天IP黑名单...
	private static final ConcurrentHashMap<String, AtomicInteger> backlist = new ConcurrentHashMap<>();
	@Autowired
	private PublishRepository publishRepository;

	/**
	 * 惩罚处理
	 */
	public String publish(SessionUser user, int serverId, String playerName, String reason, Date endPublishTime, int type) {
		// PlayerName 去拉取 PlayerId
		String json = new QueryPublishAPI(playerName).request(serverId).getResult();
		if (ErrorCode.SERVER_NOT_FOUND.equals(json)) {
			return json;
		}

		GmResult<GmManagerVO> result = JSON.parseObject(json, new TypeReference<GmResult<GmManagerVO>>() {});
		if (result == null || result.getRows() == null || result.getRows().isEmpty()) {
			return ErrorCode.PLAYER_NOT_FOUND;// 角色名称不存在.
		}

		String roleId = result.getRows().get(0).getId();
		if (StringUtils.isEmpty(roleId)) {
			return ErrorCode.PLAYER_NOT_FOUND;// 角色名称不存在.
		}

		return publish(user, serverId, roleId, playerName, reason, endPublishTime, type);
	}

	public String publish(SessionUser user, int serverId, String playerId, String playerName, String reason, Date endPublishTime, int type) {
		// 冻结、解冻、禁言、解禁、T下线
		// LOCK, UNLOCK, SHUTUP, UNSHUTUP, KICK
		String time = "";
		if (type == 0 || type == 2 || type == 5) {
			time = DateUtils.formatyyyyMMddHHmmss(endPublishTime);
		}
		new ForbidAPI(playerId, type == 5 ? 2 : type, time, reason).request(serverId);

		Publish publish = new Publish();
		publish.setServerId(serverId);
		publish.setPlayerId(playerId);
		publish.setPlayerName(playerName);
		publish.setType(type);
		publish.setPublishTime(endPublishTime);
		publish.setReason(reason);
		publish.setCreatetime(new Date());
		// 自动禁言没有操作者
		if (user == null) {
			publish.setUsername("");
			publish.setName("");
		} else {
			publish.setUsername(user.getUsername());
			publish.setName(user.getName());
		}
		publishRepository.save(publish);
		return String.valueOf(ErrorCode.OK);
	}

	/**
	 * 自动禁言一小时
	 */
	public String autoPublish(int serverId, String playerId, String playerName, String reason, String ip) {
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.HOUR_OF_DAY, 6);// 禁言一小时

		// 自动处理发广告的机器人...
		try {
			if (!StringUtils.isEmpty(ip)) {
				int count = backlist.computeIfAbsent(ip, key -> new AtomicInteger()).incrementAndGet();
				if (count > 3) {
					new ChatBacklistAPI(ip).request(serverId);
					logger.warn("添加聊天黑名单IP={}", ip);
				}
			}
		} catch (Exception e) {
			logger.warn("添加聊天黑名单异常。", e);
		}

		return publish(null, serverId, playerId, playerName, reason, calendar.getTime(), 5);
	}

	/**
	 * 获取玩家惩处记录
	 */
	public Page<Publish> getPublishByWhere(Date start, Date end, String username, int type, String playerName, int page) {
		return publishRepository.findAll(where(start, end, username, type, playerName), new PageRequest(page, PageConstant.MAX_SIZE, new Sort(Sort.Direction.DESC, "id")));
	}

	/**
	 * 条件查询时动态组装条件
	 */
	Specification<Publish> where(Date start, Date end, String username, int type, String playerName) {
		return new Specification<Publish>() {
			@Override
			public Predicate toPredicate(Root<Publish> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				List<Predicate> predicates = new ArrayList<Predicate>();
				// 账号
				if (!StringUtils.isEmpty(username)) {
					predicates.add(cb.equal(root.<String>get("username"), username));
				}

				// type
				if (type > -1) {
					predicates.add(cb.equal(root.<Integer>get("type"), type));
				}

				// 角色名称
				if (!StringUtils.isEmpty(playerName)) {
					predicates.add(cb.equal(root.<String>get("playerName"), playerName));
				}

				// 日期
				if (start != null && end != null) {
					predicates.add(cb.between(root.<Date>get("createtime"), start, end));
				}

				return query.where(predicates.toArray(new Predicate[predicates.size()])).getRestriction();
			}
		};
	}
}