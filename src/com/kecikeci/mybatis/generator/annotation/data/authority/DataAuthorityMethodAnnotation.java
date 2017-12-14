package com.kecikeci.mybatis.generator.annotation.data.authority;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 数据权限 相关设定，注解方法
 * 针对 数据权限 进行某些方法级别的权限限定
 * @author alexgaoyh
 *
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface DataAuthorityMethodAnnotation {

}
