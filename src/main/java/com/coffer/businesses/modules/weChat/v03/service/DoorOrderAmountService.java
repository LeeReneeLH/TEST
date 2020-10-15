package com.coffer.businesses.modules.weChat.v03.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.coffer.businesses.modules.weChat.v03.dao.DoorOrderAmountDao;
import com.coffer.businesses.modules.weChat.v03.entity.DoorOrderAmount;
import com.coffer.core.common.service.CrudService;

@Service
@Transactional(readOnly = true)
public class DoorOrderAmountService extends CrudService<DoorOrderAmountDao, DoorOrderAmount> {

	@Autowired
	private DoorOrderAmountDao doorOrderAmountDao;

	@Transactional
	public List<DoorOrderAmount> getAmountList(String detailId) {
		return doorOrderAmountDao.getAmountList(detailId, "01");
	}
}
