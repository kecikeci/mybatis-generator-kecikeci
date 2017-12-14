# mybatis-generator-kecikeci
MyBatis自动生成Mapper插件，配置完可根据自动生成Dao接口、xml文件、实体类。可满足大部分sql需求

## 生成的Dao接口效果

```
public interface MessageMapper {
    int deleteByPrimaryKey(Integer id);

    int selectCountByMap(Map<Object, Object> map);

    Message selectByMap(Map<Object, Object> map);

    List<Message> selectListByMap(Map<Object, Object> map);

    int insert(Message record);

    int insertSelective(Message record);

    Message selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Message record);

    int updateByPrimaryKeyWithBLOBs(Message record);

    int updateByPrimaryKey(Message record);
}
```

## 使用教程

1. 将`mybatis-generator-kecikeci.jar`放到资源文件目录`resources`下

2. `pom.xml`文件中配置插件

```
<build>
		<sourceDirectory>${project.basedir}/src/main/kotlin</sourceDirectory>
		<testSourceDirectory>${project.basedir}/src/test/kotlin</testSourceDirectory>
		<plugins>
			<plugin>
				<groupId>org.springframework.boot</groupId>
				<artifactId>spring-boot-maven-plugin</artifactId>
			</plugin>
			<plugin>
				<groupId>org.mybatis.generator</groupId>
				<artifactId>mybatis-generator-maven-plugin</artifactId>
				<version>1.3.5</version>
				<dependencies>
					<dependency>
						<groupId>com.kecikeci</groupId>
						<artifactId>mybatis-generator-kecikeci</artifactId>
						<version>1.0</version>
						<scope>system</scope>
						<systemPath>${project.basedir}/src/main/resources/mybatis-generator-kecikeci.jar</systemPath>
					</dependency>
				</dependencies>
			</plugin>
		</plugins>
	</build>
```

3. 新建`generatorConfig.xml`配置

需要引入mybatis包

```
<?xml version="1.0" encoding="UTF-8" ?>
<!DOCTYPE generatorConfiguration PUBLIC
        "-//mybatis.org//DTD MyBatis Generator Configuration 1.0//EN"
        "http://mybatis.org/dtd/mybatis-generator-config_1_0.dtd" >
<generatorConfiguration>
    <classPathEntry location="src/main/resources/mysql-connector-java-5.1.42.jar" />
    <context id="context1" targetRuntime="MyBatis3">
        <property name="beginningDelimiter" value=""/>
        <property name="endingDelimiter" value=""/>

        <!-- 为生成的Java模型创建一个toString方法 -->
        <plugin type="org.mybatis.generator.plugins.ToStringPlugin"></plugin>
        <!--  自定义的序列化 类文件 插件 -->
        <plugin type="com.kecikeci.mybatis.generator.plugin.SerializablePlugin"></plugin>
        <!-- 取消 Example 类文件的生成，在进行代码生成器的过程中，生成 selectCountByMap selectListByMap 两种方法，代替 Example 方法的生成 -->
        <plugin type="com.kecikeci.mybatis.generator.plugin.SelectCountAndListByMapPlugin"></plugin>
        <!-- 自定义注释部分（实体类entity生成过程中，将配置的数据库部分的注释部分生成，关键节点的注解部分生成） -->
        <plugin type="com.kecikeci.mybatis.generator.plugin.MyTableAnnotationPlugin"></plugin>
        <!-- 自定义 注释部分，增加字段方面的注释 -->
        <commentGenerator type="com.kecikeci.mybatis.generator.plugin.MyCommentGeneratorPlugin">
            <property name="suppressAllComments" value="true" />
        </commentGenerator>
        <!-- 数据库连接URL，用户名，密码 -->
        <jdbcConnection driverClass="com.mysql.jdbc.Driver"
                        connectionURL="jdbc:mysql://127.0.0.1:3306/springboot4kt?characterEncoding=utf-8"
                        userId="root" password="root" />
        <javaModelGenerator targetPackage="me.forxx.springboot4kt.model" targetProject="src/main/Kotlin" />
        <sqlMapGenerator targetPackage="mapper" targetProject="src/main/resources" />
        <javaClientGenerator targetPackage="me.forxx.springboot4kt.dao" targetProject="src/main/Kotlin" type="XMLMAPPER" />

        <!--生成对应表及类名-->
        <table
                tableName="message"
                enableCountByExample="false" enableUpdateByExample="false" enableDeleteByExample="false" enableSelectByExample="false" selectByExampleQueryId="false">
            <property
                    name="useActualColumnNames"
                    value="true" />
            <!-- 需要插入时返回主键值，请将此属性打开，column值为主键的列明
            <generatedKey column="taskId" sqlStatement="MySql" identity="true" />
             -->
        </table>

   </context>
</generatorConfiguration>
```

将其中的包名、数据库链接、表名，改成自己的

4. 配置Maven生成器

```
mybatis-generator:generate -e
```

如果觉得好用，请点星