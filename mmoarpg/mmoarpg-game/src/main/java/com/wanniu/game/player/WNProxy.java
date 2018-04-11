package com.wanniu.game.player;

import java.io.IOException;
import java.util.ArrayList;

import com.alibaba.fastjson.JSON;
import com.google.protobuf.GeneratedMessage;
import com.wanniu.core.logfs.Out;
import com.wanniu.core.proxy.ProxyClient;
import com.wanniu.core.proxy.ProxyType;
import com.wanniu.core.tcp.protocol.Message;
import com.wanniu.core.tcp.protocol.Packet;
import com.wanniu.core.util.StringUtil;
import com.wanniu.game.GWorld;
import com.wanniu.game.achievement.AchievementManager;
import com.wanniu.game.area.Area;
import com.wanniu.game.area.AreaData;
import com.wanniu.game.bag.WNBag;
import com.wanniu.game.buffer.BufferManager;
import com.wanniu.game.common.Const;
import com.wanniu.game.common.Utils;
import com.wanniu.game.common.msg.MessagePush;
import com.wanniu.game.daily.DailyActivityMgr;
import com.wanniu.game.illusion.IllusionManager;
import com.wanniu.game.item.NormalItem;
import com.wanniu.game.message.MessageData;
import com.wanniu.game.petNew.PetNew;
import com.wanniu.game.poes.IllusionPO;
import com.wanniu.game.poes.PetNewPO;
import com.wanniu.game.poes.PlayerAttachPO;
import com.wanniu.game.poes.VipPO;
import com.wanniu.game.poes.XianYuanPO;
import com.wanniu.game.task.TaskEvent;
import com.wanniu.game.vip.VipManager;

/**
 * 代理对象
 * 
 * @author agui
 */
public class WNProxy extends WNPlayer {

	public String playerId, name;
	public int sid, level, pro, guildExdExp, guildExdGold, btlExdGold;
	public PetNew pet;
	public float vipExpRatio, vipGoldRatio;
	public boolean robot;
	public XianYuanPO xianYuan;

	class BufferManagerProxy extends BufferManager {

		public BufferManagerProxy() {}

		public int getBuffAttrValue(String key) {
			return 0;
		}

	}

	class IllusionManagerProxy extends IllusionManager {

		public IllusionManagerProxy(WNPlayer player, IllusionPO illusionPO) {
			super(player, illusionPO);
		}

		public int addAward(String code, int value) {
			onProxyEvent(4, body -> {// 幻境奖励
				body.writeString(code);
				body.writeInt(value);
			});
			return value;
		}

	}

	class VipManagerProxy extends VipManager {

		public VipManagerProxy(WNPlayer player, VipPO po) {
			super(player, po);
		}

		public float getVipExpRatio() {
			return vipExpRatio;
		}

		public float getExtGoldRatio() {
			return vipGoldRatio;
		}

	}

	class WNBagProxy extends WNBag {

		public WNBagProxy() {
			super();
		}

		public void addEntityItem(NormalItem item, Const.GOODS_CHANGE_TYPE fromDes) {
			onProxyEvent(2, body -> {// 物品
				body.writeInt(fromDes.value);
				body.writeString(Utils.serialize(item.itemDb));
			});
		}

		public boolean addCodeItemMail(String itemCode, int number, Const.ForceType forceType, Const.GOODS_CHANGE_TYPE fromDes, String mailKey) {
			onProxyEvent(14, body -> {// 补充邮件类物品
				body.writeString(itemCode);
				body.writeInt(number);
				body.writeInt(forceType == null ? 0 : forceType.getValue());
				body.writeInt(fromDes == null ? 0 : fromDes.getValue());
				body.writeString(mailKey);
			});
			return true;
		}

		public boolean testAddCodeItem(String itemCode, int itemNum) {
			return true;
		}

	}

	class PetNewProxy extends PetNew {

		public PetNewProxy(WNPlayer master, PetNewPO po) {
			super(po, master);
		}

		public int addExp(int exp, boolean synchBattleServer) {
			onProxyEvent(5, body -> {// 出战宠物经验
				body.writeInt(exp);
			});
			return exp;
		}

	}

	class AchievementManagerProxy extends AchievementManager {

		public AchievementManagerProxy() {
			super();
		}

		public void onPassedDungeon(int id) {
			onProxyEvent(8, body -> {
				body.writeInt(id);
			});
		}

	}

	class DailyActivityMgrProxy extends DailyActivityMgr {

		public DailyActivityMgrProxy() {
			super();
		}

		public void onEvent(Const.DailyType type, String target, int num) {
			onProxyEvent(9, body -> {
				body.writeString(type.toString());
				body.writeString(target);
				body.writeInt(num);
			});
		}

	}

	class PlayerAttachPOProxy extends PlayerAttachPO {

		@Override
		public void addFirstMonsterId(int monsterId) {
			onProxyEvent(6, body -> {// 出战宠物经验
				body.writeInt(monsterId);
			});
		}

	}

	public WNProxy(String playerId, int sid) {
		this.playerId = playerId;
		this.sid = sid;
		bag = new WNBagProxy();
		dailyActivityMgr = new DailyActivityMgrProxy();
		achievementManager = new AchievementManagerProxy();
		bufferManager = new BufferManagerProxy();
	}

	public void from(Packet pak) {
		robot = pak.getBoolean();
		vipExpRatio = pak.getFloat();
		guildExdExp = pak.getInt();
		guildExdGold = pak.getInt();
		btlExdGold = pak.getInt();
		playerAttachPO = new PlayerAttachPOProxy();
		String petPo = pak.getString();
		String illusionPO = pak.getString();
		String vipPO = pak.getString();
		String xianYuan = pak.getString();
		int count = pak.getShort();
		playerAttachPO.firstKillMonsterIds = new ArrayList<>(count);
		for (int i = 0; i < count; i++) {
			playerAttachPO.firstKillMonsterIds.add(pak.getInt());
		}
		if (StringUtil.isNotEmpty(petPo)) {
			pet = new PetNewProxy(this, JSON.parseObject(petPo, PetNewPO.class));
		}
		illusionManager = new IllusionManagerProxy(this, JSON.parseObject(illusionPO, IllusionPO.class));
		vipManager = new VipManagerProxy(this, JSON.parseObject(vipPO, VipPO.class));
		if (StringUtil.isNotEmpty(xianYuan)) {
			this.xianYuan = Utils.deserialize(xianYuan, XianYuanPO.class);
		}

		Out.info("set proxy data.");
	}

	@Override
	public WNBag getWnBag() {
		return bag;
	}

	public boolean isRemote() {
		return true;
	}

	public boolean isProxy() {
		return true;
	}

	public boolean isRobot() {
		return robot;
	}

	public void changeArea(AreaData areaData) {
		Out.debug("proxy change area : ", areaData);
		ProxyClient.getInstance().add(new Message() {
			@Override
			protected void write() throws IOException {
				body.writeString(playerId);
				body.writeInt(areaData.areaId);
				body.writeString(areaData.instanceId);
			}

			@Override
			public short getType() {
				return ProxyType.CHANGE_AREA;
			}
		});
	}

	@Override
	public String getId() {
		return playerId;
	}

	public int getSid() {
		return sid;
	}

	public String getName() {
		return name;
	}

	public int getLevel() {
		return level;
	}

	public int getPro() {
		return pro;
	}

	@Override
	public void sync() {
		Out.info("proxy sync................");
	}

	@Override
	public boolean onMessage(int operate, MessageData message) {
		Out.info("proxy onMessage................", message.messageType);
		return false;
	}

	@Override
	public void update() {
		Out.warn("proxy update................");
	}

	public void free() {
		free(false);
	}

	public void free(boolean keepObject) {
		Out.info("free proxy >> ", getName(), " - ", keepObject);
		if (!keepObject) {
			GWorld.Proxys.remove(playerId);
		}
		Area area = getArea();
		if (area != null) {
			area.removePlayer(this, keepObject);
		}
	}

	@Override
	public void receive(Message msg) {
		processMessage(msg);
	}

	@Override
	public void write(Message msg) {
		processMessage(msg);
	}

	@Override
	public void receive(String route, GeneratedMessage msg) {
		processMessage(new MessagePush(route, msg));
	}

	private void processMessage(Message msg) {
		ProxyClient.getInstance().add(new Message() {
			@Override
			protected void write() throws IOException {
				body.writeString(playerId);
				body.writeBuffer(msg.getContent());
			}

			@Override
			public short getType() {
				return ProxyType.PLAYER_RECEIVE;
			}
		});
	}

	@Override
	public void onEvent(TaskEvent event) {
		onProxyEvent(1, body -> { // 事件
			body.writeInt(event.type);
			body.writeString(Utils.serialize(event.params));
		});
	}

	public PetNew getFightingPet() {
		return pet;
	}

	public void finishFightLevel(int currHard, int templateID) {
		onProxyEvent(7, body -> { // 通关副本
			body.writeInt(currHard);
			body.writeInt(templateID);
		});
	}

	public void onProxyEvent(int type, ProxyEventCB cb) {
		ProxyClient.getInstance().add(new Message() {
			@Override
			protected void write() throws IOException {
				body.writeString(getId());
				body.writeByte(type);
				cb.put(body);
			}

			@Override
			public short getType() {
				return ProxyType.PLAYER_EVENT;
			}
		});
	}

	public int getGuildExdExp() {
		return guildExdExp;
	}

	public int getGuildExdGold() {
		return guildExdGold;
	}

	public int getBtlExdGold() {
		return btlExdGold;
	}

	public int processXianYuanGet(int from) {
		// int addNum = XianYuanService.getInstance().processXianYuanGet(from,
		// xianYuan);
		// if (addNum != 0) {
		// onProxyEvent(12, body -> {
		// body.writeInt(addNum);
		// body.writeInt(from);
		// });
		// }
		return 0;
	}

}
