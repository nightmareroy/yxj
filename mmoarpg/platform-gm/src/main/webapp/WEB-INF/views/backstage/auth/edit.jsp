<%@ page language="java" contentType="text/html;charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<section class="content-header">
	<h1>${auth==null?"添加权限":"修改权限"}</h1>
	<ol class="breadcrumb">
		<li><a href="${pageContext.request.contextPath}/welcome/"><i class="fa fa-dashboard"></i> 首页</a></li>
		<li>后台管理</li>
		<li><a href="javascript:ajaxLoadPage2Body('${pageContext.request.contextPath}/backstage/auth/manage/');">权限管理</a></li>
		<li class="active">${auth==null?"添加权限":"修改权限"}</li>
	</ol>
</section>
<section class="content">
	<div class="row">
		<div class="col-xs-12">
			<div class="box box-primary">
				<div class="box-header">
					<h3 class="box-title">权限组信息</h3>
				</div>
				<form role="form" action="${pageContext.request.contextPath}/backstage/auth/${auth==null?'add':'edit'}/" method="post" id="addAuthor">
					<div class="box-body">
						<div class="form-group">
							<label for="authname">请输入权限组名称: <span class="text-red">*</span></label><input id="authname" class="form-control" placeholder="请输入权限组名称" value="${auth.name}" type="text" maxlength="128" ${auth==null?'':'disabled'}>
						</div>
						<div class="form-group">
							<label for="authname">所有权限 </label>
							<div class="row">
								<c:forEach var="e" items="${template.authResources}" varStatus="status">
									<div class="col-lg-2">
										<div class="well">
											<label> <input name="auths" type="checkbox" onclick="javascript:$(this).parent().parent().find(':checkbox').prop('checked', $(this).is(':checked'));" value="${e.key}" ${selectedSet[e.key.code]==true ?'checked':''}> ${e.key.key}
											</label>
											<c:forEach var="r" items="${e.value}">
												<c:if test="${r.code!='WELCOME'}">
													<div class="checkbox">
														<label><input name="auths" type="checkbox" value="${r}" ${selectedSet[r.code]==true ?'checked':''}>${r.key}</label>
													</div>
												</c:if>
											</c:forEach>
										</div>
									</div>
								</c:forEach>
							</div>
						</div>
						<input id="auth_id" type="hidden" value="${auth.id }">
						<button type="submit" id="btn_modify_password" class="btn btn-primary btn-lg btn-block">${auth==null?"提交申请":"确认修改"}</button>
					</div>
				</form>
			</div>
		</div>
	</div>
</section>
<script type="text/javascript">
registerFocusClearErrorMsg();
$('#addAuthor').on('submit', function() {
	var authname = $("#authname");
	if (authname.val().length == '') {
		addErrorMsg(authname, "请输入权限组名称");
		return false;
	}

	var auths = [];
	$("input[name='auths']:checked").each(function() {
		auths.push($(this).val());
	});
	var $btn = $("#btn_modify_password").button('loading');
	$.post($(this).attr("action"), {
		"id" : $("#auth_id").val(),
		"authname" : authname.val(),
		"auths" : auths,
		"token" : "${sessionScope.token}"
	}, function(result) {
		$btn.button('reset');
		ajaxLoadPage2Body("${pageContext.request.contextPath}/backstage/auth/list/");
	});
	return false; // 阻止表单自动提交事件
});
</script>