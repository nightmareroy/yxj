package cn.qeng.paycenter.api;

import java.util.HashMap;
import java.util.Map;

/**
 * 充值发货通知.
 *
 * @author 小流氓(176543888@qq.com)
 */
public class PayAPI extends GmAPI {
	private String orderId;

	private String playerId = null;
	private int productId = 0;

	public PayAPI(String orderId) {
		this.orderId = orderId;
	}

	public PayAPI(String orderId, String playerId, int productId) {
		this.orderId = orderId;
		this.playerId = playerId;
		this.productId = productId;
	}

	@Override
	protected Map<String, Object> getArgs() {
		final Map<String, Object> map = new HashMap<String, Object>();
		map.put("orderId", orderId);

		if (playerId != null && productId > 0) {
			map.put("playerId", playerId);
			map.put("productId", productId);
		}

		return map;
	}
}