<%@ page language="java" contentType="text/html;charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<section class="content-header">
	<h1>充值区间分布</h1>
	<ol class="breadcrumb">
		<li><a href="${pageContext.request.contextPath}/welcome/"><i class="fa fa-dashboard"></i> 首页</a></li>
		<li>数据仓库</li>
		<li class="active">充值区间分布</li>
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
			<form role="form" action="${pageContext.request.contextPath}/data/recharge/section/list/" method="post" id="queryData">
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
	
	<div class="row">
		<div class="col-xs-12">
			<div class="box">
				<div class="box-header">
					<h3 class="box-title">
						<i class="fa fa-list"></i> 充值区间分布
					</h3>
					<div class="box-body">
						<c:choose>
							<c:when test="${empty result}">没有找到记录</c:when>
							<c:otherwise>
								<table class="table table-bordered table-striped" id="section">
									<thead>
										<tr>
											<th>日期</th>
											<th>1-5元</th>
											<th>6-29元</th>
											<th>30-97元</th>
											<th>98-197元</th>
											<th>198-327元</th>
											<th>328-647元</th>
											<th>648-3239元</th>
											<th>3240-4999元</th>
											<th>5000元+</th>
										</tr>
									</thead>
									<tbody>
										<c:forEach var="e" items="${result}">
											<tr role="row">
												<td>${e.date}</td>
												<td>${e.v1}</td>
												<td>${e.v6}</td>
												<td>${e.v30}</td>
												<td>${e.v98}</td>
												<td>${e.v198}</td>
												<td>${e.v328}</td>
												<td>${e.v648}</td>
												<td>${e.v3240}</td>
												<td>${e.v5000}</td>
											</tr>
										</c:forEach>
									</tbody>
								</table>
							</c:otherwise>
						</c:choose>
					</div>
				</div>
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
$('#section').DataTable({
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
        title: '充值区间分布'
    },  {
        extend: 'print',
        text:'打印'
    } ]
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
});
</script>