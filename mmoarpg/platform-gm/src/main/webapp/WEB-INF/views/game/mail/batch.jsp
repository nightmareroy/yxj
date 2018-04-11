<%@ page language="java" contentType="text/html;charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<section class="content-header">
	<h1>批量补偿</h1>
	<ol class="breadcrumb">
		<li><a href="${pageContext.request.contextPath}/welcome/"><i class="fa fa-dashboard"></i> 首页</a></li>
		<li>游戏管理</li>
		<li>福利管理</li>
		<li class="active">批量补偿</li>
	</ol>
</section>
<section class="content">
<c:choose>
	<c:when test="${empty page.content}">
		<div class="row">
			<div class="col-lg-12">
				<div class="panel panel-default">
					<div class="panel-heading">
						上传Excel文件
						<div class="pull-right">
							<a href="javascript:void(0)">下载模板</a>
						</div>
					</div>
					<div class="panel-body">
						<div id="uploadfile">
							<form id="uploadwelfarefile" enctype="multipart/form-data" method="post" action="${pageContext.request.contextPath}/game/welfare/batch/uploadfile/">
								<div class="form-group">
									<label>选择文件：</label> <input type="file" id="file" name="file">
								</div>
								<button type="submit" class="btn btn-primary btn-lg btn-block">上传</button>
							</form>
						</div>
						<div style="display: none;">
							<p>
								<strong>上传进度</strong> <span class="pull-right text-muted">100% Complete</span>
							</p>
							<div class="progress progress-striped active">
								<div id="progress" class="progress-bar progress-bar-success" role="progressbar"></div>
							</div>
						</div>
					</div>
				</div>
			</div>
		</div>

		<script type="text/javascript">
			$(document).ready(function() {
				$("#uploadwelfarefile").submit(function(evt) {
					evt.preventDefault();
					$("#uploadfile").hide().next().show();

					$("#uploadwelfarefile").ajaxSubmit({
						url : $(this).attr("action"),
						dataType : "text",
						type : "POST",
						uploadProgress : function(event, position, total, percentComplete) {
							$("#progress").css("width", percentComplete + "%");
						},
						success : function(data) {
							alert("上传成功");
							ajaxLoadPage2Body('${pageContext.request.contextPath}/game/welfare/batch/apply/');
						},
						error : function(error) {
							$("#uploadfile").parent().html(error);
						}
					});
				});
			});
		</script>
	</c:when>
	<c:otherwise>
		<div class="row">
			<div class="col-xs-12">
				<div class="box">
					<div class="box-header">
						<h3 class="box-title">
							<i class="fa fa-list"></i> 批量补偿列表
						</h3>
						<div class="box-tools input-group-sm">
							<button type="button" class="btn btn-danger btn-sm" data-toggle="modal" data-target="#confirmModal" data-msg="您确认要清空当前所有数据吗？只有清理后才可以上传新的数据..." data-href="javascript:ajaxLoadPage2Body('${pageContext.request.contextPath}/game/welfare/batch/clean/');">清空数据</button>
							<button type="button" class="btn btn-success btn-sm" data-toggle="modal" data-target="#confirmModal" data-msg="一共${page.totalElements}条补偿记录，您确认现在发送吗?" data-href="javascript:ajaxLoadPage2Body('${pageContext.request.contextPath}/game/welfare/batch/send/');">确认发送</button>
						</div>
					</div>
					<div class="box-body table-responsive">
						<table class="table table-hover" id="noticesection">
							<thead>
								<tr>
									<th>用户ID</th>
									<th>角色名称</th>
									<th>标题</th>
									<th>内容</th>
									<th>奖励</th>
									<th>修改时间</th>
									<th>状态</th>
								</tr>
							</thead>
							<tbody>
									<c:forEach var="e" items="${page.content}">
										<tr>
											<td>${e.userId }</td>
											<td>${e.name }</td>
											<td>${e.title }</td>
											<td>${e.content }</td>
											<td>${e.itemInfo }</td>
											<td>${e.modifyTime }</td>
											<td>
												<c:choose>
													<c:when test="${e.state == 0}">
														<button type="button" class="btn btn-danger btn-xs">物品异常</button>
													</c:when>
													<c:when test="${e.state == 1}">
														<button type="button" class="btn btn-warning btn-xs">发送失败</button>
													</c:when>
													<c:when test="${e.state == 2}">
														<button type="button" class="btn btn-warning btn-xs">待发送</button>
													</c:when>
													<c:otherwise>
														<button type="button" class="btn btn-success btn-xs">已发送</button>
													</c:otherwise>
												</c:choose>
											</td>
										</tr>
									</c:forEach>
								</tbody>
						</table>
					</div>
					<div class="box-footer clearfix">
						<jsp:include page="../../page.jsp">
							<jsp:param name="url" value="${pageContext.request.contextPath}/game/welfare/batch/apply/" />
						</jsp:include>
					</div>
				</div>
			</div>
		</div>
	</c:otherwise>
</c:choose>
</section>