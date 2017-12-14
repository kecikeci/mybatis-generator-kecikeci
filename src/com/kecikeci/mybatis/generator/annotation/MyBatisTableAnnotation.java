package com.kecikeci.mybatis.generator.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * MyBatis 自定义的类级别的注解，对应数据库的实体表名 相当于 JPA 的 table 注解
 * name值对应数据库的表名子弹
 * @author alexgaoyh
 *
 */
@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface MyBatisTableAnnotation {

	/**
	 * 表名，真實名稱
	 * @return
	 */
	String name() default "";
	
	/**
	 * 表別名，別名
	 * @return
	 */
	String aliasName() default "";
	
	/**
	 * 命名空间，同表名
	 * @return
	 */
	String namespace() default "";
	
	/**
	 * 表名 备注
	 * @return
	 */
	String remarks() default "";
}
