package com.coffer.businesses.modules.clear.v03.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.coffer.businesses.common.Constant;
import com.coffer.businesses.modules.clear.ClearConstant;
import com.coffer.businesses.modules.clear.v03.dao.CustomerClearanceDao;
import com.coffer.businesses.modules.clear.v03.entity.ClTaskMain;
import com.coffer.businesses.modules.clear.v03.entity.CustomerClearance;
import com.coffer.businesses.modules.report.ReportGraphConstant;
import com.coffer.businesses.modules.store.StoreCommonUtils;
import com.coffer.core.common.persistence.Page;
import com.coffer.core.common.service.BusinessException;
import com.coffer.core.common.service.CrudService;
import com.coffer.core.common.utils.StringUtils;
import com.coffer.core.modules.sys.SysCommonUtils;
import com.coffer.core.modules.sys.entity.Office;
import com.coffer.core.modules.sys.utils.UserUtils;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

/**
 * 客户清分量Service
 * 
 * @author wzj
 * @version 2017-09-06
 */
@Service
@Transactional(readOnly = true)
public class CustomerClearanceService extends CrudService<CustomerClearanceDao, CustomerClearance> {

	@Autowired
	private CustomerClearanceDao customerClearanceDao;

	public CustomerClearance get(String outNo) {
		return super.get(outNo);
	}

	/**
	 * 分页
	 * 
	 * @author wzj
	 * @version 2017-09-27
	 * @param customerClearance
	 * @param page
	 * @return 分页客户清分量信息页面表
	 */
	public Page<CustomerClearance> findPageList(Page<CustomerClearance> page, CustomerClearance customerClearance) {
		// 进行数据查询和分页
		customerClearance.setPage(page);

		List<CustomerClearance> customerClearancePageList = findCustomerList(customerClearance);
		page.setList(customerClearancePageList);
		return page;
	}

	/**
	 * 根据查询条件，查询客户清分量信息
	 * 
	 * @author wzj
	 * @version 2017-09-27
	 * @param customerClearance
	 * @param page
	 * @return 查询客户清分量信息页面表
	 */
	public List<CustomerClearance> findCustomerList(CustomerClearance customerClearance) {
		List<String> type = Lists.newArrayList();
		// 商行取款
		type.add(ClearConstant.BusinessType.BANK_GET);
		// 从人行复点入库
		type.add(ClearConstant.BusinessType.PEOPLE_BANK_COMPLEX_POINT_IN);
		// 代理上缴
		type.add(ClearConstant.BusinessType.AGENCY_PAY);
		customerClearance.setBusinessTypes(type);
		// 设置为登记状态
		customerClearance.setStatus(ClearConstant.StatusType.CREATE);
		if (Constant.OfficeType.CLEAR_CENTER.equals(UserUtils.getUser().getOffice().getType())
				|| Constant.OfficeType.DIGITAL_PLATFORM.equals(UserUtils.getUser().getOffice().getType())) {
			// 数据穿透
			customerClearance.getSqlMap().put("dsf", dataScopeFilter(customerClearance.getCurrentUser(), "o", null));
		}
		List<CustomerClearance> customerClearanceAllDate = customerClearanceDao
				.findListCustomerClearance(customerClearance);
		return customerClearanceAllDate;
	}

	/**
	 * 查询客户清点量统计图信息并返回成map对应前端表格
	 * 
	 * @author wzj
	 * @version 2017-10-27
	 * @param customerClearance
	 * @return Map<String, Object>
	 */
	public Map<String, Object> findListCustomerQuantity(CustomerClearance customerClearance) {

		Map<String, Object> rtnMap = Maps.newHashMap();
		// 表格标签list
		List<String> legendDataList = Lists.newArrayList();
		// x轴list
		List<String> xAxisDataList = Lists.newArrayList();
		// 数据lsit
		List<List<String>> dateList = Lists.newArrayList();
		// 已清分数据list
		List<String> yqfDateList = Lists.newArrayList();
		// 未清分数据list
		List<String> dqfDateList = Lists.newArrayList();
		// atm数据list
		List<String> atmDateList = Lists.newArrayList();
		// 完整币数据list
		List<String> wzqDateList = Lists.newArrayList();
		// 残损币数据list
		List<String> csqDateList = Lists.newArrayList();
		// 从人行复点入库数据list
		List<String> countDateList = Lists.newArrayList();
		List<CustomerClearance> resultDateList = findListDate(customerClearance, xAxisDataList);
		String busType = "";
		// 添加数据
		for (CustomerClearance date : resultDateList) {
			busType = date.getBusType();
			countDateList.add(date.getCount());
			yqfDateList.add(date.getCountYqf());
			dqfDateList.add(date.getCountDqf());
			atmDateList.add(date.getCountAtm());
			wzqDateList.add(date.getCountWzq());
			csqDateList.add(date.getCountCsq());
		}
		// 人民银行
		if (busType.equals("1")) {
			dateList.add(countDateList);
			legendDataList.add("已复点");
		} else {
			// 商业银行
			dateList.add(yqfDateList);
			dateList.add(dqfDateList);
			dateList.add(atmDateList);
			dateList.add(wzqDateList);
			dateList.add(csqDateList);
			legendDataList.add("已清分");
			legendDataList.add("未清分");
			legendDataList.add("ATM");
			legendDataList.add("完整币");
			legendDataList.add("残损币");
		}
		// 对应报表表头X轴和每个X轴对应的数据
		rtnMap.put(ReportGraphConstant.DataGraphList.LEGEND_DATE_LIST, legendDataList);
		rtnMap.put(ReportGraphConstant.DataGraphList.XAXIS_DATE_LIST, xAxisDataList);
		rtnMap.put(ReportGraphConstant.DataGraphList.SERIES_DATE_LIST, dateList);
		return rtnMap;
	}

	/**
	 * 查询客户清分量统计信息
	 * 
	 * @author wzj
	 * @version 2017-10-19
	 * @param customerClearance
	 * @param List<String>
	 *            xAxisDataList
	 * @return List<CustomerClearance>
	 */
	public List<CustomerClearance> findListDate(CustomerClearance customerClearance, List<String> xAxisDataList) {
		List<CustomerClearance> CustomerClearanceDateList = Lists.newArrayList();
		// 业务类型添加
		List<String> type = Lists.newArrayList();
		// 商行取款
		type.add(ClearConstant.BusinessType.BANK_GET);
		// 从人行复点入库
		type.add(ClearConstant.BusinessType.PEOPLE_BANK_COMPLEX_POINT_IN);
		// 代理上缴
		type.add(ClearConstant.BusinessType.AGENCY_PAY);
		customerClearance.setBusinessTypes(type);
		if (Constant.OfficeType.CLEAR_CENTER.equals(UserUtils.getUser().getOffice().getType())
				|| Constant.OfficeType.DIGITAL_PLATFORM.equals(UserUtils.getUser().getOffice().getType())) {
			// qph add2017-11-24 数据穿透
			customerClearance.getSqlMap().put("dsf", dataScopeFilter(customerClearance.getCurrentUser(), "o", null));
		}
		// 银行类型
		String bankType = "";
		// 银行名称
		String bankName = "";
		if (Constant.OfficeType.CLEAR_CENTER.equals(UserUtils.getUser().getOffice().getType())
				|| Constant.OfficeType.DIGITAL_PLATFORM.equals(UserUtils.getUser().getOffice().getType())) {
			// 客户名称为空
			if (StringUtils.isBlank(customerClearance.getCustNo())) {
				// 设置第一个选项为默认项
				List<Office> officeList = StoreCommonUtils.getStoCustList("1,3", false);
				// bankName = officeList.get(0).getName();
				// customerClearance.setCustName(bankName);
				bankName = officeList.get(0).getId();
				customerClearance.setCustNo(bankName);
				bankType = officeList.get(0).getType();
			} else {
				// 根据选择获得名称和类型
				Office nameOffice = SysCommonUtils.findOfficeById(customerClearance.getCustNo());
				// bankName = nameOffice.getName();
				// customerClearance.setCustName(bankName);
				bankName = nameOffice.getId();
				customerClearance.setCustNo(bankName);
				bankType = nameOffice.getType();
			}
		} else {
			// 根据选择获得名称和类型
			Office nameOffice = SysCommonUtils.findOfficeById(UserUtils.getUser().getOffice().getId());
			bankName = nameOffice.getId();
			customerClearance.setCustNo(bankName);
			bankType = nameOffice.getType();
		}
		// 设置为登记状态
		customerClearance.setStatus(ClearConstant.StatusType.CREATE);
		List<CustomerClearance> resultList = customerClearanceDao.findListCustomerQuantity(customerClearance);
		// x轴时间
		List<String> listTime = Lists.newArrayList();
		for (CustomerClearance getTime : resultList) {
			if (!listTime.contains(getTime.getDates())) {
				listTime.add(getTime.getDates());
			}
		}
		// 数据为空
		if (listTime.size() == 0) {
			CustomerClearance customerClearanceNull = new CustomerClearance();
			// 这个是输入银行对应的银行类型,不是业务类型 (中国银行为1)
			customerClearanceNull.setBusType(bankType);
			customerClearanceNull.setCustNo(bankName);
			CustomerClearanceDateList.add(customerClearanceNull);
		}
		for (String time : listTime) {
			// 一天的总金额
			BigDecimal totalAmountYqf = new BigDecimal(0);
			BigDecimal totalAmountDqf = new BigDecimal(0);
			BigDecimal totalAmountAtm = new BigDecimal(0);
			BigDecimal totalAmountWzq = new BigDecimal(0);
			BigDecimal totalAmountCsq = new BigDecimal(0);
			BigDecimal totalAmountCount = new BigDecimal(0);
			for (CustomerClearance entity : resultList) {
				ClTaskMain clTaskMain = new ClTaskMain();
				// 币种
				clTaskMain.setCurrency("101");
				// 类别
				clTaskMain.setClassification("02");
				// 套别
				clTaskMain.setSets("5");
				// 现金材质
				clTaskMain.setCash("1");
				// 单位
				clTaskMain.setUnit("101");
				// 面值
				clTaskMain.setDenomination(entity.getDenomination());
				// 获取面值对应的捆数金额
				BigDecimal denominationValue = getGoodsValue(clTaskMain);
				//面值为20的捆数金额
				BigDecimal num = new BigDecimal("20000");
				// 日期对应数据
				if (time.equals(entity.getDates())) {
					// 商行交款和代理上缴
					if (entity.getBusType().equals(ClearConstant.BusinessType.BANK_GET)
							|| entity.getBusType().equals(ClearConstant.BusinessType.AGENCY_PAY)) {
						// 已清分
						String yqf = entity.getCountYqf();
						// 未清分
						String dqf = entity.getCountDqf();
						// atm
						String atm = entity.getCountAtm();
						// 完整币
						String wzq = entity.getCountWzq();
						// 残损币
						String csq = entity.getCountCsq();
						BigDecimal csqCount = new BigDecimal(0);
						BigDecimal yqfCount = new BigDecimal(0);
						BigDecimal dqfCount = new BigDecimal(0);
						BigDecimal atmCount = new BigDecimal(0);
						BigDecimal wzqCount = new BigDecimal(0);
						// 判断是否为空
						if (StringUtils.isNotBlank(yqf)) {
							yqfCount = new BigDecimal(yqf);
						}
						if (StringUtils.isNotBlank(dqf)) {
							dqfCount = new BigDecimal(dqf);
						}
						if (StringUtils.isNotBlank(atm)) {
							atmCount = new BigDecimal(atm);
						}
						if (StringUtils.isNotBlank(wzq)) {
							wzqCount = new BigDecimal(wzq);
						}
						if (StringUtils.isNotBlank(csq)) {
							csqCount = new BigDecimal(csq);
						}
						// 将面值和数量进行乘
						BigDecimal yqfMoney = denominationValue.multiply(yqfCount);
						BigDecimal dfqMoney = denominationValue.multiply(dqfCount);
						// 判断面值大于20以上的
						BigDecimal atmMoney = new BigDecimal(0);
						if (denominationValue.compareTo(num) == 1 || denominationValue.compareTo(num) == 0) {
							atmMoney = denominationValue.multiply(atmCount);
						}
						BigDecimal wzqMoney = denominationValue.multiply(wzqCount);
						BigDecimal csqMoney = denominationValue.multiply(csqCount);
						// 将金额进行累加
						totalAmountYqf = totalAmountYqf.add(yqfMoney);
						totalAmountDqf = totalAmountDqf.add(dfqMoney);
						totalAmountAtm = totalAmountAtm.add(atmMoney);
						totalAmountWzq = totalAmountWzq.add(wzqMoney);
						totalAmountCsq = totalAmountCsq.add(csqMoney);
					} else {
						// 人行复点入库
						// 人行金额
						/* 修改为yqf wzj 2017-11-21 begin */
						String count = entity.getCountYqf();
						/* end */
						BigDecimal peopleCount = new BigDecimal(0);
						if (StringUtils.isNotBlank(count)) {
							peopleCount = new BigDecimal(count);
						}
						// 将面值和数量进行乘
						BigDecimal peopleMoney = denominationValue.multiply(peopleCount);
						// 将金额累加
						totalAmountCount = totalAmountCount.add(peopleMoney);
					}
				}
				// 放入x轴数据
				if (!xAxisDataList.contains(time)) {
					xAxisDataList.add(time);
				}
			}
			// 将数据放入实体类中
			CustomerClearance customerClearanceQuantity = new CustomerClearance();
			customerClearanceQuantity.setDates(time);
			// 这个是输入银行对应的银行类型,不是业务类型 (中国银行为1)
			customerClearanceQuantity.setBusType(bankType);
			customerClearanceQuantity.setCustNo(bankName);
			customerClearanceQuantity.setCountYqf(totalAmountYqf.toString());
			customerClearanceQuantity.setCountDqf(totalAmountDqf.toString());
			customerClearanceQuantity.setCountAtm(totalAmountAtm.toString());
			customerClearanceQuantity.setCountWzq(totalAmountWzq.toString());
			customerClearanceQuantity.setCountCsq(totalAmountCsq.toString());
			customerClearanceQuantity.setCount(totalAmountCount.toString());
			// 将数据放入list中
			CustomerClearanceDateList.add(customerClearanceQuantity);
		}
		return CustomerClearanceDateList;
	}

	/**
	 * 获取goodsId
	 * 
	 * @author wzj
	 * @version 2017年10月16日
	 * @param clTaskMain
	 * @return goodsId
	 */
	public String getclTaskMainMapKey(ClTaskMain clTaskMain) {
		StringBuilder goodsId = new StringBuilder();
		// 币种
		goodsId.append(StringUtils.isEmpty(clTaskMain.getCurrency()) ? "" : clTaskMain.getCurrency());
		// 类别
		goodsId.append(StringUtils.isEmpty(clTaskMain.getClassification()) ? "" : clTaskMain.getClassification());
		// 套别
		goodsId.append(StringUtils.isEmpty(clTaskMain.getSets()) ? "" : clTaskMain.getSets());
		// 现金材质
		goodsId.append(StringUtils.isEmpty(clTaskMain.getCash()) ? "" : clTaskMain.getCash());
		// 面值
		goodsId.append(StringUtils.isEmpty(clTaskMain.getDenomination()) ? "" : clTaskMain.getDenomination());
		// 单位
		goodsId.append(StringUtils.isEmpty(clTaskMain.getUnit()) ? "" : clTaskMain.getUnit());

		return goodsId.toString();
	}

	/**
	 * 根据goodsId获取对应价值
	 * 
	 * @author wzj
	 * @version 2017年10月16日
	 * @param goodsId
	 * @return totalAmt
	 */
	public BigDecimal getGoodsValue(ClTaskMain clTaskMain) {
		// 获取goodsId
		String goodsId = this.getclTaskMainMapKey(clTaskMain);
		// 获取物品价值
		BigDecimal goodsValue = StoreCommonUtils.getGoodsValue(goodsId);

		if (goodsValue == null) {
			String strMessageContent = "物品：" + goodsId + "不存在，请与管理员联系！";
			throw new BusinessException("message.A1009", strMessageContent, new String[] { goodsId });
		}

		return goodsValue;
	}

	/**
	 * 根据查询条件，查询客户清分量信息(导出表格第一页)
	 * 
	 * @author wzj
	 * @version 2017-11-21
	 * @param customerClearance
	 * @return 查询客户清分量信息页面表
	 */
	public List<CustomerClearance> findCustomerAllList(CustomerClearance customerClearance) {
		List<String> type = Lists.newArrayList();
		// 商行取款
		type.add(ClearConstant.BusinessType.BANK_GET);
		// 从人行复点入库
		type.add(ClearConstant.BusinessType.PEOPLE_BANK_COMPLEX_POINT_IN);
		// 代理上缴
		type.add(ClearConstant.BusinessType.AGENCY_PAY);
		customerClearance.setBusinessTypes(type);
		// 设置为登记状态
		customerClearance.setStatus(ClearConstant.StatusType.CREATE);
		if (Constant.OfficeType.CLEAR_CENTER.equals(UserUtils.getUser().getOffice().getType())
				|| Constant.OfficeType.DIGITAL_PLATFORM.equals(UserUtils.getUser().getOffice().getType())) {
			// qph add2017-11-24 数据穿透
			customerClearance.getSqlMap().put("dsf", dataScopeFilter(customerClearance.getCurrentUser(), "o", null));
		}
		List<CustomerClearance> customerClearanceAllDate = customerClearanceDao
				.findListCustomerClearanceAll(customerClearance);
		return customerClearanceAllDate;
	}

	/**
	 * 根据查询条件，查询客户清分量总数量(导出表格用)
	 * 
	 * @author wzj
	 * @version 2017-12-26
	 * @param customerClearance
	 * @return
	 */
	public CustomerClearance findAllCount(CustomerClearance customerClearance) {
		List<String> type = Lists.newArrayList();
		// 商行取款
		type.add(ClearConstant.BusinessType.BANK_GET);
		// 从人行复点入库
		type.add(ClearConstant.BusinessType.PEOPLE_BANK_COMPLEX_POINT_IN);
		// 代理上缴
		type.add(ClearConstant.BusinessType.AGENCY_PAY);
		customerClearance.setBusinessTypes(type);
		// 设置为登记状态
		customerClearance.setStatus(ClearConstant.StatusType.CREATE);
		if (Constant.OfficeType.CLEAR_CENTER.equals(UserUtils.getUser().getOffice().getType())
				|| Constant.OfficeType.DIGITAL_PLATFORM.equals(UserUtils.getUser().getOffice().getType())) {
			// qph add2017-11-24 数据穿透
			customerClearance.getSqlMap().put("dsf", dataScopeFilter(customerClearance.getCurrentUser(), "o", null));
		}
		CustomerClearance customerClearanceAllCount = customerClearanceDao.findAllCount(customerClearance);
		return customerClearanceAllCount;
	}

	/**
	 * 根据查询条件，查询客户清分量总数量(导出表格第一页)
	 * 
	 * @author wzj
	 * @version 2017-12-27
	 * @param customerClearance
	 * @return 查询客户清分量信息页面表
	 */
	public CustomerClearance findCountFirstSheet(CustomerClearance customerClearance) {
		List<String> type = Lists.newArrayList();
		// 商行取款
		type.add(ClearConstant.BusinessType.BANK_GET);
		// 从人行复点入库
		type.add(ClearConstant.BusinessType.PEOPLE_BANK_COMPLEX_POINT_IN);
		// 代理上缴
		type.add(ClearConstant.BusinessType.AGENCY_PAY);
		customerClearance.setBusinessTypes(type);
		// 设置为登记状态
		customerClearance.setStatus(ClearConstant.StatusType.CREATE);
		if (Constant.OfficeType.CLEAR_CENTER.equals(UserUtils.getUser().getOffice().getType())
				|| Constant.OfficeType.DIGITAL_PLATFORM.equals(UserUtils.getUser().getOffice().getType())) {
			// qph add2017-11-24 数据穿透
			customerClearance.getSqlMap().put("dsf", dataScopeFilter(customerClearance.getCurrentUser(), "o", null));
		}
		CustomerClearance customerClearanceAllCountDate = customerClearanceDao.findCountFirstSheet(customerClearance);
		return customerClearanceAllCountDate;
	}
}