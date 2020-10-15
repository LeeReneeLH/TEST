<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<!-- 预约清分登记 -->
	<title><spring:message code="clear.bankPay.register" /> </title>
	<meta name="decorator" content="default"/>
	<script type="text/javascript" src="${ctxStatic}/common/common.js"></script>
	<script type="text/javascript" src="${ctxStatic}/jquery/jquery.jqprint-0.3.js"></script>
	<script type="text/javascript">
		$(document).ready(function() {
			//$("#name").focus();
			
			//取得一列的合计值
			var sumValue1 = getSumValue('columnValue1');		//未清分(捆)
			var sumValue2 = getSumValue('columnValue2');		//已清分(捆)
			var sumTotalAmt = getSumValue('totalAmt');			//金额(元)
			//合计值的设定
			$("#daySumValue1Show").html(formatCurrencyNum(sumValue1));
			$("#daySumValue2Show").html(formatCurrencyNum(sumValue2));
			$("#daySumAmountShow").html(formatCurrencyNum(sumTotalAmt));

			
			
			$("#inputForm").validate({

				submitHandler: function(form){
					loading('正在提交，请稍等...');
					form.submit();
				},
				//errorContainer: "#messageBox",
				errorPlacement: function(error, element) {
					//$("#messageBox").text("输入有误，请先更正。");
					if (element.is(":checkbox")||element.is(":radio")||element.parent().is(".input-append")){
						error.appendTo(element.parent().parent());
					} else {
						error.insertAfter(element);
					}
				}
			});

			
			// 保存
			$("#btnSubmit").click(function() {
				
				//页面检查
				var blnChk = chkFrom();
				if (blnChk == false){
					return false;
				}
				/* 将金额去掉逗号 修改人:sg 修改日期: 2017-12-20 begin */
				update(document.getElementById("inAmount"))
				/* end */
				$("#inputForm").submit();
				
			});
		});
		
		
		//页面检查
		function chkFrom() {	
			//-----------总金额检查---------------
			var strInAmount=$("#inAmount").val(); 
			if (typeof(strInAmount) != "undefined" && !isNaN(strInAmount)) {
				if (strInAmount <= 0) {
                	//总金额小于等于零，不能保存！
                	alertx("<spring:message code='message.I7004' />");

					return false;
				}	
			}


			//------------差额检查----------------
			//面值列表整体刷新
			AllRefresh();
			//面值列表总差额的取得
			vDiffValue = getDiffValue();
			if (vDiffValue != 0){
				//差额不为零，不能保存！
				alertx("<spring:message code='message.I7007' />");
				return false;
			}
			
			
			return true;
		}	
		
		//人员名称的设定
		function setSelectName(valueId, valueNm) {	
			$("#" + valueNm).val("");
			var selectValue=$("#" + valueId).val(); 
			if ((selectValue != undefined && selectValue != "")) {
				var selectText = $("#" + valueId).find("option:selected").text();
				$("#" + valueNm).val(selectText);
			}
		}	
		
		//总金额格式化
		function upperCase(obj){
			setSumDiff();
			changeAmountToBig(obj.value,"inAmountBigRmb");
		}
		
		/* 增加移出事件 修改人:sg 修改日期:2017-12-20 begin */
		function update(obj){
			setSumDiff();
			var money= changeAmountToNum(obj.value); 
			document.getElementById("inAmount").value=money;
		}
		/* end */
		/**
		千分位用逗号分隔
		 */
		 function formatCurrencyFloat(num) {
				num = num.toString().replace(/\$|\,/g, '');
				if (isNaN(num))
					num = "0";
				sign = (num == (num = Math.abs(num)));
				num = Math.floor(num * 100 + 0.50000000001);
				cents = num % 100;
				num = Math.floor(num / 100).toString();
				if (cents < 10)
					cents = "0" + cents;
				for (var i = 0; i < Math.floor((num.length - (1 + i)) / 3); i++)
					num = num.substring(0, num.length - (4 * i + 3)) + ','
							+ num.substring(num.length - (4 * i + 3));
				return (((sign) ? '' : '-') + num + '.' + cents);
			}
		 /**
		  将金额转换成数字
		 */
		function changeAmountToNum(amount) {
			if (typeof (amount) != "undefined"&&amount != null) {
				amount = amount.toString().replace(/\$|\,/g, '');
			}
			return amount;
		}
		 /* end */

		/**
		 * 金额转换成大写
		 * @param amount
		 * @param toId
		 */
		function changeAmountToBig(amount, toId) {
			if (amount == "" || isNaN(amount)) {
				$("#" + toId).val("");
				return;
			}else{
				if (!isNaN(amount) && amount==0){
					//零元整
					$("#" + toId).val("<spring:message code='message.I7008' />");
					return;
				}
			}
			
			$.ajax({
				url : ctx + '/clear/v03/orderClear/changRMBAmountToBig?amount=' + amount,
				type : 'post',
				dataType : 'json',
				success : function(data, status) {
					$("#" + toId).val(data.bigAmount);
					//$("#" + toId).html(data.bigAmount);
				},
				error : function(data, status, e) {
					
				}
			});
		}
		
		/**
		 * 加载select2下拉列表选项用
		 */
		function format(item) 
		{
			return item.label;
		}

		
	</script>
</head>
<body>
	<ul class="nav nav-tabs">
		<!-- 预约清分列表 -->
		<li><a href="${ctx}/clear/v03/orderClear/list"><spring:message code="clear.orderClear.title" /><spring:message code="common.list" /></a></li>
		<!-- 预约清分登记(修改) -->
		<li class="active">
			<a href="${ctx}/clear/v03/orderClear/form?inNo=${orderClearInfo.inNo}">
				<shiro:hasPermission name="clear:orderClear:edit">
					<c:choose>
						<c:when test="${not empty orderClearInfo.inNo}">
							<spring:message code="clear.orderClear.title" /><spring:message code="common.modify" /> 
						</c:when>
						<c:otherwise>
							<spring:message code="clear.orderClear.title" /><spring:message code="common.add" /> 
						</c:otherwise>
					</c:choose>
				</shiro:hasPermission>
			</a>
		</li>
	</ul>

	<form:form id="inputForm" modelAttribute="orderClearInfo" action="${ctx}/clear/v03/orderClear/save" method="post" class="form-horizontal" >
		<sys:message content="${message}"/>
		<input id="hidToDaySumAmount" name="hidToDaySumAmount" type="hidden" value="${toDayAmount}"/>
		<form:hidden path="inNo"/>
		<!-- end -->
		
		<!-- 明细 -->
		<div class="row">
			<!-- 预约清分明细 -->
			<div class="span7" style="margin-top:-20px;">
				<div class="control-group">
					<br/>
				</div>
				<div class="control-group">
					<!-- 清分机构 -->
					<label class="control-label"><spring:message code="clear.orderClear.office" />：</label>
					<div class="controls">
						<form:select path="rOffice.id" id="custNo"
							class="input-large required" disabled="disabled">
							<form:option value="">
								<spring:message code="common.select" />
							</form:option>
							<form:options
								items="${sto:getStoCustList('6',false)}"
								itemLabel="name" itemValue="id" htmlEscape="false" />
						</form:select>
						<span class="help-inline"><font color="red">*</font> </span>
						<form:hidden path="rOffice.name" />
					</div>
				</div>
				<div class="control-group">
					<!-- 总金额 -->
					<label class="control-label"><spring:message code="clear.public.totalMoney"/>：</label>
					<div class="controls">
						<form:input id="inAmount" path="inAmount" htmlEscape="false" onblur="upperCase(this)"  maxlength="12" class="required number"  style="ime-mode:disabled;"/>
						<span class="help-inline"><font color="red">*</font> </span>
					</div>
				</div>
				
				<div class="control-group">
					<!-- 总金额（格式化） -->
					<label class="control-label"><spring:message code="clear.public.totalMoneyFormat"/>：</label>
					<div class="controls">
						<textarea id="inAmountBigRmb" name="inAmountBigRmb" disabled>${sto:getUpperAmount(orderClearInfo.inAmount)}</textarea>
					</div>
				</div>
				<div class="control-group">
					<!-- 备注 -->
					<label class="control-label"><spring:message code="common.remark"/>：</label>
					<div class="controls">
						<form:textarea path="remarks" htmlEscape="false" rows="2" maxlength="60" class="input-large " style="ime-mode:active;"/>
					</div>
				</div>
			
				
					
				<%-- 所属商行当日合计 --%>
				<div class="span5" style="margin-top:10px;">
					<div >
					
						<table id="contentTable" class="table table-hover" >
						<thead>
								<tr>
									<%-- 面值 --%>
									<th style="text-align:center;line-height:14px;"><spring:message code="common.denomination" /></th>
									<%-- 未清分(捆) --%>
									<th style="text-align: center;line-height:14px;" ><spring:message code="clear.public.noneClear"/>(<spring:message code="clear.public.bundle"/>)</th>
									<%-- 已清分(捆) --%>
									<th style="text-align:center;line-height:14px;"><spring:message code="clear.public.haveClear"/>(<spring:message code="clear.public.bundle"/>)</th>
									<!-- 金额(元) -->
									<th style="text-align:center;line-height:14px;"><spring:message code="clear.public.moneyFormat"/></th>
								</tr>
							</thead>
							<tbody>
								<c:forEach items="${totalDenomList}" var="varItem" varStatus="status">
									<tr>
										<input  UseType="columnValue1"  type="hidden" value="${varItem.columnValue1}"/>
										<input  UseType="columnValue2"  type="hidden" value="${varItem.columnValue2}"/>
										<input  UseType="totalAmt"  type="hidden" value="${varItem.totalAmt}"/>
										<%-- 序号 --%>
										<td style="text-align:center;line-height:14px;">${varItem.moneyName}</td>
										<%-- 未清分(捆) --%>
										<td style="text-align:right;line-height:14px;">${varItem.columnValue1}</td>
										<%-- 已清分(捆) --%>
										<td style="text-align:right;line-height:14px;">${varItem.columnValue2}</td>
										<!-- 金额(元)  -->
										<td style="text-align:right;line-height:14px;"><fmt:formatNumber value="${varItem.totalAmt}" pattern="###,###,###,##0" /></td>
										
									</tr>
								</c:forEach>
								<!-- 当日合计 -->
								<tr>
									<td style="text-align:center;" >合计</td>
									<%-- 未清分(捆) --%>
									<td style="text-align:right;line-height:14px;">
										<label id="daySumValue1Show"></label>
									</td>
									<%-- 已清分(捆) --%>
									<td style="text-align:right;line-height:14px;">
										<label id="daySumValue2Show"></label>
									</td>
									<!-- 金额(元)  -->
									<td style="text-align:right;line-height:14px;">
										<label id="daySumAmountShow"></label>
									</td>
								</tr>
							</tbody>
						</table>
					</div>
				</div>
				
				
				
			</div>
			
			<!-- 面值明细 -->
			<div class="span7">
				<sys:denominationList dataSource="${orderClearInfo.denominationList}"  bindListName="denominationList" totalAmountId="inAmount"/>
			</div>
			
		</div>
		<div class="row">
			<div class="form-actions" style="width:100%;">
					<!-- 保存 -->
					<input id="btnSubmit" class="btn btn-primary" type="button"
						value="<spring:message code='common.commit'/>" />
						
					&nbsp;
				<!-- 返回 -->
				<input id="btnCancel" class="btn" type="button"
					value="<spring:message code='common.return'/>"
					onclick="window.location.href='${ctx}/clear/v03/orderClear/back'"/>
					
					
			</div>
		</div>

	</form:form>
</body>
</html>
