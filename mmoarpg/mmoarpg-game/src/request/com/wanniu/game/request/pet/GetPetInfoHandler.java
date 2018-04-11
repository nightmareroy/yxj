package com.wanniu.game.request.pet;

import com.wanniu.core.game.entity.GClientEvent;
import com.wanniu.core.game.protocol.PomeloRequest;
import com.wanniu.core.game.protocol.PomeloResponse;

/**
 * 获取宠物详细信息 
 * @author c
 *
 */

@GClientEvent("area.petHandler.getPetInfoRequest")
public class GetPetInfoHandler extends PomeloRequest {

	@Override
	public PomeloResponse request() throws Exception {


		return null;
	}

}
