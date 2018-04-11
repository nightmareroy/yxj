package com.wanniu.game.data.ext;

import java.util.HashMap;
import java.util.Map;

import com.wanniu.core.logfs.Out;
import com.wanniu.core.util.ClassUtil;
import com.wanniu.game.common.Const;
import com.wanniu.game.common.Const.PlayerBtlData;
import com.wanniu.game.data.UpLevelExpCO;

/**
 * @since 2017/1/22 17:00:33
 * @author auto generate
 */
public class UpLevelExpExt extends UpLevelExpCO {

	public int Pro;

	public Map<Const.PlayerBtlData, Integer> attrs;

	@Override
	public void initProperty() {
//		this.Pro = Const.PlayerPro.getV(this.pro);

		attrs = new HashMap<>();
		for (int i = 1; i <= 5; i++) {
			String propName = "prop" + i;
			String maxValue = "max" + i;
			String key;
			try {
				Object obj = ClassUtil.getProperty(this, propName) ;
				if (obj != null) {
					key = (String) obj;
//					key = AttributeUtil.getKeyByName((String) ClassUtil.getProperty(this, propName));
					PlayerBtlData pbd = PlayerBtlData.getE(key);
					if(pbd!=null){
						attrs.put(pbd, (int) ClassUtil.getProperty(this, maxValue));
					}
				}
			} catch (Exception e) {
				Out.error(e);
			}
		}
	}

}