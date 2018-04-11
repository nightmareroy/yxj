package com.wanniu.game.request.solo;

import java.io.IOException;

import com.wanniu.core.game.entity.GClientEvent;
import com.wanniu.core.game.protocol.PomeloResponse;
import com.wanniu.game.common.Const;
import com.wanniu.game.player.WNPlayer;
import com.wanniu.game.solo.vo.ResultVO;

import pomelo.area.SoloHandler.JoinSoloBattleResponse;

/**
 * 单挑信息请求
 * 
 * @author wfy
 *
 */
@GClientEvent("area.soloHandler.joinSoloBattleRequest")
public class JoinSoloBattleHandler extends SoloRequestFilter {

	public PomeloResponse request(WNPlayer player) throws Exception {
		
		return new PomeloResponse() {
			
			@Override
			protected void write() throws IOException {

				JoinSoloBattleResponse.Builder res = JoinSoloBattleResponse.newBuilder();
			    ResultVO result = player.soloManager.handleJoinSoloBattle();
			    
			    if (result.result) {
//			        TeamUtil.leaveTeamInAreaServer(player); 现在不用退队了，只需要取消跟随
			        res.setS2CCode(Const.CODE.OK);
			    } else {
			    	res.setS2CCode(Const.CODE.FAIL);
			    	res.setS2CMsg(result.info);
			    }
				body.writeBytes(res.build().toByteArray());
			}
			
		};
	}
}