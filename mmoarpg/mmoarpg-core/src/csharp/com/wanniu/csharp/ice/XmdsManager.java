package com.wanniu.csharp.ice;

import com.wanniu.core.logfs.Out;
import com.wanniu.csharp.CSharpNode;

import Xmds.XmdsManagerPrx;
import Xmds.XmdsManagerPrxHelper;

public class XmdsManager {

	private CSharpNode node;
	private XmdsManagerPrx xmdsManager;
	private Ice.Communicator ic;
	
	public XmdsManager(CSharpNode node) {
		this.node = node;
	}
	
	public XmdsManager bind() {
		try {
			ic = Ice.Util.initialize(new String[] {});
			Ice.ObjectPrx proxy = ic.stringToProxy(String.format("XmdsManager:default -h %s -p %d -t 3000", node.host, node.icePort));
			xmdsManager =  XmdsManagerPrxHelper.checkedCast(proxy);
		} catch (Exception e) {
			Out.error("XmdsManager bind() exception.", e);
		}
		return this;
	}

	public XmdsManagerPrx getManager() {
		return this.xmdsManager;
	}

	public void destory() {
		try {
			if (ic != null) {
				ic.destroy();
			}
		} catch (Exception e) {
			Out.error("XmdsManager destory() exception.", e);
		}
	}
}