package com.activiti;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.zip.ZipInputStream;

import org.activiti.engine.ProcessEngine;
import org.activiti.engine.ProcessEngines;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.ProcessDefinition;
import org.apache.commons.io.FileUtils;
import org.junit.Test;

public class ProcessImage {

	public ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();

	/** 保存流程实例图片 
	 * @throws IOException */
	@Test
	public void fun1() throws IOException {

		// 部署实例ID
		String deployId = "1";
		RepositoryService repositoryService = processEngine.getRepositoryService();

		// 获取资源名称
		String resourceName = "";
		List<String> list = repositoryService.getDeploymentResourceNames(deployId);
		if (list != null && list.size() > 0) {
		    for (String name : list) {
		        if (name.lastIndexOf(".png") >= 0) {
		            resourceName = name;
		            break;
		        }
		    }
		}
		System.out.println(resourceName);

		InputStream resourceAsStream = repositoryService.getResourceAsStream(deployId, resourceName);

		// 把图片写入到指定的文件中
		// org.apache.commons.io.FileUtils
		FileUtils.copyInputStreamToFile(resourceAsStream, new File("D:\\" + resourceName));
	}
}