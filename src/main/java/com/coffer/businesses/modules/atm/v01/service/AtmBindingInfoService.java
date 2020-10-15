package com.coffer.businesses.modules.atm.v01.service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.coffer.businesses.common.Constant;
import com.coffer.businesses.modules.allocation.AllocationCommonUtils;
import com.coffer.businesses.modules.allocation.AllocationConstant;
import com.coffer.businesses.modules.allocation.v01.dao.AllAllocateDetailDao;
import com.coffer.businesses.modules.allocation.v01.dao.AllAllocateInfoDao;
import com.coffer.businesses.modules.allocation.v01.entity.AllAllocateDetail;
import com.coffer.businesses.modules.allocation.v01.entity.AllAllocateInfo;
import com.coffer.businesses.modules.allocation.v01.entity.AtmClearBoxInfo;
import com.coffer.businesses.modules.atm.ATMConstant;
import com.coffer.businesses.modules.atm.v01.dao.AtmBindingDetailDao;
import com.coffer.businesses.modules.atm.v01.dao.AtmBindingInfoDao;
import com.coffer.businesses.modules.atm.v01.dao.AtmPlanInfoDao;
import com.coffer.businesses.modules.atm.v01.entity.AtmBindingDetail;
import com.coffer.businesses.modules.atm.v01.entity.AtmBindingInfo;
import com.coffer.businesses.modules.atm.v01.entity.AtmInfoMaintain;
import com.coffer.businesses.modules.atm.v01.entity.AtmPlanInfo;
import com.coffer.businesses.modules.store.StoreCommonUtils;
import com.coffer.businesses.modules.store.v01.dao.StoBoxInfoDao;
import com.coffer.businesses.modules.store.v01.entity.StoBoxInfo;
import com.coffer.core.common.config.Global;
import com.coffer.core.common.persistence.Page;
import com.coffer.core.common.service.CrudService;
import com.coffer.core.common.utils.Collections3;
import com.coffer.core.common.utils.DateUtils;
import com.coffer.core.common.utils.IdGen;
import com.coffer.core.common.utils.StringUtils;
import com.coffer.core.modules.sys.service.SystemService;
import com.coffer.core.modules.sys.utils.UserUtils;
import com.google.common.collect.Lists;

/**
 * ATM绑定信息Service
 * 
 * @author XL
 * @version 2017-11-13
 */
@Service
@Transactional(readOnly = true)
public class AtmBindingInfoService extends CrudService<AtmBindingInfoDao, AtmBindingInfo> {

	@Autowired
	private AtmBindingDetailDao atmBindingDetailDao;
	@Autowired
	private AtmBindingInfoDao atmBindingInfoDao;
	@Autowired
	private AtmPlanInfoDao atmPlanInfoDao;
	@Autowired
	private AllAllocateInfoDao allocateInfoDao;
	@Autowired
	private StoBoxInfoDao stoBoxInfoDao;
	@Autowired
	private AllAllocateDetailDao allocateDetailDao;

	public AtmBindingInfo get(String id) {
		return super.get(id);
	}

	public List<AtmBindingInfo> findList(AtmBindingInfo atmBindingInfo) {
		return super.findList(atmBindingInfo);
	}

	public Page<AtmBindingInfo> findPage(Page<AtmBindingInfo> page, AtmBindingInfo atmBindingInfo) {
		page.setOrderBy("a.create_date DESC");
		atmBindingInfo.setPage(page);
		page.setList(atmBindingInfoDao.findAtmBindingList(atmBindingInfo));
		return page;
	}

	@Transactional(readOnly = false)
	public void save(AtmBindingInfo atmBindingInfo) {
		this.saveAtmBindingInfo(atmBindingInfo);
	}

	@Transactional(readOnly = false)
	public void delete(AtmBindingInfo atmBindingInfo) {
		super.delete(atmBindingInfo);
	}

	/**
	 * 加钞明细列表
	 * 
	 * @author XL
	 * @version 2017年11月13日
	 * @param page
	 * @param atmBindingDetail
	 * @return
	 */
	public Page<AtmBindingDetail> findAddPage(Page<AtmBindingDetail> page, AtmBindingInfo atmBindingInfo) {
		AtmBindingDetail atmBindingDetail = new AtmBindingDetail();
		// 设置绑定主表主键
		atmBindingDetail.setBindingId(atmBindingInfo.getId());
		// 加钞明细列表
		List<AtmBindingDetail> atmBindingDetails = atmBindingDetailDao.findList(atmBindingDetail);
		for (AtmBindingDetail addAtmBindingDetail : atmBindingDetails) {
			StoBoxInfo boxInfo = new StoBoxInfo();
			boxInfo.setId(addAtmBindingDetail.getBoxNo());
			// 钞箱
			StoBoxInfo stoBoxInfo = StoreCommonUtils.getBoxInfoByRfidAndBoxNo(boxInfo);
			// 设置加钞金额
			addAtmBindingDetail.setAddAmount(stoBoxInfo != null ? stoBoxInfo.getBoxAmount() : new BigDecimal("0"));
			// 设置加钞时间
			addAtmBindingDetail.setAddDate(atmBindingInfo.getAddDate());
		}
		page.setList(atmBindingDetails);
		return page;
	}

	/**
	 * 清机明细列表
	 * 
	 * @author XL
	 * @version 2017年11月13日
	 * @param page
	 * @param addPlanId
	 * @return
	 */
	public Page<AtmBindingDetail> findClearPage(Page<AtmBindingDetail> page,AtmBindingInfo atmBindingInfo) {
		String addPlanId = atmBindingInfo.getAddPlanId();
		String atmNo = atmBindingInfo.getAtmNo();
		Date createDate = atmBindingInfo.getCreateDate();
		// 清点明细
		List<AtmBindingDetail> atmBindingDetails = Lists.newArrayList();
		AtmBindingInfo atmBinding = new AtmBindingInfo();
		Page<AtmBindingInfo> pageInfo = new Page<AtmBindingInfo>();
		pageInfo.setOrderBy("a.create_date DESC");
		atmBinding.setPage(pageInfo);
		// 设置ATM编号
		atmBinding.setAtmNo(atmNo);
		// 设置时间
		atmBinding.setSearchDateEnd(DateUtils.foramtSearchDate(createDate));
		// 绑定主表列表
		List<AtmBindingInfo> atmBindingInfos = atmBindingInfoDao.findList(atmBinding);
		if (!Collections3.isEmpty(atmBindingInfos) && atmBindingInfos.size() >= 2) {
			AtmBindingDetail atmBindingDetail = new AtmBindingDetail();
			// 设置绑定主表主键
			atmBindingDetail.setBindingId(atmBindingInfos.get(1).getId());
			atmBindingDetails = atmBindingDetailDao.findList(atmBindingDetail);
		}
		// ATM清机钞箱信息
		List<AtmClearBoxInfo> atmClearBoxInfos = AllocationCommonUtils.getAtmClearBoxInfoByPlanId(addPlanId);
		for (AtmBindingDetail clearAtmBindingDetail : atmBindingDetails) {
			boolean flag = false;
			BigDecimal clearAmount = null;
			Date devanningDate = null;
			for (AtmClearBoxInfo atmClearBoxInfo : atmClearBoxInfos) {
				// 判断是否清机
				if (clearAtmBindingDetail.getBoxNo().equals(atmClearBoxInfo.getBoxNo())) {
					flag = true;
					clearAmount = atmClearBoxInfo.getAmount();
					devanningDate = atmClearBoxInfo.getDevanningDate();
					break;
				}
			}
			if (flag) {
				clearAtmBindingDetail.setDelFlag(Constant.deleteFlag.Invalid);
				clearAtmBindingDetail.setClearAmount(clearAmount);
				clearAtmBindingDetail.setClearDate(devanningDate);
			}
		}
		page.setList(atmBindingDetails);
		return page;
	}

	/**
	 * 保存绑定明细
	 * 
	 * @author XL
	 * @version 2017年11月13日
	 * @param atmBindingDetail
	 */
	@Transactional(readOnly = false)
	public void saveDetail(AtmBindingDetail atmBindingDetail) {
		// atmBindingDetail.preInsert();
		atmBindingDetailDao.insert(atmBindingDetail);
	}
	/**
	 * 
	 * @author wanglu
	 * @version 2017年11月15日
	 * 
	 *  修改绑定明细钞箱清点状态
	 * @param AtmBindingDetail
	 */
	public List<AtmBindingDetail> getAtmBindingDetailListByBoxNo(AtmBindingDetail atmBindingDetail){
		return atmBindingDetailDao.getAtmBindingDetailListByBoxNo(atmBindingDetail);
	}
	
	/**
	 * 
	 * @author wanglu
	 * @version 2017年11月24日
	 * 
	 *  通过bindingId获取绑定详情
	 * @param AtmBindingDetail
	 */
	public List<AtmBindingDetail> getAtmBindingDetailListByBindingId(AtmBindingDetail atmBindingDetail){
		return atmBindingDetailDao.findList(atmBindingDetail);
	}

	
	/**
	 * 保存ATM绑定信息
	 * 
	 * @author qph
	 * @version 2017年11月22日
	 * @param atmBindingInfo
	 * @return
	 */
	public void saveAtmBindingInfo(AtmBindingInfo atmBindingInfo) {
		
		boolean add = false;	//判断是新增绑定还是修改加钞绑定 

		// 原绑定明细
		List<AtmBindingDetail> oldAtmBindingDetails = Lists.newArrayList();

		// 设置主键
		if(StringUtils.isEmpty(atmBindingInfo.getBindingId())) {
			atmBindingInfo.setBindingId(IdGen.uuid());
			add = true;
			atmBindingInfo.preInsert();
			atmBindingInfo.setClearDate(atmBindingInfo.getCreateDate());
			atmBindingInfo.setAddDate(atmBindingInfo.getCreateDate());
			// 查询ATM机绑定列表
			AtmBindingInfo atmBindingInfoNewest = new AtmBindingInfo();
			// 设置ATM机编号
			atmBindingInfoNewest.setAtmNo(atmBindingInfo.getAtmNo());
			Page<AtmBindingInfo> pageInfo = new Page<AtmBindingInfo>();
			// 创建时间排序
			pageInfo.setOrderBy("a.create_date DESC");
			atmBindingInfoNewest.setPage(pageInfo);
			List<AtmBindingInfo> atmBindingInfos = this.findList(atmBindingInfoNewest);
			if (!Collections3.isEmpty(atmBindingInfos)) {
				// 获取ATM机上一条绑定钞箱信息
				atmBindingInfoNewest = atmBindingInfos.get(0);
				if (atmBindingInfoNewest != null) {
					// 原绑定钞箱列表
					oldAtmBindingDetails = atmBindingInfoNewest.getAbdL();
				}
			}
		}else {
			// 原绑定明细
			oldAtmBindingDetails = this.get(atmBindingInfo).getAbdL();
			AtmBindingDetail atmBindingDetailTemp = new AtmBindingDetail();
			atmBindingDetailTemp.setBindingId(atmBindingInfo.getBindingId());
			atmBindingDetailDao.delDetailByBindingId(atmBindingDetailTemp);	//删除旧的绑定详情
			atmBindingInfo.preUpdate();
		}
		
		BigDecimal addAmount = new BigDecimal("0");
		// 处理绑定明细
		if(atmBindingInfo.getAbdL().size() > 0) {
			// 绑定钞箱编号列表
			List<String> bindBoxNos = Lists.newArrayList();
			for (AtmBindingDetail atmBindingDetail : atmBindingInfo.getAbdL()) {
				atmBindingDetail.setBindingId(atmBindingInfo.getBindingId());
				// 设置明细主键
				atmBindingDetail.setDetailId(IdGen.uuid());
				// 设置创建更新信息
				atmBindingDetail.setCreateBy(atmBindingInfo.getCreateBy());
				atmBindingDetail.setCreateName(atmBindingInfo.getCreateName());
				atmBindingDetail.setCreateDate(atmBindingInfo.getCreateDate());
				atmBindingDetail.setUpdateBy(atmBindingInfo.getUpdateBy());
				atmBindingDetail.setUpdateName(atmBindingInfo.getUpdateName());
				atmBindingDetail.setUpdateDate(atmBindingInfo.getCreateDate());
				atmBindingDetailDao.insert(atmBindingDetail);
				// 计算加钞金额
				StoBoxInfo boxInfo = new StoBoxInfo();
				boxInfo.setId(atmBindingDetail.getBoxNo());
				StoBoxInfo stoBoxInfo = StoreCommonUtils.getBoxInfoByRfidAndBoxNo(boxInfo);
				addAmount = addAmount.add(stoBoxInfo.getBoxAmount());
				bindBoxNos.add(atmBindingDetail.getBoxNo());
			}
			if (!Collections3.isEmpty(bindBoxNos)) {
				// 更新箱袋状态
				StoreCommonUtils.updateBoxStatusBatch(bindBoxNos, Constant.BoxStatus.ATM_BOX_STATUS_USE,
						atmBindingInfo.getUpdateBy());
			}
		}
		atmBindingInfo.setAddAmount(addAmount);
		
		// 设置状态
		if(add) {
			atmBindingInfo.setStatus(ATMConstant.BindingStatus.STATUS_CREATE);
			dao.insert(atmBindingInfo);
		}else {
			atmBindingInfo.setStatus(ATMConstant.BindingStatus.STATUS_CREATE);
			dao.update(atmBindingInfo);
		}
		
		// 钞箱入库流水
		AllAllocateInfo allAllocate = new AllAllocateInfo();
		// 钞箱入库明细
		List<AllAllocateDetail> allDetailList = Lists.newArrayList();
		// 加钞计划id
		allAllocate.setRouteId(atmBindingInfo.getAddPlanId());
		// 登记状态
		allAllocate.setStatus(AllocationConstant.Status.Register);
		allAllocate.setBusinessType(Global.getConfig("businessType.allocation.out.AtmBoxIn"));
		// 钞箱入库列表
		List<AllAllocateInfo> allAllocateInfoList = allocateInfoDao.findAllocationList(allAllocate);
		if (!Collections3.isEmpty(allAllocateInfoList)) {
			allAllocate = allAllocateInfoList.get(0);
			allDetailList = allAllocate.getAllDetailList();
			// 原绑定钞箱，添加到入库明细
			List<String> oldBindBoxNos = Lists.newArrayList();
			for (AtmBindingDetail atmBindingDetail : oldAtmBindingDetails) {
				AllAllocateDetail allAllocateDetail = new AllAllocateDetail();
				// 设置主键
				allAllocateDetail.setAllDetailId(IdGen.uuid());
				// 设置rfid
				allAllocateDetail.setRfid(stoBoxInfoDao.get(atmBindingDetail.getBoxNo()).getRfid());
				// 设置钞箱类型
				allAllocateDetail.setBoxType(stoBoxInfoDao.get(atmBindingDetail.getBoxNo()).getBoxType());
				// 设置钞箱编号
				allAllocateDetail.setBoxNo(atmBindingDetail.getBoxNo());
				allAllocateDetail.setScanFlag(AllocationConstant.ScanFlag.NoScan);
				allAllocateDetail.setUpdateBy(atmBindingInfo.getUpdateBy());
				allAllocateDetail.setUpdateName(atmBindingInfo.getUpdateName());
				if (add) {
					// 新增绑定
					oldBindBoxNos.add(atmBindingDetail.getBoxNo());
				} else {
					// 修改，设置金额
					StoBoxInfo boxInfo = new StoBoxInfo();
					boxInfo.setId(atmBindingDetail.getBoxNo());
					allAllocateDetail.setAmount(StoreCommonUtils.getBoxInfoByRfidAndBoxNo(boxInfo).getBoxAmount());
				}
				// 加入明细
				allDetailList.add(allAllocateDetail);
			}
			// 更新钞箱状态
			if (!Collections3.isEmpty(oldBindBoxNos)) {
				StoreCommonUtils.updateBoxStatusBatch(oldBindBoxNos, Constant.BoxStatus.ATM_BOX_STATUS_CLEAR_IN,
						atmBindingInfo.getUpdateBy());
			}
			for (Iterator<AllAllocateDetail> iterator = allDetailList.iterator(); iterator.hasNext();) {
				AllAllocateDetail allAllocateDetail = iterator.next();
				for (AtmBindingDetail allAllocateDetailNew : atmBindingInfo.getAbdL()) {
					// 钞箱与ATM绑定，从入库明细中移除
					if (allAllocateDetail.getBoxNo().equals(allAllocateDetailNew.getBoxNo())) {
						iterator.remove();
						break;
					}
				}
			}
			allAllocate.setUpdateBy(atmBindingInfo.getUpdateBy());
			allAllocate.setUpdateName(atmBindingInfo.getUpdateName());
			allAllocate.setUpdateDate(new Date());
			// 设置登记个数
			allAllocate.setRegisterNumber(allAllocate.getAllDetailList().size());

			// 执行更新处理
			allocateInfoDao.update(allAllocate);

			// 删除已经存在的详细信息
			AllAllocateDetail detailDelete = new AllAllocateDetail();
			detailDelete.setAllocationInfo(allAllocate);
			allocateDetailDao.deleteDetailByAllId(detailDelete);

			// 插入详细表
			for (AllAllocateDetail box : allAllocate.getAllDetailList()) {
				box.setAllocationInfo(allAllocate);
				allocateDetailDao.insert(box);
			}
		}

		// 更新加钞计划状态
		AtmPlanInfo atmPlanInfo = new AtmPlanInfo();
		atmPlanInfo.setAddPlanId(atmBindingInfo.getAddPlanId());
		atmPlanInfo.setAtmNo(atmBindingInfo.getAtmNo());
		// 设置状态为计划使用
		atmPlanInfo.setStatus(ATMConstant.PlanStatus.PLAN_USE);
		atmPlanInfo.setUpdateBy(atmBindingInfo.getUpdateBy());
		atmPlanInfo.setUpdateName(atmBindingInfo.getUpdateName());
		atmPlanInfo.preUpdate();
		atmPlanInfoDao.updateStatus(atmPlanInfo);

	}
	
	/**
	 * 设备名称下拉列表
	 * @author wxz
	 * @version 2017年11月23日
	 * @param atmInfoMaintain
	 * @return
	 */
	public List<AtmInfoMaintain> findByAtmName(AtmInfoMaintain atmInfoMaintain){
		// 机构过滤
		atmInfoMaintain.getSqlMap().put("dsf", SystemService.dataScopeFilter(UserUtils.getUser(), "s", ""));
		// 获取设备名称下拉列表
		List<AtmInfoMaintain> atmInfoList = atmBindingInfoDao.findByAtmName(atmInfoMaintain);
		for(AtmInfoMaintain atm : atmInfoList){
			// 型号名称拼接 (格式 ： 型号名称：atm机编号)
			atm.setAtmTypeName(atm.getAtmTypeName() + " : " +atm.getAtmId());
		}
		return atmInfoList;
	}
	
	

}