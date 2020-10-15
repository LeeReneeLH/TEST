package com.coffer.businesses.modules.common.service;



import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.coffer.businesses.modules.common.AjaxBusinessUtils;
import com.coffer.businesses.modules.common.AjaxConstant;
import com.coffer.businesses.modules.common.entity.ReceiveEntity;
import com.coffer.businesses.modules.store.v01.entity.ChangeStoreEntity;
import com.coffer.businesses.modules.store.v01.entity.StoDict;
import com.coffer.businesses.modules.store.v01.service.StoStoresInfoService;
import com.coffer.core.common.config.Global;
import com.coffer.core.common.test.SpringTransactionalContextTests;
import com.coffer.core.common.utils.DateUtils;
import com.coffer.core.modules.sys.entity.Office;
import com.coffer.core.modules.sys.entity.User;
import com.coffer.core.modules.sys.service.SystemService;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

/**
 * 类说明注释
 * 
 * @author niguoyong
 * @version 2015年10月15日
 */
public class IpadAjaxServiceTest extends SpringTransactionalContextTests {

	@Autowired
	private IpadAjaxService ipadAjaxService;
	
	@Autowired
	private  StoStoresInfoService stoStoresInfoService;

	@Autowired
	private SystemService systemService;

	/**
	 * Json实例对象
	 */
	protected static Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation() // 不导出实体中没有用@Expose注解的属性
			.enableComplexMapKeySerialization() // 支持Map的key为复杂对象的形式
			.serializeNulls().setDateFormat("yyyy-MM-dd HH:mm:ss")// 时间转化为特定格式
			// .setFieldNamingPolicy(FieldNamingPolicy.UPPER_CAMEL_CASE)//
			// 会把字段首字母大写,注:对于实体上使用了@SerializedName注解的不会生效.
			.setPrettyPrinting() // 对json结果格式化.
			// .setVersion(1.0)
			// //有的字段不是一开始就有的,会随着版本的升级添加进来,那么在进行序列化和返序列化的时候就会根据版本号来选择是否要序列化.
			// @Since(版本号)能完美地实现这个功能.还的字段可能,随着版本的升级而删除,那么
			// @Until(版本号)也能实现这个功能,GsonBuilder.setVersion(double)方法需要调用.
			.create();

	/**
	 * 同步数据字典信息
	 * 
	 * @author niguoyong
	 * @version 2015年10月15日
	 */
	@Test
	public void testGetGoodsDictInfo() {
		// 数据准备
		String serviceNo = "13";
		ReceiveEntity entity = new ReceiveEntity();
		SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyyMMddHHmmssSSS");
		try {
			Date d = sdf.parse("20150901010101000");
			// entity.setSearchDate(DateUtils.formatDate(d,
			// "yyyyMMddHHmmssSSS"));
		} catch (ParseException e) {
			e.printStackTrace();
		}

		// 执行测试方法
		String str = ipadAjaxService.getGoodsDictInfo(serviceNo, entity);

		// 测试结果验证
		Map<String, Object> map = gson.fromJson(str, new TypeToken<Map<String, Object>>() {
		}.getType());
		Assert.assertEquals("查询数据成功", map.get("resultFlag"), AjaxConstant.Interface.RESULT_FLAG_ACCESS);
		List<StoDict> dictList = (List<StoDict>) map.get("list");
		Assert.assertTrue("查询数据成功", dictList.size() > 0);
	}

	/**
	 * 同步物品信息-参数异常
	 * 
	 * @author niguoyong
	 * @version 2015年10月16日
	 */
	@Test
	public void testGetGoodsAndStores() {
		// 数据准备
		String serviceNo = "14";
		ReceiveEntity entity = new ReceiveEntity();
		SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyyMMddHHmmssSSS");
		try {
			Date d = sdf.parse("20150901010101000");
			entity.setSearchDate(DateUtils.formatDate(d, "yyyyMMddHHmmssSSS"));
		} catch (ParseException e) {
			e.printStackTrace();
		}

		// 执行测试方法
		String str = ipadAjaxService.getGoodsAndStores(serviceNo, entity);

		// 测试结果验证
		Map<String, Object> map = gson.fromJson(str, new TypeToken<Map<String, Object>>() {
		}.getType());
		Assert.assertEquals("查询数据成功", map.get("resultFlag"), AjaxConstant.Interface.RESULT_FLAG_FAILURE);
		Assert.assertEquals("查询数据成功", map.get("errorNo"), AjaxConstant.Interface.ERROR_NO_E03);
	}

	/**
	 * 同步物品信息-成功
	 * 
	 * @author niguoyong
	 * @version 2015年10月16日
	 */
	@Test
	public void testGetGoodsAndStores2() {
		// 数据准备
		String serviceNo = "14";
		ReceiveEntity entity = new ReceiveEntity();
		entity.setOfficeId("10000001");
		SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyyMMddHHmmssSSS");
		try {
			Date d = sdf.parse("20150901010101000");
			entity.setSearchDate(DateUtils.formatDate(d, "yyyyMMddHHmmssSSS"));
		} catch (ParseException e) {
			e.printStackTrace();
		}

		// 执行测试方法
		String str = ipadAjaxService.getGoodsAndStores(serviceNo, entity);

		// 测试结果验证
		Map<String, Object> map = gson.fromJson(str, new TypeToken<Map<String, Object>>() {
		}.getType());
		Assert.assertEquals("查询数据成功", map.get("resultFlag"), AjaxConstant.Interface.RESULT_FLAG_ACCESS);
		List<Map<String, Object>> list = (List<Map<String, Object>>) map.get("list");
		Assert.assertTrue("查询数据成功", list.size() > 0);
	}

	/**
	 * 同步字典关联关系
	 * 
	 * @author niguoyong
	 * @version 2015年10月19日
	 */
	@Test
	public void testGetStockCountRelevance() {
		// 数据准备
		String serviceNo = "15";
		ReceiveEntity entity = new ReceiveEntity();

		// 执行测试方法
		String str = ipadAjaxService.getStockCountRelevance(serviceNo, entity);

		// 测试结果验证
		Map<String, Object> map = gson.fromJson(str, new TypeToken<Map<String, Object>>() {
		}.getType());
		Assert.assertEquals("查询数据成功", map.get("resultFlag"), AjaxConstant.Interface.RESULT_FLAG_ACCESS);
		List<Map<String, Object>> list = (List<Map<String, Object>>) map.get("list");
		Assert.assertTrue("查询数据成功", list.size() > 0);
	}

	/**
	 * 上传盘点信息-成功
	 * 
	 * @author niguoyong
	 * @version 2015年10月19日
	 */
	public void testUploadStockCountInfo() {
		// 数据准备
		String serviceNo = "16";
		ReceiveEntity entity = new ReceiveEntity();
		entity.setOfficeId("10000001");
		entity.setUserId("2049c8b9209144bfa7e5b671cd2e49d2");
		List<Map<String, Object>> list = Lists.newArrayList();
		Map<String, Object> map1 = Maps.newHashMap();
		map1.put("goods_type", "01");
		map1.put("goods_id", "101015101101");
		map1.put("sto_num", 2);
		Map<String, Object> map2 = Maps.newHashMap();
		map2.put("goods_type", "01");
		map2.put("goods_id", "101025101101");
		map2.put("sto_num", 2);
		Map<String, Object> map3 = Maps.newHashMap();
		map3.put("goods_type", "01");
		map3.put("goods_id", "101025102101");
		map3.put("sto_num", 2);
		list.add(map1);
		list.add(map2);
		list.add(map3);
		entity.setList(list);

		// 执行测试方法
		String str = ipadAjaxService.uploadStockCountInfo(serviceNo, entity);

		// 测试结果验证
		Map<String, Object> map = gson.fromJson(str, new TypeToken<Map<String, Object>>() {
		}.getType());
		Assert.assertEquals("查询数据成功", map.get("resultFlag"), AjaxConstant.Interface.RESULT_FLAG_ACCESS);
		Assert.assertNotNull("查询数据成功", map.get("sto_no"));
	}

	/**
	 * 上传盘点信息-失败
	 * 
	 * @author niguoyong
	 * @version 2015年10月19日
	 */
	@Test
	public void testUploadStockCountInfo2() {
		// 数据准备
		String serviceNo = "16";
		ReceiveEntity entity = new ReceiveEntity();
		entity.setOfficeId("10000001");
		entity.setUserId("2049c8b9209144bfa7e5b671cd2e49d2");
		List<Map<String, Object>> list = Lists.newArrayList();
		entity.setList(list);

		// 执行测试方法
		String str = ipadAjaxService.uploadStockCountInfo(serviceNo, entity);

		// 测试结果验证
		Map<String, Object> map = gson.fromJson(str, new TypeToken<Map<String, Object>>() {
		}.getType());
		Assert.assertEquals("查询数据成功", map.get("resultFlag"), AjaxConstant.Interface.RESULT_FLAG_FAILURE);
		Assert.assertEquals("查询数据成功", map.get("errorNo"), AjaxConstant.Interface.ERROR_NO_E03);
	}

	/**
	 * 上传盘点信息-异常
	 * 
	 * @author niguoyong
	 * @version 2015年10月19日
	 */
	public void testUploadStockCountInfo3() {
		// 数据准备
		String serviceNo = "16";
		ReceiveEntity entity = new ReceiveEntity();
		entity.setOfficeId("10000001");
		entity.setUserId("2049c8b9209144bfa7e5b671cd2e49d2");
		List<Map<String, Object>> list = Lists.newArrayList();
		Map<String, Object> map1 = Maps.newHashMap();
		map1.put("goods_type", "01");
		map1.put("goods_id", "101015101101");
		map1.put("sto_num", -20);
		list.add(map1);

		entity.setList(list);

		// 执行测试方法
		ipadAjaxService.uploadStockCountInfo(serviceNo, entity);
	}

	/**
	 * 盘点信息更新库存-失败-参数错误
	 * 
	 * @author niguoyong
	 * @version 2015年10月19日
	 */
	@Test
	public void testUpdateStoreByStockCount() {
		// 数据准备
		String serviceNo = "17";
		ReceiveEntity entity = new ReceiveEntity();

		// 执行测试方法
		String str = ipadAjaxService.updateStoreByStockCount(serviceNo, entity);
		// 测试结果验证
		Map<String, Object> map = gson.fromJson(str, new TypeToken<Map<String, Object>>() {
		}.getType());
		Assert.assertEquals("查询数据成功", map.get("resultFlag"), AjaxConstant.Interface.RESULT_FLAG_FAILURE);
		Assert.assertEquals("查询数据成功", map.get("errorNo"), AjaxConstant.Interface.ERROR_NO_E03);
	}

	/**
	 * 盘点信息更新库存-失败-用户不存在
	 * 
	 * @author niguoyong
	 * @version 2015年10月19日
	 */
	@Test
	public void testUpdateStoreByStockCount2() {
		// 数据准备
		String serviceNo = "17";
		ReceiveEntity entity = new ReceiveEntity();
		entity.setOfficeId("10000001");
		entity.setUserId("d658575b3c9a4be7b740d2447c152add");
		entity.setLoginName("123");
		entity.setPassword("202cb962ac59075b964b07152d234b70");

		// 执行测试方法
		String str = ipadAjaxService.updateStoreByStockCount(serviceNo, entity);
		// 测试结果验证
		Map<String, Object> map = gson.fromJson(str, new TypeToken<Map<String, Object>>() {
		}.getType());
		Assert.assertEquals("查询数据成功", map.get("resultFlag"), AjaxConstant.Interface.RESULT_FLAG_FAILURE);
		Assert.assertEquals("查询数据成功", map.get("errorNo"), AjaxConstant.Interface.ERROR_NO_E04);
	}

	/**
	 * 盘点信息更新库存-失败-无最新盘点信息更新库存
	 * 
	 * @author niguoyong
	 * @version 2015年10月19日
	 */
	@Test
	public void testUpdateStoreByStockCount3() {
		// 数据准备
		String serviceNo = "17";
		ReceiveEntity entity = new ReceiveEntity();
		entity.setOfficeId("10000001");
		entity.setUserId("2049c8b9209144bfa7e5b671cd2e49d2");
		entity.setLoginName("jm001");
		entity.setPassword("202cb962ac59075b964b07152d234b70");

		// 执行测试方法
		String str = ipadAjaxService.updateStoreByStockCount(serviceNo, entity);

		// 测试结果验证
		Map<String, Object> map = gson.fromJson(str, new TypeToken<Map<String, Object>>() {
		}.getType());
		Assert.assertEquals("查询数据成功", map.get("resultFlag"), AjaxConstant.Interface.RESULT_FLAG_FAILURE);
		Assert.assertEquals("查询数据成功", map.get("errorNo"), AjaxConstant.Interface.ERROR_NO_E07);
	}

	/**
	 * 盘点信息更新库存-失败-无最新盘点信息更新库存
	 * 
	 * @author niguoyong
	 * @version 2015年10月19日
	 */
	@Test
	public void testUpdateStoreByStockCount4() {
		// 上传盘点
		testUploadStockCountInfo();
		
		//更新库存
		Office currentOffice = new Office();
		currentOffice.setId("10000001");
		currentOffice.setType("1");
		currentOffice.setCode("0001");
		String sto_no = AjaxBusinessUtils.getNewBusinessNo(Global.getConfig("businessType.store.stock"), currentOffice);
		List<ChangeStoreEntity> list = new ArrayList<ChangeStoreEntity>();
		ChangeStoreEntity changeStoreEntity = new ChangeStoreEntity();
		changeStoreEntity.setGoodsId("101015101101");
		changeStoreEntity.setNum((long) 10);
		ChangeStoreEntity changeStoreEntity2 = new ChangeStoreEntity();
		changeStoreEntity2.setGoodsId("101025101101");
		changeStoreEntity2.setNum((long) 10);
		ChangeStoreEntity changeStoreEntity3 = new ChangeStoreEntity();
		changeStoreEntity3.setGoodsId("101025102101");
		changeStoreEntity3.setNum((long) 10);
		list.add(changeStoreEntity);
		list.add(changeStoreEntity2);
		list.add(changeStoreEntity3);

		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
//		StoreCommonUtils.changeStoreAndSurplusStores(list, "10000001",sto_no, systemService.getUser("2049c8b9209144bfa7e5b671cd2e49d2"));
		stoStoresInfoService.changeStore(list, "10000001",sto_no, systemService.getUser("2049c8b9209144bfa7e5b671cd2e49d2"));

		// 数据准备
		String serviceNo = "17";
		ReceiveEntity entity = new ReceiveEntity();
		entity.setOfficeId("10000001");
		entity.setUserId("2049c8b9209144bfa7e5b671cd2e49d2");
		entity.setLoginName("jm001");
		entity.setPassword("202cb962ac59075b964b07152d234b70");

		// 执行测试方法
		String str = ipadAjaxService.updateStoreByStockCount(serviceNo, entity);

		// 测试结果验证
		Map<String, Object> map = gson.fromJson(str, new TypeToken<Map<String, Object>>() {
		}.getType());
		Assert.assertEquals("查询数据成功", map.get("resultFlag"), AjaxConstant.Interface.RESULT_FLAG_FAILURE);
		Assert.assertEquals("查询数据成功", map.get("errorNo"), AjaxConstant.Interface.ERROR_NO_E07);
	}

	/**
	 * 盘点信息更新库存-成功
	 * 
	 * @author niguoyong
	 * @version 2015年10月19日
	 */
	@Test
	public void testUpdateStoreByStockCount5() {
		// 上传盘点
		testUploadStockCountInfo();
		// 数据准备
		String serviceNo = "17";
		ReceiveEntity entity = new ReceiveEntity();
		entity.setOfficeId("10000001");
		entity.setUserId("2049c8b9209144bfa7e5b671cd2e49d2");
		entity.setLoginName("jm001");
		entity.setPassword("202cb962ac59075b964b07152d234b70");

		// 执行测试方法
		String str = ipadAjaxService.updateStoreByStockCount(serviceNo, entity);

		// 测试结果验证
		Map<String, Object> map = gson.fromJson(str, new TypeToken<Map<String, Object>>() {
		}.getType());
		Assert.assertEquals("查询数据成功", map.get("resultFlag"), AjaxConstant.Interface.RESULT_FLAG_ACCESS);

	}

	/**
	 * 盘点信息更新库存-失败-更新异常
	 * 
	 * @author niguoyong
	 * @version 2015年10月19日
	 */
	@Test
	public void testUpdateStoreByStockCount6() {
		// 上传盘点
		testUploadStockCountInfo3();
		// 数据准备
		String serviceNo = "17";
		ReceiveEntity entity = new ReceiveEntity();
		entity.setOfficeId("28900000");
		entity.setUserId("43bbe83e374543a094dc5eaeb785ea2e,cc52038077d84237bea3864f7302748f");
		entity.setLoginName("000003");
		entity.setPassword("202cb962ac59075b964b07152d234b70");

		// 执行测试方法
		String str = ipadAjaxService.updateStoreByStockCount(serviceNo, entity);

		// 测试结果验证
		Map<String, Object> map = gson.fromJson(str, new TypeToken<Map<String, Object>>() {
		}.getType());
		Assert.assertEquals("查询数据成功", map.get("resultFlag"), AjaxConstant.Interface.RESULT_FLAG_FAILURE);
		Assert.assertEquals("查询数据成功", map.get("errorNo"), AjaxConstant.Interface.ERROR_NO_E05);
	}

	/**
	 * 盘点用户登录-参数错误
	 * 
	 * @author niguoyong
	 * @version 2015年10月19日
	 */
	@Test
	public void testPadUserLogin() {
		// 数据准备
		String serviceNo = "30";
		ReceiveEntity entity = new ReceiveEntity();
		List<Map<String, Object>> list = Lists.newArrayList();
		entity.setList(list);
		// 执行测试方法
		String str = ipadAjaxService.padUserLogin(serviceNo, entity);

		// 测试结果验证
		Map<String, Object> map = gson.fromJson(str, new TypeToken<Map<String, Object>>() {
		}.getType());
		Assert.assertEquals("查询数据成功", map.get("resultFlag"), AjaxConstant.Interface.RESULT_FLAG_FAILURE);
		Assert.assertEquals("查询数据成功", map.get("errorNo"), AjaxConstant.Interface.ERROR_NO_E03);
	}

	/**
	 * 盘点用户登录-用户不存在
	 * 
	 * @author niguoyong
	 * @version 2015年10月19日
	 */
	@Test
	public void testPadUserLogin2() {
		// 数据准备
		String serviceNo = "30";
		ReceiveEntity entity = new ReceiveEntity();
		List<Map<String, Object>> list = Lists.newArrayList();

		Map<String, Object> map1 = Maps.newHashMap();
		map1.put("loginName", "01");
		map1.put("password", "101025101101");
		list.add(map1);
		entity.setList(list);
		// 执行测试方法
		String str = ipadAjaxService.padUserLogin(serviceNo, entity);

		// 测试结果验证
		Map<String, Object> map = gson.fromJson(str, new TypeToken<Map<String, Object>>() {
		}.getType());
		Assert.assertEquals("查询数据成功", map.get("resultFlag"), AjaxConstant.Interface.RESULT_FLAG_FAILURE);
		Assert.assertEquals("查询数据成功", map.get("errorNo"), AjaxConstant.Interface.ERROR_NO_E04);
	}

	/**
	 * 盘点用户登录-成功
	 * 
	 * @author niguoyong
	 * @version 2015年10月19日
	 */
	@Test
	public void testPadUserLogin3() {

		// 更新用户
		User user = systemService.getUser("d658575b3c9a4be7b740d2447c152add");
		user.setCsPermission("12");
		systemService.saveUser(user);
		// 数据准备
		String serviceNo = "30";
		ReceiveEntity entity = new ReceiveEntity();
		List<Map<String, Object>> list = Lists.newArrayList();

		Map<String, Object> map1 = Maps.newHashMap();
		map1.put("loginName", "admin");
		map1.put("password", "202cb962ac59075b964b07152d234b70");
		list.add(map1);
		entity.setList(list);
		// 执行测试方法
		String str = ipadAjaxService.padUserLogin(serviceNo, entity);

		// 测试结果验证
		Map<String, Object> map = gson.fromJson(str, new TypeToken<Map<String, Object>>() {
		}.getType());
		Assert.assertEquals("查询数据成功", map.get("resultFlag"), AjaxConstant.Interface.RESULT_FLAG_ACCESS);
		Assert.assertEquals("查询数据成功", ((List<Map<String, Object>>) map.get("list")).get(0).get("officeId"), "10000001");
	}

	/**
	 * 盘点所属机构下用户信息-失败
	 * 
	 * @author niguoyong
	 * @version 2015年10月19日
	 */
	@Test
	public void testGetUserByOffice() {
		// 数据准备
		String serviceNo = "31";
		ReceiveEntity entity = new ReceiveEntity();

		// 执行测试方法
		String str = ipadAjaxService.getUserByOffice(serviceNo, entity);

		// 测试结果验证
		Map<String, Object> map = gson.fromJson(str, new TypeToken<Map<String, Object>>() {
		}.getType());
		Assert.assertEquals("查询数据成功", map.get("resultFlag"), AjaxConstant.Interface.RESULT_FLAG_FAILURE);
		Assert.assertEquals("查询数据成功", map.get("errorNo"), AjaxConstant.Interface.ERROR_NO_E03);
	}

	/**
	 * 盘点所属机构下用户信息-成功
	 * 
	 * @author niguoyong
	 * @version 2015年10月19日
	 */
	@Test
	public void testGetUserByOffice2() {
		// 更新用户
		User user = systemService.getUser("d658575b3c9a4be7b740d2447c152add");
		user.setCsPermission("12");
		systemService.saveUser(user);

		// 数据准备
		String serviceNo = "31";
		ReceiveEntity entity = new ReceiveEntity();
		entity.setOfficeId("10000001");
		// 执行测试方法
		String str = ipadAjaxService.getUserByOffice(serviceNo, entity);

		// 测试结果验证
		Map<String, Object> map = gson.fromJson(str, new TypeToken<Map<String, Object>>() {
		}.getType());
		Assert.assertEquals("查询数据成功", map.get("resultFlag"), AjaxConstant.Interface.RESULT_FLAG_ACCESS);
		Assert.assertTrue("查询数据成功", ((List<Map<String, Object>>) map.get("list")).size() > 0);
	}

	/**
	 * 服务代码不存在，应答错误消息
	 * 
	 * @author niguoyong
	 * @version 2015年10月19日
	 */
	@Test
	public void testErro() {
		// 数据准备
		String serviceNo = "123";

		// 执行测试方法
		String str = ipadAjaxService.erro(serviceNo);

		// 测试结果验证
		Map<String, Object> map = gson.fromJson(str, new TypeToken<Map<String, Object>>() {
		}.getType());
		Assert.assertEquals("查询数据成功", map.get("resultFlag"), AjaxConstant.Interface.RESULT_FLAG_FAILURE);
		Assert.assertEquals("查询数据成功", map.get("errorNo"), AjaxConstant.Interface.ERROR_NO_E06);
	}

}
