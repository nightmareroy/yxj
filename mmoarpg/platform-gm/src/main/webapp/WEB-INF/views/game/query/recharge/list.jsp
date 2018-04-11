<%@ page language="java" contentType="text/html;charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>

<section class="content-header">
	<h1>查询充值记录</h1>
	<ol class="breadcrumb">
		<li><a href="${pageContext.request.contextPath}/welcome/"><i class="fa fa-dashboard"></i> 首页</a></li>
		<li>游戏管理</li>
		<li class="active">查询充值记录</li>
	</ol>
</section>

<section class="content">

	<!-- 选服列表 -->
	<jsp:include page="../../../serverlist.jsp">
		<jsp:param name="multiselect" value="false" />
		<jsp:param name="dosomething" value="javascript:selectServerId(this, false);" />
	</jsp:include>

	<div class="box box-primary">
		<div class="box-header with-border">
			<h3 class="box-title">查询条件 :</h3>
		</div>
		
		<div class="box-body">
			<div class="form-group">
				<label for="playername">角色名称 : <span class="text-red">*</span></label>
				<input id="playername" class="form-control" placeholder="请输入玩家的角色名称..."  value="${playername}" type="text">
			</div>
			<button id="btn_query_recharge" class="btn btn-primary btn-lg btn-block">查询</button>
		</div>
	</div>

	<jsp:include page="../../../error.jsp" />

	<c:if test="${not empty result}">
		<div class="box">
			<div class="box-header with-border">
				<div class="box-header with-border">
					<h3 class="box-title">充值记录列表</h3>
				</div>
				<div class="box-body">
					<table class="table table-bordered table-striped" id="pay_list">
						<thead>
							<tr>
								<th>账号</th>
								<th>角色名称</th>
								<th>充值时间</th>
								<th>充值金额</th>
								<th>是否为卡片</th>
							</tr>
						</thead>
						<tbody>
						<c:forEach var="e" items="${result.rows}">
							<tr role="row" class="odd">
								<td>${e.account}</td>
								<td>${e.name}</td>
								<td>${e.payDate}</td>
								<td>${e.money}</td>
								<td>${e.isCard}</td>
							</tr>
						</c:forEach>
						</tbody>
					</table>
				</div>
			</div>
		</div>
	</c:if>
</section>

<script>
	$(function() {
		registerFocusClearErrorMsg();
		$("#btn_query_recharge").click(function(){
			var serverIds = [];
			$("button[type=button][name=server]").each(function() {
				if (!$(this).hasClass("btn-default")) {
					serverIds.push($(this).val());
				}
			});
			if (serverIds.length == 0) {
				addErrorMsg($("#server_list"), "请先选择目标服务器....");
				return false; // 阻止表单自动提交事件
			}
			
			var playername = $("#playername");
			if (playername.val().length == 0) {
				addErrorMsg(playername, "请输入玩家的角色名称...");
				return false;
			}
			
			var serverId = serverIds[0];
			ajaxLoadPage2Body('${pageContext.request.contextPath}/game/query/recharge/list/?serverId='+serverId+'&playername='+playername.val());
		});
		$('#pay_list').DataTable({
			language : {//国际化文件
				url : "${pageContext.request.contextPath}/resources/plugins/datatables/i18n/zh_CN.json"
			},
			dom : 'Bfrtip',
			buttons : [ {
	            extend: 'copy',
	            text:'复制'
	        }, {
	            extend: 'excel',
	            text:'导出',
	            title: '查询充值记录_${playername}'
	        },  {
	            extend: 'print',
	            text:'打印'
	        } ],
	      	//跟数组下标一样，第一列从0开始，这里表格初始化时，第四列默认降序
            "order": [[ 2, "desc" ]]
		});
	});
</script>