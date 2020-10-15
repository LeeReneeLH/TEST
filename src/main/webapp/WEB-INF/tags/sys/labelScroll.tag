<%@ tag language="java" pageEncoding="UTF-8"%>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<%@ attribute name="id" required="true" type="java.lang.String" description="Id"%>
<%@ attribute name="liHeight" required="false" type="java.lang.String"  description="高度"%>

<style type="text/css">
	.scrollDiv {width:100%; overflow:hidden; word-wrap:break-word;}
	
	.slide-ul-list{
	   margin:0;
	    padding:0;
	}
	.slide-ul-list li {
	  list-style:none;
	   margin:0;
	   padding-top:0px;
	}
	.slide-ul-list li span {
	  display:inline-block;
	  overflow:hidden;
	  white-space:nowrap;
	}
	
	
</style>
	
<script type="text/javascript">

//数字滚动js

(function ($) {
    $.fn.vTicker = function (options) {
    var defaults = {
        speed: 900,
        pause: 4000,
        showItems: 1,
        animation: '',
        mousePause: true,
        isPaused: false,
        direction: 'up',
		nextValue: '',
        height: 0
    };

     var options = $.extend(defaults, options);

    moveUp = function (obj2, height, options) {

        if (options.isPaused)
            return;

        var obj = obj2.children('ul');

        var clone = obj.children('li:first').clone(true);

		if (options.nextValue != ''){
			obj.children('li')[1].innerHTML = options.nextValue;
		}else{
			return;
		}


        if (options.height > 0) {
            height = obj.children('li:first').height();
        }

        obj.animate({ top: '-=' + height + 'px' }, options.speed, function () {
            $(this).children('li:first').remove();
            $(this).css('top', '0px');
        });

        if (options.animation == 'fade') {
            obj.children('li:first').fadeOut(options.speed);
            if (options.height == 0) {
                obj.children('li:eq(' + options.showItems + ')').hide().fadeIn(options.speed).show();
            }
        }

        clone.appendTo(obj);
    };

    moveDown = function (obj2, height, options) {
		if (options.pausedId != ''){
			var strPausedId = $('#' + options.pausedId).val();
			if (strPausedId == "1")
			{
				                return;
			}
		}
        if (options.isPaused)
            return;

        var obj = obj2.children('ul');

        var clone = obj.children('li:last').clone(true);

        if (options.height > 0) {
            height = obj.children('li:first').height();
        }

        obj.css('top', '-' + height + 'px')
		.prepend(clone);

        obj.animate({ top: 0 }, options.speed, function () {
            $(this).children('li:last').remove();
        });

        if (options.animation == 'fade') {
            if (options.height == 0) {
                obj.children('li:eq(' + options.showItems + ')').fadeOut(options.speed);
            }
            obj.children('li:first').hide().fadeIn(options.speed).show();
        }
    };

    return this.each(function () {
        var obj = $(this);
        var maxHeight = 0;

        obj.css({ overflow: 'hidden', position: 'relative' })
		.children('ul').css({ position: 'absolute', margin: 0, padding: 0 })
		.children('li').css({ margin: 0, padding: 0 });

        if (options.height == 0) {
            obj.children('ul').children('li').each(function () {
                if ($(this).height() > maxHeight) {
                    maxHeight = $(this).height();
                }
            });

            obj.children('ul').children('li').each(function () {
                $(this).height(maxHeight);
            });

            obj.height(maxHeight * options.showItems);
        }
        else {
            obj.height(options.height);
        }

        //var interval = setInterval(function () {
        //    if (options.direction == 'up') {
        //        moveUp(obj, maxHeight, options);
        //    }
        //    else {
        //        moveDown(obj, maxHeight, options);
         //   }
        //}, options.pause);
        //border:1px solid #F00;

            if (options.direction == 'up') {
                moveUp(obj, maxHeight, options);
            }
            else {
                moveDown(obj, maxHeight, options);
            }

        if (options.mousePause) {
            obj.bind("mouseenter", function () {
                options.isPaused = true;
            }).bind("mouseleave", function () {
                options.isPaused = false;
            });
        }
    });
};
})(jQuery);

</script>

<div id="${id}"  class="scrollDiv" style= "float:right;" >
	<ul class="slide-ul-list" style= "width:100%">
		<li style="height:${liHeight};line-height:${liHeight};"></li>
		<li style="height:${liHeight};line-height:${liHeight};"></li>
	</ul>
	<input id="hid_${id}"  type="hidden" value=""/>
</div>