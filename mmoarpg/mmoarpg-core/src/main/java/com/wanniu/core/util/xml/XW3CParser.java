package com.wanniu.core.util.xml;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.wanniu.core.GGlobal;
import com.wanniu.core.logfs.Out;

/**
 * 基于w3c的dom解析器 ,解析指定的文件
 * @author agui
 */
public final class XW3CParser {
	
	private XW3CParser() {
	}

	/**
	 * 返回xml文档的根元素
	 */
	private static Element getRootElement(String xmlFile) throws ParserConfigurationException,
			SAXException, IOException {
		DocumentBuilderFactory domfac = DocumentBuilderFactory.newInstance();
		DocumentBuilder dombuilder;
		InputStream is = null;
		try {
			dombuilder = domfac.newDocumentBuilder();
			is = new FileInputStream(xmlFile);
			Document doc = dombuilder.parse(is);
			return doc.getDocumentElement();
		} finally {
			if (is != null) {
				try {
					is.close();
				} catch (IOException e) {
					Out.error("XW3CParser getRootElement", e);
				}
			}
		}
	}

	/**
	 * 用给定的bean解析每一个bean节点
	 */
	public static <T extends XW3CParsable> List<T> parse(String xmlFile, Class<T> clas)
			throws Exception {
		ArrayList<T> beans = new ArrayList<T>();
		Element root = getRootElement(xmlFile);

		NodeList nhosts = root.getChildNodes();

		if (nhosts != null) {
			for (int i = 0; i < nhosts.getLength(); i++) {
				T t = clas.newInstance();

				if (t.parse(nhosts.item(i))) {
					beans.add(t);
				}
			}
		}

		return beans;
	}
	
	/**
	 * 构造bean
	 */
	public static void parse(String xmlFile, XW3CParsable parser) throws Exception {
		Element root = getRootElement(xmlFile);

		NodeList nhosts = root.getChildNodes();

		if (nhosts != null) {
			for (int i = 0; i < nhosts.getLength(); i++) {
				parser.parse(nhosts.item(i));
			}
		}
	}
	

	public static void parseText(String text, XW3CParsable parser) throws Exception {
		DocumentBuilderFactory domfac = DocumentBuilderFactory.newInstance();
		DocumentBuilder dombuilder;
		ByteArrayInputStream is = null;
		Element root = null;
		try {
			dombuilder = domfac.newDocumentBuilder();
			is = new ByteArrayInputStream(text.getBytes(GGlobal.UTF_8));
			Document doc = dombuilder.parse(is);
			root = doc.getDocumentElement();
		} finally {
			if (is != null) {
				try {
					is.close();
				} catch (IOException e) {
					Out.error("XW3CParser parseText", e);
				}
			}
		}

		NodeList nhosts = root.getChildNodes();

		if (nhosts != null) {
			for (int i = 0; i < nhosts.getLength(); i++) {
				parser.parse(nhosts.item(i));
			}
		}
	}
	
	
}
