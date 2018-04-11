<%@ page language="java" contentType="text/html;charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>

<section class="content-header">
	<h1>查询充值排名</h1>
	<ol class="breadcrumb">
		<li><a href="${pageContext.request.contextPath}/welcome/"><i class="fa fa-dashboard"></i> 首页</a></li>
		<li>日志查询</li>
		<li class="active">查询充值排名</li>
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
			<h3 class="box-title">可以多选，不选为全区全服...</h3>
		</div>
		<div class="box-body">
			<form role="form" action="${pageContext.request.contextPath}/query/rechargerank/list/" method="post" id="queryRank">
				<button id="btn_query_rank" type="submit" class="btn btn-primary btn-lg btn-block">查询</button>
			</form>
		</div>
	</div>
	<c:if test="${not empty page}">
		<div class="box">
			<div class="box-header with-border">
				<div class="box-header with-border">
					<h3 class="box-title">充值排名列表（前5000名）</h3>
				</div>
				<div class="box-body">
					<c:choose>
						<c:when test="${empty page.content}">没有找到记录</c:when>
						<c:otherwise>
							<table class="table table-bordered table-striped" id="example">
								<thead>
									<tr>
										<th>角色ID</th>
										<th>角色名称</th>
										<th>总充值(单位：分)</th>
										<th>充值次数</th>
									</tr>
								</thead>
								<tbody>
									<c:forEach var="e" items="${page.content}">
										<tr role="row">
											<td>${e.id}</td>
											<td>${e.name}</td>
											<td>${e.totalRmb}</td>
											<td>${e.count }</td>
										</tr>
									</c:forEach>
								</tbody>
							</table>
						</c:otherwise>
					</c:choose>
				</div>
			</div>
		</div>
	</c:if>
</section>

<script>
	$('#queryRank').on('submit', function() {
		var serverIds = [];
		$("button[type=button][name=server]").each(function() {
			if (!$(this).hasClass("btn-default")) {
				serverIds.push($(this).val());
			}
		});
		var $btn = $("#btn_query_rank").button('loading');
		ajaxLoadPage2Body($(this).attr("action")+"?serverIds="+serverIds.join(','));
		return false;
	});

	$(function() {
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
	            title: '充值排名'
	        },  {
	            extend: 'print',
	            text:'打印'
	        } ],
	      	//跟数组下标一样，第一列从0开始，这里表格初始化时，第四列默认降序
            "order": [[ 2, "desc" ]]
		});
	});
</script>
