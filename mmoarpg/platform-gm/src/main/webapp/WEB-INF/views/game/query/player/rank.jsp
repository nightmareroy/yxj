<%@ page language="java" contentType="text/html;charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<table class="table table-bordered table-striped" id="rank_list">
	<thead>
		<tr>
			<th>榜单类型</th>
			<th>榜单名称</th>
			<th>排名</th>
		</tr>
	</thead>
	<tbody>
		<c:forEach var="e" items="${result.rows}">
			<tr role="row">
				<td>${e.type}</td>
				<td>${e.name}</td>
				<td>${e.rank}</td>
			</tr>
		</c:forEach>
	</tbody>
</table>

<script>
	$(function() {
		$('#rank_list').DataTable({
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
	        } ],
	      	//跟数组下标一样，第一列从0开始，这里表格初始化时，第四列默认降序
            "order": [[ 1, "desc" ]]
		});
	});
</script>