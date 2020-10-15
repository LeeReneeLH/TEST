<%@ tag language="java" pageEncoding="UTF-8"%>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<%@ attribute name="name" type="java.lang.String" required="true" description="验证码输入框名称"%>
<%@ attribute name="inputCssStyle" type="java.lang.String" required="false" description="验证框样式"%>
<%@ attribute name="imageCssStyle" type="java.lang.String" required="false" description="验证码图片样式"%>
<%@ attribute name="buttonCssStyle" type="java.lang.String" required="false" description="看不清按钮样式"%>
<input type="text" id="${name}" name="${name}" maxlength="5" class="txt required" style="font-weight:bold;${inputCssStyle};height:14px;"/>
<img src="${pageContext.request.contextPath}/servlet/validateCodeServlet" onclick="$('.${name}Refresh').click();" class="mid ${name} b" style="${imageCssStyle}"/>
<a href="javascript:" onclick="$('.${name}').attr('src','${pageContext.request.contextPath}/servlet/validateCodeServlet?'+new Date().getTime());" class="mid ${name}Refresh c" style="${buttonCssStyle}">看不清</a>

<style>
@media (min-width: 1900px) {
	.txt{
		width: 120px;
	}	
}
@media only screen and (min-width: 1735px) and (max-width: 1910px){ /* 110% */
	.txt{
		width: 98px;
	}
	.b{
		height: 27px;
	}
	.c{
		font-size:16px;
	}	
}
@media only screen and (min-width: 1526px) and (max-width: 1735px){ /* 125% */
	.txt{
		width: 98px;
	}
	.b{
		height: 27px;
	}
	.c{
		font-size:16px;
	}	
}
@media only screen and (min-width: 1270px) and (max-width: 1526px){  /* 150% */
	.txt{
		width: 80px;
	}
	.b{
		height: 22px;
	}
	.c{
		font-size:12px;
	}	
}
@media only screen and (min-width: 1087px) and (max-width: 1270px){ /* 175% */
	.txt{
		width: 55px;
		height: 15px;
	}
	.b{
		height: 15px;
	}
	.c{
		font-size:12px;
	}	
}
@media only screen and (min-width: 950px) and (max-width: 1087px){ /* 200% */
	.txt{
		width: 55px;
		height: 15px;
	}
	.b{
		height: 15px;
	}
	.c{
		font-size:12px;
	}	
}
@media only screen and (min-width: 751px) and (max-width: 950px){ /* 250% */
	.txt{
		width: 55px;
	}
	.b{
		height: 15px;
	}
	.c{
		font-size:12px;
	}	
}
</style>