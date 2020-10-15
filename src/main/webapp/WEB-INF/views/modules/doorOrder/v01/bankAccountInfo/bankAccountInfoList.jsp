<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>银行卡管理管理</title>
	<meta name="decorator" content="default"/>
	<script type="text/javascript">
		$(function() {
			makeProOption();
			var provinceCode = $("#provinceCode").val();
			//省份有值时城市下拉菜单初始化，无值时城市下拉菜单无值（查询时用）
			if (provinceCode == '') {
				$('#cityName').select2({
					containerCss : {
						width : '163px',
						display : 'inline-block'
					},
					data : {
						results : [],
						text : 'text'
					},
					//请选择
					placeholder : "<spring:message code='common.select'/>",
					formatSelection : format,
					formatResult : format
				});
			} else {
				makeCityOptionName();
			}
			
			//选择省份时初始化城市下拉菜单
			$("#provinceCode").on("change", function() {
				var provinceCode = $("#provinceCode").val();
				if (provinceCode != '') {
					//省份栏有值刷新城市下拉菜单
					$('#cityName').val("");
					makeCityOptionName();
				} else {
					//省份栏无值清空城市下拉菜单
					$('#cityName').val("");
					$('#cityName').select2({
						containerCss : {
							width : '163px',
							display : 'inline-block'
						},
						data : {
							results : [],
							text : 'text'
						},
						//请选择
						placeholder : "<spring:message code='common.select'/>",
						formatSelection : format,
						formatResult : format
					});
				}

			});
		});

		function page(n,s){
			$("#pageNo").val(n);
			$("#pageSize").val(s);
			$("#searchForm").submit();
        	return false;
        }
		
		//省份下拉菜单作成(实体类对象用省份编码接收)
		function makeProOption() {
			var url = "${ctx}/sys/province/getSelect2ProData";
			$.ajax({
				type : "POST",
				dataType : "json",
				url : url,
				success : function(data) {
					// 1、作成省份的选项
					$('#provinceCode').select2({
						containerCss : {
							width : '163px',
							display : 'inline-block'
						},
						data : {
							results : data,
							text : 'text'
						},
						//请选择
						placeholder : "<spring:message code='common.select'/>",
						allowClear : true,
						formatSelection : format,
						formatResult : format
					});
				},

				error : function() {
					// 系统内部异常，请稍后再试或与系统管理员联系
					alertx("<spring:message code='message.E0101'/>")
				}
			});
		}

		//城市下拉菜单作成(实体类对象用城市编码接收)
		function makeCityOptionName() {
			var url = "${ctx}/sys/city/getSelect2CityDataName";
			var provinceCode = $("#provinceCode").val();

			$.ajax({
				type : "POST",
				dataType : "json",
				url : url,
				data : {
					"provinceCode" : provinceCode
				},

				success : function(data) {
					// 1、作成城市的选项
					$('#cityName').select2({
						containerCss : {
							width : '163px',
							display : 'inline-block'
						},
						data : {
							results : data,
							text : 'text'
						},
						//请选择
						placeholder : "<spring:message code='common.select'/>",
						allowClear : true,
						formatSelection : format,
						formatResult : format
					});

				},

				error : function() {
					// 系统内部异常，请稍后再试或与系统管理员联系
					alertx("<spring:message code='message.E0101'/>")
				}
			});
		}
		/**
		 * 加载select2下拉列表选项用
		 */
		function format(item) {
			return item.text;
		}
	</script>
</head>
<body>
	<ul class="nav nav-tabs">
		<li class="active"><a href="${ctx}/doorOrder/v01/bankAccountInfo/">银行卡信息列表</a></li>
		<shiro:hasPermission name="doorOrder:v01:bankAccountInfo:edit"><li><a href="${ctx}/doorOrder/v01/bankAccountInfo/form">银行卡信息添加</a></li></shiro:hasPermission>
	</ul>
	<form:form id="searchForm" modelAttribute="bankAccountInfo" action="${ctx}/doorOrder/v01/bankAccountInfo/" method="post" class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		
		<label>账户名称：</label>
		<form:input path="accountName" htmlEscape="false" maxlength="24"
			class="input-medium" />
		<!-- 门店 -->
		<label>归属机构 ：</label>
			<c:if test="${fns:getUser().office.type eq '7'}">
				<sys:treeselect id="officeId" name="officeId"
					value="${bankAccountInfo.officeId}" labelName="${sto:getOfficeById(bankAccountInfo.officeId).name}"
					labelValue="${sto:getOfficeById(bankAccountInfo.officeId).name}"
					title="<spring:message code='door.public.cust' />"
					url="/sys/office/treeData"
					cssClass="input-xsmall"
					notAllowSelectParent="false" notAllowSelectRoot="false"
					isAll="false" allowClear="true" checkMerchantOffice="false"/>
			</c:if>
			<c:if test="${fns:getUser().office.type ne '7'}">
				<sys:treeselect id="officeId" name="officeId"
					value="${bankAccountInfo.officeId}" labelName="${sto:getOfficeById(bankAccountInfo.officeId).name}"
					labelValue="${sto:getOfficeById(bankAccountInfo.officeId).name}"
					title="<spring:message code='door.public.cust' />"
					url="/sys/office/treeData"
					cssClass="input-xsmall"
					notAllowSelectParent="false" notAllowSelectRoot="false" minType="9"
					maxType="9" isAll="true" allowClear="true"
					checkMerchantOffice="false" clearCenterFilter="true" />
			</c:if>
			&nbsp;&nbsp;
		<label>所属省份：</label>
		<form:input type="text" path="provinceCode" maxlength="50" class="input-medium" htmlEscape="false"/>
		
		<label>城市名称：</label>
		<form:input path="cityName" htmlEscape="false" maxlength="50" class="input-medium"/>
		
		<label>绑定状态：</label>
		<form:select path="status"  style="width:120px">
			<form:option value="">
				<spring:message code="common.select" />
			</form:option>
			<form:options
				items="${fns:getFilterDictList('BANK_ACCOUNT_STATUS',true,'0,1')}"
				itemLabel="label" itemValue="value" htmlEscape="false" />
		</form:select> 
		&nbsp;&nbsp;
			<li class="btns"><input id="btnSubmit" class="btn btn-primary" type="submit" value="查询"/></li>
			
	</form:form>
	<sys:message content="${message}"/>
	<table id="contentTable" class="table table-hover">
		<thead>
			<tr>
			    <!-- 序号 -->
				<th><spring:message code='door.bankAccountInfo.indexCode' /></th>
				<th><spring:message code="door.bankAccountInfo.accountNo"/></th>
                <th><spring:message code="door.bankAccountInfo.accountName"/></th>
                <th><spring:message code="door.bankAccountInfo.bankName"/></th>
                <th><spring:message code="door.bankAccountInfo.cityName"/></th>
                <th><spring:message code="door.bankAccountInfo.merchant"/></th>
                <th><spring:message code="door.bankAccountInfo.status"/></th>
				<shiro:hasPermission name="doorOrder:v01:bankAccountInfo:edit"><th>操作</th></shiro:hasPermission>
			</tr>
		</thead>
		<tbody>
		<c:forEach items="${page.list}" var="bankAccountInfo" varStatus="status">
			<tr>
			    <td>${status.index+1}</td>
                <td>${bankAccountInfo.accountNo}</td>
                <td>${bankAccountInfo.accountName}</td>
                <td>${fns:getDictLabel(bankAccountInfo.bankName,'bank_company',"")}</td>
                <td>${bankAccountInfo.cityName}</td>
                <%-- 显示商户名称 --%>
				<td>${sto:getOfficeById(bankAccountInfo.officeId).name}</td>
				<td>${fns:getDictLabel(bankAccountInfo.status,'BANK_ACCOUNT_STATUS',"")}</td>
				<shiro:hasPermission name="doorOrder:v01:bankAccountInfo:edit"><td>
				    <a href="${ctx}/doorOrder/v01/bankAccountInfo/form?id=${bankAccountInfo.id}&pageFlag=modify" title="<spring:message code='common.modify' />"><i class="fa fa-edit text-green fa-lg"></i></a>
					<a href="${ctx}/doorOrder/v01/bankAccountInfo/delete?id=${bankAccountInfo.id}" onclick="return confirmx('确认要删除该银行卡吗？', this.href)" title="<spring:message code='common.delete' />"  style="margin-left:10px;"><i class="fa fa-trash-o text-red fa-lg"></i></a>
                    <c:if test="${bankAccountInfo.status eq '0'}">
				    	<span style='width:35px;display:inline-block;'> 
							<%-- 绑定 --%>
							<a href="${ctx}/doorOrder/v01/bankAccountInfo/changeStatus4check?id=${bankAccountInfo.id}" onclick="return confirmx('是否确认绑定？', this.href)" title="<spring:message code='door.equipment.bind' />" style="margin-left:10px;">
								<i class="fa fa-chain text-green fa-lg"></i>
							</a>
						</span>
                    </c:if>
                    <c:if test="${bankAccountInfo.status eq '1'}">
						<span style='width:25px;display:inline-block;'> 
							<%-- 解绑 --%>
							<a  href="${ctx}/doorOrder/v01/bankAccountInfo/changeStatus?id=${bankAccountInfo.id}"
                                onclick="return confirmx('<spring:message code="message.E7203"/>', this.href)" onclick="return confirmx('是否确认解绑？', this.href)" title="<spring:message code='door.equipment.nobind' />" style="margin-left:10px;">
								<i class="fa fa-chain-broken text-red fa-lg"></i>
							</a>
						</span>
                    </c:if>
				</td></shiro:hasPermission>
			</tr>
		</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
</body>
</html>