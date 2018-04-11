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

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;

import cn.qeng.common.gm.RedisKeyConst;
import cn.qeng.common.gm.po.GameNoticePO;
import cn.qeng.gm.core.ErrorCode;
import cn.qeng.gm.core.RedisManager;
import cn.qeng.gm.core.session.SessionUser;
import cn.qeng.gm.module.game.domain.GameNotice;
import cn.qeng.gm.module.game.domain.GameNoticeRepository;

/**
 * 游戏内公告业务处理
 *
 * @author 小流氓(mingkai.zhou@qeng.net)
 */
@Service
public class GameNoticeService {
	private final static Logger logger = LogManager.getLogger(GameNoticeService.class);

	@Autowired
	private RedisManager redisManager;
	@Autowired
	private GameNoticeRepository gameNoticeRepository;

	/**
	 * 添加公告
	 */
	public int addNotice(SessionUser user, String title, String content) {
		GameNotice notice = new GameNotice();
		notice.setTitle(title);
		notice.setContent(content);
		notice.setCreateTime(new Date());
		notice.setModifyTime(notice.getCreateTime());
		notice.setName(user.getName());
		notice.setUsername(user.getUsername());
		gameNoticeRepository.save(notice);
		logger.info("添加游戏内公告 id={},title={},content={}", notice.getId(), title, content);
		this.publishNotice(notice);

		return ErrorCode.OK;
	}

	/**
	 * 所有的公告
	 */
	public Map<String, List<GameNotice>> getNotices() {
		Map<String, List<GameNotice>> result = new HashMap<>();
		result.put("data", gameNoticeRepository.findAll());
		return result;
	}

	public GameNotice getNotice(int id) {
		return gameNoticeRepository.findOne(id);
	}

	/**
	 * 编辑一个公告.
	 */
	public int editNotice(SessionUser user, int id, String title, String content) {
		GameNotice notice = this.getNotice(id);
		notice.setTitle(title);
		notice.setContent(content);
		notice.setModifyTime(new Date());
		notice.setName(user.getName());
		notice.setUsername(user.getUsername());
		gameNoticeRepository.save(notice);
		logger.info("编辑游戏内公告 id={},title={},content={}", notice.getId(), title, content);
		this.publishNotice(notice);

		return ErrorCode.OK;
	}

	/**
	 * 删除公告
	 */
	public void deleteNotice(int id) {
		GameNotice notice = this.getNotice(id);
		if (notice != null) {
			gameNoticeRepository.delete(notice);
			// 直接删除
			redisManager.getGlobalRedis().hdel(RedisKeyConst.REDIS_KEY_GAME_NOTICE, String.valueOf(id));
		}
	}

	/**
	 * 发布此公告.
	 */
	private void publishNotice(GameNotice notice) {
		// 直接写Redis...
		GameNoticePO po = new GameNoticePO();
		po.setId(notice.getId());
		po.setContent(replacContent(notice.getContent()));
		po.setName(notice.getTitle());
		po.setCreateDate(new SimpleDateFormat("yyyy-MM-dd").format(notice.getCreateTime()));
		redisManager.getGlobalRedis().hset(RedisKeyConst.REDIS_KEY_GAME_NOTICE, String.valueOf(notice.getId()), JSON.toJSONString(po));
	}

	// FIXME 与登录公告中一起提为一个工具类
	private String replacContent(String content) {
		content = content.replace("span", "font");
		content = content.replace("style=\"", "");
		content = content.replace("px", "");
		// 去掉样式最后一个;
		content = content.replace(";\"", "\"");
		content = content.replace(";", "\" ");
		content = content.replace(":", "=\"");
		content = content.replace("#", "ff");
		content = content.replace("font-size", "size");
		content = content.replace("<br />", "<br/>");
		content = content.replace("<p>", "");
		content = content.replace("</p>", "<br/>");
		content = content.replaceAll("\n|\t|\n\t", "");
		content = content.replace("> ", ">");
		content = "<f>" + content + "</f>";
		return content;
	}
}