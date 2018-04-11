<%@ page language="java" contentType="text/html;charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>

<section class="content-header">
	<h1>查询仙盟详情</h1>
	<ol class="breadcrumb">
		<li><a href="${pageContext.request.contextPath}/welcome/"><i class="fa fa-dashboard"></i> 首页</a></li>
		<li>游戏管理</li>
		<li class="active">查询仙盟详情</li>
	</ol>
</section>

<section class="content">

	<!-- 选服列表 -->
	<jsp:include page="../../../serverlist.jsp">
		<jsp:param name="multiselect" value="false" />
		<jsp:param name="dosomething" value="javascript:selectServerId(this, false);" />
	</jsp:include>

	<div class="box box-primary">
		<div class="box-header with-border">
			<h3 class="box-title">查询条件 :</h3>
		</div>
		
		<div class="box-body">
			<div class="checkbox">
            	<label>
					<input type="checkbox" id="vague" ${vague ? "checked" : ""}> 模糊查询
           		</label>
            </div>
                
			<div class="form-group">
				<label for="guildname">仙盟名称 : <span class="text-red">*</span></label>
				<input id="guildname" class="form-control" placeholder="请输入玩家的仙盟名称..."  value="${guildname}" type="text">
			</div>
			<button id="btn_query_guild" class="btn btn-primary btn-lg btn-block">查询</button>
		</div>
	</div>
	
	<jsp:include page="../../../error.jsp" />

	<c:if test="${not empty result}">
		<c:choose>
			<c:when test="${vague}">
				<div class="box">
					<div class="box-header with-border">
						<div class="box-header with-border">
							<h3 class="box-title">模糊查询的仙盟列表</h3>
						</div>
						<div class="box-body">
							<table class="table table-bordered table-striped" id="player_list">
								<thead>
									<tr>
										<th>仙盟名称</th>
										<th>等级</th>
										<th>盟主</th>
										<th>查看详情</th>
									</tr>
								</thead>
								<tbody>
								<c:forEach var="e" items="${result.rows}">
									<tr role="row" class="odd">
										<td>${e.name}</td>
										<td>${e.level}</td>
										<td>${e.leader}</td>
										<td><a href="javascript:ajaxLoadPage2Body('${pageContext.request.contextPath}/game/query/guild/list/?serverId=${selectedServerId}&guildname=${e.name}&vague=false');">查看</a></td>
									</tr>
								</c:forEach>
								</tbody>
							</table>
						</div>
					</div>
				</div>
			</c:when>
			<c:otherwise>
				<div class="box">
					<div class="box-header with-border">
						<div class="box-header with-border">
							<h3 class="box-title">基础信息</h3>
						</div>
						<div class="box-body">
				        	<div class="form-group">
			                	<label>仙盟ID:</label>
			                	<input type="text" class="form-control" value="${result.id}" disabled="disabled">
			                </div>
			                <div class="form-group">
			                	<label>仙盟等级:</label>
			                	<input type="text" class="form-control" value="${result.level}" disabled="disabled">
			                </div>
			                <div class="form-group">
								<label>仙盟公告:</label>
								<c:choose>
									<c:when test="${sessionScope.SESSION_USER.auth.have['QUERY_UPATE_GUILD']}">
										<div class="box-tools pull-right">
											<button id="update_guild_notice" type="button" class="btn btn-block btn-primary btn-xs">修改公告</button>
										</div>
										<textarea id="guildNotice" class="form-control" rows="3">${result.notice}</textarea>
									</c:when>
									<c:otherwise>
										<textarea class="form-control" rows="3" disabled="disabled">${result.notice}</textarea>
									</c:otherwise>
								</c:choose>
			                </div>
			                <div class="form-group">
			                	<label>仙盟成员:</label>
			                	<table class="table table-bordered table-striped" id="guild_member_list">
									<thead>
										<tr>
											<th>职务</th>
											<th>角色名称</th>
											<th>等级</th>
											<th>职业</th>
											<th>战斗力</th>
											<th>仙盟贡献</th>
											<th>状态</th>
										</tr>
									</thead>
									<tbody>
									<c:forEach var="e" items="${result.rows}">
										<tr role="row" class="odd">
											<td>${e.duty}</td>
											<td>${e.roleName}</td>
											<td>${e.level}</td>
											<td>${e.pro}</td>
											<td>${e.power}</td>
											<td>${e.contribute}</td>
											<td>${e.state}</td>
										</tr>
									</c:forEach>
									</tbody>
								</table>
			                </div>
						</div>
					</div>
				</div>
			</c:otherwise>
		</c:choose>
	</c:if>
</section>

<script>
	$(function() {
		registerFocusClearErrorMsg();
		
		$("#btn_query_guild").click(function(){
			var serverIds = [];
			$("button[type=button][name=server]").each(function() {
				if (!$(this).hasClass("btn-default")) {
					serverIds.push($(this).val());
				}
			});
			if (serverIds.length == 0) {
				addErrorMsg($("#server_list"), "请先选择目标服务器....");
				return false; // 阻止表单自动提交事件
			}
			
			var guildname = $("#guildname");
			if (guildname.val().length == 0) {
				addErrorMsg(guildname, "请输入玩家的仙盟名称...");
				return false;
			}
			
			var serverId = serverIds[0];
			ajaxLoadPage2Body('${pageContext.request.contextPath}/game/query/guild/list/?serverId='+serverId+'&guildname='+guildname.val()+"&vague="+$("#vague").is(':checked'));
		});
		
		$('#guild_member_list').DataTable({
			language : {//国际化文件
				url : "${pageContext.request.contextPath}/resources/plugins/datatables/i18n/zh_CN.json"
			},
			dom : 'Bfrtip',
			buttons : [ {
	            extend: 'copy',
	            text:'复制'
	        }, {
	            extend: 'excel',
	            text:'导出',
	            title: '仙盟成员_${guildname}'
	        },  {
	            extend: 'print',
	            text:'打印'
	        } ],
	      	//跟数组下标一样，第一列从0开始，这里表格初始化时，第四列默认降序
            "order": [[ 2, "desc" ]]
		});
		
		// 修改公告...
		$("#update_guild_notice").click(function(){
			var guildNotice = $("#guildNotice");
			if (guildNotice.val().length == 0) {
				addErrorMsg(guildNotice, "请输入仙盟公告...");
				return false;
			}
			
			var $btn = $(this).button('loading');
			$.get('${pageContext.request.contextPath}/game/query/guild/update/?serverId=${selectedServerId}&guildname=${guildname}&guildId=${result.id}&notice='+guildNotice.val(), function(result){
				$btn.button('reset');
				resultCallback("操作成功", "javascript:ajaxLoadPage2Body('${pageContext.request.contextPath}/game/query/guild/list/?serverId=${selectedServerId}&guildname=${guildname}&vague=${vague}');");
			});
		});
	});
</script>