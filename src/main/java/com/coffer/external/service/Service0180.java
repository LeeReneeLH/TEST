package com.coffer.external.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.coffer.businesses.common.Constant;
import com.coffer.businesses.modules.store.v01.entity.StoBoxDetail;
import com.coffer.businesses.modules.store.v01.entity.StoBoxInfo;
import com.coffer.businesses.modules.store.v01.service.StoBoxInfoService;
import com.coffer.core.common.utils.Collections3;
import com.coffer.core.common.utils.StringUtils;
import com.coffer.core.modules.sys.DbConfigConstant;
import com.coffer.core.modules.sys.entity.Office;
import com.coffer.core.modules.sys.entity.User;
import com.coffer.core.modules.sys.utils.DbConfigUtils;
import com.coffer.external.ExternalConstant;
import com.coffer.external.Parameter;
import com.coffer.external.hessian.HardwardBaseService;

@Component("Service0180")
@Scope("singleton")
public class Service0180 extends HardwardBaseService {

	@Autowired
	private StoBoxInfoService stoBoxInfoService;

	/**
	 * 
	 * @author xp PDA网点装箱
	 * @version 2017-07-07
	 * 
	 * @Description
	 * @param paramMap
	 * @return
	 */
	@Transactional(readOnly = false)
	public String execute(Map<String, Object> paramMap) {
		Map<String, Object> map = new HashMap<String, Object>();
		// 版本号、服务代码
		map.put(Parameter.VERSION_NO_KEY, paramMap.get(Parameter.VERSION_NO_KEY));
		map.put(Parameter.SERVICE_NO_KEY, paramMap.get(Parameter.SERVICE_NO_KEY));
		// try {
		// 创建查询的箱袋基本信息对象
		StoBoxInfo stoBoxInfo = new StoBoxInfo();
		// 解析所属机构编号
		if (paramMap.get(Parameter.OFFICE_ID_KEY) == null
				|| StringUtils.isEmpty(paramMap.get(Parameter.OFFICE_ID_KEY).toString())) {
			map.put(Parameter.RESULT_FLAG_KEY, ExternalConstant.HardwareInterface.RESULT_FLAG_FAILURE);
			map.put(Parameter.ERROR_NO_KEY, ExternalConstant.HardwareInterface.ERROR_NO_E03);
			map.put(Parameter.ERROR_MSG_KEY, "机构编号不能为空！");
			return gson.toJson(map);
		}
		String useType = DbConfigUtils.getDbConfig(DbConfigConstant.outletBoxAdd.ALLOCATION_BOXADDMANAGE_USE);
		if (DbConfigConstant.outletBoxAdd.USE_BOXNO.equals(useType)
				|| DbConfigConstant.outletBoxAdd.USE_ALL.equals(useType)) {
			// 解析箱袋编号
			if (paramMap.get(Parameter.BOX_NO_KEY) == null
					|| StringUtils.isEmpty(paramMap.get(Parameter.BOX_NO_KEY).toString())) {
				map.put(Parameter.RESULT_FLAG_KEY, ExternalConstant.HardwareInterface.RESULT_FLAG_FAILURE);
				map.put(Parameter.ERROR_NO_KEY, ExternalConstant.HardwareInterface.ERROR_NO_E03);
				map.put(Parameter.ERROR_MSG_KEY, "箱袋编号不能为空！");
				return gson.toJson(map);
			}
			// 插入箱袋编号
			stoBoxInfo.setId(paramMap.get(Parameter.BOX_NO_KEY).toString());
			// 解析箱袋类型
			if (paramMap.get(Parameter.BOX_TYPE_KEY) == null
					|| StringUtils.isEmpty(paramMap.get(Parameter.BOX_TYPE_KEY).toString())) {
				map.put(Parameter.RESULT_FLAG_KEY, ExternalConstant.HardwareInterface.RESULT_FLAG_FAILURE);
				map.put(Parameter.ERROR_NO_KEY, ExternalConstant.HardwareInterface.ERROR_NO_E03);
				map.put(Parameter.ERROR_MSG_KEY, "箱袋类型不能为空！");
				return gson.toJson(map);
			} else {
				// 判断箱袋类型，只有款箱才能进行装箱登记
				if (!paramMap.get(Parameter.BOX_TYPE_KEY).equals(Constant.BoxType.BOX_PARAGRAPH)) {
					map.put(Parameter.RESULT_FLAG_KEY, ExternalConstant.HardwareInterface.RESULT_FLAG_FAILURE);
					map.put(Parameter.ERROR_NO_KEY, ExternalConstant.HardwareInterface.ERROR_NO_E03);
					map.put(Parameter.ERROR_MSG_KEY, "箱袋类型必须为款箱！");
					return gson.toJson(map);
				}
			}
			// 插入箱袋类型
			stoBoxInfo.setBoxType(paramMap.get(Parameter.BOX_TYPE_KEY).toString());
		}
		// 解析物品列表
		@SuppressWarnings("unchecked")
		List<Map<String, Object>> list = (List<Map<String, Object>>) paramMap.get("goodsList");
		List<StoBoxDetail> stoBoxDetailList = new ArrayList<StoBoxDetail>();
		for (Map<String, Object> mapGoods : list) {
			StoBoxDetail stoBoxDetail = new StoBoxDetail();
			if (mapGoods.get(Parameter.GOODS_ID_KEY) != null
					&& StringUtils.isNotEmpty(mapGoods.get(Parameter.GOODS_ID_KEY).toString())) {
				stoBoxDetail.setGoodsId(mapGoods.get(Parameter.GOODS_ID_KEY).toString());
			} else {
				map.put(Parameter.RESULT_FLAG_KEY, ExternalConstant.HardwareInterface.RESULT_FLAG_FAILURE);
				map.put(Parameter.ERROR_NO_KEY, ExternalConstant.HardwareInterface.ERROR_NO_E03);
				map.put(Parameter.ERROR_MSG_KEY, "物品ID不能为空！");
				return gson.toJson(map);
			}
			if (mapGoods.get(Parameter.GOODS_NUM_KEY) != null
					&& StringUtils.isNotEmpty(mapGoods.get(Parameter.GOODS_NUM_KEY).toString())) {
				stoBoxDetail.setGoodsNum(mapGoods.get(Parameter.GOODS_NUM_KEY).toString());
			} else {
				map.put(Parameter.RESULT_FLAG_KEY, ExternalConstant.HardwareInterface.RESULT_FLAG_FAILURE);
				map.put(Parameter.ERROR_NO_KEY, ExternalConstant.HardwareInterface.ERROR_NO_E03);
				map.put(Parameter.ERROR_MSG_KEY, "物品数量不能为空！");
				return gson.toJson(map);
			}
			if (mapGoods.get(Parameter.GOODS_AMOUNT_KEY) != null
					&& StringUtils.isNotEmpty(mapGoods.get(Parameter.GOODS_AMOUNT_KEY).toString())) {
				stoBoxDetail.setGoodsAmount(mapGoods.get(Parameter.GOODS_AMOUNT_KEY).toString());
			} else {
				map.put(Parameter.RESULT_FLAG_KEY, ExternalConstant.HardwareInterface.RESULT_FLAG_FAILURE);
				map.put(Parameter.ERROR_NO_KEY, ExternalConstant.HardwareInterface.ERROR_NO_E03);
				map.put(Parameter.ERROR_MSG_KEY, "物品金额不能为空！");
				return gson.toJson(map);
			}
			stoBoxDetailList.add(stoBoxDetail);
		}
		stoBoxInfo.setStoBoxDetail(stoBoxDetailList);
		// 解析Rfid锁号
		if (paramMap.get(Parameter.RFID_KEY) == null
				|| StringUtils.isEmpty(paramMap.get(Parameter.RFID_KEY).toString())) {
			map.put(Parameter.RESULT_FLAG_KEY, ExternalConstant.HardwareInterface.RESULT_FLAG_FAILURE);
			map.put(Parameter.ERROR_NO_KEY, ExternalConstant.HardwareInterface.ERROR_NO_E03);
			map.put(Parameter.ERROR_MSG_KEY, "Rfid锁号不能为空！");
			return gson.toJson(map);
		}
		// 解析总金额
		if (paramMap.get(Parameter.RFID_ALL_AMOUNT_KEY) == null
				|| StringUtils.isEmpty(paramMap.get(Parameter.RFID_ALL_AMOUNT_KEY).toString())) {
			map.put(Parameter.RESULT_FLAG_KEY, ExternalConstant.HardwareInterface.RESULT_FLAG_FAILURE);
			map.put(Parameter.ERROR_NO_KEY, ExternalConstant.HardwareInterface.ERROR_NO_E03);
			map.put(Parameter.ERROR_MSG_KEY, "总金额不能为空！");
			return gson.toJson(map);
		}
		// 解析用户编号
		if (paramMap.get(Parameter.USER_ID_KEY) == null
				|| StringUtils.isEmpty(paramMap.get(Parameter.USER_ID_KEY).toString())) {
			map.put(Parameter.RESULT_FLAG_KEY, ExternalConstant.HardwareInterface.RESULT_FLAG_FAILURE);
			map.put(Parameter.ERROR_NO_KEY, ExternalConstant.HardwareInterface.ERROR_NO_E03);
			map.put(Parameter.ERROR_MSG_KEY, "用户ID不能为空！");
			return gson.toJson(map);
		}
		// 解析系统登录用户姓名
		if (paramMap.get(Parameter.USER_NAME_KEY) == null
				|| StringUtils.isEmpty(paramMap.get(Parameter.USER_NAME_KEY).toString())) {
			map.put(Parameter.RESULT_FLAG_KEY, ExternalConstant.HardwareInterface.RESULT_FLAG_FAILURE);
			map.put(Parameter.ERROR_NO_KEY, ExternalConstant.HardwareInterface.ERROR_NO_E03);
			map.put(Parameter.ERROR_MSG_KEY, "用户姓名不能为空！");
			return gson.toJson(map);
		}
		// 插入机构
		Office office = new Office();
		office.setId(paramMap.get(Parameter.OFFICE_ID_KEY).toString());
		stoBoxInfo.setOffice(office);
		// 插入rfid号
		stoBoxInfo.setRfid(paramMap.get(Parameter.RFID_KEY).toString());
		// 插入总金额
		stoBoxInfo.setBoxAmount(new BigDecimal(paramMap.get(Parameter.RFID_ALL_AMOUNT_KEY).toString()));
		// 插入当前用户
		User updateBy = new User();
		updateBy.setId(paramMap.get(Parameter.USER_ID_KEY).toString());
		stoBoxInfo.setUpdateBy(updateBy);
		// 插入用户姓名
		stoBoxInfo.setUpdateName(paramMap.get(Parameter.USER_NAME_KEY).toString());
		// 插入系统当前时间
		stoBoxInfo.setUpdateDate(new Date());
		// 更改箱袋状态
		stoBoxInfo.setBoxStatus(Constant.BoxStatus.BANK_OUTLETS);
		stoBoxInfo.setDelFlag(Constant.deleteFlag.Valid);
		// 查询箱袋信息
		List<StoBoxInfo> boxList = stoBoxInfoService.getBoxInfo(stoBoxInfo);
		// 判断箱袋状态
		if (Collections3.isEmpty(boxList)) {
			map.put(Parameter.RESULT_FLAG_KEY, ExternalConstant.HardwareInterface.RESULT_FLAG_FAILURE);
			map.put(Parameter.ERROR_NO_KEY, ExternalConstant.HardwareInterface.ERROR_NO_E99);
			map.put(Parameter.ERROR_MSG_KEY, "箱袋不存在！");
			return gson.toJson(map);
		} else if (1 == boxList.size()) {
			for (StoBoxInfo box : boxList) {
				// 判断所属机构是否相同
				if (!box.getOffice().getId().equals(stoBoxInfo.getOffice().getId())) {
					map.put(Parameter.RESULT_FLAG_KEY, ExternalConstant.HardwareInterface.RESULT_FLAG_FAILURE);
					map.put(Parameter.ERROR_NO_KEY, ExternalConstant.HardwareInterface.ERROR_NO_E99);
					map.put(Parameter.ERROR_MSG_KEY, "箱袋所属机构不一致，请确认后提交信息！");
					return gson.toJson(map);
				}
				// 判断箱子是否为尾箱
				if (box.getBoxType().equals(Constant.BoxType.BOX_TAIL)) {
					map.put(Parameter.RESULT_FLAG_KEY, ExternalConstant.HardwareInterface.RESULT_FLAG_FAILURE);
					map.put(Parameter.ERROR_NO_KEY, ExternalConstant.HardwareInterface.ERROR_NO_E99);
					map.put(Parameter.ERROR_MSG_KEY, "此rfid已绑定尾箱，不可进行装箱！");
					return gson.toJson(map);
				}
				// 判断箱子状态
				if (!(box.getBoxStatus().equals(Constant.BoxStatus.EMPTY)
						|| box.getBoxStatus().equals(Constant.BoxStatus.BANK_OUTLETS))) {
					map.put(Parameter.RESULT_FLAG_KEY, ExternalConstant.HardwareInterface.RESULT_FLAG_FAILURE);
					map.put(Parameter.ERROR_NO_KEY, ExternalConstant.HardwareInterface.ERROR_NO_E99);
					map.put(Parameter.ERROR_MSG_KEY, "箱袋状态异常！");
					return gson.toJson(map);
				}
				if (DbConfigConstant.outletBoxAdd.USE_BOXNO.equals(useType)
						|| DbConfigConstant.outletBoxAdd.USE_ALL.equals(useType)){
					// 验证箱子对应得rfid和扫描的rfid是否相同
					if (!(box.getRfid().equals(stoBoxInfo.getRfid()) && box.getId().equals(stoBoxInfo.getId()))) {
							map.put(Parameter.RESULT_FLAG_KEY, ExternalConstant.HardwareInterface.RESULT_FLAG_FAILURE);
							map.put(Parameter.ERROR_NO_KEY, ExternalConstant.HardwareInterface.ERROR_NO_E99);
							map.put(Parameter.ERROR_MSG_KEY, "箱袋编号与rfid关系不一致，请重新确认！");
							return gson.toJson(map);
					}
				}
				if(DbConfigConstant.outletBoxAdd.USE_RFID.equals(useType)){
					if (!box.getRfid().equals(stoBoxInfo.getRfid())) {
						map.put(Parameter.RESULT_FLAG_KEY, ExternalConstant.HardwareInterface.RESULT_FLAG_FAILURE);
						map.put(Parameter.ERROR_NO_KEY, ExternalConstant.HardwareInterface.ERROR_NO_E99);
						map.put(Parameter.ERROR_MSG_KEY, "rfid关系不一致，请重新确认！");
						return gson.toJson(map);
					}
				}
				// 设置箱号
				stoBoxInfo.setId(box.getId());
				// 调用saveOrUpdate方法进行更新
				stoBoxInfoService.saveOrUpdate(stoBoxInfo);
				// 成功结果
				map.put(Parameter.RESULT_FLAG_KEY, ExternalConstant.HardwareInterface.RESULT_FLAG_SUCCESS);
			}
		} else {
			// 判断两个箱子中是否存在尾箱，若存在，提示尾箱不能装箱
			for (StoBoxInfo box : boxList) {
				if (box.getBoxType().equals(Constant.BoxType.BOX_TAIL)) {
					map.put(Parameter.RESULT_FLAG_KEY, ExternalConstant.HardwareInterface.RESULT_FLAG_FAILURE);
					map.put(Parameter.ERROR_NO_KEY, ExternalConstant.HardwareInterface.ERROR_NO_E99);
					map.put(Parameter.ERROR_MSG_KEY, "此rfid已绑定尾箱，不可进行装箱！");
					return gson.toJson(map);
				}
			}
			// 若不存在尾箱则提示，提示被绑定
			map.put(Parameter.RESULT_FLAG_KEY, ExternalConstant.HardwareInterface.RESULT_FLAG_FAILURE);
			map.put(Parameter.ERROR_NO_KEY, ExternalConstant.HardwareInterface.ERROR_NO_E99);
			map.put(Parameter.ERROR_MSG_KEY, "此箱袋编号和rfid已被其他箱子绑定！");
			return gson.toJson(map);
		}
		return gson.toJson(map);

	}
}
