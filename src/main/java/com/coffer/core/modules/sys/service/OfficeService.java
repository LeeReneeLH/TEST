package com.coffer.core.modules.sys.service;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.coffer.businesses.modules.doorOrder.DoorOrderConstant;
import com.coffer.businesses.modules.doorOrder.v01.dao.DoorDayReportGuestDao;
import com.coffer.businesses.modules.doorOrder.v01.entity.DoorDayReportGuest;
import com.coffer.businesses.modules.store.StoreCommonUtils;
import com.coffer.businesses.modules.weChat.WeChatConstant;
import com.coffer.core.common.config.Global;
import com.coffer.core.common.service.BusinessException;
import com.coffer.core.common.service.TreeService;
import com.coffer.core.common.utils.Collections3;
import com.coffer.core.common.utils.StringUtils;
import com.coffer.core.modules.sys.dao.OfficeDao;
import com.coffer.core.modules.sys.entity.Office;
import com.coffer.core.modules.sys.utils.UserUtils;
import com.google.common.collect.Lists;

/**
 * 机构Service
 * 
 * @author Clark
 * @version 2015-05-13
 */
@Service
@Transactional(readOnly = true)
public class OfficeService extends TreeService<OfficeDao, Office> {

	@Autowired
	private OfficeDao officeDao;
	@Autowired
	private DoorDayReportGuestDao dayReportGuestDao;

	public List<Office> findByParentIdsLike(Office office) {
		return dao.findByParentIdsLike(office);
	}

	public List<Office> findAll() {
		return UserUtils.getOfficeList();
	}

	public List<Office> findList(Boolean isAll) {
		if (isAll != null && isAll) {
			return UserUtils.getOfficeAllList();
		} else {
			return UserUtils.getOfficeList();
		}
	}

	public List<Office> findList(Office office) {
		List<Office> list = Lists.newArrayList();
		List<Office> sourcelist = dao.findByParentIdsLike(office);
		String parentId = null;
		if (office.getParent() != null) {
			parentId = office.getParent().getId();
		}
		Office.sortList(list, sourcelist, parentId);
		return list;
	}

	public List<Office> findListBySearch(Office office) {
		return dao.findListBySearch(office);
	}

	@Transactional(readOnly = false)
	public void save(Office office) {
		/** 修改机构信息时,判断该机构是否有未完成的业务，若有，不允许修改。 WQJ 2020-1-14 START **/
		if (!StringUtils.isBlank(office.getId())) {
			// 细化修改通过条件，只要不是改机构层级的，都可以修改
			if (!StoreCommonUtils.getOfficeById(office.getId()).getParentId().equals(office.getParentId())) {
				DoorDayReportGuest dayReportGuest = new DoorDayReportGuest();
				List<String> accountsTypeList = Lists.newArrayList();
				accountsTypeList.add(DoorOrderConstant.AccountsType.ACCOUNTS_DOOR);
				// 客户总金额
				BigDecimal guestTotalAmount = new BigDecimal(0);
				// 设置客户日结表账务类型
				dayReportGuest.setAccountsTypes(accountsTypeList);
				// 设置客户ID
				dayReportGuest.setClientId(office.getId());
				// 设置账务发生机构
				// 上次日结机构为商户
				dayReportGuest.setOffice(
						StoreCommonUtils.getOfficeById(StoreCommonUtils.getOfficeById(office.getId()).getParentId()));
				// 获取客户日结
				List<DoorDayReportGuest> dayReportList = dayReportGuestDao.getAccountByAccountsType(dayReportGuest);
				if (!Collections3.isEmpty(dayReportList)) {
					// 将客户日结余额设置为余额
					guestTotalAmount = dayReportList.get(0).getTotalAmount();
					if (guestTotalAmount.compareTo(BigDecimal.ZERO) != 0) {
						throw new BusinessException("message.A1048", "", new String[] {});
					}
				}
			}
		}
		/** END **/
		if (StringUtils.isBlank(office.getId())) {
			office.setIsNewRecord(true);
			// 取得最大机构ID
			String id = Global.getConfig("officeId.init");
			Integer maxId = dao.getMaxOfficeId();
			if (maxId != null) {
				id = (maxId + 1) + "";
			}
			office.setId(id);
		}
		super.save(office);
		UserUtils.removeCache(UserUtils.CACHE_OFFICE_LIST);
	}

	@Transactional(readOnly = false)
	public void delete(Office office) {
		super.delete(office);
		UserUtils.removeCache(UserUtils.CACHE_OFFICE_LIST);
	}

	/**
	 * 
	 * @author LLF
	 * @version 2015年11月10日
	 * 
	 *          机构编码验证
	 * @param code
	 * @return
	 */
	public String checkOfficeCode(String code, String oldCode) {
		if (StringUtils.isNotBlank(oldCode) && oldCode.equals(code)) {
			return "true";
		}
		Office office = new Office();
		office.setCode(code);
		List<Office> list = dao.checkOfficeCode(office);

		if (Collections3.isEmpty(list)) {
			return "true";
		} else {
			return "false";
		}
	}

	/**
	 * 
	 * Title: getClearCenterByParentId
	 * <p>
	 * Description: 根据父机构ID查询清分中心
	 * </p>
	 * 
	 * @author: wangbaozhong
	 * @param parentsId
	 * @return Office 返回类型
	 */
	public Office getClearCenterByParentId(String parentId) {
		return dao.getClearCenterByParentId(parentId);
	}

	/**
	 * 
	 * Title: findOfficeListForInterface
	 * <p>
	 * Description:按照参数条件查询本机构及其下属机构信息列表
	 * </p>
	 * 
	 * @author: wangbaozhong
	 * @param officeId       本机构ID
	 * @param parentIds      本机构的所有父ID
	 * @param lastSearchDate 上次查询日期
	 * @return List<Office> 返回类型
	 */
	@Transactional(readOnly = true)
	public List<Office> findOfficeListForInterface(String officeId, String parentIds, String lastSearchDate) {
		return dao.findOfficeListForInterface(officeId, parentIds, lastSearchDate, Global.getConfig("jdbc.type"));
	}

	/**
	 * 
	 * @author wanglin
	 * @version 2017年04月19日
	 * 
	 *          获取门店机构信息
	 * @param Office
	 * @return list
	 */
	public List<Office> findOfficeByType(Office office) {
		// 将机构类型设置为门店
		office.setType(WeChatConstant.officetype.TYPE_STORE);
		// 获取所有门店信息
		return dao.findListBySearch(office);

	}

	/**
	 * 
	 * @author sg
	 * @version 2017年09月18日
	 * 
	 *          获取系统机构
	 * @param officeId
	 * @param officeParentId
	 * @param type
	 * @return list
	 */

	public List<Office> findCustList(String officeId, String officeParentId, List<String> list) {

		return dao.findCustList(officeId, officeParentId, list,Global.getConfig("jdbc.type"));

	}

	/**
	 * 
	 * Title: getVaildCntByOfficeId
	 * <p>
	 * Description: 查询该机构和子机构下的有效用户数量
	 * </p>
	 * 
	 * @author: wanghan
	 * @param id 机构id
	 * @return 有效用户数 int 返回类型
	 */
	public int getVaildCntByOfficeId(String id) {

		return dao.getVaildCntByOfficeId(id);
	}

	/**
	 * 根据父机构id取得下属所有人行机构
	 * 
	 * @author SongYuanYang
	 * @version 2018年3月15日
	 * 
	 *          获取人行机构信息
	 * @param Office
	 * @return list
	 */
	public List<Office> findPbocByParentId(String parentId) {
		// 获取所有人行机构信息
		return dao.findPbocByParentId(parentId);
	}

	/**
	 * 根据机构id取得机构名称
	 * 
	 * @author SongYuanYang
	 * @version 2018年3月15日
	 * 
	 *          获取机构名称
	 * @param officeId
	 * @return String
	 */
	public String getOfficeNameById(String officeId) {
		return dao.getOfficeNameById(officeId);
	}

	/**
	 * 
	 * Title: findDoorList
	 * <p>
	 * Description: 查询清分中心上级人行下所有门店
	 * </p>
	 * 
	 * @version 2019年7月4日
	 * @author: lihe
	 * @return List<Office> 返回类型
	 */
	public List<Office> findDoorList(Office office) {
		return dao.findDoorList(office);
	}

	/**
	 * @version 2019年7月9日
	 * @author: zxk
	 */
	public List<Office> selectStore() {
		return dao.selectStore();
	}

	/**
	 * 获取数字化金融服务平台机构
	 * 
	 * @version 2019年7月10日
	 * @author: wqj
	 */
	public List<Office> getPlatform() {
		return dao.getPlatform();
	}

	/**
	 * 分页查询机构信息
	 * 
	 * @author xp
	 */
	public List<Office> findListByPage(Office office) {
		/** 机构过滤除去当前登录人机构的同级机构显示 lihe start **/
		Office office2 = UserUtils.getUser().getOffice();
		if (office2.getId().equals(office.getId())) {
			office.setParentIds(office.getParentIds() + office.getId());
		}
		/** 判断此时分页查询的状态 0：不带模糊查询分页 1：带模糊查询分页 hzy start **/
		if (office.getPage().getSearchType() == 1) {
			office.setParentIds(office2.getParentIds() + office2.getId());
			//office.setType(null);
			return officeDao.findPageListBySearch(office);
		} else {
			return officeDao.findByParentIdsLike(office);
		}
		/** 判断此时分页查询的状态 0：不带模糊查询分页 1：带模糊查询分页 hzy end **/
		/** 机构过滤除去当前登录人机构的同级机构显示 lihe end **/
		// List<Office> list = officeDao.findByParentIdsLike(office);
		/** 注释掉原版本代码 start **/
		// List<Office> list = Lists.newArrayList();
		// String parentId = UserUtils.getUser().getOffice().getParentId();
		// if (office.getParent() != null) {
		// parentId = office.getParent().getId();
		// }
		// Office.sortList(list, sourcelist, parentId);
		/** end **/
		// return list;
	}

	/**
	 * 根据条件分页查询机构信息
	 * 
	 * @author xp
	 */
	public List<Office> findPageListBySearch(Office office) {
		return dao.findPageListBySearch(office);
	}

	/**
	 * 异步查询子机构信息
	 * 
	 * @param
	 * @return
	 */
	public List<Office> findListByAsync(String pId, String dbName) {
		return dao.findByParentId(pId, dbName);
	}

}
