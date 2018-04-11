<%@ page language="java" contentType="text/html;charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<section class="content-header">
	<h1>
		屏幕分辨率份额 
	</h1>
	<ol class="breadcrumb">
		<li><a href="${pageContext.request.contextPath}/welcome/"><i class="fa fa-dashboard"></i> 首页</a></li>
		<li>统计分析</li>
		<li class="active">各屏幕分辨率份额</li>
	</ol>
</section>
<section class="content">
	<div class="row">
		<div class="col-xs-12">
			<div class="box">
				<div class="box-header">
					<h3 class="box-title">
						<i class="fa fa-list"></i> 各屏幕分辨率份额比例
					</h3>
				</div>
				<div class="box-body">
					<div id="container"></div>
				</div>
				<div class="box-body">
					<table class="table table-bordered table-striped" id="screen-table">
						<thead>
							<tr>
								<th>屏幕分辨率</th>
								<th>数量</th>
							</tr>
						</thead>
						<tbody>
							<c:forEach var="e" items="${data}">
								<tr>
									<td>${e.type}</td>
									<td>${e.count}</td>
								</tr>
							</c:forEach>
						</tbody>
					</table>
				</div>
			</div>
		</div>
	</div>
</section>
<script>
	$("#screen-table").dataTable({
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
            title: '屏幕分辨率份额'
        },  {
            extend: 'print',
            text:'打印'
        } ],
        order: [[ 1, "desc" ]]
	});

	$(function() {
		// 数据图表...
		var total = 0;
		<c:forEach var="e" items="${data}">
			total = total + ${e.count};
		</c:forEach>
		var array = [
		<c:forEach var="e" items="${data}">
			['${e.type}', ${e.count}],
		</c:forEach>
		];
		
	    $('#container').highcharts({
	    	credits: {
	            enabled:false
	  		},
	        chart: {
	            type: 'pie',
	            options3d: {
	                enabled: true,
	                alpha: 45
	            }
	        },
	        title: {
	            text: ''
	        },
	        plotOptions: {
	            pie: {
	                innerSize: 100,
	                depth: 45
	            }
	        },
	        series: [{
	            name: '分辨率数据总数',
	            data: array,
	            dataLabels: {
	                formatter: function () {
	                    // 大于1则显示
	                    return this.y > 1 ? '<b>' + this.point.name + ':</b> ' + Highcharts.numberFormat((this.y*100/total),2) + '%'  : null;
	                }
	            }
	        }]
	    });
	});
</script>