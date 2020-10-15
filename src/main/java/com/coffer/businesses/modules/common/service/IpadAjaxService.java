package com.coffer.businesses.modules.common.service;

import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.coffer.businesses.common.Constant;
import com.coffer.businesses.modules.common.AjaxBusinessUtils;
import com.coffer.businesses.modules.common.AjaxConstant;
import com.coffer.businesses.modules.common.entity.ReceiveEntity;
import com.coffer.businesses.modules.report.ReportGraphConstant;
import com.coffer.businesses.modules.report.v01.dao.BoxReportGraphDao;
import com.coffer.businesses.modules.report.v01.entity.StoBoxInfoGraphEntity;
import com.coffer.businesses.modules.store.StoreCommonUtils;
import com.coffer.businesses.modules.store.StoreConstant;
import com.coffer.businesses.modules.store.v01.dao.StoDictDao;
import com.coffer.businesses.modules.store.v01.dao.StoGoodsDao;
import com.coffer.businesses.modules.store.v01.dao.StoRelevanceDao;
import com.coffer.businesses.modules.store.v01.dao.StoStockCountInfoDao;
import com.coffer.businesses.modules.store.v01.dao.StoStoresHistoryDao;
import com.coffer.businesses.modules.store.v01.entity.ChangeStoreEntity;
import com.coffer.businesses.modules.store.v01.entity.StoDict;
import com.coffer.businesses.modules.store.v01.entity.StoGoods;
import com.coffer.businesses.modules.store.v01.entity.StoRelevance;
import com.coffer.businesses.modules.store.v01.entity.StoStockCountInfo;
import com.coffer.businesses.modules.store.v01.entity.StoStoresHistory;
import com.coffer.businesses.modules.store.v01.entity.StoStoresInfo;
import com.coffer.core.common.config.Global;
import com.coffer.core.common.service.BaseService;
import com.coffer.core.common.utils.Collections3;
import com.coffer.core.common.utils.DateUtils;
import com.coffer.core.common.utils.IdGen;
import com.coffer.core.common.utils.StringUtils;
import com.coffer.core.modules.sys.dao.OfficeDao;
import com.coffer.core.modules.sys.entity.Office;
import com.coffer.core.modules.sys.entity.User;
import com.coffer.core.modules.sys.service.SystemService;
import com.coffer.core.modules.sys.utils.DictUtils;
import com.coffer.external.ExternalConstant;
import com.coffer.external.Parameter;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

/**
 * 
 * @author LLF
 * @version 2015年9月17日
 * 
 *          处理ajax请求服务类
 *
 */
@Component
@Scope("singleton")
public class IpadAjaxService extends BaseService {

	@Autowired
	private SystemService systemService;
	@Autowired
	private OfficeDao officeDao;
	@Autowired
	private StoStockCountInfoDao stoStockCountInfoDao;
	@Autowired
	private StoStoresHistoryDao stoStoresHistoryDao;
	@Autowired
	private StoDictDao stoDictDao;
	@Autowired
	private StoRelevanceDao stoRelevanceDao;
	@Autowired
	private StoGoodsDao	stoGoodsDao;
	@Autowired
	private BoxReportGraphDao boxReportGraphDao;
	
	/**
	 * 
	 * Title: findBoxStatusGraph
	 * <p>Description: 箱袋数据查询</p>
	 * @author:     wanghan
	 * @param stoBoxInfoGraphEntity
	 * @return 
	 * List<StoBoxInfoGraphEntity>    返回类型
	 */
	@Transactional(readOnly = true)
	public List<StoBoxInfoGraphEntity> findBoxStatusGraph(StoBoxInfoGraphEntity stoBoxInfoGraphEntity) {
		return boxReportGraphDao.findBoxStatusGraph(stoBoxInfoGraphEntity);
	}

	/**
	 * 
	 * @author LLF
	 * @version 2015年9月22日
	 * 
	 *          上传盘点信息
	 * @param serviceNo
	 * @param entity
	 * @return
	 */
	@Transactional(readOnly = false)
	public String uploadStockCountInfo(String serviceNo, ReceiveEntity entity) {
		String officeId = entity.getOfficeId();
		Office office = officeDao.get(officeId);
		String userId = entity.getUserId();
		List<Map<String, Object>> list = entity.getList();
		Map<String, Object> map = Maps.newHashMap();
		map.put("serviceNo", serviceNo);
		map.put("versionNo", entity.getVersionNo());
		try {
			if (StringUtils.isNotBlank(userId) && office != null && !Collections3.isEmpty(list)) {

				String userName = AjaxBusinessUtils.getUserNameByUserId(userId, officeId);
				// 盘点编号
				String sto_no = AjaxBusinessUtils
						.getNewBusinessNo(Global.getConfig("businessType.store.stock"), office);
				for (Map<String, Object> itemMap : list) {
					// 盘点信息整合
					StoStockCountInfo stoStockCountInfo = new StoStockCountInfo();
					stoStockCountInfo.setStockCountId(IdGen.uuid());
					stoStockCountInfo.setStockCountNo(sto_no);
					stoStockCountInfo.setUpdateFlag(AjaxConstant.StockCount.UPDATE_FALG_FALSE);
					stoStockCountInfo.setStockCountType(itemMap.get("goods_type").toString());
					stoStockCountInfo.setGoodsId(itemMap.get("goods_id").toString());
					stoStockCountInfo.setStockCountNum(Long.parseLong(itemMap.get("sto_num").toString()));
					stoStockCountInfo.setOffice(new Office(officeId));
					//stoStockCountInfo.setCreateBy(new User(entity.getUserId()));
					stoStockCountInfo.setCreateName(userName);
					stoStockCountInfo.setCreateDate(new Date());
					stoStockCountInfo.setGoodsTypes(entity.getGoodsTypes());
					stoStockCountInfoDao.insert(stoStockCountInfo);

				}
				map.put("resultFlag", AjaxConstant.Interface.RESULT_FLAG_ACCESS);
				map.put("sto_no", sto_no);
			} else {
				map.put("resultFlag", AjaxConstant.Interface.RESULT_FLAG_FAILURE);
				// 参数异常
				map.put("errorNo", AjaxConstant.Interface.ERROR_NO_E03);
			}
		} catch (Exception e) {
			// 处理异常
			e.printStackTrace();
			map.put("resultFlag", ExternalConstant.HardwareInterface.RESULT_FLAG_FAILURE);
			map.put("errorNo", ExternalConstant.HardwareInterface.ERROR_NO_E02);
			return gson.toJson(map);
		}
		return gson.toJson(map);
	}

	/**
	 * 
	 * @author LLF
	 * @version 2015年9月23日
	 * 
	 *          根据盘点信息更新库存
	 * @param serviceNo
	 * @param entity
	 * @return
	 */
	@Transactional(readOnly = false)
	public String updateStoreByStockCount(String serviceNo, ReceiveEntity entity) {

		String officeId = entity.getOfficeId();
		String userId = entity.getUserId();
		String loginName = entity.getLoginName();
		String password = entity.getPassword();

		Map<String, Object> map = Maps.newHashMap();
		map.put("serviceNo", serviceNo);
		map.put("versionNo", AjaxConstant.Interface.VERSION_NO_01);

		if (StringUtils.isNotBlank(userId) && StringUtils.isNotBlank(officeId) && StringUtils.isNotBlank(loginName)
				&& StringUtils.isNotBlank(password)) {
			// 用户身份验证
			User user = AjaxBusinessUtils.validateLoginUserInfo(loginName, password, true);
			if (user != null) {
				// 获取当日最新盘点信息，同时判断盘点信息是否已经更新库存
				StoStockCountInfo stoStockCountInfo = new StoStockCountInfo();
				stoStockCountInfo.setCreateDate(new Date());
				stoStockCountInfo.setOffice(new Office(officeId));
				List<StoStockCountInfo> stockList = stoStockCountInfoDao.getMaxStockNoToday(stoStockCountInfo);
				
				if (!Collections3.isEmpty(stockList)
						&& AjaxConstant.StockCount.UPDATE_FALG_FALSE.equals(stockList.get(0).getUpdateFlag())) {
					// 如果存在可以更新库存盘点信息，验证在此盘点后时候存在库存变更，需要重新盘点
					Date stockDate = stockList.get(0).getCreateDate();
					String stockNo = stockList.get(0).getStockCountNo();
					String goodsTypes = stockList.get(0).getGoodsTypes();
					StoStoresHistory stoStoresHistory = new StoStoresHistory();
					stoStoresHistory.setOffice(new Office(officeId));
					stoStoresHistory.setCreateDate(stockDate);
					List<StoStoresHistory> historyList = stoStoresHistoryDao.getStoreAfterStock(stoStoresHistory);
					
					if (Collections3.isEmpty(historyList)) {
						// 获取最新库存信息
						Map<String, Object> listMap = StoreCommonUtils.getNewestStoreInfo(officeId);
						@SuppressWarnings("unchecked")
						List<StoStoresInfo> stoStoresInfoList = (List<StoStoresInfo>) listMap
								.get("stoStoresInfoList");
						
						// 可以更新库存
						List<ChangeStoreEntity> list = Lists.newArrayList();
						// 获取此次盘点物品类型,并且获取盘点信息变更
//							StringBuffer sb = new StringBuffer();
						for (StoStockCountInfo stockCountTemp : stockList) {
//								if (!sb.toString().contains(stockCountTemp.getStockCountType())) {
//									sb.append(stockCountTemp.getStockCountType());
//									sb.append(",");
//								}
							boolean flag = true;
							long stockNum = stockCountTemp.getStockCountNum();// 盘点数量
							
							ChangeStoreEntity changeStoreEntity = new ChangeStoreEntity();
							changeStoreEntity.setGoodsId(stockCountTemp.getGoodsId());
							changeStoreEntity.setGoodType(stockCountTemp.getStockCountType());
							
							for (StoStoresInfo stoStoresInfo : stoStoresInfoList) {
								// 盘点信息存在库存中
								if (stockCountTemp.getGoodsId().equals(stoStoresInfo.getGoodsId())) {
									long storeNum = stoStoresInfo.getStoNum();// 库存数量
									changeStoreEntity.setNum(stockNum - storeNum);
									flag = false;
									break;
								}
							}
							if (flag) {
								// 盘点物品信息不再库存中
								changeStoreEntity.setNum(stockNum);
							}
							// 变更数量为零（即盘点数目与库存一致不需要再修改）
							if(changeStoreEntity.getNum() != 0) {
								list.add(changeStoreEntity);
							}
						}
						
						// 统计盘点类型中不存在的库存信息
						for (StoStoresInfo stoStoresInfo : stoStoresInfoList) {
							if (goodsTypes.contains(stoStoresInfo.getGoodsType())) {
								boolean flag = true;
								for (StoStockCountInfo stockCountTemp : stockList) {
									if (stockCountTemp.getGoodsId().equals(stoStoresInfo.getGoodsId())) {
										flag = false;
										break;
									}
								}
								// 盘点中不存在此物品库存信息，将此物品清零
								if (flag) {
									ChangeStoreEntity changeStoreEntity = new ChangeStoreEntity();
									changeStoreEntity.setGoodsId(stoStoresInfo.getGoodsId());
									changeStoreEntity.setGoodType(stoStoresInfo.getGoodsType());
									changeStoreEntity.setNum(0 - stoStoresInfo.getStoNum());
									if(changeStoreEntity.getNum() != 0) {
										list.add(changeStoreEntity);
									}
								}
							}
						}
						// 更改库存
						String[] argUser = userId.split(",");
						String changeMessage = StoreCommonUtils.changeStoreBlankBillStores(list, officeId,
								stockNo, systemService.getUser(argUser.length > 0 ? argUser[0] : ""));
						if (Constant.SUCCESS.equals(changeMessage)) {
							
							// 根据盘点编号修改盘点信息
							String userName = AjaxBusinessUtils.getUserNameByUserId(userId, officeId);
							StoStockCountInfo updateStockCountInfo = new StoStockCountInfo();
							updateStockCountInfo.setStockCountNo(stockNo);
							updateStockCountInfo.setUpdateName(userName);
							updateStockCountInfo.setUpdateDate(new Date());
							updateStockCountInfo.setManagerUserid(user.getId());
							updateStockCountInfo.setManagerUsername(user.getName());
							updateStockCountInfo.setUpdateFlag(AjaxConstant.StockCount.UPDATE_FALG_TRUE);
							stoStockCountInfoDao.updateStockCountByStockNo(updateStockCountInfo);
							// 成功
							map.put("resultFlag", AjaxConstant.Interface.RESULT_FLAG_ACCESS);
						}
					} else {
						// 无最新盘点信息，需要上传新的盘点信息
						map.put("resultFlag", AjaxConstant.Interface.RESULT_FLAG_FAILURE);
						map.put("errorNo", AjaxConstant.Interface.ERROR_NO_E07);
					}
				} else {
					// 无最新盘点信息更新库存
					map.put("resultFlag", AjaxConstant.Interface.RESULT_FLAG_FAILURE);
					map.put("errorNo", AjaxConstant.Interface.ERROR_NO_E07);
				}
			} else {
				map.put("resultFlag", AjaxConstant.Interface.RESULT_FLAG_FAILURE);
				// 用户身份信息不正确
				map.put("errorNo", AjaxConstant.Interface.ERROR_NO_E04);
			}
		} else {
			map.put("resultFlag", AjaxConstant.Interface.RESULT_FLAG_FAILURE);
			// 参数异常
			map.put("errorNo", AjaxConstant.Interface.ERROR_NO_E03);
		}

		return gson.toJson(map);
	}
	
	/**
	 * 
	 * @author LLF
	 * @version 2015年10月9日
	 * 
	 *          盘点用户登录
	 * @param serviceNo
	 * @param entity
	 * @return
	 */
	public String padUserLogin(String serviceNo, ReceiveEntity entity) {
		Map<String, Object> map = Maps.newHashMap();

		map.put("serviceNo", serviceNo);
		map.put("versionNo", AjaxConstant.Interface.VERSION_NO_01);
		try {
			List<Map<String, Object>> list = entity.getList();
			if (!Collections3.isEmpty(list)) {
				boolean flag = false;
				List<Map<String, Object>> recList = Lists.newArrayList();
//				String officeId = "";
				for (Map<String, Object> itemMap : list) {
					User user = AjaxBusinessUtils.validateLoginUserInfo(itemMap.get("loginName").toString(), itemMap
							.get("password").toString(), false);
					if (user != null && StringUtils.isNotBlank(user.getCsPermission())
							&& user.getCsPermission().contains(Global.getConfig("user.csPermission.stockcount"))) {
						flag = true;
						Map<String, Object> recMap = Maps.newHashMap();
						recMap.put("id", user.getId());
						recMap.put("name", user.getName());
						recMap.put("loginName", user.getLoginName());
						recMap.put("officeId", user.getOffice().getId());
						recList.add(recMap);
//						// 验证用户机构
//						String tempOffice = user.getOffice().getId();
//						if(StringUtils.isNotBlank(officeId)) {
//							if(!officeId.equals(tempOffice)||(StringUtils.isNotBlank(entity.getOfficeId())&&officeId.equals(entity.getOfficeId()))) {
//								flag = false;
//								map.put("resultFlag", AjaxConstant.Interface.RESULT_FLAG_FAILURE);
//								map.put("errorNo", AjaxConstant.Interface.ERROR_NO_E08);
//								break;
//							}
//						} else {
//							officeId = tempOffice;
//						}
					} else {
						flag = false;
						map.put("resultFlag", AjaxConstant.Interface.RESULT_FLAG_FAILURE);
						// 用户不存在
						map.put("errorNo", AjaxConstant.Interface.ERROR_NO_E04);
						map.put("errorMsg", itemMap.get("loginName").toString());
						break;
					}
				}
				if (flag) {
					// 成功
					map.put("resultFlag", AjaxConstant.Interface.RESULT_FLAG_ACCESS);
					map.put("list", recList);
				}
			} else {
				map.put("resultFlag", AjaxConstant.Interface.RESULT_FLAG_FAILURE);
				// 参数异常
				map.put("errorNo", AjaxConstant.Interface.ERROR_NO_E03);
			}
		} catch (Exception e) {
			// 处理异常
			map.put("resultFlag", ExternalConstant.HardwareInterface.RESULT_FLAG_FAILURE);
			map.put("errorNo", ExternalConstant.HardwareInterface.ERROR_NO_E02);
			return gson.toJson(map);
		}
		return gson.toJson(map);
	}

	/**
	 * 
	 * @author LLF
	 * @version 2015年10月10日
	 * 
	 *          同步具有盘点权限用户信息
	 * @param serviceNo
	 * @param entity
	 * @return
	 */
	public String getUserByOffice(String serviceNo, ReceiveEntity entity) {
		Map<String, Object> map = Maps.newHashMap();

		map.put("serviceNo", serviceNo);
		map.put("versionNo", AjaxConstant.Interface.VERSION_NO_01);
		try {
			if (StringUtils.isNotBlank(entity.getOfficeId())) {
				List<User> userList = systemService.findUserByOfficeId(entity.getOfficeId());
				List<Map<String, Object>> list = Lists.newArrayList();
				for (User user : userList) {
					if (StringUtils.isNotBlank(user.getCsPermission())
							&& user.getCsPermission().contains(Global.getConfig("user.csPermission.stockcount"))) {
						Map<String, Object> userMap = Maps.newHashMap();
						userMap.put("id", user.getId());
						userMap.put("loginName", user.getLoginName());
						userMap.put("password", user.getPassword());
						userMap.put("name", user.getName());
						list.add(userMap);
					}
				}
				// 成功
				map.put("resultFlag", AjaxConstant.Interface.RESULT_FLAG_ACCESS);
				map.put("list", list);
			} else {
				map.put("resultFlag", AjaxConstant.Interface.RESULT_FLAG_FAILURE);
				// 参数异常
				map.put("errorNo", AjaxConstant.Interface.ERROR_NO_E03);
			}
		} catch (Exception e) {
			// 处理异常
			e.printStackTrace();
			map.put("resultFlag", ExternalConstant.HardwareInterface.RESULT_FLAG_FAILURE);
			map.put("errorNo", ExternalConstant.HardwareInterface.ERROR_NO_E02);
			return gson.toJson(map);
		}
		return gson.toJson(map);
	}
	
	/**
	 * 
	 * @author LLF
	 * @version 2015年10月10日
	 * 
	 *  盘点同步字典信息
	 * @param serviceNo
	 * @param entity
	 * @return
	 */
	public String getGoodsDictInfo(String serviceNo, ReceiveEntity entity) {
		Map<String, Object> map = Maps.newHashMap();

		map.put("serviceNo", serviceNo);
		map.put("versionNo", AjaxConstant.Interface.VERSION_NO_01);
		try {
			List<StoDict> dictList = stoDictDao.getStockCountDictBySearchDate(entity.getSearchDate(),
					Global.getConfig("jdbc.type"));
			
			// 获取物品信息
			List<StoGoods> stoGoodsList = stoGoodsDao.findList(new StoGoods());
			
			List<Map<String,Object>> list = Lists.newArrayList();
			for(StoDict stoDict : dictList) {
				Map<String,Object> itemMap = Maps.newHashMap();
				itemMap.put("id", stoDict.getId());
				itemMap.put("label", stoDict.getLabel());
				itemMap.put("value", stoDict.getValue());
				itemMap.put("type", stoDict.getType());
				itemMap.put("ref_code", stoDict.getRefCode());
				itemMap.put("del_flag", stoDict.getDelFlag());
				// 添加排序
				itemMap.put(Parameter.SORT_KEY, stoDict.getSort());
				// 验证删除字典是否还存在物品
				if (stoDict.DEL_FLAG_DELETE.equals(stoDict.getDelFlag())) {
					String[] subGoods = Global.getStringArray(stoDict.getType());
					if (subGoods != null && subGoods.length != 0) {
						int start = Integer.valueOf(subGoods[0]);
						int end = Integer.valueOf(subGoods[1]);
						for (StoGoods stoGoods : stoGoodsList) {
							if (StoreConstant.GoodType.BLANK_BILL.equals(stoGoods.getGoodsType())
									&& stoGoods.getId().subSequence(start, end).equals(stoDict.getValue())) {
								itemMap.put("del_flag", stoDict.DEL_FLAG_NORMAL);
								break;
							}
						}
					}
				}
				list.add(itemMap);
			}
			// 成功
			map.put("searchDate", DateUtils.getCurrentMillisecond());
			map.put("resultFlag", AjaxConstant.Interface.RESULT_FLAG_ACCESS);
			map.put("list", list);
		} catch (Exception e) {
			// 处理异常
			map.put("resultFlag", ExternalConstant.HardwareInterface.RESULT_FLAG_FAILURE);
			map.put("errorNo", ExternalConstant.HardwareInterface.ERROR_NO_E02);
			return gson.toJson(map);
		}
		return gson.toJson(map);
	}
	
	/**
	 * 
	 * @author LLF
	 * @version 2015年10月10日
	 * 
	 *          同步字典关联关系
	 * @param serviceNo
	 * @param entity
	 * @return
	 */
	public String getStockCountRelevance(String serviceNo, ReceiveEntity entity) {
		Map<String, Object> map = Maps.newHashMap();

		map.put("serviceNo", serviceNo);
		map.put("versionNo", AjaxConstant.Interface.VERSION_NO_01);
		try {
			List<StoRelevance> relevanceList = stoRelevanceDao.findList(null);
			List<Map<String, Object>> list = Lists.newArrayList();
			for (StoRelevance stoRelevance : relevanceList) {
				Map<String, Object> itemMap = Maps.newHashMap();
				itemMap.put("id", stoRelevance.getRelevanceId());
				itemMap.put("currency", stoRelevance.getCurrency());
				itemMap.put("classification", stoRelevance.getClassification());
				itemMap.put("sets", stoRelevance.getSets());
				itemMap.put("cash", stoRelevance.getCash());
				itemMap.put("denomination", stoRelevance.getDenomination());
				itemMap.put("unit", stoRelevance.getUnit());
				list.add(itemMap);
			}
			// 成功
			map.put("resultFlag", AjaxConstant.Interface.RESULT_FLAG_ACCESS);
			map.put("list", list);
		} catch (Exception e) {
			// 处理异常
			map.put("resultFlag", ExternalConstant.HardwareInterface.RESULT_FLAG_FAILURE);
			map.put("errorNo", ExternalConstant.HardwareInterface.ERROR_NO_E02);
			return gson.toJson(map);
		}
		return gson.toJson(map);
	}
	
	/**
	 * 
	 * @author LLF
	 * @version 2015年10月12日
	 * 
	 *          同步物品信息（盘点）
	 * @param serviceNo
	 * @param entity
	 * @return
	 */
	public String getGoodsAndStores(String serviceNo, ReceiveEntity entity) {
		Map<String, Object> map = Maps.newHashMap();

		map.put("serviceNo", serviceNo);
		map.put("versionNo", AjaxConstant.Interface.VERSION_NO_01);
		try {
			if (StringUtils.isNotBlank(entity.getOfficeId())) {
				// 获取当前库存信息
				Map<String, Object> storeMap = StoreCommonUtils.getNewestStoreInfo(entity.getOfficeId());
				List<StoStoresInfo> storeList = (List<StoStoresInfo>) storeMap.get("stoStoresInfoList");
				// 获取当前所有物品信息
				List<StoGoods> goodsList = stoGoodsDao.findAllList(null);

				List<Map<String, Object>> list = Lists.newArrayList();

				String searchDate = entity.getSearchDate();

				for (StoGoods stoGoods : goodsList) {
					Long num = 0L;
					String storeTime = "";
					Map<String, Object> itemMap = Maps.newHashMap();
					for (StoStoresInfo stoStoresInfo : storeList) {
						if (stoGoods.getGoodsID().equals(stoStoresInfo.getGoodsId())) {
							num = stoStoresInfo.getStoNum();
							storeTime = DateUtils.formatDate(stoStoresInfo.getCreateDate(),
									Constant.Dates.FORMATE_YYYYMMDDHHMMSSSSS);
							break;
						}
					}
					String goodsTime = DateUtils.formatDate(stoGoods.getUpdateDate(),
							Constant.Dates.FORMATE_YYYYMMDDHHMMSSSSS);
					if (StringUtils.isBlank(searchDate)
							|| (StringUtils.isNotBlank(goodsTime) && searchDate.compareTo(goodsTime) < 0)
							|| (StringUtils.isNotBlank(storeTime) && searchDate.compareTo(storeTime) < 0)) {
						itemMap.put("goods_id", stoGoods.getGoodsID());
						itemMap.put("goods_name", stoGoods.getGoodsName());
						itemMap.put("goods_type", stoGoods.getGoodsType());
						itemMap.put("goods_value", stoGoods.getGoodsVal());
						itemMap.put("sto_num", num);
						itemMap.put("del_flag", stoGoods.getDelFlag());

						list.add(itemMap);
					}
				}
				// 成功
				map.put("searchDate", DateUtils.getDateTimeAll());
				map.put("resultFlag", AjaxConstant.Interface.RESULT_FLAG_ACCESS);
				map.put("list", list);
			} else {
				map.put("resultFlag", AjaxConstant.Interface.RESULT_FLAG_FAILURE);
				// 参数异常
				map.put("errorNo", AjaxConstant.Interface.ERROR_NO_E03);
			}
		} catch (Exception e) {
			// 处理异常
			map.put("resultFlag", ExternalConstant.HardwareInterface.RESULT_FLAG_FAILURE);
			map.put("errorNo", ExternalConstant.HardwareInterface.ERROR_NO_E02);
			return gson.toJson(map);
		}
		return gson.toJson(map);
	}
	
	/**
	 * 
	 * @author LLF
	 * @version 2015年9月17日
	 * 
	 *          服务代码不存在，应答错误消息
	 * @param serviceNo
	 * @return
	 */
	public String erro(String serviceNo) {
		Map<String, Object> map = Maps.newHashMap();

		map.put("serviceNo", serviceNo);
		map.put("versionNo", AjaxConstant.Interface.VERSION_NO_01);
		map.put("resultFlag", AjaxConstant.Interface.RESULT_FLAG_FAILURE);
		map.put("errorNo", AjaxConstant.Interface.ERROR_NO_E06);

		return gson.toJson(map);
	}
	
	/**
	 * 
	 * @author LF
	 * @version 2014-12-15
	 * 
	 * @Description 用户授权接口
	 * @param headInfo
	 * @return
	 */
	public String userAuthorization(String serviceNo, ReceiveEntity entity) {

		Map<String, Object> map = new HashMap<String, Object>();
		// 版本号、服务代码
		map.put("versionNo", AjaxConstant.Interface.VERSION_NO_01);
		map.put("serviceNo", serviceNo);
		try {
			List<Map<String, Object>> userList = entity.getAuthorizeList();
			if (!Collections3.isEmpty(userList)) {
				for (Map<String, Object> userMap : userList) {
					// 循环授权人，验证身份
					if (userMap.get("loginName") != null && userMap.get("password") != null) {
						User user = systemService.getUserByLoginName(userMap.get("loginName").toString());
						// 验证用户名和密码
						if (user != null && userMap.get("password").toString().equals(user.getPassword())) {
							// 用户权限验证
							if (StringUtils.isNotBlank(user.getUserType())
									&& Global.getConfig("user.csPermission.authorization").contains(user.getUserType())) {
								map.put("resultFlag", ExternalConstant.HardwareInterface.RESULT_FLAG_SUCCESS);
							} else {
								// 用户权限不足
								map.put("resultFlag", ExternalConstant.HardwareInterface.RESULT_FLAG_FAILURE);
								map.put("errorNo", ExternalConstant.HardwareInterface.ERROR_NO_E10);
								break;
							}
						} else {
							// 用户名密码错误或用户不存在
							map.put("resultFlag", ExternalConstant.HardwareInterface.RESULT_FLAG_FAILURE);
							map.put("errorNo", ExternalConstant.HardwareInterface.ERROR_NO_E09);
							break;
						}
					} else {
						// 参数异常
						map.put("resultFlag", ExternalConstant.HardwareInterface.RESULT_FLAG_FAILURE);
						map.put("errorNo", ExternalConstant.HardwareInterface.ERROR_NO_E03);
						break;
					}
				}
			} else {
				// 参数异常
				map.put("resultFlag", ExternalConstant.HardwareInterface.RESULT_FLAG_FAILURE);
				map.put("errorNo", ExternalConstant.HardwareInterface.ERROR_NO_E03);
			}
		} catch (Exception e) {
			// 处理异常
			map.put("resultFlag", ExternalConstant.HardwareInterface.RESULT_FLAG_FAILURE);
			map.put("errorNo", ExternalConstant.HardwareInterface.ERROR_NO_E02);
		}

		return gson.toJson(map);
	}
	
	/**
	 * 
	 * Title: makeFirstBoxStatusGraphData
	 * <p>Description: 系统首页箱袋状态图业务层，放前台显示数据</p>
	 * @author:     wanghan
	 * @param stoBoxInfoGraphEntity
	 * @return 
	 * Map<String,Object>    返回类型
	 */
	@SuppressWarnings("unchecked")
	public Map<String, Object> makeFirstBoxStatusGraphData(StoBoxInfoGraphEntity stoBoxInfoGraphEntity) {

		Map<String, Object> rtnMap = Maps.newHashMap();

		List<String> legendDataList = Lists.newArrayList();
		List<String> xAxisDataList = Lists.newArrayList();
		List<Map<String, Object>> seriesDataList = Lists.newArrayList();

		List<StoBoxInfoGraphEntity> resultList = findBoxStatusGraph(stoBoxInfoGraphEntity);

		Map<String, Map<String, Object>> seriesMap = Maps.newHashMap();
		for (StoBoxInfoGraphEntity entity : resultList) {
			// 放入legend配置项数据

			String box_status = DictUtils.getDictLabel(entity.getBoxStatus(), "sto_box_status", "");

			if (!legendDataList.contains(box_status)) {
				legendDataList.add(box_status);
			}

			// 放入x轴数据
			if (!xAxisDataList.contains(entity.getOfficeName())) {
				xAxisDataList.add(entity.getOfficeName());
			}

			// 按类别放入箱袋数据
			if (!seriesMap.containsKey(box_status)) {

				List<String> dataList = Lists.newArrayList();

				dataList.add(entity.getBoxNum());
				Map<String, Object> map = Maps.newHashMap();
				map.put(ReportGraphConstant.SeriesProperties.SERIES_NAME_KEY, box_status);
				map.put(ReportGraphConstant.SeriesProperties.SERIES_TYPE_KEY, ReportGraphConstant.GraphType.BAR);
				map.put(ReportGraphConstant.SeriesProperties.SERIES_DATA_KEY, dataList);
				seriesMap.put(box_status, map);
			} else {

				Map<String, Object> map = (Map<String, Object>) seriesMap.get(box_status);
				List<String> dataList = (List<String>) map.get(ReportGraphConstant.SeriesProperties.SERIES_DATA_KEY);

				dataList.add(entity.getBoxNum());
			}
		}

		Iterator<String> iterator = seriesMap.keySet().iterator();

		while (iterator.hasNext()) {
			seriesDataList.add(seriesMap.get(iterator.next()));
		}

		rtnMap.put(ReportGraphConstant.DataGraphList.LEGEND_DATE_LIST, legendDataList);
		rtnMap.put(ReportGraphConstant.DataGraphList.XAXIS_DATE_LIST, xAxisDataList);
		rtnMap.put(ReportGraphConstant.DataGraphList.SERIES_DATE_LIST, seriesDataList);
		return rtnMap;
	}
}
