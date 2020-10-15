package com.coffer.businesses.modules.store.v01.dao;

import java.util.Date;
import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.coffer.businesses.modules.store.v01.entity.StoRfidDenomination;
import com.coffer.core.common.persistence.CrudDao;
import com.coffer.core.common.persistence.annotation.MyBatisDao;

/**
 * RFID绑定面值DAO接口
 * @author yuxixuan
 * @version 2015-09-11
 */
@MyBatisDao
public interface StoRfidDenominationDao extends CrudDao<StoRfidDenomination> {

    /** 根据查询条件，查询rfid绑定信息 **/
    List<StoRfidDenomination> findRFIDList(StoRfidDenomination record);

    /** 根据查询条件，查询rfid绑定信息，机构所属的人民银行信息 **/
    List<StoRfidDenomination> findListWithStore(StoRfidDenomination record);

    /** 批量更新RFID的面值/机构等绑定信息 **/
    int updateList(StoRfidDenomination record);
    /** 批量更新RFID的面值/机构等绑定信息 （清除库区用）**/
    int updateRfidListByAtOffice(StoRfidDenomination record);

    int deleteByPrimaryKey(String rfid);

    int insert(StoRfidDenomination record);

    int insertSelective(StoRfidDenomination record);

    StoRfidDenomination selectByPrimaryKey(String rfid);

    int updateByPrimaryKeySelective(StoRfidDenomination record);

    int updateByPrimaryKey(StoRfidDenomination record);
    /**
     * 
     * Title: deleteByPrimaryKeyAndOfficeId
     * <p>Description: 按RFID和机构id删除邦定数据</p>
     * @author:     wangbaozhong
     * @param rfid
     * @param officeId 机构id
     * @return 
     * int    返回类型
     */
    public int deleteByPrimaryKeyAndOfficeId(@Param(value = "rfid") String rfid, @Param(value = "officeId") String officeId);
    
    /**
     * 
     * Title: findUnUsedListByOfficeId
     * <p>Description: 根据机构ID和业务类型 查询未使用待入库的RFID信息</p>
     * @author:     wangbaozhong
     * @param param 查询条件
     * @return RFID信息列表
     * List<StoRfidDenomination>    返回类型
     */
    public List<StoRfidDenomination> findUnUsedListByOfficeId(StoRfidDenomination param);
    
    /**
     * 
     * Title: findUsedListByOfficeId
     * <p>Description: 根据机构ID和业务类型 查询已使用入库的RFID信息</p>
     * @author:     yanbingxu
     * @param param
     * @return 
     * List<StoRfidDenomination>    返回类型
     */
    public List<StoRfidDenomination> findUsedListByOfficeId(StoRfidDenomination param);
    
    /**
     * 根据流水单号删除RFID绑定信息
     * @param allID
     * @return
     */
    public int deleteByAllID(@Param(value = "allID") String allID);
    /**
     * 
     * Title: pdaFindList
     * <p>Description: PDA根据流水号查询已绑定的RFID</p>
     * @author:     zhengkaiyuan
     * @param param 查询条件
     * @return RFID信息列表
     * List<StoRfidDenomination>    返回类型
     */
    public List<StoRfidDenomination> pdaFindList(StoRfidDenomination param);
    
    /**
     * 通过原rfid号替换成新的rfid号
     * @author caixiaojie
     * @param srcRfid 原rfid编号
     * @param dstRfid 新rfid编号
     * @param userId 更新者id
     * @param userName 更新者姓名
     * @return 更新数量
     */
    public int updateRfidByPrimaryKey(@Param(value="srcRfid") String srcRfid,@Param(value="dstRfid") String dstRfid,@Param(value="userId") String userId,@Param(value="userName") String userName,@Param(value="updateDate" ) Date updateDate);
    
    /**
     * 
     * Title: unbindingAllIdByRfid
     * <p>Description: 解除RFID流水单号绑定</p>
     * @author:     wangbaozhong
     * @param param rfid信息
     * @return 
     * int    返回类型
     */
    public int unbindingAllIdByRfid(StoRfidDenomination param);
    
    /**
     * 
     * Title: getRfidInfo
     * <p>Description: 查询rfid信息</p>
     * @author:     wangbaozhong
     * @param param 查询条件
     * @return rfid信息
     * StoRfidDenomination    返回类型
     */
    public StoRfidDenomination getRfidInfo(StoRfidDenomination param);
}