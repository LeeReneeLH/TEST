/**
 * @author Clark
 * @version 2015年10月21日
 * 
 * 
 */
package com.coffer.core.modules.sys.service;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.coffer.core.common.test.SpringTransactionalContextTests;
import com.coffer.core.modules.sys.entity.Office;

/**
 * @author Clark
 *
 */
public class OfficeServiceTest extends SpringTransactionalContextTests {

	@Autowired
	private OfficeService officeService;
	
	/**
	 * {@link com.coffer.core.modules.sys.service.OfficeService#findListBySearch(com.coffer.core.modules.sys.entity.Office)} 的测试方法。
	 */
	@Test
	public void testFindListBySearch() {
		Office office = new Office();
		office.setName("赣州银行");
		List<Office> list = officeService.findListBySearch(office);
		
		Assert.assertEquals(1, list.size());
	}
}
