
<%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
<title><spring:message code="store.box.manage" /></title>
<meta name="decorator" content="default" />
<script type="text/javascript" src="${ctxStatic}/common/common.js"></script>
<script type="text/javascript">
	$(document).ready(function() {
		boxTypeChange($("#boxType").val());
		$("#searchForm").validate({});
		/* $("#updateForm").validate({
			submitHandler : function(form) {
				alert(112);
				loading('正在提交，请稍等...');
				form.submit();
			},
			////errorContainer: "#messageBox",
			errorPlacement: function(error, element) {
				//$("#messageBox").text("输入有误，请先更正。");
				alert(113);
				if (element.is(":checkbox")||element.is(":radio")||element.parent().is(".input-append")){
					alert(114);
					error.appendTo(element.parent().parent());
				} else {
					alert(115);
					error.insertAfter(element);
				}
			}
		}); */
	});
	
	function boxTypeChange(value) {
		var boxTypeCar = "${fns:getConfig('sto.box.boxtype.car')}";
		var boxTypeBill = "${fns:getConfig('sto.box.boxtype.billbox_1')}";
		var boxTypeBill2 = "${fns:getConfig('sto.box.boxtype.billbox_2')}";
		var boxTypeBill3 = "${fns:getConfig('sto.box.boxtype.billbox_3')}";
		var boxTypeNote = "${fns:getConfig('sto.box.boxtype.atmbox')}";
		/* 追加钞箱类型 修改人：sg 修改时间：2017-11-02 begin */
		var boxTypeATM = "${fns:getConfig('sto.box.boxtype.atmShow')}";
		var boxTypeATMs = boxTypeATM.split(";");
		var check = false;
		for(var i = 0 ; i<boxTypeATMs.length ; i++){
			if(value == boxTypeATMs[i]){
				check=true;
				break;
			}
			
		}
		if (value==null||value=="") {
			$("#boxStatusGroup").hide();
			$("#carStatusGroup").hide();
			$("#officeGroup").show();
			$("#atmStatusGroup").hide();
			$("#allStatusGroup").show();
		}else if (check) {
			$("#boxStatusGroup").hide();
			$("#carStatusGroup").hide();
			$("#officeGroup").show();
			$("#atmStatusGroup").show();
			$("#allStatusGroup").hide();
		/* } else if (value == boxTypeCar) {
			$("#boxStatusGroup").hide();
			$("#officeGroup").hide();
			$("#carStatusGroup").show();
			$("#atmStatusGroup").hide(); */
		} else if (value == boxTypeBill || value == boxTypeBill2
				|| value == boxTypeBill3) {
			$("#boxStatusGroup").show();
			$("#officeGroup").show();
			$("#carStatusGroup").hide();
			$("#atmStatusGroup").hide();
			$("#allStatusGroup").hide();
		} else {
			$("#boxStatusGroup").show();
			$("#carStatusGroup").hide();
			$("#officeGroup").show();
			$("#atmStatusGroup").hide();
			$("#allStatusGroup").hide();
		}
		/* end */
	}
	/* 追加查看箱袋明细详情 修改人：xp 修改时间：2017-7-5 begin */
	function showDetail(boxNo) {
		//var content = "iframe:${ctx}/store/v01/stoBoxInfo/getStoBoxDetail?boxNo ="+$(this).parent().siblings(1).text();
		var content = "iframe:${ctx}/store/v01/stoBoxInfo/getStoBoxDetail?boxNo="+boxNo;
		top.$.jBox.open(
				content,
				"查看详情", 800, 700, {
					buttons : {
						//关闭
						"<spring:message code='common.close' />" : true
					},
					loaded : function(h) {
						$(".jbox-content", top.document).css(
								"overflow-y", "hidden");
					}
		});
	}
	/* end */
	/* 追加修改箱袋状态修改人：xp 修改时间：2017-7-11 begin */
	/* 追加箱袋类型boxType 修改人：sg 修改时间：2017-11-03 begin */
	function showBoxStatus(boxNo,boxStatus,boxType) {
		var content = "iframe:${ctx}/store/v01/stoBoxInfo/stoBoxInfoShowBoxStatus?boxNo="+boxNo+"&boxStatus="+boxStatus+"&boxType="+boxType;
		/* end */
		top.$.jBox.open(
				content,
				"更改箱袋状态", 270, 500, {
					buttons : {
						//确认
						"<spring:message code='common.confirm' />" : "ok",
						//关闭
						"<spring:message code='common.close' />" : true 
					},
					submit : function(v, h, f) {
						if (v == "ok") {
							var contentWindow = h.find("iframe")[0].contentWindow;
							// 箱号
							var boxNo = contentWindow.document.getElementById("id").value;
							// 箱袋状态
							var boxStatus = contentWindow.document.getElementById("boxStatus").value;
							// 用户名
							var authorizer = contentWindow.document.getElementById("authorizer").value;
							if(authorizer == null || authorizer == ""){
								alert('用户名不能为空');
								return false;
							}
							//密码
							var authorizeBy = contentWindow.document.getElementById("authorizeBy").value;
							if(authorizeBy == null || authorizeBy == ""){
								alert('密码不能为空');
								return false;
							}
							var url = "${ctx}/store/v01/stoBoxInfo/stoBoxInfoUpdateStatus";
								url = url + "?boxNo="+boxNo;
								url = url + "&boxStatus="+boxStatus;
								url = url + "&authorizer="+authorizer;
								url = url + "&authorizeBy="+authorizeBy;
							//$("#cancelReason").removeClass();
							$("#updateForm").attr("action", url);
								$("#updateForm").submit();
								return true;
						}
					},
					loaded : function(h) {
						$(".jbox-content", top.document).css(
								"overflow-y", "hidden");
					}
		});
	}
	/* end */
</script>
<style>
i{margin-right:10px;}
</style>
</head>
<body>
	<ul class="nav nav-tabs">
	
		<li class="active"><a href="${ctx}/store/v01/stoBoxInfo/"> <spring:message
					code="store.boxInfo.List" /></a></li>
					<!-- 屏蔽箱袋添加页面，修改人：xp 修改时间：2017-8-3 begin-->
		<!-- 解开钞箱添加页面的屏蔽，修改人：sg 修改时间：2017-11-3 begin-->
		<shiro:hasPermission name="store:stoBoxInfo:edit">
			<li><a href="${ctx}/store/v01/stoBoxInfo/form"> 
			<spring:message	code="store.rfid.boxAdd" /><spring:message code="common.add" />
			</a></li>
		</shiro:hasPermission>
		<shiro:hasPermission name="store:stoBoxInfo:edit">
			<li><a href="${ctx}/store/v01/stoBoxInfo/formAccurate"> 
			<spring:message	code="store.rfid.boxAccurateInfo" /><spring:message code="common.add" />
			</a></li>
		</shiro:hasPermission>
		<shiro:hasPermission name="store:stoBoxInfo:edit">
			<li><a href="${ctx}/store/v01/stoBoxInfo/formATM"> 
			<spring:message	code="store.rfid.atmBoxAdd" /><spring:message code="common.add" />
			</a></li>
		</shiro:hasPermission>
		<!-- end -->
		<!--  end-->
	</ul>
		<form:form id="updateForm" modelAttribute="updateParam" action="" method="post" >
		
		</form:form>
	<form:form id="searchForm" modelAttribute="stoBoxInfo"
		action="${ctx}/store/v01/stoBoxInfo/list?isSearch=true" method="post"
		class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}" />
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}" />
		<sys:tableSort id="orderBy" name="orderBy" value="${page.orderBy}" callback="page();"/>
		<label><spring:message code="common.boxNo" />：</label>
		<form:input path="searchBoxNo" htmlEscape="false" maxlength="10"
			class="input-small" />
		&nbsp;
		<c:set var="showBoxType" value="${fns:getConfig('sto.box.boxtype.show')}" />
		<label><spring:message code="store.boxType" />：</label>
		<form:select path="boxType" id="boxType" class="input-medium"
			onchange="boxTypeChange(this.value);">
			<form:option value="">
				<spring:message code="common.select" />
			</form:option>
			<form:options items="${fns:getFilterDictList('sto_box_type', true, showBoxType)}" 
					itemLabel="label" itemValue="value" htmlEscape="false" />
		</form:select>
		&nbsp;<label><spring:message code="common.boxStatus" />：</label>
				<c:set var="showCar" value="${fns:getConfig('sto.box.boxstatus.car')} " />
				<!-- 修改钞箱类型的状态 修改人：sg 修改时间：2017-11-06 -->
				<c:set var="showAtmbox" value="${fns:getConfig('sto.box.boxstatus.atmboxs')} " />
				<!-- end -->
				<c:set var="showOther" value="${fns:getConfig('sto.box.boxstatus.other')} " />
		<div class="btn-group-vertical" id="boxStatusGroup">
			<form:select path="boxStatus" class="input-medium">
				<form:option value="">
					<spring:message code="common.select" />
				</form:option>
				<form:options items="${fns:getFilterDictList('sto_box_status',true,'10,11,12,13')}"
					itemLabel="label" itemValue="value" htmlEscape="false" />
			</form:select>
		</div>
		<div class="btn-group-vertical" id="carStatusGroup">
			<form:select path="carStatus" class="input-medium">
				<form:option value="">
					<spring:message code="common.select" />
				</form:option>
				<!--小车只有空闲，在整点室，在库房 -->
				<form:options items="${fns:getFilterDictList('sto_box_status',true,showCar)}"
					itemLabel="label" itemValue="value" htmlEscape="false" />
			</form:select>
		</div>
		<div class="btn-group-vertical" id="atmStatusGroup">
			<form:select path="atmBoxStatus" class="input-medium">
				<form:option value="">
					<spring:message code="common.select" />
				</form:option>
				<!--钞箱 -->
				<!-- 修改钞箱类型显示的状态 修改人：sg 修改时间：2017-11-06 -->
				<form:options items="${fns:getFilterDictList('sto_box_status',true,showAtmbox)}"
					itemLabel="label" itemValue="value" htmlEscape="false" />
				<!-- end -->
			</form:select>
		</div>
		<!-- 所有箱袋状态 修改人：xl 修改日期：2018-01-03 begin -->
		<div class="btn-group-vertical" id="allStatusGroup">
			<form:select path="allBoxStatus" class="input-medium">
				<form:option value="">
					<spring:message code="common.select" />
				</form:option>
				<form:options items="${fns:getFilterDictList('sto_box_status',false,'')}"
					itemLabel="label" itemValue="value" htmlEscape="false" />
			</form:select>
		</div>
		<!-- end -->
		<div class="btn-group-vertical" id="officeGroup">
					<!-- 将使用网点修改成所属机构 修改人：sg 修改日期：2017-11-03 begin -->
			&nbsp;<label><spring:message code="store.ownershipInstitution" />：</label>
					<!-- end -->
			<sys:treeselect id="searchOffice" name="searchOffice.id"
				value="${stoBoxInfo.searchOffice.id}" labelName="searchOffice.name"
				labelValue="${stoBoxInfo.searchOffice.name}" title="机构"
				url="/sys/office/treeData" notAllowSelectRoot="false"
				notAllowSelectParent="false" allowClear="true"
				cssClass="input-medium" checkGroupOffice="true"/>
		</div>	
		<!-- 追加绑定状态查询 修改人：sg 修改日期：2017-11-03 begin -->
		<%-- <div class="btn-group-vertical" id="delFlags">
		<label><spring:message code="store.boxDelflags" />：</label>
			<form:select path="delFlag" class="input-medium">
				<form:option value="">
					<spring:message code="common.select" />
				</form:option>
				<!--绑定状态 -->
				<form:options items="${fns:getFilterDictList('sto_box_delFlag',false,'1')}"
					itemLabel="label" itemValue="value" htmlEscape="false" />
			</form:select>
		</div> --%>
		<!-- end -->	
		&nbsp;<input id="btnSubmit" class="btn btn-primary" type="submit"
			value="<spring:message code="common.search"/>" />
	</form:form>
	<sys:message content="${message}" />
	<table id="contentTable"
		class="table table-hover">
		<thead>
			<tr>
				<th><spring:message code="common.seqNo" /></th>
				<th class="sort-column a.box_no"><spring:message code="common.boxNo" /></th>
				<th class="sort-column a.rfid"><spring:message code="store.rfid" /></th>
				<th class="sort-column a.box_type"><spring:message code="store.boxType" /></th>
				<!-- 追加钞箱类型，将使用网点改为所属机构 修改人：sg 修改日期：2017-11-03 -->
				<th ><spring:message code="store.atmBoxType" /></th>
				<th class="sort-column o5.name"><spring:message code="store.ownershipInstitution" /></th>
				<!-- end -->
				<th class="sort-column a.box_status"><spring:message code="common.boxStatus" /></th>
				<th class="sort-column a.del_flag"><spring:message code="common.bindingStatus" /></th>
				<!-- 删除编辑权限 修改人：xp 修改时间：2017-7-25 begin-->
				<th class="sort-column a.out_date"><spring:message code="common.outTime" /></th>
				<!-- end -->
				<th><spring:message code="common.operation" /></th>
			</tr>
		</thead>
		<tbody>
			<c:forEach items="${page.list}" var="box" varStatus="status">
				<tr>
					<td>${status.index+1}</td>
					<td><a href="${ctx}/store/v01/stoBoxInfo/getStoBoxHistoryGraph?boxNo=${box.id}&rfid=${box.rfid}">${box.id}</a></td>
					<td>${box.rfid}</td>
					<td>${fns:getDictLabel(box.boxType,'sto_box_type',"")}</td>
					<!-- 追加钞箱类型 修改人：sg 修改日期：2017-11-03 -->
					<td>
						${box.atmBoxMod.modName}
					</td>
					<!-- end -->
					<td>${box.office.name}</td>
					<!-- 追加箱袋状态查询 修改人：xp 修改时间：2017-7-10 begin -->
					<shiro:hasPermission name="store:stoBoxInfo:edit"> 
					<!-- 追加箱袋类型和判定绑定状态 修改人：sg 修改时间：2017-11-03 begin -->
					<!-- 如果是绑定状态可以点击进行修改,如果不是小车显示箱袋状态 -->
					<c:if test="${box.boxType!='91'}">
					<c:if test="${box.delFlag==0}">
					<td><a href = "#" onclick="showBoxStatus('${box.id}','${box.boxStatus}','${box.boxType}');javascript:return false;" id = "boxStatus">${fns:getDictLabel(box.boxStatus,'sto_box_status',"")}</a></td>
					</c:if>
					<!-- 如果不是绑定状态不可以点击进行修改 -->
					<c:if test="${box.delFlag!=0}">
					<td>${fns:getDictLabel(box.boxStatus,'sto_box_status',"")}</td>
					</c:if>
					</c:if>
					<!-- 是小车不显示装箱状态 -->
					<c:if test="${box.boxType=='91'}">
					<td></td>
					</c:if>
					<!-- end -->
					</shiro:hasPermission> 
					<shiro:lacksPermission name="store:stoBoxInfo:edit">
					<td>${fns:getDictLabel(box.boxStatus,'sto_box_status',"")}</td>
					</shiro:lacksPermission> 
					<!-- end -->
					<td>${fns:getDictLabel(box.delFlag,'sto_box_delFlag',"")}</td>
					<td><fmt:formatDate value="${box.outDate}" pattern="yyyy-MM-dd" /></td>
					
						<td>
						<!-- 追加钞箱的修改和删除 修改人：sg 修改时间：2017-11-02 begin -->
						<shiro:hasPermission name="store:stoBoxInfo:edit"> 
							<!-- 如果是钞箱或者小车则显示修改和删除 -->
							<%-- <c:if test="${fn:indexOf(fns:getConfig('sto.box.boxtype.atmShow'), box.boxType)!=-1}"> --%>
									<!-- 去掉修改按钮  修改人：xp 修改时间：2017-8-1-->									
									<%-- <c:if test="${box.boxStatus eq fns:getConfig('stoboxinfo.boxStatus.emptyBox')}"> --%>
									<!-- 非空箱状态，不可修改  修改人：xl 修改时间：2017-12-28 begin-->	
									<!-- 修改 -->
								<%-- 	<a href="${ctx}/store/v01/stoBoxInfo/form?id=${box.id}" title="<spring:message code='common.modify'/>"><i class="fa fa-edit text-green fa-lg"></i></a> --%>
									<!-- end-->	
									<!-- 删除 -->
									<a href="${ctx}/store/v01/stoBoxInfo/delete?id=${box.id}"
									onclick="return confirmx('<spring:message code="message.I0001"/>', this.href)" title='<spring:message code="common.delete" />'><i class="fa fa-trash-o text-red fa-lg"></i></i></a>
								<%-- </c:if> --%>
									<!-- 去掉修改按钮  修改人：xp 修改时间：2017-8-1-->
								<%-- <c:if test="${box.boxStatus ne fns:getConfig('stoboxinfo.boxStatus.emptyBox') and fn:contains(fns:getConfig('stoboxinfo.boxUser.type'),fns:getUser().userType)}">
									<a href="${ctx}/store/v01/stoBoxInfo/form?id=${box.id}" title="<spring:message code='common.modify'/>"><i class="fa fa-edit text-green fa-lg"></i></a>
								</c:if> --%>
								<%-- </c:if> --%>
							 </shiro:hasPermission> 
							 <!-- end -->
							<!-- end -->
						<!-- 追加查看箱袋明细详情 修改人：xp 修改时间：2017-7-5 begin -->
						<c:if test="${box.boxStatus != '10' }">
							<a href="#" onclick="showDetail('${box.id}');javascript:return false;" title="查看明细"><i class="fa fa-eye fa-lg"></i></a>
						</c:if>
						</td>
					
					
				</tr>
			</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
</body>
</html>
