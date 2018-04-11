<%@ page language="java" contentType="text/html;charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

<section class="content-header">
	<h1>${whitelist==null?'新增名单':'编辑名单'}</h1>
	<ol class="breadcrumb">
		<li><a href="${pageContext.request.contextPath}/welcome/"><i class="fa fa-dashboard"></i> 首页</a></li>
		<li>运维工具</li>
		<li><a href="javascript:ajaxLoadPage2Body('${pageContext.request.contextPath}/maintain/whitelist/list/');">名单列表</a></li>
		<li class="active">${whitelist==null?'新增名单':'编辑名单'}</li>
	</ol>
</section>

<section class="content">
	<div class="row">
		<div class="col-xs-12">
			<div class="box box-primary">
				<div class="box-header">
					<h3 class="box-title">名单配置</h3>
				</div>
				<form role="form" action="${pageContext.request.contextPath}/maintain/whitelist/${whitelist==null?'add':'edit'}/" method="post" id="editwhitelist">
					<div class="box-body">
						<div class="form-group">
							<label for="whitelisttype">白名单类型: <span class="text-red">*</span></label>
							<select class="form-control select2" style="width: 100%;" id="whitelisttype" ${whitelist==null?'':'disabled'}>
								<option value="1" ${whitelist.white==1?'selected':''}>白名单</option>
								<option value="0" ${whitelist.white==0?'selected':''}>黑名单</option>
							</select>
						</div>
						<div class="form-group">
							<label for="type">类型: <span class="text-red">*</span></label>
							<select class="form-control select2" style="width: 100%;" id="type" ${whitelist==null?'':'disabled'}>
								<option value="1" ${whitelist.type==1?'selected':''}>IP</option>
								<option value="2" ${whitelist.type==2?'selected':''}>UID</option>
							</select>
						</div>
						<div class="form-group">
							<label for="ip">IP或UID: <span class="text-red">*</span></label>
							<input id="ip" class="form-control" placeholder="请输入IP或UID..." value="${whitelist.ip}" type="text" maxlength="64" ${whitelist==null?'':'disabled'}>
						</div>
						<div class="form-group">
							<label for="hour">终止时间（单位：小时）0=永久: <span class="text-red">*</span></label>
							<input id="hour" class="form-control" placeholder="请终止时间（单位：小时）0=永久..." value="${whitelist.time}" type="text" maxlength="64">
						</div>
						<div class="form-group">
							<label for="describe">备注：<span class="text-red">*</span></label>
							<input id="describe" class="form-control" placeholder="请给个备注，以便理解此次操作的原因嘛..." value="${whitelist.desc}" type="text" maxlength="64">
						</div>
						<button type="submit" id="btn_edit_whitelist" class="btn btn-primary btn-lg btn-block">提交</button>
					</div>
				</form>
			</div>
		</div>
	</div>
</section>
<script>
	registerFocusClearErrorMsg();
	$('#editwhitelist').on('submit', function() {
		var ip = $("#ip");
		if (ip.val().length == '') {
			addErrorMsg(ip, "请输入IP或UID...");
			return false;
		}
		var hour = $("#hour");
		if (hour.val().length == '') {
			addErrorMsg(hour, "请终止时间（单位：小时）0=永久...");
			return false;
		}
		var describe = $("#describe");
		if (describe.val().length == '') {
			addErrorMsg(describe, "请给个备注，以便理解此次操作的原因嘛...");
			return false;
		}
		var $btn = $("#btn_edit_whitelist").button('loading');
		$.post($(this).attr("action"), {
			"whitelisttype":$("#whitelisttype").val(),
			"type" : $("#type").val(),
			"ip" : ip.val(),
			"hour" : hour.val(),
			"describe" : describe.val(),
			"token" : "${sessionScope.token}"
		}, function(result) {
			$btn.button('reset');
			resultCallback("操作成功", "javascript:ajaxLoadPage2Body('${pageContext.request.contextPath}/maintain/whitelist/list/');");
	    });
		return false;
	});
</script>