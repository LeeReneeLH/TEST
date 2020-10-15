package com.coffer.businesses.modules.report.v01.service;

import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import org.apache.commons.lang.StringEscapeUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.coffer.businesses.common.Constant;
import com.coffer.businesses.common.utils.CommonUtils;
import com.coffer.businesses.modules.allocation.AllocationConstant;
import com.coffer.businesses.modules.allocation.v01.entity.AllAllocateInfo;
import com.coffer.businesses.modules.allocation.v01.entity.AllAllocateItem;
import com.coffer.businesses.modules.report.ReportConstant;
import com.coffer.businesses.modules.store.v01.entity.StoAreaSettingInfo;
import com.coffer.businesses.modules.store.v01.entity.StoDict;
import com.coffer.businesses.modules.store.v01.entity.StoGoodsLocationInfo;
import com.coffer.businesses.modules.store.v01.entity.StoStoresInfo;
import com.coffer.core.common.service.BaseService;
import com.coffer.core.common.utils.DateUtils;
import com.coffer.core.common.utils.ObjectUtils;
import com.coffer.core.common.utils.StringUtils;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

/**
 * 图形数据Service
 * @author chengshu
 * @version 2016-05-18
 */
@Service
@Transactional(readOnly = true)
public class GraphService extends BaseService {

	/**
	 * @author chengshu
	 * @version 2016/05/18
	 *
	 * 组装人民银行物品库存详细信息（饼图）
	 * @param pbcStoresInfoList 物品库存详细信息
	 * @param titleList 饼图标题信息
	 * @param dataList 饼图数据信息
	 */
    public void makeBankOutletsInOutEChartData(List<AllAllocateInfo> allocateInfoList, List<String> titleList,
			List<Map<String, String>> dataList) {

		Map<String, String> typeMap = Maps.newHashMap();
		Map<String, Integer> dateMap = Maps.newHashMap();

		// 取得所有物品信息，计算数量
		for (AllAllocateInfo allocateInfo : allocateInfoList) {
			for (AllAllocateItem item : allocateInfo.getAllAllocateItemList()) {

				// 记录已经确认的物品信息
				if(AllocationConstant.confirmFlag.Confirm.equals(item.getConfirmFlag())){
					// 记录物品编号和物品名称
					if (!typeMap.containsKey(item.getGoodsId())) {
						typeMap.put(item.getGoodsId(), item.getGoodsName());
					}

					// 记录物品数量
					if (!dateMap.containsKey(item.getGoodsId())) {
						dateMap.put(item.getGoodsId(), 1);
					} else {
						dateMap.put(item.getGoodsId(), dateMap.get(item.getGoodsId()) + 1);
					}
				}
			}
		}

		// 饼图使用数据
		Map<String, String> data = Maps.newHashMap();

		// 设置饼图使用的数据格式
		for (Entry<String, String> entry : typeMap.entrySet()) {

			// 设置饼图分类标题
			titleList.add(entry.getValue());

			// 初始化饼图数据
			data = Maps.newHashMap();
			// 设置饼图数据
			data.put("name", getDenominationByGoodsName(entry.getValue()));
			data.put("value", CommonUtils.toString(dateMap.get(entry.getKey())));

			dataList.add(data);
		}
	}
    
    /**
	 * @author chengshu
	 * @version 2017-08-24
	 * 
	 * @Description 组装页面饼图用JSON数据
	 * 
	 * @param jsonData 页面饼图用JSON数据
	 * @param rmbGoodsKeysMap 人民币库存信息字典
	 * @param rmbGoodsStoreMap 人民币库存信息数量
	 * @param interGoodsKeysMap 国际货币库存信息数量
	 * @param interGoodsStoreMap 国际货币库存信息数量
	 */
	public void setStoreInfoPieJsonData(Map<String, Object> jsonData, Map<String, Map<String, String>> rmbGoodsKeysMap,
			Map<String, Map<String, Long>> rmbGoodsStoreMap, Map<String, String> interGoodsKeysMap, Map<String, Long> interGoodsStoreMap) {
		
		// 设置人民币库存饼图JSON
		setRMBJsonData(jsonData, rmbGoodsKeysMap, rmbGoodsStoreMap);
		
		// 设置国际货币库存饼图JSON
		setInterJsonData(jsonData, interGoodsKeysMap, interGoodsStoreMap);
		
		// 设置库存为空的币种，设置空数据
		setStoreNullJsonData(jsonData, rmbGoodsKeysMap, interGoodsKeysMap);
	}
	
    /**
	 * @author chengshu
	 * @version 2017-08-24
	 * 
	 * @Description 组装金库库存空数据JSON数据
	 * 
	 * @param jsonData 页面饼图用JSON数据
	 * @param rmbGoodsKeysMap 人民币库存信息字典
	 * @param interGoodsKeysMap 国际货币库存信息数量
	 */
	public void setStoreNullJsonData(Map<String, Object> jsonData, Map<String, Map<String, String>> rmbGoodsKeysMap,
			Map<String, String> interGoodsKeysMap) {

		List<String> titleNullList = Lists.newArrayList();
		titleNullList.add("没有数据");

		List<Map<String, String>> dataNullList = Lists.newArrayList();
		// 饼图使用数据
		Map<String, String> data = Maps.newHashMap();
		// 设置饼图数据
		data.put("name", "无数据");
		data.put("value", "1");
		dataNullList.add(data);

		// 人民币库存信息为空的场合
		String currency = ReportConstant.Currency.RMB;
		if(!rmbGoodsKeysMap.containsKey(currency + ReportConstant.MoneyType.CIRCULATION_MONEY)) {
			jsonData.put("titleFullPie", titleNullList);
			jsonData.put("dataFullPie", dataNullList);
		}
		if(!rmbGoodsKeysMap.containsKey(currency + ReportConstant.MoneyType.DEMANGED_MONEY)) {
			jsonData.put("titleDamagePie", titleNullList);
			jsonData.put("dataDamagePie", dataNullList);
		}
		if(!rmbGoodsKeysMap.containsKey(currency + ReportConstant.MoneyType.ATM_MONEY)) {
			jsonData.put("titleAtmPie", titleNullList);
			jsonData.put("dataAtmPie", dataNullList);
		}
		if(!rmbGoodsKeysMap.containsKey(currency + ReportConstant.MoneyType.COUNTWAIT_MONEY)) {
			jsonData.put("titleCountwaitPie", titleNullList);
			jsonData.put("dataCountwaitPie", dataNullList);
		}

		// 国际货币库存信息为空的场合
		if(0 == interGoodsKeysMap.size()) {
			jsonData.put("titleInterPie", titleNullList);
			jsonData.put("dataInterPie", dataNullList);
		}
	}
	
    /**
	 * @author chengshu
	 * @version 2017-08-24
	 * 
	 * @Description 组装金库库存空数据JSON数据
	 * 
	 * @param jsonData 页面饼图用JSON数据
	 */
	public void setCommonNullJsonData(Map<String, Object> jsonData) {

		List<String> titleNullList = Lists.newArrayList();
		titleNullList.add("没有数据");

		List<Map<String, String>> dataNullList = Lists.newArrayList();
		// 饼图使用数据
		Map<String, String> data = Maps.newHashMap();
		// 设置饼图数据
		data.put("name", "无数据");
		data.put("value", "1");
		dataNullList.add(data);

		jsonData.put("title", titleNullList);
		jsonData.put("data", dataNullList);
	}
    
    /**
	 * @author chengshu
	 * @version 2016/05/18
	 *
	 * @Description 组装各库区内物品详细信息（柱图）
	 * @param areaList 库区和物品信息
	 * @param titleList 面值标题信息
	 * @param areaTitleList 库区标题信息
	 * @param dataList 数据信息
	 * @param denominationList 面值信息
	 */
	public void makePbcAreaAndGoodsInfoEChartData(List<StoAreaSettingInfo> areaList, List<String> titleList,
	        List<String> areaTitleList, List<Map<String, Object>> dataList, List<StoDict> denominationList) {
	
	    // 数据
	    Map<String, Object> data = Maps.newHashMap();
	    List<String> valueList = Lists.newArrayList();
	    TreeMap<String, Map<String, Object>> dataMap = Maps.newTreeMap();
	
	    // 设置面值标题
	    TreeMap<String, String> goodsDictMap = Maps.newTreeMap();
	    for (StoDict stoDict : denominationList) {
	        // 面值标题信息
	        titleList.add(stoDict.getLabel());
	        goodsDictMap.put(stoDict.getValue(), stoDict.getLabel());
	
	        // 初始化数据
	        data = Maps.newHashMap();
	        valueList = Lists.newArrayList();
	        // 实现设置页面显示用数据为空，后面再详细设置(valueList)
	        data.put("name", stoDict.getLabel());
	        data.put("data", valueList);
	        dataMap.put(stoDict.getValue(), data);
	    }
	
	    String areaName = "";
	
	    for (StoAreaSettingInfo areaInfo : areaList) {
	        // 取得当前库区名称
	        areaName = areaInfo.getStoreAreaName();
	        // 设置库区标题
	        areaTitleList.add(areaName);
	
	        // 循环所有面值，设置当前库区内各面值的数量
	        setAreaGoodsNum(goodsDictMap, areaInfo.getGoodsLocationInfoList(), dataMap);
	    }
	
	    for (Map.Entry<String, Map<String, Object>> dataEntry : dataMap.entrySet()) {
	        if (dataEntry.getValue().get("valid") == null) {
	            titleList.remove(dataEntry.getValue().get("name"));
	            continue;
	        }
	        dataList.add(dataEntry.getValue());
	    }
	}

	/**
	 * @author chengshu
	 * @version 2017-08-24
	 * 
	 * @Description 组装出入库信息（线图）
	 * @param inList 入库（上缴）现金量信息
	 * @param outList 出库（下拨）现金量信息
	 * 
	 * @return
	 */
	public Map<String, Object> makeInOutAmountLineData(List<AllAllocateInfo> inAllocateList,
			List<AllAllocateInfo> outAllocateList) {
	
	    // 设置时间轴列表
	    Calendar cal = Calendar.getInstance();
	    cal.add(Calendar.DAY_OF_MONTH, -30);
	    List<String> dateList = Lists.newArrayList();
	
	    String ln = "";
	    for (int i = 0; i < 30; i++) {
	        // 单数换行，横州上下显示
	        ln = "";
	        if (i % 2 > 0) {
	            ln += "\n";
	        }
	
	        cal.add(Calendar.DAY_OF_MONTH, 1);
	        dateList.add(ln + DateUtils.formatDate(cal.getTime(), Constant.Dates.FORMATE_YYYY_MM_DD));
	    }
	
	    // 入库信息
	    List<Double> inAmountList = setInoutAllocateInfo(inAllocateList, dateList);
	
	    // 出库信息
	    List<Double> outAmountList = setInoutAllocateInfo(outAllocateList, dateList);
	
	    // 组装好的数据返回页面
	    Map<String, Object> jsonData = Maps.newHashMap();
	    jsonData.put("date", dateList);
	    jsonData.put("inDate", inAmountList);
	    jsonData.put("outDate", outAmountList);
	
	    return jsonData;
	}

	/**
	 * @author chengshu
	 * @version 2017-08-24
	 * 
	 * @Description 设置库存详细信息
	 * 
	 * @param rmbGoodsKeysMap 人民币库存信息字典
	 * @param rmbGoodsStoreMap 人民币库存信息数量
	 * @param interGoodsKeysMap 国际货币库存信息字典
	 * @param interGoodsStoreMap 国际货币库存信息金额
	 * 
	 * @return 库存饼图显示用信息
	 */
	public void setStoreInfoPieData(List<StoStoresInfo> storeInfoList, Map<String, Map<String, String>> rmbGoodsKeysMap, Map<String, Map<String, Long>> rmbGoodsStoreMap,
			Map<String, String> interGoodsKeysMap, Map<String, Long> interGoodsStoreMap) {
	
		// 库存信息存在的场合，循环获取库存信息
		for (StoStoresInfo storeInfo : storeInfoList) {
			if (StringUtils.startsWith(storeInfo.getGoodsId(), ReportConstant.Currency.RMB)) {
				setRMBStorePieData(storeInfo.getGoodsId(), storeInfo.getGoodsName(), storeInfo.getStoNum(), rmbGoodsKeysMap, rmbGoodsStoreMap);
			} else {
				setOtherStorePieData(storeInfo.getGoodsId(), storeInfo.getGoodsName(), storeInfo.getStoNum(), interGoodsKeysMap, interGoodsStoreMap);
			}
		}
	}

	/**
	 * @author chengshu
	 * @version 2017-08-24
	 * 
	 * @Description 组装人民币JSON数据
	 * 
	 * @param jsonData 页面饼图用JSON数据
	 * @param rmbGoodsKeysMap 人民币库存信息字典
	 * @param rmbGoodsStoreMap 人民币库存信息数量
	 */
	private void setRMBJsonData(Map<String, Object> jsonData, Map<String, Map<String, String>> rmbGoodsKeysMap,
			Map<String, Map<String, Long>> rmbGoodsStoreMap) {

		boolean fullFlag = false;
		boolean damageFlag = false;
		boolean atmFlag = false;
		boolean countwaitFlag = false;
		
		List<String> titleNullList = Lists.newArrayList();
		titleNullList.add("无数据");
		
		List<Map<String, String>> dataNullList = Lists.newArrayList();
		// 饼图使用数据
		Map<String, String> data = Maps.newHashMap();
		// 设置饼图数据
		data.put("name", "无数据");
		data.put("value", " ");
		dataNullList.add(data);

		// 设置人民币库存饼图信息
		for (Entry<String, Map<String, String>> currencyEntry : rmbGoodsKeysMap.entrySet()) {

			if (StringUtils.endsWith(currencyEntry.getKey(), ReportConstant.MoneyType.CIRCULATION_MONEY)) {
				fullFlag = true;
				// 初始化（流通币）饼图分类标题
				List<String> titleFullList = Lists.newArrayList();
				// 初始化（流通币）饼图显示数据
				List<Map<String, String>> dataFullList = Lists.newArrayList();

				// 设置JSON数据
				setStorePieData(titleFullList, dataFullList, currencyEntry.getValue(),
						rmbGoodsStoreMap.get(currencyEntry.getKey()));
				jsonData.put("titleFullPie", titleFullList);
				jsonData.put("dataFullPie", dataFullList);

			} else if (StringUtils.endsWith(currencyEntry.getKey(), ReportConstant.MoneyType.DEMANGED_MONEY)) {
				damageFlag = true;
				// 初始化（残损币）饼图分类标题
				List<String> titleDamageList = Lists.newArrayList();
				// 初始化（残损币）饼图显示数据
				List<Map<String, String>> dataDamageList = Lists.newArrayList();

				// 设置JSON数据
				setStorePieData(titleDamageList, dataDamageList, currencyEntry.getValue(),
						rmbGoodsStoreMap.get(currencyEntry.getKey()));
				jsonData.put("titleDamagePie", titleDamageList);
				jsonData.put("dataDamagePie", dataDamageList);

			} else if (StringUtils.endsWith(currencyEntry.getKey(), ReportConstant.MoneyType.ATM_MONEY)) {
				atmFlag = true;
				// 初始化（ATM币）饼图分类标题
				List<String> titleAtmList = Lists.newArrayList();
				// 初始化（ATM币）饼图显示数据
				List<Map<String, String>> dataAtmList = Lists.newArrayList();

				// 设置JSON数据
				setStorePieData(titleAtmList, dataAtmList, currencyEntry.getValue(),
						rmbGoodsStoreMap.get(currencyEntry.getKey()));
				jsonData.put("titleAtmPie", titleAtmList);
				jsonData.put("dataAtmPie", dataAtmList);

			} else if (StringUtils.endsWith(currencyEntry.getKey(), ReportConstant.MoneyType.COUNTWAIT_MONEY)) {
				countwaitFlag = true;
				// 初始化（待整点币）饼图分类标题
				List<String> titleCountwaitList = Lists.newArrayList();
				// 初始化（待整点币）饼图显示数据
				List<Map<String, String>> dataCountwaitList = Lists.newArrayList();

				// 设置JSON数据
				setStorePieData(titleCountwaitList, dataCountwaitList, currencyEntry.getValue(),
						rmbGoodsStoreMap.get(currencyEntry.getKey()));
				jsonData.put("titleCountwaitPie", titleCountwaitList);
				jsonData.put("dataCountwaitPie", dataCountwaitList);
			}
		}

		if (!fullFlag) {
			jsonData.put("titleFullPie", titleNullList);
			jsonData.put("dataFullPie", dataNullList);
		}
		if (!damageFlag) {
			jsonData.put("titleDamagePie", titleNullList);
			jsonData.put("dataDamagePie", dataNullList);
		}
		if (!atmFlag) {
			jsonData.put("titleAtmPie", titleNullList);
			jsonData.put("dataAtmPie", dataNullList);
		}
		if (!countwaitFlag) {
			jsonData.put("titleCountwaitPie", titleNullList);
			jsonData.put("dataCountwaitPie", dataNullList);
		}
	}
	
    /**
	 * @author chengshu
	 * @version 2017-08-24
	 * 
	 * @Description 组装国际货币JSON数据
	 * 
	 * @param jsonData 页面饼图用JSON数据
	 * @param interGoodsKeysMap 国际货币库存信息字典
	 * @param interGoodsStoreMap 国际货币库存信息金额
	 */
	private void setInterJsonData(Map<String, Object> jsonData, Map<String, String> interGoodsKeysMap,
			Map<String, Long> interGoodsStoreMap) {

		// 初始化（待整点币）饼图分类标题
		List<String> titleInterList = Lists.newArrayList();
		// 初始化（待整点币）饼图显示数据
		List<Map<String, String>> dataInterList = Lists.newArrayList();
		
		// 设置JSON数据 
		setStorePieData(titleInterList, dataInterList, interGoodsKeysMap, interGoodsStoreMap);
		jsonData.put("titleInterPie", titleInterList);
		jsonData.put("dataInterPie", dataInterList);
	}
	
    /**
	 * @author chengshu
	 * @version 2017-08-24
	 * 
	 * @Description 组装转换JSON用的数据
	 * 
	 * @param jsonData 页面饼图用JSON数据
	 * @param rmbGoodsKeysMap 人民币库存信息字典
	 * @param rmbGoodsStoreMap 人民币库存信息数量
	 * @param interGoodsKeysMap 国际货币库存信息字典
	 * @param interGoodsStoreMap 国际货币库存信息金额
	 * 
	 * @return 库存饼图显示用JSON数据
	 */
	private void setStorePieData(List<String> titleList, List<Map<String, String>> dataList, Map<String, String> typeMap,
			Map<String, Long> dateMap) {

		// 饼图使用数据
		Map<String, String> data = Maps.newHashMap();

		// 设置饼图使用的数据格式
		for (Entry<String, String> entry : typeMap.entrySet()) {

			// 设置饼图分类标题
			titleList.add(entry.getValue());

			// 初始化饼图数据
			data = Maps.newHashMap();
			// 设置饼图数据
			data.put("name", getDenominationByGoodsName(entry.getValue()));
			data.put("value", CommonUtils.toString(dateMap.get(entry.getKey())));

			dataList.add(data);
		}
	}

	/**
     * @author chengshu
     * @version 2016/05/18
     *
     * @Description 组装饼图显示的物品名称
     * @param areaList 物品全称
     */
    private String getDenominationByGoodsName(String goodsName) {
        String[] goods = goodsName.split(Constant.Punctuation.HALF_UNDERLINE);

        if (goods.length == 6) {
        	StringBuffer sb = new StringBuffer();
        	sb.append(goods[1]).append("\r\n");
        	sb.append(goods[3]).append("\r\n");
        	sb.append(goods[4]).append("\r\n");
        	sb.append(goods[5]);
            return StringEscapeUtils.unescapeHtml(sb.toString());
        }
        return "";
    }
    
    /**
     * @author chengshu
     * @version 2016/05/18
     *
     * 设置各个库区内物品数量信息
     * @param goodsDictMap 面值字典<Code:Name>
     * @param goodsList 当前库区内物品信息
     * @param dataMap 页面显示用数据
     */
    @SuppressWarnings("unchecked")
    private void setAreaGoodsNum(TreeMap<String, String> goodsDictMap, List<StoGoodsLocationInfo> goodsList,
            TreeMap<String, Map<String, Object>> dataMap) {

        boolean equalsFlag = false;
        String goodesNum = "";

        for (Map.Entry<String, String> goodsEntry : goodsDictMap.entrySet()) {

            // 循环库区内物品信息，取得面值的数量
            for (StoGoodsLocationInfo goods : goodsList) {

                // 库区内物品有当前面值的场合，设置当前面值的数量
                if (goodsEntry.getKey().equals(StringUtils.substring(goods.getGoodsId(), 7, 9))) {
                    equalsFlag = true;
                    goodesNum = CommonUtils.toString(goods.getGoodsNum());
                    break;
                }
            }

            // 设置物品数量
            if (equalsFlag) {
                ((List<String>) dataMap.get(goodsEntry.getKey()).get("data")).add(goodesNum);
                dataMap.get(goodsEntry.getKey()).put("valid", "1");
                equalsFlag = false;
                goodesNum = "";
            } else {
                // 库区内没有当前面值的场合，设置空
                ((List<String>) dataMap.get(goodsEntry.getKey()).get("data")).add("");
            }
            
        }
    }

	/**
	 * @author chengshu
	 * @version 2017/08/24
	 *
	 * @Description 组装出入库信息（线图）
	 * 
	 * @param allocateInfoList 出入库现金量信息
	 * @param dataList 日期列表
	 */
	private List<Double> setInoutAllocateInfo(List<AllAllocateInfo> allocateInfoList, List<String> dataList) {

		TreeMap<String, Double> allocateAmountMap = Maps.newTreeMap();

		boolean flag = true;
		boolean inFlag = false;
		Double confirmAmount = new Double(0);

		for (String date : dataList) {

			flag = true;
			inFlag = false;
			if (date.startsWith("\n")) {
				date = date.substring(1);
			}

			for (AllAllocateInfo inoutAllocateInfo : allocateInfoList) {
				if (date.equals(DateUtils.formatDate(inoutAllocateInfo.getScanDate(), Constant.Dates.FORMATE_YYYY_MM_DD))) {
					// 取得当前的金额
					confirmAmount = ObjectUtils.bigDecimalToDouble(inoutAllocateInfo.getConfirmAmount());
					// 找到匹配日期
					inFlag = true;
					// 有当日信息的场合，合计现金量
					if (allocateAmountMap.containsKey(date)) {
						allocateAmountMap.put(date, (allocateAmountMap.get(date) + confirmAmount));
					} else {
						// 无当日信息的场合，添加到Map里
						allocateAmountMap.put(date, confirmAmount);
					}

					flag = false;
				} else if (inFlag) {
					break;
				}
			}

			// 没有当日信息的场合，设置成0
			if (flag) {
				allocateAmountMap.put(date, (double) 0);
			}
		}

		// 把每日出入库现金的合计量，保存到页面显示用List里
		List<Double> amountList = Lists.newArrayList();
		for (Map.Entry<String, Double> entry : allocateAmountMap.entrySet()) {
			amountList.add(entry.getValue());
		}

		return amountList;
	}
	
	/**
	 * @author chengshu
	 * @version 2017-08-24
	 * 
	 * @Description 设置人民币库存详细信息
	 * 
	 * @param goodsId 物品ID
	 * @param goodsName 物品名称
	 * @param goodsNum 物品数量
	 * @param goodsKeysMap 物品字典信息
	 * @param goodsStoreMap 物品库存信息
	 * 
	 * @return 库存饼图显示用信息
	 */
	private void setRMBStorePieData(String goodsId, String goodsName, long goodsNum,
			Map<String, Map<String, String>> goodsKeysMap, Map<String, Map<String, Long>> goodsStoreMap) {

		// 取得币种和种别
		String goodsIdAndMoneyType = StringUtils.substring(goodsId, 0, 5);
		// 物品信息
		String goodsType = StringUtils.substring(goodsId, 5);

		// 保存物品库存信息
		if (goodsKeysMap.containsKey(goodsIdAndMoneyType)) {
			goodsKeysMap.get(goodsIdAndMoneyType).put(goodsType, goodsName);
			goodsStoreMap.get(goodsIdAndMoneyType).put(goodsType, goodsNum);

		} else {
			Map<String, String> nameMap = Maps.newHashMap();
			nameMap.put(goodsType, goodsName);
			Map<String, Long> numMap = Maps.newHashMap();
			numMap.put(goodsType, goodsNum);
			
			goodsKeysMap.put(goodsIdAndMoneyType, nameMap);
			goodsStoreMap.put(goodsIdAndMoneyType, numMap);
			
		}
	}

	/**
	 * @author chengshu
	 * @version 2017-08-24
	 * 
	 * @Description 设置国际货币（人民币以外）库存详细信息
	 * 
	 * @param goodsId 物品ID
	 * @param goodsName 物品名称
	 * @param goodsNum 物品数量
	 * @param goodsKeysMap 物品字典信息
	 * @param goodsStoreMap 物品库存信息
	 * 
	 * @return 库存饼图显示用信息
	 */
	private void setOtherStorePieData(String goodsId, String goodsName, long amount, Map<String, String> goodsKeysMap,
			Map<String, Long> goodsStoreMap) {

		// 取得币种
		String currency = StringUtils.substring(goodsId, 3);

		// 保存物品库存信息
		if (goodsKeysMap.containsKey(currency)) {
			goodsStoreMap.put(currency, goodsStoreMap.get(currency) + amount);

		} else {
			goodsKeysMap.put(currency, goodsName);
			goodsStoreMap.put(currency, amount);
		}
	}
}
