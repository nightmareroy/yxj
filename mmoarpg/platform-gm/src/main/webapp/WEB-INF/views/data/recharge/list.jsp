<%@ page language="java" contentType="text/html;charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<section class="content-header">
	<h1>充值大盘走势</h1>
	<ol class="breadcrumb">
		<li><a href="${pageContext.request.contextPath}/welcome/"><i class="fa fa-dashboard"></i> 首页</a></li>
		<li>数据仓库</li>
		<li class="active">充值大盘走势</li>
	</ol>
</section>
<section class="content">

	<!-- 选服列表 -->
	<jsp:include page="../../serverlist.jsp">
		<jsp:param name="multiselect" value="true" />
		<jsp:param name="dosomething" value="javascript:selectServerId(this, true);" />
	</jsp:include>
	
	<div class="box box-primary">
		<div class="box-header with-border">
			<h3 class="box-title">查询条件</h3>
		</div>
		<div class="box-body">
			<form role="form" action="${pageContext.request.contextPath}/data/recharge/list/" method="post" id="queryData">
				<div class="form-group">
					<label>查询时间: </label>
					<div class="input-group">
						<div class="input-group-addon">
							<i class="fa fa-clock-o"></i>
						</div>
						<input type="text" class="form-control pull-right" id="reservationtime" value="${reservationtime}">
					</div>
				</div>
				<button id="btn_query_rank" type="submit" class="btn btn-primary btn-lg btn-block">查询</button>
			</form>
		</div>
	</div>
	
	<div class="row">
		<div class="col-xs-12">
			<div class="box">
				<div class="box-header">
					<h3 class="box-title">
						<i class="fa fa-list"></i> 充值大盘走势
					</h3>
					<div class="box-body">
						<div id="recharge_container"></div>
					</div>
				</div>
			</div>
		</div>
	</div>
</section>
<script>
$('#queryData').on('submit', function() {
	var serverIds = [];
	$("button[type=button][name=server]").each(function() {
		if (!$(this).hasClass("btn-default")) {
			serverIds.push($(this).val());
		}
	});
	var $btn = $("#btn_query_rank").button('loading');
	ajaxLoadPage2Body($(this).attr("action")+"?serverIds="+serverIds.join(',')+"&reservationtime="+$("#reservationtime").val());
	return false;
});
</script>
<script>
$(function() {
	$('#reservationtime').daterangepicker({
		"maxDate": "${maxtime}",
        locale: {
            format: 'YYYY-MM-DD'
        },
        ranges: {
            '今天': [moment(), moment()],
            '昨天': [moment().subtract(1, 'days'), moment().subtract(1, 'days')],
            '最近7天': [moment().subtract(6, 'days'), moment()],
            '最近30开': [moment().subtract(29, 'days'), moment()],
            '这个月': [moment().startOf('month'), moment().endOf('month')],
            '上个月': [moment().subtract(1, 'month').startOf('month'), moment().subtract(1, 'month').endOf('month')]
         }
	});
});
</script>
<script>
$(function () {
    $('#recharge_container').highcharts({
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
            categories: ${data.chargeDays},
            crosshair: true
        }],
        yAxis: [{ // Primary yAxis
            labels: {
                style: {
                    color: Highcharts.getOptions().colors[2]
                }
            },
            title: {
                text: '充值订单数(单位：次)',
                style: {
                    color: Highcharts.getOptions().colors[2]
                }
            },
            opposite: true
        }, { // Secondary yAxis
            gridLineWidth: 0,
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
            }
        }, { // Tertiary yAxis
            gridLineWidth: 0,
            title: {
                text: '充值人数(单位：人)',
                style: {
                    color: Highcharts.getOptions().colors[1]
                }
            },
            labels: {
                style: {
                    color: Highcharts.getOptions().colors[1]
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
            x: 150,
            verticalAlign: 'top',
            y: 10,
            floating: true,
            backgroundColor: (Highcharts.theme && Highcharts.theme.legendBackgroundColor) || '#FFFFFF'
        },
        series: [{
            name: '充值金额',
            type: 'column',
            yAxis: 1,
            data: doSomething(${data.chargeRmbs}, function(v){return v/100;}),
            tooltip: {
                valueSuffix: ' 元'
            }
        }, {
            name: '充值人数',
            type: 'spline',
            yAxis: 2,
            data: ${data.chargeNums},
            marker: {
                enabled: false
            },
            dashStyle: 'shortdot',
            tooltip: {
                valueSuffix: ' 人'
            }
        }, {
            name: '充值订单数',
            type: 'spline',
            data: ${data.chargeCounts},
            tooltip: {
                valueSuffix: ' 次'
            }
        }]
    });
});
</script>