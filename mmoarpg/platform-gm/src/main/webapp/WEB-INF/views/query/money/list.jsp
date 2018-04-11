<%@ page language="java" contentType="text/html;charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>

<section class="content-header">
	<h1>查询货币记录</h1>
	<ol class="breadcrumb">
		<li><a href="${pageContext.request.contextPath}/welcome/"><i class="fa fa-dashboard"></i> 首页</a></li>
		<li>日志查询</li>
		<li class="active">查询货币记录</li>
	</ol>
</section>
<section class="content">
	<div class="box box-primary">
		<div class="box-header with-border">
			<h3 class="box-title">查询条件 </h3>
		</div>
		<div class="box-body">
			<div class="form-group">
				<label>时间 : <span class="text-red">*</span></label>
				<div id="moneyLabel" class="input-group date">
					<div class="input-group-addon">
						<i class="fa fa-calendar"></i>
					</div>
					<input class="form-control pull-right" id="moneyDate" value="${today}" type="text">
				</div>
			</div>
			<div class="form-group">
				<label for="playerId">用户ID:<span class="text-red">*</span></label>
				<input id="playerId" class="form-control" placeholder="用户ID:必填" value="${playerId}" type="text" maxlength="36">
			</div>
			<div class="form-group">
				<label for="type">操作类别:</label> 
				<select class="form-control" id="changeType">
					<option value="0" ${type==0?'selected':''}>全部</option>
					<option value="1" ${type==1?'selected':''}>增加</option>
					<option value="2" ${type==2?'selected':''}>减少</option>
				</select>
			</div>
			<div class="form-group">
				<label for="itemId">货币类型:</label>
				<select class="form-control" id="moneyId">
					<option value="">全部</option>
					<c:forEach var="e" items="${moneyType}">
						<option value="${e.id}" ${moneyId == e.id ? 'selected' : ''}><spring:message code='i18n.item.${e.id}' text='未知' />(${e.id})</option>
					</c:forEach>
				</select>
			</div>
			<button type="submit" class="btn btn-primary btn-lg btn-block" onclick="javascript:ajaxLoadPage2Body('${pageContext.request.contextPath}/query/money/check/?datepicker='+$('#moneyDate').val()+'&playerId='+$('#playerId').val()+'&type='+$('#changeType').val()+'&moneyId='+$('#moneyId').val());">查询</button>
		</div>
	</div>
	<c:if test="${not empty page}">
		<div class="box">
			<div class="box-header with-border">
				<div class="box-header with-border">
					<h3 class="box-title">货币流水列表</h3>
					<div class="box-tools pull-right">
						<button class="btn btn-primary btn-xs" onclick="javascript:ajaxLoadPage2Body('${pageContext.request.contextPath}/query/money/check/?datepicker=${yesterday}&playerId='+$('#playerId').val()+'&type=${type}&moneyId=${moneyId}');">上一天</button>
		                <button class="btn btn-primary btn-xs" onclick="javascript:ajaxLoadPage2Body('${pageContext.request.contextPath}/query/money/check/?datepicker=${tomorrow}&playerId='+$('#playerId').val()+'&type=${type}&moneyId=${moneyId}');">下一天</button>
		            </div>
				</div>
				<div class="box-body">
					<c:choose>
						<c:when test="${empty page.content}">没有找到记录</c:when>
						<c:otherwise>
							<table class="table table-bordered table-striped" id="money">
								<thead>
									<tr>
										<th>时间</th>
										<th>角色ID</th>
										<th>角色名称</th>
										<th>货币类型</th>
										<th>动作前数量</th>
										<th>增加/减少</th>
										<th>涉及货币数量</th>
										<th>动作后数量</th>
										<th>原因</th>
									</tr>
								</thead>
								<tbody>
									<c:forEach var="e" items="${page.content}">
										<tr role="row">
											<td><fmt:formatDate value="${e.date}" type="both" /></td>
											<td>${e.id}</td>
											<td>${e.name}</td>
											<td><spring:message code='i18n.item.${e.type}' text='未知' />(${e.type})</td>
											<td>${e.before}</td>
											<td><spring:message code='i18n.operate.type.${e.operate}' text='未知(${e.operate})' /></td>
											<td>${e.money}</td>
											<td>${e.after}</td>
											<td><spring:message code='i18n.func.code.${e.origin}' text='未知' />(${e.origin})</td>
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
		$('#moneyLabel').daterangepicker({
		    "singleDatePicker": true,
		    "startDate": "${today}",
		    "maxDate": "${maxtime}",
		    locale: {
	            format: 'YYYY-MM-DD'
	        }
		}, function(start, end, label) {  
	         $('#moneyDate').val(start.format('YYYY-MM-DD'));  
	    });
		$('#money').DataTable({
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
	            title: '查询货币记录_${today}_'+$('#playerId').val()
	        },  {
	            extend: 'print',
	            text:'打印'
	        } ],
	      	//跟数组下标一样，第一列从0开始，这里表格初始化时，第四列默认降序
            "order": [[ 0, "desc" ]]
		});
	});
</script>