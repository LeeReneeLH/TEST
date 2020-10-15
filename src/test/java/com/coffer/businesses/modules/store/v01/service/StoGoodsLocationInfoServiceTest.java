package com.coffer.businesses.modules.store.v01.service;

import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.coffer.businesses.modules.allocation.v02.entity.PbocAllAllocateInfo;
import com.coffer.core.common.test.SpringTransactionalContextTests;
import com.google.common.collect.Lists;

public class StoGoodsLocationInfoServiceTest  extends SpringTransactionalContextTests {
	
	@Autowired
    StoGoodsLocationInfoService stoGoodsLocationInfoService;

	@Test
	public void test() {
		
		List<PbocAllAllocateInfo> list = Lists.newArrayList();
		PbocAllAllocateInfo pbocAllAllocateInfo = new PbocAllAllocateInfo();
		pbocAllAllocateInfo.setAllId("000001160614500003");
		list.add(pbocAllAllocateInfo);
		List<Map<String,Object>> resultMap = stoGoodsLocationInfoService.findStoreAreaByInStoreId(list);
		System.out.println(resultMap);
	}

}
