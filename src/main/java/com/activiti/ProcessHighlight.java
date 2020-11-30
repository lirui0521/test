package com.activiti;

import java.awt.GraphicsEnvironment;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.activiti.bpmn.model.BpmnModel;
import org.activiti.engine.ProcessEngine;
import org.activiti.engine.ProcessEngines;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.TaskService;
import org.activiti.engine.history.HistoricActivityInstance;
import org.activiti.engine.impl.persistence.entity.ProcessDefinitionEntity;
import org.activiti.engine.impl.pvm.PvmTransition;
import org.activiti.engine.impl.pvm.process.ActivityImpl;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.IdentityLink;
import org.activiti.engine.task.Task;
import org.activiti.image.ProcessDiagramGenerator;
import org.apache.commons.io.FileUtils;
import org.junit.Test;

//流程节点高亮
public class ProcessHighlight {
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
		//ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
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
		//ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
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
		String taskId = "10004";
		processEngine.getTaskService().complete(taskId);
		System.out.println("提交任务");
	}

	/** 带参提交任务 */
	@Test
	public void completeMyPersonalTask2() {
		//ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
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
		//ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
		// 任务ID
		String taskId = "15004";
		// 可以从页面上获取重要/不重要的选项，设置流程变量
		Map<String, Object> variables = new HashMap<String, Object>();
		variables.put("msg", "N");
		processEngine.getTaskService().complete(taskId, variables);
		System.out.println("驳回任务");
	}
	
	//流程追踪图片
	@Test
	public void fun1() throws IOException {


		/*InputStream resourceAsStream = getWorkTraceInputStream("10001");
		FileUtils.copyInputStreamToFile(resourceAsStream, new File("D:\\" + "bbb.png"));
		System.out.println("获得追踪图片成功");*/
		
		generateImage("10001");
	}
	
	
	private InputStream getWorkTraceInputStream(String pid) {
		GraphicsEnvironment g = GraphicsEnvironment.getLocalGraphicsEnvironment();
		String fontName[] = g.getAvailableFontFamilyNames();

		/*ActSystemAssignEntity entity = this.actSystemAssignMapper.queryByPid(pid);
		String status = WorkflowEnums.ActSystemAssignEnums.PROCESS.getCode();
		if (entity != null) {
			status = entity.getStatus();
		} else {
			return null;
		}*/
		// 如果是作废状态
		/*if (status.equalsIgnoreCase(WorkflowEnums.ActSystemAssignEnums.CANCEL.getCode())
				|| status.equalsIgnoreCase(WorkflowEnums.ActSystemAssignEnums.FINISH.getCode())) {

			BpmnModel bm = repositoryService.getBpmnModel(entity.getProcessDefinitionId());
			ProcessDiagramGenerator generator = this.processEngineConfiguration.getProcessDiagramGenerator();
			Collection<FlowElement> flowElements = bm.getMainProcess().getFlowElements();
			String startId = "";
			String endId = "";
			if (flowElements != null && flowElements.size() > 0) {
				for (FlowElement element : flowElements) {
					if ("StartEvent".equalsIgnoreCase(element.getName())) {
						startId = element.getId();
					}
					if ("EndEvent".equalsIgnoreCase(element.getName())) {
						endId = element.getId();
					}
				}
			}
			List<String> highLightActivites = new ArrayList<>();
			List<WorkFlowNodeEntity> workFlowNodeVoList = this.workFlowNodeMapper
					.queryByProcessDefinitionId(entity.getProcessDefinitionId());
			if (status.equalsIgnoreCase(WorkflowEnums.ActSystemAssignEnums.CANCEL.getCode())) {
				if (StringUtils.isNotBlank(startId)) {
					highLightActivites.add(startId);
				}
				if (workFlowNodeVoList != null && workFlowNodeVoList.size() > 0) {
					for (WorkFlowNodeEntity node : workFlowNodeVoList) {
						highLightActivites.add(node.getElementId());
						if (node.getElementId().equalsIgnoreCase(entity.getActivityId())) {
							break;
						}
					}
				}
			} else {
				// 如果是完结 去历史所有节点
				List<HistoricActivityInstance> highLightedActivitList = historyService
						.createHistoricActivityInstanceQuery().processInstanceId(pid).list();
				for (HistoricActivityInstance tempActivity : highLightedActivitList) {
					String activityId = tempActivity.getActivityId();
					highLightActivites.add(activityId);
				}
			}
			InputStream imageStream = null;
			logger.info("宋体");
			imageStream = generator.generateDiagram(bm, "png", highLightActivites, new ArrayList<String>(), "宋体", "宋体",
					null, 1.0d);
			return imageStream;
		}*/
		ProcessInstance processInstance = processEngine.getRuntimeService().createProcessInstanceQuery().processInstanceId(pid)
				.singleResult();
		ProcessDefinitionEntity definitionEntity = (ProcessDefinitionEntity) processEngine.getRepositoryService()
				.getProcessDefinition(processInstance.getProcessDefinitionId());

		List<HistoricActivityInstance> highLightedActivitList = processEngine.getHistoryService().createHistoricActivityInstanceQuery()
				.processInstanceId(pid).list();

		// 高亮环节id集合
		List<String> highLightedActivitis = new ArrayList<String>();
		// 高亮线路id集合
		List<String> highLightedFlows = getHighLightedFlows(definitionEntity, highLightedActivitList);

		for (HistoricActivityInstance tempActivity : highLightedActivitList) {
			String activityId = tempActivity.getActivityId();
			highLightedActivitis.add(activityId);
		}
		BpmnModel bm = processEngine.getRepositoryService().getBpmnModel(processInstance.getProcessDefinitionId());
		ProcessDiagramGenerator generator = processEngine.getProcessEngineConfiguration().getProcessDiagramGenerator();
		//logger.info("使用宋体输出");
		InputStream imageStream = generator.generateDiagram(bm, "png", highLightedActivitis, highLightedFlows,
				"黑体", "宋体", null, 10.0d);
		return imageStream;
	}
	
	 private List<String> getHighLightedFlows(
	            ProcessDefinitionEntity processDefinitionEntity,
	            List<HistoricActivityInstance> historicActivityInstances) {
	        List<String> highFlows = new ArrayList<String>();// 用以保存高亮的线flowId
	        for (int i = 0; i < historicActivityInstances.size() - 1; i++) {// 对历史流程节点进行遍历
	            ActivityImpl activityImpl = processDefinitionEntity
	                    .findActivity(historicActivityInstances.get(i)
	                            .getActivityId());// 得到节点定义的详细信息
	            List<ActivityImpl> sameStartTimeNodes = new ArrayList<ActivityImpl>();// 用以保存后需开始时间相同的节点
	            ActivityImpl sameActivityImpl1 = processDefinitionEntity
	                    .findActivity(historicActivityInstances.get(i + 1)
	                            .getActivityId());
	            // 将后面第一个节点放在时间相同节点的集合里
	            sameStartTimeNodes.add(sameActivityImpl1);
	            for (int j = i + 1; j < historicActivityInstances.size() - 1; j++) {
	                HistoricActivityInstance activityImpl1 = historicActivityInstances
	                        .get(j);// 后续第一个节点
	                HistoricActivityInstance activityImpl2 = historicActivityInstances
	                        .get(j + 1);// 后续第二个节点
	                if (activityImpl1.getStartTime().equals(
	                        activityImpl2.getStartTime())) {
	                    // 如果第一个节点和第二个节点开始时间相同保存
	                    ActivityImpl sameActivityImpl2 = processDefinitionEntity
	                            .findActivity(activityImpl2.getActivityId());
	                    sameStartTimeNodes.add(sameActivityImpl2);
	                } else {
	                    // 有不相同跳出循环
	                    break;
	                }
	            }
	            List<PvmTransition> pvmTransitions = activityImpl
	                    .getOutgoingTransitions();// 取出节点的所有出去的线
	            for (PvmTransition pvmTransition : pvmTransitions) {
	                // 对所有的线进行遍历
	                ActivityImpl pvmActivityImpl = (ActivityImpl) pvmTransition
	                        .getDestination();
	                // 如果取出的线的目标节点存在时间相同的节点里，保存该线的id，进行高亮显示
	                if (sameStartTimeNodes.contains(pvmActivityImpl)) {
	                    highFlows.add(pvmTransition.getId());
	                }
	            }
	        }
	        return highFlows;
	    }
	 
	 
	 
	 
	 public  void generateImage(String processInstanceId){
		  //1.创建核心引擎流程对象processEngine
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
		TaskService taskService = processEngine.getTaskService();
		Task task = taskService.createTaskQuery().processInstanceId(processInstanceId).singleResult();
		//流程定义
		BpmnModel bpmnModel = processEngine.getRepositoryService().getBpmnModel(task.getProcessDefinitionId()); 
		
		//正在活动节点
		List<String> activeActivityIds = processEngine.getRuntimeService().getActiveActivityIds(task.getExecutionId());

		ProcessDiagramGenerator pdg = processEngine.getProcessEngineConfiguration().getProcessDiagramGenerator();
		//生成流图片
       InputStream inputStream = pdg.generateDiagram(bpmnModel, "PNG", activeActivityIds, activeActivityIds,  
    		   "黑体", "宋体",  
       		processEngine.getProcessEngineConfiguration().getProcessEngineConfiguration().getClassLoader(), 1.0);  
       try {  
       	//生成本地图片
       	File file = new File("D:/test.png");
       	FileUtils.copyInputStreamToFile(inputStream, file);
           //return IOUtils.toByteArray(inputStream);  
        } catch (Exception e) {  
            throw new RuntimeException("生成流程图异常！", e);  
        } finally {  
            //IOUtils.closeQuietly(inputStream); 
        }  
	}
}
