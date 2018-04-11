package com.wanniu.game.request.achievement;

import java.io.IOException;
import java.util.Map;

import com.wanniu.core.game.LangService;
import com.wanniu.core.game.entity.GClientEvent;
import com.wanniu.core.game.protocol.PomeloRequest;
import com.wanniu.core.game.protocol.PomeloResponse;
import com.wanniu.game.achievement.po.BaseInfo;
import com.wanniu.game.common.Const;
import com.wanniu.game.data.ArmourAttributeCO;
import com.wanniu.game.data.GameData;
import com.wanniu.game.data.ext.AchievementConfigExt;
import com.wanniu.game.data.ext.AchievementExt;
import com.wanniu.game.player.WNPlayer;
import com.wanniu.game.poes.AchievementDataPO.HolyArmour;

import pomelo.area.AchievementHandler.ActivateHolyArmorRequest;
import pomelo.area.AchievementHandler.ActivateHolyArmorResponse;
import pomelo.area.AchievementHandler.HolyArmor;

/**
 * 激活元始圣甲
 * 
 * @author 李玥
 *
 */
@GClientEvent("area.achievementHandler.activateHolyArmorRequest")
public class ActivateHolyArmorRequestHandler extends PomeloRequest {

	public PomeloResponse request() throws Exception {
		WNPlayer player = (WNPlayer) pak.getPlayer();
		
		ActivateHolyArmorRequest req = ActivateHolyArmorRequest.parseFrom(pak.getRemaingBytes());
		int id=req.getId();
		
		return new PomeloResponse() {
			
			@Override
			protected void write() throws IOException {

				ActivateHolyArmorResponse.Builder res = ActivateHolyArmorResponse.newBuilder();
				
				HolyArmour armour = player.achievementManager.achievementDataPO.holyArmourMap.get(id);
				if(armour.states==1)
				{
					res.setS2CCode(FAIL);
					res.setS2CMsg(LangService.getValue("ACHIEVEMENT_NOT_ACTIVATED"));
					return;
				}
				if(armour.states==3)
				{
					res.setS2CCode(FAIL);
					res.setS2CMsg(LangService.getValue("ACHIEVEMENT_ACTIVATED"));
					return;
				}
				
				player.achievementManager.activateHolyArmour(id);
				
				
				
			    
			    
			    res.setS2CCode(OK);
				body.writeBytes(res.build().toByteArray());
			}
			
		};
	}
}