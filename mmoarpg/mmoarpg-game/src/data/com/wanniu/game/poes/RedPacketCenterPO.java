package com.wanniu.game.poes;

import java.util.HashMap;
import java.util.Map;

import com.wanniu.core.game.entity.GEntity;
import com.wanniu.game.DBTable;
import com.wanniu.game.common.Table;
import com.wanniu.game.redpacket.RedPacket;



@DBTable(Table.player_red_packet)
public class RedPacketCenterPO extends GEntity {

	public Map<String, RedPacket> redPackets = new HashMap<>();

}
