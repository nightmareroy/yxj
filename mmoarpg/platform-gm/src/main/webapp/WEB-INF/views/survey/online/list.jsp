<%@ page language="java" contentType="text/html;charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<section class="content-header">
	<h1>
		实时查询： <small id="reservationtime"><i class="fa fa-calendar"> ${today}</i></small>
	</h1>
	<ol class="breadcrumb">
		<li><a href="${pageContext.request.contextPath}/welcome/"><i class="fa fa-dashboard"></i> 首页</a></li>
		<li>实时数据</li>
		<li class="active">实时在线</li>
	</ol>
</section>
<section class="content">
	<div class="row">
		<div class="col-xs-12">
			<div class="box">
				<div class="box-header">
					<h3 class="box-title">
						<i class="fa fa-list"></i> 在线情况
					</h3>
					<div class="box-body">
						<div id="online_container"></div>
					</div>
				</div>
			</div>
		</div>
	</div>
	<div class="box">
		<div class="box-header with-border">
			<div class="box-header with-border">
				<h3 class="box-title">在线数据展示</h3>
			</div>
			<div class="box-body">
				<c:choose>
					<c:when test="${empty result}">没有找到记录</c:when>
					<c:otherwise>
						<table class="table table-bordered table-striped" id="online">
							<thead>
								<tr>
									<th>时间</th>
									<th>今天在线人数</th>
									<th>昨天在线人数</th>
								</tr>
							</thead>
							<tbody>
								<c:forEach var="e" items="${result.onlineData}">
									<tr role="row" class="odd">
										<td>${e.localDate}&nbsp;${e.localTime}</td>
										<td>${e.todayData}</td>
										<td>${e.yesterData}</td>
									</tr>
								</c:forEach>
							</tbody>
						</table>
					</c:otherwise>
				</c:choose>
			</div>
		</div>
	</div>
</section>
<script>
	$(function() {
		$('#online').DataTable({
			language : {//国际化文件
				url : "${pageContext.request.contextPath}/resources/plugins/datatables/i18n/zh_CN.json"
			},
			dom : 'Bfrtip',
			buttons : [ {
	            extend: 'copy',
	            text:'复制'
	        }, {
	            extend: 'excel',
	            text:'导出',
	            title: '实时在线'
	        },  {
	            extend: 'print',
	            text:'打印'
	        } ]
		});
	});
</script>
<script>
	var categories = [];
	var today = [];
	var yestoday = [];
	<c:forEach var="e" items="${result.onlineData}">
		categories.push('${e.localTime}');
		today.push(${e.todayData});
		yestoday.push(${e.yesterData});
	</c:forEach>

	$(function () {
	    $('#online_container').highcharts({
	        chart: {
	            type: 'areaspline'
	        },
	        title: {
	            text: '在线曲线图'
	        },
	        colors: ['#BEBEBE', '#50B432'],
	        legend: {
	            layout: 'vertical',
	            align: 'left',
	            verticalAlign: 'top',
	            x: 150,
	            y: 100,
	            floating: true,
	            borderWidth: 1,
	            backgroundColor: '#FFFFFF'
	        },
	        xAxis: {
	            categories: categories,
	        },
	        yAxis: {
	            title: {
	                text: '在线人数'
	            }
	        },
	        tooltip: {
	            shared: true,
	            valueSuffix: ' 人'
	        },
	        credits: {
	            enabled: false
	        },
	        plotOptions: {
	            areaspline: {
	                fillOpacity: 0.3
	            }
	        },
	        series: [ {
	            name: '昨天在线',
	            data: yestoday
	        },{
	            name: '当前在线',
	            data: today
	        }]
	    });
	    $('#reservationtime').daterangepicker({
		    "singleDatePicker": true,
		    "startDate": "${today}",
		    "maxDate": "${maxtime}",
		    locale: {
	            format: 'YYYY-MM-DD'
	        },
		}, function(start, end, label) {
			ajaxLoadPage2Body("${pageContext.request.contextPath}/survey/online/list/?reservationtime="+start.format('YYYY-MM-DD'));
		});
	});
</script>


