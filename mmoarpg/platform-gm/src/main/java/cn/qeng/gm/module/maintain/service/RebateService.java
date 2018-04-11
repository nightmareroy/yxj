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

import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import cn.qeng.common.gm.RedisKeyConst;
import cn.qeng.gm.core.ErrorCode;
import cn.qeng.gm.core.RedisManager;
import cn.qeng.gm.core.session.SessionUser;
import cn.qeng.gm.util.ExcelUtils;

/**
 * 返利业务逻辑处理类.
 *
 * @author 周明凯(zhoumingkai@qeng.cn)
 */
@Service
public class RebateService {
	private final static Logger logger = LogManager.getLogger(RebateService.class);
	@Autowired
	private RedisManager redisManager;

	/**
	 * 处理上传的文件...
	 */
	public int handleUploadFile(SessionUser user, CommonsMultipartFile file) throws Exception {
		if (file == null) {
			return ErrorCode.ERROR;
		}
		Map<String, String> rebateInfo = new HashMap<>();
		ExcelUtils.readData(file.getInputStream(), file.getFileItem().getName(), 1).stream().filter(array -> !"".equals(array[0])).forEach(row -> rebateInfo.put(row[0], row[1]));
		logger.info("上传返利数据 user={},data={}", user.getUsername(), rebateInfo);

		// 存入全局库里.
		redisManager.getGlobalRedis().hmset(RedisKeyConst.REDIS_KEY_REBATE, rebateInfo);
		return ErrorCode.OK;
	}

	/**
	 * 获取当前还未领取的返利.
	 */
	public Map<String, String> getAllRebate() {
		return redisManager.getGlobalRedis().hgetAll(RedisKeyConst.REDIS_KEY_REBATE);
	}

	public int clean(SessionUser user) {
		logger.info("清空返利数据 user={},data={}", user.getUsername(), getAllRebate());
		redisManager.getGlobalRedis().del(RedisKeyConst.REDIS_KEY_REBATE);
		return ErrorCode.OK;
	}
}