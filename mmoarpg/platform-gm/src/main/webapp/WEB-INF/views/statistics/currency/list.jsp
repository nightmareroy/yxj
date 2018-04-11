<%@ page language="java" contentType="text/html;charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<section class="content-header">
	<h1>
		每日货币产出/消耗<small id="selectTime"><i class="fa fa-calendar">${start} ~ ${end}</i></small>
	</h1>
	<ol class="breadcrumb">
		<li><a href="${pageContext.request.contextPath}/welcome/"><i class="fa fa-dashboard"></i> 首页</a></li>
		<li>统计分析</li>
		<li class="active">每日货币产出/消耗</li>
	</ol>
</section>
<section class="content">
	<div class="box">
		<div id="container"></div>
	</div>
	<div class="box">
		<div class="box-header with-border">
			<div class="box-header with-border">
				<h3 class="box-title">每日货币产出/消耗</h3>
			</div>
			<div class="box-body">
				<table class="table table-bordered table-striped" id="chargesection">
					<thead>
						<tr>
							<th>日期</th>
							<th>金币产出</th>
							<th>金币消耗</th>
							<th>钻石产出</th>
							<th>钻石消耗</th>
						</tr>
					</thead>
				</table>
			</div>
		</div>
	</div>
</section>
<script>
	$(function() {
		 var table = $('#chargesection').DataTable({
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
	            title: '充值区间'
	        },  {
	            extend: 'print',
	            text:'打印'
	        } ],
	        order: [[ 0, "desc" ]],
	        ajax: "${pageContext.request.contextPath}/statistics/currency/get/",
	        dataSrc: '',
	        columns:[ { data: 'date' },
	                  { data: 'goldOutput' },
	                  { data: 'goldConsume' },
	                  { data: 'zuanOutput' },
	                  { data: 'zuanConsume' }
	                  ]
		});
		$('#selectTime').daterangepicker({
			timePicker24Hour: true,
			maxDate: "${maxtime}",
	        locale: {
	            format: 'YYYY-MM-DD',
	            applyLabel : '确定',  
                cancelLabel : '取消',  
                fromLabel : '起始时间',  
                toLabel : '结束时间',  
                customRangeLabel : '自定义起始日期',  
                daysOfWeek : [ '日', '一', '二', '三', '四', '五', '六' ],  
                monthNames : [ '一月', '二月', '三月', '四月', '五月', '六月', '七月', '八月', '九月', '十月', '十一月', '十二月' ]
	        },
	        ranges: {
	            '今天': [moment(), moment()],
	            '昨天': [moment().subtract(1, 'days'), moment().subtract(1, 'days')],
	            '最近7天': [moment().subtract(6, 'days'), moment()],
	            '最近30天': [moment().subtract(29, 'days'), moment()],
	            '这个月': [moment().startOf('month'), moment().endOf('month')],
	            '上个月': [moment().subtract(1, 'month').startOf('month'), moment().subtract(1, 'month').endOf('month')]
	         }
		}, function(start, end, label) {
			var queryUri = "${pageContext.request.contextPath}/statistics/currency/list/?start="+start.format('YYYY-MM-DD')+"&end="+end.format('YYYY-MM-DD');
			ajaxLoadPage2Body(queryUri);
		});
	});
</script>
<script>
	
	var date = [<c:forEach var="a" items="${data.data}">${a.date},</c:forEach>];
	var goldOut = [<c:forEach var="a" items="${data.data}">${a.goldOutput},</c:forEach>];
	var zuanOut = [<c:forEach var="a" items="${data.data}">${a.zuanOutput},</c:forEach>];
	var goldConsume = [<c:forEach var="a" items="${data.data}">${a.goldConsume},</c:forEach>];
	var zuanConsume = [<c:forEach var="a" items="${data.data}">${a.zuanConsume},</c:forEach>];
	
    $('#container').highcharts({
        chart: {
            type: 'column'
        },
        title: {
            text: '每日金币钻石产出/消耗'
        },
        xAxis: {
            categories: date,
            crosshair: true
        },
        yAxis: {
            min: 0,
            title: {
                text: '数值'
            }
        },
        tooltip: {
            headerFormat: '<span style="font-size:10px">{point.key}</span><table>',
            pointFormat: '<tr><td style="color:{series.color};padding:0">{series.name}: </td>' +
            '<td style="padding:0"><b>{point.y:.0f}</b></td></tr>',
            footerFormat: '</table>',
            shared: true,
            useHTML: true
        },
        plotOptions: {
            column: {
                pointPadding: 0.2,
                borderWidth: 0
            }
        },
        series: [{
            name: '金币产出',
            data: goldOut
        }, {
            name: '金币消耗',
            data: goldConsume
        }, {
            name: '钻石产出',
            data: zuanOut
        }, {
            name: '钻石消耗',
            data: zuanConsume
        }],
        credits: {
        	enabled: false
       	}
    });
</script>
