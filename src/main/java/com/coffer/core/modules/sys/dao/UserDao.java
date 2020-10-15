/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.coffer.core.modules.sys.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.coffer.core.common.persistence.CrudDao;
import com.coffer.core.common.persistence.annotation.MyBatisDao;
import com.coffer.core.modules.sys.entity.User;

/**
 * 用户DAO接口
 * @author ThinkGem
 * @version 2014-05-16
 */
@MyBatisDao
public interface UserDao extends CrudDao<User> {
	
	/**
	 * 根据登录名称查询用户
	 * @param loginName
	 * @return
	 */
	public User getByLoginName(User user);

	/**
	 * 通过OfficeId获取用户列表，仅返回用户id和name（树查询用户时用）
	 * @param user
	 * @return
	 */
	public List<User> findUserByOfficeId(User user);
	
	/**
	 * 
	 * @author chenxin
	 * @version 2015年10月10日
	 * 通过OfficeId获取用户列表（接口用）
	 * @param user
	 * @return
	 */
	public List<User> findUserInfoByOfficeId(@Param("searchDate") String searchDate, @Param("user") User user,
			@Param("userTypes") List<String> userTypes, @Param("dbName") String dbName);
	
	/**
	 * 查询全部用户数目
	 * @return
	 */
	public long findAllCount(User user);
	
	/**
	 * 更新用户密码
	 * @param user
	 * @return
	 */
	public int updatePasswordById(User user);
	
	/**
	 * 更新登录信息，如：登录IP、登录时间
	 * @param user
	 * @return
	 */
	public int updateLoginInfo(User user);

	/**
	 * 删除用户角色关联数据
	 * @param user
	 * @return
	 */
	public int deleteUserRole(User user);
	
	/**
	 * 插入用户角色关联数据
	 * @param user
	 * @return
	 */
	public int insertUserRole(User user);
	
	/**
	 * 更新用户信息
	 * @param user
	 * @return
	 */
	public int updateUserInfo(User user);
	
	/**
	 * 
	 * Title: updateByIdcardNo
	 * <p>Description: 通过身份证号更新用户信息</p>
	 * @author:     wangbaozhong
	 * @param user 用户信息
	 * @return 
	 * int    返回类型
	 */
	public int updateByIdcardNo(User user);

	/**
	 * 根据用户脸部识别ID查询用户
	 * @param user 查询条件
	 * @return
	 */
	public User getByUserFaceId(User user);
	
	/**
	 * 根据机构ID查询人民银行、商业机构、清分中心设定脸部识别ID数量
	 * Title: getPbocFaceIdCntByOfficeId
	 * <p>Description: </p>
	 * @author:     wangbaozhong
	 * @param officeId 机构ID
	 * @return 设定脸部识别ID数量
	 * int    返回类型
	 */
	public int getPbocFaceIdCntByOfficeId(@Param("officeId") String officeId, @Param("dbName") String dbName);
	
	/**
	 * 根据设定参数区间，查询最大脸部识别ID
	 * @author WangBaozhong
	 * @version 2017年4月12日
	 * 
	 *  
	 * @param maxId	设定区间最大ID
	 * @param minId	设定区间最小ID
	 * @return	数据库中最大ID
	 */
	public Long getMaxFaceId(@Param("minId") String minId, @Param("maxId") String maxId);
	
	/**
	 * 按身份证号查询用户是否存在
	 * @author WangBaozhong
	 * @version 2017年6月13日
	 * 
	 *  
	 * @param user 查询条件 身份证号
	 * @return 用户信息
	 */
	public User checkUserByIdCard(User user);
	
	/**
	 * 
	 * Title: findUserInfoByOfficeIds
	 * <p>Description: </p>
	 * @author:     wangbaozhong
	 * @param lastSearchDate	上次查询日期
	 * @param officeIds	机构列表
	 * @return 用户信息列表
	 * List<User>    返回类型
	 */
	public List<User> findUserInfoByOfficeIds(@Param("lastSearchDate") String lastSearchDate,
			@Param("officeIds") List<String> officeIds, @Param("dbName") String dbName);
	

	/**
	 * 根据用户类型查询所有用户
	 * @author Qipeihong
	 * @version 2017年8月16日
	 * 
	 *  
	 * @param user 查询条件 用户类型
	 * @return 用户信息
	 */
	public List<User> findUserInfoByUserType(User user);
	
	/**
	 * 根据身份证号查询用户信息
	 * @author zxk
	 * @version 2019年7月9日
	 * @param user
	 * @return
	 */
	public User selectUserByCard(User user);

    /**
     * 更新登录名和密码
     *
     * @param loginName
     * @param password
     * @param userId
     * @return
     * @author yinkai
     * @version 2019年8月30日
     */
    public int updateLoginNameAndPassword(@Param(value = "loginName") String loginName, @Param(value = "password") String password, @Param(value = "userId") String userId);
	
    /**
     * 根据unionId获取用户信息
     *
     * @param unionId
     * @return
     * @author gj
     * @version 2019年12月1日
     */
    public User getUserByUnionId(String unionId);
    /**
     * 
     * Title: getUserById
     * <p>Description:根据id获取用户信息 </p>
     * @author:     HaoShijie
     * @param id
     * @return 
     * List<User>    返回类型
     */
    public User getUserById(String id);
}
