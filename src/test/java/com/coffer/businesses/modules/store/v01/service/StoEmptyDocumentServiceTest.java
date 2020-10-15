package com.coffer.businesses.modules.store.v01.service;

import java.util.List;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.coffer.businesses.modules.store.v01.entity.ChangeStoreEntity;
import com.coffer.businesses.modules.store.v01.entity.StoEmptyDocument;
import com.coffer.core.common.test.SpringTransactionalContextTests;
import com.google.common.collect.Lists;

public class StoEmptyDocumentServiceTest extends SpringTransactionalContextTests {
	
	@Autowired
	StoEmptyDocumentService stoEmptyDocumentService;
	@Test
	public void testChangeBlankBillStores() {
		
		List<StoEmptyDocument> list = stoEmptyDocumentService.findList(null);
		
		ChangeStoreEntity changeStoreEntity = new ChangeStoreEntity();
		
		changeStoreEntity.setGoodsId("301010000000");
		changeStoreEntity.setNum(3l);
		
		List<ChangeStoreEntity> changeList = Lists.newArrayList();
		changeList.add(changeStoreEntity);
		
		stoEmptyDocumentService.changeBlankBillStores(changeList, "28900000");
		
		List<StoEmptyDocument> resultList = stoEmptyDocumentService.findList(null);
		
		System.out.println("list:"+list);
		System.out.println("resultList:"+resultList);
	}

}
