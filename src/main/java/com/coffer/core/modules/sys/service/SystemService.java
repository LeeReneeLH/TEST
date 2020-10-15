package com.coffer.core.modules.sys.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
//import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.activiti.engine.IdentityService;
import org.activiti.engine.identity.Group;
import org.apache.shiro.crypto.hash.Md5Hash;
import org.apache.shiro.session.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.coffer.businesses.common.Constant;
import com.coffer.businesses.common.utils.BusinessUtils;
import com.coffer.businesses.modules.clear.ClearCommonUtils;
import com.coffer.businesses.modules.clear.v03.entity.TellerAccountsMain;
import com.coffer.businesses.modules.store.StoreCommonUtils;
import com.coffer.businesses.modules.store.v01.entity.StoEscortInfo;
import com.coffer.core.common.config.Global;
import com.coffer.core.common.persistence.Page;
import com.coffer.core.common.security.Digests;
import com.coffer.core.common.security.shiro.session.SessionDAO;
import com.coffer.core.common.service.BaseService;
import com.coffer.core.common.service.BusinessException;
import com.coffer.core.common.service.ServiceException;
import com.coffer.core.common.utils.CacheUtils;
import com.coffer.core.common.utils.Collections3;
import com.coffer.core.common.utils.Encodes;
import com.coffer.core.common.utils.StringUtils;
import com.coffer.core.modules.sys.SysCommonUtils;
import com.coffer.core.modules.sys.dao.MenuDao;
import com.coffer.core.modules.sys.dao.RoleDao;
import com.coffer.core.modules.sys.dao.UserDao;
import com.coffer.core.modules.sys.entity.Menu;
import com.coffer.core.modules.sys.entity.Office;
import com.coffer.core.modules.sys.entity.Role;
import com.coffer.core.modules.sys.entity.User;
//import com.coffer.core.modules.sys.security.SystemAuthorizingRealm;
import com.coffer.core.modules.sys.utils.LogUtils;
import com.coffer.core.modules.sys.utils.UserUtils;

/**
 * 系统管理，安全相关实体的管理类,包括用户、角色、菜单.
 * 
 * @author Clark
 * @version 2015-01-08
 */
@Service
@Transactional(readOnly = true)
public class SystemService extends BaseService {

	public static final String HASH_ALGORITHM = "SHA-1";
	public static final int HASH_INTERATIONS = 1024;
	public static final int SALT_SIZE = 8;
	/** 岗位：系统管理员 */
	public static final String SYS_USER_TYPE_ADMIN = Global.getConfig("sys.userType.admin");
	/** 顶级机构 */
	public static final String TOP_OFFICE_ID = Global.getConfig("top.office.id");
	@Autowired
	private UserDao userDao;
	@Autowired
	private RoleDao roleDao;
	@Autowired
	private MenuDao menuDao;
	@Autowired
	private SessionDAO sessionDao;
	// @Autowired
	// private SystemAuthorizingRealm systemRealm;
	@Autowired
	private FaceIdSerialNumberService faceIdSerialNumberService;

	public SessionDAO getSessionDao() {
		return sessionDao;
	}

	 @Autowired
	 private IdentityService identityService;

	// -- User Service --//

	/**
	 * 获取用户
	 * 
	 * @param id
	 * @return
	 */
	public User getUser(String id) {
		return UserUtils.get(id);
	}

	/**
	 * 根据登录名获取用户
	 * 
	 * @param loginName
	 * @return
	 */
	public User getUserByLoginName(String loginName) {
		return UserUtils.getByLoginName(loginName);
	}

	public Page<User> findUser(Page<User> page, User user) {
		// 生成数据权限过滤条件（dsf为dataScopeFilter的简写，在xml中使用 ${sqlMap.dsf}调用权限SQL）
		user.getSqlMap().put("dsf", dataScopeFilter(user.getCurrentUser(), "o", "a"));
		// 设置分页参数
		user.setPage(page);
		// 执行分页查询
		List<User> userList = userDao.findList(user);
		// 取出用户拥有的角色
		for (User currentUser : userList) {
			currentUser.setRoleList(roleDao.findList(new Role(currentUser)));
		}
		page.setList(userList);
		return page;
	}

	/**
	 * 无分页查询人员列表
	 * 
	 * @param user
	 * @return
	 */
	public List<User> findUser(User user) {
		// 生成数据权限过滤条件（dsf为dataScopeFilter的简写，在xml中使用 ${sqlMap.dsf}调用权限SQL）
		user.getSqlMap().put("dsf", dataScopeFilter(user.getCurrentUser(), "o", "a"));
		List<User> list = userDao.findList(user);
		return list;
	}

	/**
	 * 通过部门ID获取用户列表，仅返回用户id和name（树查询用户时用）
	 * 
	 * @param user
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<User> findUserByOfficeId(String officeId) {
		List<User> list = (List<User>) CacheUtils.get(UserUtils.USER_CACHE,
				UserUtils.USER_CACHE_LIST_BY_OFFICE_ID_ + officeId);
		if (list == null) {
			User user = new User();
			user.setOffice(new Office(officeId));
			list = userDao.findUserByOfficeId(user);
			CacheUtils.put(UserUtils.USER_CACHE, UserUtils.USER_CACHE_LIST_BY_OFFICE_ID_ + officeId, list);
		}
		return list;
	}

	/**
	 * 
	 * @author chenxin
	 * @version 2015年10月10日 通过部门ID获取用户列表（接口用）
	 * @param userTypes
	 * @return
	 */
	public List<User> findUserInfoByOfficeId(Map<String, Object> headInfoMap, List<String> typeList) {
		List<User> userList = new ArrayList<User>();
		if (headInfoMap != null && headInfoMap.get("officeId") != null
				&& StringUtils.isNotBlank(headInfoMap.get("officeId").toString())) {
			User user = new User();
			user.setOffice(new Office(headInfoMap.get("officeId").toString()));
			String searchDate = headInfoMap.get("searchDate") != null ? headInfoMap.get("searchDate").toString() : "";
			userList = userDao.findUserInfoByOfficeId(searchDate, user, typeList,Global.getConfig("jdbc.type"));
		}
		return userList;
	}

	/**
	 * 
	 * Title: findUserInfoByOfficeId
	 * <p>
	 * Description: 按照机构ID及查询日期查询用户信息
	 * </p>
	 * 
	 * @author: wangbaozhong
	 * @param officeId
	 *            机构ID
	 * @param lastSearchDate
	 *            上次查询日期
	 * @return 用户列表 List<User> 返回类型
	 */
	@Transactional(readOnly = true)
	public List<User> findUserInfoByOfficeId(String officeId, String lastSearchDate) {
		User user = new User();
		user.setOffice(new Office(officeId));
		return userDao.findUserInfoByOfficeId(lastSearchDate, user, null, Global.getConfig("jdbc.type"));
	}

	/**
	 * 
	 * Title: findUserInfoByOfficeIds
	 * <p>
	 * Description:
	 * </p>
	 * 
	 * @author: wangbaozhong
	 * @param lastSearchDate
	 *            上次查询日期
	 * @param officeIds
	 *            机构列表
	 * @return 用户信息列表 List<User> 返回类型
	 */
	@Transactional(readOnly = true)
	public List<User> findUserInfoByOfficeIds(String lastSearchDate, List<String> officeIds) {
		return userDao.findUserInfoByOfficeIds(lastSearchDate, officeIds, Global.getConfig("jdbc.type"));
	}

	@Transactional(readOnly = false)
	public synchronized void saveUser(User user) {
		// 如果用户岗位不是系统管理员，则所属机构不允许选择顶级机构。
		if (!SYS_USER_TYPE_ADMIN.equals(user.getUserType()) && TOP_OFFICE_ID.equals(user.getOffice().getId())) {
			throw new BusinessException("message.E9001", "");
		}
		// 当前保存用户所属机构
		Office uesrOffice = SysCommonUtils.findOfficeById(user.getOffice().getId());
		Office pbocOffice = null;
		if (!Constant.OfficeType.CENTRAL_BANK.equals(uesrOffice.getType())
				&& !Constant.OfficeType.DIGITAL_PLATFORM.equals(uesrOffice.getType()) 
				&& !Constant.OfficeType.ROOT.equals(uesrOffice.getType())
				&& StringUtils.isNotBlank(user.getInitFaceIdFlag())) {
			pbocOffice = BusinessUtils.getPbocCenterByOffice(uesrOffice);
			// 添加异常信息 当前用户所属机构未指定人行 修改人：xp
			if (pbocOffice == null) {
				throw new BusinessException("message.E9006", "");
			}
		} else {
			pbocOffice = uesrOffice;
		}

		// 用户人脸识别ID未设定，并且 初始化flag不为空，则生成用户人脸识别ID
		if (Global.TRUE.equals(Global.getConfig("sys.needfaceId")) && StringUtils.isNotBlank(user.getInitFaceIdFlag())
				&& user.getUserFaceId() == null) {

			// 生成脸谱ID
			long faceId = faceIdSerialNumberService.getSerialNumber(uesrOffice.getType(), pbocOffice.getId());
			if (faceId == -1) {
				// message.E9002=[保存失败]：脸谱ID生成区间未设定或生成脸谱机构类型与预定不符，
				// 请稍后再试或与系统管理员联系！
				throw new BusinessException("message.E9002", "");
			} else if (faceId == -2) {
				// message.E9003=[保存失败]：脸谱ID超出设定上限，请稍后再试或与系统管理员联系！
				throw new BusinessException("message.E9003", "");
			}

			user.setUserFaceId(faceId);
		}

		if (StringUtils.isBlank(user.getId())) {
			String loginName = user.getLoginName();
			if (StringUtils.isBlank(loginName)) {
				// 自动生成登陆用户名
				loginName = SysCommonUtils.createLoginName(user.getOffice().getId());
				user.setLoginName(loginName);
				
			}
			// 验证登陆用户名是否存在
			if (getUserByLoginName(loginName) != null) {
				user.setUserFaceId(null);
				// message.E9004=[保存失败]：登录名【{1}】已存在！
				throw new BusinessException("message.E9004", "", new String[] { loginName });
			}

			// 验证人行机构下脸谱ID是否存在
			if (Global.TRUE.equals(Global.getConfig("sys.needfaceId")) && UserUtils.getByUserFaceIdOfficeId(user.getUserFaceId(), pbocOffice.getId()) != null) {
				// message.E9005=[保存失败]：脸谱ID【{0}】已存在！
				throw new BusinessException("message.E9005", "", new String[] { Long.toString(user.getUserFaceId()) });
			}
			
			user.preInsert();
			userDao.insert(user);
			// 将当前用户同步到Activiti
			// saveActivitiUser(user);
		} else {
			// 清除原用户机构用户缓存
			User oldUser = userDao.get(user.getId());
			if (oldUser.getOffice() != null && oldUser.getOffice().getId() != null) {
				CacheUtils.remove(UserUtils.USER_CACHE,
						UserUtils.USER_CACHE_LIST_BY_OFFICE_ID_ + oldUser.getOffice().getId());
			}
			// 修改人：QPH 修改时间 ： 2017-10-27 begin 修改内容：用户修改时验证用户身上是否还有柜员账务
			// 若用户修改的是用户类型
			if (!user.getUserType().equals(oldUser.getUserType())) {
				// 获取柜员用户类型
				List<String> userTypeList = Global.getList("clear.teller.businessTypes");
				// 若删除用户类型为柜员
				if (userTypeList.contains(oldUser.getUserType())) {
					// 获取escort表用户数据
					StoEscortInfo stoEscortInfo = StoreCommonUtils.findByUserId(user.getId());
					// 通过用户ID获取柜员账务
					if (stoEscortInfo != null && StringUtils.isNotBlank(stoEscortInfo.getId())) {
						List<TellerAccountsMain> tellerAccountsMainList = ClearCommonUtils
								.getNewestTellerAccounts(stoEscortInfo.getId());
						if (!Collections3.isEmpty(tellerAccountsMainList)) {
							for (TellerAccountsMain tellerAccountsMain : tellerAccountsMainList) {
								if (BigDecimal.ZERO.compareTo(tellerAccountsMain.getTotalAmount()) != 0) {
									// 清除用户缓存
									UserUtils.clearCache(user);
									throw new BusinessException("message.E9008", "");
								}
							}
						}
					}
				}
			}
			// end
			// 更新用户数据
			user.preUpdate();
			userDao.update(user);
		}
		// 是否需要保存身份信息到押运交接表
		String userType = Global.getConfig("need.idcardNo.userType");
		if (userType.indexOf(user.getUserType()) >= 0) {
			SysCommonUtils.saveEscortInfo(user);
		}
		if (StringUtils.isNotBlank(user.getId())) {
			// 更新用户与角色关联
			userDao.deleteUserRole(user);
			if (user.getRoleList() != null && user.getRoleList().size() > 0) {
				userDao.insertUserRole(user);
			} else {
				throw new ServiceException(user.getLoginName() + "没有设置角色！");
			}
			//将当前用户同步到Activiti
			//saveActivitiUser(user);
			// 清除用户缓存
			UserUtils.clearCache(user);
			// // 清除权限缓存
			// systemRealm.clearAllCachedAuthorizationInfo();
		}
	}

	@Transactional(readOnly = false)
	public void updateUserInfo(User user) {
		user.preUpdate();
		userDao.updateUserInfo(user);
		// 清除用户缓存
		UserUtils.clearCache(user);
		// // 清除权限缓存
		// systemRealm.clearAllCachedAuthorizationInfo();
	}

	@Transactional(readOnly = false)
	public void deleteUser(User user) {
		SysCommonUtils.deleteEscortInfo(user.getId());
		user.preUpdate();
		userDao.delete(user);
		// 同步到Activiti
		//deleteActivitiUser(user);
		// 清除用户缓存
		UserUtils.clearCache(user);
		// // 清除权限缓存
		// systemRealm.clearAllCachedAuthorizationInfo();
	}

	@Transactional(readOnly = false)
	public void updatePasswordById(String id, String loginName, String newPassword) {
		User user = new User(id);
		user.setPassword(entryptPassword(newPassword));
		user.preUpdate();
		userDao.updatePasswordById(user);
		// 清除用户缓存
		user.setLoginName(loginName);
		UserUtils.clearCache(user);
		// // 清除权限缓存
		// systemRealm.clearAllCachedAuthorizationInfo();
	}

	@Transactional(readOnly = false)
	public void updateUserLoginInfo(User user) {
		// 保存上次登录信息
		user.setOldLoginIp(user.getLoginIp());
		user.setOldLoginDate(user.getLoginDate());
		// 更新本次登录信息
		user.setLoginIp(UserUtils.getSession().getHost());
		user.setLoginDate(new Date());
		userDao.updateLoginInfo(user);
	}

	/**
	 * @author Clark
	 * @version 2015-01-08 与C#程序共用加密算法 或 生成安全的密码，生成随机的16位salt并经过1024次 sha-1 hash
	 * @param plainPassword
	 * @return
	 */
	public static String entryptPassword(String plainPassword) {
		String password = "";
		if (Global.isCommonMd5Used()) {
			// 与C#程序共用加密算法
			password = new Md5Hash(plainPassword).toString();
		} else {
			// 不与C#程序共用加密算法
			byte[] salt = Digests.generateSalt(SALT_SIZE);
			byte[] hashPassword = Digests.sha1(plainPassword.getBytes(), salt, HASH_INTERATIONS);
			password = Encodes.encodeHex(salt) + Encodes.encodeHex(hashPassword);
		}
		return password;
	}

	/**
	 * 验证密码
	 * 
	 * @param plainPassword
	 *            明文密码
	 * @param password
	 *            密文密码
	 * @return 验证成功返回true
	 */
	public static boolean validatePassword(String plainPassword, String password) {
		if (Global.isCommonMd5Used()) {
			// 与C#程序共用加密算法
			return password.equals(new Md5Hash(plainPassword).toString());
		} else {
			// 不与C#程序共用加密算法
			byte[] salt = Encodes.decodeHex(password.substring(0, 16));
			byte[] hashPassword = Digests.sha1(plainPassword.getBytes(), salt, HASH_INTERATIONS);
			return password.equals(Encodes.encodeHex(salt) + Encodes.encodeHex(hashPassword));
		}
	}

	/**
	 * 获得活动会话
	 * 
	 * @return
	 */
	public Collection<Session> getActiveSessions() {
		return sessionDao.getActiveSessions(false);
	}

	// -- Role Service --//

	public Role getRole(String id) {
		return roleDao.get(id);
	}

	public Role getRoleByName(String name) {
		Role r = new Role();
		r.setName(name);
		return roleDao.getByName(r);
	}
	// ADD-START  原因：添加工作流用户组标识  add by wangbaozhong  2018/04/17 
	public Role getRoleByEnname(String enname) {
		Role r = new Role();
		r.setEnname(enname);
		return roleDao.getByEnname(r);
	}
	// ADD-END  原因：添加工作流用户组标识  add by wangbaozhong  2018/04/17 
	
	public List<Role> findRole(Role role) {
		return roleDao.findList(role);
	}

	public List<Role> findAllRole() {
		return UserUtils.getRoleList();
	}

	@Transactional(readOnly = false)
	public void saveRole(Role role) {
		if (StringUtils.isBlank(role.getId())) {
			role.preInsert();
			roleDao.insert(role);
			// 同步到Activiti
			saveActivitiGroup(role);
		} else {
			role.preUpdate();
			roleDao.update(role);
		}
		// 更新角色与菜单关联
		roleDao.deleteRoleMenu(role);
		if (role.getMenuList().size() > 0) {
			roleDao.insertRoleMenu(role);
		}
		// 更新角色与部门关联
		roleDao.deleteRoleOffice(role);
		if (role.getOfficeList().size() > 0) {
			roleDao.insertRoleOffice(role);
		}
		// 同步到Activiti
		saveActivitiGroup(role);
		// 清除用户角色缓存
		UserUtils.removeCache(UserUtils.CACHE_ROLE_LIST);
		// // 清除权限缓存
		// systemRealm.clearAllCachedAuthorizationInfo();
	}

	@Transactional(readOnly = false)
	public void deleteRole(Role role) {
		roleDao.delete(role);
		// 同步到Activiti
		deleteActivitiGroup(role);
		// 清除用户角色缓存
		UserUtils.removeCache(UserUtils.CACHE_ROLE_LIST);
		// // 清除权限缓存
		// systemRealm.clearAllCachedAuthorizationInfo();
	}

	@Transactional(readOnly = false)
	public Boolean outUserInRole(Role role, User user) {
		List<Role> roles = user.getRoleList();
		for (Role e : roles) {
			if (e.getId().equals(role.getId())) {
				roles.remove(e);
				saveUser(user);
				return true;
			}
		}
		return false;
	}

	@Transactional(readOnly = false)
	public User assignUserToRole(Role role, User user) {
		if (user == null) {
			return null;
		}
		List<String> roleIds = user.getRoleIdList();
		if (roleIds.contains(role.getId())) {
			return null;
		}
		user.getRoleList().add(role);
		saveUser(user);
		return user;
	}

	// -- Menu Service --//

	public Menu getMenu(String id) {
		return menuDao.get(id);
	}

	public List<Menu> findAllMenu() {
		return UserUtils.getMenuList();
	}

	@Transactional(readOnly = false)
	public void saveMenu(Menu menu) {

		// 获取父节点实体
		menu.setParent(this.getMenu(menu.getParent().getId()));

		// 获取修改前的parentIds，用于更新子节点的parentIds
		String oldParentIds = menu.getParentIds();

		// 设置新的父节点串
		menu.setParentIds(menu.getParent().getParentIds() + menu.getParent().getId() + ",");

		// 保存或更新实体
		// if (StringUtils.isBlank(menu.getId())){
		if (menu.getIsNewRecord()) {
			menu.preInsert();
			menuDao.insert(menu);
		} else {
			menu.preUpdate();
			menuDao.update(menu);
		}

		// 更新子节点 parentIds
		Menu m = new Menu();
		m.setParentIds("%," + menu.getId() + ",%");
		List<Menu> list = menuDao.findByParentIdsLike(m);
		for (Menu e : list) {
			e.setParentIds(e.getParentIds().replace(oldParentIds, menu.getParentIds()));
			menuDao.updateParentIds(e);
		}
		// 清除用户菜单缓存
		UserUtils.removeCache(UserUtils.CACHE_MENU_LIST);
		// // 清除权限缓存
		// systemRealm.clearAllCachedAuthorizationInfo();
		// 清除日志相关缓存
		CacheUtils.remove(LogUtils.CACHE_MENU_NAME_PATH_MAP);
	}

	@Transactional(readOnly = false)
	public void updateMenuSort(Menu menu) {
		menuDao.updateSort(menu);
		// 清除用户菜单缓存
		UserUtils.removeCache(UserUtils.CACHE_MENU_LIST);
		// // 清除权限缓存
		// systemRealm.clearAllCachedAuthorizationInfo();
		// 清除日志相关缓存
		CacheUtils.remove(LogUtils.CACHE_MENU_NAME_PATH_MAP);
	}

	@Transactional(readOnly = false)
	public void deleteMenu(Menu menu) {
		menuDao.delete(menu);
		// 清除用户菜单缓存
		UserUtils.removeCache(UserUtils.CACHE_MENU_LIST);
		// // 清除权限缓存
		// systemRealm.clearAllCachedAuthorizationInfo();
		// 清除日志相关缓存
		CacheUtils.remove(LogUtils.CACHE_MENU_NAME_PATH_MAP);
	}

	/**
	 * 获取Key加载信息
	 */
	public static boolean printKeyLoadMessage() {
		StringBuilder sb = new StringBuilder();
		sb.append(
				"\r\n=================================================================================================\r\n");
		sb.append(
				"\r\n           ﹀ 　　                                                                                                                                           \r\n");
		sb.append("\r\n       ﹀ _▁▂▃__ 　　　                                            \r\n");
		sb.append("\r\n       __▁▂▁___ ﹀ ﹋ ●                                             \r\n");
		sb.append("\r\n       ﹋ ﹀__▁▂▃▁__ 　　　　                                      \r\n");
		sb.append("\r\n                                                                  \r\n");
		sb.append(
				"\r\n       █▆▅▇▆▅▄▃▄▅▆▄▅▄▃▂▃▄▆▅▇▆▄▅█▇▆▄▂                                                              \r\n");
		sb.append("\r\n       -__~~--___-▔~~__--__~~~_----__----~~~---▔---___▔--__-▔           \r\n");
		sb.append("\r\n       ___---_~__-~_▔___----_-__▔-__-_-~~~--_-----▔~~---__--~~   \r\n");
		sb.append("\r\n       █▇▆▅▃▁▂▁-－ _--_--－------－▔-－－~~▔▁▂▃▄▅▇                                          \r\n");
		sb.append(
				"\r\n=================================================================================================\r\n");
		System.out.println(sb.toString());
		return true;
	}

	/**
	 * 
	 * Title: updateByIdcardNo
	 * <p>
	 * Description: 通过身份证号更新用户信息
	 * </p>
	 * 
	 * @author: wangbaozhong
	 * @param user
	 *            用户信息
	 * @return int 返回类型
	 */
	@Transactional(readOnly = false)
	public int updateByIdcardNo(User user) {
		return userDao.updateByIdcardNo(user);
	}

	/**
	 * 按身份证号查询用户是否存在
	 * 
	 * @author WangBaozhong
	 * @version 2017年6月13日
	 * 
	 * 
	 * @param user
	 *            查询条件 身份证号
	 * @return 用户信息
	 */
	public User checkUserByIdCard(User user) {
		return userDao.checkUserByIdCard(user);
	}
	
	///////////////// Synchronized to the Activiti //////////////////
	
	// start 已废弃，同步见：ActGroupEntityServiceFactory.java、ActUserEntityServiceFactory.java

	/**
	 * 是需要同步Activiti数据，如果从未同步过，则同步数据。
	 */
	/*
	private static boolean isSynActivitiIndetity = true;
	public void afterPropertiesSet() throws Exception {
		if (!Global.isSynActivitiIndetity()){
			return;
		}
		if (isSynActivitiIndetity){
			isSynActivitiIndetity = false;
	        // 同步角色数据
			List<Group> groupList = identityService.createGroupQuery().list();
			if (groupList.size() == 0){
			 	Iterator<Role> roles = roleDao.findAllList(new Role()).iterator();
			 	while(roles.hasNext()) {
			 		Role role = roles.next();
			 		saveActivitiGroup(role);
			 	}
			}
		 	// 同步用户数据
			List<org.activiti.engine.identity.User> userList = identityService.createUserQuery().list();
			if (userList.size() == 0){
			 	Iterator<User> users = userDao.findAllList(new User()).iterator();
			 	while(users.hasNext()) {
			 		saveActivitiUser(users.next());
			 	}
			}
		}
	}*/
	
	// end 已废弃，同步见：ActGroupEntityServiceFactory.java、ActUserEntityServiceFactory.java
	private void saveActivitiGroup(Role role) {
		if (!Global.isSynActivitiIndetity()){
			return;
		}
		
		String groupId = role.getEnname();
		if (StringUtils.isBlank(groupId)){
			return;
		}
		
		
		// 如果修改了英文名，则删除原Activiti角色
		if (StringUtils.isNotBlank(role.getOldEnname()) && !role.getOldEnname().equals(role.getEnname())){
			identityService.deleteGroup(role.getOldEnname());
		}
		
		Group group = identityService.createGroupQuery().groupId(groupId).singleResult();
		if (group == null) {
			group = identityService.newGroup(groupId);
		}
		group.setName(role.getName());
		group.setType(role.getRoleType());
		identityService.saveGroup(group);
		
		// 删除用户与用户组关系
		List<org.activiti.engine.identity.User> activitiUserList = identityService.createUserQuery().memberOfGroup(groupId).list();
		for (org.activiti.engine.identity.User activitiUser : activitiUserList){
			identityService.deleteMembership(activitiUser.getId(), groupId);
		}

		// 创建用户与用户组关系
		List<User> userList = findUser(new User(new Role(role.getId())));
		for (User e : userList){
			String userId = e.getLoginName();//ObjectUtils.toString(user.getId());
			// 如果该用户不存在，则创建一个
			org.activiti.engine.identity.User activitiUser = identityService.createUserQuery().userId(userId).singleResult();
			if (activitiUser == null){
				activitiUser = identityService.newUser(userId);
				activitiUser.setFirstName(e.getName());
				activitiUser.setLastName(StringUtils.EMPTY);
				activitiUser.setEmail(e.getEmail());
				activitiUser.setPassword(StringUtils.EMPTY);
				identityService.saveUser(activitiUser);
			}
			identityService.createMembership(userId, groupId);
		}
	}

	public void deleteActivitiGroup(Role role) {
		if (!Global.isSynActivitiIndetity()){
			return;
		}
		if(role!=null) {
			String groupId = role.getEnname();
			if (!StringUtils.isBlank(groupId)) {
				
				identityService.deleteGroup(groupId);
			}
		}
	}

	private void saveActivitiUser(User user) {
		if (!Global.isSynActivitiIndetity()){
			return;
		}
		String userId = user.getLoginName();//ObjectUtils.toString(user.getId());
		org.activiti.engine.identity.User activitiUser = identityService.createUserQuery().userId(userId).singleResult();
		if (activitiUser == null) {
			activitiUser = identityService.newUser(userId);
		}
		activitiUser.setFirstName(user.getName());
		activitiUser.setLastName(StringUtils.EMPTY);
		activitiUser.setEmail(user.getEmail());
		activitiUser.setPassword(StringUtils.EMPTY);
		identityService.saveUser(activitiUser);
		
		// 删除用户与用户组关系
		List<Group> activitiGroups = identityService.createGroupQuery().groupMember(userId).list();
		for (Group group : activitiGroups) {
			identityService.deleteMembership(userId, group.getId());
		}
		// 创建用户与用户组关系
		for (Role role : user.getRoleList()) {
	 		String groupId = role.getEnname();
	 		if (StringUtils.isBlank(groupId)) {
	 			continue;
	 		}
	 		// 如果该用户组不存在，则创建一个
		 	Group group = identityService.createGroupQuery().groupId(groupId).singleResult();
            if(group == null) {
	            group = identityService.newGroup(groupId);
	            group.setName(role.getName());
	            group.setType(role.getRoleType());
	            identityService.saveGroup(group);
            }
			identityService.createMembership(userId, role.getEnname());
		}
	}

	private void deleteActivitiUser(User user) {
		if (!Global.isSynActivitiIndetity()){
			return;
		}
		if(user!=null) {
			String userId = user.getLoginName();//ObjectUtils.toString(user.getId());
			if (!StringUtils.isBlank(userId)) {
				identityService.deleteUser(userId);
			}
		}
	}
	
	///////////////// Synchronized to the Activiti end //////////////////
}
