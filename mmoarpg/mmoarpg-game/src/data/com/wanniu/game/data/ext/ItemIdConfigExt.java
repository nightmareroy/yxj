package com.wanniu.game.data.ext;

import java.util.ArrayList;
import java.util.List;

import com.wanniu.game.common.Const;
import com.wanniu.game.data.ItemIdConfigCO;

public class ItemIdConfigExt extends ItemIdConfigCO {
	
	private List<OrderRule> orderRules;

	@Override
	public void initProperty() { 
//		orderRules = new ArrayList<>();
//		orderRules.add(new OrderRule("itemOrder", Const.OrderType.Asc.value));
//	    String[] pairList = this.orderRule.split(",");
//	    for(int i = 0; i < pairList.length; ++i){
//	        String pair = pairList[i];
//
//	        String[] rule = pair.split(":");
//	        if(rule.length >= 2){
//	            int key = Integer.parseInt(rule[0]);
//	            String orderKey = rule[1];
//	            int orderType = (Const.OrderType.getE(orderKey) != null) ? Const.OrderType.getE(orderKey).value : Const.OrderType.Asc.value;
//	            if(key == 1){
//	            	orderRules.add(new OrderRule("Qcolor", orderType));
//	            }else if(key == 2){
//	            	orderRules.add(new OrderRule("level", orderType));
//	            }else if(key == 3){
//	            	orderRules.add(new OrderRule("upLevel", orderType));
//	            }else if(key == 4){
//	            	orderRules.add(new OrderRule("gotTime", orderType));
//	            }
//	        }
//	    }
	}
	
	public static class OrderRule {
		public String orderKey;
		public int orderType;
		
		public OrderRule(String orderKey, int orderType) {
			this.orderKey = orderKey;
			this.orderType = orderType;
		}
	}

	@Override
	public String getKey() {
		return super.getKey();
	}
	
	
}
