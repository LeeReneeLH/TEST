/**
 * @author WangBaozhong
 * @version 2016年5月13日
 * 
 * 
 */
package com.coffer.businesses.modules.store.v01.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.coffer.businesses.common.Constant;
import com.coffer.businesses.modules.store.v01.dao.StoAreaSettingInfoDao;
import com.coffer.businesses.modules.store.v01.dao.StoGoodsLocationInfoDao;
import com.coffer.businesses.modules.store.v01.entity.StoAreaSettingInfo;
import com.coffer.businesses.modules.store.v01.entity.StoGoodsLocationInfo;
import com.coffer.core.common.service.CrudService;
import com.coffer.core.common.utils.DateUtils;
import com.coffer.core.common.utils.IdGen;
import com.coffer.core.common.utils.StringUtils;
import com.coffer.core.modules.sys.dao.OfficeDao;
import com.coffer.core.modules.sys.entity.Office;
import com.coffer.core.modules.sys.utils.UserUtils;
import com.google.common.collect.Lists;

/**
 * 库房区域设定Service
 * @author WangBaozhong
 *
 */
@Service
@Transactional(readOnly = true)
public class StoAreaSettingInfoService extends CrudService<StoAreaSettingInfoDao, StoAreaSettingInfo> {
	private static final String[] areaTopArray = {"A", "B", "C", "D", "E" ,"F" ,"G" ,"H", "J", "K", "L", "M", "N", "O", "P","Q", "R","S", "T","U", "V", "W", "X", "Y", "Z"};
	
	@Autowired
	private StoGoodsLocationInfoDao locatioinInfoDao;
	
	@Autowired
	private OfficeDao officeDao;
	
	/**
	 * 根据机构ID查询库区位置信息列表
	 * @author WangBaozhong
	 * @version 2016年5月17日
	 * 
	 *  
	 * @param office 机构
	 * @param storeAreaType 库区类型
	 * @return 库区位置信息列表
	 */
	public List<List<StoAreaSettingInfo>> findAreaStoreInfoByOfficeId(Office office, String storeAreaType) {
		
		StoAreaSettingInfo param = new StoAreaSettingInfo();
		// 用户所属机构为数字化金融平台时查询出所有下属人行数据
		if (Constant.OfficeType.DIGITAL_PLATFORM.equals(param.getCurrentUser().getOffice().getType())) {
			// 取得下属人行机构id
			List<String> officeIdList = Lists.newArrayList();
			office.setParentIds(office.getParentIds() + office.getId());
			for (Office item : officeDao.findByParentIdsLike(office)) {
				officeIdList.add(item.getId());
			}
			UserUtils.clearCache();
			param.setOfficeIdList(officeIdList);
		} else {
			// 设定所属机构ID
			param.setOfficeId(office.getId()); 
		}
		// 设定库区类型
		param.setStoreAreaType(storeAreaType); 
		List<StoAreaSettingInfo> infoList = super.findList(param);
		
		int preRowNum = 0;
		boolean isFirst = true;
		List<StoAreaSettingInfo> colList = null;
		List<List<StoAreaSettingInfo>> rowList = new ArrayList<List<StoAreaSettingInfo>>();
		for (StoAreaSettingInfo tempInfo : infoList) {
			
			if (isFirst) {
				isFirst = false;
				preRowNum = tempInfo.getxPosition();
				colList = new ArrayList<StoAreaSettingInfo>();
				rowList.add(colList);
			}
			
			if (preRowNum != tempInfo.getxPosition()) {
				colList = new ArrayList<StoAreaSettingInfo>();
				rowList.add(colList);
			}

			StoGoodsLocationInfo goodsLocationInfo = locatioinInfoDao.findAreaStoreStatisticsInfoByAreaID(tempInfo.getId());
			
			if (goodsLocationInfo != null) {
				if (tempInfo.getMaxSaveDays() == 0) {
					tempInfo.setSlamCode(Constant.SlamCode.SLAM_COLOR_NONE);
				} else {
					tempInfo.setSlamCode(this.getSlamCode(goodsLocationInfo.getAreaMinDate(), tempInfo.getMaxSaveDays(), tempInfo.getMinSaveDays()));
				}
				tempInfo.setStoNum(goodsLocationInfo.getStoNum());
				tempInfo.setAreaMaxDate(goodsLocationInfo.getAreaMaxDate());
				tempInfo.setAreaMinDate(goodsLocationInfo.getAreaMinDate());
			} else {
				tempInfo.setSlamCode(Constant.SlamCode.SLAM_COLOR_NONE);
				tempInfo.setStoNum(0l);
			}
			
			if(Constant.deleteFlag.Invalid.equals(tempInfo.getDelFlag())) {
				tempInfo.setSlamCode(Constant.SlamCode.SLAM_COLOR_INVILD);
			}
			
			colList.add(tempInfo);
			
			preRowNum = tempInfo.getxPosition();
		}
		
		return rowList;
	}
	
	/**
	 * 根据库区内物品最大保存日数与最小保存日数和当前日期比较，判断返回预警样式
	 * @author WangBaozhong
	 * @version 2016年5月17日
	 * 
	 *  
	 * @param saveDate 物品保存日期
	 * @param areaMaxSlamDays 最大保存日数
	 * @param areaMixSlamDays 最小保存日数
	 * @return 预警样式
	 */
	private String getSlamCode(Date saveDate, int areaMaxSlamDays, int areaMixSlamDays) {
		if (saveDate != null) {
			Date maxSlamDay = DateUtils.addDate(saveDate, areaMaxSlamDays);
			Date minSlamDay = DateUtils.addDate(saveDate, areaMixSlamDays);
			Date nowDate = new Date();
			if (DateUtils.compareDate(nowDate, maxSlamDay) >= 0) {
				return Constant.SlamCode.SLAM_COLOR_RED;
			} else if (DateUtils.compareDate(nowDate, minSlamDay) >= 0 && DateUtils.compareDate(nowDate, maxSlamDay) < 0) {
				return Constant.SlamCode.SLAM_COLOR_YELLOW;
			}
		}
		return Constant.SlamCode.SLAM_COLOR_NONE;
		
	}
	/**
	 * 保存库房区域
	 * @author WangBaozhong
	 * @version 2016年5月13日
	 * 
	 *  
	 * @param stoAreaSettingInfo 库房区域设置参数
	 */
	@Transactional(readOnly=false)
	public void saveAreaSettingInfo(StoAreaSettingInfo stoAreaSettingInfo) {
		
		int iMaxRowCnt = stoAreaSettingInfo.getRowCnt();
		int iMaxColCnt = stoAreaSettingInfo.getColCnt();
		int iCellCnt = 0;
		// 库区序号
		int iStoreNo = StringUtils.isBlank(stoAreaSettingInfo.getStoreAreaType()) ? 1 : Integer.parseInt(stoAreaSettingInfo.getStoreAreaType());
		// 判断库区序号是否大于26字母大小，如果大于则重新计算
		iStoreNo = iStoreNo > areaTopArray.length ? iStoreNo % areaTopArray.length : iStoreNo;
		// 设定库区首字母
		String strAreaPreName = areaTopArray[iStoreNo - 1];
		for(int iRowIndex = 1; iRowIndex <= iMaxRowCnt; iRowIndex++) {
			
			for (int iColIndex = 1; iColIndex <= iMaxColCnt; iColIndex++) {
				iCellCnt ++;
				StoAreaSettingInfo tempInfo = new StoAreaSettingInfo();
				tempInfo.setId(IdGen.uuid());							// 主键ID
				tempInfo.setStoreAreaName(strAreaPreName + iCellCnt);	//库区名称
				tempInfo.setStoreAreaType(stoAreaSettingInfo.getStoreAreaType()); //库区类型
				tempInfo.setOfficeId(stoAreaSettingInfo.getOfficeId());	//归属机构ID
				tempInfo.setxPosition(iRowIndex);						//行位置
				tempInfo.setyPosition(iColIndex);						//列位置
				tempInfo.setRowCnt(iMaxRowCnt);							//最大行数
				tempInfo.setColCnt(iMaxColCnt);							// 最列数
				tempInfo.setMaxCapability(stoAreaSettingInfo.getMaxCapability());	//最大容量
				tempInfo.setMaxSaveDays(stoAreaSettingInfo.getMaxSaveDays());		//最大保存日数
				tempInfo.setMinSaveDays(stoAreaSettingInfo.getMinSaveDays());		//最小保存日数
				tempInfo.setIsNewRecord(true);
				save(tempInfo);
			}
		}
	}
	
	/**
	 * 按所属机构ID查询库房区域
	 * @author WangBaozhong
	 * @version 2016年5月13日
	 * 
	 *  
	 * @param officeId 机构ID
	 * @return 所属机构的库房区域列表
	 */
	public List<StoAreaSettingInfo> findListByOfficeId(String officeId) {
		StoAreaSettingInfo param = new StoAreaSettingInfo();
		param.setOfficeId(officeId);
		return findList(param);
	}
	
	/**
	 * 设定某区域无效
	 * @author WangBaozhong
	 * @version 2016年5月13日
	 * 
	 *  
	 * @param storeAreaId 库房区域ID
	 * @return 更新数量
	 */
	@Transactional(readOnly=false)
	public void delete(String storeAreaId) {
		StoAreaSettingInfo param = new StoAreaSettingInfo();
		param.setId(storeAreaId);
		delete(param);
	}
	
	/**
	 * 更新某区域
	 * @author WangBaozhong
	 * @version 2016年5月13日
	 * 
	 *  
	 * @param param 更新参数
	 * @return 更新数量
	 */
	@Transactional(readOnly=false)
	public void updateByStoreAreaId(StoAreaSettingInfo param) {
		param.setIsNewRecord(false);
		save(param);
	}
	
	/**
	 * 根据库房区域ID取得区域信息
	 * @author WangBaozhong
	 * @version 2016年5月13日
	 * 
	 *  
	 * @param storeAreaId 库房区域ID
	 * @return 库房区域信息
	 */
	public StoAreaSettingInfo getByStoreAreaId(String storeAreaId) {
		return dao.getByStoreAreaId(storeAreaId);
	}
	
	/**
	 * 根据机构ID和库区类型删除所有区域信息
	 *
	 * @author WangBaozhong
	 * @version 2016年5月16日
	 * 
	 *  
	 * @param officeId 机构ID
	 * @param storeAreaType 库区类型
	 */
	@Transactional(readOnly = false)
	public void deleteAllByOfficeIdAndAreaType(String officeId, String storeAreaType) {
		dao.deleteAllByOfficeIdAndAreaType(officeId, storeAreaType);
	}
	
	/**
	 * 查询库区使用量
	 * @author WangBaozhong
	 * @version 2016年5月19日
	 * 
	 *  
	 * @param stoAreaSettingInfo 查询条件
	 * @return 库区使用量
	 */
	public List<StoAreaSettingInfo> findAreaActualStorageList(StoAreaSettingInfo stoAreaSettingInfo) {
		return dao.findAreaActualStorageList(stoAreaSettingInfo);
	}
	
	/**
     * 取得所有库区和库区内物品信息
     * @author chengshu
     * @version 2016年5月19日
     * 
     *  
     * @param areaSettingInfo 查询条件
     * @return 库区和库区内物品信息
     */
    public List<StoAreaSettingInfo> findAreaAndGoodsNumList(StoAreaSettingInfo areaSettingInfo) {
        return dao.findAreaAndGoodsNumList(areaSettingInfo);
    }
}
