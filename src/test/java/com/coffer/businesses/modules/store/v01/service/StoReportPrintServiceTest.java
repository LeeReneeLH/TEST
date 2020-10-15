package com.coffer.businesses.modules.store.v01.service;

import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.coffer.businesses.modules.store.v01.entity.StoBoxInfo;
import com.coffer.core.common.test.SpringTransactionalContextTests; 


/**
 * 类说明注释
 * 
 * @author niguoyong
 * @version 2015年9月17日
 */
public class StoReportPrintServiceTest extends SpringTransactionalContextTests {

	@Autowired
	private StoReportPrintService stoReportPrintService;

	@Test
	public void testReportMessage() {
		//执行
		Map<String, Object> map=stoReportPrintService.reportMessage();
		// 测试结果验证
		Assert.assertNotNull("查询数据成功", map.get("outlets_list"));
		Assert.assertNotNull("查询数据成功", map.get("list"));
	}

	@Test
	public void testfindInTransitBoxInfos() {
		//执行
		List<StoBoxInfo> map=stoReportPrintService.findInTransitBoxInfos();
		// 测试结果验证
		Assert.assertNotNull("查询数据成功", map);
	}
}
