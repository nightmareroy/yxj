<%@ page language="java" contentType="text/html;charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<section class="content-header">
	<h1>${loginPlatform==null?"添加登录平台":"编辑登录平台" }</h1>
	<ol class="breadcrumb">
		<li><a href="${pageContext.request.contextPath}/welcome/"><i class="fa fa-dashboard"></i> 首页</a></li>
		<li>后台管理</li>
		<li><a href="javascript:ajaxLoadPage2Body('${pageContext.request.contextPath}/backstage/loginplatform/manage/');">平台管理</a></li>
		<li class="active">${loginPlatform==null?"添加登录平台":"编辑登录平台" }</li>
	</ol>
</section>
<section class="content">
	<div class="row">
		<div class="col-xs-12">
			<div class="box box-primary">
				<div class="box-header">
					<h3 class="box-title">登录平台信息</h3>
				</div>
				<form role="form" action="${pageContext.request.contextPath}/backstage/loginplatform/${loginPlatform==null?'add':'edit'}/" method="post" id="addLoginPlatform">
					<div class="box-body">
					<div class="form-group">
							<label for="loginPlatformId">平台编号: <span class="text-red">*</span></label> <input id="loginPlatformId" class="form-control" placeholder="平台名称" type="text" value="${loginPlatform.id}" maxlength="64" ${loginPlatform==null?'':'disabled'}>
						</div>
						<div class="form-group">
							<label for="loginPlatformName">名称: <span class="text-red">*</span></label> <input id="loginPlatformName" class="form-control" placeholder="登录方式" type="text" value="${loginPlatform.name}" maxlength="64" >
						</div>
						<div class="form-group">
							<label for="secretkey">秘钥: <span class="text-red">*</span></label> <input id="secretkey" class="form-control" placeholder="秘钥" type="text" value="${loginPlatform.secretkey}" maxlength="64">
						</div>
						<input id="loginPlatform_id" type="hidden" value="${loginPlatform.id }">
						<button type="submit" id="btn_modify" class="btn btn-primary btn-lg btn-block">${loginPlatform==null?"提交申请":"确认修改"}</button>
					</div>
				</form>
			</div>
		</div>
	</div>
</section>
<script type="text/javascript">
$(".select2").select2();
registerFocusClearErrorMsg();
$('#addLoginPlatform').on('submit', function() {
	var loginPlatformName = $("#loginPlatformName");
	if (loginPlatformName.val().length == 0) {
		addErrorMsg(loginPlatformName, "请输入权限组账号");
		return false;
	}
	var loginPlatformId = $("#loginPlatformId");
	if (loginPlatformId.val().length == 0) {
		addErrorMsg(loginPlatformId, "请输入平台名称");
		return false;
	}
	var secretkey = $("#secretkey");
	if (secretkey.val().length == 0) {
		addErrorMsg(secretkey, "请输入权限组名称");
		return false;
	}
	var $btn = $("#btn_modify").button('loading');
	$.post($(this).attr("action"), {
		"id" : $("#loginPlatform_id").val(),
		"loginPlatformId" : loginPlatformId.val(),
		"secretkey" : secretkey.val(),
		"loginPlatformName" : loginPlatformName.val(),
		"token" : "${sessionScope.token}"
	}, function(result) {
		$btn.button('reset');
		ajaxLoadPage2Body("${pageContext.request.contextPath}/backstage/loginplatform/list/");
	});
	return false; // 阻止表单自动提交事件
});
</script>