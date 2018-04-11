<%@ page language="java" contentType="text/html;charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

<section class="content-header">
	<h1>充值补单记录</h1>
	<ol class="breadcrumb">
		<li><a href="${pageContext.request.contextPath}/welcome/"><i class="fa fa-dashboard"></i> 首页</a></li>
		<li>游戏管理</li>
		<li><a href="javascript:ajaxLoadPage2Body('${pageContext.request.contextPath}/game/recharge/index/');">充值补单</a></li>
		<li class="active">充值补单记录</li>
	</ol>
</section>
<section class="content">
	<div class="row">
		<div class="col-xs-12">
			<div class="box">
				<div class="box-header">
					<h3 class="box-title">
						<i class="fa fa-list"></i> 充值补单列表
					</h3>
				</div>
				<!-- /.box-header -->
				<div class="box-body table-responsive no-padding">
					<table class="table table-hover">
						<tbody>
							<tr>
								<th>充值时间</th>
								<th>玩家</th>
								<th>充值档</th>
								<th>原因</th>
								<th>状态</th>
								<th>操作人</th>
							</tr>
							<c:forEach var="e" items="${page.content}">
								<tr role="row" class="odd">
									<td>${e.createTime }</td>
									<td>${e.player }</td>
									<td><spring:message code="template.recharge.product.${e.productId}" text="未知(${e.productId})"/></td>
									<td>${e.rechargeReson}</td>
									<td>
										<c:choose>
											<c:when test="${e.singal == -2 }">
												<span class="label label-danger">失败</span> 未找到【${e.player }】玩家
											</c:when>
											<c:when test="${e.singal == 0 }">
												<span class="label label-danger">失败</span> 服务器维护
											</c:when>
											<c:otherwise>
												<span class="label label-success">成功</span>
											</c:otherwise>
										</c:choose>
									</td>
									<td>${e.name}(${e.username})</td>
								</tr>
							</c:forEach>
						</tbody>
					</table>
				</div>
				<div class="box-footer clearfix">
					<jsp:include page="../../page.jsp">
						<jsp:param name="url" value="${pageContext.request.contextPath}/game/recharge/list/" />
					</jsp:include>
				</div>
			</div>
		</div>
	</div>
</section>