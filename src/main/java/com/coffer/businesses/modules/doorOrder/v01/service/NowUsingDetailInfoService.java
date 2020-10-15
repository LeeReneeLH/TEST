package com.coffer.businesses.modules.doorOrder.v01.service;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.coffer.businesses.modules.doorOrder.v01.dao.NowUsingDetailInfoDao;
import com.coffer.businesses.modules.doorOrder.v01.entity.NowUsingDetailInfo;
import com.coffer.core.common.persistence.Page;
import com.coffer.core.common.service.CrudService;

@Service
public class NowUsingDetailInfoService extends CrudService<NowUsingDetailInfoDao, NowUsingDetailInfo> {

	@Autowired
	private NowUsingDetailInfoDao nowUsingDetailInfoDao;

	/**
	 * 
	 * Title: findList
	 * <p>
	 * Description: 现在使用情况详情
	 * </p>
	 * 
	 * @author: guojian
	 * @param page
	 * @param nowUsingDetailInfo
	 * @return List<NowUsingDetailInfo> 返回类型
	 */
	public List<NowUsingDetailInfo> findList(NowUsingDetailInfo nowUsingDetailInfo) {
		return nowUsingDetailInfoDao.findList(nowUsingDetailInfo);
	}

	/**
	 * 
	 * Title: findPage
	 * <p>
	 * Description: 现在使用情况详情分页
	 * </p>
	 * 
	 * @author: guojian
	 * @param page
	 * @param NowUsingDetailInfo
	 * @return Page<NowUsingDetailInfo> 返回类型
	 */
	public Page<NowUsingDetailInfo> findPage(Page<NowUsingDetailInfo> page, NowUsingDetailInfo nowUsingDetailInfo) {
		nowUsingDetailInfo.setPage(page);
		page.setList(this.findList(nowUsingDetailInfo));
		return page;
	}

	/**
	 * 
	 * Title: getTotal
	 * <p>
	 * Description: 统计总金额及各面值金额
	 * </p>
	 * 
	 * @author: guojian
	 * @param NowUsingDetailInfo
	 * @return NowUsingDetailInfo 返回类型
	 */
	public NowUsingDetailInfo getTotal(NowUsingDetailInfo nowUsingDetailInfo) {
		List<NowUsingDetailInfo> deatilList = this.findList(nowUsingDetailInfo);
		Integer totalHundredCount = 0;
		Integer totalFiftyCount = 0;
		Integer totalTwentyCount = 0;
		Integer totalTenCount = 0;
		Integer totalFiveCount = 0;
		Integer totalOneCount = 0;
		Integer totalOoneCount = 0;
		Integer totalOfiveCount = 0;
		Integer totalPaperCount = 0;
		BigDecimal totalPaperAmount = new BigDecimal(0);
		BigDecimal totalForceAmount = new BigDecimal(0);
		BigDecimal totalOtherAmount = new BigDecimal(0);
		BigDecimal totalAmount = new BigDecimal(0);
		for (NowUsingDetailInfo detailInfo : deatilList) {
			totalHundredCount += detailInfo.getHundred();
			totalFiftyCount += detailInfo.getFifty();
			totalTwentyCount += detailInfo.getTwenty();
			totalTenCount += detailInfo.getTen();
			totalFiveCount += detailInfo.getFive();
			totalOneCount += detailInfo.getOne();
			totalOfiveCount += detailInfo.getOfive();
			totalOoneCount += detailInfo.getOone();
			totalPaperCount += detailInfo.getPaperCount();
			totalPaperAmount = totalPaperAmount.add(detailInfo.getPaperAmount());
			totalForceAmount = totalForceAmount.add(detailInfo.getForceAmount());
			totalOtherAmount = totalOtherAmount.add(detailInfo.getOtherAmount());
			totalAmount = totalAmount.add(detailInfo.getTotalAmount());
		}
		NowUsingDetailInfo totalInfo = new NowUsingDetailInfo();
		totalInfo.setSaveBatch("合计");
		totalInfo.setHundred(totalHundredCount);
		totalInfo.setFifty(totalFiftyCount);
		totalInfo.setTwenty(totalTwentyCount);
		totalInfo.setTen(totalTenCount);
		totalInfo.setFive(totalFiveCount);
		totalInfo.setOne(totalOneCount);
		totalInfo.setOfive(totalOfiveCount);
		totalInfo.setOone(totalOoneCount);
		totalInfo.setPaperCount(totalPaperCount);
		totalInfo.setPaperAmount(totalPaperAmount);
		totalInfo.setForceAmount(totalForceAmount);
		totalInfo.setOtherAmount(totalOtherAmount);
		totalInfo.setTotalAmount(totalAmount);
		return totalInfo;
	}

}
