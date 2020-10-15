package com.coffer.core.modules.sys.service;

import java.util.Date;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.coffer.businesses.common.Constant;
import com.coffer.core.common.service.BusinessException;
import com.coffer.core.common.service.CrudService;
import com.coffer.core.common.utils.DateUtils;
import com.coffer.core.modules.sys.dao.AutoVaultCommunicationDao;
import com.coffer.core.modules.sys.entity.AutoVaultCommunication;

/**
 * 通信Service
 * 
 * @author SongYuanYang
 * @version 2018年5月4日
 */
@Service
@Transactional(readOnly = true)
public class AutoVaultCommunicationService extends CrudService<AutoVaultCommunicationDao, AutoVaultCommunication> {

	/**
	 * 更新通信表状态
	 * 
	 * @author WangBaozhong
	 * @version 2018年5月23日
	 * 
	 * @param autoVaultCommunication
	 *            通信信息
	 * @return void
	 */
	@Transactional(readOnly = false)
	public void updateStatus(AutoVaultCommunication autoVaultCommunication) {
		dao.updateStatus(autoVaultCommunication);
	}

	/**
	 * @author SongYuanYang
	 * @version 2018年5月23日
	 * 
	 * @Description 插入信息
	 * @param autoVaultCommunication
	 * 				
	 */
	@Transactional(readOnly = false)
	public void saveMessage(AutoVaultCommunication autoVaultCommunication) {
		dao.insert(autoVaultCommunication);
	}
	
	/**
	 * 切换状态  失败<>终止
	 * 
	 * @author SongYuanYang
	 * @version 2018年5月23日
	 * 
	 * @param autoVaultCommunication
	 */
	@Transactional(readOnly = false)
	public void change(AutoVaultCommunication autoVaultCommunication) {
		// 数据一致性判断
		checkVersion(autoVaultCommunication);
		// 失败时改为终止，终止时改为失败
		if (Constant.CommunicationStatus.FAIL.equals(autoVaultCommunication.getStatus())) {
			autoVaultCommunication.setStatus(Constant.CommunicationStatus.TERMINATION);
		} else if ((Constant.CommunicationStatus.TERMINATION.equals(autoVaultCommunication.getStatus()))) {
			autoVaultCommunication.setStatus(Constant.CommunicationStatus.FAIL);
		}
		// 设置更新信息
		autoVaultCommunication.setUpdateBy(autoVaultCommunication.getCurrentUser());
		autoVaultCommunication.setUpdateDate(new Date());
		dao.updateStatus(autoVaultCommunication);
	}
	
	/**
	 * 通信表的数据一致性验证
	 * 
	 * @author SongYuanYang
	 * @version 2018年5月23日
	 * 
	 * @param autoVaultCommunication
	 */
	public void checkVersion(AutoVaultCommunication autoVaultCommunication) {
		// 取得原数据
		AutoVaultCommunication oldData = super.get(autoVaultCommunication.getId());
		if (oldData != null) {
			String oldUpdateDate = DateUtils.formatDate(oldData.getUpdateDate(),
					Constant.Dates.FORMATE_YYYYMMDDHHMMSSSSSSSS);
			if (!oldUpdateDate.equals(autoVaultCommunication.getStrUpdateDate())) {
				throw new BusinessException("message.E0007");
			}
		}else {
			//[操作失败]该数据已被删除，请重新操作。
			throw new BusinessException("message.E0008");
		}
	}

}
