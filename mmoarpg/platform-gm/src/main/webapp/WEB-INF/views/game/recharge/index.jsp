<%@ page language="java" contentType="text/html;charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<section class="content-header">
	<h1>充值补单</h1>
	<ol class="breadcrumb">
		<li><a href="${pageContext.request.contextPath}/welcome/"><i class="fa fa-dashboard"></i> 首页</a></li>
		<li>游戏管理</li>
		<li class="active">充值补单</li>
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
					<h3 class="box-title">充值补单信息</h3>
					<c:if test="${sessionScope.SESSION_USER.auth.have['MANAGE_RECHARGE_LOG']}">
						<div class="box-tools input-group input-group-sm">
							<button type="button" class="btn btn-block btn-default btn-sm" onclick="javascript:ajaxLoadPage2Body('${pageContext.request.contextPath}/game/recharge/list/ui/');">充值补单记录</button>
						</div>
					</c:if>
				</div>
				<form role="form" action="${pageContext.request.contextPath}/game/recharge/add/" method="post" id="addSimulated">
					<div class="box-body">
						<div class="form-group">
							<label>类型：</label> 
							<label><input type="radio" name="playerType" value="0"> 角色ID &nbsp;</label>
							<label><input type="radio" name="playerType" value="1" checked> 角色名称&nbsp;</label>
						</div>
						<div id="playerId-div" class="form-group" style="display: none;">
							<label for="playerId">角色ID: <span class="text-red">*</span></label>
							<input id="playerId" class="form-control" placeholder="角色ID" type="text"  maxlength="64">
						</div>
						<div id="playerName-div" class="form-group">
							<label for="playerName">角色名称: <span class="text-red">*</span></label>
							<input id="playerName" class="form-control" placeholder="角色名称" type="text"  maxlength="64">
						</div>
						<div class="form-group">
							<label for="currencyAmt">充值钻石: <span class="text-red">*</span></label>
							<select class="form-control" id="currencyAmt">
								<option value="106"><spring:message code="template.recharge.product.106"/></option>
								<option value="105"><spring:message code="template.recharge.product.105"/></option>
								<option value="104"><spring:message code="template.recharge.product.104"/></option>
								<option value="103"><spring:message code="template.recharge.product.103"/></option>
								<option value="102"><spring:message code="template.recharge.product.102"/></option>
								<option value="101" selected><spring:message code="template.recharge.product.101"/></option>
								<option value="107"><spring:message code="template.recharge.product.107"/></option>
								<option value="108"><spring:message code="template.recharge.product.108"/></option>
								<option value="1"><spring:message code="template.recharge.product.1"/></option>
								<option value="2"><spring:message code="template.recharge.product.2"/></option>
								<option value="301"><spring:message code="template.recharge.product.301"/></option>
								<option value="302"><spring:message code="template.recharge.product.302"/></option>
								<option value="303"><spring:message code="template.recharge.product.303"/></option>
							</select>
						</div>
						<div class="form-group">
							<label for="reson">原因: <span class="text-red">*</span></label>
							<input id="reson" class="form-control" placeholder="请输入要补的单号，方便查证..." type="text" maxlength="64">
						</div>
						<button type="submit" id="btn_modify_simulated" class="btn btn-primary btn-lg btn-block">发送模拟货币</button>
					</div>
				</form>
			</div>
			
			<div class="alert alert-danger alert-dismissible">
				<button type="button" class="close" data-dismiss="alert" aria-hidden="true">×</button>
				<h4><i class="icon fa fa-ban"></i> 温馨提示! </h4>
				何为补单，玩家充值未到账，且确认有问题的异常订单才可以使用此功能给那玩家补上那笔充值... 
			</div>
		</div>
	</div>
</section>
<script type="text/javascript">
registerFocusClearErrorMsg();

//控制发送状态
$("input[name='playerType']").click(function() {
	// 使用角色ID做参数
	if ($(this).val() == 0) {
		$("#playerName-div").hide();
		$("#playerId-div").show();
	}
	//发给单个邮件带附件
	if ($(this).val() == 1) {
		$("#playerName-div").show();
		$("#playerId-div").hide();
	}
});

$('#addSimulated').on('submit', function() {
	var serverIds = [];
	$("button[type=button][name=server]").each(function() {
		if (!$(this).hasClass("btn-default")) {
			serverIds.push($(this).val());
		}
	});
	if (serverIds.length == 0) {
		addErrorMsg($("#server_list"), "请选择一个目标服务器....");
		return false; // 阻止表单自动提交事件
	}
	
	var type = $("input[name='playerType']:checked").val();
	var playerId = $("#playerId");
	if (type == 0){
		if (playerId.val().length == 0) {
			addErrorMsg(playerId, "请输入玩家ID...");
			return false;
		}
	}
	
	var playerName = $("#playerName");
	if(type == 1){
		if (playerName.val().length == 0) {
			addErrorMsg(playerName, "请输入玩家名称...");
			return false;
		}
	}
	
	var reson = $("#reson");
	if (reson.val() == '') {
		addErrorMsg(reson, "请输入本次充值补单的说明...");
		return false;
	} 
	
	var $btn = $("#btn_modify_simulated").button('loading');
	$.post($(this).attr("action"), {
		"serverId":serverIds[0],
		"type" : type,
		"player" : type==0?playerId.val():playerName.val(),
		"currencyAmt" : $("#currencyAmt").val(),
		"reson" : reson.val()
	}, function(result) {
		$btn.button('reset');
		if(result==0){
			resultCallback("目标服务器正在维护...", "javascript:ajaxLoadPage2Body('${pageContext.request.contextPath}/game/recharge/list/');");
		}else if(result ==1){
			resultCallback("充值成功，请赶快查看", "javascript:ajaxLoadPage2Body('${pageContext.request.contextPath}/game/recharge/list/');");
		}else{
			resultCallback("查无此玩家，请确认是否输入错误", "javascript:void(0);");
		}
	});
	return false; // 阻止表单自动提交事件
});
</script>