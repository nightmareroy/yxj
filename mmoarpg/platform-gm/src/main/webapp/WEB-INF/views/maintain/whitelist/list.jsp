<%@ page language="java" contentType="text/html;charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

<section class="content-header">
	<h1>登录白名单</h1>
	<ol class="breadcrumb">
		<li><a href="${pageContext.request.contextPath}/welcome/"><i class="fa fa-dashboard"></i> 首页</a></li>
		<li>运维工具</li>
		<li class="active">登录白名单</li>
	</ol>
</section>
<section class="content">
	<div class="row">
		<div class="col-xs-12">
			<div class="box">
				<div class="box-header">
					<h3 class="box-title">
						<i class="fa fa-list"></i> 名单列表
					</h3>
					<div class="box-tools input-group input-group-sm">
						<button type="button" class="btn btn-block btn-default btn-sm" onclick="javascript:ajaxLoadPage2Body('${pageContext.request.contextPath}/maintain/whitelist/addUI/');">添加名单</button>
					</div>
				</div>
				<div class="box-body table-responsive">
					<table class="table table-hover" id="whitelist">
						<thead>
							<tr>
								<th>名单类型</th>
								<th>类型</th>
								<th>名单</th>
								<th>终止时间</th>
								<th>描述</th>
								<th>操作</th>
							</tr>
						</thead>
						<tbody>
							<c:forEach var="e" items="${result}">
								<tr role="row">
									<td>${e.white==1?'白名单':'黑名单'}</td>
									<td>${e.type==1?'IP':'UID'}</td>
									<td>${e.ip}</td>
									<td>${e.time==null?'永久':e.time}</td>
									<td>${e.desc}</td>
									<td>
										<a data-toggle="modal" data-target="#confirmModal" data-msg="您确认要编辑[${e.ip}]吗？" data-href="javascript:ajaxLoadPage2Body('${pageContext.request.contextPath}/maintain/whitelist/editUI/?whitelisttype=${e.white}&type=${e.type}&ip=${e.ip}');">编辑</a> 
										<a data-toggle="modal" data-target="#confirmModal" data-msg="您确认要删除[${e.ip}]吗？" data-href="javascript:ajaxLoadPage2Body('${pageContext.request.contextPath}/maintain/whitelist/delete/?whitelisttype=${e.white}&type=${e.type}&ip=${e.ip}');">删除</a>
									</td>
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
		$('#whitelist').DataTable({
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
	            title: '登录白名单'
	        },  {
	            extend: 'print',
	            text:'打印'
	        } ],
	      	//跟数组下标一样，第一列从0开始，这里表格初始化时，第四列默认降序
            "order": [[ 3, "desc" ]]
		});
	});
</script>