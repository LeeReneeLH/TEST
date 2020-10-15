package com.coffer.businesses.modules.store.v01.service;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.coffer.businesses.common.utils.GoodDictUtils;
import com.coffer.businesses.modules.store.StoreConstant;
import com.coffer.businesses.modules.store.v01.dao.StoDictDao;
import com.coffer.businesses.modules.store.v01.entity.StoDict;
import com.coffer.core.common.service.BusinessException;
import com.coffer.core.common.service.CrudService;
import com.coffer.core.common.utils.CacheUtils;
import com.coffer.core.common.utils.Collections3;
import com.coffer.core.common.utils.StringUtils;

/**
 * 字典Service
 * @author ThinkGem
 * @version 2014-05-16
 */
@Service
@Transactional(readOnly = true)
public class StoDictService extends CrudService<StoDictDao, StoDict> {
	
	/**
	 * 查询字段类型列表
	 * @return
	 */
	public List<StoDict> findTypeList() {
		return dao.findTypeList(new StoDict());
	}

	/**
	 * 保存物品字典数据
	 */
	@Transactional(readOnly = false)
	public void save(StoDict dict) {

		Boolean isExist = false;
		if (StringUtils.isBlank(dict.getId())) {
			// 新增时，判断是否已经存在具有相同类型和键值的数据
			isExist = checkExist(dict);
		} else {
			// 修改时，如果修改了物品键值或类型，判断是否已经存在具有相同类型和键值的数据
			StoDict record = dao.get(dict.getId());
			if (record != null
					&& ((!StringUtils.isBlank(record.getValue()) && !record.getValue().equals(dict.getValue()))
					|| (!StringUtils.isBlank(record.getType()) && !record.getType().equals(dict.getType())))) {
				isExist = checkExist(dict);
			}
		}
		if (isExist) {
			String strMessageContent = "类型为" + dict.getType() + "且键值为" + dict.getValue() + "的数据已存在！";
			throw new BusinessException("message.E1005", strMessageContent,
					new String[] { dict.getType(), dict.getValue() });
		}

		// 保存
		super.save(dict);
		CacheUtils.remove(GoodDictUtils.CACHE_GOOD_DICT_MAP);
		CacheUtils.remove(GoodDictUtils.CACHE_GOOD_DICT_MAP_WITH_FG);
	}

	/**
	 * 删除物品字典数据
	 */
	@Transactional(readOnly = false)
	public void delete(StoDict dict) {
		dict.preUpdate();
		super.delete(dict);
		CacheUtils.remove(GoodDictUtils.CACHE_GOOD_DICT_MAP);
		CacheUtils.remove(GoodDictUtils.CACHE_GOOD_DICT_MAP_WITH_FG);
	}

	/**
	 * 恢复物品字典数据
	 */
	@Transactional(readOnly = false)
	public void revert(StoDict dict) {
		dict.preUpdate();
		dao.revert(dict);
		CacheUtils.remove(GoodDictUtils.CACHE_GOOD_DICT_MAP);
		CacheUtils.remove(GoodDictUtils.CACHE_GOOD_DICT_MAP_WITH_FG);
	}
	
    /**
     * 取得物品字典对应的VALUE值
     */
    public String getValue(StoDict dict) {
        List<StoDict> dictList = dao.findList(dict);
        if(Collections3.isEmpty(dictList)){
            return "";
        }else{
            return dictList.get(0).getValue();
        }
    }
    
    /**
     * 根据输入内容，生成物品ID
     * 
     * @author:     ChengShu
     * @param currency 币种
     * @param classification 类别
     * @param sets 套别
     * @param cash 材质
     * @param denomination 面值
     * @param unit 单位
     * @return 
     * String    返回类型
     */
    public String getPbocGoodsId(String currency, String cash, String denomination){
        
        StoDict dict = new StoDict();
        // 物品字典：TYPE
        dict.setType(StoreConstant.currencyMap.get(StringUtils.join(currency, cash)));
        // 物品字典：UNIT_VAL
        dict.setUnitVal(BigDecimal.valueOf(Double.valueOf(denomination)));
        
        // 面值对应的编码
        String value = getValue(dict);
        
        String goodsId = StringUtils.join(
                                    currency,                           // 币种
                                    StoreConstant.MoneyType.ISSUE_FUND, // 类别 
                                    StoreConstant.SetType.SET_0,        // 套别
                                    cash,                               // 材质
                                    value,                              // 面值
                                    StoreConstant.Unit.bag);            // 单位
        
        return goodsId;
    }

	/**
	 * 判断是否已经存在具有相同类型和键值的数据
	 * 
	 * @author yuxixuan
	 * @version 2016年1月11日
	 * 
	 * 
	 * @param dict
	 * @return
	 */
	private Boolean checkExist(StoDict dict) {
		Boolean isExist = false;
		StoDict checkParam = new StoDict();
		checkParam.setType(dict.getType());
		checkParam.setValue(dict.getValue());
		// 无论是否停用都不可重复
		checkParam.setDelFlag(null);
		List<StoDict> list = dao.findList(checkParam);
		if (list != null && !list.isEmpty()) {
			isExist = true;
		}
		return isExist;
	}
}
