<%@ page language="java" contentType="text/html;charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<table class="table table-bordered table-striped" id="mount_list">
	<thead>
		<tr>
			<th>坐骑名称</th>
			<th>坐骑品阶</th>
			<th>星级</th>
			<th>属性</th>
		</tr>
	</thead>
	<tbody>
		<c:forEach var="e" items="${result.rows}">
			<tr role="row">
				<td>${e.name}</td>
				<td>${e.quality}</td>
				<td>${e.star}</td>
				<td>${e.attr}</td>
			</tr>
		</c:forEach>
	</tbody>
</table>

<script>
	$(function() {
		$('#mount_list').DataTable({
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
	            title: '个人排名_${playerId}'
	        },  {
	            extend: 'print',
	            text:'打印'
	        } ]
		});
	});
</script>