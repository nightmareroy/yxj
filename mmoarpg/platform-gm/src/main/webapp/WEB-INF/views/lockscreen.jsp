<%@page language="java" contentType="text/html;charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

<!DOCTYPE html>
<html>
	<head>
		<meta charset="utf-8">
		<title>Game Manager - Powered By 清源科技</title>
		<link rel="shortcut icon" type="image/x-icon" href="${pageContext.request.contextPath}/resources/images/favicon.ico">
		<meta content="width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no" name="viewport">
		<!--[if lte IE 10]>
		<script type="text/javascript">
			location.href = '${pageContext.request.contextPath}/unsupport-browser/';
		</script>
		<![endif]-->
		<link rel="stylesheet" href="${pageContext.request.contextPath}/resources/bootstrap/css/bootstrap.min.css">
		<link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/font-awesome.min.css">
		<link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/AdminLTE.min.css">
		<link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/skins/_all-skins.min.css">
	</head>

<body class="hold-transition lockscreen">
	<div class="lockscreen-wrapper">
		<div class="lockscreen-logo">
			<%@ include file="logo.jsp"%>
		</div>
		<div class="lockscreen-name">${name}(${username})</div>

		<div class="lockscreen-item">
			<div class="lockscreen-image">
				<img src="${pageContext.request.contextPath}/resources/images/face.gif" alt="User Image">
			</div>
			<form class="lockscreen-credentials" action="${pageContext.request.contextPath}/login/" method="post">
				<div class="input-group">
					<input id="password" type="password" class="form-control" placeholder="请输入密码...">
					<div class="input-group-btn">
						<button type="submit" class="btn" id="btn_login">
							<i class="fa fa-arrow-right text-muted"></i>
						</button>
					</div>
				</div>
			</form>
		</div>
		<div class="help-block text-center">Enter your password to retrieve your session</div>
		<div class="text-center">
			<a href="${pageContext.request.contextPath}/">使用帐号密码登录</a>
		</div>
		<div class="lockscreen-footer text-center">
			Copyright &copy; 2017 <b>清源科技</b><br> All rights reserved
		</div>
	</div>

	<script src="${pageContext.request.contextPath}/resources/js/jquery-3.2.0.min.js"></script>
	<script src="${pageContext.request.contextPath}/resources/bootstrap/js/bootstrap.min.js"></script>
	<script src="${pageContext.request.contextPath}/resources/js/shake.js"></script>
	<script src="${pageContext.request.contextPath}/resources/js/md5.js"></script>
	<script>
		$(function() {
			// 获得焦点时，清理错误提示
			$('input').focus(function() { $(this).parent().removeClass('has-error'); });

			var showErrorMsg = function(input, msg){
				input.val("").attr("placeholder", msg).shake(2, 12, 500);
			}
			
			$('form').on('submit', function() {
				var password = $("#password");
				if (password.val() == '') {
					showErrorMsg(password, "请输入密码...");
					return false; // 阻止表单自动提交事件
				}
				
				var $btn = $("#btn_login").button('loading');
				
				$.post($(this).attr("action"), {
					"username" : "${username}",
					"password" : hex_md5(password.val())
				}, function(result) {
					$btn.button('reset');
					// 登录成功
					if (result == '0') {
						window.location.href = "${pageContext.request.contextPath}/welcome/";
					}
					// 账号不存在
					else if (result == '1001') {
						window.location.href = "${pageContext.request.contextPath}";
					}
					// 账号已锁定 
					else if (result == '1003') {
						showErrorMsg(password, "您的账号已被锁定...");
					}
					// 连错10次，那10分钟后再尝试...
					else if (result == '1004') {
						showErrorMsg(password, "您已连错10次，5分钟后再尝试...");
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