package com.coffer.businesses.modules.store;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;

import org.springframework.transaction.annotation.Transactional;

import com.coffer.businesses.common.Constant;
import com.coffer.businesses.common.utils.BusinessUtils;
import com.coffer.businesses.modules.allocation.AllocationConstant;
import com.coffer.businesses.modules.allocation.v02.entity.AllAllocateGoodsAreaDetail;
import com.coffer.businesses.modules.allocation.v02.entity.PbocAllAllocateItem;
import com.coffer.businesses.modules.store.v01.entity.ChangeStoreEntity;
import com.coffer.businesses.modules.store.v01.entity.StoAddCashGroup;
import com.coffer.businesses.modules.store.v01.entity.StoAreaSettingInfo;
import com.coffer.businesses.modules.store.v01.entity.StoBoxDetail;
import com.coffer.businesses.modules.quartz.entity.Quartz;
import com.coffer.businesses.modules.quartz.service.QuartzService;
//import com.coffer.businesses.modules.collection.CollectionCommonUtils;
import com.coffer.businesses.modules.store.v01.entity.StoBoxInfo;
import com.coffer.businesses.modules.store.v01.entity.StoCarInfo;
import com.coffer.businesses.modules.store.v01.entity.StoDict;
import com.coffer.businesses.modules.store.v01.entity.StoEscortInfo;
import com.coffer.businesses.modules.store.v01.entity.StoGoodSelect;
import com.coffer.businesses.modules.store.v01.entity.StoGoods;
import com.coffer.businesses.modules.store.v01.entity.StoGoodsLocationInfo;
import com.coffer.businesses.modules.store.v01.entity.StoRelevance;
import com.coffer.businesses.modules.store.v01.entity.StoRfidDenomination;
import com.coffer.businesses.modules.store.v01.entity.StoRouteDetail;
import com.coffer.businesses.modules.store.v01.entity.StoRouteInfo;
import com.coffer.businesses.modules.store.v01.entity.StoStoresHistory;
import com.coffer.businesses.modules.store.v01.entity.StoStoresInfo;
import com.coffer.businesses.modules.store.v01.service.StoAddCashGroupService;
import com.coffer.businesses.modules.store.v01.service.StoAreaSettingInfoService;
import com.coffer.businesses.modules.store.v01.service.StoBoxInfoService;
import com.coffer.businesses.modules.store.v01.service.StoCarInfoService;
import com.coffer.businesses.modules.store.v01.service.StoEmptyDocumentService;
import com.coffer.businesses.modules.store.v01.service.StoEscortInfoService;
import com.coffer.businesses.modules.store.v01.service.StoGoodsLocationInfoService;
import com.coffer.businesses.modules.store.v01.service.StoGoodsService;
import com.coffer.businesses.modules.store.v01.service.StoRelevanceService;
import com.coffer.businesses.modules.store.v01.service.StoRfidDenominationService;
import com.coffer.businesses.modules.store.v01.service.StoRouteInfoService;
import com.coffer.businesses.modules.store.v01.service.StoStoresInfoService;
import com.coffer.businesses.modules.store.v02.entity.PbocStoStoresInfo;
import com.coffer.businesses.modules.store.v02.entity.StoDocTempInfo;
import com.coffer.businesses.modules.store.v02.entity.StoOfficeStamperInfo;
import com.coffer.businesses.modules.store.v02.entity.StoOriginalBanknote;
import com.coffer.businesses.modules.store.v02.service.PbocStoStoresInfoService;
import com.coffer.businesses.modules.store.v02.service.StoDocStamperMgrService;
import com.coffer.businesses.modules.store.v02.service.StoOfficeStamperInfoService;
import com.coffer.businesses.modules.store.v02.service.StoOriginalBanknoteService;
import com.coffer.core.common.service.BusinessException;
import com.coffer.core.common.utils.DateUtils;
import com.coffer.core.common.utils.NumToRMB;
import com.coffer.core.common.utils.SpringContextHolder;
import com.coffer.core.common.utils.StringUtils;
import com.coffer.core.modules.sys.entity.Office;
import com.coffer.core.modules.sys.entity.User;
import com.coffer.core.modules.sys.service.OfficeService;
import com.coffer.core.modules.sys.service.SystemService;
import com.coffer.core.modules.sys.utils.DictUtils;
import com.coffer.core.modules.sys.utils.UserUtils;
import com.coffer.external.ExternalConstant;
import com.google.common.collect.Lists;

/**
 * 共同处理类
 * 
 * @author niguoyong
 * @version 2015-09-06
 */
public class StoreCommonUtils extends BusinessUtils {

	/** 线路管理-服务层 **/
	private static StoRouteInfoService routeInfoService = SpringContextHolder.getBean(StoRouteInfoService.class);
	/** 人员管理-服务层 **/
	private static StoEscortInfoService escortInfoService = SpringContextHolder.getBean(StoEscortInfoService.class);
	/** 箱袋管理-服务层 **/
	private static StoBoxInfoService boxInfoService = SpringContextHolder.getBean(StoBoxInfoService.class);
	/** 系统管理-服务层 **/
	private static SystemService systemService = SpringContextHolder.getBean(SystemService.class);
	/** 库存管理-服务层 **/
	private static StoStoresInfoService stoStoresInfoService = SpringContextHolder.getBean(StoStoresInfoService.class);
	/** 物品管理-服务层 **/
	private static StoGoodsService stoGoodsService = SpringContextHolder.getBean(StoGoodsService.class);
	/** 物品关联配置-服务层 **/
	private static StoRelevanceService stoRelevanceService = SpringContextHolder.getBean(StoRelevanceService.class);
	/** 重空管理-服务层 **/
	private static StoEmptyDocumentService stoEmptyDocumentService = SpringContextHolder
			.getBean(StoEmptyDocumentService.class);

	/** 库区物品-服务层 **/
	private static StoGoodsLocationInfoService stoGoodsLocationInfoService = SpringContextHolder
			.getBean(StoGoodsLocationInfoService.class);
	/** RFID面值绑定Service **/
	private static StoRfidDenominationService stoRfidDenominationService = SpringContextHolder
			.getBean(StoRfidDenominationService.class);
	/** 人民银行库存管理Service **/
	private static PbocStoStoresInfoService pbocStoStoresInfoService = SpringContextHolder
			.getBean(PbocStoStoresInfoService.class);

	private static StoOriginalBanknoteService stoOriginalBanknoteService = SpringContextHolder
			.getBean(StoOriginalBanknoteService.class);
	/** 单据印章管理 Service **/
	private static StoDocStamperMgrService stoDocStamperMgrService = SpringContextHolder
			.getBean(StoDocStamperMgrService.class);
	/** 机构印章 Service **/
	private static StoOfficeStamperInfoService stoOfficeStamperInfoService = SpringContextHolder
			.getBean(StoOfficeStamperInfoService.class);

	/** 库房区域设定Service **/
	private static StoAreaSettingInfoService stoAreaSettingInfoService = SpringContextHolder
			.getBean(StoAreaSettingInfoService.class);

	/** 库房区域设定Service **/
	private static StoCarInfoService stoCarInfoService = SpringContextHolder.getBean(StoCarInfoService.class);
	
	/** 加钞组Service **/
	private static StoAddCashGroupService stoAddCashGroupService = SpringContextHolder.getBean(StoAddCashGroupService.class);

	/** 机构Service **/
	private static OfficeService officeService = SpringContextHolder.getBean(OfficeService.class);
	/** 任务调度Service **/
	private static QuartzService quartzService = SpringContextHolder.getBean(QuartzService.class);
	/**
	 * 
	 * @author niguoyong
	 * @version 2015-09-06
	 * 
	 *          验证箱袋类型是否符合业务同时验证箱袋编号
	 * @param boxNo
	 * @param office
	 * @param status
	 * @return
	 */
	public static String validateBoxNoOrType(String boxNo, Office office, boolean status, String boxType,
			StoBoxInfo boxInfoInput) {
		int length = boxType.length();
		if (boxNo.length() > length && boxType.equals(boxNo.substring(boxNo.length() - length))) {
			return BusinessUtils.isBoxNoValidate(boxNo, office, status, boxInfoInput);
		} else {
			return "message.E0001";
		}
	}

	/**
	 * 获取根据钞箱类型已创建的钞箱数量
	 * 
	 * @author niguoyong
	 * @version 2015-09-06
	 * 
	 * 
	 * @param modId
	 * @return
	 */
	public static Integer getStoAtmBoxModListSize(String modId) {
		StoBoxInfo boxInfo = new StoBoxInfo();
		List<StoBoxInfo> list = Lists.newArrayList();
		if (StringUtils.isNoneBlank(modId)) {
			boxInfo.setModId(modId);
			list = boxInfoService.findList(boxInfo);
		}
		return list.size();
	}

	/**
	 * 根据多个箱号，查询多个箱子
	 * 
	 * @author chengshu
	 * @version 2015-10-13
	 * 
	 * @param boxList
	 *            箱号列表
	 * @return
	 */
	public static List<StoBoxInfo> getBoxListByArray(List<String> boxList) {
		StoBoxInfo boxInfo = new StoBoxInfo();
		boxInfo.setBoxNos(boxList);
		List<StoBoxInfo> list = boxInfoService.searchBoxListByArray(boxInfo);
		return list;
	}

	/**
	 * 获取物品关联币种
	 * 
	 * @author yuxixuan
	 * @return
	 */
	public static List<StoDict> getReleCurrencyList() {
		return stoRelevanceService.getReleCurrencyList(new StoRelevance());
	}
	
	/**
	 * 
	 * Title: getBoxInfoByRfidAndBoxNo
	 * <p>Description: 根据rfid或箱号查询箱袋记录</p>
	 * @author:     wanghan
	 * @param boxInfo 箱袋实体类对象
	 * @return 箱袋实体类对象
	 * StoBoxInfo    返回类型
	 */
	public static StoBoxInfo getBoxInfoByRfidAndBoxNo(StoBoxInfo boxInfo) {
		return boxInfoService.getBoxInfoByRfidAndBoxNo(boxInfo);
	}

	/**
	 * 
	 * @author niguoyong
	 * @version 2015-09-06
	 * 
	 *          修改箱袋状态
	 * @param boxInfo
	 */
	@Transactional(readOnly = false)
	public static void updateBoxStatus(StoBoxInfo boxInfo) {
		boxInfoService.updateStatus(boxInfo);
	}

	/**
	 * 根据网点查询线路，及线路下所有网点信息
	 * 
	 * @author niguoyong
	 * @version 2015-09-06
	 * @param officeId
	 * @return
	 */
	public static StoRouteInfo searchStoRouteInfoByOffice(String officeId) {
		return routeInfoService.searchStoRouteInfoByOfficeId(officeId);
	}

	/**
	 * 
	 * 创建临时路线
	 * 
	 * @author niguoyong
	 * @version 2015-09-06
	 * @Description
	 * @param routeId
	 * @param officeList
	 * @return
	 */
	public static String createTemporaryRoute(String routeId, List<Office> officeList) {
		StoRouteInfo stoRouteInfo = null;
		List<StoRouteDetail> stoRouteDetailList = Lists.newArrayList();
		// 是否创建新的临时路线
		if (StringUtils.isNotEmpty(routeId)) {
			// 不创建新的临时路线
			stoRouteInfo = routeInfoService.get(routeId);
			stoRouteDetailList = stoRouteInfo.getStoRouteDetailList();
		} else {
			stoRouteInfo = new StoRouteInfo();
			stoRouteInfo.setRouteType(Constant.RouteInfo.TEMPORARY_ROUTE);
			stoRouteInfo.setRouteName(routeInfoService.getTemporaryRouteSeq());
		}
		// 检查所有需要创建临时路线网点信息
		for (Office office : officeList) {
			// // 获取当前网点所在线路
			// StoRouteInfo hisStoRouteInfo =
			// routeInfoService.searchStoRouteInfoByOffice(office.getId());
			// //
			// 判断当前网点所在线路是否为临时线路或routeId路线，为临时线路不是routeID路线在hisStoRouteInfo线路中将网点删除，如果hisStoRouteInfo线路只最后一个网点将此网点删除
			// if (hisStoRouteInfo != null &&
			// !routeId.equals(hisStoRouteInfo.getRouteId())
			// &&
			// StoreConstant.RouteInfo.TEMPORARY_ROUTE.equals(hisStoRouteInfo.getRouteType()))
			// {
			// // 路线存在，不是routeId路线路线中存在的，是存在临时路线中，删除网点
			// if (hisStoRouteInfo.getStoRouteDetailList().size() > 1) {
			// // 当前路线存在多个网点，只删除网点信息
			// routeInfoService.delete(hisStoRouteInfo.getRouteId(),
			// office.getId());
			// } else {
			// // 当前线路只存在一个网点，删除线路
			// routeInfoService.delete(hisStoRouteInfo.getRouteId());
			// }
			// }
			// 将当前网点加到需要创建的网点明细中
			for (StoRouteDetail stoRouteDetail : stoRouteInfo.getStoRouteDetailList()) {
				// 当前网点已经在线路中存在，无需添加
				if (office.getId().equals(stoRouteDetail.getOffice().getId())) {
					continue;
				}
			}
			StoRouteDetail stoRouteDetail = new StoRouteDetail();
			stoRouteDetail.setOffice(office);
			stoRouteDetailList.add(stoRouteDetail);
		}
		stoRouteInfo.setStoRouteDetailList(stoRouteDetailList);
		// 创建临时线路
		routeInfoService.save(stoRouteInfo);
		return stoRouteInfo.getId();
	}

	/**
	 * 
	 * 删除临时线路中网点信息
	 * 
	 * @author niguoyong
	 * @version 2015-09-06
	 * 
	 * @Description
	 * @param routeId
	 * @param officeId
	 */
	public static String deleteTemporaryRouteInOffice(String routeId, String officeId) {
		// 删除网点
		routeInfoService.delete(routeId, officeId);
		// 返回常规线路id；
		StoRouteInfo stoRouteInfo = routeInfoService.searchStoRouteInfoByOfficeId(officeId);
		if (stoRouteInfo != null) {
			return stoRouteInfo.getId();
		}
		return null;
	}

	/**
	 * 
	 * @author wangbaozhong
	 * @version 2016年1月27日
	 * 
	 *          根据线路ID查询线路信息
	 * @param routeId
	 *            路线ID
	 * @return 路线信息
	 */
	public static StoRouteInfo getRouteInfoById(String routeId) {
		return routeInfoService.get(routeId);
	}

	/**
	 * 
	 * 获取押运人员下拉列表
	 * 
	 * @author niguoyong
	 * @version 2015-09-06
	 * 
	 * @Description
	 * @return
	 */
	public static List<StoEscortInfo> getStoEscortinfoList(String getType) {
		return escortInfoService.getStoEscortinfoList(getType);
	}

	/**
	 * 
	 * 根据当前用户获取押运人员下拉列表
	 * 
	 * @author xp
	 * @version 2017-08-21
	 * 
	 * @Description
	 * @return
	 */
	public static List<StoEscortInfo> getFilterStoEscortinfoList(String getType, String officeId) {
		List<StoEscortInfo> filterEscortInfo = Lists.newArrayList();
		List<StoEscortInfo> EscortInfo = escortInfoService.getStoEscortinfoList(getType);
		for (StoEscortInfo stoEscortInfo : EscortInfo) {
			if (stoEscortInfo.getOffice().getId().equals(officeId)) {
				filterEscortInfo.add(stoEscortInfo);
			}
		}
		return filterEscortInfo;
	}

	/**
	 * 
	 * 获取交接人员列表
	 * 
	 * @author chengshu
	 * @version 2016-02-25
	 * 
	 * @Description
	 * @return
	 */
	public static List<StoEscortInfo> getStoEscortinfoList(StoEscortInfo stoEscortInfo) {
		return escortInfoService.findList(stoEscortInfo);
	}

	/**
	 * 
	 * @author niguoyong
	 * @version 2015-09-06
	 * 
	 *          根据身份证编号验证身份证信息
	 * @param idcardNo
	 * @return
	 */
	public static StoEscortInfo checkIdcardNo(String idcardNo) {
		return escortInfoService.checkIdcardNo(idcardNo);
	}

	/**
	 * 
	 * @author niguoyong
	 * @version 2015-09-06
	 * 
	 *          保存行内人员信息
	 * @param stoEscortInfo
	 */
	public static void saveBankEscort(StoEscortInfo stoEscortInfo) {
		// 设置人员类型
		if (stoEscortInfo != null && stoEscortInfo.getUser() != null
				&& StringUtils.isNotBlank(stoEscortInfo.getUser().getUserType())) {
			stoEscortInfo.setEscortType(stoEscortInfo.getUser().getUserType());
		}
		escortInfoService.save(stoEscortInfo);
	}

	/**
	 * 
	 * @author niguoyong
	 * @version 2015-09-06
	 * 
	 *          根据用户删除人员
	 * @param userId
	 */
	public static void deleteBankEscort(String userId) {
		// 用户ID信息是否存在
		if (StringUtils.isNotBlank(userId)) {
			// 当前用户是否在人员信息中
			StoEscortInfo oldEscort = escortInfoService.findByUserId(userId);
			// 存在删除此人员
			if (oldEscort != null && StringUtils.isNotBlank(oldEscort.getId())) {
				escortInfoService.delete(oldEscort.getId());
			}
		}
	}

	/**
	 * 根据主键获取押运人员对象
	 * 
	 * @author niguoyong
	 * @version 2015-09-06
	 * 
	 * 
	 * @param id
	 * @return
	 */
	public static StoEscortInfo getEscortById(String id) {
		return escortInfoService.get(id);
	}

	/**
	 * 
	 * @author niguoyong
	 * @version 2015-09-06 * 根据用户类型分岗位查询用户
	 * @param officeId
	 * @param userType
	 * @return
	 */
	public static List<StoEscortInfo> findEscortInfoByUserType(String officeId, String userType) {
		return escortInfoService.findEscortInfoByUserType(officeId, userType);
	}

	/**
	 * 
	 * @author niguoyong
	 * @version 2015-09-06
	 * 
	 * 
	 *          根据登录名获取人员信息
	 * @param loginName
	 * @return
	 */
	public static StoEscortInfo findStoEscortInfoByLoginName(String loginName) {
		User user = systemService.getUserByLoginName(loginName);
		if (user != null) {
			return escortInfoService.findByUserId(user.getId());
		}
		return null;
	}

	/**
	 * 
	 * @author LLF
	 * @version 2015年9月14日
	 * 
	 * @param list
	 * @param officeId所属金库
	 * @param businessId业务流水
	 * @param businessType业务类型
	 * @return
	 */
	public static String changeStore(List<ChangeStoreEntity> list, String officeId, String businessId, User user) {
		return stoStoresInfoService.changeStore(list, officeId, businessId, user);
	}

	/**
	 * @author ChengShu
	 * @version 2015年9月16日
	 * 
	 * @Description 根据物品编号取得物品价值
	 * @param goods
	 *            物品编号
	 * @return 物品的价值
	 */
	public static BigDecimal getGoodsValue(String goods) {

		// 取得物品名称与物品价值关联MAP
		Map<String, BigDecimal> goodsMap = stoGoodsService.getGoodsValMap();
		// 计算总金额
		return goodsMap.get(goods);
	}

	/**
	 * @author ChengShu
	 * @version 2015年9月16日
	 * 
	 * @Description 根据物品ID取得物品名称
	 * @param goods
	 *            物品编号
	 * @return 物品名称
	 */
	public static String getGoodsName(String goodsId) {

		// 取得物品名称与物品价值关联MAP
		Map<String, String> goodsMap = stoGoodsService.getGoodsNameMap();
		// 计算总金额
		return goodsMap.get(goodsId);
	}

	/**
	 * 根据物品Id取得最新的库存信息 重空查询机构为空
	 * 
	 * @author niguoyong
	 * @return
	 */
	public static StoStoresInfo getStoStoresInfoByGoodsId(String goodsId, String officeId) {
		return stoStoresInfoService.getStoStoresInfoByGoodsId(goodsId, officeId, null);
	}

	/**
	 * 
	 * Title: getPbocStoStoresInfoByGoodsId
	 * <p>
	 * Description: 根据物品ID和所属机构ID查询物品库存
	 * </p>
	 * 
	 * @author: wangbaozhong
	 * @param goodsId
	 *            物品ID
	 * @param officeId
	 *            所属机构ID
	 * @return 物品库存 PbocStoStoresInfo 返回类型
	 */
	public static PbocStoStoresInfo getPbocStoStoresInfoByGoodsId(String goodsId, String officeId) {
		return pbocStoStoresInfoService.getPbocStoStoresInfoByGoodsId(goodsId, officeId, null);
	}

	/**
	 * 判断物品是否有库存
	 * 
	 * @author yuxixuan
	 * @param goodsId
	 *            物品ID
	 * @param officeId
	 *            机构ID：如果为空，表示查询所有机构该物品库存
	 * @param excludeZeroFg
	 *            是否排除数量为0的库存："Y"表示排除
	 * @return true:有库存；fasle：无库存
	 */
	public static Boolean checkStoresInfoExist(String goodsId, String officeId, String excludeZeroFg) {
		Boolean existFg = false;
		StoStoresInfo stoStoresInfo = stoStoresInfoService.getStoStoresInfoByGoodsId(goodsId, officeId, excludeZeroFg);
		if (stoStoresInfo != null && StringUtils.isNotBlank(stoStoresInfo.getStoId())) {
			existFg = true;
		}
		return existFg;
	}

	/**
	 * 
	 * @author LLF
	 * @version 2015年9月24日
	 * 
	 *          获取当前金库最新库存信息
	 * @param stoStoresInfoList
	 * @param officeId
	 * @return
	 */
	public static Map<String, Object> getNewestStoreInfo(String officeId) {
		return stoStoresInfoService.getNewestStoreInfo(officeId);
	}

	// /**
	// * 查询有效物品
	// *
	// * @param stoGoods
	// * @return
	// */
	// public static List<StoGoods> getGoodsList(StoGoods stoGoods) {
	// return stoGoodsService.findList(stoGoods);
	// }

	/**
	 * 
	 * @author LLF
	 * @version 2015年9月28日
	 * 
	 *          修改物品库存预剩余库存数量
	 * @param list
	 * @param officeId
	 */
	@Transactional(readOnly = false)
	public static String changeSurplusStore(List<ChangeStoreEntity> list, String officeId) {
		return stoStoresInfoService.changeSurplusStore(list, officeId);
	}

	/**
	 * 
	 * Title: changePbocSurplusStore
	 * <p>
	 * Description: 修改人行预剩库存
	 * </p>
	 * 
	 * @author: wangbaozhong
	 * @param list
	 *            变更物品列表
	 * @param officeId
	 *            机构ID
	 * @return String 返回类型
	 */
	@Transactional(readOnly = false)
	public static String changePbocSurplusStore(List<ChangeStoreEntity> list, String officeId) {
		return pbocStoStoresInfoService.changeSurplusStore(list, officeId);
	}

	/**
	 * 
	 * Title: changePbocStore
	 * <p>
	 * Description: 修改人行库存
	 * </p>
	 * 
	 * @author: wangbaozhong
	 * @param list
	 *            变更物品列表
	 * @param officeId
	 *            机构ID
	 * @param allId
	 *            流水单号
	 * @param updateUser
	 *            修改人
	 * @return String 返回类型
	 */
	@Transactional(readOnly = false)
	public static String changePbocStore(List<ChangeStoreEntity> list, String officeId, String allId, User updateUser) {
		return pbocStoStoresInfoService.changeStore(list, officeId, allId, updateUser);
	}

	/**
	 * 
	 * @author LLF
	 * @version 2015年9月28日
	 * 
	 *          修改库存数量和剩余库存数量
	 * @param list
	 * @param officeId
	 * @param businessId
	 * @param user
	 * @return
	 */
	@Transactional(readOnly = false)
	public static String changeStoreAndSurplusStores(List<ChangeStoreEntity> list, String officeId, String businessId,
			User user) {

		String message = stoStoresInfoService.changeStore(list, officeId, businessId, user);

		if (Constant.SUCCESS.equals(message)) {
			message = stoStoresInfoService.changeSurplusStore(list, user.getOffice().getId());
		}
		return message;
	}

	/**
	 * 
	 * @author chengshu
	 * @version 2016年6月15日
	 * 
	 *          修改人民银行库存数量和剩余库存数量
	 * @param list
	 * @param officeId
	 * @param businessId
	 * @param user
	 * @return
	 */
	@Transactional(readOnly = false)
	public static String changePbocStoreAndSurplusStores(List<ChangeStoreEntity> list, String officeId,
			String businessId, User user) {

		String message = pbocStoStoresInfoService.changeStore(list, officeId, businessId, user);

		if (Constant.SUCCESS.equals(message)) {
			message = pbocStoStoresInfoService.changeSurplusStore(list, officeId);
		}
		return message;
	}

	/**
	 * 
	 * @author LLF
	 * @version 2015年12月23日
	 * 
	 *          重空装配修改物品库存和重空库存
	 * @param list
	 * @param officeId
	 * @param businessId
	 * @param user
	 * @return
	 */
	@Transactional(readOnly = false)
	public static String changeStoreBlankBillStores(List<ChangeStoreEntity> list, String officeId, String businessId,
			User user) {
		String message = changeStoreAndSurplusStores(list, officeId, businessId, user);
		if (Constant.SUCCESS.equals(message)) {
			message = stoEmptyDocumentService.changeBlankBillStores(list, user.getOffice().getId());
		}
		return message;
	}

	/**
	 * 
	 * @author LLF
	 * @version 2015年12月16日
	 * 
	 * 
	 * @param list
	 * @return
	 */
	public static String changeBlankBillStore(List<ChangeStoreEntity> list) {
		String message = "";
		return message;
	}

	/**
	 * 
	 * @author niguoyong
	 * @version 2015-09-21
	 * 
	 *          物品ID解析成物品信息
	 * @param goodsID
	 *            物品ID
	 * @return 物品
	 */
	public static StoGoodSelect splitGood(String goodsID) {
		StoGoodSelect stoGoodSelect = new StoGoodSelect();
		// 币种
		stoGoodSelect.setCurrency(StringUtils.mid(goodsID, 0, 3));
		// 类别
		stoGoodSelect.setClassification(StringUtils.mid(goodsID, 3, 2));
		// 套别
		stoGoodSelect.setEdition(StringUtils.mid(goodsID, 5, 1));
		// 软/硬币
		stoGoodSelect.setCash(StringUtils.mid(goodsID, 6, 1));
		// 面值
		stoGoodSelect.setDenomination(StringUtils.mid(goodsID, 7, 2));
		// 单位
		stoGoodSelect.setUnit(StringUtils.mid(goodsID, 9, 3));

		return stoGoodSelect;
	}

	/**
	 * 
	 * @author chengshu
	 * @version 2015-10-16
	 * 
	 *          根据机构ID，取得机构
	 * @param officeId
	 *            机构ID
	 * @return 机构
	 */
	public static Office getOfficeById(String officeId) {

		return boxInfoService.getOfficeById(officeId);
	}

	/**
	 * 
	 * @author chengshu
	 * @version 2015-10-16
	 * 
	 *          根据机构ID，取得机构
	 * @param boxList
	 *            箱号列表
	 * @param status
	 *            状态
	 */
	@Transactional(readOnly = false)
	public static int updateBoxStatusBatch(List<String> boxList, String status, User user) {
		int i;
		// 更改箱袋状态
		i = boxInfoService.updateStatusBatch(boxList, status, user);
		if (i == 0) {
			String strMessageContent = "箱袋信息更新失败！";
			throw new BusinessException(ExternalConstant.HardwareInterface.ERROR_NO_E99, strMessageContent);
		}
		return i;
	}

	/**
	 * 根据物品ID取得物品名称
	 * 
	 * @author yuxixuan
	 * @version 2015-10-28
	 * @param goodsId
	 *            物品ID
	 * @return 物品名称
	 */
	public static String getGoodsNameById(String goodsId) {
		String goodsName = "";
		StoGoods stoGoods = stoGoodsService.get(goodsId);
		if (stoGoods != null) {
			goodsName = stoGoods.getGoodsName();
		}
		return goodsName;
	}

	/**
	 * 根据物品ID获取物品信息
	 * 
	 * @author WangBaozhong
	 * @version 2016年5月19日
	 * 
	 * 
	 * @param goodsId
	 *            物品ID
	 * @return 物品信息
	 */
	public static StoGoods getGoodsInfoById(String goodsId) {
		StoGoods stoGoods = stoGoodsService.get(goodsId);
		return stoGoods;
	}

	/**
	 * 更新箱子状态和出库预约时间
	 * 
	 * @author chengshu
	 * @version 2015-10-28
	 * @param boxInfo
	 *            箱袋信息
	 * @return 更新个数
	 */
	@Transactional(readOnly = false)
	public static int updateOutdateBatch(List<String> boxList, Date outDate, User user) {
		int i;
		// 更改箱袋状态
		i = boxInfoService.updateOutdateBatch(boxList, outDate, user);
		if (i == 0) {
			String strMessageContent = "箱袋信息更新失败！";
			throw new BusinessException(ExternalConstant.HardwareInterface.ERROR_NO_E99, strMessageContent);
		}
		return i;
	}

	/**
	 * 生成重空物品ID
	 * 
	 * @author yuxixuan
	 * @param stoGoods
	 * @return
	 */
	public static String genBlankBillId(StoGoods stoGoods) {
		String id = "";
		if (stoGoods != null && stoGoods.getStoBlankBillSelect() != null) {
			String idTemp = stoGoods.getStoBlankBillSelect().getBlankBillKind()
					+ stoGoods.getStoBlankBillSelect().getBlankBillType();
			id = StoreCommonUtils.fillStr(idTemp, 12, "0", Constant.fillStrDirection.FILL_STR_RIGHT);
		}
		return id;
	}

	/**
	 * 取得物品变更列表
	 * 
	 * @author yuxixuan
	 * @param stoGoods
	 * @return
	 */
	public static List<StoStoresHistory> findStoStoresHistoryList(StoStoresHistory stoStoresHistory) {

		// 取得重空变更列表
		List<StoStoresHistory> historyList = stoStoresInfoService.findStoStoresHistoryList(stoStoresHistory);
		// 重新设置时间格式
		for (StoStoresHistory history : historyList) {
			history.setTime(
					DateUtils.formatDate(history.getCreateDate(), AllocationConstant.Dates.FORMATE_YYYY_MM_DD_HH_MM));
			history.setStoStatusName(DictUtils.getDictLabel(history.getStoStatus(), "all_businessType", ""));
		}

		return historyList;
	}

	/**
	 * 取得绑定调拨物品库区信息列表
	 * 
	 * @author WangBaozhong
	 * @version 2016年5月23日
	 * 
	 * 
	 * @param allocateItemList
	 *            调拨物品信息
	 * @param intervalDays
	 *            查询物品间隔日数
	 * @param errorMessageCode
	 *            出错消息代码
	 * @param errorGoodsId
	 *            出错物品ID
	 * @param errorAllId
	 *            出错流水单号
	 * @param officeId
	 *            所属金库ID
	 * @return 绑定物品库区信息列表
	 */
	public static List<AllAllocateGoodsAreaDetail> getBindingAreaInfoToDetail(
			List<PbocAllAllocateItem> allocateItemList, int intervalDays, String errorMessageCode, String errorGoodsId,
			String errorAllId, String officeId) {
		return stoGoodsLocationInfoService.getBindingAreaInfoToDetail(allocateItemList, intervalDays, errorMessageCode,
				errorGoodsId, errorAllId, officeId);
	}

	/**
	 * 根据物品ID取得物品在库区内的数量
	 * 
	 * @author WangBaozhong
	 * @version 2016年6月6日
	 * 
	 * 
	 * @param goodsId
	 *            物品ID
	 * @param officeId
	 *            所属金库ID
	 * @return 物品在库区内的数量
	 */
	public static long getGoodsNumInStoreAreaByGoodsId(String goodsId, String officeId) {
		return stoGoodsLocationInfoService.getGoodsNumInStoreAreaByGoodsId(goodsId, officeId);
	}

	// /**
	// * 获取人行物品缩略名称（人行用）
	// * @author WangBaozhong
	// * @version 2016年5月31日
	// *
	// *
	// * @param goodsName 物品原名
	// * @return 截取后的物品名称，如（纸币_50元）
	// */
	// public static String getPbocGoodsName(String goodsName) {
	// String strNewGoodsName = "";
	// if (StringUtils.isNotBlank(goodsName)) {
	// String[] goodsPropertyArray = goodsName.split("_");
	// if (goodsPropertyArray.length == 6) {
	// strNewGoodsName = goodsPropertyArray[3] + "_" + goodsPropertyArray[4];
	// }
	// }
	//
	// return strNewGoodsName;
	// }
	//
	/**
	 * 获取人行物品缩略名称（人行用）
	 * 
	 * @author WangBaozhong
	 * @version 2016年5月31日
	 * 
	 * 
	 * @param goodsId
	 *            物品ID
	 * @return 截取后的物品名称，如（纸币_50元）
	 */
	public static String getPbocGoodsNameByGoodId(String goodsId) {
		String strOldGoodsName = getGoodsNameById(goodsId);
		String strNewGoodsName = "";
		if (StringUtils.isNotBlank(strOldGoodsName)) {
			String[] goodsPropertyArray = strOldGoodsName.split("_");
			if (goodsPropertyArray.length == 6) {
				strNewGoodsName = goodsPropertyArray[3] + "_" + goodsPropertyArray[4];
			}
		}

		return strNewGoodsName;
	}

	/**
	 * 获取人行物品缩略名称（人行用）
	 * 
	 * @author WangBaozhong
	 * @version 2016年5月31日
	 * 
	 * 
	 * @param goodsId
	 *            物品ID
	 * @return 截取后的物品名称，如（纸币50元(4套)）
	 */
	public static String getPbocGoodsNameByGoodId2(String goodsId) {
		String strOldGoodsName = getGoodsNameById(goodsId);
		String strNewGoodsName = "";
		if (StringUtils.isNotBlank(strOldGoodsName)) {
			String[] goodsPropertyArray = strOldGoodsName.split("_");
			if (goodsPropertyArray.length >= 6) {
				strNewGoodsName = goodsPropertyArray[3] + goodsPropertyArray[4]
						+ Constant.Punctuation.HALF_LEFT_ROUND_BRACKETS + goodsPropertyArray[2]
						+ Constant.Punctuation.HALF_RIGHT_ROUND_BRACKETS;
			}
		}

		return strNewGoodsName;
	}

	/**
	 * 获取人行物品面值（人行用）
	 * 
	 * @author WangBaozhong
	 * @version 2016年9月02日
	 * 
	 * 
	 * @param goodsId
	 *            物品ID
	 * @return 截取后的物品名称，如（50元）
	 */
	public static String getPbocGoodsDenominationByGoodId(String goodsId) {
		String strOldGoodsName = getGoodsNameById(goodsId);
		String strNewGoodsName = "";
		if (StringUtils.isNotBlank(strOldGoodsName)) {
			String[] goodsPropertyArray = strOldGoodsName.split("_");
			if (goodsPropertyArray.length == 6) {
				strNewGoodsName = goodsPropertyArray[4];
			}
		}

		return strNewGoodsName;
	}

	/**
	 * 根据RFID和出入库流水单号查询物品所在库区位置信息
	 * 
	 * @author WangBaozhong
	 * @version 2016年6月7日
	 * 
	 * 
	 * @param stoGoodsLocationInfo
	 *            查询条件
	 * @return 物品所在库区位置信息
	 */
	public static StoGoodsLocationInfo getGoodsLocationInfoByAllIDAndRfid(StoGoodsLocationInfo stoGoodsLocationInfo) {
		return stoGoodsLocationInfoService.getByAllIDAndRfid(stoGoodsLocationInfo);
	}

	/**
	 * 批量更新RFID状态
	 * 
	 * @author WangBaozhong
	 * @version 2016年6月7日
	 * 
	 * 
	 * @param rfidList
	 *            RFID列表
	 * @param status
	 *            状态
	 * @param userId
	 *            更新人ID
	 * @param userName
	 *            更新人名称
	 * @return 更新数量
	 */
	public static int updateRfidStatus(List<String> rfidList, String status, String userId, String userName,
			String curBusinessType) {
		return stoRfidDenominationService.updateRfidStatus(rfidList, status, userId, userName, curBusinessType);
	}

	/**
	 * 批量更新RFID状态
	 * 
	 * @author WangBaozhong
	 * @version 2016年6月7日
	 * 
	 * 
	 * @param rfidList
	 *            RFID列表
	 * @param status
	 *            状态
	 * @param userId
	 *            更新人ID
	 * @param userName
	 *            更新人名称
	 * @param rfidUseFlag
	 *            rfid使用状态
	 * @param curBusinessType
	 *            当前业务类型
	 * @return 更新数量
	 */
	public static int updateRfidStatus(List<String> rfidList, String status, String userId, String userName,
			String rfidUseFlag, Office atOffice, String curBusinessType) {
		return stoRfidDenominationService.updateRfidStatus(rfidList, status, userId, userName, rfidUseFlag, atOffice,
				curBusinessType);
	}
	
	/**
	 * 批量更新RFID状态(清除库区用) 
	 * @author WangBaozhong
	 * @version 2017年11月2日
	 * 
	 *  
	 * @param rfidList RFID列表
	 * @param status 状态
	 * @param userId 更新人ID
	 * @param userName 更新人名称
	 * @param rfidUseFlag rfid使用状态
	 * @param curBusinessType 当前业务类型
	 * @return 更新数量
	 */
	@Transactional(readOnly = false)
	public static int updateRfidStatusForClear(List<String> rfidList, String status, String userId, String userName, 
			String rfidUseFlag, Office atOffice, String curBusinessType) {
		return stoRfidDenominationService.updateRfidStatusForClear(rfidList, status, userId, userName, rfidUseFlag, atOffice, curBusinessType);
	}

	/**
	 * 根据流水单号和实际出库RFID更新库区内物品状态 将审批时预订物品还原为未使用，将实际出库物品更新为已使用
	 * 
	 * @author WangBaozhong
	 * @version 2016年6月8日
	 * 
	 * 
	 * @param allId
	 *            出库流水单号
	 * @param actualRfidList
	 *            实际出库rfid列表
	 * @param reserveRfidList
	 *            预订出库rfid列表
	 * @param updateUser
	 *            更新者信息
	 * @throws BusinessException
	 *             更新失败时返回异常
	 */
	public static void updateGoodsOutStoreStatus(String allId, List<String> actualRfidList,
			List<String> reserveRfidList, User updateUser, String officeId) {
		stoGoodsLocationInfoService.updateGoodsOutStoreStatus(allId, actualRfidList, reserveRfidList, updateUser,officeId);
	}

	/**
	 * 
	 * Title: getGoodsListFromLocation
	 * <p>
	 * Description: 根据库区位置信息查询物品
	 * </p>
	 * 
	 * @author: wangbaozhong
	 * @param entity
	 *            库区位置信息
	 * @return 物品列表 List<StoGoodsLocationInfo> 返回类型
	 */
	public static List<StoGoodsLocationInfo> getGoodsListFromLocation(StoGoodsLocationInfo entity) {
		return stoGoodsLocationInfoService.findList(entity);
	}

	/**
	 * 根据原封箱号获取原封箱信息
	 * 
	 * @author WangBaozhong
	 * @version 2016年7月12日
	 * 
	 * 
	 * @param boxId
	 *            原封箱号
	 * @param officeId
	 *            登记机构ID
	 * @return 原封箱信息
	 */
	public static StoOriginalBanknote getStoOriginalBanknoteByBoxId(String boxId, String officeId) {
		return stoOriginalBanknoteService.getOriginalBanknoteById(boxId, officeId);
	}

	/**
	 * 根据位置ID变更物品预订状态
	 * 
	 * @author WangBaozhong
	 * @version 2016年6月2日
	 * 
	 * 
	 * @param stoGoodsLocationInfo
	 *            更新信息
	 * @return 变更数量
	 */
	@Transactional(readOnly = false)
	public static int updateReserveGoodsStatus(String id, String status) {
		StoGoodsLocationInfo locationInfo = new StoGoodsLocationInfo();
		locationInfo.setId(id);
		locationInfo.setDelFlag(status);
		return stoGoodsLocationInfoService.updateReserveGoodsStatus(locationInfo);
	}

	/**
	 * 根据RFID修改物品出库状态
	 * 
	 * @author WangBaozhong
	 * @version 2016年6月8日
	 * 
	 * 
	 * @return 更新数量
	 */
	@Transactional(readOnly = false)
	public static int updateOutStoreGoodsStatusByRfid(String rfid, String status,String officeId) {
		StoGoodsLocationInfo locationInfo = new StoGoodsLocationInfo();
		locationInfo.setRfid(rfid);
		locationInfo.setDelFlag(status);
		locationInfo.preUpdate();
		locationInfo.setOfficeId(officeId);
		return stoGoodsLocationInfoService.updateOutStoreGoodsStatusByRfid(locationInfo);
	}

	/**
	 * 
	 * Title: getCommercialBankUserInfoList
	 * <p>
	 * Description: 获取当前登录金融机构金库管理员和操作员信息
	 * </p>
	 * 
	 * @author: wangbaozhong
	 * @return List<StoEscortInfo> 返回类型
	 */
	public static List<StoEscortInfo> getCommercialBankUserInfoList() {
		String officeId = UserUtils.getUser().getOffice().getId();
		return escortInfoService.getCommercialBankUserInfoList(Constant.Escort.ALL_ESCORT_LIST, officeId);
	}

	/**
	 * 
	 * Title: getCommercialBankUserInfoList
	 * <p>
	 * Description: 获取当前登录金融机构金库管理员和操作员信息
	 * </p>
	 * 
	 * @author: wangbaozhong
	 * @param officeId
	 *            所属机构ID
	 * @return List<StoEscortInfo> 返回类型
	 */
	public static List<StoEscortInfo> getCommercialBankUserInfoList(String officeId) {
		return escortInfoService.getCommercialBankUserInfoList(Constant.Escort.ALL_ESCORT_LIST, officeId);
	}

	/**
	 * 
	 * Title: getStoDocTempInfo
	 * <p>
	 * Description: 按照业务类型和状态查询单据印章表
	 * </p>
	 * 
	 * @author: wangbaozhong
	 * @param businessType
	 *            业务类型
	 * @param status
	 *            状态
	 * @return StoDocTempInfo 返回类型
	 */
	@Transactional(readOnly = true)
	public static StoDocTempInfo getStoDocTempInfo(String businessType, String status, String officeId) {
		return stoDocStamperMgrService.getByBusinessAndStatus(businessType, status, officeId);
	}

	/**
	 * 
	 * Title: getById
	 * <p>
	 * Description: 根据ID查询机构印章
	 * </p>
	 * 
	 * @author: wangbaozhong
	 * @param id
	 * @return StoOfficeStamperInfo 返回类型
	 */
	@Transactional(readOnly = true)
	public static StoOfficeStamperInfo getStoOfficeStamperInfoById(String id) {
		return stoOfficeStamperInfoService.getById(id);
	}

	/**
	 * 
	 * Title: findByUserId
	 * <p>
	 * Description: 根据用户Id查询当前人员
	 * </p>
	 * 
	 * @author: wangbaozhong
	 * @param userId
	 *            用户Id
	 * @return StoEscortInfo 返回类型
	 */
	@Transactional(readOnly = true)
	public static StoEscortInfo findByUserId(String userId) {
		return escortInfoService.findByUserId(userId);
	}

	/**
	 * 
	 * Title: addDateStamper
	 * <p>
	 * Description:
	 * </p>
	 * 
	 * @author: zhengkaiyuan
	 * @param imageBytes
	 * @param color
	 * @return
	 * @throws Exception
	 *             byte[] 返回类型
	 */
	public static byte[] addDateStamper(byte[] imageBytes, Color color) throws Exception {
		InputStream inputStream = new ByteArrayInputStream(imageBytes);
		BufferedImage bufferedImage = ImageIO.read(inputStream);
		Graphics2D graphics2d = bufferedImage.createGraphics();
		graphics2d.setColor(color);
		Font font = new Font("宋体", Font.BOLD, 32);
		graphics2d.setFont(font);
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy.MM.dd");
		String dateString = simpleDateFormat.format(new Date());
		FontMetrics fontMetrics = graphics2d.getFontMetrics(font);
		int width = fontMetrics.stringWidth(dateString);
		int height = fontMetrics.getHeight();
		graphics2d.drawString(dateString, (bufferedImage.getWidth() - width) / 2,
				(bufferedImage.getHeight() - height) / 2 + 32);
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		ImageIO.write(bufferedImage, "PNG", byteArrayOutputStream);
		return byteArrayOutputStream.toByteArray();
	}

	/**
	 * 
	 * Title: insertInToHistory
	 * <p>
	 * Description: 插入RFID历史 表
	 * </p>
	 * 
	 * @author: wangbaozhong
	 * @param rfidList
	 *            RFID列表
	 * @param office
	 *            当前机构 void 返回类型
	 */
	@Transactional(readOnly = false)
	public static void insertInToRfidDemHistory(List<String> rfidList, Office updateOffice) {
		stoRfidDenominationService.insertInToHistory(rfidList, updateOffice);
	}

	/**
	 * 
	 * Title: getByStoreAreaId
	 * <p>
	 * Description: 根据库房区域ID取得区域信息
	 * </p>
	 * 
	 * @author: wangbaozhong
	 * @param storeAreaId
	 *            库房区域ID
	 * @return 库房区域信息 StoAreaSettingInfo 返回类型
	 */
	public static StoAreaSettingInfo getByStoreAreaId(String storeAreaId) {
		return stoAreaSettingInfoService.getByStoreAreaId(storeAreaId);
	}

	/**
	 * 根据条件，重新绑定RIFD
	 * 
	 * @author WangBaozhong
	 * @version 2017年4月20日
	 * 
	 * 
	 * @param bussnessType
	 *            业务类型
	 * @param loginUser
	 *            当前登录用户
	 * @param rfidDenominationList
	 *            RFID列表
	 * @param allId
	 *            当前业务流水单号
	 */
	@Transactional(readOnly = false)
	public static void reBindingRfid(String bussnessType, User loginUser,
			List<StoRfidDenomination> rfidDenominationList, String allId) {
		stoRfidDenominationService.reBindingRfid(bussnessType, loginUser, rfidDenominationList, allId);
	}

	/**
	 * 
	 * Title: findAllList
	 * <p>
	 * Description: 按条件查询物品信息列表
	 * </p>
	 * 
	 * @author: wangbaozhong
	 * @param stoGoods
	 *            查询条件
	 * @return 物品信息列表 List<StoGoods> 返回类型
	 */
	@Transactional(readOnly = false)
	public static List<StoGoods> findAllGoodsList(StoGoods stoGoods) {
		return stoGoodsService.findAllList(stoGoods);
	}

	/**
	 * 根据多个箱号，查询多个箱子
	 * 
	 * @author liuyaowen
	 * @version 2017-7-11
	 * 
	 * @param boxNo
	 *            箱号
	 * @return
	 */
	public static List<StoBoxDetail> getBoxDetailList(String boxNo) {
		StoBoxDetail stoBoxDetail = new StoBoxDetail();
		stoBoxDetail.setBoxNo(boxNo);
		List<StoBoxDetail> list = boxInfoService.findStoBoxDetailList(stoBoxDetail);
		return list;
	}

	/**
	 * 将金额转化为大写字符
	 * 
	 * @author SongYuanYang
	 * @version 2017-7-20
	 * 
	 * @param amount
	 *            金额
	 * @return
	 */
	public static String getUpperAmount(String amount) {
		if (StringUtils.isBlank(amount)) {
			return "";
		}
		String upperAmount = NumToRMB.changeToBig(Double.parseDouble(amount));
		return upperAmount;
	}

	/**
	 * @author LLF
	 * @version 2017-7-30
	 * @return
	 */
	public static List<StoCarInfo> getStoCarInfoList(String id) {
		Office office = new Office();
		office.setId(id);
		StoCarInfo stoCarInfo = new StoCarInfo();
		stoCarInfo.setOffice(office);
		List<StoCarInfo> carList = stoCarInfoService.findList(stoCarInfo);
		List<StoRouteInfo> routeList = routeInfoService.findList(new StoRouteInfo());
		List<StoCarInfo> list = Lists.newArrayList();
		List<StoCarInfo> carOffice = Lists.newArrayList();

		for (int i = 0; i < carList.size(); i++) {
			for (int j = 0; j < routeList.size(); j++) {
				if (carList.get(i).getCarNo().equals(routeList.get(j).getCarNo())) {
					list.add(carList.get(i));
				}
				if (!(carList.get(i).getOffice().getId().equals(id))) {
					carOffice.add(carList.get(i));
				}
			}
		}
		carList.removeAll(list);
		carList.removeAll(carOffice);
		return carList;
	}

	/**
	 * 获取车辆列表
	 * 
	 * @author yanbingxu
	 * @version 2017-8-1
	 * @return
	 */
	public static List<StoCarInfo> getStoCarInfoAllList() {
		StoCarInfo stoCarInfo = new StoCarInfo();
		return stoCarInfoService.findList(stoCarInfo);
	}
	
	/**
	 * 获取加钞组信息列表
	 * 
	 * @author wanglu
	 * @version 2017-11-14
	 * @return
	 */
	public static List<StoAddCashGroup> getStoAddCashGroupList() {
		StoAddCashGroup stoAddCashGroup = new StoAddCashGroup();
		// 添加机构筛选条件 修改人：xl 修改时间：2017-12-29 begin
		stoAddCashGroup.setOffice(UserUtils.getUser().getOffice());
		// end
		return stoAddCashGroupService.findList(stoAddCashGroup);
	}

	/**
	 * 获取车牌开头
	 * 
	 * @author yanbingxu
	 * @version 2017-8-16
	 * @return
	 */
	public static List<String> getCarHeaderList() {
		List<String> list = Lists.newArrayList();
		String carHeader = "京,津,沪,渝,冀,豫,云,辽,黑,湘,皖,鲁,新,苏,浙,赣,鄂,桂,甘,晋,蒙,陕,吉,闽,贵,粤,青,藏,川,宁,琼,使,领";
		for (String header : carHeader.split(",")) {
			list.add(header);
		}
		return list;
	}

	/**
	 * 获取当前机构所有车辆列表
	 * 
	 * @author xp
	 * @version 2017-8-22
	 * @return
	 */
	public static List<StoCarInfo> getAllStoCarInfoList(String id) {
		Office office = new Office();
		office.setId(id);
		StoCarInfo stoCarInfo = new StoCarInfo();
		stoCarInfo.setOffice(office);
		List<StoCarInfo> carList = stoCarInfoService.findList(stoCarInfo);
		return carList;
	}
	/**
	 * 获取门店列表
	 * 
	 * @author ZXK
	 * @version 2019-7-5
	 * @return
	 */
	public static List<Office> getDoorList() {
		List<Office> list = officeService.selectStore();
		return list;
	}
	

	/**
	 * 按登记机构所在线路查询车辆
	 * 
	 * @author qph
	 * @version 2017-8-22
	 * @return
	 */
	public static List<StoCarInfo> getStoCarInfoListByPoint(String officeId) {

		StoRouteInfo stoRouteInfo = routeInfoService.searchStoRouteInfoByOfficeId(officeId);
		StoCarInfo stoCarInfo = new StoCarInfo();
		List<StoCarInfo> carList = Lists.newArrayList();
		if (stoRouteInfo != null) {
			stoCarInfo.setCarNo(stoRouteInfo.getCarNo());
			carList = stoCarInfoService.findList(stoCarInfo);

		}
		return carList;
	}

	/**
	 * 
	 * * 获取用户，根据用户类型和机构过滤
	 * 
	 * @author QPH
	 * @version 2017年8月30日
	 * @param userType
	 *            用户类型
	 * @param officeId
	 *            机构ID
	 * @return 用户集合
	 */
	public static List<StoEscortInfo> getUsersByTypeAndOffice(String userType, String officeId) {
		List<StoEscortInfo> userList = escortInfoService.findEscortInfoByUserType(officeId, userType);
		return userList;
	}

	/**
	 * 获取机构下拉列表
	 * 
	 * @author sg
	 * @version 2017-09-18
	 * @param types
	 *            机构类型
	 * 
	 * @return 系统机构
	 */
	public static List<Office> getStoCustList(String types, boolean isAll) {
		List<String> list = null;
		if (types != null && !"".equals(types)) {
			String[] typea = types.split(",");
			list = Arrays.asList(typea);
		}
		Office office = UserUtils.getUser().getOffice();
		String officeId = null;
		String officeParentId = null;
		if (isAll == false) {
			//清分中心看上级人行下属的所有机构，其他只看自己下属机构
			if (office.getType().equals(Constant.OfficeType.CLEAR_CENTER)) {
				officeId = office.getId();
				officeParentId = office.getParentId();
			}else {
				officeId=office.getId();
				officeParentId=officeId;
			}
		}
		return officeService.findCustList(officeId, officeParentId, list);
	}

	/**
	 * 获取机构对应下拉列表
	 * 
	 * @author wzj
	 * @version 2017-11-29
	 * @param types
	 *            机构类型
	 * 
	 * @return 系统机构
	 */
	public static List<Office> getStoCorrespondenceCustList(String types, boolean isAll) {
		List<String> list = null;
		if (types != null && !"".equals(types)) {
			String[] typea = types.split(",");
			list = Arrays.asList(typea);
		}
		Office office = UserUtils.getUser().getOffice();
		String officeId = null;
		String officeParentId = null;
		if (isAll == false) {
			officeId = office.getId();
			officeParentId = office.getParentId();
		}
		// 清分中心
		if (office.getType().equals(Constant.OfficeType.CLEAR_CENTER)) {
			List<Office> lists = Lists.newArrayList();
			lists.add(office);
			return lists;
		} else {
			return officeService.findCustList(officeId, officeParentId, list);
		}
	}
	/**
	 * 根据机构ID获取员工信息
	 * 
	 * @author sg
	 * @version 2017-09-20
	 * @param officeId
	 *            机构Id
	 * 
	 * @return 员工信息
	 */
	public static List<StoEscortInfo> getBankUserList(String officeId) {

		return escortInfoService.findBankUserList(officeId);
	}

	/**
	 * 
	 * Title: getBoxInfoByRfidOrBoxNo
	 * <p>
	 * Description: 根据rfid或箱号查询箱袋记录
	 * </p>
	 * 
	 * @author: wanghan
	 * @param boxInfo
	 *            箱袋实体类对象
	 * @return 箱袋实体类对象 StoBoxInfo 返回类型
	 */
	public static StoBoxInfo getBoxInfo(StoBoxInfo boxInfo) {
		return boxInfoService.get(boxInfo);
	}
	
	/**
	 * 
	 * Title: getRouteInfoListByOfficeId
	 * <p>Description: 根据机构信息查询线路信息列表</p>
	 * @author:     wangbaozhong
	 * @param curOffice	机构信息
	 * @return 线路信息列表
	 * List<StoRouteInfo>    返回类型
	 */
	public static List<StoRouteInfo> getRouteInfoListByOfficeId(String officeId) {
		return routeInfoService.findRouteInfoListByOfficeId(officeId);
	}

	/**
	 * 根据条件查询机构List
	 * 
	 * @author qph
	 * @version 2018-01-17
	 * @param office
	 *            机构
	 * 
	 * @return 机构list
	 */
	public static List<Office> findOfficeListBySearch(Office office) {

		return officeService.findListBySearch(office);
	}
	

	/**
	 * 获取数字化金融服务平台机构
	 * @version 2019年7月10日
	 * @author: wqj
	 */
	public static List<Office> getPlatform(){
		return officeService.getPlatform();
	}
	/**
	 * 获取状态为运行的任务
	 * @version 2019年12月03日
	 * @author: wangpengyu
	 * 
	 */
	public static List<Quartz> selectQuartzAll(){
		return quartzService.findList();
	}
	/**
	 * 获取状态为运行的任务
	 * @version 2019年12月10日
	 * @author: wangpengyu
	 * 
	 */
	public static Quartz selectOfficeId(String jobName){
		Quartz quartz = new Quartz();
		quartz.setJobName(jobName);
		return quartzService.getJob(quartz);
	}
}
