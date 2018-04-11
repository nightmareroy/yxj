<%@ page language="java" contentType="text/html;charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<section class="content-header">
	<h1>
		每日蓝钻礼包领取<small id="selectTime"><i class="fa fa-calendar">${day}</i></small>
	</h1>
	<ol class="breadcrumb">
		<li><a href="${pageContext.request.contextPath}/welcome/"><i class="fa fa-dashboard"></i> 首页</a></li>
		<li>统计分析</li>
		<li class="active">每日蓝钻礼包领取</li>
	</ol>
</section>
<section class="content">
	<div class="box">
		<div class="box-header with-border">
        	<h3 class="box-title">每日蓝钻礼包领取人数</h3>
        </div>
		<div class="box-body">
			<table class="table table-bordered">
				<tbody>
					<tr>
						<th>领取礼包总人数</th>
						<th>领取贵族礼包人数</th>
						<th>领取豪华礼包人数</th>
						<th>领取年费礼包人数</th>
					</tr>
					<tr>
						<td>${data.people}</td>
						<td>${data.type1}</td>
						<td>${data.type2}</td>
						<td>${data.type3}</td>
					</tr>
				</tbody>
			</table>
		</div>
	</div>
	<div class="box">
		<div style="width:100%;height:400px;position:absolute;background:#ffffff;">
			<div style="width:400px;height:400px;position:absolute;top:0px;margin-left:-200px;left:50%;">
				<div id="container"></div>
			</div>
		</div>
	</div>
</section>
<script>
	var count = ${data.people};
	var type1 = ${data.type1};
	var type2 = ${data.type2};
	var type3 = ${data.type3};
	$('#selectTime')
			.daterangepicker(
					{
						singleDatePicker : true,
						showDropdowns : true,
						timePicker24Hour : true,
						maxDate : "${maxtime}",
						locale : {
							format : 'YYYY-MM-DD',
							applyLabel : '确定',
							cancelLabel : '取消',
							fromLabel : '起始时间',
							toLabel : '结束时间',
							customRangeLabel : '自定义起始日期',
							daysOfWeek : [ '日', '一', '二', '三', '四', '五', '六' ],
							monthNames : [ '一月', '二月', '三月', '四月', '五月', '六月',
									'七月', '八月', '九月', '十月', '十一月', '十二月' ]
						},
						ranges : {
							'今天' : [ moment(), moment() ],
							'昨天' : [ moment().subtract(1, 'days'),
									moment().subtract(1, 'days') ],
							'最近7天' : [ moment().subtract(6, 'days'), moment() ],
							'最近30天' : [ moment().subtract(29, 'days'), moment() ],
							'这个月' : [ moment().startOf('month'),
									moment().endOf('month') ],
							'上个月' : [
									moment().subtract(1, 'month').startOf(
											'month'),
									moment().subtract(1, 'month')
											.endOf('month') ]
						}
					},
					function(start, end, label) {
						var queryUri = "${pageContext.request.contextPath}/statistics/blue/list/?day="
								+ start.format('YYYY-MM-DD');
						ajaxLoadPage2Body(queryUri);
					});
</script>
<script>
	    Highcharts.chart('container', {
	        chart: {
	            type: 'solidgauge',
	            marginTop: 50
	        },
	        credits: {
	        	enabled: false
	       	},
	        title: {
	            text: '礼包领取',
	            style: {
	                fontSize: '24px'
	            }
	        },
	        tooltip: {
	            borderWidth: 0,
	            backgroundColor: 'none',
	            shadow: false,
	            style: {
	                fontSize: '16px'
	            },
	            pointFormat: '{series.name}<br><span style="font-size:2em; color: {point.color}; font-weight: bold">{point.y}人</span>',
	            positioner: function (labelWidth, labelHeight) {
	                return {
	                    x: 200 - labelWidth / 2,
	                    y: 180
	                };
	            }
	        },
	        pane: {
	            startAngle: 0,
	            endAngle: 360,
	            background: [{ // Track for Move
	                outerRadius: '112%',
	                innerRadius: '88%',
	                backgroundColor: Highcharts.Color(Highcharts.getOptions().colors[0]).setOpacity(0.3).get(),
	                borderWidth: 0
	            }, { // Track for Exercise
	                outerRadius: '87%',
	                innerRadius: '63%',
	                backgroundColor: Highcharts.Color(Highcharts.getOptions().colors[1]).setOpacity(0.3).get(),
	                borderWidth: 0
	            }, { // Track for Stand
	                outerRadius: '62%',
	                innerRadius: '38%',
	                backgroundColor: Highcharts.Color(Highcharts.getOptions().colors[2]).setOpacity(0.3).get(),
	                borderWidth: 0
	            }]
	        },
	        yAxis: {
	            min: 0,
	            max: count,
	            lineWidth: 0,
	            tickPositions: []
	        },
	        plotOptions: {
	            solidgauge: {
	                borderWidth: '34px',
	                dataLabels: {
	                    enabled: false
	                },
	                linecap: 'round',
	                stickyTracking: false
	            }
	        },
	        series: [{
	            name: '贵族礼包',
	            borderColor: Highcharts.getOptions().colors[0],
	            data: [{
	                color: Highcharts.getOptions().colors[0],
	                radius: '100%',
	                innerRadius: '100%',
	                y: type1
	            }]
	        }, {
	            name: '豪华礼包',
	            borderColor: Highcharts.getOptions().colors[1],
	            data: [{
	                color: Highcharts.getOptions().colors[1],
	                radius: '75%',
	                innerRadius: '75%',
	                y: type2
	            }]
	        }, {
	            name: '年费礼包',
	            borderColor: Highcharts.getOptions().colors[2],
	            data: [{
	                color: Highcharts.getOptions().colors[2],
	                radius: '50%',
	                innerRadius: '50%',
	                y: type3
	            }]
	        }]
	    },
	                     /**
	     * In the chart load callback, add icons on top of the circular shapes
	     */
	                     function callback() {
	        // Move icon
	        this.renderer.path(['M', -8, 0, 'L', 8, 0, 'M', 0, -8, 'L', 8, 0, 0, 8])
	            .attr({
	            'stroke': '#303030',
	            'stroke-linecap': 'round',
	            'stroke-linejoin': 'round',
	            'stroke-width': 2,
	            'zIndex': 10
	        })
	            .translate(190, 26)
	            .add(this.series[2].group);
	        // Exercise icon
	        this.renderer.path(['M', -8, 0, 'L', 8, 0, 'M', 0, -8, 'L', 8, 0, 0, 8, 'M', 8, -8, 'L', 16, 0, 8, 8])
	            .attr({
	            'stroke': '#fff',
	            'stroke-linecap': 'round',
	            'stroke-linejoin': 'round',
	            'stroke-width': 2,
	            'zIndex': 10
	        })
	            .translate(190, 61)
	            .add(this.series[2].group);
	        // Stand icon
	        this.renderer.path(['M', 0, 8, 'L', 0, -8, 'M', -8, 0, 'L', 0, -8, 8, 0])
	            .attr({
	            'stroke': '#303030',
	            'stroke-linecap': 'round',
	            'stroke-linejoin': 'round',
	            'stroke-width': 2,
	            'zIndex': 10
	        })
	            .translate(190, 96)
	            .add(this.series[2].group);
	    });
</script>