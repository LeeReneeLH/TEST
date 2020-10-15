package com.coffer.businesses.modules.store.v01.service;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.coffer.businesses.modules.store.v01.entity.StoDict;
import com.coffer.core.common.persistence.Page;
import com.coffer.core.common.test.SpringTransactionalContextTests;

/**
 * 测试物品字典Service
 * 
 * @author yuxixuan
 *
 */
public class StoDictServiceTest extends SpringTransactionalContextTests {

	@Autowired
	private StoDictService stoDictService;


	/**
	 * 测试保存物品字典
	 */
	@Test
	public void testSaveRelevance() {
		// 1、准备数据
		StoDict dict = makeParam();
		// 2、保存（新增）
		stoDictService.save(dict);
		// 3、查询列表（新增）
		Page<StoDict> pageAdd = new Page<StoDict>();
		StoDict searchParamAdd = new StoDict();
		// 类型
		searchParamAdd.setType("currency");
		// 描述
		searchParamAdd.setDescription("币种");
		Page<StoDict> resultPageAdd = stoDictService.findPage(pageAdd, searchParamAdd);
		// 4、对比件数（新增）
		Assert.assertEquals(7, resultPageAdd.getList().size());
		// 5、对比列表记录（新增）
		StoDict resultRecordAdd = resultPageAdd.getList().get(6);
		// 键值
		Assert.assertEquals("107", resultRecordAdd.getValue());
		// 标签
		Assert.assertEquals("韩元", resultRecordAdd.getLabel());
		// 类型
		Assert.assertEquals("currency", resultRecordAdd.getType());
		// 描述
		Assert.assertEquals("币种", resultRecordAdd.getDescription());
		// 排序
		Assert.assertEquals(Long.valueOf(70L), resultRecordAdd.getSort());
		// 计算列
		Assert.assertEquals(null, resultRecordAdd.getUnitVal());
		// 关联代码
		Assert.assertEquals("krw", resultRecordAdd.getRefCode());
		// 6、查询明细（新增）
		StoDict resultDetailAdd = stoDictService.get(resultRecordAdd.getId());
		// 7、对比明细结果（新增）
		// 键值
		Assert.assertEquals("107", resultDetailAdd.getValue());
		// 标签
		Assert.assertEquals("韩元", resultDetailAdd.getLabel());
		// 类型
		Assert.assertEquals("currency", resultDetailAdd.getType());
		// 描述
		Assert.assertEquals("币种", resultDetailAdd.getDescription());
		// 排序
		Assert.assertEquals(Long.valueOf(70L), resultDetailAdd.getSort());
		// 计算列
		Assert.assertEquals(null, resultDetailAdd.getUnitVal());
		// 关联代码
		Assert.assertEquals("krw", resultDetailAdd.getRefCode());
		// 备注
		Assert.assertEquals("测试备注", resultDetailAdd.getRemarks());
		// 8、保存（修改）
		// 键值
		resultDetailAdd.setValue("108");
		// 标签
		resultDetailAdd.setLabel("卢布");
		// 类型
		resultDetailAdd.setType("currency");
		// 描述
		resultDetailAdd.setDescription("币种");
		// 排序
		resultDetailAdd.setSort(Long.valueOf(70L));
		// 计算列
		resultDetailAdd.setUnitVal(null);
		// 关联代码
		resultDetailAdd.setRefCode("rub");
		// 备注
		resultDetailAdd.setRemarks("测试修改备注");
		stoDictService.save(resultDetailAdd);

		// 9、查询列表（修改）
		Page<StoDict> pageEdit = new Page<StoDict>();
		StoDict searchParamEdit = new StoDict();
		// 类型
		searchParamEdit.setType("currency");
		// 描述
		searchParamEdit.setDescription("币种");
		Page<StoDict> resultPageEdit = stoDictService.findPage(pageEdit, searchParamEdit);
		// 10、查询明细（修改）
		StoDict resultRecordEdit = resultPageEdit.getList().get(6);
		StoDict resultDetailEdit = stoDictService.get(resultRecordEdit.getId());
		// 11、对比明细结果（修改）
		// 键值
		Assert.assertEquals("108", resultDetailEdit.getValue());
		// 标签
		Assert.assertEquals("卢布", resultDetailEdit.getLabel());
		// 类型
		Assert.assertEquals("currency", resultDetailEdit.getType());
		// 描述
		Assert.assertEquals("币种", resultDetailEdit.getDescription());
		// 排序
		Assert.assertEquals(Long.valueOf(70L), resultDetailEdit.getSort());
		// 计算列
		Assert.assertEquals(null, resultDetailEdit.getUnitVal());
		// 关联代码
		Assert.assertEquals("rub", resultDetailEdit.getRefCode());
		// 备注
		Assert.assertEquals("测试修改备注", resultDetailEdit.getRemarks());
	}

	/**
	 * 测试删除物品字典
	 */
	@Test
	public void testDelete() {
		// 1、准备数据
		StoDict dict = makeParam();
		// 2、保存（新增）
		stoDictService.save(dict);
		// 3、查询列表（新增）
		Page<StoDict> pageAdd = new Page<StoDict>();
		StoDict searchParamAdd = new StoDict();
		// 类型
		searchParamAdd.setType("currency");
		// 描述
		searchParamAdd.setDescription("币种");
		Page<StoDict> resultPageAdd = stoDictService.findPage(pageAdd, searchParamAdd);
		// 4、对比件数（新增）
		Assert.assertEquals(7, resultPageAdd.getList().size());
		// 5、删除记录
		stoDictService.delete(resultPageAdd.getList().get(6));
		// 6、再次查询
		Page<StoDict> resultPageAdd2 = stoDictService.findPage(pageAdd, searchParamAdd);
		// 7、对比件数
		Assert.assertEquals(resultPageAdd2.getList().size(), 6);
	}

	/**
	 * 准备数据
	 * 
	 * @return
	 */
	private StoDict makeParam() {
		StoDict dict = new StoDict();
		// 键值
		dict.setValue("107");
		// 标签
		dict.setLabel("韩元");
		// 类型
		dict.setType("currency");
		// 描述
		dict.setDescription("币种");
		// 排序
		dict.setSort(70L);
		// 计算列
		dict.setUnitVal(null);
		// 关联代码
		dict.setRefCode("krw");
		// 备注
		dict.setRemarks("测试备注");

		return dict;
	}
}
