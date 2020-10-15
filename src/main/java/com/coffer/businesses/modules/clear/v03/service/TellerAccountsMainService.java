package com.coffer.businesses.modules.clear.v03.service;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.coffer.businesses.common.Constant;
import com.coffer.businesses.modules.clear.ClearConstant;
import com.coffer.businesses.modules.clear.v03.dao.TellerAccountsMainDao;
import com.coffer.businesses.modules.clear.v03.entity.TellerAccountsMain;
import com.coffer.businesses.modules.store.StoreCommonUtils;
import com.coffer.businesses.modules.store.v01.entity.StoEscortInfo;
import com.coffer.core.common.config.Global;
import com.coffer.core.common.persistence.Page;
import com.coffer.core.common.service.BusinessException;
import com.coffer.core.common.service.CrudService;
import com.coffer.core.common.utils.Collections3;
import com.coffer.core.common.utils.StringUtils;
import com.coffer.core.modules.sys.entity.Office;
import com.coffer.core.modules.sys.entity.User;
import com.coffer.core.modules.sys.utils.UserUtils;
import com.google.common.collect.Lists;

/**
 * 柜员账务Service
 * @author xl
 * @version 2017-10-23
 */
@Service
@Transactional(readOnly = true)
public class TellerAccountsMainService extends CrudService<TellerAccountsMainDao, TellerAccountsMain> {
	@Autowired
	private TellerAccountsMainDao tellerAccountsMainDao;

	public TellerAccountsMain get(String id) {
		return super.get(id);
	}
	
	public List<TellerAccountsMain> findList(TellerAccountsMain tellerAccountsMain) {
		return super.findList(tellerAccountsMain);
	}
	
	public Page<TellerAccountsMain> findPage(Page<TellerAccountsMain> page, TellerAccountsMain tellerAccountsMain) {
		return super.findPage(page, tellerAccountsMain);
	}

	/**
	 * 柜员账务列表
	 * 
	 * @author XL
	 * @version 2017年10月23日
	 * @param page
	 * @param tellerAccountsMain
	 * @return 柜员账务列表
	 */
	public Page<TellerAccountsMain> findPageForMain(Page<TellerAccountsMain> page,
			TellerAccountsMain tellerAccountsMain) {
		// 查询柜员集合
		List<StoEscortInfo> tellerList = StoreCommonUtils.findEscortInfoByUserType("",
				Global.getConfig(Constant.CLEAR_TELLER_BUSINESSTYPE));
		// 查询条件：开始时间
		tellerAccountsMain.getSqlMap().put("dsf", dataScopeFilter(tellerAccountsMain.getCurrentUser(), "o", null));
		// 设置柜员
		tellerAccountsMain.setTellerList(tellerList);
		// 设置分页对象
		tellerAccountsMain.setPage(page);
		// 柜员账务列表
		List<TellerAccountsMain> tellerAccountsList = tellerAccountsMainDao.findTellerAccountsList(tellerAccountsMain);
		page.setList(tellerAccountsList);
		return page;
	}
	
	/**
	 * 柜员账务主表添加
	 * 
	 * @author XL
	 * @version 2017年10月23日
	 * @param tellerAccountsMain
	 */
	@Transactional(readOnly = false)
	public synchronized void insertTellerAccounts(TellerAccountsMain tellerAccountsMain) {
		// 根据用户Id，金额类型查询柜员账务
		TellerAccountsMain info = this.getNewestTellerAccounts(tellerAccountsMain.getTellerBy(),
				tellerAccountsMain.getCashType());
		// 验证备付金业务是否验证柜员账务
		List<String> proList = Global.getList("accounts.businessType.pro");
		boolean flag = true;
		// 柜员余额
		BigDecimal totalAmount = new BigDecimal(0);
		if (info != null) {
			totalAmount = info.getTotalAmount();
		}
		Office office = StoreCommonUtils.getOfficeById(UserUtils.getUser().getOffice().getId());
		if ((ClearConstant.clearProvisionsOpen.CLEARPROVISIONSOPEN_FALSE.equals(office.getProvisionsSwitch())
				|| StringUtils.isEmpty(office.getProvisionsSwitch()))
				&& proList.contains(tellerAccountsMain.getBussinessType())) {
			flag = false;
		}
		// 判断出入库
		if (ClearConstant.AccountsInoutType.ACCOUNTS_OUT.equals(tellerAccountsMain.getInoutType())) {
			// 出库，验证柜员余额
			totalAmount = totalAmount.subtract(tellerAccountsMain.getOutAmount());
			if (totalAmount.compareTo(BigDecimal.ZERO) < 0) {
				if(flag){
				// 业务状态为登记
					if (ClearConstant.StatusType.CREATE.equals(tellerAccountsMain.getBussinessStatus())) {
						throw new BusinessException("message.A1011", "交接人" + tellerAccountsMain.getTellerName() + "余额不足！",
							new String[] { tellerAccountsMain.getTellerName() });
					} else {
					// 业务状态为冲正
					throw new BusinessException("message.A1012", "交接人" + tellerAccountsMain.getTellerName() + "余额不足！",
							new String[] { tellerAccountsMain.getTellerName() });
					}
				}

			}
		} else {
			// 入库
			totalAmount = totalAmount.add(tellerAccountsMain.getInAmount());
		}
		// 设置余额
		tellerAccountsMain.setTotalAmount(totalAmount);
		tellerAccountsMain.preInsert();
		// 接口调用
		if (tellerAccountsMain.getLoginUser() != null) {
			// 登陆人
			User loginUser = tellerAccountsMain.getLoginUser();
			// 设置创建人
			tellerAccountsMain.setCreateBy(loginUser);
			tellerAccountsMain.setCreateName(loginUser.getName());
			// 设置更新人
			tellerAccountsMain.setUpdateBy(loginUser);
			tellerAccountsMain.setUpdateName(loginUser.getName());
		}
		// 设置登记时间
		tellerAccountsMain.setRegisterDate(tellerAccountsMain.getCreateDate());
		dao.insert(tellerAccountsMain);
	}
	
	/**
	 * 根据用户Id，金额类型查询柜员账务
	 * 
	 * @author XL
	 * @version 2017年10月23日
	 * @param EscortInfoId
	 * @param cashType
	 * @return 柜员账务信息
	 */
	public TellerAccountsMain getNewestTellerAccounts(String EscortInfoId, String cashType) {
		TellerAccountsMain tellerAccountsMain = new TellerAccountsMain();
		// 设置柜员ID
		tellerAccountsMain.setTellerBy(EscortInfoId);
		// 设置金额类型
		tellerAccountsMain.setCashType(cashType);
		// 获取柜员账务列表
		List<TellerAccountsMain> tellerlist = dao.findList(tellerAccountsMain);
		if (!Collections3.isEmpty(tellerlist)) {
			// 返回最新一条柜员账务
			return tellerlist.get(0);
		} else {
			return null;
		}
	}

	/**
	 * 
	 * @author qipeihong
	 * @version 2017年10月25日 根据用户Id查询柜员账务（用户删除修改用）
	 * 
	 * @param EscortInfoId
	 * @return
	 */

	public List<TellerAccountsMain> getNewestTellerAccounts(String EscortInfoId) {
		List<TellerAccountsMain> tellerAccountsMainList = Lists.newArrayList();
		TellerAccountsMain tellerAccountsMainCash = new TellerAccountsMain();
		TellerAccountsMain tellerAccountsMainPro = new TellerAccountsMain();
		tellerAccountsMainCash.setTellerBy(EscortInfoId);
		// 非备付金
		tellerAccountsMainCash.setCashType(ClearConstant.CashTypeProvisions.PROVISIONS_FALSE);
		tellerAccountsMainPro.setTellerBy(EscortInfoId);
		// 备付金
		tellerAccountsMainPro.setCashType(ClearConstant.CashTypeProvisions.PROVISIONS_TRUE);
		List<TellerAccountsMain> tellerlist = dao.findList(tellerAccountsMainCash);
		List<TellerAccountsMain> tellerlistpro = dao.findList(tellerAccountsMainPro);
		if (!Collections3.isEmpty(tellerlist)) {
			tellerAccountsMainList.add(tellerlist.get(0));
		} 
		if (!Collections3.isEmpty(tellerlistpro)) {
			tellerAccountsMainList.add(tellerlistpro.get(0));
		}
		return tellerAccountsMainList;
	}

}