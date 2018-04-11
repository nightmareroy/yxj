<%@ page language="java" contentType="text/html;charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<section class="content-header">
	<h1>
		每日总况 <small id="reservationtime"><i class="fa fa-calendar"> <u>${now}</u></i></small>
	</h1>
	<ol class="breadcrumb">
		<li><a href="${pageContext.request.contextPath}/welcome/"><i class="fa fa-dashboard"></i> Home</a></li>
		<li class="active">每日总况</li>
	</ol>
</section>

<script>
$(function() {
	$('#reservationtime').daterangepicker({
	    "singleDatePicker": true,
	    "startDate": "${now}",
	    "endDate": "${now}",
	    "maxDate": "${now}",
	    locale: {
            format: 'YYYY-MM-DD'
        },
	}, function(start, end, label) {
		$('#reservationtime').html("<i class='fa fa-calendar'> <u>"+start.format('YYYY-MM-DD')+"</u></i>");
		refresh(start.format('YYYY-MM-DD'));
	});
	
	// 刷新整个界面
	var refresh = function(today) {
		ajaxLoadPage2Div("basic", "${pageContext.request.contextPath}/survey/day/basic/?today="+today);
		ajaxLoadPage2Div("time", "${pageContext.request.contextPath}/survey/day/time/?today="+today);
		ajaxLoadPage2Div("conversion", "${pageContext.request.contextPath}/survey/day/conversion/?today="+today);
		ajaxLoadPage2Div("shop", "${pageContext.request.contextPath}/survey/day/shop/?today="+today);
	}
	
	refresh("");//默认刷新
});
</script>

<section class="content">
	<!-- 基本信息 -->
	<div id="basic" class="row"></div>
	
	<!-- 时间段充值数据 -->
	<div class="row">
		<div id="time" class="col-md-12"></div>
	</div>

	<div class="row">
		<div class="col-md-6" id="conversion"></div>
		<div class="col-md-6" id="shop"></div>
	</div>
</section>