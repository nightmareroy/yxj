package com.wanniu.game.request.fightLevel;

import java.io.IOException;
import java.util.List;

import com.wanniu.core.game.entity.GClientEvent;
import com.wanniu.core.game.protocol.PomeloResponse;
import com.wanniu.game.player.WNPlayer;

import pomelo.area.FightLevelHandler.GetMonsterLeaderRequest;
import pomelo.area.FightLevelHandler.GetMonsterLeaderResponse;
import pomelo.item.ItemOuterClass.MiniItem;

/**
 * 首领详情
 * 
 * @author Yangzz
 *
 */
@GClientEvent("area.fightLevelHandler.getMonsterLeaderRequest")
public class GetMonsterLeaderHandler extends FightLevelLine {

	public PomeloResponse request(WNPlayer player) throws Exception {

		GetMonsterLeaderRequest req = GetMonsterLeaderRequest.parseFrom(pak.getRemaingBytes());
		int monsterId = req.getS2CMonsterId();
		int areaId = req.getS2CAreaId();

		return new PomeloResponse() {
			@Override
			protected void write() throws IOException {
				GetMonsterLeaderResponse.Builder res = GetMonsterLeaderResponse.newBuilder();

				GetMonsterLeaderData data = player.fightLevelManager.getMonsterLeader(player, monsterId, areaId);
				res.setS2CCode(OK);
				res.addAllS2CDropItems(data.dropItems);
				res.setS2CReqLevel(data.reqLevel);
				res.setS2CReqUpLevel(data.reqUpLevel);
				res.setS2CRefreshPoint(data.refreshPoint);
				body.writeBytes(res.build().toByteArray());

			}
		};
	}

	public static class GetMonsterLeaderData {
		public List<MiniItem> dropItems;
		public int reqLevel;
		public int reqUpLevel;
		public String refreshPoint;

		public GetMonsterLeaderData(List<MiniItem> dropItems, int reqLevel, int reqUpLevel, String refreshPoint) {
			this.dropItems = dropItems;
			this.reqLevel = reqLevel;
			this.reqUpLevel = reqUpLevel;
			this.refreshPoint = refreshPoint;
		}
	}
}