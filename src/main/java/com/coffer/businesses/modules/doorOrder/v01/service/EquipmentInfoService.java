package com.coffer.businesses.modules.doorOrder.v01.service;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.coffer.businesses.common.Constant;
import com.coffer.businesses.modules.doorOrder.v01.dao.EquipmentInfoDao;
import com.coffer.businesses.modules.doorOrder.v01.entity.EquipmentInfo;
import com.coffer.businesses.modules.weChat.v03.dao.DoorOrderInfoDao;
import com.coffer.businesses.modules.weChat.v03.entity.DoorOrderInfo;
import com.coffer.core.common.persistence.Page;
import com.coffer.core.common.service.BusinessException;
import com.coffer.core.common.service.CrudService;
import com.coffer.core.common.utils.Collections3;
import com.coffer.core.common.utils.DateUtils;
import com.coffer.core.common.utils.StringUtils;
import com.coffer.core.modules.sys.entity.Office;
import com.coffer.core.modules.sys.entity.User;
import com.coffer.core.modules.sys.utils.UserUtils;
import com.google.common.collect.Lists;

/**
 * 机具管理Service
 *
 * @author 机具管理
 * @version 2019-06-26
 */
@Service
@Transactional(readOnly = true)
public class EquipmentInfoService extends CrudService<EquipmentInfoDao, EquipmentInfo> {

    @Autowired
    private EquipmentInfoDao equipmentInfoDao;

    @Autowired
    private DoorOrderInfoDao doorOrderInfoDao;

    /**
     * 连线异常
     */
    private static String[] connStatusException = {"05"};

    /**
     * 其他连线状态
     */
    private static String[] connStatusElse = {"01", "02", "03", "04"};

    public EquipmentInfo get(String id) {
        return super.get(id);
    }

    /**
	 * <p>
	 * Description: 获取机具列表
	 * </p>
	 * 
	 * @param equipmentInfo
	 * @return List<EquipmentInfo> 返回类型
	 */
    public List<EquipmentInfo> findList(EquipmentInfo equipmentInfo) {
        return super.findList(equipmentInfo);
    }

    /**
	 * <p>
	 * Description: 获取机具列表分页
	 * </p>
	 * 
	 * @param page,equipmentInfo
	 * @return Page<EquipmentInfo> 返回类型
	 */
    public Page<EquipmentInfo> findPage(Page<EquipmentInfo> page, EquipmentInfo equipmentInfo) {
        if (StringUtils.isNotBlank(equipmentInfo.getFirstFlag())) {
            List<String> connStatus = Lists.newArrayList();
            // 设置连线状态
            if (Constant.deleteFlag.Valid.equals(equipmentInfo.getFirstFlag())) {
                connStatus = Arrays.asList(connStatusElse);
            }
            if (Constant.deleteFlag.Invalid.equals(equipmentInfo.getFirstFlag())) {
                connStatus = Arrays.asList(connStatusException);
            }
            // 设置连线状态列表
            equipmentInfo.setConnStatusList(connStatus);
            equipmentInfo.setStatus(Constant.EquipmentStatus.BIND);
        }
        if (StringUtils.isNotBlank(equipmentInfo.getClientFlag())) {
            equipmentInfo.setpOfficeId(UserUtils.getUser().getOffice().getId());
            equipmentInfo.setStatus(Constant.EquipmentStatus.BIND);
        } else if (StringUtils.isNotBlank(equipmentInfo.getDoorFlag())) {
            equipmentInfo.setaOffice(UserUtils.getUser().getOffice());
            equipmentInfo.setStatus(Constant.EquipmentStatus.BIND);
        }
        if (StringUtils.isNotBlank(UserUtils.getUser().getOffice().getType())) {

            switch (UserUtils.getUser().getOffice().getType()) {
                case Constant.OfficeType.CLEAR_CENTER:
                    equipmentInfo.setVinOffice(UserUtils.getUser().getOffice());
                    break;
                case Constant.OfficeType.MERCHANT:
                    equipmentInfo.setpOfficeId(UserUtils.getUser().getOffice().getId());
                    break;
                case Constant.OfficeType.STORE:
                    equipmentInfo.setaOffice(UserUtils.getUser().getOffice());
                    break;
                default:
                    equipmentInfo.getSqlMap().put("dsf", dataScopeFilter(equipmentInfo.getCurrentUser(), "b", null));
            }
        }
        // 查询条件： 开始时间
        if (equipmentInfo.getCreateTimeStart() != null) {
            equipmentInfo.setSearchDateStart(
                    DateUtils.foramtSearchDate(DateUtils.getDateStart(equipmentInfo.getCreateTimeStart())));
        }
        // 查询条件： 结束时间
        if (equipmentInfo.getCreateTimeEnd() != null) {
            equipmentInfo.setSearchDateEnd(
                    DateUtils.foramtSearchDate(DateUtils.getDateEnd(equipmentInfo.getCreateTimeEnd())));
        }
        return super.findPage(page, equipmentInfo);
    }
    
    
    /**
	 * <p>
	 * Description: 获取机具列表
	 * </p>
	 * 
	 * @param page,equipmentInfo
	 * @return Page<EquipmentInfo> 返回类型
	 */
    public List<EquipmentInfo> findEqpList(EquipmentInfo equipmentInfo) {
        if (StringUtils.isNotBlank(equipmentInfo.getFirstFlag())) {
            List<String> connStatus = Lists.newArrayList();
            // 设置连线状态
            if (Constant.deleteFlag.Valid.equals(equipmentInfo.getFirstFlag())) {
                connStatus = Arrays.asList(connStatusElse);
            }
            if (Constant.deleteFlag.Invalid.equals(equipmentInfo.getFirstFlag())) {
                connStatus = Arrays.asList(connStatusException);
            }
            // 设置连线状态列表
            equipmentInfo.setConnStatusList(connStatus);
            equipmentInfo.setStatus(Constant.EquipmentStatus.BIND);
        }
        if (StringUtils.isNotBlank(equipmentInfo.getClientFlag())) {
            equipmentInfo.setpOfficeId(UserUtils.getUser().getOffice().getId());
            equipmentInfo.setStatus(Constant.EquipmentStatus.BIND);
        } else if (StringUtils.isNotBlank(equipmentInfo.getDoorFlag())) {
            equipmentInfo.setaOffice(UserUtils.getUser().getOffice());
            equipmentInfo.setStatus(Constant.EquipmentStatus.BIND);
        }
        if (StringUtils.isNotBlank(UserUtils.getUser().getOffice().getType())) {

            switch (UserUtils.getUser().getOffice().getType()) {
                case Constant.OfficeType.CLEAR_CENTER:
                    equipmentInfo.setVinOffice(UserUtils.getUser().getOffice());
                    break;
                case Constant.OfficeType.MERCHANT:
                    equipmentInfo.setpOfficeId(UserUtils.getUser().getOffice().getId());
                    break;
                case Constant.OfficeType.STORE:
                    equipmentInfo.setaOffice(UserUtils.getUser().getOffice());
                    break;
                default:
                    equipmentInfo.getSqlMap().put("dsf", dataScopeFilter(equipmentInfo.getCurrentUser(), "b", null));
            }
        }
        // 查询条件： 开始时间
        if (equipmentInfo.getCreateTimeStart() != null) {
            equipmentInfo.setSearchDateStart(
                    DateUtils.foramtSearchDate(DateUtils.getDateStart(equipmentInfo.getCreateTimeStart())));
        }
        // 查询条件： 结束时间
        if (equipmentInfo.getCreateTimeEnd() != null) {
            equipmentInfo.setSearchDateEnd(
                    DateUtils.foramtSearchDate(DateUtils.getDateEnd(equipmentInfo.getCreateTimeEnd())));
        }
        return super.findList(equipmentInfo);
    }


    /**
	 * <p>
	 * Description: 机具解绑及绑定
	 * </p>
	 * 
	 * @param equipmentInfo
	 * @return void 返回类型
	 */
    @Transactional(readOnly = false)
    public void save(EquipmentInfo equipmentInfo) {
        // 绑定、解绑门店同时对维护机构字段做同样操作
        EquipmentInfo info = equipmentInfoDao.get(equipmentInfo.getId());
        if (Constant.EquipmentStatus.BIND.equals(equipmentInfo.getStatus())) {
            if (info.getaOffice() != null && !StringUtils.isEmpty(info.getaOffice().getId())) {
                throw new BusinessException("message.E7216", "", new String[]{});
            }
            User user = UserUtils.getUser();
            Office userOffice = user.getOffice();
            equipmentInfo.setVinOffice(userOffice);
        } else if (Constant.EquipmentStatus.NOBIND.equals(equipmentInfo.getStatus())) {
            // 设备当前有余额，不允许解绑
            Map<String, Object> equipmentBalanceInfo = doorOrderInfoDao.getEquipmentBalanceInfo(equipmentInfo.getId());
            if (equipmentBalanceInfo != null) {
                throw new BusinessException("message.E7215", "", new String[]{});
            }
            // 设备当前处于'在用'状态, 不允许解绑
            List<DoorOrderInfo> list = doorOrderInfoDao.getEquipmentStatus(equipmentInfo.getId());
            if(!Collections3.isEmpty(list)){
            	throw new BusinessException("message.E7218", "", new String[]{});
            }
            
            equipmentInfo.getaOffice().setId("");
            equipmentInfo.getaOffice().setName("");
        }
        super.save(equipmentInfo);
    }

    /**
	 * <p>
	 * Description: 删除机具（暂不用）
	 * </p>
	 * 
	 * @param equipmentInfo
	 * @return void 返回类型
	 */
    @Transactional(readOnly = false)
    public void delete(EquipmentInfo equipmentInfo) {
        super.delete(equipmentInfo);
    }

    /**
	 * <p>
	 * Description: 门店已绑定机具列表查询
	 * </p>
	 * 
	 * @param id
	 * @return List<EquipmentInfo> 返回类型
	 */
    public List<EquipmentInfo> checkDoorBinded(String id) {
        return equipmentInfoDao.checkDoorBinded(id);
    }

    /**
     * <p>
     * Description：获取上商户下所有设备信息列表
     * </p>
     *
     * @author yinkai
     * @param office 商户
     * @return 设备列表
     */
    public List<EquipmentInfo> findEquipmentByMerchant(Office office) {
        return equipmentInfoDao.findEquipmentByMerchant(office);
    }

}