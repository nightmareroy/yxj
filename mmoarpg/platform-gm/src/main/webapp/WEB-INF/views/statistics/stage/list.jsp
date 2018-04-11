<%@ page language="java" contentType="text/html;charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<section class="content-header">
	<h1>副本通关转化率:<small id="today"><i class="fa fa-calendar"><u> ${today}</u></i></small></h1>
	<ol class="breadcrumb">
		<li><a href="${pageContext.request.contextPath}/welcome/"><i class="fa fa-dashboard"></i> 首页</a></li>
		<li>日志查询</li>
		<li class="active">副本通关转化率</li>
	</ol>
</section>
<section class="content">
	<c:if test="${not empty page}">
		<div class="box">
			<div class="box-header with-border">
				<div class="box-header with-border">
					<h3 class="box-title">副本通关数据列表</h3>
				</div>
				<div class="box-body">
					<c:choose>
						<c:when test="${empty page.content}">没有找到记录</c:when>
						<c:otherwise>
							<table class="table table-bordered table-striped" id="example">
								<thead>
									<tr>
										<th>关卡ID</th>
										<th>关卡名称</th>
										<th>总人数</th>
										<th>通关人数</th>
										<th>失败人数</th>
										<th>1星通过</th>
										<th>2星通过</th>
										<th>3星通过</th>
										<th>失败次数</th>
										<th>失败率</th>
									</tr>
								</thead>
								<tbody>
									<c:forEach var="e" items="${page.content}">
										<tr role="row" class="odd">
											<td>${e.stageId}</td>
											<td><spring:message code="template.stage.${e.stageId}" text="未知"/></td>
											<td>${e.totalNum}</td>
											<td>${e.completeNum}</td>
											<td>${e.failNum}</td>
											<td>${e.completeCount1Star}</td>
											<td>${e.completeCount2Star}</td>
											<td>${e.completeCount3Star}</td>
											<td>${e.failCount}</td>
											<td><fmt:formatNumber value="${e.failCount*100/(e.completeCount1Star + e.completeCount2Star + e.completeCount3Star + e.failCount)}" pattern="#0.00" />%</td>
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
	            title: '副本通关转化'
	        },  {
	            extend: 'print',
	            text:'打印'
	        } ],
	      	//跟数组下标一样，第一列从0开始，这里表格初始化时，第四列默认降序
            "order": [[ 0, "asc" ]]
		});
		
		$('#today').daterangepicker({
		    "singleDatePicker": true,
		    "startDate": "${today}",
		    "maxDate": "${maxtime}",
		    locale: {
	            format: 'YYYY-MM-DD'
	        },
		}, function(start, end, label) {
			ajaxLoadPage2Body("${pageContext.request.contextPath}/statistics/stage/query/?today="+start.format('YYYY-MM-DD'));
		});
	});
</script>