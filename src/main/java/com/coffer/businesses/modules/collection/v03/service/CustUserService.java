package com.coffer.businesses.modules.collection.v03.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.coffer.businesses.modules.collection.v03.dao.CheckCashAmountDao;
import com.coffer.businesses.modules.collection.v03.dao.CustUserDao;
import com.coffer.businesses.modules.collection.v03.entity.CustUser;
import com.coffer.businesses.modules.collection.v03.entity.SelectItem;
import com.coffer.businesses.modules.collection.v03.entity.StoreOffice;
import com.coffer.businesses.modules.store.v01.entity.StoEscortInfo;
import com.coffer.core.common.persistence.Page;
import com.coffer.core.common.service.CrudService;
import com.coffer.core.common.utils.Collections3;
import com.coffer.core.common.utils.StringUtils;

/**
 * 客户Service
 * 
 * @author wanglin
 * @version 2017-11-13
 */
@Service
@Transactional(readOnly = true)
public class CustUserService extends CrudService<CustUserDao, CustUser> {
	@Autowired
	private CustUserDao custUserDao;
	
	public Page<CustUser> findList(Page<CustUser> page, CustUser storeOffice) {
		Page<CustUser> pageData =  super.findPage(page, storeOffice);
		return pageData;
	}
	
	@Transactional(readOnly = false)
	public void save(CustUser custUser) {
		super.save(custUser);
	}

	@Transactional(readOnly = false)
	public void delete(CustUser custUser) {
		super.delete(custUser);
	}

	/**
	 * 
	 * @author wanglin
	 * @version 2017年11月10日
	 * 
	 *          客户编码验证
	 * @param code
	 * @return
	 */
	public String checkCustUserCode(String code, String oldCode) {
		if (StringUtils.isNotBlank(oldCode) && oldCode.equals(code)) {
			return "true";
		}
		CustUser office = new CustUser();
//		office.setCode(code);
		List<CustUser> list = dao.checkCustUserCode(office);

		if (Collections3.isEmpty(list)) {
			return "true";
		} else {
			return "false";
		}
	}

	/**
	 * 商户下拉列表
	 * 
	 * @return
	 */
	public List<SelectItem> findStoreSelect(StoreOffice storeOffice) {
		return dao.findStoreSelect(storeOffice);
	}

	/**
	 * 根据登录名获取用户
	 * 
	 * @param loginName
	 * @return
	 */
	public CustUser getUserByLoginName(String loginName) {
		CustUser custUser = new CustUser();
		custUser.setLoginName(loginName);
		return custUserDao.getUserByLoginName(custUser);
	}
	
	
	
	/**
	 * 验证身份证号信息是否存在
	 * 
	 * @param loginName
	 * @return
	 */
	public String checkIdcardNo(String id, String oldIdcardNo, String idcardNo) {
		if (StringUtils.isBlank(oldIdcardNo) && StringUtils.isBlank(idcardNo)) {
			return "true";
		}
		// 身份信息无变化
		if (oldIdcardNo.equals(idcardNo)) {
			return "true";
		}
		CustUser custUser = new CustUser();
		if (StringUtils.isNotBlank(id)) {
			// 变更：取得身份信息
			custUser.setId(id);
			custUser.setIdcardNo(idcardNo);
			CustUser custUserTemp = custUserDao.findByIdcardNo(custUser);
			// 判断当前身份信息是否存在
			if (custUserTemp == null) {
				return "true";
			}
		} else {
			// 创建：取得身份信息
			custUser.setIdcardNo(idcardNo);
			CustUser custUserTemp = custUserDao.findByIdcardNo(custUser);
			// 判断身份信息是否存在
			if (custUserTemp == null) {
				return "true";
			}
		}

		return "false";
	}
	
	
	
	
}
