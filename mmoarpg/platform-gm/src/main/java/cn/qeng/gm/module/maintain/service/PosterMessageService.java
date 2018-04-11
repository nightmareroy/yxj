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
package cn.qeng.gm.module.maintain.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cn.qeng.gm.module.maintain.domain.Poster;
import cn.qeng.gm.module.maintain.domain.PosterMessage;
import cn.qeng.gm.module.maintain.domain.PosterMessgeRepository;

/**
 * 紧急通知人业务处理
 * 
 * @since 2.0
 * @author 小流氓(mingkai.zhou@qeng.net)
 */
@Service
public class PosterMessageService {
	@Autowired
	private PosterMessgeRepository posterMessgeRepository;

	/**
	 * 获取紧急联系人相关信息
	 */
	public List<PosterMessage> getPosterMessage() {
		List<Poster> posterMessage = posterMessgeRepository.findAll();
		List<PosterMessage> result = new ArrayList<>();
		for (Poster posters : posterMessage) {
			String poster = posters.getPoster();
			String[] p = poster.split("\n");
			for (int i = 0; i < p.length; i++) {
				String onePoster = p[i];
				String[] n = onePoster.split(",");
				PosterMessage message = new PosterMessage();
				message.setName(n[0]);
				message.setState(Integer.valueOf(n[1]));
				message.setPoster(n[2]);
				message.setPhoneNumber(n[3]);
				result.add(message);
			}
		}
		return result;
	}

	/**
	 * 所有紧急联系人信息
	 */
	public Poster getPoster() {
		List<Poster> posters = posterMessgeRepository.findAll();
		Poster rusult = new Poster();
		for (Poster poster : posters) {
			rusult.setTitle(poster.getTitle());
			rusult.setContent(poster.getContent());
		}
		return rusult;
	}

	public void updateInfo(String head, String title, String name, String status, String poster, String phone) {
		String[] nameArr = name.split(",");
		String[] statusArr = status.split(",");
		String[] posterArr = poster.split(",");
		String[] phoneArr = phone.split(",");
		if (nameArr == null || nameArr.length == 0) {
			return;// 参数校验不合法
		}
		if (nameArr.length != statusArr.length || nameArr.length != posterArr.length) {
			return;// 参数校验不合法
		}
		// 如果出现手机为0的情况则
		if (phoneArr == null || phoneArr.length == 0 || phoneArr.length < nameArr.length) {
			if (phoneArr == null)
				phoneArr = new String[nameArr.length];
			if (phoneArr.length < nameArr.length) {
				phoneArr = Arrays.copyOf(phoneArr, nameArr.length);
			}
			for (int i = 0; i < nameArr.length; i++) {
				if (phoneArr[i] == null || phoneArr[i].isEmpty()) {
					phoneArr[i] = "-";
				}
			}
		}
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < nameArr.length; i++) {
			sb.append(nameArr[i]).append(",").append(statusArr[i]).append(",").append(posterArr[i]).append(",").append(phoneArr[i]).append("\n");
		}
		List<Poster> list = posterMessgeRepository.findAll();
		if (list == null || list.isEmpty()) {
			Poster p = new Poster();
			p.setId(1);
			p.setTitle(head);
			p.setContent(title);
			p.setPoster(sb.toString());
			posterMessgeRepository.saveAndFlush(p);
		} else {
			Poster p = list.get(0);
			p.setTitle(head);
			p.setContent(title);
			p.setPoster(sb.toString());
			posterMessgeRepository.save(p);
		}
	}
}
