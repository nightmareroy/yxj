package com.wanniu.game.chat.command;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 标识GM命令的注解.
 * 
 * @author 小流氓(zhoumingkai@qeng.cn)
 */
@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Command {
	String[] value();
}