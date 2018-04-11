<%@ page language="java" contentType="text/html;charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>

<section class="content-header">
	<h1>查询排行信息</h1>
	<ol class="breadcrumb">
		<li><a href="${pageContext.request.contextPath}/welcome/"><i class="fa fa-dashboard"></i> 首页</a></li>
		<li>游戏管理</li>
		<li class="active">查询排行信息</li>
	</ol>
</section>

<section class="content">

	<!-- 选服列表 -->
	<jsp:include page="../../../serverlist.jsp">
		<jsp:param name="multiselect" value="false" />
		<jsp:param name="dosomething" value="javascript:selectServerId(this, false);" />
	</jsp:include>

	<div class="nav-tabs-custom">
		<ul class="nav nav-tabs">
   			<li class="active"><a href="#tab_1" data-toggle="tab">充值榜</a></li>
          	<li><a href="#tab_2" data-toggle="tab">战力榜</a></li>
          	<li><a href="#tab_3" data-toggle="tab">等级榜</a></li>
       	</ul>
      	<div class="tab-content">
        	<div class="tab-pane active" id="tab_1">
        	sadfasdfsd
       		</div>
       		<div class="tab-pane" id="tab_2">
       		
       		2222222
       		</div>
       		<div class="tab-pane" id="tab_3">
       		
       		3
       		</div>
        </div>
	</div>
</section>
<script>
	$(function() {		
		$("#tab_1").click(function(e){
			alert(11);
	    });
	});
</script>