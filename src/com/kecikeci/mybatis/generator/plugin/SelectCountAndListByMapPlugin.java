package com.kecikeci.mybatis.generator.plugin;

import org.mybatis.generator.api.IntrospectedColumn;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.PluginAdapter;
import org.mybatis.generator.api.dom.java.*;
import org.mybatis.generator.api.dom.xml.Attribute;
import org.mybatis.generator.api.dom.xml.Document;
import org.mybatis.generator.api.dom.xml.TextElement;
import org.mybatis.generator.api.dom.xml.XmlElement;
import org.mybatis.generator.codegen.mybatis3.MyBatis3FormattingUtilities;

import java.util.List;

public class SelectCountAndListByMapPlugin extends PluginAdapter {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean validate(List<String> warnings) {
		return true;
	}
	
	@Override
	public boolean clientInsertMethodGenerated(Method method, Interface interfaze,
			IntrospectedTable introspectedTable) {
		interfaze.addJavaDocLine("/**");
		interfaze.addJavaDocLine(" * http://4xx.me");
		interfaze.addJavaDocLine(" * By 九羲公子");
		interfaze.addJavaDocLine(" */");
		interfaze.addMethod(generateSelectCountByMap(method, introspectedTable));
		interfaze.addMethod(generateSelectByMap(method, introspectedTable));
		interfaze.addMethod(generateSelectListByMap(method, introspectedTable));
		interfaze.addImportedType(new FullyQualifiedJavaType("java.util.Map"));
		interfaze.addImportedType(new FullyQualifiedJavaType("java.util.List"));
		return true;
	}

	@Override
	public boolean sqlMapDocumentGenerated(Document document, IntrospectedTable introspectedTable) {
		
		//   形.如 表名+别名  ： sys_sysman_resource sys_sysman_resource
		String tableWithAliasName = introspectedTable.getAliasedFullyQualifiedTableNameAtRuntime();//数据库表名  
		
		// 形如 表名   ：  sys_sysman_resource
		String tableName = introspectedTable.getFullyQualifiedTableNameAtRuntime();
		
		String aliasName = tableWithAliasName.replaceFirst(tableName, "");
		
		String dynamicTableJudge = "<if test=\"dynamicTableName != null and dynamicTableName != ''\"> ${dynamicTableName} " + aliasName + " </if>"
				+ "<if test=\"dynamicTableName == null or dynamicTableName == ''\"> " + tableWithAliasName + "</if>";
		
		XmlElement parentElement = document.getRootElement();

		// 产生 selectCountByMap 方法
		XmlElement selectCountByMapElement = new XmlElement("select");
		selectCountByMapElement.addAttribute(new Attribute("id", "selectCountByMap"));
		selectCountByMapElement.addAttribute(new Attribute("parameterType", "map"));
		selectCountByMapElement.addAttribute(new Attribute("resultType", "int"));
		
		
		selectCountByMapElement.addElement(
				new TextElement(
				"select count(*) from  " + dynamicTableJudge + " <include refid=\"Base_Query_Map_Condition\" /> "
				));
		
		parentElement.addElement(selectCountByMapElement);
		// ------------


		// 产生 selectByMap 方法
		XmlElement selectByMapElement = new XmlElement("select");
		selectByMapElement.addAttribute(new Attribute("id", "selectByMap"));
		selectByMapElement.addAttribute(new Attribute("parameterType", "map"));
		selectByMapElement.addAttribute(new Attribute("resultMap", "BaseResultMap"));
		// ----------

		selectByMapElement.addElement(
				new TextElement(
						"select <include refid=\"Base_Column_List\" /> from  " + dynamicTableJudge + " <include refid=\"Base_Query_Map_Condition\" /> "
				));

		parentElement.addElement(selectByMapElement);
		// ------------


		// 产生 selectListByMap 方法
		XmlElement selectListByMapElement = new XmlElement("select");
		selectListByMapElement.addAttribute(new Attribute("id", "selectListByMap"));
		selectListByMapElement.addAttribute(new Attribute("parameterType", "map"));
		selectListByMapElement.addAttribute(new Attribute("resultMap", "BaseResultMap"));
		
		// order by 后缀的部分 的集合 	比如这张表里面主键字段为 id ，则这段代码返回 id desc 字符串
		if(introspectedTable.getPrimaryKeyColumns() == null || introspectedTable.getPrimaryKeyColumns().size() == 0) {
			throw new RuntimeException("无主键！");
		}	
		StringBuilder orderByStr = new StringBuilder();
		orderByStr.setLength(0);
		for (IntrospectedColumn introspectedColumn : introspectedTable
				.getPrimaryKeyColumns()) {
			orderByStr.append(MyBatis3FormattingUtilities
					.getEscapedColumnName(introspectedColumn) + " desc, " );
		}
		orderByStr.delete(orderByStr.length() - 2, orderByStr.length());
		// ----------
		
		selectListByMapElement.addElement(
				new TextElement(
						"select <include refid=\"Base_Column_List\" /> from  " + dynamicTableJudge + " <include refid=\"Base_Query_Map_Condition\" /> "
								+ " order by  " + orderByStr.toString()
						));
		
		parentElement.addElement(selectListByMapElement);
		// ------------
		
		
		
		// 产生  Base_Query_Map_Condition sql 语句
		XmlElement baseQueryMapConditionElement = new XmlElement("sql");
		baseQueryMapConditionElement.addAttribute(new Attribute("id",
				"Base_Query_Map_Condition"));
		
		XmlElement trimElement = new XmlElement("trim");
		trimElement.addAttribute(new Attribute("prefix", "where"));
		trimElement.addAttribute(new Attribute("prefixOverrides", "and | or"));
		
		// alexgaoyh begin equal = 相等部分的 操作
		StringBuilder sbEqual = new StringBuilder();
		for (IntrospectedColumn introspectedColumn : introspectedTable
                .getAllColumns()) {
			XmlElement valuesNotNullElement = new XmlElement("if"); //$NON-NLS-1$
			sbEqual.setLength(0);
			sbEqual.append(introspectedColumn.getJavaProperty());
			sbEqual.append(" != null"); //$NON-NLS-1$
			sbEqual.append(" and "); //$NON-NLS-1$
			sbEqual.append(introspectedColumn.getJavaProperty());
			sbEqual.append(" != ''"); //$NON-NLS-1$
            valuesNotNullElement.addAttribute(new Attribute(
                    "test", sbEqual.toString())); //$NON-NLS-1$

            sbEqual.setLength(0);
            sbEqual.append(" AND ");
            sbEqual.append(MyBatis3FormattingUtilities
                    .getAliasedEscapedColumnName(introspectedColumn));
            sbEqual.append(" = "); //$NON-NLS-1$
            sbEqual.append(MyBatis3FormattingUtilities
                    .getParameterClause(introspectedColumn));
            valuesNotNullElement.addElement(new TextElement(sbEqual.toString()));
            trimElement.addElement(valuesNotNullElement);
		}
		// alexgaoyh end equal = 相等部分的 操作
		
		// alexgaoyh begin greater 大于
//		StringBuilder sbGreater = new StringBuilder();
//		for (IntrospectedColumn introspectedColumn : introspectedTable
//				.getAllColumns()) {
//			XmlElement valuesNotNullElement = new XmlElement("if"); //$NON-NLS-1$
//			sbGreater.setLength(0);
//			sbGreater.append("myGreater_" + introspectedColumn.getJavaProperty());
//			sbGreater.append(" != null"); //$NON-NLS-1$
//			sbGreater.append(" and "); //$NON-NLS-1$
//			sbGreater.append("myGreater_" + introspectedColumn.getJavaProperty());
//			sbGreater.append(" != ''"); //$NON-NLS-1$
//			valuesNotNullElement.addAttribute(new Attribute(
//					"test", sbGreater.toString())); //$NON-NLS-1$
//
//			sbGreater.setLength(0);
//			sbGreater.append(" AND ");
//			sbGreater.append(MyBatis3FormattingUtilities
//					.getAliasedEscapedColumnName(introspectedColumn));
//			sbGreater.append(" &gt; "); //$NON-NLS-1$
//			sbGreater.append(MyBatis3FormattingUtilities
//					.getParameterClause(introspectedColumn, "myGreater_"));
//			valuesNotNullElement.addElement(new TextElement(sbGreater.toString()));
//			trimElement.addElement(valuesNotNullElement);
//		}
//		// alexgaoyh end greater 大于
//
//		// alexgaoyh begin lesser 小于
//		StringBuilder sbLesser = new StringBuilder();
//		for (IntrospectedColumn introspectedColumn : introspectedTable
//				.getAllColumns()) {
//			XmlElement valuesNotNullElement = new XmlElement("if"); //$NON-NLS-1$
//			sbLesser.setLength(0);
//			sbLesser.append("myLesser_" + introspectedColumn.getJavaProperty());
//			sbLesser.append(" != null"); //$NON-NLS-1$
//			sbLesser.append(" and "); //$NON-NLS-1$
//			sbLesser.append("myLesser_" + introspectedColumn.getJavaProperty());
//			sbLesser.append(" != ''"); //$NON-NLS-1$
//			valuesNotNullElement.addAttribute(new Attribute(
//					"test", sbLesser.toString())); //$NON-NLS-1$
//
//			sbLesser.setLength(0);
//			sbLesser.append(" AND ");
//			sbLesser.append(MyBatis3FormattingUtilities
//					.getAliasedEscapedColumnName(introspectedColumn));
//			sbLesser.append(" &lt; "); //$NON-NLS-1$
//			sbLesser.append(MyBatis3FormattingUtilities
//					.getParameterClause(introspectedColumn, "myLesser_"));
//			valuesNotNullElement.addElement(new TextElement(sbLesser.toString()));
//			trimElement.addElement(valuesNotNullElement);
//		}
		// alexgaoyh end lesser 大于
		
		// alexgaoyh begin like 相似部分的 操作-前后完全匹配相似
		StringBuilder sblike = new StringBuilder();
		for (IntrospectedColumn introspectedColumn : introspectedTable
                .getAllColumns()) {
			XmlElement valuesNotNullElement = new XmlElement("if"); //$NON-NLS-1$
			sblike.setLength(0);
			sblike.append("myLike_" + introspectedColumn.getJavaProperty());
			sblike.append(" != null"); //$NON-NLS-1$
			sblike.append(" and "); //$NON-NLS-1$
			sblike.append("myLike_" + introspectedColumn.getJavaProperty());
			sblike.append(" != ''"); //$NON-NLS-1$
            valuesNotNullElement.addAttribute(new Attribute(
                    "test", sblike.toString())); //$NON-NLS-1$

            sblike.setLength(0);
            sblike.append(" AND ");
            sblike.append(MyBatis3FormattingUtilities
                    .getAliasedEscapedColumnName(introspectedColumn));
            sblike.append(" like "); //$NON-NLS-1$
            sblike.append("CONCAT('%', " + MyBatis3FormattingUtilities
                    .getParameterClause(introspectedColumn, "myLike_") + ",'%' )");
            valuesNotNullElement.addElement(new TextElement(sblike.toString()));
            trimElement.addElement(valuesNotNullElement);
		}
		// alexgaoyh end like 相似部分的 操作-前后完全匹配相似
		
		// alexgaoyh begin like 相似部分的 操作-前端匹配
		StringBuilder sblikeStart = new StringBuilder();
		for (IntrospectedColumn introspectedColumn : introspectedTable
				.getAllColumns()) {
			XmlElement valuesNotNullElement = new XmlElement("if"); //$NON-NLS-1$
			sblikeStart.setLength(0);
			sblikeStart.append("myLikeStart_" + introspectedColumn.getJavaProperty());
			sblikeStart.append(" != null"); //$NON-NLS-1$
			sblikeStart.append(" and "); //$NON-NLS-1$
			sblikeStart.append("myLikeStart_" + introspectedColumn.getJavaProperty());
			sblikeStart.append(" != ''"); //$NON-NLS-1$
			valuesNotNullElement.addAttribute(new Attribute(
					"test", sblikeStart.toString())); //$NON-NLS-1$

			sblikeStart.setLength(0);
			sblikeStart.append(" AND ");
			sblikeStart.append(MyBatis3FormattingUtilities
					.getAliasedEscapedColumnName(introspectedColumn));
			sblikeStart.append(" like "); //$NON-NLS-1$
			sblikeStart.append("CONCAT('%', " + MyBatis3FormattingUtilities
					.getParameterClause(introspectedColumn, "myLikeStart_") + ")");
			valuesNotNullElement.addElement(new TextElement(sblikeStart.toString()));
			trimElement.addElement(valuesNotNullElement);
		}
		// alexgaoyh end like 相似部分的 操作-前端匹配
		
		// alexgaoyh begin like 相似部分的 操作-后端匹配
		StringBuilder sblikeEnd = new StringBuilder();
		for (IntrospectedColumn introspectedColumn : introspectedTable
				.getAllColumns()) {
			XmlElement valuesNotNullElement = new XmlElement("if"); //$NON-NLS-1$
			sblikeEnd.setLength(0);
			sblikeEnd.append("myLikeEnd_" + introspectedColumn.getJavaProperty());
			sblikeEnd.append(" != null"); //$NON-NLS-1$
			sblikeEnd.append(" and "); //$NON-NLS-1$
			sblikeEnd.append("myLikeEnd_" + introspectedColumn.getJavaProperty());
			sblikeEnd.append(" != ''"); //$NON-NLS-1$
			valuesNotNullElement.addAttribute(new Attribute(
					"test", sblikeEnd.toString())); //$NON-NLS-1$

			sblikeEnd.setLength(0);
			sblikeEnd.append(" AND ");
			sblikeEnd.append(MyBatis3FormattingUtilities
					.getAliasedEscapedColumnName(introspectedColumn));
			sblikeEnd.append(" like "); //$NON-NLS-1$
			sblikeEnd.append("CONCAT( " + MyBatis3FormattingUtilities
					.getParameterClause(introspectedColumn, "myLikeEnd_") + ",'%' )");
			valuesNotNullElement.addElement(new TextElement(sblikeEnd.toString()));
			trimElement.addElement(valuesNotNullElement);
		}
		// alexgaoyh end like 相似部分的 操作-后端匹配
		
		// alexgaoyh begin in 语句
		StringBuilder sbIn = new StringBuilder();
		for (IntrospectedColumn introspectedColumn : introspectedTable
				.getAllColumns()) {
			XmlElement valuesNotNullElement = new XmlElement("if"); //$NON-NLS-1$
			sbIn.setLength(0);
			sbIn.append("myIn_" + introspectedColumn.getJavaProperty());
			sbIn.append(" != null"); //$NON-NLS-1$
			sbIn.append(" and "); //$NON-NLS-1$
			sbIn.append("myIn_" + introspectedColumn.getJavaProperty());
			sbIn.append(" != ''"); //$NON-NLS-1$
			valuesNotNullElement.addAttribute(new Attribute(
					"test", sbIn.toString())); //$NON-NLS-1$

			sbIn.setLength(0);
			sbIn.append(" AND ");
			sbIn.append(MyBatis3FormattingUtilities
					.getAliasedEscapedColumnName(introspectedColumn));
			sbIn.append(" in "); //$NON-NLS-1$
			sbIn.append("<foreach item=\"item\" index=\"index\" collection=\"" + "myIn_" + introspectedColumn.getJavaProperty() + "\" open=\"(\" separator=\",\" close=\")\"> #{item} </foreach>");
			valuesNotNullElement.addElement(new TextElement(sbIn.toString()));
			trimElement.addElement(valuesNotNullElement);
		}
		// alexgaoyh end in 语句

		
		baseQueryMapConditionElement.addElement(trimElement);
		parentElement.addElement(baseQueryMapConditionElement);
		// ------------

		return super.sqlMapDocumentGenerated(document, introspectedTable);
	}

	private Method generateSelectCountByMap(Method method, IntrospectedTable introspectedTable) {
		
		Method m = new Method("selectCountByMap");
		
		m.setVisibility(method.getVisibility());
		
		m.setReturnType(FullyQualifiedJavaType.getIntInstance());

		m.addParameter(new Parameter(new FullyQualifiedJavaType("Map<Object, Object>"), "map"));

		context.getCommentGenerator().addGeneralMethodComment(m,
				introspectedTable);
		return m;
	}


	private Method generateSelectByMap(Method method, IntrospectedTable introspectedTable) {

		Method m = new Method("selectByMap");

		m.setVisibility(method.getVisibility());

		m.setReturnType(new FullyQualifiedJavaType(introspectedTable.getBaseRecordType()));

		m.addParameter(new Parameter(new FullyQualifiedJavaType("Map<Object, Object>"), "map"));

		context.getCommentGenerator().addGeneralMethodComment(m,
				introspectedTable);
		return m;
	}

	private Method generateSelectListByMap(Method method, IntrospectedTable introspectedTable) {
		
		Method m = new Method("selectListByMap");
		
		m.setVisibility(method.getVisibility());
		
		m.setReturnType(new FullyQualifiedJavaType("java.util.List<"+introspectedTable.getBaseRecordType()+">"));

		m.addParameter(new Parameter(new FullyQualifiedJavaType("Map<Object, Object>"), "map"));

		context.getCommentGenerator().addGeneralMethodComment(m,
				introspectedTable);
		return m;
	}


}
