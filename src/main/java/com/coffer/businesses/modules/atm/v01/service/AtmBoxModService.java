package com.coffer.businesses.modules.atm.v01.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.coffer.businesses.common.Constant;
import com.coffer.businesses.modules.atm.v01.dao.AtmBoxModDao;
import com.coffer.businesses.modules.atm.v01.entity.AtmBoxMod;
import com.coffer.core.common.persistence.Page;
import com.coffer.core.common.service.BusinessException;
import com.coffer.core.common.service.CrudService;
import com.coffer.core.common.utils.DateUtils;

/**
 * 钞箱类型配置Service
 * 
 * @author wxz
 * @version 2017-11-03
 */
@Service
@Transactional(readOnly = true)
public class AtmBoxModService extends CrudService<AtmBoxModDao, AtmBoxMod> {

	public AtmBoxMod get(String id) {
		return super.get(id);
	}

	public List<AtmBoxMod> findList(AtmBoxMod atmBoxMod) {
		return super.findList(atmBoxMod);
	}

	public Page<AtmBoxMod> findPage(Page<AtmBoxMod> page, AtmBoxMod atmBoxMod) {
		return super.findPage(page, atmBoxMod);
	}

	@Transactional(readOnly = false)
	public void save(AtmBoxMod atmBoxMod) {
		// 保存
		super.save(atmBoxMod);
	}

	@Transactional(readOnly = false)
	public void delete(AtmBoxMod atmBoxMod) {
		// 删除
		super.delete(atmBoxMod);
	}
	
	/**
	 * ATM机维护信息表的数据一致性验证
	 * 
	 * @param atmInfoMaintain
	 * @author wxz
	 * @version 2017-11-03
	 */
	public void checkVersion(AtmBoxMod atmBoxMod) {

		// 数据一致性验证(获取数据)
		AtmBoxMod oldData = get(atmBoxMod.getId());

		//判断数据是否被删除
		if (!Constant.deleteFlag.Invalid.equals(oldData.getDelFlag())) {
			// 获取更新时间的查询结果并格式化
			String oldUpdateDate = DateUtils.formatDate(oldData.getUpdateDate(),
					Constant.Dates.FORMATE_YYYYMMDDHHMMSSSSSSSS);
			// 判断两次时间是否相等
			if (!oldUpdateDate.equals(atmBoxMod.getStrUpdateDate())) {
				throw new BusinessException("message.E4014", "", new String[] { oldData.getModName() });
			}
		} else {
			throw new BusinessException("message.E4015", "", new String[] { oldData.getModName() });
		}
	}

}