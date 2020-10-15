package com.coffer.businesses.modules.collection.v03.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.coffer.businesses.modules.collection.v03.dao.StoreOfficeDao;
import com.coffer.businesses.modules.collection.v03.entity.StoreOffice;
import com.coffer.core.common.persistence.Page;
import com.coffer.core.common.service.BusinessException;
import com.coffer.core.common.service.CrudService;
import com.coffer.core.common.utils.Collections3;
import com.coffer.core.common.utils.StringUtils;

/**
 * 商户Service
 * 
 * @author wanglin
 * @version 2017-11-13
 */
@Service
@Transactional(readOnly = true)
public class StoreOfficeService extends CrudService<StoreOfficeDao, StoreOffice> {
	@Autowired
	private StoreOfficeDao storeOfficeDao;
	
	public Page<StoreOffice> findList(Page<StoreOffice> page, StoreOffice storeOffice) {
		Page<StoreOffice> pageData =  super.findPage(page, storeOffice);
		return pageData;
	}
	
	@Transactional(readOnly = false)
	public void save(StoreOffice storeOffice) {
		
		//商户编号重复性检查
		String strReturn = checkStoreOfficeCode(storeOffice.getCode(),storeOffice.getOldCode());
		//失败的场合
		if ("false".equals(strReturn)){
			//[保存失败]:商户编码已经存在！
			throw new BusinessException("message.E7201", "", new String[] {});
		}
		//商户记录修改且启用标识改变的场合，门店表启用标识一致性变更
		if (!storeOffice.getIsNewRecord()){
			StoreOffice  storeOfficeTemp = storeOfficeDao.get(storeOffice.getId());
			if (storeOfficeTemp != null){
				if (!storeOfficeTemp.getEnabledFlag().equals(storeOffice.getEnabledFlag())){
					storeOfficeDao.updShopEnabledFlag(storeOffice);
				}		
			}
		}

		//商户保存
		super.save(storeOffice);
	}

	@Transactional(readOnly = false)
	public void delete(StoreOffice storeOffice) {
		super.delete(storeOffice);					//商户删除
		storeOfficeDao.shopDelete(storeOffice);		//门店删除
	}

	/**
	 * 
	 * @author wanglin
	 * @version 2017年11月10日
	 * 
	 *          商户编码验证
	 * @param code
	 * @return
	 */
	public String checkStoreOfficeCode(String code, String oldCode) {
		if (StringUtils.isNotBlank(oldCode) && oldCode.equals(code)) {
			return "true";
		}
		StoreOffice storeOffice = new StoreOffice();
		storeOffice.setCode(code);
		List<StoreOffice> list = dao.checkStoreOfficeCode(storeOffice);

		if (Collections3.isEmpty(list)) {
			return "true";
		} else {
			return "false";
		}
	}

	/**
	 * 
	 * Title: getVaildCntByOfficeId
	 * <p>Description: 查询该商户和门店下的有效用户数量</p>
	 * @author:     wl
	 * @param id 商户id
	 * @return 有效用户数
	 * int    返回类型
	 */
	public int getVaildCntByOfficeId(String id) {

		return dao.getVaildCntByOfficeId(id);
	}
	
}
