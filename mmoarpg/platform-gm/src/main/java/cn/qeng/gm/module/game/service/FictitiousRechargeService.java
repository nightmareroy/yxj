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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import cn.qeng.gm.api.RechargeAPI;
import cn.qeng.gm.api.rpc.RpcResponse;
import cn.qeng.gm.core.PageConstant;
import cn.qeng.gm.core.session.SessionUser;
import cn.qeng.gm.module.game.WelfareConstant;
import cn.qeng.gm.module.game.domain.FictitiousRecharge;
import cn.qeng.gm.module.game.domain.FictitiousRechargeRepository;

/**
 * 模拟充值业务处理类
 *
 * @author 小流氓(mingkai.zhou@qeng.net)
 */
@Service
public class FictitiousRechargeService {
	private final static Logger logger = LogManager.getLogger(FictitiousRechargeService.class);
	@Autowired
	private FictitiousRechargeRepository rechargeLogRepository;

	/**
	 * 获取所有的模拟充值记录
	 */
	public Page<FictitiousRecharge> listFictitiousRecharge(int page) {
		return rechargeLogRepository.findAll(new PageRequest(page, PageConstant.MAX_SIZE, new Sort(Sort.Direction.DESC, "createTime", "id")));
	}

	public int sendRecharge(SessionUser user, int serverId, int type, String player, int productId, String reson) {
		logger.info("充值补单 serverId={},player={},productId={}", serverId, player, productId);
		FictitiousRecharge recharge = new FictitiousRecharge();
		recharge.setServerId(serverId);
		recharge.setType(type);
		recharge.setPlayer(player);
		recharge.setCreateTime(new Date());
		recharge.setProductId(productId);
		recharge.setRechargeReson(reson);
		recharge.setUsername(user.getUsername());
		recharge.setName(user.getName());
		recharge.setSingal(WelfareConstant.PENDING_APPROVAL);
		rechargeLogRepository.save(recharge);
		return recharge.getSingal();
	}

	public void agree(SessionUser user, int id) {
		logger.info("同意审批  user={},id={}", user.getUsername(), id);
		FictitiousRecharge recharge = rechargeLogRepository.findOne(id);
		if (recharge.getSingal() == WelfareConstant.PENDING_APPROVAL) {
			recharge.setAuditName(user.getName());
			recharge.setAuditUsername(user.getUsername());
			recharge.setSingal(WelfareConstant.ALREADY_APPROVE);
			rechargeLogRepository.save(recharge);

			sendRecharge(recharge);
		}
	}

	private void sendRecharge(FictitiousRecharge recharge) {
		RpcResponse response = new RechargeAPI(recharge.getType(), recharge.getPlayer(), recharge.getProductId(), 0).request(recharge.getServerId());
		// 充值成功
		int result = response.getStatus().getOrDefault(recharge.getServerId(), (byte) 0);
		if (result == 1) {
			// recharge.setSingal(WelfareConstant.ALREADY_APPROVE);
			// rechargeLogRepository.save(recharge);
		}
		// 充值失败
		else {
			recharge.setSingal(WelfareConstant.MAIL_SEND_FAIL);
			recharge.setResult(result);
			rechargeLogRepository.save(recharge);
		}
	}

	public void refuse(SessionUser user, int id) {
		logger.info("拒绝审批  user={},id={}", user.getUsername(), id);
		FictitiousRecharge recharge = rechargeLogRepository.findOne(id);
		if (recharge.getSingal() == WelfareConstant.PENDING_APPROVAL) {
			recharge.setAuditName(user.getName());
			recharge.setAuditUsername(user.getUsername());
			recharge.setSingal(WelfareConstant.HAS_REFUSED);
			rechargeLogRepository.save(recharge);
		}
	}
}