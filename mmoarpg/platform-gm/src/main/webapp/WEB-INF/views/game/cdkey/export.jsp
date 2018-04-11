<%@ page language="java" contentType="text/html;charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

<section class="content-header">
	<h1>导出兑换码</h1>
	<ol class="breadcrumb">
		<li><a href="${pageContext.request.contextPath}/welcome/"><i class="fa fa-dashboard"></i> 首页</a></li>
		<li>游戏管理</li>
		<li>兑换码管理</li>
		<li><a href="javascript:ajaxLoadPage2Body('${pageContext.request.contextPath}/game/cdkey/list/');">礼包列表</a></li>
		<li><a href="javascript:ajaxLoadPage2Body('${pageContext.request.contextPath}/game/cdkey/info/?pid=${cp.id}');">批次管理</a></li>
		<li class="active">导出兑换码</li>
	</ol>
</section>
<section class="content">
	<div class="row">
		<div class="col-xs-12">
			<div class="box">
				<div class="box-header">
					<h3 class="box-title">
						<i class="fa fa-list"></i> ${data.name}[${data.code}]
					</h3>
				</div>
				<div class="box-body table-responsive">
					<table id="exportTable" class="table table-hover">
						<thead>
								<tr>
									<th>兑换码</th>
									<th>使用数量</th>
									<th>使用时间</th>
									<th>使用玩家</th>
									<th>使用渠道</th>
								</tr>
							</thead>
							<tbody>
								<c:forEach var="e" items="${data.cdkCodes}">
									<tr role="row" class="odd">
										<td>${e.code}</td>
										<td>${e.useNum}</td>
										<td>${e.useDate}</td>
										<td>${e.usePlayerId}</td>
										<td>${e.useChannel}</td>
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
$(function() {
	$('#exportTable').DataTable({
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
            title: 'CDK-${data.name}-${data.code}'
        },  {
            extend: 'print',
            text:'打印'
        } ]
	});
});
</script>