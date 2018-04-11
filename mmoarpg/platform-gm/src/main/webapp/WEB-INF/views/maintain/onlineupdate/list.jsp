<%@ page language="java" contentType="text/html;charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

<section class="content-header">
	<h1>服务器版本</h1>
	<ol class="breadcrumb">
		<li><a href="${pageContext.request.contextPath}/welcome/"><i class="fa fa-dashboard"></i> 首页</a></li>
		<li>运维工具</li>
		<li class="active">服务器版本</li>
	</ol>
</section>
<section class="content">
	<div class="row">
		<div class="col-xs-12">
			<div class="box">
				<div class="box-header">
					<h3 class="box-title">
						<i class="fa fa-list"></i> 版本列表
					</h3>
					<div class="box-tools input-group input-group-sm">
						<button type="button" class="btn btn-block btn-default btn-sm" onclick="javascript:ajaxLoadPage2Body('${pageContext.request.contextPath}/maintain/onlineupdate/uploadUI/');">上传新版本</button>
					</div>
				</div>
				<div class="box-body table-responsive no-padding">
					<table class="table table-hover">
						<tbody>
							<tr>
								<th>流水号</th>
								<th>游戏服</th>
								<th>战斗服</th>
								<th>上传人</th>
								<th>上传时间</th>
								<th>操作</th>
							</tr>
							<c:forEach var="e" items="${page.content}">
								<tr role="row">
									<td>${e.id }</td>
									<td>${e.gameserver }</td>
									<td>${e.battleserver }</td>
									<td>${e.name}(${e.username })</td>
									<td>${e.createTime }</td>
									<td>
									<a style="cursor:pointer;" data-toggle="modal" data-target="#confirmModal" data-msg="您确认使用编号为[${e.id}]的版本吗？" data-href="javascript:ajaxLoadPage2Body('${pageContext.request.contextPath}/maintain/onlineupdate/useUI/?id=${e.id}');"><span class="label label-success">使用</span></a>
									<a style="cursor:pointer;" data-toggle="modal" data-target="#confirmModal" data-msg="您确认删除编号为[${e.id}]的版本吗？" data-href="javascript:ajaxLoadPage2Body('${pageContext.request.contextPath}/maintain/onlineupdate/delete/?id=${e.id}');"><span class="label label-danger">删除</span></a>
									</td>
								</tr>
							</c:forEach>
						</tbody>
					</table>
				</div>
				<div class="box-footer clearfix">
					<jsp:include page="../../page.jsp">
						<jsp:param name="url" value="${pageContext.request.contextPath}/maintain/onlineupdate/list/" />
					</jsp:include>
				</div>
			</div>
		</div>
	</div>
</section>