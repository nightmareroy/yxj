package com.wanniu.game.guild.guildBoss;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.wanniu.core.game.LangService;
import com.wanniu.core.logfs.Out;
import com.wanniu.core.util.DateUtil;
import com.wanniu.game.GWorld;
import com.wanniu.game.area.Area;
import com.wanniu.game.area.AreaData;
import com.wanniu.game.area.AreaUtil;
import com.wanniu.game.common.Const;
import com.wanniu.game.common.Const.GOODS_CHANGE_TYPE;
import com.wanniu.game.common.Const.ManagerType;
import com.wanniu.game.common.Const.PlayerBtlData;
import com.wanniu.game.common.Const.PlayerEventType;
import com.wanniu.game.common.ConstsTR;
import com.wanniu.game.common.ModuleManager;
import com.wanniu.game.common.msg.MessageUtil;
import com.wanniu.game.data.base.MapBase;
import com.wanniu.game.data.ext.InspireLevelExt;
import com.wanniu.game.monster.GuildBossRatioConfig;
import com.wanniu.game.player.PlayerUtil;
import com.wanniu.game.player.WNPlayer;
import com.wanniu.game.poes.GuildBossPo;
import com.wanniu.game.poes.GuildPO;
import com.wanniu.game.poes.InspirePO;
import com.wanniu.game.poes.PlayerPO;
import com.wanniu.game.team.TeamData.TeamMemberData;
import com.wanniu.redis.PlayerPOManager;

import pomelo.area.GuildBossHandler.GetGuildBossInfoResponse;
import pomelo.area.GuildBossHandler.GuildBossRankInfo;
import pomelo.area.PlayerHandler.SuperScriptPush;
import pomelo.area.PlayerHandler.SuperScriptType;

public class GuildBossManager extends ModuleManager {
	private WNPlayer player;
	private MapBase mapProp;

	public GuildBossManager(WNPlayer player) {
		this.player = player;
		this.mapProp = GuildBossService.getInstance().getGuildBossMap();
	}

	public String handleEnterGuildBossArea() {
		GuildPO guildPo = player.guildManager.getGuildInfo();
		String msg = this.canEnter(guildPo);
		if (msg == null) {
			String instanceId = GuildBossCenter.getInstance().getGuildBossScenceIdByGuildId(guildPo.id, guildPo);
			if (instanceId == null) {
				Out.info("仙盟活动有新的工会进入场景。。。guildId=", guildPo.id);
				Area area = GuildBossService.getInstance().enterGuildBossSence(player, GuildBossService.getInstance().getGuildBossLevel());
				GuildBossCenter.getInstance().addOneGuildId(guildPo.id, area.instanceId, guildPo);
			} else {
				Area area = AreaUtil.getArea(instanceId);
				if (area == null) {
					Out.warn("发现有玩家在进工会BOSS场景的时候发现场景为空,guildId=", guildPo.id);
					area = GuildBossService.getInstance().enterGuildBossSence(player, GuildBossService.getInstance().getGuildBossLevel());
					GuildBossCenter.getInstance().addOneGuildId(guildPo.id, area.instanceId, guildPo);
				} else {
					AreaUtil.dispatchByInstanceId(player, new AreaData(GuildBossService.GUILDBOSS_MAP_ID, instanceId));
				}
			}
		}
		return msg;
	}

	/**
	 * 计算BUF加成(进场景)
	 * 
	 * @return
	 */
	public Map<String, Integer> calAllInfluence() {
		Map<String, Integer> map = new HashMap<>();
		if (this.player.area == null) {
			return map;
		}
		if (this.player.area.areaId == GuildBossService.GUILDBOSS_MAP_ID) {
			GuildPO guildPO = this.player.guildManager.guild;
			if (guildPO == null) {// 连工会都没有,直接忽略
				return map;
			}
			String instanceId = GuildBossCenter.getInstance().getGuildBossScenceIdByGuildIdNoLock(guildPO.id);
			if (instanceId != null) {
				Area currentArea = AreaUtil.getArea(instanceId);
				if (currentArea != null) {
					if (currentArea.getActor(this.player.getId()) != null) {
						int totalAtkAdd = ((GuildBossArea) currentArea).getTotalAtkAdd(this.player);
						map.put(PlayerBtlData.PhyPer.toString(), totalAtkAdd);
						map.put(PlayerBtlData.MagPer.toString(), totalAtkAdd);
						int totalDefAdd = ((GuildBossArea) currentArea).getTotalDefAdd(this.player);
						map.put(PlayerBtlData.Def.toString(), totalDefAdd / 100);// 历史问题啦，先除100，保障功能OK
					}
				}
			}
		} else {
			return map;
		}
		return map;
	}

	/**
	 * 
	 * @param index(1:个人2:仙盟)
	 * @return
	 */
	public String handlerInspireGuildBoss(int index) {
		GuildPO guildPO = this.player.guildManager.guild;
		if (this.player.area.areaId != GuildBossService.GUILDBOSS_MAP_ID) {// 不在工会BOSS场景下不让鼓舞
			return LangService.getValue("GUILD_BOSS_INSPIRE_SCENE");
		}
		InspirePO inspirePO = null;
		GuildBossPo guildBossPO = null;
		if (guildPO != null) {
			GuildRankBean bean = GuildBossAreaHurtRankCenter.getInstance().onlyGetGuildRankBean(guildPO.id);
			if (bean == null) {
				Out.warn("在进行工会鼓舞的时候发生了未知错误2...playerId=", this.player.getId());
				return LangService.getValue("GUILD_BOSS_INSPIRE_SCENE");
			} else if (bean.hasKilled()) {
				return LangService.getValue("GUILD_BOSS_INSPIRE_DEAD");
			}
		}

		if (index == GuildBossConstant.SINGLE_INPIRE) {
			guildBossPO = getAndCheckUpdateGuildBossPo(player.player.id);
			inspirePO = guildBossPO.inspire;
		} else if (index == GuildBossConstant.GUILD_INPIRE) {
			if (guildPO == null) {
				Out.warn("在进行工会鼓舞的时候发生了未知错误...playerId=", this.player.getId());
				return LangService.getValue("PLAYER_PRO_ILLEGALITY");
			}
			inspirePO = getAndCheckGuildBossAtkPoForGuild(guildPO);
		} else if (index == GuildBossConstant.GUILD_DEF_INPIRE) {// 仙盟防御鼓舞
			if (guildPO == null) {
				Out.warn("在进行工会鼓舞的时候发生了未知错误...playerId=", this.player.getId());
				return LangService.getValue("PLAYER_PRO_ILLEGALITY");
			}
			inspirePO = getAndCheckGuildBossDefPoForGuild(guildPO);
		} else {
			return LangService.getValue("PARAM_ERROR");
		}
		int currentCount = inspirePO.count;
		int nextCount = currentCount + 1;
		InspireLevelExt co = GuildBossRatioConfig.getInspireLevelCO(index, nextCount);
		if (co == null) {// 鼓舞次数已达上限
			return LangService.getValue("GUILD_BOSS_INSPIRE_MAXCOUNT");
		}
		boolean isEnoughMoney = this.player.moneyManager.costTicketAndDiamond(co.inspireCost, GOODS_CHANGE_TYPE.BOSS_GUILD_INSPIRE).isSuccess();
		if (!isEnoughMoney) {
			return LangService.getValue("TICKET_NOT_ENOUGH");
		}
		inspirePO.count = nextCount;
		String instanceId = GuildBossCenter.getInstance().getGuildBossScenceIdByGuildIdNoLock(guildPO.id);
		if (instanceId != null) {
			Area currentArea = AreaUtil.getArea(instanceId);
			if (currentArea != null) {
				if (index == GuildBossConstant.SINGLE_INPIRE) {
					((GuildBossArea) currentArea).receiveSinlgInspire(this.player, guildBossPO, true);// 通知场景
				} else if (index == GuildBossConstant.GUILD_INPIRE || index == GuildBossConstant.GUILD_DEF_INPIRE) {// 工会攻击鼓舞
					guildPO.modify = true;
					((GuildBossArea) currentArea).receiveGuildInspire(index, nextCount);// 通知场景
					pushAllAfterinprire(guildPO.name, this.player.getName(), co.totalInspirePlus / 100, index);
				}
			}
		}
		return null;
	}

	public void pushAllAfterinprire(String guildName, String roleName, int totalAdd, int index) {
		String tempStr2 = "";
		if (index == GuildBossConstant.GUILD_INPIRE) {
			tempStr2 = String.format(LangService.getValue("ACTIVITY_DAILY_GUILD_BOSS"), guildName, roleName, "" + totalAdd + "%", Const.TipsType.NORMAL);
		} else {
			tempStr2 = String.format(LangService.getValue("ACTIVITY_DAILY_GUILD_DEF_BOSS"), guildName, roleName, "" + totalAdd, Const.TipsType.NORMAL);
		}
		MessageUtil.sendRollChat(GWorld.__SERVER_ID, tempStr2, Const.CHAT_SCOPE.WORLD);
	}

	public GuildBossPo getAndCheckUpdateGuildBossPo(String playerId) {
		GuildBossPo guildBossPO = PlayerPOManager.findPO(ConstsTR.guildBossTR, playerId, GuildBossPo.class);
		if (guildBossPO == null) {
			synchronized (this.player) {
				guildBossPO = PlayerPOManager.findPO(ConstsTR.guildBossTR, playerId, GuildBossPo.class);
				if (guildBossPO == null) {
					guildBossPO = new GuildBossPo();
					PlayerPOManager.put(ConstsTR.guildBossTR, playerId, guildBossPO);
				}
			}
		}
		checkUpdate(guildBossPO.inspire);
		Date now = new Date();
		boolean isSameDay = DateUtil.isSameDay(guildBossPO.pointDate, now);
		if (!isSameDay) {
			guildBossPO.pointDate = new Date();
			guildBossPO.hasPoint = 0;
		}
		boolean isUpdateAuc = false;
		if (guildBossPO.aucpointDate == null) {
			isUpdateAuc = true;
		} else if (!DateUtil.isSameDay(guildBossPO.aucpointDate, now)) {
			isUpdateAuc = true;
		}
		if (isUpdateAuc) {
			guildBossPO.aucpointDate = new Date();
			guildBossPO.aucpoint = 0;
		}
		return guildBossPO;
	}

	public InspirePO getAndCheckGuildBossAtkPoForGuild(GuildPO guildPO) {
		InspirePO inspirePo = guildPO.inspire;
		if (inspirePo == null) {
			synchronized (guildPO) {
				inspirePo = guildPO.inspire;
				if (inspirePo == null) {
					inspirePo = new InspirePO();
					guildPO.inspire = inspirePo;
					guildPO.modify = true;
				}
			}
		}
		if (checkUpdate(guildPO.inspire)) {
			guildPO.modify = true;
		}
		return inspirePo;
	}

	public InspirePO getAndCheckGuildBossDefPoForGuild(GuildPO guildPO) {
		InspirePO defInspirePo = guildPO.defInspire;
		if (defInspirePo == null) {
			synchronized (guildPO) {
				defInspirePo = guildPO.defInspire;
				if (defInspirePo == null) {
					defInspirePo = new InspirePO();
					guildPO.defInspire = defInspirePo;
					guildPO.modify = true;
				}
			}
		}
		if (checkUpdate(guildPO.defInspire)) {
			guildPO.modify = true;
		}
		return defInspirePo;
	}

	private boolean checkUpdate(InspirePO inspire) {
		if (inspire == null || inspire.date == null) {
			return false;
		}
		Date now = new Date();
		boolean isSameDay = DateUtil.isSameDay(inspire.date, now);
		if (!isSameDay) {
			inspire.count = 0;
			inspire.date = now;
			return true;
		}

		return false;
	}

	public String handlerGetBossInfo(GetGuildBossInfoResponse.Builder res) {
		GuildPO guildPo = player.guildManager.getGuildInfo();
		String msg = this.canShow(guildPo);
		if (msg == null) {
			if (GuildBossCenter.getInstance().isOpen()) {
				GuildBossPo guildBossPO = player.guildBossManager.getAndCheckUpdateGuildBossPo(player.player.id);
				guildBossPO.hasPoint = 1;
			}
			this.pushScripts();
			String guildId = guildPo.id;
			int rank = 1;
			List<RankBean> list = null;
			if (GuildBossCenter.getInstance().isOpen()) {
				list = player.guildBossAreaHurtRankManager.getRankBeanListOnBegin(guildId);
			} else {
				list = player.guildBossAreaHurtRankManager.getAndSetRankBeanList(guildId);
			}
			if (list != null && !list.isEmpty()) {
				for (RankBean bean : list) {
					GuildBossRankInfo.Builder bd = getGuildBossRankInfo(bean, rank++);
					res.addRankList(bd);
				}
			}
			GuildRankBean bean = GuildBossAreaHurtRankCenter.getInstance().onlyGetGuildRankBean(guildId);
			if (bean == null) {
				res.setKilled(false);
			} else {
				if (bean.hasKilled()) {
					res.setKilled(true);
				} else {
					res.setKilled(false);
				}
			}
		}
		return msg;
	}

	private GuildBossRankInfo.Builder getGuildBossRankInfo(RankBean bean, int rank) {
		String playerId = bean.getId();
		PlayerPO po = PlayerUtil.getPlayerBaseData(playerId);
		if (po != null) {
			GuildBossRankInfo.Builder bd = GuildBossRankInfo.newBuilder();
			bd.setDamage(bean.getHurt());
			bd.setId(playerId);
			bd.setName(po.name);
			bd.setPro(po.pro);
			bd.setLevel(po.level);
			bd.setRank(rank);
			return bd;
		} else {
			Out.warn("根据工会BOSS伤害排行榜获取某个玩家的数据的时候发现角色不存在！,playerId=", playerId);
			return null;
		}

	}

	@Override
	public List<SuperScriptType> getSuperScript() {
		boolean isOpen = GuildBossCenter.getInstance().isOpen();
		GuildBossPo guildBossPO = player.guildBossManager.getAndCheckUpdateGuildBossPo(player.player.id);
		List<SuperScriptType> list = new ArrayList<>();

		SuperScriptType.Builder data2 = SuperScriptType.newBuilder();
		data2.setType(Const.SUPERSCRIPT_TYPE.GUILD_BOSS.getValue());
		if (isOpen && guildBossPO.hasPoint == 0) {
			data2.setNumber(1);
		} else {
			data2.setNumber(0);
		}
		list.add(data2.build());
		return list;
	}

	public boolean needUpdateRedPoint() {
		boolean isOpen = GuildBossCenter.getInstance().isOpen();
		GuildBossPo guildBossPO = player.guildBossManager.getAndCheckUpdateGuildBossPo(player.player.id);
		if (isOpen && guildBossPO.hasPoint == 0) {
			return true;
		}
		return false;
	}

	public void pushScripts() {
		SuperScriptPush.Builder data = SuperScriptPush.newBuilder();
		List<SuperScriptType> list = getSuperScript();
		if (list != null && !list.isEmpty()) {
			data.addAllS2CData(list);
			player.receive("area.playerPush.onSuperScriptPush", data.build());
		}
		this.player.guildManager.pushRedPoint();
	}

	/**
	 * 可进入返回null
	 * 
	 * @return
	 */
	private String canEnter(GuildPO guildPo) {
		// 不是工会成员不能进入
		if (guildPo == null) {
			return LangService.getValue("DUNGEON_GUILDBOSS_NOT_GUILDMEMBER");
		}
		GuildRankBean bean = GuildBossAreaHurtRankCenter.getInstance().onlyGetGuildRankBean(guildPo.id);
		if (bean != null && bean.hasKilled()) {
			return LangService.getValue("DUNGEON_GUILDBOSS_BOSS_DEAD");
		}
		if (this.mapProp.reqUpLevel > 0 && this.player.getPlayer().upLevel < this.mapProp.reqUpLevel) {
			return LangService.getValue("PLAER_UPLEVEL_NOT_ENOUGH");
		} else if (this.mapProp.reqLevel > 0 && this.player.getLevel() < this.mapProp.reqLevel) {
			return LangService.getValue("PLAYER_LEVEL_NOT_ENOUGH") + this.mapProp.reqUpLevel;
		} else {
			if (GuildBossCenter.getInstance().isOpen()) {
				// 判断是否在时段内
				Map<String, TeamMemberData> members = player.getTeamManager().getTeamMembers();
				if (members == null || members.isEmpty()) {
					return null;
				}
				for (TeamMemberData member : members.values()) {
					if (member.getPlayer() == null || member.getPlayer().guildManager == null || member.getPlayer().guildManager.guild == null) {
						return LangService.getValue("DUNGEON_GUILDBOSS_NOT_JOIN_GUILD");
					} else if (!member.getPlayer().guildManager.guild.id.equals(guildPo.id)) {
						return LangService.getValue("DUNGEON_GUILDBOSS_NOT_SAME_GUILD");
					}
				}
				return null;
			} else {
				return LangService.getValue("DUNGEON_GUILDBOSS_NOT_OPEN");
			}
		}

	}

	private String canShow(GuildPO guildPo) {
		// 不是工会成员不能进入
		if (guildPo == null) {
			return LangService.getValue("DUNGEON_GUILDBOSS_NOT_GUILDMEMBER");
		}
		return null;
	}

	@Override
	public void onPlayerEvent(PlayerEventType eventType) {
		// TODO Auto-generated method stub

	}

	@Override
	public ManagerType getManagerType() {
		return ManagerType.GUILD_BOSS;
	};
}
