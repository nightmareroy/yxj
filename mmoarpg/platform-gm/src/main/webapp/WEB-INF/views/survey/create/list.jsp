<%@ page language="java" contentType="text/html;charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<section class="content-header">
	<h1>
		实时创角查询： <small id="reservationtime"><i class="fa fa-calendar"> ${today}</i></small>
	</h1>
	<ol class="breadcrumb">
		<li><a href="${pageContext.request.contextPath}/welcome/"><i class="fa fa-dashboard"></i> 首页</a></li>
		<li>实时数据</li>
		<li class="active">实时创角</li>
	</ol>
</section>
<section class="content">
	<div class="row">
		<div class="col-xs-12">
			<div class="box">
				<div class="box-header">
					<h3 class="box-title">
						<i class="fa fa-list"></i> 创角情况
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
 $('#reservationtime').daterangepicker({
     "singleDatePicker": true,
     "startDate": "${today}",
     "maxDate": "${maxtime}",
     locale: {
         format: 'YYYY-MM-DD'
      },
   },function(start, end, label) {
	  ajaxLoadPage2Body("${pageContext.request.contextPath}/survey/create/list/?reservationtime="+start.format('YYYY-MM-DD'));
  });
</script>
<script>
	var categories = ${result.hour};
	var createNum = ${result.count};
	function formatHours(){
		for(var i=0;i<categories.length;i++){
			categories[i] = categories[i] + "时";
		}
	}
	formatHours();
	$(function () {
	    $('#container').highcharts({
	        chart: {
	            type: 'spline'
	        },
	        title: {
	            text: '实时创角人数'
	        },
	        xAxis: {
	            categories: categories
	        },
	        yAxis: {
	            title: {
	                text: '当天累计创角人数（人）'
	            }
	        },
	        plotOptions: {
	            line: {
	                dataLabels: {
	                    enabled: true          // 开启数据标签
	                },
	                enableMouseTracking: false // 关闭鼠标跟踪，对应的提示框、点击事件会失效
	            }
	        },
	        series: [{
	            name: '创角',
	            data: createNum
	        }],
	        credits: {
	        	enabled: false
        	}
	    });
	});
</script>
