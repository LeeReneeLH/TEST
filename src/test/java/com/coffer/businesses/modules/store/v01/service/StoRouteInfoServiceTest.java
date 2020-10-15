package com.coffer.businesses.modules.store.v01.service;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;

import com.coffer.businesses.common.Constant;
import com.coffer.businesses.modules.store.v01.entity.StoEscortInfo;
import com.coffer.businesses.modules.store.v01.entity.StoRouteDetail;
import com.coffer.businesses.modules.store.v01.entity.StoRouteInfo;
import com.coffer.core.common.persistence.Page;
import com.coffer.core.common.test.SpringTransactionalContextTests;
import com.coffer.core.modules.sys.entity.Office;
import com.google.common.collect.Lists;

/**
 * 类说明注释
 * 
 * @author niguoyong
 * @version 2015年9月10日
 */
public class StoRouteInfoServiceTest extends SpringTransactionalContextTests {

	@Autowired
	private StoRouteInfoService stoRouteInfoService;

	/** 测试get */
	@Test
	public void testGetString() {
		// 数据准备
		// 执行测试方法
		StoRouteInfo stoRouteInfo = stoRouteInfoService.get("20150911090322912");
		// 测试结果验证
		Assert.assertNotNull("查询数据成功", stoRouteInfo);
	}

	@Test
	public void testFindPagePageOfStoRouteInfoStoRouteInfo() {
		Page<StoRouteInfo> page = new Page<StoRouteInfo>();
		StoRouteInfo stoRouteInfo = new StoRouteInfo();
		Assert.assertNotNull("成功", stoRouteInfoService.findPage(page, stoRouteInfo));
	}

	/** 测试Save登录 */
	@Test
	@Rollback(true)
	public void testSaveStoRouteInfo() {
		/** 测试Save登录 */
		// 数据准备
		StoRouteInfo stoRouteInfo = new StoRouteInfo();
		stoRouteInfo.setRouteName("asdf");
		stoRouteInfo.setEscortInfo1(new StoEscortInfo());
		stoRouteInfo.setEscortInfo2(new StoEscortInfo());
		stoRouteInfo.setRouteType(Constant.RouteInfo.CONVENTIONAL_ROUTE);
		stoRouteInfo.getEscortInfo1().setId("20150731140640739");
		stoRouteInfo.getEscortInfo2().setId("20150731134908876");
		stoRouteInfo.setDetailNum(1);
		List<StoRouteDetail> list = Lists.newArrayList();
		StoRouteDetail detail = new StoRouteDetail();
		detail.setOffice(new Office());
		detail.getOffice().setId("10000010");
		list.add(detail);
		stoRouteInfo.setStoRouteDetailList(list);
		// 执行测试方法
		stoRouteInfoService.save(stoRouteInfo);
		// 测试结果验证
		StoRouteInfo stoRouteInfo2 = stoRouteInfoService.searchStoRouteInfoByOfficeId("10000010");
		Assert.assertEquals("登录成功", stoRouteInfo2.getRouteName(), "asdf");
		Assert.assertEquals("登录成功", stoRouteInfo2.getEscortInfo1().getId(), "20150731140640739");
		Assert.assertEquals("登录成功", stoRouteInfo2.getEscortInfo2().getId(), "20150731134908876");
		Assert.assertTrue("登录成功", stoRouteInfo2.getDetailNum() == 1);

		/** 测试Save更新 */
		stoRouteInfo2.setRouteName("1234");
		stoRouteInfo2.setRouteType(Constant.RouteInfo.CONVENTIONAL_ROUTE);
		stoRouteInfo2.getEscortInfo1().setId("20150731134908876");
		stoRouteInfo2.getEscortInfo2().setId("20150731140640739");
		stoRouteInfo2.setDetailNum(2);
		List<StoRouteDetail> list2 = Lists.newArrayList();
		StoRouteDetail detail2 = new StoRouteDetail();
		detail2.setOffice(new Office());
		detail2.getOffice().setId("10000011");
		list2.add(detail2);
		StoRouteDetail detail3 = new StoRouteDetail();
		detail3.setOffice(new Office());
		detail3.getOffice().setId("10000012");
		list2.add(detail3);
		stoRouteInfo2.setStoRouteDetailList(list2);
		// 执行测试方法
		stoRouteInfoService.save(stoRouteInfo2);
		// 测试结果验证
		StoRouteInfo stoRouteInfo3 = stoRouteInfoService.get(stoRouteInfo2.getId());
		Assert.assertEquals("更新成功", stoRouteInfo3.getRouteName(), "1234");
		Assert.assertEquals("更新成功", stoRouteInfo3.getEscortInfo1().getId(), "20150731134908876");
		Assert.assertEquals("更新成功", stoRouteInfo3.getEscortInfo2().getId(), "20150731140640739");
		Assert.assertTrue("更新成功", stoRouteInfo3.getDetailNum() == 2);

		/** 测试Save删除 */
		stoRouteInfoService.delete(stoRouteInfo2.getId());
		Assert.assertNull("删除成功", stoRouteInfoService.get(stoRouteInfo2.getId()));
	}

	@Test
	@Rollback(true)
	public void testDeleteStringString() {
		/** 测试Save登录 */
		// 数据准备
		StoRouteInfo stoRouteInfo = new StoRouteInfo();
		stoRouteInfo.setRouteName("asdf");
		stoRouteInfo.setEscortInfo1(new StoEscortInfo());
		stoRouteInfo.setEscortInfo2(new StoEscortInfo());
		stoRouteInfo.setRouteType(Constant.RouteInfo.CONVENTIONAL_ROUTE);
		stoRouteInfo.getEscortInfo1().setId("20150731140640739");
		stoRouteInfo.getEscortInfo2().setId("20150731134908876");
		stoRouteInfo.setDetailNum(1);
		List<StoRouteDetail> list = Lists.newArrayList();
		StoRouteDetail detail = new StoRouteDetail();
		detail.setOffice(new Office());
		detail.getOffice().setId("10000010");
		list.add(detail);
		stoRouteInfo.setStoRouteDetailList(list);
		stoRouteInfoService.save(stoRouteInfo);
		StoRouteInfo stoRouteInfo2 = stoRouteInfoService.searchStoRouteInfoByOfficeId("10000010");
		// 执行测试方法
		stoRouteInfoService.delete(stoRouteInfo2.getId(), "10000010");
		// 测试结果验证
		Assert.assertNull("删除成功", stoRouteInfoService.get(stoRouteInfo2.getId()));

	}

	@Test
	public void testGetTemporaryRouteSeq() {

		Assert.assertEquals("成功", stoRouteInfoService.getTemporaryRouteSeq(), "临时线路0001");
	}

	// @Test
	// public void testGetOfficeNotInRouteOffice() {
	// // 数据准备
	// // 执行测试方法
	// List<Office> stoRouteInfo =
	// stoRouteInfoService.getOfficeNotInRouteOffice("20150911090322912");
	// // 测试结果验证
	// Assert.assertNull("查询数据成功", stoRouteInfo);
	// }

}
