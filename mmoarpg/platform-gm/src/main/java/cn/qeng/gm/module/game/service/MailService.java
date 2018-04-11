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
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import com.alibaba.fastjson.JSON;

import cn.qeng.gm.api.MailDeleteAPI;
import cn.qeng.gm.api.MailSendAPI;
import cn.qeng.gm.api.rpc.RpcResponse;
import cn.qeng.gm.core.ErrorCode;
import cn.qeng.gm.core.PageConstant;
import cn.qeng.gm.core.session.SessionUser;
import cn.qeng.gm.module.game.WelfareConstant;
import cn.qeng.gm.module.game.domain.BatchApply;
import cn.qeng.gm.module.game.domain.BatchApplyRepository;
import cn.qeng.gm.module.game.domain.ItemInfo;
import cn.qeng.gm.module.game.domain.Mail;
import cn.qeng.gm.module.game.domain.MailRepository;
import cn.qeng.gm.module.maintain.service.ServerService;
import cn.qeng.gm.util.ExcelUtils;

/**
 * 福利系统业务逻辑处理类.
 *
 * @author 小流氓(mingkai.zhou@qeng.net)
 */
@Service
public class MailService {
	private final static Logger logger = LogManager.getLogger(MailService.class);
	@Autowired
	private ServerService serverService;
	@Autowired
	private MailRepository welfareRepository;
	@Autowired
	private BatchApplyRepository batchApplyRepository;

	/**
	 * 申请福利
	 */
	public String apply(SessionUser user, int[] serverIds, int mailType, String playerId, String title, String content, String reason, List<ItemInfo> itemList, Date createRoleTime, int minLevel) {
		Mail mail = new Mail();
		mail.setMailType(mailType);
		if (mailType == 2 || mailType == 3) {
			mail.setPlayerId("");
		} else {
			mail.setPlayerId(playerId);
		}
		// 区服ID...
		mail.setSidList(JSON.toJSONString(serverIds));
		mail.setSidListSize(serverIds.length);
		mail.setCreateRoleTime(createRoleTime);
		mail.setMinLevel(minLevel);

		mail.setTitle(title);
		mail.setContent(content);
		mail.setReason(reason);
		mail.setItemList(JSON.toJSONString(itemList));
		mail.setCreateTime(new Date());
		mail.setApplyName(user.getName());
		mail.setApplyUsername(user.getUsername());
		mail.setState(WelfareConstant.PENDING_APPROVAL);
		welfareRepository.save(mail);
		return "ok";
	}

	/**
	 * 同意发放
	 */
	public void agreeMail(SessionUser user, int id) {
		logger.info("同意审批福利  user={},id={}", user.getUsername(), id);
		Mail mail = welfareRepository.findOne(id);
		if (mail.getState() != WelfareConstant.ALREADY_APPROVE) {
			mail.setAuditName(user.getName());
			mail.setAuditUsername(user.getUsername());
			mail.setState(WelfareConstant.ALREADY_APPROVE);
			welfareRepository.save(mail);
			postMail(mail);
		}
	}

	/**
	 * 向玩家发送奖励邮件
	 */
	public void postMail(Mail mail) {
		int type = (mail.getMailType() == 2 || mail.getMailType() == 3) ? 1 : 0;
		StringBuilder sb = new StringBuilder(1024);
		for (ItemInfo item : mail.getItemListx()) {
			sb.append(item.getItemId()).append(":").append(item.getItemNumber()).append(";");
		}
		if (sb.length() > 0) {
			sb.deleteCharAt(sb.length() - 1);
		}
		MailSendAPI api = new MailSendAPI(type, mail.getPlayerId(), mail.getTitle(), mail.getContent(), mail.getCreateRoleTime(), mail.getMinLevel(), sb.toString(), mail.getId());

		sb.setLength(0);// 清0记录结果
		for (Integer serverId : serverService.getSidList(mail.getSidList())) {
			RpcResponse response = api.request(serverId);
			if (ErrorCode.SERVER_NOT_FOUND.equals(response.getResult())) {
				sb.append(serverId).append(":Maintain;");
				continue;
			}
			// 结果
			int statu = response.getStatus().getOrDefault(serverId, (byte) 1);
			if (statu == -2) {
				sb.append(mail.getPlayerId()).append(":not found;");
			}
		}
		mail.setResult(sb.toString());
		if (!mail.getResult().isEmpty()) {
			mail.setState(WelfareConstant.MAIL_SEND_FAIL);
		}
		welfareRepository.save(mail);
	}

	/**
	 * 拒绝发放
	 */
	public void refuseMail(SessionUser user, int id) {
		logger.info("拒绝审批福利  user={},id={}", user.getUsername(), id);
		Mail mail = welfareRepository.findOne(id);
		mail.setState(WelfareConstant.HAS_REFUSED);
		mail.setAuditName(user.getName());
		mail.setAuditUsername(user.getUsername());
		welfareRepository.save(mail);
	}

	/**
	 * 回收
	 */
	public void deleteMail(int id) {
		Mail mail = welfareRepository.findOne(id);
		mail.setState(WelfareConstant.MAIL_SEND_BACK);
		StringBuilder sb = new StringBuilder(1024);
		for (Integer serverId : serverService.getSidList(mail.getSidList())) {
			RpcResponse response = new MailDeleteAPI(id).request(serverId);
			if (ErrorCode.SERVER_NOT_FOUND.equals(response.getResult())) {
				sb.append(serverId).append(":Maintain;");
				continue;
			}
		}
		mail.setResult(sb.toString());
		if (!mail.getResult().isEmpty()) {
			mail.setState(WelfareConstant.MAIL_SEND_BACK_FAIL);
		}
		welfareRepository.save(mail);
	}

	/**
	 * 所有邮件列表
	 */
	public Page<Mail> getWelfare(int page) {
		return welfareRepository.findAll(new PageRequest(page, PageConstant.MAX_SIZE, new Sort(Sort.Direction.DESC, "createTime", "id")));
	}

	public Page<BatchApply> getBatchApply(int page) {
		return batchApplyRepository.findAll(new PageRequest(page, PageConstant.MAX_SIZE, new Sort(Sort.Direction.ASC, "state", "createTime")));
	}

	/**
	 * 处理上传的文件...
	 */
	public int handleUploadFile(SessionUser user, CommonsMultipartFile file) throws Exception {
		if (file == null) {
			return ErrorCode.ERROR;
		}
		List<BatchApply> applys = new ArrayList<>();

		ExcelUtils.readData(file.getInputStream(), file.getFileItem().getName(), 1).forEach(row -> {
			// 用户ID，角色名称，邮件标题，邮件内容，邮件附件
			BatchApply apply = new BatchApply();
			apply.setPlayerId(row[0]);
			apply.setPlayerName(row[1]);
			apply.setTitle(row[2]);
			apply.setContent(row[3]);
			apply.setItemInfo(row[4]);
			apply.setCreateTime(new Date());
			apply.setModifyTime(apply.getCreateTime());
			// 状态
			if (v(apply.getItemInfo()) != null) {
				apply.setState(2);// 待发送
			} else {
				apply.setState(0);// 奖励格式异常
			}
			applys.add(apply);
		});
		if (!applys.isEmpty()) {
			batchApplyRepository.save(applys);
		}
		return ErrorCode.OK;
	}

	public Map<Integer, Integer> v(String item) {
		try {
			Map<Integer, Integer> itemInfoMap = new HashMap<>();
			for (String x : item.split(";")) {
				if (StringUtils.isEmpty(x)) {
					continue;
				}
				String[] x1 = x.split(":");
				itemInfoMap.put(Integer.parseInt(x1[0]), Integer.parseInt(x1[1]));
			}
			return itemInfoMap;
		} catch (Exception e) {
			return null;
		}
	}

	public void cleanBatchApply() {
		batchApplyRepository.deleteAll();
	}

	public synchronized void sendBatchApply() {
		batchApplyRepository.findAll().forEach(batch -> {
			if (batch.getState() == 1 || batch.getState() == 2) {
				try {} catch (Exception e) {
					batch.setState(1);
				}
				batchApplyRepository.save(batch);
			}
		});
	}
}