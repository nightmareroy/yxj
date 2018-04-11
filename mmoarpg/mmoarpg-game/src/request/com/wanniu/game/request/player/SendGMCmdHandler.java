package com.wanniu.game.request.player;

import java.io.IOException;

import com.wanniu.core.game.LangService;
import com.wanniu.core.game.entity.GClientEvent;
import com.wanniu.core.game.protocol.PomeloRequest;
import com.wanniu.core.game.protocol.PomeloResponse;
import com.wanniu.core.logfs.Out;
import com.wanniu.core.util.StringUtil;
import com.wanniu.game.GWorld;
import com.wanniu.game.common.Const;
import com.wanniu.game.common.msg.ErrorResponse;
import com.wanniu.game.mail.SysMailConst;
import com.wanniu.game.player.WNPlayer;
import com.wanniu.game.prepaid.PrepaidService;

import pomelo.area.PlayerHandler.SendGMCmdRequest;
import pomelo.area.PlayerHandler.SendGMCmdResponse;

/**
 * 压测工具发送GM指令
 * @author agui
 */
@GClientEvent("area.playerHandler.sendGMCmdRequest")
public class SendGMCmdHandler extends PomeloRequest {

	public PomeloResponse request() throws Exception {
		if(GWorld.ROBOT) {
			WNPlayer player = (WNPlayer) pak.getPlayer();
	
			SendGMCmdRequest req = SendGMCmdRequest.parseFrom(pak.getRemaingBytes());
		    String cmd = req.getC2SMsg();
		    if(StringUtil.isEmpty(cmd)){
		        return new ErrorResponse(LangService.getValue("DATA_ERR"));
		    }
		    String[] strs = cmd.trim().split(" ");
		    if(strs.length <= 0){
		        return new ErrorResponse(LangService.getValue("DATA_ERR"));
		    }
		    
		    String op = strs[1];
		    Out.debug("sendGMCmdRequest cmd:", op);
		    switch(op){
		        case "add":
		            String code = strs[2];
		            int stackNum = Integer.parseInt(strs[3]);
		            player.bag.addCodeItemMail(code, stackNum, Const.ForceType.DEFAULT, Const.GOODS_CHANGE_TYPE.gm, SysMailConst.TEST_1);
		            break;
		        case "finishTask":
		            int taskId = Integer.parseInt(strs[2]);
		            player.taskManager.gmFinishTask(taskId);
		            break;
		        case "openFunc":
		            player.functionOpenManager.gmOpenFunction(0);
		            break;
		        case "allSkin":
		        	player.mountManager.addAllSkin();
		        	break;
		        case "unlock":
		        	int num = Integer.parseInt(strs[2]);
		        	player.getWnBag().addBagGridCount(num);
		        	break;
		        case "paySuccess":
		        	String orderId = strs[2];
		        	PrepaidService.getInstance().onPaySuccess(orderId);
		        	break;
		        default :
		            Out.error("not exists cmd : ", cmd);
		    }
		}

		return new PomeloResponse() {
			@Override
			protected void write() throws IOException {
				SendGMCmdResponse.Builder res = SendGMCmdResponse.newBuilder();
				res.setS2CCode(OK);
				body.writeBytes(res.build().toByteArray());
			}
		};
	}
	
}