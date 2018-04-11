package com.wanniu.game.request.achievement;

import java.io.IOException;

import com.wanniu.core.game.LangService;
import com.wanniu.core.game.entity.GClientEvent;
import com.wanniu.core.game.protocol.PomeloRequest;
import com.wanniu.core.game.protocol.PomeloResponse;
import com.wanniu.game.achievement.po.BaseInfo;
import com.wanniu.game.common.Const;
import com.wanniu.game.data.GameData;
import com.wanniu.game.data.ext.AchievementConfigExt;
import com.wanniu.game.data.ext.AchievementExt;
import com.wanniu.game.player.WNPlayer;

import pomelo.area.AchievementHandler.AchievementGetAwardRequest;
import pomelo.area.AchievementHandler.AchievementGetAwardResponse;

/**
 * 成就 请求奖励
 * 
 * @author wfy
 *
 */
@GClientEvent("area.achievementHandler.achievementGetAwardRequest")
public class AchievementGetAwardHandler extends PomeloRequest {

	public PomeloResponse request() throws Exception {
		WNPlayer player = (WNPlayer) pak.getPlayer();
		
		AchievementGetAwardRequest req = AchievementGetAwardRequest.parseFrom(pak.getRemaingBytes());
		int achieveId = req.getC2SId();
		int _type = req.getC2SType();
		
		return new PomeloResponse() {
			
			@Override
			protected void write() throws IOException {

				AchievementGetAwardResponse.Builder res = AchievementGetAwardResponse.newBuilder();
				
				if (!player.functionOpenManager.isOpen(Const.FunctionType.ACHIEVEMENT.getValue())) {
					res.setS2CCode(Const.CODE.FAIL);
					res.setS2CMsg(LangService.getValue("FUNC_SET_PLAYED_NOT_OPEN"));
					body.writeBytes(res.build().toByteArray());
					return;
				}
				AchievementConfigExt configExt = null;
				if (_type == 0) {
					configExt = GameData.AchievementConfigs.get(achieveId);
				} else if (_type == 1) {
					AchievementExt achievementExt = GameData.Achievements.get(achieveId);
					configExt = GameData.AchievementConfigs.get(achievementExt.chapterID);
				}
				
				if (player.getLevel() < configExt.lv 
						|| (configExt.quest != 0 && !player.taskManager.finishedNormalTasks.containsKey(configExt.quest))) {
					res.setS2CCode(Const.CODE.FAIL);
					res.setS2CMsg(LangService.getValue("ACHIEVE_CHAPTER_NOT_OPEN"));
					body.writeBytes(res.build().toByteArray());
					return;
				}

			    BaseInfo baseInfo = player.achievementManager.getAward(_type, achieveId);
			    if(baseInfo.code == Const.CODE.OK){
			    	res.setS2CCode(baseInfo.code);
			    	player.achievementManager.updateSuperScript();
			    }else{
			    	res.setS2CCode(baseInfo.code);
			    	res.setS2CMsg(baseInfo.msg);
			    }
			    
				body.writeBytes(res.build().toByteArray());
			}
			
		};
	}
}