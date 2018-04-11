package cn.qeng.paycenter.api.rpc;

import java.util.concurrent.CountDownLatch;

public class RpcResponse {
	private final long key;
	private final CountDownLatch counter;
	private String result;

	public RpcResponse(long key) {
		this.key = key;
		this.counter = new CountDownLatch(1);
	}

	public long getKey() {
		return key;
	}

	public String getResult() {
		return result;
	}

	public void setResult(String result) {
		this.result = result;
	}

	public CountDownLatch getCounter() {
		return counter;
	}

	@Override
	public String toString() {
		return "RpcResponse [key=" + key + ", counter=" + counter + ", result=" + result + "]";
	}
}