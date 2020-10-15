package com.coffer.external.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.coffer.businesses.common.Constant;
import com.coffer.businesses.modules.atm.v01.entity.AtmInfoMaintain;
import com.coffer.businesses.modules.atm.v01.service.AtmInfoMaintainService;
import com.coffer.businesses.modules.store.v01.entity.StoBoxInfo;
import com.coffer.businesses.modules.store.v01.service.StoBoxInfoService;
import com.coffer.core.common.config.Global;
import com.coffer.core.common.utils.DateUtils;
import com.coffer.core.common.utils.StringUtils;
import com.coffer.core.modules.sys.utils.DictUtils;
import com.coffer.external.ExternalConstant;
import com.coffer.external.Parameter;
import com.coffer.external.hessian.HardwardBaseService;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

/**
 * Title: Service02
 * <p>
 * Description: 款（钞）箱（袋）查询接口
 * </p>
 * 
 * @author wangbaozhong
 * @date 2017年7月6日 上午10:35:07
 */
@Component("Service02")
@Scope("singleton")
public class Service02 extends HardwardBaseService {

	@Autowired
	private StoBoxInfoService boxService;
	@Autowired
	private AtmInfoMaintainService atmInfoMaintainService;

	/**
	 * 
	 * @author LF 箱袋查询接口
	 * @version 2015-06-04
	 * 
	 * @Description
	 * @param paramMap
	 * @return
	 */
	@Override
	public String execute(Map<String, Object> paramMap) {
		Map<String, Object> map = new HashMap<String, Object>();
		// 版本号、服务代码
		map.put(Parameter.VERSION_NO_KEY, paramMap.get(Parameter.VERSION_NO_KEY));
		map.put(Parameter.SERVICE_NO_KEY, paramMap.get(Parameter.SERVICE_NO_KEY));
		/* 添加非空校验 修改人：sg 修改日期：2017-11-07 begin */
		// 箱袋类型
		String boxType = StringUtils.toString(paramMap.get(Parameter.BOX_TYPE_KEY));

		// 箱袋RFID
		String rfid = StringUtils.toString(paramMap.get(Parameter.RFID_KEY));
		// 箱号
		String boxNo = StringUtils.toString(paramMap.get(Parameter.BOX_NO_KEY));
		// 所属机构
		String officeId = StringUtils.toString(paramMap.get(Parameter.OFFICE_ID_KEY));
		// 有效标识
		String delFlag = StringUtils.toString(paramMap.get(Parameter.DEL_FLAG_KEY));
		/* end */
		try {
			/* 判断是否是ATM机 修改人：sg 修改日期：2017-11-07 begin */
			// 获取配置接口最大传输箱袋储量
			int listLen = Integer.valueOf(Global.getConfig("box.search.interface.list"));
			// 不是ATM机时查询箱袋信息
			if (StringUtils.isBlank(boxType) || !Constant.BoxType.BOX_ATM.equals(boxType)) {
				List<StoBoxInfo> list = boxService.searchBoxInfoList(paramMap);
				/* end */
				if (list != null && list.size() != 0) {

					if (list.size() > listLen) {
						list = list.subList(0, listLen - 1);
					}
					for (StoBoxInfo stoBoxInfo : list) {
						stoBoxInfo.setOfficeName(stoBoxInfo.getOffice().getName());
						stoBoxInfo.setBoxTypeName(DictUtils.getDictLabel(stoBoxInfo.getBoxType(), "sto_box_type", ""));
						stoBoxInfo.setBoxStatusName(
								DictUtils.getDictLabel(stoBoxInfo.getBoxStatus(), "sto_box_status", ""));
						stoBoxInfo.setOutTime(DateUtils.formatDate(stoBoxInfo.getOutDate()));
					}
					map.put(Parameter.LIST_KEY, list);
				} else {
					map.put(Parameter.LIST_KEY, null);
				}
			} else {
				// 是ATM机时查询ATM机信息
				AtmInfoMaintain atmInfoMaintains = new AtmInfoMaintain();

				// 插入ATM机编号
				atmInfoMaintains.setAtmId(boxNo);
				// 插入rfid号
				atmInfoMaintains.setRfid(rfid);
				// 插入有效标识
				atmInfoMaintains.setDelFlag(delFlag);
				// 插入所属机构
				atmInfoMaintains.setTofficeId(officeId);
				List<AtmInfoMaintain> lists = atmInfoMaintainService.searchAtmInfoMaintain(atmInfoMaintains);
				if (lists != null && lists.size() != 0) {
					if (lists.size() > listLen) {
						lists = lists.subList(0, listLen - 1);
					}
					List<Map<String, Object>> resultList = Lists.newArrayList();
					for (AtmInfoMaintain atmInfoMaintain : lists) {
						Map<String, Object> resultMap = Maps.newHashMap();
						resultMap.put(Parameter.RFID_KEY, atmInfoMaintain.getRfid());
						resultMap.put(Parameter.DEL_FLAG_KEY, atmInfoMaintain.getDelFlag());
						resultMap.put(Parameter.OFFICE_NAME_KEY, atmInfoMaintain.getTofficeName());
						resultList.add(resultMap);
					}
					map.put(Parameter.LIST_KEY, resultList);
				} else {
					map.put(Parameter.LIST_KEY, null);
				}
			}
			/* end */
			// 成功结果
			map.put(Parameter.RESULT_FLAG_KEY, ExternalConstant.HardwareInterface.RESULT_FLAG_SUCCESS);
		} catch (Exception e) {
			// 处理异常
			e.printStackTrace();
			map.put(Parameter.RESULT_FLAG_KEY, ExternalConstant.HardwareInterface.RESULT_FLAG_FAILURE);
			map.put(Parameter.ERROR_NO_KEY, ExternalConstant.HardwareInterface.ERROR_NO_E02);
		}

		return gson.toJson(map);
	}

}
