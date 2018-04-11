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
package cn.qeng.gm.core.dfa;

import java.util.LinkedList;
import java.util.List;

/**
 * DFA敏感词库树上的节点.
 *
 * @since 2.5
 * @author 小流氓(176543888@qq.com)
 */
class DFANode {
	private int value; // 节点名称
	private List<DFANode> subNodes; // 子节点
	private boolean isLast;// 默认false

	public DFANode(int value, boolean isLast) {
		this.value = value;
		this.isLast = isLast;
	}

	/**
	 * @param subNode
	 * @return 就是传入的subNode
	 */
	private DFANode addSubNode(final DFANode subNode) {
		if (subNodes == null)
			subNodes = new LinkedList<DFANode>();
		subNodes.add(subNode);
		return subNode;
	}

	/**
	 * 有就直接返回该子节点， 没有就创建添加并返回该子节点
	 */
	public DFANode addIfNoExist(final int value, final boolean isLast) {
		if (subNodes == null) {
			return addSubNode(new DFANode(value, isLast));
		}
		for (DFANode subNode : subNodes) {
			if (subNode.value == value) {
				if (!subNode.isLast && isLast)
					subNode.isLast = true;
				return subNode;
			}
		}
		return addSubNode(new DFANode(value, isLast));
	}

	public DFANode querySub(final int value) {
		if (subNodes == null) {
			return null;
		}
		for (DFANode subNode : subNodes) {
			if (subNode.value == value)
				return subNode;
		}
		return null;
	}

	public boolean isLast() {
		return isLast;
	}

	public void setLast(boolean isLast) {
		this.isLast = isLast;
	}

	@Override
	public int hashCode() {
		return value;
	}
}