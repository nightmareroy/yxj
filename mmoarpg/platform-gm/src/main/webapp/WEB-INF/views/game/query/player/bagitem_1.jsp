<%@ page language="java" contentType="text/html;charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<table class="table table-bordered table-striped" id="equip_list">
	<thead>
		<tr>
			<th>装备名称</th>
			<th>强化等级</th>
			<th>宝石属性</th>
			<th>基础属性</th>
			<th>扩展属性</th>
			<th>传奇属性</th>
		</tr>
	</thead>
	<tbody>
		<c:forEach var="e" items="${result.rows}">
			<tr role="row">
				<td>${e.itemName}</td>
				<td>${e.level}</td>
				<td>${e.gemAttr}</td>
				<td>${e.baseAttr}</td>
				<td>${e.extAttr}</td>
				<td>${e.legendAttr}</td>
			</tr>
		</c:forEach>
	</tbody>
</table>

<script>
	$(function() {
		$('#equip_list').DataTable({
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
	            title: '装备_${playerId}'
	        },  {
	            extend: 'print',
	            text:'打印'
	        } ]
		});
	});
</script>