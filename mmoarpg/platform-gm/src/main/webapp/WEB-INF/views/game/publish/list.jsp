<%@ page language="java" contentType="text/html;charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<section class="content-header">
	<h1>处罚管理</h1>
	<ol class="breadcrumb">
		<li><a href="${pageContext.request.contextPath}/welcome/"><i class="fa fa-dashboard"></i> 首页</a></li>
		<li>游戏管理</li>
		<li class="active">处罚管理</li>
	</ol>
</section>
<section class="content">
	<div class="row">
		<div class="col-xs-12">
			<div class="box">
				<div class="box-header">
					<h3 class="box-title">
						<i class="fa fa-list"></i> 惩处记录
					</h3>
					<div class="box-tools input-group input-group-sm">
						<button type="button" class="btn btn-default btn-sm" onclick="javascript:ajaxLoadPage2Body('${pageContext.request.contextPath}/game/publish/addUI/');">新增处罚</button>
						&nbsp;
						<button type="button" class="btn btn-default btn-sm" onclick="javascript:ajaxLoadPage2Body('${pageContext.request.contextPath}/game/publish/query/');">高级搜索</button>
					</div>
				</div>
				<div class="box-body table-responsive no-padding">
					<table class="table table-hover">
						<tbody>
							<tr>
								<th>操作时间</th>
								<th>角色ID</th>
								<th>角色名称</th>
								<th>惩处类型</th>
								<th>惩罚时长</th>
								<th>惩罚原因</th>
								<th>操作者</th>
								<th>快捷操作</th>
							</tr>
							<c:forEach var="e" items="${page.content}">
								<tr role="row" class="odd">
									<td><fmt:formatDate value="${e.createtime}" pattern="yyyy-MM-dd HH:mm:ss"/></td>
									<td>${e.playerId}</td>
									<td>${e.playerName}</td>
									<td><c:choose>
											<c:when test="${e.type == 0}">
												<span class="label label-danger">手动封号</span>
											</c:when>
											<c:when test="${e.type == 1}">
												<span class="label label-success">手动解封</span>
											</c:when>
											<c:when test="${e.type == 2}">
												<span class="label label-warning">手动禁言</span>
											</c:when>
											<c:when test="${e.type == 3}">
												<span class="label label-success">手动解禁</span>
											</c:when>
											<c:when test="${e.type == 4}">
												<span class="label label-success">T下线</span>
											</c:when>
											<c:otherwise>
												<span class="label label-success">自动禁言</span>
											</c:otherwise>
										</c:choose></td>
									<td><c:choose>
											<c:when test="${e.type == 0}">
												<fmt:formatDate value="${e.publishTime}" pattern="yyyy-MM-dd HH:mm:ss"/>
											</c:when>
											<c:when test="${e.type == 1}">
												-
											</c:when>
											<c:when test="${e.type == 2 || e.type == 4}">
												<fmt:formatDate value="${e.publishTime}" pattern="yyyy-MM-dd HH:mm:ss"/>
											</c:when>
											<c:otherwise>
												-
											</c:otherwise>
										</c:choose></td>
									<td class="reason-data" style="height:30px;display:block;overflow:hidden;">
										<div class="reason-short"></div>
										<div class="reason-info">${fn:escapeXml(e.reason)}</div>
									</td>	
									<td>${e.name}(${e.username})</td>
									<td><c:choose>
											<c:when test="${e.type == 0}">
												<a style="cursor:pointer;"data-toggle="modal" data-target="#confirmModal" data-msg="请再次确认是否要解除该玩家的封号" 
												data-href="javascript:unPublish(${e.serverId}, '${e.playerName}',1, '管理界面一键解封');">解除封号</a>
											</c:when>
											<c:when test="${e.type == 2 || e.type == 5}">
												<a style="cursor:pointer;"data-toggle="modal" data-target="#confirmModal" data-msg="请再次确认是否要解除该玩家的禁言" 
												data-href="javascript:unPublish(${e.serverId}, '${e.playerName}',3, '管理界面一键解禁');">解除禁言</a>
											</c:when>
										</c:choose></td>
								</tr>
							</c:forEach>
						</tbody>
					</table>
				</div>
				<div class="box-footer clearfix">
					<jsp:include page="../../page.jsp">
						<jsp:param name="url" value="${pageContext.request.contextPath}/game/publish/list/" />
						<jsp:param name="args" value="start=${start}&end=${end}&username=${username}&type=${type}&playerId=${playerId}" />
					</jsp:include>
				</div>
			</div>
		</div>
	</div>
</section>
<script>
	$('.reason-data').each(function(i){
		var info = $(this).find('.reason-info').html();
		if(info.length > 15){
			$(this).find('.reason-short').html(info.substring(0, 15) + "...<font color='#00a65a'>详细</font>");
		}else{
			$(this).find('.reason-short').html(info).hide();
		}
	});
	$('.reason-data').click(function(){
		if($(this).css("height") == "30px"){
			$(this).find('.reason-short').hide();
			$(this).find('.reason-info').show();
			$(this).css("height", "auto");
		}else{
			$(this).find('.reason-short').show();
			$(this).find('.reason-info').hide();
			$(this).css("height", "30px");
		}
	});	
	function unPublish(serverId, playerName, type, reason){
		$.post("${pageContext.request.contextPath}/game/publish/active/?serverId="+serverId+"&playerName="+playerName+"&type="+type+"&reason="+reason,function(result){
			ajaxLoadPage2Body("${pageContext.request.contextPath}/game/publish/list/");
		});
	}
</script>