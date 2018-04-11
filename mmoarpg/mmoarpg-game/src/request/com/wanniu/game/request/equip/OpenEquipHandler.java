package com.wanniu.game.request.equip;

import java.io.IOException;

import com.wanniu.core.game.LangService;
import com.wanniu.core.game.entity.GClientEvent;
import com.wanniu.core.game.protocol.PomeloRequest;
import com.wanniu.core.game.protocol.PomeloResponse;
import com.wanniu.core.logfs.Out;
import com.wanniu.game.common.Const;
import com.wanniu.game.common.msg.ErrorResponse;
import com.wanniu.game.equip.EquipManager;
import com.wanniu.game.equip.RedPointBean;
import com.wanniu.game.player.WNPlayer;
import com.wanniu.game.poes.RedPointPO;

import pomelo.area.EquipHandler.OpenEquipHandlerRequest;
import pomelo.area.EquipHandler.OpenEquipHandlerResponse;

/**
 * 打开装备界面(取消红点)
 *
 * @author Feiling(feiling@qeng.cn)
 */
@GClientEvent("area.equipHandler.openEquipHandlerRequest")
public class OpenEquipHandler extends PomeloRequest {
	

	public PomeloResponse request() throws Exception {
		WNPlayer player = (WNPlayer) pak.getPlayer();
		EquipManager equipManager = player.equipManager;

		OpenEquipHandlerRequest req = OpenEquipHandlerRequest.parseFrom(pak.getRemaingBytes());
		int code = req.getCode();
//		RedPointPO redPoint = equipManager.getAndCheckUpdateRedPointPO(player.getId());		
//		RedPointBean bean = equipManager.findRedPointBean(redPoint.list,code);
//		if(bean != null) {
//			int number = equipManager.getMakePoint();
//			if(number > 0) {//说明可以亮红点了
//				bean.point = 1;
//				equipManager.pushScripts();
//			}
//		}
		Out.debug("opennnnnnnnnnnnnn  ",code);
		Const.SUPERSCRIPT_TYPE type=Const.SUPERSCRIPT_TYPE.getType(code);
		if(type==null)
		{
			return new ErrorResponse(LangService.getValue("PARAMETER ERROR"));
		}
		switch (type) {
		case REBORN:
			if(player.equipManager.getRebornPoint()>0)
			{
				player.playerBasePO.openRebornToday=true;
			}
			break;
		case REBUILD:
			if(player.equipManager.getRebuildPoint()>0)
			{
				player.playerBasePO.openRebuildToday=true;
			}
			break;
		case KAIGUANG:
			if(player.equipManager.getKaiguangPoint()>0)
			{
				player.playerBasePO.openKaiguangToday=true;
			}
			break;

		default:
			return new ErrorResponse(LangService.getValue("PARAMETER ERROR"));
		}
		
		equipManager.pushScripts();
		
		return new PomeloResponse() {
			@Override
			protected void write() throws IOException {
				OpenEquipHandlerResponse.Builder res = OpenEquipHandlerResponse.newBuilder();		
				res.setS2CCode(OK);
				body.writeBytes(res.build().toByteArray());
				return;
			}
		};
	}
	
	
	
}