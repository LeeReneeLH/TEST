/**
 * @author John
 * @version 2015年5月26日
 * 
 * 
 */
package com.coffer.businesses.modules.store.v01.service;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.coffer.businesses.modules.store.v01.entity.StoEscortInfo;
import com.coffer.businesses.modules.store.v01.entity.StoStoresInfo;
import com.coffer.core.common.test.SpringTransactionalContextTests;
import com.coffer.core.modules.sys.entity.Office;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

/**
 * @author John
 * 
 */
public class StoStoreInfoServiceTest extends SpringTransactionalContextTests {

	@Autowired
	private StoStoresInfoService stoStoresInfoService;


	/**
	 * 
	 * @author LLF
	 * @version 2015年9月14日
	 * 
	 *          测试库房物品变更
	 */
	@Test
	public void changeStore() {
		
		// 测试数据准备
		List<Map<String,Object>> list = Lists.newArrayList();
		Map<String,Object> map = Maps.newHashMap();
		map.put("goodsId", "000000000001");
		map.put("userId", "0203010001");
		map.put("num", 20);
		list.add(map);
		
		//String message = stoStoresInfoService.changeStore(list, "10000002", "150914200006", "42");
		
		//System.out.println(message);
		StoStoresInfo stoStoresInfo1 = new StoStoresInfo();
		stoStoresInfo1.setCreateDate(new Date());
		stoStoresInfo1.setOffice(new Office("10000001"));
		stoStoresInfo1.setStoId("92031223132");
		stoStoresInfo1.setIsNewRecord(true);
		stoStoresInfoService.save(stoStoresInfo1);
		
		StoStoresInfo stoStoresInfo2 = new StoStoresInfo();
		stoStoresInfo2.setCreateDate(new Date());
		stoStoresInfo2.setOffice(new Office("10000001"));
		stoStoresInfo2.setIsNewRecord(true);
		stoStoresInfo2.setStoId("99901223132");
		stoStoresInfoService.save(stoStoresInfo2);
		
		
		StoStoresInfo stoStoresInfo = new StoStoresInfo();
		stoStoresInfo.setOffice(new Office("10000001"));
		stoStoresInfo.setCreateDate(new Date());
		List<StoStoresInfo> stoStoresInfoList = stoStoresInfoService.findList(stoStoresInfo);
		System.out.println(stoStoresInfoList);
		Assert.assertEquals(stoStoresInfoList.size(), 2);
	}
	
	@Test
	public void changeStoreSub() {
		
		// 测试数据准备
		List<Map<String,Object>> list = Lists.newArrayList();
		Map<String,Object> map = Maps.newHashMap();
		map.put("goodsId", "000000000001");
		map.put("userId", "0203010001");
		map.put("num", -5);
		list.add(map);
		
		//String message = stoStoresInfoService.changeStore(list, "10000002", "150914200006", "42");
		
		//System.out.println(message);
		
		StoStoresInfo stoStoresInfo2 = new StoStoresInfo();
		stoStoresInfo2.setCreateDate(new Date());
		stoStoresInfo2.setOffice(new Office("10000001"));
		stoStoresInfo2.setIsNewRecord(true);
		stoStoresInfo2.setStoId("99901223132");
		stoStoresInfo2.setStoNum((long)2);
		stoStoresInfoService.save(stoStoresInfo2);
		
		StoStoresInfo stoStoresInfo = new StoStoresInfo();
		stoStoresInfo.setOffice(new Office("10000001"));
		stoStoresInfo.setCreateDate(new Date());
		List<StoStoresInfo> stoStoresInfoList = stoStoresInfoService.findList(stoStoresInfo);
		System.out.println(stoStoresInfoList.get(0));
		long s = stoStoresInfoList.get(0).getStoNum();
		Assert.assertEquals(s, (long)2);
	}
	
	

}
