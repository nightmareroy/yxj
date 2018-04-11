<%@ page language="java" contentType="text/html;charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<section class="content-header">
	<h1>首页</h1>
	<ol class="breadcrumb">
		<li><i class="fa fa-dashboard"></i> 首页</li>
	</ol>
</section>

<div class="pad margin no-print">
	<div class="callout callout-success" style="margin-bottom: 0 !important;">
		<h4>
			<i class="fa fa-info"></i> 登录信息：
		</h4>
		<p>
			建议不要在非办公环境下登录系统
			<!-- 异地登录情况与实际不符，建议您马上修改密码！ -->
		<p>
			本次登录IP：${sessionScope.SESSION_USER.loginIp}(${sessionScope.SESSION_USER.loginCity})<br> 本次登录时间：
			<fmt:formatDate value="${sessionScope.SESSION_USER.loginTime}" type="both" />
			<br>
		<p>
			上次登录IP：${sessionScope.SESSION_USER.lastLoginIp}(${sessionScope.SESSION_USER.lastLoginCity})<br> 上次登录时间：
			<fmt:formatDate value="${sessionScope.SESSION_USER.lastLoginTime}" type="both" />
			<br>
	</div>
</div>

<section class="invoice">
	<div class="row">
		<div class="col-xs-12">
			<h2 class="page-header">
				<i class="fa fa-file-word-o text-red"></i> 红头文件. <small class="pull-right">Date:${today}</small>
			</h2>
		</div>
	</div>
	<div class="row">
		<div class="col-xs-12">
			<b>${result.title}</b>
			<p>${result.content}
			<p>
		</div>
	</div>
	<p>
	<div class="row">
		<div class="col-xs-12">
			<div class="box">
				<div class="box-header">
					<h3 class="box-title">紧急联系人</h3>
				</div>
				<div class="box-body table-responsive no-padding">
					<table class="table table-hover">
						<thead>
							<tr>
								<th>姓名</th>
								<th>状态</th>
								<th>岗位</th>
								<th>手机号</th>
							</tr>
						</thead>
						<tbody>
							<c:forEach var="e" items="${message}">
								<tr role="row" class="odd">
									<td>${e.name}</td>
									<td><c:choose>
											<c:when test="${e.state==1}">
												<span class="label label-success">公司值班</span>
											</c:when>
											<c:when test="${e.state==2}">
												<span class="label label-warning">家里值班</span>
											</c:when>
											<c:otherwise>
												<span class="label label-danger">临时休息</span>
											</c:otherwise>
										</c:choose></td>
									<td>${e.poster}</td>
									<td>${e.phoneNumber}</td>
								</tr>
							</c:forEach>
						</tbody>
					</table>
				</div>
			</div>
		</div>
	</div>
	<c:if test="${sessionScope.SESSION_USER.auth.have['POSTER_EDIT']}">
	<button type="button" class="btn btn-block btn-primary btn-sm edit-admin-btn">编辑</button>
	</c:if>
</section>
<section class="invoice" style="display:none">
	<div class="row">
		<div class="col-xs-12">
			<h2 class="page-header">
				<i class="fa fa-file-word-o text-red"></i> 红头文件. <small class="pull-right">Date:${today}</small>
			</h2>
		</div>
	</div>
	<div class="row">
		<div class="col-xs-12">
			<b><input style="margin-bottom:5px;" class="form-control admin-head" value="${result.title}"></b>
			<p><textarea class="form-control admin-title">${result.content}</textarea>
			<p>
		</div>
	</div>
	<p>
	<div class="row">
		<div class="col-xs-12">
			<div class="box">
				<div class="box-header">
					<h3 class="box-title">紧急联系人</h3>
				</div>
				<div class="box-body table-responsive no-padding">
					<table class="table table-hover">
						<thead>
							<tr>
								<th>姓名</th>
								<th>状态</th>
								<th>岗位</th>
								<th>手机号</th>
								<th>操作</th>
							</tr>
						</thead>
						<tbody class="edit-body">
							<c:forEach var="e" items="${message}">
								<tr role="row" class="odd admin-info">
									<td><input style="margin-bottom:5px;" class="form-control admin-name" value="${e.name}"></td>
									<td>
										<select class="form-control admin-status">
										<c:choose>
											<c:when test="${e.state==1}">
												<option value="1" selected>公司值班</option>
					                    		<option value="2">家里值班</option>
					                    		<option value="3">临时休息</option>
											</c:when>
											<c:when test="${e.state==2}">
												<option value="1">公司值班</option>
					                    		<option value="2" selected>家里值班</option>
					                    		<option value="3">临时休息</option>
											</c:when>
											<c:otherwise>
												<option value="1">公司值班</option>
					                    		<option value="2">家里值班</option>
					                    		<option value="3" selected>临时休息</option>
											</c:otherwise>
										</c:choose>
										</select>
									</td>
									<td><input class="form-control admin-poster" value="${e.poster}"></td>
									<td><input class="form-control admin-phone" value="${e.phoneNumber}"></td>
									<td><span style="position:absolute;margin-top:10px;cursor:pointer;" class="label label-danger admin-delete">删除</span></td>
								</tr>
							</c:forEach>
							<tr role="row" class="odd add-admin-tr">
								<td colspan='5'><span style="cursor:pointer;" class="label label-success add-admin">添加新成员</span></td>
							</tr>
						</tbody>
					</table>
				</div>
			</div>
		</div>
	</div>
	<button type="button" class="btn btn-block btn-success btn-sm submit-admin-btn">提交</button>
</section>
<div class="clearfix"></div>
<script src="${pageContext.request.contextPath}/resources/js/jquery-3.2.0.min.js"></script>
<script>
	$('.add-admin').click(function(){
		var html = '<tr role="row" class="odd admin-info">'
		+'<td><input style="margin-bottom:5px;" class="form-control admin-name" value="新成员"></td>'
		+'<td><select class="form-control admin-status"><option value="1">值班</option><option value="2" selected>休假</option></select></td>'
		+'<td><input class="form-control admin-poster" value="运营大大"></td>'
		+'<td><input class="form-control admin-phone" value="13912345678"></td>'
		+'<td><span style="position:absolute;margin-top:10px;cursor:pointer;" class="label label-danger admin-delete">删除</span></td>'
		+'</tr>';
		$('.add-admin').parents('.add-admin-tr').before(html);
	});
	$('.edit-body').on('click', '.admin-delete', function(){
		$(this).parents('.admin-info').remove();
	});
	$('.edit-admin-btn').click(function(){
		$('.invoice:eq(0)').hide();
		$('.invoice:eq(1)').show();
	});
	$('.submit-admin-btn').click(function(){
		var head = $('.admin-head').val();
		var title = $('.admin-title').val(); 
		var name = [];
		var status = [];
		var poster = [];
		var phone = [];
		if($('.admin-name').length == 0){
			return false;
		}
		$('.admin-name').each(function(){
			name.push($(this).val());
		})
		$('.admin-status').each(function(){
			status.push($(this).val());
		})
		$('.admin-poster').each(function(){
			poster.push($(this).val());
		})
		$('.admin-phone').each(function(){
			phone.push($(this).val());
		})
		$.ajax({  
            type:'post',  
            traditional :true,  
            url:'${pageContext.request.contextPath}/maintain/poster/update',  
            data:{
            	'head':encodeURI(encodeURI(head)),
            	'title':encodeURI(encodeURI(title)),
            	'name':name,
            	'status':status,
            	'poster':poster,
            	'phone':phone
            },  
            success:function(data){  
                if(data == 0){
                	window.location.href='${pageContext.request.contextPath}/welcome';	
                }
            }  
        }); 
	});
</script>