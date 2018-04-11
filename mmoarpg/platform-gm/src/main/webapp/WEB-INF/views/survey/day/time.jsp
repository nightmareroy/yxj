<%@ page language="java" contentType="text/html;charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<div class="box">
	<div class="box-header with-border">
		<h3 class="box-title">
			<i class="fa fa-bar-chart">时间段内充值情况</i>
		</h3>
		<div class="box-tools pull-right">
			<button type="button" class="btn btn-box-tool" data-toggle="tooltip" data-original-title="提示....">
				<i class="fa fa-question"></i>
			</button>
			<button type="button" class="btn btn-box-tool" data-widget="collapse">
				<i class="fa fa-minus"></i>
			</button>
			<button type="button" class="btn btn-box-tool" data-widget="remove">
				<i class="fa fa-times"></i>
			</button>
		</div>
	</div>
	<div class="box-body">
		<div class="row">
			<div class="col-md-8">
				<div id="container" style="min-width:300px;height:280px"></div>
			</div>
			<div class="col-md-4">
				<p class="text-center">
					<strong>今日留存情况</strong>
				</p>
				<c:forEach var="e" items="${data.retaineds}">
					<div class="progress-group">
						<span class="progress-text">今天登录 / ${e.day}天前注册数</span>
						<span class="progress-number"><b>${e.loginNum}</b> / ${e.createNum}</span>
						<div class="progress sm">
							<div class="progress-bar progress-bar-aqua" style="width: ${e.loginNum * 100 / e.createNum}%"></div>
						</div>
					</div>
				</c:forEach>
			</div>
		</div>
	</div>
	
	<!-- 充值相关 -->
	<div class="box-footer">
		<div class="row">
			<div class="col-sm-3 col-xs-6">
				<div class="description-block border-right">
					<jsp:include page="show.jsp">
						<jsp:param name="today" value="${data.basicdata.todayChargeNum }" />
						<jsp:param name="yesterday" value="${data.basicdata.yesterdayChargeNum }" />
						<jsp:param name="show" value="integer" />
					</jsp:include>
					<span class="description-text">充值人数</span>
				</div>
			</div>
			<!-- 付费率：充值人数/活跃人数 -->
			<div class="col-sm-3 col-xs-6">
				<div class="description-block border-right">
					<jsp:include page="show.jsp">
						<jsp:param name="today" value="${data.basicdata.todayDAU==0?'':data.basicdata.todayChargeNum/data.basicdata.todayDAU}" />
						<jsp:param name="yesterday" value="${data.basicdata.yesterdayDAU==0?'':data.basicdata.yesterdayChargeNum/data.basicdata.yesterdayDAU}" />
						<jsp:param name="show" value="percentage" />
					</jsp:include>
					<span class="description-text">付费率</span>
				</div>
			</div>
			<!-- ARPPU值：充值总金额/充值人数 -->
			<div class="col-sm-3 col-xs-6">
				<div class="description-block border-right">
					<jsp:include page="show.jsp">
						<jsp:param name="today" value="${data.basicdata.todayChargeNum == 0?'0':data.basicdata.todayRMB/100/data.basicdata.todayChargeNum}" />
						<jsp:param name="yesterday" value="${data.basicdata.yesterdayChargeNum == 0 ?'0':data.basicdata.yesterdayRMB/100/data.basicdata.yesterdayChargeNum}" />
					</jsp:include>
					<span class="description-text">ARPPU</span>
				</div>
			</div>
			<!-- 活跃ARPU：充值总金额/活跃玩家数 -->
			<div class="col-sm-3 col-xs-6">
				<div class="description-block">
					<jsp:include page="show.jsp">
						<jsp:param name="today" value="${data.basicdata.todayDAU == 0 ? '0' : data.basicdata.todayRMB/100 / data.basicdata.todayDAU}" />
						<jsp:param name="yesterday" value="${data.basicdata.yesterdayDAU == 0 ? '0' : data.basicdata.yesterdayRMB/100 / data.basicdata.yesterdayDAU}" />
					</jsp:include>
					<span class="description-text">活跃ARPU</span>
				</div>
			</div>
		</div>
	</div>
</div>
<script>
$(function () {
	var timedata = [];
	var rmbdata = [];
	var numdata = [];
	<c:forEach var="e" items="${data.rechargeList}">
		timedata.push(${e.hour}+"点");	
		rmbdata.push(<fmt:formatNumber value="${e.rmb / 100}" pattern="#0.00#"/>);	
		numdata.push(${e.num});	
	</c:forEach>
    $('#container').highcharts({
    	credits: {
            enabled:false
  		},
        chart: {
            zoomType: 'xy'
        },
        title: {
            text: ''
        },
        xAxis: [{
            categories: timedata,
            crosshair: true
        }],
        yAxis: [{ // Primary yAxis
            labels: {
                style: {
                    color: Highcharts.getOptions().colors[1]
                }
            },
            title: {
                text: '充值人数(单位：人)',
                style: {
                    color: Highcharts.getOptions().colors[1]
                }
            }
        }, { // Secondary yAxis
            title: {
                text: '充值金额(单位：元)',
                style: {
                    color: Highcharts.getOptions().colors[0]
                }
            },
            labels: {
                style: {
                    color: Highcharts.getOptions().colors[0]
                }
            },
            opposite: true
        }],
        tooltip: {
            shared: true
        },
        legend: {
            layout: 'vertical',
            align: 'left',
            x: 100,
            verticalAlign: 'top',
            y: 50,
            floating: true,
            backgroundColor: (Highcharts.theme && Highcharts.theme.legendBackgroundColor) || '#FFFFFF'
        },
        series: [{
            name: '充值金额',
            type: 'column',
            yAxis: 1,
            data: rmbdata,
            tooltip: {
                valueSuffix: ' 元'
            }
        }, {
            name: '充值人数',
            type: 'spline',
            data: numdata,
            tooltip: {
                valueSuffix: '人'
            }
        }]
    });
});
</script>