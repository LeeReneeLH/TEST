package com.coffer.businesses.modules.clear.v03.service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.coffer.businesses.common.Constant;
import com.coffer.businesses.common.utils.BusinessUtils;
import com.coffer.businesses.modules.clear.ClearConstant;
import com.coffer.businesses.modules.clear.v03.dao.ClAtmAmountDao;
import com.coffer.businesses.modules.clear.v03.dao.ClAtmDetailDao;
import com.coffer.businesses.modules.clear.v03.dao.ClAtmMainDao;
import com.coffer.businesses.modules.clear.v03.entity.ClAtmAmount;
import com.coffer.businesses.modules.clear.v03.entity.ClAtmDetail;
import com.coffer.businesses.modules.clear.v03.entity.ClAtmMain;
import com.coffer.businesses.modules.store.v01.dao.StoDictDao;
import com.coffer.businesses.modules.store.v01.entity.StoDict;
import com.coffer.core.common.persistence.Page;
import com.coffer.core.common.service.BusinessException;
import com.coffer.core.common.service.CrudService;
import com.coffer.core.common.utils.StringUtils;
import com.coffer.core.modules.sys.dao.OfficeDao;
import com.coffer.core.modules.sys.entity.Office;
import com.coffer.core.modules.sys.utils.UserUtils;
import com.google.common.collect.Lists;


/**
 * ATM钞箱拆箱Service
 * @author wl
 * @version 2017-02-13
 */
@Service
@Transactional(readOnly = true)
public class ClAtmService extends CrudService<ClAtmMainDao, ClAtmMain> {
	@Autowired
	private ClAtmAmountDao clAtmAmountDao;
	
	@Autowired
	private ClAtmDetailDao clAtmDetailDao;
	@Autowired
	private StoDictDao stoDictDao;
	@Autowired
	private ClAtmMainDao clAtmMainDao;
	
	@Autowired
	private OfficeDao officeDao;

	
	public ClAtmMain get(String id) {
		ClAtmMain checkCashMain  = super.get(id);
		return checkCashMain;
	}
	
	/**
	 * 
	 * @author wanglin
	 * @version 一览页面数据
	 * @param ClAtmMain
	 * @return
	 */
	public List<ClAtmMain> findList(ClAtmMain checkCashMain) {
		return super.findList(checkCashMain);
	}
	
	public Page<ClAtmMain> findPage(Page<ClAtmMain> page, ClAtmMain clAtmMain) {
		// 生成数据权限过滤条件（dsf为dataScopeFilter的简写，在xml中使用 ${sqlMap.dsf}调用权限SQL）
		clAtmMain.getSqlMap().put("dsf", dataScopeFilter(UserUtils.getUser(), "o1", null));
		// 设置分页参数
		clAtmMain.setPage(page);
		// 执行分页查询
		List<ClAtmMain> userList = super.findList(clAtmMain);
		page.setList(userList);
		return page;

	}

	
	
	/**
	 * 
	 * @author wanglin
	 * @version 每笔金额列表的取得
	 * 
	 * @param checkCashMain
	 */
	public List<ClAtmAmount> findAmountList(ClAtmMain checkCashMain) {
		ClAtmAmount checkCashAmount = new ClAtmAmount();
		checkCashAmount.setOutNo(checkCashMain.getOutNo());
		return clAtmAmountDao.findList(checkCashAmount);
	}
	
	/**
	 * 
	 * @author wanglin
	 * @version 每笔金额面值列表的取得
	 * 
	 * @param checkCashMain
	 */
	public List<ClAtmDetail> findAmountDetailList(ClAtmMain checkCashMain) {
		ClAtmDetail checkCashDetail = new ClAtmDetail();
		checkCashDetail.setOutNo(checkCashMain.getOutNo());
		return clAtmDetailDao.findList(checkCashDetail);
	}

	
	
	
	
	/**
	 * 
	 * @author wanglin
	 * @version 整单删除
	 * 
	 * @param checkCashMain
	 */
	@Transactional(readOnly = false)
	public void deleteMain(ClAtmMain checkCashMain) {
		
		ClAtmAmount checkCashAmount = new ClAtmAmount();
		try{
			checkCashAmount.setOutNo(checkCashMain.getOutNo());
			checkCashAmount.preUpdate();
			clAtmMainDao.logicDelete(checkCashAmount);		//款箱拆箱表
			clAtmAmountDao.logicDelete(checkCashAmount);	//每笔金额表
			clAtmDetailDao.logicDelete(checkCashAmount);	//每笔金额面值表
		} catch (Exception e) {
			String strErrMsg = "ATM拆箱单号：" + checkCashAmount.getOutNo();
			logger.error(strErrMsg + ",删除失败！", e.getMessage());
			throw new BusinessException("message.I7003", strErrMsg);
		}
		
	}


	/**
	 * 
	 * @author wanglin
	 * @version 保存处理
	 * 
	 * @param clAtmMain
	 */
	@Transactional(readOnly = false)
	public void save(ClAtmMain clAtmMain) {
		try{
	
			String outNo = clAtmMain.getOutNo();				//单号
			String outNewNo = "";								//单号(新单号)
			String outRowNo = "";								//行号
			String sumCheckAmount = "0";

			//单号的生成
			if (StringUtils.isBlank(outNo)){
				outNewNo = BusinessUtils.getNewBusinessNo(ClearConstant.BusinessType.ATM_CASH_BOX,UserUtils.getUser().getOffice());
			}else{
				//修正的场合
				outNewNo = outNo;
				
				//处理前检查（防止多用同时操作）
				ClAtmMain checkMainTemp = new ClAtmMain();
				checkMainTemp = clAtmMainDao.get(outNewNo);
				String chkUpdateCnt = clAtmMain.getUpdateCnt();
				if (checkMainTemp != null && StringUtils.isNotBlank(chkUpdateCnt) && !chkUpdateCnt.equals(checkMainTemp.getUpdateCnt())){
					String strErrMsg = "ATM拆箱单号：" + outNewNo;
					throw new BusinessException("message.I7224", strErrMsg);
				}
			}
			

	
			//券别面值list
			StoDict stoDict = new StoDict();
			stoDict.setType(Constant.DenominationType.RMB_PDEN); // 人民币：纸币
			List<StoDict> denDictList = stoDictDao.findList(stoDict);
			
			Map<String,String> denDictMap = new HashMap<String,String>();  
			for (StoDict info : denDictList) {
				denDictMap.put(info.getValue(), String.valueOf(info.getUnitVal()));
			}
			
			
			//删除已有数据（逻辑删除）
			ClAtmAmount clAtmAmountDel = new ClAtmAmount();
			clAtmAmountDel.setOutNo(outNewNo);
			clAtmAmountDel.preUpdate();
			//款箱拆箱每笔明细表(ATM)
			clAtmAmountDao.logicDelete(clAtmAmountDel);
			//款箱拆箱每笔面值明细表(ATM)
			clAtmDetailDao.logicDelete(clAtmAmountDel);

			//每笔金额的循环
			for (int k = 0; k < clAtmMain.getAmountInfo().length; k++) {
				sumCheckAmount = "0";
				String strAmountInfo = clAtmMain.getAmountInfo()[k];
				String strAmountDetailInfo = clAtmMain.getAmountDetailInfo()[k];
				if (strAmountInfo == null || StringUtils.isBlank(strAmountInfo)) {
					continue;
				}
				

				//-----------------------------------------------------
				//--------  每笔面值明细表的做成  (CHECK_CASH_DETAIL)  ---------
				//-----------------------------------------------------
				String [] arrPayValue = strAmountDetailInfo.split("@");	// 每笔面值List（券别-张数）
				//新数据做成
				outRowNo =  arrPayValue[0];
				for (int i = 1; i < arrPayValue.length; i++) {
					String [] arrSplit =  arrPayValue[i].split("-");
					if (arrSplit != null && arrSplit.length > 1){
						if (StringUtils.isNotBlank(arrSplit[1])){
							ClAtmDetail checkCashDetail = new ClAtmDetail();
							checkCashDetail = new ClAtmDetail();
							checkCashDetail.setOutNo(outNewNo);						//单号
							checkCashDetail.setOutRowNo(outRowNo);					//行号
							checkCashDetail.setCurrency(Constant.Currency.RMB);		//币种
							checkCashDetail.setDenomination(arrSplit[0]);			//券别
							checkCashDetail.setCountZhang(arrSplit[1]);				//张数
							checkCashDetail.setUnitId(Constant.Unit.piece);			//单位
							String unitVal = denDictMap.get(arrSplit[0]);
							String detailAmount =String.valueOf(Double.valueOf(unitVal) * Double.valueOf(arrSplit[1]));
							checkCashDetail.setDetailAmount(detailAmount);			//金额
							sumCheckAmount = String.valueOf(Double.valueOf(sumCheckAmount) + Double.valueOf(detailAmount));
							checkCashDetail.setParValue(unitVal); 					//面值
							checkCashDetail.preInsert();
							clAtmDetailDao.insert(checkCashDetail);
						}
					}
				 } 

				//-----------------------------------------------------
				//----------  每笔明细表的做成  (CHECK_CASH_AMOUNT)  ----------
				//-----------------------------------------------------
				String [] arrAmountInfo = strAmountInfo.split("@");						// 每笔面值
				ClAtmAmount checkAmount = new ClAtmAmount();
				//登记的场合
				checkAmount.setOutNo(outNewNo);											//单号
				checkAmount.setOutRowNo(outRowNo);										//明细序号
				checkAmount.setCheckAmount(sumCheckAmount);								//清点金额
				checkAmount.setPackNum(arrAmountInfo[1]);								//箱号
				checkAmount.preInsert();
				clAtmAmountDao.insert(checkAmount);

			}

			//-----------------------------------------------------
			//----------  款箱拆箱主表的做成  (CHECK_CASH_MAIN)  ----------
			//-----------------------------------------------------
			int intBoxCount = 0;
			ClAtmMain clAtmMainSave = new ClAtmMain();
			
			//门店检索
			Office office = officeDao.get(clAtmMain.getCustNo());
			
			//每笔明细表的取得总金额的计算
			String mainInputAmount = clAtmMain.getInputAmount();
			String mainCheckAmount = "0";
			String mianDiffAmount  = "0";
			ClAtmAmount cashAmountTemp = new ClAtmAmount();
			cashAmountTemp.setOutNo(outNewNo);
			List<ClAtmAmount>  cashAmountList = clAtmAmountDao.findList(cashAmountTemp);
			for (ClAtmAmount CashItem : cashAmountList) {
				intBoxCount = intBoxCount +1;
				
				mainCheckAmount = String.valueOf(Double.valueOf(mainCheckAmount) 
						   + Double.valueOf(CashItem.getCheckAmount())); 
				
			}
			
			// 差额
			mianDiffAmount = String.valueOf(Double.valueOf(mainCheckAmount) 
					   - Double.valueOf(mainInputAmount)); 
			//保存
			if (StringUtils.isBlank(outNo)){
				//登记的场合
				clAtmMainSave.setOutNo(outNewNo);									// 单号
				clAtmMainSave.setCustNo(clAtmMain.getCustNo());						// 机构ID
				clAtmMainSave.setCustName(office.getName());						// 门店名称
				clAtmMainSave.setInputAmount(mainInputAmount);						// 拆箱总金额
				clAtmMainSave.setCheckAmount(mainCheckAmount);						// 清点总金额
				clAtmMainSave.setDiffAmount(mianDiffAmount);						// 差额
				clAtmMainSave.setBoxCount(String.valueOf(intBoxCount));				// 总笔数
				clAtmMainSave.setRegDate(new Date());								// 登记日期
				clAtmMainSave.setRemarks(clAtmMain.getRemarks());					// 备注
				clAtmMainSave.setCashPlanId(clAtmMain.getCashPlanId());				// 加钞计划ID
				clAtmMainSave.preInsert();
				clAtmMainDao.insert(clAtmMainSave);
			}else{
				//修改的场合
				clAtmMainSave = clAtmMainDao.get(outNewNo);
				clAtmMainSave.setCustNo(clAtmMain.getCustNo());						// 机构ID
				clAtmMainSave.setCustName(office.getName());						// 门店名称
				clAtmMainSave.setInputAmount(mainInputAmount);						// 拆箱总金额
				clAtmMainSave.setCheckAmount(mainCheckAmount);						// 清点总金额
				clAtmMainSave.setDiffAmount(mianDiffAmount);						// 差额
				clAtmMainSave.setBoxCount(String.valueOf(intBoxCount));				// 总笔数
				clAtmMainSave.setRemarks(clAtmMain.getRemarks());					// 备注
				clAtmMainSave.setCashPlanId(clAtmMain.getCashPlanId());				// 加钞计划ID
				clAtmMainSave.preUpdate();
				clAtmMainDao.update(clAtmMainSave);
			}
		} catch (BusinessException e) {
			throw e;
		} catch (Exception e) {
			String strErrMsg = "ATM拆箱单号：" + clAtmMain.getOutNo() ;
			logger.error(strErrMsg + ",保存失败！", e.getMessage());
			throw new BusinessException("message.I7003", strErrMsg);
		}
		
	}

	/**
	 * 异常处理的场合，页面每笔和面值列表的还原
	 * 
	 * @author wanglin
	 * @version 2017年7月7日
	 * @param clAtmMain
	 * @return 还原后的clAtmMain
	 */
	public void dataReturn(ClAtmMain clAtmMain) {
		List<ClAtmAmount> amountList = Lists.newArrayList();
		List<ClAtmDetail> amountDetailList = Lists.newArrayList();
		//每笔金额的循环
		for (int k = 0; k < clAtmMain.getAmountInfo().length; k++) {
			String strAmountInfo = clAtmMain.getAmountInfo()[k];				// 每笔列表
			if (strAmountInfo == null || StringUtils.isBlank(strAmountInfo)) {
				continue;
			}
			
			// 每笔list的还原
			ClAtmAmount clAtmAmount = new ClAtmAmount();
			String [] arrAmountInfo = strAmountInfo.split("@");						
			clAtmAmount.setOutRowNo(arrAmountInfo[0]);
			clAtmAmount.setPackNum(arrAmountInfo[1]);
			clAtmAmount.setCheckAmount(arrAmountInfo[2]);
			amountList.add(clAtmAmount);
			
			String strAmountDetailInfo = clAtmMain.getAmountDetailInfo()[k];	// 每笔面值列表
			String [] arrPayValue = strAmountDetailInfo.split("@");				// 每笔面值List（券别-张数）
			
			//券别面值list
			StoDict stoDict = new StoDict();
			stoDict.setType(Constant.DenominationType.RMB_PDEN); // 人民币：纸币
			List<StoDict> denDictList = stoDictDao.findList(stoDict);
			
			Map<String,String> denDictMap = new HashMap<String,String>();  
			for (StoDict info : denDictList) {
				denDictMap.put(info.getValue(), String.valueOf(info.getUnitVal()));
			}
			//面值list的还原
			String outRowNo =  arrPayValue[0];
			for (int i = 1; i < arrPayValue.length; i++) {
				String [] arrSplit =  arrPayValue[i].split("-");
				if (arrSplit != null && arrSplit.length > 1){
					if (StringUtils.isNotBlank(arrSplit[1])){
						ClAtmDetail clAtmDetail = new ClAtmDetail();
						clAtmDetail.setOutRowNo(outRowNo);
						String unitVal = denDictMap.get(arrSplit[0]);
						clAtmDetail.setParValue(unitVal);
						clAtmDetail.setDenomination(arrSplit[0]);
						clAtmDetail.setCountZhang(arrSplit[1]);
						String detailAmount =String.valueOf(Double.valueOf(unitVal) * Double.valueOf(arrSplit[1]));
						clAtmDetail.setDetailAmount(detailAmount);
						amountDetailList.add(clAtmDetail);
					}
				}
			 } 
		}
		clAtmMain.setAmountInfo(null);
		clAtmMain.setAmountDetailInfo(null);
		clAtmMain.setAmountList(amountList);
		clAtmMain.setAmountDetailList(amountDetailList);
		
	}
	
}