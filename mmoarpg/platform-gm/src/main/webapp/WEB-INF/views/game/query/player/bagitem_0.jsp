<%@ page language="java" contentType="text/html;charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<table class="table table-bordered table-striped" id="bag_item_list">
	<thead>
		<tr>
			<th>物品编号</th>
			<th>物品名称</th>
			<th>物品数量</th>
			<th>是否装备</th>
			<th>基础属性</th>
			<th>扩展属性</th>
			<th>传奇属性</th>
		</tr>
	</thead>
	<tbody>
		<c:forEach var="e" items="${result.rows}">
			<tr role="row">
				<td>${e.itemId}</td>
				<td>${e.itemName}</td>
				<td>${e.itemNum}</td>
				<td>${e.isEquip}</td>
				<td>${e.baseAttr}</td>
				<td>${e.extAttr}</td>
				<td>${e.legendAttr}</td>
			</tr>
		</c:forEach>
	</tbody>
</table>

<script>
	$(function() {
		$('#bag_item_list').DataTable({
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
	            title: '背包物品_${playerId}'
	        },  {
	            extend: 'print',
	            text:'打印'
	        } ]
		});
	});
</script>