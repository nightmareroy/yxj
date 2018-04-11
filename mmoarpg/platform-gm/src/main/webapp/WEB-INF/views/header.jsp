<%@page language="java" contentType="text/html;charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<header class="main-header">

	<a href="${pageContext.request.contextPath}/welcome/" class="logo">
		<span class="logo-mini"><b>G</b>M</span> <span class="logo-lg"><%@ include file="logo.jsp"%></span>
	</a>

	<!-- 顶部导航 -->
	<nav class="navbar navbar-static-top">

		<!-- 控制左边导航收缩 -->
		<a href="#" class="sidebar-toggle" data-toggle="offcanvas" role="button"> <span class="sr-only">三</span></a>

		<div class="navbar-custom-menu">
			<ul class="nav navbar-nav">
				<!-- 右上角 个人信息区 -->
				<li class="dropdown user user-menu">
					<a href="#" class="dropdown-toggle" data-toggle="dropdown">
						<img src="${pageContext.request.contextPath}/resources/images/head/${sessionScope.SESSION_USER.head}.png" class="user-image" alt="User Image"> <span class="hidden-xs">${sessionScope.SESSION_USER.name}</span>
					</a>
					<ul class="dropdown-menu">
						<li class="user-header">
							<img src="${pageContext.request.contextPath}/resources/images/head/${sessionScope.SESSION_USER.head}.png" class="img-circle" alt="User Image">
							<p>${sessionScope.SESSION_USER.name} - ${sessionScope.SESSION_USER.auth.name} <small>${sessionScope.SESSION_USER.remarks}</small></p>
						</li>
						<li class="user-body">
							<div class="row">
								<div class="col-xs-6 text-center">
									<i class="fa fa-clock-o"></i> <fmt:formatDate value="${sessionScope.SESSION_USER.loginTime}" type="date"/>
								</div>
								<div class="col-xs-6 text-center">IP:${sessionScope.SESSION_USER.loginIp}</div>
							</div>
						</li>
						<li class="user-footer">
							<div class="pull-left">
								<a href="${pageContext.request.contextPath}/lockscreen?username=${sessionScope.SESSION_USER.username}" class="btn btn-default btn-flat">一键锁定</a>
							</div>
							<div class="pull-right">
								<a href="${pageContext.request.contextPath}/logout" class="btn btn-default btn-flat">安全退出</a>
							</div>
						</li>
					</ul>
				</li>
			</ul>
		</div>
	</nav>
</header>