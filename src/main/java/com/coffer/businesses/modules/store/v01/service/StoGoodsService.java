package com.coffer.businesses.modules.store.v01.service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.coffer.businesses.modules.store.StoreCommonUtils;
import com.coffer.businesses.modules.store.StoreConstant;
import com.coffer.businesses.modules.store.v01.dao.StoGoodsDao;
import com.coffer.businesses.modules.store.v01.entity.StoBlankBillSelect;
import com.coffer.businesses.modules.store.v01.entity.StoGoodSelect;
import com.coffer.businesses.modules.store.v01.entity.StoGoods;
import com.coffer.businesses.modules.store.v01.entity.StoRelevance;
import com.coffer.core.common.persistence.Page;
import com.coffer.core.common.service.BusinessException;
import com.coffer.core.common.service.CrudService;
import com.coffer.core.common.utils.StringUtils;

/**
 * 物品管理Service
 * @author Ray
 * @version 2015-09-10
 */
@Service
@Transactional(readOnly = true)
public class StoGoodsService extends CrudService<StoGoodsDao, StoGoods> {
	
	/** 物品名称与物品价值关联MAP **/
	private static Map<String, BigDecimal> GOODS_VAL_MAP;
	
	/** 物品名称MAP **/
	private static Map<String, String> GOODS_NAME_MAP;

	@Autowired
	private StoGoodsDao	stoGoodsDao;

	/**
	 * 根据ID取得物品
	 */
	public StoGoods get(String id) {
		StoGoods stoGoods = super.get(id);
		// 根据备注，取得各属性的值
		stoGoods = getVlaueByRemark(stoGoods);
		return stoGoods;
	}
	
	/**
	 * 取得查询列表
	 */
	public Page<StoGoods> findPage(Page<StoGoods> page, StoGoods stoGoods) {
		page = super.findPage(page, stoGoods);
		List<StoGoods> goods = page.getList();
		for (StoGoods sg : goods) {
			sg = getVlaueByRemark(sg);
		}
		return page;
	}
	
	/**
	 * 保存物品
	 * 
	 * @param stoGoods
	 * @return
	 */
	@Transactional(readOnly = false)
	public void saveGoods(StoGoods stoGoods) {

		if (StringUtils.isEmpty(stoGoods.getId())) {
			// 物品编号不存在的场合，新增

			// 预处理
			stoGoods.preInsert();
			// 设置ID和备注
			if (StoreConstant.GoodType.CURRENCY.equals(stoGoods.getGoodsType())) {
				// 货币的场合
				stoGoods = makeCurrencyInfo(stoGoods);
			} else if (StoreConstant.GoodType.BLANK_BILL.equals(stoGoods.getGoodsType())) {
				// 重空的场合
				stoGoods = makeBlankBillInfo(stoGoods);
			}
			// 验证物品是否存在
			StoGoods stoGoodsCheck = stoGoodsDao.get(stoGoods.getId());
			if (stoGoodsCheck != null && StringUtils.isNotEmpty(stoGoodsCheck.getId())) {
				throw new BusinessException("message.E1029", "");
			}
			// 恢复逻辑删除的物品
			int count = recover(stoGoods);
			// 逻辑删除记录不存在时，则新增物品。
			if (count == 0) {
				stoGoodsDao.insert(stoGoods);
			}
		} else {
			// 物品编号不存在的场合，修改。
			stoGoodsDao.update(stoGoods);
		}

		// 货币的场合,更新物品名称与物品价值关联MAP
		if (StoreConstant.GoodType.CURRENCY.equals(stoGoods.getGoodsType())) {
			setGoodsValMap();
		}
		// 更新物品名称
		setGoodsNameMap();
	}
	
	/**
	 * 删除物品
	 */
	@Transactional(readOnly = false)
	public void delete(StoGoods stoGoods) {
		// 验证物品是否有库存
		if (StoreCommonUtils.checkStoresInfoExist(stoGoods.getId(), null, "Y")) {
			throw new BusinessException("message.E1030", "");
		}
		// 预处理
		stoGoods.preUpdate();
		super.delete(stoGoods);
		// 更新物品名称与物品价值关联MAP
		setGoodsValMap();
		// 更新物品名称
		setGoodsNameMap();
	}

	/**
	 * @author ChengShu
	 * @version 2015年9月16日
	 * 
	 * @Description 取得物品名称与物品价值关联MAP
	 * @return 物品名称与物品价值关联MAP
	 */
	public Map<String, BigDecimal> getGoodsValMap() {

		// 如果MAP是空的场合
		if (GOODS_VAL_MAP == null) {
			setGoodsValMap();
		}

		return GOODS_VAL_MAP;
	}
	
	/**
	 * @author ChengShu
	 * @version 2015年9月16日
	 * 
	 * @Description 取得物品ID与物品名称关联MAP
	 * @return 物品ID与物品名称关联MAP
	 */
	public Map<String, String> getGoodsNameMap() {

		// 如果MAP是空的场合
		if (GOODS_NAME_MAP == null) {
			setGoodsNameMap();
		}

		return GOODS_NAME_MAP;
	}
	
	/**
	 * 计算物品价值
	 * 
	 * @param stoRelevance
	 * @return
	 */
	public BigDecimal calcGoodsVal(StoRelevance stoRelevance) {
		return stoGoodsDao.calcGoodsVal(stoRelevance);
	}
	
	/**
	 * @author ChengShu
	 * @version 2015年9月16日
	 * 
	 * @Description 设置物品名称与物品价值关联MAP
	 * @return 物品名称与物品价值关联MAP
	 */
	private void setGoodsValMap() {

		// 初始化MAP
		GOODS_VAL_MAP = new HashMap<String, BigDecimal>();

		// 查询最新的物品信息
		List<StoGoods> stoGoodsList = stoGoodsDao.findList(new StoGoods());

		// 设置物品名称与物品价值关联MAP
		for (StoGoods stoGoods : stoGoodsList) {
			GOODS_VAL_MAP.put(stoGoods.getGoodsID(), stoGoods.getGoodsVal());
		}
	}
	
	/**
	 * @author ChengShu
	 * @version 2015年9月16日
	 * 
	 * @Description 设置物品名称与物品价值关联MAP
	 * @return 物品名称与物品价值关联MAP
	 */
	private void setGoodsNameMap() {

		// 初始化MAP
		GOODS_NAME_MAP = new HashMap<String, String>();

		// 查询最新的物品信息
		List<StoGoods> stoGoodsList = stoGoodsDao.findList(new StoGoods());

		// 设置物品名称与物品价值关联MAP
		for (StoGoods stoGoods : stoGoodsList) {
			GOODS_NAME_MAP.put(stoGoods.getGoodsID(), stoGoods.getGoodsName());
		}
	}

	/**
	 * 设置货币物品信息
	 * 
	 * @param stoGoods
	 * @return
	 */
	private StoGoods makeCurrencyInfo(StoGoods stoGoods) {
		// 如果套别为空，赋值为0，用于物品编号占位
		if (StringUtils.isEmpty(stoGoods.getStoGoodSelect().getEdition())) {
			stoGoods.getStoGoodSelect().setEdition(StoreConstant.Sets.FOREIGN_CURRENCY_SETS);
		}
		// 设置ID
		StringBuffer id = new StringBuffer();
		id.append(stoGoods.getStoGoodSelect().getCurrency()).append(stoGoods.getStoGoodSelect().getClassification())
				.append(stoGoods.getStoGoodSelect().getEdition()).append(stoGoods.getStoGoodSelect().getCash())
				.append(stoGoods.getStoGoodSelect().getDenomination()).append(stoGoods.getStoGoodSelect().getUnit());
		stoGoods.setId(id.toString());
		// 设置备注
		StringBuffer remark = new StringBuffer();
		remark.append(stoGoods.getStoGoodSelect().getCurrency()).append(StoreConstant.Punctuation.COMMA)
				.append(stoGoods.getStoGoodSelect().getClassification()).append(StoreConstant.Punctuation.COMMA)
				.append(stoGoods.getStoGoodSelect().getEdition()).append(StoreConstant.Punctuation.COMMA)
				.append(stoGoods.getStoGoodSelect().getCash()).append(StoreConstant.Punctuation.COMMA)
				.append(stoGoods.getStoGoodSelect().getDenomination()).append(StoreConstant.Punctuation.COMMA)
				.append(stoGoods.getStoGoodSelect().getUnit());
		stoGoods.setRemarks(remark.toString());
		return stoGoods;
	}

	/**
	 * 设置重空物品信息
	 * 
	 * @param stoGoods
	 * @return
	 */
	private StoGoods makeBlankBillInfo(StoGoods stoGoods) {
		// 设置ID
		stoGoods.setId(StoreCommonUtils.genBlankBillId(stoGoods));
		// 设置备注
		String remark = stoGoods.getStoBlankBillSelect().getBlankBillKind() + StoreConstant.Punctuation.COMMA
				+ stoGoods.getStoBlankBillSelect().getBlankBillType();
		stoGoods.setRemarks(remark);
		// 设置物品价值（新增重空时，默认为1）
		stoGoods.setGoodsVal(BigDecimal.ONE);
		return stoGoods;
	}

	/**
	 * 恢复逻辑删除的物品
	 * 
	 * @param stoGoods
	 * @return
	 */
	private int recover(StoGoods stoGoods) {
		int count = stoGoodsDao.recover(stoGoods);
		return count;
	}

	/**
	 * 根据备注，取得各属性的值
	 * 
	 * @param stoGoods
	 * @return
	 */
	private StoGoods getVlaueByRemark(StoGoods stoGoods) {
		if (stoGoods != null && StringUtils.isNotBlank(stoGoods.getRemarks())) {
			String[] dictVal = stoGoods.getRemarks().split(StoreConstant.Punctuation.COMMA);
			if (StoreConstant.GoodType.CURRENCY.equals(stoGoods.getGoodsType()) && dictVal != null
					&& dictVal.length == 6) {
				// 货币的场合
				StoGoodSelect stoGoodSelect = new StoGoodSelect();
				stoGoodSelect.setCurrency(dictVal[0]);
				stoGoodSelect.setClassification(dictVal[1]);
				stoGoodSelect.setEdition(dictVal[2]);
				stoGoodSelect.setCash(dictVal[3]);
				stoGoodSelect.setDenomination(dictVal[4]);
				stoGoodSelect.setUnit(dictVal[5]);
				stoGoods.setStoGoodSelect(stoGoodSelect);
			} else if (StoreConstant.GoodType.BLANK_BILL.equals(stoGoods.getGoodsType()) && dictVal != null
					&& dictVal.length == 2) {
				// 重空的场合
				StoBlankBillSelect stoBlankBillSelect = new StoBlankBillSelect();
				stoBlankBillSelect.setBlankBillKind(dictVal[0]);
				stoBlankBillSelect.setBlankBillType(dictVal[1]);
				stoGoods.setStoBlankBillSelect(stoBlankBillSelect);
			}
		}
		return stoGoods;
	}
	
	/**
	 * 
	 * Title: findAllList
	 * <p>Description: 按条件查询物品信息列表</p>
	 * @author:     wangbaozhong
	 * @param stoGoods 查询条件
	 * @return 物品信息列表
	 * List<StoGoods>    返回类型
	 */
	@Transactional(readOnly = false)
	public List<StoGoods> findAllList(StoGoods stoGoods) {
		return dao.findAllList(stoGoods);
	}
}