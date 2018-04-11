package com.wanniu.game.poes;

import java.util.Date;

import com.wanniu.core.game.entity.GEntity;
import com.wanniu.game.DBField;

public class FeeOrderPO extends GEntity {

	@DBField(isPKey=true,fieldType="varchar",size=64)
	public String orderId;
	public int productId;
	public String playerId;
	public Date createtime;
	public boolean isCard;
	public boolean isSuperPackage;
}