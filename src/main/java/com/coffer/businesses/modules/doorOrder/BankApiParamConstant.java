package com.coffer.businesses.modules.doorOrder;


import com.coffer.core.common.config.Global;

public class BankApiParamConstant {

    public static final String API_URL = Global.getConfig("bank_service_url");
    /**
     * 转账信息查询接口
     */
    public static final String INQUIRY_OF_TRANSFER_INFORMATION = API_URL + Global.getConfig("inquiryOfTransferInformation");
    /**
     * 账户明细查询接口
     */
    public static final String ACCOUNT_DETAIL_SINQUIRY = API_URL + Global.getConfig("accountDetailsInquiry");
    /**
     * 账户余额查询接口
     */
    public static final String ACCOUNT_BALANCE_INQUIRY = API_URL + Global.getConfig("accountBalanceInquiry");
    /**
     * 单笔支付接口
     */
    public static final String SINGLE_PAYMENT = API_URL + Global.getConfig("singlePayment");

    /**
     * 单笔支付参数
     */
    public class SinglePaymentParams {
        /** 请求 */
        // 付款人账号
        public static final String PAYMENT_ACCOUNT_KEY = "paymentAccount";
        // 付款人名称
        public static final String NAME_OF_PAYERS_ACCOUNT_KEY = "nameOfpayersAccount";
        // 收款人账号
        public static final String PAYEE_ACCOUNT_KEY = "payeeAccount";
        // 收款人名称
        public static final String NAME_OF_PAYEE_KEY = "nameOfPayee";
        // 付款金额
        public static final String PAY_AMOUNT_KEY = "payAmount";
        // 本行、他行标志
        public static final String BANK_FLAG_KEY = "bankFlag";
        // 本地、异地标志
        public static final String PLACE_FLAG_KEY = "placeFlag";

        /** 响应 */
        // 交易状态
        public static final String TRANSACTION_STATUS_KEY = "transactionStatus";
        // 受理编号
        public static final String TO_ACCEPT_THE_NUMBER_KEY = "acceptNo";
    }

    /**
     * 账户余额查询参数
     */
    public class AccountBalanceInquiryParams {
        /** 请求 */
        // 待查询账号
        public static final String ACCOUNT_KEY = "account";
        /** 响应 */
        // 客户号
        public static final String CUSTOMER_NUMBER_KEY = "customerNumber";
        // 账号余额
        public static final String ACCOUNT_BALANCE_KEY = "accountBalance";
        // 保留余额
        public static final String KEEP_THEBALANCE_KEY = "keepTheBalance";
        // 冻结余额
        public static final String FREEZE_THE_BALANCE_KEY = "freezeTheBalance";
        // 控制余额
        public static final String CONTROL_THE_BALANCE_KEY = "controlTheBalance";
        // 可用金额
        public static final String AVAILABLE_BALANCE_KEY = "availableBalance";
        // 透支余额
        public static final String OVERDRAFT_BALANCE_KEY = "overdraftBalance";
    }

    /**
     * 转账信息查询参数
     */
    public class InquiryOfTransferInformationParams {
        /** 请求 */
        // 待查账号
        public static final String ACCOUNT_KEY = "account";
        // 开始日期
        public static final String BEGIN_DATE_KEY = "beginDate";
        // 结束日期
        public static final String END_DATE_KEY = "endDate";
        // 受理编号
        public static final String TO_ACCEPT_THE_NUMBER_KEY = "toAcceptTheNumber";
        // 查询笔数
        public static final String NUMBER_OF_QUERIES_KEY = "numberOfQueries";
        // 查询起始笔数
        public static final String START_NUMBER_OF_QUERIES_KEY = "startNumberOfQueries";
        /** 响应 */
        // 交易流水号
        public static final String TRADE_SERIAL_NUMBER_KEY = "tradeSerialNumber";
    }

    public static class TransactionStatusValues {
        //A-业务登记
        public static final String REGISTER = "A";
        //0-待补录
        public static final String WAITING_RECORD = "0";
        //1-待记帐
        public static final String WAITING_TALLY = "1";
        //2-待复核
        public static final String WAITING_REVIEW = "2";
        //3-待授权
        public static final String WAITING_AUTHORIZE = "3";
        //4-完成
        public static final String COMPLETE = "4";
        //8-拒绝
        public static final String REFUSE = "8";
        //9-撤销
        public static final String REVOCATION = "9";
    }
}
