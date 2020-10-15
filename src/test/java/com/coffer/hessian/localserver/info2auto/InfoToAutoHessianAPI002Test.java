package com.coffer.hessian.localserver.info2auto;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath*:/spring-context-hessian-client-test.xml" })
@Transactional
public class InfoToAutoHessianAPI002Test {

	// 方式一:使用依赖注入：
	@Autowired
	InfoToAutoHessianService infoToAutoHessianService;

	@Test
	public void clientTest() {
		// 测试
		invokeInterface("infoToAutoHessianAPI002Test");
		Assert.assertEquals(1, 1);
	}

	/**
	 * 调用接口
	 * 
	 * @param jsonFileName
	 */
	private void invokeInterface(String jsonFileName) {
		String param = ReadJsonUtils.readJson(jsonFileName);
		String resultStr = infoToAutoHessianService.service(param);
		System.out.println(jsonFileName + "=====+++++ start ++++++=====");
		System.out.println(resultStr);
		System.out.println(jsonFileName + "=====+++++ end ++++++=====");
	}
}
