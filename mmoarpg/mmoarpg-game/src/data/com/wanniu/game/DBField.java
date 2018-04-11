package com.wanniu.game;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author agui
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface DBField {
	
	boolean include() default true;		// 是否要存表
	boolean isPKey() default false;		// 是否是主键
	String fieldType() default "";		// 字段类型
	int size() default 0;				// 字段长度
	String comment() default "";		// 字段描述
	
}