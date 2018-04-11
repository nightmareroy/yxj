package com.wanniu.game.data.ext;

import java.util.ArrayList;

import com.wanniu.core.util.StringUtil;
import com.wanniu.game.data.DrawCO;

public class DrawExt extends DrawCO { 

	public ArrayList<DrawItem> items;
	public int totalRate;

	/** 属性构造 */
	public void initProperty() { 
		this.items = new ArrayList<>();
	    this.totalRate = 0;

	    if(StringUtil.isEmpty(this.propLibrary)) {
	    	return;
	    }
	    String[] items = this.propLibrary.split(";");

	    for (String ss : items) {

	        String[] rw = ss.split(":");

        	DrawItem item = new DrawItem();
	        if( rw.length == 3 ) {

	        	
	        	item.itemCode = rw[0];
	        	item.itemNum = Integer.parseInt(rw[1]);
	        	item.itemRate = Integer.parseInt(rw[2]);

	            this.totalRate += item.itemRate;
	        }
	        else if( rw.length == 2 ) {

	        	item.itemCode = rw[0];
	        	item.itemNum = Integer.parseInt(rw[1]);
	        }
	        else if( rw.length == 1 ) {

	        	item.itemCode = rw[0];
	        }
	        this.items.add(item);
	    }
	}

	public static class DrawItem{
		public String itemCode;
		public int itemNum;
		public int itemRate;
	}
}