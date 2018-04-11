<%@ page language="java" contentType="text/html;charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<section class="content-header">
	<h1>
		今日实时充值
	</h1>
	<ol class="breadcrumb">
		<li><a href="${pageContext.request.contextPath}/welcome/"><i class="fa fa-dashboard"></i> 首页</a></li>
		<li>实时数据</li>
		<li class="active">今日实时充值</li>
	</ol>
</section>
<section class="content">
	<div class="row">
		<div class="col-xs-12">
			<div class="panel-body" style="padding:0px; padding-bottom:20px;">
				<div style="font: bold 12px Arial, Helvetica, sans-serif; margin: 0; padding: 0; min-width: 600px; color: #bbbbbb;">
					<div style="background: #202020; margin: 0 auto; padding: 30px; border: 1px solid #333;">
						<div style="font-family: 'BebasNeueRegular', Arial, Helvetica, sans-serif; font-size: 36px; text-align: center; text-shadow: 0 0 5px #00c6ff;"></div>
						<ul style="margin: 0 auto; padding: 0px; list-style: none; text-align: center;">
							<li id="realtime-recharge-day" style="display: inline; font-size: 10em; text-align: center; font-family: 'BebasNeueRegular', Arial, Helvetica, sans-serif; text-shadow: 0 0 5px #00c6ff; color: #FCFCFC;">Loading...</li>
						</ul>
					</div>
				</div>
			</div>
			<div class="box">
				<div class="box-header">
					<h3 class="box-title">
					</h3>
					<div class="box-body">
						<div id="charge_container"></div>
					</div>
				</div>
			</div>
		</div>
	</div>
</section>
<script>
	<c:forEach var="e" items="${result.onlineData}">
		categories.push('${e.localTime}');
		today.push(${e.todayData});
		yestoday.push(${e.yesterData});
	</c:forEach>
	var times = [];
	var chargeCount = [];
	var order = [];
	var chargeNum = [];
	function formatHours(){
		for(var i=0;i<times.length;i++){
			times[i] = times[i] + "时";
		}
	}
	formatHours();
	$(function () {
		$('#charge_container').highcharts({
	        chart: {
	            zoomType: 'xy'
	        },
	        title: {
	            text: '时间段内充值情况'
	        },
	        xAxis: [{
	            categories: times,
	            crosshair: true
	        }],
	        yAxis: [{ // Primary yAxis
	            labels: {
	                style: {
	                    color: Highcharts.getOptions().colors[2]
	                }
	            },
	            title: {
	                text: '充值人数（人）',
	                style: {
	                    color: Highcharts.getOptions().colors[2]
	                }
	            },
	            opposite: true
	        }, { // Secondary yAxis
	            gridLineWidth: 0,
	            title: {
	                text: '充值金额（元）',
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
	                text: '订单数（单）',
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
	            x: 80,
	            verticalAlign: 'top',
	            y: 55,
	            floating: true,
	            backgroundColor: (Highcharts.theme && Highcharts.theme.legendBackgroundColor) || '#FFFFFF'
	        },
	        series: [{
	            name: '充值金额',
	            type: 'column',
	            yAxis: 1,
	            data: chargeCount,
	            tooltip: {
	                valueSuffix: ' 元'
	            }
	        }, {
	            name: '订单数',
	            type: 'spline',
	            yAxis: 2,
	            data: order,
	            marker: {
	                enabled: false
	            },
	            dashStyle: 'shortdot',
	            tooltip: {
	                valueSuffix: ' 单'
	            }
	        }, {
	            name: '充值人数',
	            type: 'spline',
	            data: chargeNum,
	            tooltip: {
	                valueSuffix: ' 人'
	            }
	        }],
	        credits: {
	        	enabled: false
        	}
	    });
	});
	
	var request;//申明一个句柄
	if(request != null)
	  request.abort();
	
	function updateRecharge(cache) {
		var reg=/ /g;
		request =$.get('${pageContext.request.contextPath}/survey/charge/query/?cache='+cache, function(data) {
			if(data){
				var result = eval("("+data+")");
				times = result.times;
				chargeCount = result.chargeCount;
				order = result.order;
				chargeNum = result.chargeNum;
				formatHours();
				$('#realtime-recharge-day').html("￥" + result.chargeSum);
				var chart = $('#charge_container').highcharts();
				chart.xAxis[0].setCategories(times, true);//更新数据
				chart.series[0].setData(chargeCount, true);
				chart.series[1].setData(order, true);
				chart.series[2].setData(chargeNum, true);
				chart.redraw();//重绘图形
			}
			updateRecharge(true);//每次请求完成,再发一次请求,避免客户端定时刷新来获取数据
		});
	}
	updateRecharge(false);
</script>