package com.coffer.businesses.modules.store.v01.service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.coffer.businesses.common.Constant;
import com.coffer.businesses.common.utils.BusinessUtils;
import com.coffer.businesses.modules.store.v01.dao.StoBoxDetailDao;
import com.coffer.businesses.modules.store.v01.dao.StoBoxInfoDao;
import com.coffer.businesses.modules.store.v01.dao.StoBoxInfoHistoryDao;
import com.coffer.businesses.modules.store.v01.entity.StoBoxDetail;
import com.coffer.businesses.modules.store.v01.entity.StoBoxInfo;
import com.coffer.businesses.modules.store.v01.entity.StoEscortInfo;
import com.coffer.core.common.config.Global;
import com.coffer.core.common.persistence.Page;
import com.coffer.core.common.service.BusinessException;
import com.coffer.core.common.service.CrudService;
import com.coffer.core.common.utils.Collections3;
import com.coffer.core.common.utils.DateUtils;
import com.coffer.core.common.utils.IdGen;
import com.coffer.core.common.utils.StringUtils;
import com.coffer.core.modules.sys.dao.OfficeDao;
import com.coffer.core.modules.sys.entity.Office;
import com.coffer.core.modules.sys.entity.User;
import com.coffer.core.modules.sys.utils.UserUtils;
import com.coffer.external.ExternalConstant;
import com.coffer.external.Parameter;

/**
 * 箱袋信息管理Service
 * 
 * @author niguoyong
 * @version 2015-09-01
 */
@Service
@Transactional(readOnly = true)
public class StoBoxInfoService extends CrudService<StoBoxInfoDao, StoBoxInfo> {

	@Autowired
	private StoBoxInfoDao boxDao;
	@Autowired
	private OfficeDao officeDao;

	// 添加箱袋明细的数据持久层 修改人：xp 修改时间：2017-7-6 begin
	@Autowired
	private StoBoxDetailDao stoBoxDetailDao;

	// end
	// 添加箱袋明细变更状态 修改人：xp 修改时间：2017-7-6 begin
	@Autowired
	private StoBoxInfoHistoryDao HistoryDao;

	/**
	 * 
	 * @author niguoyong
	 * @version 2015-09-01
	 * 
	 * 
	 * @param boxNo
	 * @return
	 */
	public StoBoxInfo get(String boxNo) {
		return boxDao.get(boxNo);
	}

	/**
	 * 
	 * @author sg
	 * @version 2017-11-09
	 * 
	 * 
	 * @param boxNo
	 * @return
	 */
	public StoBoxInfo getATM(String boxNo) {
		return boxDao.getATM(boxNo);
	}

	/**
	 * 
	 * @author niguoyong
	 * @version 2015-09-01
	 * 
	 * 
	 * @param officeId
	 * @return
	 */
	public Office getOfficeById(String officeId) {
		return officeDao.get(officeId);
	}

	/**
	 * 查询单个箱袋信息对象(根据箱袋编号或者RFID编码)
	 * 
	 * @author niguoyong
	 * @version 2015-09-01
	 * 
	 * @Description
	 * @param info
	 *            箱袋编号orRFID
	 * @return
	 * @edit xp 将返回结果改为多条数据 2017-7-10
	 */

	public List<StoBoxInfo> getBindingBoxInfo(StoBoxInfo stoBoxInfo) {
		return boxDao.getBoxInfoByIdOrRfid(stoBoxInfo);
	}

	/**
	 * 查询箱袋信息对象(根据箱袋编号或者RFID编码)
	 * 
	 * @author xp
	 * @version 2017-07-24
	 * 
	 */
	@Transactional(readOnly = true)
	public List<StoBoxInfo> getBoxInfo(StoBoxInfo stoBoxInfo) {
		return boxDao.getBoxInfoByIdOrRfid(stoBoxInfo);
	}
	
	/**
	 * 查询在库尾箱信息(根据金库机构id)
	 * 
	 * @author SongYuanYang
	 * @version 2017-12-8
	 * 
	 */
	@Transactional(readOnly = true)
	public List<StoBoxInfo> findTailBoxList(String officeId) {
		return boxDao.findTailBoxList(officeId);
	}

	/**
	 * 根据条件查询款箱
	 * 
	 * @param page
	 * @param box
	 * @return
	 */
	public Page<StoBoxInfo> findPage(Page<StoBoxInfo> page, StoBoxInfo box) {

		// 生成数据权限过滤条件（dsf为dataScopeFilter的简写，在xml中使用 ${sqlMap.dsf}调用权限SQL）
		box.getSqlMap().put("dsf", dataScopeFilter(UserUtils.getUser(), "o5", null));
		// 查询数据列表
		return super.findPage(page, box);
	}

	/**
	 * 根据条件查询款箱
	 * 
	 * @param page
	 * @param box
	 * @return
	 */
	public List<StoBoxInfo> findList(StoBoxInfo box) {

		// 查询数据列表
		List<StoBoxInfo> list = boxDao.findList(box);

		return list;
	}

	/**
	 * 保存款箱信息
	 * 
	 * @author niguoyong
	 * @version 2015-09-01
	 * 
	 * @Description
	 * @param box
	 * @return
	 */
	@Transactional(readOnly = false)
	public int saveInfo(StoBoxInfo box, Office currentOffice) {
		// 箱袋类型
		String boxType = box.getBoxType();
		// 箱袋信息不为空
		if (box != null) {
			// 箱袋编号为空，创建新箱袋
			if (StringUtils.isEmpty(box.getId())) {
				// 创建箱袋数量
				Integer boxNum = box.getBoxNum();
				// 最大箱袋序列号
				int seqNo = 0;
				String strMaxBoxNo = boxDao.getCurrentMaxBoxNo(boxType, Global.getConfig("jdbc.type"));
				// 获取当前箱袋类型最大的箱袋编号
				if (!StringUtils.isEmpty(strMaxBoxNo)) {
					seqNo = BusinessUtils.castSeqNo2Int(strMaxBoxNo);
				}
				// 判断创建箱袋数量
				if (boxNum != null && boxNum > 0) {

					// 验证箱袋编号是否已经达到最大
					int valiadateNum = valiadateSeqNo(boxNum + seqNo);
					if (valiadateNum > 0) {
						// 计划创建箱袋数量比实际能创建的箱袋数量多多少
						return boxNum - valiadateNum;
					}
					// end

					for (int i = 0; i < boxNum; i++) {
						// 创建新箱袋信息
						StoBoxInfo newBox = new StoBoxInfo();
						String RFID = "";
						// 设置箱袋类型
						newBox.setBoxType(boxType);
						// 设置序列号
						newBox.setSeqNo(i + 1 + seqNo);
						// 设置箱袋状态
						newBox.setBoxStatus(Constant.BoxStatus.EMPTY);
						/* 修改钞箱类型及小车的添加方式 修改人:sg 修改日期:2017-11-10 begin */
						// 箱袋类型为钞箱or小车
						if (boxType.equals(Constant.BoxType.BOX_BACK) || boxType.equals(Constant.BoxType.BOX_GET)
								|| boxType.equals(Constant.BoxType.BOX_DEPOSITE)
								|| boxType.equals(Constant.BoxType.BOX_CYCLE)
								|| boxType.equals(Constant.BoxType.BOX_CAR)) {
							// 设置机构
							// newBox.setOffice(currentOffice);
							newBox.setOffice(box.getOffice());
							// 创建RFID，根据机构
							if (boxType.equals(Constant.BoxType.BOX_CAR)) {
								// 小车RFID生成
								RFID = this.generateRFIDs(boxType, i + 1 + seqNo, box.getOffice().getId(), null);

							} else {
								// 钞箱RFID生成
								// 钞箱类型
								newBox.setAtmBoxMod(box.getAtmBoxMod());
								RFID = this.generateRFIDs(boxType, i + 1 + seqNo, box.getOffice().getId(),
										box.getAtmBoxMod().getBoxTypeNo());
								// 设置钞箱金额
								newBox.setBoxAmount(box.getBoxAmount().multiply(new BigDecimal(10000)));
							}
							/* end */
						} else {
							// 设置机构为用户当前所选机构
							newBox.setOffice(box.getOffice());
							// 创建RFID，根据用户选择的机构
							RFID = BusinessUtils.generateRFID(boxType, i + 1 + seqNo, box.getOffice().getId(), null);
						}
						// 初始箱袋绑定状态为未绑定
						newBox.setIsNewRecord(true);
						newBox.setDelFlag(StoEscortInfo.DEL_FLAG_AUDIT);
						newBox.setRfid(RFID);
						newBox.setId(BusinessUtils.generateBoxNo(boxType, i + 1 + seqNo));
						newBox.setOutDate(box.getOutDate());
						super.save(newBox);
					}
				}
			}
			// 箱袋编号不为空，修改箱袋信息
			else {
				super.save(box);
			}
		}
		// 验证箱袋编号是否已经达到最大
		return -1;
	}

	/**
	 * 根据款箱编号删除款箱
	 * 
	 * @author niguoyong
	 * @version 2015-09-01
	 * 
	 * @Description
	 * @param boxNo
	 * @return
	 */
	@Transactional(readOnly = false)
	public int delete(String boxNo) {
		StoBoxInfo box = new StoBoxInfo();
		box.setId(boxNo);
		// 更改箱袋delFlag状态
		return boxDao.delete(box);
	}

	/**
	 * 更新箱袋状态
	 * 
	 * @author niguoyong
	 * @version 2015-09-01
	 * 
	 * @Description
	 * @param stoBoxInfo
	 */
	@Transactional(readOnly = false)
	public void updateStatus(StoBoxInfo stoBoxInfo) {
		boxDao.updateStatus(stoBoxInfo);
	}

	/**
	 * 批量更新箱袋状态
	 * 
	 * @author chengshu
	 * @version 2015-10-22
	 * 
	 * @Description
	 * @param boxList
	 *            箱号列表
	 * @param status
	 *            状态
	 */
	@Transactional(readOnly = false)
	public int updateStatusBatch(List<String> boxList, String status, User user) {
		StoBoxInfo boxInfo = new StoBoxInfo();
		if (user != null) {
			boxInfo.setUpdateBy(user);
			boxInfo.setUpdateName(user.getName());
		}
		boxInfo.setUpdateDate(new Date());
		boxInfo.setBoxNos(boxList);
		boxInfo.setBoxStatus(status);
		return boxDao.updateStatusBatch(boxInfo);
	}

	/**
	 * @author 箱袋查询接口
	 * @param headInfo
	 * @return
	 */
	public List<StoBoxInfo> searchBoxInfoList(Map<String, Object> headInfo) {

		return searchBoxInfo(headInfo);
	}

	/**
	 * @author 箱袋查询接口
	 * @param boxNos
	 *            箱号数组
	 * @return
	 */
	public List<StoBoxInfo> searchBoxListByArray(StoBoxInfo stoBoxInfo) {

		return boxDao.findBoxAndRouteList(stoBoxInfo);
	}

	/**
	 * @author 箱袋绑定RFID接口
	 * @param headInfo
	 * @return
	 */
	@Transactional(readOnly = false)
	public int updateBoxDelflag(Map<String, Object> headInfo) {
		StoBoxInfo box = new StoBoxInfo();
		if (headInfo.get("boxNo") != null && StringUtils.isNotEmpty("boxNo")) {
			box.setId(BusinessUtils.fillBoxNo(headInfo.get("boxNo").toString()));
		}
		if (headInfo.get("rfid") != null && StringUtils.isNotEmpty("rfid")) {
			box.setRfid((headInfo.get("rfid").toString()));
		}
		// 设置更新人信息
		User loginUser = UserUtils.get(headInfo.get(Parameter.USER_ID_KEY).toString());
		if (loginUser != null) {
			box.setUpdateBy(loginUser);
		}
		box.setUpdateDate(new Date());
		// 更改箱袋delFlag状态
		return boxDao.updateBoxDelflag(box);
	}

	/**
	 * @author 更新箱袋状态和出库预约时间
	 * @param headInfo
	 * @return
	 */
	@Transactional(readOnly = false)
	public int updateOutdateBatch(List<String> boxList, Date outDate, User user) {
		StoBoxInfo boxInfo = new StoBoxInfo();
		if (user != null) {
			boxInfo.setUpdateBy(user);
			boxInfo.setUpdateName(user.getName());
		}
		boxInfo.setUpdateDate(new Date());
		boxInfo.setBoxNos(boxList);
		boxInfo.setOutDate(outDate);
		// 更改箱袋状态
		return boxDao.updateOutdateBatch(boxInfo);
	}

	/**
	 * @author LF 根据条件查询箱袋信息
	 * @param headInfo
	 * @return
	 */
	private List<StoBoxInfo> searchBoxInfo(Map<String, Object> headInfo) {

		StoBoxInfo box = new StoBoxInfo();
		// 判断箱袋编号
		if (headInfo.get("boxNo") != null && StringUtils.isNotEmpty(headInfo.get("boxNo").toString())) {
			box.setId(headInfo.get("boxNo").toString());
		}
		// 判断箱袋RFID锁号
		if (headInfo.get("rfid") != null && StringUtils.isNotEmpty(headInfo.get("rfid").toString())) {
			box.setRfid(headInfo.get("rfid").toString());
		}
		// 判断箱袋类型
		if (headInfo.get("boxType") != null && StringUtils.isNotEmpty(headInfo.get("boxType").toString())) {
			box.setBoxType(headInfo.get("boxType").toString());
		}
		// 判断有效标识
		if (headInfo.get("delFlag") != null && StringUtils.isNotEmpty(headInfo.get("delFlag").toString())) {
			box.setDelFlag(headInfo.get("delFlag").toString());
		} else {
			box.setDelFlag("");
		}
		// 判断所属机构
		if (headInfo.get("officeId") != null && StringUtils.isNotEmpty(headInfo.get("officeId").toString())) {
			String officeId = headInfo.get("officeId").toString();
			Office off = new Office(officeId);

			box.setOffice(off);
		}
		// desc delFlag
		return boxDao.searchBoxInfoList(box);
	}

	/**
	 * 
	 * @author niguoyong
	 * @version 2015-09-01
	 * 
	 *          验证自增序列是否达到最大
	 * @param seqNo
	 * @return
	 */
	private int valiadateSeqNo(int seqNo) {
		// 自增序列最大长度
		int seqNoLength = Integer.parseInt(Global.getConfig("boxNo.seqNo.max"));
		// 如果自增序列大于最大序列号，差>0
		return seqNo - seqNoLength;
	}

	/**
	 * 根据条件查询款箱
	 * 
	 * @param page
	 * @param box
	 * @return
	 */
	public Page<StoBoxInfo> findBoxList(Page<StoBoxInfo> page, StoBoxInfo box) {

		// 生成数据权限过滤条件（dsf为dataScopeFilter的简写，在xml中使用 ${sqlMap.dsf}调用权限SQL）
		box.getSqlMap().put("dsf", dataScopeFilter(UserUtils.getUser(), "o5", null));
		// 查询数据列表
		List<StoBoxInfo> boxList = dao.findBoxList(box);
		page.setList(boxList);

		return page;
	}

	/**
	 * 查询对应的箱袋明细
	 * 
	 * @author xp
	 * @version 2017-7-6
	 * 
	 */
	@Transactional(readOnly = true)
	public List<StoBoxDetail> findStoBoxDetailList(StoBoxDetail stoBoxDetail) {
		return stoBoxDetailDao.findList(stoBoxDetail);
	}

	/**
	 * 更新或者添加箱子及插入箱袋明细
	 * 
	 * @author xp
	 * @version 2017-7-6
	 */
	@Transactional(readOnly = false)
	public void saveOrUpdate(StoBoxInfo box) {
		List<StoBoxInfo> stoBoxInfoList = boxDao.getBoxInfoByIdOrRfid(box);

		if (Collections3.isEmpty(stoBoxInfoList)) {
			int i = boxDao.insert(box);
			if (i == 0) {
				String strMessageContent = "boxNo为：" + box.getId() + "信息异常";
				throw new BusinessException(ExternalConstant.HardwareInterface.ERROR_NO_E99, strMessageContent);
			}
		} else {
			int i = boxDao.update(box);
			if (i == 0) {
				String strMessageContent = "boxNo为：" + box.getId() + "信息异常";
				throw new BusinessException(ExternalConstant.HardwareInterface.ERROR_NO_E99, strMessageContent);
			}
		}
		stoBoxDetailDao.deleteByBoxNo(box.getId());
		if (!Collections3.isEmpty(box.getStoBoxDetail())) {
			for (StoBoxDetail stoBoxDetail : box.getStoBoxDetail()) {
				stoBoxDetail.setDetailId(IdGen.uuid());
				stoBoxDetail.setBoxNo(box.getId());
				int i = stoBoxDetailDao.insert(stoBoxDetail);
				if (i == 0) {
					String strMessageContent = "goodsId为：" + stoBoxDetail.getGoodsId() + "信息异常";
					throw new BusinessException(ExternalConstant.HardwareInterface.ERROR_NO_E99, strMessageContent);
				}
			}
		}
	}

	/***
	 * 
	 * Title: getBoxInfoByRfidAndBoxNo
	 * <p>
	 * Description: 根据箱号、RFID查询箱子明细信息
	 * </p>
	 * 
	 * @author: wangbaozhong
	 * @param stoBoxInfo
	 *            箱号、RIFD查询条件
	 * @return 箱子明细信息 StoBoxInfo 返回类型
	 */
	public StoBoxInfo getBoxInfoByRfidAndBoxNo(StoBoxInfo stoBoxInfo) {
		return dao.getBoxInfoByRfidAndBoxNo(stoBoxInfo);
	}

	/***
	 * 
	 * <p>
	 * Description: 根据箱号、RFID查询箱子明细信息
	 * </p>
	 * 
	 * @author: wangbaozhong
	 * @param stoBoxInfo
	 *            箱号List查询条件
	 * @return 箱子明细信息 StoBoxInfo 返回类型
	 */
	public List<StoBoxInfo> searchBoxList(StoBoxInfo stoBoxInfo) {
		return boxDao.searchBoxInfoList(stoBoxInfo);
	}

	public int updateInfo(StoBoxInfo stoBoxInfo) {
		return boxDao.updateInfo(stoBoxInfo);
	}

	/**
	 * 删除箱袋明细
	 * 
	 * @author xp
	 * @version 2017-08-22
	 * 
	 * @Description
	 * @param boxNo
	 * @return
	 */
	@Transactional(readOnly = false)
	public int deleteBoxDetail(String boxNo) {
		return stoBoxDetailDao.deleteByBoxNo(boxNo);
	}

	/**
	 * 生成RFID码
	 * 
	 * @author sg
	 * @date 2017年11月02日
	 * 
	 * @Description
	 * @param boxType
	 * @param seqNo
	 * @param boxTypeNo
	 * @param officeId
	 * @return
	 */
	public String generateRFIDs(String boxType, int seqNo, String officeId, String boxTypeNo) {
		String RFID = "";
		// 小车RFID生成规则：机构8位+箱袋编号8位
		if (Constant.BoxType.BOX_CAR.equals(boxType)) {
			if (StringUtils.isNotBlank(officeId)) {
				RFID = BusinessUtils.fillOfficeId(officeId) + BusinessUtils.generateBoxNo(boxType, seqNo);
			}
		}
		// 钞箱RFID生成规则：钞箱类型编号8位+箱袋编号8位
		else {
			if (StringUtils.isNoneBlank(boxTypeNo)) {
				String boxTypeNo1 = boxTypeNo + Global.getConfig("atmBox.mark");
				RFID = BusinessUtils.fillBoxTypeNo(boxTypeNo1) + BusinessUtils.generateBoxNo(boxType, seqNo);
			}
		}

		return RFID;
	}

	/**
	 * @author sg 箱袋主表的数据一致性验证
	 * 
	 * @param StoBoxInfo
	 */
	public void checkVersion(StoBoxInfo stoBoxInfo) {
		// 数据一致性验证
		StoBoxInfo oldData = getATM(stoBoxInfo.getId());
		if (oldData != null) {
			String oldUpdateDate = DateUtils.formatDate(oldData.getUpdateDate(),
					Constant.Dates.FORMATE_YYYYMMDDHHMMSSSSSSSS);
			if (!oldUpdateDate.equals(stoBoxInfo.getStrUpdateDate())) {
				throw new BusinessException("message.E0007", "", new String[] { stoBoxInfo.getId() });
			}
		} else {
			throw new BusinessException("message.E0008", "", new String[] { stoBoxInfo.getId() });
		}
	}

	/**
	 * @author sg 箱袋绑定RFID接口
	 * @param headInfo
	 * @return
	 */
	@Transactional(readOnly = false)
	public int updateBoxDelflags(StoBoxInfo box) {
		// 更改箱袋delFlag状态
		return boxDao.updateBoxDelflag(box);
	}

	/**
	 * PDA钞箱出库(入库) 更新箱子状态
	 * 
	 * @author wxz
	 * @param StoBoxInfo
	 * @version 2017-11-15
	 * @return
	 */
	@Transactional(readOnly = false)
	public int updateAtmStatus(StoBoxInfo box) {
		return boxDao.updateAtmStatus(box);
	}
	
	/**
	 * 根据箱号物理删除箱子信息
	 * 
	 * @author WQJ
	 * @version 2019-1-9
	 * 
	 * @Description
	 * @param boxNo
	 * @return
	 */
	@Transactional(readOnly = false)
	public int realDelete(String boxNo) {
		//物理删除
		return boxDao.realDelete(boxNo);
	}
}

//
// /**
// *
// * @author niguoyong
// * @version 2015-09-01
// *
// * @Description 获取金库下所有机构Id信息
// * @param parantId
// * @param officeList
// * @return
// */
// private List<String> getOfficeIdList(String parantId, List<Office>
// officeList) {
// List<String> officeIdList = Lists.newArrayList();
// for (Office office : officeList) {
// officeIdList.add(office.getId());
// }
// // 将自身机构Id放入
// officeIdList.add(parantId);
// return officeIdList;
// }
