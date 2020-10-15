package com.coffer.businesses.modules.doorOrder.v01.service;

import com.coffer.businesses.modules.doorOrder.BankApiParamConstant;
import com.coffer.businesses.modules.doorOrder.BankApiParamConstant.*;
import com.coffer.core.common.service.BaseService;
import com.coffer.core.common.service.BusinessException;
import com.coffer.core.common.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * 银企直联Service
 *
 * @author yinkai
 * @date 2019-07-18
 */
@Service
public class BankService extends BaseService {

    @Autowired
    RestTemplate restTemplate;

    /**
     * 单笔支付，本行转账时不需要判断同城异地，标识置0即可
     *
     * @param paymentAccount      付款人账号
     * @param nameOfpayersAccount 付款账号名称
     * @param payeeAccount        收款人账号
     * @param nameOfPayee         收款人名称
     * @param payAmount           付款金额
     * @param bankFlag            本行转账：0 他行转账：1
     * @param placeFlag           同城转账：0 异地转账：1
     * @return transactionStatus:交易状态， toAcceptTheNumber:受理编号，tradeSerialNumber:交易流水号
     * 交易状态:
     * A-业务登记，0-待补录，1-待记帐，2-待复核，3-待授权，4-完成，8-拒绝，9-撤销
     * 实际只会返回有4-完成和8-拒绝两种状态
     * @author yinkai
     */
    public Map<String, Object> singlePayment(String paymentAccount,
                                             String nameOfpayersAccount,
                                             String payeeAccount,
                                             String nameOfPayee,
                                             String payAmount,
                                             String bankFlag,
                                             String placeFlag) {
        // 单笔支付接口参数
        Map<String, Object> singlePaymentParams = new HashMap<>();
        // 支付必填项：付款账号，付款人，收款账号，收款人，收款金额
        singlePaymentParams.put(SinglePaymentParams.PAYMENT_ACCOUNT_KEY, paymentAccount);
        singlePaymentParams.put(SinglePaymentParams.NAME_OF_PAYERS_ACCOUNT_KEY, nameOfpayersAccount);
        singlePaymentParams.put(SinglePaymentParams.PAYEE_ACCOUNT_KEY, payeeAccount);
        singlePaymentParams.put(SinglePaymentParams.NAME_OF_PAYEE_KEY, nameOfPayee);
        singlePaymentParams.put(SinglePaymentParams.PAY_AMOUNT_KEY, payAmount);
        // 他行转账，异地转账
        singlePaymentParams.put(SinglePaymentParams.BANK_FLAG_KEY, bankFlag);
        singlePaymentParams.put(SinglePaymentParams.PLACE_FLAG_KEY, placeFlag);
        // 发送请求
        Map<String, Object> resultMap = sendGetRequest(singlePaymentParams, BankApiParamConstant.SINGLE_PAYMENT);
        // 失败返回时抛异常
        if (resultMap.get("errorCode") != null) {
            throw new BusinessException("message.E7211", "", new String[]{resultMap.get("errorMessage").toString()});
        }
        // 单笔查询接口参数
        Map<String, Object> inquiryParams = new HashMap<>();
        String date = DateUtils.formatDate(new Date());
        date = date.replaceAll("-", "");
        // 账号，开始时间，结束时间，受理编号，查询总笔数，查询起始笔数
        inquiryParams.put(InquiryOfTransferInformationParams.BEGIN_DATE_KEY, date);
        inquiryParams.put(InquiryOfTransferInformationParams.END_DATE_KEY, date);
        inquiryParams.put(InquiryOfTransferInformationParams.NUMBER_OF_QUERIES_KEY, "1");
        inquiryParams.put(InquiryOfTransferInformationParams.START_NUMBER_OF_QUERIES_KEY, "1");
        inquiryParams.put(InquiryOfTransferInformationParams.TO_ACCEPT_THE_NUMBER_KEY, resultMap.get(SinglePaymentParams.TO_ACCEPT_THE_NUMBER_KEY));
        inquiryParams.put(InquiryOfTransferInformationParams.ACCOUNT_KEY, paymentAccount);
        // 发送请求
        Map<String, Object> queryResultMap = sendGetRequest(inquiryParams, BankApiParamConstant.INQUIRY_OF_TRANSFER_INFORMATION);
        // 失败返回时抛异常
        if (queryResultMap.get("errorCode") != null) {
            throw new BusinessException("message.E7212", "", new String[]{queryResultMap.get("errorMessage").toString()});
        }
        // 从查询结果中提取银行流水号
        Map<String, Object> detail = (Map<String, Object>) queryResultMap.get("detail");
        List<Map<String, Object>> details = (List<Map<String, Object>>) detail.get("details");
        if (details != null && details.size() > 0) {
            Object serialNumber = details.get(0).get(InquiryOfTransferInformationParams.TRADE_SERIAL_NUMBER_KEY);
            // 结果中放入银行交易流水
            resultMap.put(InquiryOfTransferInformationParams.TRADE_SERIAL_NUMBER_KEY, serialNumber);
        }
        return resultMap;
    }

    /**
     * 账户余额查询
     *
     * @param account 待查询账户
     * @return account：账号，
     * customerNumber：客户号，
     * accountBalance：账户余额，
     * keepTheBalance：保留余额，
     * freezeTheBalance：冻结余额
     * controlTheBalance：控制余额，
     * availableBalance：可用金额，
     * overdraftBalance：透支余额
     * @author yinkai
     */
    public Map<String, Object> accountBalanceInquiry(String account) {
        Map<String, Object> params = new HashMap<>();
        params.put(AccountBalanceInquiryParams.ACCOUNT_KEY, account);
        Map<String, Object> resultMap = sendGetRequest(params, BankApiParamConstant.ACCOUNT_BALANCE_INQUIRY);
        if (resultMap.get("errorCode") != null) {
            throw new BusinessException("message.E7212", "", new String[]{resultMap.get("errorMessage").toString()});
        }
        Map<String, Object> lists = (Map<String, Object>) resultMap.get("lists");
        List<Map<String, Object>> details = (List<Map<String, Object>>) lists.get("details");
        return details.get(0);

    }

    /**
     * 发送请求
     *
     * @param url    请求地址
     * @param params 请求参数
     * @return 响应信息Map
     * @author yinkai
     */
    public Map<String, Object> sendGetRequest(Map<String, Object> params, String url) {
        // 请求头
        HttpHeaders headers = new HttpHeaders();
        MediaType type = MediaType.parseMediaType("application/json;charset=UTF-8");
        headers.setContentType(type);
        // 发送请求
        ResponseEntity<String> responseEntity = restTemplate.getForEntity(url, String.class, params);
        String result = responseEntity.getBody();
        Map<String, Object> resultMap = gson.fromJson(result, Map.class);
        return resultMap;
    }

    /**
     * 交易是否可以进行
     *
     * @param transactionStatus 转账状态
     * @return true：可交易 false：不可交易
     * @author yinkai
     */
    private boolean transactionCheck(String transactionStatus) {
        // 只有成功或失败，处理两种回滚状态
        boolean refuse = BankApiParamConstant.TransactionStatusValues.REFUSE.equals(transactionStatus);
        boolean revocation = BankApiParamConstant.TransactionStatusValues.REVOCATION.equals(transactionStatus);
        if (refuse || revocation) {
            return false;
        }
        return true;
    }

}
