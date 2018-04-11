package com.wanniu.game.request.pet;

import com.wanniu.core.game.entity.GClientEvent;
import com.wanniu.core.game.protocol.PomeloRequest;
import com.wanniu.core.game.protocol.PomeloResponse;

/**
 * 宠物放生
 * 
 * @author c
 *
 */

@GClientEvent("area.petHandler.freePetRequest")
public class FreePetHandler extends PomeloRequest {

	@Override
	public PomeloResponse request() throws Exception {


		return null;
	}

}
