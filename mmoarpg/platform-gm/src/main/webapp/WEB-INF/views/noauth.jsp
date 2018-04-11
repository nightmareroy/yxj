<%@ page language="java" contentType="text/html;charset=UTF-8" pageEncoding="UTF-8"%>

<section class="content-header">
	<h1>
		权限不足 <small>哎呀...没法混了...</small>
	</h1>
	<ol class="breadcrumb">
		<li><a href="${pageContext.request.contextPath}/welcome/"><i class="fa fa-dashboard"></i> 首页</a></li>
		<li class="active">权限不足</li>
	</ol>
</section>

<div class="pad margin no-print">
	<div class="callout callout-danger" style="margin-bottom: 0 !important;">
		<h4>
			<i class="fa fa-info"></i> 权限不足
		</h4>
		<p>您当前权限，不足以操作此项功能...</p>
	</div>
</div>

<section class="content">
	<div class="row">
		<div class="col-xs-12">
			<div class="box box-danger">
				<div class="box-header">
					<h3 class="box-title">给您几点建议...</h3>
				</div>
				<div class="box-body">
					<p>1.联系管理员，申请开通此功能权限.</p>
					<p>2.请同事吃饭，让他帮你操作一下此功能.</p>
					<p>3.偷偷的告诉你，刷新浏览器，100次后可激活所有权限...</p>
					<p>4.刷新过程中不要能任何其他的事，否则重新计数，一般人我不告诉他...</p>
				</div>
			</div>
		</div>
	</div>
</section>
