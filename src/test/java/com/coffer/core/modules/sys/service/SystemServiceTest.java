/**
 * @author Clark
 * @version 2015年10月20日
 * 
 * 
 */
package com.coffer.core.modules.sys.service;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.coffer.businesses.common.Constant;
import com.coffer.core.common.test.SpringTransactionalContextTests;
import com.coffer.core.modules.sys.entity.Office;
import com.coffer.core.modules.sys.entity.Role;
import com.coffer.core.modules.sys.entity.User;
import com.google.common.collect.Lists;

/**
 * @author Clark
 *
 */
public class SystemServiceTest extends SpringTransactionalContextTests {

	@Autowired
	private SystemService systemService;
	
	/**
	 * {@link com.coffer.core.modules.sys.service.SystemService#getUser(java.lang.String)} 的测试方法。
	 */
	@Test
	public void testGetUser() {
		User user = systemService.getUser("1");
		Assert.assertNotNull(user);
	}

	/**
	 * {@link com.coffer.core.modules.sys.service.SystemService#getUserByLoginName(java.lang.String)} 的测试方法。
	 */
	@Test
	public void testGetUserByLoginName() {
		User user = systemService.getUserByLoginName("clark");
		Assert.assertNotNull(user);
	}

	/**
	 * {@link com.coffer.core.modules.sys.service.SystemService#saveUser(com.coffer.core.modules.sys.entity.User)} 的测试方法。
	 */
	@Test
	public void testSaveUser1() {
		User user = systemService.getUserByLoginName("tester");
		Assert.assertNull(user);
		
		Office office = new Office();
		office.setId("99999999");
		List<Role> roleList = Lists.newArrayList();
		Role role = new Role();
		role.setId("1");
		roleList.add(role);
		
		user = new User();
		user.setLoginName("tester");
		user.setPassword("tester");
		user.setName("测试用户");
		user.setUserType(Constant.SysUserType.SYSTEM);
		user.setOffice(office);
		user.setRoleList(roleList);
		systemService.saveUser(user);
		
		user = systemService.getUserByLoginName("tester");
		Assert.assertNotNull(user);
	}

	/**
	 * {@link com.coffer.core.modules.sys.service.SystemService#updateUserInfo(com.coffer.core.modules.sys.entity.User)} 的测试方法。
	 */
	@Test
	public void testSaveUser2() {
		Office office = new Office();
		office.setId("99999999");
		List<Role> roleList = Lists.newArrayList();
		Role role = new Role();
		role.setId("1");
		roleList.add(role);
		
		User user = new User();
		user.setLoginName("tester");
		user.setPassword("tester");
		user.setName("测试用户");
		user.setUserType(Constant.SysUserType.SYSTEM);
		user.setOffice(office);
		user.setRoleList(roleList);
		systemService.saveUser(user);
		
		user = systemService.getUserByLoginName("tester");
		
		user.setEmail("tester@126.com");
		user.setNo("210211201510201234");
		systemService.saveUser(user);
		
		user = systemService.getUserByLoginName("tester");
		
		Assert.assertEquals("tester@126.com", user.getEmail());
		Assert.assertEquals("210211201510201234", user.getNo());
	}

	/**
	 * {@link com.coffer.core.modules.sys.service.SystemService#deleteUser(com.coffer.core.modules.sys.entity.User)} 的测试方法。
	 */
	@Test
	public void testDeleteUser() {
		Office office = new Office();
		office.setId("99999999");
		List<Role> roleList = Lists.newArrayList();
		Role role = new Role();
		role.setId("1");
		roleList.add(role);
		
		User user = new User();
		user.setLoginName("tester");
		user.setPassword("tester");
		user.setName("测试用户");
		user.setUserType(Constant.SysUserType.SYSTEM);
		user.setOffice(office);
		user.setRoleList(roleList);
		systemService.saveUser(user);
		
		user = systemService.getUserByLoginName("tester");
		Assert.assertNotNull(user);
		
		systemService.deleteUser(user);
		
		user = systemService.getUserByLoginName("tester");
		Assert.assertNull(user);
	}

	/**
	 * {@link com.coffer.core.modules.sys.service.SystemService#getRole(java.lang.String)} 的测试方法。
	 */
	@Test
	public void testGetRole() {
		Role role = systemService.getRole("1");
		Assert.assertNotNull(role);
	}
}
