/*
 * Copyright © 2016 qeng.cn All Rights Reserved.
 * 
 * 感谢您加入清源科技，不用多久，您就会升职加薪、当上总经理、出任CEO、迎娶白富美、从此走上人生巅峰
 * 除非符合本公司的商业许可协议，否则不得使用或传播此源码，您可以下载许可协议文件：
 * 
 * 		http://www.noark.xyz/qeng/LICENSE
 *
 * 1、未经许可，任何公司及个人不得以任何方式或理由来修改、使用或传播此源码;
 * 2、禁止在本源码或其他相关源码的基础上发展任何派生版本、修改版本或第三方版本;
 * 3、无论你对源代码做出任何修改和优化，版权都归清源科技所有，我们将保留所有权利;
 * 4、凡侵犯清源科技相关版权或著作权等知识产权者，必依法追究其法律责任，特此郑重法律声明！
 */
package cn.qeng.gm.core;

import java.io.PrintWriter;
import java.io.StringWriter;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.context.request.async.AsyncRequestTimeoutException;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.NoHandlerFoundException;

/**
 * 全局异常处理
 *
 * @author 小流氓(176543888@qq.com)
 */
@ControllerAdvice
public class ExceptionHandlerAdvice {
	private final static Logger logger = LogManager.getLogger(ExceptionHandlerAdvice.class);

	/**
	 * 404
	 */
	@ResponseStatus(HttpStatus.NOT_FOUND)
	@ExceptionHandler(NoHandlerFoundException.class)
	public String handle(NoHandlerFoundException ex) {
		return "404";
	}

	/**
	 * 所有异常都去500
	 */
	@ExceptionHandler(value = Exception.class)
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	public ModelAndView exception(Exception exception, WebRequest request) {
		logger.error("exception.", exception);
		ModelAndView modelAndView = new ModelAndView("500");
		StringWriter sw = new StringWriter();
		exception.printStackTrace(new PrintWriter(sw));
		modelAndView.addObject("errorMessage", sw.toString().replaceAll("\n", "<br>").replaceAll("\\t", "________"));
		return modelAndView;
	}

	/**
	 * Comat超时
	 */
	@ResponseBody
	@ExceptionHandler(value = AsyncRequestTimeoutException.class)
	public String asyncRequestTimeoutException(Exception exception, WebRequest request) {
		return "";
	}
}