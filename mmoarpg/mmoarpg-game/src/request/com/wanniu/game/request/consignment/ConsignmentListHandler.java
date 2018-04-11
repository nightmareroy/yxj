package com.wanniu.game.request.consignment;

import java.io.IOException;

import com.wanniu.core.game.entity.GClientEvent;
import com.wanniu.core.game.protocol.PomeloRequest;
import com.wanniu.core.game.protocol.PomeloResponse;
import com.wanniu.game.GWorld;
import com.wanniu.game.common.Const.TaskType;
import com.wanniu.game.consignmentShop.ConsignmentLineService;
import com.wanniu.game.consignmentShop.ConsignmentLineService.ConsignmentQueryParam;
import com.wanniu.game.player.WNPlayer;

import pomelo.area.ConsignmentLineHandler.ConsignmentListRequest;
import pomelo.area.ConsignmentLineHandler.ConsignmentListResponse;

@GClientEvent("area.consignmentLineHandler.consignmentListRequest")
public class ConsignmentListHandler extends PomeloRequest {

	@Override
	public PomeloResponse request() throws Exception {
		WNPlayer player = (WNPlayer) pak.getPlayer();
		ConsignmentListRequest msg = ConsignmentListRequest.parseFrom(pak.getRemaingBytes());
		int pro = msg.getC2SPro();
		int qColor = msg.getC2SQcolor();
		int order = msg.getC2SOrder();
		int itemSecondType = msg.getC2SItemSecondType();
		int page = msg.getC2SPage();
		String itemType = msg.getC2SItemType();
		int level = msg.getC2SLevel();

		ConsignmentQueryParam opts = new ConsignmentQueryParam();
		opts.pro = pro;
		opts.qColor = qColor;
		opts.order = order;
		opts.itemSecondType = itemSecondType;
		opts.page = page;
		opts.itemType = itemType;
		opts.level = level;

		ConsignmentListResponse.Builder result = ConsignmentLineService.getInstance().query(player, GWorld.__SERVER_ID, opts);
		result.setS2CCode(OK);

		player.getPlayerTasks().dealTaskEvent(TaskType.OPEN_SONSIGMENT, 1);

		return new PomeloResponse() {
			@Override
			protected void write() throws IOException {
				body.writeBytes(result.build().toByteArray());
			}
		};
	}
}
