package com.coffer.businesses.modules.allocation.v02.service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.coffer.businesses.common.Constant;
import com.coffer.businesses.common.utils.BusinessUtils;
import com.coffer.businesses.modules.allocation.AllocationConstant;
import com.coffer.businesses.modules.allocation.v02.dao.AllAllocateGoodsAreaDetailDao;
import com.coffer.businesses.modules.allocation.v02.dao.PbocAllAllocateDetailDao;
import com.coffer.businesses.modules.allocation.v02.dao.PbocAllAllocateInfoDao;
import com.coffer.businesses.modules.allocation.v02.dao.PbocAllAllocateItemsDao;
import com.coffer.businesses.modules.allocation.v02.entity.AllAllocateGoodsAreaDetail;
import com.coffer.businesses.modules.allocation.v02.entity.PbocAllAllocateDetail;
import com.coffer.businesses.modules.allocation.v02.entity.PbocAllAllocateInfo;
import com.coffer.businesses.modules.allocation.v02.entity.PbocAllAllocateItem;
import com.coffer.businesses.modules.allocation.v02.entity.PbocApprovalPrintDetail;
import com.coffer.businesses.modules.store.StoreCommonUtils;
import com.coffer.businesses.modules.store.StoreConstant;
import com.coffer.businesses.modules.store.v01.entity.ChangeStoreEntity;
import com.coffer.businesses.modules.store.v01.entity.StoGoodSelect;
import com.coffer.businesses.modules.store.v01.entity.StoRfidDenomination;
import com.coffer.businesses.modules.store.v02.entity.PbocStoStoresInfo;
import com.coffer.businesses.modules.store.v02.entity.StoOriginalBanknote;
import com.coffer.core.common.config.Global;
import com.coffer.core.common.persistence.Page;
import com.coffer.core.common.service.BusinessException;
import com.coffer.core.common.service.CrudService;
import com.coffer.core.common.utils.Collections3;
import com.coffer.core.common.utils.IdGen;
import com.coffer.core.common.utils.NumToRMB;
import com.coffer.core.common.utils.StringUtils;
import com.coffer.core.modules.act.service.ActTaskService;
import com.coffer.core.modules.sys.SysCommonUtils;
import com.coffer.core.modules.sys.entity.Office;
import com.coffer.core.modules.sys.entity.User;
import com.coffer.core.modules.sys.utils.DbConfigUtils;
import com.coffer.core.modules.sys.utils.UserUtils;
import com.coffer.external.Parameter;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

/**
 * 人行调拨主表管理Service
 * 
 * @author LLF
 * @version 2016-05-25
 */
@Service
@Transactional(readOnly = true)
public class PbocAllAllocateInfoService extends CrudService<PbocAllAllocateInfoDao, PbocAllAllocateInfo> {

	@Autowired
	private PbocAllAllocateItemsDao pbocAllAllocateItemsDao;

	@Autowired
	private PbocAllAllocateInfoDao pbocAllAllocateInfoDao;

	@Autowired
	private AllAllocateGoodsAreaDetailDao goodsAreaDetailDao;

	@Autowired
	PbocAllAllocateDetailDao allocateDetailDao;

	@Autowired
	private AllAllocateGoodsAreaDetailDao allocateGoodsAreaDetailDao;
	
	@Autowired
	private ActTaskService actTaskService;

	@Override
	public PbocAllAllocateInfo get(String allId) {
		return super.get(allId);
	}

	@Override
	public List<PbocAllAllocateInfo> findList(PbocAllAllocateInfo pbocAllAllocateInfo) {
		return super.findList(pbocAllAllocateInfo);
	}

	@Override
	public Page<PbocAllAllocateInfo> findPage(Page<PbocAllAllocateInfo> page, PbocAllAllocateInfo pbocAllAllocateInfo) {
		if (StringUtils.isBlank(page.getOrderBy())) {
			page.setOrderBy("a.update_date desc");
		}
		return super.findPage(page, pbocAllAllocateInfo);
	}

	@Override
	@Transactional(readOnly = false)
	public void save(PbocAllAllocateInfo pbocAllAllocateInfo) {
		super.save(pbocAllAllocateInfo);
	}

	/**
	 * 保存申请审批信息
	 * 
	 * @author WangBaozhong
	 * @version 2016年6月8日
	 * 
	 * 
	 * @param pbocAllAllocateInfo
	 *            申请审批信息
	 */
	@Transactional(readOnly = false)
	public void savePbocAllAllocateInfo(PbocAllAllocateInfo pbocAllAllocateInfo) {
		if (StringUtils.isBlank(pbocAllAllocateInfo.getAllId())) {
			// 主键空的场合，设置主键
			if (AllocationConstant.BusinessType.PBOC_APPLICATION_HANDIN.equals(pbocAllAllocateInfo.getBusinessType())) {
				// 申请上缴
				pbocAllAllocateInfo.setInoutType(AllocationConstant.InoutType.In);
				pbocAllAllocateInfo.setAllId(
						BusinessUtils.getPbocNewBusinessNo(Global.getConfig("businessType.allocation.pboc.cashHandin"),
								pbocAllAllocateInfo.getLoginUser().getOffice()));
				// 非金融平台用户登录时设置登记机构为本机构
				if (!Constant.OfficeType.DIGITAL_PLATFORM
						.equals(pbocAllAllocateInfo.getCurrentUser().getOffice().getType())) {
					// 设定申请用户机构
					if (pbocAllAllocateInfo.getRoffice() == null) {
						pbocAllAllocateInfo.setRoffice(pbocAllAllocateInfo.getLoginUser().getOffice());
					}
				}
			} else if (AllocationConstant.BusinessType.PBOC_APPLICATION_ALLOCATION
					.equals(pbocAllAllocateInfo.getBusinessType())) {
				// 申请下拨
				pbocAllAllocateInfo.setInoutType(AllocationConstant.InoutType.Out);
				pbocAllAllocateInfo.setAllId(
						BusinessUtils.getPbocNewBusinessNo(Global.getConfig("businessType.allocation.pboc.cashOrder"),
								pbocAllAllocateInfo.getLoginUser().getOffice()));
				// 非金融平台用户登录时设置登记机构为本机构
				if (!Constant.OfficeType.DIGITAL_PLATFORM
						.equals(pbocAllAllocateInfo.getCurrentUser().getOffice().getType())) {
					// 设定申请用户机构
					if (pbocAllAllocateInfo.getRoffice() == null) {
						pbocAllAllocateInfo.setRoffice(pbocAllAllocateInfo.getLoginUser().getOffice());
					}
				}
			} else if (AllocationConstant.BusinessType.PBOC_AGENT_HANDIN
					.equals(pbocAllAllocateInfo.getBusinessType())) {
				// 代理上缴
				pbocAllAllocateInfo.setInoutType(AllocationConstant.InoutType.In);
				pbocAllAllocateInfo.setAllId(BusinessUtils.getPbocNewBusinessNo(
						Global.getConfig("businessType.allocation.pboc.cashAgentHandin"),
						pbocAllAllocateInfo.getLoginUser().getOffice()));
				// 非金融平台用户登录时设置登记机构为本机构
				if (!Constant.OfficeType.DIGITAL_PLATFORM
						.equals(pbocAllAllocateInfo.getCurrentUser().getOffice().getType())) {
					// 设定申请用户机构
					if (pbocAllAllocateInfo.getRoffice() == null) {
						pbocAllAllocateInfo.setRoffice(pbocAllAllocateInfo.getLoginUser().getOffice());
					}
				}
			}

			// 设定状态:待审批
			pbocAllAllocateInfo.setStatus(AllocationConstant.PbocOrderStatus.TO_APPROVE_STATUS);

			// 设置接收机构
			// 取得所属人行机构
			Office aOffice = BusinessUtils.getPbocCenterByOffice(pbocAllAllocateInfo.getRoffice());
			pbocAllAllocateInfo.setAoffice(aOffice);
		} else {
			// 更新的场合
			// 数据一致性验证
			checkVersion(pbocAllAllocateInfo);
			// 删除物品明细信息
			pbocAllAllocateItemsDao.deleteBYAllId(pbocAllAllocateInfo.getAllId());
		}
		// 保存调拨主表信息
		super.save(pbocAllAllocateInfo);
		// 保存调拨物品明细表信息
		List<PbocAllAllocateItem> itemList = pbocAllAllocateInfo.getPbocAllAllocateItemList();
		for (PbocAllAllocateItem item : itemList) {
			item.setAllId(pbocAllAllocateInfo.getAllId());
			item.setAllItemsId(IdGen.uuid());
			pbocAllAllocateItemsDao.insert(item);
		}
	}

	/**
	 * 保存复点入库物品列表
	 * 
	 * @author WangBaozhong
	 * @version 2016年7月9日
	 * 
	 * 
	 * @param itemList
	 *            复点入库物品列表
	 */
	@Transactional(readOnly = false)
	public void saveReCountingInstoreItems(List<PbocAllAllocateItem> itemList) {
		// 保存调拨物品明细表信息
		for (PbocAllAllocateItem item : itemList) {
			item.setAllItemsId(IdGen.uuid());
			pbocAllAllocateItemsDao.insert(item);
		}
	}

	/**
	 * 保存复点出入库信息
	 * 
	 * @author WangBaozhong
	 * @version 2016年6月23日
	 * 
	 * 
	 * @param pbocAllAllocateInfo
	 *            复点登记信息
	 */
	@Transactional(readOnly = false)
	public void saveReCountingPbocAllAllocateInfo(PbocAllAllocateInfo pbocAllAllocateInfo) {
		// 复点出入库
		pbocAllAllocateInfo.setAllId(
				BusinessUtils.getPbocNewBusinessNo(Global.getConfig("businessType.allocation.pboc.recounting"),
						pbocAllAllocateInfo.getLoginUser().getOffice()));
		// 取得当前机构
		Office cuOffice = pbocAllAllocateInfo.getCurrentUser().getOffice();
		if (Constant.OfficeType.CENTRAL_BANK.equals(cuOffice.getType())) {
			// 人行时设置登记机构为本机构
			pbocAllAllocateInfo.setRoffice(cuOffice);
		}
		// 设置业务类型
		pbocAllAllocateInfo.setBusinessType(AllocationConstant.BusinessType.PBOC_RE_COUNTING);
		// 设定状态:待配款
		pbocAllAllocateInfo.setStatus(AllocationConstant.PbocOrderStatus.TO_QUOTA_STATUS);
		// 设定审批人及审批时间
		pbocAllAllocateInfo.setApprovalBy(pbocAllAllocateInfo.getLoginUser());
		pbocAllAllocateInfo.setApprovalDate(new Date());

		// 保存调拨物品明细表信息
		List<PbocAllAllocateItem> itemList = pbocAllAllocateInfo.getPbocAllAllocateItemList();
		for (PbocAllAllocateItem item : itemList) {
			// 取得库存信息
			PbocStoStoresInfo storeInfo = StoreCommonUtils.getPbocStoStoresInfoByGoodsId(item.getGoodsId(),
					pbocAllAllocateInfo.getRoffice().getId());
			// 判断物品是否存在库存
			if (storeInfo == null) {
				String strGoodsName = StoreCommonUtils.getGoodsNameById(item.getGoodsId());
				strGoodsName = StringUtils.isBlank(strGoodsName) ? "" : strGoodsName;
				// [审批失败]：物品[{0}:{1}]对应库存信息不存在，请稍后再试或与系统管理员联系！
				throw new BusinessException("message.E2037", null, new String[] { item.getGoodsId(), strGoodsName });
			}
			String strGoodsName = StringUtils.isBlank(storeInfo.getGoodsName()) ? "" : storeInfo.getGoodsName();

			if (storeInfo.getSurplusStoNum() == null || storeInfo.getSurplusStoNum() == 0L
					|| item.getMoneyNumber() > storeInfo.getSurplusStoNum()) {
				// [审批失败]：物品[{0}]库存不足！
				throw new BusinessException("message.E2038", null, new String[] { strGoodsName });
			}
			// 判断库区物品是否充足
			long lGoodsNum = StoreCommonUtils.getGoodsNumInStoreAreaByGoodsId(item.getGoodsId(),
					pbocAllAllocateInfo.getRoffice().getId());
			if (item.getMoneyNumber() > lGoodsNum) {
				// [审批失败]：物品[{0}:{1}]对应库存信息不存在，请稍后再试或与系统管理员联系！
				throw new BusinessException("message.E2037", null, new String[] { item.getGoodsId(), strGoodsName });
			}

			item.setAllId(pbocAllAllocateInfo.getAllId());
			item.setAllItemsId(IdGen.uuid());
			item.setRegistType(AllocationConstant.RegistType.RegistStore);
			pbocAllAllocateItemsDao.insert(item);
		}
		// 保存调拨主表信息
		super.save(pbocAllAllocateInfo);
		this.bindingGoodsAreaInfo(pbocAllAllocateInfo);
		// 开始复点流程
		String procInsId =  actTaskService.startProcess("pbocRecounting", "PBOC_ALL_ALLOCATE_INFO", pbocAllAllocateInfo.getAllId());
		// 完成第一个任务
		actTaskService.completeFirstTask(procInsId);
	}

	/**
	 * 
	 * Title: savePbocApproveRegiste
	 * <p>
	 * Description: 审批登记
	 * </p>
	 * 
	 * @author: wangbaozhong
	 * @param pbocAllAllocateInfo
	 *            void 返回类型
	 */
	@Transactional(readOnly = false)
	public void savePbocApproveRegiste(PbocAllAllocateInfo pbocAllAllocateInfo) {
		// 取得登记机构
		Office rOffice = SysCommonUtils.findOfficeById(pbocAllAllocateInfo.getRoffice().getId());
		// 取得所属人行机构
		Office aOffice = BusinessUtils.getPbocCenterByOffice(rOffice);
		// 主键空的场合，设置主键
		if (AllocationConstant.BusinessType.PBOC_APPLICATION_HANDIN.equals(pbocAllAllocateInfo.getBusinessType())) {
			// 申请上缴
			pbocAllAllocateInfo.setInoutType(AllocationConstant.InoutType.In);
			pbocAllAllocateInfo.setAllId(
					BusinessUtils.getPbocNewBusinessNo(Global.getConfig("businessType.allocation.pboc.cashHandin"),
							pbocAllAllocateInfo.getLoginUser().getOffice()));
			// 设置接收机构
			pbocAllAllocateInfo.setAoffice(aOffice);
		} else if (AllocationConstant.BusinessType.PBOC_APPLICATION_ALLOCATION
				.equals(pbocAllAllocateInfo.getBusinessType())) {
			// 申请下拨
			pbocAllAllocateInfo.setInoutType(AllocationConstant.InoutType.Out);
			pbocAllAllocateInfo.setAllId(
					BusinessUtils.getPbocNewBusinessNo(Global.getConfig("businessType.allocation.pboc.cashOrder"),
							pbocAllAllocateInfo.getLoginUser().getOffice()));

			// 设置接收机构
			pbocAllAllocateInfo.setAoffice(aOffice);
		} else if (AllocationConstant.BusinessType.PBOC_AGENT_HANDIN.equals(pbocAllAllocateInfo.getBusinessType())) {
			// 代理上缴
			pbocAllAllocateInfo.setInoutType(AllocationConstant.InoutType.In);
			pbocAllAllocateInfo.setAllId(
					BusinessUtils.getPbocNewBusinessNo(Global.getConfig("businessType.allocation.pboc.cashAgentHandin"),
							pbocAllAllocateInfo.getLoginUser().getOffice()));
			// 设置接收机构
			pbocAllAllocateInfo.setAoffice(aOffice);
		} else if (AllocationConstant.BusinessType.PBOC_ALLOCATION_IN_STORE
				.equals(pbocAllAllocateInfo.getBusinessType())) {
			// 调拨入库
			pbocAllAllocateInfo.setInoutType(AllocationConstant.InoutType.In);
			pbocAllAllocateInfo.setAllId(BusinessUtils.getPbocNewBusinessNo(
					Global.getConfig("businessType.allocation.pboc.allocatedInStore"),
					pbocAllAllocateInfo.getLoginUser().getOffice()));
			// Office rOffice = new Office();
			// rOffice.setName(pbocAllAllocateInfo.getRofficeName());
			// pbocAllAllocateInfo.setRoffice(rOffice);
			// 设置接收机构
			String finishAllIds = pbocAllAllocateInfo.getAllocatefinishAllIdList().toString();
			finishAllIds = finishAllIds.replace("[", "").replace("]", "");
			// 调拨入库对应调拨出库列表
			pbocAllAllocateInfo.setAllocateOutAllIds(finishAllIds);
		} else if (AllocationConstant.BusinessType.PBOC_ALLOCATION_OUT_STORE
				.equals(pbocAllAllocateInfo.getBusinessType())) {
			// 调拨出库
			pbocAllAllocateInfo.setInoutType(AllocationConstant.InoutType.Out);
			pbocAllAllocateInfo.setAllId(BusinessUtils.getPbocNewBusinessNo(
					Global.getConfig("businessType.allocation.pboc.allocatedOutStore"),
					pbocAllAllocateInfo.getLoginUser().getOffice()));
			// aOffice = new Office();
			// aOffice.setName(pbocAllAllocateInfo.getRofficeName());
			pbocAllAllocateInfo.setAllocateInAfterOutFlag(AllocationConstant.AllocateInAfterOutFlag.NOT_IN);
			// 设置接收机构
			// pbocAllAllocateInfo.setAoffice(aOffice);
		} else if (AllocationConstant.BusinessType.PBOC_DESTROY_OUT_STORE
				.equals(pbocAllAllocateInfo.getBusinessType())) {
			// 销毁出库
			pbocAllAllocateInfo.setInoutType(AllocationConstant.InoutType.Out);
			pbocAllAllocateInfo.setAllId(
					BusinessUtils.getPbocNewBusinessNo(Global.getConfig("businessType.allocation.pboc.destoryOutStore"),
							pbocAllAllocateInfo.getLoginUser().getOffice()));
			// aOffice = new Office();
			// aOffice.setName(pbocAllAllocateInfo.getAofficeName());
			// 设置接收机构
			// pbocAllAllocateInfo.setAoffice(aOffice);
		}

		// 设定审批人及审批时间
		pbocAllAllocateInfo.setApprovalBy(pbocAllAllocateInfo.getLoginUser());
		pbocAllAllocateInfo.setApprovalDate(new Date());

		if (AllocationConstant.BusinessType.PBOC_APPLICATION_ALLOCATION.equals(pbocAllAllocateInfo.getBusinessType())
				|| AllocationConstant.BusinessType.PBOC_ALLOCATION_OUT_STORE
						.equals(pbocAllAllocateInfo.getBusinessType())
				|| AllocationConstant.BusinessType.PBOC_DESTROY_OUT_STORE
						.equals(pbocAllAllocateInfo.getBusinessType())) {

			// 业务类型为 申请下拨，调拨出库，销毁出库 设定状态:待配款
			pbocAllAllocateInfo.setStatus(AllocationConstant.PbocOrderStatus.TO_QUOTA_STATUS);
		} else {
			// 上缴审批
			pbocAllAllocateInfo.setApprovalBy(pbocAllAllocateInfo.getLoginUser());
			pbocAllAllocateInfo.setApprovalDate(new Date());
			// 设定状态:待入库
			pbocAllAllocateInfo.setStatus(AllocationConstant.PbocOrderStatus.InHandoverStatus.TO_IN_STORE_STATUS);
		}

		// 保存调拨物品明细表信息
		List<PbocAllAllocateItem> itemList = pbocAllAllocateInfo.getPbocAllAllocateItemList();

		for (PbocAllAllocateItem item : itemList) {
			item.setAllId(pbocAllAllocateInfo.getAllId());
			item.setAllItemsId(IdGen.uuid());
			item.setRegistType(AllocationConstant.RegistType.RegistStore);
			pbocAllAllocateItemsDao.insert(item);
		}
		for (PbocAllAllocateItem item : itemList) {
			item.setAllId(pbocAllAllocateInfo.getAllId());
			item.setAllItemsId(IdGen.uuid());
			item.setRegistType(AllocationConstant.RegistType.RegistPoint);
			pbocAllAllocateItemsDao.insert(item);
		}
		// 保存调拨主表信息
		super.save(pbocAllAllocateInfo);
		if (AllocationConstant.BusinessType.PBOC_APPLICATION_ALLOCATION.equals(pbocAllAllocateInfo.getBusinessType())
				|| AllocationConstant.BusinessType.PBOC_ALLOCATION_OUT_STORE
						.equals(pbocAllAllocateInfo.getBusinessType())
				|| AllocationConstant.BusinessType.PBOC_DESTROY_OUT_STORE
						.equals(pbocAllAllocateInfo.getBusinessType())) {
			this.bindingGoodsAreaInfo(pbocAllAllocateInfo);
		}

		if (!Collections3.isEmpty(pbocAllAllocateInfo.getPbocAllAllocateDetailList())) {
			List<StoRfidDenomination> rfidDenominationList = Lists.newArrayList();
			StoRfidDenomination rfidDenomination = null;
			for (PbocAllAllocateDetail detail : pbocAllAllocateInfo.getPbocAllAllocateDetailList()) {
				rfidDenomination = new StoRfidDenomination();
				rfidDenomination.setRfid(detail.getGoodsLocationInfo().getRfid());
				rfidDenominationList.add(rfidDenomination);
			}
			// 绑定RFID卡
			StoreCommonUtils.reBindingRfid(pbocAllAllocateInfo.getBusinessType(), pbocAllAllocateInfo.getLoginUser(),
					rfidDenominationList, pbocAllAllocateInfo.getAllId());

			dao.updateAllocateInAfterOutFlag(pbocAllAllocateInfo.getAllocateOutAllIds(),
					AllocationConstant.AllocateInAfterOutFlag.ALREADY_IN);
		}
	}

	/**
	 * 保存 原封新券入库登记信息
	 * 
	 * @author WangBaozhong
	 * @version 2016年7月11日
	 * 
	 * 
	 * @param pbocAllAllocateInfo
	 *            原封新券入库登记信息
	 */
	@Transactional(readOnly = false)
	public void savePbocOriginalBanknoteInStore(PbocAllAllocateInfo pbocAllAllocateInfo) {

		// 主键空的场合，设置主键
		// 原封新券入库
		pbocAllAllocateInfo.setInoutType(AllocationConstant.InoutType.In);
		pbocAllAllocateInfo.setAllId(BusinessUtils.getPbocNewBusinessNo(
				Global.getConfig("businessType.allocation.pboc.originalBanknoteInStore"),
				pbocAllAllocateInfo.getLoginUser().getOffice()));
		// 设置业务类型：原封新券入库
		pbocAllAllocateInfo.setBusinessType(AllocationConstant.BusinessType.PBOC_ORIGINAL_BANKNOTE_IN_STORE);
		// 设定状态:待交接
		pbocAllAllocateInfo.setStatus(AllocationConstant.PbocOrderStatus.InHandoverStatus.TO_HANDOVER_STATUS);

		// 保存调拨物品明细表信息
		for (PbocAllAllocateItem item : pbocAllAllocateInfo.getPbocAllAllocateItemList()) {
			item.setAllId(pbocAllAllocateInfo.getAllId());
			item.setAllItemsId(IdGen.uuid());
			item.setRegistType(AllocationConstant.RegistType.RegistStore);
			pbocAllAllocateItemsDao.insert(item);
		}
		pbocAllAllocateInfo.setCreateBy(pbocAllAllocateInfo.getLoginUser());
		pbocAllAllocateInfo.setCreateDate(new Date());
		pbocAllAllocateInfo.setUpdateBy(pbocAllAllocateInfo.getLoginUser());
		pbocAllAllocateInfo.setUpdateDate(new Date());
		pbocAllAllocateInfo.setScanGateDate(new Date());
		// 保存调拨主表信息
		pbocAllAllocateInfoDao.insert(pbocAllAllocateInfo);
	}

	@Override
	@Transactional(readOnly = false)
	public void delete(PbocAllAllocateInfo pbocAllAllocateInfo) {
		// 数据一致性验证
		checkVersion(pbocAllAllocateInfo);
		// 删除
		super.delete(pbocAllAllocateInfo);
	}

	/**
	 * 批量修改流水单号对应业务状态
	 * 
	 * @author WangBaozhong
	 * @version 2016年6月1日
	 * 
	 * 
	 * @param allIdList
	 *            流水单号列表
	 * @param updateUser
	 *            更新者
	 * @param targetStatus
	 *            目标业务状态
	 * @param toQuotaAllIdList
	 *            下拨申请配款成功流水单号
	 */
	@Transactional(readOnly = false)
	public void updateStatusByAllIds(List<String> allIdList, User user, String targetStatus,
			List<String> toQuotaAllIdList) {
		for (String allId : allIdList) {
			PbocAllAllocateInfo pbocAllAllocateInfo = super.get(allId);

			if (AllocationConstant.BusinessType.PBOC_APPLICATION_ALLOCATION
					.equals(pbocAllAllocateInfo.getBusinessType())
					|| AllocationConstant.BusinessType.PBOC_ALLOCATION_OUT_STORE
							.equals(pbocAllAllocateInfo.getBusinessType())
					|| AllocationConstant.BusinessType.PBOC_DESTROY_OUT_STORE
							.equals(pbocAllAllocateInfo.getBusinessType())
					|| AllocationConstant.BusinessType.PBOC_RE_COUNTING.equals(pbocAllAllocateInfo.getBusinessType())) {
				// 流水单号原业务状态为待审批，并且目标状态为驳回或待配款
				if (AllocationConstant.PbocOrderStatus.TO_APPROVE_STATUS.equals(pbocAllAllocateInfo.getStatus())
						&& (AllocationConstant.PbocOrderStatus.REJECT_STATUS.equals(targetStatus)
								|| AllocationConstant.PbocOrderStatus.TO_QUOTA_STATUS.equals(targetStatus))) {
					pbocAllAllocateInfo.setApprovalBy(user);
					pbocAllAllocateInfo.setApprovalDate(new Date());
					// 批量审批时，将申请物品拷贝为审批物品
					if (AllocationConstant.PbocOrderStatus.TO_QUOTA_STATUS.equals(targetStatus)) {
						pbocAllAllocateInfo.setConfirmAmount(pbocAllAllocateInfo.getRegisterAmount());
						List<PbocAllAllocateItem> itemList = pbocAllAllocateInfo.getPbocAllAllocateItemList();
						for (PbocAllAllocateItem item : itemList) {
							PbocStoStoresInfo storeInfo = StoreCommonUtils.getPbocStoStoresInfoByGoodsId(
									item.getGoodsId(), UserUtils.getUser().getOffice().getId());

							if (storeInfo == null) {
								String strGoodsName = StoreCommonUtils.getGoodsNameById(item.getGoodsId());
								strGoodsName = StringUtils.isBlank(strGoodsName) ? "" : strGoodsName;
								// [审批失败]：物品[{0}:{1}]对应库存信息不存在，请稍后再试或与系统管理员联系！
								throw new BusinessException("message.E2037", null,
										new String[] { item.getGoodsId(), strGoodsName });
							}
							String strGoodsName = StringUtils.isBlank(storeInfo.getGoodsName()) ? ""
									: storeInfo.getGoodsName();

							if (storeInfo.getSurplusStoNum() == null || storeInfo.getSurplusStoNum() == 0L
									|| item.getMoneyNumber() > storeInfo.getSurplusStoNum()) {
								// [审批失败]：物品[{0}]库存不足！
								throw new BusinessException("message.E2038", null, new String[] { strGoodsName });
							}

							long lGoodsNum = StoreCommonUtils.getGoodsNumInStoreAreaByGoodsId(item.getGoodsId(),
									user.getOffice().getId());
							if (item.getMoneyNumber() > lGoodsNum) {
								// [审批失败]：流水单号：[{0}]，库区内审批库物品[{1}]库存不足！
								throw new BusinessException("message.E2032", null,
										new String[] { allId, strGoodsName });
							}

							item.setAllItemsId(IdGen.uuid());
							item.setRegistType(AllocationConstant.RegistType.RegistStore);
							pbocAllAllocateItemsDao.insert(item);
						}
						// 保存下拨申请配款成功流水单号
						toQuotaAllIdList.add(allId);
					}
					// 流水单号原业务状态为待配款，并且目标状态为待出库
				} else if (AllocationConstant.PbocOrderStatus.TO_QUOTA_STATUS.equals(pbocAllAllocateInfo.getStatus())
						&& (AllocationConstant.PbocOrderStatus.OutHandoverStatus.TO_OUT_STORE_STATUS
								.equals(targetStatus)
								|| AllocationConstant.PbocOrderStatus.RecountingStatus.TO_OUT_STORE_STATUS
										.equals(targetStatus))) {
					pbocAllAllocateInfo.setQuotaPersonBy(user);
					pbocAllAllocateInfo.setQuotaDate(new Date());
					// 发送通知
					List<String> paramsList = Lists.newArrayList();
					paramsList.add(pbocAllAllocateInfo.getRoffice().getName());
					paramsList.add(pbocAllAllocateInfo.getAllId());
					// 申请下拨配款时消息目标机构为接收机构，销毁出库、复点出库、调拨出库配款时消息目标机构为登记机构
					if (AllocationConstant.BusinessType.PBOC_APPLICATION_ALLOCATION
							.equals(pbocAllAllocateInfo.getBusinessType())) {
						SysCommonUtils.allocateMessageQueueAdd(pbocAllAllocateInfo.getBusinessType(),
								targetStatus, paramsList, pbocAllAllocateInfo.getAoffice().getId(), UserUtils.getUser());
					} else {
						SysCommonUtils.allocateMessageQueueAdd(pbocAllAllocateInfo.getBusinessType(),
								targetStatus, paramsList, pbocAllAllocateInfo.getRoffice().getId(), UserUtils.getUser());
					}
				} else {
					// 其他状态不予修改
					continue;
				}
			} else {
				// 上缴审批
				pbocAllAllocateInfo.setApprovalBy(user);
				pbocAllAllocateInfo.setApprovalDate(new Date());
				// 驳回操作时不登记审批信息
				if (!AllocationConstant.PbocOrderStatus.REJECT_STATUS.equals(targetStatus)) {
					pbocAllAllocateInfo.setConfirmAmount(pbocAllAllocateInfo.getRegisterAmount());
					List<PbocAllAllocateItem> itemList = pbocAllAllocateInfo.getPbocAllAllocateItemList();
					for (PbocAllAllocateItem item : itemList) {
						item.setAllItemsId(IdGen.uuid());
						item.setRegistType(AllocationConstant.RegistType.RegistStore);
						pbocAllAllocateItemsDao.insert(item);
					}
				}
			}

			pbocAllAllocateInfo.setStatus(targetStatus);
			pbocAllAllocateInfo.preUpdate();
			pbocAllAllocateInfoDao.updateStatusByAllId(pbocAllAllocateInfo);
		}
	}

	/**
	 * 修改流水单号对应业务状态
	 * 
	 * @author WangBaozhong
	 * @version 2016年6月1日
	 * 
	 * 
	 * @param pbocAllAllocateInfo
	 *            业务信息
	 * @param updateUser
	 *            更新者
	 * @param targetStatus
	 *            目标业务状态
	 */
	@Transactional(readOnly = false)
	public void updateStatusByAllId(PbocAllAllocateInfo pbocAllAllocateInfo, User user, String targetStatus) {

		if (AllocationConstant.BusinessType.PBOC_APPLICATION_ALLOCATION.equals(pbocAllAllocateInfo.getBusinessType())) {

			// 流水单号原业务状态为待审批，并且目标状态为驳回或待配款
			if (AllocationConstant.PbocOrderStatus.TO_APPROVE_STATUS.equals(pbocAllAllocateInfo.getStatus())
					&& (AllocationConstant.PbocOrderStatus.REJECT_STATUS.equals(targetStatus)
							|| AllocationConstant.PbocOrderStatus.TO_QUOTA_STATUS.equals(targetStatus))) {
				pbocAllAllocateInfo.setApprovalBy(user);
				pbocAllAllocateInfo.setApprovalDate(new Date());
				// 批量审批时，将申请物品拷贝为审批物品
				if (AllocationConstant.PbocOrderStatus.TO_QUOTA_STATUS.equals(targetStatus)) {
					List<PbocAllAllocateItem> itemList = pbocAllAllocateInfo.getPbocAllAllocateItemList();
					for (PbocAllAllocateItem item : itemList) {
						if (AllocationConstant.RegistType.RegistPoint.equals(item.getRegistType())) {
							continue;
						}
						PbocStoStoresInfo storeInfo = StoreCommonUtils.getPbocStoStoresInfoByGoodsId(item.getGoodsId(),
								pbocAllAllocateInfo.getAoffice().getId());

						if (storeInfo == null) {
							String strGoodsName = StoreCommonUtils.getGoodsNameById(item.getGoodsId());
							// [审批失败]：物品[{0}:{1}]对应库存信息不存在，请稍后再试或与系统管理员联系！
							throw new BusinessException("message.E2037", null,
									new String[] { item.getGoodsId(), strGoodsName });
						}

						if (storeInfo.getSurplusStoNum() == null || storeInfo.getSurplusStoNum() == 0L
								|| item.getMoneyNumber() > storeInfo.getSurplusStoNum()) {
							// [审批失败]：物品[{0}]库存不足！
							throw new BusinessException("message.E2038", null,
									new String[] { storeInfo.getGoodsName() });
						}

						long lGoodsNum = StoreCommonUtils.getGoodsNumInStoreAreaByGoodsId(item.getGoodsId(),
								pbocAllAllocateInfo.getAoffice().getId());
						if (item.getMoneyNumber() > lGoodsNum) {
							// [审批失败]：流水单号：[{0}]，库区内审批库物品[{1}]库存不足！
							throw new BusinessException("message.E2032", null,
									new String[] { pbocAllAllocateInfo.getAllId(),
											StoreCommonUtils.getGoodsNameById(item.getGoodsId()) });
						}
						item.setAllId(pbocAllAllocateInfo.getAllId());
						item.setAllItemsId(IdGen.uuid());
						pbocAllAllocateItemsDao.insert(item);
					}
					// 绑定物品库区信息
					this.bindingGoodsAreaInfo(pbocAllAllocateInfo);
				}
			}
		} else {
			// 上缴审批
			pbocAllAllocateInfo.setApprovalBy(user);
			pbocAllAllocateInfo.setApprovalDate(new Date());
			// 驳回操作时不登记审批信息
			if (!AllocationConstant.PbocOrderStatus.REJECT_STATUS.equals(targetStatus)) {
				List<PbocAllAllocateItem> itemList = pbocAllAllocateInfo.getPbocAllAllocateItemList();
				for (PbocAllAllocateItem item : itemList) {
					if (AllocationConstant.RegistType.RegistPoint.equals(item.getRegistType())) {
						continue;
					}
					item.setAllId(pbocAllAllocateInfo.getAllId());
					item.setAllItemsId(IdGen.uuid());
					pbocAllAllocateItemsDao.insert(item);
				}
			}
		}
		pbocAllAllocateInfo.setStatus(targetStatus);
		pbocAllAllocateInfo.preUpdate();
		pbocAllAllocateInfoDao.updateStatusByAllId(pbocAllAllocateInfo);
		
	}

	/**
	 * 按流水单号还原库存 并更新配款修改物品
	 * 
	 * @author WangBaozhong
	 * @version 2016年6月1日
	 * 
	 * 
	 * @param pbocAllAllocateInfo
	 *            业务信息
	 * @param updateUser
	 *            更新者
	 * @param targetStatus
	 *            目标业务状态
	 */
	@Transactional(readOnly = false)
	public void reApprovalByAllId(PbocAllAllocateInfo pbocAllAllocateInfo, User user, String targetStatus) {
		// 还原库存
		reBackStore(pbocAllAllocateInfo.getAllId());
		if (AllocationConstant.BusinessType.PBOC_APPLICATION_ALLOCATION.equals(pbocAllAllocateInfo.getBusinessType())) {

			// 流水单号原业务状态为待配款或待出库，并且目标状态为驳回或待配款
			if ((AllocationConstant.PbocOrderStatus.TO_QUOTA_STATUS.equals(pbocAllAllocateInfo.getStatus())
					|| AllocationConstant.PbocOrderStatus.OutHandoverStatus.TO_OUT_STORE_STATUS
							.equals(pbocAllAllocateInfo.getStatus()))
					&& (AllocationConstant.PbocOrderStatus.REJECT_STATUS.equals(targetStatus)
							|| AllocationConstant.PbocOrderStatus.TO_QUOTA_STATUS.equals(targetStatus))) {
				// 批量审批时，将申请物品拷贝为审批物品
				if (AllocationConstant.PbocOrderStatus.TO_QUOTA_STATUS.equals(targetStatus)) {
					List<PbocAllAllocateItem> itemList = pbocAllAllocateInfo.getPbocAllAllocateItemList();
					for (PbocAllAllocateItem item : itemList) {
						if (AllocationConstant.RegistType.RegistPoint.equals(item.getRegistType())) {
							continue;
						}
						PbocStoStoresInfo storeInfo = StoreCommonUtils.getPbocStoStoresInfoByGoodsId(item.getGoodsId(),
								UserUtils.getUser().getOffice().getId());

						if (storeInfo == null) {
							String strGoodsName = StoreCommonUtils.getGoodsNameById(item.getGoodsId());
							// [修改失败]：物品[{0}:{1}]对应库存信息不存在，请稍后再试或与系统管理员联系！
							throw new BusinessException("message.E2042", null,
									new String[] { item.getGoodsId(), strGoodsName });
						}

						if (storeInfo.getSurplusStoNum() == null || storeInfo.getSurplusStoNum() == 0L
								|| item.getMoneyNumber() > storeInfo.getSurplusStoNum()) {
							// [修改失败]：物品[{0}]库存不足！
							throw new BusinessException("message.E2043", null,
									new String[] { storeInfo.getGoodsName() });
						}

						long lGoodsNum = StoreCommonUtils.getGoodsNumInStoreAreaByGoodsId(item.getGoodsId(),
								user.getOffice().getId());
						if (item.getMoneyNumber() > lGoodsNum) {
							// [修改失败]：流水单号：[{0}]，库区内审批库物品[{1}]库存不足！
							throw new BusinessException("message.E2044", null,
									new String[] { pbocAllAllocateInfo.getAllId(),
											StoreCommonUtils.getGoodsNameById(item.getGoodsId()) });
						}
						item.setAllId(pbocAllAllocateInfo.getAllId());
						item.setAllItemsId(IdGen.uuid());
						pbocAllAllocateItemsDao.insert(item);
					}
				}
			}
		}
		pbocAllAllocateInfo.setStatus(targetStatus);
		pbocAllAllocateInfo.preUpdate();
		pbocAllAllocateInfoDao.updateStatusByAllId(pbocAllAllocateInfo);
		// UPDATE-START  原因：修改为统一调用一个共通方法  update by SonyYuanYang  2018/05/03
		// 审批通过时指定库区
		// this.bindingGoodsArea(pbocAllAllocateInfo);
		this.bindingGoodsAreaInfo(pbocAllAllocateInfo);
		// UPDATE-END  原因：修改为统一调用一个共通方法  update by SonyYuanYang  2018/05/03
	}

	/**
	 * 根据流水单号绑定物品与库区位置信息
	 * 
	 * @author WangBaozhong
	 * @version 2016年6月2日
	 * 
	 * 
	 * @param allIdList
	 *            流水单号列表
	 * @return 成功返回success，失败返回错误消息
	 */
//	@Transactional(readOnly = false)
//	private void bindingGoodsArea(PbocAllAllocateInfo pbocAllAllocateInfo) {
//		List<PbocAllAllocateItem> quotaItemList = Lists.newArrayList();
//
//		List<PbocAllAllocateItem> temQuotaItemList = getQuotaItemListByAllId(pbocAllAllocateInfo.getAllId());
//		quotaItemList.addAll(temQuotaItemList);
//
//		Map<String, ChangeStoreEntity> entiryMap = Maps.newHashMap();
//		ChangeStoreEntity entity = null;
//
//		// 判断库存
//		for (PbocAllAllocateItem item : quotaItemList) {
//			PbocStoStoresInfo storeInfo = StoreCommonUtils.getPbocStoStoresInfoByGoodsId(item.getGoodsId(),
//					UserUtils.getUser().getOffice().getId());
//
//			if (storeInfo == null) {
//				String strGoodsName = StoreCommonUtils.getGoodsNameById(item.getGoodsId());
//				strGoodsName = StringUtils.isBlank(strGoodsName) ? "" : strGoodsName;
//				// [修改失败]：物品[{0}:{1}]对应库存信息不存在，请稍后再试或与系统管理员联系！
//				throw new BusinessException("message.E2042", null, new String[] { item.getGoodsId(), strGoodsName });
//			}
//
//			if (storeInfo.getSurplusStoNum() == null || storeInfo.getSurplusStoNum() == 0L
//					|| item.getMoneyNumber() > storeInfo.getSurplusStoNum()) {
//				// [修改失败]：物品[{0}]库存不足！
//				throw new BusinessException("message.E2043", null, new String[] { storeInfo.getGoodsName() });
//			}
//			// 按物品统计数量
//			if (!entiryMap.containsKey(item.getGoodsId())) {
//				entity = new ChangeStoreEntity();
//				entity.setGoodsId(item.getGoodsId());
//				entity.setNum(item.getMoneyNumber());
//				entiryMap.put(item.getGoodsId(), entity);
//			} else {
//				entity = entiryMap.get(item.getGoodsId());
//				entity.setNum(entity.getNum() + item.getMoneyNumber());
//			}
//
//		}
//
//		if (quotaItemList.size() > 0) {
//			// 取得绑定调拨物品库区信息列表
//			String strDaysInterval = Global.getConfig("store.area.getgoods.days.interval");
//			int iDaysInterval = StringUtils.isNotBlank(strDaysInterval) ? Integer.parseInt(strDaysInterval) : 0;
//			String errorMessageCode = null;
//			String errorGoodsId = null;
//			String errorAllId = null;
//			List<AllAllocateGoodsAreaDetail> goodsAreaDetailList = StoreCommonUtils.getBindingAreaInfoToDetail(
//					quotaItemList, iDaysInterval, errorMessageCode, errorGoodsId, errorAllId,
//					UserUtils.getUser().getOffice().getId());
//
//			if (goodsAreaDetailList == null) {
//				throw new BusinessException(errorMessageCode, null,
//						new String[] { errorAllId, StoreCommonUtils.getPbocGoodsNameByGoodId(errorGoodsId) });
//			}
//
//			insertToGoodsAreaDetail(goodsAreaDetailList);
//
//			// 减掉预剩库存
//			List<ChangeStoreEntity> entiryList = Lists.newArrayList();
//			Iterator<String> keyIterator = entiryMap.keySet().iterator();
//			while (keyIterator.hasNext()) {
//				entity = entiryMap.get(keyIterator.next());
//				entity.setNum(-entity.getNum());
//				entiryList.add(entity);
//			}
//			StoreCommonUtils.changePbocSurplusStore(entiryList, UserUtils.getUser().getOffice().getId());
//		}
//	}

	/**
	 * 根据流水单号查询配款物品信息列表
	 * 
	 * @author WangBaozhong
	 * @version 2016年5月24日
	 * 
	 * 
	 * @param allId
	 *            流水单号
	 * @return 配款物品信息列表
	 */
	public List<PbocAllAllocateItem> getQuotaItemListByAllId(String allId) {
		PbocAllAllocateItem param = new PbocAllAllocateItem();
		// 流水号
		param.setAllId(allId);
		// 登记种别
		param.setRegistType(AllocationConstant.RegistType.RegistStore);

		return pbocAllAllocateItemsDao.findItemsList(param);
	}

	/**
	 * 根据调拨流水单号删除库区位置关联数据
	 * 
	 * @author WangBaozhong
	 * @version 2016年5月23日
	 * 
	 * 
	 * @param allId
	 *            调拨流水单号
	 */
	@Transactional(readOnly = false)
	public void deleteGoodsAreaDetailByAllId(String allId) {
		goodsAreaDetailDao.deleteByAllId(allId);
	}

	/**
	 * 作成调拨物品与库区位置关联数据
	 * 
	 * @author WangBaozhong
	 * @version 2016年5月23日
	 * 
	 * 
	 * @param detailList
	 *            数据列表
	 */
	@Transactional(readOnly = false)
	public void insertToGoodsAreaDetail(List<AllAllocateGoodsAreaDetail> detailList) {
		for (AllAllocateGoodsAreaDetail detail : detailList) {
			detail.setId(IdGen.uuid());
			goodsAreaDetailDao.insert(detail);
		}
	}

	/**
	 * 根据流水单号 查询打印配款物品位置信息列表
	 * 
	 * @author WangBaozhong
	 * @version 2016年5月24日
	 * 
	 * 
	 * @param allId
	 *            流水单号
	 * @return 打印配款物品位置信息列表
	 */
	public PbocAllAllocateInfo getPrintQuotaDataByAllId(String allId) {

		return goodsAreaDetailDao.getPrintQuotaDataByAllId(allId, AllocationConstant.RegistType.RegistStore);
	}

	// /**
	// * 根据流水单号 查询复点出入库物品位置信息列表
	// * @author WangBaozhong
	// * @version 2016年5月24日
	// *
	// *
	// * @param allId 流水单号
	// * @return 打印配款物品位置信息列表
	// */
	// public PbocAllAllocateInfo getRecountingDataByAllId(String allId) {
	//
	// return goodsAreaDetailDao.getPrintQuotaDataByAllId(allId, null);
	// }

	/**
	 * 根据流水单号 查询预订出库RFID信息
	 * 
	 * @author WangBaozhong
	 * @version 2016年6月8日
	 * 
	 * 
	 * @param allId
	 *            流水单号
	 * @return 预订出库RFID列表
	 */
	public List<String> getReserveRfidListByAllId(String allId) {

		List<String> reserveRfidList = Lists.newArrayList();

		PbocAllAllocateInfo info = goodsAreaDetailDao.getPrintQuotaDataByAllId(allId,
				AllocationConstant.RegistType.RegistStore);
		if (info != null && info.getPbocAllAllocateItemList() != null) {

			for (PbocAllAllocateItem item : info.getPbocAllAllocateItemList()) {
				if (item.getGoodsAreaDetailList() == null) {
					continue;
				}
				for (AllAllocateGoodsAreaDetail detail : item.getGoodsAreaDetailList()) {
					reserveRfidList.add(detail.getGoodsLocationInfo().getRfid());
				}
			}
		}

		return reserveRfidList;
	}

	/**
	 * 取得配款物品和对应库区位置信息
	 * 
	 * @author WangBaozhong
	 * @version 2016年5月23日
	 * 
	 * 
	 * @return 配款物品和对应库区位置信息列表
	 */
	public List<PbocAllAllocateInfo> getQuotaGoodsAreaInfo(List<String> allIdList) {

		List<PbocAllAllocateInfo> printDataList = Lists.newArrayList();

		for (String allId : allIdList) {
			PbocAllAllocateInfo data = new PbocAllAllocateInfo();
			int printRowSpanNum = 0;
			// 自动化库房不开启时查询库位信息
			if (!AllocationConstant.AutomaticStoreSwitch.OPEN.equals(DbConfigUtils.getDbConfig("auto.vault.switch"))) {
				data = getPrintQuotaDataByAllId(allId);
				if (data != null) {
					// 根据配款物品ID取得物品名称
					for (PbocAllAllocateItem item : data.getPbocAllAllocateItemList()) {
						String strGoodsName = StoreCommonUtils.getGoodsNameById(item.getGoodsId());
						item.setGoodsName(strGoodsName);

						if (item.getGoodsAreaDetailList() != null) {

							printRowSpanNum += (item.getGoodsAreaDetailList().size() + 1);
						}
					}
					data.setPrintRowSpanNum(printRowSpanNum);

					printDataList.add(data);
				}
				// 自动化库房开启时查询物品信息
			} else {
				data = get(allId);
				if (data != null) {
					List<PbocAllAllocateItem> itemList = Lists.newArrayList();
					// 根据配款物品ID取得物品名称
					for (PbocAllAllocateItem item : data.getPbocAllAllocateItemList()) {
						String strGoodsName = StoreCommonUtils.getGoodsNameById(item.getGoodsId());
						item.setGoodsName(strGoodsName);

						if (AllocationConstant.RegistType.RegistStore.equals(item.getRegistType())) {
							itemList.add(item);
						}
					}
					data.setPbocAllAllocateItemList(itemList);
					data.setPrintRowSpanNum(itemList.size());

					printDataList.add(data);
				}
			}

		}

		return printDataList;
	}

	// /**
	// * 取得配款物品和对应库区位置信息
	// * @author WangBaozhong
	// * @version 2016年5月23日
	// *
	// *
	// * @return 配款物品和对应库区位置信息列表
	// */
	// public List<PbocAllAllocateInfo>
	// getRecountingViewGoodsAreaInfo(List<String> allIdList) {
	//
	// List<PbocAllAllocateInfo> printDataList = Lists.newArrayList();
	//
	// for (String allId : allIdList) {
	// PbocAllAllocateInfo data = getRecountingDataByAllId(allId);
	// int printRowSpanNum = 0;
	// if (data != null) {
	// // 根据配款物品ID取得物品名称
	// for (PbocAllAllocateItem item : data.getPbocAllAllocateItemList()) {
	// String strGoodsName =
	// StoreCommonUtils.getGoodsNameById(item.getGoodsId());
	// item.setGoodsName(strGoodsName);
	//
	// if (item.getGoodsAreaDetailList() != null) {
	//
	// printRowSpanNum += (item.getGoodsAreaDetailList().size() + 1);
	// }
	// }
	// data.setPrintRowSpanNum(printRowSpanNum);
	//
	// printDataList.add(data);
	// }
	//
	// }
	//
	// return printDataList;
	// }

	/**
	 * 配款打印后修改人行库存 Title: changePbocStore
	 * <p>
	 * Description:
	 * </p>
	 * 
	 * @author: wangbaozhong
	 * @param allIdList
	 *            流水单号列表
	 * @param updateUser
	 *            更新人 void 返回类型
	 */
	public void changePbocStore(List<String> allIdList, User updateUser) {
		for (String allId : allIdList) {
			PbocAllAllocateInfo data = getPrintQuotaDataByAllId(allId);
			if (data != null) {
				// 根据配款物品ID取得物品名称
				List<ChangeStoreEntity> changeStoreEntityList = Lists.newArrayList();
				ChangeStoreEntity changeStoreEntity = null;
				for (PbocAllAllocateItem item : data.getPbocAllAllocateItemList()) {
					changeStoreEntity = new ChangeStoreEntity();
					changeStoreEntity.setGoodsId(item.getGoodsId());
					changeStoreEntity.setNum(-item.getMoneyNumber());
					changeStoreEntityList.add(changeStoreEntity);
				}
				if (AllocationConstant.PbocOrderStatus.TO_QUOTA_STATUS.equals(data.getStatus())) {
					// 减实际库存
					StoreCommonUtils.changePbocStore(changeStoreEntityList, updateUser.getOffice().getId(), allId,
							updateUser);
				}
			}
		}
	}

	/**
	 * @author chengshu
	 * @version 2016/06/01
	 * 
	 * @Description 更新调拨信息
	 * @param pbocAllAllocateInfo
	 *            更新内容
	 * @return 更新件数
	 */
	@Transactional(readOnly = false)
	public int updateAllocateConfirmStatus(PbocAllAllocateInfo inputInfo) {

		PbocAllAllocateInfo pbocAllAllocateInfo = new PbocAllAllocateInfo();
		// 更新主键
		pbocAllAllocateInfo.setAllId(inputInfo.getAllId());
		// 状态
		pbocAllAllocateInfo.setStatus(inputInfo.getStatus());
		// 入库金额
		pbocAllAllocateInfo.setInstoreAmount(inputInfo.getInstoreAmount());
		// 备注信息
		pbocAllAllocateInfo.setRemarks(inputInfo.getRemarks());
		// 操作人员
		User updateUser = new User();
		updateUser.setId(inputInfo.getUserId());
		updateUser.setName(inputInfo.getUserName());

		// 根据业务类型 记录 出入库时通过扫描门时间
		if ((AllocationConstant.BusinessType.PBOC_AGENT_HANDIN.equals(inputInfo.getBusinessType())
				|| AllocationConstant.BusinessType.PBOC_ALLOCATION_IN_STORE.equals(inputInfo.getBusinessType())
				|| AllocationConstant.BusinessType.PBOC_APPLICATION_HANDIN.equals(inputInfo.getBusinessType()))
				&& AllocationConstant.PbocOrderStatus.InHandoverStatus.TO_HANDOVER_STATUS
						.equals(inputInfo.getStatus())) {
			// 接收时通过扫描门时间
			pbocAllAllocateInfo.setScanGateDate(new Date());
		} else if ((AllocationConstant.BusinessType.PBOC_ALLOCATION_OUT_STORE.equals(inputInfo.getBusinessType())
				|| AllocationConstant.BusinessType.PBOC_APPLICATION_ALLOCATION.equals(inputInfo.getBusinessType())
				|| AllocationConstant.BusinessType.PBOC_DESTROY_OUT_STORE.equals(inputInfo.getBusinessType()))
				&& AllocationConstant.PbocOrderStatus.OutHandoverStatus.TO_HANDOVER_STATUS
						.equals(inputInfo.getStatus())) {
			// 出库金额
			pbocAllAllocateInfo.setOutstoreAmount(inputInfo.getOutstoreAmount());
			// 核对结果
			pbocAllAllocateInfo.setCheckResult(inputInfo.getCheckResult());
			// 出库时通过扫描门时间
			pbocAllAllocateInfo.setScanGateDate(new Date());
		} else if (AllocationConstant.BusinessType.PBOC_RE_COUNTING.equals(inputInfo.getBusinessType())) {
			if (AllocationConstant.InOutCoffer.OUT.equals(inputInfo.getInoutType())
					&& AllocationConstant.PbocOrderStatus.RecountingStatus.TO_OUT_STORE_HANDOVER_STATUS
							.equals(inputInfo.getStatus())) {
				// 出库金额
				pbocAllAllocateInfo.setOutstoreAmount(inputInfo.getOutstoreAmount());
				// 核对结果
				pbocAllAllocateInfo.setCheckResult(inputInfo.getCheckResult());
				// 出库时通过扫描门时间
				pbocAllAllocateInfo.setScanGateDate(new Date());
			} else if (AllocationConstant.InOutCoffer.IN.equals(inputInfo.getInoutType())
					&& AllocationConstant.PbocOrderStatus.RecountingStatus.TO_IN_STORE_HANDOVER_STATUS
							.equals(inputInfo.getStatus())) {
				// 接收时通过扫描门时间
				pbocAllAllocateInfo.setInstoreAmount(inputInfo.getInstoreAmount());
				pbocAllAllocateInfo.setInStoreScanGateDate(new Date());
				// 退库标记
				pbocAllAllocateInfo.setCancellingStocksFlag(inputInfo.getCancellingStocksFlag());
			}
		}

		// 更新者
		pbocAllAllocateInfo.setUpdateBy(updateUser);
		// 更新时间
		pbocAllAllocateInfo.setUpdateDate(new Date());
		// 执行更新处理
		return updateAllocateInfoSelective(pbocAllAllocateInfo);
	}

	/**
	 * @author chengshu
	 * @version 2016/06/01
	 * 
	 * @Description 插入箱袋信息
	 * @param inputParam
	 *            电文传入信息
	 */
	@Transactional(readOnly = false)
	public void insertAllocateDetailInfo(PbocAllAllocateInfo inputParam) {

		// 箱袋列表
		List<PbocAllAllocateDetail> detailList = inputParam.getPbocAllAllocateDetailList();
		// 流水号
		String allId = inputParam.getAllId();

		// 执行插入处理
		PbocAllAllocateDetail allocateDetail = new PbocAllAllocateDetail();
		for (PbocAllAllocateDetail detail : detailList) {
			allocateDetail = new PbocAllAllocateDetail();
			// 主键UUID
			allocateDetail.setId(IdGen.uuid());
			// 流水号
			allocateDetail.setAllId(allId);
			// 扫描状态
			allocateDetail.setScanFlag(AllocationConstant.ScanFlag.Scan);
			// // 箱号
			// allocateDetail.setBoxNo(detail.getRfid());
			// RFID
			allocateDetail.setRfid(detail.getRfid());
			// 强制标识
			allocateDetail.setStatus(detail.getStatus());
			// 有效标识
			allocateDetail.setDelFlag(AllocationConstant.deleteFlag.Valid);

			// 箱袋场所
			if (AllocationConstant.InOutCoffer.OUT.equals(inputParam.getInoutType())) {
				if (AllocationConstant.BusinessType.PBOC_RE_COUNTING.equals(inputParam.getBusinessType())) {
					// 出库的场合，场所是在途
					allocateDetail.setPlace(AllocationConstant.Place.ClassficationRoom);
				} else {
					// 出库的场合，场所是在途
					allocateDetail.setPlace(AllocationConstant.Place.onPassage);
				}
				// 设置出入库类型
				allocateDetail.setInoutType(AllocationConstant.InOutCoffer.OUT);
			} else {
				// 入库的场合，场所是在库房
				allocateDetail.setPlace(AllocationConstant.Place.StoreRoom);
				// 设置出入库类型
				allocateDetail.setInoutType(AllocationConstant.InOutCoffer.IN);
			}

			// 执行插入处理
			saveAllocateDetail(allocateDetail);
		}
	}

	/**
	 * @author chengshu
	 * @version 2016/06/01
	 * 
	 * @Description 更新调拨信息
	 * @param allocateInfo
	 *            更新内容
	 * @return 更新件数
	 */
	private int updateAllocateInfoSelective(PbocAllAllocateInfo allocateInfo) {
		return pbocAllAllocateInfoDao.updateSelective(allocateInfo);
	}

	/**
	 * @author chengshu
	 * @version 2016/06/01
	 * 
	 * @Description 插入箱袋信息
	 * @param allocateDetail
	 *            箱袋内容
	 * @return 更新件数
	 */
	private void saveAllocateDetail(PbocAllAllocateDetail allocateDetail) {
		allocateDetailDao.insert(allocateDetail);
	}

	/**
	 * 
	 * @author LLF
	 * @version 2016年6月2日
	 * 
	 *          人行闸门任务查询
	 * @param pbocAllAllocateInfo
	 * @return
	 */
	public List<PbocAllAllocateInfo> findListInterface(PbocAllAllocateInfo pbocAllAllocateInfo) {
		return dao.findListInterface(pbocAllAllocateInfo);
	}

	/**
	 * @author chengshu
	 * @version 2016/06/01
	 * 
	 * @Description 查询应该出库物品信息
	 * @param inputInfo
	 *            电文传入参数
	 * @return 更新件数
	 */
	@Transactional(readOnly = false)
	public List<Map<String, String>> findIsNecessaryOut(PbocAllAllocateInfo inputInfo) {

		// 查询必须出库的箱袋信息
		List<AllAllocateGoodsAreaDetail> necessaryOutInfolist = allocateGoodsAreaDetailDao
				.findIsNecessaryOut(inputInfo.getAllId());

		Map<String, String> errorMap = Maps.newHashMap();
		List<Map<String, String>> errorList = Lists.newArrayList();

		for (AllAllocateGoodsAreaDetail detail : necessaryOutInfolist) {
			if (Constant.NecessaryOut.NECESSARY_OUT_YES.equals(detail.getIsNecessaryOut())
					&& !inputInfo.getRfidList().contains(detail.getRfid())) {

				errorMap = Maps.newHashMap();
				errorMap.put(Parameter.STORE_ESCORT_RFID,
						org.apache.commons.lang3.StringUtils.left(detail.getRfid(), 8));

				// 获取原封箱信息
				StoOriginalBanknote originalBankNote = StoreCommonUtils.getStoOriginalBanknoteByBoxId(detail.getRfid(),
						inputInfo.getAoffice().getId());

				if (originalBankNote != null) {
					errorMap.put(Parameter.STORE_ESCORT_RFID, detail.getStoreAreaName() + Global.getConfig("store.area")
							+ Constant.Punctuation.HALF_COLON + detail.getRfid());
				} else {
					// 原封券以外，则截取rfid前八位
					errorMap.put(Parameter.STORE_ESCORT_RFID,
							detail.getStoreAreaName() + Global.getConfig("store.area") + Constant.Punctuation.HALF_COLON
									+ org.apache.commons.lang3.StringUtils.left(detail.getRfid(), 8));
				}

				// errorMap.put(Parameter.STATUS_KEY,
				// HardwareConstant.ErrorRfidType.first_out);

				errorList.add(errorMap);
			}
		}

		return errorList;
	}

	/**
	 * 调拨主表的数据一致性验证
	 * 
	 * @param stoRelevance
	 */
	public void checkVersion(PbocAllAllocateInfo pbocAllAllocateInfo) {
		// PbocAllAllocateInfo result = dao.getByIdVersion(pbocAllAllocateInfo);
		// if (result == null) {
		// throw new BusinessException("message.E0007", "", new String[] {
		// pbocAllAllocateInfo.getAllId() });
		// }

		// 数据一致性验证
		PbocAllAllocateInfo oldData = super.get(pbocAllAllocateInfo.getAllId());
		if (!oldData.getStrUpdateDate().equals(pbocAllAllocateInfo.getStrUpdateDate())) {
			throw new BusinessException("message.E0007", "", new String[] { pbocAllAllocateInfo.getAllId() });
		}
	}

	/**
	 * 
	 * Title: computeGoodsAmount
	 * <p>
	 * Description: 按照登记类型计算物品总价值
	 * </p>
	 * 
	 * @author: wangbaozhong
	 * @param pbocAllAllocateInfo
	 *            物品明细 void 返回类型
	 */
	public void computeGoodsAmount(PbocAllAllocateInfo pbocAllAllocateInfo) {
		BigDecimal registeAmount = new BigDecimal(0.0d);
		BigDecimal confirmAmount = new BigDecimal(0.0d);

		for (PbocAllAllocateItem item : pbocAllAllocateInfo.getPbocAllAllocateItemList()) {
			if (AllocationConstant.RegistType.RegistPoint.equals(item.getRegistType())) {
				registeAmount = registeAmount.add(item.getMoneyAmount());
			} else if (AllocationConstant.RegistType.RegistStore.equals(item.getRegistType())) {
				confirmAmount = confirmAmount.add(item.getMoneyAmount());
			}
		}
		pbocAllAllocateInfo.setRegisterAmount(registeAmount.doubleValue());
		pbocAllAllocateInfo.setConfirmAmount(confirmAmount.doubleValue());

		pbocAllAllocateInfo.setRegisterAmountBig(NumToRMB.changeToBig(pbocAllAllocateInfo.getRegisterAmount()));
		pbocAllAllocateInfo.setConfirmAmountBig(NumToRMB.changeToBig(pbocAllAllocateInfo.getConfirmAmount()));

		if (pbocAllAllocateInfo.getInstoreAmount() != null) {
			pbocAllAllocateInfo.setInstoreAmountBig(NumToRMB.changeToBig(pbocAllAllocateInfo.getInstoreAmount()));
		}
	}

	/**
	 * 
	 * Title: computeGoodsAmount
	 * <p>
	 * Description: 按照登记类型计算复点物品总价值
	 * </p>
	 * 
	 * @author: wangbaozhong
	 * @param pbocAllAllocateInfo
	 *            物品明细 void 返回类型
	 */
	public void computeRecountingGoodsAmount(PbocAllAllocateInfo pbocAllAllocateInfo) {
		BigDecimal registeAmount = new BigDecimal(0.0d);
		BigDecimal confirmAmount = new BigDecimal(0.0d);

		for (PbocAllAllocateItem item : pbocAllAllocateInfo.getPbocAllAllocateItemList()) {
			if (AllocationConstant.RegistType.RegistStore.equals(item.getRegistType())) {
				registeAmount = registeAmount.add(item.getMoneyAmount());
			} else if (AllocationConstant.RegistType.RegistPoint.equals(item.getRegistType())) {
				confirmAmount = confirmAmount.add(item.getMoneyAmount());
			}
		}
		pbocAllAllocateInfo.setRegisterAmount(registeAmount.doubleValue());
		pbocAllAllocateInfo.setConfirmAmount(confirmAmount.doubleValue());

		pbocAllAllocateInfo.setRegisterAmountBig(NumToRMB.changeToBig(pbocAllAllocateInfo.getRegisterAmount()));
		pbocAllAllocateInfo.setConfirmAmountBig(NumToRMB.changeToBig(pbocAllAllocateInfo.getConfirmAmount()));

		if (pbocAllAllocateInfo.getInstoreAmount() != null) {
			pbocAllAllocateInfo.setInstoreAmountBig(NumToRMB.changeToBig(pbocAllAllocateInfo.getInstoreAmount()));
		}

		if (pbocAllAllocateInfo.getOutstoreAmount() != null) {
			pbocAllAllocateInfo.setOutstoreAmountBig(NumToRMB.changeToBig(pbocAllAllocateInfo.getOutstoreAmount()));
		}
	}

	/**
	 * 
	 * Title: getApprovalPrintDetail
	 * <p>
	 * Description: 获取审批打印明细
	 * </p>
	 * 
	 * @author: wangbaozhong
	 * @param allId
	 *            流水单号
	 * @return PbocApprovalPrintDetail 返回类型
	 */
	public PbocApprovalPrintDetail getApprovalPrintDetail(String allId) {

		PbocApprovalPrintDetail rtn = new PbocApprovalPrintDetail();

		PbocAllAllocateInfo allocateInfo = super.get(allId);
		// 登记机构名字
		rtn.setRofficeName(allocateInfo.getRofficeName());
		// 业务状态
		rtn.setStatus(allocateInfo.getStatus());
		// 预约日期
		rtn.setApplyDate(allocateInfo.getApplyDate());
		// 业务类型
		rtn.setBusinessType(allocateInfo.getBusinessType());
		// 审批金额
		rtn.setConfirmAmount(allocateInfo.getConfirmAmount());
		// 审批人名字
		rtn.setApprovalName(allocateInfo.getApprovalName());
		// 创建日期
		rtn.setCreateDate(allocateInfo.getCreateDate());
		// 审批时间
		rtn.setApprovalDate(allocateInfo.getApprovalDate());

		BigDecimal fullCouponBigDec = new BigDecimal(0d);
		BigDecimal damagedBigDec = new BigDecimal(0d);
		BigDecimal originalBigDec = new BigDecimal(0d);
		// 按物品种类取得审批物品总金额
		for (PbocAllAllocateItem item : allocateInfo.getPbocAllAllocateItemList()) {
			if (!AllocationConstant.RegistType.RegistStore.equals(item.getRegistType())) {
				continue;
			}

			StoGoodSelect goodSelect = StoreCommonUtils.splitGood(item.getGoodsId());
			// 统计完整券
			if (StoreConstant.GoodsClassification.FULL_COUPON.equals(goodSelect.getClassification())) {
				fullCouponBigDec = fullCouponBigDec.add(item.getMoneyAmount());
			} else if (StoreConstant.GoodsClassification.DAMAGED_COUPON.equals(goodSelect.getClassification())) {
				// 统计残损券
				damagedBigDec = damagedBigDec.add(item.getMoneyAmount());
			} else if (StoreConstant.GoodsClassification.ORIGINAL_COUPON.equals(goodSelect.getClassification())) {
				// 原封新券
				originalBigDec = originalBigDec.add(item.getMoneyAmount());
			}
		}

		// 完整券金额
		rtn.setFullCouponAmount(fullCouponBigDec.doubleValue());
		// 损伤券金额
		rtn.setDamagedCouponAmount(damagedBigDec.doubleValue());
		// 原封新券
		rtn.setOriginalCouponAmount(originalBigDec.doubleValue());

		return rtn;
	}

	/**
	 * 
	 * Title: reBackStore
	 * <p>
	 * Description: 还原库存信息
	 * </p>
	 * 
	 * @author: wangbaozhong
	 * @param allId
	 *            流水单号 void 返回类型
	 */
	@Transactional(readOnly = false)
	public void reBackStore(String allId) {
		PbocAllAllocateInfo pbocAllAllocateInfo = goodsAreaDetailDao.getPrintQuotaDataByAllId(allId,
				AllocationConstant.RegistType.RegistStore);
		List<ChangeStoreEntity> entiryList = Lists.newArrayList();

		if (pbocAllAllocateInfo == null) {
			return;
		}
		PbocAllAllocateInfo pInfo = pbocAllAllocateInfoDao.get(allId);
		// 还原库区中物品预定状态
		for (PbocAllAllocateItem approvalItem : pbocAllAllocateInfo.getPbocAllAllocateItemList()) {
			for (AllAllocateGoodsAreaDetail areaDetail : approvalItem.getGoodsAreaDetailList()) {
				// 申请下拨时还原接收机构信息
				if (AllocationConstant.BusinessType.PBOC_APPLICATION_ALLOCATION.equals(pInfo.getBusinessType())) {
					StoreCommonUtils.updateOutStoreGoodsStatusByRfid(areaDetail.getGoodsLocationInfo().getRfid(),
							Constant.deleteFlag.Valid, pInfo.getAoffice().getId());
				} else {
					// 复点、销毁、调拨出库时还原登记机构信息
					StoreCommonUtils.updateOutStoreGoodsStatusByRfid(areaDetail.getGoodsLocationInfo().getRfid(),
							Constant.deleteFlag.Valid, pInfo.getRoffice().getId());
				}
			}
			ChangeStoreEntity changeStoreEntity = new ChangeStoreEntity();
			changeStoreEntity.setGoodsId(approvalItem.getGoodsId());
			changeStoreEntity.setNum(approvalItem.getMoneyNumber());
			entiryList.add(changeStoreEntity);
		}
		// 申请下拨时还原接收机构信息
		if (AllocationConstant.BusinessType.PBOC_APPLICATION_ALLOCATION.equals(pInfo.getBusinessType())) {
			StoreCommonUtils.changePbocSurplusStore(entiryList, pInfo.getAoffice().getId());
		} else {
			// 复点、销毁、调拨出库时还原登记机构信息
			StoreCommonUtils.changePbocSurplusStore(entiryList, pInfo.getRoffice().getId());
		}

		// 还原库存信息
		// StoreCommonUtils.changePbocSurplusStore(entiryList,
		// UserUtils.getUser().getOffice().getId());

		// 删除库房登记调拨物品明细
		pbocAllAllocateItemsDao.deleteBYAllIdAndRegistType(allId, AllocationConstant.RegistType.RegistStore);

		// 删除库区预定信息
		goodsAreaDetailDao.deleteByAllId(allId);
	}

	/**
	 * 删除配款审批信息 Title: deleteQuotaInfo
	 * <p>
	 * Description:
	 * </p>
	 * 
	 * @author: wangbaozhong
	 * @param pbocAllAllocateInfo
	 *            void 返回类型
	 */
	@Transactional(readOnly = false)
	public void deleteQuotaInfo(PbocAllAllocateInfo pbocAllAllocateInfo) {
		// 数据一致性验证
		checkVersion(pbocAllAllocateInfo);

		reBackStore(pbocAllAllocateInfo.getAllId());
		// 删除
		super.delete(pbocAllAllocateInfo);
	}

	/**
	 * 根据调拨命令号，查询调拨出库流水列表(没有入库的业务流水)
	 * 
	 * @author WangBaozhong
	 * @version 2017年4月20日
	 * 
	 * 
	 * @param commondNumber
	 *            调拨命令号
	 * @return 调拨出库流水列表
	 */
	public List<PbocAllAllocateInfo> searchByCommandId(String commondNumber) {
		PbocAllAllocateInfo pbocAllAllocateInfo = new PbocAllAllocateInfo();
		// 调拨命令号
		pbocAllAllocateInfo.setCommondNumber(commondNumber);
		// 调拨出库后入库标记 没有入库的业务
		pbocAllAllocateInfo.setAllocateInAfterOutFlag(AllocationConstant.AllocateInAfterOutFlag.NOT_IN);
		if (Constant.OfficeType.CENTRAL_BANK.equals(UserUtils.getUser().getOffice().getType())) {
			// 设定接收机构
			pbocAllAllocateInfo.setAoffice(UserUtils.getUser().getOffice());
		}
		// 调拨出库业务类型
		pbocAllAllocateInfo.setBusinessType(AllocationConstant.BusinessType.PBOC_ALLOCATION_OUT_STORE);

		return super.findList(pbocAllAllocateInfo);

	}

	/**
	 * 更新 调拨出库后入库标记
	 * 
	 * @author WangBaozhong
	 * @version 2017年4月21日
	 * 
	 * 
	 * @param allId
	 *            流水单号
	 * @param allocateInAfterOutFlag
	 *            调拨出库后入库标记
	 */
	@Transactional(readOnly = false)
	public void deleteAllocateIn(PbocAllAllocateInfo pbocAllAllocateInfo) {

		delete(pbocAllAllocateInfo);

		dao.updateAllocateInAfterOutFlag(pbocAllAllocateInfo.getAllocateOutAllIds(),
				AllocationConstant.AllocateInAfterOutFlag.NOT_IN);
	}

	/**
	 * 绑定物品与库区位置信息
	 * 
	 * @author SongYuanYang
	 * @version 2018年5月2日
	 * 
	 * @param pbocAllAllocateInfo
	 *            主表信息
	 */
	@Transactional(readOnly = false)
	public void bindingGoodsAreaInfo(PbocAllAllocateInfo pbocAllAllocateInfo) {
		// 取得物品信息列表
		List<PbocAllAllocateItem> quotaItemList = this.getQuotaItemListByAllId(pbocAllAllocateInfo.getAllId());
		Map<String, ChangeStoreEntity> entiryMap = Maps.newHashMap();
		ChangeStoreEntity entity = null;
		// 判断库存
		for (PbocAllAllocateItem item : quotaItemList) {
			// 按物品统计数量
			if (!entiryMap.containsKey(item.getGoodsId())) {
				entity = new ChangeStoreEntity();
				entity.setGoodsId(item.getGoodsId());
				entity.setNum(item.getMoneyNumber());
				entiryMap.put(item.getGoodsId(), entity);
			} else {
				entity = entiryMap.get(item.getGoodsId());
				entity.setNum(entity.getNum() + item.getMoneyNumber());
			}
		}
		if (quotaItemList.size() > 0) {
			// 自动化库房不开启时进行关联库区
			if (!AllocationConstant.AutomaticStoreSwitch.OPEN.equals(DbConfigUtils.getDbConfig("auto.vault.switch"))) {
				// 取得绑定调拨物品库区信息列表
				String strDaysInterval = Global.getConfig("store.area.getgoods.days.interval");
				int iDaysInterval = StringUtils.isNotBlank(strDaysInterval) ? Integer.parseInt(strDaysInterval) : 0;
				String errorMessageCode = null;
				String errorGoodsId = null;
				String errorAllId = null;
				List<AllAllocateGoodsAreaDetail> goodsAreaDetailList = null;
				// 申请下拨时关联接收机构库区，销毁出库、复点出库、调拨出库时关联登记机构库区
				if (AllocationConstant.BusinessType.PBOC_APPLICATION_ALLOCATION
						.equals(pbocAllAllocateInfo.getBusinessType())) {
					goodsAreaDetailList = StoreCommonUtils.getBindingAreaInfoToDetail(quotaItemList, iDaysInterval,
							errorMessageCode, errorGoodsId, errorAllId, pbocAllAllocateInfo.getAoffice().getId());
				} else {
					goodsAreaDetailList = StoreCommonUtils.getBindingAreaInfoToDetail(quotaItemList, iDaysInterval,
							errorMessageCode, errorGoodsId, errorAllId, pbocAllAllocateInfo.getRoffice().getId());
				}

				if (goodsAreaDetailList == null) {
					throw new BusinessException(errorMessageCode, null,
							new String[] { errorAllId, StoreCommonUtils.getPbocGoodsNameByGoodId(errorGoodsId) });
				}
				this.insertToGoodsAreaDetail(goodsAreaDetailList);
			}
			// 减掉预剩库存
			List<ChangeStoreEntity> entiryList = Lists.newArrayList();
			Iterator<String> keyIterator = entiryMap.keySet().iterator();
			while (keyIterator.hasNext()) {
				entity = entiryMap.get(keyIterator.next());
				entity.setNum(-entity.getNum());
				entiryList.add(entity);
			}
			// 申请下拨时减接收机构库存，销毁出库、复点出库、调拨出库时减登记机构预剩余库存
			if (AllocationConstant.BusinessType.PBOC_APPLICATION_ALLOCATION
					.equals(pbocAllAllocateInfo.getBusinessType())) {
				StoreCommonUtils.changePbocSurplusStore(entiryList, pbocAllAllocateInfo.getAoffice().getId());
			} else {
				StoreCommonUtils.changePbocSurplusStore(entiryList, pbocAllAllocateInfo.getRoffice().getId());
			}

		}
	}

}