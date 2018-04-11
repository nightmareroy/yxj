package com.wanniu.game.common;

/**
 * 一种非正常入侵异常.
 * <p>
 * 主要用于主动发现一些玩家恶意修改参数的行为.
 * 
 * @author 小流氓(zhoumingkai@qeng.cn)
 */
public class HackerException extends RuntimeException {

	private static final long serialVersionUID = -1348741325818957718L;

	public HackerException() {}

	public HackerException(String message) {
		super(message);
	}
}