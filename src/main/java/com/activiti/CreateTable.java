package com.activiti;

import org.activiti.engine.ProcessEngine;
import org.activiti.engine.ProcessEngineConfiguration;
import org.activiti.engine.ProcessEngines;
import org.junit.Test;

public class CreateTable {

	/*
	 * 1.通过JDBC方式创建表
	 */
	@Test
	public void CreatTable1(){
		//创建流程引擎配置对象
		ProcessEngineConfiguration  processEngineConfiguration =ProcessEngineConfiguration.createStandaloneProcessEngineConfiguration();
		//配置数据库连接对象
		processEngineConfiguration.setJdbcDriver("com.mysql.jdbc.Driver");
		processEngineConfiguration.setJdbcUrl("jdbc:mysql://localhost:3306/activiti?userUnicode=true&amp;characterEncoding=utf8");
		processEngineConfiguration.setJdbcUsername("root");
		processEngineConfiguration.setJdbcPassword("123456");
		//表生成策略（如果没有表则创建表）
		processEngineConfiguration.setDatabaseSchemaUpdate(ProcessEngineConfiguration.DB_SCHEMA_UPDATE_TRUE);
		//工作流核心对象 流程引擎
		ProcessEngine processEngine = processEngineConfiguration.buildProcessEngine();
		System.out.println(processEngine);	
	}
	
	@Test
	public void CreatTable2(){
		// 加载配置文件创建ProcessEngine对象
		ProcessEngine processEngine = ProcessEngineConfiguration.createProcessEngineConfigurationFromResource("activiti.cfg.xml").buildProcessEngine();
		// 验证是否创建成功
		System.out.println(processEngine);
	}
	
	@Test
	public void CreatTable3(){
		// 使用ProcessEngines的getDefaultProcessEngine() 方法会自动加载classpath下面的activiti.cfg.xml文件
		ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
		// 验证是否创建成功
		System.out.println(processEngine);
	}
	
}