<%@ page language="java" contentType="text/html;charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

<section class="content-header">
	<h1>监控邮箱管理</h1>
	<ol class="breadcrumb">
		<li><a href="${pageContext.request.contextPath}/welcome/"><i class="fa fa-dashboard"></i> 首页</a></li>
		<li>运维工具</li>
		<li class="active">监控邮箱管理</li>
	</ol>
</section>
<section class="content">
	<div class="row">
		<div class="col-xs-12">
			<div class="box">
				<div class="box-header">
					<h3 class="box-title">
						<i class="fa fa-list"></i> 邮箱列表
					</h3>
					<div class="box-tools input-group input-group-sm">
						<button type="button" class="btn btn-block btn-default btn-sm" onclick="javascript:ajaxLoadPage2Body('${pageContext.request.contextPath}/maintain/email/addUI/');">添加邮箱</button>
					</div>
				</div>
				<div class="box-body table-responsive no-padding">
					<table class="table table-hover">
						<tbody>
							<tr>
								<th>Email</th>
								<th>姓名</th>
								<th>备注</th>
								<th>添加时间</th>
								<th>操作</th>
							</tr>
							<tr>
								<c:forEach var="e" items="${page.content}">
									<tr role="row" class="odd">
										<td>${e.addr}</td>
										<td>${e.name}</td>
										<td>${e.remarks}</td>
										<td>${e.createTime}</td>
										<td>
										    <a data-toggle="modal" data-target="#confirmModal" data-msg="您确认要编辑[${e.name}(${e.addr})]吗？" data-href="javascript:ajaxLoadPage2Body('${pageContext.request.contextPath}/maintain/email/editUI/?addr=${e.addr}');">编辑</a> 
										    <a data-toggle="modal" data-target="#confirmModal" data-msg="您确认要删除[${e.name}(${e.addr})]吗？"data-href="javascript:ajaxLoadPage2Body('${pageContext.request.contextPath}/maintain/email/delete/?addr=${e.addr}');">删除</a>
										</td>
								</c:forEach>
							</tr>
						</tbody>
					</table>
				</div>
				<div class="box-footer clearfix">
					<jsp:include page="../../page.jsp">
						<jsp:param name="url" value="${pageContext.request.contextPath}/maintain/email/list/" />
					</jsp:include>
				</div>
			</div>
		</div>
	</div>
</section>