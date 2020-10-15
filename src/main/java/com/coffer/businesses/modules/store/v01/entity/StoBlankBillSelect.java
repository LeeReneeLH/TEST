package com.coffer.businesses.modules.store.v01.entity;

import java.io.Serializable;
import java.util.List;

import org.hibernate.validator.constraints.NotBlank;

/**
 * 重空选项
 * 
 * @author yuxixuan
 *
 */
public class StoBlankBillSelect implements Serializable {

	private static final long serialVersionUID = 8886937044379886460L;
	/** 重空分类 */
	private String blankBillKind;
	/** 重空类型 */
	private String blankBillType;
	/** ‘重空分类’保留项，逗号分隔 */
	private String blankBillKindReserve;
	/** ‘重空分类’保留项，数组 */
	private List<String> blankBillKindReserveList;
	/** ‘重空分类’移除项，逗号分隔 */
	private String blankBillKindRemove;
	/** ‘重空分类’移除项，数组 */
	private List<String> blankBillKindRemoveList;
	/** ‘重空类型’保留项，逗号分隔 */
	private String blankBillTypeReserve;
	/** ‘重空类型’保留项，数组 */
	private List<String> blankBillTypeReserveList;
	/** ‘重空类型’移除项，逗号分隔 */
	private String blankBillTypeRemove;
	/** ‘重空类型’移除项，数组 */
	private List<String> blankBillTypeRemoveList;
	/** 重空分类关联代码 */
	private String kindRefCode;

	@NotBlank(message = "重空分类不能为空")
	public String getBlankBillKind() {
		return blankBillKind;
	}

	public void setBlankBillKind(String blankBillKind) {
		this.blankBillKind = blankBillKind;
	}

	@NotBlank(message = "重空类型不能为空")
	public String getBlankBillType() {
		return blankBillType;
	}

	public void setBlankBillType(String blankBillType) {
		this.blankBillType = blankBillType;
	}

	public String getBlankBillKindReserve() {
		return blankBillKindReserve;
	}

	public void setBlankBillKindReserve(String blankBillKindReserve) {
		this.blankBillKindReserve = blankBillKindReserve;
	}

	public List<String> getBlankBillKindReserveList() {
		return blankBillKindReserveList;
	}

	public void setBlankBillKindReserveList(List<String> blankBillKindReserveList) {
		this.blankBillKindReserveList = blankBillKindReserveList;
	}

	public String getBlankBillKindRemove() {
		return blankBillKindRemove;
	}

	public void setBlankBillKindRemove(String blankBillKindRemove) {
		this.blankBillKindRemove = blankBillKindRemove;
	}

	public List<String> getBlankBillKindRemoveList() {
		return blankBillKindRemoveList;
	}

	public void setBlankBillKindRemoveList(List<String> blankBillKindRemoveList) {
		this.blankBillKindRemoveList = blankBillKindRemoveList;
	}

	public String getBlankBillTypeReserve() {
		return blankBillTypeReserve;
	}

	public void setBlankBillTypeReserve(String blankBillTypeReserve) {
		this.blankBillTypeReserve = blankBillTypeReserve;
	}

	public List<String> getBlankBillTypeReserveList() {
		return blankBillTypeReserveList;
	}

	public void setBlankBillTypeReserveList(List<String> blankBillTypeReserveList) {
		this.blankBillTypeReserveList = blankBillTypeReserveList;
	}

	public String getBlankBillTypeRemove() {
		return blankBillTypeRemove;
	}

	public void setBlankBillTypeRemove(String blankBillTypeRemove) {
		this.blankBillTypeRemove = blankBillTypeRemove;
	}

	public List<String> getBlankBillTypeRemoveList() {
		return blankBillTypeRemoveList;
	}

	public void setBlankBillTypeRemoveList(List<String> blankBillTypeRemoveList) {
		this.blankBillTypeRemoveList = blankBillTypeRemoveList;
	}

	public String getKindRefCode() {
		return kindRefCode;
	}

	public void setKindRefCode(String kindRefCode) {
		this.kindRefCode = kindRefCode;
	}
}
