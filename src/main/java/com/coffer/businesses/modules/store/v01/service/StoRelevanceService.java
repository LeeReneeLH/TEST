package com.coffer.businesses.modules.store.v01.service;

import java.util.Arrays;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.coffer.businesses.common.utils.GoodDictUtils;
import com.coffer.businesses.modules.store.StoreConstant;
import com.coffer.businesses.modules.store.v01.dao.StoRelevanceDao;
import com.coffer.businesses.modules.store.v01.entity.StoBlankBillSelect;
import com.coffer.businesses.modules.store.v01.entity.StoDict;
import com.coffer.businesses.modules.store.v01.entity.StoRelevance;
import com.coffer.core.common.persistence.Page;
import com.coffer.core.common.service.BusinessException;
import com.coffer.core.common.service.CrudService;
import com.coffer.core.common.utils.DateUtils;
import com.coffer.core.common.utils.StringUtils;
import com.google.gson.Gson;

/**
 * 物品关联配置Service
 * 
 * @author yuxixuan
 * @version 2015-09-11
 */
@Service
@Transactional(readOnly = true)
public class StoRelevanceService extends CrudService<StoRelevanceDao, StoRelevance> {

	// ================= 自动生成的代码没有用到====start
	// public StoRelevance get(String id) {
	// return super.get(id);
	// }
	//
	// public List<StoRelevance> findList(StoRelevance stoRelevance) {
	// return super.findList(stoRelevance);
	// }
	//
	// public Page<StoRelevance> findPage(Page<StoRelevance> page, StoRelevance
	// stoRelevance) {
	// return super.findPage(page, stoRelevance);
	// }
	//
	// @Transactional(readOnly = false)
	// public void save(StoRelevance stoRelevance) {
	// super.save(stoRelevance);
	// }
	//
	// @Transactional(readOnly = false)
	// public void delete(StoRelevance stoRelevance) {
	// super.delete(stoRelevance);
	// }
	// ================= 自动生成的代码没有用到====end

	/**
	 * 保存物品关联
	 * 
	 * @param stoRelevance
	 */
	@Transactional(readOnly = false)
	public void saveRelevance(StoRelevance stoRelevance) {
		// 如果套别为空，赋值为0，用于物品编号占位
		if (StringUtils.isEmpty(stoRelevance.getSets())) {
			stoRelevance.setSets(StoreConstant.Sets.FOREIGN_CURRENCY_SETS);
		}
		// 添加物品关联配置时，判断该币种、类别、套别、材质的组合是否已经在数据库存在
		checkExist(stoRelevance);
		// 设置共通项目
		stoRelevance.preInsert();
		// 设置组ID
		stoRelevance.setGroupId(DateUtils.getDateTimeAll());

		// dao.saveRelevance(stoRelevance);
		dao.delRelevanceMysql(stoRelevance);
		dao.saveRelevanceMysql(stoRelevance);
	}

	/**
	 * 查询物品关联
	 * 
	 * @param page
	 * @param stoRelevance
	 * @return
	 */
	public Page<StoRelevance> findPageRelevance(Page<StoRelevance> page, StoRelevance stoRelevance) {
		stoRelevance.setPage(page);
		page.setList(dao.findListRelevance(stoRelevance));
		return page;
	}

	/**
	 * 根据组ID取得物品关联
	 * 
	 * @param groupId
	 * @return
	 */
	public StoRelevance getRelevance(String groupId) {
		StoRelevance stoRelevance = dao.getRelevance(groupId);
		stoRelevance.setDenomiListJson(new Gson().toJson(stoRelevance.getDenomiList()));
		stoRelevance.setUnitListJson(new Gson().toJson(stoRelevance.getUnitList()));
		return dao.getRelevance(groupId);
	}

	/**
	 * 根据组ID删除物品关联
	 * 
	 * @param groupId
	 * @return
	 */
	@Transactional(readOnly = false)
	public void deleteRelevance(StoRelevance stoRelevance) {
		dao.deleteRelevance(stoRelevance);
	}

	/**
	 * 获取物品关联币种
	 * 
	 * @author yuxixuan
	 * @return
	 */
	public List<StoDict> getReleCurrencyList(StoRelevance stoRelevance) {
		// 转换成数组
		if (StringUtils.isNotBlank(stoRelevance.getCurrencyReserve())) {
			stoRelevance.setCurrencyReserveList(Arrays.asList(stoRelevance.getCurrencyReserve().split(",")));
		}
		if (StringUtils.isNotBlank(stoRelevance.getCurrencyRemove())) {
			stoRelevance.setCurrencyRemoveList(Arrays.asList(stoRelevance.getCurrencyRemove().split(",")));
		}
		return dao.getReleCurrencyList(stoRelevance);
	}

	/**
	 * 获取物品关联类别
	 * 
	 * @author yuxixuan
	 * @return
	 */
	public List<StoDict> getReleClassificationList(StoRelevance stoRelevance) {
		// 转换成数组
		if (StringUtils.isNotBlank(stoRelevance.getClassificationReserve())) {
			stoRelevance
					.setClassificationReserveList(Arrays.asList(stoRelevance.getClassificationReserve().split(",")));
		}
		if (StringUtils.isNotBlank(stoRelevance.getClassificationRemove())) {
			stoRelevance.setClassificationRemoveList(Arrays.asList(stoRelevance.getClassificationRemove().split(",")));
		}
		return dao.getReleClassificationList(stoRelevance);
	}

	/**
	 * 获取物品关联套别
	 * 
	 * @author yuxixuan
	 * @return
	 */
	public List<StoDict> getReleSetsList(StoRelevance stoRelevance) {
		// 转换成数组
		if (StringUtils.isNotBlank(stoRelevance.getEditionReserve())) {
			stoRelevance.setEditionReserveList(Arrays.asList(stoRelevance.getEditionReserve().split(",")));
		}
		if (StringUtils.isNotBlank(stoRelevance.getEditionRemove())) {
			stoRelevance.setEditionRemoveList(Arrays.asList(stoRelevance.getEditionRemove().split(",")));
		}
		return dao.getReleSetsList(stoRelevance);
	}

	/**
	 * 获取物品关联材质
	 * 
	 * @author yuxixuan
	 * @return
	 */
	public List<StoDict> getReleCashList(StoRelevance stoRelevance) {
		// 转换成数组
		if (StringUtils.isNotBlank(stoRelevance.getCashReserve())) {
			stoRelevance.setCashReserveList(Arrays.asList(stoRelevance.getCashReserve().split(",")));
		}
		if (StringUtils.isNotBlank(stoRelevance.getCashRemove())) {
			stoRelevance.setCashRemoveList(Arrays.asList(stoRelevance.getCashRemove().split(",")));
		}
		// 如果套别为空，赋值为0，用于查询
		if (StringUtils.isEmpty(stoRelevance.getSets())) {
			stoRelevance.setSets(StoreConstant.Sets.FOREIGN_CURRENCY_SETS);
		}
		return dao.getReleCashList(stoRelevance);
	}

	/**
	 * 获取物品关联面值
	 * 
	 * @author yuxixuan
	 * @return
	 */
	public List<StoDict> getReleDenominationList(StoRelevance stoRelevance) {
		// 转换成数组
		if (StringUtils.isNotBlank(stoRelevance.getDenominationReserve())) {
			stoRelevance.setDenominationReserveList(Arrays.asList(stoRelevance.getDenominationReserve().split(",")));
		}
		if (StringUtils.isNotBlank(stoRelevance.getDenominationRemove())) {
			stoRelevance.setDenominationRemoveList(Arrays.asList(stoRelevance.getDenominationRemove().split(",")));
		}
		// 如果套别为空，赋值为0，用于查询
		if (StringUtils.isEmpty(stoRelevance.getSets())) {
			stoRelevance.setSets(StoreConstant.Sets.FOREIGN_CURRENCY_SETS);
		}
		return dao.getReleDenominationList(stoRelevance);
	}

	/**
	 * 获取物品关联单位
	 * 
	 * @author yuxixuan
	 * @return
	 */
	public List<StoDict> getReleUnitList(StoRelevance stoRelevance) {
		// 转换成数组
		if (StringUtils.isNotBlank(stoRelevance.getUnitReserve())) {
			stoRelevance.setUnitReserveList(Arrays.asList(stoRelevance.getUnitReserve().split(",")));
		}
		if (StringUtils.isNotBlank(stoRelevance.getUnitRemove())) {
			stoRelevance.setUnitRemoveList(Arrays.asList(stoRelevance.getUnitRemove().split(",")));
		}
		// 如果套别为空，赋值为0，用于查询
		if (StringUtils.isEmpty(stoRelevance.getSets())) {
			stoRelevance.setSets(StoreConstant.Sets.FOREIGN_CURRENCY_SETS);
		}
		return dao.getReleUnitList(stoRelevance);
	}

	/**
	 * 根据币种和材质获取面值选项
	 * 
	 * @author yuxixuan
	 * @return
	 */
	public List<StoDict> getDenOptions(StoRelevance stoRelevance) {
		return dao.getDenOptions(stoRelevance);
	}

	/**
	 * 根据材质获取单位选项
	 * 
	 * @author yuxixuan
	 * @return
	 */
	public List<StoDict> getUnitOptions(StoRelevance stoRelevance) {
		return dao.getUnitOptions(stoRelevance);
	}

	/**
	 * 获取字典表重空分类
	 * 
	 * @author yuxixuan
	 * @param stoBlankBillSelect
	 * @return
	 */
	public List<StoDict> getBlankBillKindList(StoBlankBillSelect stoBlankBillSelect) {
		// 转换成数组
		if (StringUtils.isNotBlank(stoBlankBillSelect.getBlankBillKindReserve())) {
			stoBlankBillSelect.setBlankBillKindReserveList(
					Arrays.asList(stoBlankBillSelect.getBlankBillKindReserve().split(",")));
		}
		if (StringUtils.isNotBlank(stoBlankBillSelect.getBlankBillKindRemove())) {
			stoBlankBillSelect
					.setBlankBillKindRemoveList(Arrays.asList(stoBlankBillSelect.getBlankBillKindRemove().split(",")));
		}
		return dao.getBlankBillKindList(stoBlankBillSelect);
	}

	/**
	 * 获取字典表重空类型
	 * 
	 * @author yuxixuan
	 * @param stoBlankBillSelect
	 * @return
	 */
	public List<StoDict> getBlankBillTypeList(StoBlankBillSelect stoBlankBillSelect) {
		// 转换成数组
		if (StringUtils.isNotBlank(stoBlankBillSelect.getBlankBillTypeReserve())) {
			stoBlankBillSelect.setBlankBillTypeReserveList(
					Arrays.asList(stoBlankBillSelect.getBlankBillTypeReserve().split(",")));
		}
		if (StringUtils.isNotBlank(stoBlankBillSelect.getBlankBillTypeRemove())) {
			stoBlankBillSelect
					.setBlankBillTypeRemoveList(Arrays.asList(stoBlankBillSelect.getBlankBillTypeRemove().split(",")));
		}
		return dao.getBlankBillTypeList(stoBlankBillSelect);
	}

	/**
	 * 获取物品表重空分类
	 * 
	 * @author yuxixuan
	 * @param stoBlankBillSelect
	 * @return
	 */
	public List<StoDict> getBlbiKindList(StoBlankBillSelect stoBlankBillSelect) {
		// 转换成数组
		if (StringUtils.isNotBlank(stoBlankBillSelect.getBlankBillKindReserve())) {
			stoBlankBillSelect.setBlankBillKindReserveList(
					Arrays.asList(stoBlankBillSelect.getBlankBillKindReserve().split(",")));
		}
		if (StringUtils.isNotBlank(stoBlankBillSelect.getBlankBillKindRemove())) {
			stoBlankBillSelect
					.setBlankBillKindRemoveList(Arrays.asList(stoBlankBillSelect.getBlankBillKindRemove().split(",")));
		}
		return dao.getBlbiKindList(stoBlankBillSelect);
	}

	/**
	 * 获取物品表重空类型
	 * 
	 * @author yuxixuan
	 * @param stoBlankBillSelect
	 * @return
	 */
	public List<StoDict> getBlbiTypeList(StoBlankBillSelect stoBlankBillSelect) {
		// 转换成数组
		if (StringUtils.isNotBlank(stoBlankBillSelect.getBlankBillTypeReserve())) {
			stoBlankBillSelect.setBlankBillTypeReserveList(
					Arrays.asList(stoBlankBillSelect.getBlankBillTypeReserve().split(",")));
		}
		if (StringUtils.isNotBlank(stoBlankBillSelect.getBlankBillTypeRemove())) {
			stoBlankBillSelect
					.setBlankBillTypeRemoveList(Arrays.asList(stoBlankBillSelect.getBlankBillTypeRemove().split(",")));
		}
		return dao.getBlbiTypeList(stoBlankBillSelect);
	}

	/**
	 * 判断物品关联是否已经存在
	 * 
	 * @param stoRelevance
	 */
	private void checkExist(StoRelevance stoRelevance) {
		StoRelevance searchParam = new StoRelevance();
		searchParam.setCurrency(stoRelevance.getCurrency());
		searchParam.setClassification(stoRelevance.getClassification());
		searchParam.setCash(stoRelevance.getCash());
		searchParam.setSets(stoRelevance.getSets());
		List<StoRelevance> list = dao.findListRelevance(searchParam);
		if (StringUtils.isEmpty(stoRelevance.getGroupId()) && list != null && !list.isEmpty()) {
			String currencyName = GoodDictUtils.getDictLabel(stoRelevance.getCurrency(), "currency", "");
			String classificationName = GoodDictUtils.getDictLabel(stoRelevance.getClassification(), "classification",
					"");
			String editionName = GoodDictUtils.getDictLabel(stoRelevance.getSets(), "edition", "");
			if (StringUtils.isBlank(editionName)) {
				editionName = "空";
			}
			String cashName = GoodDictUtils.getDictLabel(stoRelevance.getCash(), "cash", "");
			throw new BusinessException("message.E1002", "",
					new String[] { currencyName, classificationName, editionName, cashName, });
		}
	}
}