package com.coffer.businesses.modules.doorOrder.app.v01.service;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.coffer.businesses.modules.doorOrder.v01.dao.BackAccountsMainDao;
import com.coffer.businesses.modules.doorOrder.v01.entity.BackAccountsMain;
import com.coffer.businesses.modules.doorOrder.v01.service.BackAccountsMainService;
import com.coffer.core.common.service.CrudService;
import com.coffer.core.modules.sys.entity.User;
import com.coffer.external.ExternalConstant;
import com.coffer.external.Parameter;
import com.google.common.collect.Maps;

/**
 * 回款管理Service
 * 
 * @author XL
 * @version 2019-06-26
 */
@Service
@Transactional(readOnly = true)
public class BackAccountsMainAppService extends CrudService<BackAccountsMainDao, BackAccountsMain> {

	@Autowired
	private BackAccountsMainDao backAccountsMainDao;

	@Autowired
	private BackAccountsMainService backAccountsMainService;

	public List<BackAccountsMain> findPage(BackAccountsMain backAccountsMain, User userInfo) {
		backAccountsMain.setOfficeId(userInfo.getOffice().getId());
		backAccountsMain.setCreateBy(userInfo);
		backAccountsMain.getSqlMap().put("dsf", "OR o6.parent_ids LIKE '%" + userInfo.getOffice().getId() + "%'");
		return backAccountsMainDao.getBackAccountsList(backAccountsMain);
	}

	/**
	 * 清分回款凭证图片上传
	 *
	 * @author XL
	 * @version 2019年8月29日
	 * @param id
	 * @param photoImg
	 * @return
	 */
	@Transactional(readOnly = false)
	public Map<String, Object> saveBackAccountsPic(String id, byte[] photoImg, User user) {
		Map<String, Object> jsonData = Maps.newHashMap();
		BackAccountsMain backAccountsMain = backAccountsMainService.get(id);
		if (backAccountsMain == null) {
			jsonData.put(Parameter.ERROR_NO_KEY, ExternalConstant.HardwareInterface.ERROR_NO_E01);
			jsonData.put(Parameter.ERROR_MSG_KEY, "回款信息不存在！");
			jsonData.put(Parameter.RESULT_FLAG_KEY, ExternalConstant.HardwareInterface.RESULT_FLAG_FAILURE);
		} else {
			// 凭条照片
			 backAccountsMain.setPhoto(photoImg);
			// 上传用户
			backAccountsMain.setUpdateBy(user);
			// 上传时间
			backAccountsMain.setUpdateDate(new Date());
			int i = backAccountsMainDao.update(backAccountsMain);
			if (i > 0) {
				jsonData.put(Parameter.RESULT_FLAG_KEY, ExternalConstant.HardwareInterface.RESULT_FLAG_SUCCESS);
			} else {
				jsonData.put(Parameter.ERROR_NO_KEY, ExternalConstant.HardwareInterface.ERROR_NO_E02);
				jsonData.put(Parameter.ERROR_MSG_KEY, "图片保存失败,请重试！");
				jsonData.put(Parameter.RESULT_FLAG_KEY, ExternalConstant.HardwareInterface.RESULT_FLAG_FAILURE);
			}
		}
		return jsonData;
	}

}