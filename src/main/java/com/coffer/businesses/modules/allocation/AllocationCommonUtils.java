package com.coffer.businesses.modules.allocation;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.ui.Model;

import com.coffer.businesses.common.Constant;
import com.coffer.businesses.common.utils.BusinessUtils;
import com.coffer.businesses.modules.allocation.v01.entity.AllAllocateInfo;
import com.coffer.businesses.modules.allocation.v01.entity.AllAllocateItem;
import com.coffer.businesses.modules.allocation.v01.entity.AtmClearBoxInfo;
import com.coffer.businesses.modules.allocation.v01.service.AllocationService;
import com.coffer.businesses.modules.allocation.v01.service.CashBetweenService;
import com.coffer.businesses.modules.allocation.v02.entity.PbocAllAllocateInfo;
import com.coffer.businesses.modules.allocation.v02.entity.PbocAllHandoverUserDetail;
import com.coffer.businesses.modules.allocation.v02.service.PbocAllAllocateInfoService;
import com.coffer.businesses.modules.store.StoreCommonUtils;
import com.coffer.businesses.modules.store.v01.entity.StoBlankBillSelect;
import com.coffer.businesses.modules.store.v01.entity.StoGoodSelect;
import com.coffer.businesses.modules.store.v01.entity.StoGoods;
import com.coffer.core.common.utils.SpringContextHolder;
import com.coffer.core.modules.sys.entity.User;
import com.coffer.core.modules.sys.service.SystemService;
import com.coffer.core.modules.sys.utils.UserUtils;
import com.google.common.collect.Lists;

/**
 * 共同处理类
 * 
 * @author LF
 * @version 2014-11-18
 */
public class AllocationCommonUtils extends BusinessUtils {

	// 系统服务
	private static SystemService systemService = SpringContextHolder.getBean(SystemService.class);
	
	// 调拨服务
	private static AllocationService allocationService = SpringContextHolder.getBean(AllocationService.class);
	
	// 人行调拨服务
	private static PbocAllAllocateInfoService pbocAllAllocateInfoService = SpringContextHolder.getBean(PbocAllAllocateInfoService.class);
	// 库间调拨服务
	private static CashBetweenService cashBetweenService = SpringContextHolder.getBean(CashBetweenService.class);
	/**
	 * 
	 * @author chengs
	 * @version 2016年1月12日
	 * 
	 * 查询调拨信息
	 * @param allocateInfo 查询条件
	 * @return 调拨信息
	 */
	public static List<AllAllocateInfo> findAllocation(AllAllocateInfo allocateInfo) {

		List<AllAllocateInfo> allocationList = allocationService.findAllocation(allocateInfo);

		return allocationList;
	}

	/**
	 * 
	 * @author wangbaozhong
	 * @version 2015年9月16日
	 * 
	 *          取得物品Key
	 * 
	 * @param param 物品参数
	 * @return 物品Key
	 */
	public static String getGoodsKey(AllAllocateItem param) {
		StringBuffer strBuf = new StringBuffer();
		// 币种
		strBuf.append(StringUtils.isEmpty(param.getCurrency()) ? "" : param.getCurrency()) 
				 // 类别
				.append(StringUtils.isEmpty(param.getClassification()) ? "" : param.getClassification())
				// 套别
				.append(StringUtils.isEmpty(param.getSets()) ? "0" : param.getSets()) 
				 // 材质
				.append(StringUtils.isEmpty(param.getCash()) ? "" : param.getCash())
				 // 面值
				.append(StringUtils.isEmpty(param.getDenomination()) ? "" : param.getDenomination())
				 // 单位
				.append(StringUtils.isEmpty(param.getUnit()) ? "" : param.getUnit());

		return strBuf.toString();
	}
	/**
	 * 
	 * @author QPH
	 * @version 2017年7月10日
	 * 
	 *          取得物品Key
	 * 
	 * @param param 物品参数
	 * @return 物品Key
	 */
	public static String getGoodsKeybygoodsId(AllAllocateItem param) {
		StringBuffer strBuf = new StringBuffer();
		// 币种
		strBuf.append(StringUtils.isEmpty( param.getscurrency(param.getGoodsId())) ? "" : param.getscurrency(param.getGoodsId())) 
				 // 类别
				.append(StringUtils.isEmpty(param.getsclassification(param.getGoodsId())) ? "" : param.getsclassification(param.getGoodsId()))
				// 套别
				.append(StringUtils.isEmpty(param.getssets(param.getGoodsId())) ? "0" : param.getssets(param.getGoodsId())) 
				 // 材质
				.append(StringUtils.isEmpty(param.getscash(param.getGoodsId())) ? "" : param.getscash(param.getGoodsId()))
				 // 面值
				.append(StringUtils.isEmpty(param.getsdenomination(param.getGoodsId())) ? "" : param.getsdenomination(param.getGoodsId()))
				 // 单位
				.append(StringUtils.isEmpty(param.getsunit(param.getGoodsId())) ? "" : param.getsunit(param.getGoodsId()));

		return strBuf.toString();
	}
	
	/**
	 * 
	 * @author wangbaozhong
	 * @version 2015年9月16日
	 * 
	 *          取得物品Key
	 * 
	 * @param param 物品参数
	 * @return 物品Key
	 */
	public static String getGoodsKeyFromStoGoodSelect(StoGoodSelect param) {
		StringBuffer strBuf = new StringBuffer();
		// 币种
		strBuf.append(StringUtils.isEmpty(param.getCurrency()) ? "" : param.getCurrency()) 
				 // 类别
				.append(StringUtils.isEmpty(param.getClassification()) ? "" : param.getClassification())
				// 套别
				.append(StringUtils.isEmpty(param.getEdition()) ? "" : param.getEdition()) 
				 // 材质
				.append(StringUtils.isEmpty(param.getCash()) ? "" : param.getCash())
				 // 面值
				.append(StringUtils.isEmpty(param.getDenomination()) ? "" : param.getDenomination())
				 // 单位
				.append(StringUtils.isEmpty(param.getUnit()) ? "" : param.getUnit());

		return strBuf.toString();
	}
	
	/**
	 * 转换重空物品ID
	 * 
	 * @author wangbaozhong
	 * @param param 调拨物品
	 * @return 重空物品ID
	 */
	public static String changeItemToImpBlankDocId(AllAllocateItem param){
		//物品管理Entity
		StoGoods goods = new StoGoods();
		//重空选项
		StoBlankBillSelect bbs = new StoBlankBillSelect();
		// 重空种类
		bbs.setBlankBillKind(param.getCurrency());
		// 重空类型
		bbs.setBlankBillType(param.getClassification());
		goods.setStoBlankBillSelect(bbs);
		
		return StoreCommonUtils.genBlankBillId(goods);
	}
	
	/**
	 * 
	 * @author chenxin
	 * @version 2015年10月29日
	 * 
	 * 取得物品名称 
	 * @param param
	 * @return
	 */
	public static String getGoodsName(AllAllocateItem param) {
		StringBuffer strBuf = new StringBuffer();
		// 币种
		strBuf.append(StringUtils.isEmpty(param.getCurrency()) ? "" : param.getCurrency()) 
				 // 类别
				.append(StringUtils.isEmpty(param.getClassification()) ? "" : param.getClassification())
				// 套别
				.append(StringUtils.isEmpty(param.getSets()) ? "0" : param.getSets()) 
				 // 材质
				.append(StringUtils.isEmpty(param.getCash()) ? "0" : param.getCash())
				 // 面值
				.append(StringUtils.isEmpty(param.getDenomination()) ? "00" : param.getDenomination())
				 // 单位
				.append(StringUtils.isEmpty(param.getUnit()) ? "000" : param.getUnit());

		return StoreCommonUtils.getGoodsName(strBuf.toString());
	}
	
	/**
	 * @author ChengShu
	 * @date 2014/11/18
	 *
	 * @Description 验证管理员
	 * @param systemService
	 *            用户验证用Service
	 * @param allocation
	 *            箱袋信息
	 * @return 跳转页面
	 */
	public static String authorization(AllAllocateInfo allocateInfo) {

		// 页面输入的用户名
		String username = allocateInfo.getUserName();
		// 页面输入的密码
		String password = allocateInfo.getPassword();
		String resultMessage = "";

		if (StringUtils.isNotBlank(username) && StringUtils.isNotBlank(password)) {
			// 根据用户名取得用户信息
			User user = systemService.getUserByLoginName(username);

			// 验证用户名和密码
			if (user != null && SystemService.validatePassword(password, user.getPassword())) {
				// 验证用户的机构是否是当前机构，并且用户的权限是否是主管
				if (UserUtils.getUser().getOffice().getId().equals(user.getOffice().getId())
						&& Constant.SysUserType.COFFER_MANAGER.equals(user.getUserType())) {
					resultMessage = AllocationConstant.SUCCESS;

				} else {
					// 如果该用户不是主管
					resultMessage = "message.E0006";
				}

			} else {
				// 用户名密码错误或用户不存在
				resultMessage = "message.E0005";
			}
		} else {
			// 没有输入用户名或者密码
			resultMessage = "message.E0004";
		}

		return resultMessage;
	}
	
	/**
	 * 根据流水单号 查询预订出库RFID信息
	 * @author WangBaozhong
	 * @version 2016年6月8日
	 * 
	 *  
	 * @param allId 流水单号
	 * @return 预订出库RFID列表
	 */
	public static List<String> getReserveRfidListByAllId(String allId) {
		return pbocAllAllocateInfoService.getReserveRfidListByAllId(allId);
	}

	/**
	 * ATM清机插入调拨信息的方法
	 * 
	 * @author xp
	 * @param allAllocateInfo
	 */
	public static void saveRegisterInfo(AllAllocateInfo allAllocateInfo) {
		cashBetweenService.saveClearRegisterInfo(allAllocateInfo);
	}

	/**
	 * 查询箱子状态(ATM清机) 2017-11-10
	 * 
	 * @author xp
	 * @param checkBoxList
	 */
	public static void checkBox(List<String> checkBoxList) {
		// 验证箱子是否存在于其他未完成的流水中
		allocationService.checkBoxExitsInUnfinishAllocate(checkBoxList);
	}
	
	/**
	 * 
	 * Title: getAtmClearBoxInfoByPlanId
	 * <p>
	 * Description: 根据加钞计划批次号查询ATM清机钞箱信息
	 * </p>
	 * 
	 * @author: xp
	 * @param planId
	 *            加钞计划批次号
	 * @return 加钞计划批次号对应ATM清机钞箱信息列表 List<AtmClearBoxInfo> 返回类型
	 */
	public static List<AtmClearBoxInfo> getAtmClearBoxInfoByPlanId(String planId) {
		// 查询内容 (1)钞箱编号  (2)钞箱中的现金量  (3) 拆箱时间
		// 查询步骤：
		// 1.根据planId查询 ALL_ALLOCATE_ITEMS表中的箱子和金额信息，
		// 查询条件ALL_ALLOCATE_ITEMS.confirm_flag = 0(PDA登记或页面登记) 并且 ALL_ALLOCATE_ITEMS.BATCH_NO=planId
		// 2. 查询钞箱对应all_allocate_info表中的创建时间作为拆箱时间
		// 3. 第一步查询出的箱子对应调拨主表的del_flag = 1,此条数据无效
		List<AtmClearBoxInfo> rtnList = allocationService.getAtmClearBoxInfoByPlanId(planId);
		return rtnList;
	}
	
	/**
	 * 根据钞箱号获取加钞计划ID
	 * 
	 * @author XL
	 * @version 2017年12月13日
	 * @param boxNo
	 * @return
	 */
	public static String getPlanIdByBoxNo(String boxNo) {
		return allocationService.getPlanIdByBoxNo(boxNo);
	}
	
	/**
	 * 
	 * Title: pbocHandoverFilter
	 * <p>Description: 人行调拨业务交接人员过滤</p>
	 * @author:     yanbingxu
	 * @param pbocAllAllocateInfo
	 * @param model 
	 * void    返回类型
	 */
	public static void pbocHandoverFilter(PbocAllAllocateInfo pbocAllAllocateInfo, Model model) {
		// 交接信息初始化
		StringBuffer pbocHandover = new StringBuffer();
		StringBuffer cofferHandover = new StringBuffer();
		List<String> pbocHandoverIdList = Lists.newArrayList();
		List<String> cofferHandoverIdList = Lists.newArrayList();
		// 授权人信息初始化
		StringBuffer authorize = new StringBuffer();
		List<String> authorizeIdList = Lists.newArrayList();

		List<PbocAllHandoverUserDetail> handoverDetailList = Lists.newArrayList();
		if (pbocAllAllocateInfo.getPbocAllHandoverInfo() != null) {
			// 根据交接id取得交接人员信息
			handoverDetailList = pbocAllAllocateInfo.getPbocAllHandoverInfo().getHandoverUserDetailList();
			// 授权信息
			authorize.append(pbocAllAllocateInfo.getPbocAllHandoverInfo().getManagerUserName());
			authorizeIdList.add(pbocAllAllocateInfo.getPbocAllHandoverInfo().getManagerUserId());
			// 过滤交接信息
			for (PbocAllHandoverUserDetail handover : handoverDetailList) {
				if (AllocationConstant.BusinessType.PBOC_APPLICATION_ALLOCATION
						.equals(pbocAllAllocateInfo.getBusinessType())
						|| AllocationConstant.BusinessType.PBOC_ALLOCATION_OUT_STORE
								.equals(pbocAllAllocateInfo.getBusinessType())
						|| AllocationConstant.BusinessType.PBOC_DESTROY_OUT_STORE
								.equals(pbocAllAllocateInfo.getBusinessType())) {
					if (AllocationConstant.UserType.handover.equals(handover.getType())) {
						pbocHandover.append(handover.getEscortName() + Constant.Punctuation.HALF_SPACE);
						pbocHandoverIdList.add(handover.getEscortId());
					}
					if (AllocationConstant.UserType.accept.equals(handover.getType())) {
						cofferHandover.append(handover.getEscortName() + Constant.Punctuation.HALF_SPACE);
						cofferHandoverIdList.add(handover.getEscortId());
					}
				} else {
					if (AllocationConstant.UserType.handover.equals(handover.getType())) {
						cofferHandover.append(handover.getEscortName() + Constant.Punctuation.HALF_SPACE);
						cofferHandoverIdList.add(handover.getEscortId());
					}
					if (AllocationConstant.UserType.accept.equals(handover.getType())) {
						pbocHandover.append(handover.getEscortName() + Constant.Punctuation.HALF_SPACE);
						pbocHandoverIdList.add(handover.getEscortId());
					}
				}
			}
			model.addAttribute("pbocHandover", pbocHandover);
			model.addAttribute("cofferHandover", cofferHandover);
			model.addAttribute("pbocHandoverIdList", pbocHandoverIdList);
			model.addAttribute("cofferHandoverIdList", cofferHandoverIdList);
			model.addAttribute("authorize", authorize);
			model.addAttribute("authorizeIdList", authorizeIdList);
		}
	}

}
