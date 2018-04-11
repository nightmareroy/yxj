package com.wanniu.game.util;

import java.util.LinkedList;
import java.util.List;

/**
 * DFA敏感词库树上的节点.
 *
 * @author 小流氓(zhoumingkai@qeng.cn)
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