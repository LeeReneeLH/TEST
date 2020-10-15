package com.coffer.businesses.modules.store.v01.service;

import java.math.BigDecimal;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.coffer.businesses.modules.store.v01.entity.StoGoodSelect;
import com.coffer.businesses.modules.store.v01.entity.StoGoods;
import com.coffer.businesses.modules.store.v01.entity.StoRelevance;
import com.coffer.core.common.persistence.Page;
import com.coffer.core.common.service.BusinessException;
import com.coffer.core.common.test.SpringTransactionalContextTests;

/**
 * 测试物品管理Service
 * 
 * @author yuxixuan
 *
 */
public class StoGoodsServiceTest extends SpringTransactionalContextTests {

	@Autowired
	private StoGoodsService stoGoodsService;

	/**
	 * 测试保存物品（正常系）
	 */
	@Test
	public void testSaveGoods_1() {
		// 1、数据准备
		StoGoods stoGoods = makeParam();
		// 2、保存（新增）
		stoGoodsService.saveGoods(stoGoods);
		// 3、查询列表（新增）
		Page<StoGoods> pageAdd = new Page<StoGoods>();
		StoGoods searchParamAdd = new StoGoods();
		// 物品名称
		searchParamAdd.setGoodsName("测试物品名称");
		// 物品类型：货币
		searchParamAdd.setGoodsType("01");
		Page<StoGoods> resultPageAdd = stoGoodsService.findPage(pageAdd, searchParamAdd);
		// 4、对比件数（新增）
		Assert.assertEquals(1, resultPageAdd.getList().size());
		// 5、对比列表记录（新增）
		StoGoods resultRecordAdd = resultPageAdd.getList().get(0);
		// 物品名称
		Assert.assertEquals("测试物品名称", resultRecordAdd.getGoodsName());
		// 物品描述
		Assert.assertEquals("测试物品描述", resultRecordAdd.getDescription());
		// 物品价值
		Assert.assertEquals(BigDecimal.valueOf(1000000), resultRecordAdd.getGoodsVal());
		// 物品类型
		Assert.assertEquals("01", resultRecordAdd.getGoodsType());
		// 6、查询明细（新增）
		StoGoods resultDetailAdd = stoGoodsService.get(resultRecordAdd.getGoodsID());
		// 7、对比明细结果（新增）
		// 物品名称
		Assert.assertEquals("测试物品名称", resultDetailAdd.getGoodsName());
		// 物品描述
		Assert.assertEquals("测试物品描述", resultDetailAdd.getDescription());
		// 物品价值
		Assert.assertEquals(BigDecimal.valueOf(1000000), resultDetailAdd.getGoodsVal());
		// 币种：港币
		Assert.assertEquals("106", resultDetailAdd.getStoGoodSelect().getCurrency());
		// 类别：ATM币
		Assert.assertEquals("01", resultDetailAdd.getStoGoodSelect().getClassification());
		// 套别：外币无套别
		Assert.assertEquals("0", resultDetailAdd.getStoGoodSelect().getEdition());
		// 材质：纸币
		Assert.assertEquals("1", resultDetailAdd.getStoGoodSelect().getCash());
		// 面值：1000港币
		Assert.assertEquals("01", resultDetailAdd.getStoGoodSelect().getDenomination());
		// 单位：捆
		Assert.assertEquals("101", resultDetailAdd.getStoGoodSelect().getUnit());
		// 8、保存（修改）
		// 物品名称
		resultDetailAdd.setGoodsName("测试修改物品名称");
		// 物品描述
		resultDetailAdd.setDescription("测试修改物品描述");
		stoGoodsService.saveGoods(resultDetailAdd);
		// 9、查询列表（修改）
		Page<StoGoods> pageEdit = new Page<StoGoods>();
		StoGoods searchParamEdit = new StoGoods();
		// 物品名称
		searchParamEdit.setGoodsName("测试修改物品名称");
		// 物品类型：货币
		searchParamEdit.setGoodsType("01");
		Page<StoGoods> resultPageEdit = stoGoodsService.findPage(pageEdit, searchParamEdit);
		// 10、查询明细（修改）
		StoGoods resultRecordEdit = resultPageEdit.getList().get(0);
		StoGoods resultDetailEdit = stoGoodsService.get(resultRecordEdit.getGoodsID());
		// 11、对比明细结果（修改）
		// 物品名称
		Assert.assertEquals("测试修改物品名称", resultDetailEdit.getGoodsName());
		// 物品描述
		Assert.assertEquals("测试修改物品描述", resultDetailEdit.getDescription());
		// 物品价值
		Assert.assertEquals(BigDecimal.valueOf(1000000), resultDetailEdit.getGoodsVal());
		// 币种：港币
		Assert.assertEquals("106", resultDetailEdit.getStoGoodSelect().getCurrency());
		// 类别：ATM币
		Assert.assertEquals("01", resultDetailEdit.getStoGoodSelect().getClassification());
		// 套别：外币无套别
		Assert.assertEquals("0", resultDetailEdit.getStoGoodSelect().getEdition());
		// 材质：纸币
		Assert.assertEquals("1", resultDetailEdit.getStoGoodSelect().getCash());
		// 面值：1000港币
		Assert.assertEquals("01", resultDetailEdit.getStoGoodSelect().getDenomination());
		// 单位：捆
		Assert.assertEquals("101", resultDetailEdit.getStoGoodSelect().getUnit());

	}

	/**
	 * 测试保存物品（异常系）
	 */
	@Test
	public void testSaveGoods_2() {
		try {
			StoGoods stoGoods1 = makeParam();
			// 1、第一次保存
			stoGoodsService.saveGoods(stoGoods1);
			// 2、再次保存相同数据
			StoGoods stoGoods2 = makeParam();
			stoGoodsService.saveGoods(stoGoods2);
			Assert.fail();
		} catch (BusinessException be) {
			Assert.assertEquals("message.E1029", be.getMessageCode());
		}
	}

	/**
	 * 测试删除物品
	 */
	@Test
	public void testDelete() {
		// 1、准备数据
		StoGoods stoGoods = makeParam();
		// 2、保存
		stoGoodsService.saveGoods(stoGoods);
		// 3、查询列表
		Page<StoGoods> pageAdd = new Page<StoGoods>();
		StoGoods searchParamAdd = new StoGoods();
		// 物品名称
		searchParamAdd.setGoodsName("测试物品名称");
		// 物品类型：货币
		searchParamAdd.setGoodsType("01");
		Page<StoGoods> resultPageAdd = stoGoodsService.findPage(pageAdd, searchParamAdd);
		// 4、对比件数
		Assert.assertEquals(1, resultPageAdd.getList().size());
		// 5、删除记录
		stoGoodsService.delete(resultPageAdd.getList().get(0));
		// 6、再次查询
		Page<StoGoods> resultPageAdd2 = stoGoodsService.findPage(pageAdd, searchParamAdd);
		// 7、对比件数
		Assert.assertEquals(resultPageAdd2.getList().size(), 0);

	}

	/**
	 * 准备数据
	 * 
	 * @return
	 */
	private StoGoods makeParam() {

		StoGoods stoGoods = new StoGoods();
		// 物品名称
		stoGoods.setGoodsName("测试物品名称");
		// 物品描述
		stoGoods.setDescription("测试物品描述");
		// 物品类型：货币
		stoGoods.setGoodsType("01");
		// 共通部分
		StoGoodSelect stoGoodSelect = new StoGoodSelect();
		// 币种：港币
		stoGoodSelect.setCurrency("106");
		// 类别：ATM币
		stoGoodSelect.setClassification("01");
		// 套别：外币无套别
		stoGoodSelect.setEdition("");
		// 材质：纸币
		stoGoodSelect.setCash("1");
		// 面值：1000港币
		stoGoodSelect.setDenomination("01");
		// 单位：捆
		stoGoodSelect.setUnit("101");
		stoGoods.setStoGoodSelect(stoGoodSelect);

		// 计算物品价值
		StoRelevance stoRelevance = new StoRelevance();
		stoRelevance.setDenomination(stoGoodSelect.getDenomination());
		stoRelevance.setUnit(stoGoodSelect.getUnit());
		stoRelevance.setCurrencyRefCode("hkd");
		stoRelevance.setCashRefCode("p");
		stoGoods.setGoodsVal(stoGoodsService.calcGoodsVal(stoRelevance));
		// 返回
		return stoGoods;
	}

}
