<%@ page language="java" contentType="text/html;charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<section class="content-header">
	<h1>停机维护准备</h1>
	<ol class="breadcrumb">
		<li><a href="${pageContext.request.contextPath}/welcome/"><i class="fa fa-dashboard"></i> 首页</a></li>
		<li>运维工具</li>
		<li class="active">停机维护准备</li>
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
					<h3 class="box-title">配置对外时间</h3>
				</div>
				<form role="form" action="${pageContext.request.contextPath}/maintain/ready/reset/" method="post" id="ready_reset">
					<div class="box-body">
						<div class="form-group" id="createRoleTime-div">
							<label for="externalTime">维护结束时间: <span class="text-red">*</span></label>
							<input id="externalTime" class="form-control" type="text" maxlength="64">
							<span class="help-block">请输入维护结束时间...</span>
						</div>
						<button type="submit" id="btn_modify_time" class="btn btn-primary btn-lg btn-block">修改对外时间</button>
					</div>
				</form>
			</div>
		</div>
	</div>
</section>
<script>
	registerFocusClearErrorMsg();
	
	$('#ready_reset').on('submit', function() {
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
		
		var externalTime = $("#externalTime");
		if (externalTime.val().length == '') {
			addErrorMsg(externalTime, "请输入维护结束时间...");
			return false;
		}

		var $btn = $("#btn_modify_time").button('loading');
		$.post($(this).attr("action"), {
			"serverIds" : serverIds,
			"externalTime" : externalTime.val()
		}, function(result) {
			$btn.button('reset');
			resultCallback("设计成功，确认返回区服列表", "javascript:ajaxLoadPage2Body('${pageContext.request.contextPath}/maintain/server/list');");
	    });
		return false;
	});
	
	$('#externalTime').daterangepicker({
		singleDatePicker: true,
		timePicker24Hour: true,
		timePicker: true,
		single: true,
        locale: {
            format: 'YYYY-MM-DD HH:mm:ss'
        }
	});
</script>