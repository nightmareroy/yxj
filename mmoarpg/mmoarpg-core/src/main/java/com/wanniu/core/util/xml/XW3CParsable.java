package com.wanniu.core.util.xml;

import org.w3c.dom.Node;

/**
 * 一个统一的，提供解析方法的接口，解析方法可以自定参数 实现此接口表示可以放入统一解析
 * @author agui
 */
public interface XW3CParsable {
	
	boolean parse(Node xmlBean);
}
