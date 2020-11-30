package com.activiti;
import java.util.List;
import org.activiti.engine.ProcessEngine;
import org.activiti.engine.ProcessEngines;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;

import org.activiti.engine.repository.ProcessDefinition;
import org.junit.Test;

public class ProcessQuery {

	public ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();

	/** 查询流程实例 */
	@Test
	public void fun1() {

		RepositoryService repositoryService = processEngine.getRepositoryService();
		List<ProcessDefinition> list = repositoryService.createProcessDefinitionQuery()
		                                                //查询条件，可以点出来
		                                                .orderByDeploymentId().asc()
		                                                .list();
		if (list != null && list.size() > 0) {
		    for (ProcessDefinition definition : list) {
		        System.out.println("流程定义ID：" + definition.getId());//流程定义的KEY+版本+随机生成的数
		        System.out.println("流程定义名称：" + definition.getName());//与bpmn文件的name属性相对应
		        System.out.println("流程定义KEY：" + definition.getKey());//与bpmn文件的id属性相对应
		        System.out.println("流程定义版本：" + definition.getVersion());
		        System.out.println("流程部署ID：" + definition.getDeploymentId());
		        System.out.println("资源名称BPMN文件：" + definition.getResourceName());
		        System.out.println("流程部署资源名称：" + definition.getDiagramResourceName());
		        System.out.println("##############################################################");
		    }
		}
	}
	//删除流程定义
	@Test
	public void fun2() {
		RepositoryService repositoryService = processEngine.getRepositoryService();
		// 不带级联删除，只能删除没有启动的流程，如果流程启动就会抛出异常
		repositoryService.deleteDeployment("1");

		// 带级联删除，不管流程是否启动，都可以删除
		// 参数：（流程实例ID，是否级联）deleteDeployment("1101") == deleteDeployment("1101", false)
		//repositoryService.deleteDeployment("1101", true);
	}
	
	
	//删除流程实例
	@Test
	public void fun3() {
		RuntimeService runtimeService =processEngine.getRuntimeService();
		runtimeService.deleteProcessInstance("10001", "删除流程实例");
		System.out.println("流程实例删除成功");
		
	}
	
	//删除流程实例
	@Test
	public void fun4() {
		TaskService taskService =processEngine.getTaskService();
		taskService.deleteTask("17502", "删除任务");
		System.out.println("当前任务删除成功");
		
	}
}