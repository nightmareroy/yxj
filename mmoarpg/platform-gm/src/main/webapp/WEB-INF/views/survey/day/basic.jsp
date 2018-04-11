<%@ page language="java" contentType="text/html;charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<div class="col-md-3 col-sm-6 col-xs-12">
	<div class="info-box bg-aqua">
		<span class="info-box-icon"><i class="fa fa-user-plus"></i></span>

		<div class="info-box-content">
			<span class="info-box-text">新增玩家</span> <span class="info-box-number">${data.todayCreateNum }</span>

			<div class="progress">
				<div class="progress-bar" style="width: ${100 * data.todayCreateNum / data.yesterdayCreateNum}%"></div>
			</div>
			<span class="progress-description"> 今天新增约占昨日新增的<fmt:formatNumber value="${100 * data.todayCreateNum / data.yesterdayCreateNum}" pattern="#0.00" />%</span>
		</div>
	</div>
</div>

<div class="col-md-3 col-sm-6 col-xs-12">
	<div class="info-box bg-yellow">
		<span class="info-box-icon"><i class="fa fa-users"></i></span>

		<div class="info-box-content">
			<span class="info-box-text">日活跃数</span> <span class="info-box-number">${data.todayDAU }</span>

			<div class="progress">
				<div class="progress-bar" style="width: ${100 * data.todayDAU / data.yesterdayDAU}%"></div>
			</div>
			<span class="progress-description"> 今天活跃约占昨日活跃的<fmt:formatNumber value="${100 * data.todayDAU / data.yesterdayDAU}" pattern="#0.00" />% </span>
		</div>
	</div>
</div>

<div class="col-md-3 col-sm-6 col-xs-12">
	<div class="info-box bg-green">
		<span class="info-box-icon"><i class="fa fa-line-chart"></i></span>

		<div class="info-box-content">
			<span class="info-box-text">最高在线</span> <span class="info-box-number">${data.todayOnlineNum }</span>

			<div class="progress">
				<div class="progress-bar" style="width: ${100 * data.todayOnlineNum / data.yesterdayOnlineNum}%"></div>
			</div>
			<span class="progress-description"> 今天在线约占昨日在线的<fmt:formatNumber value="${100 * data.todayOnlineNum / data.yesterdayOnlineNum}" pattern="#0.00" />% </span>
		</div>
	</div>
</div>

<div class="col-md-3 col-sm-6 col-xs-12">
	<div class="info-box bg-red">
		<span class="info-box-icon"><i class="fa fa-rmb"></i></span>

		<div class="info-box-content">
			<span class="info-box-text">充值金额</span> <span class="info-box-number"><fmt:formatNumber value="${data.todayRMB / 100}" pattern="#,#00.00#"/></span>

			<div class="progress">
				<div class="progress-bar" style="width: ${100 * data.todayRMB / data.yesterdayRMB}%"></div>
			</div>
			<span class="progress-description"> 今天付费约占昨日付费的<fmt:formatNumber value="${100 * data.todayRMB / data.yesterdayRMB}" pattern="#0.00" />% </span>
		</div>
	</div>
</div>