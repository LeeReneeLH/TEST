<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title><spring:message code="store.user.manage"/></title>
	<meta name="decorator" content="default"/>
	<script type="text/javascript" src="${ctxStatic}/common/common.js"></script>
	<script type="text/javascript">
		$(document).ready(function() {
			
		});
	</script>
	<style>
	<!-- /* 输入项 */
	.item {
		display: inline;
		float: left;
	}
	/* 清除浮动 */
	.clear {
		clear: both;
	}
	/* 标签宽度 */
	.label_width {
		width: 120px;
	}
	-->
	</style>
</head>
<body>
      <div class="form-horizontal">
		<div class="control-group item" style="margin-top: 20px;">
			<%-- 流水单号 --%>
			<label class="control-label" style="width: 100px"><spring:message code="allocation.allId" />：</label>
			<div class="controls" style="margin-left: 120px">
				<input id="allId" name="allId" type="text" readOnly="readOnly"
					style="width: 170px;" value="${allAllocateInfo.allId}" />
			</div>
		</div>
		<div class="control-group item" style="margin-top: 20px;">
			<%-- 业务类型 --%>
			<label class="control-label"><spring:message code="clear.task.business.type" />：</label>
			<div class="controls" >
				<input id="businessType" name="businessType" type="text"
					readOnly="readOnly" style="width: 170px;"
					value="${fns:getDictLabel(allAllocateInfo.businessType, 'all_businessType', '')}" />
			</div>
		</div>
	 </div>
	 <div class="row">
		<c:forEach items="${stoEscortList}" var="stoEscort" varStatus="status">
		<c:if test="${status.count%2!=0}">
		
		<div class="form-horizontal span4" style="width: 350px">
			<%-- 人员 --%>
			<b style="margin-left: 20px">人员${status.count}</b>
			<div class="clear"></div>
			<!-- 照片 -->
			<div class="control-group item">
				<label class="control-label">照片：</label>
					
				<div class="controls" style="width:150px;height: 150px;overflow:hidden;text-align: center;">
					<img src="${ctx}/store/v01/stoEscortInfo/showImage?id=${stoEscort.id}"  style="height: 150px;width:auto;"/>
				</div>
			</div>
			<div class="clear"></div>
		
		<!-- 姓名 -->
		<div class="control-group item">
		
			<label class="control-label"><spring:message code="common.name"/>：</label>
			<div class="controls" >
				<input type="text" value="${stoEscort.escortName}" readOnly="readOnly" style="width: 145px;"/>
			</div>
		</div>
		<div class="clear"></div>
		<!-- 身份证 -->
		<div class="control-group item">
		
			<label class="control-label"><spring:message code="common.idcardNo"/>：</label>
			<div class="controls" >
				<input type="text" value="${stoEscort.idcardNo}" readOnly="readOnly" style="width: 145px;"/>
			</div>
		</div>
		<div class="clear"></div>
		<!-- 电话 -->
		<div class="control-group item">
			<label class="control-label"><spring:message code="common.phone"/>：</label>
			<div class="controls" >
				<input type="text" value="${stoEscort.phone}" readOnly="readOnly" style="width: 145px;"/>
			</div>
		</div>
		<div class="clear"></div>
		<!-- 所属机构 -->
		<div class="control-group item" id="officeDiv">
			<label class="control-label"><spring:message code="common.affiliation"/>：</label>
			<div class="controls" >
			<%-- <input type="text" value="${stoEscort.office.name}" readOnly="readOnly" style="width: 140px;"/>--%>
			<textarea rows="2" readOnly="readOnly" style="width: 145px;overflow-x:hidden;">${stoEscort.office.name}</textarea>
			</div>
		</div>
	</div>
	</c:if>
	
	<c:if test="${status.count%2==0}">
	
		<div class="form-horizontal span4" style="width: 350px">
		
			<h4 style="color:#dc776a;text-align:center;border-bottom:1px solid #eee;margin-bottom:10px;">
			</h4>
			<%-- 人员 --%>
			<b>人员${status.count}</b>
			<div class="clear"></div>
			<!-- 照片 -->
			<div class="control-group" style="position: relative;">
				<label class="control-label" style="position: absolute;">照片：</label>
					
				<div class="controls" style="width:150px;height: 150px;overflow:hidden;text-align: center;">
					<img src="${ctx}/store/v01/stoEscortInfo/showImage?id=${stoEscort.id}" style="height: 150px;width:auto;"/>
				</div>
			</div>
			<div class="clear"></div>
			<!-- 姓名 -->
		<div class="control-group item">
		
			<label class="control-label"><spring:message code="common.name"/>：</label>
			<div class="controls" >
				<input type="text" value="${stoEscort.escortName}" readOnly="readOnly" style="width: 145px;"/>
			</div>
		</div>
		<div class="clear"></div>
		<!-- 身份证 -->
		<div class="control-group item">
		
			<label class="control-label"><spring:message code="common.idcardNo"/>：</label>
			<div class="controls" >
			<!-- 显示人员身份证号  修改人：XL 修改时间：2018-09-06 begin  --> 
			<input type="text" value="${stoEscort.idcardNo}" readOnly="readOnly" style="width: 145px;"/>
			<!-- end -->
			</div>
		</div>
		<div class="clear"></div>
		<!-- 电话 -->
		<div class="control-group item">
			<label class="control-label"><spring:message code="common.phone"/>：</label>
			<div class="controls" >
			<input type="text" value="${stoEscort.phone}" readOnly="readOnly" style="width: 145px;"/>
			</div>
		</div>
		<div class="clear"></div>
		<!-- 所属机构 -->
		<div class="control-group item" id="officeDiv">
			<label class="control-label"><spring:message code="common.affiliation"/>：</label>
			<div class="controls" >
			<%-- <input type="text" value="${stoEscort.office.name}" readOnly="readOnly" style="width: 140px;"/>--%>
			<textarea rows="2" readOnly="readOnly" style="width: 145px;overflow-x:hidden;">${stoEscort.office.name}</textarea>
			</div>
		</div>
	</div>
	
	</c:if>
	</c:forEach>
	</div>
</body>
</html>
