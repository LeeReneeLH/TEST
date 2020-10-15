<li class="nav nav-tabs">
	<!-- <a class="accordion-toggle" data-toggle="collapse" href="#collapseOne" onclick="toggle()"> -->
	<a class="accordion-toggle" data-toggle="collapse" onclick="toggle()">
	<spring:message code="common.detailInfo" />
	<i id="icon_down" class="icon-chevron-down"></i>
	<i id="icon_right" class="icon-chevron-right" style="display:none;"></i>
	</a>
</li>
<script type="text/javascript">
	function toggle(){
		$("#icon_down").toggle();
		$("#icon_right").toggle();
		$("#collapseOne").toggle();
	}
</script>
