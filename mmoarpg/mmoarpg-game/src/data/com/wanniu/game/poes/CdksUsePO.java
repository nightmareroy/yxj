package com.wanniu.game.poes;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * CDK使用记录
 * 
 * @author lxm
 *
 */
public class CdksUsePO {

	public List<CdkUse> listCdk = new ArrayList<>();

	public CdksUsePO() {

	}

	public static class CdkUse {
		public String cdk;

		public Date useDate;

		public int channel;
		
		public CdkUse() {
			
		}

		public CdkUse(String cdk, Date useDate, int channel) {
			this.cdk = cdk;
			this.useDate = useDate;
			this.channel = channel;
		}
	}
}
