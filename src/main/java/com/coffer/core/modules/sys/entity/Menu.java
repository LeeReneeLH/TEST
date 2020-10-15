/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.coffer.core.modules.sys.entity;

import java.util.List;

import org.hibernate.validator.constraints.Length;

import com.coffer.core.common.persistence.TreeEntity;
import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * 菜单Entity
 * @author ThinkGem
 * @version 2013-05-15
 */
public class Menu extends TreeEntity<Menu> {

	private static final long serialVersionUID = 1L;
	private String href; 	// 链接
	private String target; 	// 目标（ mainFrame、_blank、_self、_parent、_top）
	private String icon; 	// 图标
	private String isShow; 	// 是否在菜单中显示（1：显示；0：不显示）
	private String versionNo; 	// 版本号
	private String permission; // 权限标识
	// ADD-START add by yanbingxu  2018/04/24
	private String actCategory;// 工作流分组标识
	// ADD-END add by yanbingxu  2018/04/24
	// ADD-START add by hzy 2020年05月25日
	private String treeNeed; // 是否需要显示机构树
	// ADD-END add by hzy 2020年05月25日
	private String userId;
	
	public Menu(){
		super();
		this.sort = 30;
		this.isShow = "1";
		this.versionNo = "0101";
	}
	
	public Menu(String id){
		super(id);
	}
	
//	@JsonBackReference
//	@NotNull
	public Menu getParent() {
		return parent;
	}

	public void setParent(Menu parent) {
		this.parent = parent;
	}

	@Length(min=0, max=2000)
	public String getHref() {
		return href;
	}

	public void setHref(String href) {
		this.href = href;
	}

	@Length(min=0, max=20)
	public String getTarget() {
		return target;
	}

	public void setTarget(String target) {
		this.target = target;
	}
	
	@Length(min=0, max=100)
	public String getIcon() {
		return icon;
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}
	
	@Length(min=1, max=1)
	public String getIsShow() {
		return isShow;
	}

	public void setIsShow(String isShow) {
		this.isShow = isShow;
	}

	@Length(min=0, max=200)
	public String getPermission() {
		return permission;
	}

	public void setPermission(String permission) {
		this.permission = permission;
	}

	public String getParentId() {
		return parent != null && parent.getId() != null ? parent.getId() : "0";
	}

	@JsonIgnore
	public static void sortList(List<Menu> list, List<Menu> sourcelist, String parentId, boolean cascade){
		for (int i=0; i<sourcelist.size(); i++){
			Menu e = sourcelist.get(i);
			if (e.getParent()!=null && e.getParent().getId()!=null
					&& e.getParent().getId().equals(parentId)){
				list.add(e);
				if (cascade){
					// 判断是否还有子节点, 有则继续获取子节点
					for (int j=0; j<sourcelist.size(); j++){
						Menu child = sourcelist.get(j);
						if (child.getParent()!=null && child.getParent().getId()!=null
								&& child.getParent().getId().equals(e.getId())){
							sortList(list, sourcelist, e.getId(), true);
							break;
						}
					}
				}
			}
		}
	}

	@JsonIgnore
	public static String getRootId(){
		return "1";
	}
	
	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}
	
	public String getVersionNo() {
		return versionNo;
	}

	public void setVersionNo(String versionNo) {
		this.versionNo = versionNo;
	}

	/**
	 * @return the actCategory
	 */
	public String getActCategory() {
		return actCategory;
	}

	/**
	 * @param actCategory
	 *            the actCategory to set
	 */
	public void setActCategory(String actCategory) {
		this.actCategory = actCategory;
	}

	public String getTreeNeed() {
		return treeNeed;
	}

	public void setTreeNeed(String treeNeed) {
		this.treeNeed = treeNeed;
	}

}