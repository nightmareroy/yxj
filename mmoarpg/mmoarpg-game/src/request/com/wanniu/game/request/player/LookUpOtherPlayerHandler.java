package com.wanniu.game.request.player;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.TypeReference;
import com.wanniu.core.game.LangService;
import com.wanniu.core.game.entity.GClientEvent;
import com.wanniu.core.game.protocol.PomeloRequest;
import com.wanniu.core.game.protocol.PomeloResponse;
import com.wanniu.core.util.StringUtil;
import com.wanniu.game.common.Const.PlayerBtlData;
import com.wanniu.game.common.ConstsTR;
import com.wanniu.game.equip.EquipUtil;
import com.wanniu.game.guild.GuildUtil;
import com.wanniu.game.player.PlayerUtil;
import com.wanniu.game.player.WNPlayer;
import com.wanniu.game.poes.GuildMemberPO;
import com.wanniu.game.poes.GuildPO;
import com.wanniu.game.poes.PlayerBasePO;
import com.wanniu.game.poes.PlayerBasePO.EquipStrengthPos;
import com.wanniu.game.poes.PlayerPO;
import com.wanniu.game.poes.TitlePO;
import com.wanniu.redis.GameDao;
import com.wanniu.redis.PlayerPOManager;

import pomelo.Common.AttributeSimple;
import pomelo.Common.Avatar;
import pomelo.area.PlayerHandler.LookUpOtherPlayerRequest;
import pomelo.area.PlayerHandler.LookUpOtherPlayerResponse;
import pomelo.item.ItemOuterClass.EquipGridStrengthInfo;
import pomelo.item.ItemOuterClass.ItemDetail;
import pomelo.player.PlayerOuterClass.LookUpPlayer;

@GClientEvent("area.playerHandler.lookUpOtherPlayerRequest")
public class LookUpOtherPlayerHandler extends PomeloRequest {

	@Override
	public PomeloResponse request() throws Exception {
		WNPlayer player = (WNPlayer) pak.getPlayer();
		return new PomeloResponse() {
			protected void write() throws IOException {
				LookUpOtherPlayerRequest req = LookUpOtherPlayerRequest.parseFrom(pak.getRemaingBytes());
				String other_playerId = req.getC2SPlayerId();

				LookUpOtherPlayerResponse.Builder res = LookUpOtherPlayerResponse.newBuilder();
				if (StringUtil.isEmpty(other_playerId)) {
					res.setS2CCode(FAIL);
					res.setS2CMsg("");
					body.writeBytes(res.build().toByteArray());
					return;
				}

				if (player.getId().equals(other_playerId)) {
					res.setS2CCode(FAIL);
					res.setS2CMsg(LangService.getValue("PLEASE_OPEN_PLAYER_INTERFACE"));
					body.writeBytes(res.build().toByteArray());
					return;
				}

				WNPlayer target = PlayerUtil.getOnlinePlayer(other_playerId);
				if (target != null && target.isProxy()) {
					res.setS2CCode(FAIL);
					res.setS2CMsg(LangService.getValue("CANNOT_LOOKUP"));
					body.writeBytes(res.build().toByteArray());
					return;
				}
				PlayerPO otherPlayer = null;
				int titleId = 0;
				List<ItemDetail> equipments = new ArrayList<>();
				List<AttributeSimple> attr_list = new ArrayList<>();
				List<EquipGridStrengthInfo> strenght_list = new ArrayList<>();
				if (target == null) {
					otherPlayer = PlayerPOManager.findPO(ConstsTR.playerTR, other_playerId, PlayerPO.class);
					if (otherPlayer == null) {
						res.setS2CCode(FAIL);
						res.setS2CMsg(LangService.getValue("CANNOT_LOOKUP"));
						body.writeBytes(res.build().toByteArray());
						return;
					}
					TitlePO rankpo = PlayerPOManager.findPO(ConstsTR.playerTitleTR, other_playerId, TitlePO.class);
					if (rankpo != null)
						titleId = rankpo.selectedRankId;
					PlayerBasePO playerBasePO = PlayerPOManager.findPO(ConstsTR.playerBaseTR, otherPlayer.id, PlayerBasePO.class);
					equipments = EquipUtil.getAllEquipDetails4PayLoad(playerBasePO, playerBasePO.equipGrids);
					Map<PlayerBtlData, Integer> attr_map = new HashMap<>();
					attr_map = GameDao.get(otherPlayer.id, ConstsTR.playerBtlData.value, new TypeReference<Map<PlayerBtlData, Integer>>() {
					});
					if (attr_map != null) {
						for (PlayerBtlData pbd : attr_map.keySet()) {
							AttributeSimple.Builder asb = AttributeSimple.newBuilder();
							asb.setId(pbd.id);
							asb.setValue(attr_map.get(pbd));
							attr_list.add(asb.build());
						}
					}
					strenght_list = toJson4StrengthPos(playerBasePO.strengthPos);
				} else {
					otherPlayer = target.player;
					titleId = target.titleManager.getTitleId();
					equipments = target.equipManager.getAllEquipDetails4PayLoad();
					attr_list = target.btlDataManager._getPlayerAttr();
					strenght_list = toJson4StrengthPos(target.playerBasePO.strengthPos);
				}

				if (otherPlayer != null) {
					LookUpPlayer.Builder data = LookUpPlayer.newBuilder();
					data.setName(otherPlayer.name);
					data.setLevel(otherPlayer.level);
					data.setUpLevel(otherPlayer.upLevel);
					data.setUpOrder(otherPlayer.upOrder);
					data.setPro(otherPlayer.pro);
					data.setFightPower(otherPlayer.fightPower);
					data.setExp(otherPlayer.exp);
					// data.setFightPowerRank(0);
					data.setTitleId(titleId);
					data.addAllEquipments(equipments);
					List<Avatar> avatars = new ArrayList<>();
					List<Avatar> equipAvatars = PlayerUtil.getBattlerServerAvatar(otherPlayer.id);
					avatars.addAll(equipAvatars);
					data.addAllAvatars(avatars);

					data.addAllAttrs(attr_list);
					GuildPO guildPO = GuildUtil.getPlayerGuild(otherPlayer.id);
					if (guildPO != null) {
						GuildMemberPO guildMemberPO = GuildUtil.getGuildMember(otherPlayer.id);
						data.setGuildName(guildPO.name);
						data.setGuildIcon(guildPO.icon);
						data.setGuildJob(guildMemberPO.job);
					}

					if (target != null) {
						data.setPkValue(target.pkRuleManager.getPkValueData());
					}
					data.addAllStrengthPos(strenght_list);
					res.setS2CData(data.build());
					res.setS2CCode(OK);
					body.writeBytes(res.build().toByteArray());
					return;
				} else {
					res.setS2CCode(FAIL);
					res.setS2CMsg(LangService.getValue("PARAM_ERROR"));
					body.writeBytes(res.build().toByteArray());
					return;
				}

			}
		};
	}

	private static List<EquipGridStrengthInfo> toJson4StrengthPos(Map<Integer, EquipStrengthPos> strengthPos) {
		List<EquipGridStrengthInfo> list = new ArrayList<>();
		if (strengthPos == null)
			return list;
		for (int pos : strengthPos.keySet()) {
			list.add(getStrenghInfo(strengthPos, pos));
		}
		return list;
	}

	/**
	 * 获取单个格子 协议信息
	 */
	private static EquipGridStrengthInfo getStrenghInfo(Map<Integer, EquipStrengthPos> strengthPos, int pos) {
		EquipStrengthPos info = strengthPos.get(pos);
		EquipGridStrengthInfo.Builder data = EquipGridStrengthInfo.newBuilder();
		data.setPos(pos);
		data.setEnSection(info.enSection);
		data.setEnLevel(info.enLevel);
		data.addAllJewelAtts(EquipUtil.toJson4Gem(info));
		data.setSocks(info.socks);
		return data.build();
	}
}
