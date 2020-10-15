package com.coffer.businesses.modules.clear.v03.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.coffer.businesses.modules.clear.v03.dao.ClearConfirmDao;
import com.coffer.businesses.modules.clear.v03.entity.ClearConfirm;
import com.coffer.businesses.modules.collection.CollectionConstant;
import com.coffer.businesses.modules.weChat.WechatMessageUtils;
import com.coffer.core.common.persistence.Page;
import com.coffer.core.common.service.CrudService;
import com.coffer.core.modules.sys.utils.UserUtils;


/**
 * 清分接收Service
 * @author wl
 * @version 2017-02-13
 */
@Service
@Transactional(readOnly = true)
public class ClearConfirmService extends CrudService<ClearConfirmDao, ClearConfirm> {
	@Autowired
	private ClearConfirmDao clearConfirmDao;
	
	public ClearConfirm get(String id) {
		ClearConfirm clearConfirm  = super.get(id);
		return clearConfirm;
	}
	
	public List<ClearConfirm> findList(ClearConfirm clearConfirm) {
		return super.findList(clearConfirm);
	}
	
	public Page<ClearConfirm> findPage(Page<ClearConfirm> page, ClearConfirm clearConfirm) {
		return super.findPage(page, clearConfirm);
	}
	
	/**
	 * 
	 * @author wanglin
	 * @version 当页一览数据的取得
	 * 
	 * @param ClearConfirm
	 * @return
	 */
	public Page<ClearConfirm> findPageList(Page<ClearConfirm> page, ClearConfirm clearConfirm) {
		// 生成数据权限过滤条件（dsf为dataScopeFilter的简写，在xml中使用 ${sqlMap.dsf}调用权限SQL）
		clearConfirm.getSqlMap().put("dsf", dataScopeFilter(UserUtils.getUser(), "o2", null));
		// 设置分页参数
		clearConfirm.setPage(page);
		// 执行分页查询
		List<ClearConfirm> userList = super.findList(clearConfirm);
		page.setList(userList);
		return page;
	}

	/**
	 * 
	 * @author wanglin
	 * @version 2017年10月10日
	 * 预约接收
	 * @param ClearConfirm 
	 * @return
	 */
	@Transactional(readOnly = false)
	public void confirm(ClearConfirm clearConfirm) {
		//状态的更新
		//状态 (1：登记，2：接收)
		clearConfirm.setStatus(CollectionConstant.clearStatusType.CONFIRM);
		clearConfirm.preUpdate();
		clearConfirmDao.updateStatus(clearConfirm);
		// 发送模板
		WechatMessageUtils.sendTemplateToGuest(clearConfirm);
		
	}
	
	
	
}