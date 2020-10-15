		$(document).ready(function() {
			$("#btnSubmit").click(function(){
				$("#searchForm").attr("action", CTX + "/allocation/v02/pbocStockIn/list");
				$("#searchForm").submit();
			});
		});