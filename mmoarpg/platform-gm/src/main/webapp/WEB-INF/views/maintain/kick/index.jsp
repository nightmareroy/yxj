<%@ page language="java" contentType="text/html;charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<section class="content-header">
	<h1>停机维护踢人</h1>
	<ol class="breadcrumb">
		<li><a href="${pageContext.request.contextPath}/welcome/"><i class="fa fa-dashboard"></i> 首页</a></li>
		<li>运维工具</li>
		<li class="active">停机维护踢人</li>
	</ol>
</section>
<section class="content">

	<!-- 选服列表 -->
	<jsp:include page="../../serverlist.jsp">
		<jsp:param name="multiselect" value="true" />
		<jsp:param name="dosomething" value="javascript:selectServerId(this, true);" />
	</jsp:include>
	
	<div class="row">
		<div class="col-xs-12">
			<div class="box box-primary">
				<div class="box-header">
					<h3 class="box-title">停机维护前踢人操作</h3>
				</div>
				<form role="form" action="${pageContext.request.contextPath}/maintain/kick/all/" method="post" id="ready_kick">
					<div class="box-body">
						<button type="submit" id="btn_ready_kick" class="btn btn-primary btn-lg btn-block">确认踢人</button>
					</div>
				</form>
			</div>
		</div>
	</div>
</section>
<script>
	registerFocusClearErrorMsg();
	
	$('#ready_kick').on('submit', function() {
		var serverIds = [];
		$("button[type=button][name=server]").each(function() {
			if (!$(this).hasClass("btn-default")) {
				serverIds.push($(this).val());
			}
		});
		if (serverIds.length == 0) {
			addErrorMsg($("#server_list"), "只少选择一个目标服务器....");
			return false; // 阻止表单自动提交事件
		}

		var $btn = $("#btn_ready_kick").button('loading');
		$.post($(this).attr("action"), {
			"serverIds" : serverIds
		}, function(result) {
			$btn.button('reset');
			resultCallback("踢人成功，确认返回区服列表", "javascript:ajaxLoadPage2Body('${pageContext.request.contextPath}/maintain/server/list');");
	    });
		return false;
	});
</script>