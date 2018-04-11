package com.wanniu.game.request.pet;

import com.wanniu.core.game.entity.GClientEvent;
import com.wanniu.core.game.protocol.PomeloRequest;
import com.wanniu.core.game.protocol.PomeloResponse;

/**
 * 切换宠物战斗模式
 * 
 * @author c
 */

@GClientEvent("area.petHandler.changePetPkModelRequest")
public class ChangePetPkModelHandler extends PomeloRequest {

	@Override
	public PomeloResponse request() throws Exception {


		return null;
	}
}
