package com.coffer.businesses.modules.store.v01.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.coffer.businesses.common.Constant;
import com.coffer.businesses.common.utils.BusinessUtils;
import com.coffer.businesses.modules.store.v01.dao.StoEscortInfoDao;
import com.coffer.businesses.modules.store.v01.entity.StoEscortInfo;
import com.coffer.core.common.config.Global;
import com.coffer.core.common.persistence.Page;
import com.coffer.core.common.service.BusinessException;
import com.coffer.core.common.service.CrudService;
import com.coffer.core.common.utils.CacheUtils;
import com.coffer.core.common.utils.Encodes;
import com.coffer.core.common.utils.StringUtils;
import com.coffer.core.modules.sys.SysCommonUtils;
import com.coffer.core.modules.sys.entity.Office;
import com.coffer.core.modules.sys.entity.User;
import com.coffer.core.modules.sys.service.FaceIdSerialNumberService;
import com.coffer.core.modules.sys.utils.UserUtils;
import com.coffer.external.ExternalConstant;
import com.coffer.external.Parameter;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

/**
 * 人员管理Service
 * 
 * @author niguoyong
 * @date 2015-09-06
 */
@Service
@Transactional(readOnly = true)
public class StoEscortInfoService extends CrudService<StoEscortInfoDao, StoEscortInfo> {

	@Autowired
	private StoEscortInfoDao stoEscortInfoDao;
	@Autowired
	private FaceIdSerialNumberService faceIdSerialNumberService;

	public static final String CACHE_STOESCORT_MAP = "stoEscortMap";
	/** 商业银行 用户信息 **/
	public static final String CACHE_COM_BANK_USER_INFO_MAP = "commercialBankUserInfoMap";

	public StoEscortInfo get(String id) {
		return super.get(id);
	}

	public List<StoEscortInfo> findList(StoEscortInfo stoEscortInfo) {
		return super.findList(stoEscortInfo);
	}

	public Page<StoEscortInfo> findPage(Page<StoEscortInfo> page, StoEscortInfo stoEscortInfo) {
		stoEscortInfo.getSqlMap().put("dsf", dataScopeFilter(UserUtils.getUser(), "o15", null));
		return super.findPage(page, stoEscortInfo);
	}

	/**
	 * @author niguoyong @date 2015-09-06
	 * 
	 * @Description 处理“人员增加”（或“人员编辑”）画面的保存操作 @param 参数 @return @throws
	 */
	@Transactional(readOnly = false)
	public void save(StoEscortInfo stoEscortInfo) {

		// 创建RFID
		if (stoEscortInfo != null && StringUtils.isBlank(stoEscortInfo.getRfid())) {
			stoEscortInfo.setRfid(createRfid(stoEscortInfo));
			stoEscortInfo.setBindingRfid(Constant.Escort.UN_BINDING_FLAG);
		}

		// 当前保存用户所属机构
		Office uesrOffice = stoEscortInfo.getOffice();
		Office pbocOffice = null;
		if (!Constant.OfficeType.CENTRAL_BANK.equals(uesrOffice.getType())
				&& !Constant.OfficeType.DIGITAL_PLATFORM.equals(uesrOffice.getType())) {
			pbocOffice = BusinessUtils.getPbocCenterByOffice(uesrOffice);
		} else {
			pbocOffice = uesrOffice;
		}

		// 用户人脸识别ID未设定，并且 初始化flag不为空，则生成用户人脸识别ID
		if (Global.TRUE.equals(Global.getConfig("sys.needfaceId"))
				&& StringUtils.isNotBlank(stoEscortInfo.getInitFaceIdFlag()) && stoEscortInfo.getUserFaceId() == null) {

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

			stoEscortInfo.setUserFaceId(faceId);
		}

		// 保存
		if (StringUtils.isEmpty(stoEscortInfo.getId())) {
			// 押运员ID为空的场合，新增。
			stoEscortInfo.setIsNewRecord(true);
			// modify by yuxixuan 2017-07-19 ------start
			// 鞍山金库数字化金融服务平台系统中，押运人员不采集指纹，所以直接将del_flag改为0.
			// stoEscortInfo.setDelFlag(StoEscortInfo.DEL_FLAG_AUDIT);
			stoEscortInfo.setDelFlag(StoEscortInfo.DEL_FLAG_NORMAL);
			// modify by yuxixuan 2017-07-19 ------end
			stoEscortInfo.setBindingRoute(Constant.Escort.UN_BINDING_ROUTE);
			super.save(stoEscortInfo);
		} else {
			// 押运员ID不为空的场合，修改
			super.save(stoEscortInfo);
		}

		// 清理缓存
		if (Constant.SysUserType.ESCORT.equals(stoEscortInfo.getEscortType())) {
			CacheUtils.remove(CACHE_STOESCORT_MAP);
		}

	}

	/**
	 * @author niguoyong @date 2015-09-06
	 * 
	 * @Description 修改人员绑定 @param 参数 @return @throws
	 */
	@Transactional(readOnly = false)
	public void updateBinding(StoEscortInfo stoEscortInfo) {
		stoEscortInfoDao.updateBinding(stoEscortInfo);
	}

	/**
	 * 
	 * 获取商行用户下拉列表List
	 * 
	 * @author wangbaozhong
	 * @date 2016-09-13
	 * 
	 * @return
	 */
	@Transactional(readOnly = true)
	public List<StoEscortInfo> getCommercialBankUserInfoList(String getType, String officeId) {
		@SuppressWarnings("unchecked")
		Map<String, List<StoEscortInfo>> dictMap = (Map<String, List<StoEscortInfo>>) CacheUtils
				.get(CACHE_COM_BANK_USER_INFO_MAP);
		if (dictMap == null) {
			dictMap = Maps.newHashMap();
			List<StoEscortInfo> allList = Lists.newArrayList();
			// 获取金库主管
			List<StoEscortInfo> cofferMgrlist = stoEscortInfoDao
					.findBankEscortList(Constant.SysUserType.COFFER_MANAGER);
			// 将所有人员信息放入缓存
			allList.addAll(cofferMgrlist);

			// 获取金库操作员
			List<StoEscortInfo> cofferOptlist = stoEscortInfoDao.findBankEscortList(Constant.SysUserType.COFFER_OPT);
			// 将所有人员信息放入缓存
			allList.addAll(cofferOptlist);

			dictMap.put(Constant.Escort.ALL_ESCORT_LIST, allList);
			CacheUtils.put(CACHE_STOESCORT_MAP, dictMap);
		}
		List<StoEscortInfo> stoEscortInfoList = dictMap.get(getType);

		if (StringUtils.isBlank(officeId)) {
			if (stoEscortInfoList != null) {
				return stoEscortInfoList;
			} else {
				return Lists.newArrayList();
			}
		}
		List<StoEscortInfo> rtnEscortInfoList = Lists.newArrayList();
		for (StoEscortInfo escortInfo : stoEscortInfoList) {
			if (escortInfo.getOffice().getId().equals(officeId)) {
				rtnEscortInfoList.add(escortInfo);
			}
		}
		return rtnEscortInfoList;
	}

	/**
	 * 
	 * 押运人员列表准备
	 * 
	 * @author niguoyong
	 * @date 2015-09-06
	 * 
	 * @Description
	 * @param type
	 * @return
	 */
	public List<StoEscortInfo> getStoEscortinfoList(String getType) {
		// 线路选择押运人员
		String usedEscort = Global.getConfig("route.used.escort");
		if (StringUtils.isNotBlank(usedEscort) && Constant.Escort.USER_ESCORT.equals(usedEscort)) {
			return isOrNoStoEscortInfoList(getType);
		}
		return null;
	}

	/**
	 * @author niguoyong @date 2015-09-06
	 * 
	 * @Description 删除 @param 参数 @return @throws
	 */
	@Transactional(readOnly = false)
	public void delete(StoEscortInfo stoEscortInfo) {
		stoEscortInfo.preUpdate();
		super.delete(stoEscortInfo);
		CacheUtils.remove(CACHE_STOESCORT_MAP);
	}

	// /**
	// * @author niguoyong
	// * @date 2015-09-06
	// *
	// * @Description 在“添加用户信息”画面添加押运人员
	// * @param 参数
	// * @return
	// * @throws
	// */
	// @Transactional(readOnly = false)
	// public void saveEscortInfo(String[] userId) {
	//
	// for (String id : userId) {
	// User user = userDao.get(id);
	// if (user != null && StringUtils.isNotBlank(user.getIdcardNo())) {
	// StoEscortInfo stoEscortInfo = new StoEscortInfo();
	// stoEscortInfo.setUser(user);
	// stoEscortInfo.setEscortName(user.getName());
	// stoEscortInfo.setIdcardNo(user.getIdcardNo());
	// stoEscortInfo.setOffice(user.getOffice());
	// stoEscortInfo.setPhone(user.getMobile());
	// stoEscortInfo.setEscortType(user.getUserType());
	// super.save(stoEscortInfo);
	// }
	// }
	// }

	/**
	 * @author niguoyong @date 2015-09-06
	 * 
	 * @Description 根据身份证号查找押运人员 @param 参数 @return @throws
	 */
	public StoEscortInfo findByIdcardNo(String idcardNo) {
		StoEscortInfo stoEscortInfo = new StoEscortInfo();
		stoEscortInfo.setIdcardNo(idcardNo);
		List<StoEscortInfo> list = findList(stoEscortInfo);
		if (list == null || list.isEmpty()) {
			// 没有找到的场合
			return null;
		}

		// 身份证号唯一的场合
		return list.get(0);
	}

	/**
	 * 
	 * @author niguoyong
	 * @date 2015-09-06 根据身份证获取信息
	 * 
	 * @param headInfo
	 * @return
	 */
	public StoEscortInfo searchEscort(Map<String, Object> headInfo) {
		return findByIdcardNo(headInfo.get("idcardNo").toString());

	}

	/**
	 * 
	 * @author niguoyong
	 * @date 2015-09-06 人员信息采集接口
	 * 
	 * @param headInfo
	 * @return
	 * @throws Exception
	 */
	@Transactional(readOnly = false)
	public boolean updateEscortInfo(Map<String, Object> headInfo) throws Exception {
		StoEscortInfo stoescortinfo = stoEscortInfoDao.get(headInfo.get("escortId").toString());
		if (stoescortinfo != null) {
			// 设定人员姓名
			if (headInfo.get("escortName") != null && StringUtils.isNotEmpty(headInfo.get("escortName").toString())) {
				stoescortinfo.setEscortName(headInfo.get("escortName").toString());
			}
			// 设定地址
			if (headInfo.get("address") != null && StringUtils.isNotEmpty(headInfo.get("address").toString())) {
				stoescortinfo.setAddress(headInfo.get("address").toString());
			}
			// 设定出生日期
			if (headInfo.get("identityBirth") != null
					&& StringUtils.isNotEmpty(headInfo.get("identityBirth").toString())) {
				stoescortinfo.setIdentityBirth(headInfo.get("identityBirth").toString());
			}
			// 设定发证机关
			if (headInfo.get("identityVisa") != null
					&& StringUtils.isNotEmpty(headInfo.get("identityVisa").toString())) {
				stoescortinfo.setIdentityVisa(headInfo.get("identityVisa").toString());
			}
			// 设定性别
			if (headInfo.get("identityGender") != null
					&& StringUtils.isNotEmpty(headInfo.get("identityGender").toString())) {
				stoescortinfo.setIdentityGender(headInfo.get("identityGender").toString());
			}
			// 设定民族
			if (headInfo.get("identityNational") != null
					&& StringUtils.isNotEmpty(headInfo.get("identityNational").toString())) {
				stoescortinfo.setIdentityNational(headInfo.get("identityNational").toString());
			}
			// 绑定指纹待定
			if (headInfo.get("fingerNo1") != null && headInfo.get("fingerNo2") != null) {

				byte[] fingerNo1 = Encodes.decodeBase64(headInfo.get("fingerNo1").toString());
				byte[] fingerNo2 = Encodes.decodeBase64(headInfo.get("fingerNo2").toString());
				if (fingerNo1.length != 0 && fingerNo2.length != 0) {
					stoescortinfo.setFingerNo1(fingerNo1);
					stoescortinfo.setFingerNo2(fingerNo2);
					stoescortinfo.setDelFlag(StoEscortInfo.DEL_FLAG_NORMAL);
				}
			}
			// PDA指纹采集
			if (headInfo.get("pdaFingerNo1") != null) {
				byte[] fingerNo1 = Encodes.decodeBase64(headInfo.get("pdaFingerNo1").toString());
				if (fingerNo1.length != 0) {
					stoescortinfo.setPdaFingerNo1(fingerNo1);
				}
			}
			// PDA指纹采集
			if (headInfo.get("pdaFingerNo2") != null) {

				byte[] fingerNo2 = Encodes.decodeBase64(headInfo.get("pdaFingerNo2").toString());
				if (fingerNo2.length != 0) {
					stoescortinfo.setPdaFingerNo2(fingerNo2);
				}
			}
			// 身份证照片 追加时间：2016-04-06 追加人：LLF
			if (headInfo.get("photo") != null && StringUtils.isNotBlank(headInfo.get("photo").toString())) {
				byte[] photo = Encodes.decodeBase64(headInfo.get("photo").toString());
				if (photo.length != 0) {
					stoescortinfo.setPhoto(photo);
				}
			}

			// 押运人员密码
			if (headInfo.get("password") != null && StringUtils.isNotEmpty(headInfo.get("password").toString())) {
				stoescortinfo.setPassword(headInfo.get("password").toString());
				stoescortinfo.setDelFlag(StoEscortInfo.DEL_FLAG_NORMAL);
			}

			// 人员绑定RFID标识
			if (headInfo.get("bindingRfid") != null && StringUtils.isNotEmpty(headInfo.get("bindingRfid").toString())) {
				stoescortinfo.setBindingRfid(headInfo.get("bindingRfid").toString());
			}
			// 人员印章
			if (headInfo.get(Parameter.USER_STAMPER_KEY) != null
					&& StringUtils.isNotBlank(headInfo.get(Parameter.USER_STAMPER_KEY).toString())) {
				byte[] userStamper = Encodes.decodeBase64(headInfo.get(Parameter.USER_STAMPER_KEY).toString());
				if (userStamper.length != 0) {
					stoescortinfo.setUserStamper(userStamper);
				}
			}
			super.save(stoescortinfo);
			// 设定人员姓名
			if (headInfo.get("escortName") != null && StringUtils.isNotEmpty(headInfo.get("escortName").toString())) {
				// 根据用户身份证号码，更新用户表中的 用户姓名
				User user = new User();
				user.setIdcardNo(stoescortinfo.getIdcardNo());
				user.setName(headInfo.get("escortName").toString());
				user.setUpdateDate(new Date());
				SysCommonUtils.updateByIdcardNo(user);
			}

			CacheUtils.remove(CACHE_STOESCORT_MAP);
			return true;
		}
		// 人员被删除
		return false;
	}

	/**
	 * 
	 * @author chenxin
	 * @version 2015年10月10日 根据机构获取人员信息(指定类型)
	 * @param headInfoMap
	 * @return
	 */
	public List<StoEscortInfo> findEscortInfo(Map<String, Object> headInfoMap, List<String> userTypes) {
		List<StoEscortInfo> list = new ArrayList<StoEscortInfo>();
		if (headInfoMap != null && headInfoMap.get("officeId") != null
				&& StringUtils.isNotBlank(headInfoMap.get("officeId").toString())) {
			if (userTypes == null || userTypes.size() <= 0) {
				userTypes = null;
			}
			String searchDate = headInfoMap.get("searchDate") != null ? headInfoMap.get("searchDate").toString() : "";
			list = stoEscortInfoDao.findEscortInfoByUserType(searchDate, headInfoMap.get("officeId").toString(),
					userTypes, Global.getConfig("jdbc.type"));
		}
		return list;
	}

	/**
	 * 
	 * @author chenxin
	 * @version 2015年10月10日 根据机构获取人员信息(指定机构ID)
	 * @param headInfoMap
	 * @return
	 */
	public List<StoEscortInfo> findPbocEscortInfo(Map<String, Object> headInfoMap, List<String> userTypes,
			List<String> officeTypes) {
		List<StoEscortInfo> list = new ArrayList<StoEscortInfo>();
		if (headInfoMap != null && headInfoMap.get("officeId") != null
				&& StringUtils.isNotBlank(headInfoMap.get("officeId").toString())) {
			if (userTypes == null || userTypes.size() <= 0) {
				userTypes = null;
			}
			String searchDate = headInfoMap.get("searchDate") != null ? headInfoMap.get("searchDate").toString() : "";
			officeTypes = (officeTypes == null || officeTypes.size() <= 0) ? null : officeTypes;
			list = stoEscortInfoDao.findPbocEscortInfoByOfficeId(searchDate, headInfoMap.get("officeId").toString(),
					userTypes, officeTypes, Global.getConfig("jdbc.type"));
		}
		return list;
	}

	/**
	 * 
	 * 获取下拉列表List
	 * 
	 * @author niguoyong
	 * @date 2015-09-06
	 * 
	 * @Description getType noBindingEscortList：未绑定线路的人员信息；allEscortList：所有押运人员信息
	 * @return
	 */
	private List<StoEscortInfo> isOrNoStoEscortInfoList(String getType) {

		@SuppressWarnings("unchecked")
		Map<String, List<StoEscortInfo>> dictMap = (Map<String, List<StoEscortInfo>>) CacheUtils
				.get(CACHE_STOESCORT_MAP);
		if (dictMap == null) {
			dictMap = Maps.newHashMap();
			List<StoEscortInfo> allList = Lists.newArrayList();
			List<StoEscortInfo> list = stoEscortInfoDao.findBankEscortList(Constant.SysUserType.ESCORT);
			// 将所有人员信息放入缓存
			allList.addAll(list);
			dictMap.put(Constant.Escort.ALL_ESCORT_LIST, allList);
			// 线路是否可以重复绑定押运人员
			String bindingEscort = Global.getConfig("route.double.binding.escort");
			if (StringUtils.isNotBlank(bindingEscort)
					&& Constant.RouteInfo.ROUTE_DOUBLE_BINDING_ESCORT.equals(bindingEscort)) {
				for (int i = list.size() - 1; i >= 0; i--) {
					// 过滤已经绑定线路的押运人员
					StoEscortInfo stoEscort = list.get(i);
					if (StringUtils.isNotBlank(stoEscort.getBindingRoute())
							&& Constant.Escort.BINDING_ROUTE.equals(stoEscort.getBindingRoute())) {
						list.remove(i);
					}
				}
			}
			dictMap.put(Constant.Escort.NO_BINDING_ESCORT_LIST, list);
			CacheUtils.put(CACHE_STOESCORT_MAP, dictMap);
		}
		List<StoEscortInfo> stoEscortinfoList = dictMap.get(getType);
		if (stoEscortinfoList == null) {
			stoEscortinfoList = Lists.newArrayList();
		}
		return stoEscortinfoList;
	}

	/**
	 * 
	 * @author niguoyong
	 * @date 2015-09-06
	 * 
	 *       根据身份证编号验证身份
	 * @param idcardNo
	 * @return
	 */
	public StoEscortInfo checkIdcardNo(String idcardNo) {
		return stoEscortInfoDao.findByIdcardNo(idcardNo);
	}

	/**
	 * 
	 * @author niguoyong
	 * @date 2015-09-06
	 * 
	 *       根据用户Id查询当前人员
	 * @param userId
	 * @return
	 */
	public StoEscortInfo findByUserId(String userId) {
		return stoEscortInfoDao.findByUserId(userId);
	}

	/**
	 * 
	 * 逻辑删除
	 * 
	 * @author niguoyong
	 * @date 2015-09-06
	 * 
	 * @Description
	 * @param id
	 * @return
	 */
	@Transactional(readOnly = false)
	public int delete(String id) {
		int deleteNum = stoEscortInfoDao.deleteById(id);
		// 清理缓存
		CacheUtils.remove(CACHE_STOESCORT_MAP);
		return deleteNum;
	}

	/**
	 * 
	 * * @author niguoyong
	 * 
	 * @date 2015-09-06
	 * 
	 *       根据用户类型查询金库人员信息
	 * @param officeId
	 * @param userType
	 * @return
	 */
	public List<StoEscortInfo> findEscortInfoByUserType(String officeId, String userType) {
		String[] params = StringUtils.split(userType, ";");
		return stoEscortInfoDao.findEscortInfoByUserType(null, officeId, Arrays.asList(params),
				Global.getConfig("jdbc.type"));
	}

	/**
	 * 
	 * @author LLF
	 * @version 2016年5月10日 创建人员RFID
	 * 
	 * @param stoEscortInfo
	 * @return
	 */
	private String createRfid(StoEscortInfo stoEscortInfo) {
		if (stoEscortInfo != null && stoEscortInfo.getOffice() != null
				&& StringUtils.isNotBlank(stoEscortInfo.getOffice().getId())) {
			int seqNo = stoEscortInfoDao.getSeqNo(stoEscortInfo.getEscortType());
			String escortNo = BusinessUtils.generateBoxNo(stoEscortInfo.getEscortType(), seqNo + 1);

			return BusinessUtils.fillOfficeId(stoEscortInfo.getOffice().getId()) + StringUtils.substring(escortNo, 1)
					+ Constant.Escort.RFID_ESCORT_TYPE;
		} else {
			return "";
		}
	}

	/**
	 * @author xp @date 2017-8-2
	 * 
	 * @Description 修改脸谱 @param 参数 @return @throws
	 */
	@Transactional(readOnly = false)
	public void update(StoEscortInfo stoEscortInfo) {
		int i = stoEscortInfoDao.update(stoEscortInfo);
		if (i == 0) {
			String strMessageContent = "userFaceId为：" + stoEscortInfo.getUserFaceId() + "信息异常";
			throw new BusinessException(ExternalConstant.HardwareInterface.ERROR_NO_E99, strMessageContent);
		}
	}

	/**
	 * @author xp @date 2017-8-8
	 * 
	 * @Description 查询当前机构及子机构的所有人员 @param 参数 @return @throws
	 */
	public List<StoEscortInfo> findEscortList(StoEscortInfo stoEscortInfo) {
		stoEscortInfo.getSqlMap().put("dsf", " OR o15.parent_ids LIKE '%" + stoEscortInfo.getOffice().getId() + "%'");
		return super.findList(stoEscortInfo);
	}

	/**
	 * @author xp @date 2017-8-8
	 * 
	 * @Description 查询当前机构及子机构的所有人员 @param 参数 @return @throws
	 */
	public List<StoEscortInfo> findBankUserList(String officeId) {

		return stoEscortInfoDao.findBankUserList(officeId);
	}

	/**
	 * @author xp @date 2017-9-27
	 * @param paramMap
	 * @Description 查询人员照片信息
	 * @return List<StoEscortInfo>
	 */
	@Transactional(readOnly = true)
	public List<StoEscortInfo> findPhotoList(String officeId, String searchDate) {
		return stoEscortInfoDao.findPhotoList(officeId, searchDate, Global.getConfig("jdbc.type"));
	}

	/**
	 * @author liuyaowen
	 * 
	 * @Description 修改人员绑定
	 */
	@Transactional(readOnly = false)
	public void updateBindingRfid(StoEscortInfo stoEscortInfo) {
		stoEscortInfoDao.updateBindingRfid(stoEscortInfo);
	}

	public StoEscortInfo findByRfid(String rfid) {
		return stoEscortInfoDao.findByRfid(rfid);
	}

	/**
	 * @author liuyaowen
	 * 
	 * @Description 根据身份证号查找押运人员
	 */
	public StoEscortInfo findLikeByIdcardNo(String idcardNo) {
		StoEscortInfo stoEscortInfo = new StoEscortInfo();
		stoEscortInfo.setIdcardNo(idcardNo);
		List<StoEscortInfo> list = stoEscortInfoDao.findLikeByIdcardNo(stoEscortInfo);
		if (list == null || list.isEmpty()) {
			// 没有找到的场合
			return null;
		}

		// 身份证号唯一的场合
		return list.get(0);
	}

	/**
	 * 
	 * @author sg
	 * @date 2017-11-24
	 * 
	 *       根据escortId取得未删除的人员信息
	 * @param escortId
	 * @return
	 */
	public StoEscortInfo findByEscortId(String escortId) {
		return stoEscortInfoDao.findByEscortId(escortId);
	}
}