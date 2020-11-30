package com.activiti;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.activiti.engine.ProcessEngine;
import org.activiti.engine.ProcessEngines;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.junit.Test;

//排他网关
public class ExclusiveGateWay {

	public ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();

	/** 部署流程定义 */
	@Test
	public void deploymentProcessDefinition() {

		Deployment deployment = processEngine.getRepositoryService()// 与流程定义和部署对象相关的Service
				.createDeployment() // 创建一个部署对象
				.name("报销申请")// 添加部署的名称
				.addClasspathResource("diagrams/baoxiao.bpmn")// 从classpath的资源中加载，一次只能加载一个文件
				.addClasspathResource("diagrams/baoxiao.png")// 从classpath的资源中加载，一次只能加载一个文件
				.deploy(); // 完成部署
		System.out.println("部署ID:" + deployment.getId()); // 1
		System.out.println("部署名称:" + deployment.getName()); // helloworld入门程序

	}

	/** 启动流程实例 **/
	@Test
	public void startProcessInstance() {
		ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
		// 流程定义的key
		String processDefinitionKey = "baoxiao_process";
		ProcessInstance pi = processEngine.getRuntimeService()// 与正在执行 的流程实例和执行对象相关的Service
				.startProcessInstanceByKey(processDefinitionKey); // 使用流程定义的key启动流程实例,key对应helloworld.bpmn文件中id的属性值，使用key值启动，默认是按照最新版本的流程定义启动
		System.out.println("流程实例ID:" + pi.getId());
		System.out.println("流程定义ID:" + pi.getProcessDefinitionId());
	}

	/** 查询当前人的个人任务 */
	@Test
	public void findMyPersonalTask() {
		ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
		//String assignee = "安琪";
		List<Task> list = processEngine.getTaskService()// 与正在执行的任务管理相关的Service
				.createTaskQuery()// 创建任务查询
				//.taskAssignee(assignee)// 指定个人任查询，指定办理人
				.list();
		if (list != null && list.size() > 0) {
			for (Task task : list) {
				System.out.println("任务ID:" + task.getId());
				System.out.println("任务名称:" + task.getName());
				System.out.println("任务的创建时间:" + task.getCreateTime());
				System.out.println("任务的办理人:" + task.getAssignee());
				System.out.println("流程实例ID:" + task.getProcessInstanceId());
				System.out.println("执行对象ID:" + task.getExecutionId());
				System.out.println("流程定义ID:" + task.getProcessDefinitionId());
				System.out.println("############################################");
			}
		}else {
			System.out.println("暂无待办任务");
		}
	}
	
	/** 无参 提交任务 */
	@Test
	public void completeMyPersonalTask() {
		ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
		// 任务ID
		String taskId = "37502";
		processEngine.getTaskService().complete(taskId);
		System.out.println("提交任务");
	}

	/** 带参提交任务 */
	@Test
	public void completeMyPersonalTask2() {
		ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
		// 任务ID
		String taskId = "32503";
		// 可以从页面上获取重要/不重要的选项，设置流程变量
		Map<String, Object> variables = new HashMap<String, Object>();
		variables.put("amount", "800"); 
		processEngine.getTaskService().complete(taskId,variables); 
		System.out.println("提交任务");
	}
	
	
	/** 带参驳回任务 */
	@Test
	public void completeMyPersonalTask3() {
		ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
		// 任务ID
		String taskId = "15004";
		// 可以从页面上获取重要/不重要的选项，设置流程变量
		Map<String, Object> variables = new HashMap<String, Object>();
		variables.put("msg", "N");
		processEngine.getTaskService().complete(taskId, variables);
		System.out.println("驳回任务");
	}
}