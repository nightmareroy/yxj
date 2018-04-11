package com.wanniu.game.data.ext;

import java.util.ArrayList;
import java.util.HashMap;

import com.wanniu.core.logfs.Out;
import com.wanniu.core.util.StringUtil;
import com.wanniu.game.bag.WNBag.SimpleItemInfo;
import com.wanniu.game.common.Const;
import com.wanniu.game.common.Const.ForceType;
import com.wanniu.game.data.ActivityConfigCO;

public class ActivityConfigExt extends ActivityConfigCO{
	public HashMap<Integer, ArrayList<SimpleItemInfo>> costItems;
	public HashMap<Integer, ArrayList<SimpleItemInfo>> RankReward;
	public int indexDay;
	public String condition;

	@Override
	public void initProperty() {
		RankReward = new HashMap<>();
		costItems = new HashMap<>();
		if (this.mailID==0) {
			Out.error("ActivityConfig`s MailID == 0 ID:", this.id);
		}
		if (this.extend1!=null) {
			this.costItems.put(0, this.parseString(this.extend1))  ;
		}
		if( this.itemCode!=null ) {
	        this.RankReward.put(0, this.parseString(this.itemCode));
	    }else{
	    	this.RankReward.put(1, this.parseString(this.zSItemCode));
	    	this.RankReward.put(2, this.parseString(this.cKItemCode));
	    	this.RankReward.put(3, this.parseString(this.fSItemCode));
	    	this.RankReward.put(4, this.parseString(this.lRItemCode));
	    	this.RankReward.put(5, this.parseString(this.mSItemCode));
	    }
		
		if(StringUtil.isEmpty(this.notes1)) {
			return;
		}
		String[] notes = this.notes1.split("_");
		if(notes.length > 2){
			String indexDay = notes[1];
			if(indexDay.equals("OneDay")){
                this.indexDay = 1;
                this.condition = notes[2];
            }else if(indexDay.equals("TwoDay")){
                this.indexDay = 2;
                this.condition = notes[2];
            }else if(indexDay.equals("ThreeDay")){
                this.indexDay = 3;
                this.condition = notes[2];
            }else if(indexDay.equals("FourDay")){
                this.indexDay = 4;
                this.condition = notes[2];
            }else if(indexDay.equals("FiveDay")){
                this.indexDay = 5;
                this.condition = notes[2];
            }else if(indexDay.equals("SixDay")){
                this.indexDay = 6;
                this.condition = notes[2];
            }else if(indexDay.equals("SevenDay")){
                this.indexDay = 7;
                this.condition = notes[2];
            }
		}
		
	}
	
	/*

	activityConfigProp.prototype.initProperty = function() {
	    if (!this.MailID) {
	        logger.error('ActivityConfig`s MailID == 0 ID:', this.Id);
	    }

	    this.RankReward = {};
	    this.costItems = {};

	    if (this.Extend1) {
	        this.costItems[0] = this.parseString(this.Extend1);
	    }

	    if( this.ItemCode ) {

	        this.RankReward[0] = this.parseString(this.ItemCode);
	    }
	    else {
	        this.RankReward[1] = this.parseString(this.ZSItemCode);
	        this.RankReward[2] = this.parseString(this.CKItemCode);
	        this.RankReward[3] = this.parseString(this.FSItemCode);
	        this.RankReward[4] = this.parseString(this.LRItemCode);
	        this.RankReward[5] = this.parseString(this.MSItemCode);
	    }
	    //if(this.Type === consts.ActivityRewardType.OPEN_SEVEN_DAY){
	        var notes = this.Notes1.split('_');
	        if(notes.length > 2){
	            var indexDay = notes[1];
	            //console.log('openSevenDayIndexDay:', indexDay);
	            if(indexDay === 'OneDay'){
	                this.indexDay = 1;
	                this.condition = notes[2];
	            }else if(indexDay === 'TwoDay'){
	                this.indexDay = 2;
	                this.condition = notes[2];
	            }
	            else if(indexDay === 'ThreeDay'){
	                this.indexDay = 3;
	                this.condition = notes[2];
	            }
	            else if(indexDay === 'FourDay'){
	                this.indexDay = 4;
	                this.condition = notes[2];
	            }
	            else if(indexDay === 'FiveDay'){
	                this.indexDay = 5;
	                this.condition = notes[2];
	            }
	            else if(indexDay === 'SixDay'){
	                this.indexDay = 6;
	                this.condition = notes[2];
	            }
	            else if(indexDay === 'SevenDay'){
	                this.indexDay = 7;
	                this.condition = notes[2];
	            }
	            //console.log('openSevenDay:', this.indexDay);
	        }
	    //}

	};
	*/
	public static class ActivityConfigItem{
		public String itemCode;
		public int itemNum;
		public ForceType isBind; 
	}

	
	public ArrayList<SimpleItemInfo> parseString(String itemCode){
		ArrayList<SimpleItemInfo> RankReward = new ArrayList<>();
		if(StringUtil.isEmpty(itemCode)) {
			return RankReward;
		}
		String[] rewards = itemCode.split(";");
		for(String ss:rewards){
			String [] rw = ss.split(":");
			if(rw.length == 2){
				SimpleItemInfo item = new SimpleItemInfo();
				item.itemCode = rw[0];
				item.itemNum = Integer.parseInt(rw[1]);
				item.forceType  = Const.ForceType.BIND;
				RankReward.add(item);
			}
		}
		return RankReward;
	}
	/*
	activityConfigProp.prototype.parseString = function(ItemCode) {

	var RankReward = [];

	var rewards = ItemCode.split(';');

	for (var i in rewards) {

	    var rw = rewards[i].split(':');

	    if( rw.length === 2 ) {

	        RankReward.push({

	            itemCode:rw[0],

	            itemNum:Number(rw[1]),

	            isBind: consts.ForceType.BIND//策划要求活动强制绑定 2016/10/14
	        });
	    }
	}

	return RankReward;
	}
	*/
}














