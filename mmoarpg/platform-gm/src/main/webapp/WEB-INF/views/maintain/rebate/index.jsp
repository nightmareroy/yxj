<%@ page language="java" contentType="text/html;charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<section class="content-header">
	<h1>上传返利数据</h1>
	<ol class="breadcrumb">
		<li><a href="${pageContext.request.contextPath}/welcome/"><i class="fa fa-dashboard"></i> 首页</a></li>
		<li>维护工具</li>
		<li class="active">上传返利数据</li>
	</ol>
</section>
<section class="content">
	<div class="row">
		<div class="col-xs-12">
			<div class="box box-primary">
				<div class="box-header">
					<h3 class="box-title">上传返利数据</h3>
				</div>
				<div class="box-body">
					<div id="uploadfile">
						<form action="${pageContext.request.contextPath}/maintain/rebate/upload/" enctype="multipart/form-data" method="post" id="uploadclassfile">
							<div class="form-group">
		                 		<label for="rebateFile">返利数据</label>
		                  		<input type="file" id="rebateFile" name="rebateFile"><p class="help-block">请上传充值返利.xlsx</p>
		               		</div>
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
</section>
<script type="text/javascript">
	registerFocusClearErrorMsg();
	
	$("#uploadclassfile").submit(function(evt) {
		evt.preventDefault();
		
		var rebateFile = $("#rebateFile");
		if (rebateFile.val().length == '') {
			addErrorMsg(rebateFile, "请上传充值返利.xlsx...");
			return false;
		}
		
		$("#uploadfile").hide().next().show();
		
		$("#uploadclassfile").ajaxSubmit({
			uploadProgress : function(event, position, total, percentComplete) {
				$("#progress").css("width", percentComplete + "%");
			},
			success : function(result) {
				resultCallback("上传成功", "javascript:ajaxLoadPage2Body('${pageContext.request.contextPath}/maintain/rebate/list/');");
			},
			error : function(result) {
				resultCallback("上传失败", "javascript:ajaxLoadPage2Body('${pageContext.request.contextPath}/maintain/rebate/list/');");
			}
		});
	});
</script>