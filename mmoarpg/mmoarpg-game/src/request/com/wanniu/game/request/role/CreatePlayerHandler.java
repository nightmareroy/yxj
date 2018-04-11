package com.wanniu.game.request.role;

import java.io.IOException;
import java.util.List;

import com.wanniu.core.GGlobal;
import com.wanniu.core.game.LangService;
import com.wanniu.core.game.entity.GClientEvent;
import com.wanniu.core.game.protocol.PomeloRequest;
import com.wanniu.core.game.protocol.PomeloResponse;
import com.wanniu.core.logfs.Out;
import com.wanniu.core.util.StringUtil;
import com.wanniu.game.GWorld;
import com.wanniu.game.area.PlayerRemote;
import com.wanniu.game.common.Const;
import com.wanniu.game.common.msg.ErrorResponse;
import com.wanniu.game.data.GameData;
import com.wanniu.game.data.ext.CharacterExt;
import com.wanniu.game.player.BILogService;
import com.wanniu.game.player.PlayerUtil;
import com.wanniu.game.player.RebateService;
import com.wanniu.game.player.WNPlayer;
import com.wanniu.game.player.bi.LogReportService;
import com.wanniu.game.util.BlackWordUtil;

import cn.qeng.common.login.TokenInfo;
import pomelo.connector.RoleHandler.CreatePlayerRequest;
import pomelo.connector.RoleHandler.CreatePlayerResponse;

/**
 * 创建角色
 * 
 * @author agui
 */
@GClientEvent("connector.roleHandler.createPlayerRequest")
public class CreatePlayerHandler extends PomeloRequest {

	public PomeloResponse request() throws Exception {
		CreatePlayerRequest req = CreatePlayerRequest.parseFrom(pak.getRemaingBytes());
		String uid = pak.getUid();
		final int pro = req.getC2SPro();
		if (pro == Const.PlayerPro.YU_JIAN.value || pro == Const.PlayerPro.SHEN_JIAN.value) {
			return new ErrorResponse("该职业暂未开放！");
		}
		final String name = req.getC2SName();
		if (StringUtil.isEmpty(uid)) {
			return new ErrorResponse(LangService.getValue("PLAYER_UID_NULL"));
		}
		// 判定名字是否合法
		if (StringUtil.isEmpty(name)) {
			return new ErrorResponse(LangService.getValue("PLAYER_NAME_NULL"));
		}

		if (name.length() > GWorld.__SERVER_LANG.getNameLimit()) {
			return new ErrorResponse(LangService.getValue("PLAYER_NAME_LONG"));
		}

		if (pro == 0) {
			return new ErrorResponse(LangService.getValue("PLAYER_PRO_ILLEGALITY"));
		}

		Integer count = pak.getAttr(GGlobal.__KEY_ROLE_COUNT);
		if (count != null && count >= Const.PLAYER.maxNum) {
			return new ErrorResponse(LangService.getValue("ROLE_NUM_UPPER_LIMIT"));
		}

		// 屏蔽字
		if (BlackWordUtil.isIncludeBlackString(name)) {
			return new ErrorResponse(LangService.getValue("PLAYER_ID_SENSITIVE"));
		}
		// 特殊字符
		if (BlackWordUtil.isIncludeSpecialChar(name)) {
			return new ErrorResponse(LangService.getValue("PLAYER_ID_CHARACTER"));
		}

		List<CharacterExt> charData = GameData.findCharacters(t -> t.pro == pro);
		if (charData.size() == 0) {
			return new ErrorResponse(LangService.getValue("PLAYER_PRO_ILLEGALITY"));
		}

		// 创建角色
		WNPlayer player = PlayerRemote.createPlayer(uid, name, pro, pak.getAttr(GGlobal.__KEY_LOGIC_SERVERID));
		if (player == null) {
			return new ErrorResponse(LangService.getValue("PLAYER_NAME_HAS_USED"));
		}

		// 创建角色也要修正此类参数...
		TokenInfo token = pak.getAttr(GGlobal.__KEY_TOKEN_INFO);
		if (token != null) {
			player.getPlayer().channel = token.getChannel();
			player.getPlayer().subchannel = token.getSubchannel();
			player.getPlayer().subchannelUID = token.getSubchannelUid();
			player.getPlayer().mac = token.getMac();
			player.getPlayer().os = token.getOs();
			player.getPlayer().ip = pak.getIp();
		}

		Out.info("创建角色 playerId=", player.getId(), ",name=", player.getName(), ",uid=", uid);
		BILogService.getInstance().ansycReportPlayerData(pak.getSession(), player.getPlayer(), true);
		RebateService.getInstance().ansycCheckRebate(player);
		LogReportService.getInstance().ansycReportCreatePlayer(player);

		pak.setAttr(GGlobal.__KEY_ROLE_COUNT, count + 1);
		return new PomeloResponse() {
			@Override
			protected void write() throws IOException {
				CreatePlayerResponse.Builder res = CreatePlayerResponse.newBuilder();

				res.setS2CPlayer(PlayerUtil.transToJson4Basic(player));

				res.setS2CCode(OK);

				// res.addAllS2CPlayers(PlayerRemote.getPlayersByUidAndLogicServerId(uid,
				// pak.getAttr(GGlobal._KEY_LOGICSERVERID)));

				body.writeBytes(res.build().toByteArray());

			}
		};
	}

}
