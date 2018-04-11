<%@ page language="java" contentType="text/html;charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

<section class="content-header">
	<h1>模拟充值</h1>
	<ol class="breadcrumb">
		<li><a href="${pageContext.request.contextPath}/welcome/"><i class="fa fa-dashboard"></i> 首页</a></li>
		<li>游戏管理</li>
		<li class="active">模拟充值</li>
	</ol>
</section>
<section class="content">
	<div class="row">
		<div class="col-xs-12">
			<div class="box">
				<div class="box-header">
					<h3 class="box-title">
						<i class="fa fa-list"></i> 模拟充值记录列表
					</h3>
					<div class="box-tools input-group input-group-sm">
						<button type="button" class="btn btn-block btn-default btn-sm" onclick="javascript:ajaxLoadPage2Body('${pageContext.request.contextPath}/game/fictitious/index/');">申请充值</button>
					</div>
				</div>
				<div class="box-body table-responsive no-padding">
					<table class="table table-hover">
						<tbody>
							<tr>
								<th>流水号</th>
								<th>区服</th>
								<th>玩家</th>
								<th>充值档</th>
								<th>申请人</th>
								<th>申请时间</th>
								<th>申请原因</th>
								<th>状态</th>
								<th>操作</th>
							</tr>
							<c:forEach var="e" items="${page.content}">
								<tr role="row" class="odd">
									<td>${e.id }</td>
									<td>${e.serverId }</td>
									<td>${e.player }</td>
									<td><spring:message code="template.recharge.product.${e.productId}" text="未知(${e.productId})"/></td>
									<td>${e.name}(${e.username})</td>
									<td>${e.createTime }</td>
									<td>${e.rechargeReson}</td>
									<td>
										<c:choose>
											<c:when test="${e.singal == 0}">
												<span class="label label-warning">等待审批</span>
											</c:when>
											<c:when test="${e.singal == 1}">
												<span class="label label-success">已经审批</span>
											</c:when>
											<c:when test="${e.singal == 2}">
												<span class="label label-danger">已经拒绝</span>
											</c:when>
											<c:otherwise>
												<c:choose>
													<c:when test="${e.result == -2 }">
														<span class="label label-danger" data-toggle="tooltip" data-widget="chat-pane-toggle" data-original-title="未找到【${e.player }】玩家">充值失败</span>
													</c:when>
													<c:when test="${e.result == 0 }">
														<span class="label label-danger" data-toggle="tooltip" data-widget="chat-pane-toggle" data-original-title="服务器维护">充值失败</span>
													</c:when>
													<c:otherwise>
														<span class="label label-danger">充值失败</span>
													</c:otherwise>
												</c:choose>
											</c:otherwise>
										</c:choose>
									</td>
									<td>
										<c:choose>
											<c:when test="${e.singal == 0}">
												<a style="cursor:pointer;" data-toggle="modal" data-target="#confirmModal" data-msg="您确认要同意编号为[${e.id}]的充值吗？" data-href="javascript:ajaxLoadPage2Body('${pageContext.request.contextPath}/game/fictitious/agree/${e.id}/');"><span class="label label-success">同意</span></a>
												<a style="cursor:pointer;" data-toggle="modal" data-target="#confirmModal" data-msg="您确认要拒绝编号为[${e.id}]的充值吗？" data-href="javascript:ajaxLoadPage2Body('${pageContext.request.contextPath}/game/fictitious/refuse/${e.id}/');"><span class="label label-danger">拒绝</span></a>
											</c:when>
											<c:when test="${e.singal == 1}">
												审批人(${e.auditName})
											</c:when>
											<c:when test="${e.singal == 2}">
												拒绝人(${e.auditName})
											</c:when>
											<c:otherwise>
												审批人(${e.auditName})
											</c:otherwise>
										</c:choose>
									</td>
								</tr>
							</c:forEach>
						</tbody>
					</table>
				</div>
				<div class="box-footer clearfix">
					<jsp:include page="../../page.jsp">
						<jsp:param name="url" value="${pageContext.request.contextPath}/game/fictitious/list/" />
					</jsp:include>
				</div>
			</div>
		</div>
	</div>
</section>