package com.wanniu.game.request.pet;

import com.wanniu.core.game.entity.GClientEvent;
import com.wanniu.core.game.protocol.PomeloRequest;
import com.wanniu.core.game.protocol.PomeloResponse;

/**
 * 洗练升级 
 * @author c
 *
 */
@GClientEvent("area.petHandler.upGradeRandPropertyRequest")
public class UpGradeRandPropertyHandler extends PomeloRequest {

	@Override
	public PomeloResponse request() throws Exception {

		return null;
	}
}