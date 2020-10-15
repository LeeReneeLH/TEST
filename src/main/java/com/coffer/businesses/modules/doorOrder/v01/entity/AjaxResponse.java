package com.coffer.businesses.modules.doorOrder.v01.entity;

public class AjaxResponse {
	private boolean isok; // 请求是否处理成功
	private int code; // 请求响应状态码（200、400、500）
	private String message; // 请求结果描述信息
	private Object data; // 请求结果数据（通常用于查询操作）
	private Long allCount; // 总条数 
	private Integer currentPageCount; //当前分页的条数

	public Long getAllCount() {
		return allCount;
	}

	public void setAllCount(Long allCount) {
		this.allCount = allCount;
	}

	public Integer getCurrentPageCount() {
		return currentPageCount;
	}

	public void setCurrentPageCount(Integer currentPageCount) {
		this.currentPageCount = currentPageCount;
	}

	public boolean isIsok() {
		return isok;
	}

	public void setIsok(boolean isok) {
		this.isok = isok;
	}

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public Object getData() {
		return data;
	}

	public void setData(Object data) {
		this.data = data;
	}

	public AjaxResponse() {
	}

	// 请求成功的响应，不带查询数据（用于删除、修改、新增接口）
	public static AjaxResponse success() {
		AjaxResponse ajaxResponse = new AjaxResponse();
		ajaxResponse.setIsok(true);
		ajaxResponse.setCode(200);
		ajaxResponse.setMessage("请求响应成功!");
		return ajaxResponse;
	}

	// 请求成功的响应，带有查询数据（用于数据查询接口）
	public static AjaxResponse success(Object obj) {
		AjaxResponse ajaxResponse = new AjaxResponse();
		ajaxResponse.setIsok(true);
		ajaxResponse.setCode(200);
		ajaxResponse.setMessage("请求响应成功!");
		ajaxResponse.setData(obj);
		return ajaxResponse;
	}

	// 请求成功的响应，带有查询数据（用于数据查询接口）
	public static AjaxResponse success(Object obj, String message) {
		AjaxResponse ajaxResponse = new AjaxResponse();
		ajaxResponse.setIsok(true);
		ajaxResponse.setCode(200);
		ajaxResponse.setMessage(message);
		ajaxResponse.setData(obj);
		return ajaxResponse;
	}
	
	// 请求成功的响应，带有查询数据（用于数据查询接口）
	public static AjaxResponse success(Object obj, Long allCount, Integer currentPageCount) {
		AjaxResponse ajaxResponse = new AjaxResponse();
		ajaxResponse.setIsok(true);
		ajaxResponse.setCode(200);
		ajaxResponse.setMessage("请求响应成功!");
		if(allCount != null) {
			ajaxResponse.setAllCount(allCount);
		}
		if(currentPageCount != null) {
			ajaxResponse.setCurrentPageCount(currentPageCount);
		}
		ajaxResponse.setData(obj);
		return ajaxResponse;
	}
}
