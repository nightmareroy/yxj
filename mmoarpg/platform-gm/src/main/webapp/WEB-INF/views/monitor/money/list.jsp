<%@ page language="java" contentType="text/html;charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<section class="content-header">
	<h1>货币监控</h1>
	<ol class="breadcrumb">
		<li><a href="${pageContext.request.contextPath}/welcome/"><i class="fa fa-dashboard"></i> 首页</a></li>
		<li>监控系统</li>
		<li class="active">货币监控</li>
	</ol>
</section>
<section class="content">
	<div class="row">
		<div class="col-xs-12">
			<div class="box">
				<div class="box-header">
					<h3 class="box-title">
						<i class="fa fa-list"></i> 货币异常预警列表
					</h3>
				</div>
				<div class="box-body table-responsive no-padding">
					<table class="table table-hover">
						<tbody>
							<tr>
								<th>预警时间</th>
								<th>用户ID</th>
								<th>角色名称</th>
								<th>角色等级</th>
								<th>货币类型</th>
								<th>总收益</th>
								<th>预警值</th>
								<th>操作</th>
							</tr>
							<c:forEach var="e" items="${page.content}">
								<tr role="row">
									<td><fmt:formatDate value="${e.date}" type="both" /></td>
									<td>${e.id}</td>
									<td>${e.name}</td>
									<td>${e.level}</td>
									<td><spring:message code='i18n.item.${e.type}' text='未知' />(${e.type})</td>
									<td>${e.money}</td>
									<td>${e.threshold}</td>
									<td>
										<a style="cursor:pointer;" data-toggle="modal" data-target="#confirmModal" data-msg="您确认查询[${e.name}]获得明细吗？" data-href="javascript:ajaxLoadPage2Body('${pageContext.request.contextPath}/query/money/check/?playerId=${e.id}&datepicker=<fmt:formatDate value="${e.date}" pattern="yyyy-MM-dd"/>&type=1&moneyId=${e.type}');"><span class="label label-success">查询明细</span></a>
										<a style="cursor:pointer;" data-toggle="modal" data-target="#confirmModal" data-msg="您确认要标识[${e.name}]的异常为解决状态吗？" data-href="javascript:ajaxLoadPage2Body('${pageContext.request.contextPath}/monitor/money/delete/${e.id}/');"><span class="label label-danger">标识解决</span></a>
									</td>
								</tr>
							</c:forEach>
						</tbody>
					</table>
				</div>
				<div class="box-footer clearfix">
					<jsp:include page="../../page.jsp">
						<jsp:param name="url" value="${pageContext.request.contextPath}/monitor/money/list/" />
					</jsp:include>
				</div>
			</div>
		</div>
	</div>
</section>