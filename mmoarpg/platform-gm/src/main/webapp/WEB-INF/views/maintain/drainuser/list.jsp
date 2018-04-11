<%@ page language="java" contentType="text/html;charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>

<section class="content-header">
	<h1>流失用户提取</h1>
	<ol class="breadcrumb">
		<li><a href="${pageContext.request.contextPath}/welcome/"><i class="fa fa-dashboard"></i> 首页</a></li>
		<li>运维工具</li>
		<li class="active">流失用户提取</li>
	</ol>
</section>
<section class="content">
	<div class="box box-primary">
		<div class="box-header with-border">
			<h3 class="box-title">提取条件 :</h3>
		</div>
		<div class="box-body">
			<div class="form-group">
				<label>Date : <span class="text-red">*</span></label>
				<div id="drainuserLabel" class="input-group date">
					<div class="input-group-addon">
						<i class="fa fa-calendar"></i>
					</div>
					<input id="drainuserDate" class="form-control pull-right" value="${today}" type="text">
				</div>
			</div>
			<button type="submit" class="btn btn-primary btn-lg btn-block" onclick="javascript:ajaxLoadPage2Body('${pageContext.request.contextPath}/maintain/drainuser/query/?today='+$('#drainuserDate').val());">查询</button>
		</div>
	</div>
	<c:if test="${not empty page}">
		<div class="box">
			<div class="box-header with-border">
				<div class="box-header with-border">
					<h3 class="box-title">玩家列表</h3>
				</div>
				<div class="box-body">
					<c:choose>
						<c:when test="${empty page.content}">没有找到记录</c:when>
						<c:otherwise>
							<table class="table table-bordered table-striped" id="example">
								<thead>
									<tr>
										<th>创角时间</th>
										<th>OPENID</th>
										<th>角色ID</th>
										<th>用户ID</th>
										<th>角色名称</th>
										<th>IP</th>
									</tr>
								</thead>
								<tbody>
									<c:forEach var="e" items="${page.content}">
										<tr role="row" class="odd">
											<td><fmt:formatDate value="${e.createTime}" type="both" /></td>
											<td>${e.openId}</td>
											<td>${e.id}</td>
											<td>${e.code}</td>
											<td>${e.name}</td>
											<td>${e.ip}</td>
										</tr>
									</c:forEach>
								</tbody>
							</table>
						</c:otherwise>
					</c:choose>
				</div>
			</div>
		</div>
	</c:if>
</section>

<script>
	$(function() {
		$('#drainuserLabel').daterangepicker({
		    "singleDatePicker": true,
		    "startDate": "${today}",
		    "maxDate": "${maxtime}",
		    locale: {
	            format: 'YYYY-MM-DD'
	        }
		}, function(start, end, label) {  
	         $('#drainuserDate').val(start.format('YYYY-MM-DD'));  
	    });
		
		$('#example').DataTable({
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
	            title: '流失用户信息_${today}'
	        },  {
	            extend: 'print',
	            text:'打印'
	        } ],
	      	//跟数组下标一样，第一列从0开始，这里表格初始化时，第四列默认降序
            "order": [[ 0, "asc" ]]
		});
	});
</script>