package com.coffer.external.service;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.coffer.businesses.common.Constant;
import com.coffer.businesses.modules.store.StoreCommonUtils;
import com.coffer.businesses.modules.store.v01.entity.StoGoods;
import com.coffer.core.common.utils.DateUtils;
import com.coffer.external.ExternalConstant;
import com.coffer.external.Parameter;
import com.coffer.external.hessian.HardwardBaseService;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

/**
 * Title: Service73
 * <p>
 * Description: 数据字典关联关系同步接口（对外）
 * </p>
 * 
 * @author wangbaozhong
 * @date 2017年7月10日 上午10:41:10
 */
@Component("Service73")
@Scope("singleton")
public class Service73 extends HardwardBaseService {

	@Override
	public String execute(Map<String, Object> paramMap) {
		logger.debug(this.getClass().getName() + "数据字典关联关系同步接口(对外) -----------------开始");
		Map<String, Object> respMap = Maps.newHashMap();
		// 版本号、服务代码
		respMap.put(Parameter.VERSION_NO_KEY, paramMap.get(Parameter.VERSION_NO_KEY));
		respMap.put(Parameter.SERVICE_NO_KEY, paramMap.get(Parameter.SERVICE_NO_KEY));
		
		// TODO 握手加密验证信息
		
		try {
			StoGoods stoGoods = new StoGoods();
			// 获取增量日期查询条件
			String searchDate = paramMap.get(Parameter.SEARCH_DATE_KEY) == null ? ""
					: paramMap.get(Parameter.SEARCH_DATE_KEY).toString();
			stoGoods.setUpdateDateStr(searchDate);
			// 查询物品信息列表
			List<StoGoods> goodsInfoList = StoreCommonUtils.findAllGoodsList(stoGoods);
			// 格式化输出
			List<Map<String, Object>> rtnMapList = Lists.newArrayList();
			Map<String, Object> innerMap = null;
			for (StoGoods tempGoods : goodsInfoList) {
				innerMap = Maps.newHashMap();
				innerMap.put(Parameter.GOODS_ID_KEY, tempGoods.getGoodsID());
				innerMap.put(Parameter.GOODS_NAME_KEY, tempGoods.getGoodsName());
				innerMap.put(Parameter.GOODS_TYPE_KEY, tempGoods.getGoodsType());
				innerMap.put(Parameter.GOODS_VALUE_KEY, tempGoods.getGoodsVal());
				innerMap.put(Parameter.DEL_FLAG_KEY, tempGoods.getDelFlag());
				rtnMapList.add(innerMap);
			}
			respMap.put(Parameter.LIST_KEY, rtnMapList);
			// 指定最后查询日期
			if (goodsInfoList.size() > 0) {
				respMap.put(Parameter.SEARCH_DATE_KEY,
						DateUtils.formatDate(goodsInfoList.get(goodsInfoList.size() - 1).getUpdateDate(),
								Constant.Dates.FORMATE_YYYYMMDDHHMMSSSSS));
			} else {
				respMap.put(Parameter.SEARCH_DATE_KEY,
						DateUtils.formatDate(new Date(), Constant.Dates.FORMATE_YYYYMMDDHHMMSSSSS));
			}
			respMap.put(Parameter.RESULT_FLAG_KEY, ExternalConstant.HardwareInterface.RESULT_FLAG_SUCCESS);
		} catch (Exception e) {
			// 处理异常
			e.printStackTrace();
			respMap.put(Parameter.RESULT_FLAG_KEY, ExternalConstant.HardwareInterface.RESULT_FLAG_FAILURE);
			respMap.put(Parameter.ERROR_NO_KEY, ExternalConstant.HardwareInterface.ERROR_NO_E02);
		}
		logger.debug(this.getClass().getName() + "数据字典关联关系同步接口(对外) -----------------结束");
		return gson.toJson(respMap);
	}

}
