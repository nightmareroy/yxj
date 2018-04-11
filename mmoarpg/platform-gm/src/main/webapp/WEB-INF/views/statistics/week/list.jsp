<%@ page language="java" contentType="text/html;charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<section class="content-header">
	<h1>
		每周数据报表
	</h1>
	<ol class="breadcrumb">
		<li><a href="${pageContext.request.contextPath}/welcome/"><i class="fa fa-dashboard"></i> 首页</a></li>
		<li>统计分析</li>
		<li class="active">每周数据报表</li>
	</ol>
</section>
<section class="content">
	<div class="box">
		<div class="box-header with-border">
			<div class="box-header with-border">
				<h3 class="box-title">每周数据报表</h3>
			</div>
			<div class="box-body">
				<c:choose>
					<c:when test="${!empty result}">没有找到记录</c:when>
					<c:otherwise>
						<table class="table table-bordered table-striped" id="online">
							<thead>
								<tr>
									<th>周数</th>
									<th>活跃玩家</th>
									<th>新增注册</th>
									<th>老用户数</th>
									<th>充值人数</th>
									<th>充值次数</th>
									<th>充值总额（元）</th>
									<th>付费率</th>
									<th>ARPPU（元/人）</th>
									<th>活跃ARPU（元/人）</th>
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
	            title: '每周数据报表'
	        },  {
	            extend: 'print',
	            text:'打印'
	        } ],
	        order: [[ 0, "desc" ]],
	        ajax: "${pageContext.request.contextPath}/statistics/week/list/",
	        dataSrc: '',
	        columns:[ { data: 'week' },
	                  { data: 'activeUser' },
	                  { data: 'createUser' },
	                  { data: 'previous' },
	                  { data: 'charged' },
	                  { data: 'chargeSum' },
	                  { data: 'chargeCount' },
	                  { data: 'payRatio' },
	                  { data: 'arppu' },
	                  { data: 'activeArpu' }
	                  ],
            columnDefs : [ {
				targets : 0,
				render : function(data, type, row) {
					return data + "("+row.startDate + "-" + row.endDate+")";
	          	}
			}]
		});
	});
</script>