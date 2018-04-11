package com.wanniu.game.item.po;

import java.io.Serializable;

/**
 * 物品 总数量,绑定数量，未绑定数量
 *
 */
public class DetailItemNum implements Serializable {

	private static final long serialVersionUID = 1L;

	public int totalNum;

	public int bindNum;

	public int unBindNum;
}
