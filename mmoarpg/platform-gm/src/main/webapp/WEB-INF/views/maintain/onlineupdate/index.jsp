<%@ page language="java" contentType="text/html;charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<section class="content-header">
	<h1>更新线上区服</h1>
	<ol class="breadcrumb">
		<li><a href="${pageContext.request.contextPath}/welcome/"><i class="fa fa-dashboard"></i> 首页</a></li>
		<li>维护工具</li>
		<li class="active">更新线上区服</li>
	</ol>
</section>
<section class="content">
	<!-- 选服列表 -->
	<jsp:include page="../../serverlist.jsp">
		<jsp:param name="multiselect" value="true" />
		<jsp:param name="dosomething" value="javascript:selectServerId(this, true);" />
	</jsp:include>

	<div class="row">
		<div class="col-xs-12">
			<div class="box box-primary">
				<div class="box-header">
					<h3 class="box-title">当前正在使用${packet.gameserver}的版本...</h3>
				</div>
				<div class="box-body">
					<div id="uploadfile">
						<form action="${pageContext.request.contextPath}/maintain/onlineupdate/update/" method="post" id="uploadclassfile">
							<input type="hidden" id="packageId" name="packageId" value="${packet.id}">
							<input type="hidden" id="serverIds" name="serverIds[]">
							<button type="submit" id="btn_onekey_update" class="btn btn-primary btn-lg btn-block">一键更新</button>
						</form>
					</div>
				</div>
			</div>
		</div>
	</div>
	
	<div class="box box-warning">
		<div class="box-header">
			<h3 class="box-title">执行结果</h3>
		</div>
		<div class="box-body" id="exec_result">
        </div>
	</div>
</section>
<script type="text/javascript">
	registerFocusClearErrorMsg();
	
	$("#uploadclassfile").submit(function(evt) {
		evt.preventDefault();
		
		var serverIds = [];
		$("button[type=button][name=server]").each(function() {
			if (!$(this).hasClass("btn-default")) {
				serverIds.push($(this).val());
			}
		});
		if (serverIds.length == 0) {
			addErrorMsg($("#server_list"), "只少选择一个目标服务器....");
			return false; // 阻止表单自动提交事件
		}
		$("#serverIds").val(serverIds);
		
		var $btn = $("#btn_onekey_update").button('loading');
		$("#uploadclassfile").ajaxSubmit({
			success : function(result) {
				resultCallback("开始更新", "javascript:void(0);");
			},
			error : function(result) {
				resultCallback("更新失败", "javascript:void(0);");
			}
		});
	});
</script>
<script type="text/javascript">
	var websocket;
	if(websocket != null){
		websocket.close();
	}
	if ('WebSocket' in window) {
		websocket = new WebSocket("ws://"+document.location.host+"${pageContext.request.contextPath}/package/updateing");
	} else if ('MozWebSocket' in window) {
		websocket = new MozWebSocket("ws://${pageContext.request.contextPath}/package/updateing");
	} else {
		websocket = new SockJS("http://"+document.location.host+"/${pageContext.request.contextPath}/sockjs/package/updateing");
	}
	websocket.onopen = function(evnt) {
		websocket.send('ChatMonitor');
	};
	
	var pauseArray = new Array(12);//
	websocket.onmessage = function(evnt) {
		var json = $.parseJSON(evnt.data);
		var log = json.log;
		if(json.level == 'error'){
			log ="<span class='text-red'>"+log+"</span>";
		}
		var ResultDiv = $('#exec_result');
		ResultDiv.append(log + "<br/>").scrollTop(ResultDiv[0].scrollHeight);
	};
	websocket.onerror = function(evnt) {
	};
	websocket.onclose = function(evnt) {
	}
</script>