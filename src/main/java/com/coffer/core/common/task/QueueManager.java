package com.coffer.core.common.task;

import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.coffer.businesses.common.Constant;
import com.coffer.businesses.modules.allocation.AllocationConstant;
import com.coffer.core.common.service.BusinessException;
import com.coffer.core.common.utils.StringUtils;
import com.coffer.core.modules.sys.SysCommonUtils;
import com.coffer.core.modules.sys.entity.AutoVaultCommunication;
import com.coffer.core.modules.sys.entity.MessageScheduleQueue;
import com.coffer.core.modules.sys.utils.DbConfigUtils;
import com.coffer.external.Parameter;
import com.coffer.external.hessian.IHardwareService;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import net.sf.json.JSONObject;

/**
 * 定时队列
 * 
 * @author yanbingxu
 * @version 2017-11-14
 */
@Service
@Lazy(false)
public class QueueManager {

	@Autowired
	private IHardwareService autoVaultService;
	
	/**
	 * 日志对象
	 */
	protected Logger logger = LoggerFactory.getLogger(getClass());
	
	/**
	 * 阻塞队列初始化
	 */
	private static BlockingQueue<MessageScheduleQueue> message = null;
	private static BlockingQueue<Map<String, String>> gps = null;
	private static BlockingQueue<Map<String, String>> communication = null;

	private static String IN_JSON = "inJson";
	private static String ID = "id";
	
	static {
		message = new LinkedBlockingQueue<MessageScheduleQueue>();
		gps = new LinkedBlockingQueue<Map<String, String>>();
		communication = new LinkedBlockingQueue<Map<String, String>>();
	}

	/**
	 * 
	 * Title: put
	 * <p>Description: 向队列中添加消息</p>
	 * @author:     yanbingxu
	 * @param params 
	 * void    返回类型
	 */
	public void put(MessageScheduleQueue params) {
		try {
			message.put(params);
		} catch (InterruptedException e) {
			logger.debug("**************** 消息队列处于等待状态 ************************");
			e.printStackTrace();
		}
	}

	/**
	 * 
	 * Title: send
	 * <p>Description: 从队列中取消息并发送</p>
	 * @author:     yanbingxu 
	 * void    返回类型
	 */
	@Scheduled(fixedDelay = 1000)
	public void send() {
		MessageScheduleQueue parameter = message.poll();

		if (parameter != null) {
			logger.debug("**************** 消息对列----发送开始 ************************");
			SysCommonUtils.pushMessage(parameter);
			logger.debug("**************** 消息对列----发送结束 ************************");
		}
	}
	
	/**
	 * 
	 * Title: gpsPut
	 * <p>Description: 向队列中记录车辆位置</p>
	 * @author:     yanbingxu
	 * @param location 
	 * void    返回类型
	 */
	public void gpsPut(Map<String, String> location) {
		try {
			gps.put(location);
		} catch (InterruptedException e) {
			logger.debug("**************** gps队列处于等待状态 ************************");
			e.printStackTrace();
		}
	}
	
	/**
	 * 
	 * Title: gpsSend
	 * <p>Description: 从队列中取车辆位置并发送至前台</p>
	 * @author:     yanbingxu 
	 * void    返回类型
	 */
	@Scheduled(fixedDelay = 2000)
	public void gpsSend() {
		List<Map<String, String>> locationList = Lists.newArrayList();
		gps.drainTo(locationList);
		for (Map<String, String> location : locationList) {
			if (location != null) {
				SysCommonUtils.gpsLocationSend(location);
			}
		}
	}
	
	/**
	 * 
	 * Title: communicationPut
	 * <p>Description: 向队列中记录通信信息</p>
	 * @author:     yanbingxu
	 * @param communicationJson
	 * @param id 
	 * void    返回类型
	 */
	public void communicationPut(String inJson, String id) {
		if (AllocationConstant.AutomaticStoreSwitch.OPEN.equals(DbConfigUtils.getDbConfig("auto.vault.switch"))) {
			try {
				Map<String, String> map = Maps.newConcurrentMap();
				map.put(IN_JSON, inJson);
				map.put(ID, id);
				communication.put(map);
			} catch (InterruptedException e) {
				logger.debug("**************** communication队列处于等待状态 ************************");
				e.printStackTrace();
				throw new BusinessException();
			}
		}
	}
	
	/**
	 * 
	 * Title: communicationPutSchedule
	 * <p>Description: 定时向队列中记录通信信息</p>
	 * @author:     yanbingxu 
	 * void    返回类型
	 */
	@Scheduled(fixedDelay = 10000)
	public void communicationPutSchedule() {
		if (AllocationConstant.AutomaticStoreSwitch.OPEN.equals(DbConfigUtils.getDbConfig("auto.vault.switch"))) {
			List<AutoVaultCommunication> list = SysCommonUtils.findFailedCommunicationList();
			for (AutoVaultCommunication communication : list) {
				communication.setStatus(Constant.CommunicationStatus.TO_BE_SENT);
				SysCommonUtils.updateCommunication(communication);
				communicationPut(communication.getInJson(), communication.getId());
			}
		}
	}
	
	/**
	 * 
	 * Title: communicationSend
	 * <p>Description: 从队列中取通信信息并尝试通信</p>
	 * @author:     yanbingxu 
	 * void    返回类型
	 */
	@Scheduled(fixedDelay = 1000)
	public void communicationSend() {
		Map<String, String> map = communication.poll();
		if (map != null) {
			AutoVaultCommunication autoVaultCommunication = new AutoVaultCommunication();
			String outJson = "";
			try {
				outJson = autoVaultService.service(map.get(IN_JSON));
			} catch (Exception ex) {
				autoVaultCommunication.setException(ex.toString());
			}

			autoVaultCommunication.setId(map.get(ID));
			if (StringUtils.isNotBlank(outJson)) {
				JSONObject json = JSONObject.fromObject(outJson);
				autoVaultCommunication.setStatus(json.get(Parameter.RESULT_FLAG_KEY).toString());
				autoVaultCommunication.setOutJson(outJson);
			} else {
				autoVaultCommunication.setStatus(Constant.CommunicationStatus.FAIL);
			}

			SysCommonUtils.updateCommunication(autoVaultCommunication);
		}
	}
	
}
