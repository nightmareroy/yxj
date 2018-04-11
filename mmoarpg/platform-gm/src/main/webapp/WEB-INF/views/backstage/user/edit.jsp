<%@ page language="java" contentType="text/html;charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<section class="content-header">
	<h1>${user==null?"添加账号":"编辑账号" }</h1>
	<ol class="breadcrumb">
		<li><a href="${pageContext.request.contextPath}/welcome/"><i class="fa fa-dashboard"></i> 首页</a></li>
		<li>后台管理</li>
		<li><a href="javascript:ajaxLoadPage2Body('${pageContext.request.contextPath}/backstage/user/manage/');">账号管理</a></li>
		<li class="active">${user==null?"添加账号":"编辑账号" }</li>
	</ol>
</section>
<section class="content">
	<div class="row">
		<div class="col-xs-12">
			<div class="box box-primary">
				<div class="box-header">
					<h3 class="box-title">账号信息</h3>
				</div>
				<form role="form" action="${pageContext.request.contextPath}/backstage/user/${user==null?'add':'edit'}/" method="post" id="addUser">
					<div class="box-body">
						<div class="form-group">
							<label for="username">账号: <span class="text-red">*</span></label> <input id="username" class="form-control" placeholder="账号" type="text" value="${user.username}" maxlength="64" ${user==null?'':'disabled'}>
						</div>
						<div class="form-group">
							<label for="name">名称: <span class="text-red">*</span></label> <input id="name" class="form-control" placeholder="名称" type="text" value="${user.name}" maxlength="64">
						</div>
						<div class="form-group">
							<label for="password">密码: <span class="text-red">*</span></label> <input id="password" class="form-control" placeholder="密码" type="text" value="${user.password}" maxlength="32">
						</div>
						<div class="form-group">
							<label for="authgroup">权限组: <span class="text-red">*</span></label> <select class="form-control select2" style="width: 100%;" id="auth">
								<c:forEach var="e" items="${auths}">
									<c:if test="${e.id == user.authGroupId}">
										<option value="${user.authGroupId}">${e.name }</option>
									</c:if>
								</c:forEach>
								<c:forEach var="a" items="${auths}">
									<option value="${a.id }">${a.name }</option>
								</c:forEach>
							</select>
						</div>
						<div class="form-group">
							<label for="loginPlatformId">登录平台: <span class="text-red">*</span></label> <select class="form-control select2" style="width: 100%;" id="loginPlatformId">
								<c:forEach var="a" items="${loginplatforms}">
									<option value="${a.id }">${a.name }</option>
								</c:forEach>
							</select>
						</div>
						<input id="user_id" type="hidden" value="${user.id }">
						<button type="submit" id="btn_modify_password" class="btn btn-primary btn-lg btn-block">${user==null?"提交申请":"确认修改"}</button>
					</div>
				</form>
			</div>
		</div>
	</div>
</section>
<script type="text/javascript">
	$(".select2").select2();
	registerFocusClearErrorMsg();
	$('#addUser').on('submit', function() {
		var username = $("#username");
		if (username.val().length == 0) {
			addErrorMsg(username, "请输入权限组账号");
			return false;
		}
		var name = $("#name");
		if (name.val().length == 0) {
			addErrorMsg(name, "请输入权限组名称");
			return false;
		}
		var password = $("#password");
		if (password.val().length == 0) {
			addErrorMsg(password, "请输入权限组密码");
			return false;
		} else if (password.val().length < 6) {
			addErrorMsg(password, "密码长度在6位到16位之间!");
			return false;
		}
		var $btn = $("#btn_modify_password").button('loading');
		$.post($(this).attr("action"), {
			"id" : $("#user_id").val(),
			"name" : name.val(),
			"username" : username.val(),
			"password" : password.val(),
			"auth" : $("#auth").val(),
			"loginPlatformId" : $("#loginPlatformId").val(),
			"token" : "${sessionScope.token}"
		}, function(result) {
			$btn.button('reset');
			if(result=='11'){
				ajaxLoadPage2Body("${pageContext.request.contextPath}/backstage/user/list/");
			}else if(result =='12'){
				addErrorMsg(username, "次账号已经被注册");
			}else if(result =='OK'){
				ajaxLoadPage2Body("${pageContext.request.contextPath}/backstage/user/list/");
			}
		});
		return false; // 阻止表单自动提交事件
	});
</script>