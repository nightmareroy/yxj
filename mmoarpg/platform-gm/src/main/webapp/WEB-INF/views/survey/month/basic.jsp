<%@ page language="java" contentType="text/html;charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<div class="col-md-3 col-sm-6 col-xs-12">
	<div class="info-box bg-aqua">
		<span class="info-box-icon"><i class="fa fa-user-plus"></i></span>

		<div class="info-box-content">
			<span class="info-box-text">月新增玩家</span> <span class="info-box-number">${data.todayCreateNum }</span>

			<div class="progress">
				<div class="progress-bar" style="width: ${100 * data.todayCreateNum / data.yesterdayCreateNum}%"></div>
			</div>
			<span class="progress-description"> 本月新增约占上月新增的<fmt:formatNumber value="${100 * data.todayCreateNum / data.yesterdayCreateNum}" pattern="#0.00" />%</span>
		</div>
	</div>
</div>

<div class="col-md-3 col-sm-6 col-xs-12">
	<div class="info-box bg-yellow">
		<span class="info-box-icon"><i class="fa fa-users"></i></span>

		<div class="info-box-content">
			<span class="info-box-text">月活跃数</span> <span class="info-box-number">${data.todayDAU }</span>

			<div class="progress">
				<div class="progress-bar" style="width: ${100 * data.todayDAU / data.yesterdayDAU}%"></div>
			</div>
			<span class="progress-description"> 本月活跃约占上月活跃的<fmt:formatNumber value="${100 * data.todayDAU / data.yesterdayDAU}" pattern="#0.00" />% </span>
		</div>
	</div>
</div>

<div class="col-md-3 col-sm-6 col-xs-12">
	<div class="info-box bg-green">
		<span class="info-box-icon"><i class="fa fa-line-chart"></i></span>

		<div class="info-box-content">
			<span class="info-box-text">月充值人数</span> <span class="info-box-number">${data.todayChargeNum }</span>

			<div class="progress">
				<div class="progress-bar" style="width: ${100 * data.todayChargeNum / data.yesterdayChargeNum}%"></div>
			</div>
			<span class="progress-description"> 本月在线约占上月在线的<fmt:formatNumber value="${100 * data.todayChargeNum / data.yesterdayChargeNum}" pattern="#0.00" />% </span>
		</div>
	</div>
</div>

<div class="col-md-3 col-sm-6 col-xs-12">
	<div class="info-box bg-red">
		<span class="info-box-icon"><i class="fa fa-rmb"></i></span>

		<div class="info-box-content">
			<span class="info-box-text">月充值金额</span> <span class="info-box-number"><fmt:formatNumber value="${data.todayRMB / 100}" pattern="#,#00.00#"/></span>

			<div class="progress">
				<div class="progress-bar" style="width: ${100 * data.todayRMB / data.yesterdayRMB}%"></div>
			</div>
			<span class="progress-description"> 本月付费约占上月付费的<fmt:formatNumber value="${100 * data.todayRMB / data.yesterdayRMB}" pattern="#0.00" />% </span>
		</div>
	</div>
</div>