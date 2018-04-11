<%@ page language="java" contentType="text/html;charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<section class="content-header">
	<h1>权限管理</h1>
	<ol class="breadcrumb">
		<li><a href="${pageContext.request.contextPath}/welcome/"><i class="fa fa-dashboard"></i> 首页</a></li>
		<li>后台管理</li>
		<li class="active">权限管理</li>
	</ol>
</section>
<section class="content">
	<div class="row">
		<div class="col-xs-12">
			<div class="box">
				<div class="box-header">
					<h3 class="box-title">
						<i class="fa fa-list"></i> 权限组列表
					</h3>
					<div class="box-tools input-group input-group-sm">
						<button type="button" class="btn btn-block btn-default btn-sm" onclick="javascript:ajaxLoadPage2Body('${pageContext.request.contextPath}/backstage/auth/addUI/');">添加权限组</button>
					</div>
				</div>
				<!-- /.box-header -->
				<div class="box-body table-responsive no-padding">
					<table class="table table-hover">
						<tbody>
							<tr>
								<th>名称</th>
								<th>创建时间</th>
								<th>操作</th>
							</tr>
							<c:forEach var="e" items="${page.content}">
								<tr role="row" class="odd">
									<td>${e.name}</td>
									<td>${e.createTime }</td>
									<td>
										<c:choose>
											<c:when test="${e.superman}">
												<span class="label label-success">至高无上</span>
											</c:when>
											<c:otherwise>
												<a data-toggle="modal" data-target="#confirmModal" data-msg="您确认要编辑[${e.name}]权限组吗？" data-href="javascript:ajaxLoadPage2Body('${pageContext.request.contextPath}/backstage/auth/editUI/${e.id}/?');">编辑</a>
												<a data-toggle="modal" data-target="#confirmModal" data-msg="您确认要删除[${e.name}]权限组吗？" data-href="javascript:ajaxLoadPage2Body('${pageContext.request.contextPath}/backstage/auth/delete/${e.id}/');">删除 </a>
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
						<jsp:param name="url" value="${pageContext.request.contextPath}/backstage/auth/list/" />
					</jsp:include>
				</div>
			</div>
		</div>
	</div>
</section>