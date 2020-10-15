<%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Insert title here</title>

<script type="text/javascript">	
	
	function change(num){
		var sheet1 = document.getElementById('normalclear1');
		var sheet2 = document.getElementById('normalclear2');
		var sheet3 = document.getElementById('normalclear3');
		if(num == 1){
		
			sheet1.className = 'active';
			$("#normalclear2").removeClass();
			$("#normalclear3").removeClass();
			$("#planType").val("01");
			return;
		}
		if(num == 2){
		
			sheet2.className = 'active';
			$("#normalclear1").removeClass();
			$("#normalclear3").removeClass();
			$("#planType").val("02");
			return;
			
		}
		if(num == 3){
		
			sheet3.className = 'active';
			$("#normalclear2").removeClass();
			$("#normalclear1").removeClass();
			$("#planType").val("03");
			return;
	
		}

	}
	</script>

</head>
<body>
	<table id="contentTable" class="table  table-hover">
	<thead>
		<tr>
			<!-- 序号 -->
			<th style="text-align: center; width: 50px"><spring:message
					code="common.seqNo" /></th>
			<!-- 工位类型 -->
			<th style="text-align: center"><spring:message
					code="clear.task.positionType" /></th>
			<!-- 员工姓名 -->
			<th style="text-align: center"><spring:message
					code="clear.task.empName" /></th>
			<!-- 分配数量 (捆)-->
			<th style="text-align: center"><spring:message
					code="clear.task.distributionAmount" />(<spring:message code='clear.public.bundle' />)</th>
			<c:forEach items="${sto:getGoodDictListWithFg('cnypden')}" var="item">
				<th style="text-align: center">${item.label}(<spring:message code='clear.public.bundle' />)</th>
			</c:forEach>
			<!-- 总分配数量 (捆)-->
			<th style="text-align: center"><spring:message
					code="clear.task.distributionTotalAmount" />(<spring:message code='clear.public.bundle' />)</th>

		</tr>
		</thead>
		<tbody>
		<c:forEach items="${clTaskMainList}" var="clTaskMain" varStatus="status">
			<tr>
				<td style="text-align: center; width: 50px">${status.index+1}</td>
				<td style="text-align: center">${fns:getDictLabel(clTaskMain.workingPositionType,'clear_working_position_type',"")}</td>
				<td style="text-align: center">${clTaskMain.taskUser.name}</td>
				<td style="text-align: center"><input
						onchange="value=value.replace(/[^0-9|^\\-]/g,'');changeNum('${clTaskMain.taskUser.id}')"
						onkeyup="value=value.replace(/[^0-9|^\\-]/g,'');changeNum('${clTaskMain.taskUser.id}')"
						name="countTotal" id="${clTaskMain.taskUser.id}count" style="width: 60px"
						maxlength="3"></td>
				<c:forEach items="${clTaskMain.clTaskDetailList}" var="item">
					<td id="${item.denomination}${clTaskMain.taskUser.id}" style="text-align: center">${item.totalCount}</td>
				</c:forEach>
				<td style="text-align: center" id="${clTaskMain.taskUser.id}sum">${clTaskMain.totalCount}</td>
			</tr>
		</c:forEach>
		</tbody>
			<tr>
				<!-- 合计 -->
				<th style="text-align: center; width: 50px"><spring:message code='common.total' /></th>
				<th style="text-align: center"></th>
				<th style="text-align: center"></th>
				<th id="totalsum" style="text-align: center">0
				</th>
				
				<c:forEach items="${clTaskMainListSum}" var="item">
					<td id="${item.denomination}totalsum" style="text-align: center">${item.totalCount}</td>
				</c:forEach>
				<th style="text-align: center"id="totalsumnum">${total}</th>
			</tr>
		
	</table>
</body>
</html>