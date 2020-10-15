/**
 * @author Administrator
 * @date 2014-10-16
 * 
 * @Description 
 */
package com.coffer.businesses.modules.store;

import java.util.HashMap;

import com.coffer.businesses.common.Constant;
import com.coffer.core.common.config.Global;

/**
 * @author Lemon
 *
 */
public class StoreConstant extends Constant {

	/**
	 * 物品类型
	 * @author Ray
	 *
	 */
	public static class GoodType {
		/** 货币：01 */
		public static final String CURRENCY = Global.getConfig("sto.goods.goodsType.currency");
		/** 贵重金属：02 */
		public static final String METAL = Global.getConfig("sto.goods.goodsType.metal");
		/** 重空：03 */
		public static final String BLANK_BILL = Global.getConfig("sto.goods.goodsType.blankBill");
	}

	/**
	 * 套别
	 * 
	 * @author yuxixuan
	 */
	public static class Sets {
		/** 外币套别（占位） */
		public static final String FOREIGN_CURRENCY_SETS = Global.getConfig("sto.relevance.foreignCurrencySets");
	}
	
	/**
	 * 报表种别
	 * 
	 * @author chengshu
	 */
	public static class ReportType {
		/** 物品库存报表 */
		public static final String GOODS_STORES = "goodsStores";
	}

	/**
	 * 物品字典类型
	 * 
	 * @author yuxixuan
	 *
	 */
	public static class GoodDictType {
		/** 类型：单位 */
		public static final String UNIT = Global.getConfig("sto.dict.type.unit");
	}
	
	/**
	 * 设备状态
	 * 
	 * @author chengshu
	 *
	 */
	public static class TeStatus {
		/** 有效 */
		public static final String Valid = "0";
		/** 无效 */
		public static final String Invalid = "1";
	}
	
	/**
	 * 设备状态
	 * 
	 * @author chengshu
	 *
	 */
	public static class BusinessType {
		/** 重空 */
		public static final String Empty = "13";

		/** 其他需要，另行追加，根据系统字典【business_type】 */
		
	}
	
	/**
	 * 库区物品使用状态
	 * @author WangBaozhong
	 *
	 */
	public static class StoreAreaGoodsUsedStatus {
		/**
		 * 使用状态 0：未使用
		 */
		public static final String GOODS_STATUS_UNUSED = "0";
		/**
		 * 使用状态 1：已预订
		 */
		public static final String GOODS_STATUS_RESERVED = "1";
		/**
		 *  使用状态 2:已使用
		 */
		public static final String GOODS_STATUS_USED = "2";
		/**
		 *  使用状态 3:已替换
		 */
		public static final String GOODS_STATUS_REPLACE = "3";
		/**
		 *  使用状态 4:已清除
		 */
		public static final String GOODS_STATUS_CLEAR = "4";
	}
	
    /**
     * 根据币种和材质，取得字典中币种对应的Type
     * 
     * @author chengshu
     */
    @SuppressWarnings("serial")
    public final static HashMap<String, String> currencyMap = new HashMap<String, String>() {
        {
            // 人民币：纸币
            put("1011", "cnypden");
            // 人民币：硬币
            put("1012", "cnyhden");
        }
    };
    
    /**
     * RFID使用标识
     * 
     * @author chengshu
     *
     */
    public static class RfidUseFlag {
        /** RFID初始化绑定，未入库扫描，可以修改绑定信息 */
        public static final String init = "0";
        /** RFID入库扫描成功，不可以修改绑定信息 */
        public static final String use = "1";
        /** RFID出库扫描成功，可以修改绑定信息 */
        public static final String outStore = "2";
        /** RFID商行接收，可以修改绑定信息 */
        public static final String businessBankAccept = "3";
        /** RFID替换，可以修改绑定信息 */
        public static final String rfidReplace = "4";
        /** RFID已清除，可以修改绑定信息 */
        public static final String rfidClear = "5";
    }
    
    /**
     * 是否按照物品类型存放不同库区
    * Title: IsSavegoodsByAreaType 
    * <p>Description: </p>
    * @author wangbaozhong
    * @date 2016年6月27日 下午4:58:03
     */
    public static class IsSavegoodsByAreaType {
    	/** 按照物品类型存放不同库区 **/
    	public static final String YES = "yes";
    }
    /**
     * 
    * Title: GoodsClassification 
    * <p>Description: 物品类别</p>
    * @author wangbaozhong
    * @date 2016年8月18日 下午8:12:59
     */
    public static class GoodsClassification {
    	/** 流通券 **/
    	public static final String FULL_COUPON = "06";
    	/** 残损未复点券 **/
    	public static final String DAMAGED_COUPON = "07";
    	/** 原封新券 **/
    	public static final String ORIGINAL_COUPON = "09";
    }
    
    
    /**
     * 印章操作类型
     * @author Zhengkaiyuan
     */
    public static class StamperOperation{
    	/** 插入、修改  如果数据存在直接覆盖 **/
    	public static final String STAMPER_ADD = "1";
    	/** 删除 **/
    	public static final String STAMPER_DELETE = "2";
    	/** 查询 **/
    	public static final String STAMPER_QUERY = "3";
    }
    /**
     * 印章类型
     * @author Zhengkaiyuan
     */
    public static class StamperType{
    	/** 机构印章类型 **/
    	public static final String OFFICE_STAMPER_TYPE = "OFFICE_STAMPER_TYPE";
    	/** 人行机构印章类型 **/
    	public static final String PBOC_OFFICE_STAMPER_TYPE = "PBOC_OFFICE_STAMPER_TYPE";
    }
    /**
     * 印章数据是否已上传
     * @author Zhengkaiyuan
     */
    public static class IsStamperUpload{
    	/** 未上传 **/
    	public static final String STAMPER_NO_UPLOAD = "0";
    	/** 已上传 **/
    	public static final String STAMPER_UPLOAD = "1";
    }
    
    /**
     * 
    * Title: OperationType 
    * <p>Description: 操作类型 </p>
    * @author wangbaozhong
    * @date 2016年9月20日 上午8:53:52
     */
    public static class OperationType {
    	/** 保存登记信息 **/
    	public static final String SAVE_REGIST = "saveRegist";
    	/** 保存修改信息 **/
    	public static final String SAVE_UPDATE = "saveUpdate";
    	/** 保存人行机构印章 **/
    	public static final String SAVE_PBOC_STAMPER = "savePobcStamper";
    	/** 跳转至登记页面 **/
    	public static final String TO_REGIST_PAGE = "toRegistPage";
    	/** 跳转至更新页面 **/
    	public static final String TO_UPDATE_PAGE = "toUpdatePage";
    	/** 跳转至查看明细页面 **/
    	public static final String TO_SHOW_DETAIL_PAGE = "toShowDetailPage";
    	/** 跳转至添加人行机构印章页面 **/
    	public static final String TO_ADD_PBOC_STAMPER_PAGE = "toAddPbocStamperPage";
    	
    }
    
    /**
     * 
    * Title: StoreAreaType 
    * <p>Description: 库区类型</p>
    * @author wangbaozhong
    * @date 2016年9月28日 上午9:52:31
     */
    public static class StoreAreaType {
    	/** 原封新券库区 **/
    	public static final String STORE_AREA_ORIGINAL = "02";
    }
}
