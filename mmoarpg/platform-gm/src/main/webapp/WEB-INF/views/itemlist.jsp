<%@ page language="java" contentType="text/html;charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<div class="row">
	<div class="col-lg-5">
		<div class="box box-solid">
			<div class="box-header with-border">
				<h3 class="box-title">道具清单</h3>
			</div>
			<div class="box-body">
				<div class="box">
					<table id="itemList" class="table table-hover">
						<thead>
							<tr>
								<th>道具编号</th>
								<th>道具名称</th>
								<th>道具数量</th>
								<th>删除</th>
							</tr>
						</thead>
						<tbody>
						</tbody>
					</table>
				</div>
			</div>
		</div>
	</div>
	<div class="col-lg-7">
		<div class="box box-solid">
			<div class="box-header with-border">
				<h3 class="box-title">道具模板</h3>
			</div>
			<div class="box-body">
				<table class="table table-striped table-bordered"
					id="dataTables-itemlist">
					<thead>
						<tr>
							<th>道具编号</th>
							<th>道具名称</th>
							<th>添加</th>
						</tr>
					</thead>
					<tbody>
						<c:forEach var="e" items="${items}">
							<tr>
								<td>${e.key}</td>
								<td>${e.value}</td>
								<td><a href="javascript:addItemTd('${e.key}','${e.value}');">添加</a></td>
							</tr>
						</c:forEach>
					</tbody>
				</table>
			</div>
		</div>
	</div>
</div>
<script type="text/javascript">
	$(function() {
		$('#dataTables-itemlist').DataTable({
			responsive : true
		});
	});
</script>