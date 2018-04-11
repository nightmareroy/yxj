package com.wanniu.game.request.pet;

import com.wanniu.core.game.entity.GClientEvent;
import com.wanniu.core.game.protocol.PomeloRequest;
import com.wanniu.core.game.protocol.PomeloResponse;

/**
 * 获取随机宠物名字
 * @author c
 *
 */
@GClientEvent("area.petHandler.randPetNameRequest")
public class RandPetNameHandler extends PomeloRequest {

	@Override
	public PomeloResponse request() throws Exception {


		return null;
	}
}
