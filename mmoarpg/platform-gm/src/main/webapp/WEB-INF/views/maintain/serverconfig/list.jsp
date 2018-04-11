<%@ page language="java" contentType="text/html;charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<section class="content-header">
	<h1>区服配置中心</h1>
	<ol class="breadcrumb">
		<li><a href="${pageContext.request.contextPath}/welcome/"><i class="fa fa-dashboard"></i> 首页</a></li>
		<li>运维工具</li>
		<li class="active">区服配置中心</li>
	</ol>
</section>
<section class="content">
	<div class="row">
		<div class="col-xs-12">
			<div class="box">
				<div class="box-header">
					<h3 class="box-title">
						<i class="fa fa-list"></i> 区服列表
					</h3>
					<div class="box-tools input-group input-group-sm">
						<button type="button" class="btn btn-block btn-default btn-sm" onclick="javascript:ajaxLoadPage2Body('${pageContext.request.contextPath}/maintain/serverconfig/addUI/');">添加新区</button>
					</div>
				</div>
				<div class="box-body">
					<c:choose>
						<c:when test="${empty page}">没有找到记录</c:when>
						<c:otherwise>
							<table class="table table-bordered table-striped" id="server_list">
								<thead>
									<tr>
										<th>配置模板</th>
										<th>机器IP</th>
										<th>区服编号</th>
										<th>区服名称</th>
										<th>应用编号</th>
										<th>大区编号</th>
										<th>对外配置</th>
										<th>Redis配置</th>
										<th>Redis密码</th>
										<th>战斗服配置</th>
										<th>操作</th>
									</tr>
								</thead>
								<tbody>
									<c:forEach var="e" items="${page}">
										<tr role="row" class="odd">
											<td>${e.template}</td>
											<td>${e.gameHost}</td>
											<td>${e.id}</td>
											<td>${e.serverName}</td>
											<td>${e.appId}</td>
											<td>${e.areaId}</td>
											<td>${e.pubhost}:${e.port}</td>
											<td>${e.redisHost}:${e.redisPort} > ${e.redisIndex}</td>
											<td>${e.redisPassword}</td>
											<td>${e.battleHost}:${e.battleFastPort}/${e.battleIcePort}</td>
											<td>
												<a data-toggle="modal" data-target="#confirmModal" data-msg="您确认要编辑[${e.serverName}]吗？" data-href="javascript:ajaxLoadPage2Body('${pageContext.request.contextPath}/maintain/serverconfig/editUI/?id=${e.id}');">编辑</a>
												&nbsp;<a data-toggle="modal" data-target="#confirmModal" data-msg="您确认要删除[${e.serverName}]吗？" data-href="javascript:ajaxLoadPage2Body('${pageContext.request.contextPath}/maintain/serverconfig/delete/?id=${e.id}');">删除</a>
											</td>
										</tr>
									</c:forEach>
								</tbody>
							</table>
						</c:otherwise>
					</c:choose>
				</div>
			</div>
		</div>
	</div>
</section>

<script>
	$(function() {
		$('#server_list').DataTable({
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
	            title: '区服配置信息'
	        },  {
	            extend: 'print',
	            text:'打印'
	        } ],
	      	//跟数组下标一样，第一列从0开始，这里表格初始化时，第四列默认降序
            "order": [[ 1, "asc" ]],
		});
	});
</script>