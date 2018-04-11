<%@ page language="java" contentType="text/html;charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<section class="content-header">
	<h1>修改密码</h1>
	<ol class="breadcrumb">
		<li><a href="${pageContext.request.contextPath}/welcome/"><i class="fa fa-dashboard"></i> 首页</a></li>
		<li class="active">修改密码</li>
	</ol>
</section>
<section class="content">
	<div class="row">
		<div class="col-xs-12">
			<div class="box box-primary">
				<div class="box-header">
					<h3 class="box-title">修改密码信息</h3>
				</div>
				<form role="form" action="${pageContext.request.contextPath}/backstage/user/password/modify/" method="post" id="modifyPassword">
					<div class="box-body">
						<div class="form-group">
							<label for="OldPassword">旧密码 : <span class="text-red">*</span></label> <input id="OldPassword" name="OldPassword" class="form-control" placeholder="请输入旧密码" type="password" maxlength="16" data-easyform="length:1 32;char-normal;" data-message="原密码不可以为空~" data-easytip="position:top;class:easy-red;">
						</div>
						<div class="form-group">
							<label for="NewPassword">新密码 : <span class="text-red">*</span></label><input id="NewPassword" class="form-control" placeholder="请输入新密码" type="password" maxlength="16" data-easyform="length:6 32;char-normal;" data-message="新密码必须为6—16位" data-easytip="position:top;class:easy-red;">
						</div>
						<div class="form-group">
							<label for="NewPasswordAgain">确认新密码 : <span class="text-red">*</span></label> <input id="NewPasswordAgain" class="form-control" placeholder="请再次输入新密码" type="password" data-easyform="length:6 16;equal:#NewPassword;" data-message="两次密码输入要一致" data-easytip="position:top;class:easy-red;">
						</div>
						
						<button type="submit" id="btn_modify_password" class="btn btn-primary btn-lg btn-block">确认修改</button>
					</div>
				</form>
			</div>
		</div>
	</div>
</section>
<script type="text/javascript">
	$('#modifyPassword').easyform();

	$('#modifyPassword').on('submit', function() {
		var OldPassword = $("#OldPassword");
		if (OldPassword.val().length == 0) {
			addErrorMsg(OldPassword, "请输入原来的密码");
			return false;
		}

		var NewPassword = $("#NewPassword");
		if (NewPassword.val().length == 0) {
			addErrorMsg(NewPassword, "请输入新密码");
			return false;
		}else if(NewPassword.val().length < 6){ 
			addErrorMsg(NewPassword,"密码长度在6位到16位之间！");
			return false;
		}

		var NewPasswordAgain = $("#NewPasswordAgain");
		if (NewPasswordAgain.val().length == 0) {
			addErrorMsg(NewPasswordAgain, "再次输入新密码");
			return false;
		}
		
		if (NewPassword.val() != NewPasswordAgain.val()) {
			addErrorMsg(NewPasswordAgain,"两次密码不一致...");
			return false;
		}
		var $btn = $("#btn_modify_password").button('loading');
		$.post($(this).attr("action"), {
			"OldPassword" : OldPassword.val(),
			"NewPassword" : NewPassword.val(),
			"token" : "${sessionScope.token}"
		}, function(result) {
			$btn.button('reset');
			//修改成功
			if(result =='0'){
				resultCallback("修改成功，重新登录", "javascript:window.location.href = '${pageContext.request.contextPath}/lockscreen?username=${sessionScope.SESSION_USER.username}';");
			} else if(result =='1002'){
				addErrorMsg(OldPassword, "请输入原来的密码");
			}
		});
		return false; // 阻止表单自动提交事件
	});
</script>