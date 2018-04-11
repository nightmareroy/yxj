<%@ page language="java" contentType="text/html;charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

<section class="content-header">
	<h1>登录公告</h1>
	<ol class="breadcrumb">
		<li><a href="${pageContext.request.contextPath}/welcome/"><i class="fa fa-dashboard"></i> 首页</a></li>
		<li>游戏管理</li>
		<li class="active">登录公告</li>
	</ol>
</section>
<section class="content">
	<div class="row">
		<div class="col-xs-12">
			<div class="box">
				<div class="box-header">
					<h3 class="box-title">
						<i class="fa fa-list"></i> 登录公告列表
					</h3>
					<div class="box-tools input-group input-group-sm">
						<button type="button" class="btn btn-block btn-default btn-sm" onclick="javascript:ajaxLoadPage2Body('${pageContext.request.contextPath}/game/login/notice/addUI/');">添加公告</button>
					</div>
				</div>
				<div class="box-body table-responsive">
					<table class="table table-hover" id="noticesection">
						<thead>
							<tr>
								<th>流水号</th>
								<th>标题</th>
								<th>内容(单击展示全部)</th>
								<th>状态</th>
								<th>发布人</th>
								<th>操作</th>
							</tr>
						</thead>
					</table>
				</div>
			</div>
		</div>
	</div>
</section>
<script>
	$(function() {
		 var contentHeight = '40px';
		 var table = $('#noticesection').DataTable({
			language : {//国际化文件
				url : "${pageContext.request.contextPath}/resources/plugins/datatables/i18n/zh_CN.json"
			},
	        order: [[ 0, "desc" ]],
	        ajax: "${pageContext.request.contextPath}/game/login/notice/query/",
	        dataSrc: '',
	        columns:[ { data: 'id' },
	                  { data: 'title' },
	                  { data: 'content', width:"600px"},
	                  { data: 'enable' },
	                  { data: function (json){return json.name+'('+json.username+')'; } }
	                  ],
            "columnDefs" : [ {
	          	"targets" : 2,//操作按钮目标列
	          	"data" : null,
	          	"render" : function(data, type, row) {
	          		var html = '<div class="notice_content" style="height:'+contentHeight+';overflow:hidden;cursor:pointer;">' + data + '</div>';
		          	return html;
	          	}
          	},{
	          	"targets" : 3,//操作按钮目标列
	          	"data" : null,
	          	"render" : function(data, type, row) {
	          		var html = "";
		          	if(data == 1){
		          		html = '<span class="label label-success">启用</span>';
		          	}else{
		          		html = '<span class="label label-danger">封存</span>';
		          	}
		          	return html;
	          	}
          	},{
	          	"targets" : 5,//操作按钮目标列
	          	"data" : null,
	          	"render" : function(data, type, row) {
	          		var html = "";
		          	html += '<a style="cursor:pointer;" data-toggle="modal" data-target="#confirmModal" data-msg="您确认要编辑['+data.title+']登录公告吗？" data-href="javascript:ajaxLoadPage2Body(\'${pageContext.request.contextPath}/game/login/notice/editUI/'+data.id+'/\');">编辑</a>';
		          	html += '&nbsp;<a style="cursor:pointer;" data-toggle="modal" data-target="#confirmModal" data-msg="您确认要启用['+data.title+']登录公告吗？" data-href="javascript:ajaxLoadPage2Body(\'${pageContext.request.contextPath}/game/login/notice/enable/'+data.id+'/\');">启用</a>';
		          	html += '&nbsp;<a style="cursor:pointer;" data-toggle="modal" data-target="#confirmModal" data-msg="您确认要删除['+data.title+']登录公告吗？" data-href="javascript:ajaxLoadPage2Body(\'${pageContext.request.contextPath}/game/login/notice/delete/'+data.id+'/\');">删除</a>';
		          	return html;
	          	}
          	} ],
		});
		$('#noticesection').on('click', '.notice_content', function(){
			if($(this).css("height") == contentHeight){
				$(this).css("height", "auto");
			}else{
				$(this).css("height", contentHeight);
			}
		});
	});
</script>