<%@ page language="java" contentType="text/html;charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<section class="content-header">
	<h1>福利审批</h1>
	<ol class="breadcrumb">
		<li><a href="${pageContext.request.contextPath}/welcome/"><i class="fa fa-dashboard"></i> 首页</a></li>
		<li>游戏管理</li>
		<li>福利管理</li>
		<li class="active">福利审批</li>
	</ol>
</section>
<section class="content">
	<div class="row">
		<div class="col-xs-12">
			<div class="box">
				<div class="box-header">
					<h3 class="box-title">
						<i class="fa fa-list"></i> 审批列表
					</h3>
					<div class="box-tools input-group input-group-sm">
						<button type="button" class="btn btn-block btn-default btn-sm" onclick="javascript:ajaxLoadPage2Body('${pageContext.request.contextPath}/game/mail/manage/');">申请福利</button>
					</div>
				</div>
				<div class="box-body table-responsive no-padding">
					<table class="table table-hover">
						<tbody>
							<tr>
								<th>ID</th>
								<th>区服</th>
								<th>发送目标</th>
								<th>邮件标题</th>
								<th>邮件内容</th>
								<th>邮件附件</th>
								<th>申请人</th>
								<th>申请时间</th>
								<th>申请原因</th>
								<th>状态</th>
								<th>操作</th>
							</tr>
							<c:forEach var="e" items="${page.content}">
								<tr role="row" class="odd">
									<td>${e.id}</td>
									<td>
										<button type="button" class="btn btn-box-tool" data-toggle="tooltip" data-widget="chat-pane-toggle" data-original-title="${e.sidList}">数量:${e.sidListSize}</button>
									</td>
									<td>
										<c:choose>
											<c:when test="${e.mailType==0}">${e.playerId }</c:when>
											<c:when test="${e.mailType==1}">${e.playerId }</c:when>
											<c:otherwise>全服</c:otherwise>
										</c:choose>
									</td>
									<td class="expanded info-head">${e.title }</td>
									<td class="expanded info-content">${e.content }</td>
									<td class="expanded info-item">
										<c:forEach var="i" items="${e.itemListx}">${i.itemName}x${i.itemNumber};</c:forEach>
									</td>
									<td>${e.applyName}(${e.applyUsername})</td>
									<td><fmt:formatDate value="${e.createTime }" pattern="yyyy-MM-dd HH:mm:ss"/></td>
									<td>${e.reason }</td>
									<td><c:choose>
											<c:when test="${e.state == 0}">
												<span class="label label-warning">等待审批</span>
											</c:when>
											<c:when test="${e.state == 1}">
												<span class="label label-success">已经审批</span>
											</c:when>
											<c:when test="${e.state == 2}">
												<span class="label label-danger">已经拒绝</span>
											</c:when>
											<c:when test="${e.state == 3}">
												<span class="label label-warning" data-toggle="tooltip" data-widget="chat-pane-toggle" data-original-title="${e.result}">发送失败</span>
											</c:when>
											<c:when test="${e.state == 5}">
												<span class="label label-danger">回收失败</span>
											</c:when>
											<c:otherwise>
												<span class="label label-warning">成功回收</span>
											</c:otherwise>
										</c:choose></td>
									<td><c:choose>
											<c:when test="${e.state == 0}">
												<a style="cursor:pointer;" data-toggle="modal" data-target="#confirmModal" data-msg="您确认要同意编号为[${e.id}]的邮件吗？" data-href="javascript:ajaxLoadPage2Body('${pageContext.request.contextPath}/game/mail/agree/${e.id}/');"><span class="label label-success">同意</span></a>
												<a style="cursor:pointer;" data-toggle="modal" data-target="#confirmModal" data-msg="您确认要拒绝编号为[${e.id}]的邮件吗？" data-href="javascript:ajaxLoadPage2Body('${pageContext.request.contextPath}/game/mail/refuse/${e.id}/');"><span class="label label-danger">拒绝</span></a>
											</c:when>
											<c:when test="${e.state == 1}">
												<!-- 回收功能 -->
												<c:if test="${e.mailType==2 || e.mailType==3}">
													<a style="cursor:pointer;" data-toggle="modal" data-target="#confirmModal" data-msg="您确认要回收编号为[${e.id}]的邮件吗？" data-href="javascript:ajaxLoadPage2Body('${pageContext.request.contextPath}/game/mail/delete/${e.id}/');"><span class="label label-danger">回收</span></a>
												</c:if>
												&nbsp;审批人(${e.auditName})
											</c:when>
											<c:when test="${e.state == 2}">
												拒绝人(${e.auditName})
											</c:when>
											<c:when test="${e.state == 3}">
												失败人(${e.auditName})
											</c:when>
											<c:otherwise>
												回收人(${e.auditName})
											</c:otherwise>
										</c:choose></td>
								</tr>
							</c:forEach>
						</tbody>
					</table>
				</div>
				<div class="box-footer clearfix">
					<jsp:include page="../../page.jsp">
						<jsp:param name="url" value="${pageContext.request.contextPath}/game/mail/approval/list/" />
					</jsp:include>
				</div>
			</div>
		</div>
	</div>
</section>
<script>
	$('.info-head').each(function(){
		initShort($(this), 10);
	});
	$('.info-content').each(function(){
		initShort($(this), 15);
	});
	$('.info-item').each(function(){
		initShort($(this), 10);
	});
	function initShort(dom, length){
		var data = dom.html().trim();
		var short_ = "";
		if(data.length > length){
			short_ = data.substring(0, length) + '...';
		}else{
			short_ = data;
		}
		dom.html('<div class="info-short">'+short_+'</div><div class="info-data" style="display:none;">'+data+'</div>');
	}
	$('.odd').click(function(){
		if($(this).find('.info-short').is(":visible")){
			$(this).find('.info-short').hide();
			$(this).find('.info-data').show();
		}else{
			$(this).find('.info-short').show();
			$(this).find('.info-data').hide();
		}
	});
</script>
