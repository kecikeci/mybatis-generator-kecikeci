package com.kecikeci.mybatis.generator.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * MyBatis 自定义的列名的注解  
 * 数据库的注释值	数据库的表结构的列名
 * @author alexgaoyh
 *
 */
@Target({ ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface MyBatisColumnAnnotation {

	String name() default "";

	String value() default "";
	
	String tableAlias() default "";
	
	String chineseNote() default "";
}
