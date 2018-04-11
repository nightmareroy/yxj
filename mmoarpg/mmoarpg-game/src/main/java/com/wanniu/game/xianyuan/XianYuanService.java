package com.wanniu.game.xianyuan;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.wanniu.core.game.LangService;
import com.wanniu.core.game.protocol.PomeloRequest;
import com.wanniu.game.data.FateCO;
import com.wanniu.game.data.GameData;
import com.wanniu.game.player.WNPlayer;
import com.wanniu.game.poes.XianYuanPO;

import pomelo.xianyuan.XianYuanHandler.XianYuanGetInfo;
import pomelo.xianyuan.XianYuanHandler.XianYuanResponse;

/**
 * @author wanghaitao
 */
public class XianYuanService {
	private XianYuanService() {}

	public static XianYuanService getInstance() {
		return Inner.instance;
	}

	private static class Inner {
		private static XianYuanService instance = new XianYuanService();
	}

	/**
	 * 获取玩家的仙缘信息
	 */
	public void applyXianYuanGetInfo(WNPlayer player, XianYuanResponse.Builder res) {
		XianYuanPO xianYuanPo = player.allBlobData.xianYuan;
		if (xianYuanPo == null) {
			res.setS2CCode(PomeloRequest.FAIL);
			res.setS2CMsg(LangService.getValue("XIAN_YUAN_NOT_OPEN"));
			return;
		}

		res.setS2CCode(PomeloRequest.OK);
		res.setTotalXianYuan(player.moneyManager.getXianYuan());
		Map<Integer, Integer> reviceNumbers = xianYuanPo.reviceNumbers;
		Map<Integer, FateCO> fates = GameData.Fates;
		List<XianYuanGetInfo> xianYuanGetInfos = new ArrayList<>();
		for (Entry<Integer, FateCO> fate : fates.entrySet()) {
			XianYuanGetInfo.Builder xianYuanGetInfo = XianYuanGetInfo.newBuilder();
			int configId = fate.getKey();
			FateCO fc = fate.getValue();
			xianYuanGetInfo.setConfigId(configId);
			int todayRecive = 0;
			if (reviceNumbers != null && reviceNumbers.containsKey(configId)) {
				todayRecive = reviceNumbers.get(configId);
			}
			xianYuanGetInfo.setTodayRecive(todayRecive);
			xianYuanGetInfo.setTodayLimite(fc.numLimit);
			xianYuanGetInfos.add(xianYuanGetInfo.build());
		}
		res.addAllXianYuanGetInfo(xianYuanGetInfos);
	}

	/**
	 * 创建仙缘信息
	 */
	public XianYuanPO createXianYuan(String playerId) {
		XianYuanPO xianYuanPo = new XianYuanPO();
		xianYuanPo.createTime = new Date();
		xianYuanPo.updateTime = new Date();
		return xianYuanPo;
	}

	/**
	 * 处理玩家获取仙缘值
	 * 
	 * @param from globalConfig中的配置
	 */
	public int processXianYuanGet(int from, XianYuanPO xianYuanPo) {
		FateCO fate = GameData.Fates.get(from);
		if (fate == null || xianYuanPo == null) {
			return 0;
		}

		int singleNumber = fate.singleNum;
		int numLimit = fate.numLimit;
		Map<Integer, Integer> reviceNumbers = xianYuanPo.reviceNumbers;
		int todayReciveNum = 0;
		if (reviceNumbers != null) {
			Integer reciveNumber = reviceNumbers.get(from);
			if (reciveNumber != null) {
				todayReciveNum = reciveNumber;
			}
			if (todayReciveNum >= numLimit) {
				return 0;
			}
			// added by liyue
			if (todayReciveNum + singleNumber > numLimit) {
				return numLimit - todayReciveNum;
			}
		}
		return singleNumber;
	}

	/**
	 * 清空仙缘值获得上限.
	 */
	public void refreshNewDay(XianYuanPO xianYuanPo) {
		if (xianYuanPo.reviceNumbers != null) {
			xianYuanPo.reviceNumbers.clear();
		}
	}
}
