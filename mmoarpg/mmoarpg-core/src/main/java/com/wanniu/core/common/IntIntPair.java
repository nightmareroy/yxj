package com.wanniu.core.common;

/** int2int模版 */
public class IntIntPair implements java.io.Serializable {
	private static final long serialVersionUID = 1L;

	public int first;
	public int second;

	public IntIntPair() {

	}

	public IntIntPair(int first, int second) {
		this.first = first;
		this.second = second;
	}
}
