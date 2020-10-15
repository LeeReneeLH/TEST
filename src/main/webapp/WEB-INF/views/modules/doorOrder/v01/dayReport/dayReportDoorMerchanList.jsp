<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>商户日结管理</title>
	<meta name="decorator" content="default"/>
	<script type="text/javascript">
		<%-- 关闭加载动画 --%>
		window.onload=function(){
			window.top.clickallMenuTreeFadeOut();
		}
		$(document).ready(function() {
			$.ajax({
				type : "POST",
				dataType : "json",
				async: false,
				url : "${ctx}/doorOrder/v01/dayReportDoorMerchan/checkSevenCode",
				data : "",
				success : function(data, status) {
					if(!data){
						$("#exportDiv").hide();
					}else{
						$("#exportDiv").show();
					}
				},
				error : function(XmlHttpRequest, textStatus, errorThrown) {
					alertx("系统异常，请联系管理员！");
				}
			});
			//导出
			$("#exportSubmit").on('click',function(){
				//日结主键列表
				var dayReportIds='';
				$('[name="checkdayReportIds"]').each(function () {
					if($(this).prop('checked')){
						dayReportIds+=","+$(this).val();
					}
				});
				if(dayReportIds==''){
					alertx("请选择要导出行！");
					return;
				}
				$("#dayReportIds").val(dayReportIds.substr(1));
				$("#searchForm").prop("action", "${ctx}/doorOrder/v01/dayReportDoorMerchan/exportDayReport");
				$("#searchForm").submit();
				$("#searchForm").prop("action", "${ctx}/doorOrder/v01/dayReportDoorMerchan/");	
			});
			
			//$("#reportDiv").attr("style","visibility:hidden");
			
			//导出银行转账信息
			$("#exportButton").on('click',function(){
				//日结主键列表
				var dayReportIds='';
				$('[name="checkdayReportIds"]').each(function () {
					if($(this).prop('checked')){
						dayReportIds+=","+$(this).val();
					}
				});
				$("#dayReportIds").val(dayReportIds.substr(1));
				$("#searchForm").prop("action", "${ctx}/doorOrder/v01/dayReportDoorMerchan/exportDayReportAccount");
				$("#searchForm").submit();
				$("#searchForm").prop("action", "${ctx}/doorOrder/v01/dayReportDoorMerchan/");		
			});
			
			//汇款
			$("#paySubmit").on('click',function(){
				//日结主键列表
				var dayReportIds='';
				$('[name="checkdayReportIds"]').each(function () {
					if($(this).prop('checked')){
						dayReportIds+=","+$(this).val();
					}
				});
				if(dayReportIds==''){
					alertx("请选择要汇款行！");
					return;
				}
				top.$.jBox.confirm('是否确认汇款？','系统提示',function(v,h,f){
					if(v=='ok'){
						$("#dayReportIds").val(dayReportIds.substr(1));
						$("#searchForm").prop("action", "${ctx}/doorOrder/v01/dayReportDoorMerchan/paidByIds");
						$("#searchForm").submit();
						//resetTip(); //loading();
						loading("请勿刷新页面！正在进行汇款操作，请稍等...");
						top.$('.jbox-body .jbox-icon').css('top','55px');
					}
				},{buttonsFocus:1, closed:function(){
					if (typeof closed == 'function') {
						closed();
					}
				}});
				top.$('.jbox-body .jbox-icon').css('top','55px');
				
			});
			
			//全选
			$("#chooseAll").on('click',function(){
				if($(this).prop('checked')){
					$('[name="checkdayReportIds"]').each(function () {
						$(this).prop("checked",true);
					});
				}else{
					$('[name="checkdayReportIds"]').each(function () {
						$(this).prop("checked",false);
					});
				}
			});
			
			// 传统存款日结
			$("#report").on('click',function(){
				top.$.jBox.confirm('是否确认结算？','系统提示',function(v,h,f){
					if(v=='ok'){
						var createTimeStart = $('[name="createTimeStart"]').val();
						var createTimeEnd = $('[name="createTimeEnd"]').val();
						$("#searchForm").prop("action", "${ctx}/doorOrder/v01/dayReportDoorMerchan/traditionalSaveReport?searchDateStart="+createTimeStart+"&searchDateEnd="+createTimeEnd );
						$("#searchForm").submit();
						loading("请勿刷新页面！正在确认结算记录，请稍等...");
						top.$('.jbox-body .jbox-icon').css('top','55px');
					}
				},{buttonsFocus:1, closed:function(){
					if (typeof closed == 'function') {
						closed();
					}
				}});
				top.$('.jbox-body .jbox-icon').css('top','55px');
			});
			
			// 批量确认
			$("#confirm").on('click',function(){
				//日结主键列表
				var dayReportIds='';
				$('[name="checkdayReportIds"]').each(function () {
					if($(this).prop('checked')){
						dayReportIds+=","+$(this).val();
					}
				});
				if(dayReportIds==''){
					alertx("请选择要确认行！");
					return;
				}
				top.$.jBox.confirm('是否确认记录？','系统提示',function(v,h,f){
					if(v=='ok'){
						$("#dayReportIds").val(dayReportIds.substr(1));
						$("#searchForm").prop("action", "${ctx}/doorOrder/v01/dayReportDoorMerchan/confirm");
						$("#searchForm").submit();
						loading("请勿刷新页面！正在确认结算记录，请稍等...");
						top.$('.jbox-body .jbox-icon').css('top','55px');
					}
				},{buttonsFocus:1, closed:function(){
					if (typeof closed == 'function') {
						closed();
					}
				}});
				top.$('.jbox-body .jbox-icon').css('top','55px');
				
			});
			
		});
		function page(n,s){
			$("#pageNo").val(n);
			$("#pageSize").val(s);
			if(checkMonry() === false) return;
			window.top.clickSelect();
			$("#searchForm").submit();
        	return false;
        }
		
		function confirmy(mess, href, closed){
			top.$.jBox.confirm(mess,'系统提示',function(v,h,f){
				if(v=='ok'){
					if (typeof href == 'function') {
						href();
					}else{
						resetTip(); //loading();
						$("#pay").attr("href",'#');
						loading("请勿刷新页面！正在进行传统存款结算，请稍等...");
						location = href;
					}
				}
			},{buttonsFocus:1, closed:function(){
				if (typeof closed == 'function') {
					closed();
				}
			}});
			top.$('.jbox-body .jbox-icon').css('top','55px');
			return false;
		}
		
		function loading(mess){
			if (mess == undefined || mess == ""){
				mess = "请勿刷新页面！正在汇款，请稍等...";
			}
			resetTip();
			top.$.jBox.tip(mess,'loading',{opacity:0});
		}
		
		//日结类型
		/* function reportChange(){
			var amountType=$("#businessType").val();
			if(amountType==02){
				$("#reportDiv").attr("style","visibility:''");
			}else{
				$("#reportDiv").attr("style","visibility:hidden");
			}
		} */
		
		// 查询提交按钮事件 
		$(document).ready(function(){
			$("#btnSubmit").click(function(){
				if(checkMonry() === false) return;
				window.top.clickSelect();
				$("#searchForm").submit();
			});
			
		});
		
		// 金额范围校验
		function checkMonry() {
			if ($("#reportMoneyStart").val() != ""
					&& $("#reportMoneyEnd").val() != ""
					&& parseFloat($("#reportMoneyStart").val()) > parseFloat($("#reportMoneyEnd").val())) {
				alertx("存款总额范围错误！");
				return false;
			}
		}
		
		// 金额格式转化  
		function moneyFmt(money) {
			if (money.val() == "") {
				return;
			}
			var vv = Math.round((money.val().replace(/\.{2,}/g, ".")).replace(
					/[^\d.]/g, "") * 100) / 100;
			vv += "";
			if (isNaN(parseInt(vv))) {
				money.val("0.00");
			} else if (vv.indexOf(".") > -1) {
				money.val(vv);
			} else {
				money.val(vv + ".00");
			}
		}
		
	$(document).ready(function() {
		$("#saveInfo").on('click',function(){
			var indexArr = []
			//实际日结金额
			var acturalReportAmounts = ''
			//日结主键列表
			var dayReportIds='';
			//当前页面的代付状态列表
			var paidStatuss = '';
			//备注列表
			var remarksList = '';
			try {
				$('[name="acturalReportAmount"]').each(function (index) {
					var currentAmount = $(this).prop('value')
					var reg1 = /^-?(?:\d+|\d{1,3}(?:,\d{3})+)(?:\.\d+)?$/
					if(currentAmount != '' && !reg1.test(currentAmount.replace(/,/g, ""))) {
						throw new Error('输入异常') 
					} else {
						/* if(currentAmount != '' && currentAmount <= 0) {
							throw new Error('金额异常') 
						} */
						var reg2 = /^\d+(\.\d{1,2})?$/
						if(currentAmount != '' && !reg2.test(currentAmount.replace(/,/g, ""))) {
							throw new Error('小数异常') 
						}
					}
					acturalReportAmounts+=","+$(this).val().replace(/,/g, "");
				});
				$('[name="checkdayReportIds"]').each(function (index) {
					dayReportIds+=","+$(this).val();
				});
				$('[name="paidStatus"]').each(function (index) {
					paidStatuss+=","+$(this).val();
				});
				$('[name="remarks"]').each(function(index) {
					remarksList+= ","+$(this).val();
				})
				
				loading('正在提交，请稍等...');
				$("#searchForm").attr("action", "${ctx}/doorOrder/v01/dayReportDoorMerchan/saveActuralReprotAmount?dayReportIds="+dayReportIds + "&acturalReportAmounts=" + acturalReportAmounts + "&paidStatuss=" + paidStatuss + "&remarksList=" + remarksList);
				$("#searchForm").submit();
			} catch(e) {
				if(e.message == '输入异常') {
					top.$.jBox.tip("存在非法输入，请检查输入金额是否都为数字!");
				} else if(e.message == '小数异常') {
					top.$.jBox.tip("存在非法输入，请检查输入金额小数点后为2位!");
				} 
				/* else {
					top.$.jBox.tip("存在非法输入，请检查输入金额是否都大于0!");
				 }*/
				
			}
		});
		bindKeyEvent($('[name="acturalReportAmount"]'));
		//add by guojian 2020-05-09 end
		checkRemarks($('[name="remarks"]'));
	});
	
	//add by guojian 2020-05-09 start
	function bindKeyEvent(obj){
		obj.keyup(function () {
			var value = $(this).val().replace(/,/g, "")
			if(value.split(".")[0].length <= 10) {
				var reg = value.match(/\d+\.?\d{0,2}/);
		        var txt = '';
		        if (reg != null) {
		            txt = reg[0];
		        }
		        $(this).val(txt);
			} else {
				var value2 = value.slice(0,10)
				var reg2 = value2.match(/\d+\.?\d{0,2}/);
				var txt2 = '';
		        if (reg2 != null) {
		            txt2 = reg2[0];
		        }
				$(this).val(txt2);
			}
	    }).change(function () {
	        $(this).keypress();
	        var v = $(this).val();
	        var txt = '';
	        if (/\.$/.test(v))
	        {
	            $(this).val(v.substr(0, v.length - 1));
	        }
	    });
	}
	//add by guojian 2020-05-09 end
	//add by guojian 2020-07-22 start
	function checkRemarks(obj) {
		obj.keyup(function () {
			var value = $(this).val().replace(/,/g, "")
			var reg = value.match(/^[a-zA-Z0-9\u4e00-\u9fa5\.]+/);
	        var txt = '';
	        if (reg != null) {
	            txt = reg[0];
	        }
	        $(this).val(txt);
	    })
	}
	//add by guojian 2020-07-22 end
	
	</script>
</head>
<body>
	<ul class="nav nav-tabs">   
		<shiro:hasPermission name="doorOrder:v01:dayReportDoorMerchan:view"><li class="active"><a onclick="window.top.clickSelect();" href="${ctx}/doorOrder/v01/dayReportDoorMerchan/"><spring:message code='door.accountManage.merchanReportList'/></a></li></shiro:hasPermission>
		<shiro:hasPermission name="doorOrder:v01:dayReportDoorError:view"><li><a onclick="window.top.clickSelect();" href="${ctx}/doorOrder/v01/dayReportDoorMerchan/errorList"><spring:message code='door.accountManage.errorReportList'/></a></li></shiro:hasPermission>
		<%-- <shiro:hasPermission name="doorOrder:v01:centeraccounts:view"><li><a href="${ctx}/doorOrder/v01/dayReportMain/">账务日结列表</a></li></shiro:hasPermission> --%>
	</ul>
	<form:form id="searchForm" modelAttribute="dayReportDoorMerchan" action="${ctx}/doorOrder/v01/dayReportDoorMerchan/?uninitDateFlag=0" method="post" class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		<input id="dayReportIds" name="dayReportIds" type="hidden"/>
		<sys:tableSort id="orderBy" name="orderBy" value="${page.orderBy}" callback="page();"/>
		<div class="row search-flex">
		<div>
		<!-- 开始日期 -->
		<label><spring:message code="common.startDate" />：</label>
		<!--  清分属性去除 wzj 2017-11-15 begin -->
		<input id="createTimeStart"  name="createTimeStart" type="text" readonly="readonly" maxlength="20" class="input-small Wdate createTime" 
			value="<fmt:formatDate value="${dayReportDoorMerchan.createTimeStart}" pattern="yyyy-MM-dd HH:mm"/>" style="padding-right: 20px;"
			onclick="WdatePicker({isShowClear:true,readOnly:true,startDate:'%y-%M-01 00:00:00',dateFmt:'yyyy-MM-dd HH:mm',realTimeFmt:'HH:mm',maxDate:'#F{$dp.$D(\'createTimeEnd\')||\'%y-%M-%d\'}'});"/>
		<!-- end -->
		</div>
		<div>
		<!-- 结束日期 -->
		<label><spring:message code="common.endDate" />：</label>
		<!--  清分属性去除 wzj 2017-11-15 begin -->
		<input id="createTimeEnd" name="createTimeEnd" type="text" readonly="readonly" maxlength="20" class="input-small Wdate createTime" 
			value="<fmt:formatDate value="${dayReportDoorMerchan.createTimeEnd}" pattern="yyyy-MM-dd HH:mm"/>" style="padding-right: 20px;"
			onclick="WdatePicker({isShowClear:true,readOnly:true,startDate:'%y-%M-01 00:00:00',dateFmt:'yyyy-MM-dd HH:mm',realTimeFmt:'HH:mm',minDate:'#F{$dp.$D(\'createTimeStart\')}',maxDate:'%y-%M-%d'});"/>&nbsp;
		</div>
		<div>
		<%-- 门店 --%>
		<label><spring:message code="door.public.cust" /> ：</label>
		<sys:treeselect id="officeName" name="officeId"
			value="${dayReportDoorMerchan.officeId}" labelName="officeName"
			labelValue="${dayReportDoorMerchan.officeName}" title="<spring:message code='door.public.cust' />"
			url="/sys/office/treeData" cssClass="required input-small"
			notAllowSelectParent="false" notAllowSelectRoot="false" minType="8" maxType="9"
			isAll="true"  allowClear="true" checkMerchantOffice="true" clearCenterFilter="true"/>
		</div>
		<div>
		<!-- 代付状态 -->
		<label><spring:message code="door.accountManage.payStatus"/>：</label>
		<form:select path="paidStatus" class="input-medium required">
		<form:option value=""><spring:message code="common.select" /></form:option>				
		<form:options items="${fns:getFilterDictList('deposit_payment_status',true,'0,1,2')}" 
			itemLabel="label" itemValue="value" htmlEscape="false" />
		</form:select>
		</div>
		<div>
		<!-- 结算类型 -->
		<label><spring:message code="door.accountManage.reportType"/>：</label>
		<form:select path="settlementType" id="businessType" class="input-medium required" onchange="reportChange()">		
			<form:options items="${fns:getFilterDictList('report_type',true,'01,03,04')}" 
								itemLabel="label" itemValue="value" htmlEscape="false" />
		</form:select>
		</div>
		&nbsp;&nbsp;
		<%-- 业务备注   zxk 2020-4-16 begin--%>
		<div>
			<label><spring:message code="door.accountManage.remarks" />：</label>
			<form:input path="sevenCode" htmlEscape="false" maxlength="7"
				class="input-small" />
		</div>
		<!-- end -->
		&nbsp;&nbsp;
		<%-- 日结金额筛选 zxk 2020-4-16 begin--%>
		<div>
			<label><spring:message code="door.accountManage.saveMoneyTotal"/>：</label>
			<form:input  path="reportMoneyStart" htmlEscape="false" maxlength="13" class="input-small" style="text-align:right;padding-right: 20px;"
			onBlur="moneyFmt($(this))"  onkeyup="value=value.replace(/[^0-9//./,]/g,'');" placeholder="0.00"/>
		</div>
		<div>
			<label>~</label>
			<form:input  path="reportMoneyEnd"  htmlEscape="false" maxlength="13" class="input-small" style="text-align:right;padding-right: 20px;"
             onBlur="moneyFmt($(this))"  onkeyup="value=value.replace(/[^0-9//./,]/g,'');" placeholder="9999999999.99"/>
		</div>
		<!-- end -->
		&nbsp;&nbsp;
		<div>
		<input id="btnSubmit" class="btn btn-primary" type="button" value="<spring:message code="common.search"/>"/>
		</div>
		&nbsp;&nbsp;
		<!-- 确认 -->
		<shiro:hasPermission name="doorOrder:v01:dayReportDoorMerchan:confirm">
			<div>
				<input id="confirm" class="btn btn-default" type="button" value="<spring:message code="common.confirm"/>" />
			</div>
			&nbsp;&nbsp;
		</shiro:hasPermission>
		<!-- 导出 -->
		<div>
		<input id="exportSubmit"  class="btn btn-red" type="button" value="<spring:message code='common.export'/>" />
		</div>
		&nbsp;&nbsp;
		<!-- 转账导出 -->
		<div id="exportDiv">
			<input id="exportButton"  class="btn btn-red" type="button" value="<spring:message code='door.accountManage.bankExport'/>" title = "<spring:message code='door.accountManage.bankExportTitle'/>"/>
			&nbsp;&nbsp;
		</div>
		<!-- 汇款 -->
		<div>
		<c:if test="${fns:getUser().userType=='21'&&fns:getUser().getOffice().type=='6'}">
			<input id="paySubmit" class="btn btn-red" type="button" value="<spring:message code="door.accountManage.paid"/>" />
			&nbsp;&nbsp;
		</c:if>
		</div>
		<!-- 保存实际日结金额 -->
		<shiro:hasPermission name="doorOrder:v01:dayReportActuralAmout:view">
			<div>
				<input id="saveInfo" class="btn btn-red" type="button" maxlength="200" value="<spring:message code="door.accountManage.save"/>" />
			</div>
			&nbsp;&nbsp;
		</shiro:hasPermission>
		<!-- 传统存款手动结算(只有清分中心管理员可见) -->
		<div>
		<c:if test="${fns:getUser().userType=='21'&&fns:getUser().getOffice().type=='6'}">
		<%-- <a href="${ctx}/doorOrder/v01/dayReportDoorMerchan/traditionalSaveReport?createTimeStart=${dayReportDoorMerchan.createTimeStart}&createTimeEnd=${dayReportDoorMerchan.createTimeEnd}" 
		onclick="return confirmy('<spring:message code="message.A1003"/>', this.href)"> --%>
		<input id="report" class="btn btn-primary" type="button" value="<spring:message code="door.accountManage.traditionalSettlement"/>"/>
		<!-- </a> -->
		</c:if>  
		</div>
		<!-- 电子线下存款手动结算(只有清分中心管理员可见) -->
		<%-- <div class="control-group">
		<c:if test="${fns:getUser().userType=='21'&&fns:getUser().getOffice().type=='6'}">
		<a href="${ctx}/doorOrder/v01/dayReportDoorMerchan/offlineSaveReport" onclick="return confirmx('<spring:message code="message.A1003"/>', this.href)"><input id="report" class="btn btn-primary" type="button" value="电子线下结算"/></a>
		</c:if>  
		</div> --%>
		</div>
	</form:form>
	<sys:message content="${message}"/>
	<div class="table-con">
	<table id="contentTable" class="table table-hover">
		<thead>
			<tr>
				<th><input type="checkbox" name="chooseAll" value="" id="chooseAll"/></th>
			    <!-- 序号 -->
				<th><spring:message code='common.seqNo'/></th>
				<!-- 门店名称 -->
				<th class="sort-column o2.NAME"><spring:message code='door.accountManage.doorName'/></th>
				<!-- 纸币数量 -->
				<th><spring:message code="door.accountManage.paperMoneyCount"/></th>
			    <!--纸币金额(元) -->
				<th><spring:message code="door.accountManage.paperMoneyTotal"/></th>
				<!-- 硬币数量 -->
				<%-- <th><spring:message code="door.accountManage.coinMoneyCount"/></th> --%>
				<!--硬币金额 -->
				<%-- <th><spring:message code="door.accountManage.coinMoneyTotal"/></th> --%>
				<!-- 强制金额(元) -->
				<th><spring:message code="door.accountManage.forceMoney"/></th>
				<!-- 其他金额(元) -->
				<th><spring:message code="door.accountManage.otherMoney"/></th>
				<!--存款总量 -->
				<th><spring:message code="door.accountManage.saveTotal"/></th>
		        <!-- 存款总额-->
		        <th class="sort-column a.total_amount"><spring:message code="door.accountManage.saveMoneyTotal"/></th>
		        <!-- 实际结算金额-->
		        <th class="sort-column a.actural_report_amount"><spring:message code="door.accountManage.acturalReportAmount"/></th>
		        <!-- 备注 --> 
		        <th class="sort-column a.remarks"><spring:message code="door.accountManage.errorRemarks"/></th>
		        <!-- 业务备注 -->
		        <th class="sort-column a.SEVEN_CODE"><spring:message code="door.accountManage.remarks"/></th>
		        <!-- 结算类型 -->
		        <th><spring:message code="door.accountManage.reportType"/></th>
				<!-- 结算时间 -->
				<th class="sort-column a.report_date"><spring:message code="door.accountManage.reportDate"/></th>
				<!-- 结算人 -->
				<th class="sort-column a.rname"><spring:message code="door.accountManage.reportName"/></th>
				<!-- 结算机构 -->
				<th class="sort-column a.roffice_id"><spring:message code="door.accountManage.reportOffice"/></th>
				<!-- 代付状态 -->
				<th class="sort-column a.paid_status"><spring:message code='door.accountManage.payStatus'/></th>
				<!-- 实际付款金额 -->
				<th class="sort-column a.paid_amount"><spring:message code="door.accountManage.paidAmount"/></th>
				<!-- 付款时间 -->
				<th class="sort-column a.paid_date"><spring:message code="door.accountManage.paidTime"/></th>
				<!-- 操作 -->
				<th colspan="2" style="width:70px;"><spring:message code='common.operation'/></th>
			</tr>
		</thead>
		<tbody>
		<c:forEach items="${page.list}" var="dayReportDoorMerchan" varStatus="status">
			<!-- 存款总额与实际日结金额不等行高亮  gzd 2020-07-29 -->
			<tr style="color:${status.index eq 0 ? 'green':''};background-color:${status.index ne 0 && dayReportDoorMerchan.totalAmount != dayReportDoorMerchan.acturalReportAmount ? '#ff94325e' : ''};">
				<td>
					<c:if test="${status.index eq 0}"></c:if>
					<c:if test="${status.index ne 0}">
                    	<input type="checkbox" name="checkdayReportIds" value="${dayReportDoorMerchan.id}"/>
                    	<input type="hidden" name="paidStatus" value="${dayReportDoorMerchan.paidStatus}"/>
                    </c:if>
				</td>
				<td>
                     <c:if test="${status.index eq 0}"></c:if>
                     <c:if test="${status.index ne 0}">
                      ${status.index}
                     </c:if>
                </td>
				<td>${dayReportDoorMerchan.officeName}</td>
				
				<!-- 结算类型为存款时，显示如下部分 -->
				<td>${dayReportDoorMerchan.paperMoneyCount}</td>
				<td>
					<c:choose>
						<c:when test="${dayReportDoorMerchan.paperMoneyTotal==null}"><fmt:formatNumber value="0" pattern="#,##0.00#" /></c:when>
						<c:otherwise>
								<fmt:formatNumber value="${dayReportDoorMerchan.paperMoneyTotal}" pattern="#,##0.00#" />
						</c:otherwise>
					</c:choose>
				</td>
				<%-- <td>${dayReportDoorMerchan.coinMoneyCount}</td>
				<td>
					<c:choose>
						<c:when test="${dayReportDoorMerchan.coinMoneyTotal==null}"><fmt:formatNumber value="0" pattern="#,##0.00#" /></c:when>
						<c:otherwise><fmt:formatNumber value="${dayReportDoorMerchan.coinMoneyTotal}" pattern="#,##0.00#" /></c:otherwise>
					</c:choose>
				</td> --%>
				<td>
					<c:choose>
						<c:when test="${dayReportDoorMerchan.pack==null}"><fmt:formatNumber value="0" pattern="#,##0.00#" /></c:when>
						<c:otherwise><fmt:formatNumber value="${dayReportDoorMerchan.pack}" pattern="#,##0.00#" /></c:otherwise>
					</c:choose>
				</td>
				<td>
					<c:choose>
						<c:when test="${dayReportDoorMerchan.other==null}"><fmt:formatNumber value="0" pattern="#,##0.00#" /></c:when>
						<c:otherwise><fmt:formatNumber value="${dayReportDoorMerchan.other}" pattern="#,##0.00#" /></c:otherwise>
					</c:choose>
				</td>
				<td>${dayReportDoorMerchan.saveTotal}</td>
				<td><fmt:formatNumber value="${dayReportDoorMerchan.totalAmount}" pattern="#,##0.00#" /></td>
				<%-- <td><fmt:formatNumber value="${dayReportDoorMerchan.acturalReportAmount}" pattern="#,##0.00#" /></td> --%>
				<td style="text-align:center;">
					<c:if test="${status.index eq 0}"><fmt:formatNumber value="${dayReportDoorMerchan.acturalReportAmount}" pattern="#,##0.00#" /></text></c:if>
					<c:if test="${status.index ne 0}">
						<c:choose>
							<c:when test="${dayReportDoorMerchan.paidStatus == 1}">
								<input name="acturalReportAmount" type="text" value="<fmt:formatNumber value="${dayReportDoorMerchan.acturalReportAmount}" pattern="#,##0.00#" />" style="width:100%;margin:0;padding:0;text-align:center;" maxlength="13" />
							</c:when>
							<c:otherwise>
								<input name="acturalReportAmount" type="text" value="<fmt:formatNumber value="${dayReportDoorMerchan.acturalReportAmount}" pattern="#,##0.00#" />" style="width:100%;margin:0;padding:0;text-align:center;" readonly="readonly" />
							</c:otherwise>
						</c:choose>
					</c:if>
				</td>
				<td>
					<c:if test="${status.index ne 0}">
						<input name="remarks" type="text" value="${dayReportDoorMerchan.remarks}"  style="width:100%;margin:0;padding:0;text-align:center;" onmouseover="this.title=this.value" maxlength="200"/>
					</c:if>		
				</td>
				<td>${dayReportDoorMerchan.sevenCode}</td>
				<td>${fns:getDictLabel(dayReportDoorMerchan.settlementType,'report_type',"")} </td>
				<td><fmt:formatDate value="${dayReportDoorMerchan.reportDate}" pattern="yyyy-MM-dd HH:mm:ss"/></td>
				<td>${dayReportDoorMerchan.rname}</td>
				<td>${dayReportDoorMerchan.rofficeName}</td>
				<!-- 判断结算类型，存款结算显示未代付，待确认，已代付。差错结算显示已处理，未处理 -->
				<%-- <c:if test="${dayReportDoorMerchan.paidStatus eq '0'}">
				<td style="color: green">
					${fns:getDictLabel(dayReportDoorMerchan.paidStatus,'deposit_payment_status',"")}
				</td>
				</c:if>
				<c:if test="${dayReportDoorMerchan.paidStatus eq '1'}">
				<td style="color: #f39c12">
					${fns:getDictLabel(dayReportDoorMerchan.paidStatus,'deposit_payment_status',"")}
				</td>
				</c:if>
				<c:if test="${dayReportDoorMerchan.paidStatus == null || dayReportDoorMerchan.paidStatus == ''}">
				<td>
				</td>
				</c:if>
				<c:if test="${dayReportDoorMerchan.paidStatus eq '2'}">
				<td style="color: red">
						${fns:getDictLabel(dayReportDoorMerchan.paidStatus,'deposit_payment_status',"")}
				</td>
				</c:if> --%>
				<!-- update hzy 配置颜色  2020/05/26 start -->
				<td>${fns:getDictLabelWithCss(dayReportDoorMerchan.paidStatus, 'deposit_payment_status', '',true)}</td>
				<!-- update hzy 配置颜色  2020/05/26 end -->
				<td><fmt:formatNumber value="${dayReportDoorMerchan.paidAmount}" pattern="#,##0.00#" /></td>
				<td><fmt:formatDate value="${dayReportDoorMerchan.paidDate}" pattern="yyyy-MM-dd HH:mm:ss"/></td>
				<td style="width:70px;">
				<c:if test="${status.index ne 0}">
				<a href="${ctx}/doorOrder/v01/dayReportDoorMerchan/detailView?reportId=${dayReportDoorMerchan.reportId}&clientId=${dayReportDoorMerchan.officeId}&settlementType=${dayReportDoorMerchan.settlementType}" title = "<spring:message code='common.view'/>">
					<i class="fa fa-eye fa-lg"></i>
				</a>
				</c:if>
				</td>
			</tr>
		</c:forEach>
		</tbody>
	</table>
	</div>
	<div class="pagination">${page}</div>
</body>
</html>