<%@ page language="java" contentType="text/html;charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<section class="content-header">
	<h1>CDKEY查询</h1>
	<ol class="breadcrumb">
		<li><a href="${pageContext.request.contextPath}/welcome/"><i class="fa fa-dashboard"></i> 首页</a></li>
		<li>游戏管理</li>
		<li>兑换码管理</li>
		<li class="active">CDKEY查询</li>
	</ol>
</section>
<section class="content">
	<div class="row">
		<div class="col-xs-12">
			<div class="box box-primary">
				<div class="box-header">
					<h3 class="box-title">CDKEY查询</h3>
				</div>
				<form role="form" id="query_cdkey">
					<div class="box-body">
						<div class="form-group">
							<label for="code">CDKEY编码: <span class="text-red">*</span></label>
							<input id="code" class="form-control" placeholder="CDKEY" type="text" value="${code}" maxlength="18">
							<span class="help-block">请输入CDKEY编码...</span>
						</div>
						<button type="submit" id="btn_cdkey" class="btn btn-primary btn-lg btn-block">提交</button>
					</div>
				</form>
			</div>
		</div>
	</div>
	<c:if test="${not empty code}">
		<c:choose>
			<c:when test="${empty cdkey || empty cdkCode}">
				<div class="alert alert-danger alert-dismissible">
		            <button type="button" class="close" data-dismiss="alert" aria-hidden="true">×</button>
		            <h4><i class="icon fa fa-ban"></i> 非法CDKEY</h4>
		           	关于非法CDKEY，我没有什么好说的，就是随便输入的或在已有Key做出的修改后的一种编号，你可以理解为他是个假货.
		        </div>
			</c:when>
			<c:otherwise>
				<div class="row">
					<div class="col-xs-12">
						<div class="box box-primary">
							<div class="box-header">
								<h3 class="box-title">CDKEY使用情况</h3>
							</div>
							<div class="box-body">
								<div class="form-group">
									<label for="code">CDK编号:</label>
									<input class="form-control" type="text" value="${cdkey.code}" disabled="disabled">
								</div>
								<div class="form-group">
									<label for="code">CDK名称:</label>
									<input class="form-control" type="text" value="${cdkey.name}" disabled="disabled">
								</div>
								<c:choose>
									<c:when test="${empty cdkCode.usePlayerId}">
										<div class="alert alert-success alert-dismissible">
							                <button type="button" class="close" data-dismiss="alert" aria-hidden="true">×</button>
							                <h4><i class="icon fa fa-check"></i> 未使用</h4>
							             	此编号的CDKEY尚无人使用...
						              	</div>
									</c:when>
									<c:otherwise>
										<div class="form-group">
											<label for="code">使用角色ID:</label>
											<input class="form-control" type="text" value="${cdkCode.usePlayerId}" disabled="disabled">
										</div>
										<div class="form-group">
											<label for="code">使用渠道:</label>
											<input class="form-control" type="text" value="${cdkCode.useChannel}" disabled="disabled">
										</div>
										<div class="form-group">
											<label for="code">使用次数:</label>
											<input class="form-control" type="text" value="${cdkCode.useNum}" disabled="disabled">
										</div>
										<div class="form-group">
											<label for="code">使用时间:</label>
											<input class="form-control" type="text" value="<fmt:formatDate value="${cdkCode.useDate}" pattern="yyyy-MM-dd HH:mm:ss"/>" disabled="disabled">
										</div>
									</c:otherwise>
								</c:choose>
							</div>
						</div>
					</div>
				</div>
			</c:otherwise>
		</c:choose>
	</c:if>
</section>
<script>
	$('#query_cdkey').on('submit', function() {
		var code = $("#code");
		if (code.val().length == '') {
			addErrorMsg(code, "请输入CDKEY编码...");
			return false;
		}
		var $btn = $("#btn_cdkey").button('loading');
		ajaxLoadPage2Body('${pageContext.request.contextPath}/game/cdkey/query/?code='+code.val());
		return false;
	});
</script>