package com.coffer.businesses.modules.allocation;


import com.coffer.businesses.common.Constant;

public class AllocationConstant extends Constant {

	/**
	 * @author chengshu 业务类型
	 */
	public static class BusinessType {
		/** 库外箱袋调拨 */
		public static final String OutBank_Box_Handover = "10";

		/** 库间总括类别 */
		public static final String Between = "2";
		/** 库间现金 */
		public static final String Between_Cash = "20";
		/** 库间清分 */
		public static final String Between_Clear = "21";
		/** 库间配钞 */
		public static final String Between_ATM_Add = "22";
		/** 库间清机 */
		public static final String Between_ATM_Clear = "23";
		/** 库间 上门收款 入库 */
		public static final String DOOR_TO_DOOR_COLLECTION = "24";
		
		/** 现金操作：现金上缴 */
		public static final String OutBank_Cash_HandIn = "30";
		/** 现金操作：现金预约 */
		public static final String OutBank_Cash_Reservation = "31";
		
		/** 重空预约 **/
		public static final String Imp_Blank_Doc_Reservation = "40";
		
		/** 申请上缴 **/
		public static final String PBOC_APPLICATION_HANDIN = "50";
		/** 申请下拨 **/
		public static final String PBOC_APPLICATION_ALLOCATION = "51";
		/** 代理上缴 **/
		public static final String PBOC_AGENT_HANDIN = "52";
		/** 调拨入库 **/
		public static final String PBOC_ALLOCATION_IN_STORE = "53";
		/** 调拨出库 **/
		public static final String PBOC_ALLOCATION_OUT_STORE = "54";
		/** 销毁出库 **/
		public static final String PBOC_DESTROY_OUT_STORE = "55";
		/** 复点出入库 */
		public static final String PBOC_RE_COUNTING = "56";
		/** 原封新券入库 **/
		public static final String PBOC_ORIGINAL_BANKNOTE_IN_STORE = "57";
		/** 人行初始化库区 */
		public static final String PBOC_INIT_STORE = "58";
		/** 人行清理库区 */
		public static final String PBOC_CLEAR_STORE = "59";
		/** 人行原封新券出库业务 **/
		public static final String PBOC_ORIGINAL_BANKNOT_OUT = "60";
		/**
		 * @author qipeihong 钞箱出库 add
		 */
		public static final String ATM_BOX_HANDOUT = "61";
		
		/**
		 * @author qipeihong 钞箱入库 add
		 */
		public static final String ATM_BOX_HANDIN = "62";
	}
	
	/**
	 * @author chengshu 出入库种别
	 *
	 */
	public static class InOutCoffer {
		/** 金库外交接 */
        public static final String OUT  = "0";
		/** 金库内交接 */
        public static final String IN  = "1";
    }
	
	/**
	 * @author wangbaozhong 交接状态（节点）
	 */
	public static class Status {
		/** 登记 */
		public static final String Register = "10";
		/** 待交接（已过扫描门，未指纹交接） */
		public static final String HandoverTodo = "11";
		/** 在途（已指纹交接） */
		public static final String Onload = "12";
		/** 已接收（接收完成） */
		public static final String Accpeted = "13";
		/** 已完成（指纹交接完成） */
		public static final String Finish = "14";
		/** 已确认 (库间确认) **/
		public static final String BetweenConfirm = "15";
		/** 现金预约未配款 **/
		public static final String CashOrderQuotaNo = "20";
		/** 现金预约已装箱 */
		public static final String CashOrderQuotaYes = "21";
		/** 网点上缴金额未确认 */
		public static final String BankOutletsHandInConfirmNo = "22";
		/** 网点上缴金额已确认 */
		public static final String BankOutletsHandInConfirmYes = "23";
		/** 重空预约未装配 **/
		public static final String ImpBlankDocQuotaNo = "24";
		/** 重空预约已装配 **/
		public static final String ImpBlankDocQuotaYes = "25";
		/** 撤回 **/
		public static final String CANCEL_STATUS = "90";
	}


	
    /**
	 * 人行申请调拨状态
	 * 
	 * @author chengshu
	 */
    public static class PbocOrderStatus {

		/** 待审批 **/
        public static final String TO_APPROVE_STATUS = "20";
		/** 驳回 **/
        public static final String REJECT_STATUS = "21";
		/** 待配款 **/
        public static final String TO_QUOTA_STATUS = "22";

		/** 人行申请下拨状态 */
        public static class OutHandoverStatus {
			/** 待出库 **/
            public static final String TO_OUT_STORE_STATUS = "40";
			/** 待交接 **/
            public static final String TO_HANDOVER_STATUS = "41";
			/** 待接收 **/
            public static final String TO_ACCEPT_STATUS = "42";
        }

		/** 人行申请上缴状态 */
        public static class InHandoverStatus {
			/** 待入库 **/
            public static final String TO_IN_STORE_STATUS = "40";
			/** 待交接 **/
            public static final String TO_HANDOVER_STATUS = "41";
        }

		/** 复点管理状态 */
        public static class RecountingStatus {
			/** 待出库 **/
            public static final String TO_OUT_STORE_STATUS = "40";
			/** 待出库交接 **/
            public static final String TO_OUT_STORE_HANDOVER_STATUS = "41";
			/** 清分中 **/
            public static final String TO_RECOUNTING_STATUS = "42";
			/** 待入库交接 **/
            public static final String TO_IN_STORE_HANDOVER_STATUS = "43";
        }

		/** 完成 **/
        public static final String FINISH_STATUS = "99";
    }

	/**
	 * 场所
	 * 
	 * @author wangbaozhong
	 */
	public static class Place {
		/** 空箱 */
		public static final String BlankBox = "10";
		/** 在网点 */
		public static final String BankOutlets = "11";
		/** 在途 */
		public static final String onPassage = "12";
		/** 在库房 */
		public static final String StoreRoom = "13";
		/** 在整点室 */
		public static final String ClassficationRoom = "14";
		/** 在票据室 */
		public static final String BillRoom = "15";
		/** 在重空室 */
		public static final String ImportantcredenceRoom = "16";
		/** 在押运寄存中心 */
		public static final String EscortRoom = "17";

	}

	/**
	 * @author chengshu 库间现金出入库类型
	 */
	public static class BetweenCashType {
		/** 现金库 */
		public static final String Store = "cash";
		/** 清分室 */
		public static final String Clear = "clear";
	}
	
	/**
	 * 扫描表示
	 * 
	 * @author chengshu
	 */
	public static class ScanFlag {
		/** 未扫描 */
		public static final String NoScan = "0";
		/** 已扫描 */
		public static final String Scan = "1";
		/** 补录 */
		public static final String Additional = "2";
		/** 强制补录 */
		public static final String UnknownAdditional = "3";
	}
	
	/**
	 * 接收场所
	 * 
	 * @author chengshu
	 */
	public static class AcceptPlace {
		/** 押运人员接收 */
		public static final String Escort = "0";
		/** 现金库接收 */
		public static final String Store = "1";
		/** 清分室接收 */
		public static final String Clear = "1";		
	}
	
	/**
	 * 交接场所
	 * 
	 * @author chengshu
	 */
	public static class HandoverPlace {
		/** 现金库 */
		public static final String Store = "0";
		/** 清分室 */
		public static final String Clear = "1";
	}
    
    /**
	 * 现金库(出库，接收)，清分室种别(出库，接收)
	 * 
	 * @author wangbaozhong
	 */
    public static class PageType {
    	/**
		 * 库房接收登记(库间)
		 */
        public static final String CashInStore  = "cashInStore";
        /**
		 * 库房出库登记(库间)
		 */
        public static final String CashOutStore  = "cashOutStore";
        /**
		 * 整点室接收登记(库间)
		 */
        public static final String CashInClassficationRoom  = "cashInClassficationRoom";
        
        /**
		 * 整点室出库登记(库间)
		 */
        public static final String CashOutClassficationRoom  = "cashOutClassficationRoom";
        
        /**
		 * 库房登记详细(库间)
		 */
        public static final String CashShowDetail  = "cashShowDetail";
        
        /**
		 * 网点编辑（库外）
		 */
        public static final String PointEdit = "pointEdit";
        /**
		 * 网点添加（库外）
		 */
        public static final String PointAdd = "pointAdd";
        /**
		 * 网点查看（库外）
		 */
        public static final String PointView = "pointView";
        /**
		 * 网店添加（临时线路）
		 */
        public static final String PointTempAdd = "pointTempAdd";
        /**
		 * 网店编辑（临时线路）
		 */
        public static final String PointTempEdit = "pointTempEdit";
        /**
		 * 库房编辑（临时线路）
		 */
        public static final String TempStoreEdit = "tempStoreEdit";
        /**
		 * 库房编辑（库外）
		 */
        public static final String StoreEdit = "storeEdit";
        /**
		 * 库房查看（库外）
		 */
        public static final String StoreView = "storeView";
        /**
		 * 库房管理员查看（库外）
		 */
        public static final String StoreMasterView = "storeMasterView";
        
        /**
		 * 库房同业追加
		 */
        public static final String StoreSameTradeAdd = "storeSameTradeAdd";
        /**
		 * 库房同业修改
		 */
        public static final String StoreSameTradeEdit = "storeSameTradeEdit";
        /**
		 * 库房同业查看
		 */
        public static final String StoreSameTradeView = "storeSameTradeView";
        
        /**
		 * 人行下拨审批列表
		 */
        public static final String StoreApprovalList = "storeApprovalList";
        
        /**
		 * 人行下拨审批编辑
		 */
        public static final String StoreApprovalEdit = "storeApprovalEdit";
        /**
		 * 人行下拨审批查看
		 */
        public static final String StoreApprovalView = "storeApprovalView";
        /**
		 * 人行上缴审批列表
		 */
        public static final String StoreHandinApprovalList = "storeHandinApprovalList";
        /**
		 * 人行上缴审批查看
		 */
        public static final String StoreHandinApprovalView = "storeHandinApprovalView";
        /**
		 * 人行上缴审批编辑
		 */
        public static final String StoreHandinApprovalEdit = "storeHandinApprovalEdit";
        
        /**
		 * 人行配款列表
		 */
        public static final String StoreQuotaList = "storeQuotaList";
        
        /**
		 * 人行上缴查看
		 */
        public static final String StoreHandinView = "storeHandinView";
        
        
        /**
		 * 人行配款查看
		 */
        public static final String StoreQuotaView = "storeQuotaView";
        
        /**
		 * 人行复点查看
		 */
        public static final String StoreRecountingView = "storeRecountingView";
        
        /**
		 * 人行复点列表
		 */
        public static final String StoreRecountingList = "storeRecountingList";
        
        /**
		 * 商行申请列表
		 */
        public static final String BussnessApplicationList = "bussnessApplicationList";
        
        /**
		 * 商行申请查看
		 */
        public static final String BussnessApplicationView = "bussnessApplicationView";
        /**
		 * 商行申请编缉
		 */
        public static final String BussnessApplicationEdit = "bussnessApplicationEdit";
        
        /**
		 * 人行审批登记编辑
		 */
        public static final String PbocAgentOrderEdit = "PbocAgentOrderEdit";
        /**
		 * 销毁出库申请列表
		 */
        public static final String DestroyApprovalList = "destroyApprovalList";
        /**
		 * 销毁出库申请查看
		 */
        public static final String DestroyApprovalView = "destroyApprovalView";
        
        /**
		 * 调拨入库查看
		 */
        public static final String AllocatedInStoreView = "allocatedInStoreView";
        /**
		 * 调拨出库查看
		 */
        public static final String AllocatedOutStoreView = "allocatedOutStoreView";
        
    }
    
    /**
	 * 交接表
	 * 
	 * @author Lemon
	 */
    public static class Handover {
        /**
		 * 交接状态
		 * 
		 * @author Lemon
		 */
        public static class TaskFlag {
            /**
			 * 未交接
			 */
            public static final String Unfinished  = "0";
            /**
			 * 已交接
			 */
            public static final String Finished   = "1";
        }
    }
    
    /**
	 * 登记种别
	 * 
	 * @author suiwei
	 */
    public static class RegistType {
    	/**
		 * 库房登记
		 */
    	public static final String RegistStore = "10";
    	/**
		 * 网点登记
		 */
    	public static final String RegistPoint = "11";
    	/**
		 * 整点室登记
		 */
    	public static final String RegistClassficationRoom = "12";
    }
    
    /**
	 * 检查结果
	 * 
	 * @author wangbaozhong
	 *
	 */
    public static class CheckResult {
    	/**
		 * 一致
		 */
    	public static final String Same = "0";
    	
    	/**
		 * 不一致
		 */
    	public static final String Different = "1";
    }
    
    /**
	 * 签收方式
	 * 
	 * @author wangbaozhong
	 *
	 */
    public static class HandoverType {
    	
    	/**
		 * 系统登录
		 */
    	public static final String SystemLogin = "0";
    	
    	/**
		 * 指纹
		 */
    	public static final String Finger = "1";
    	
    	/**
		 * 身份证
		 */
    	public static final String IdentityCard = "2";
    }

    /**
	 * 签名图片信息
	 * 
	 * @author wangbaozhong
	 *
	 */
    public static class SignPicInfo {
		/** 签名图片序号1 **/
    	public static final String SIGN_NUM_1 = "1";
		/** 签名图片序号2 **/
    	public static final String SIGN_NUM_2 = "2";
		/** 签名标记：有 **/
    	public static final String SIGN_YES = "1";
		/** 签名人员类型：网点 **/
    	public static final String SIGN_TYPE_POINT = "point";
		/** 签名人员类型：解款员 **/
    	public static final String SIGN_TYPE_ESCORT = "escort";
    }
    
    /**
	 * 人员类型
	 * 
	 * @author chengshu
	 */
    public static class UserType {
		/** 移交人 **/
        public static final String handover = "1";
		/** 接收人 **/
        public static final String accept = "2";
		/** 押运人 **/
        public static final String manager = "3";
		/** 授权人 **/
        public static final String escort = "4";
    }

	/**
	 * 发行基金出入库类型
	 */
    public static class inoutType {
		/** 出库 **/
        public static final String STOCK_OUT  = "0";
		/** 入库 **/
        public static final String STOCK_IN  = "1";
	}
    
	/** 是否现实 驳回数据 **/
    public static class ShowRejectData {
		/** 显示 **/
        public static final String SHOW_OUT  = "0";
		/** 不显示 **/
        public static final String SHOW_NONE  = "1";
    }
    
	/** 退库标识 **/
    public static class CancellingStockFlag {
		// 无退库
    	public static final String CANCEL_STOCKS_NO = "0";
		// 有退库
    	public static final String CANCEL_STOCKS_YES = "1";
    }
    
    /**
	 * 调拨出库后入库标记
	 * 
	 * @author WangBaozhong
	 *
	 */
    public static class AllocateInAfterOutFlag {
		/** 1：调拨出库已入库 **/
    	public static final String ALREADY_IN = "1";
		/** 0:调拨出库未入库 **/
    	public static final String NOT_IN = "0";
    }
    
    /**
	 * 网点预约确认标识
	 * 
	 * @author qipeihong
	 */
	public static class  confirmFlag{
		/* 预约 */
		public static final String Appointment = "0";
		/* 确认 */
		public static final String Confirm = "1";
	}
	
	/**
	 * 库间调拨状态
	 * 
	 * @author SongYuanYang
	 */
    public static class BetweenStatus {
		/** 待接收 **/
        public static final String TO_ACCEPT_STATUS = "11";
		/** 清分中 **/
        public static final String TO_IN_SORTING = "12";
		/** 待入库 **/
        public static final String TO_IN_STORE_STATUS = "13";
		/** 完成 **/
        public static final String FINISH_STATUS = "99";
    }
    
	/**
	 * 人员操作类型
	 * 
	 * @author liuyaowen
	 */
	public static class OperationType {
		/** 移交 **/
		public static final String TURN_OVER = "0";
		/** 接收 **/
		public static final String ACCEPT = "1";
		/** 授权 **/
		public static final String AUTHORIZATION = "2";
		/** 扫描门授权 **/
		public static final String SCANNING_DOOR_AUTHORIZATION = "3";
	}
    
    /**
	 * 库间调拨确认标识
	 * 
	 * @author SongYuanYang
	 */
    public static class BetweenConfirmFlag {
		/** 未确认 **/
        public static final String UNCONFIRMED = "0";
		/** 已确认 **/
        public static final String CONFIRMED = "1";
    }
    

    /**
	 * 类别
	 * 
	 * @author SongYuanYang
	 *
	 */
	public static class MoneyType {
		/** ATM币 */
		public static final String ATM_MONEY = "01";
		/** 流通币 */
		public static final String CIRCULATION_MONEY = "02";
		/** 残损币 **/
		public static final String DEMANGED_MONEY = "03";
		/** 假币 **/
		public static final String COUNTERFEIT_MONEY = "04";
		/** 待整点币 */
		public static final String COUNTWAIT_MONEY = "05";
		/** 发行基金 */
        public static final String ISSUE_FUND = "06";
	}
	
	/**
	 * 类别
	 * 
	 * @author qipeihong
	 *
	 */
	public static class TaskType {
		/** 常规线路 */
		public static final String ROUTINET_TASK= "01";
		/** 临时线路 */
		public static final String TEMPORARY_TASK = "02";
	}

	/**
	 * 
	 * @author xp 工作流返回页面
	 */
	public static class backType {
		/** 现金上缴 */
		public static final String CASH_HAND_IN = "toCashHandinList";
		/** 箱袋上缴 */
		public static final String HAND_IN = "toHandInList";
		/** 现金下拨 */
		public static final String CASH_HAND_OUT = "toCashOrderList";
		/** 箱袋下拨 */
		public static final String HAND_OUT = "toHandOutList";
	}

	/**
	 * 
	 * @author xp 首页显示现金业务金额常量
	 *
	 */
	public static class AllocateType {
		/** 常规业务 */
		public static final String NORMAL_BUSINESS = "1";
		/** 临时业务 */
		public static final String TEMP_BUSINESS = "2";
	}

	/**
	 * 调拨业务授权原因
	 * 
	 * @author XP
	 *
	 */
	public static class managerReason{
		/**人脸识别不好用*/
		public static final String FACE_NO_USE = "1";
		/** 身份证不好用 */
		public static final String IDCARD_NO_USE = "2";
		/** 网点扫描箱子数量有误 */
		public static final String BOX_NUMBER_WRONG = "3";
	}

	/**
	 * ATM加钞业务状态
	 * 
	 * @author QPH
	 *
	 */
	public static class AtmBusinessStatus {
		/** 登记 */
		public static final String Register = "10";
		/** 已扫描 */
		public static final String HandoverTodo = "11";
		/** 已交接 */
		public static final String Onload = "12";
	}
	
	/**
	 * 自动化库房开关
	 * 
	 * @author SongYuanYang
	 *
	 */
	public static class AutomaticStoreSwitch {
		/** 开启  */
		public static final String OPEN = "0";
		/** 关闭  */
		public static final String CLOSE = "1";
	}
}
