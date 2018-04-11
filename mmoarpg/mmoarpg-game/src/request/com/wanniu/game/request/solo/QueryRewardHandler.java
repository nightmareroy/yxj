package com.wanniu.game.request.solo;

import java.io.IOException;

import com.wanniu.core.game.entity.GClientEvent;
import com.wanniu.core.game.protocol.PomeloResponse;
import com.wanniu.game.common.Const;
import com.wanniu.game.player.WNPlayer;
import com.wanniu.game.solo.vo.ResultVO;

import pomelo.area.SoloHandler.QueryRewardResponse;

/**
 * 单挑信息请求
 * 
 * @author wfy
 *
 */
@GClientEvent("area.soloHandler.queryRewardRequest")
public class QueryRewardHandler extends SoloRequestFilter {

	public PomeloResponse request(WNPlayer player) throws Exception {
		
		return new PomeloResponse() {
			
			@Override
			protected void write() throws IOException {

				QueryRewardResponse.Builder res = QueryRewardResponse.newBuilder();
			    ResultVO result = player.soloManager.handleQueryReward();
			    res.setS2CCode(Const.CODE.OK);
			    res.setS2CHasReward(result.get(ResultVO.KEY.HAS_REWARD));
			    
				body.writeBytes(res.build().toByteArray());
			}
			
		};
	}
}