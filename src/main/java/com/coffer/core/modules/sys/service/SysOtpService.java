package com.coffer.core.modules.sys.service;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.coffer.core.common.persistence.Page;
import com.coffer.core.common.service.BusinessException;
import com.coffer.core.common.service.CrudService;
import com.coffer.core.common.utils.Collections3;
import com.coffer.core.modules.sys.DbConfigConstant;
import com.coffer.core.modules.sys.dao.SysOtpDao;
import com.coffer.core.modules.sys.entity.SysOtp;
import com.coffer.core.modules.sys.entity.User;
import com.coffer.core.modules.sys.utils.UserUtils;
import com.google.common.collect.Maps;

import ft.otp.verify.OTPVerify;

/**
 * otp动态口令管理Service
 * @author qph
 * @version 2018-07-02
 */
@Service
@Transactional(readOnly = true)
public class SysOtpService extends CrudService<SysOtpDao, SysOtp> {

	public SysOtp get(String id) {
		return super.get(id);
	}
	
	public List<SysOtp> findList(SysOtp sysOtp) {
		return super.findList(sysOtp);
	}
	
	public Page<SysOtp> findPage(Page<SysOtp> page, SysOtp sysOtp) {
		// 生成数据权限过滤条件（dsf为dataScopeFilter的简写，在xml中使用 ${sqlMap.dsf}调用权限SQL）
		sysOtp.getSqlMap().put("dsf", dataScopeFilter(UserUtils.getUser(), "o10", null));
		return super.findPage(page, sysOtp);
	}
	
	@Transactional(readOnly = false)
	public void save(SysOtp sysOtp) {
		super.save(sysOtp);
	}
	
	@Transactional(readOnly = false)
	public void delete(SysOtp sysOtp) {
		super.delete(sysOtp);
	}
	
	/**
	 * 
	 * @author qipeihong
	 * @version 2018-07-05
	 * 
	 *          根据tokenId查询动态令牌
	 * @param sysotp
	 * @return
	 */
	public SysOtp getSysOtpByTokenId(SysOtp sysotp) {
		return dao.getByTokenId(sysotp);
	}

	/**
	 * 
	 * @author qipeihong
	 * @version 2018-07-05
	 * 
	 *          验证OTP动态口令服务
	 * @param user
	 * @param command
	 * @param status
	 * @return
	 */
	@Transactional(readOnly = false)
	public boolean verify(User user, String command, SysOtp sysOtpParam) {
		Long nReturn;
		int currsucc = 0; // 成功值
		int currdft = 0; // 漂移值
		SysOtp resultOtp = new SysOtp();
		// 判断sysOtpParam
		if (sysOtpParam == null) {
			SysOtp sysOtp = new SysOtp();
			sysOtp.setUser(user);
			// 获取otp令牌
			List<SysOtp> sysOtpList = dao.findList(sysOtp);

			if (Collections3.isEmpty(sysOtpList)) {
				return false;
			}
			resultOtp = sysOtpList.get(0);
		} else {
			resultOtp = sysOtpParam;
		}
		// 获取密钥
		String authKey = resultOtp.getAuthKey();
		// 判断成功值
		if (resultOtp.getCurrsucc() == null) {
			currsucc = 0;
		} else {
			currsucc = resultOtp.getCurrsucc().intValue();
		}
		// 判断漂移值
		if (resultOtp.getCurrdft() == null) {
			currdft = 0;
		} else {
			currdft = resultOtp.getCurrdft().intValue();
		}
		Map hashMap = Maps.newHashMap();
		hashMap = OTPVerify.ET_CheckPwdz201(authKey, // 令牌密钥
					System.currentTimeMillis() / 1000, // 调用本接口计算机的当前时间
					0, // 给0
				DbConfigConstant.otpConstant.UPDATESECONDS, // 给60，因为每60秒变更新的动态口令
				currdft, // 漂移值，用于调整硬件与服务器的时间偏差，见手册说明
					20, // 认证窗口，见手册说明
				currsucc, // 成功值，用于调整硬件与服务器的时间偏差，见手册说明
				command); // 要认证的动态口令OTP

		nReturn = (Long) hashMap.get("returnCode");
		// 验证成功
		if (nReturn == OTPVerify.OTP_SUCCESS) {
			// 储存成功后返回的成功值和漂移值
			resultOtp.setCurrsucc((Long) hashMap.get("currentUTCEpoch"));
			resultOtp.setCurrdft((Long) hashMap.get("currentDrift"));
			dao.update(resultOtp);
			return true;
			// 验证失败
		} else {
			return false;
		}
	}

	/**
	 * 
	 * @author qipeihong
	 * @version 2018-07-05
	 * 
	 *          同步OTP动态口令服务
	 * @param sysOtp
	 * @return
	 */
	// ET z201同步接口调用
	@Transactional(readOnly = false)
	public void synchronous(SysOtp sysOtp) {
		// 密钥
		String authKey = sysOtp.getAuthKey();
		// 口令
		String command = sysOtp.getCommand();
		// 下一条口令
		String nextCommand = sysOtp.getNextcommand();
		Map hashMap = Maps.newHashMap();
		// 漂移值
		int currdft = 0;
		// 成功值
		int currsucc = 0;
		Long nReturn;
		// 判断成功值
		if (sysOtp.getCurrsucc() == null) {
			currsucc = 0;
		} else {
			currsucc = sysOtp.getCurrsucc().intValue();
		}
		// 判断漂移值
		if (sysOtp.getCurrdft() == null) {
			currdft = 0;
		} else {
			currdft = sysOtp.getCurrdft().intValue();
		}
		hashMap = OTPVerify.ET_Syncz201(authKey, System.currentTimeMillis() / 1000, 0,
				DbConfigConstant.otpConstant.UPDATESECONDS, currdft, 40, currsucc,
				command, nextCommand);
		// 获取返回值
		nReturn = (Long) hashMap.get("returnCode");
		// 成功或无需验证
		if (nReturn == OTPVerify.OTP_SUCCESS) {
			// 储存成功后返回的成功值和漂移值
			sysOtp.setCurrsucc((Long) hashMap.get("currentUTCEpoch"));
			sysOtp.setCurrdft((Long) hashMap.get("currentDrift"));
			dao.update(sysOtp);
		// 重复验证		
		} else if( nReturn == OTPVerify.OTP_ERR_REPLAY){
			throw new BusinessException("message.E9019", "", new String[] {});
		} else {
			throw new BusinessException("message.E9018", "", new String[] {});
		}
	}

}