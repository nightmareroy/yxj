<%@ page language="java" contentType="text/html;charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<section class="content-header">
	<h1>道具监控</h1>
	<ol class="breadcrumb">
		<li><a href="${pageContext.request.contextPath}/welcome/"><i class="fa fa-dashboard"></i> 首页</a></li>
		<li class="active">道具监控</li>
	</ol>
</section>
<section class="content">
	<div class="row">
		<div class="col-xs-12">
			<div class="box">
				<div class="box-header">
					<h3 class="box-title">
						<i class="fa fa-list"></i> 道具监控列表
					</h3>
				</div>
				<div class="box-body table-responsive no-padding">
					<table class="table table-hover">
						<tbody>
							<tr>
								<th>ID</th>
								<th>角色ID</th>
								<th>角色名称</th>
								<th>角色账号</th>
								<th>监控发生时间</th>
								<th>道具ID</th>
								<th>道具数量</th>
								<th>角色等级</th>
								<th>角色VIP等级</th>
								<th>处理结果</th>
							</tr>
							<c:forEach var="e" items="${page.content}">
								<tr role="row" class="odd">
									<td>${e.id}</td>
									<td>${e.userId}</td>
									<td>${e.name}</td>
									<td>${e.uID}</td>
									<td><fmt:formatDate value="${e.eventTime}" type="both" /></td>
									<td>${e.itemId}</td>
									<td>${e.itemNum}</td>
									<td>${e.level}</td>
									<td>${e.vIPLevel }</td>
									<td><c:choose>
											<c:when test="${e.state == 0}">
												<a style="cursor: pointer;" data-toggle="modal" data-target="#confirmModal" data-msg="您确认[${e.userId}]的处理结果吗？" data-href="javascript:ajaxLoadPage2Body('${pageContext.request.contextPath}/monitor/item/check/${e.id}/?');"><span class="label label-warning">等待处理</span></a>
											</c:when>
											<c:when test="${e.state == 1}">
												<span class="label label-success">已经处理</span>
											</c:when>
										</c:choose></td>
								</tr>
							</c:forEach>
						</tbody>
					</table>
				</div>
				<div class="box-footer clearfix">
					<jsp:include page="../../page.jsp">
						<jsp:param name="url" value="${pageContext.request.contextPath}/monitor/item/list/" />
					</jsp:include>
				</div>
			</div>
		</div>
	</div>
</section>