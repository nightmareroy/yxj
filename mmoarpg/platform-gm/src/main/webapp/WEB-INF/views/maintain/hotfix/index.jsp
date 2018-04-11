<%@ page language="java" contentType="text/html;charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<section class="content-header">
	<h1>代码热更新</h1>
	<ol class="breadcrumb">
		<li><a href="${pageContext.request.contextPath}/welcome/"><i class="fa fa-dashboard"></i> 首页</a></li>
		<li>维护工具</li>
		<li class="active">代码热更新</li>
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
		代码热更新功能，很好很强大，但是弄死好会死人的... 
	</div>

	<div class="row">
		<div class="col-xs-12">
			<div class="box box-primary">
				<div class="box-header">
					<h3 class="box-title">脚本信息</h3>
				</div>
				<div class="box-body">
					<div id="uploadfile">
						<form action="${pageContext.request.contextPath}/maintain/hotfix/exec/" enctype="multipart/form-data" method="post" id="uploadclassfile">
							<div class="form-group">
								<label for="className">类名: <span class="text-red">*</span></label>
								<input id="className" name="className" value="com.wanniu.game.money.MoneyManager" class="form-control" placeholder="类的全名，带包名的那种..." type="text">
							</div>
							<div class="form-group">
		                 		<label for="classFile">Class文件</label>
		                  		<input type="file" id="classFile" name="classFile"><p class="help-block">请上传编译好的Class文件</p>
		               		</div>
		               		<input type="hidden" id="serverIds" name="serverIds[]">
							<button type="submit" class="btn btn-primary btn-lg btn-block">确认上传</button>
						</form>
					</div>
						
					<div style="display: none;">
						<p><strong>上传进度</strong> <span class="pull-right text-muted">100% Complete</span></p>
						<div class="progress progress-sm active">
				            <div id="progress" class="progress-bar progress-bar-success progress-bar-striped" role="progressbar">
				               	<span class="sr-only">20% Complete</span>
				            </div>
				           </div>
					</div>
				</div>
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
	
	$("#uploadclassfile").submit(function(evt) {
		evt.preventDefault();
		
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
		
		var className = $("#className");
		if (className.val().length == '') {
			addErrorMsg(className, "请输入Class的类名...");
			return false;
		}
		
		var classFile = $("#classFile");
		if (classFile.val().length == '') {
			addErrorMsg(classFile, "请上传Class文件...");
			return false;
		}
		
		$("#serverIds").val(serverIds);
		$("#uploadfile").hide().next().show();
		
		$("#uploadclassfile").ajaxSubmit({
			uploadProgress : function(event, position, total, percentComplete) {
				$("#progress").css("width", percentComplete + "%");
			},
			success : function(result) {
				$("#exec_result").html(result);
			},
			error : function(result) {
				$("#exec_result").html(result);
			}
		});
	});
</script>