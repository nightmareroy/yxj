package com.wanniu.game.data.ext;

import java.util.Map;
import java.util.TreeMap;

import com.wanniu.core.logfs.Out;
import com.wanniu.game.common.Const.Position;
import com.wanniu.game.data.MasteryPropCO;
import com.wanniu.game.player.AttributeUtil;

public class MasteryPropExt extends MasteryPropCO {
    public Map<String, Integer> attrs;
    
    public static class MasteryCostItem{
    	public String itemCode;
    	public int itemNum;
    	
    	public MasteryCostItem(String itemCode,int itemNum){
    		this.itemCode = itemCode;
    		this.itemNum = itemNum;
    	}
    }
    
    public MasteryCostItem costs;
    public Position Coords;
    
    public void initProperty(){
    	Map<String, Integer> data = new TreeMap<>();
    	String key = AttributeUtil.getKeyByName(this.prop);
        if(key != null){
        	data.put(key, this.value);
        }
        else{
            Out.error("MasteryLevelProp attrName not exist ",this.prop);
        }
        this.attrs = data;
        MasteryCostItem costs = new MasteryCostItem(this.costItem, this.itemCount);
        this.costs = costs;


        String[] str = this.coord.split(",");
        Position coord = new Position(Integer.parseInt(str[0]), Integer.parseInt(str[1]));
        if(coord.y == 0){
            Out.error("MasteryLevelProp y is null "+this.iD);
        }
        this.Coords = coord;
    }
}
