<%@ page language="java" contentType="text/html;charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<section class="content-header">
	<h1>
		每月总况 <small id="reservationtime"><i class="fa fa-calendar"> <u><fmt:formatDate pattern="yyyy-MM" value="${now}"/></u></i></small>
	</h1>
	<ol class="breadcrumb">
		<li><a href="${pageContext.request.contextPath}/welcome/"><i class="fa fa-dashboard"></i> Home</a></li>
		<li class="active">每月总况</li>
	</ol>
</section>

<script>
$(function() {
	$('#reservationtime').datepicker({
		startView: 1,
	    minViewMode: 1,
	    maxViewMode: 2,
	    language: "zh-CN",
	    autoclose: true
	}).on("changeDate", function(e) {
		var today = e.date.getFullYear().toString() + "-"+ (e.date.getMonth()>8?(e.date.getMonth()+1).toString():"0"+(e.date.getMonth()+1).toString());
		$('#reservationtime').html("<i class='fa fa-calendar'> <u>"+today+"</u></i>");
		
		refresh(today);
    });
	
	// 刷新整个界面
	var refresh = function(today) {
		ajaxLoadPage2Div("basic", "${pageContext.request.contextPath}/survey/month/basic/?today="+today);
		ajaxLoadPage2Div("charge", "${pageContext.request.contextPath}/survey/month/charge/?today="+today);
	}
	
	refresh("");//默认刷新
});
</script>

<section class="content">
	<!-- 基本信息 -->
	<div id="basic" class="row"></div>
	<!-- 充值信息 -->
	<div class="row">
		<div id="charge" class="col-md-12"></div>
	</div>
</section>