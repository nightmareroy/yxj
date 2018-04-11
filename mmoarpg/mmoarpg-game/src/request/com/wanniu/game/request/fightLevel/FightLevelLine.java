package com.wanniu.game.request.fightLevel;

import com.wanniu.core.game.protocol.PomeloRequest;
import com.wanniu.core.game.protocol.PomeloResponse;
import com.wanniu.game.player.WNPlayer;

public abstract class FightLevelLine extends PomeloRequest {

	public PomeloResponse request() throws Exception {
		WNPlayer player = (WNPlayer) pak.getPlayer();
		return request(player);
	}

	public abstract PomeloResponse request(WNPlayer player) throws Exception;

	public short getType() {
		return 0x401;
	}

}
