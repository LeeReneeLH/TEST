package com.coffer.businesses.modules.atm.v01.service;

import java.io.File;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.validation.ConstraintViolationException;
import javax.validation.Validator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import com.coffer.businesses.common.Constant;
import com.coffer.businesses.modules.allocation.AllocationConstant;
import com.coffer.businesses.modules.atm.ATMConstant;
import com.coffer.businesses.modules.atm.AtmCommonUtils;
import com.coffer.businesses.modules.atm.v01.dao.AtmBoxModDao;
import com.coffer.businesses.modules.atm.v01.dao.AtmBrandsInfoDao;
import com.coffer.businesses.modules.atm.v01.dao.AtmInfoMaintainDao;
import com.coffer.businesses.modules.atm.v01.dao.AtmPlanInfoDao;
import com.coffer.businesses.modules.atm.v01.entity.AtmBoxMod;
import com.coffer.businesses.modules.atm.v01.entity.AtmBrandsInfo;
import com.coffer.businesses.modules.atm.v01.entity.AtmInfoMaintain;
import com.coffer.businesses.modules.atm.v01.entity.AtmPlanInfo;
import com.coffer.core.common.beanvalidator.BeanValidators;
import com.coffer.core.common.config.Global;
import com.coffer.core.common.persistence.Page;
import com.coffer.core.common.service.CrudService;
import com.coffer.core.common.utils.Collections3;
import com.coffer.core.common.utils.DateUtils;
import com.coffer.core.common.utils.IdGen;
import com.coffer.core.common.utils.StringUtils;
import com.coffer.core.common.utils.excel.ImportExcel;
import com.coffer.core.modules.sys.service.SystemService;
import com.coffer.core.modules.sys.utils.UserUtils;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

/**
 * 加钞计划导入Service
 * 
 * @author XL
 * @version 2017-11-07
 */
@Service
@Transactional(readOnly = true)
public class AtmPlanInfoService extends CrudService<AtmPlanInfoDao, AtmPlanInfo> {

	@Autowired
	private AtmPlanInfoDao atmPlanInfoDao;

	@Autowired
	private AtmInfoMaintainDao atmInfoMaintainDao;

	@Autowired
	private AtmBrandsInfoDao atmBrandsInfoDao;

	@Autowired
	private AtmBoxModDao atmBoxModDao;

	public AtmPlanInfo get(String id) {
		return super.get(id);
	}

	/**
	 * 加钞计划查询
	 * 
	 * @author XL
	 * @version 2017年11月07日
	 * @param page
	 * @param atmPlanInfo
	 * @return
	 */
	public Page<AtmPlanInfo> findAddPlanList(Page<AtmPlanInfo> page, AtmPlanInfo atmPlanInfo) {
		// 查询条件： 开始时间
		if (atmPlanInfo != null && atmPlanInfo.getCreateTimeStart() != null) {
			atmPlanInfo.setSearchDateStart(
					DateUtils.foramtSearchDate(DateUtils.getDateStart(atmPlanInfo.getCreateTimeStart())));
		}
		// 查询条件： 结束时间
		if (atmPlanInfo != null && atmPlanInfo.getCreateTimeEnd() != null) {
			atmPlanInfo
					.setSearchDateEnd(DateUtils.foramtSearchDate(DateUtils.getDateEnd(atmPlanInfo.getCreateTimeEnd())));
		}
		page.setOrderBy("add_plan_id desc");
		// 生成数据权限过滤条件（dsf为dataScopeFilter的简写，在xml中使用 ${sqlMap.dsf}调用权限SQL）
		atmPlanInfo.getSqlMap().put("dsf", dataScopeFilter(UserUtils.getUser(), "o8", null));
		// 设置分页
		atmPlanInfo.setPage(page);
		// 执行分页语句
		page.setList(atmPlanInfoDao.findAddPlanList(atmPlanInfo));
		return page;
	}

	/**
	 * 查询加钞计划信息
	 * 
	 * @author XL
	 * @version 2017年11月07日
	 * @param atmPlanInfo
	 * @return
	 */
	public List<AtmPlanInfo> findAddPlanList(AtmPlanInfo atmPlanInfo) {
		return atmPlanInfoDao.findAddPlanList(atmPlanInfo);
	}

	/**
	 * 计划详细查询
	 * 
	 * @author XL
	 * @version 2017年11月07日
	 * @param page
	 * @param atmPlanInfo
	 * @return
	 */
	public Page<AtmPlanInfo> addPlanView(Page<AtmPlanInfo> page, AtmPlanInfo atmPlanInfo) {
		// 执行分页语句
		page.setList(atmPlanInfoDao.addPlanView(atmPlanInfo));
		return page;
	}

	/**
	 * 计划导入保存
	 * 
	 * @author XL
	 * @version 2017年11月07日
	 * @param AtmPlanInfos
	 * @param validator
	 * @param map
	 * @return
	 */
	@Transactional(readOnly = false)
	public boolean save(List<AtmPlanInfo> AtmPlanInfos, Validator validator, Map<String, Object> map) {
		// 提示消息列表
		@SuppressWarnings("unchecked")
		List<String> msgs = (List<String>) map.get("msgs");
		try {
			// 效验加钞计划信息
			for (AtmPlanInfo atmPlanInfo : AtmPlanInfos) {
				BeanValidators.validateWithException(validator, atmPlanInfo);
			}
			// 保存加钞计划信息
			for (AtmPlanInfo atmPlanInfo : AtmPlanInfos) {
				super.save(atmPlanInfo);
			}
		} catch (ConstraintViolationException e) {
			// 校验失败
			e.printStackTrace();
			msgs.add("message.E4021");
			return false;
		} catch (Exception e) {
			// 保存失败
			e.printStackTrace();
			msgs.add("message.E4020");
			return false;
		}
		msgs.add("message.I4011");
		return true;
	}

	/**
	 * 逻辑删除
	 * 
	 * @author XL
	 * @version 2017年11月07日
	 * @param AtmPlanInfo
	 * @return
	 */
	@Transactional(readOnly = false)
	public void delete(AtmPlanInfo atmPlanInfo) {
		super.delete(atmPlanInfo);
	}

	/**
	 * 加钞计划导入
	 * 
	 * @author XL
	 * @version 2017年11月07日
	 * @param AtmPlanInfo
	 * @param validator
	 * @return
	 */
	@Transactional(readOnly = false)
	public Map<String, Object> importAtmPlanInfo(CommonsMultipartFile atmPlanInfo, Validator validator) {
		// 返回内容
		Map<String, Object> map = Maps.newHashMap();
		// 提示消息
		List<String> msgs = Lists.newArrayList();
		map.put("msgs", msgs);
		// 加钞计划id
		String addPlanId = IdGen.getIdByTime();
		// 导入计划列表
		List<AtmPlanInfo> atmPlanInfos = Lists.newArrayList();

		// 1.上传加钞计划单
		String path = Global.getConfig(ATMConstant.UploadAddPlanInfoDir) + addPlanId
				+ atmPlanInfo.getOriginalFilename();
		File localFile = new File(path);
		if (!uploadAddPlanInfo(atmPlanInfo, localFile, map)) {
			return map;
		}

		// 2.读取加钞计划文件信息，取得关键数据
		if (!readAddPlanInfoInfo(localFile, atmPlanInfos, addPlanId, map)) {
			return map;
		}

		// 3.验证加钞计划ID
		addPlanId = atmPlanInfos.get(0).getAddPlanId();
		if (!validateAddPlanId(addPlanId, map)) {
			return map;
		}

		// 4.验证导入文件ATM机信息
		if (!validateAtmInfo(atmPlanInfos, map)) {
			return map;
		}

		// 5.将关键信息存入加钞计划表
		if (!save(atmPlanInfos, validator, map)) {
			return map;
		}

		// 6.导入成功，显示
		map.put("addPlanId", addPlanId);
		return map;
	}

	/**
	 * 上传加钞单文档
	 * 
	 * @author XL
	 * @version 2017年11月07日
	 * @param AtmPlanInfo
	 * @param localFile
	 * @param map
	 * @return
	 */
	private boolean uploadAddPlanInfo(CommonsMultipartFile AtmPlanInfo, File localFile, Map<String, Object> map) {
		/*
		 * 1.判断上传文件是否存在
		 * 
		 * 2.1文件存在->将文件复制到指定路径下 2.2文件不存在->返回文件不存在
		 * 
		 * 3.返回消息提示key
		 */
		@SuppressWarnings("unchecked")
		List<String> msgs = (List<String>) map.get("msgs");
		if (!AtmPlanInfo.isEmpty()) {
			try {
				AtmPlanInfo.transferTo(localFile);
			} catch (Exception e) {
				// 上传失败
				msgs.add("message.E4017");
				return false;
			}
		} else {
			msgs.add("message.E4018");
			return false;
		}
		msgs.add("message.I4009");
		return true;
	}

	/**
	 * 读取加钞单文档内容
	 * 
	 * @author XL
	 * @version 2017年11月07日
	 * @param localFile
	 * @param atmPlanInfos
	 * @param addPlanId
	 * @param map
	 * @return
	 */
	private boolean readAddPlanInfoInfo(File localFile, List<AtmPlanInfo> atmPlanInfos, String addPlanId,
			Map<String, Object> map) {
		// 提示消息列表
		@SuppressWarnings("unchecked")
		List<String> msgs = (List<String>) map.get("msgs");
		try {
			// 获取数据list信息
			ImportExcel ei = new ImportExcel(localFile, 3, 0);
			List<AtmPlanInfo> dataList = ei.getDataList(AtmPlanInfo.class);
			// 加钞计划文件标题头
			String addPlanName = StringUtils.trim(StringUtils.toString(ei.getCellValue(ei.getRow(0), 0)));
			// 加钞计划文件标题为空
			if (StringUtils.isBlank(addPlanName)) {
				msgs.add("message.E4052");
				return false;
			}
			// 加钞计划ID
			String addPlanIdImport = StringUtils.trim(StringUtils.toString(ei.getCellValue(ei.getRow(2), 1)));
			// 是否导入加钞计划ID
			if (StringUtils.isNotBlank(addPlanIdImport)) {
				addPlanId = addPlanIdImport;
			}
			// 加钞计划是否为空
			if (Collections3.isEmpty(dataList)) {
				msgs.add("message.E4051");
				return false;
			}
			for (AtmPlanInfo atmPlanInfo : dataList) {
				if (atmPlanInfo.getNoImp().contains("制表人")) {
					break;
				}
				// 验证是否存在未填项
				if (StringUtils.isBlank(atmPlanInfo.getAtmNo())) {
					msgs.add("message.E4044");
					return false;
				}
				if (StringUtils.isBlank(atmPlanInfo.getAtmAccount())) {
					msgs.add("message.E4045");
					return false;
				}
				if (StringUtils.isBlank(atmPlanInfo.getAtmAddress())) {
					msgs.add("message.E4046");
					return false;
				}
				if (StringUtils.isBlank(atmPlanInfo.getAddAmountStr())) {
					msgs.add("message.E4047");
					return false;
				}
				if (StringUtils.isBlank(atmPlanInfo.getGetBoxNumStr())) {
					msgs.add("message.E4048");
					return false;
				}
				if (StringUtils.isBlank(atmPlanInfo.getAtmTypeNo())) {
					msgs.add("message.E4049");
					return false;
				}
				if (StringUtils.isBlank(atmPlanInfo.getAtmTypeName())) {
					msgs.add("message.E4050");
					return false;
				}
				// 设置ATM机编号
				atmPlanInfo.setAtmNo(StringUtils.trim(atmPlanInfo.getAtmNo()));
				// 设置柜员编号
				atmPlanInfo.setAtmAccount(StringUtils.trim(atmPlanInfo.getAtmAccount()));
				// 设置装机地址
				atmPlanInfo.setAtmAddress(StringUtils.trim(atmPlanInfo.getAtmAddress()));
				// 设置加钞金额
				atmPlanInfo.setAddAmount(new BigDecimal(StringUtils.trim(atmPlanInfo.getAddAmountStr())));
				// 设置钞箱数量
				atmPlanInfo.setGetBoxNum(Integer.parseInt(StringUtils.trim(atmPlanInfo.getGetBoxNumStr())));
				// 设置设备型号编号
				atmPlanInfo.setAtmTypeNo(StringUtils.trim(atmPlanInfo.getAtmTypeNo()));
				// 设置设备型号名称
				atmPlanInfo.setAtmTypeName(StringUtils.trim(atmPlanInfo.getAtmTypeName()));
				// 设置加钞计划文件标题头
				atmPlanInfo.setAddPlanName(addPlanName);
				// 设置加钞计划ID
				atmPlanInfo.setAddPlanId(addPlanId);
				atmPlanInfos.add(atmPlanInfo);
			}
			// 获取品牌型号信息
			List<AtmBrandsInfo> brandsList = AtmCommonUtils.getAtmTypesinfoList();
			for (AtmPlanInfo atmPlanInfo : atmPlanInfos) {
				// 回收箱数量
				int backBoxNum = 0;
				// 存款箱数量
				int depositBoxNum = 0;
				// 品牌名
				String atmBrandsName = "";
				// 获取品牌型号下回收箱与存款箱数量
				if (atmPlanInfo != null && StringUtils.isNotBlank(atmPlanInfo.getAtmTypeNo())) {
					for (AtmBrandsInfo atmBrandsInfo : brandsList) {
						if (atmBrandsInfo.getAtmTypeNo().equals(atmPlanInfo.getAtmTypeNo())) {
							backBoxNum = atmBrandsInfo.getBackBoxNumber();
							depositBoxNum = atmBrandsInfo.getDepositBoxNumber();
							atmBrandsName = atmBrandsInfo.getAtmBrandsName();
							break;
						}
					}
				}
				// 设置存款箱与回收箱数量
				atmPlanInfo.setBackBoxNum(backBoxNum);
				atmPlanInfo.setDepositBoxNum(depositBoxNum);
				// 设置总钞箱数量
				atmPlanInfo.setSumBoxNum(atmPlanInfo.getGetBoxNum() + backBoxNum + depositBoxNum);
				// 设置品牌名
				atmPlanInfo.setAtmBrandsName(atmBrandsName);
			}
		} catch (Exception e) {
			msgs.add("message.E4022");
			return false;
		}
		msgs.add("message.I4010");
		return true;
	}

	/**
	 * 获取机构下的所有加钞计划
	 * 
	 * @author XL
	 * @version 2017年11月07日
	 * @param map
	 * @return
	 */
	public List<Map<String, Object>> getPDAPlanList(Map<String, Object> map) {
		List<Map<String, Object>> atmPlanList = atmPlanInfoDao.findByMap(map);
		return atmPlanList;
	}

	/**
	 * 获取同步的加钞计划
	 * 
	 * @author XL
	 * @version 2017年11月07日
	 * @param addPlanId
	 * @return
	 */
	public List<Map<String, Object>> getPDAPlanDetail(String addPlanId) {
		AtmPlanInfo atmPlanInfo = new AtmPlanInfo();
		atmPlanInfo.setAddPlanId(addPlanId);
		/** 添加加钞计划查询状态为计划出库 author:wxz begin */
		atmPlanInfo.setStatus(ATMConstant.PlanStatus.PLAN_OUT);
		/** end */
		List<Map<String, Object>> atmPlanDetail = atmPlanInfoDao.findByAddPlanId(atmPlanInfo);
		return atmPlanDetail;
	}

	/**
	 * 更新加钞计划状态
	 * 
	 * @author XL
	 * @version 2017年11月07日
	 * @param atmPlanInfo
	 */
	@Transactional(readOnly = false)
	public void updateStatus(AtmPlanInfo atmPlanInfo) {
		atmPlanInfo.preUpdate();
		atmPlanInfoDao.updateStatus(atmPlanInfo);
	}

	/**
	 * 更新加钞计划状态
	 * 
	 * @author sg
	 * @version 2017年11月22日
	 * @param atmPlanInfo
	 */
	@Transactional(readOnly = false)
	public void updateStatuss(AtmPlanInfo atmPlanInfo) {
		atmPlanInfoDao.updateStatus(atmPlanInfo);
	}

	/**
	 * 验证自助设备是否存在加钞计划
	 * 
	 * @author XL
	 * @version 2017年11月07日
	 * @param atmPlanInfo
	 * @return
	 */
	public AtmPlanInfo validatePlanExist(AtmPlanInfo atmPlanInfo) {
		List<AtmPlanInfo> list = dao.validatePlanExist(atmPlanInfo);
		if (list.isEmpty()) {
			return null;
		}
		return list.get(0);
	}

	/**
	 * 统计当前计划下各型号下不同钞箱类型数量
	 * 
	 * @author XL
	 * @version 2017年11月07日
	 * @param addPlanId
	 * @return
	 */
	public List<Map<String, Object>> getATMtypenoBoxNum(String addPlanId) {
		return dao.getATMtypenoBoxNum(addPlanId);
	}

	/**
	 * 统计当前计划下各型号下不同钞箱类型数量
	 * 
	 * @author qph
	 * @version 2017年11月23日
	 * @param addPlanId
	 * @return
	 */
	public List<Map<String, Object>> getATMtypeByAddPlanId(String addPlanId) {
		return dao.getATMtypeByAddPlanId(addPlanId);
	}

	/**
	 * 根据查询条件，取得ATM机计划加钞表
	 * 
	 * @author XL
	 * @version 2017年11月07日
	 * @param page
	 * @param atmPlanInfo
	 * @return
	 */
	public Page<AtmPlanInfo> findAllList(Page<AtmPlanInfo> page, AtmPlanInfo atmPlanInfo) {
		// 查询条件：开始时间
		if (atmPlanInfo.getCreateTimeStart() != null) {
			atmPlanInfo
					.setSearchDateStart(DateUtils.formatDate(DateUtils.getDateStart(atmPlanInfo.getCreateTimeStart()),
							AllocationConstant.Dates.FORMATE_YYYY_MM_DD_HH_MM_SS));
		}
		// 查询条件：结束时间
		if (atmPlanInfo.getCreateTimeEnd() != null) {
			atmPlanInfo.setSearchDateEnd(DateUtils.formatDate(DateUtils.getDateEnd(atmPlanInfo.getCreateTimeEnd()),
					AllocationConstant.Dates.FORMATE_YYYY_MM_DD_HH_MM_SS));
		}
		// 执行查询处理
		List<AtmPlanInfo> atmClearList = atmPlanInfoDao.findAllList(atmPlanInfo);
		page.setList(atmClearList);
		return page;
	}

	/**
	 * 汇总当前计划各类型钞箱数量
	 * 
	 * @author XL
	 * @version 2017年11月07日
	 * @param addPlanId
	 * @return
	 */
	public List<Map<String, Object>> boxTypeCollect(String addPlanId) {
		AtmPlanInfo atmPlanInfo = new AtmPlanInfo();
		atmPlanInfo.setAddPlanId(addPlanId);
		return dao.boxTypeCollect(atmPlanInfo);
	}

	/**
	 * 验证加钞计划id
	 * 
	 * @author XL
	 * @version 2017年11月07日
	 * @param addPlanId
	 * @return
	 */
	public boolean validateAddPlanId(String addPlanId, Map<String, Object> map) {
		@SuppressWarnings("unchecked")
		List<String> msgs = (List<String>) map.get("msgs");
		AtmPlanInfo atmPlanInfo = new AtmPlanInfo();
		// 生成数据权限过滤条件（dsf为dataScopeFilter的简写，在xml中使用 ${sqlMap.dsf}调用权限SQL）
		//atmPlanInfo.getSqlMap().put("dsf", dataScopeFilter(UserUtils.getUser(), "o8", null));
		// 设置加钞计划id
		atmPlanInfo.setAddPlanId(addPlanId);
		List<AtmPlanInfo> atmPlanInfos = atmPlanInfoDao.findList(atmPlanInfo);
		// 存在加钞计划id
		if (!Collections3.isEmpty(atmPlanInfos)) {
			map.put("errorList", addPlanId);
			msgs.add("message.E4027");
			return false;
		}
		return true;
	}

	/**
	 * 验证导入文件ATM机信息
	 * 
	 * @author XL
	 * @version 2017年11月07日
	 * @param atmPlanInfos
	 */
	@SuppressWarnings("unchecked")
	public boolean validateAtmInfo(List<AtmPlanInfo> atmPlanInfos, Map<String, Object> map) {
		// 提示信息列表
		List<String> msgs = (List<String>) map.get("msgs");
		// 错误信息
		List<String> errorList = Lists.newArrayList();
		// ATM存在加钞计划中
		List<String> atmExist = Lists.newArrayList();
		// 柜员编号错误
		List<String> tellerIdError = Lists.newArrayList();
		// 加钞地址错误
		List<String> atmAddressError = Lists.newArrayList();
		// ATM型号编号错误
		List<String> atmTypeNoError = Lists.newArrayList();
		// ATM型号名称错误
		List<String> atmTypeNameError = Lists.newArrayList();
		// 加钞金额与钞箱个数不符
		List<String> amountNumError = Lists.newArrayList();
		// 取款箱个数不符
		List<String> getBoxNum = Lists.newArrayList();
		// 1,验证ATM编号是否正确
		for (AtmPlanInfo atmPlanInfo : atmPlanInfos) {
			AtmInfoMaintain atmInfoMaintain = new AtmInfoMaintain();
			// 设置ATM机编号
			atmInfoMaintain.setAtmId(atmPlanInfo.getAtmNo());
			// 生成数据权限过滤条件
			atmInfoMaintain.getSqlMap().put("dsf", dataScopeFilter(UserUtils.getUser(), "o5", ""));
			// ATM列表
			atmInfoMaintain = atmInfoMaintainDao.findByAtmId(atmInfoMaintain);
			// ATM不存在
			if (atmInfoMaintain == null) {
				errorList.add(atmPlanInfo.getAtmNo());
			}
		}
		if (!Collections3.isEmpty(errorList)) {
			msgs.add("message.E4030");
			map.put("errorList", errorList);
			return false;
		}
		// 2,验证导入文件ATM机编号是否重复
		List<String> atmNos = Lists.newArrayList();
		for (AtmPlanInfo atmPlanInfo : atmPlanInfos) {
			if (atmNos.contains(atmPlanInfo.getAtmNo())) {
				if (!errorList.contains(atmPlanInfo.getAtmNo())) {
					errorList.add(atmPlanInfo.getAtmNo());
				}
			} else {
				atmNos.add(atmPlanInfo.getAtmNo());
			}
		}
		if (!Collections3.isEmpty(errorList)) {
			msgs.add("message.E4029");
			map.put("errorList", errorList);
			return false;
		}
		// 3,验证ATM信息
		for (AtmPlanInfo atmPlanInfo : atmPlanInfos) {
			AtmPlanInfo atmPlanInfoSearch = new AtmPlanInfo();
			// 状态列表
			List<String> statuses = Lists.newArrayList();
			statuses.add(ATMConstant.PlanStatus.PLAN_CREATE);
			statuses.add(ATMConstant.PlanStatus.PLAN_SYN);
			statuses.add(ATMConstant.PlanStatus.PLAN_OUT);
			// 设置状态
			atmPlanInfoSearch.setStatuses(statuses);
			// 设置ATM机编号
			atmPlanInfoSearch.setAtmNo(atmPlanInfo.getAtmNo());
			// 加钞计划列表
			List<AtmPlanInfo> atmPlanInfosSearch = atmPlanInfoDao.findList(atmPlanInfoSearch);
			// 验证ATM机是在加钞计划中
			if (!Collections3.isEmpty(atmPlanInfosSearch)) {
				atmExist.add(atmPlanInfo.getAtmNo());
			}
			AtmInfoMaintain atmInfoMaintain = new AtmInfoMaintain();
			// 设置ATM机编号
			atmInfoMaintain.setAtmId(atmPlanInfo.getAtmNo());
			// ATM机
			atmInfoMaintain = atmInfoMaintainDao.findByAtmId(atmInfoMaintain);
			// 验证柜员号
			if (!atmPlanInfo.getAtmAccount().equals(atmInfoMaintain.getTellerId())) {
				tellerIdError.add(atmPlanInfo.getAtmAccount());
			}
			// 验证网点
			if (!atmPlanInfo.getAtmAddress().equals(atmInfoMaintain.getAofficeName())) {
				atmAddressError.add(atmPlanInfo.getAtmAddress());
			}
			// 验证设备型号编号
			if (!atmPlanInfo.getAtmTypeNo().equals(atmInfoMaintain.getAtmTypeNo())) {
				atmTypeNoError.add(atmPlanInfo.getAtmTypeNo());
			} else {
				// ATM品牌
				AtmBrandsInfo atmBrandsInfo = new AtmBrandsInfo();
				// 设置型号编号
				atmBrandsInfo.setAtmTypeNo(atmPlanInfo.getAtmTypeNo());
				// ATM品牌列表
				List<AtmBrandsInfo> atmBrandsInfos = atmBrandsInfoDao.findList(atmBrandsInfo);
				// 验证取款箱个数
				if (Collections3.isEmpty(atmBrandsInfos)
						|| atmPlanInfo.getGetBoxNum() > atmBrandsInfos.get(0).getGetBoxNumber()) {
					getBoxNum.add(atmPlanInfo.getAtmNo());
				}
			}
			// 验证设备型号
			if (!atmPlanInfo.getAtmTypeName().equals(atmInfoMaintain.getAtmTypeName())) {
				atmTypeNameError.add(atmPlanInfo.getAtmTypeName());
			}
			// 验证取款箱数量和加钞金额是否准确，验证规则：加钞金额>0，取款箱数量必须>=1;加钞金额=0，取款箱必须为0；
			if (atmPlanInfo.getAddAmount() != null && atmPlanInfo.getAddAmount().compareTo(BigDecimal.ZERO) > 0) {
				if (atmPlanInfo.getGetBoxNum() == null || atmPlanInfo.getGetBoxNum() == 0) {
					amountNumError.add(atmPlanInfo.getAtmNo());
				}
			} else {
				if (atmPlanInfo.getGetBoxNum() != null && atmPlanInfo.getGetBoxNum() > 0) {
					amountNumError.add(atmPlanInfo.getAtmNo());
				}
			}
		}
		// 验证结果
		if (!Collections3.isEmpty(atmExist)) {
			msgs.add("message.E4028");
			map.put("errorList", atmExist);
			return false;
		}
		if (!Collections3.isEmpty(tellerIdError)) {
			msgs.add("message.E4031");
			map.put("errorList", tellerIdError);
			return false;
		}
		if (!Collections3.isEmpty(atmAddressError)) {
			msgs.add("message.E4032");
			map.put("errorList", atmAddressError);
			return false;
		}
		if (!Collections3.isEmpty(atmTypeNoError)) {
			msgs.add("message.E4033");
			map.put("errorList", atmTypeNoError);
			return false;
		}
		if (!Collections3.isEmpty(atmTypeNameError)) {
			msgs.add("message.E4034");
			map.put("errorList", atmTypeNameError);
			return false;
		}
		if (!Collections3.isEmpty(getBoxNum)) {
			msgs.add("message.E4035");
			map.put("errorList", getBoxNum);
			return false;
		}
		if (!Collections3.isEmpty(amountNumError)) {
			msgs.add("message.E4019");
			map.put("errorList", amountNumError);
			return false;
		}
		return true;
	}

	/**
	 * 单独修改加钞计划的加钞组信息
	 * 
	 * @author wanglu
	 * @version 2017年11月16日
	 * @param AtmPlanInfo
	 * @return
	 */
	@Transactional(readOnly = false)
	public void bindPlanAddCashGroup(AtmPlanInfo atmPlanInfo) {
		atmPlanInfoDao.bindPlanAddCashGroup(atmPlanInfo);
	}
	
	/**
	 *获取当日问完成ATM机所属加钞计划信息 
	 * @author wanglu
	 * @version 2017年11月23日
	 * @param Map
	 * @return
	 */
	public List<AtmPlanInfo> getAtmPlanInfoByStatus(AtmPlanInfo atmPlanInfo){
		return atmPlanInfoDao.getAtmPlanInfoByStatus(atmPlanInfo);
	}
	
	/**
	 *根据加钞计划ID获取ATM机信息 
	 * @author wanglu
	 * @version 2017年11月23日
	 * @param Map
	 * @return
	 */
	public List<AtmPlanInfo> getAtmInfoByPlanId(AtmPlanInfo atmPlanInfo){
		return atmPlanInfoDao.getAtmInfoByPlanId(atmPlanInfo);
	}

	/**
	 * 获取ATM机信息
	 * 
	 * @author xl
	 * @version 2017年11月24日
	 * @return
	 */
	public List<AtmPlanInfo> getDataList() {
		List<AtmPlanInfo> dataList = Lists.newArrayList();
		AtmInfoMaintain atmInfoMaintain = new AtmInfoMaintain();
		// 生成数据权限过滤条件（dsf为dataScopeFilter的简写，在xml中使用 ${sqlMap.dsf}调用权限SQL）
		atmInfoMaintain.getSqlMap().put("dsf", SystemService.dataScopeFilter(UserUtils.getUser(), "o5", ""));
		// ATM机列表
		List<AtmInfoMaintain> atmInfoMaintainList = atmInfoMaintainDao.findList(atmInfoMaintain);
		// 序号
		int noImp = 1;
		for (AtmInfoMaintain atmInfo : atmInfoMaintainList) {
			AtmPlanInfo atmPlanInfo = new AtmPlanInfo();
			// 设置序号
			atmPlanInfo.setNoImp(String.valueOf(noImp++));
			// 设置ATM机编号
			atmPlanInfo.setAtmNo(atmInfo.getAtmId());
			// 设置柜员号
			atmPlanInfo.setAtmAccount(atmInfo.getTellerId());
			// 设置网点
			atmPlanInfo.setAtmAddress(atmInfo.getAofficeName());
			// 设置加钞金额
			atmPlanInfo.setAddAmountStr("");
			// 设置个数
			atmPlanInfo.setGetBoxNumStr("");
			// 设备型号编号
			atmPlanInfo.setAtmTypeNo(atmInfo.getAtmTypeNo());
			// 设备型号名称
			atmPlanInfo.setAtmTypeName(atmInfo.getAtmTypeName());
			dataList.add(atmPlanInfo);
		}
		// 该机构不存在ATM机
		if (Collections3.isEmpty(atmInfoMaintainList)) {
			dataList.add(new AtmPlanInfo());
		}
		return dataList;
	}

	/**
	 * 查询加钞计划
	 * 
	 * @author xl
	 * @version 2017年11月24日
	 * @return
	 */
	public AtmPlanInfo getView(String addPlanId) {
		Page<AtmPlanInfo> page = new Page<AtmPlanInfo>();
		AtmPlanInfo atmPlanInfo = new AtmPlanInfo();
		atmPlanInfo.setAddPlanId(addPlanId);
		page.setOrderBy("add_plan_id desc");
		// 生成数据权限过滤条件（dsf为dataScopeFilter的简写，在xml中使用 ${sqlMap.dsf}调用权限SQL）
		atmPlanInfo.getSqlMap().put("dsf", dataScopeFilter(UserUtils.getUser(), "o8", null));
		atmPlanInfo = atmPlanInfoDao.findAddPlanList(atmPlanInfo).get(0);
		return atmPlanInfo;
	}

	/**
	 * 统计当前计划下各型号钞箱类型数量
	 * 
	 * @author xl
	 * @version 2017年11月27日
	 * @param addPlanId
	 * @return
	 */
	public List<Map<String, Object>> getBoxNumbyAddPlanId(String addPlanId) {
		// 钞箱类型汇总
		List<Map<String, Object>> boxTypeCollect = this.getATMtypeByAddPlanId(addPlanId);
		List<Map<String, Object>> boxTypeList = Lists.newArrayList();
		for (Map<String, Object> boxTypeMap : boxTypeCollect) {
			Map<String, Object> typeMap = Maps.newHashMap();
			if (StringUtils.isNotBlank((String) boxTypeMap.get("boxType"))) {
				typeMap.put("type", boxTypeMap.get("type"));
				typeMap.put("boxNum", boxTypeMap.get("boxNum"));
				// 查询钞箱类型名
				AtmBoxMod atmBoxMod = new AtmBoxMod();
				atmBoxMod.setBoxTypeNo((String) boxTypeMap.get("boxType"));
				List<AtmBoxMod> atmBoxModList = atmBoxModDao.findList(atmBoxMod);
				if (!Collections3.isEmpty(atmBoxModList)) {
					typeMap.put("boxTypeName", atmBoxModList.get(0).getModName());
				} else {
					typeMap.put("boxTypeName", boxTypeMap.get("boxType"));
				}
				boxTypeList.add(typeMap);
			}
		}
		return boxTypeList;
	}
	
	/**
	 * 根据ATM机编号获取ATM机信息
	 * 
	 * @param atmNos ATM机编号
	 * @author wxz
	 * @version 2017年12月6日
	 * @return
	 */
	public List<AtmPlanInfo> getDataList(String atmNos) {
		
		String[] atmNoArray = atmNos.split(Constant.Punctuation.COMMA);
		List<String> atmNosList = Arrays.asList(atmNoArray);
		
		List<AtmPlanInfo> dataList = Lists.newArrayList();
		AtmInfoMaintain atmInfoMaintain = new AtmInfoMaintain();
		// 生成数据权限过滤条件（dsf为dataScopeFilter的简写，在xml中使用 ${sqlMap.dsf}调用权限SQL）
		atmInfoMaintain.getSqlMap().put("dsf", SystemService.dataScopeFilter(UserUtils.getUser(), "o5", ""));
		// 序号
		int noImp = 1;
		for(String atmNo : atmNosList){
			atmInfoMaintain.setAtmId(atmNo);
		
			// ATM机
			AtmInfoMaintain atmInfo = atmInfoMaintainDao.findByAtmId(atmInfoMaintain);
			if (atmInfo != null) {
				AtmPlanInfo atmPlanInfo = new AtmPlanInfo();
				// 设置序号
				atmPlanInfo.setNoImp(String.valueOf(noImp++));
				// 设置ATM机编号
				atmPlanInfo.setAtmNo(atmInfo.getAtmId());
				// 设置柜员号
				atmPlanInfo.setAtmAccount(atmInfo.getTellerId());
				// 设置网点
				atmPlanInfo.setAtmAddress(atmInfo.getAofficeName());
				// 设置加钞金额
				atmPlanInfo.setAddAmountStr("");
				// 设置个数
				atmPlanInfo.setGetBoxNumStr("");
				// 设备型号编号
				atmPlanInfo.setAtmTypeNo(atmInfo.getAtmTypeNo());
				// 设备型号名称
				atmPlanInfo.setAtmTypeName(atmInfo.getAtmTypeName());
				dataList.add(atmPlanInfo);
			}
			// 该机构不存在ATM机
			// if (Collections3.isEmpty(atmInfoMaintainList)) {
			// dataList.add(new AtmPlanInfo());
			// }
		}
		return dataList;
	}
	
	/**
	 * 手动生成加钞计划
	 * 
	 * @author wxz
	 * @version 2017年12月07日
	 * @param AtmPlanInfo
	 * @param validator
	 * @return
	 */
	@Transactional(readOnly = false)
	public Map<String, Object> manualAtmPlanInfo(AtmPlanInfo atmPlanInfo, Validator validator) {
		// 返回内容
		Map<String, Object> map = Maps.newHashMap();
		// 提示消息
		List<String> msgs = Lists.newArrayList();
		map.put("msgs", msgs);
		// 加钞计划id
		String addPlanId = IdGen.getIdByTime();
		// 标题
		GregorianCalendar ca = new GregorianCalendar();
		int AM_PM = ca.get(GregorianCalendar.AM_PM);
		String upDown = "";
		if (AM_PM == 1) {
			upDown = "下午";
		} else if (AM_PM == 0) {
			upDown = "上午";
		}
		String titleName = DateUtils.formatDate(new Date()) + "人民币现金收付管理系统加钞计划单（" + upDown + "）";
		// 获取手动生成计划列表
		List<AtmPlanInfo> atmPlanInfos = atmPlanInfo.getAddPlanList();
		// 格式化手动生成计划列表
		Iterator<AtmPlanInfo> it = atmPlanInfos.iterator();
		while(it.hasNext()){
			if(StringUtils.isBlank(it.next().getAddAmountStr())){
				it.remove();
			}
		}
		
		// ATM机添加加钞计划id
		for(AtmPlanInfo atm : atmPlanInfos){
			atm.setAddPlanId(addPlanId);
			atm.setAddAmount(new BigDecimal(StringUtils.trim(atm.getAddAmountStr())));
			atm.setGetBoxNum(Integer.parseInt(StringUtils.trim(atm.getGetBoxNumStr())));
			atm.setAddPlanName(titleName);
			atm.setPlanId(null);
		}
		
		// 获取品牌型号信息
		List<AtmBrandsInfo> brandsList = AtmCommonUtils.getAtmTypesinfoList();
		for (AtmPlanInfo atmPlan : atmPlanInfos) {
			// 回收箱数量
			int backBoxNum = 0;
			// 存款箱数量
			int depositBoxNum = 0;
			// 品牌名
			String atmBrandsName = "";
			// 获取品牌型号下回收箱与存款箱数量
			if (atmPlan.getAtmTypeNo() != null && StringUtils.isNotBlank(atmPlan.getAtmTypeNo())) {
				for (AtmBrandsInfo atmBrandsInfo : brandsList) {
					if (atmBrandsInfo.getAtmTypeNo().equals(atmPlan.getAtmTypeNo())) {
						backBoxNum = atmBrandsInfo.getBackBoxNumber();
						depositBoxNum = atmBrandsInfo.getDepositBoxNumber();
						atmBrandsName = atmBrandsInfo.getAtmBrandsName();
						break;
					}
				}
			}
			// 设置存款箱与回收箱数量
			atmPlan.setBackBoxNum(backBoxNum);
			atmPlan.setDepositBoxNum(depositBoxNum);
			// 设置总钞箱数量 修改人：xl 修改时间：2017-12-08 begin
			atmPlan.setSumBoxNum(atmPlan.getGetBoxNum() + backBoxNum + depositBoxNum);
			// end
			// 设置品牌名
			atmPlan.setAtmBrandsName(atmBrandsName);
		}

		// 1.验证加钞计划ID
		addPlanId = atmPlanInfos.get(0).getAddPlanId();
		if (!validateAddPlanId(addPlanId, map)) {
			return map;
		}

		// 2.验证导入文件ATM机信息
		if (!validateAtmInfo(atmPlanInfos, map)) {
			return map;
		}

		// 3.将关键信息存入加钞计划表
		if (!save(atmPlanInfos, validator, map)) {
			return map;
		}

		// 4.导入成功，显示
		map.put("addPlanId", addPlanId);
		return map;
	}
	
	/**
	 * 保存修改后的加钞计划(仅修改保存使用)
	 * 
	 * @author wxz
	 * @version 2017年12月13日
	 * @param request
	 * @param response
	 * @param model
	 * @return 保存加钞计划列表页面
	 */
	@Transactional(readOnly = false)
	public Map<String, Object> editSaveAtmPlan(AtmPlanInfo atmPlanInfo, Validator validator) {
		// 返回内容
		Map<String, Object> map = Maps.newHashMap();
		// 提示消息
		List<String> msgs = Lists.newArrayList();
		map.put("msgs", msgs);
		AtmPlanInfo atmPlanMain = new AtmPlanInfo();
		// 加钞计划id
		String addPlanId = "";
		// 加钞计划name
		String addPlanName = "";
		// 获取手动生成计划列表
		List<AtmPlanInfo> planList = atmPlanInfo.getAddPlanList();
		// 格式化手动生成计划列表
		Iterator<AtmPlanInfo> it = planList.iterator();
		while(it.hasNext()){
			if(StringUtils.isBlank(it.next().getAddAmountStr())){
				it.remove();
			}
		}
		// 修改后新增的计划列表
		List<AtmPlanInfo> newPlanList = Lists.newArrayList();
		// 修改后原有的计划列表
		List<AtmPlanInfo> oldPlanList = Lists.newArrayList();
		for(AtmPlanInfo atm : planList){
			// 判断修改后保存的加钞计划是否有新增信息
			if(StringUtils.isNotBlank(atm.getPlanId())){
				atmPlanMain.setAddPlanId(atm.getAddPlanId());
				atmPlanMain.setPlanId(atm.getPlanId());
				AtmPlanInfo planInfo = atmPlanInfoDao.get(atmPlanMain);
				addPlanId = planInfo.getAddPlanId();
				addPlanName = planInfo.getAddPlanName();
				planInfo.setAddAmount(new BigDecimal(StringUtils.trim(atm.getAddAmountStr())));
				planInfo.setGetBoxNum(Integer.parseInt(StringUtils.trim(atm.getGetBoxNumStr())));
				planInfo.setUpdateBy(UserUtils.getUser());
				planInfo.setUpdateDate(new Date());
				planInfo.setUpdateName(UserUtils.getUser().getName());
				planInfo.setDelFlag(atm.getDelFlag());
				
				oldPlanList.add(planInfo);
				// 验证导入文件ATM机信息
				if (!validateAtmBox(oldPlanList, map)) {
					return map;
				} else {
					// 更新修改后的加钞计划
					atmPlanInfoDao.update(planInfo);
				}
			} else { // ATM机添加加钞计划id
				atm.setAddPlanId(addPlanId);
				atm.setAddAmount(new BigDecimal(StringUtils.trim(atm.getAddAmountStr())));
				atm.setGetBoxNum(Integer.parseInt(StringUtils.trim(atm.getGetBoxNumStr())));
				atm.setAddPlanName(addPlanName);
				atm.setPlanId(null);
				newPlanList.add(atm);
			}
		}
		// 判断修改后保存的加钞计划是否有新增信息
		if(!Collections3.isEmpty(newPlanList)){
			// 获取品牌型号信息
			List<AtmBrandsInfo> brandsList = AtmCommonUtils.getAtmTypesinfoList();
			for (AtmPlanInfo atmPlan : newPlanList) {
				if(StringUtils.isBlank(atmPlan.getPlanId())){
					// 回收箱数量
					int backBoxNum = 0;
					// 存款箱数量
					int depositBoxNum = 0;
					// 品牌名
					String atmBrandsName = "";
					// 获取品牌型号下回收箱与存款箱数量
					if (atmPlan.getAtmTypeNo() != null && StringUtils.isNotBlank(atmPlan.getAtmTypeNo())) {
						for (AtmBrandsInfo atmBrandsInfo : brandsList) {
							if (atmBrandsInfo.getAtmTypeNo().equals(atmPlan.getAtmTypeNo())) {
								backBoxNum = atmBrandsInfo.getBackBoxNumber();
								depositBoxNum = atmBrandsInfo.getDepositBoxNumber();
								atmBrandsName = atmBrandsInfo.getAtmBrandsName();
								break;
							}
						}
					}
					// 设置存款箱与回收箱数量
					atmPlan.setBackBoxNum(backBoxNum);
					atmPlan.setDepositBoxNum(depositBoxNum);
					// 设置总钞箱数量
					atmPlan.setSumBoxNum(atmPlan.getGetBoxNum() + backBoxNum + depositBoxNum);
					// end
					// 设置品牌名
					atmPlan.setAtmBrandsName(atmBrandsName);
				}
			}
			// 1.验证导入文件ATM机信息
			if (!validateAtmInfo(newPlanList, map)) {
				return map;
			}
			// 2.将关键信息存入加钞计划表
			if (!save(newPlanList, validator, map)) {
				return map;
			}
		} else {
			// 添加保存成功提示信息
			@SuppressWarnings("unchecked")
			List<String> mesgs = (List<String>) map.get("msgs");
			mesgs.add("message.I4011");
		}

		// 3.导入成功，显示
		map.put("addPlanId", addPlanId);
		return map;
	}
	
	/**
	 * 验证ATM机信息(修改保存后的验证)
	 * 
	 * @author wxz
	 * @version 2017年12月21日
	 * @param atmPlanInfos
	 */
	public boolean validateAtmBox(List<AtmPlanInfo> atmPlanInfos, Map<String, Object> map) {
		// 提示信息列表
		@SuppressWarnings("unchecked")
		List<String> msgs = (List<String>) map.get("msgs");
		// 错误信息
		List<String> errorList = Lists.newArrayList();
		// 柜员编号错误
		List<String> tellerIdError = Lists.newArrayList();
		// 加钞地址错误
		List<String> atmAddressError = Lists.newArrayList();
		// ATM型号编号错误
		List<String> atmTypeNoError = Lists.newArrayList();
		// ATM型号名称错误
		List<String> atmTypeNameError = Lists.newArrayList();
		// 加钞金额与钞箱个数不符
		List<String> amountNumError = Lists.newArrayList();
		// 取款箱个数不符
		List<String> getBoxNum = Lists.newArrayList();
		// 1,验证ATM编号是否正确
		for (AtmPlanInfo atmPlanInfo : atmPlanInfos) {
			AtmInfoMaintain atmInfoMaintain = new AtmInfoMaintain();
			// 设置ATM机编号
			atmInfoMaintain.setAtmId(atmPlanInfo.getAtmNo());
			// ATM列表
			atmInfoMaintain = atmInfoMaintainDao.findByAtmId(atmInfoMaintain);
			// ATM不存在
			if (atmInfoMaintain == null) {
				errorList.add(atmPlanInfo.getAtmNo());
			}
		}
		if (!Collections3.isEmpty(errorList)) {
			msgs.add("message.E4030");
			map.put("errorList", errorList);
			return false;
		}
		if (!Collections3.isEmpty(errorList)) {
			msgs.add("message.E4029");
			map.put("errorList", errorList);
			return false;
		}
		// 3,验证ATM信息
		for (AtmPlanInfo atmPlanInfo : atmPlanInfos) {
			AtmPlanInfo atmPlanInfoSearch = new AtmPlanInfo();
			// 状态列表
			List<String> statuses = Lists.newArrayList();
			statuses.add(ATMConstant.PlanStatus.PLAN_CREATE);
			statuses.add(ATMConstant.PlanStatus.PLAN_SYN);
			statuses.add(ATMConstant.PlanStatus.PLAN_OUT);
			// 设置状态
			atmPlanInfoSearch.setStatuses(statuses);
			// 设置ATM机编号
			atmPlanInfoSearch.setAtmNo(atmPlanInfo.getAtmNo());
			AtmInfoMaintain atmInfoMaintain = new AtmInfoMaintain();
			// 设置ATM机编号
			atmInfoMaintain.setAtmId(atmPlanInfo.getAtmNo());
			// ATM机
			atmInfoMaintain = atmInfoMaintainDao.findByAtmId(atmInfoMaintain);
			// 验证柜员号
			if (!atmPlanInfo.getAtmAccount().equals(atmInfoMaintain.getTellerId())) {
				tellerIdError.add(atmPlanInfo.getAtmAccount());
			}
			// 验证网点
			if (!atmPlanInfo.getAtmAddress().equals(atmInfoMaintain.getAofficeName())) {
				atmAddressError.add(atmPlanInfo.getAtmAddress());
			}
			// 验证设备型号编号
			if (!atmPlanInfo.getAtmTypeNo().equals(atmInfoMaintain.getAtmTypeNo())) {
				atmTypeNoError.add(atmPlanInfo.getAtmTypeNo());
			} else {
				// ATM品牌
				AtmBrandsInfo atmBrandsInfo = new AtmBrandsInfo();
				// 设置型号编号
				atmBrandsInfo.setAtmTypeNo(atmPlanInfo.getAtmTypeNo());
				// ATM品牌列表
				List<AtmBrandsInfo> atmBrandsInfos = atmBrandsInfoDao.findList(atmBrandsInfo);
				// 验证取款箱个数
				if (Collections3.isEmpty(atmBrandsInfos)
						|| atmPlanInfo.getGetBoxNum() > atmBrandsInfos.get(0).getGetBoxNumber()) {
					getBoxNum.add(atmPlanInfo.getAtmNo());
				}
			}
			// 验证设备型号
			if (!atmPlanInfo.getAtmTypeName().equals(atmInfoMaintain.getAtmTypeName())) {
				atmTypeNameError.add(atmPlanInfo.getAtmTypeName());
			}
			// 验证取款箱数量和加钞金额是否准确，验证规则：加钞金额>0，取款箱数量必须>=1;加钞金额=0，取款箱必须为0；
			if (atmPlanInfo.getAddAmount() != null && atmPlanInfo.getAddAmount().compareTo(BigDecimal.ZERO) > 0) {
				if (atmPlanInfo.getGetBoxNum() == null || atmPlanInfo.getGetBoxNum() == 0) {
					amountNumError.add(atmPlanInfo.getAtmNo());
				}
			} else {
				if (atmPlanInfo.getGetBoxNum() != null && atmPlanInfo.getGetBoxNum() > 0) {
					amountNumError.add(atmPlanInfo.getAtmNo());
				}
			}
		}
		// 验证结果
		if (!Collections3.isEmpty(tellerIdError)) {
			msgs.add("message.E4031");
			map.put("errorList", tellerIdError);
			return false;
		}
		if (!Collections3.isEmpty(atmAddressError)) {
			msgs.add("message.E4032");
			map.put("errorList", atmAddressError);
			return false;
		}
		if (!Collections3.isEmpty(atmTypeNoError)) {
			msgs.add("message.E4033");
			map.put("errorList", atmTypeNoError);
			return false;
		}
		if (!Collections3.isEmpty(atmTypeNameError)) {
			msgs.add("message.E4034");
			map.put("errorList", atmTypeNameError);
			return false;
		}
		if (!Collections3.isEmpty(getBoxNum)) {
			msgs.add("message.E4035");
			map.put("errorList", getBoxNum);
			return false;
		}
		if (!Collections3.isEmpty(amountNumError)) {
			msgs.add("message.E4019");
			map.put("errorList", amountNumError);
			return false;
		}
		return true;
	}

}