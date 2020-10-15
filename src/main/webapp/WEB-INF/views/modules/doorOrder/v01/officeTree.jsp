<%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
    <title>机构管理</title>
    <meta name="decorator" content="default" />
    <%@include file="/WEB-INF/views/include/treeview.jsp"%>

    <!-- 新增机构查询的分页功能,页面加载时初始化ztree  xp start -->
    <script type="text/javascript">
    	<%-- 关闭加载动画 --%>
		window.onload=function(){
			window.parent.clickallMenuTreeFadeOut();
		}
        var key, lastValue = "", nodeList = [], type = getQueryString("type", "${url}");
        var tree, setting = {view:{selectedMulti:false,dblClickExpand:false},check:{enable:"${checked}",nocheckInherit:true},
            async:{enable:(type==3),url:"${ctx}/sys/user/treeData",autoParam:["id=officeId"]},
            data:{simpleData:{enable:true}},callback:{<%--
				beforeClick: function(treeId, treeNode){
					if("${checked}" == "true"){
						//tree.checkNode(treeNode, !node.checked, true, true);
						tree.expandNode(treeNode, true, false, false);
					}
				}, --%>
                onClick:function(event, treeId, treeNode){
                    tree.expandNode(treeNode);
                },onCheck: function(e, treeId, treeNode){
                    var nodes = tree.getCheckedNodes(true);
                    for (var i=0, l=nodes.length; i<l; i++) {
                        tree.expandNode(nodes[i], true, false, false);
                    }
                    return false;
                },onAsyncSuccess: function(event, treeId, treeNode, msg){
                    var nodes = tree.getNodesByParam("pId", treeNode.id, null);
                    for (var i=0, l=nodes.length; i<l; i++) {
                        try{tree.checkNode(nodes[i], treeNode.checked, true);}catch(e){}
                        //tree.selectNode(nodes[i], false);
                    }
                    selectCheckNode();
                },onDblClick: function(){//<c:if test="${!checked}">
                    top.$.jBox.getBox().find("button[value='ok']").trigger("click");
                    //$("input[type='text']", top.mainFrame.document).focus();//</c:if>
                }
            }
        };

        function expandNodes(nodes) {
            if (!nodes) return;
            for (var i=0, l=nodes.length; i<l; i++) {
                tree.expandNode(nodes[i], true, false, false);
                if (nodes[i].isParent && nodes[i].zAsync) {
                    expandNodes(nodes[i].children);
                }
            }
        }
        $(document).ready(function() {
            $.post("${ctx}/sys/office/treeDataOfficeForDoor?isAll=${isAll}&isNotType=${isNotType}",function(zNodes){
                tree = $.fn.zTree.init($("#ztree"), setting, zNodes);//打开全部节点;
                showAllNode(tree.getNodes());
            },'json');
            key = $("#key");
            key.bind("focus", focusKey).bind("blur", blurKey).bind("change cut input propertychange", searchNode);
            key.bind('keydown', function (e){if(e.which == 13){searchNode();}});
            setTimeout("search();", "300");
        });

        // 默认选择节点
        function selectCheckNode(){
            var ids = "${selectIds}".split(",");
            for(var i=0; i<ids.length; i++) {
                var node = tree.getNodeByParam("id", (type==3?"u_":"")+ids[i]);
                if("${checked}" == "true"){
                    try{tree.checkNode(node, true, true);}catch(e){}
                    tree.selectNode(node, false);
                }else{
                    tree.selectNode(node, true);
                }
            }
        }
        function focusKey(e) {
            if (key.hasClass("empty")) {
                key.removeClass("empty");
            }
        }
        function blurKey(e) {
            if (key.get(0).value === "") {
                key.addClass("empty");
            }
            searchNode(e);
        }
        //搜索节点
        function searchNode() {
            // 取得输入的关键字的值
            var value = $.trim(key.get(0).value);

            // 按名字查询
            var keyType = "name";<%--
			if (key.hasClass("empty")) {
				value = "";
			}--%>

            // 如果和上次一次，就退出不查了。
            if (lastValue === value) {
                return;
            }

            // 保存最后一次
            lastValue = value;

            var nodes = tree.getNodes();
            // 如果要查空字串，就退出不查了。
            if (value == "") {
                showAllNode(nodes);
                return;
            }
            hideAllNode(nodes);
            nodeList = tree.getNodesByParamFuzzy(keyType, value);
            updateNodes(nodeList);
        }

        //隐藏所有节点
        function hideAllNode(nodes){
            nodes = tree.transformToArray(nodes);
            for(var i=nodes.length-1; i>=0; i--) {
                tree.hideNode(nodes[i]);
            }
        }

        //显示所有节点
        function showAllNode(nodes){
            nodes = tree.transformToArray(nodes);
            for(var i=nodes.length-1; i>=0; i--) {
                /* if(!nodes[i].isParent){
                    tree.showNode(nodes[i]);
                }else{ */
                if(nodes[i].type == 9){
                    tree.expandNode(nodes[i],true,false,false,false);
                }else{
                    tree.expandNode(nodes[i],true,false,false,false);
                }
                tree.showNode(nodes[i]);
                showAllNode(nodes[i].children);
                /* } */
            }
        }

        //更新节点状态
        function updateNodes(nodeList) {
            tree.showNodes(nodeList);
            for(var i=0, l=nodeList.length; i<l; i++) {

                //展开当前节点的父节点
                tree.showNode(nodeList[i].getParentNode());
                //tree.expandNode(nodeList[i].getParentNode(), true, false, false);
                //显示展开符合条件节点的父节点
                while(nodeList[i].getParentNode()!=null){
                    tree.expandNode(nodeList[i].getParentNode(), true, false, false);
                    nodeList[i] = nodeList[i].getParentNode();
                    tree.showNode(nodeList[i].getParentNode());
                }
                //显示根节点
                tree.showNode(nodeList[i].getParentNode());
                //展开根节点
                tree.expandNode(nodeList[i].getParentNode(), true, false, false);
            }
        }
        // 开始搜索
        function search() {
            $("#key").focus();
        }
    </script>
    <!-- 追加机构查询的分页功能  xp end -->

    <style type="text/css">
        .ztree {
            overflow: auto;
            margin: 0;
            _margin-top: 10px;
            padding: 10px 0 0 10px;
        }

        #left {
            background: #f2f2f2 !important;
        }
    </style>
</head>
<body>
<sys:message content="${message}" />
<div id="content" class="row-fluid">
    <div id="left" class="accordion-group">
        <div class="accordion-heading"
             style="height: 40px; line-height: 30px;">
            <a class="accordion-toggle"
               style="background-image: none; border-bottom: 1px solid #ccc;color:#7f40bc !important">
               <c:if test="${isNotType != null}">所有商户</c:if><c:if test="${isNotType == null}">所有门店</c:if>
               <i class="icon-refresh pull-right"
                    style="position: relative; top: -50px;" onclick="refreshTree();"></i></a>
        </div>
        <div id="search" class="form-search" style="padding: 60px 0 0 13px;">
            <label for="key" class="control-label"
                   style="padding: 5px 5px 3px 0;">关键字：</label> <input type="text"
                                                                       class="empty" id="key" name="key" maxlength="50"
                                                                       style="width: 110px;">
        </div>

        <div id="ztree" class="ztree"></div>
    </div>
    <div id="openClose" class="close">&nbsp;</div>
    <div id="right">
        <iframe id="officeContent"
                src="${ctx}${menuname}" width="100%"
                height="91%" frameborder="0"></iframe>
    </div>
</div>
<script type="text/javascript">
    var setting = {data:{simpleData:{enable:true,idKey:"id",pIdKey:"pId",rootPId:'0'}},
        callback:{
            /*父节点被展开时调用 start*/
            /* onExpand:function(event, treeId, treeNode){
                var treeObj = $.fn.zTree.getZTreeObj("ztree");

                $.ajax({
                    url: "${ctx}/sys/office/treeDataOfficeForDoor",//请求的action路径
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
                                        	newNode = treeObj.addNodes(parentZNode,jsondata, false); */
            /**父节点再次展开时不需要进行异步加载**/
            /* treeNode.zAsync = true;
        }
    }
}
});
},	 */
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
            onClick:function(event, treeId, treeNode){
                var id = treeNode.pId == '0' ? treeNode.id :treeNode.id;
                //var pIds = id == '' ? treeNode.pIds : treeNode.pIds+id+",";
                $('#officeContent').attr("src","${ctx}${menuname}?doorId="+id);
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
        var leftWidth = ($("#left").width() <= 0 ? 0 : 250);
        $("#right").width($("#content").width()- leftWidth - $("#openClose").width() -5);
        $(".ztree").width(leftWidth - 10).height(frameObj.height() - 116);
    }
</script>
<script src="${ctxStatic}/common/wsize.min.js" type="text/javascript"></script>
</body>
</html>