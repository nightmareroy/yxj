<%@ page language="java" contentType="text/html;charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<section class="content-header">
	<h1>区服管理</h1>
	<ol class="breadcrumb">
		<li><a href="${pageContext.request.contextPath}/welcome/"><i class="fa fa-dashboard"></i> 首页</a></li>
		<li>运维工具</li>
		<li class="active">区服管理</li>
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
						<button type="button" class="btn btn-block btn-default btn-sm" onclick="javascript:ajaxLoadPage2Body('${pageContext.request.contextPath}/maintain/server/addUI/');">添加大区</button>
					</div>
				</div>
				<div class="box-body">
					<c:choose>
						<c:when test="${empty page.content}">没有找到记录</c:when>
						<c:otherwise>
							<table class="table table-bordered table-striped" id="server_list">
								<thead>
									<tr>
										<th>排序号（无用数值）</th>
										<th>大区编号</th>
										<th>区服编号</th>
										<th>区服名称</th>
										<th>链接地址</th>
										<th>状态</th>
										<th>开服日期</th>
										<th>在线人数</th>
										<th>是否新服</th>
										<th>是否热服</th>
										<th>是否推荐</th>
										<th>对外情况</th>
										<th>对外时间</th>
										<th>备注</th>
										<th>操作</th>
									</tr>
								</thead>
								<tbody>
									<c:forEach var="e" items="${page.content}">
										<tr role="row" class="odd">
											<c:choose>
												<c:when test="${e.areaId==0}">
													<td>${e.id * 1000000}</td>
													<td>-</td>
													<td>${e.id}</td>
													<td>${e.serverName}</td>
													<td></td>
													<td></td>
													<td></td>
													<td></td>
													<td></td>
													<td></td>
													<td></td>
													<td></td>
													<td></td>
													<td>${e.describe}</td>
												</c:when>
												<c:otherwise>
													<td>${e.areaId * 1000000 + e.id}</td>
													<td>${e.areaId}</td>
													<td>${e.id}</td>
													<td>${e.serverName}</td>
													<td>${e.ip}:${e.port}</td>
													<td>
														<c:choose>
															<c:when test="${e.state == 0}">
																<span class="label label-danger">维护</span>
															</c:when>
															<c:when test="${e.state == 1}">
																<span class="label label-success">流畅</span>
															</c:when>
															<c:when test="${e.state == 2}">
																<span class="label label-success">繁忙</span>
															</c:when>
															<c:otherwise>
																<span class="label label-warning">爆满</span>
															</c:otherwise>
														</c:choose>
													</td>
													<td><fmt:formatDate value="${e.openDate}" pattern="yyyy-MM-dd"/></td>
													<td>${e.olCount}/${e.olLimit}</td>
													<td><label><input name="server_state" value="1" id="${e.id}" type="checkbox" ${e.isNew?'checked':''} ${e.master>0?'disabled':''}> 新服</label></td>
													<td><label><input name="server_state" value="2" id="${e.id}" type="checkbox" ${e.isHot?'checked':''} ${e.master>0?'disabled':''}> 热服</label></td>
													<td><label><input name="server_state" value="3" id="${e.id}" type="checkbox" ${e.isRecommend?'checked':''} ${e.master>0?'disabled':''}> 推荐</label></td>
													<td>
														<c:choose>
															<c:when test="${e.showState == 0}">
																<span class="label label-danger">隐藏</span>
															</c:when>
															<c:when test="${e.showState == 2}">
																<span class="label label-success">对外</span>
															</c:when>
															<c:otherwise>
																<span class="label label-warning">对内</span>
															</c:otherwise>
														</c:choose>
													</td>
													<td><fmt:formatDate value="${e.externalTime}" pattern="yyyy-MM-dd HH:mm:ss"/></td>
													<td>${e.describe}</td>
												</c:otherwise>
											</c:choose>
											<td>
												<c:choose>
													<c:when test="${e.master == 0}">
														<a data-toggle="modal" data-target="#confirmModal" data-msg="您确认要编辑[${e.serverName}]吗？" data-href="javascript:ajaxLoadPage2Body('${pageContext.request.contextPath}/maintain/server/editUI/${e.id}/');">编辑</a>
														&nbsp;<a data-toggle="modal" data-target="#confirmModal" data-msg="您确认要删除[${e.serverName}]吗？" data-href="javascript:ajaxLoadPage2Body('${pageContext.request.contextPath}/maintain/server/delete/${e.id}/');">删除</a>
													</c:when>
													<c:otherwise>
														已合服(${e.master})
													</c:otherwise>
												</c:choose>
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
		// 控制发送状态
		$("input[name='server_state']").click(function() {
			$.get("${pageContext.request.contextPath}/maintain/server/update/?sid="+$(this).prop('id')+"&type="+$(this).val()+"&checked="+$(this).prop('checked'), function(result){});
		});
		
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
	            title: '区服信息'
	        },  {
	            extend: 'print',
	            text:'打印'
	        } ],
	      	//跟数组下标一样，第一列从0开始，这里表格初始化时，第四列默认降序
            "order": [[ 0, "asc" ]],
            "columnDefs": [
                {
                  "targets": [ 0 ],
                  "visible": false,
                  "searchable": false
                }
            ]
		});
	});
</script>