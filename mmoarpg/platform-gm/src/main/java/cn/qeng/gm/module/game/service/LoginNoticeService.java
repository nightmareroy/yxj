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

import java.util.Date;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.wanniu.AuthServer;

import cn.qeng.common.gm.po.AnnouncementPO;
import cn.qeng.gm.core.ErrorCode;
import cn.qeng.gm.core.session.SessionUser;
import cn.qeng.gm.module.game.domain.LoginNotice;
import cn.qeng.gm.module.game.domain.LoginNoticeRepository;
import cn.qeng.gm.module.game.domain.NoticeResult;

/**
 * 登录前公告业务处理
 *
 * @author 小流氓(mingkai.zhou@qeng.net)
 */
@Service
public class LoginNoticeService {
	private final static Logger logger = LogManager.getLogger(LoginNoticeService.class);
	@Autowired
	private LoginNoticeRepository noticeRepository;

	/**
	 * 添加公告
	 */
	public int addNotice(SessionUser user, String title, String content) {
		LoginNotice notice = new LoginNotice();
		notice.setTitle(title);
		notice.setContent(content);
		notice.setCreateTime(new Date());
		notice.setModifyTime(notice.getCreateTime());
		notice.setName(user.getName());
		notice.setUsername(user.getUsername());
		noticeRepository.save(notice);
		logger.info("添加登录公告 id={},title={},content={}", notice.getId(), title, content);
		return ErrorCode.OK;
	}

	/**
	 * 所有的公告
	 */
	public NoticeResult getNotices() {
		NoticeResult result = new NoticeResult();
		List<LoginNotice> r = noticeRepository.findAll();
		if (r != null && r.size() > 0) {
			result.getData().addAll(r);
		}
		return result;
	}

	public LoginNotice getNotice(int id) {
		return noticeRepository.findOne(id);
	}

	/**
	 * 编辑一个公告.
	 */
	public int editNotice(SessionUser user, int id, String title, String content) {
		LoginNotice notice = this.getNotice(id);
		notice.setTitle(title);
		notice.setContent(content);
		notice.setModifyTime(new Date());
		notice.setName(user.getName());
		notice.setUsername(user.getUsername());
		noticeRepository.save(notice);
		logger.info("编辑登录公告 id={},title={},content={}", notice.getId(), title, content);

		if (notice.isEnable()) {
			this.publishNotice(notice);
		}

		return ErrorCode.OK;
	}

	/**
	 * 删除公告
	 */
	public void deleteNotice(int id) {
		LoginNotice notice = this.getNotice(id);
		if (notice != null) {
			noticeRepository.delete(notice);
			// 如果启用还要通知LoginServer
			if (notice.isEnable()) {
				this.publishNotice(notice);
			}
		}
	}

	public void enableNotice(int id) {
		for (LoginNotice notice : noticeRepository.findAllByEnable(true)) {
			notice.setEnable(false);
			notice.setModifyTime(new Date());
			noticeRepository.save(notice);
			logger.info("取消启用登录公告 id={},title={},content={}", notice.getId(), notice.getTitle(), notice.getContent());
		}

		// 启用新的
		LoginNotice notice = this.getNotice(id);
		notice.setEnable(true);
		notice.setModifyTime(new Date());
		noticeRepository.save(notice);
		logger.info("启用登录公告 id={},title={},content={}", notice.getId(), notice.getTitle(), notice.getContent());

		this.publishNotice(notice);
	}

	/**
	 * 发布此公告.
	 */
	private void publishNotice(LoginNotice notice) {
		AuthServer.del(AuthServer.ANNOUNCE);// 删除所有.

		AnnouncementPO po = new AnnouncementPO();
		po.setName(notice.getTitle());
		po.setContent(replacContent(notice.getContent()));
		po.setIsUse(notice.isEnable());
		AuthServer.hset(AuthServer.ANNOUNCE, String.valueOf(notice.getId()), JSON.toJSONString(po));

		// 发布变更，内容无所谓
		AuthServer.publish(AuthServer.K_LOGIN_ANNOUNCE, JSONObject.parseObject("{}"));
	}

	private String replacContent(String content) {
		content = content.replaceAll("<span ", "<font ");
		content = content.replaceAll(" style=\"", " ");
		content = content.replaceAll("px", "");
		content = content.replaceAll("</span>", "</f>");
		content = content.replaceAll(":#", "='ff");
		content = content.replaceAll("<font ", "<f ");
		content = content.replaceAll("font-size:", "size='");
		content = content.replaceAll("&nbsp;", " ");
		content = content.replaceAll("\">", "'>");
		content = content.replaceAll(";'", "'");
		// 去掉样式最后一个;
		content = content.replaceAll("<br />", "<br/>");
		content = content.replaceAll("<p>", "");
		content = content.replaceAll("</p>", "<br/><br/>");
		content = content.replaceAll("\n|\t|\n\t", "");
		content = content.replaceAll("> ", ">");
		content = "<f>" + content + "</f>";
		return content;
	}
}