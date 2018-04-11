<%@ page language="java" contentType="text/html;charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<section class="content-header">
	<h1>福利申请</h1>
	<ol class="breadcrumb">
		<li><a href="${pageContext.request.contextPath}/welcome/"><i class="fa fa-dashboard"></i> 首页</a></li>
		<li>游戏管理</li>
		<li>福利管理</li>
		<li class="active">福利申请</li>
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
					<h3 class="box-title">邮件配置</h3>
				</div>
				<form role="form" action="${pageContext.request.contextPath}/game/mail/apply/" method="post" id="applyWelfare">
					<div class="box-body">
						<div class="form-group">
							<label>邮件类型：</label> 
							<label><input type="radio" name="mailType" value="0" checked> 单个不带附件 &nbsp;</label>
							<label><input type="radio" name="mailType" value="1"> 单个带附件&nbsp;</label>
							<label><input type="radio" name="mailType" value="2"> 全服不带附件&nbsp;</label>
							<label><input type="radio" name="mailType" value="3"> 全服带附件</label>
						</div>
						<div class="form-group" id="playerId-div">
							<label for="playerId">角色ID或角色名(多个角色用;分隔): <span class="text-red">*</span></label>
							<input id="playerId" class="form-control" placeholder="请输入用户ID编号..." type="text" value="${welfare.playerId}" maxlength="64">
						</div>
						<div class="form-group" id="createRoleTime-div" style="display: none;">
							<label for="createRoleTime">最后创角时间: <span class="text-red">*</span></label>
							<input id="createRoleTime" class="form-control" type="text" value="<fmt:formatDate value="${date}" pattern="yyyy-MM-dd HH:mm:ss"/>" maxlength="64">
						</div>
						<div class="form-group" id="minLevel-div" style="display: none;">
							<label for="minLevel">最低等级: <span class="text-red">*</span></label>
							<input id="minLevel" class="form-control" type="text" value="1" maxlength="64">
						</div>
						<div class="form-group">
							<label for="title">邮件标题: <span class="text-red">*</span></label>
							<input id="title" class="form-control" placeholder="请输入邮件标题..." type="text" value="${welfare.title}" maxlength="64">
						</div>
						<div class="form-group">
							<label for="content">邮件正文：<span class="text-red">*</span></label>
							<textarea id="content" class="form-control" placeholder="请输入邮件内容..." rows="4" maxlength="2048"></textarea>
						</div>
						<div class="form-group">
							<label for="reason">申请原因：<span class="text-red">*</span></label>
							<input id="reason" class="form-control" placeholder="申请原因" type="text" value="${welfare.playerId}" maxlength="64">
							<span class="help-block">必需写上本次申请的理由，否则不与通过...</span>
						</div>
						<div class="form-group" id="div-item" style="display: none;">
							<label>福利道具: <span class="text-red">*</span></label>
							<%@ include file="../../itemlist.jsp"%>
						</div>
						<button type="submit" id="btn_modify_welfare" class="btn btn-primary btn-lg btn-block">申请福利</button>
					</div>
				</form>
			</div>
		</div>
	</div>
</section>
<script>
	registerFocusClearErrorMsg();
	
	// 控制发送状态
	$("input[name='mailType']").click(function() {
		//发给单个邮件不带附件
		if ($(this).val() == 0) {
			$("#div-item").hide();
			$("#playerId-div").show();
			$("#createRoleTime-div").hide();
			$("#minLevel-div").hide();
		}
		//发给单个邮件带附件
		if ($(this).val() == 1) {
			$("#div-item").show();
			$("#playerId-div").show();
			$("#createRoleTime-div").hide();
			$("#minLevel-div").hide();
		}
		//发给全服邮件不带附件
		if ($(this).val() == 2) {
			$("#div-item").hide();
			$("#playerId-div").hide();
			$("#createRoleTime-div").show();
			$("#minLevel-div").show();
		}
		//发给全服邮件带附件
		if ($(this).val() == 3) {
			$("#div-item").show();
			$("#playerId-div").hide();
			$("#createRoleTime-div").show();
			$("#minLevel-div").show();
		}
	});
	$('#applyWelfare').on('submit', function() {
		var mailType = $("input[name='mailType']:checked").val();
		var playerId = $("#playerId");
		var createRoleTime = $("#createRoleTime");
		var minLevel = $("#minLevel");
		
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

		//发给单个玩家
		if (mailType == 0 || mailType == 1) {
			if (serverIds.length != 1) {
				addErrorMsg($("#server_list"), "只能选择一个目标服务器....");
				return false; // 阻止表单自动提交事件
			}
			if (playerId.val().length == '') {
				addErrorMsg(playerId, "请输入用户ID编号...");
				return false;
			}
		}
		// 全服发送
		else {
			if (createRoleTime.val().length == '') {
				addErrorMsg(createRoleTime, "全服发送，请选择一个最晚创角时间...");
				return false;
			}
			if (minLevel.val().length == '') {
				addErrorMsg(minLevel, "全服发送，请选择一个最低等级...");
				return false;
			}
		}
		var title = $("#title");
		if (title.val().length == '') {
			addErrorMsg(title, "请输入邮件标题...");
			return false;
		}
		var content = $("#content");
		if (content.val().length == '') {
			addErrorMsg(content, "请输入邮件内容...");
			return false;
		}
		var reason = $("#reason");
		if (reason.val().length == '') {
			addErrorMsg(reason, "请输入原因");
			return false;
		}

		var itemIdArray = [];
		$("input[id=itemId]").each(function() {
			itemIdArray.push($(this).val());
		});
		var itemNameArray = [];
		$("input[id=itemName]").each(function() {
			itemNameArray.push($(this).val());
		});
		var itemNumArray = [];
		$("input[id=itemNum]").each(function() {
			itemNumArray.push($(this).val());
		});
		var $btn = $("#btn_modify_welfare").button('loading');
		$.post($(this).attr("action"), {
			"serverIds" : serverIds,
			"mailType" : mailType,
			"playerId" : playerId.val(),
			"content" : content.val(),
			"reason" : reason.val(),
			"itemIdList" : itemIdArray,
			"itemNameList" : itemNameArray,
			"itemNumList" : itemNumArray,
			"title" : title.val(),
			"createRoleTime" : createRoleTime.val(),
			"minLevel" : minLevel.val()
		}, function(result) {
			if(result == 'ok'){
				// 如果有审批权限，直接跳过去，没有还在本页
				<c:if test="${sessionScope.SESSION_USER.auth.have['MANAGE_MAIL_APPROVAL']}">
				resultCallback("申请成功，耐心等待大大审批", "javascript:ajaxLoadPage2Body('${pageContext.request.contextPath}/game/mail/approval/manage/');");
				</c:if>
				<c:if test="${!sessionScope.SESSION_USER.auth.have['MANAGE_MAIL_APPROVAL']}">
				resultCallback("申请成功，耐心等待大大审批", "javascript:ajaxLoadPage2Body('${pageContext.request.contextPath}/game/mail/manage/');");
				</c:if>
			} else {
				$btn.button('reset');
				resultCallback("申请失败，用户ID无法解析...", "javascript:void(0)';");
			}
	    });
		return false;
	});
	
	$('#post-time').daterangepicker({
		singleDatePicker: true,
		timePicker24Hour: true,
		timePicker: true,
		single: true,
        locale: {
            format: 'YYYY-MM-DD HH:mm:ss'
        },
        ranges: {
            '今天': [moment(), moment()],
            '昨天': [moment().subtract(1, 'days'), moment().subtract(1, 'days')],
            '最近7天': [moment().subtract(6, 'days'), moment()],
            '最近30开': [moment().subtract(29, 'days'), moment()],
            '这个月': [moment().startOf('month'), moment().endOf('month')],
            '上个月': [moment().subtract(1, 'month').startOf('month'), moment().subtract(1, 'month').endOf('month')]
         }
	});
</script>