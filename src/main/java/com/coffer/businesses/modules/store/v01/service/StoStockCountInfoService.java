package com.coffer.businesses.modules.store.v01.service;

import java.util.Date;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.coffer.businesses.modules.store.v01.dao.StoStockCountInfoDao;
import com.coffer.businesses.modules.store.v01.entity.StoStockCountInfo;
import com.coffer.core.common.persistence.Page;
import com.coffer.core.common.service.CrudService;
import com.coffer.core.common.utils.DateUtils;
import com.coffer.core.modules.sys.entity.User;
import com.coffer.core.modules.sys.utils.UserUtils;

/**
 * 盘点管理Service
 * @author LLF
 * @version 2015-09-22
 */
@Service
@Transactional(readOnly = true)
public class StoStockCountInfoService extends CrudService<StoStockCountInfoDao, StoStockCountInfo> {

	public StoStockCountInfo get(String id) {
		return super.get(id);
	}
	
	public List<StoStockCountInfo> findList(StoStockCountInfo stoStockCountInfo) {
		return super.findList(stoStockCountInfo);
	}
	
	public Page<StoStockCountInfo> findPage(Page<StoStockCountInfo> page, StoStockCountInfo stoStockCountInfo) {
		// 生成数据权限过滤条件（dsf为dataScopeFilter的简写，在xml中使用 ${sqlMap.dsf}调用权限SQL）
		User user = UserUtils.getUser();
		stoStockCountInfo.setOffice(user.getOffice());
		stoStockCountInfo.getSqlMap().put("dsf", dataScopeFilter(user, "o8", null));
		
		if (stoStockCountInfo != null && stoStockCountInfo.getCreateTimeStart() != null) {
			stoStockCountInfo.setCreateTimeStart(DateUtils.getDateStart(stoStockCountInfo.getCreateTimeStart()));
		} else {
			stoStockCountInfo.setCreateTimeStart(DateUtils.getDateStart(new Date()));
		}
		if (stoStockCountInfo != null && stoStockCountInfo.getCreateTimeEnd() != null) {
			stoStockCountInfo.setCreateTimeEnd(DateUtils.getDateEnd(stoStockCountInfo.getCreateTimeEnd()));
		} else {
			stoStockCountInfo.setCreateTimeEnd(DateUtils.getDateEnd(new Date()));
		}
		
		return super.findPage(page, stoStockCountInfo);
	}
	
	@Transactional(readOnly = false)
	public void save(StoStockCountInfo stoStockCountInfo) {
		super.save(stoStockCountInfo);
	}
	
	@Transactional(readOnly = false)
	public void delete(StoStockCountInfo stoStockCountInfo) {
		super.delete(stoStockCountInfo);
	}
	
	
}