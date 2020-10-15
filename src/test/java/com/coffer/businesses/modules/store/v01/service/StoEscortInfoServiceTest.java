package com.coffer.businesses.modules.store.v01.service;

import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;

import com.coffer.businesses.common.Constant;
import com.coffer.businesses.modules.store.v01.entity.StoEscortInfo;
import com.coffer.core.common.persistence.Page;
import com.coffer.core.common.test.SpringTransactionalContextTests;
import com.coffer.core.common.utils.CacheUtils;
import com.coffer.core.modules.sys.entity.Office;
import com.google.common.collect.Maps;

/**
 * 类说明注释
 * 
 * @author niguoyong
 * @version 2015年9月10日
 */
public class StoEscortInfoServiceTest extends SpringTransactionalContextTests {

	@Autowired
	private StoEscortInfoService stoEscortInfoService;

	/**
	 * 测试get取得一条数据
	 */
	@Test
	public void testGetString() {
		// 数据准备
		String id = "20150731140640739";
		// 执行测试方法
		StoEscortInfo stoEscortInfo = stoEscortInfoService.get(id);
		// 测试结果验证
		Assert.assertNotNull("查询数据成功", stoEscortInfo);
	}

	/**
	 * 测试FindList取得一条数据
	 */
	@Test
	public void testFindListStoEscortInfo() {
		// 数据准备
		StoEscortInfo stoEscortInfo = new StoEscortInfo();
		stoEscortInfo.setIdcardNo("32118119871027463");
		stoEscortInfo.setEscortName("张");
		stoEscortInfo.setEscortType("90");
		stoEscortInfo.setOffice(new Office());
		stoEscortInfo.getOffice().setId("10000001");
		// 执行测试方法
		List<StoEscortInfo> list = stoEscortInfoService.findList(stoEscortInfo);
		// 测试结果验证
		Assert.assertEquals("查询数据成功", list.size(), 1);
	}

	/**
	 * 测试FindPage取得一条数据
	 */
	@Test
	public void testFindPagePageOfStoEscortInfoStoEscortInfo() {
		// 数据准备
		Page<StoEscortInfo> page = new Page<StoEscortInfo>();
		page.setPageNo(1);
		page.setPageSize(90);
		StoEscortInfo stoEscortInfo = new StoEscortInfo();
		stoEscortInfo.setEscortType("90");
		// 执行测试方法
		Page<StoEscortInfo> list = stoEscortInfoService.findPage(page, stoEscortInfo);
		// 测试结果验证
		Assert.assertTrue("查询数据成功", list.getList().size()>0);
	}

	/**
	 * 测试Save登录数据
	 */
	@Test
	@Rollback(true)
	public void testSaveStoEscortInfo() {
		// 数据准备
		StoEscortInfo stoEscortInfo = new StoEscortInfo();
		stoEscortInfo.setEscortName("程澍");
		stoEscortInfo.setEscortType("90");
		stoEscortInfo.setIdcardNo("420101197106287428");
		stoEscortInfo.setPhone("13845678903");
		stoEscortInfo.setOffice(new Office());
		stoEscortInfo.getOffice().setId("10000001");

		// 执行测试方法
		stoEscortInfoService.save(stoEscortInfo);
		// 测试结果验证
		Assert.assertNotNull("登陆成功", stoEscortInfoService.findByIdcardNo("420101197106287428"));
	}

	/**
	 * 测试Save更新数据
	 */
	@Test
	@Rollback(true)
	public void testSaveStoEscortInfo2() {
		// 数据准备
		StoEscortInfo stoEscortInfo = stoEscortInfoService.findByIdcardNo("321181198710274635");
		stoEscortInfo.setEscortName("张可");
		stoEscortInfo.setEscortType("90");
		stoEscortInfo.setIdcardNo("321181198710274635");
		stoEscortInfo.setPhone("13811111111");
		stoEscortInfo.setDelFlag("0");
		stoEscortInfo.setOffice(new Office());
		stoEscortInfo.getOffice().setId("10000001");

		// 执行测试方法
		stoEscortInfoService.save(stoEscortInfo);
		// 测试结果验证
		Assert.assertEquals("更新成功", stoEscortInfoService.findByIdcardNo("321181198710274635").getPhone(), "13811111111");
	}

	/**
	 * 测试Save更新数据
	 */
	@Test
	@Rollback(true)
	public void testupdateBinding() {
		// 数据准备
		StoEscortInfo hisStoEscort = stoEscortInfoService.findByIdcardNo("321181198710274635");
		hisStoEscort.setBindingRoute(Constant.Escort.BINDING_ROUTE);
		// 执行测试方法
		stoEscortInfoService.updateBinding(hisStoEscort);
		// 测试结果验证
		Assert.assertEquals("更新成功", stoEscortInfoService.findByIdcardNo("321181198710274635").getBindingRoute(),
				Constant.Escort.BINDING_ROUTE);
	}

	/**
	 * 测试GetStoEscortinfoList取得下拉列表
	 */
	@Test
	public void testGetStoEscortinfoList() {
		// 数据准备
		// 执行测试方法
		List<StoEscortInfo> list = stoEscortInfoService.getStoEscortinfoList(Constant.Escort.NO_BINDING_ESCORT_LIST);
		// 测试结果验证
		Assert.assertTrue("取得成功", list.size() > 0);

		// 测试方法2
		List<StoEscortInfo> list2 = stoEscortInfoService.getStoEscortinfoList("aaaaaa");
		// 测试结果验证
		Assert.assertFalse("取得失败", list2.size() > 0);
	}

	/**
	 * 测试Delete删除
	 */
	@Test
	@Rollback(true)
	public void testDeleteStoEscortInfo() {
		// 数据准备
		StoEscortInfo hisStoEscort = stoEscortInfoService.findByIdcardNo("420101197106287428");
		// 执行测试方法
		stoEscortInfoService.delete(hisStoEscort);
		// 测试结果验证
		Assert.assertNull("删除成功", stoEscortInfoService.findByIdcardNo("420101197106287428"));
	}

	/**
	 * 测试FindByIdcardNo用id检索
	 */
	@Test
	public void testFindByIdcardNo() {
		// 执行测试方法
		StoEscortInfo hisStoEscort = stoEscortInfoService.findByIdcardNo("321181198710274635");
		// 测试结果验证
		Assert.assertTrue("取得成功", hisStoEscort.getIdcardNo().equals("321181198710274635"));

		// 执行测试方法
		StoEscortInfo hisStoEscort2 = stoEscortInfoService.findByIdcardNo("420101197106287428");
		// 测试结果验证
		Assert.assertNull("取得失败", hisStoEscort2);
	}

	/**
	 * 测试SearchEscort用id检索接口
	 */
	@Test
	public void testSearchEscort() {
		// 数据准备
		Map<String, Object> headInfo = Maps.newHashMap();
		headInfo.put("idcardNo", "321181198710274635");

		// 执行测试方法
		StoEscortInfo hisStoEscort = stoEscortInfoService.searchEscort(headInfo);
		// 测试结果验证
		Assert.assertTrue("取得成功", hisStoEscort.getIdcardNo().equals("321181198710274635"));
	}

	/**
	 * 测试UpdateEscortInfo完善人员信息
	 */
	@Test
	@Rollback(true)
	public void testUpdateEscortInfo() {
		// 数据准备
		Map<String, Object> headInfo = Maps.newHashMap();
		headInfo.put("escortId", "20150831161216575");
		headInfo.put("escortName", "张三三");
		headInfo.put("address", "大连市春田园");
		headInfo.put("identityBirth", "19870101");
		headInfo.put("identityVisa", "大连市公安局");
		headInfo.put("identityGender", "男");
		headInfo.put("identityNational", "汉");
		byte[] a = { 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1 };
		byte[] b = { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
		headInfo.put("fingerNo1", a);
		headInfo.put("fingerNo2", a);
		headInfo.put("pdaFingerNo1", b);
		headInfo.put("pdaFingerNo2", b);
		// 执行测试方法
		try {
			stoEscortInfoService.updateEscortInfo(headInfo);
		} catch (Exception e) {
			Assert.assertTrue("异常", true);
		}

		// 测试结果验证
		StoEscortInfo hisStoEscort = stoEscortInfoService.findByIdcardNo("320421196704253213");
		Assert.assertEquals("更新成功", hisStoEscort.getEscortName(), "张三三");
		Assert.assertEquals("更新成功", hisStoEscort.getAddress(), "大连市春田园");
		Assert.assertEquals("更新成功", hisStoEscort.getIdentityBirth(), "19870101");
		Assert.assertEquals("更新成功", hisStoEscort.getIdentityVisa(), "大连市公安局");
		Assert.assertEquals("更新成功", hisStoEscort.getIdentityGender(), "男");
		Assert.assertEquals("更新成功", hisStoEscort.getIdentityNational(), "汉");
		// BASE64解密
		//BASE64Decoder dencode = new BASE64Decoder();
		// Assert.assertEquals("更新成功", hisStoEscort.getFingerNo1(),
		// dencode.decodeBuffer(a.toString()));
		// Assert.assertEquals("更新成功", hisStoEscort.getPdaFingerNo1(),
		// dencode.decodeBuffer(b.toString()));

	}

	/**
	 * 测试CheckIdcardNo用身份证检索
	 */
	@Test
	public void testCheckIdcardNo() {
		// 数据准备
		// 执行测试方法
		Assert.assertNotNull(stoEscortInfoService.checkIdcardNo("321181198710274635"));
	}

	@Test
	public void testFindByUserId() {
		// 数据准备
		// 执行测试方法
		Assert.assertNotNull(stoEscortInfoService.findByUserId("3d52b7c287a54f55b387718f39924ede"));
	}

	@SuppressWarnings("static-access")
	@Test
	@Rollback(true)
	public void testDeleteString() {
		// 数据准备
		CacheUtils.put(stoEscortInfoService.CACHE_STOESCORT_MAP, "");
		// 执行测试方法
		stoEscortInfoService.delete("20150911112623858");
		// 测试结果验证
		Assert.assertNull("删除成功", stoEscortInfoService.findByIdcardNo("20150911112623858"));
		Assert.assertNull("删除成功", CacheUtils.get(stoEscortInfoService.CACHE_STOESCORT_MAP));
	}

}
