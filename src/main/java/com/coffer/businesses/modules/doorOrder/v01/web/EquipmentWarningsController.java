package com.coffer.businesses.modules.doorOrder.v01.web;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.coffer.businesses.common.Constant;
import com.coffer.businesses.modules.doorOrder.v01.entity.EquipmentInfo;
import com.coffer.businesses.modules.doorOrder.v01.entity.EquipmentWarnings;
import com.coffer.businesses.modules.doorOrder.v01.service.EquipmentInfoService;
import com.coffer.businesses.modules.doorOrder.v01.service.EquipmentWarningsService;
import com.coffer.core.common.config.Global;
import com.coffer.core.common.persistence.Page;
import com.coffer.core.common.utils.StringUtils;
import com.coffer.core.common.web.BaseController;
import com.coffer.core.modules.sys.entity.Office;
import com.coffer.core.modules.sys.service.OfficeService;
import com.coffer.core.modules.sys.utils.UserUtils;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import static com.coffer.core.common.service.BaseService.dataScopeFilter;

/**
 * 机具报警Controller
 * 
 * @author zhaohaoran
 * @date 2019-07-15
 */
@Controller
@RequestMapping(value = "${adminPath}/doorOrder/v01/equipmentWarnings")
public class EquipmentWarningsController extends BaseController{
	@Autowired
	private EquipmentWarningsService equipmentWarningsService;
	@Autowired
	private EquipmentInfoService equipmentInfoService;
	@Autowired
	private OfficeService officeService;

	/**
	 * ModelAttribute
	 * 
	 * @author zhaohaoran 
	 * @date 2019-07-16
	 */
	@ModelAttribute
	public EquipmentWarnings get(@RequestParam(required = false) String id) {
		EquipmentWarnings equipmentWarnings = null;
		if (StringUtils.isNotBlank(id)) {
			equipmentWarnings = equipmentWarningsService.get(id);
		}
		if (equipmentWarnings == null) {
			equipmentWarnings = new EquipmentWarnings();
		}
		return equipmentWarnings;
	}
	
	@RequiresPermissions("doorOrder:equipmentWarnings:view")
	@RequestMapping(value = { "" })
	public String index(Office office, Model model) {
		model.addAttribute("list", officeService.findDoorList(office));
		return "modules/doorOrder/v01/equipmentWarnings/equipmentWarningsIndex";
	}

	/**
	 * 所有分页列表
	 * 
	 * @author zhaohaoran 
	 * @date 2019-07-16 
	 */
	@RequiresPermissions("doorOrder:equipmentWarnings:view")
	@RequestMapping(value = { "list" })
    public String list(EquipmentWarnings equipmentWarnings, @RequestParam(required = false) boolean isSearch,
                       HttpServletRequest request, HttpServletResponse response, Model model) {
        Page<EquipmentWarnings> page = new Page<EquipmentWarnings>(request, response);
        //归属门店机构
        Office officeDoor = new Office();
        //屏蔽掉原来代码 hzy 2020-04-15
       // officeDoor.setId(equipmentWarnings.getId());
        // 查询对应机具所在门店机构信息  hzy 2020-04-15 start
        officeDoor = officeService.get(equipmentWarnings.getMachNo());
        // 查询对应机具所在门店机构信息  hzy 2020-04-15 end
        equipmentWarnings.setOffice(officeDoor); 
        officeDoor = UserUtils.getUser().getOffice();
        String otype = officeDoor.getType();
        switch (otype) {
            //清分中心
            case Constant.OfficeType.CLEAR_CENTER:
                equipmentWarnings.setCenterId(UserUtils.getUser().getOffice().getId());
                break;
            //门店
            case Constant.OfficeType.STORE:
                equipmentWarnings.setDoorId(UserUtils.getUser().getOffice().getId());
                break;
            //商户
            case Constant.OfficeType.MERCHANT:
                equipmentWarnings.setMerchantId((UserUtils.getUser().getOffice().getId()));
                break;
            default:
                equipmentWarnings.getSqlMap().put("dsf", dataScopeFilter(equipmentWarnings.getCurrentUser(), "b", null));
                break;
        }
        page = equipmentWarningsService.findPage(page, equipmentWarnings);
        model.addAttribute("page", page);
        return "modules/doorOrder/v01/equipmentWarnings/equipmentWarningsList";
    }
		
	/**
	 * 返回
	 * 
	 * @author zhaohaoran
	 * @date 2019-07-16
	 */
	@RequestMapping(value = "back")
	public String back(EquipmentWarnings equipmentWarnings, Model model, RedirectAttributes redirectAttributes) {
		return "redirect:" + Global.getAdminPath() + "/doorOrder/v01/equipmentWarnings/list?isSearch=true&repage";
	}
	
	/**
	 * @author zhr 
	 * @date 2019-07-30
	 * @Description 查看画面 
	 */
	@RequiresPermissions("doorOrder:equipmentWarnings:view")
	@RequestMapping(value = "view")
	public String view(EquipmentWarnings equipmentWarnings, Model model) {
		EquipmentInfo eq = new EquipmentInfo();
		eq.setId(equipmentWarnings.getMachNo());	
		EquipmentInfo equipmentInfo = equipmentInfoService.get(eq);
		if(equipmentInfo == null){
			model.addAttribute("equipmentInfo", eq);
		}else{
			model.addAttribute("equipmentInfo", equipmentInfo);
		}
		return "modules/doorOrder/v01/equipmentWarnings/equipmentWarningsView";		
	}
	
	
	/**
	 * @author zhr
	 * @version 2019-08-05获取报警JSON数据。
	 * 
	 * @param extId
	 *            排除的ID
	 * @param type
	 *            等于
	 * @param maxType
	 *            小于等于
	 * @param isAll
	 * @param isNotNeedSubPobc
	 *            是否列出当前人行机构下子人行及其商业机构
	 * @param response
	 * @return
	 */
	@RequiresPermissions("user")
	@ResponseBody
	@RequestMapping(value = "treeData")
	public List<Map<String, Object>> treeData(@RequestParam(required = false) String extId,
			@RequestParam(required = false) Long type, @RequestParam(required = false) Long maxType,
			@RequestParam(required = false) Boolean isAll, @RequestParam(required = false) String tradeFlag,
			@RequestParam(required = false) Boolean isNotNeedSubPobc, HttpServletResponse response,
			@RequestParam(required = false) Long minType,@RequestParam(required = false) Boolean clearCenterFilter) {
		response.setContentType("application/json; charset=UTF-8");
		List<Map<String, Object>> mapList = Lists.newArrayList();
		Office loginUserOffice = UserUtils.getUser().getOffice();
		String otype=Constant.OfficeType.STORE;
		/*if(loginUserOffice.getType()==Constant.OfficeType.ROOT){
			otype=Constant.OfficeType.ROOT;
		}else if(loginUserOffice.getType()==Constant.OfficeType.CLEAR_CENTER){
			otype=Constant.OfficeType.CLEAR_CENTER;
		}else if(loginUserOffice.getType()==Constant.OfficeType.STORE){
			otype=Constant.OfficeType.STORE;
		}else if(loginUserOffice.getType()==Constant.OfficeType.MERCHANT){
			otype=Constant.OfficeType.MERCHANT;
		}*/
		List<Office> list = equipmentWarningsService.findDoorList(loginUserOffice,otype);
		for (int i = 0; i < list.size(); i++) {
			Office e = list.get(i);
			if ((extId == null
					|| (extId != null && !extId.equals(e.getId()) && e.getParentIds().indexOf("," + extId + ",") == -1))
					&& (type == null || (type != null && Integer.parseInt(e.getType()) == type.intValue()))
					&& (maxType == null || (maxType != null && Integer.parseInt(e.getType()) <= maxType.intValue()))
					&& (minType == null || (minType != null && Integer.parseInt(e.getType()) >= minType.intValue()))
					&& (StringUtils.isBlank(tradeFlag)
							|| (StringUtils.isNotBlank(tradeFlag) && tradeFlag.equals(e.getTradeFlag())))
					&& (isNotNeedSubPobc == null || (isNotNeedSubPobc != null && isNotNeedSubPobc
							&& e.getParentId().equals(loginUserOffice.getId())))
					&& (clearCenterFilter == null
							|| (clearCenterFilter != null && clearCenterFilter
									&& Constant.OfficeType.CLEAR_CENTER.equals(loginUserOffice.getType())
									&& e.getParentIds().indexOf("," + loginUserOffice.getParentId() + ",") != -1)
							|| (clearCenterFilter != null && clearCenterFilter
									&& !Constant.OfficeType.CLEAR_CENTER.equals(loginUserOffice.getType())
									&& e.getParentIds().indexOf("," + loginUserOffice.getId() + ",") != -1))) {
				Map<String, Object> map = Maps.newHashMap();
				map.put("id", e.getId());
				map.put("pId", e.getParentId());
				map.put("pIds", e.getParentIds());
				map.put("name", e.getName());
				map.put("type", e.getType());
				mapList.add(map);
			}
		}
		return mapList;
	}

	
}
