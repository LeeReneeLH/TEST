package com.coffer.core.modules.sys.service;

import java.util.Date;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.coffer.core.common.persistence.Page;
import com.coffer.core.common.service.CrudService;
import com.coffer.core.common.utils.DateUtils;
import com.coffer.core.modules.sys.dao.LogDao;
import com.coffer.core.modules.sys.entity.Log;

/**
 * 日志Service
 * @author Clark
 * @version 2015-05-20
 */
@Service
@Transactional(readOnly = true)
public class LogService extends CrudService<LogDao, Log> {

	public Page<Log> findPage(Page<Log> page, Log log) {
		
		// 设置默认时间范围，默认当前月
		if (log.getBeginDate() == null){
			log.setBeginDate(DateUtils.setDays(DateUtils.parseDate(DateUtils.getDate()), 1));
		}
		if (log.getEndDate() == null){
			// 设定最大日期
			log.setEndDate(DateUtils.getDateEnd(new Date()));
		}else {
			Date dateEnd = DateUtils.getDateEnd(log.getEndDate());
			log.setEndDate(dateEnd);
		}
		
		return super.findPage(page, log);
	}
}
