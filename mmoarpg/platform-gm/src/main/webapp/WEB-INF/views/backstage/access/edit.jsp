<%@ page language="java" contentType="text/html;charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<section class="content-header">
	<h1>${access==null?"添加访问限制":"编辑访问限制" }</h1>
	<ol class="breadcrumb">
		<li><a href="${pageContext.request.contextPath}/welcome/"><i class="fa fa-dashboard"></i> 首页</a></li>
		<li>后台管理</li>
		<li><a href="javascript:ajaxLoadPage2Body('${pageContext.request.contextPath}/backstage/access/manage/');">访问限制</a></li>
		<li class="active">${access==null?"添加访问限制":"编辑访问限制" }</li>
	</ol>
</section>
<section class="content">
	<div class="row">
		<div class="col-xs-12">
			<div class="box box-primary">
				<div class="box-header">
					<h3 class="box-title">访问限制信息</h3>
				</div>
				<form role="form" action="${pageContext.request.contextPath}/backstage/access/${access==null?'add':'edit'}/" method="post" id="addAccess">
					<div class="box-body">
						<div class="form-group">
							<label for="ipUserName">登录平台: <span class="text-red">*</span></label> <select class="form-control select2" style="width: 100%;" id="ipUserName">
								<c:forEach var="a" items="${loginplatforms}">
									<option value="${a.id }">${a.name }(${a.id })</option>
								</c:forEach>
							</select>
						</div>
						<div class="form-group">
							<label for="IP">IP规则: <span class="text-red">*</span></label>
							<input id="IP" class="form-control" placeholder="IP" type="text" value="${access.ip}" maxlength="256">
							<span class="help-block">例：192.168.2.57;192.*.3.*;192.168.2.88-192.168.2.100</span>
						</div>
						<div class="form-group">
							<label for="name">备注: <span class="text-red">*</span></label>
							<input id="name" class="form-control" placeholder="简单说明用途..." type="text" value="${access.name}" maxlength="64">
						</div>
						<input id="access_id" type="hidden" value="${access.id }">
						<button type="submit" id="btn_modify_access" class="btn btn-primary btn-lg btn-block">${user==null?"提交申请":"确认修改"}</button>
					</div>
				</form>
			</div>
		</div>
	</div>
</section>
<script type="text/javascript">
	registerFocusClearErrorMsg();
	$('#addAccess').on('submit', function() {
		var IP = $("#IP");
		if (IP.val().length == 0) {
			addErrorMsg(IP, "请输入IP规则，例：192.168.2.57;192.*.3.*;192.168.2.88-192.168.2.100");
			return false;
		}
		var name = $("#name");
		if (name.val().length == 0) {
			addErrorMsg(name, "请输入名称");
			return false;
		}
		var $btn = $("#btn_modify_access").button('loading');
		$.post($(this).attr("action"), {
			"id" : $("#access_id").val(),
			"name" : name.val(),
			"IP" : IP.val(),
			"ipUserName" : $("#ipUserName").val(),
			"token" : "${sessionScope.token}"
		}, function(result) {
			$btn.button('reset');
			ajaxLoadPage2Body("${pageContext.request.contextPath}/backstage/access/list/");
		});
		return false;
	});
</script>