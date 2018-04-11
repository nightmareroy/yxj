<%@ page language="java" contentType="text/html;charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<section class="content-header">
	<h1>高级搜索</h1>
	<ol class="breadcrumb">
		<li><a href="${pageContext.request.contextPath}/welcome/"><i class="fa fa-dashboard"></i> 首页</a></li>
		<li>后台管理</li>
		<li><a href="javascript:ajaxLoadPage2Body('${pageContext.request.contextPath}/backstage/log/manage/');">日志列表</a></li>
		<li class="active">高级搜索</li>
	</ol>
</section>

<section class="content">
	<div class="row">
		<div class="col-xs-12">
			<div class="box box-primary">
				<div class="box-header">
					<h3 class="box-title">搜索条件</h3>
				</div>
				<div class="box-body">
					<div class="form-group">
						<label>操作时间: </label>
						<div class="input-group">
							<div class="input-group-addon">
								<i class="fa fa-clock-o"></i>
							</div>
							<input type="text" class="form-control pull-right" id="reservationtime">
						</div>
					</div>

					<div class="form-group">
						<label>操作账号: </label> <select class="form-control select2" style="width: 100%;" id="user">
							<option value="">所有人</option>
							<c:forEach var="user" items="${userlist}">
								<option value="${user.username}">${user.name}(${user.username})</option>
							</c:forEach>
						</select>
					</div>

					<div class="form-group">
						<label>操作分类: </label> <select class="form-control select2" style="width: 100%;" id="classify">
							<option value="">全部</option>
							<c:forEach var="c" items="${classify}">
								<option value="${c.name() }">${c.key}</option>
							</c:forEach>
						</select>
					</div>

					<div class="form-group">
						<label>操作者IP: </label>
						<div class="input-group">
							<div class="input-group-addon">
								<i class="fa fa-laptop"></i>
							</div>
							<input type="text" id="ip" class="form-control" data-inputmask="'alias': 'ip'" data-mask="">
						</div>
					</div>

					<button type="submit" id="login-btn" class="btn btn-primary btn-lg btn-block" onclick="javascript:ajaxLoadPage2Body('${pageContext.request.contextPath}/backstage/log/list/?reservationtime='+$('#reservationtime').val()+'&ip='+$('#ip').val()+'&classify='+$('#classify').val()+'&user='+$('#user').val());">查询</button>
				</div>
			</div>
		</div>
	</div>
</section>
<script>
$(function() {
	$(".select2").select2();
	$("[data-mask]").inputmask();
	$('#reservationtime').daterangepicker({
		timePicker24Hour: true,
		timePicker: true,
        locale: {
            format: 'YYYY-MM-DD HH:mm:ss'
        },
        ranges: {
            '今天': [moment(), moment()],
            '昨天': [moment().subtract(1, 'days'), moment().subtract(1, 'days')],
            '最近7天': [moment().subtract(6, 'days'), moment()],
            '最近30开': [moment().subtract(29, 'days'), moment()],
            '这个月': [moment().startOf('month'), moment().endOf('month')],
            '上个月': [moment().subtract(1, 'month').startOf('month'), moment().subtract(1, 'month').endOf('month')]
         }
	});
});
</script>