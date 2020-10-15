package com.coffer.businesses.modules.store.v01.service;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.coffer.businesses.modules.store.v01.entity.StoDict;
import com.coffer.businesses.modules.store.v01.entity.StoRelevance;
import com.coffer.core.common.persistence.Page;
import com.coffer.core.common.service.BusinessException;
import com.coffer.core.common.test.SpringTransactionalContextTests;

/**
 * 测试物品关联配置Service
 * 
 * @author yuxixuan
 *
 */
public class StoRelevanceServiceTest extends SpringTransactionalContextTests {

	@Autowired
	private StoRelevanceService stoRevelanceService;


	/**
	 * 测试保存物品关联（正常系）
	 */
	@Test
	public void testSaveRelevance_1() {
		// 1、准备数据
		StoRelevance srParam = makeParamHkd();
		// 2、保存（新增）
		stoRevelanceService.saveRelevance(srParam);
		// 3、查询列表（新增）
		Page<StoRelevance> pageAdd = new Page<StoRelevance>();
		StoRelevance srSearch = new StoRelevance();
		srSearch.setCurrency("106");
		Page<StoRelevance> srListAdd = stoRevelanceService.findPageRelevance(pageAdd, srSearch);
		// 4、对比件数
		Assert.assertEquals(1, srListAdd.getList().size());
		// 5、对比列表记录
		StoRelevance srRecord = srListAdd.getList().get(0);
		// 币种：港币
		Assert.assertEquals("港币", srRecord.getCurrencyName());
		// 类别：ATM币
		Assert.assertEquals("ATM币", srRecord.getClassificationName());
		// 套别：外币无套别
		Assert.assertEquals(null, srRecord.getSetsName());
		// 材质：纸币
		Assert.assertEquals("纸币", srRecord.getCashName());
		// 6、查询明细
		StoRelevance srResultAdd = stoRevelanceService.getRelevance(srListAdd.getList().get(0).getGroupId());
		// 7、对比明细结果
		// 币种：港币
		Assert.assertEquals("106", srResultAdd.getCurrency());
		// 类别：ATM币
		Assert.assertEquals("01", srResultAdd.getClassification());
		// 套别：外币无套别
		Assert.assertEquals("0", srResultAdd.getSets());
		// 材质：纸币
		Assert.assertEquals("1", srResultAdd.getCash());
		// 面值
		List<String> denomiListAdd = new ArrayList<String>();
		// 面值：1000港币
		denomiListAdd.add("01");
		// 面值：500港币
		denomiListAdd.add("02");
		Assert.assertEquals(denomiListAdd, srResultAdd.getDenomiList());
		// 单位
		List<String> unitList = new ArrayList<String>();
		// 单位：捆
		unitList.add("101");
		// 单位：把
		unitList.add("102");
		Assert.assertEquals(unitList, srResultAdd.getUnitList());
		// 8、保存（修改）
		// 面值
		List<String> denomiListEdit = new ArrayList<String>();
		// 面值：100港币
		denomiListEdit.add("03");
		// 面值：50港币
		denomiListEdit.add("04");
		srResultAdd.setDenomiList(denomiListEdit);
		// 单位
		List<String> unitListEdit = new ArrayList<String>();
		// 单位：张
		unitListEdit.add("103");
		srResultAdd.setUnitList(unitListEdit);
		stoRevelanceService.saveRelevance(srResultAdd);
		// 9、查询列表（修改）
		Page<StoRelevance> pageEdit = new Page<StoRelevance>();
		Page<StoRelevance> srListEdit = stoRevelanceService.findPageRelevance(pageEdit, srSearch);
		// 10、查询修改后明细
		StoRelevance srResultEdit = stoRevelanceService.getRelevance(srListEdit.getList().get(0).getGroupId());
		// 11、对比修改后明细结果
		// 币种：港币
		Assert.assertEquals("106", srResultEdit.getCurrency());
		// 类别：ATM币
		Assert.assertEquals("01", srResultEdit.getClassification());
		// 套别：外币无套别
		Assert.assertEquals("0", srResultEdit.getSets());
		// 材质：纸币
		Assert.assertEquals("1", srResultEdit.getCash());
		// 面值
		Assert.assertEquals(denomiListEdit, srResultEdit.getDenomiList());
		// 单位
		Assert.assertEquals(unitListEdit, srResultEdit.getUnitList());

	}

	/**
	 * 测试保存物品关联（异常系）
	 */
	@Test
	public void testSaveRelevance_2() {
		try {
			StoRelevance srParam1 = makeParamHkd();
			// 1、第一次保存
			stoRevelanceService.saveRelevance(srParam1);
			// 2、再次保存相同数据
			StoRelevance srParam2 = makeParamHkd();
			stoRevelanceService.saveRelevance(srParam2);
			Assert.fail();
		} catch (BusinessException be) {
			Assert.assertEquals("message.E1002", be.getMessageCode());
		}
	}

	/**
	 * 测试根据组ID删除物品关联
	 */
	@Test
	public void testDeleteRelevance() {
		// 1、准备数据
		StoRelevance srParam = makeParamHkd();
		// 2、保存
		stoRevelanceService.saveRelevance(srParam);
		// 3、查询列表
		Page<StoRelevance> page = new Page<StoRelevance>();
		Page<StoRelevance> srList1 = stoRevelanceService.findPageRelevance(page, srParam);
		// 4、对比件数
		Assert.assertEquals(1, srList1.getList().size());
		// 5、删除记录
		stoRevelanceService.deleteRelevance(srList1.getList().get(0));
		// 6、再次查询
		Page<StoRelevance> srList2 = stoRevelanceService.findPageRelevance(page, srParam);
		// 7、对比件数
		Assert.assertEquals(0, srList2.getList().size());
	}

	/**
	 * 测试获取物品关联币种
	 */
	@Test
	public void testGetReleCurrencyList() {
		// 1、准备数据
		StoRelevance srParam1 = makeParamGbp();
		stoRevelanceService.saveRelevance(srParam1);
		StoRelevance srParam2 = makeParamHkd();
		stoRevelanceService.saveRelevance(srParam2);
		// 2、获取币种列表
		StoRelevance stoRelevance = new StoRelevance();
		stoRelevance.setCurrencyReserve("104");
		stoRelevance.setCurrencyRemove("106");
		List<StoDict> currencyList = stoRevelanceService.getReleCurrencyList(stoRelevance);
		// 3、对比结果
		// 3.1 对比件数
		Assert.assertEquals(1, currencyList.size());
		// 3.2 对比结果
		Assert.assertEquals("104", currencyList.get(0).getValue());
		Assert.assertEquals("英镑", currencyList.get(0).getLabel());
	}

	/**
	 * 测试获取物品关联类别
	 */
	@Test
	public void testGetReleClassificationList() {
		// 1、获取类别列表
		StoRelevance stoRelevance = new StoRelevance();
		stoRelevance.setCurrency("101");
		stoRelevance.setClassificationReserve("01,02");
		stoRelevance.setClassificationRemove("04");
		List<StoDict> stoDictList = stoRevelanceService.getReleClassificationList(stoRelevance);
		// 2、对比结果
		// 2.1 对比件数
		Assert.assertEquals(2, stoDictList.size());
		// 2.2 对比结果
		Assert.assertEquals("01", stoDictList.get(0).getValue());
		Assert.assertEquals("ATM币", stoDictList.get(0).getLabel());
		Assert.assertEquals("02", stoDictList.get(1).getValue());
		Assert.assertEquals("流通币", stoDictList.get(1).getLabel());
	}

	/**
	 * 测试获取物品关联套别
	 */
	@Test
	public void testGetReleSetsList() {
		// 1、获取套别列表
		StoRelevance stoRelevance = new StoRelevance();
		stoRelevance.setCurrency("101");
		stoRelevance.setClassification("01");
		List<StoDict> stoDictList = stoRevelanceService.getReleSetsList(stoRelevance);
		// 2、对比结果
		// 2.1 对比件数
		Assert.assertEquals(1, stoDictList.size());
		// 2.2 对比结果
		Assert.assertEquals("5", stoDictList.get(0).getValue());
		Assert.assertEquals("第五套", stoDictList.get(0).getLabel());
	}

	/**
	 * 测试获取物品关联材质
	 */
	@Test
	public void testGetReleCashList() {
		// 1、获取材质列表
		StoRelevance stoRelevance = new StoRelevance();
		stoRelevance.setCurrency("105");
		stoRelevance.setClassification("01");
		stoRelevance.setSets("");
		stoRelevance.setCashReserve("1");
		stoRelevance.setCashRemove("2");
		List<StoDict> stoDictList = stoRevelanceService.getReleCashList(stoRelevance);
		// 2、对比结果
		// 2.1 对比件数
		Assert.assertEquals(1, stoDictList.size());
		// 2.2 对比结果
		Assert.assertEquals("1", stoDictList.get(0).getValue());
		Assert.assertEquals("纸币", stoDictList.get(0).getLabel());
	}

	/**
	 * 测试获取物品关联面值
	 */
	@Test
	public void testGetReleDenominationList() {
		// 1、获取面值列表
		StoRelevance stoRelevance = new StoRelevance();
		stoRelevance.setCurrency("105");
		stoRelevance.setClassification("01");
		stoRelevance.setSets("");
		stoRelevance.setCash("1");
		stoRelevance.setCurrencyRefCode("jpy");
		stoRelevance.setCashRefCode("p");
		stoRelevance.setDenominationReserve("01,02");
		stoRelevance.setDenominationRemove("04");
		List<StoDict> stoDictList = stoRevelanceService.getReleDenominationList(stoRelevance);
		// 2、对比结果
		// 2.1 对比件数
		Assert.assertEquals(2, stoDictList.size());
		// 2.2 对比结果
		Assert.assertEquals("01", stoDictList.get(0).getValue());
		Assert.assertEquals("10000日元", stoDictList.get(0).getLabel());
		Assert.assertEquals("02", stoDictList.get(1).getValue());
		Assert.assertEquals("5000日元", stoDictList.get(1).getLabel());
	}

	/**
	 * 测试获取物品关联单位
	 */
	@Test
	public void testGetReleUnitList() {
		// 1、获取单位列表
		StoRelevance stoRelevance = new StoRelevance();
		stoRelevance.setCurrency("105");
		stoRelevance.setClassification("01");
		stoRelevance.setSets("");
		stoRelevance.setCash("1");
		stoRelevance.setDenomination("01");
		List<StoDict> stoDictList = stoRevelanceService.getReleUnitList(stoRelevance);
		// 2、对比结果
		// 2.1 对比件数
		Assert.assertEquals(2, stoDictList.size());
		// 2.2 对比结果
		Assert.assertEquals("101", stoDictList.get(0).getValue());
		Assert.assertEquals("捆", stoDictList.get(0).getLabel());
		Assert.assertEquals("102", stoDictList.get(1).getValue());
		Assert.assertEquals("把", stoDictList.get(1).getLabel());
	}

	/**
	 * 测试根据币种和材质获取面值选项
	 */
	@Test
	public void testGetDenOptions() {
		// 1、获取数据
		StoRelevance stoRelevance = new StoRelevance();
		stoRelevance.setCurrency("105");
		stoRelevance.setCash("1");
		List<StoDict> stoDictList = stoRevelanceService.getDenOptions(stoRelevance);
		// 2、对比结果
		// 2.1 对比件数
		Assert.assertEquals(6, stoDictList.size());
		// 2.2 对比结果
		Assert.assertEquals("01", stoDictList.get(0).getValue());
		Assert.assertEquals("10000日元", stoDictList.get(0).getLabel());
		Assert.assertEquals("02", stoDictList.get(1).getValue());
		Assert.assertEquals("5000日元", stoDictList.get(1).getLabel());
		Assert.assertEquals("03", stoDictList.get(2).getValue());
		Assert.assertEquals("2000日元", stoDictList.get(2).getLabel());
		Assert.assertEquals("04", stoDictList.get(3).getValue());
		Assert.assertEquals("1000日元", stoDictList.get(3).getLabel());
		Assert.assertEquals("05", stoDictList.get(4).getValue());
		Assert.assertEquals("500日元", stoDictList.get(4).getLabel());
		Assert.assertEquals("06", stoDictList.get(5).getValue());
		Assert.assertEquals("100日元", stoDictList.get(5).getLabel());
	}

	/**
	 * 测试根据材质获取单位选项
	 */
	@Test
	public void testGetUnitOptions() {
		// 1、获取数据
		StoRelevance stoRelevance = new StoRelevance();
		stoRelevance.setCash("1");
		List<StoDict> stoDictList = stoRevelanceService.getUnitOptions(stoRelevance);
		// 2、对比结果
		// 2.1 对比件数
		Assert.assertEquals(3, stoDictList.size());
		// 2.2 对比结果
		Assert.assertEquals("101", stoDictList.get(0).getValue());
		Assert.assertEquals("捆", stoDictList.get(0).getLabel());
		Assert.assertEquals("102", stoDictList.get(1).getValue());
		Assert.assertEquals("把", stoDictList.get(1).getLabel());
		Assert.assertEquals("103", stoDictList.get(2).getValue());
		Assert.assertEquals("张", stoDictList.get(2).getLabel());
	}

	/**
	 * 准备数据：港币
	 * 
	 * @return
	 */
	private StoRelevance makeParamHkd() {

		StoRelevance srParam = new StoRelevance();
		// 币种：港币
		srParam.setCurrency("106");
		// 类别：ATM币
		srParam.setClassification("01");
		// 套别：外币无套别
		srParam.setSets("");
		// 材质：纸币
		srParam.setCash("1");
		// 面值
		List<String> denomiList = new ArrayList<String>();
		// 面值：1000港币
		denomiList.add("01");
		// 面值：500港币
		denomiList.add("02");
		srParam.setDenomiList(denomiList);
		// 单位
		List<String> unitList = new ArrayList<String>();
		// 单位：捆
		unitList.add("101");
		// 单位：把
		unitList.add("102");
		srParam.setUnitList(unitList);

		// 返回
		return srParam;
	}

	/**
	 * 准备数据：英镑
	 * 
	 * @return
	 */
	private StoRelevance makeParamGbp() {

		StoRelevance srParam = new StoRelevance();
		// 币种：人民币
		srParam.setCurrency("104");
		// 类别：ATM币
		srParam.setClassification("01");
		// 套别：外币无套别
		srParam.setSets("");
		// 材质：纸币
		srParam.setCash("1");
		// 面值
		List<String> denomiList = new ArrayList<String>();
		// 面值：50英镑
		denomiList.add("01");
		// 面值：20英镑
		denomiList.add("02");
		srParam.setDenomiList(denomiList);
		// 单位
		List<String> unitList = new ArrayList<String>();
		// 单位：捆
		unitList.add("101");
		// 单位：把
		unitList.add("102");
		srParam.setUnitList(unitList);

		// 返回
		return srParam;
	}
}
