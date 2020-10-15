package com.coffer.businesses.modules.doorOrder.v01.web;

import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.coffer.businesses.common.Constant.EquipmentStatus;
import com.coffer.businesses.modules.doorOrder.v01.entity.AjaxDoorOrderInfo;
import com.coffer.businesses.modules.doorOrder.v01.entity.AjaxEquipmentInfo;
import com.coffer.businesses.modules.doorOrder.v01.entity.AjaxResponse;
import com.coffer.businesses.modules.doorOrder.v01.entity.EquipmentInfo;
import com.coffer.businesses.modules.doorOrder.v01.service.EquipmentInfoService;
import com.coffer.businesses.modules.weChat.v03.entity.DoorOrderInfo;
import com.coffer.businesses.modules.weChat.v03.service.DoorOrderInfoService;
import com.coffer.core.common.config.Global;
import com.coffer.core.common.persistence.Page;
import com.coffer.core.common.service.BusinessException;
import com.coffer.core.common.utils.Collections3;
import com.coffer.core.common.utils.StringUtils;
import com.coffer.core.common.web.BaseController;
import com.google.common.collect.Lists;

/**
 * 机具管理Controller
 *
 * @author 机具管理
 * @version 2019-06-26
 */
@Controller
@RequestMapping(value = "${adminPath}/doorOrder/v01/equipmentInfo")
public class EquipmentInfoController extends BaseController {

    @Autowired
    private EquipmentInfoService equipmentInfoService;
    
    @Autowired
    private DoorOrderInfoService doorOrderInfoService;

    @ModelAttribute
    public EquipmentInfo get(@RequestParam(required = false) String id) {
        EquipmentInfo entity = null;
        if (StringUtils.isNotBlank(id)) {
            entity = equipmentInfoService.get(id);
        }
        if (entity == null) {
            entity = new EquipmentInfo();
        }
        return entity;
    }

    @RequiresPermissions("doorOrder:equipmentInfo:view")
    @RequestMapping(value = {"list", ""})
    public String list(EquipmentInfo equipmentInfo, HttpServletRequest request, HttpServletResponse response,
                       Model model) {
        Page<EquipmentInfo> page = equipmentInfoService.findPage(new Page<EquipmentInfo>(request, response),
                equipmentInfo);
        model.addAttribute("page", page);
        return "modules/doorOrder/v01/equipmentInfo/equipmentInfoList";
    }

    @RequiresPermissions("doorOrder:equipmentInfo:view")
    @RequestMapping(value = "form")
    public String form(EquipmentInfo equipmentInfo, Model model) {
        model.addAttribute("equipmentInfo", equipmentInfo);
        return "modules/doorOrder/v01/equipmentInfo/equipmentInfoForm";
    }

    @RequiresPermissions("doorOrder:equipmentInfo:edit")
    @RequestMapping(value = "save")
    public String save(EquipmentInfo equipmentInfo, Model model, RedirectAttributes redirectAttributes) {
        if (!beanValidator(model, equipmentInfo)) {
            return form(equipmentInfo, model);
        }
        equipmentInfoService.save(equipmentInfo);
        addMessage(redirectAttributes, "保存机具管理成功");
        return "redirect:" + Global.getAdminPath() + "/doorOrder/v01/equipmentInfo/?repage";
    }

    @RequiresPermissions("doorOrder:equipmentInfo:edit")
    @RequestMapping(value = "delete")
    public String delete(EquipmentInfo equipmentInfo, RedirectAttributes redirectAttributes) {
        equipmentInfoService.delete(equipmentInfo);
        addMessage(redirectAttributes, "删除机具管理成功");
        return "redirect:" + Global.getAdminPath() + "/doorOrder/v01/equipmentInfo/?repage";
    }

    @RequiresPermissions("doorOrder:equipmentInfo:bind")
    @RequestMapping(value = "bind")
    public String bind(EquipmentInfo equipmentInfo, RedirectAttributes redirectAttributes, Model model) {
        String message = "";
        Locale locale = LocaleContextHolder.getLocale();
        checkParam(equipmentInfo);
        equipmentInfo.setStatus(EquipmentStatus.BIND);
        //equipmentInfoService.save(equipmentInfo);
        try {
            equipmentInfoService.save(equipmentInfo);
            message = msg.getMessage("message.E5002", new String[]{}, locale);
        } catch (BusinessException be) {
            message = msg.getMessage(be.getMessageCode(), be.getParameters(), locale);
        }
        addMessage(redirectAttributes, message);
        return "redirect:" + Global.getAdminPath() + "/doorOrder/v01/equipmentInfo/?repage";
    }

    /**
     * 参数判断是否为空
     *
     * @param equipmentInfo
     * @return
     */
    public void checkParam(EquipmentInfo equipmentInfo) {

        if (StringUtils.isBlank(equipmentInfo.getId())) {
            throw new BusinessException("message.E7204", "", new String[]{});
        }
        if (equipmentInfo.getaOffice() == null) {
            throw new BusinessException("message.E7207", "", new String[]{});
        }
        if (StringUtils.isBlank(equipmentInfo.getaOffice().getId())) {
            throw new BusinessException("message.E7205", "", new String[]{});
        }
        if (StringUtils.isBlank(equipmentInfo.getaOffice().getName())) {
            throw new BusinessException("message.E7206", "", new String[]{});
        }

    }

    @RequiresPermissions("doorOrder:equipmentInfo:nobind")
    @RequestMapping(value = "nobind")
    public String nobind(EquipmentInfo equipmentInfo, @RequestParam(value = "office") String officeId, RedirectAttributes redirectAttributes) {
        String message = "";
        Locale locale = LocaleContextHolder.getLocale();
        if (StringUtils.isBlank(equipmentInfo.getStatus())) {
            throw new BusinessException("message.E7204");
        }
        // 其他用户解绑之前检查页面数据是否已经和数据库同步
        if (!equipmentInfo.getaOffice().getId().equals(officeId)) {
            message = msg.getMessage("message.E5003", new String[]{}, locale);
            addMessage(redirectAttributes, message);
            return "redirect:" + Global.getAdminPath() + "/doorOrder/v01/equipmentInfo/?repage";
        }
        equipmentInfo.setStatus(EquipmentStatus.NOBIND);
        try {
            equipmentInfoService.save(equipmentInfo);
            message = msg.getMessage("message.E5001", new String[]{}, locale);
        } catch (BusinessException be) {
            message = msg.getMessage(be.getMessageCode(), be.getParameters(), locale);
        }
        addMessage(redirectAttributes, message);
        return "redirect:" + Global.getAdminPath() + "/doorOrder/v01/equipmentInfo/?repage";
    }

    /**
     * 返回上一级页面
     *
     * @param response
     * @param request
     * @return
     */
    @RequestMapping(value = "back")
    public String back(HttpServletResponse response, HttpServletRequest request) {
        return "redirect:" + Global.getAdminPath() + "/doorOrder/v01/equipmentInfo/?repage";
    }

    /**
     * 判断门店是否已绑定机具
     *
     * @param id
     * @return
     */
//    @ModelAttribute
//    public EquipmentInfo checkDoorBinded(@RequestParam(required = false) String id) {
//        EquipmentInfo entity = null;
//        if (StringUtils.isNotBlank(id)) {
//            entity = equipmentInfoService.checkDoorBinded(id);
//        }
//        if (entity == null) {
//            entity = new EquipmentInfo();
//        }
//        return entity;
//    }
    
    /**
     * 获取机具在线状态接口
     *
     * Description: 
     *
     * @author: GJ
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "getEquipmentStatus")
    public AjaxResponse getEquipmentStatus() {
    	DoorOrderInfo doorOrderInfo = new DoorOrderInfo();
    	List<AjaxEquipmentInfo> result = Lists.newArrayList();
    	EquipmentInfo equipmentInfo = new EquipmentInfo();
    	//根据当前登录人所属机构获取对应的机具列表
    	List<EquipmentInfo> list = equipmentInfoService.findEqpList(equipmentInfo);
    	List<String> idList = Lists.newArrayList();
    	if(!Collections3.isEmpty(list)) {
    		for (EquipmentInfo temp : list) {
        		idList.add(temp.getId());
    		}
        	doorOrderInfo.setEqpIds(idList);
        	//根据获取的机具列表查询机具中的存款信息
        	List<DoorOrderInfo> infoResult = doorOrderInfoService.getInfoByEqpIds(doorOrderInfo);
    		for (int i = 0; i < list.size(); i++) {
        		AjaxEquipmentInfo info = new AjaxEquipmentInfo();
        		info.setId(list.get(i).getId());
        		info.setName(list.get(i).getName());
        		info.setSeriesNumber(list.get(i).getSeriesNumber());
        		AjaxDoorOrderInfo orderInfo = new AjaxDoorOrderInfo();
        		orderInfo.setAmount("0");
        		orderInfo.setTotalCount("0");
        		orderInfo.setPercent("0");
        		orderInfo.setBagCapacity("0");
        		if(!Collections3.isEmpty(infoResult)) {
					for(int j = 0; j < infoResult.size(); j++) {
		    			if(list.get(i).getId().toUpperCase().equals(infoResult.get(j).getEquipmentId().toUpperCase())) {
		    				orderInfo.setAmount(infoResult.get(j).getAmount());
		    				String strTotalCount = infoResult.get(j).getMoneyCount();
		    				if(strTotalCount == null) {
		    					strTotalCount = "0";
		    				}
		    				String strBagCapacity = infoResult.get(j).getBagCapacity();
		    				orderInfo.setTotalCount(strTotalCount);
		    				orderInfo.setBagCapacity(strBagCapacity);
		    				float floatTotalCount = 0;
		    				if(!StringUtils.isEmpty(strTotalCount)) {
		    					floatTotalCount = Float.parseFloat(strTotalCount);
		    				}
		    				float floatBagCapacity = 0;
		    				if(!StringUtils.isEmpty(strBagCapacity)) {
		    					floatBagCapacity = Float.parseFloat(strBagCapacity);
		    				}
		    				double percent = 0;
		    				if(!StringUtils.isEmpty(strTotalCount) 
		    						&& !StringUtils.isEmpty(strBagCapacity) 
		    						&& floatBagCapacity != 0) {
		    					percent = floatTotalCount / floatBagCapacity;
		    				}
		    				orderInfo.setPercent(StringUtils.toString(percent));
		    				break;
		    			}
		    		}
        		} 
    			info.setOrderInfo(orderInfo);
        		result.add(info);
    		}
    	}
    	return AjaxResponse.success(result);
    }
    
}