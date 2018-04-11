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
package cn.qeng.gm.module.game.service;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSON;
import com.wanniu.util.StringUtil;

import cn.qeng.common.gm.RedisKeyConst;
import cn.qeng.common.gm.po.CdkCode;
import cn.qeng.common.gm.po.CdkItem;
import cn.qeng.common.gm.po.CdkPO;
import cn.qeng.gm.core.PageConstant;
import cn.qeng.gm.core.RedisManager;
import cn.qeng.gm.module.game.domain.Cdkey;
import cn.qeng.gm.module.game.domain.CdkeyRepository;
import cn.qeng.gm.module.game.domain.ItemInfo;
import cn.qeng.gm.util.DateUtils;

/**
 * 
 *
 * @author 小流氓(mingkai.zhou@qeng.net)
 */
@Service
@Transactional
public class CdkeyService {
	@Autowired
	private CdkeyRepository cdkeyRepository;
	@Autowired
	private RedisManager redisManager;

	public Page<Cdkey> getCdkeyPage(int page) {
		return cdkeyRepository.findAll(new PageRequest(page, PageConstant.MAX_SIZE, new Sort(Sort.Direction.DESC, "createTime", "id")));
	}

	public Cdkey getCdkeyById(int id) {
		return cdkeyRepository.findOne(id);
	}

	// 生成一批CDK...
	public void add(String name, String datepicker, int type, int minLevel, int useMax, int codeNum, String[] itemIdList, String[] itemNameList, int[] itemNumList) throws ParseException {
		Cdkey cdkey = new Cdkey();
		cdkey.setName(name);

		String[] times = datepicker.split(" - ");
		cdkey.setStartTime(DateUtils.parseyyyyMMddHHmmss(times[0]));
		cdkey.setEndTime(DateUtils.parseyyyyMMddHHmmss(times[1]));

		cdkey.setType(type);
		cdkey.setMinLevel(minLevel);
		cdkey.setUseMax(useMax);
		cdkey.setCodeNum(codeNum);

		List<ItemInfo> itemList = new ArrayList<>();
		for (int i = 0; i < itemIdList.length; i++) {
			ItemInfo item = new ItemInfo();
			item.setItemId(itemIdList[i]);
			item.setItemName(itemNameList[i]);
			item.setItemNumber(itemNumList[i]);
			itemList.add(item);
		}
		cdkey.setItemList(JSON.toJSONString(itemList));

		cdkey.setCreateTime(new Date());
		cdkey.setModifyTime(cdkey.getCreateTime());

		cdkey.setRemaining(cdkey.getCodeNum());
		cdkey.setSyncTime(cdkey.getCreateTime());

		cdkeyRepository.save(cdkey);

		synchronized (this) {
			if (!redisManager.getGlobalRedis().hexists(RedisKeyConst.REDIS_KEY_CDK, String.valueOf(cdkey.getId()))) {
				gen(cdkey);
			}
		}
	}

	private void gen(cn.qeng.gm.module.game.domain.Cdkey cdkey) {
		String code = String.valueOf(cdkey.getId());
		int num = cdkey.getCodeNum();
		int channel = -1;// 专用渠道，此版本放弃
		Date beginDate = cdkey.getStartTime();
		Date endDate = cdkey.getEndTime();
		String name = cdkey.getName();
		int maxUseCount = cdkey.getUseMax();
		int minLevel = cdkey.getMinLevel();
		String serverIds = "";// 专用服务器ID,此版本放弃
		int cdkType = cdkey.getType();// 0-普通1-单个通用
		List<ItemInfo> itemList = JSON.parseArray(cdkey.getItemList(), ItemInfo.class);
		// 通用类型的只能用一次和生成一个
		if (cdkType == 1) {
			num = 1;
			maxUseCount = 1;
		}

		List<CdkItem> items = new ArrayList<>();
		for (ItemInfo item : itemList) {
			items.add(new CdkItem(item.getItemId(), item.getItemNumber()));
		}

		int length = 12;
		List<CdkCode> codes = CdkGenerator.generateDistinctCode(code, num, length - code.length(), length - code.length());

		CdkPO po = new CdkPO(code, name, num, beginDate, endDate, channel, maxUseCount, items, codes, minLevel, cdkType);
		if (StringUtil.isNotEmpty(serverIds)) {
			for (String sid : serverIds.split(";")) {
				po.getServerIds().add(Integer.parseInt(sid));
			}
		}
		redisManager.getGlobalRedis().hsetnx(RedisKeyConst.REDIS_KEY_CDK, code, JSON.toJSONString(po));
	}

	public CdkPO getExportCdk(int id) {
		String result = redisManager.getGlobalRedis().hget(RedisKeyConst.REDIS_KEY_CDK, String.valueOf(id));
		return JSON.parseObject(result, CdkPO.class);
	}
}