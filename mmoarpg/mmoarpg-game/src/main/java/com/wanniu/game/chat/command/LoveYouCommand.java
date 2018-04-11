package com.wanniu.game.chat.command;

import com.wanniu.game.chat.GMChatUtil;
import com.wanniu.game.player.WNPlayer;

/**
 * 把自己变得很牛逼啊.
 *
 * @author 小流氓(zhoumingkai@qeng.cn)
 */
@Command("@gm love you")
public class LoveYouCommand extends AbsCommand {

	@Override
	public String help() {
		return "@gm love you 嘿嘿，你懂的...";
	}

	@Override
	protected String exec(WNPlayer player, String... args) {
		player.baseDataManager.upgrade(60, 0);

		// @gm add diamond 999999999（加元宝）
		GMChatUtil.checkContent(player, "@gm add rmb 88888888");
		// @gm add money 999999999（加绑元）
		GMChatUtil.checkContent(player, "@gm add money 88888888");
		// @gm add gold 999999999（加银两）
		GMChatUtil.checkContent(player, "@gm add gold 88888888");
		// @gm openFunc（开启所有功能）
		GMChatUtil.checkContent(player, "@gm openFunc");
		// @gm add gr10 10（加红玛瑙10级10个，后面2个10分表代表等级和数量）
		GMChatUtil.checkContent(player, "@gm add gr10 10");
		// @gm add gg10 10（加绿玛瑙10级10个，后面2个10分表代表等级和数量）
		GMChatUtil.checkContent(player, "@gm add gg10 10");
		// @gm add gb10 10（加蓝玛瑙10级10个，后面2个10分表代表等级和数量）
		GMChatUtil.checkContent(player, "@gm add gb10 10");
		// @gm add gp10 10（加紫玛瑙10级10个，后面2个10分表代表等级和数量）
		GMChatUtil.checkContent(player, "@gm add gp10 10");
		// @gm add gy10 10（加黄玛瑙10级10个，后面2个10分表代表等级和数量）
		GMChatUtil.checkContent(player, "@gm add gy10 10");

		// 等级满级（目前79）
		// 修为突破满
		// 10个部位强化到90级（这个没有gm命令）
		// （但是穿了无敌屠龙刀 和原始圣甲，结合强化加成后会数值溢出，测试装备失效）
		//
		//
		//
		GMChatUtil.checkContent(player, "@gm add 无敌屠龙刀 1");
		GMChatUtil.checkContent(player, "@gm add 伪原始圣甲 1");
		//
		// 不用屠龙刀的话，就发一套70绿套也可以~有物品代码
		//
		// 获得所有坐骑
		// 坐骑培养到70级
		// 获得所有宠物
		// 所有宠物培养突破到50级

		return "爱我就对了...";
	}
}