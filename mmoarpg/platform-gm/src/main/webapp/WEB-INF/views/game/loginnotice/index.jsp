<%@ page language="java" contentType="text/html;charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<section class="content-header">
	<h1>${notice==null? '添加公告':'编辑公告'}</h1>
	<ol class="breadcrumb">
		<li><a href="${pageContext.request.contextPath}/welcome/"><i class="fa fa-dashboard"></i> 首页</a></li>
		<li><a href="${pageContext.request.contextPath}/game/login/notice/list/">登录公告</a></li>
		<li class="active">${notice==null? '添加公告':'编辑公告'}</li>
	</ol>
</section>

<section class="content">
	<div class="row">
		<div class="col-xs-12">
			<div class="box box-primary">
				<div class="box-header">
					<h3 class="box-title">登录公告信息</h3>
				</div>
				
				<form role="form" action="${pageContext.request.contextPath}/game/login/notice/${notice==null? 'add':'edit'}/" method="post" id="editNotice">
					<div class="box-body">
						<div class="form-group">
							<label for="title">标题: <span class="text-red">*</span></label>
							<input id="title" class="form-control" placeholder="标题" type="text" maxlength="100" value="${notice.title}">
						</div>
						
						<div class="form-group">
							<label>内容：<span class="text-red">*</span></label>
							<textarea id="content" class="form-control ckeditor" rows="20">${notice.content}</textarea>
						</div>
						
						<input id="notice_id" type="hidden" value="${notice.id}">
						<button type="submit" id="btn_modify_notice" class="btn btn-primary btn-lg btn-block">提交公告</button>
					</div>
				</form>
			</div>
		</div>
	</div>
</section>
<script>
$(function() {
	registerFocusClearErrorMsg();
	
	var ckeditor = CKEDITOR.replace('content');
	$('#editNotice').on('submit', function() {
		var title = $("#title");
		if (title.val().length == '') {
			addErrorMsg(title, "请输入登录公告标题");
			return false;
		}
		var $btn = $("#btn_modify_notice").button('loading');
		$.post($(this).attr("action"), {
			"id" : $("#notice_id").val(),
			"title" : title.val(),
			"content" : ckeditor.getData()
		}, function(result) {
			$btn.button('reset');
			if(result==0){
				resultCallback("公告发送成功", "javascript:ajaxLoadPage2Body('${pageContext.request.contextPath}/game/login/notice/list/');");
			}else{
				resultCallback("公告发送失败，请稍后再次尝试...", "javascript:void(0);");
			}
		});
		return false; // 阻止表单自动提交事件
	});	
});
</script>