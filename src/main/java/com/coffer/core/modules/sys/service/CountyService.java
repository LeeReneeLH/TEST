package com.coffer.core.modules.sys.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.coffer.core.common.service.CrudService;
import com.coffer.core.modules.sys.dao.CountyDao;
import com.coffer.core.modules.sys.entity.CountyEntity;
/**
 * 
* Title: CountyService 
* <p>Description: 县区业务层</p>
* @author wanghan
* @date 2017年12月25日 上午11:11:30
 */
@Service
@Transactional(readOnly = true)
public class CountyService extends CrudService<CountyDao,CountyEntity>{

	/**
	 * 
	 * Title: findSelect2CountyData
	 * <p>Description: 查询县级表有效数据</p>
	 * @author:     wanghan
	 * @param countyEntity 县级表实体类对象
	 * @return 返回县级表实体类对象集合
	 * List<CountyEntity>    返回类型
	 */
	@Transactional(readOnly = true)
	public List<CountyEntity> findSelect2CountyData(CountyEntity countyEntity){
		return dao.findSelect2CountyData(countyEntity);
	}
	
	/**
	 * 
	 * Title: revert
	 * <p>Description: 更改县/区记录无效为有效</p>
	 * @author:     wanghan
	 * @param countyEntity 县级表实体类对象
	 * void    返回类型
	 */
	@Transactional(readOnly = false)
	public void revert(CountyEntity countyEntity) {
		dao.revert(countyEntity);
	}
	
	/**
	 * 
	 * Title: findCountyCodeNum
	 * <p>Description: 查询当前条件的县/区记录个数</p>
	 * @author:     wanghan
	 * @param countyCode 县/区编码
	 * @return 县/区记录个数
	 * int    返回类型
	 */
	@Transactional(readOnly = true)
	public int findCountyCodeNum(String countyCode) {
		return dao.findCountyCodeNum(countyCode);
	}
}
