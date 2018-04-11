package com.wanniu.game.request.entry;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.wanniu.core.GGame;
import com.wanniu.core.GGlobal;
import com.wanniu.core.game.LangService;
import com.wanniu.core.game.entity.GClientEvent;
import com.wanniu.core.game.protocol.PomeloHeader;
import com.wanniu.core.game.protocol.PomeloRequest;
import com.wanniu.core.game.protocol.PomeloResponse;
import com.wanniu.core.logfs.Out;
import com.wanniu.core.tcp.protocol.Packet;
import com.wanniu.core.util.StringUtil;
import com.wanniu.game.GWorld;
import com.wanniu.game.area.Area;
import com.wanniu.game.area.AreaUtil;
import com.wanniu.game.common.Const.KickReason;
import com.wanniu.game.common.msg.MessagePush;
import com.wanniu.game.data.base.MapBase;
import com.wanniu.game.petNew.PetManager;
import com.wanniu.game.player.BILogService;
import com.wanniu.game.player.PlayerDao;
import com.wanniu.game.player.PlayerUtil;
import com.wanniu.game.player.WNPlayer;
import com.wanniu.game.player.bi.LogReportService;
import com.wanniu.game.player.po.AllBlobPO;

import cn.qeng.common.login.TokenInfo;
import io.netty.channel.Channel;
import pomelo.Common.KeyValueStruct;
import pomelo.area.PlayerHandler.KickPlayerPush;
import pomelo.connector.EntryHandler.BindPlayerRequest;
import pomelo.connector.EntryHandler.BindPlayerResponse;
import pomelo.item.ItemOuterClass.CountItems;
import pomelo.player.PlayerOuterClass.Player;
import pomelo.player.PlayerOuterClass.Stores;

/**
 * 绑定角色
 * 
 * @author agui
 */
@GClientEvent("connector.entryHandler.bindPlayerRequest")
public class BindPlayerHandler extends PomeloRequest {

	public void execute(Packet pak) {
		watcher.begin(pak.getHeader().getLength());
		this.pak = pak;
		PomeloResponse res = null;
		try {
			res = this.request();
		} catch (Exception e) {
			res = GGame.getInstance().getErrResponse(e);
		}
		if (res != null) {
			PomeloHeader header = res.getHeader();
			header.setType(pak.getHeader().getType());
			watcher.end(res.getContent().readableBytes());
			write(res);
			if (tmpPlayer != null && tmpArea != null) {
				// AreaUtil.pushClient(tmpPlayer, tmpArea.areaId,tmpArea.instanceId);
				tmpPlayer.setState((byte) 1);
			}
		} else {
			watcher.end(0);
		}
	}

	private WNPlayer tmpPlayer = null;
	private Area tmpArea = null;

	public PomeloResponse request() throws Exception {
		BindPlayerRequest req = BindPlayerRequest.parseFrom(pak.getRemaingBytes());
		String uid = pak.getUid();
		String playerId = req.getC2SPlayerId();

		return new PomeloResponse() {
			@Override
			protected void write() throws IOException {
				BindPlayerResponse.Builder res = BindPlayerResponse.newBuilder();

				if (StringUtil.isEmpty(uid)) {
					Out.warn(pak.getIp(), " bindPlayerRequest session uid null!");
					res.setS2CCode(KICK);
					res.setS2CMsg(LangService.getValue("VERIFY_FAIL"));
					body.writeBytes(res.build().toByteArray());
					return;
				}

				if (StringUtil.isEmpty(playerId)) {
					Out.warn("bindPlayerRequest msg playerId null!");
					res.setS2CCode(KICK);
					res.setS2CMsg(LangService.getValue("PLAYER_ID_NULL"));
					body.writeBytes(res.build().toByteArray());
					return;
				}

				try {
					WNPlayer player = PlayerUtil.getOnlinePlayerByUid(uid);
					// 在线则先提玩家下线
					if (player != null) {
						player.setLogoutTime(new Date());
						Channel session = player.getSession();
						if (session != pak.getSession() && session.isActive()) {
							// 该玩家状态不是sessionClosed，是异常状态，返回错误
							KickPlayerPush.Builder data = KickPlayerPush.newBuilder();
							data.setS2CReasonType(KickReason.NEW_LOGIN.value);
							session.write(new MessagePush("area.playerPush.kickPlayerPush", data.build()).getContent()).await(2000);
							session.close();
						}
					}
					if (player == null || !player.getId().equals(playerId)) {
						if (player != null) {
							player.doLogout(true);
						}
						// 如果area没有该玩家创建新玩家
						// 获取玩家
						AllBlobPO allBlobData = PlayerDao.getAllBlobData(playerId);
						if (allBlobData.player == null) {
							res.setS2CCode(FAIL);
							res.setS2CMsg(LangService.getValue("PLAYER_ID_NULL"));
							body.writeBytes(res.build().toByteArray());
							return;
						}

						// 创建WNPlayer
						player = new WNPlayer(allBlobData);
					}

					Date freezeTime = player.getPlayer().freezeTime;
					if (freezeTime != null && freezeTime.getTime() > System.currentTimeMillis()) {
						res.setS2CCode(FAIL);
						res.setS2CMsg(LangService.getValue("ROLE_LOCKED") + "：" + player.player.freezeReason);
						body.writeBytes(res.build().toByteArray());
						return;
					}

					// 加入缓存
					player.bind(pak.getSession());

					long beginTime = System.currentTimeMillis();

					TokenInfo token = pak.getAttr(GGlobal.__KEY_TOKEN_INFO);
					if (token != null) {
						player.getPlayer().channel = token.getChannel();
						player.getPlayer().subchannel = token.getSubchannel();
						player.getPlayer().subchannelUID = token.getSubchannelUid();
						player.getPlayer().mac = token.getMac();
						player.getPlayer().os = token.getOs();
						player.getPlayer().ip = pak.getIp();
					}

					Out.info("角色进入游戏 uid=", player.getUid(), ",playerId=", player.getId(), ",name=", player.getName(), ",IP=", player.getPlayer().ip);
					LogReportService.getInstance().ansycReportLogin(player.getPlayer());
					BILogService.getInstance().ansycReportPlayerData(player.getSession(), player.getPlayer(), false);
					
					Player.Builder data = bindLoadData(player);

					// 分配场景
					player.setState((byte) 0);
					Area area = AreaUtil.dispatch(player);
					MapBase sceneProp = area.prop;
					data.setAreaId(sceneProp.templateID);
					data.setInstanceId(area.instanceId);
					data.setMapId(area.areaId);

					// 正常返回
					res.setS2CCode(OK);
					res.setS2CPlayer(data);
					res.setS2CSceneType(sceneProp.type);
					res.setS2CSceneUseAgent(sceneProp.useAgent);
					res.setS2CChangePkType(sceneProp.changePKtype);
					res.setS2CRideMount(sceneProp.rideMount);

					body.writeBytes(res.build().toByteArray());

					WNPlayer login_player = player;
					GWorld.getInstance().ansycExec(() -> {
						login_player.onLogin();
						area.onPlayerLogin(login_player);
					});

					Out.debug(player.getName(), " enter game use : ", System.currentTimeMillis() - beginTime);
					tmpPlayer = player;
					tmpArea = area;
				} catch (Exception e) {
					Out.error(e);
				} finally {
					LoginQueue.removeBindQueue(pak.getSession());
				}

			}
		};
	}

	/**
	 * 网络流序列化
	 */
	public Player.Builder bindLoadData(WNPlayer player) {
		Player.Builder data = Player.newBuilder();
		data.setId(player.getId());
		data.setUid(player.getUid());
		data.setName(player.getName());
		data.setLevel(player.player.level);
		data.setExp(player.player.exp);
		data.setPro(player.player.pro);
		data.setUpOrder(player.player.upOrder);
		data.setClassExp(player.player.classExp);
		data.setPrestige(player.player.prestige);
		data.setJuewei(0);
		data.setGold(player.player.gold);
		data.setTicket(player.player.ticket);
		data.setDiamond(player.player.diamond);
		data.setFriendly(player.player.friendly);
		data.setConsumePoint(player.moneyManager.getConsumePoint());
		data.setVip(player.baseDataManager.getVip());
		data.setHp(player.playerTempData.hp);
		data.setMp(player.playerTempData.mp);
		data.setNeedExp(player.player.needExp);
		data.setFightPower(player.player.fightPower);
		data.setUpLevel(player.player.upLevel);
		data.setReqLevel(0);
		Stores.Builder store = Stores.newBuilder();
		store.setBag(player.bag.toJson4Payload());
		store.setWareHouse(player.wareHouse.toJson4Payload());
		store.setRecycle(player.recycle.toJson4Payload());
		data.setStore(store);
		data.setEquipments(player.equipManager.toJson4Payload());
		data.addAllFashionInfo(player.fashionManager.toJson4Fashion());
		data.addAllStrengthPos(player.equipManager.toJson4StrengthPos());
		data.setTasks(player.taskManager.toJson4Payload());
		data.setSetData(player.sysSetManager.toJson4Payload());

		data.setHookSetData(player.hookSetManager.toJson4Payload());
		data.addAllSkillKeys(player.skillKeyManager.toJson4Payload());
		data.setCountItems(CountItems.newBuilder());
		data.setPawnGold(player.player.pawnGold);
		data.setGuildpoint(player.player.guildpoint);
		data.setTreasurePoint(player.player.treasurePoint);
		data.setZoneId(String.valueOf(player.getLogicServerId()));
		data.setPetPkModel(PetManager.getPkModel());
		data.addAllFunctionList(player.functionOpenManager.toJson4PayLoad());

		List<KeyValueStruct> ccs = new ArrayList<>();
		if (player.playerAttachPO.config != null) {
			for (String key : player.playerAttachPO.config.keySet()) {
				KeyValueStruct.Builder cc = KeyValueStruct.newBuilder();
				cc.setKey(key);
				cc.setValue(player.playerAttachPO.config.get(key));
				ccs.add(cc.build());
			}
		}
		data.addAllClientConfig(ccs);
		data.setFightingPetId(player.petNewManager.getFightingPetId());
		data.setOpenMount(player.player.openMount ? 1 : 0);
		data.setSolopoint(player.soloManager.getSolopoint());
		data.setPayGiftData(player.prepaidManager.getFirstPayStatus());
		return data;
	};

	public short getType() {
		return 0x102;
	}

}
