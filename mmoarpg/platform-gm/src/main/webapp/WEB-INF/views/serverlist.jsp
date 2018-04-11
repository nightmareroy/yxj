<%@ page language="java" contentType="text/html;charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<div class="box box-primary" id="server_list_div">
	<div class="box-header with-border">
		<h3 class="box-title">服务器列表</h3>
		<div class="box-tools pull-right">
			<button type="button" class="btn btn-box-tool" data-widget="collapse">
				<i class="fa fa-minus"></i>
			</button>
		</div>
	</div>
	<div class="box-body">
		<div class="box-group" id="server_list">
		
			<c:if test="${param.multiselect}">
				<button type="button" class="btn btn-primary btn-xs" onclick="javascript:AllSelect(this);">全选</button>
				<button type="button" class="btn btn-primary btn-xs" onclick="javascript:ReverseSelection(this);">反选</button>
				<p>
			</c:if>
			
			<c:forEach var="serverGroup" items="${servers}" varStatus="s">
				<div class="panel box box-success">
					<div class="box-header with-border">
						<h4 class="box-title">
							<a data-toggle="collapse" data-parent="#server_list" href="#collapse-${serverGroup.key}"> ${serverGroup.key} </a>
						</h4>
					</div>
					<div id="collapse-${serverGroup.key}" class="panel-collapse collapse ${s.index==0?'in':''}">
						<div class="box-body">
							<c:forEach var="server" items="${serverGroup.value}" varStatus="i">
								<button name="server" type="button" class="btn ${empty selectedServerMap[server.id] ? 'btn-default' : 'btn-primary'} btn-xs" style="min-width: 110px" value="${server.id}" onclick="${param.dosomething}">${server.serverName}(${server.id})</button>
							</c:forEach>
						</div>
					</div>
				</div>
			</c:forEach>
		</div>
	</div>
</div>