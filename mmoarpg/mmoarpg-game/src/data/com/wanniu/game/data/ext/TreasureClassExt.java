package com.wanniu.game.data.ext;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.wanniu.core.logfs.Out;
import com.wanniu.core.util.ClassUtil;
import com.wanniu.core.util.StringUtil;
import com.wanniu.game.data.TreasureClassCO;
import com.wanniu.game.item.data.tc.TCItemData;

/**
 * @since 2017/2/4 15:11:28
 * @author auto generate
 */
public class TreasureClassExt extends TreasureClassCO {

	public List<TCItemData> items;

	/**
	 * 填写道具代码或者TC代码
		【特别约定】
		所有道具代码全都以【小写的字母开头】
		所有TC代码全都以【大写字母或者数字开头】
		所有TC代码全都是【中文】，表示装备类型
		
		1、当填写道具代码时：直接读取相关的脚本生成对应道具。本子段可填写数量以及随机数量范围。
		
		举例：
		hp1#10，表示掉落10个hp1
		hp1#{1,10}，表示随机掉落1-10个hp1
		
		
		2、填写TC名称：需要执行该TC，并将该TC掉落与本TC掉落物品合并后生成总掉落
		
		20161017朱晶晶添加：
		怪物掉落金币和经验时特殊处理，走公式计算。
	 */
	@Override
	public void initProperty() {
		items = new ArrayList<>();
		for (int i = 1; i <= 20; i++) {
			Object _item = null;
			Object _rare = null;
			try {
				_item = ClassUtil.getProperty(this, "item" + i);
				_rare = ClassUtil.getProperty(this, "prob" + i);
			} catch (Exception e) {
				Out.error(e);
			}
			String item = null;
			int rare = 0;
			if (_item != null) {
				item = _item.toString();
				if (item.trim().length() == 0)
					continue;
				rare = (int) _rare;

				String[] itemData = item.split("#");
				String itemCode = itemData[0];
				int num = 1;
				List<Integer> expandParas = new ArrayList<>();
				if (itemData.length > 1) {
					// itemCode#num 掉落
					if (StringUtil.isNotEmpty(itemData[1])) {
						String[] expandParaStrs = itemData[1].split(",");
						for (String expandParaStr : expandParaStrs) {
							expandParas.add(Integer.parseInt(expandParaStr.replace("{", "").replace("}", "")));
						}
					}
					if (itemData[1].substring(0, 1).equals("{")) {
						String[] numData = itemData[1].substring(1, itemData[1].length() - 1).split(",");
						int minNum = Integer.parseInt(numData[0]);
						int maxNum = Integer.parseInt(numData[1]);

						TCItemData tc = new TCItemData();
						tc.tcType = TCItemData.TC_ITEMCODE;
						tc.code = itemCode;
						tc.minNum = minNum;
						tc.maxNum = maxNum;
						tc.rare = rare;
						tc.expandParas = expandParas;
						items.add(tc);
					} else {
						num = Integer.parseInt(itemData[1]);
						TCItemData tc = new TCItemData();
						tc.tcType = TCItemData.TC_ITEMCODE;
						tc.code = itemCode;
						tc.num = num;
						tc.rare = rare;
						tc.expandParas = expandParas;
						items.add(tc);
					}
				} else {
					// 嵌套tc 掉落
					TCItemData tc = new TCItemData();
					Matcher matcher_tc = Pattern.compile("^[0-9A-Z]").matcher(itemCode);
					Matcher matcher_item = Pattern.compile("^[a-z]").matcher(itemCode);
					if(matcher_tc.find()) {
						tc.tcType = TCItemData.TC_INNER_TC;
					} else if(matcher_item.find()) {
						tc.tcType = TCItemData.TC_ITEMCODE;
					} else {
						tc.tcType = TCItemData.TC_EQUIP_TYPE;
					}
					
					tc.code = itemCode;
					tc.num = num;
					tc.rare = rare;
					tc.expandParas = expandParas;
					items.add(tc);
				}
			}
		}
	}

	@Override
	public String getKey() {
		return this.tcCode;
	}

}