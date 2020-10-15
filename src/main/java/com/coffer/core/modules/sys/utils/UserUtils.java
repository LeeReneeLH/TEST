/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.coffer.core.modules.sys.utils;

import java.util.List;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.UnavailableSecurityManagerException;
import org.apache.shiro.session.InvalidSessionException;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.Subject;

import com.coffer.core.common.config.Global;
import com.coffer.core.common.service.BaseService;
import com.coffer.core.common.utils.CacheUtils;
import com.coffer.core.common.utils.IdGen;
import com.coffer.core.common.utils.SpringContextHolder;
import com.coffer.core.modules.sys.dao.MenuDao;
import com.coffer.core.modules.sys.dao.OfficeDao;
import com.coffer.core.modules.sys.dao.RoleDao;
import com.coffer.core.modules.sys.dao.UserDao;
import com.coffer.core.modules.sys.entity.Menu;
import com.coffer.core.modules.sys.entity.Office;
import com.coffer.core.modules.sys.entity.Role;
import com.coffer.core.modules.sys.entity.User;
import com.coffer.core.modules.sys.security.SystemAuthorizingRealm.Principal;

/**
 * 用户工具类
 * @author ThinkGem
 * @version 2013-12-05
 */
public class UserUtils {

	private static UserDao userDao = SpringContextHolder.getBean(UserDao.class);
	private static RoleDao roleDao = SpringContextHolder.getBean(RoleDao.class);
	private static MenuDao menuDao = SpringContextHolder.getBean(MenuDao.class);
	private static OfficeDao officeDao = SpringContextHolder.getBean(OfficeDao.class);

	public static final String USER_CACHE = "userCache";
	public static final String USER_CACHE_ID_ = "id_";
	public static final String USER_CACHE_LOGIN_NAME_ = "ln";
	public static final String USER_CACHE_LIST_BY_OFFICE_ID_ = "oid_";
	
	public static final String CACHE_ROLE_LIST = "roleList";
	public static final String CACHE_MENU_LIST = "menuList";
	public static final String CACHE_OFFICE_LIST = "officeList";
	public static final String CACHE_OFFICE_ALL_LIST = "officeAllList";
	
	/**
	 * 根据ID获取用户
	 * @param id
	 * @return 取不到返回null
	 */
	public static User get(String id){
		User user = (User)CacheUtils.get(USER_CACHE, USER_CACHE_ID_ + id);
		if (user ==  null){
			user = userDao.get(id);
			if (user == null){
				return null;
			}
			user.setRoleList(roleDao.findList(new Role(user)));
			CacheUtils.put(USER_CACHE, USER_CACHE_ID_ + user.getId(), user);
			CacheUtils.put(USER_CACHE, USER_CACHE_LOGIN_NAME_ + user.getLoginName(), user);
		}
		return user;
	}
	
	/**
	 * 根据登录名获取用户
	 * @param loginName
	 * @return 取不到返回null
	 */
	public static User getByLoginName(String loginName){
		User user = (User)CacheUtils.get(USER_CACHE, USER_CACHE_LOGIN_NAME_ + loginName);
		if (user == null){
			user = userDao.getByLoginName(new User(null, loginName));
			if (user == null){
				return null;
			}
			user.setRoleList(roleDao.findList(new Role(user)));
			CacheUtils.put(USER_CACHE, USER_CACHE_ID_ + user.getId(), user);
			CacheUtils.put(USER_CACHE, USER_CACHE_LOGIN_NAME_ + user.getLoginName(), user);
		}
		return user;
	}
	
	/**
	 * 
	 * Title: getByUserFaceId
	 * <p>Description: 根据用户脸部识别ID查询用户</p>
	 * @author:     wangbaozhong
	 * @param userFaceId 用户脸部识别ID
	 * @return 
	 * User    返回类型
	 */
	public static User getByUserFaceId(Long userFaceId){
		User user = new User();
		user.setUserFaceId(userFaceId);
		user = userDao.getByUserFaceId(user);
		if (user == null){
			return null;
		}
			
		return user;
	}
	/**
	 * 
	 * Title: getByUserFaceIdOfficeId
	 * <p>Description: 根据用户脸部识别ID和所属机构查询用户</p>
	 * @author:     wangbaozhong
	 * @param userFaceId 用户脸部识别ID
	 * @param officeId 所属机构ID
	 * @return 
	 * User    返回类型
	 */
	public static User getByUserFaceIdOfficeId(Long userFaceId, String officeId){
		User user = new User();
		user.setUserFaceId(userFaceId);
		Office userOffice = new Office();
		userOffice.setId(officeId);
		user.setOffice(userOffice);
		user = userDao.getByUserFaceId(user);
		if (user == null){
			return null;
		}
			
		return user;
	}
	
	/**
	 * 根据机构ID查询人民银行、商业机构、清分中心设定脸部识别ID数量
	 * Title: getPbocFaceIdCntByOfficeId
	 * <p>Description: </p>
	 * @author:     wangbaozhong
	 * @param officeId	机构ID
	 * @return 
	 * int    返回类型
	 */
	public static int getPbocFaceIdCntByOfficeId(String officeId) {
		
		return userDao.getPbocFaceIdCntByOfficeId(officeId, Global.getConfig("jdbc.type"));
	}
	
	/**
	 * 清除当前用户缓存
	 */
	public static void clearCache(){
		removeCache(CACHE_ROLE_LIST);
		removeCache(CACHE_MENU_LIST);
		removeCache(CACHE_OFFICE_LIST);
		removeCache(CACHE_OFFICE_ALL_LIST);
		UserUtils.clearCache(getUser());
	}
	
	/**
	 * 清除指定用户缓存
	 * @param user
	 */
	public static void clearCache(User user){
		CacheUtils.remove(USER_CACHE, USER_CACHE_ID_ + user.getId());
		CacheUtils.remove(USER_CACHE, USER_CACHE_LOGIN_NAME_ + user.getLoginName());
		CacheUtils.remove(USER_CACHE, USER_CACHE_LOGIN_NAME_ + user.getOldLoginName());
		if (user.getOffice() != null && user.getOffice().getId() != null){
			CacheUtils.remove(USER_CACHE, USER_CACHE_LIST_BY_OFFICE_ID_ + user.getOffice().getId());
		}
	}
	
	/**
	 * 获取当前用户
	 * @return 取不到返回 new User()
	 */
	public static User getUser(){
		Principal principal = getPrincipal();
		if (principal!=null){
			User user = get(principal.getId());
			if (user != null){
				return user;
			}
			return new User();
		}
		// 如果没有登录，则返回实例化空的User对象。
		return new User();
	}

	/**
	 * 获取当前用户角色列表
	 * @return
	 */
	public static List<Role> getRoleList(){
		@SuppressWarnings("unchecked")
		List<Role> roleList = (List<Role>)getCache(CACHE_ROLE_LIST);
		if (roleList == null){
			User user = getUser();
			if (user.isAdmin()){
				roleList = roleDao.findAllList(new Role());
			}else{
				Role role = new Role();
				role.getSqlMap().put("dsf", BaseService.dataScopeFilter(user.getCurrentUser(), "o", "u"));
				roleList = roleDao.findList(role);
			}
			putCache(CACHE_ROLE_LIST, roleList);
		}
		return roleList;
	}
	
	/**
	 * 获取当前用户授权菜单
	 * @return
	 */
	public static List<Menu> getMenuList(){
		@SuppressWarnings("unchecked")
		List<Menu> menuList = (List<Menu>)getCache(CACHE_MENU_LIST);
		if (menuList == null){
			User user = getUser();
			if (user.isAdmin()){
				menuList = menuDao.findAllList(new Menu());
			}else{
				Menu m = new Menu();
				m.setUserId(user.getId());
				menuList = menuDao.findByUserId(m);
			}
			putCache(CACHE_MENU_LIST, menuList);
		}
		return menuList;
	}
	
	/**
	 * 获取当前用户授权中工作流分类不为空的菜单
	 * @return
	 */
	public static List<Menu> getMenuListForCategory(){
		// 取得category不为空的该用户菜单
		User user = getUser();
		Menu m = new Menu();
		m.setUserId(user.getId());
		List<Menu> menuList = menuDao.findByUserIdAndCategory(m);
		return menuList;
	}
	
	/**
	 * 获取当前用户有权限访问的部门
	 * @return
	 */
	public static List<Office> getOfficeList(){
		@SuppressWarnings("unchecked")
		List<Office> officeList = (List<Office>)getCache(CACHE_OFFICE_LIST);
		if (officeList == null){
			User user = getUser();
			if (user.isAdmin()){
				officeList = officeDao.findAllList(new Office());
			}else{
				Office office = new Office();
				office.getSqlMap().put("dsf", BaseService.dataScopeFilter(user, "a", ""));
				officeList = officeDao.findList(office);
			}
			putCache(CACHE_OFFICE_LIST, officeList);
		}
		return officeList;
	}

	/**
	 * 获取当前用户有权限访问的部门
	 * @return
	 */
	public static List<Office> getOfficeAllList(){
		@SuppressWarnings("unchecked")
		List<Office> officeList = (List<Office>)getCache(CACHE_OFFICE_ALL_LIST);
		if (officeList == null){
			officeList = officeDao.findAllList(new Office());
		}
		return officeList;
	}
	
	/**
	 * 获取授权主要对象
	 */
	public static Subject getSubject(){
		return SecurityUtils.getSubject();
	}
	
	/**
	 * 获取当前登录者对象
	 */
	public static Principal getPrincipal(){
		try{
			Subject subject = SecurityUtils.getSubject();
			Principal principal = (Principal)subject.getPrincipal();
			if (principal != null){
				return principal;
			}
//			subject.logout();
		}catch (UnavailableSecurityManagerException e) {
			
		}catch (InvalidSessionException e){
			
		}
		return null;
	}
	
	public static Session getSession(){
		try{
			Subject subject = SecurityUtils.getSubject();
			Session session = subject.getSession(false);
			if (session == null){
				session = subject.getSession();
			}
			if (session != null){
				return session;
			}
//			subject.logout();
		}catch (InvalidSessionException e){
			
		}
		return null;
	}
	
	// ============== User Cache ==============
	
	public static Object getCache(String key) {
		return getCache(key, null);
	}
	
	public static Object getCache(String key, Object defaultValue) {
//		Object obj = getCacheMap().get(key);
		Object obj = getSession().getAttribute(key);
		return obj==null?defaultValue:obj;
	}

	public static void putCache(String key, Object value) {
//		getCacheMap().put(key, value);
		getSession().setAttribute(key, value);
	}

	public static void removeCache(String key) {
//		getCacheMap().remove(key);
		getSession().removeAttribute(key);
	}
	
//	public static Map<String, Object> getCacheMap(){
//		Principal principal = getPrincipal();
//		if(principal!=null){
//			return principal.getCacheMap();
//		}
//		return new HashMap<String, Object>();
//	}
	/**
	 * 
	 * Title: createUserCacheId
	 * <p>Description: 创建缓存ID</p>
	 * @author:     wangbaozhong
	 * @return 当前系统时间(yyyyMMddHHmmssSSS)格式ID
	 * String    返回类型
	 */
	public static String createUserCacheId() {
		return IdGen.uuid();
	}

	/**
	 * 获取用户，根据用户类型和机构过滤
	 * 
	 * @author XL
	 * @version 2017年8月22日
	 * @param userType
	 *            用户类型
	 * @param officeId
	 *            机构ID
	 * @return 用户集合
	 */
	public static List<User> getUsersByTypeAndOffice(String userType, String officeId) {
		User user = new User();
		// 生成数据权限过滤条件（dsf为dataScopeFilter的简写，在xml中使用 ${sqlMap.dsf}调用权限SQL）
		user.getSqlMap().put("dsf", BaseService.dataScopeFilter(user.getCurrentUser(), "o", "a"));
		Office office = new Office(officeId);
		user.setOffice(office);
		user.setUserType(userType);
		List<User> userList = userDao.findList(user);
		return userList;
	}

	/**
	 * 获取用户，根据用户类型和机构过滤
	 * 
	 * @author qph
	 * @version 2018年5月2日
	 * @param userType
	 *            用户类型
	 * @param officeId
	 *            机构ID
	 * @return 用户集合
	 */
	public static List<User> getUsersByTypeAndOfficeByfilter(String userType, String officeId) {
		User user = new User();
		// 生成数据权限过滤条件（dsf为dataScopeFilter的简写，在xml中使用 ${sqlMap.dsf}调用权限SQL）
		user.getSqlMap().put("dsf", BaseService.dataScopeFilter(user.getCurrentUser(), "o", "a"));
		user.setUserType(userType);
		List<User> userList = userDao.findList(user);
		return userList;
	}

	/**
	 * 根据用户类型获取用户
	 * @param usertype
	 * @return 取不到返回null
	 */
	public static List<User> getByUserType(String usertype){
		User user = new User();
		user.setUserType(usertype);
		List<User> userList = userDao.findUserInfoByUserType(user);
		if (userList == null) {
			return null;
		}

		return userList;
	}

	/**
	 * 根据用户类型、角色、机构
	 * @param userType
	 * @param officeId
	 * @return
	 */
	public static List<User> getUsersByTypeRoleOffice(String userType,String roleId, String officeId) {
		User user = new User();
		// 生成数据权限过滤条件（dsf为dataScopeFilter的简写，在xml中使用 ${sqlMap.dsf}调用权限SQL）
		user.getSqlMap().put("dsf", BaseService.dataScopeFilter(user.getCurrentUser(), "o", "a"));
		Office office = new Office(officeId);
		Role role = new Role(roleId);
		user.setOffice(office);
		user.setUserType(userType);
		user.setRole(role);
		List<User> userList = userDao.findList(user);
		return userList;
	}
	
}
