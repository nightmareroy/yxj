<%@ page language="java" contentType="text/html;charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<section class="content-header">
	<h1>${server==null?"添加大区":"编辑服务器" }</h1>
	<ol class="breadcrumb">
		<li><a href="${pageContext.request.contextPath}/welcome/"><i class="fa fa-dashboard"></i> 首页</a></li>
		<li>维护工具</li>
		<li><a href="javascript:ajaxLoadPage2Body('${pageContext.request.contextPath}/maintain/server/list/');">区服管理</a></li>
		<li class="active">${user==null?"添加大区":"编辑服务器" }</li>
	</ol>
</section>
<section class="content">
	<div class="row">
		<div class="col-xs-12">
			<div class="box box-primary">
				<div class="box-header">
					<h3 class="box-title">区服信息</h3>
				</div>
				<form role="form" action="${pageContext.request.contextPath}/maintain/server/${server==null?'add':'edit'}/" method="post" id="addServer">
					<div class="box-body">
						<div class="form-group">
							<label for="server_id">区服编号: <span class="text-red">*</span></label>
							<input id="server_id" class="form-control" placeholder="请输入区服编号..." type="text" value="${server.id}" maxlength="64" ${server==null?'':'disabled'}>
						</div>
						<div class="form-group">
							<label for="server_name">区服名称: <span class="text-red">*</span></label>
							<input id="server_name" class="form-control" placeholder="请输入区服名称..." type="text" value="${server.serverName}" maxlength="128">
						</div>
						
						<!-- 不是大区，还要添加其他信息 -->
						<c:if test="${server!=null&&server.areaId>0}">
							<div class="form-group">
								<label for="areaId">大区编号: <span class="text-red">*</span></label>
								<input id="areaId" class="form-control" type="text" value="${server.areaId}">
							</div>
							<div class="form-group">
								<label for="ip">链接地址: <span class="text-red">*</span></label>
								<input id="ip" class="form-control" type="text" value="${server.ip}:${server.port}" ${server==null?'':'disabled'}>
							</div>
							<div class="form-group">
								<label>开服日期: <span class="text-red">*</span></label>
								<div class="input-group">
									<div class="input-group-addon"><i class="fa fa-clock-o"></i></div>
									<input type="text" class="form-control pull-right" id="opendate">
								</div>
							</div>
							<div class="form-group">
								<label for="olLimit">在线上限: <span class="text-red">*</span></label>
								<input id="olLimit" class="form-control" type="text" value="${server.olLimit}">
							</div>
							
							<div class="form-group">
								<label for="showState">对外状态: <span class="text-red">*</span></label>
								<select id="showState" class="form-control select2" style="width: 100%;">
									<option value="0" ${server.showState == 0 ? 'selected':'' }>隐藏</option>
									<option value="1" ${server.showState == 1 ? 'selected':'' }>对内</option>
									<option value="2" ${server.showState == 2 ? 'selected':'' }>对外</option>
								</select>
							</div>
							<div class="form-group">
								<label>对外时间: <span class="text-red">*</span></label>
								<div class="input-group">
									<div class="input-group-addon"><i class="fa fa-clock-o"></i></div>
									<input type="text" class="form-control pull-right" id="externalTime">
								</div>
							</div>
						</c:if>
						
						<div class="form-group">
							<label for="server_describe">区服描述:</label>
							<input id="server_describe" class="form-control" placeholder="请输入区服描述..." type="text" value="${server.describe}" maxlength="128">
						</div>
						<button type="submit" id="btn_modify_server" class="btn btn-primary btn-lg btn-block">${server==null?"确认添加":"确认修改"}</button>
					</div>
				</form>
			</div>
		</div>
	</div>
</section>
<script type="text/javascript">
	registerFocusClearErrorMsg();
	$('#addServer').on('submit', function() {
		var server_id = $("#server_id");
		if (server_id.val().length == 0) {
			addErrorMsg(server_id, "请输入区服编号...");
			return false;
		}
		var server_name = $("#server_name");
		if (server_name.val().length == 0) {
			addErrorMsg(server_name, "请输入区服名称...");
			return false;
		}
		var $btn = $("#btn_modify_server").button('loading');
		$.post($(this).attr("action"), {
			"serverId" : server_id.val(),
			"serverName" : server_name.val(),
			"describe" : $("#server_describe").val(),
			"token" : "${sessionScope.token}",
			
			"areaId":$("#areaId").val(),
			"opendate":$("#opendate").val(),
			"olLimit":$("#olLimit").val(),
			"showState":$("#showState").val(),
			"externalTime":$("#externalTime").val(),
		}, function(result) {
			$btn.button('reset');
			if(result =='OK'){
				ajaxLoadPage2Body("${pageContext.request.contextPath}/maintain/server/list/");
			} else {
				ajaxLoadPage2Body("${pageContext.request.contextPath}/maintain/server/list/");
			}
		});
		return false; // 阻止表单自动提交事件
	});
	
	$('#opendate').daterangepicker({
	    "singleDatePicker": true,
	    "startDate": "${opendate}",
	    locale: {
            format: 'YYYY-MM-DD'
        },
	});
	$('#externalTime').daterangepicker({
	    "singleDatePicker": true,
	    timePicker24Hour: true,
		timePicker: true,
	    "startDate": "${externalTime}",
	    locale: {
            format: 'YYYY-MM-DD HH:mm:ss'
        },
	});
</script>