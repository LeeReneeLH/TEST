package com.coffer.businesses.modules.store.v01.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.coffer.businesses.modules.store.v01.dao.StoBoxInfoDao;
import com.coffer.businesses.modules.store.v01.dao.StoBoxInfoHistoryDao;
import com.coffer.businesses.modules.store.v01.entity.StoBoxInfo;
import com.coffer.businesses.modules.store.v01.entity.StoBoxInfoHistory;
import com.coffer.core.common.utils.Collections3;

@Service
@Transactional(readOnly = false)
public class stoBoxInfoHistoryService {
	@Autowired
	private StoBoxInfoDao boxDao;
	// 添加箱袋明细的数据持久层 修改人：xp 修改时间：2017-7-6 begin
	@Autowired
	private StoBoxInfoHistoryDao HistoryDao;

	/**
	 * 更新或者添加箱子及插入箱袋明细
	 * 
	 * @author xp
	 * @version 2017-7-6
	 */
	@Transactional(readOnly = false)
	public void saveOrUpdate(StoBoxInfo box) {
		List<StoBoxInfo> stoBoxInfoList = boxDao.getBoxInfoByIdOrRfid(box);

		if (Collections3.isEmpty(stoBoxInfoList)) {
			boxDao.insert(box);
		} else {
			boxDao.update(box);
		}

	}

	/**
	 * 更新授权箱袋基本信息
	 * 
	 * @author xp
	 * @version 2017-7-12
	 */
	@Transactional(readOnly = false)
	public void insertAuthorizerInfo(StoBoxInfoHistory historyBox) {
		HistoryDao.insert(historyBox);
	}
	
	/**
	 * 更新授权箱袋基本信息
	 * 
	 * @author SongYuanYang
	 * @version 2017-9-13
	 */
	@Transactional(readOnly = false)
	public List<StoBoxInfoHistory> getStoBoxHistory(StoBoxInfoHistory stoBoxInfoHistory) {
		return HistoryDao.findListByRfidAndBoxNo(stoBoxInfoHistory);
	}
	
	/**
	 * 根据箱号物理删除箱子历史信息
	 * 
	 * @author WQJ
	 * @version 2019-1-9
	 * 
	 * @Description
	 * @param boxNo
	 * @return
	 */
	@Transactional(readOnly = false)
	public int realDeleteHistory(String boxNo) {
		//物理删除
		return HistoryDao.realDeleteHistory(boxNo);
	}
}
