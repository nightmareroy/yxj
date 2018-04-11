/*
 * Copyright © 2017 qeng.cn All Rights Reserved.
 * 
 * 感谢您加入清源科技，不用多久，您就会升职加薪、当上总经理、出任CEO、迎娶白富美、从此走上人生巅峰
 * 除非符合本公司的商业许可协议，否则不得使用或传播此源码，您可以下载许可协议文件：
 * 
 * 		http://www.noark.xyz/qeng/LICENSE
 *
 * 1、未经许可，任何公司及个人不得以任何方式或理由来修改、使用或传播此源码;
 * 2、禁止在本源码或其他相关源码的基础上发展任何派生版本、修改版本或第三方版本;
 * 3、无论你对源代码做出任何修改和优化，版权都归清源科技所有，我们将保留所有权利;
 * 4、凡侵犯清源科技相关版权或著作权等知识产权者，必依法追究其法律责任，特此郑重法律声明！
 */
package cn.qeng.paycenter.controller;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeSet;

import javax.servlet.http.HttpServletRequest;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import cn.qeng.paycenter.domain.OrderInfo;
import cn.qeng.paycenter.util.HttpsUtils;
import cn.qeng.paycenter.util.Md5;

/**
 * 魅族充值成功回调发货接口.
 *
 * @author 周明凯(zhoumingkai@qeng.cn)
 */
@RestController
@RequestMapping("/paycenter/to")
public class MeizuNotifyController extends AbstractNotifyController {
	private final static Logger logger = LogManager.getLogger(MeizuNotifyController.class);

	@Value("${android.meizu.app.id}")
	private int appId;
	@Value("${android.meizu.check.url}")
	private String checkurl;
	@Value("${android.meizu.app.secret}")
	private String app_secret;

	/**
	 * 魅族充值成功回调发货接口.
	 */
	@ResponseBody
	@RequestMapping("/meizuNotify.jsp")
	public String notify(HttpServletRequest request) throws Exception {
		logger.info("魅族充值成功回调 params={}", request.getParameterMap());

		final String notifyIp = request.getRemoteHost();
		String order_id = request.getParameter("order_id");// 订单 ID
		String cp_order_id = request.getParameter("cp_order_id");// 游戏订单 ID
		double total_price = Double.parseDouble(request.getParameter("total_price"));// 总金额
		String status = request.getParameter("status");// 支付状态，1：支付成功
		String sign = request.getParameter("sign");// 签名串(用 app_secret 进行签名)

		return super.notify(request, cp_order_id, sign, status, total_price, notifyIp, order_id);
	}

	// 签名规则：
	// 将参与签名的参数以ASCII码的增序排序，若遇到相同的首字母，则比较第二个字
	// 母，以此类推。完成排序后，把所有参数以“&”字符连接起来，并在最后以“:”字符将魅
	// 族分配的app_key或者app_secret【app_key、app_secret使用那个，请看据体接口文档】连
	// 接起来如：
	// 【
	// MD5(key1=value1&key2=value2&key3=value3&key4=value4&key5=value5:f32fdc02123a82524
	// eb4ea95e1383d0b)
	// 】
	public static String sortParams(Map<String, String[]> params) {
		TreeSet<String> set = new TreeSet<String>(params.keySet());
		StringBuilder sb = new StringBuilder(256);
		for (String key : set) {
			if (key.equals("sign")) {
				continue;
			}
			String v = params.get(key)[0];
			if (v != null) {
				sb.append(key).append("=").append(v).append("&");
			}
		}
		sb.deleteCharAt(sb.length() - 1);// 去除最后一个“&”符号
		return sb.toString();
	}

	@Override
	protected String buildParams(HttpServletRequest request) {
		return sortParams(request.getParameterMap());
	}

	@Override
	protected boolean checkSign(HttpServletRequest request, String params, String sign) throws Exception {
		StringBuilder sb = new StringBuilder(256);
		sb.append(params).append(":").append(app_secret);// 签名串(用 app_secret 进行签名)
		return Md5.getMD5(sb.toString()).equals(sign.toUpperCase());
	}

	@Override
	protected String resultSuccess() {
		return "{\"code\":200}";
	}

	@Override
	protected boolean checkStatus(String status, OrderInfo info) throws Exception {
		// 状态不是1，直接交易失败
		if (!"1".equals(status)) {
			return false;
		}

		// 需要2次认证...
		Map<String, String[]> params = new HashMap<String, String[]>();
		params.put("app_id", new String[] { String.valueOf(appId) });
		params.put("cp_orderid", new String[] { info.getId() });
		params.put("ts", new String[] { String.valueOf(System.currentTimeMillis()) });
		String strparams = sortParams(params);

		StringBuilder sb = new StringBuilder(256);
		sb.append(strparams).append(":").append(app_secret);// 签名串(用 app_secret 进行签名)
		String json = HttpsUtils.get(checkurl + "?" + strparams + "&sign=" + Md5.getMD5(sb.toString()));

		if (StringUtils.isEmpty(json)) {
			return false;
		}

		// {"code":200,"message":"","value":{"uid":"979066","deliver_status":"2","game_server_id":"10002","extend_param":"","status":"1","buy_amount":"1","product_name":"6元","product_desc":"6元","success_time":"1515494826","order_id":"1801091847001011941","total_price":"6.0","cp_orderid":"356","sign":"161a013849410c6a8e6b63adf293dbd2","product_per_price":"600.0","create_time":"1515494820","app_id":"514"}}
		JSONObject resultObject = JSON.parseObject(json);
		int code = resultObject.getIntValue("code");
		if (code == 200) {
			// 取出openid
			JSONObject dataObject = resultObject.getJSONObject("value");
			// 状态
			if (!"1".equals(dataObject.getString("status"))) {
				logger.warn("魅族充值2次认证时，交易状态异常 id={}", info.getId());
				return false;
			}

			// 验证钱
			if ((info.getMoney() / 100.0D) != dataObject.getDoubleValue("total_price")) {
				logger.warn("魅族充值2次认证时，交易金额异常 id={}", info.getId());
				return false;
			}

			// 基本OK
			return true;
		}

		// 失败...
		return false;
	}

	public static void main(String[] args) throws Exception {
		// app_id=514&buy_amount=1&cTime=1515494820&cp_order_id=356&extend_param=&game_server_id=10002&notify_time=1515494826160&order_id=1801091847001011941&product_desc=6元&product_name=6元&product_per_price=600.0&status=1&success_time=1515494826&total_price=6.0&uid=979066
		String url = "https://poly-game.meizu.com/poly/order/query";

		Map<String, String[]> params = new HashMap<String, String[]>();
		params.put("app_id", new String[] { String.valueOf(514) });
		params.put("cp_orderid", new String[] { String.valueOf(356) });
		params.put("ts", new String[] { String.valueOf(System.currentTimeMillis()) });
		String x = sortParams(params);

		StringBuilder sb = new StringBuilder(256);
		sb.append(x).append(":").append("8a687a9798c0a3b7edbdfb5cb2be854a");// 签名串(用 app_secret 进行签名)

		// app_id int Y Y 游戏 ID
		// cp_orderid String Y Y 游戏订单 ID
		// ts long Y Y 时间戳
		// sign String Y N 签名串(用 app_secret 进行签名)
		HttpsUtils.get(url + "?" + x + "&sign=" + Md5.getMD5(sb.toString()));
	}
}