package com.wanniu.game.player;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ScheduledFuture;

import com.google.protobuf.GeneratedMessage;
import com.wanniu.core.game.JobFactory;
import com.wanniu.core.game.protocol.PomeloPush;
import com.wanniu.core.logfs.Out;
import com.wanniu.core.tcp.protocol.Message;
import com.wanniu.core.util.RandomUtil;
import com.wanniu.game.area.Area;
import com.wanniu.game.common.Const.GOODS_CHANGE_TYPE;
import com.wanniu.game.common.Const.HandsUpState;
import com.wanniu.game.common.Const.SYS_CHAT_TYPE;
import com.wanniu.game.common.Const.TipsType;
import com.wanniu.game.common.Utils;
import com.wanniu.game.common.msg.MessagePush;
import com.wanniu.game.message.MessageData;
import com.wanniu.game.player.po.AllBlobPO;
import com.wanniu.game.poes.PlayerPO;
import com.wanniu.game.util.RobotUtil;

import pomelo.Common.KeyValueStruct;
import pomelo.area.FightLevelHandler.OnConfirmEnterFubenPush;

/**
 * 机器人数据对象
 * 
 * @author agui
 */
public class WNRobot extends WNPlayer {

	public ScheduledFuture<?> quitTeamFuture, handFuture, hpFuture;

	public WNRobot(AllBlobPO allBlobData) {
		super(allBlobData);
	}

	public WNRobot(PlayerPO player) {
		super(player);
	}

	public boolean isRobot() {
		return true;
	}

	@Override
	public void sync() {
		Out.debug("sync................");
	}

	@Override
	public boolean onMessage(int operate, MessageData message) {
		Out.debug("onMessage................", message.messageType);
		return false;
	}

	@Override
	public void pushEffectData() {
		Out.debug("pushEffectData................");
	}

	@Override
	public void pushDynamicData(String key, Object value) {
		Out.debug("pushDynamicData1................");
	}

	@Override
	public void pushDynamicData(String key, Object value, GOODS_CHANGE_TYPE origin) {
		Out.debug("pushDynamicData2................");
	}

	@Override
	public void pushDynamicData(String key, Object value, GOODS_CHANGE_TYPE origin, List<KeyValueStruct> itemChange) {
		Out.debug("pushDynamicData3................");
	}

	@Override
	public void pushDynamicData(Map<String, Object> atts) {
		Out.debug("pushDynamicData4................");
	}

	@Override
	public void pushAndRefreshEffect(boolean isHpMpValid) {
		Out.debug("pushAndRefreshEffect................");
	}

	@Override
	public void sendSysTip(String content) {
		Out.debug("sendSysTip................");
	}

	@Override
	public void sendSysTip(String content, TipsType type) {
		Out.debug("sendSysTip2................");
	}

	@Override
	public void puchFuncGoToTicketNotEnough() {
		Out.debug("puchFuncGoToTicketNotEnough................");
	}

	@Override
	public void puchFuncGoToPickItem() {
		Out.debug("puchFuncGoToPickItem................");
	}

	@Override
	public void sendLeaveWord() {
		Out.debug("sendLeaveWord................");
	}

	@Override
	public void pushChatSystemMessage(SYS_CHAT_TYPE type, String value1, Object value2, String value3) {
		Out.debug("pushChatSystemMessage................");
	}

	@Override
	public void pushChatSystemMessage(SYS_CHAT_TYPE type, String value1, Object value2, String value3, GOODS_CHANGE_TYPE from) {
		Out.debug("pushChatSystemMessage2................");
	}

	@Override
	public void update() {
		Out.warn("update................");
	}

	public void free() {
		Out.info("free robot >> ", getName());
		try {
			if (hpFuture != null) {
				hpFuture.cancel(true);
				hpFuture = null;
			}
			getTeamManager().setTeamData(null);
			if (quitTeamFuture != null) {
				quitTeamFuture.cancel(true);
				quitTeamFuture = null;
			}
		} finally {
			JobFactory.addDelayJob(() -> {
				RobotUtil.freeRobot(this);
			}, Utils.getSecMills(5, 10));
		}
	}

	@Override
	public void receive(Message msg) {
		String route = ((PomeloPush) msg).getRoute();
		if (route == null) {
			return;
		}
		if (route.endsWith("throwPointItemListPush")) {
			if (RandomUtil.getInt(100) < 70) {
				Area area = getArea();
				area.onRobotThrowPoint(this);
			}
		} else {
			// Out.info("receive::", route);
		}
	}

	@Override
	public void write(Message msg) {
		String route = ((PomeloPush) msg).getRoute();
		if (route == null) {
			return;
		}
		if (route.endsWith("onConfirmEnterFubenPush")) {
			handConfirmEnterFuben(((MessagePush) msg).getMsg());
		} else if (route.endsWith("closeHandUpPush")) {
			if (handFuture != null) {
				handFuture.cancel(true);
				handFuture = null;
			}
		} else {
			// Out.info("write::", route);
		}
	}

	@Override
	public void receive(String route, GeneratedMessage msg) {
		if (route.endsWith("playerRelivePush")) {
			RobotUtil.onRobotDie(area, this);
		} else if (route.endsWith("onConfirmEnterFubenPush")) {
			handConfirmEnterFuben(msg);
		} else if (route.endsWith("five2FiveApplyMatchPush")) {
			JobFactory.addDelayJob(() -> {
				five2FiveManager.refuseMatch(this);
			}, Utils.getSecMills(2, 8));
		} else {
			// Out.info("receive route::", route);
		}
	}

	public void handConfirmEnterFuben(GeneratedMessage msg) {
		OnConfirmEnterFubenPush fuben = (OnConfirmEnterFubenPush) msg;
		handFuture = JobFactory.addDelayJob(() -> {
			handFuture = null;
			fightLevelManager.replyEnterDungeon(this, HandsUpState.ACCEPT.value, fuben.getS2CFubenId());
		}, Utils.getSecMills(1, 10));
	}

}
