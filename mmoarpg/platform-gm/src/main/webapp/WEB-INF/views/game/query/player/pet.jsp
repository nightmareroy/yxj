<%@ page language="java" contentType="text/html;charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<table class="table table-bordered table-striped" id="pet_list">
	<thead>
		<tr>
			<th>宠物名称</th>
			<th>宠物品质</th>
			<th>宠物战力</th>
			<th>突破等级</th>
			<th>宠物等级</th>
			<th>技能数量</th>
			<th>是否出战</th>
		</tr>
	</thead>
	<tbody>
		<c:forEach var="e" items="${result.rows}">
			<tr role="row">
				<td>${e.name}</td>
				<td>${e.quality}</td>
				<td>${e.fightPower}</td>
				<td>${e.upLevel}</td>
				<td>${e.level}</td>
				<td>${e.skillNum}</td>
				<td>${e.quality}</td>
				<td>${e.isOut}</td>
			</tr>
		</c:forEach>
	</tbody>
</table>

<script>
	$(function() {
		$('#pet_list').DataTable({
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