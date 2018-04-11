package com.wanniu.game.request.fightLevel.illusion;

import java.io.IOException;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import com.wanniu.core.game.entity.GClientEvent;
import com.wanniu.core.game.protocol.PomeloResponse;
import com.wanniu.game.player.GlobalConfig;
import com.wanniu.game.player.WNPlayer;
import com.wanniu.game.poes.IllusionPO;
import com.wanniu.game.request.fightLevel.FightLevelLine;

import pomelo.area.FightLevelHandler.GetLllsion2InfoResponse;
import pomelo.area.FightLevelHandler.MJItemMax;

/**
 * 获取幻境2界面信息
 */
@GClientEvent("area.fightLevelHandler.getLllsion2InfoRequest")
public class GetIllsion2InfoHandler extends FightLevelLine {

	public PomeloResponse request(WNPlayer player) throws Exception {
		GetLllsion2InfoResponse.Builder res = GetLllsion2InfoResponse.newBuilder();
		IllusionPO illusion = player.illusionManager.illusionPO;
		Map<Integer, Integer> boxs = illusion.boxs;
		Integer lv1 = null;
		Integer lv2 = null;
		Integer lv3 = null;
		if (boxs != null) {
			lv1 = boxs.get(1);
			lv2 = boxs.get(2);
			lv3 = boxs.get(3);
		}
		res.setS2CTodayLv1(lv1 == null ? 0 : lv1);
		res.setS2CTodayLv2(lv2 == null ? 0 : lv2);
		res.setS2CTodayLv3(lv3 == null ? 0 : lv3);
		res.setS2CMaxNum(GlobalConfig.Mysterious_MaxNumEveryday);
		
		Map<String, Integer> items = illusion.items;
		Set<Entry<String,Integer>> sets = GlobalConfig.mysteriousMaxNumVcardInfo.entrySet();
		for(Entry<String,Integer> s : sets) {
			String code = s.getKey();
			int vl = s.getValue();					
			int today = 0;
			if(items != null && !items.isEmpty()) {
				Integer it = items.get(code);
				today = it == null ? 0 : it;
			}
			MJItemMax.Builder bd = MJItemMax.newBuilder();
			bd.setItemCode(code);
			bd.setS2CMaxMl(vl);
			bd.setS2CTodayMl(today);
			res.addItemInfo(bd);
		}				
		res.setS2CCode(OK);
		return new PomeloResponse() {
			@Override
			protected void write() throws IOException {				
				body.writeBytes(res.build().toByteArray());
			}
		};
	}
}