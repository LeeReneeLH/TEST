package com.coffer.businesses.modules.collection.v03.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.coffer.businesses.modules.collection.CollectionConstant;
import com.coffer.businesses.modules.collection.v03.dao.ShopOfficeDao;
import com.coffer.businesses.modules.collection.v03.dao.StoreOfficeDao;
import com.coffer.businesses.modules.collection.v03.entity.SelectItem;
import com.coffer.businesses.modules.collection.v03.entity.ShopOffice;
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
public class ShopOfficeService extends CrudService<ShopOfficeDao, ShopOffice> {
	@Autowired
	private StoreOfficeDao storeOfficeDao;
	
	public Page<ShopOffice> findList(Page<ShopOffice> page, ShopOffice storeOffice) {
		Page<ShopOffice> pageData =  super.findPage(page, storeOffice);
		return pageData;
	}
	
	@Transactional(readOnly = false)
	public void save(ShopOffice shopOffice) {
		//门店编号重复性检查
		String strReturn = checkShopOfficeCode(shopOffice.getCode(),shopOffice.getOldCode());
		//失败的场合
		if ("false".equals(strReturn)){
			//[保存失败]:门店编码已经存在！
			throw new BusinessException("message.E7202", "", new String[] {});
		}
		
		
		//门店商户逻辑关系检查
		//门店 启用标识 = 启用 的场合
		if (CollectionConstant.enabledFlagType.OK.equals( shopOffice.getEnabledFlag())){
			StoreOffice storeOfficeTemp = storeOfficeDao.get(shopOffice.getStoreId());
			if (storeOfficeTemp != null) {
				//所属商户禁用的场合
				if (CollectionConstant.enabledFlagType.NO.equals(storeOfficeTemp.getEnabledFlag())){
					//该门店所属商户被禁用，请先启用！
					throw new BusinessException("message.I7231", "", new String[] {});
				}
			}
		}
		//门店保存
		super.save(shopOffice);
	}

	@Transactional(readOnly = false)
	public void delete(ShopOffice office) {
		super.delete(office);
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
	public String checkShopOfficeCode(String code, String oldCode) {
		if (StringUtils.isNotBlank(oldCode) && oldCode.equals(code)) {
			return "true";
		}
		ShopOffice office = new ShopOffice();
		office.setCode(code);
		List<ShopOffice> list = dao.checkShopOfficeCode(office);

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
	 * 
	 * Title: getVaildCntByOfficeId
	 * <p>Description: 查询该门店下的有效用户数量</p>
	 * @author:     wl
	 * @param id 商户id
	 * @return 有效用户数
	 * int    返回类型
	 */
	public int getVaildCntByOfficeId(String id) {

		return dao.getVaildCntByOfficeId(id);
	}
	
	
}
