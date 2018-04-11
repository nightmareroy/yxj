<%@page language="java" contentType="text/html;charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

<section class="content-header">
	<h1>500 Error Page</h1>
	<ol class="breadcrumb">
		<li><a href="${pageContext.request.contextPath}/welcome/"><i class="fa fa-dashboard"></i> Home</a></li>
		<li class="active">500</li>
	</ol>
</section>

<section class="content">
	<div class="error-page">
		<h2 class="headline text-red">500</h2>
		<div class="error-content">
			<h3>
				<i class="fa fa-warning text-red"></i> 尴尬，服务器出现了错误.
			</h3>
			<p>对于由此造成的不便，我们深表歉意。
			<p>我们对本次操作造作的错误表示“严重关切”
			<p>我们希望有关各方进一步深化交流合作，加快推动项目研发进度
			<p>我们将一如既往、坚持不懈地，推动实现5个9可靠性这一战略目标
		</div>
	</div>
	<div class="row">
		<div class="col-md-12">
			<div class="box box-solid">
				<div class="box-header with-border">
					<h3 class="box-title">异常信息</h3>
				</div>
				<div class="box-body">${errorMessage }</div>
			</div>
		</div>
	</div>
</section>