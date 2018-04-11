<%@ page language="java" contentType="text/html;charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

<section class="content-header">
	<h1>新增处罚</h1>
	<ol class="breadcrumb">
		<li><a href="${pageContext.request.contextPath}/welcome/"><i class="fa fa-dashboard"></i> 首页</a></li>
		<li>游戏管理</li>
		<li><a href="javascript:ajaxLoadPage2Body('${pageContext.request.contextPath}/game/publish/list/');">处罚管理</a></li>
		<li class="active">新增处罚</li>
	</ol>
</section>
<section class="content">

	<!-- 选服列表 -->
	<jsp:include page="../../serverlist.jsp">
		<jsp:param name="multiselect" value="false" />
		<jsp:param name="dosomething" value="javascript:selectServerId(this, false);" />
	</jsp:include>

	<div class="row">
		<div class="col-xs-12">
			<div class="box box-primary">
				<div class="box-header">
					<h3 class="box-title">处罚信息</h3>
				</div>
				<form role="form" action="${pageContext.request.contextPath}/game/publish/active/" method="post" id="publish">
					<div class="box-body">
						<div class="form-group">
							<label>操作类型：</label> 
							<label> <input type="radio" name="Type" value="0" checked> 封号 &nbsp;</label>
							<label> <input type="radio" name="Type" value="1"> 解封 &nbsp; </label>
							<label> <input type="radio" name="Type" value="2"> 禁言 &nbsp; </label>
							<label> <input type="radio" name="Type" value="3"> 解禁 &nbsp; </label>
							<label> <input type="radio" name="Type" value="4"> T下线 &nbsp; </label>
						</div>
						<div class="form-group" id="userId-div">
							<label for="playerName">角色名称: <span class="text-red">*</span></label>
							<input id="playerName" class="form-control" placeholder="角色名称" type="text" maxlength="64">
						</div>
						<div class="form-group" id="datepicker-div">
							<label>结束时间 : <span class="text-red">*</span></label>
							<div class="input-group date">
								<div class="input-group-addon">
									<i class="fa fa-calendar"></i>
								</div>
								<input class="form-control pull-right" id="datepicker" value="${today}" type="text">
							</div>
						</div>
						<div class="form-group">
							<label for="reason">处罚原因：<span class="text-red">*</span></label>
							<input id="reason" class="form-control" placeholder="处罚原因" type="text" maxlength="64">
						</div>
						<button type="submit" id="btn_modify_publish" class="btn btn-primary btn-lg btn-block">提交</button>
					</div>
				</form>
			</div>
		</div>
	</div>
</section>
<script>
	$(function() {
		$('#datepicker').daterangepicker({
			"singleDatePicker": true,
			timePicker24Hour: true,
			timePicker: true,
	        locale: {
	            format: 'YYYY-MM-DD HH:mm:ss'
	        }
		});
	});
	$("input[name='Type']").click(function() {
		//封号处理
		if ($(this).val() == 0) {
			$("#datepicker-div").show();
			$("#userId-div").show();
		}
		//解封处理
		if ($(this).val() == 1) {
			$("#datepicker-div").hide();
			$("#userId-div").show();
		}
		//禁言处理
		if ($(this).val() == 2) {
			$("#datepicker-div").show();
			$("#userId-div").show();
		}
		//解禁处理
		if ($(this).val() == 3 || $(this).val() == 4) {
			$("#datepicker-div").hide();
			$("#userId-div").show();
		}
	});
	$('#publish').on('submit', function() {
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
		
		var serverId = serverIds[0];
		
		
		var Type = $("input[name='Type']:checked").val();
		var playerName = $("#playerName");
		if (playerName.val().length == '') {
			addErrorMsg(playerName, "请输入角色名称...");
			return false;
		}
		var datepicker = $("#datepicker");
		if (Type == 0 || Type == 2) {
		   if (datepicker.val().length == '') {
			addErrorMsg(datepicker, "请输入处罚结束时间...");
			return false;
		   }
		}
		var reason = $("#reason");
		if (reason.val().length == '') {
			addErrorMsg(reason, "请输入处罚他的原因");
			return false;
		}
		
		var $btn = $("#btn_modify_publish").button('loading');
		$.post($(this).attr("action"), {
			"serverId":serverId,
			"type" : Type,
			"playerName" : playerName.val(),
			"datepicker" : datepicker.val(),
			"reason" : reason.val()
		}, function(result) {
			$btn.button('reset');
			if (result == 'SERVER_NOT_FOUND'){
				resultCallback("服务器正在维护中...", "javascript:void(0);");
			} else if (result == 'PLAYER_NOT_FOUND'){
				resultCallback("您输入的角色名称未找到.", "javascript:void(0);");
			} else {
				resultCallback("操作成功", "javascript:ajaxLoadPage2Body('${pageContext.request.contextPath}/game/publish/list/');");
			}
	    });
		return false;
	});
</script>