package com.wanniu.game.prepaid;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.alibaba.fastjson.JSON;
import com.wanniu.core.GConfig;
import com.wanniu.core.GGame;
import com.wanniu.core.db.GCache;
import com.wanniu.core.game.protocol.PomeloRequest;
import com.wanniu.core.game.protocol.PomeloResponse;
import com.wanniu.core.logfs.Out;
import com.wanniu.core.util.StringUtil;
import com.wanniu.core.util.http.HttpRequester;
import com.wanniu.game.common.Const;
import com.wanniu.game.common.ConstsTR;
import com.wanniu.game.common.Utils;
import com.wanniu.game.data.GameData;
import com.wanniu.game.player.WNPlayer;
import com.wanniu.game.player.bi.LogReportService;
import com.wanniu.game.poes.FeeOrderPO;
import com.wanniu.game.poes.PlayerPO;
import com.wanniu.redis.PlayerPOManager;

import pomelo.area.PrepaidHandler.PrepaidOrderIdResponse;

public class PrepaidService {
	private static PrepaidService instance;
	/**
	 * 充值中心创建订单地址
	 */
	private static String createOrderUrl = GConfig.getInstance().get("server.pay.orderUrl");
	private static String notifyOrderUrl = GConfig.getInstance().get("server.pay.notifyUrl");

	public static synchronized PrepaidService getInstance() {
		if (instance == null)
			instance = new PrepaidService();
		return instance;
	}

	private HashMap<String, String> prepaidConfig;

	private PrepaidService() {

	}

	public void init(HashMap<String, String> prepaidConfig) {
		this.prepaidConfig = prepaidConfig;
	}

	public Object getConfig(String configName) {
		return this.prepaidConfig.get(configName);
	}

	public void onPaySuccess(String orderId) {
		String orderKey = ConstsTR.feeOrder.value + "/" + orderId;
		String order = GCache.get(orderKey);
		if (StringUtil.isEmpty(order)) {
			Out.warn("充值回调，重复通知... orderId=", orderId);
			return;
		}
		FeeOrderPO po = JSON.parseObject(order, FeeOrderPO.class);
		// 删除记录，防止重复通知
		GCache.remove(orderKey);
		String playerId = po.playerId;
		int productId = po.productId;
		boolean isCard = po.isCard;
		boolean isSuperPackage = po.isSuperPackage;
		Out.info("充值回调 orderId=", orderId, ",playerId=", playerId, ",productId=", productId, ",isCard=", isCard, ",isSuperPackage=", isSuperPackage);

		PrepaidManager manager = PrepaidCenter.getInstance().findPrepaid(playerId);
		manager.onCharge(productId, isCard, isSuperPackage, true);
		PrepaidCenter.getInstance().update(playerId, manager);

		// 上报充值信息，以供分析...
		try {
			int type = 0;// 0=充值，1=月卡，2=超级礼包
			int money = 0;
			if (isCard) {
				type = 1;
				money = GameData.Cards.get(productId).payMoneyAmount;
			} else {
				if (!isSuperPackage) {
					type = 0;
					money = GameData.Pays.get(productId).payMoneyAmount;
				} else {
					type = 2;
					money = GameData.SuperPackages.get(productId).packagePrice;
				}
			}
			PlayerPO player = PlayerPOManager.findPO(ConstsTR.playerTR, playerId, PlayerPO.class);
			LogReportService.getInstance().ansycReportRecharge(player, productId, type, money);
		} catch (Exception e) {
			Out.warn("上报充值异常啦...", e);
		}
	}

	public int getResponseType(HashMap<String, String> dataMap) {
		if (dataMap.containsKey("pm_id")) {
			if (Integer.parseInt(dataMap.get("pm_id")) == 30) {
				return Const.PrepaidType.WP_PREPAID_REQUEST.getValue();
			}
		}
		return Const.PrepaidType.PREPAID_REQUEST.getValue();
	}

	/**
	 * 创建充值订单号
	 * 
	 * @param productId usercenter分配的产品编号，例如逸仙诀80
	 * @param channelId usercenter分配的渠道id，例如：清源安卓1001，当乐1003
	 * @param player
	 * @param isCard
	 * @param isSuperPackage 是否超值礼包购买
	 * @return
	 */
	public PomeloResponse createOrderId(int productId, int channelId, WNPlayer player, boolean isCard, boolean isSuperPackage, String imei, int os) {
		float money = 0;
		if (isCard) {
			money = (float) GameData.Cards.get(productId).payMoneyAmount;
		} else {
			if (!isSuperPackage)
				money = (float) GameData.Pays.get(productId).payMoneyAmount;
			else
				money = GameData.SuperPackages.get(productId).packagePrice;
		}
		Map<String, String> params = new HashMap<>();
		params.put("appid", String.valueOf(GGame.__APP_ID));
		params.put("channel", String.valueOf(channelId));
		params.put("money", String.valueOf(money));
		params.put("username", player.getUid());
		params.put("serverid", String.valueOf(GGame.__SERVER_ID));
		params.put("roleid", player.getId());
		params.put("subchannel", player.getPlayer().subchannel);

		String orderId = null;

		if (GConfig.getInstance().isEnablePay()) {
			try {
				orderId = new HttpRequester().sendPost(createOrderUrl, params).getContent();
			} catch (Exception e) {
				Out.error(e);
			}
		}
		PrepaidOrderIdResponse.Builder res = PrepaidOrderIdResponse.newBuilder();
		if (!StringUtil.isNotEmpty(orderId)) {
			res.setS2CCode(PomeloRequest.FAIL);
		} else {
			res.setS2CCode(PomeloRequest.OK);
			res.setS2COrderId(orderId);

			FeeOrderPO po = new FeeOrderPO();
			po.orderId = orderId;
			po.createtime = new Date();
			po.playerId = player.getId();
			po.productId = productId;
			po.isCard = isCard;
			po.isSuperPackage = isSuperPackage;

			// 5天过期
			GCache.put(ConstsTR.feeOrder.value + "/" + po.orderId, Utils.serialize(po), 60 * 60 * 24 * 5);
			// GameDao.update(GameDao.getKey(ConstsTR.feeOrder, po.orderId), po);
			Out.info("创建订单 playerId=", player.getId(), ",name=", player.getName(), ",productId=", productId, ",isCard=", isCard, ",isSuperPackage=", isSuperPackage);

			// 如果有配置充值回调接口，则发给客户端
			if (StringUtils.isNotEmpty(notifyOrderUrl)) {
				res.setAppNotifyUrl(notifyOrderUrl);
			}
		}
		PomeloResponse me = new PomeloResponse() {

			@Override
			protected void write() throws IOException {
				body.writeBytes(res.build().toByteArray());
			}
		};
		return me;
	}

	public PomeloResponse createOrderId(int productId, int channelId, WNPlayer player, boolean isCard, String imei, int os) {
		return createOrderId(productId, channelId, player, isCard, false, imei, os);
	}

	public void onSimulationOrder(String orderId, String playerId, int productId) {
		String orderKey = ConstsTR.feeOrder.value + "/" + orderId;
		if (!GCache.exists(orderKey)) {
			FeeOrderPO po = new FeeOrderPO();
			po.orderId = orderId;
			po.createtime = new Date();
			po.playerId = playerId;
			po.productId = productId;
			// 小于 100 算月卡
			if (productId < 100) {
				po.isCard = true;
			}
			// 大于300算超值礼包
			else if (productId > 300) {
				po.isSuperPackage = true;
			}

			// 5天过期
			GCache.put(orderKey, Utils.serialize(po), 60 * 60 * 24 * 1);
			Out.info("创建虚拟订单 playerId=", playerId, ",productId=", productId, ",isCard=", po.isCard, ",isSuperPackage=", po.isSuperPackage);
		}
	}
}