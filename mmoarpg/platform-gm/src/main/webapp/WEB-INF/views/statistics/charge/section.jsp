<%@ page language="java" contentType="text/html;charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<section class="content-header">
	<h1>
		充值区间<small id="sectiontime"><i class="fa fa-calendar">${start} ~ ${end}</i></small>
	</h1>
	<ol class="breadcrumb">
		<li><a href="${pageContext.request.contextPath}/welcome/"><i class="fa fa-dashboard"></i> 首页</a></li>
		<li>统计分析</li>
		<li class="active">充值区间</li>
	</ol>
</section>
<section class="content">
	<div class="box">
		<div class="box-header with-border">
			<div class="box-header with-border">
				<h3 class="box-title">充值区间</h3>
			</div>
			<div class="box-body">
				<c:choose>
					<c:when test="${!empty result}">没有找到记录</c:when>
					<c:otherwise>
						<table class="table table-bordered table-striped" id="chargesection">
							<thead>
								<tr>
									<th>日期</th>
									<th>1-9</th>
									<th>10-29</th>
									<th>30-49</th>
									<th>50-99</th>
									<th>100-199</th>
									<th>200-499</th>
									<th>500-999</th>
									<th>1000-1999</th>
									<th>2000-4999</th>
									<th>5000-9999</th>
									<th>10000-99999</th>
									<th>100000+</th>
								</tr>
							</thead>
						</table>
					</c:otherwise>
				</c:choose>
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
	        ajax: "${pageContext.request.contextPath}/statistics/charge/list/",
	        dataSrc: '',
	        columns:[ { data: 'date' },
	                  { data: 'v1' },
	                  { data: 'v10' },
	                  { data: 'v30' },
	                  { data: 'v50' },
	                  { data: 'v100' },
	                  { data: 'v200' },
	                  { data: 'v500' },
	                  { data: 'v1000' },
	                  { data: 'v2000' },
	                  { data: 'v5000' },
	                  { data: 'v10000' },
	                  { data: 'v100000' }
	                  ]
		});
		$('#sectiontime').daterangepicker({
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
			var queryUri = "${pageContext.request.contextPath}/statistics/charge/list/?start="+start.format('YYYY-MM-DD')+"&end="+end.format('YYYY-MM-DD');
			table.ajax.url(queryUri).load();
			$('#sectiontime .fa-calendar').html(start.format('YYYY-MM-DD') + " ~ " + end.format('YYYY-MM-DD'));
		});
	});
</script>
