package com.wanniu.game.player.po;

/**
 * 对应的protobuf 的 Avatar对象, 由于JSON.toString方法传入 protobuf的avatar对象会 报错
 * 
 * @author Yangzz
 *
 */
public class AvatarObj {

	public int tag;
	public String fileName;
	public int effectType;

	public AvatarObj() {

	}

	public AvatarObj(int tag, String fileName, int effectType) {
		this.tag = tag;
		this.fileName = fileName;
		this.effectType = effectType;
	}
}
