package com.wanniu.game.request.role;

import java.io.IOException;

import com.wanniu.core.game.LangService;
import com.wanniu.core.game.entity.GClientEvent;
import com.wanniu.core.game.protocol.PomeloRequest;
import com.wanniu.core.game.protocol.PomeloResponse;
import com.wanniu.core.logfs.Out;
import com.wanniu.core.util.StringUtil;
import com.wanniu.game.common.Const;
import com.wanniu.game.common.msg.ErrorResponse;
import com.wanniu.game.player.PlayerDao;
import com.wanniu.game.player.PlayerService;
import com.wanniu.game.player.WNPlayer;
import com.wanniu.game.util.BlackWordUtil;
import com.wanniu.redis.GameDao;

import pomelo.connector.RoleHandler.ChangePlayerNameRequest;
import pomelo.connector.RoleHandler.ChangePlayerNameResponse;

/**
 * 修改玩家名字
 * 
 * @author Feil
 */
@GClientEvent("connector.roleHandler.changePlayerNameRequest")
public class ChangePlayerNameHandler extends PomeloRequest {
	public PomeloResponse request() throws Exception {
		ChangePlayerNameRequest req = ChangePlayerNameRequest.parseFrom(pak.getRemaingBytes());
		String uid = pak.getUid();
		WNPlayer player = (WNPlayer) pak.getPlayer();
		final String name = req.getC2SName();
		if (StringUtil.isEmpty(uid)) {
			return new ErrorResponse(LangService.getValue("PLAYER_UID_NULL"));
		}
		// 判定名字是否合法
		if (StringUtil.isEmpty(name)) {
			return new ErrorResponse(LangService.getValue("PLAYER_NAME_NULL"));
		}

		if (name.length() > 6) {
			return new ErrorResponse(LangService.getValue("PLAYER_NAME_LONG"));
		}

		// 屏蔽字
		if (BlackWordUtil.isIncludeBlackString(name)) {
			return new ErrorResponse(LangService.getValue("PLAYER_ID_SENSITIVE"));
		}
		// 特殊字符
		if (BlackWordUtil.isIncludeSpecialChar(name)) {
			return new ErrorResponse(LangService.getValue("PLAYER_ID_CHARACTER"));
		}
		if (player != null) {
			int hasCount = player.bag.findItemNumByCode(Const.ITEM_CODE.Changename.value);
			// 判断个数
			if (hasCount <= 0) {
				return new ErrorResponse(LangService.getValue("NOT_ENOUGH_ITEM"));
			}
		}

		boolean isPutSuccess = PlayerDao.putName(name, player.getId());
		if (!isPutSuccess) {
			Out.warn("发现有玩家重名,改名失败,要改的名字为:", name, "playerId=", player.getId());
			return new ErrorResponse(LangService.getValue("PLAYER_NAME_HAS_USED"));
		}

		// 建议本服名称与角色ID对应的关系，也叫本服玩家列表吧...
		if (!GameDao.putName(name, player.getId())) {
			Out.warn("建立本服角色列表时异常啦!", name);
		}

		String oldName = player.getName();
		PlayerDao.freeName(oldName);
		GameDao.freeName(oldName);

		player.player.name = name;
		player.bag.discardItem(Const.ITEM_CODE.Changename.value, 1, Const.GOODS_CHANGE_TYPE.use, null, false, false);
		PlayerService.getInstance().afterPlayerChangeName(player);

		return new PomeloResponse() {
			@Override
			protected void write() throws IOException {
				ChangePlayerNameResponse.Builder res = ChangePlayerNameResponse.newBuilder();
				res.setS2CCode(OK);
				res.setS2CName(name);
				body.writeBytes(res.build().toByteArray());
			}
		};
	}
}