<%@ page language="java" contentType="text/html;charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<section class="content-header">
	<h1>操作日志</h1>
	<ol class="breadcrumb">
		<li><a href="${pageContext.request.contextPath}/welcome/"><i class="fa fa-dashboard"></i> 首页</a></li>
		<li>后台管理</li>
		<li class="active">操作日志</li>
	</ol>
</section>
<section class="content">
	<div class="row">
		<div class="col-xs-12">
			<div class="box">
				<div class="box-header">
					<h3 class="box-title">
						<i class="fa fa-list"></i> 日志列表
					</h3>
					<div class="box-tools input-group input-group-sm">
						<button type="button" class="btn btn-block btn-default btn-sm" onclick="javascript:ajaxLoadPage2Body('${pageContext.request.contextPath}/backstage/log/query/');">高级搜索</button>
					</div>
				</div>

				<div class="box-body table-responsive no-padding">
					<table class="table table-hover">
						<tbody>
							<tr>
								<th>时间</th>
								<th>名称（账号）</th>
								<th>操作</th>
								<th>IP</th>
								<th>耗时</th>
							</tr>
							<c:forEach var="e" items="${page.content}">
								<tr role="row">
									<td>${e.createTime }</td>
									<td>${e.name}(${e.username})</td>
									<td><spring:message code="${e.operation }" text="${e.operation }" arguments="${e.arguments }" argumentSeparator=","/></td>
									<td>${e.ip }</td>
									<td>${e.cost } ms</td>
								</tr>
							</c:forEach>
						</tbody>
					</table>
				</div>
				<div class="box-footer clearfix">
					<jsp:include page="../../page.jsp">
						<jsp:param name="url" value="${pageContext.request.contextPath}/backstage/log/list/" />
						<jsp:param name="args" value="reservationtime=${reservationtime}&ip=${ip}&classify=${classify}&user=${user}"/>
					</jsp:include>
				</div>
			</div>
		</div>
	</div>
</section>