<%@ page language="java" contentType="text/html;charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<section class="content-header">
	<h1>等级分布:<small id="today"><i class="fa fa-calendar"><u> ${empty today?"全部":today}</u></i></small></h1>
	<ol class="breadcrumb">
		<li><a href="${pageContext.request.contextPath}/welcome/"><i class="fa fa-dashboard"></i> 首页</a></li>
		<li>实时数据</li>
		<li class="active">等级分布</li>
	</ol>
</section>
<section class="content">
	<div class="row">
		<div class="col-xs-12">
			<div class="box">
				<div class="box-header">
					<h3 class="box-title">
						<i class="fa fa-list"></i> 等级分布情况
					</h3>
					<div class="box-body">
						<div id="container"></div>
					</div>
				</div>
			</div>
		</div>
	</div>
</section>
<script>
var categories = [];
var xdata = [];

<c:forEach var="e" items="${result}">
categories.push(${e.data});
xdata.push(${e.level});

</c:forEach>

$(function () {
    $('#container').highcharts({
	credits : {
		enabled : false
	},
	chart : {
		zoomType : 'xy'
	},
	title : {
		text : '等级分布'
	},
	xAxis : [ {
		categories : categories
	} ],
	yAxis : [ { // Primary yAxis
		labels : {
			style : {
				color : '#89A54E'
			}
		},
		title : {
			text : '当前人数',
			style : {
				color : '#89A54E'
			}
		}
	}, { // Secondary yAxis
		title : {
			text : '',
			style : {
				color : '#4572A7'
			}
		},
		labels : {
			style : {
				color : '#4572A7'
			}
		},
		opposite : true
	} ],
	tooltip : {
		shared : true
	},
	legend : {
		layout : 'vertical',
		align : 'left',
		x : 120,
		verticalAlign : 'top',
		y : 50,
		floating : true,
		backgroundColor : '#FFFFFF'
	},
	series : [ {
		name : '人数',
		color : '#4572A7',
		type : 'column',
		yAxis : 1,
		data : xdata,
		tooltip : {
			valueSuffix : '人'
		}

	},]
	});
    
    $('#today').daterangepicker({
	    "singleDatePicker": true,
	    "startDate": "${empty today?maxtime:today}",
	    "maxDate": "${maxtime}",
	    locale: {
            format: 'YYYY-MM-DD'
        },
	}, function(start, end, label) {
		ajaxLoadPage2Body("${pageContext.request.contextPath}/survey/level/list/?today="+start.format('YYYY-MM-DD'));
	});
});
</script>