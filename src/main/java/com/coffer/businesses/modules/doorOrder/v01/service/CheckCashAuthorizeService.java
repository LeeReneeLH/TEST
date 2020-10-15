package com.coffer.businesses.modules.doorOrder.v01.service;

import java.util.Collections;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.coffer.businesses.common.Constant;
import com.coffer.businesses.modules.doorOrder.DoorOrderConstant;
import com.coffer.businesses.modules.doorOrder.v01.dao.CheckCashAuthorizeDao;
import com.coffer.businesses.modules.doorOrder.v01.entity.CheckCashAuthorize;
import com.coffer.core.common.persistence.Page;
import com.coffer.core.common.service.CrudService;
import com.coffer.core.common.utils.Collections3;
import com.coffer.core.modules.sys.dao.OfficeDao;
import com.coffer.core.modules.sys.entity.Office;
import com.coffer.core.modules.sys.utils.UserUtils;
import com.google.common.collect.Lists;
/**
 * 
* Title: CheckCashAuthorizeService 
* <p>Description:授权管理service </p>
* @author HaoShijie
* @date 2020年5月6日 上午10:11:25
 */
@Service
public class CheckCashAuthorizeService  extends CrudService<CheckCashAuthorizeDao, CheckCashAuthorize> {	
	@Autowired
	private CheckCashAuthorizeDao  checkCashAuthorizeDao;
	@Autowired
	private OfficeDao  officeDao;
	
	/**
	 * 
	 * Title: getAuthorizeList
	 * <p>Description:查询授权信息列表</p>
	 * @author:     HaoShijie
	 * @param checkCashAuthorize
	 * @return 
	 * List<CheckCashAuthorize>    返回类型
	 */
	public List<CheckCashAuthorize> getAuthorizeList(CheckCashAuthorize checkCashAuthorize) {
		// 数据范围过滤
		if (UserUtils.getUser().getOffice().getType().equals(Constant.OfficeType.CLEAR_CENTER)) {
			checkCashAuthorize.getSqlMap().put("dsf",
					"AND (a.office_id =" + checkCashAuthorize.getCurrentUser().getOffice().getId()
							+ " OR o.parent_ids LIKE '%" + checkCashAuthorize.getCurrentUser().getOffice().getParentId() + "%')");
		}else{
			checkCashAuthorize.getSqlMap().put("dsf",
					"AND (a.office_id =" + checkCashAuthorize.getCurrentUser().getOffice().getId()
							+ " OR o.parent_ids LIKE '%" + checkCashAuthorize.getCurrentUser().getOffice().getId() + "%')");
		}
		// 列表查询
		List<CheckCashAuthorize> result = checkCashAuthorizeDao.getAuthorizeList(checkCashAuthorize);
		if (Collections3.isEmpty(result)) {
			result = Lists.newArrayList();
		}
		return result;
	}
	/**
	 * 
	 * Title: getAuthorizePage
	 * <p>Description:分页查询授权信息列表 </p>
	 * @author:     HaoShijie
	 * @param page
	 * @param checkCashAuthorize
	 * @return 
	 * Page<CheckCashAuthorize>    返回类型
	 */
	public Page<CheckCashAuthorize> getAuthorizePage(Page<CheckCashAuthorize> page, CheckCashAuthorize checkCashAuthorize) {
		checkCashAuthorize.setPage(page);
		page.setList(getAuthorizeList(checkCashAuthorize));
		return page;
	}
	
	
	/**
	 * 保存数据（插入或更新）
	 * 
	 * @author ZXK
	 * @version 2020-5-20
	 * @param 
	 */
	@Transactional(readOnly = false)
	public void save(CheckCashAuthorize checkCashAuthorize) {
		
		//机构类型
		String oType = checkCashAuthorize.getOfficeType();
		
		if(StringUtils.isBlank(oType)){
			oType = officeDao.get(checkCashAuthorize.getOfficeId()).getType();
			checkCashAuthorize.setOfficeType(oType);
		}
		
		if(DoorOrderConstant.AuthorizeType.REPORT_PAID.equals(checkCashAuthorize.getType())){
			checkCashAuthorize.setAmount(null);
		  }
		//根据机构类型赋予权限级别
		switch(oType){
		//油站编码
		case Constant.OfficeType.PETROL_CODE: 
			checkCashAuthorize.setAuthorizeRank(DoorOrderConstant.AuthorizeRank.PETROL_CODE_RANK);
			break;
		//门店	
		case Constant.OfficeType.STORE: 
			checkCashAuthorize.setAuthorizeRank(DoorOrderConstant.AuthorizeRank.STORE_RANK);
			break;
		//商户	
		case Constant.OfficeType.MERCHANT: 
			checkCashAuthorize.setAuthorizeRank(DoorOrderConstant.AuthorizeRank.MERCHANT_RANK);
			break;
		//公司	
		case Constant.OfficeType.COFFER: 
			checkCashAuthorize.setAuthorizeRank(DoorOrderConstant.AuthorizeRank.COFFER_RANK);
			break;
		//清分中心	
		case Constant.OfficeType.CLEAR_CENTER: 
			checkCashAuthorize.setAuthorizeRank(DoorOrderConstant.AuthorizeRank.CLEAR_CENTER_RANK);
			break;
		//区域运行商	
		case Constant.OfficeType.CENTRAL_BANK: 
			checkCashAuthorize.setAuthorizeRank(DoorOrderConstant.AuthorizeRank.CENTRAL_BANK_RANK);
			break;
		//云平台	
		case Constant.OfficeType.DIGITAL_PLATFORM: 
			checkCashAuthorize.setAuthorizeRank(DoorOrderConstant.AuthorizeRank.DIGITAL_PLATFORM_RANK);
			break;
		//顶级机构	
		case Constant.OfficeType.ROOT: 
			checkCashAuthorize.setAuthorizeRank(DoorOrderConstant.AuthorizeRank.ROOT_RANK);
			break;
		//其他默认(默认权限最低)
		default:
			checkCashAuthorize.setAuthorizeRank(DoorOrderConstant.AuthorizeRank.ROOT_RANK);
			break;
		}
		super.save(checkCashAuthorize);
	}
	
	
	/**
	 * 
	 * Title: selectDoorRestrictAmount
	 * 查询门店限制金额(参数所需: 门店id 授权类型type)
	 * @author:    ZXK
	 * @version: 2020-5-20
	 * @param checkCashAuthorize
	 * @return 
	 *    
	 */
	public CheckCashAuthorize selectDoorRestrictAmount(CheckCashAuthorize checkCashAuthorize){
		//获取门店id
		String doorId = checkCashAuthorize.getOfficeId();
		//获取门店对应层级关系集合
		List<String> lists = Lists.newArrayList();
		Office office = officeDao.get(doorId);
		String[] list = office.getParentIds().split(",");
		Collections.addAll(lists,list);
		//清分中心
		Office clear = officeDao.getClearCenterByParentId(list[3]);
		lists.add(clear.getId());
		//门店
		lists.add(doorId);
		//油站编码(根据门店Id 查询油站编码)
		/*List<Office> childList = officeDao.getOfficeByParentId(office);
		if(!Collections3.isEmpty(childList)){
			for (Office child : childList) {
				lists.add(child.getId());
			}
		}*/
		checkCashAuthorize.setLists(lists);
		
		//查询符合条件的信息列表
		List<CheckCashAuthorize> ccaList = checkCashAuthorizeDao.getListByTypeAndOffice(checkCashAuthorize);
		if(Collections3.isEmpty(ccaList)){
			ccaList.add(new CheckCashAuthorize());
		}
		return ccaList.get(0);
	}
	/**
	 * 
	 * Title: getByIdForCheckType
	 * <p>Description:根据id和类型获取授权信息 </p>
	 * @author:     HaoShijie
	 * @param checkCashAuthorize
	 * @return 
	 * CheckCashAuthorize    返回类型
	 */
	public CheckCashAuthorize getByIdForCheckType(CheckCashAuthorize checkCashAuthorize){
		return dao.getByIdForCheckType(checkCashAuthorize);
	}
	
}
