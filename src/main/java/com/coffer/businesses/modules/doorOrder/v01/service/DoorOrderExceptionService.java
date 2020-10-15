package com.coffer.businesses.modules.doorOrder.v01.service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.coffer.businesses.common.Constant;
import com.coffer.businesses.modules.doorOrder.DoorOrderConstant;
import com.coffer.businesses.modules.doorOrder.v01.dao.DoorOrderExceptionDao;
import com.coffer.businesses.modules.doorOrder.v01.dao.DoorOrderExceptionDetailDao;
import com.coffer.businesses.modules.doorOrder.v01.dao.SaveTypeDao;
import com.coffer.businesses.modules.doorOrder.v01.entity.DoorOrderException;
import com.coffer.businesses.modules.doorOrder.v01.entity.DoorOrderExceptionDetail;
import com.coffer.businesses.modules.doorOrder.v01.entity.OfficeAmount;
import com.coffer.businesses.modules.doorOrder.v01.entity.SaveType;
import com.coffer.core.common.persistence.Page;
import com.coffer.core.common.service.CrudService;
import com.coffer.core.common.utils.DateUtils;
import com.coffer.core.common.utils.IdGen;
import com.coffer.core.modules.sys.entity.Office;
import com.coffer.core.modules.sys.entity.User;
import com.coffer.core.modules.sys.utils.UserUtils;
import com.coffer.external.Parameter;
import com.coffer.external.service.Service0805;
import com.google.common.collect.Lists;

/**
 * 存款异常信息Service
 * 
 * @author zxk
 * @version 2019-11-11
 */
@Service
@Transactional(readOnly = true)
public class DoorOrderExceptionService extends CrudService<DoorOrderExceptionDao, DoorOrderException> {

	@Autowired
	private DoorOrderExceptionDao doorOrderExceptionDao;
	@Autowired
	private DoorOrderExceptionDetailDao doorOrderExceptionDetailDao;
	@Autowired
	private Service0805 service0805;
	@Autowired
	private SaveTypeDao saveTypeDao;

	public DoorOrderException get(String id) {
		return super.get(id);
	}

	public List<DoorOrderException>  findList(DoorOrderException doorOrderException) {
		return super.findList(doorOrderException);
	}

	public Page<DoorOrderException> findPage(Page<DoorOrderException> page, DoorOrderException doorOrderException) {
		// 查询条件： 开始时间
		if (doorOrderException.getCreateTimeStart() != null) {
			doorOrderException.setSearchDateStart(
					DateUtils.foramtSearchDate(DateUtils.getDateStart(doorOrderException.getCreateTimeStart())));
		}
		// 查询条件： 结束时间
		if (doorOrderException.getCreateTimeEnd() != null) {
			doorOrderException.setSearchDateEnd(
					DateUtils.foramtSearchDate(DateUtils.getDateEnd(doorOrderException.getCreateTimeEnd())));
		}
		return super.findPage(page, doorOrderException);
	}
	
	@Transactional(readOnly = false)
	public void delete(DoorOrderException doorOrderException) {
		super.delete(doorOrderException);
	}

	/**
	 * 门店人员列表
	 * @author gzd
	 * @version 2019年11月15日
	 * @param door_id
	 * @return
	 */
	public List<User> getPerson(String doorId){
		
		return doorOrderExceptionDao.getPerson(doorId);
	}
	
	/**
	 * 存款备注列表（七位码）
	 * @author gzd
	 * @version 2019年12月12日
	 * @param door_id
	 * @return
	 */
	public List<Office> getRemarks(String doorId){
		
		return doorOrderExceptionDao.getRemarks(doorId);
	}

	/**
	 * 存款异常登记
	 * 
	 * @author zxk
	 * @version 2019年11月12日
	 * @param doorOrderException
	 * @return
	 */
	@Transactional(readOnly = false)
	public void save(DoorOrderException doorOrderException) {
		// 金额明细列表
		String[] detailList = doorOrderException.getDetailList().split(",", -1);
		// 新增记录 登记
		// 设置状态（登记）
		doorOrderException.setCurrency(DoorOrderConstant.Currency.CNY);
		doorOrderException.setStatus(DoorOrderConstant.ExceptionStatus.REGISTER);
		// 设置耗时 gzd 2020-06-05
		String costTime = Long.toString(
				(doorOrderException.getEndTime().getTime() - doorOrderException.getStartTime().getTime()) / 1000);
		doorOrderException.setCostTime(costTime);
		User user = UserUtils.get(doorOrderException.getUser().getId());
		if (user != null) {
			doorOrderException.setUserName(user.getName());
		}
		// 凭条列表
		String[] tickertapeList = doorOrderException.getTickertapeList().split(",", -1);
		// 金额列表
		String[] amountList = doorOrderException.getAmountList().split(",", -1);
		// 业务类型列表
		String[] businessTypeList = doorOrderException.getBusTypeList().split(",", -1);
		// 存款备注列表
		String[] remarksList = doorOrderException.getRemarksList().split(",", -1);

		for (int i = 1; i < tickertapeList.length; i++) {
			doorOrderException.setTickerTape(tickertapeList[i]);
			// 获取 业务类型
			//
			doorOrderException.setBusinessType(businessTypeList[i]);
			doorOrderException.setTotalAmount(amountList[i]);
			doorOrderException.setRemarks(remarksList[i]);
			// 循环插入记录
			doorOrderException.preInsert();
			// 异常明细删除
			DoorOrderExceptionDetail doorOrderExceptionDetail = new DoorOrderExceptionDetail();
			doorOrderExceptionDetail.setId(doorOrderException.getId());
			doorOrderExceptionDetailDao.deleteById(doorOrderExceptionDetail);

			// 重新录入明细
			DoorOrderExceptionDetail doorOrderExDetail;
			// 面值数量
			// 判断如果速存时没有面值 直接存入金额
			boolean cc = false;
			// 明细列表
			List<Map<String, Object>> details = Lists.newArrayList();
			for (int k = 0; k < detailList.length; k++) {
				// 添加金额明细列表
				String[] detail = detailList[k].split("_", -1);

				if (detail[0].equals(doorOrderException.getTickerTape())) {

					doorOrderExDetail = new DoorOrderExceptionDetail();
					// 主键
					doorOrderExDetail.setDetailId(IdGen.uuid());
					// 主表Id
					doorOrderExDetail.setId(doorOrderException.getId());
					// 存款方式
					doorOrderExDetail.setType(detail[1]);
					if (detail[1].equals(DoorOrderConstant.SaveMethod.CASH_SAVE)) {
						// 面值
						doorOrderExDetail.setDenomination(detail[2].equals("") ? null : detail[2]);
						// 张数
						doorOrderExDetail.setCount(detail[3].equals("") ? null : detail[3]);
					} else {
						// 面值
						doorOrderExDetail.setDenomination(null);
						// 张数
						doorOrderExDetail.setCount(null);
					}
					// 金额
					doorOrderExDetail.setAmount(new BigDecimal(detail[4]));
					// 币种
					doorOrderExDetail.setCurrency(DoorOrderConstant.Currency.CNY);
					// 序号
					doorOrderExDetail.setRowNo(k);
					doorOrderExceptionDetailDao.insert(doorOrderExDetail);
					// 接口 获取每种面值对应张数总和
					if (detail[1].equals(DoorOrderConstant.SaveMethod.CASH_SAVE)) {
						List<Map<String, Object>> list01 = Lists.newArrayList();
						// 速存存款
						Map<String, Object> map = new HashMap<String, Object>();
						if (detail[2].equals("15")) {
							map.put(DoorOrderConstant.ServiceParameter.ID, "15");
							map.put(DoorOrderConstant.ServiceParameter.COUNT, Double.parseDouble(detail[3]));
						} else if (detail[2].equals("16")) {
							map.put(DoorOrderConstant.ServiceParameter.ID, "16");
							map.put(DoorOrderConstant.ServiceParameter.COUNT, Double.parseDouble(detail[3]));
						} else if (detail[2].equals("17")) {
							map.put(DoorOrderConstant.ServiceParameter.ID, "17");
							map.put(DoorOrderConstant.ServiceParameter.COUNT, Double.parseDouble(detail[3]));
						} else if (detail[2].equals("18")) {
							map.put(DoorOrderConstant.ServiceParameter.ID, "18");
							map.put(DoorOrderConstant.ServiceParameter.COUNT, Double.parseDouble(detail[3]));
						} else if (detail[2].equals("19")) {
							map.put(DoorOrderConstant.ServiceParameter.ID, "19");
							map.put(DoorOrderConstant.ServiceParameter.COUNT, Double.parseDouble(detail[3]));
						} else if (detail[2].equals("20")) {
							map.put(DoorOrderConstant.ServiceParameter.ID, "20");
							map.put(DoorOrderConstant.ServiceParameter.COUNT, Double.parseDouble(detail[3]));
						} else if (detail[2].equals("21")) {
							map.put(DoorOrderConstant.ServiceParameter.ID, "21");
							map.put(DoorOrderConstant.ServiceParameter.COUNT, Double.parseDouble(detail[3]));
						} else if (detail[2].equals("22")) {
							map.put(DoorOrderConstant.ServiceParameter.ID, "22");
							map.put(DoorOrderConstant.ServiceParameter.COUNT, Double.parseDouble(detail[3]));
						} else if (detail[2].equals("23")) {
							map.put(DoorOrderConstant.ServiceParameter.ID, "23");
							map.put(DoorOrderConstant.ServiceParameter.COUNT, Double.parseDouble(detail[3]));
						} else if (detail[2].equals("24")) {
							map.put(DoorOrderConstant.ServiceParameter.ID, "24");
							map.put(DoorOrderConstant.ServiceParameter.COUNT, Double.parseDouble(detail[3]));
						} else if (detail[2].equals("25")) {
							map.put(DoorOrderConstant.ServiceParameter.ID, "25");
							map.put(DoorOrderConstant.ServiceParameter.COUNT, Double.parseDouble(detail[3]));
						} else if (detail[2].equals("26")) {
							map.put(DoorOrderConstant.ServiceParameter.ID, "26");
							map.put(DoorOrderConstant.ServiceParameter.COUNT, Double.parseDouble(detail[3]));
						} else if (detail[2].equals("27")) {
							map.put(DoorOrderConstant.ServiceParameter.ID, "27");
							map.put(DoorOrderConstant.ServiceParameter.COUNT, Double.parseDouble(detail[3]));
						} else if (detail[2].equals("28")) {
							map.put(DoorOrderConstant.ServiceParameter.ID, "28");
							map.put(DoorOrderConstant.ServiceParameter.COUNT, Double.parseDouble(detail[3]));
						} else if (detail[2].equals("29")) {
							map.put(DoorOrderConstant.ServiceParameter.ID, "29");
							map.put(DoorOrderConstant.ServiceParameter.COUNT, Double.parseDouble(detail[3]));
						} else if (detail[2].equals("30")) {
							map.put(DoorOrderConstant.ServiceParameter.ID, "30");
							map.put(DoorOrderConstant.ServiceParameter.COUNT, Double.parseDouble(detail[3]));
						} else if (detail[2].equals("31")) {
							map.put(DoorOrderConstant.ServiceParameter.ID, "31");
							map.put(DoorOrderConstant.ServiceParameter.COUNT, Double.parseDouble(detail[3]));
						} else if (detail[2].equals("32")) {
							map.put(DoorOrderConstant.ServiceParameter.ID, "32");
							map.put(DoorOrderConstant.ServiceParameter.COUNT, Double.parseDouble(detail[3]));
						} else if (detail[2].equals("33")) {
							map.put(DoorOrderConstant.ServiceParameter.ID, "33");
							map.put(DoorOrderConstant.ServiceParameter.COUNT, Double.parseDouble(detail[3]));
						} else if (detail[2].equals("")) {
							cc = true;
						}
						list01.add(map);
						if (cc) {
							list01 = null;
						}
						// 明细对象
						Map<String, Object> map1 = new HashMap<String, Object>();
						map1.put(DoorOrderConstant.ServiceParameter.TYPE, DoorOrderConstant.SaveMethod.CASH_SAVE);
						map1.put(DoorOrderConstant.ServiceParameter.AMOUNT,
								Double.parseDouble(detail[4].equals("") ? "0" : detail[4]));
						map1.put(DoorOrderConstant.ServiceParameter.DENOMINATION, list01);
						// 加入明细
						details.add(map1);
					} else if (detail[1].equals(DoorOrderConstant.SaveMethod.BAG_SAVE)) {
						// 强制存款
						Map<String, Object> map2 = new HashMap<String, Object>();
						map2.put(DoorOrderConstant.ServiceParameter.TYPE, DoorOrderConstant.SaveMethod.BAG_SAVE);
						map2.put(DoorOrderConstant.ServiceParameter.AMOUNT,
								Double.parseDouble(detail[4].equals("") ? "0" : detail[4]));
						map2.put(DoorOrderConstant.ServiceParameter.DENOMINATION, null);
						// 加入明细
						details.add(map2);
					} else if (detail[1].equals(DoorOrderConstant.SaveMethod.OTHER_SAVE)) {
						// 其他存款
						Map<String, Object> map3 = new HashMap<String, Object>();
						map3.put(DoorOrderConstant.ServiceParameter.TYPE, DoorOrderConstant.SaveMethod.OTHER_SAVE);
						map3.put(DoorOrderConstant.ServiceParameter.AMOUNT,
								Double.parseDouble(detail[4].equals("") ? "0" : detail[4]));
						map3.put(DoorOrderConstant.ServiceParameter.DENOMINATION, null);
						// 加入明细
						details.add(map3);
					}
				}

			}
			// 插入数据后调用0805接口
			Map<String, Object> paramMap = new HashMap<String, Object>();
			// 接口编号
			paramMap.put(DoorOrderConstant.ServiceParameter.SERVICE_NO, DoorOrderConstant.ServiceParameter.NO);
			// 机具号
			paramMap.put(DoorOrderConstant.ServiceParameter.EQUIPMENT_ID, doorOrderException.getEqpId());
			// 业务类型
			// id 转 code
			SaveType saveType = saveTypeDao.get(businessTypeList[i]);
			paramMap.put(DoorOrderConstant.ServiceParameter.BUSINESS_TYPE, saveType.getTypeCode());
			// 钞袋号
			paramMap.put(DoorOrderConstant.ServiceParameter.BAG_NO, doorOrderException.getBagNo());
			// 总金额
			paramMap.put(DoorOrderConstant.ServiceParameter.TOTAL_AMOUNT,
					Double.parseDouble(doorOrderException.getTotalAmount()));
			// 凭条号
			paramMap.put(DoorOrderConstant.ServiceParameter.TICKER_TAPE, tickertapeList[i]);
			// 用户id
			paramMap.put(DoorOrderConstant.ServiceParameter.USER_ID, doorOrderException.getUser().getId());
			// 币种
			paramMap.put(DoorOrderConstant.ServiceParameter.CURRENCY, DoorOrderConstant.Currency.CNY);
			// 存款备注 gzd 2019-12-12
			/*
			 * String
			 * remarks=doorOrderException.getRemarks()+doorOrderException.
			 * getRemarksLast(); if(remarks.length()!=7){ return
			 * tickertapeList[i] +":" + "存款备注位数有误！"; }
			 * doorOrderException.setRemarks(remarks);
			 */
			paramMap.put(DoorOrderConstant.ServiceParameter.REMARKS_KEY, doorOrderException.getRemarks());
			// 开始时间
			paramMap.put(DoorOrderConstant.ServiceParameter.START_TIME_KEY,
					String.valueOf(doorOrderException.getStartTime().getTime()));
			// 结束时间
			paramMap.put(DoorOrderConstant.ServiceParameter.END_TIME_KEY,
					String.valueOf(doorOrderException.getEndTime().getTime()));
			// 耗时
			paramMap.put(DoorOrderConstant.ServiceParameter.COST_TIME_KEY, doorOrderException.getCostTime());
			paramMap.put(DoorOrderConstant.ServiceParameter.DETAIL, details);// detail
			paramMap.put(Parameter.ORDER_FROM, Service0805.FROM_EXCEPTION);
			/*try {*/
			service0805.execute(paramMap);
			doorOrderException.setBusinessType(saveType.getTypeCode());
			dao.insert(doorOrderException);
			/*} catch (BusinessException be) {
				return tickertapeList[i] + ":" + be.getMessageContent();
			}*/

		}
		// return null;

	}
	
	/**
	 * 存款异常处理(修改)
	 * 
	 * @author zxk
	 * @version 2019年11月12日
	 * @param doorOrderException
	 * @return
	 */
	@Transactional(readOnly = false)
	public void update(DoorOrderException doorOrderException) {
		// 金额明细列表
		String[] detailList = doorOrderException.getDetailList().split(",", -1);
		// 修改主表信息
		// 币种
		doorOrderException.setCurrency(DoorOrderConstant.Currency.CNY);
		// 设置状态（已处理）
		doorOrderException.setStatus(DoorOrderConstant.ExceptionStatus.PROCESSED);
		// 设置耗时 gzd 2020-01-07
		String costTime = Long.toString(
				(doorOrderException.getEndTime().getTime() - doorOrderException.getStartTime().getTime()) / 1000);
		doorOrderException.setCostTime(costTime);
		// 清空异常信息
		// doorOrderException.setExceptionReason(null);
		doorOrderException.preUpdate();
		// 异常明细删除
		DoorOrderExceptionDetail doorOrderExceptionDetail = new DoorOrderExceptionDetail();
		doorOrderExceptionDetail.setId(doorOrderException.getId());
		doorOrderExceptionDetailDao.deleteById(doorOrderExceptionDetail);
		// 插入修改后数据
		// 重新录入明细
		DoorOrderExceptionDetail doorOrderExDetail;
		// 面值数量
		// 判断如果速存时没有面值 直接存入金额
		boolean cc = false;
		List<Map<String, Object>> details = Lists.newArrayList();
		for (int k = 1; k < detailList.length; k++) {
			// 金额明细列表
			String[] detail = detailList[k].split("_", -1);

			doorOrderExDetail = new DoorOrderExceptionDetail();
			// 主键
			doorOrderExDetail.setDetailId(IdGen.uuid());
			// 主表Id
			doorOrderExDetail.setId(doorOrderException.getId());
			// 存款方式转化及录入
			if (detail[0].equals(DoorOrderConstant.SaveMethodName.CASH_SAVE)) {
				// 速存存款
				doorOrderExDetail.setType(DoorOrderConstant.SaveMethod.CASH_SAVE);
				// 面值
				doorOrderExDetail.setDenomination(detail[1].equals("") ? null : detail[1]);
				// 张数
				doorOrderExDetail.setCount(detail[2].equals("") ? null : detail[2]);
			} else if (detail[0].equals(DoorOrderConstant.SaveMethodName.BAG_SAVE)) {
				// 封包存款
				doorOrderExDetail.setType(DoorOrderConstant.SaveMethod.BAG_SAVE);
				// 面值
				doorOrderExDetail.setDenomination(null);
				// 张数
				doorOrderExDetail.setCount(null);
			} else if (detail[0].equals(DoorOrderConstant.SaveMethodName.OTHER_SAVE)) {
				// 其他存款
				doorOrderExDetail.setType(DoorOrderConstant.SaveMethod.OTHER_SAVE);
				// 面值
				doorOrderExDetail.setDenomination(null);
				// 张数
				doorOrderExDetail.setCount(null);
			} else {
				// 其他存款
				doorOrderExDetail.setType(detail[0]);
				// 面值
				doorOrderExDetail.setDenomination(detail[1].equals("") ? null : detail[1]);
				// 张数
				doorOrderExDetail.setCount(detail[2].equals("") ? null : detail[2]);
			}

			// 金额
			doorOrderExDetail.setAmount(new BigDecimal(detail[3].equals("") ? "0" : detail[3]));
			// 币种
			doorOrderExDetail.setCurrency(DoorOrderConstant.Currency.CNY);
			// 序号
			doorOrderExDetail.setRowNo(k);
			// 增加明细记录
			doorOrderExceptionDetailDao.insert(doorOrderExDetail);
			// 接口 获取每种面值对应张数总和
			if (detail[0].equals(DoorOrderConstant.SaveMethodName.CASH_SAVE)) {
				List<Map<String, Object>> list01 = Lists.newArrayList();
				// 速存存款
				Map<String, Object> map = new HashMap<String, Object>();
				if (detail[1].equals("15")) {
					map.put(DoorOrderConstant.ServiceParameter.ID, "15");
					map.put(DoorOrderConstant.ServiceParameter.COUNT,
							Double.parseDouble(detail[2].equals("") ? "0" : detail[2]));
				} else if (detail[1].equals("16")) {
					map.put(DoorOrderConstant.ServiceParameter.ID, "16");
					map.put(DoorOrderConstant.ServiceParameter.COUNT,
							Double.parseDouble(detail[2].equals("") ? "0" : detail[2]));
				} else if (detail[1].equals("17")) {
					map.put(DoorOrderConstant.ServiceParameter.ID, "17");
					map.put(DoorOrderConstant.ServiceParameter.COUNT,
							Double.parseDouble(detail[2].equals("") ? "0" : detail[2]));
				} else if (detail[1].equals("18")) {
					map.put(DoorOrderConstant.ServiceParameter.ID, "18");
					map.put(DoorOrderConstant.ServiceParameter.COUNT,
							Double.parseDouble(detail[2].equals("") ? "0" : detail[2]));
				} else if (detail[1].equals("19")) {
					map.put(DoorOrderConstant.ServiceParameter.ID, "19");
					map.put(DoorOrderConstant.ServiceParameter.COUNT,
							Double.parseDouble(detail[2].equals("") ? "0" : detail[2]));
				} else if (detail[1].equals("20")) {
					map.put(DoorOrderConstant.ServiceParameter.ID, "20");
					map.put(DoorOrderConstant.ServiceParameter.COUNT,
							Double.parseDouble(detail[2].equals("") ? "0" : detail[2]));
				} else if (detail[1].equals("21")) {
					map.put(DoorOrderConstant.ServiceParameter.ID, "21");
					map.put(DoorOrderConstant.ServiceParameter.COUNT,
							Double.parseDouble(detail[2].equals("") ? "0" : detail[2]));
				} else if (detail[1].equals("22")) {
					map.put(DoorOrderConstant.ServiceParameter.ID, "22");
					map.put(DoorOrderConstant.ServiceParameter.COUNT,
							Double.parseDouble(detail[2].equals("") ? "0" : detail[2]));
				} else if (detail[1].equals("23")) {
					map.put(DoorOrderConstant.ServiceParameter.ID, "23");
					map.put(DoorOrderConstant.ServiceParameter.COUNT,
							Double.parseDouble(detail[2].equals("") ? "0" : detail[2]));
				} else if (detail[1].equals("24")) {
					map.put(DoorOrderConstant.ServiceParameter.ID, "24");
					map.put(DoorOrderConstant.ServiceParameter.COUNT,
							Double.parseDouble(detail[2].equals("") ? "0" : detail[2]));
				} else if (detail[1].equals("25")) {
					map.put(DoorOrderConstant.ServiceParameter.ID, "25");
					map.put(DoorOrderConstant.ServiceParameter.COUNT,
							Double.parseDouble(detail[2].equals("") ? "0" : detail[2]));
				} else if (detail[1].equals("26")) {
					map.put(DoorOrderConstant.ServiceParameter.ID, "26");
					map.put(DoorOrderConstant.ServiceParameter.COUNT,
							Double.parseDouble(detail[2].equals("") ? "0" : detail[2]));
				} else if (detail[1].equals("27")) {
					map.put(DoorOrderConstant.ServiceParameter.ID, "27");
					map.put(DoorOrderConstant.ServiceParameter.COUNT,
							Double.parseDouble(detail[2].equals("") ? "0" : detail[2]));
				} else if (detail[1].equals("28")) {
					map.put(DoorOrderConstant.ServiceParameter.ID, "28");
					map.put(DoorOrderConstant.ServiceParameter.COUNT,
							Double.parseDouble(detail[2].equals("") ? "0" : detail[2]));
				} else if (detail[1].equals("29")) {
					map.put(DoorOrderConstant.ServiceParameter.ID, "29");
					map.put(DoorOrderConstant.ServiceParameter.COUNT,
							Double.parseDouble(detail[2].equals("") ? "0" : detail[2]));
				} else if (detail[1].equals("30")) {
					map.put(DoorOrderConstant.ServiceParameter.ID, "30");
					map.put(DoorOrderConstant.ServiceParameter.COUNT,
							Double.parseDouble(detail[2].equals("") ? "0" : detail[2]));
				} else if (detail[1].equals("31")) {
					map.put(DoorOrderConstant.ServiceParameter.ID, "31");
					map.put(DoorOrderConstant.ServiceParameter.COUNT,
							Double.parseDouble(detail[2].equals("") ? "0" : detail[2]));
				} else if (detail[1].equals("32")) {
					map.put(DoorOrderConstant.ServiceParameter.ID, "32");
					map.put(DoorOrderConstant.ServiceParameter.COUNT,
							Double.parseDouble(detail[2].equals("") ? "0" : detail[2]));
				} else if (detail[1].equals("33")) {
					map.put(DoorOrderConstant.ServiceParameter.ID, "33");
					map.put(DoorOrderConstant.ServiceParameter.COUNT,
							Double.parseDouble(detail[2].equals("") ? "0" : detail[2]));
				} else if (detail[1].equals("")) {
					cc = true;
				}
				list01.add(map);
				if (cc) {
					list01 = null;
				}
				// 明细对象
				Map<String, Object> map1 = new HashMap<String, Object>();
				map1.put(DoorOrderConstant.ServiceParameter.TYPE, DoorOrderConstant.SaveMethod.CASH_SAVE);
				map1.put(DoorOrderConstant.ServiceParameter.AMOUNT,
						Double.parseDouble(detail[3].equals("") ? "0" : detail[3]));
				map1.put(DoorOrderConstant.ServiceParameter.DENOMINATION, list01);
				// 加入明细
				details.add(map1);
			} else if (detail[0].equals(DoorOrderConstant.SaveMethodName.BAG_SAVE)) {
				// 强制存款
				Map<String, Object> map2 = new HashMap<String, Object>();
				map2.put(DoorOrderConstant.ServiceParameter.TYPE, DoorOrderConstant.SaveMethod.BAG_SAVE);
				map2.put(DoorOrderConstant.ServiceParameter.AMOUNT,
						Double.parseDouble(detail[3].equals("") ? "0" : detail[3]));
				map2.put(DoorOrderConstant.ServiceParameter.DENOMINATION, null);
				// 加入明细
				details.add(map2);
			} else if (detail[0].equals(DoorOrderConstant.SaveMethodName.OTHER_SAVE)) {
				// 其他存款
				Map<String, Object> map3 = new HashMap<String, Object>();
				map3.put(DoorOrderConstant.ServiceParameter.TYPE, DoorOrderConstant.SaveMethod.OTHER_SAVE);
				map3.put(DoorOrderConstant.ServiceParameter.AMOUNT,
						Double.parseDouble(detail[3].equals("") ? "0" : detail[3]));
				map3.put(DoorOrderConstant.ServiceParameter.DENOMINATION, null);
				// 加入明细
				details.add(map3);
			} else {
				// 异常存款
				Map<String, Object> map4 = new HashMap<String, Object>();
				map4.put(DoorOrderConstant.ServiceParameter.TYPE, detail[0]);
				map4.put(DoorOrderConstant.ServiceParameter.AMOUNT,
						Double.parseDouble(detail[3].equals("") ? "0" : detail[3]));
				map4.put(DoorOrderConstant.ServiceParameter.DENOMINATION, null);
				// 加入明细
				details.add(map4);
			}
		}
		// 插入数据后调用0805接口
		Map<String, Object> paramMap = new HashMap<String, Object>();
		// 接口编号
		paramMap.put(DoorOrderConstant.ServiceParameter.SERVICE_NO, DoorOrderConstant.ServiceParameter.NO);
		// 机具号
		paramMap.put(DoorOrderConstant.ServiceParameter.EQUIPMENT_ID, doorOrderException.getEqpId());
		// 业务类型
		paramMap.put(DoorOrderConstant.ServiceParameter.BUSINESS_TYPE, doorOrderException.getBusinessType());
		// 钞袋号
		paramMap.put(DoorOrderConstant.ServiceParameter.BAG_NO, doorOrderException.getBagNo());
		// 总金额
		paramMap.put(DoorOrderConstant.ServiceParameter.TOTAL_AMOUNT,
				Double.parseDouble(doorOrderException.getTotalAmount()));
		// 凭条号
		paramMap.put(DoorOrderConstant.ServiceParameter.TICKER_TAPE, doorOrderException.getTickerTape());
		// 用户id
		paramMap.put(DoorOrderConstant.ServiceParameter.USER_ID, doorOrderException.getUser().getId());
		// 币种
		paramMap.put(DoorOrderConstant.ServiceParameter.CURRENCY, DoorOrderConstant.Currency.CNY);
		// 开始时间
		paramMap.put(DoorOrderConstant.ServiceParameter.START_TIME_KEY,
				String.valueOf(doorOrderException.getStartTime().getTime()));
		// 结束时间
		paramMap.put(DoorOrderConstant.ServiceParameter.END_TIME_KEY,
				String.valueOf(doorOrderException.getEndTime().getTime()));
		// 耗时
		paramMap.put(DoorOrderConstant.ServiceParameter.COST_TIME_KEY, doorOrderException.getCostTime());
		// 明细对象
		paramMap.put(DoorOrderConstant.ServiceParameter.DETAIL, details);
		// 存款备注 （先拼接后传值）
		String remarks = "";
		if (doorOrderException.getRemarksLast() != null) {
			remarks = doorOrderException.getRemarks() + (String) doorOrderException.getRemarksLast();
		} else {
			remarks = doorOrderException.getRemarks();
		}
		doorOrderException.setRemarks(remarks);
		paramMap.put(DoorOrderConstant.ServiceParameter.REMARKS_KEY, doorOrderException.getRemarks());
		paramMap.put(DoorOrderConstant.ServiceParameter.START_TIME_KEY,
				String.valueOf(doorOrderException.getStartTime().getTime()));
		paramMap.put(DoorOrderConstant.ServiceParameter.END_TIME_KEY,
				String.valueOf(doorOrderException.getEndTime().getTime()));
		paramMap.put(DoorOrderConstant.ServiceParameter.COST_TIME_KEY,
				String.valueOf(doorOrderException.getCostTime()));
		paramMap.put(Parameter.ORDER_FROM, Service0805.FROM_EXCEPTION);
		service0805.execute(paramMap);
		dao.update(doorOrderException);
	}
	
	
	/**
	 * Description: 查询存款异常/已处理 总数
	 * 
	 * @version 2020年09月24日
	 * @author: ZXK
	 * @param officeAmount
	 * @return OfficeAmount 返回类型
	 */
	public OfficeAmount getDoorExceptionCount(){
		
		OfficeAmount office = new OfficeAmount();
		//Office userOffice = UserUtils.getUser().getOffice();
		// 数据过滤(中心看上级人行下的所有门店，其他用户正常穿透)
		   User userInfo = UserUtils.getUser();
		if (UserUtils.getUser().getOffice().getType().equals(Constant.OfficeType.CLEAR_CENTER)) {
			office.getSqlMap().put("dsf2", "LEFT JOIN sys_office office ON office.ID = u9.office_id ");// 查询存款人对应的部门parent_ids
			office.getSqlMap().put("dsf",
					"AND (a.door_id =" + userInfo.getOffice().getId() + " OR office.parent_ids LIKE '%"
							+ userInfo.getOffice().getParentId() + "%'" + " OR o2.parent_ids LIKE '%"
							+ userInfo.getOffice().getParentId() + "%')");// 查询出当机具没有绑定时
																			// 但存在存款人
																			// 根据存款人所在机构
																			// 查询存款异常信息
		} else if (UserUtils.getUser().getOffice().getType().equals(Constant.OfficeType.DIGITAL_PLATFORM)) {// 判断是否是平台登录
			// 如果是平台登录 显示出无机具无用户的特殊情况的存款异常
			office.getSqlMap().put("dsf",
					"AND (a.door_id =" + userInfo.getOffice().getId() + " OR a.door_id IS NULL " + " OR a.door_id ='' "
							+ " OR o2.parent_ids LIKE '%" + userInfo.getOffice().getId() + "%')");
		} else {
			office.getSqlMap().put("dsf2", "LEFT JOIN sys_office office ON office.ID = u9.office_id ");// 查询存款人对应的部门parent_ids
			office.getSqlMap().put("dsf",
					"AND (a.door_id =" + userInfo.getOffice().getId() + " OR office.parent_ids LIKE '%"
							+ userInfo.getOffice().getId() + "%'" + " OR o2.parent_ids LIKE '%"
							+ userInfo.getOffice().getId() + "%')");// 查询出当机具未绑定时
																	// 但存在存款人
																	// 根据存款人所在机构
																	// 查询存款异常信息
		}
		/* 初始化开始时间和结束时间为当前时间 */
		office.setCreateTimeStart(new Date());
		office.setCreateTimeEnd(new Date());
		// 查询条件： 开始时间
		if (office.getCreateTimeStart() != null) {
			office.setSearchDateStart(DateUtils.foramtSearchDate(DateUtils.getDateStart(office.getCreateTimeStart())));
		}
		// 查询条件： 结束时间
		if (office.getCreateTimeEnd() != null) {
			office.setSearchDateEnd(DateUtils.foramtSearchDate(DateUtils.getDateEnd(office.getCreateTimeEnd())));
		}
		
		return doorOrderExceptionDao.getDoorExceptionCount(office);
		
	}

}
