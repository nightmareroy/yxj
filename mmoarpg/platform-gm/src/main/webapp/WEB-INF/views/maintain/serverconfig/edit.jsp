<%@ page language="java" contentType="text/html;charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<section class="content-header">
	<h1>${config==null?"添加新区":"编辑配置" }</h1>
	<ol class="breadcrumb">
		<li><a href="${pageContext.request.contextPath}/welcome/"><i class="fa fa-dashboard"></i> 首页</a></li>
		<li>维护工具</li>
		<li><a href="javascript:ajaxLoadPage2Body('${pageContext.request.contextPath}/maintain/server/list/');">区服管理</a></li>
		<li class="active">${config==null?"添加新区":"编辑配置" }</li>
	</ol>
</section>
<section class="content">
	<div class="row">
		<div class="col-xs-12">
			<div class="box box-primary">
				<div class="box-header">
					<h3 class="box-title">区服信息</h3>
				</div>
				<form role="form" action="${pageContext.request.contextPath}/maintain/serverconfig/${config==null?'add':'edit'}/" method="post" id="addServer">
					<div class="box-body">
						<div class="form-group">
							<label for="id">区服编号: <span class="text-red">*</span></label>
							<input id="id" class="form-control" placeholder="请输入区服编号..." type="text" value="${config.id}" maxlength="64" ${config==null?'':'disabled'}>
							<p class="help-block">区服编号10001开始...</p>
						</div>
						<div class="form-group">
							<label for="name">区服名称: <span class="text-red">*</span></label>
							<input id="name" class="form-control" type="text" value="${config.serverName}" maxlength="128">
							<p class="help-block">虽然此配置没有什么用，但也要写一个便与识别的名称...</p>
						</div>
						<div class="form-group">
							<label for="template">共享配置模板编号: <span class="text-red">*</span></label>
							<input id="template" class="form-control" type="text" value="${config.template}" maxlength="64">
							<p class="help-block">目前只有腾讯安卓tencent...</p>
						</div>
						<div class="form-group">
							<label for="appId">应用编号: <span class="text-red">*</span></label>
							<input id="appId" class="form-control" type="text" value="${config.appId}" maxlength="128">
							<p class="help-block">默认80，请勿随意修改...</p>
						</div>
						<div class="form-group">
							<label for="areaId">大区编号: <span class="text-red">*</span></label>
							<input id="areaId" class="form-control" type="text" value="${config.areaId}" maxlength="128">
							<p class="help-block">选择界面左边导航编号...</p>
						</div>
						<div class="form-group">
							<label for="gameHost">机器IP: <span class="text-red">*</span></label>
							<input id="gameHost" class="form-control" type="text" value="${config.gameHost}" maxlength="128">
							<p class="help-block">区服所在机器IP...</p>
						</div>
						
						<div class="form-group">
							<label for="pubhost">区服公网IP(域名): <span class="text-red">*</span></label>
							<input id="pubhost" class="form-control" type="text" value="${config.pubhost}" maxlength="128">
							<p class="help-block">区服公网IP,可以配置域名...</p>
						</div>
						<div class="form-group">
							<label for="port">区服对外端口: <span class="text-red">*</span></label>
							<input id="port" class="form-control" type="text" value="${config.port}" maxlength="128">
							<p class="help-block">建议区间3010-3019，注意防火墙或安全组里的端口开放...</p>
						</div>
						
						<div class="form-group">
							<label for="redisHost">存档RedisIP: <span class="text-red">*</span></label>
							<input id="redisHost" class="form-control" type="text" value="${config.redisHost}" maxlength="128">
							<p class="help-block">默认都是本地Redis，127.0.0.1</p>
						</div>
						<div class="form-group">
							<label for="redisPort">存档Redis端口: <span class="text-red">*</span></label>
							<input id="redisPort" class="form-control" type="text" value="${config.redisPort}" maxlength="128">
							<p class="help-block">建议同步对外端口最后一位，方便管理</p>
						</div>
						<div class="form-group">
							<label for="redisPassword">存档Redis密码: </label>
							<input id="redisPassword" class="form-control" type="text" value="${config.redisPassword}" maxlength="128">
							<p class="help-block">建议使用有密码的Redis...</p>
						</div>
						<div class="form-group">
							<label for="redisIndex">存档RedisDB编号: <span class="text-red">*</span></label>
							<input id="redisIndex" class="form-control" type="text" value="${config.redisIndex}" maxlength="128">
							<p class="help-block">建议每个区服一个进程，默认为0</p>
						</div>
						
						<div class="form-group">
							<label for="battleHost">战斗服地址: <span class="text-red">*</span></label>
							<input id="battleHost" class="form-control" type="text" value="${config.battleHost}" maxlength="128">
							<p class="help-block">请配置局域网IP地址...</p>
						</div>
						<div class="form-group">
							<label for="battleFastPort">战斗服端口: <span class="text-red">*</span></label>
							<input id="battleFastPort" class="form-control" type="text" value="${config.battleFastPort}" maxlength="128">
							<p class="help-block">同步对外端口，3360起...</p>
						</div>
						<div class="form-group">
							<label for="battleIcePort">ICE端口: <span class="text-red">*</span></label>
							<input id="battleIcePort" class="form-control" type="text" value="${config.battleIcePort}" maxlength="128">
							<p class="help-block">同步对外端口，3900起...</p>
						</div>
						<button type="submit" id="btn_modify_server" class="btn btn-primary btn-lg btn-block">${config==null?"确认添加":"确认修改"}</button>
					</div>
				</form>
			</div>
		</div>
	</div>
</section>
<script type="text/javascript">
	registerFocusClearErrorMsg();
	$('#addServer').on('submit', function() {
		var id = $("#id");
		if (id.val().length == 0) {
			addErrorMsg(id, "请输入区服编号...");
			return false;
		}
		
		var name = $("#name");
		if (name.val().length == 0) {
			addErrorMsg(name, "请输入区服名称...");
			return false;
		}
		var template = $("#template");
		if (template.val().length == 0) {
			addErrorMsg(template, "请输入共享配置模板编号...");
			return false;
		}
		
		var appId = $("#appId");
		if (appId.val().length == 0) {
			addErrorMsg(appId, "请输入应用编号，默认80，请勿随意修改...");
			return false;
		}
		
		var areaId = $("#areaId");
		if (areaId.val().length == 0) {
			addErrorMsg(areaId, "请输入大区编号，选择界面左边导航编号...");
			return false;
		}
		var gameHost = $("#gameHost");
		if (gameHost.val().length == 0) {
			addErrorMsg(gameHost, "请输入机器所在IP，虽然没有什么其他用...");
			return false;
		}
		
		var pubhost = $("#pubhost");
		if (pubhost.val().length == 0) {
			addErrorMsg(pubhost, "请输入区服公网IP,可以配置域名...");
			return false;
		}
		var port = $("#port");
		if (port.val().length == 0) {
			addErrorMsg(port, "请输入对外端口，建议区间3010-3019，注意防火墙或安全组里的端口开放...");
			return false;
		}
		
		var redisHost = $("#redisHost");
		if (redisHost.val().length == 0) {
			addErrorMsg(redisHost, "请输入存档Redis地址...");
			return false;
		}
		var redisPort = $("#redisPort");
		if (redisPort.val().length == 0) {
			addErrorMsg(redisPort, "请输入存档Redis端口...");
			return false;
		}
		
		var redisPassword = $("#redisPassword");
		var redisIndex = $("#redisIndex");
		if (redisIndex.val().length == 0) {
			addErrorMsg(redisIndex, "请输入存档Redis库索引编号...");
			return false;
		}
		
		var battleHost = $("#battleHost");
		if (battleHost.val().length == 0) {
			addErrorMsg(battleHost, "请输入战斗服地址...");
			return false;
		}
		var battleFastPort = $("#battleFastPort");
		if (battleFastPort.val().length == 0) {
			addErrorMsg(battleFastPort, "请输入战斗服端口...");
			return false;
		}
		var battleIcePort = $("#battleIcePort");
		if (battleIcePort.val().length == 0) {
			addErrorMsg(battleIcePort, "请输入战斗服ICE端口...");
			return false;
		}
		
		var $btn = $("#btn_modify_server").button('loading');
		$.post($(this).attr("action"), {
			"id" : id.val(),
			"name" : name.val(),
			"appId" : appId.val(),
			"areaId" : areaId.val(),
			"gameHost" : gameHost.val(),
			"template" : template.val(),
			"token" : "${sessionScope.token}",
			
			"pubhost":pubhost.val(),
			"port":port.val(),
			
			"redisHost":redisHost.val(),
			"redisPort":redisPort.val(),
			"redisPassword":redisPassword.val(),
			"redisIndex":redisIndex.val(),
			
			"battleHost":battleHost.val(),
			"battleFastPort":battleFastPort.val(),
			"battleIcePort":battleIcePort.val()
		}, function(result) {
			$btn.button('reset');
			if(result =='OK'){
				ajaxLoadPage2Body("${pageContext.request.contextPath}/maintain/serverconfig/list/");
			} else {
				ajaxLoadPage2Body("${pageContext.request.contextPath}/maintain/serverconfig/list/");
			}
		});
		return false; // 阻止表单自动提交事件
	});
</script>