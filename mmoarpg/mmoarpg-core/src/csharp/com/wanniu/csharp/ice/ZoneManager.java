package com.wanniu.csharp.ice;

import com.wanniu.core.GGame;
import com.wanniu.core.logfs.Out;
import com.wanniu.csharp.CSharpNode;

import Ice.Current;
import Ice.ObjectAdapter;
import Ice.ObjectPrx;
import Pomelo.ZoneManagerPrx;
import Pomelo.ZoneManagerPrxHelper;
import Pomelo._ZoneManagerCallbackDisp;

public class ZoneManager {
	
	private CSharpNode node;
	private ZoneManagerPrx zoneManager;
	private Ice.Communicator ic;
	
	public ZoneManager(CSharpNode node) {
		this.node = node;
	}
	
	public ZoneManager bind() {
		try {
			ic = Ice.Util.initialize(new String[]{});
			Ice.ObjectPrx proxy = ic.stringToProxy(String.format("zoneManager:default -h %s -p %d -t 3000", node.host, node.icePort)).ice_twoway().ice_secure(false);
			zoneManager = ZoneManagerPrxHelper.checkedCast(proxy);
			if (zoneManager == null) {
				throw new Error("Invalid proxy");
			}
			ObjectAdapter adapter = ic.createObjectAdapter("");
			ObjectPrx prx = adapter.add(new _ZoneManagerCallbackDisp() {
				private static final long serialVersionUID = 0;

				@Override
				public void eventNotify(String eventType, String msg, Current __current) {
					try {
						GGame.getInstance().battleServerEvent(eventType, msg);
					} catch (Exception e) {
						Out.error(e);
					}
				}
			}, Ice.Util.stringToIdentity(node.getNodeId()));
			proxy.ice_getCachedConnection().setAdapter(adapter);
			zoneManager.setCallback(prx.ice_getIdentity());
		} catch (Exception e) {
			Out.error("ZoneManager bind() exception.", e);
		}
		return this;
	}

	public ZoneManagerPrx getManager() {
		return this.zoneManager;
	}
	
	public void destory() {
		try {
			if (ic != null) {
				ic.destroy();
			}
		} catch (Exception e) {
			Out.error("ZoneManager destory() exception.", e);
		}
	}
	
}
