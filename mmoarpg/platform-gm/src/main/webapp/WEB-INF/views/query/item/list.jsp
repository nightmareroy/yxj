<%@ page language="java" contentType="text/html;charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>

<section class="content-header">
	<h1>查询道具记录</h1>
	<ol class="breadcrumb">
		<li><a href="${pageContext.request.contextPath}/welcome/"><i class="fa fa-dashboard"></i> 首页</a></li>
		<li>日志查询</li>
		<li class="active">查询道具记录</li>
	</ol>
</section>
<section class="content">
	<div class="box box-primary">
		<div class="box-header with-border">
			<h3 class="box-title">查询条件 :</h3>
		</div>
		<div class="box-body">
			<div class="form-group">
				<label>Date : <span class="text-red">*</span></label>
				<div id="itemLabel" class="input-group date">
					<div class="input-group-addon">
						<i class="fa fa-calendar"></i>
					</div>
					<input id="itemDate" class="form-control pull-right" value="${today}" type="text">
				</div>
			</div>
			<div class="form-group">
				<label for="playerId">角色ID : <span class="text-red">*</span></label>
				<input id="playerId" class="form-control" placeholder="角色ID:必填" value="${playerId}" type="text">
			</div>
			<div class="form-group">
				<label for="type">类型: <span>(选填)</span></label> 
				<select class="form-control" id="changeType">
					<option value="0" ${type==0?'selected':''}>全部</option>
					<option value="1" ${type==1?'selected':''}>增加</option>
					<option value="2" ${type==2?'selected':''}>减少</option>
				</select>
			</div>
			<div class="form-group">
				<label for="itemcode">道具ID : <span>(选填)</span></label> 
				<input id="itemcode" class="form-control" placeholder="道具ID:选填" value="${itemcode}" type="text">
			</div>
			<button type="submit" class="btn btn-primary btn-lg btn-block" onclick="javascript:ajaxLoadPage2Body('${pageContext.request.contextPath}/query/item/check/?datepicker='+$('#itemDate').val()+'&playerId='+$('#playerId').val()+'&type='+$('#changeType').val()+'&itemcode='+$('#itemcode').val());">查询</button>
		</div>
	</div>
	<c:if test="${not empty page}">
		<div class="box">
			<div class="box-header with-border">
				<div class="box-header with-border">
					<h3 class="box-title">道具流水列表</h3>
					<div class="box-tools pull-right">
						<button class="btn btn-primary btn-xs" onclick="javascript:ajaxLoadPage2Body('${pageContext.request.contextPath}/query/item/check/?datepicker=${yesterday}&playerId='+$('#playerId').val()+'&type=${type}&itemcode=${itemcode}');">上一天</button>
		                <button class="btn btn-primary btn-xs" onclick="javascript:ajaxLoadPage2Body('${pageContext.request.contextPath}/query/item/check/?datepicker=${tomorrow}&playerId='+$('#playerId').val()+'&type=${type}&itemcode=${itemcode}');">下一天</button>
		            </div>
				</div>
				<div class="box-body">
					<c:choose>
						<c:when test="${empty page.content}">没有找到记录</c:when>
						<c:otherwise>
							<table class="table table-bordered table-striped" id="example">
								<thead>
									<tr>
										<th>时间</th>
										<th>角色ID</th>
										<th>角色名称</th>
										<th>道具ID</th>
										<th>道具名称</th>
										<th>类型</th>
										<th>流动数量</th>
										<th>原因</th>
									</tr>
								</thead>
								<tbody>
									<c:forEach var="e" items="${page.content}">
										<tr role="row" class="odd">
											<td><fmt:formatDate value="${e.date}" type="both" /></td>
											<td>${e.id}</td>
											<td>${e.name}</td>
											<td>${e.itemcode}</td>
											<td><spring:message code='i18n.item.${e.itemcode}' text='未知' /></td>
											<td><spring:message code='i18n.operate.type.${e.operate}' text='未知(${e.operate})' /></td>
											<td>${e.count}</td>
											<td><spring:message code='i18n.func.code.${e.origin}' text='未知' />（${e.origin}）</td>
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
		$('#itemLabel').daterangepicker({
		    "singleDatePicker": true,
		    "startDate": "${today}",
		    "maxDate": "${maxtime}",
		    locale: {
	            format: 'YYYY-MM-DD'
	        }
		}, function(start, end, label) {  
	         $('#itemDate').val(start.format('YYYY-MM-DD'));  
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
	            title: '查询道具记录_${today}_'+$('#playerId').val()
	        },  {
	            extend: 'print',
	            text:'打印'
	        } ],
	      	//跟数组下标一样，第一列从0开始，这里表格初始化时，第四列默认降序
            "order": [[ 0, "desc" ]]
		});
	});
</script>