<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<!-- 标题：物品关联 -->
	<title><spring:message code="store.goodsRelevance" /></title>
	<meta name="decorator" content="default"/>
	<script type="text/javascript" src="${ctxStatic}/common/common.js"></script>
	<script type="text/javascript">
		$(document).ready(function() {
			
			// 加载select2下拉列表选项用
			//function format(item) { return item.label; }
			
			// 页面初始化时，判断是否显示套别
			setSets();
			
			// 页面初始化时，设置面值的选项
			//setDenOptions();
			
			// 页面初始化时，设置单位的选项
			//setUnitOptions();
			
			// 币种变化时,判断是否显示套别
			$("#currency").change(setSets);
			
			// 币种变化时,设置面值选项
			/* $("#currency").change(function(){
				// 设置面值选项
				setDenOptions();
				// 清除面值选中项
				$('#denominationSel').select2("val","");
			}); */
			
			// 材质变化时,设置面值选项
			/* $("#cash").change(function(){
				// 设置面值选项
				setDenOptions();
				// 清除面值选中项
				$('#denominationSel').select2("val","");
				// 设置单位选项
				setUnitOptions();
				// 清除单位选中项
				$('#unitSel').select2("val","");
			}); */
			
			// 设置套别是否可用
			function setSets(){
				var currency = $("#currency").val();
				if(currency == "${fns:getConfig('sto.relevance.currency.cny')}"){
					// 人民币的场合，显示“套别”
					$("#setsGroup").show();
					$("#sets").removeAttr('disabled');
				}else{
					// 其他币种的场合，不显示“套别”
					$("#setsGroup").hide();
					$("#sets").attr('disabled','disabled');
				}
			}
			
			// 设置面值的选项
/* 			function setDenOptions(){
				var currency = $("#currency").val();
				var cash = $("#cash").val(); 
				var url = '${ctx}/store/v01/stoRelevance/getDenOptions';
				var stoRelevance = {};
				stoRelevance.currency = currency;
				stoRelevance.cash = cash;
				$.ajax({
					type : "POST",
					dataType : "json",
					url : url,
					data : {
						param : JSON.stringify(stoRelevance)
					},
					success : function(serverResult, textStatus) {
						$('#denominationSel').select2({
							data:{ results: serverResult, text: 'label' },
							multiple: true,
							formatSelection: format,
							formatResult: format
						});
					},
					error : function(XmlHttpRequest, textStatus, errorThrown) {
						// TODO
						alert("error")
					}
				});
			} */
			
			// 设置单位的选项
			/* function setUnitOptions(){
				var cash = $("#cash").val(); 
				var url = '${ctx}/store/v01/stoRelevance/getUnitOptions';
				var stoRelevance = {};
				stoRelevance.cash = cash;
				$.ajax({
					type : "POST",
					dataType : "json",
					url : url,
					data : {
						param : JSON.stringify(stoRelevance)
					},
					success : function(serverResult, textStatus) {
						$('#unitSel').select2({
							data:{ results: serverResult, text: 'label' },
							multiple: true,
							formatSelection: format,
							formatResult: format
						});
					},
					error : function(XmlHttpRequest, textStatus, errorThrown) {
						// TODO
						alert("error")
					}
				});
			} */
		});
	</script>
</head>
<body>
	<!-- 标签页 -->
	<ul class="nav nav-tabs">
		<!-- 物品关联列表 -->
		<li class="active">
			<a href="${ctx}/store/v01/stoRelevance/">
				<spring:message code="store.goodsRelevance" /><spring:message code="common.list" />
			</a>
		</li>
		<!-- 物品关联添加 -->
		<shiro:hasPermission name="store:stoRelevance:edit">
		<li>
			<a href="${ctx}/store/v01/stoRelevance/form">
				<spring:message code="store.goodsRelevance" /><spring:message code="common.add" />
			</a>
		</li>
		</shiro:hasPermission>
	</ul>
	<form:form id="searchForm" modelAttribute="stoRelevance"
	 action="${ctx}/store/v01/stoRelevance/list?isSearch=true" method="post"
	 class="breadcrumb form-search" style="height: auto;">
		<!-- 分页参数 -->
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		<sys:tableSort id="orderBy" name="orderBy" value="${page.orderBy}" callback="page();"/>
		<!-- 查询条件 -->
		<!-- 币种 -->
		<label><spring:message code="common.currency" />：</label>
		<form:select path="currency" id="currency" class="input-medium">
			<form:option value="">
				<spring:message code="common.select" />
			</form:option>
			<form:options items="${sto:getGoodDictList('currency')}"
				itemLabel="label" itemValue="value" htmlEscape="false"/>
		</form:select>
		<!-- 类别 -->
		&nbsp;<label><spring:message code="common.classification" />：</label>
		<form:select path="classification" id="classification" class="input-medium">
			<form:option value="">
				<spring:message code="common.select" />
			</form:option>
			<form:options items="${sto:getGoodDictList('classification')}"
				itemLabel="label" itemValue="value" htmlEscape="false"/>
		</form:select>
		<!-- 换行 -->
		<!-- <div style="height: 10px"></div> -->
		<!-- 套别 -->
		<div id="setsGroup" style="display:inline;">
		&nbsp;<label><spring:message code="common.edition" />：</label>
		<form:select path="sets" id="sets" class="input-medium">
			<form:option value="">
				<spring:message code="common.select" />
			</form:option>
			<form:options items="${sto:getGoodDictList('edition')}"
				itemLabel="label" itemValue="value" htmlEscape="false"/>
		</form:select>
		</div>
		<!-- 材质 -->
		&nbsp;<label><spring:message code="common.cash" />：</label>
		<form:select path="cash" id="cash" class="input-medium">
			<form:option value="">
				<spring:message code="common.select" />
			</form:option>
			<form:options items="${sto:getGoodDictList('cash')}"
				itemLabel="label" itemValue="value" htmlEscape="false"/>
		</form:select>

		<!-- 面值 -->
		<%-- <label><spring:message code="common.denomination" />：</label>
		<form:input path="denomiList" id="denominationSel" class="input-large"/> --%>
	    <!-- 单位 -->
		<%-- &nbsp;<label><spring:message code="common.units" />：</label>
		<form:input path="unitList" id="unitSel" class="input-large"/> --%>
		<!-- 查询 -->
		&nbsp;<input id="btnSubmit" class="btn btn-primary" type="submit" value="<spring:message code="common.search" />"/>
		<div class="clearfix"></div>
	</form:form>
	<!-- 消息 -->
	<sys:message content="${message}"/>
	<!-- 列表 -->
	<table id="contentTable" class="table table-hover">
		<thead>
			<tr>
				<!-- 序号 -->
				<th><spring:message code="common.seqNo" /></th>
				<!-- 币种 -->
				<th class="sort-column CURRENCY_NAME"><spring:message code="common.currency" /></th>
				<!-- 类别 -->
				<th class="sort-column CLASSIFICATION_NAME"><spring:message code="common.classification" /></th>
				<!-- 套别 -->
				<th class="sort-column SETS_NAME"><spring:message code="common.edition" /></th>
				<!-- 材质 -->
				<th class="sort-column CASH_NAME"><spring:message code="common.cash" /></th>
				<!-- 操作 -->
				<shiro:hasPermission name="store:stoRelevance:edit">
				<th><spring:message code="common.operation" /></th>
				</shiro:hasPermission>
			</tr>
		</thead>
		<tbody>
		<c:forEach items="${page.list}" var="stoRelevance" varStatus="status">
			<tr>
				<td>${status.index+1}</td>
				<td>${stoRelevance.currencyName}</td>
				<td>${stoRelevance.classificationName}</td>
				<td>${stoRelevance.setsName}</td>
				<td>${stoRelevance.cashName}</td>
				<shiro:hasPermission name="store:stoRelevance:edit">
				<td>
					<!-- 修改 -->
    				<a href="${ctx}/store/v01/stoRelevance/form?groupId=${stoRelevance.groupId}" title="编辑">
    					<%-- <spring:message code="common.modify" /> --%><i class="fa fa-edit text-green fa-lg"></i>
    				</a>
    				<!-- 删除 -->
    				<!-- 提示消息：确认要删除吗？ -->
					<a href="${ctx}/store/v01/stoRelevance/delete?groupId=${stoRelevance.groupId}"
						onclick="return confirmx('<spring:message code="message.I0001"/>', this.href)" title="删除" style="margin-left:10px;">
						<%-- <spring:message code="common.delete" /> --%><i class="fa fa-trash-o text-red fa-lg"></i>
					</a>
				</td>
				</shiro:hasPermission>
			</tr>
		</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
</body>
</html>
