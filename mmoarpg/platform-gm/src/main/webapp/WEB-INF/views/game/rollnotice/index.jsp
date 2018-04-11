<%@ page language="java" contentType="text/html;charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

<section class="content-header">
	<h1>${notice==null? '添加公告':'编辑公告'}</h1>
	<ol class="breadcrumb">
		<li><a href="${pageContext.request.contextPath}/welcome/"><i class="fa fa-dashboard"></i> 首页</a></li>
		<li><a href="${pageContext.request.contextPath}/game/roll/notice/list/">滚动公告</a></li>
		<li class="active">${notice==null? '添加公告':'编辑公告'}</li>
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
					<h3 class="box-title">滚动公告信息</h3>
				</div>
				<form role="form" action="${pageContext.request.contextPath}/game/roll/notice/${notice==null?'add':'edit'}/" method="post" id="editRollNotice">
					<div class="box-body">
						<div class="form-group">
							<label>滚动时间: </label>
							<div class="input-group">
								<div class="input-group-addon">
									<i class="fa fa-clock-o"></i>
								</div>
								<c:choose>
									<c:when test="${notice==null}">
										<input type="text" class="form-control pull-right" id="reservationtime">
									</c:when>
									<c:otherwise>
										<input type="text" class="form-control pull-right" id="reservationtime" value="${notice.startTime} - ${notice.endTime}">
									</c:otherwise>
								</c:choose>
							</div>
						</div>
						<div class="form-group">
							<label for="interval">滚动间隔(分钟): <span class="text-red">*</span></label>
							<input id="interval" class="form-control" type="text" value="${notice==null?5:notice.interval}" maxlength="64">
						</div>
						<div class="form-group">
							<label>内容：<span class="text-red">*</span></label>
							<textarea id="content" class="form-control ckeditor" rows="9">${notice.content}</textarea>
						</div>
						<input id="notice_id" type="hidden" value="${notice.id}">
						<button type="submit" id="btn_modify_notice" class="btn btn-primary btn-lg btn-block">${notice==null? '发布公告':'编辑公告'}</button>
					</div>
				</form>
			</div>
		</div>
	</div>
</section>
<script>
$(function() {
	registerFocusClearErrorMsg();
	$('#reservationtime').daterangepicker({
		timePicker24Hour: true,
		timePicker: true,
        locale: {
            format: 'YYYY-MM-DD HH:mm:ss'
        },
        ranges: {
            '今天': [moment(), moment()],
            '昨天': [moment().subtract(1, 'days'), moment().subtract(1, 'days')],
            '最近7天': [moment().subtract(6, 'days'), moment()],
            '最近30开': [moment().subtract(29, 'days'), moment()],
            '这个月': [moment().startOf('month'), moment().endOf('month')],
            '上个月': [moment().subtract(1, 'month').startOf('month'), moment().subtract(1, 'month').endOf('month')]
         }
	});
});

var ckeditor = CKEDITOR.replace('content');
$('#editRollNotice').on('submit', function() {
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
	
	var reservationtime = $("#reservationtime");
	if (reservationtime.val().length == '') {
		addErrorMsg(reservationtime, "请输入时间区间");
		return false;
	}
	
	var interval = $("#interval");
	if (interval.val().length == '') {
		addErrorMsg(interval, "请输入时间间隔");
		return false;
	}
	
	var $btn = $("#btn_modify_notice").button('loading');
	$.post($(this).attr("action"), {
		"sids" : serverIds,
		"id" : $("#notice_id").val(),
		"interval" : interval.val(),
		"reservationtime" : reservationtime.val(),
		"content" : ckeditor.getData()
	}, function(result) {
		$btn.button('reset');
		resultCallback("操作成功", "javascript:ajaxLoadPage2Body('${pageContext.request.contextPath}/game/roll/notice/list/');");
	});
	return false; // 阻止表单自动提交事件
});			
</script>