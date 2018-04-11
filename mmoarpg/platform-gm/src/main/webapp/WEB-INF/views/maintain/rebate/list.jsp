<%@ page language="java" contentType="text/html;charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<section class="content-header">
	<h1>返利数据列表</h1>
	<ol class="breadcrumb">
		<li><a href="${pageContext.request.contextPath}/welcome/"><i class="fa fa-dashboard"></i> 首页</a></li>
		<li>维护工具</li>
		<li class="active">返利数据列表</li>
	</ol>
</section>
<section class="content">
	<div class="row">
		<div class="col-xs-12">
			<div class="box">
				<div class="box-header">
					<h3 class="box-title">
						<i class="fa fa-list"></i> 未发放的返利数据列表
					</h3>
					<div class="box-tools input-group-sm">
						<button type="button" class="btn btn-danger btn-sm" data-toggle="modal" data-target="#confirmModal" data-msg="您确认要清空当前所有数据吗？测试期间用的功能，别犯傻..." data-href="javascript:ajaxLoadPage2Body('${pageContext.request.contextPath}/maintain/rebate/clean/');">清空数据</button>
						<button type="button" class="btn btn-success btn-sm" data-toggle="modal" data-target="#confirmModal" data-msg="上传返利数据，会覆盖当前值噢，您确认上传吗?" data-href="javascript:ajaxLoadPage2Body('${pageContext.request.contextPath}/maintain/rebate/addUI/');">上传数据</button>
					</div>
				</div>
				<div class="box-body table-responsive">
					<table class="table table-hover" id="rebate_list">
						<thead>
							<tr>
								<th>用户编号(UID)</th>
								<th>返利(元宝)</th>
							</tr>
						</thead>
						<tbody>
						<c:forEach var="e" items="${data}">
							<tr role="row">
								<td>${e.key}</td>
								<td>${e.value}</td>
							</tr>
						</c:forEach>
						</tbody>
					</table>
				</div>
			</div>
		</div>
	</div>
</section>
<script>
	$(function() {
		$('#rebate_list').DataTable({
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
	            title: '返利数据'
	        },  {
	            extend: 'print',
	            text:'打印'
	        } ],
	      	//跟数组下标一样，第一列从0开始，这里表格初始化时，第四列默认降序
            "order": [[ 1, "desc" ]]
		});
	});
</script>