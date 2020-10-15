/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.coffer.businesses.modules.store.v01.service;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.coffer.businesses.common.Constant;
import com.coffer.businesses.common.utils.GoodDictUtils;
import com.coffer.businesses.modules.store.StoreCommonUtils;
import com.coffer.businesses.modules.store.StoreConstant;
import com.coffer.businesses.modules.store.v01.dao.StoEmptyDocumentDao;
import com.coffer.businesses.modules.store.v01.entity.ChangeStoreEntity;
import com.coffer.businesses.modules.store.v01.entity.StoEmptyDocument;
import com.coffer.businesses.modules.store.v01.entity.StoGoods;
import com.coffer.core.common.persistence.Page;
import com.coffer.core.common.service.BusinessException;
import com.coffer.core.common.service.CrudService;
import com.coffer.core.common.utils.Collections3;
import com.coffer.core.common.utils.StringUtils;
import com.coffer.core.modules.sys.entity.Office;
import com.coffer.core.modules.sys.entity.User;
import com.coffer.core.modules.sys.utils.UserUtils;
import com.google.common.collect.Lists;

/**
 * 重空管理Service
 * 
 * @author LLF
 * @version 2015-12-11
 */
@Service
@Transactional(readOnly = true)
public class StoEmptyDocumentService extends CrudService<StoEmptyDocumentDao, StoEmptyDocument> {

	@Autowired
	StoStoresInfoService stoStoresInfoService;

	public StoEmptyDocument get(String id) {
		return super.get(id);
	}

	public List<StoEmptyDocument> findList(StoEmptyDocument stoEmptyDocument) {
		return super.findList(stoEmptyDocument);
	}

	public Page<StoEmptyDocument> findPage(Page<StoEmptyDocument> page, StoEmptyDocument stoEmptyDocument) {
		return super.findPage(page, stoEmptyDocument);
	}

	@Transactional(readOnly = false)
	public void save(StoEmptyDocument stoEmptyDocument) {
		super.save(stoEmptyDocument);
	}

	/**
	 * 
	 * @author LLF
	 * @version 2015年12月15日
	 * 
	 *          批量保存重空创建修改信息
	 * @param stoEmptyDocumentList
	 */
	@Transactional(readOnly = false)
	public void save(List<StoEmptyDocument> stoEmptyDocumentList) {
		User user = UserUtils.getUser();
		for (StoEmptyDocument stoEmptyDocument : stoEmptyDocumentList) {
			// 验证重空区间值是否已经存在
			List<StoEmptyDocument> checkList = dao.checkEmptyDocument(stoEmptyDocument);
			if (Collections3.isEmpty(checkList)) {
				// 计算库存变更量
				List<ChangeStoreEntity> changeList = Lists.newArrayList();
				ChangeStoreEntity changeStoreEntity = new ChangeStoreEntity();
				// 生成物品ID
				StoGoods stoGoods = new StoGoods();
				stoGoods.setStoBlankBillSelect(stoEmptyDocument.getStoBlankBillSelect());
				String goodsId = StoreCommonUtils.genBlankBillId(stoGoods);
				changeStoreEntity.setGoodsId(goodsId);
				BigDecimal balanceNumber = stoEmptyDocument.getBalanceNumber() != null ? stoEmptyDocument
						.getBalanceNumber() : new BigDecimal(0);
				Long changeNum = stoEmptyDocument.getCreateNumber().subtract(balanceNumber).longValue();
				changeStoreEntity.setNum(changeNum);
				changeList.add(changeStoreEntity);

				// 保存重空信息
				stoEmptyDocument.setGoodsId(goodsId);
				stoEmptyDocument.setBalanceNumber(stoEmptyDocument.getCreateNumber());
				stoEmptyDocument.setOffice(user.getOffice());
				super.save(stoEmptyDocument);
				// 修改库存量
				StoreCommonUtils.changeStoreAndSurplusStores(changeList, user.getOffice().getId(),
						stoEmptyDocument.getId(), user);
			} else {
				throw new BusinessException("message.E1036", "", new String[] {
						GoodDictUtils.getDictLabel(stoEmptyDocument.getDocumentType(), "blank_bill_type", ""),
						String.valueOf(stoEmptyDocument.getStartNumber()),
						String.valueOf(stoEmptyDocument.getEndNumber()) });
			}
		}
	}

	@Transactional(readOnly = false)
	public void delete(StoEmptyDocument stoEmptyDocument) {
		User user = UserUtils.getUser();
		// 计算库存变更量
		List<ChangeStoreEntity> changeList = Lists.newArrayList();
		ChangeStoreEntity changeStoreEntity = new ChangeStoreEntity();
		StoGoods stoGoods = new StoGoods();
		stoGoods.setStoBlankBillSelect(stoEmptyDocument.getStoBlankBillSelect());
		String goodsId = StoreCommonUtils.genBlankBillId(stoGoods);
		changeStoreEntity.setGoodsId(goodsId);
		BigDecimal balanceNumber = new BigDecimal(0);
		Long changeNum = balanceNumber.subtract(stoEmptyDocument.getCreateNumber()).longValue();
		changeStoreEntity.setNum(changeNum);
		changeList.add(changeStoreEntity);
		super.delete(stoEmptyDocument);
		// 修改库存量
		StoreCommonUtils.changeStoreAndSurplusStores(changeList, user.getOffice().getId(), stoEmptyDocument.getId(),
				user);
	}

	@Transactional(readOnly = false)
	public synchronized String changeBlankBillStores(List<ChangeStoreEntity> list, String officeId) {
		// 查询当前重空未被使用的凭证信息
		StoEmptyDocument stoEmptyDocumentTemp = new StoEmptyDocument();
		stoEmptyDocumentTemp.setOffice(new Office(officeId));
		List<StoEmptyDocument> documentList = dao.findEmptyDocument(stoEmptyDocumentTemp);

		for (ChangeStoreEntity changeStoreEntity : list) {
			if (!StoreConstant.GoodType.BLANK_BILL.equals(changeStoreEntity.getGoodType())) {
				continue;
			}
			boolean flag = false;
			for (StoEmptyDocument stoEmptyDocument : documentList) {
				if (changeStoreEntity.getGoodsId().equals(stoEmptyDocument.getGoodsId())) {
					BigDecimal balanceNumber = stoEmptyDocument.getBalanceNumber();
					Long change = balanceNumber.longValue() + changeStoreEntity.getNum();
					// change大于=0 预剩库存充足 change小于0预剩库存不充足
					if (change >= 0) {
						// 修改预剩库存
						stoEmptyDocument.setBalanceNumber(new BigDecimal(change));
						save(stoEmptyDocument);
						flag = true;
						break;

					} else {
						// 当前区间库存不足递减
						stoEmptyDocument.setBalanceNumber(new BigDecimal(0));
						save(stoEmptyDocument);
						changeStoreEntity.setNum(change);
					}
				}
			}
			// 重空物品数量不足
			if (!flag) {
				String goodName = StoreCommonUtils.getGoodsName(changeStoreEntity.getGoodsId());
				goodName = StringUtils.isBlank(goodName) ? "" : goodName;
				throw new BusinessException("message.E1037", "", new String[] {
						changeStoreEntity.getGoodsId(), goodName });
			}
		}
		return Constant.SUCCESS;
	}
}