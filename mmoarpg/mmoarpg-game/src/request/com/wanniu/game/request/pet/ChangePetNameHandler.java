package com.wanniu.game.request.pet;

import com.wanniu.core.game.entity.GClientEvent;
import com.wanniu.core.game.protocol.PomeloRequest;
import com.wanniu.core.game.protocol.PomeloResponse;

/**
 * 宠物改名
 * 
 * @author c
 *
 */

@GClientEvent("area.petHandler.changePetNameRequest")
public class ChangePetNameHandler extends PomeloRequest {

	@Override
	public PomeloResponse request() throws Exception {

		return null;
	}
}
