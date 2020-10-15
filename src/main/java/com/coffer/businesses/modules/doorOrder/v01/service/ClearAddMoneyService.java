package com.coffer.businesses.modules.doorOrder.v01.service;


import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import com.coffer.businesses.modules.doorOrder.DoorOrderConstant;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.coffer.businesses.modules.doorOrder.v01.dao.ClearAddMoneyDao;
import com.coffer.businesses.modules.doorOrder.v01.entity.ClearAddMoney;
import com.coffer.core.common.persistence.Page;
import com.coffer.core.common.service.CrudService;
import com.coffer.core.common.utils.Collections3;
import com.coffer.core.common.utils.DateUtils;
import com.coffer.core.common.utils.IdGen;

/**
 * 清机加钞记录Service
 *
 * @author ZXK
 * @version 2019-07-23
 */
@Service
@Transactional(readOnly = true)
public class ClearAddMoneyService extends CrudService<ClearAddMoneyDao, ClearAddMoney> {

    public ClearAddMoney get(String id) {
        return super.get(id);
    }

    /**
     * 获取清机加钞记录列表
     *
     * @param page
     * @param clearAddMoney
     * @return
     * @author ZXK
     * @version 2019年7月23日
     */
    public Page<ClearAddMoney> findPage(Page<ClearAddMoney> page, ClearAddMoney clearAddMoney) {
        // 查询条件： 开始时间
        if (clearAddMoney.getCreateTimeStart() != null) {
            clearAddMoney.setSearchDateStart(
                    DateUtils.foramtSearchDate(DateUtils.getDateStart(clearAddMoney.getCreateTimeStart())));
        }
        // 查询条件： 结束时间
        if (clearAddMoney.getCreateTimeEnd() != null) {
            clearAddMoney.setSearchDateEnd(
                    DateUtils.foramtSearchDate(DateUtils.getDateEnd(clearAddMoney.getCreateTimeEnd())));
        }

        clearAddMoney.getSqlMap().put("dsf", dataScopeFilter(clearAddMoney.getCurrentUser(), "o", null));

        return super.findPage(page, clearAddMoney);
    }

    /**
     * 根据机具编号查询单条数据
     *
     * @param equipmentId
     * @return
     * @author zhr
     */
	
	public List<ClearAddMoney> getByEquipmentId(String equipmentId,String type,String id) {
		return dao.getByEquipmentId(equipmentId,type,id);
	}
	

    /**
     * 清机加钞余额清零，门店预约设备存款冲正调用
     *
     * @param eqpId 设备ID
     * @author yinkai
     */
	@Transactional(readOnly = false)
    public void setSurplusAmountEmpty(String eqpId) {
        // 门店预约主表冲正，清机加钞记录添加，设备余额为0，类型暂时为空
        ClearAddMoney clearAddMoney = new ClearAddMoney();
        Date currentDate = new Date();
        // 按照日期逆序排序，查找当前设备的最新记录
        clearAddMoney.setEquipmentId(eqpId);
        Page<ClearAddMoney> page = new Page<>();
        page.setOrderBy("A.CREATE_DATE DESC");
        clearAddMoney.setPage(page);
        List<ClearAddMoney> clearAddMoneyList = dao.findList(clearAddMoney);
        if (!Collections3.isEmpty(clearAddMoneyList)) {
            // 插入一条余额为0的数据，表示存款被冲正
            clearAddMoney = clearAddMoneyList.get(0);
            clearAddMoney.setCount(0);
            clearAddMoney.setSurplusAmount(new BigDecimal(0));
            clearAddMoney.setAmount(new BigDecimal(0));
            clearAddMoney.setType(DoorOrderConstant.ClearStatus.DELETE);
            clearAddMoney.setCreateDate(currentDate);
            clearAddMoney.setUpdateDate(currentDate);
            clearAddMoney.setId(IdGen.uuid());
            dao.insert(clearAddMoney);
        }
    }
	
	
	/**
     * 获取存款流水列表
     *
     * @param page
     * @param clearAddMoney
     * @return
     * @author ZXK
     * @version 2020-6-2
     */
    public Page<ClearAddMoney> getDepositSerialList(Page<ClearAddMoney> page, ClearAddMoney clearAddMoney) {
        // 查询条件： 开始时间
        if (clearAddMoney.getCreateTimeStart() != null) {
            clearAddMoney.setSearchDateStart(
                    DateUtils.foramtSearchDate(DateUtils.getDateStart(clearAddMoney.getCreateTimeStart())));
        }
        // 查询条件： 结束时间
        if (clearAddMoney.getCreateTimeEnd() != null) {
            clearAddMoney.setSearchDateEnd(
                    DateUtils.foramtSearchDate(DateUtils.getDateEnd(clearAddMoney.getCreateTimeEnd())));
        }
        clearAddMoney.setPage(page);
		page.setList(dao.getDepositSerialList(clearAddMoney));
        return page;
    }
}
