<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<style>
		table #red a {
			color: red !important;
		}
		table #red a:hover {
			color: green !important;
		}
		.lable1{
			line-height: 30px;
		}
		.input1{
			margin-bottom: 0px !important;
		} 	
	</style>
	<title>清机记录</title>
	<meta name="decorator" content="default"/>
	<script type="text/javascript">
	<%-- 关闭加载动画 --%>
	window.onload=function(){
		window.top.clickallMenuTreeFadeOut();
	}
	// 验证   gzd 2020-04-16
	$(document).ready(function() {
		$("#searchForm").validate({
			submitHandler: function(form){
				var beforeAmount = changeAmountToNum($("#beforeAmount").val());
				var afterAmount = changeAmountToNum($("#afterAmount").val());
				// 没有金额为空 且 前小于后
				if($("#beforeAmount").val()!="" && $("#afterAmount").val()!="" && parseFloat(beforeAmount) > parseFloat(afterAmount)){
					alertx("总金额范围有误！");
					return;
				}
				// 金额格式化
				$("#beforeAmount").val(changeAmountToNum($("#beforeAmount").val()));
				$("#afterAmount").val(changeAmountToNum($("#afterAmount").val()));
				
				//提交按钮失效，防止重复提交
				$("#btnSubmit").attr("disabled",true);
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
	});
		
		$(document).ready(function() {
			$("#exportSubmit").on('click',function(){
				$("#searchForm").prop("action", "${ctx}/doorOrder/v01/clearEquipmentRecord/exportDetail");
				$("#searchForm").submit();
				$("#searchForm").prop("action", "${ctx}/doorOrder/v01/clearEquipmentRecord/clearEquipmentRecordList");		
				$("#btnSubmit").attr("disabled",false);
			});
		});
		function page(n,s){
			$("#pageNo").val(n);
			$("#pageSize").val(s);
			$("#searchForm").submit();
        	return false;
        }
		
		//总金额模糊查询输入限制  gzd 2020-04-16
		$(document).ready(function(){
			bindKeyEvent($("#beforeAmount"));
			bindKeyEvent($("#afterAmount"));
		});
		
		//正则   gzd 2020-04-16
		function bindKeyEvent(obj){
			obj.keyup(function () {
		        var reg = $(this).val().match(/\d+\.?\d{0,2}/);
		        var txt = '';
		        if (reg != null) {
		            txt = reg[0];
		        }
		        $(this).val(txt);
		    }).change(function () {
		        $(this).keypress();
		        var v = $(this).val();
		        if (/\.$/.test(v))
		        {
		            $(this).val(v.substr(0, v.length - 1));
		        }
		    });
		}
		
		/**
		 * 将金额转换成数字
		 * @param amount
		 * @author gzd 2020-04-16
		 * @returns
		 */
		function changeAmountToNum(amount) {
			if (typeof(amount) != "undefined") {
				amount = amount.toString().replace(/\$|\,/g,'');
			}
			return amount;
		}
		
		//拆箱单号弹窗拆箱明细
		function showBusinessIdDetail(id) {
			var content = "iframe:${ctx}/collection/v03/checkCash/view?id=" + id + "&type=1";
			top.$.jBox.open(
				content,
				"款箱拆箱查看详情", 1550, 700, {
					buttons: {
						//关闭
						"<spring:message code='common.close' />": true
					},
					loaded: function (h) {
						$(".jbox-content", top.document).css(
								"overflow-y", "hidden");
						h.find("iframe")[0].contentWindow.displayDiv1.setAttribute("style","display:none");
						h.find("iframe")[0].contentWindow.displayLi1.setAttribute("style","display:none");
						h.find("iframe")[0].contentWindow.displayLi2.setAttribute("style","display:none");
						h.find("iframe")[0].contentWindow.displayLi3.setAttribute("style","display:none");
					}
			});
		}
		
		//款袋编号弹窗存款明细
		function showBagNoDetail(id) {
			var content = "iframe:${ctx}/weChat/v03/doorOrderInfo/doorOrderDetailForm?id=" + id;
			top.$.jBox.open(
				content,
				"查看详情", 1400, 750, {
					buttons: {
						//关闭
						"<spring:message code='common.close' />": true
					},
					loaded: function (h) {
						$(".jbox-content", top.document).css(
								"overflow-y", "hidden");
						h.find("iframe")[0].contentWindow.disDiv1.setAttribute("style","display:none");
						h.find("iframe")[0].contentWindow.disLi1.setAttribute("style","display:none");
						h.find("iframe")[0].contentWindow.disLi2.setAttribute("style","display:none");
					}
			});
		}
		
	</script>
</head>
<body>
    <ul class="nav nav-tabs">
	    <!-- 设备缴存列表 -->
		<li>
			<a onclick="window.top.clickSelect();" href="${ctx}/doorOrder/v01/depositPanorama/">
				<spring:message code="door.panorama.equipDeposit" />
			</a>
		</li>
		<!-- 封包缴存列表 -->
		<li>
			<a onclick="window.top.clickSelect();" href="${ctx}/page/a?menuname=/doorOrder/v01/depositPanorama/packageList" target="mainFrame">
				<spring:message code="door.panorama.otherDeposit" />
			</a>
		</li>
		<!-- 清机记录 -->
		<li class="active">
			<a onclick="window.top.clickSelect();" href="${ctx}/doorOrder/v01/clearEquipmentRecord/clearEquipmentRecordList?equipmentId=${clearEquipmentRecord.equipmentId}">
				<spring:message code="door.clearEquipmentRecord.list" />
			</a>
		</li>
	</ul>
	<form:form id="searchForm" modelAttribute="clearEquipmentRecord" action="${ctx}/doorOrder/v01/clearEquipmentRecord/clearEquipmentRecordList" method="post" class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		<input id="equipmentId" name="equipmentId" type="hidden" value="${clearEquipmentRecord.equipmentId}"/>
		<sys:tableSort id="orderBy" name="orderBy" value="${page.orderBy}" callback="page();"/>
		<div class="row search-flex" style="padding-left: 15px;">
			<!-- 款袋编号 -->
			<div>
				<label><spring:message code="door.clearEquipmentRecord.bagNo" />:</label>
				<form:input path="bagNo" htmlEscape="false" maxlength="20" class="input-small" />
			</div>
			<!-- 凭条号 -->
			<div>
				<label><spring:message code="door.clearEquipmentRecord.tickerTape" />:</label>
				<form:input path="tickerTape" htmlEscape="false" maxlength="20" class="input-small"/>
			</div>
			<!-- 业务备注 -->
			<div>
				<label><spring:message code="door.clearEquipmentRecord.remarks" />:</label>
				<form:input path="remarks" htmlEscape="false" maxlength="20" class="input-small"/>
			</div>
			<!-- 存款人 -->
			<div>
				<label><spring:message code="door.clearEquipmentRecord.depositMan" />:</label>
				<form:input path="depositMan" htmlEscape="false" maxlength="20" class="input-small"/>
			</div>
			<div>
				<%-- 清机员--%>
				<label><spring:message code="door.clearEquipmentRecord.payPeople" />:</label>
				<form:input path="payPeople" htmlEscape="false" maxlength="200" class="input-small"/>
			</div>
			<%-- 起始金额  gzd 2020-04-16 --%>
			<div>
				<label><spring:message code="common.totalMoney" />:</label>
				<input id="beforeAmount"  name="beforeAmount" type="text" maxlength="20" class="input-small" style="text-align:right;"
					value="${clearEquipmentRecord.beforeAmount}" placeholder="0.00" />
			</div>
			<%-- 结束金额  gzd 2020-04-16 --%>
			<div>
				<label>~</label>
				<input id="afterAmount" name="afterAmount" type="text" maxlength="20" class="input-small" style="text-align:right;"
					value="${clearEquipmentRecord.afterAmount}" placeholder="9999999999.99" />
			</div>
			<%-- 装卸袋时间 --%>
			<div>
			<label><spring:message code="door.clearEquipmentRecord.upDownDate" />:</label> <input
				id="createTimeStart" name="createTimeStart" type="text"
				readonly="readonly" maxlength="20"
				class="input-small Wdate createTime"
				value="<fmt:formatDate value="${clearEquipmentRecord.createTimeStart}" pattern="yyyy-MM-dd"/>"
				onclick="WdatePicker({isShowClear:true,readOnly:true,dateFmt:'yyyy-MM-dd',maxDate:'#F{$dp.$D(\'createTimeEnd\')||\'%y-%M-%d\'}'});" />
			</div>
			<%-- 结束日期 --%>
			<div>
			<label>~</label> <input
				id="createTimeEnd" name="createTimeEnd" type="text"
				readonly="readonly" maxlength="20"
				class="input-small Wdate createTime"
				value="<fmt:formatDate value="${clearEquipmentRecord.createTimeEnd}" pattern="yyyy-MM-dd"/>"
				onclick="WdatePicker({isShowClear:true,readOnly:true,dateFmt:'yyyy-MM-dd',minDate:'#F{$dp.$D(\'createTimeStart\')}',maxDate:'%y-%M-%d'});" />
			</div>
			&nbsp;&nbsp;
			<div>
			<input id="btnSubmit" class="btn btn-primary" type="submit" value="<spring:message code='common.search'/>"/>
			</div>
			&nbsp;&nbsp;
			<!-- 导出 -->
			<div>
			<input id="exportSubmit"  class="btn btn-red" type="button" value="<spring:message code='common.export'/>" />
			</div>
			<div>
			<!-- 返回 -->
			&nbsp;
			<input id="btnCancel" class="btn" type="button" value="<spring:message code='common.return'/>" onclick="window.location.href='${ctx}/doorOrder/v01/depositPanorama/'"/>
			</div>
		</div>
	</form:form>
	<div id="clear_sel" class="row search-flex" style="padding-left: 25px;margin-bottom: 10px;margin-top: -10px;">
		<!-- 区域 -->
		<div style="float: left;">
			<label class="lable1"><spring:message code="door.clearEquipmentRecord.area" />:</label>
			<input class="input1" type="text" value="${page.list[0].area }" maxlength="20" class="input-medium" readOnly="readOnly"/>
		</div>&nbsp;&nbsp;
		<!-- 所属公司 -->
		<div style="float: left;">
			<label class="lable1"><spring:message code="door.clearEquipmentRecord.doorName" />:</label>
			<input class="input1" type="text" value="${page.list[0].doorName }" maxlength="20" class="input-large" readOnly="readOnly"/>
		</div>&nbsp;&nbsp;
		<!-- 机具编号 -->
		<div style="float: left;">
			<label class="lable1"><spring:message code="door.clearEquipmentRecord.seriesNumber" />:</label>
			<input class="input1" type="text" value="${page.list[0].seriesNumber }" maxlength="20" class="input-small" readOnly="readOnly"/>
		</div>
	</div>
	<sys:message content="${message}"/>
	<div class="table-con">
	<table id="contentTable" class="table table-hover">
		<thead>
			<tr>
			    <%-- 序号--%>
				<th><spring:message code="common.seqNo" /></th>
				<%-- 款袋编号--%>
				<th class="sort-column bagNo"><spring:message code="door.clearEquipmentRecord.bagNo" /></th>
				<%-- 装袋批次--%>
				<th class="sort-column batchNo"><spring:message code="door.clearEquipmentRecord.batchNo" /></th>
				<%-- 速存张数--%>
				<th class="sort-column paperCount"><spring:message code="door.clearEquipmentRecord.paperCount" /></th>
				<%-- 速存金额--%>
				<th class="sort-column paperAmount"><spring:message code="door.clearEquipmentRecord.paperAmount" /></th>
				<%-- 强存金额--%>
				<th class="sort-column forceAmount"><spring:message code="door.clearEquipmentRecord.forceAmount" /></th>
				<%-- 其他金额 --%>
				<th class="sort-column otherAmount"><spring:message code="door.clearEquipmentRecord.otherAmount" /></th>
				<%-- 总金额--%>
				<th class="sort-column totalAmount"><spring:message code="door.clearEquipmentRecord.totalAmount" /></th>
				<%-- 装袋时间--%>
				<th class="sort-column beforeDate"><spring:message code="door.clearEquipmentRecord.beforeDate" /></th>
				<%-- 钞袋使用时间--%>
				<th width="150px"><spring:message code="door.clearEquipmentRecord.useDate" /></th>
				<%-- 卸袋时间--%>
				<th class="sort-column afterDate"><spring:message code="door.clearEquipmentRecord.afterDate" /></th>
				<%-- 清机员 --%>
				<th class="sort-column payPeople"><spring:message code="door.clearEquipmentRecord.payPeople" /></th>
				<%-- 拆箱单号--%>
				<th class="sort-column outNo"><spring:message code="door.clearEquipmentRecord.outNo" /></th>
				<%-- 更新日志--%>
				<!-- <th colspan="2" style="text-align: center;">更新日志</th> -->
				<%-- 更新人--%>
				<th class="sort-column updateName"><spring:message code="door.clearEquipmentRecord.updateName" /></th>
				<%-- 更新时间--%>
				<th class="sort-column updateDate"><spring:message code="door.clearEquipmentRecord.updateDate" /></th>
				<%-- 状态--%>
				<th class="sort-column status"><spring:message code="door.clearEquipmentRecord.status" /></th>
			</tr>
		</thead>
		<tbody>
		<c:forEach items="${page.list}" var="clearEquipmentRecord" varStatus="status">
			<tr>
				<td>${status.index+1}</td>
				<td>
					<a href="javascript:void(0);" onclick="showBagNoDetail('${clearEquipmentRecord.id}')"> 
						${clearEquipmentRecord.bagNo }
					</a>
				</td>
				<td>
					${clearEquipmentRecord.batchNo }
				</td>
				<td>
					${clearEquipmentRecord.paperCount }
				</td>
				<td style="text-align:right;">
					<fmt:formatNumber value="${clearEquipmentRecord.paperAmount }" pattern="#,##0.00#" />
				</td>
				<td style="text-align:right;">
					<fmt:formatNumber value="${clearEquipmentRecord.forceAmount }" pattern="#,##0.00#" />
				</td>
				<td style="text-align:right;">
					<fmt:formatNumber value="${clearEquipmentRecord.otherAmount }" pattern="#,##0.00#" />
				</td>
				<td style="text-align:right;">
					<fmt:formatNumber value="${clearEquipmentRecord.totalAmount }" pattern="#,##0.00#" />
				</td>
				<td>
					<fmt:formatDate value="${clearEquipmentRecord.beforeDate}" pattern="yyyy-MM-dd HH:mm:ss" />
				</td>
				<td>
					<fmt:formatDate value="${clearEquipmentRecord.beforeDate}" pattern="yyyy-MM-dd HH:mm:ss" />~<br/>
					<c:choose>
						<c:when test="${clearEquipmentRecord.afterDate != null}">
							<fmt:formatDate value="${clearEquipmentRecord.afterDate}" pattern="yyyy-MM-dd HH:mm:ss" />
						</c:when>
						<c:otherwise>
							${fns:getDate('yyyy-MM-dd HH:mm:ss')}
						</c:otherwise>
					</c:choose>
				</td>
				<td>
					<fmt:formatDate value="${clearEquipmentRecord.afterDate}" pattern="yyyy-MM-dd HH:mm:ss" />
				</td>
				<td>
					${clearEquipmentRecord.payPeople}
				</td>
				<c:choose>
					<c:when test="${clearEquipmentRecord.errorType != null}">
						<td id="red">
					</c:when>
					<c:otherwise>
						<td>
					</c:otherwise>
				</c:choose>
				<c:choose>
					<c:when test="${clearEquipmentRecord.status == 3}">
						<a href="javascript:void(0);" onclick="showBusinessIdDetail('${clearEquipmentRecord.outNo}')">
							${clearEquipmentRecord.outNo}
						</a>
					</c:when>
				</c:choose>
				</td>
				<td>
					${clearEquipmentRecord.updateName}
				</td>
				<td>
					<fmt:formatDate value="${clearEquipmentRecord.updateDate}" pattern="yyyy-MM-dd HH:mm:ss" />
				</td>
				<td>
					${fns:getDictLabelWithCss(clearEquipmentRecord.status, 'sys_clear_type', '未命名',true)}
				</td>
				<%-- <td>
					${clearEquipmentRecord.doorName }
				</td> --%>
				
			</tr>
		</c:forEach>
		</tbody>
	</table>
	</div>
	<div class="pagination">${page}</div>
	<!-- <div class="form-actions" style="width:100%">
	 <input id="btnCancel" class="btn" type="button" value="<spring:message code='common.return'/>" 
	onclick="window.location.href='${ctx}/doorOrder/v01/depositPanorama/'"/> --%>
	<!-- onclick="history.go(-1)"/> 
	</div>-->
</body>
</html>