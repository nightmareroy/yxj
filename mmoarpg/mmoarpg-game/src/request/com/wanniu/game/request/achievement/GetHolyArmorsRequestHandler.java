package com.wanniu.game.request.achievement;

import java.io.IOException;
import java.util.Map;

import com.wanniu.core.game.LangService;
import com.wanniu.core.game.entity.GClientEvent;
import com.wanniu.core.game.protocol.PomeloRequest;
import com.wanniu.core.game.protocol.PomeloResponse;
import com.wanniu.core.logfs.Out;
import com.wanniu.game.achievement.po.BaseInfo;
import com.wanniu.game.common.Const;
import com.wanniu.game.data.ArmourAttributeCO;
import com.wanniu.game.data.GameData;
import com.wanniu.game.data.ext.AchievementConfigExt;
import com.wanniu.game.data.ext.AchievementExt;
import com.wanniu.game.player.WNPlayer;
import com.wanniu.game.poes.AchievementDataPO.HolyArmour;

import pomelo.area.AchievementHandler.GetHolyArmorsRequest;
import pomelo.area.AchievementHandler.GetHolyArmorsResponse;
import pomelo.area.AchievementHandler.HolyArmor;

/**
 * 成就 元始圣甲列表
 * 
 * @author 李玥
 *
 */
@GClientEvent("area.achievementHandler.getHolyArmorsRequest")
public class GetHolyArmorsRequestHandler extends PomeloRequest {

	public PomeloResponse request() throws Exception {
		WNPlayer player = (WNPlayer) pak.getPlayer();
		
//		GetHolyArmorsRequest req = GetHolyArmorsRequest.parseFrom(pak.getRemaingBytes());
		
		return new PomeloResponse() {
			
			@Override
			protected void write() throws IOException {

				GetHolyArmorsResponse.Builder res = GetHolyArmorsResponse.newBuilder();
				
				
				for (HolyArmour armour : player.achievementManager.achievementDataPO.holyArmourMap.values()) {
					HolyArmor.Builder haBuilder=HolyArmor.newBuilder();
					haBuilder.setId(armour.id);
					haBuilder.setStates(armour.states);
					res.addHolyArmors(haBuilder.build());
				}
				
			    
			    
			    res.setS2CCode(OK);
				body.writeBytes(res.build().toByteArray());
			}
			
		};
	}
}