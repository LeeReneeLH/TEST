package com.coffer.businesses.modules.clear.v03.dao;

import java.util.List;

import com.coffer.businesses.modules.clear.v03.entity.CustomerClearance;
import com.coffer.core.common.persistence.CrudDao;
import com.coffer.core.common.persistence.annotation.MyBatisDao;

/**
 * 客户清分DAO接口
 * 
 * @author wzj
 * @version 2017-09-06
 */
@MyBatisDao
public interface CustomerClearanceDao extends CrudDao<CustomerClearance> {

	/**
	 * 分页查询所有银行取款
	 * 
	 * @author wzj
	 * @version 2017年9月27日
	 * @param customerClearance
	 * @return List<CustomerClearance>
	 */
	public List<CustomerClearance> findListCustomerClearance(CustomerClearance customerClearance);

	/**
	 * 查询客户银行取款
	 * 
	 * @author wzj
	 * @version 2017年10月16日
	 * @param customerClearance
	 * @return List<CustomerClearance>
	 */
	public List<CustomerClearance> findListCustomerQuantity(CustomerClearance customerClearance);

	/**
	 * 分页查询所有银行取款(导出表格第一页)
	 * 
	 * @author wzj
	 * @version 2017年11月21日
	 * @param customerClearance
	 * @return List<CustomerClearance>
	 */
	public List<CustomerClearance> findListCustomerClearanceAll(CustomerClearance customerClearance);

	/**
	 * 查询所有银行取款对应面值总数量
	 * 
	 * @author wzj
	 * @version 2017年12月26日
	 * @param customerClearance
	 * @return List<CustomerClearance>
	 */
	public CustomerClearance findAllCount(CustomerClearance customerClearance);

	/**
	 * 查询所有银行取款对应面值总数量
	 * 
	 * @author wzj
	 * @version 2017年12月27日
	 * @param customerClearance
	 * @return List<CustomerClearance>
	 */
	public CustomerClearance findCountFirstSheet(CustomerClearance customerClearance);
}
