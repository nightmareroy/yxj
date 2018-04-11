<%@ page language="java" contentType="text/html;charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<section class="content-header">
	<h1>
		留存率<small id="remaintime"><i class="fa fa-calendar">${start} - ${end}</i></small>
	</h1>
	<ol class="breadcrumb">
		<li><a href="${pageContext.request.contextPath}/welcome/"><i class="fa fa-dashboard"></i> 首页</a></li>
		<li>数据仓库</li>
		<li class="active">留存率</li>
	</ol>
</section>
<section class="content">
	<!-- 选服列表 -->
	<jsp:include page="../../serverlist.jsp">
		<jsp:param name="multiselect" value="true" />
		<jsp:param name="dosomething" value="javascript:selectServerId(this, true);" />
	</jsp:include>
	
	<div class="box box-primary">
		<div class="box-header with-border">
			<h3 class="box-title">查询条件</h3>
		</div>
		<div class="box-body">
			<form role="form" action="${pageContext.request.contextPath}/data/remain/list/" method="post" id="queryData">
				<div class="form-group">
					<label>查询时间: </label>
					<div class="input-group">
						<div class="input-group-addon">
							<i class="fa fa-clock-o"></i>
						</div>
						<input type="text" class="form-control pull-right" id="reservationtime" value="${reservationtime}">
					</div>
				</div>
				<button id="btn_query_rank" type="submit" class="btn btn-primary btn-lg btn-block">查询</button>
			</form>
		</div>
	</div>
	
	<div class="box">
		<div class="box-header with-border">
			<div class="box-header with-border">
				<h3 class="box-title">留存率</h3>
			</div>
			<div class="box-body">
				<c:choose>
					<c:when test="${empty data}">没有找到记录</c:when>
					<c:otherwise>
						<table class="table table-bordered table-striped" id="remain">
							<thead>
								<tr>
									<th>日期</th>
									<th>基数</th>
									<th>2日留存</th>
									<th>3日留存</th>
									<th>4日留存</th>
									<th>5日留存</th>
									<th>6日留存</th>
									<th>7日留存</th>
								</tr>
							</thead>
							<tbody>
							<c:forEach var="e" items="${data}">
								<tr>
									<td>${e.date}</td>
									<td>${e.createNum}</td>
									<td><fmt:formatNumber value="${e.remain2/e.createNum}" type="percent"/>(${e.remain2})</td>
									<td><fmt:formatNumber value="${e.remain3/e.createNum}" type="percent"/>(${e.remain3})</td>
									<td><fmt:formatNumber value="${e.remain4/e.createNum}" type="percent"/>(${e.remain4})</td>
									<td><fmt:formatNumber value="${e.remain5/e.createNum}" type="percent"/>(${e.remain5})</td>
									<td><fmt:formatNumber value="${e.remain6/e.createNum}" type="percent"/>(${e.remain6})</td>
									<td><fmt:formatNumber value="${e.remain7/e.createNum}" type="percent"/>(${e.remain7})</td>
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
$('#queryData').on('submit', function() {
	var serverIds = [];
	$("button[type=button][name=server]").each(function() {
		if (!$(this).hasClass("btn-default")) {
			serverIds.push($(this).val());
		}
	});
	var $btn = $("#btn_query_rank").button('loading');
	ajaxLoadPage2Body($(this).attr("action")+"?serverIds="+serverIds.join(',')+"&reservationtime="+$("#reservationtime").val());
	return false;
});
</script>
<script>
	$(function() {
		$('#reservationtime').daterangepicker({
			"maxDate": "${maxtime}",
	        locale: {
	            format: 'YYYY-MM-DD'
	        },
	        ranges: {
	            '今天': [moment(), moment()],
	            '昨天': [moment().subtract(1, 'days'), moment().subtract(1, 'days')],
	            '最近7天': [moment().subtract(6, 'days'), moment()],
	            '最近30开': [moment().subtract(29, 'days'), moment()],
	            '这个月': [moment().startOf('month'), moment().endOf('month')],
	            '上个月': [moment().subtract(1, 'month').startOf('month'), moment().subtract(1, 'month').endOf('month')]
	         }
		});
		
		 var table = $('#remain').DataTable({
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
	            title: '留存率'
	        },  {
	            extend: 'print',
	            text:'打印'
	        } ],
	        order: [[ 0, "desc" ]]
		});
	});
</script>
