<%@ page language="java" contentType="text/html;charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>

<section class="content-header">
	<h1>查询升级记录</h1>
	<ol class="breadcrumb">
		<li><a href="${pageContext.request.contextPath}/welcome/"><i class="fa fa-dashboard"></i> 首页</a></li>
		<li>日志查询</li>
		<li class="active">查询升级记录</li>
	</ol>
</section>
<section class="content">
	<div class="box box-primary">
		<div class="box-header with-border">
			<h3 class="box-title">查询条件 :</h3>
		</div>
		<div class="box-body">
			<div class="form-group">
				<label for="playerId">角色ID : <span class="text-red">*</span></label>
				<input id="playerId" class="form-control" placeholder="角色ID:必填" value="${playerId}" type="text">
			</div>
			<button type="submit" class="btn btn-primary btn-lg btn-block" onclick="javascript:ajaxLoadPage2Body('${pageContext.request.contextPath}/query/level/check/?playerId='+$('#playerId').val());">查询</button>
		</div>
	</div>
	<c:if test="${not empty page}">
		<div class="box">
			<div class="box-header with-border">
				<div class="box-header with-border">
					<h3 class="box-title">升级列表</h3>
				</div>
				<div class="box-body">
					<c:choose>
						<c:when test="${empty page.content}">没有找到记录</c:when>
						<c:otherwise>
							<table class="table table-bordered table-striped" id="example">
								<thead>
									<tr>
										<th>升级时间</th>
										<th>角色ID</th>
										<th>角色名称</th>
										<th>等级</th>
										<th>经验值</th>
										<th>用时</th>
									</tr>
								</thead>
								<tbody>
									<c:forEach var="e" items="${page.content}">
										<tr role="row">
											<td><fmt:formatDate value="${e.date}" type="both" /></td>
											<td>${e.id}</td>
											<td>${e.name}</td>
											<td>${e.level}</td>
											<td>${e.exp}</td>
											<td>
												<c:choose>
													<c:when test="${e.time == 0}">-</c:when>
													<c:when test="${e.time < 60}">${e.time}秒</c:when>
													<c:when test="${e.time < 60*60}"><fmt:formatNumber value="${e.time/60}" pattern="#0" />分</c:when>
													<c:when test="${e.time < 60*60*24}"><fmt:formatNumber value="${e.time/60/60}" pattern="#0" />时</c:when>
													<c:otherwise><fmt:formatNumber value="${e.time/60/60/24}" pattern="#0"/>天</c:otherwise>
												</c:choose>
											</td>
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
	            title: '查询角色升级记录_${playerId}'
	        },  {
	            extend: 'print',
	            text:'打印'
	        } ],
	      	//跟数组下标一样，第一列从0开始，这里表格初始化时，第四列默认降序
            "order": [[ 0, "desc" ]]
		});
	});
</script>
