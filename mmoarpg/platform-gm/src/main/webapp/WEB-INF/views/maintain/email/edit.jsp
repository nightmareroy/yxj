<%@ page language="java" contentType="text/html;charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<section class="content-header">
	<h1>${email==null?"添加监控邮箱":"编辑监控邮箱"}</h1>
	<ol class="breadcrumb">
		<li><a href="${pageContext.request.contextPath}/welcome/"><i class="fa fa-dashboard"></i> 首页</a></li>
		<li>运维工具</li>
		<li><a href="javascript:ajaxLoadPage2Body('${pageContext.request.contextPath}/maintain/email/list/');">监控邮箱管理</a></li>
		<li class="active">${platforms==null?"添加监控邮箱":"编辑监控邮箱"}</li>
	</ol>
</section>
<section class="content">
	<div class="row">
		<div class="col-xs-12">
			<div class="box box-primary">
				<div class="box-header">
					<h3 class="box-title">邮箱配置</h3>
				</div>
				<form role="form" action="${pageContext.request.contextPath}/maintain/email/${email==null?'add':'edit'}/" method="post" id="editEmail">
					<div class="box-body">
						<div class="form-group">
							<label for="addr">邮箱地址: <span class="text-red">*</span></label>
							<input id="addr" class="form-control" placeholder="请输入邮箱地址" type="text" value="${email.addr}" maxlength="128" ${email==null?'':'disabled'}>
						</div>
						<div class="form-group">
							<label for="name">真实姓名: <span class="text-red">*</span></label>
							<input id="name" class="form-control" placeholder="工作人员的真实姓名..." type="text" value="${email.name}" maxlength="64">
						</div>
						<div class="form-group">
							<label for="remarks">备注: <span class="text-red">*</span></label>
							<input id="remarks" class="form-control" placeholder="对人员的工作描述" type="text" value="${email.remarks}" maxlength="64">
						</div>
						<input id="Platform_id" type="hidden" value="${platforms.id }">
						<button type="submit" id="btn_modify_email" class="btn btn-primary btn-lg btn-block">${email==null?"确认添加":"确认修改"}</button>
					</div>
				</form>
			</div>
		</div>
	</div>
</section>
<script type="text/javascript">
registerFocusClearErrorMsg();
$('#editEmail').on('submit', function() {
	var addr = $("#addr");
	if (addr.val().length == 0) {
		addErrorMsg(addr, "请输入邮箱地址...");
		return false;
	}
	var name = $("#name");
	if (name.val().length == 0) {
		addErrorMsg(name, "请输入真实姓名...");
		return false;
	}
	var remarks = $("#remarks");
	if (remarks.val().length == 0) {
		addErrorMsg(remarks, "对人员的工作描述");
		return false;
	}
	var $btn = $("#btn_modify_email").button('loading');
	$.post($(this).attr("action"), {
		"addr" : addr.val(),
		"name" : name.val(),
		"remarks" : remarks.val(),
		"token" : "${sessionScope.token}"
	}, function(result) {
		$btn.button('reset');
		ajaxLoadPage2Body("${pageContext.request.contextPath}/maintain/email/list/");
	});
	return false; // 阻止表单自动提交事件
});
</script>