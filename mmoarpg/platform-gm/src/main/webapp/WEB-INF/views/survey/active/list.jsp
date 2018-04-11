<%@ page language="java" contentType="text/html;charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<section class="content-header">
	<h1>
		日常活跃度<small id="active-time" style="cursor:pointer;"><i class="fa fa-calendar"> ${today}</i></small>
	</h1>
	<ol class="breadcrumb">
		<li><a href="${pageContext.request.contextPath}/welcome/"><i class="fa fa-dashboard"></i> 首页</a></li>
		<li>实时数据</li>
		<li class="active">日常活跃度</li>
	</ol>
</section>
<section class="content">
	<div class="row">
		<div class="col-xs-12">
			<div class="box">
				<div class="box-header">
					<h3 class="box-title">
						<i class="fa fa-list"></i> 日常活跃度
					</h3>
					<div class="box-body">
						<div id="container"></div>
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
				<table class="table table-bordered table-striped" id="task-active-table">
					<thead>
						<tr>
							<th>任务ID</th>
							<th>完成人数</th>
						</tr>
					</thead>
					<tbody>
						<c:forEach var="e" items="${data}">
							<tr>
								<td>${e.taskId}</td>
								<td>${e.count}</td>
							</tr>
						</c:forEach>
					</tbody>
				</table>
			</div>
		</div>
	</div>
</section>
<script>
 $('#active-time').daterangepicker({
     "singleDatePicker": true,
     "startDate": "${today}",
     "maxDate": "${maxtime}",
     locale: {
         format: 'YYYY-MM-DD'
      },
   },function(start, end, label) {
	  ajaxLoadPage2Body("${pageContext.request.contextPath}/survey/active/list/?date="+start.format('YYYY-MM-DD'));
  });
</script>
<script>
	var taskIds = [];
	var counts = [];
	<c:forEach var="e" items="${data}">
		taskIds.push('${e.taskId}');
		counts.push(${e.count});
	</c:forEach>
	
	$(function () {
	    $('#container').highcharts({
	        chart: {
	            type: 'spline'
	        },
	        title: {
	            text: '日常任务完成数'
	        },
	        xAxis: {
	        	label: '任务ID',
	            categories: taskIds
	        },
	        yAxis: {
	            title: {
	                text: '日常任务完成数（人）'
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
	            name: '完成人数',
	            data: counts
	        }],
	        credits: {
	        	enabled: false
        	}
	    });
	});
</script>
<script>	
$('#task-active-table').DataTable({
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
        title: '日常任务完成'
    },  {
        extend: 'print',
        text:'打印'
    } ],
    order: [[ 1, "desc" ]]
});
</script>