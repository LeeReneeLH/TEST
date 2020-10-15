package com.coffer.core.modules.sys.security;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

import javax.annotation.PostConstruct;

import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authc.credential.HashedCredentialsMatcher;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.Permission;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.crypto.hash.Md5Hash;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.util.ByteSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.coffer.core.common.config.Global;
import com.coffer.core.common.servlet.ValidateCodeServlet;
import com.coffer.core.common.utils.Collections3;
import com.coffer.core.common.utils.Encodes;
import com.coffer.core.common.utils.SpringContextHolder;
import com.coffer.core.common.utils.StringUtils;
import com.coffer.core.common.web.Servlets;
import com.coffer.core.modules.sys.DbConfigConstant;
import com.coffer.core.modules.sys.entity.Menu;
import com.coffer.core.modules.sys.entity.Role;
import com.coffer.core.modules.sys.entity.SysOtp;
import com.coffer.core.modules.sys.entity.SysOtpOffice;
import com.coffer.core.modules.sys.entity.User;
import com.coffer.core.modules.sys.service.SysOtpOfficeService;
import com.coffer.core.modules.sys.service.SysOtpService;
import com.coffer.core.modules.sys.service.SystemService;
import com.coffer.core.modules.sys.utils.LogUtils;
import com.coffer.core.modules.sys.utils.UserUtils;
import com.coffer.core.modules.sys.web.LoginController;

/**
 * 系统安全认证实现类
 * @author Clark
 * @version 2015-01-09
 */
@Service
//@DependsOn({"userDao","roleDao","menuDao"})
public class SystemAuthorizingRealm extends AuthorizingRealm {

	private Logger logger = LoggerFactory.getLogger(getClass());
	
	private SystemService systemService;

	/** 动态口令-服务层 **/
	private SysOtpService sysOtpService;
	/** 令牌机构管理-服务层 **/
	private SysOtpOfficeService sysOtpOfficeService;

	/**
	 * 认证回调函数, 登录时调用
	 */
	@Override
	protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authcToken) {
		UsernamePasswordToken token = (UsernamePasswordToken) authcToken;
		
		int activeSessionSize = getSystemService().getSessionDao().getActiveSessions(false).size();
		if (logger.isDebugEnabled()){
			logger.debug("login submit, active session size: {}, username: {}", activeSessionSize, token.getUsername());
		}
		
		// 校验登录验证码
		if (LoginController.isValidateCodeLogin(token.getUsername(), false, false)){
			Session session = UserUtils.getSession();
			String code = (String)session.getAttribute(ValidateCodeServlet.VALIDATE_CODE);
			if (token.getCaptcha() == null || !token.getCaptcha().toUpperCase().equals(code)){
				throw new AuthenticationException("msg:验证码错误, 请重试.");
			}
		}
		User user = getSystemService().getUserByLoginName(token.getUsername());
		// 校验用户名密码
		if (user != null) {
			// 校验动态口令
			String sysOtpSwitch = Global.getConfig("sys.login.sysOtpSwitch");
			// 是否验证动态口令
			if (DbConfigConstant.checkIfExit.TRUE.equals(sysOtpSwitch)) {
				// ADD-START 原因：登录用户机构是否验证动态口令  XL 2018-10-29
				SysOtpOffice sysOtpOffice = new SysOtpOffice();
				sysOtpOffice.setOffice(user.getOffice());
				List<SysOtpOffice> sysOtpOfficeList = getSysOtpOfficeService().findList(sysOtpOffice);
				// ADD-END 原因：登录用户机构是否验证动态口令  XL 2018-10-29
				List<String> userTypeList = Global.getList("sys.login.userType");
				if (!userTypeList.contains(user.getUserType()) && !Collections3.isEmpty(sysOtpOfficeList)) {
					// 获取该用户令牌
					SysOtp sysOtp = new SysOtp();
					sysOtp.setUser(user);
					List<SysOtp> sysOtpList = getSysOtpService().findList(sysOtp);
					// 未绑定令牌
					if (Collections3.isEmpty(sysOtpList)) {
						throw new AuthenticationException("msg:该用户未绑定令牌。");
					}
					sysOtp = sysOtpList.get(0);
					// 用户机构与令牌机构不一致
					if (!sysOtp.getOffice().getId().equals(user.getOffice().getId())) {
						throw new AuthenticationException("msg:该用户机构与令牌机构不一致。");
					}
					if (StringUtils.isEmpty(token.getCommand())) {
						throw new AuthenticationException("msg:动态口令错误, 请重试。");
					} else {
						boolean flag = getSysOtpService().verify(user, token.getCommand(), sysOtp);
						if (!flag) {
							throw new AuthenticationException("msg:动态口令错误, 请重试。");
						}
					}
				}

			}
//			if (Global.NO.equals(user.getLoginFlag())){
//				throw new AuthenticationException("msg:该已帐号禁止登录.");
//			}
			String password = user.getPassword();
			// 使用统一登录平台验证用户是否存在
			// 创建与删除用户时使用统一登录平台验证：0：不使用验证，1：使用验证
			String isUsed = Global.getConfig("used.commonLogin.check");
			if (StringUtils.isNotBlank(isUsed) && "1".equals(isUsed)) {
				if (StringUtils.isBlank(password)) {
					password = "670b14728ad9902aecba32e22fa4f6bd";
				}
			}
			if (Global.isCommonMd5Used()) {
				// 与C#程序共用加密算法
				return new SimpleAuthenticationInfo(new Principal(user, false), password, getName());
			} else {
				// 不与C#程序共用加密算法
				byte[] salt = Encodes.decodeHex(user.getPassword().substring(0,16));
				return new SimpleAuthenticationInfo(new Principal(user, false), 
						user.getPassword().substring(16), ByteSource.Util.bytes(salt), getName());
			}
		} else {
			return null;
		}
	}

	/**
	 * 授权查询回调函数, 进行鉴权但缓存中无用户的授权信息时调用
	 */
	@Override
	protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
		Principal principal = (Principal) getAvailablePrincipal(principals);
		// 获取当前已登录的用户
		if (!Global.TRUE.equals(Global.getConfig("user.multiAccountLogin"))){
			Collection<Session> sessions = getSystemService().getSessionDao().getActiveSessions(true, principal, UserUtils.getSession());
			if (sessions.size() > 0){
				// 如果是登录进来的，则踢出已在线用户
				if (UserUtils.getSubject().isAuthenticated()){
					for (Session session : sessions){
						getSystemService().getSessionDao().delete(session);
					}
				}
				// 记住我进来的，并且当前用户已登录，则退出当前用户提示信息。
				else{
					UserUtils.getSubject().logout();
					throw new AuthenticationException("msg:账号已在其它地方登录，请重新登录。");
				}
			}
		}
		User user = getSystemService().getUserByLoginName(principal.getLoginName());
		if (user != null) {
			SimpleAuthorizationInfo info = new SimpleAuthorizationInfo();
			List<Menu> list = UserUtils.getMenuList();
			for (Menu menu : list){
				if (StringUtils.isNotBlank(menu.getPermission())){
					// 添加基于Permission的权限信息
					for (String permission : StringUtils.split(menu.getPermission(),",")){
						info.addStringPermission(permission);
					}
				}
			}
			// 添加用户权限
			info.addStringPermission("user");
			// 添加用户角色信息
			for (Role role : user.getRoleList()){
				info.addRole(role.getName());
			}
			// 更新登录IP和时间
			getSystemService().updateUserLoginInfo(user);
			// 记录登录日志
			LogUtils.saveLog(Servlets.getRequest(), "系统登录");
			return info;
		} else {
			return null;
		}
	}
	
	@Override
	protected void checkPermission(Permission permission, AuthorizationInfo info) {
		authorizationValidate(permission);
		super.checkPermission(permission, info);
	}
	
	@Override
	protected boolean[] isPermitted(List<Permission> permissions, AuthorizationInfo info) {
		if (permissions != null && !permissions.isEmpty()) {
            for (Permission permission : permissions) {
        		authorizationValidate(permission);
            }
        }
		return super.isPermitted(permissions, info);
	}
	
	@Override
	public boolean isPermitted(PrincipalCollection principals, Permission permission) {
		authorizationValidate(permission);
		return super.isPermitted(principals, permission);
	}
	
	@Override
	protected boolean isPermittedAll(Collection<Permission> permissions, AuthorizationInfo info) {
		if (permissions != null && !permissions.isEmpty()) {
            for (Permission permission : permissions) {
            	authorizationValidate(permission);
            }
        }
		return super.isPermittedAll(permissions, info);
	}
	
	/**
	 * 授权验证方法
	 * @param permission
	 */
	private void authorizationValidate(Permission permission){
		// 模块授权预留接口
	}
	
	/**
	 * 设定密码校验的Hash算法与迭代次数
	 */
	@PostConstruct
	public void initCredentialsMatcher() {
		if (Global.isCommonMd5Used()) {
			// 与C#程序共用加密算法
			HashedCredentialsMatcher matcher = new HashedCredentialsMatcher(Md5Hash.ALGORITHM_NAME);
			setCredentialsMatcher(matcher);
		} else {
			// 不与C#程序共用加密算法
			HashedCredentialsMatcher matcher = new HashedCredentialsMatcher(SystemService.HASH_ALGORITHM);
			matcher.setHashIterations(SystemService.HASH_INTERATIONS);
			setCredentialsMatcher(matcher);
		}
	}
	
//	/**
//	 * 清空用户关联权限认证，待下次使用时重新加载
//	 */
//	public void clearCachedAuthorizationInfo(Principal principal) {
//		SimplePrincipalCollection principals = new SimplePrincipalCollection(principal, getName());
//		clearCachedAuthorizationInfo(principals);
//	}

	/**
	 * 清空所有关联认证
	 * @Deprecated 不需要清空，授权缓存保存到session中
	 */
	@Deprecated
	public void clearAllCachedAuthorizationInfo() {
//		Cache<Object, AuthorizationInfo> cache = getAuthorizationCache();
//		if (cache != null) {
//			for (Object key : cache.keys()) {
//				cache.remove(key);
//			}
//		}
	}

	/**
	 * 获取系统业务对象
	 */
	public SystemService getSystemService() {
		if (systemService == null){
			systemService = SpringContextHolder.getBean(SystemService.class);
		}
		return systemService;
	}
	
	/**
	 * 获取系统业务对象
	 */
	public SysOtpService getSysOtpService() {
		if (sysOtpService == null) {
			sysOtpService = SpringContextHolder.getBean(SysOtpService.class);
		}
		return sysOtpService;
	}
	
	/**
	 * 获取系统业务对象(令牌机构管理)
	 */
	public SysOtpOfficeService getSysOtpOfficeService() {
		if (sysOtpOfficeService == null) {
			sysOtpOfficeService = SpringContextHolder.getBean(SysOtpOfficeService.class);
		}
		return sysOtpOfficeService;
	}

	/**
	 * 授权用户信息
	 */
	public static class Principal implements Serializable {

		private static final long serialVersionUID = 1L;
		
		private String id; // 编号
		private String loginName; // 登录名
		private String name; // 姓名
		private boolean mobileLogin; // 是否手机登录
		
//		private Map<String, Object> cacheMap;

		public Principal(User user, boolean mobileLogin) {
			this.id = user.getId();
			this.loginName = user.getLoginName();
			this.name = user.getName();
			this.mobileLogin = mobileLogin;
		}

		public String getId() {
			return id;
		}

		public String getLoginName() {
			return loginName;
		}

		public String getName() {
			return name;
		}

		public boolean isMobileLogin() {
			return mobileLogin;
		}

//		@JsonIgnore
//		public Map<String, Object> getCacheMap() {
//			if (cacheMap==null){
//				cacheMap = new HashMap<String, Object>();
//			}
//			return cacheMap;
//		}

		/**
		 * 获取SESSIONID
		 */
		public String getSessionid() {
			try{
				return (String) UserUtils.getSession().getId();
			}catch (Exception e) {
				return "";
			}
		}
		
		@Override
		public String toString() {
			return id;
		}

	}
}
