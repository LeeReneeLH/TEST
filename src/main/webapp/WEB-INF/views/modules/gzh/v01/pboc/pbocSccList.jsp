<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>可疑币浓度</title>
	<meta name="decorator" content="default"/>
	<script type="text/javascript" src="${ctxStatic}/common/common.js"></script>
	<script type="text/javascript">
		// 修改物品
		function showDetail() {
			
			var content = "iframe:${ctx}/gzh/v02/pbocGzh/toPbocShowDetail";
			top.$.jBox.open(
					content,
					"查看详情", 300, 500, {
						buttons : {
							//关闭
							"<spring:message code='common.close' />" : true
						},
						loaded : function(h) {
							$(".jbox-content", top.document).css(
									"overflow-y", "auto");
						}
			});
		}
	</script>
</head>
<body>
	<div class="row">
		<form:form id="searchForm" action="" method="post" class="breadcrumb form-search">
			
			<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
			<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
			<sys:tableSort id="orderBy" name="orderBy" value="${page.orderBy}" callback="page();"/>
				
			<%-- 银行名称 --%>
			<label>银行名称：</label>
			<input type="text" id="bankName" name="bankName" class="input-small">
		
			<%-- 开始日期 --%>
			<label>登记日期：</label>
			<input id="createTimeStart"  name="createTimeStart" type="text" readonly="readonly" maxlength="20" class="input-small Wdate createTime" 
				   value="" 
				   onclick="WdatePicker({isShowClear:false,readOnly:true,dateFmt:'yyyy-MM-dd',maxDate:'#F{$dp.$D(\'createTimeEnd\')}'});"/>
			<%-- 结束日期 --%>
			<label>至</label>
			<input id="createTimeEnd" name="createTimeEnd" type="text" readonly="readonly" maxlength="20" class="input-small Wdate createTime" 
			       value=""
			       onclick="WdatePicker({isShowClear:false,readOnly:true,dateFmt:'yyyy-MM-dd',minDate:'#F{$dp.$D(\'createTimeStart\')}'});"/>
			&nbsp;
			<%-- 查询 --%>
			<input id="btnSubmit" class="btn btn-primary" type="submit" value="<spring:message code='common.search'/>"/>
				
			
		</form:form>
	</div>
	<table  id="contentTable" class="table table-hover">
		<thead>
			<tr>
				<%-- 银行名称 --%>
				<th>银行名称</th>
				<%-- 假币浓度 --%>
				<th>假币浓度</th>
				<%-- 残损币浓度 --%>
				<th>残损币浓度</th>
				<%-- 登记日期 --%>
				<th>登记日期</th>
				<%-- 详情 --%>
				<th>详情</th>
				<%-- 评分 --%>
				<th>评分</th>
			</tr>
		</thead>
		<tbody>
			<tr>
				<%-- 银行名称 --%>
				<td>中国工商银行</td>
				<%-- 假币浓度 --%>
				<td>10%</td>
				<%-- 残损币浓度 --%>
				<td>15%</td>
				<%-- 登记日期--%>
				<td>2016-07-05 10:20</td>
				<%-- 详情--%>
				<td><a href="#" onclick="showDetail();javascript:return false;" title="查看"><i class="fa fa-eye fa-lg"></i></a></td>
				<%-- 状态 --%>
				<td></td>
				
			</tr>
			<tr>
				<%-- 银行名称 --%>
				<td>中国农业银行</td>
				<%-- 假币浓度 --%>
				<td>0.5%</td>
				<%-- 残损币浓度 --%>
				<td>15%</td>
				<%-- 登记日期--%>
				<td>2016-07-04 10:20</td>
				<%-- 详情--%>
				<td><a href="#" onclick="showDetail();javascript:return false;" title="查看"><i class="fa fa-eye fa-lg"></i></a></td>
				<%-- 评分 --%>
				<td></td>
				
			</tr>
			<tr>
				<%-- 银行名称 --%>
				<td>中国银行</td>
				<%-- 假币浓度 --%>
				<td>1.5%</td>
				<%-- 残损币浓度 --%>
				<td>25%</td>
				<%-- 登记日期--%>
				<td>2016-07-04 10:20</td>
				<%-- 详情--%>
				<td><a href="#" onclick="showDetail();javascript:return false;" title="查看"><i class="fa fa-eye fa-lg"></i></a></td>
				<%-- 评分 --%>
				<td></td>
				
			</tr>
			<tr>
				<%-- 银行名称 --%>
				<td>中国邮政储蓄银行</td>
				<%-- 假币浓度 --%>
				<td>1.5%</td>
				<%-- 残损币浓度 --%>
				<td>25%</td>
				<%-- 登记日期--%>
				<td>2016-07-04 10:20</td>
				<%-- 详情--%>
				<td><a href="#" onclick="showDetail();javascript:return false;" title="查看"><i class="fa fa-eye fa-lg"></i></a></td>
				<%-- 评分 --%>
				<td></td>
				
			</tr>
			<tr>
				<%-- 银行名称 --%>
				<td>中国建设银行</td>
				<%-- 假币浓度 --%>
				<td>1.5%</td>
				<%-- 残损币浓度 --%>
				<td>25%</td>
				<%-- 登记日期--%>
				<td>2016-07-04 10:20</td>
				<%-- 详情--%>
				<td><a href="#" onclick="showDetail();javascript:return false;" title="查看"><i class="fa fa-eye fa-lg"></i></a></td>
				<%-- 评分 --%>
				<td></td>
				
			</tr>
			<tr>
				<%-- 银行名称 --%>
				<td>中国交通银行</td>
				<%-- 假币浓度 --%>
				<td>1.5%</td>
				<%-- 残损币浓度 --%>
				<td>25%</td>
				<%-- 登记日期--%>
				<td>2016-07-04 10:20</td>
				<%-- 详情--%>
				<td><a href="#" onclick="showDetail();javascript:return false;" title="查看"><i class="fa fa-eye fa-lg"></i></a></td>
				<%-- 评分 --%>
				<td></td>
				
			</tr>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
</body>
</html>
