package com.wanniu.game.request.solo;

import java.io.IOException;

import com.wanniu.core.game.entity.GClientEvent;
import com.wanniu.core.game.protocol.PomeloResponse;
import com.wanniu.game.common.Const;
import com.wanniu.game.player.WNPlayer;
import com.wanniu.game.solo.vo.ResultVO;

import pomelo.area.SoloHandler.JoinSoloResponse;

/**
 * 单挑信息请求
 * 
 * @author wfy
 *
 */
@GClientEvent("area.soloHandler.joinSoloRequest")
public class JoinSoloHandler extends SoloRequestFilter {

	public PomeloResponse request(WNPlayer player) throws Exception {
		
		return new PomeloResponse() {
			
			@Override
			protected void write() throws IOException {

				JoinSoloResponse.Builder res = JoinSoloResponse.newBuilder();
			    ResultVO result = player.soloManager.handleJoinSolo();
			    
			    if (result.result) {
//			        TeamUtil.leaveTeamInAreaServer(player);不要求用户退出队伍
			        res.setS2CCode(Const.CODE.OK);
			        res.setS2CAvgMatchTime(result.get(ResultVO.KEY.AVG_MATCHTIME));
			        res.setS2CStartJoinTime(result.get(ResultVO.KEY.START_JOINTIME));
			    } else {
			    	res.setS2CCode(Const.CODE.FAIL);
			    	res.setS2CMsg(result.info);
			    }
				body.writeBytes(res.build().toByteArray());
			}
			
		};
	}
}