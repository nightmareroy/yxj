<%@ page language="java" contentType="text/html;charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<section class="content-header">
	<h1>执行脚本</h1>
	<ol class="breadcrumb">
		<li><a href="${pageContext.request.contextPath}/welcome/"><i class="fa fa-dashboard"></i> 首页</a></li>
		<li>维护工具</li>
		<li class="active">执行脚本</li>
	</ol>
</section>
<section class="content">
	<!-- 选服列表 -->
	<jsp:include page="../../serverlist.jsp">
		<jsp:param name="multiselect" value="true" />
		<jsp:param name="dosomething" value="javascript:selectServerId(this, true);" />
	</jsp:include>

	<div class="alert alert-danger alert-dismissible">
		<button type="button" class="close" data-dismiss="alert" aria-hidden="true">×</button>
		<h4><i class="icon fa fa-ban"></i> 温馨提示! </h4>
		脚本功能，很好很强大，但是弄死好会死人的... 
	</div>

	<div class="row">
		<div class="col-xs-12">
			<div class="box box-primary">
				<div class="box-header">
					<h3 class="box-title">脚本信息</h3>
				</div>
				<form role="form" action="${pageContext.request.contextPath}/maintain/script/exec/" method="post" id="execScript">
					<div class="box-body">
						<div class="form-group">
							<label for="script">脚本内容: <span class="text-red">*</span></label>
							<textarea id="script" class="form-control" rows="10" placeholder="脚本功能，很好很强大，弄死好会死人的...">
package com.wanniu.core.groovy;

public class GroovyExcutor implements IGameGroovy {
	public String execute() {
		// dosomeing...
		
		return "OK";
	}
}
							</textarea>
						</div>
						<button type="submit" id="btn_exce_script" class="btn btn-primary btn-lg btn-block">确认执行</button>
					</div>
				</form>
			</div>
		</div>
	</div>

	<div class="box box-warning">
		<div class="box-header">
			<h3 class="box-title">执行结果</h3>
		</div>
		<div class="box-body" id="exec_result">
        </div>
	</div>
</section>
<script type="text/javascript">
	registerFocusClearErrorMsg();
	
	$('#execScript').on('submit', function() {
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
		
		var script = $("#script");
		if (script.val().length == '') {
			addErrorMsg(script, "请输入脚本内容...");
			return false;
		}
		
		var $btn = $("#btn_exce_script").button('loading');
		$.post($(this).attr("action"), {
			"serverIds" : serverIds,
			"script" : script.val(),
		}, function(result) {
			$btn.button('reset');
			$("#exec_result").html(result);
		});
		return false; // 阻止表单自动提交事件
	});
</script>