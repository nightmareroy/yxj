package com.wanniu.game.request.area;

import java.io.IOException;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.wanniu.core.game.JobFactory;
import com.wanniu.core.game.entity.GClientEvent;
import com.wanniu.core.game.protocol.PomeloRequest;
import com.wanniu.core.game.protocol.PomeloResponse;
import com.wanniu.core.logfs.Out;
import com.wanniu.game.area.Area;
import com.wanniu.game.arena.ArenaService;
import com.wanniu.game.common.Utils;
import com.wanniu.game.common.msg.ErrorResponse;
import com.wanniu.game.player.WNPlayer;
import com.wanniu.game.team.TeamData.TeamMemberData;

import pomelo.area.PlayerHandler.EnterSceneRequest;
import pomelo.area.PlayerHandler.EnterSceneResponse;

/**
 * 加载100%
 */
@GClientEvent("area.playerHandler.enterSceneRequest")
public class EnterSceneHandler extends PomeloRequest {

	public PomeloResponse request() throws Exception {
		EnterSceneRequest rquest = EnterSceneRequest.parseFrom(pak.getRemaingBytes());
		final String instanceId = rquest.getC2SInstanceId();

		WNPlayer player = (WNPlayer) pak.getPlayer();

		Area area = player.getArea();

		// 当客户端所要进入的场景不为空此不等于当前场景，忽略掉本次请求
		if (StringUtils.isNotEmpty(instanceId) && !area.instanceId.equals(instanceId)) {
			if (StringUtils.isNotEmpty(instanceId)) {
				Out.warn("连续切图吗？playerId=", player.getId(), ",name=", player.getName(), ",area.instanceId=", area.instanceId, ",C2SInstanceId=", instanceId);
			}
			return new ErrorResponse("");
		}

		area.playerEnterRequest(player);

		area.onPlayerEntered(player);// FIXME onEndEnterScene内有调用，为什么还要主动调用一次

		player.onEndEnterScene();

		// 我是队长时,主动调用机器跟随功能
//		Map<String, TeamMemberData> members = player.getTeamManager().getTeamMembers();
//		if (members != null && player.getTeamManager().isTeamLeader() && area.areaId != ArenaService.ARENA_MAP_ID// 竞技场内独立参战不需要拉队员进去
//		) {
//			for (TeamMemberData member : members.values()) {
//				if (member.getPlayer().isRobot() && !area.isNormal()) {
//					JobFactory.addDelayJob(() -> {
//						member.getPlayer().onEndEnterScene();
//					}, Utils.getSecMills(3, 4));
//				}
//			}
//		}

		return new PomeloResponse() {
			@Override
			protected void write() throws IOException {
				EnterSceneResponse.Builder res = EnterSceneResponse.newBuilder();
				res.setS2CCode(OK);
				res.setS2CInstanceId(player.getInstanceId());
				res.setS2CAreaIndex(player.getLineIndex());
				body.writeBytes(res.build().toByteArray());
			}
		};
	}

	public short getType() {
		return 0x502;
	}
}