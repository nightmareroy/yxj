<%@ page language="java" contentType="text/html;charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

<section class="content-header">
	<h1>账号管理</h1>
	<ol class="breadcrumb">
		<li><a href="${pageContext.request.contextPath}/welcome/"><i class="fa fa-dashboard"></i> 首页</a></li>
		<li>后台管理</li>
		<li class="active">账号管理</li>
	</ol>
</section>
<section class="content">
	<div class="row">
		<div class="col-xs-12">
			<div class="box">
				<div class="box-header">
					<h3 class="box-title">
						<i class="fa fa-list"></i> 账号列表
					</h3>
					<div class="box-tools input-group input-group-sm">
						<button type="button" class="btn btn-block btn-default btn-sm" onclick="javascript:ajaxLoadPage2Body('${pageContext.request.contextPath}/backstage/user/addUI/');">添加账号</button>
					</div>
				</div>
				<div class="box-body table-responsive no-padding">
					<table class="table table-hover">
						<tbody>
							<tr>
								<th>账号</th>
								<th>名称</th>
								<th>创建时间</th>
								<th>登录方式</th>
								<th>Status</th>
								<th>权限组</th>
								<th>操作</th>
							</tr>
							<c:forEach var="e" items="${page.content}">
								<tr role="row" class="odd">
									<td>${e.username }</td>
									<td>${e.name}</td>
									<td>${e.createTime }</td>
									<td>${loginplatform[e.loginPlatformId].name}</td>
									<td><c:choose>
											<c:when test="${e.status == 0}">
												<span class="label label-success"><spring:message code='template.user.type.${e.status}' text='正常' /></span>
											</c:when>
											<c:when test="${e.status == 1}">
												<span class="label label-warning"><spring:message code='template.user.type.${e.status}' text='异常' /></span>
											</c:when>
											<c:otherwise>
												<span class="label label-danger"><spring:message code='template.user.type.${e.status}' text='锁定' /></span>
											</c:otherwise>
										</c:choose></td>
									<td>${author[e.authGroupId].name}</td>
									<td><a data-toggle="modal" data-target="#confirmModal" data-msg="您确认要编辑[${e.name}]账号吗？" data-href="javascript:ajaxLoadPage2Body('${pageContext.request.contextPath}/backstage/user/editUI/${e.id}/');">编辑</a> 
										<c:if test="${sessionScope.SESSION_USER.username != e.username}">
											<a data-toggle="modal" data-target="#confirmModal" data-msg="您确认要删除[${e.name}]账号吗？" data-href="javascript:ajaxLoadPage2Body('${pageContext.request.contextPath}/backstage/user/delete/${e.id}/');">删除</a>
										</c:if>
									</td>
								</tr>
							</c:forEach>
						</tbody>
					</table>
				</div>
				<div class="box-footer clearfix">
					<jsp:include page="../../page.jsp">
						<jsp:param name="url" value="${pageContext.request.contextPath}/backstage/user/list/" />
					</jsp:include>
				</div>
			</div>
		</div>
	</div>
</section>