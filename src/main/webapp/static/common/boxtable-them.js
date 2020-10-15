function wTableSize(){
    			//查询条的高度				盒子的高度
		var h = 159;//8+8+10 搜索框外边距  +18+16+2 table标题  35+18+18 页码 + 20+16+4  title +20 页面  +2 table
		/* console.log(h);
		 console.log($("#searchForm").height());
		 console.log($("#searchForm").height() + 159);*/
		 var searchH = $("#searchForm").height();	//记录查询框的高度
		if($(".boxhand-num").height() != null){
    		h = h + $("#box_sel").height(); 	
    		/*console.log(h);*/
    	}
    	if($("#clear_sel").height() != null){
    		h = h + $("#clear_sel").height(); 
    		/*console.log(h);*/
    	}
    	
    	h = h + $("#searchForm").height();
        $(".table-con").height("calc( 100vh - "+h+"px)");
       /* console.log($("#searchForm").height());*/
        if(searchH != $("#searchForm").height()){	// 绘完table后 可能会改变 查询框的高度  如果改变 重新绘制
        		h = h - searchH +  $("#searchForm").height();
        	  $(".table-con").height("calc( 100vh - "+h+"px)");
        }
        if($(".table-con").height() < 150){
        	$(".table-con").height("150px");
        }
      /*  console.log($(".boxhand-num").height());
        console.log($("#clear_sel").height());
        console.log(h);*/
}
$(window).resize(function(){
	wTableSize();
});
$(document).ready(function() {
	wTableSize();
});