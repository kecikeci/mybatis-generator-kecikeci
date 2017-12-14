package com.kecikeci.mybatis.generator.plugin;

import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.PluginAdapter;
import org.mybatis.generator.api.dom.java.TopLevelClass;
import org.mybatis.generator.internal.util.StringUtility;

import java.util.List;

/**
 * 生成实体类时，对整个类增加 Table 的注解部分
 * @author alexgaoyh
 *
 */
public class MyTableAnnotationPlugin extends PluginAdapter {

	/**
	 * 生成基础实体类
	 * 
	 * @param topLevelClass
	 * @param introspectedTable
	 * @return
	 */
	@Override
	public boolean modelBaseRecordClassGenerated(TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {
		processEntityClass(topLevelClass, introspectedTable);
		return true;
	}
	
	/**
	 * 处理实体类的包和@Table注解
	 *
	 * @param topLevelClass
	 * @param introspectedTable
	 */
	private void processEntityClass(TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {
	}

	@Override
	public boolean validate(List<String> warnings) {
		return true;
	}
}
