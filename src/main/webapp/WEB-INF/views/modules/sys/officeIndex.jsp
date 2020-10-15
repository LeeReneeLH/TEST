<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>机构管理</title>
	<meta name="decorator" content="default"/>
	<%@include file="/WEB-INF/views/include/treeview.jsp" %>
	
	<!-- 新增机构查询的分页功能,页面加载时初始化ztree  xp start -->
	<script type="text/javascript">
		$(document).ready(function() {
			$.post("${ctx}/sys/office/treeDataOffice",function(data){
				$.fn.zTree.init($("#ztree"), setting, data).expandAll(false);
			},'json');
		});
	</script>
	<!-- 追加机构查询的分页功能  xp end -->
	
	<style type="text/css">
		.ztree {overflow:auto;margin:0;_margin-top:10px;padding:10px 0 0 10px;}
		
	#left{background: #f2f2f2 !important;}

	</style>
</head>
<body>
	<sys:message content="${message}"/>
	<div id="content" class="row-fluid">
		<div id="left" class="accordion-group">
			<div class="accordion-heading" style="height:40px;line-height:30px;">
		    	<a class="accordion-toggle" style="background-image:none;border-bottom:1px solid #ccc;">组织机构<i class="icon-refresh pull-right" style="position:relative;top:-50px;" onclick="refreshTree();"></i></a>
		    </div>
			<div id="ztree" class="ztree"></div>
		</div>
		<div id="openClose" class="close">&nbsp;</div>
		<div id="right">
			<iframe id="officeContent" src="${ctx}/sys/office/list?id=&parentIds=" width="100%" height="91%" frameborder="0"></iframe>
		</div>
	</div>
	<script type="text/javascript">
	var setting = {data:{simpleData:{enable:true,idKey:"id",pIdKey:"pId",rootPId:'0'}},
			callback:{
				/*父节点被展开时调用 start*/
				onExpand:function(event, treeId, treeNode){
					var treeObj = $.fn.zTree.getZTreeObj("ztree");
					
					$.ajax({
                        url: "${ctx}/sys/office/treeDataOffice",//请求的action路径
                        data:{"pId":treeNode.id},
                        error: function () {//请求失败处理函数
                            alert('请求失败');
                        },
                        success:function(data)
                            { //添加子节点到指定的父节点      	
                                var jsondata= eval(data);
                                if(jsondata == null || jsondata == ""){
                                    //末节点的数据为空   所以不再添加节点
                                    }
                                else{
                                        var parentZNode = treeObj.getNodeByParam("id", treeNode.id, null);//获取指定父节点
                                        var zAsync = treeNode.zAsync;
                                        if(zAsync){ 
                                        	treeObj.reAsyncChildNodes(treeNode,"refresh"); 
                                        }else{
                                        	newNode = treeObj.addNodes(parentZNode,jsondata, false);
                                        	/**父节点再次展开时不需要进行异步加载**/
                                        	treeNode.zAsync = true;
                                        } 
                                    }
                            }
                        });
                },	
                /*父节点被展开时调用  end*/
                
				/* 新增单击ztree时，异步获取子节点数据的方法  start */
				/* onClick:function(event, treeId, treeNode){
					 var treeObj = $.fn.zTree.getZTreeObj("ztree");
					 if(treeNode.children == undefined || treeNode.children == null){  
                         $.ajax({
                             url: "${ctx}/sys/office/treeDataOffice",//请求的action路径
                             data:{"pId":treeNode.id},
                             error: function () {//请求失败处理函数
                                 alert('请求失败');
                             },
                             success:function(data)
                                 { //添加子节点到指定的父节点
                                     var jsondata= eval(data);
                                     if(jsondata == null || jsondata == ""){
                                         //末节点的数据为空   所以不再添加节点
                                         }
                                     else{
                                             var parentZNode = treeObj.getNodeByParam("id", treeNode.id, null);//获取指定父节点
                                             var zAsync = treeNode.zAsync;
                                             if(zAsync){ 
                                             	treeObj.reAsyncChildNodes(treeNode,"refresh"); 
                                             }else{
                                             	newNode = treeObj.addNodes(parentZNode,jsondata, false);
                                             	treeNode.zAsync = true;
                                             } 
                                         }
                                 }
                             });
                     } 
                 },  */
                /* 新增单机ztree时，异步获取子节点数据的方法  end */
				/* 该事件原本为单击事件，为了进行异步操作，改为双击事件 */
				onDblClick:function(event, treeId, treeNode){
				var id = treeNode.pId == '0' ? treeNode.id :treeNode.id;
				//var pIds = id == '' ? treeNode.pIds : treeNode.pIds+id+",";					
					$('#officeContent').attr("src","${ctx}/sys/office/search?id="+id);
				}
			}
		};
		
		function refreshTree(){
			$.post("${ctx}/sys/office/treeDataOffice",function(data){
				$.fn.zTree.init($("#ztree"), setting, data).expandAll(true);
			},'json');
		}
		// 注释下面一行，使得仅在机构管理画面加载时刷新树
//		refreshTree();
		 
		var leftWidth = 250; // 左侧窗口大小
		var htmlObj = $("html"), mainObj = $("#main");
		var frameObj = $("#left, #openClose, #right, #right iframe");
		function wSize(){
			var strs = getWindowSize().toString().split(",");
			htmlObj.css({"overflow-x":"hidden", "overflow-y":"hidden"});
			mainObj.css("width","auto");
			frameObj.height(strs[0] - 5);
			var leftWidth = ($("#left").width() < 0 ? 0 : $("#left").width());
			$("#right").width($("#content").width()- leftWidth - $("#openClose").width() -5);
			$(".ztree").width(leftWidth - 10).height(frameObj.height() - 46);
		}
	</script>
	<script src="${ctxStatic}/common/wsize.min.js" type="text/javascript"></script>
</body>
</html>