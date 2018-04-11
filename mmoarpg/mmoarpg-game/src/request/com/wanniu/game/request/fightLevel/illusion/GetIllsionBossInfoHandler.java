package com.wanniu.game.request.fightLevel.illusion;

import java.io.IOException;
import java.util.Date;

import com.wanniu.core.game.entity.GClientEvent;
import com.wanniu.core.game.protocol.PomeloResponse;
import com.wanniu.core.util.DateUtil;
import com.wanniu.game.area.AreaUtil;
import com.wanniu.game.common.Const;
import com.wanniu.game.data.GameData;
import com.wanniu.game.data.MonsterRefreshCO;
import com.wanniu.game.player.WNPlayer;
import com.wanniu.game.request.fightLevel.FightLevelLine;

import pomelo.area.FightLevelHandler.GetLllsionBossInfoResponse;
import pomelo.area.FightLevelHandler.IllsionBossInfo;

/**
 * 获取幻境-领主界面信息
 * 
 * @author Yangzz
 *
 */
@GClientEvent("area.fightLevelHandler.getLllsionBossInfoRequest")
public class GetIllsionBossInfoHandler extends FightLevelLine {

	@Override
	public PomeloResponse request(WNPlayer player) throws Exception {

//		GetLllsionBossInfoRequest req = GetLllsionBossInfoRequest.parseFrom(pak.getRemaingBytes());

		return new PomeloResponse() {
			@Override
			protected void write() throws IOException {
				GetLllsionBossInfoResponse.Builder res = GetLllsionBossInfoResponse.newBuilder();

				for (MonsterRefreshCO refreshCO : GameData.MonsterRefreshs.values()) {
					Date bornBeginTime = AreaUtil.formatToday(refreshCO.rebornBeginTime);
					Date bornEndTime = AreaUtil.formatToday(refreshCO.rebornEndTime);
					IllsionBossInfo.Builder info = IllsionBossInfo.newBuilder();
					info.setId(refreshCO.iD);
					
					Long nextRefreshTime = 0L;
					
					if (System.currentTimeMillis() < bornBeginTime.getTime()) {
						nextRefreshTime = bornBeginTime.getTime();
					} else if(System.currentTimeMillis() < bornEndTime.getTime()) {
						nextRefreshTime = refreshCO.coolDownTime * Const.Time.Minute.getValue() 
								- (System.currentTimeMillis() - bornBeginTime.getTime()) % (refreshCO.coolDownTime * Const.Time.Minute.getValue())
								+ System.currentTimeMillis();
					} else {
						nextRefreshTime = DateUtil.getDateAfter(bornBeginTime, 1).getTimeInMillis();
					}
					
//					if (nextRefreshTime <= System.currentTimeMillis()) {
//						nextRefreshTime = 0l;
//					}
					Date date = new Date();
					date.setTime(nextRefreshTime);
					info.setNextRefreshTime(nextRefreshTime - System.currentTimeMillis());
					res.addBossInfos(info);
				}

				res.setS2CCode(OK);
				body.writeBytes(res.build().toByteArray());

			}
		};
	}
}