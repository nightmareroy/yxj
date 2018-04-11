<%@ page language="java" contentType="text/html;charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<section class="content-header">
	<h1>寻宝数据报表<small id="remaintime"><i class="fa fa-calendar"><u>${start} ~ ${end}</u></i></small></h1>
	<ol class="breadcrumb">
		<li><a href="${pageContext.request.contextPath}/welcome/"><i class="fa fa-dashboard"></i> 首页</a></li>
		<li>统计分析</li>
		<li class="active">寻宝数据报表</li>
	</ol>
</section>
<section class="content">
		<div class="box">
			<div class="box-header with-border">
				<div class="box-header with-border">
					<h3 class="box-title">寻宝数据列表</h3>
				</div>
				<div class="box-body">
					<c:choose>
						<c:when test="${empty result}">没有找到记录</c:when>
						<c:otherwise>
							<table class="table table-bordered table-striped" id="example">
								<thead>
									<tr>
										<th>日期</th>
										<th>寻宝大类</th>
										<th>总参与人数</th>
										<th>总参与次数</th>
										<th>总收益</th>
										<th>买一次人数</th>
										<th>买一次次数</th>
										<th>买一次收益</th>
										<th>买5次人数</th>
										<th>买5次次数</th>
										<th>买5次收益</th>
									</tr>
								</thead>
								<tbody>
									<c:forEach var="e" items="${result}">
										<tr role="row" class="odd">
											<td>${e.today}</td>
											<td><spring:message code='jsp.lottery.type.${e.type}' text='未知(${e.type})' /></td>
											<td>${e.number}</td>
											<td>${e.count}</td>
											<td>${e.money} <spring:message code='jsp.lottery.money.${e.type}' text='未知(${e.type})' /></td>
											<td>${e.number1}</td>
											<td>${e.count1}</td>
											<td>${e.money1} <spring:message code='jsp.lottery.money.${e.type}' text='未知(${e.type})' /></td>
											<td>${e.number5}</td>
											<td>${e.count5}</td>
											<td>${e.money5} <spring:message code='jsp.lottery.money.${e.type}' text='未知(${e.type})' /></td>
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
		$('#remaintime').daterangepicker({
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
			ajaxLoadPage2Body("${pageContext.request.contextPath}/statistics/lottery/query/?start="+start.format('YYYY-MM-DD')+"&end="+end.format('YYYY-MM-DD'));
		});
		
		$('#example').DataTable({
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
	            title: '寻宝数据报表_${today}_${end}'
	        },  {
	            extend: 'print',
	            text:'打印'
	        } ],
	      	//跟数组下标一样，第一列从0开始，这里表格初始化时，第四列默认降序
            "order": [[ 0, "desc" ]]
		});
	});
</script>