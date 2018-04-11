<%@ page language="java" contentType="text/html;charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<section class="content-header">
	<h1>高级搜索</h1>
	<ol class="breadcrumb">
		<li><a href="${pageContext.request.contextPath}/welcome/"><i class="fa fa-dashboard"></i> 首页</a></li>
		<li>游戏管理</li>
		<li><a href="javascript:ajaxLoadPage2Body('${pageContext.request.contextPath}/game/publish/list/');">惩处记录</a></li>
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
							<option value="" selected>所有人</option>
							<c:forEach var="user" items="${userlist}">
								<option value="${user.username}">${user.name}(${user.username})</option>
							</c:forEach>
						</select>
					</div>
					<div class="form-group">
						<label>惩处类型: </label> <select class="form-control select2" style="width: 100%;" id="type">
							<option value="-1" selected>所有类型</option>
							<option value="0">封号</option>
							<option value="1">解除封号</option>
							<option value="2">禁言</option>
							<option value="3">解除禁言</option>
							<option value="4">T下线</option>
						</select>
					</div>
					<div class="form-group">
						<label for="playerName">角色名称: <span class="text-red">*</span></label>
						<input id="playerName" class="form-control" placeholder="默认为所有用户" type="text">
					</div>
					<button type="submit" class="btn btn-primary btn-lg btn-block" onclick="onSubmit()">查询</button>
				</div>
			</div>
		</div>
	</div>
</section>
<script>
	var startTime = "";
	var endTime = "";
	var datePicker = $('#reservationtime').daterangepicker({
		timePicker24Hour: true,
		timePicker: true,
		startDate:'${startTime}',
		maxDate:'${maxTime}',
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
	},function(start, end, label) {
		startTime = start.format('YYYY-MM-DD HH:mm:ss');
		endTime = end.format('YYYY-MM-DD HH:mm:ss');
	});
	function onSubmit(){
		if(startTime == "" || endTime == ""){
			var time = $('#reservationtime').val().split(' - ');
			startTime = time[0];
			endTime = time[1];
		}
		var url = "${pageContext.request.contextPath}/game/publish/list/?start="
				+startTime+"&end="+endTime+"&username="+$('#user').val()
				+"&type="+$('#type').val()
				+"&playerName="+$('#playerName').val();
		ajaxLoadPage2Body(url);
	}
</script>