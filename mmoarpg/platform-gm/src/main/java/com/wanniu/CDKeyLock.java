package com.wanniu;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

public class CDKeyLock {

	private Lock lock;
	private Condition condition;
	private boolean ok;
	private byte[] data;
	
	public CDKeyLock(Lock lock, Condition condition) {
		this.lock = lock;
		this.condition = condition;
	}

	public Lock getLock() {
		return lock;
	}

	public Condition getCondition() {
		return condition;
	}

	public void setOk(boolean ok) {
		this.ok = ok;
	}
	
	public boolean isOk() {
		return ok;
	}
	
	public byte[] getData() {
		return data;
	}

	public void setData(byte[] data) {
		this.data = data;
	}
	

}
