<%@ page language="java" contentType="text/html;charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<div class="box">
	<div class="box-header with-border">
		<h3 class="box-title">
			<i class="fa fa-bar-chart">本月充值情况</i>
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
		<div id="container"></div>
	</div>
</div>
<script>
$(function () {
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