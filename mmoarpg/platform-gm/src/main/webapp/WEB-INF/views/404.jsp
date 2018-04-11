<%@ page language="java" contentType="text/html;charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

<section class="content-header">
	<h1>404错误页面</h1>
	<ol class="breadcrumb">
		<li><a href="${pageContext.request.contextPath}/welcome/"><i class="fa fa-dashboard"></i> Home</a></li>
		<li class="active">404</li>
	</ol>
</section>

<section class="content">
	<div class="error-page">
		<h2 class="headline text-yellow">404</h2>

		<div class="error-content">
			<h3>
				<i class="fa fa-warning text-yellow"></i> 哎呀! 页面未找到...
			</h3>

			<p>对不起，我找不到你要找的那页面。
			
			<p>真的没找到，可那又能怎么样呢？要不你来打我一顿。
			
			<p>好了，别闹了，如果确认有问题的话请反馈给开发大大查一下。
			
		</div>
	</div>
</section>