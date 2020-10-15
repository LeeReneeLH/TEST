package com.coffer.businesses.modules.clear.v03.service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.coffer.businesses.common.Constant;
import com.coffer.businesses.common.utils.BusinessUtils;
import com.coffer.businesses.modules.clear.ClearCommonUtils;
import com.coffer.businesses.modules.clear.ClearConstant;
import com.coffer.businesses.modules.clear.v03.dao.OrderClearDetailDao;
import com.coffer.businesses.modules.clear.v03.dao.OrderClearMainDao;
import com.coffer.businesses.modules.clear.v03.entity.DenominationCtrl;
import com.coffer.businesses.modules.clear.v03.entity.DenominationInfo;
import com.coffer.businesses.modules.clear.v03.entity.OrderClearDetail;
import com.coffer.businesses.modules.clear.v03.entity.OrderClearMain;
import com.coffer.businesses.modules.weChat.WeChatConstant;
import com.coffer.core.common.persistence.Page;
import com.coffer.core.common.service.BusinessException;
import com.coffer.core.common.service.CrudService;
import com.coffer.core.common.utils.DateUtils;
import com.coffer.core.common.utils.StringUtils;
import com.coffer.core.modules.sys.SysCommonUtils;
import com.coffer.core.modules.sys.entity.User;
import com.coffer.core.modules.sys.utils.UserUtils;
import com.google.common.collect.Lists;

/**
 * 预约清分功能Service
 * 
 * @author wanglin
 * @version 2017-07-06
 */
@Service
@Transactional(readOnly = true)
public class OrderClearService extends CrudService<OrderClearMainDao, OrderClearMain> {
	@Autowired
	private OrderClearMainDao orderClearMainDao;

	@Autowired
	private OrderClearDetailDao orderClearDetailDao;

	@Override
	public OrderClearMain get(String inNo) {
		return super.get(inNo);
	}

	/**
	 * 
	 * @author wanglin
	 * @version 获取当日预约金额
	 * 
	 * @param orderClearMain
	 * @return
	 */
	public BigDecimal getToDayAmount(OrderClearMain orderClearMain) {

		// 登记用户所属机构
		if (StringUtils.isBlank(orderClearMain.getRegisterOffice())) {
			User user = UserUtils.getUser();
			orderClearMain.setRegisterOffice(user.getOffice().getId());
			orderClearMain.setRegisterDate(new Date());
		}
		return orderClearMainDao.getToDayAmount(orderClearMain);
	}

	/**
	 * 
	 * @author sg
	 * @version 获取未清分捆数
	 * 
	 * @param orderClearMain
	 * @return
	 */
	public OrderClearMain findClearList() {
		// 登记用户所属机构
		OrderClearMain orderClearMain = new OrderClearMain();
		User user = UserUtils.getUser();
		orderClearMain.setRegisterOffice(user.getOffice().getId());
		orderClearMain.setSearchDateStart(DateUtils.foramtSearchDate(DateUtils.getDateStart(new Date())));
		orderClearMain.setSearchDateEnd(DateUtils.foramtSearchDate(DateUtils.getDateEnd(new Date())));
		return orderClearMainDao.findClearList(orderClearMain);
	}

	/**
	 * 保存登记信息
	 * 
	 * @author wanglin
	 * @version 2017年7月7日
	 * @param allAllocateInfo
	 *            主表信息
	 */
	@Transactional(readOnly = false)
	public void save(OrderClearMain orderClearInfo) {
		int intMainResult = 0;
		// ---------------------------------------------
		// ------------预约清分主表 (CL_IN_MAIN)的做成----------
		// ---------------------------------------------
		// 状态 1：登记
		orderClearInfo.setStatus(ClearConstant.StatusType.CREATE);
		// 插入登记机构信息
		orderClearInfo.setrOffice(SysCommonUtils.findOfficeById(orderClearInfo.getrOffice().getId()));
		// 入库总金额
		orderClearInfo.setInAmount(new BigDecimal(orderClearInfo.getInAmount().toString().replace(",", "")));

		// 当前用户
		User user = UserUtils.getUser();
		// 微信申请方式用户  XL 2018-05-21 begin
		if (WeChatConstant.MethodType.METHOD_WECHAT.equals(orderClearInfo.getMethod())) {
			user =orderClearInfo.getCreateBy();
		}
		// end
		// 登记人信息设定
		orderClearInfo.setRegisterOffice(user.getOffice().getId());
		orderClearInfo.setRegisterOfficeNm(user.getOffice().getName());
		orderClearInfo.setRegisterBy(user.getId());
		orderClearInfo.setRegisterName(user.getName());
		orderClearInfo.setRegisterDate(new Date());
		// 保存
		if (StringUtils.isBlank(orderClearInfo.getInNo())) {
			// 修改业务类型  XL 2018-05-18 begin
			// 生成交款单号
			orderClearInfo.setInNo(BusinessUtils.getClearNewBusinessNo(ClearConstant.BusinessType.ORDER_CLEAR,
					orderClearInfo.getCurrentUser().getOffice()));
			// end
			// 追加
			orderClearInfo.preInsert();
			intMainResult = dao.insert(orderClearInfo);
		} else {
			// 修改
			orderClearInfo.preUpdate();
			intMainResult = dao.update(orderClearInfo);
		}

		// add qph 2017-12-04 清分预约消息设置
		List<String> params = Lists.newArrayList();
		params.add(orderClearInfo.getInNo());
		SysCommonUtils.clearMessageQueueAdd(ClearConstant.BusinessType.ORDER_CLEAR, orderClearInfo.getStatus(), params,
				orderClearInfo.getrOffice().getId(), UserUtils.getUser());

		if (intMainResult == 0) {
			String strErrMsg = "预约单号：" + orderClearInfo.getInNo();
			logger.error("预约清分主表-" + strErrMsg + ",保存失败！");
			throw new BusinessException("message.E7010", "", new String[] { orderClearInfo.getInNo() });
		}

		// ----------------------------------------------
		// ----------预约清分明细表 (CL_IN_DETAIL)的做成----------
		// ----------------------------------------------
		// 逻辑删除明细表
		OrderClearDetail orderClearDetailTemp = new OrderClearDetail();
		orderClearDetailTemp.setInNo(orderClearInfo.getInNo());
		orderClearDetailTemp.preUpdate();
		orderClearDetailDao.delete(orderClearDetailTemp);
		// 明细表做成
		for (DenominationInfo item : orderClearInfo.getDenominationList()) {
			// 待清分数和已清分数都为空的场合，不做插入
			if ((item.getColumnValue1() == null || item.getColumnValue1().equals(""))
					&& (item.getColumnValue2() == null || item.getColumnValue2().equals(""))) {
				continue;
			}

			OrderClearDetail orderClearDetail = new OrderClearDetail();

			// 主表交款单号
			orderClearDetail.setInNo(orderClearInfo.getInNo());

			// 币种(人民币)
			orderClearDetail.setCurrency(Constant.Currency.RMB);
			// 面值
			orderClearDetail.setDenomination(item.getMoneyKey());

			// 单位
			orderClearDetail.setUnitId(Constant.Unit.bundle);

			// 待清分数
			if (!("0".equals(item.getColumnValue1()))) {
				orderClearDetail.setCountDqf(item.getColumnValue1());
			}
			// 已清分数
			if (!("0".equals(item.getColumnValue2()))) {
				orderClearDetail.setCountYqf(item.getColumnValue2());
			}
			// 总数
			int intCountDqf = 0;
			if (item.getColumnValue1() != null && !item.getColumnValue1().equals("")) {
				intCountDqf = Integer.valueOf(item.getColumnValue1());
			}
			int intCountYqf = 0;
			if (item.getColumnValue2() != null && !item.getColumnValue2().equals("")) {
				intCountYqf = Integer.valueOf(item.getColumnValue2());
			}
			orderClearDetail.setTotalCount(String.valueOf(intCountDqf + intCountYqf));

			// 总金额
			orderClearDetail.setTotalAmt(new BigDecimal(item.getTotalAmt()));

			orderClearDetail.preInsert();
			// 插入明细表数据
			// 如果总数为零则不插入
			if ((intCountDqf + intCountYqf) != 0) {
				int intDetailResult = orderClearDetailDao.insert(orderClearDetail);
				if (intDetailResult == 0) {
					String strErrMsg = "预约单号：" + orderClearInfo.getInNo();
					logger.error("预约清分明细-" + strErrMsg + ",保存失败！");
					throw new BusinessException("message.E7001", "", new String[] { orderClearInfo.getInNo() });
				}
			}

		}

	}

	/**
	 * 
	 * @author wanglin
	 * @version 当页一览数据的取得
	 * 
	 * @param orderClearMain
	 * @return
	 */
	public Page<OrderClearMain> findPageList(Page<OrderClearMain> page, OrderClearMain orderClearInfo) {
		// 生成数据权限过滤条件（dsf为dataScopeFilter的简写，在xml中使用 ${sqlMap.dsf}调用权限SQL）
		orderClearInfo.getSqlMap().put("dsf", dataScopeFilter(UserUtils.getUser(), "o1", null));
		// 设置分页参数
		orderClearInfo.setPage(page);
		// 执行分页查询
		List<OrderClearMain> userList = orderClearMainDao.findList(orderClearInfo);
		page.setList(userList);
		return page;
	}

	/**
	 * 
	 * @author wanglin
	 * @version 通过机构ID查询面值合计
	 * 
	 * @param orderClearDetail
	 * @return
	 */
	public List<OrderClearDetail> findListByOffice(OrderClearMain orderClearMain) {
		// 登记用户所属机构
		if (StringUtils.isBlank(orderClearMain.getRegisterOffice())) {
			User user = UserUtils.getUser();
			orderClearMain.setRegisterOffice(user.getOffice().getId());
			orderClearMain.setRegisterDate(new Date());
		}
		return orderClearDetailDao.findListByOffice(orderClearMain);
	}
	
	/**
	 * 查询当日微信端预约信息
	 * 
	 * @author XL
	 * @version 2018-05-21
	 * @param orderClearDetail
	 * @return
	 */
	public OrderClearMain getByDateAndOffice(String orderDate, String officeId) {
		//预约清分信息
		OrderClearMain orderClearMain = new OrderClearMain();
		//设置时间
		orderClearMain.setRegisterTime(orderDate);
		//设置机构
		orderClearMain.setRegisterOffice(officeId);
		//设置申请方式
		orderClearMain.setMethod(WeChatConstant.MethodType.METHOD_WECHAT);
		//设置状态
		orderClearMain.setStatus(WeChatConstant.OrderClearStatus.REGISTER);
		//查询信息
		orderClearMain=orderClearMainDao.getByDateAndOffice(orderClearMain);
		if (orderClearMain == null || StringUtils.isBlank(orderClearMain.getInNo())) {
			//当日无信息
			orderClearMain = new OrderClearMain();
			orderClearMain.setDenominationList(ClearCommonUtils.getDenominationList());
		} else {
			// 面值列表数据的取得
			DenominationCtrl denomCtrl = new DenominationCtrl();
			denomCtrl.setMoneyKeyName("denomination");
			denomCtrl.setColumnName1("countDqf");
			denomCtrl.setColumnName2("countYqf");
			//设置列表信息
			orderClearMain.setDenominationList(
					ClearCommonUtils.getDenominationList(orderClearMain.getOrderClearDetailList(), denomCtrl));
		}
		return orderClearMain;
	}

}
