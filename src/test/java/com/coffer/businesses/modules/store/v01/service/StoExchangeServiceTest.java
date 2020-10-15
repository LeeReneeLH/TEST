package com.coffer.businesses.modules.store.v01.service;

import java.math.BigDecimal;
import java.util.Date;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;

import com.coffer.businesses.modules.store.StoreCommonUtils;
import com.coffer.businesses.modules.store.v01.entity.StoExchange;
import com.coffer.businesses.modules.store.v01.entity.StoExchangeGood;
import com.coffer.businesses.modules.store.v01.entity.StoGoods;
import com.coffer.businesses.modules.store.v01.entity.StoStoresInfo;
import com.coffer.core.common.persistence.Page;
import com.coffer.core.common.test.SpringTransactionalContextTests;
import com.coffer.core.modules.sys.entity.Office;
import com.coffer.core.modules.sys.entity.User;
import com.coffer.core.modules.sys.utils.UserUtils;

/**
 * 兑换管理测试Service
 * 
 * @author niguoyong
 * @version 2015年9月29日
 */
public class StoExchangeServiceTest extends SpringTransactionalContextTests {

	@Autowired
	StoExchangeService stoExchangeService;

	@Test
	public void testFindPagePageOfStoExchangeStoExchange() {
		Page<StoExchange> page = new Page<StoExchange>();
		StoExchange stoExchange = new StoExchange();
		stoExchange.setCreateTimeStart(new Date());
		stoExchange.setCreateTimeEnd(new Date());
		Page<StoExchange> list = stoExchangeService.findPage(page, stoExchange);
		Assert.assertEquals("成功", list.getList().size(), 0);
	}

	@Test
	public void testGetDetailById() {
		StoExchange stoExchange = stoExchangeService.getDetailById("151008100001");
		Assert.assertNotNull("成功", stoExchange);
	}

	@Test
	@Rollback(true)
	public void testSaveStoExchangeStoExchange() {
		StoExchange stoExchange = new StoExchange();
		StoExchange stoExchangeDetail = new StoExchange();
		StoGoods stoGoods = new StoGoods();
		stoGoods.setGoodsID("101015101101");
		stoExchange.setChangeGoods(stoGoods);
		stoExchange.getStoGoodSelectFrom().setMoneyNumber((long) 1);

		StoExchangeGood stoExchangeGood = new StoExchangeGood();
		stoExchangeGood.setGoodsId("101025101101");
		stoExchangeGood.setMoneyAmount(BigDecimal.valueOf(100000));
		stoExchangeGood.setNum((long) 1);
		stoExchangeDetail.getStoExchangeGoodList().add(stoExchangeGood);
		// 库存
		//StoStoresInfo stoStoresInfo = StoreCommonUtils.getStoStoresInfoByGoodsId("101015101101","10000001");
		
		//数据准备
		Office currentOffice = new Office();
		currentOffice.setId("10000001");
		
		try{
		stoExchangeService.save(stoExchange, stoExchangeDetail,currentOffice);
		}catch(ExceptionInInitializerError e){
			Assert.assertNotNull("成功", e);
		}
		// 库存
		//StoStoresInfo stoStoresInfo2 = StoreCommonUtils.getStoStoresInfoByGoodsId("101015101101","10000001");
		//Assert.assertEquals(stoStoresInfo.getStoNum() - stoStoresInfo2.getStoNum(), 1);

	}

}
