package com.activiti;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.activiti.engine.ProcessEngine;
import org.activiti.engine.ProcessEngines;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.IdentityLink;
import org.activiti.engine.task.Task;
import org.junit.Test;

//过个处理人
public class ProcessAudit2 {
	public ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();

	/** 部署流程定义 */
	@Test
	public void deploymentProcessDefinition() {

		Deployment deployment = processEngine.getRepositoryService()// 与流程定义和部署对象相关的Service
				.createDeployment() // 创建一个部署对象
				.name("审批流程")// 添加部署的名称
				.addClasspathResource("diagrams/audit.bpmn")// 从classpath的资源中加载，一次只能加载一个文件
				.addClasspathResource("diagrams/audit.png")// 从classpath的资源中加载，一次只能加载一个文件
				.deploy(); // 完成部署
		System.out.println("部署ID:" + deployment.getId()); // 1
		System.out.println("部署名称:" + deployment.getName()); // helloworld入门程序

	}

	/** 启动流程实例 **/
	@Test
	public void startProcessInstance() {
		ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
		// 流程定义的key
		String processDefinitionKey = "audit_process";
		ProcessInstance pi = processEngine.getRuntimeService()// 与正在执行 的流程实例和执行对象相关的Service
				.startProcessInstanceByKey(processDefinitionKey); // 使用流程定义的key启动流程实例,key对应helloworld.bpmn文件中id的属性值，使用key值启动，默认是按照最新版本的流程定义启动
		System.out.println("流程实例ID:" + pi.getId());
		System.out.println("流程定义ID:" + pi.getProcessDefinitionId());
	}

	/** 查询当前人的个人任务 */
	@Test
	public void findMyPersonalTask() {
		ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
		//String assignee = "朱静怡";
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
	
	//根据任务id获取当前任务处理人(一个或多个)
	//如果当前任务有多个处理人，且任务没有被领取，则会显示多个处理人
	//如何任务被领取,则 任务处理人人为领取人
	@Test
	public void testQueryGroupUserByTaskId(){
		String taskId ="57502";
		List<IdentityLink> identityLinkList = processEngine.getTaskService()
											.getIdentityLinksForTask(taskId);
		if(identityLinkList!=null&&identityLinkList.size()>0){
			for(IdentityLink identityLink:identityLinkList){
				System.out.println("任务ID:"+identityLink.getTaskId()+",处理人："+identityLink.getUserId());
				}
		}
	}
	
	//任务领取
	@Test
	public void testClaimTask(){
		String taskId ="57502";
		processEngine.getTaskService().claim(taskId, "朱静怡");
		System.out.println("任务领取成功");
	}
	
	//任务领取回退
	@Test
	public void resetTaskAssignee(){
		String taskId ="57502";
		processEngine.getTaskService().setAssignee(taskId, null);
		System.out.println("任务回退成功");
	}
	
	//任务转发给其他人
	@Test
	public void changeTaskAssignee(){
		String taskId ="57502";
		processEngine.getTaskService().setAssignee(taskId, "小红");
		System.out.println("任务转发成功");
	}
	
	
	
	/** 无参 提交任务 */
	@Test
	public void completeMyPersonalTask() {
		ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
		// 任务ID
		String taskId = "55004";
		processEngine.getTaskService().complete(taskId);
		System.out.println("提交任务");
	}

	/** 带参提交任务 */
	@Test
	public void completeMyPersonalTask2() {
		ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
		// 任务ID
		String taskId = "55004";
		// 可以从页面上获取重要/不重要的选项，设置流程变量
		Map<String, Object> variables = new HashMap<String, Object>();
		variables.put("userName", "李思齐"); 
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
