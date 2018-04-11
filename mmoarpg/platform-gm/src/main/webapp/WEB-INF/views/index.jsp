<%@page language="java" contentType="text/html;charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

<!DOCTYPE html>
<html>
<head>
	<meta charset="utf-8">
	<meta name="renderer" content="webkit">
	<title>Game Manager - Powered By 清源科技</title>
	<link rel="shortcut icon" type="image/x-icon" href="${pageContext.request.contextPath}/resources/images/favicon.ico">
	<meta content="width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no" name="viewport">

	<!--[if lte IE 10]>
		<script type="text/javascript">
			location.href = '${pageContext.request.contextPath}/unsupport-browser/';
		</script>
	<![endif]-->
	<link rel="stylesheet" href="${pageContext.request.contextPath}/resources/bootstrap/css/bootstrap.min.css">
	<link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/AdminLTE.min.css">
	<link rel="stylesheet" href="${pageContext.request.contextPath}/resources/plugins/iCheck/square/blue.css">
</head>
<body class="hold-transition login-page">
	<div class="login-box">
		<div class="login-logo">
			<%@ include file="logo.jsp"%>
		</div>
		<div class="login-box-body">
			<p class="login-box-msg">登录</p>
			<form action="${pageContext.request.contextPath}/login/" method="post">
				<div class="form-group has-feedback">
					<input type="text" id="username" class="form-control" placeholder="请输入账号"> <span class="glyphicon glyphicon-user form-control-feedback"></span>
				</div>
				<div class="form-group has-feedback">
					<input type="password" id="password" class="form-control" placeholder="请输入密码"> <span class="glyphicon glyphicon-lock form-control-feedback"></span>
				</div>
				<div class="row">
					<div class="col-xs-8">
						<div class="checkbox icheck">
							<label> <input id="sevendays" type="checkbox"> 七日免登录 </label>
						</div>
					</div>
					<div class="col-xs-4">
						<button type="submit" class="btn btn-primary btn-block btn-flat" id="btn_login">登录</button>
					</div>
				</div>
			</form>
		</div>
	</div>

	<script src="${pageContext.request.contextPath}/resources/js/jquery-3.2.0.min.js"></script>
	<script src="${pageContext.request.contextPath}/resources/bootstrap/js/bootstrap.min.js"></script>
	<script src="${pageContext.request.contextPath}/resources/plugins/iCheck/icheck.min.js"></script>
	<script src="${pageContext.request.contextPath}/resources/js/shake.js"></script>
	<script src="${pageContext.request.contextPath}/resources/js/md5.js"></script>
	<script>
		$(function() {
			$('input').iCheck({// 7日复选样式
				checkboxClass : 'icheckbox_square-blue',
				radioClass : 'iradio_square-blue',
				increaseArea : '20%' // optional
			});
			
			// 获得焦点时，清理错误提示
			$('input').focus(function() { $(this).parent().removeClass('has-error'); });

			var showErrorMsg = function(input, msg){
				input.val("").attr("placeholder", msg).shake(2, 12, 500).parent().addClass('has-error');
			}
			
			$('form').on('submit', function() {
				
				var username = $("#username");
				if (username.val() == '') {
					showErrorMsg(username, "请输入账号...");
					return false; // 阻止表单自动提交事件
				}
				
				var password = $("#password");
				if (password.val() == '') {
					showErrorMsg(password, "请输入密码...");
					return false; // 阻止表单自动提交事件
				}
				
				var $btn = $("#btn_login").button('loading');
				
				$.post($(this).attr("action"), {
					"username" : username.val(),
					"password" : hex_md5(password.val()),
					"sevendays" : $("#sevendays:checked").val()
				}, function(result) {
					$btn.button('reset');
					// 登录成功
					if (result == '0') {
						window.location.href = "${pageContext.request.contextPath}/welcome/";
					}
					// 账号不存在
					else if (result == '1001') {
						showErrorMsg(username, "您输入的账号不存在...");
					}
					// 账号已锁定 
					else if (result == '1003') {
						showErrorMsg(username, "您的账号已被锁定...");
					}
					// 连错10次，那10分钟后再尝试...
					else if (result == '1004') {
						showErrorMsg(username, "您已连错10次，5分钟后再尝试...");
					}
					// 您所在地区访问受限，请呼叫管理员...
					else if (result == '1005') {
						showErrorMsg(username, "您所在地区访问受限，请呼叫管理员...");
					}
					// 密码错误
					else {
						showErrorMsg(password, "您输入的密码错误...");
					}
				});
				return false; // 阻止表单自动提交事件
		  	});
		});
	</script>
</body>
</html>