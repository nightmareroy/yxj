package com.wanniu.game.request.activity;

import java.io.IOException;

import com.wanniu.core.game.entity.GClientEvent;
import com.wanniu.core.game.protocol.PomeloRequest;
import com.wanniu.core.game.protocol.PomeloResponse;
import com.wanniu.game.activity.ActivityNoticeService;
import com.wanniu.game.player.WNPlayer;

import pomelo.area.ActivityHandler.ActivityNoticeRes;

/**
 * 获取游戏内公告数据
 * 
 * @author 小流氓(176543888@qq.com)
 */
@GClientEvent("area.activityHandler.activityNoticeRequest")
public class ActivityNoticeHandler extends PomeloRequest {

	@Override
	public PomeloResponse request() throws Exception {
		WNPlayer player = (WNPlayer) pak.getPlayer();
		String context = ActivityNoticeService.getInstance().getNotice(player);

		return new PomeloResponse() {
			protected void write() throws IOException {
				ActivityNoticeRes.Builder res = ActivityNoticeRes.newBuilder();
				res.setS2CCode(OK);
				res.setS2CContext(context);
				body.writeBytes(res.build().toByteArray());
			}
		};
	}

	public static class ContextInfo {
		public String Content;
		public String ReleasePerson;
		public String ReleaseTime;
		public String NoticeTitle;
		public int ID;
		public int isRead;
	}
}