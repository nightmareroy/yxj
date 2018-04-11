package cn.qeng.gm.api.rpc;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

public class RpcResponse {
	private final long key;
	private final CountDownLatch counter;

	private Map<Integer, Byte> status = new HashMap<>();
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

	public void putStatus(int sid, byte code) {
		status.put(sid, code);
	}

	public Map<Integer, Byte> getStatus() {
		return status;
	}

	@Override
	public String toString() {
		return "RpcResponse [key=" + key + ", status=" + status + ", result=" + result + "]";
	}
}