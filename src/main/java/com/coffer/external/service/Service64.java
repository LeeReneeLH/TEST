package com.coffer.external.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.coffer.businesses.modules.store.StoreConstant.IsStamperUpload;
import com.coffer.businesses.modules.store.StoreConstant.StamperOperation;
import com.coffer.businesses.modules.store.StoreConstant.StamperType;
import com.coffer.businesses.modules.store.v02.entity.StoOfficeStamperInfo;
import com.coffer.businesses.modules.store.v02.service.StoOfficeStamperInfoService;
import com.coffer.core.common.utils.Encodes;
import com.coffer.core.common.utils.IdGen;
import com.coffer.core.common.utils.StringUtils;
import com.coffer.core.modules.sys.entity.Dict;
import com.coffer.core.modules.sys.entity.Office;
import com.coffer.core.modules.sys.entity.User;
import com.coffer.core.modules.sys.service.OfficeService;
import com.coffer.core.modules.sys.service.SystemService;
import com.coffer.core.modules.sys.utils.DictUtils;
import com.coffer.external.ExternalConstant;
import com.coffer.external.Parameter;
import com.coffer.external.hessian.HardwardBaseService;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

/**
 * Title: Service64
 * <p>
 * Description: 印章操作接口
 * </p>
 * 
 * @author wangbaozhong
 * @date 2017年7月6日 上午10:41:10
 */
@Component("Service64")
@Scope("singleton")
public class Service64 extends HardwardBaseService {

	@Autowired
	private OfficeService officeService;
	@Autowired
	private SystemService systemService;
	@Autowired
	private StoOfficeStamperInfoService stoOfficeStamperInfoService;

	/**
	 * 64：印章操作接口
	 * 
	 * @author Zhengkaiyuan
	 * @version 2016年9月19日
	 *
	 *
	 * @param paramMap
	 * @return
	 */
	@Override
	public String execute(Map<String, Object> paramMap) {

		Map<String, Object> respMap = Maps.newHashMap();
		try {
			// 版本号、服务代码
			respMap.put(Parameter.VERSION_NO_KEY, paramMap.get(Parameter.VERSION_NO_KEY));
			respMap.put(Parameter.SERVICE_NO_KEY, paramMap.get(Parameter.SERVICE_NO_KEY));
			// 获取印章操作接口参数
			logger.warn("64：印章操作接口--------获取接口参数信息开始");
			StoOfficeStamperInfo officeStamperInfo = getOfficeStamperInfoParam(paramMap);
			logger.warn("64：印章操作接口--------获取接口参数信息结束");
			// 验证错误信息
			if (!StringUtils.isBlank(officeStamperInfo.getErrorMsg())) {
				respMap.put(Parameter.RESULT_FLAG_KEY, ExternalConstant.HardwareInterface.RESULT_FLAG_FAILURE);
				respMap.put(Parameter.ERROR_NO_KEY, officeStamperInfo.getErrorMsg());
				respMap.put(Parameter.STAMPER_LIST_KEY, officeStamperInfo.getStamperList());
			} else {
				List<Map<String, Object>> stoOfficeStamperInfoList = null;
				// 根据操作类型进行处理
				if (StamperOperation.STAMPER_ADD.equals(officeStamperInfo.getType())) {
					addOfficeStamper(officeStamperInfo);
				} else if (StamperOperation.STAMPER_DELETE.equals(officeStamperInfo.getType())) {
					deleteOfficeStamper(officeStamperInfo);
				} else if (StamperOperation.STAMPER_QUERY.equals(officeStamperInfo.getType())) {
					stoOfficeStamperInfoList = queryOfficeStamper(officeStamperInfo);
				}
				// 处理成功添加返回值
				respMap.put(Parameter.RESULT_FLAG_KEY, ExternalConstant.HardwareInterface.RESULT_FLAG_SUCCESS);
				respMap.put(Parameter.STAMPER_LIST_KEY, stoOfficeStamperInfoList);
			}
		} catch (Exception e) {
			respMap.put(Parameter.RESULT_FLAG_KEY, ExternalConstant.HardwareInterface.RESULT_FLAG_FAILURE);
			respMap.put(Parameter.ERROR_NO_KEY, ExternalConstant.HardwareInterface.ERROR_NO_E02);
		}
		return gson.toJson(respMap);
	}

	/**
	 * 添加印章
	 * 
	 * @author Zhengkaiyuan
	 * @version 2016年9月19日
	 *
	 *
	 * @param stoOfficeStamperInfo
	 *            添加Entity
	 */
	@Transactional(readOnly = false)
	public void addOfficeStamper(StoOfficeStamperInfo stoOfficeStamperInfo) {
		for (StoOfficeStamperInfo stoOfficeStamperInfoTemp : stoOfficeStamperInfo.getStamperList()) {
			StoOfficeStamperInfo addStoOfficeStamperInfo = this.queryOfficeStamperByOfficeAndType(
					stoOfficeStamperInfo.getOffice(), stoOfficeStamperInfoTemp.getOfficeStamperType());
			// 判断是否已存在，不存在-插入，存在-更新
			if (addStoOfficeStamperInfo == null) {
				stoOfficeStamperInfoTemp.setIsNewRecord(true);
				stoOfficeStamperInfoTemp.setId(IdGen.uuid());
				stoOfficeStamperInfoTemp.setCreateBy(stoOfficeStamperInfo.getUser());
				stoOfficeStamperInfoTemp.setCreateName(stoOfficeStamperInfo.getUser().getName());
			} else {
				stoOfficeStamperInfoTemp.setIsNewRecord(false);
				stoOfficeStamperInfoTemp.setId(addStoOfficeStamperInfo.getId());
				stoOfficeStamperInfoTemp.setUpdateBy(stoOfficeStamperInfo.getUser());
				stoOfficeStamperInfoTemp.setUpdateName(stoOfficeStamperInfo.getUser().getName());
			}
			// 设置机构
			stoOfficeStamperInfoTemp.setOffice(stoOfficeStamperInfo.getOffice());
			// 插入
			stoOfficeStamperInfoService.save(stoOfficeStamperInfoTemp);
		}

	}

	/**
	 * 删除印章
	 * 
	 * @author Zhengkaiyuan
	 * @version 2016年9月19日
	 *
	 *
	 * @param stoOfficeStamperInfo
	 *            删除条件Entity
	 */
	@Transactional(readOnly = false)
	public void deleteOfficeStamper(StoOfficeStamperInfo stoOfficeStamperInfo) {
		for (StoOfficeStamperInfo stoOfficeStamperInfoTemp : stoOfficeStamperInfo.getStamperList()) {
			stoOfficeStamperInfoTemp.setUpdateBy(stoOfficeStamperInfo.getUser());
			stoOfficeStamperInfoTemp.setUpdateName(stoOfficeStamperInfo.getUser().getName());
			stoOfficeStamperInfoTemp.setOffice(stoOfficeStamperInfo.getOffice());
			stoOfficeStamperInfoService.delete(stoOfficeStamperInfoTemp);
		}
	}

	/**
	 * 查询印章
	 * 
	 * @author Zhengkaiyuan
	 * @version 2016年9月19日
	 *
	 *
	 * @param stoOfficeStamperInfo
	 *            查询条件Entity
	 * @return 返回类型 List<Map<String, Object>>
	 */
	@Transactional(readOnly = true)
	public List<Map<String, Object>> queryOfficeStamper(StoOfficeStamperInfo stoOfficeStamperInfo) {
		List<Map<String, Object>> resList = Lists.newArrayList();
		// 查询
		List<StoOfficeStamperInfo> stoOfficeStamperInfoList = null;
		if (stoOfficeStamperInfo.getStamperList() == null || stoOfficeStamperInfo.getStamperList().isEmpty()) {
			stoOfficeStamperInfoList = stoOfficeStamperInfoService.findList(stoOfficeStamperInfo);
		} else {
			stoOfficeStamperInfoList = Lists.newArrayList();
			for (StoOfficeStamperInfo findStoOfficeStamperInfoTemp : stoOfficeStamperInfo.getStamperList()) {
				StoOfficeStamperInfo queryStoOfficeStamperInfo = this.queryOfficeStamperByOfficeAndType(
						stoOfficeStamperInfo.getOffice(), findStoOfficeStamperInfoTemp.getOfficeStamperType());
				if (queryStoOfficeStamperInfo != null) {
					stoOfficeStamperInfoList.add(queryStoOfficeStamperInfo);
				}
			}
		}
		// 封装返回数据
		for (StoOfficeStamperInfo stoOfficeStamperInfoTemp : stoOfficeStamperInfoList) {
			Map<String, Object> resMap = Maps.newHashMap();
			resMap.put(Parameter.STAMPER_NAME_KEY,
					stoOfficeStamperInfoTemp.getStamperName() != null
							&& !StringUtils.isBlank(stoOfficeStamperInfoTemp.getStamperName())
									? stoOfficeStamperInfoTemp.getStamperName()
									: "");
			resMap.put(Parameter.STAMPER_TYPE_KEY, stoOfficeStamperInfoTemp.getOfficeStamperType());

			resMap.put(Parameter.STAMPER_HEIGHT_KEY,
					stoOfficeStamperInfoTemp.getStamperHeight() != null
							? stoOfficeStamperInfoTemp.getStamperHeight().toString()
							: "");

			resMap.put(Parameter.STAMPER_WIDTH_KEY,
					stoOfficeStamperInfoTemp.getStamperWidth() != null
							? stoOfficeStamperInfoTemp.getStamperWidth().toString()
							: "");
			if (stoOfficeStamperInfoTemp.getOfficeStamper() != null
					&& stoOfficeStamperInfoTemp.getOfficeStamper().length != 0) {
				String stamperDataString = Encodes.encodeBase64(stoOfficeStamperInfoTemp.getOfficeStamper());
				resMap.put(Parameter.STAMPER_DATA_KEY, stamperDataString);
			}
			resList.add(resMap);
		}
		return resList;
	}

	/**
	 * 根据机构和类型查询印章
	 * 
	 * @author Zhengkaiyuan
	 * @version 2016年9月19日
	 *
	 *
	 * @param office
	 *            机构
	 * @param stamperType
	 *            印章类型
	 * @return 返回类型 StoOfficeStamperInfo
	 */
	@Transactional(readOnly = true)
	public StoOfficeStamperInfo queryOfficeStamperByOfficeAndType(Office office, String stamperType) {
		StoOfficeStamperInfo stoOfficeStamperInfo = new StoOfficeStamperInfo();
		stoOfficeStamperInfo.setOffice(office);
		stoOfficeStamperInfo.setOfficeStamperType(stamperType);
		StoOfficeStamperInfo resStoOfficeStamperInfo = stoOfficeStamperInfoService.get(stoOfficeStamperInfo);
		return resStoOfficeStamperInfo;
	}

	/**
	 * 查询印章类型是否存在
	 * 
	 * @author Zhengkaiyuan
	 * @version 2016年9月19日
	 *
	 *
	 * @param stamperType
	 *            印章类型
	 * @return 返回类型 boolean
	 */
	@Transactional(readOnly = true)
	public boolean isExistStamperType(String stamperType) {
		List<Dict> stamperTypeList = Lists.newArrayList();
		stamperTypeList.addAll(DictUtils.getDictList(StamperType.OFFICE_STAMPER_TYPE));
		stamperTypeList.addAll(DictUtils.getDictList(StamperType.PBOC_OFFICE_STAMPER_TYPE));
		for (Dict dictTemp : stamperTypeList) {
			if (dictTemp.getValue().equals(stamperType)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 取得印章操作接口参数
	 * 
	 * @author Zhengkaiyuan
	 * @version 2016年9月19日
	 *
	 *
	 * @param requestMap
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	private StoOfficeStamperInfo getOfficeStamperInfoParam(Map<String, Object> requestMap) throws Exception {
		StoOfficeStamperInfo officeStamperInfo = new StoOfficeStamperInfo();

		// 验证机构ID
		Object officeId = requestMap.get(Parameter.OFFICE_ID_KEY);
		// 验证机构ID
		if (officeId == null || StringUtils.isBlank(officeId.toString())) {
			logger.warn("64：印章操作接口--------参数机构ID为空");
			officeStamperInfo.setErrorMsg(ExternalConstant.HardwareInterface.ERROR_NO_E03);
			return officeStamperInfo;
		}
		Office office = officeService.get(officeId.toString());
		if (office == null) {
			logger.warn("64：印章操作接口--------参数机构ID对应机构不存在， 参数机构ID：" + officeId.toString());
			officeStamperInfo.setErrorMsg(ExternalConstant.HardwareInterface.ERROR_NO_E03);
			return officeStamperInfo;
		}
		officeStamperInfo.setOffice(office);
		// 验证操作类型
		Object type = requestMap.get(Parameter.STAMPER_OPERATION_KEY);
		if (type == null || StringUtils.isBlank(type.toString())) {
			logger.warn("64：印章操作接口--------参数操作类型为空");
			officeStamperInfo.setErrorMsg(ExternalConstant.HardwareInterface.ERROR_NO_E03);
			return officeStamperInfo;
		}
		officeStamperInfo.setType(type.toString());
		// 验证印章列表
		Object stamperList = requestMap.get(Parameter.STAMPER_LIST_KEY);
		// 查询操作验证
		if (StamperOperation.STAMPER_QUERY.equals(type.toString())) {
			List<StoOfficeStamperInfo> queryOfficeStamperInfoList = Lists.newArrayList();
			if (stamperList != null && !StringUtils.isBlank(stamperList.toString())) {
				List<Map<String, Object>> queryOfficeStamperInfoMapList = (List<Map<String, Object>>) stamperList;
				for (Map<String, Object> officeStamperInfoMap : queryOfficeStamperInfoMapList) {
					if (officeStamperInfoMap.get(Parameter.STAMPER_TYPE_KEY) == null
							|| StringUtils.isBlank((String) officeStamperInfoMap.get(Parameter.STAMPER_TYPE_KEY))) {
						logger.warn("64：印章操作接口--------参数印章类型为空");
						officeStamperInfo.setErrorMsg(ExternalConstant.HardwareInterface.ERROR_NO_E03);
						return officeStamperInfo;
					}
					StoOfficeStamperInfo stoOfficeStamperInfoTemp = new StoOfficeStamperInfo();
					stoOfficeStamperInfoTemp
							.setOfficeStamperType((String) officeStamperInfoMap.get(Parameter.STAMPER_TYPE_KEY));
					queryOfficeStamperInfoList.add(stoOfficeStamperInfoTemp);
				}
				if (!queryOfficeStamperInfoList.isEmpty()) {
					officeStamperInfo.setStamperList(queryOfficeStamperInfoList);
				}
			}
			return officeStamperInfo;
		}
		if (stamperList == null || StringUtils.isBlank(stamperList.toString())) {
			logger.warn("64：印章操作接口--------参数印章列表为空");
			officeStamperInfo.setErrorMsg(ExternalConstant.HardwareInterface.ERROR_NO_E03);
			return officeStamperInfo;
		}
		// 获取要操作的数据
		List<StoOfficeStamperInfo> officeStamperInfoList = Lists.newArrayList();
		List<Map<String, Object>> officeStamperInfoMapList = (List<Map<String, Object>>) stamperList;
		for (Map<String, Object> officeStamperInfoMap : officeStamperInfoMapList) {
			if (officeStamperInfoMap.get(Parameter.STAMPER_TYPE_KEY) == null
					|| StringUtils.isBlank((String) officeStamperInfoMap.get(Parameter.STAMPER_TYPE_KEY))) {
				logger.warn("64：印章操作接口--------参数印章类型为空");
				officeStamperInfo.setErrorMsg(ExternalConstant.HardwareInterface.ERROR_NO_E03);
				return officeStamperInfo;
			}
			StoOfficeStamperInfo stoOfficeStamperInfoTemp = new StoOfficeStamperInfo();
			stoOfficeStamperInfoTemp
					.setOfficeStamperType((String) officeStamperInfoMap.get(Parameter.STAMPER_TYPE_KEY));
			if (officeStamperInfoMap.get(Parameter.STAMPER_DATA_KEY) == null
					|| StringUtils.isBlank(officeStamperInfoMap.get(Parameter.STAMPER_DATA_KEY).toString())) {
				stoOfficeStamperInfoTemp.setIsStamperUpload(IsStamperUpload.STAMPER_NO_UPLOAD);
			} else {
				// 印章数据解密
				byte[] stamperDataBytes = Encodes
						.decodeBase64((String) officeStamperInfoMap.get(Parameter.STAMPER_DATA_KEY));
				stoOfficeStamperInfoTemp.setOfficeStamper(stamperDataBytes);
				stoOfficeStamperInfoTemp.setIsStamperUpload(IsStamperUpload.STAMPER_UPLOAD);
			}
			stoOfficeStamperInfoTemp.setStamperName((String) officeStamperInfoMap.get(Parameter.STAMPER_NAME_KEY));
			if (officeStamperInfoMap.get(Parameter.STAMPER_HEIGHT_KEY) != null
					&& StringUtils.isNumber(officeStamperInfoMap.get(Parameter.STAMPER_HEIGHT_KEY).toString())) {
				stoOfficeStamperInfoTemp.setStamperHeight(
						StringUtils.toInteger(officeStamperInfoMap.get(Parameter.STAMPER_HEIGHT_KEY)));
			}
			if (officeStamperInfoMap.get(Parameter.STAMPER_WIDTH_KEY) != null
					&& StringUtils.isNumber(officeStamperInfoMap.get(Parameter.STAMPER_WIDTH_KEY).toString())) {
				stoOfficeStamperInfoTemp
						.setStamperWidth(StringUtils.toInteger(officeStamperInfoMap.get(Parameter.STAMPER_WIDTH_KEY)));
			}
			officeStamperInfoList.add(stoOfficeStamperInfoTemp);
			// 验证印章类型是否存在
			if (!isExistStamperType(stoOfficeStamperInfoTemp.getOfficeStamperType())) {
				logger.warn("64：印章操作接口--------参数印章类型为不存在，印章类型：" + stoOfficeStamperInfoTemp.getOfficeStamperType());
				officeStamperInfo.setErrorMsg(ExternalConstant.HardwareInterface.ERROR_NO_E03);
				return officeStamperInfo;
			}
		}
		// 获取用户ID
		Object userId = requestMap.get(Parameter.USER_ID_KEY);
		// 验证用户ID
		if (userId == null || StringUtils.isBlank(userId.toString())) {
			logger.warn("64：印章操作接口--------参数用户ID为空");
			officeStamperInfo.setErrorMsg(ExternalConstant.HardwareInterface.ERROR_NO_E03);
			return officeStamperInfo;
		}
		User user = systemService.getUser(userId.toString());
		if (user == null) {
			logger.warn("64：印章操作接口--------参数用户ID对应用户不存在， 参数用户ID：" + userId.toString());
			officeStamperInfo.setErrorMsg(ExternalConstant.HardwareInterface.ERROR_NO_E03);
			return officeStamperInfo;
		}
		officeStamperInfo.setUser(user);
		// 删除操作验证
		if (StamperOperation.STAMPER_DELETE.equals(type.toString())) {
			List<StoOfficeStamperInfo> deleteStoOfficeStamperInfoNoExistsList = Lists.newArrayList();
			for (StoOfficeStamperInfo stoOfficeStamperInfoTemp : officeStamperInfoList) {
				StoOfficeStamperInfo deleteStoOfficeStamperInfo = this.queryOfficeStamperByOfficeAndType(office,
						stoOfficeStamperInfoTemp.getOfficeStamperType());
				if (deleteStoOfficeStamperInfo == null) {
					deleteStoOfficeStamperInfoNoExistsList.add(stoOfficeStamperInfoTemp);
				}
			}
			if (deleteStoOfficeStamperInfoNoExistsList.isEmpty()) {
				officeStamperInfo.setStamperList(officeStamperInfoList);
				return officeStamperInfo;
			} else {
				logger.warn("64：印章操作接口--------操作印章不存在");
				officeStamperInfo.setErrorMsg(ExternalConstant.HardwareInterface.ERROR_NO_E24);
				officeStamperInfo.setStamperList(deleteStoOfficeStamperInfoNoExistsList);
				return officeStamperInfo;
			}
		}

		// 插入操作验证
		if (StamperOperation.STAMPER_ADD.equals(type.toString())) {
			officeStamperInfo.setStamperList(officeStamperInfoList);
			return officeStamperInfo;
		}
		// 印章操作类型不正确
		logger.warn("64：印章操作接口--------参数操作类型错误");
		officeStamperInfo.setErrorMsg(ExternalConstant.HardwareInterface.ERROR_NO_E03);
		return officeStamperInfo;
	}

}
