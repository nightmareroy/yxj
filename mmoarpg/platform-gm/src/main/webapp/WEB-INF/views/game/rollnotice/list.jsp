<%@ page language="java" contentType="text/html;charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

<section class="content-header">
	<h1>滚动公告</h1>
	<ol class="breadcrumb">
		<li><a href="${pageContext.request.contextPath}/welcome/"><i class="fa fa-dashboard"></i> 首页</a></li>
		<li>游戏管理</li>
		<li class="active">滚动公告</li>
	</ol>
</section>
<section class="content">
	<div class="row">
		<div class="col-xs-12">
			<div class="box">
				<div class="box-header">
					<h3 class="box-title">
						<i class="fa fa-list"></i> 滚动公告列表
					</h3>
					<div class="box-tools input-group input-group-sm">
						<button type="button" class="btn btn-block btn-default btn-sm" onclick="javascript:ajaxLoadPage2Body('${pageContext.request.contextPath}/game/roll/notice/addUI/');">添加公告</button>
					</div>
				</div>
				<div class="box-body table-responsive">
					<table class="table table-hover" id="noticesection">
						<thead>
							<tr>
								<th>流水号</th>
								<th>区服</th>
								<th>开始时间</th>
								<th>结束时间</th>
								<th>时间间隔</th>
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
		 var contentHeight = '20px';
		 var table = $('#noticesection').DataTable({
			language : {//国际化文件
				url : "${pageContext.request.contextPath}/resources/plugins/datatables/i18n/zh_CN.json"
			},
	        order: [[ 0, "desc" ]],
	        ajax: "${pageContext.request.contextPath}/game/roll/notice/query/",
	        dataSrc: '',
	        columns:[ 
	        			{ data: 'id' },
	        			{ data: 'sids' },
	        			{ data: 'startTime' },
	        	  	  { data: 'endTime' },
	        		  { data: 'interval' },
	        		  { data: 'content'},
	        		  { data: ''},
	                  { data: function (json){return json.name+'('+json.username+')'; } },
	                  ],
            "columnDefs" : [ 
            {
       	         "targets" : 1,
       	         "data" : null,
       	         "render" : function(data, type, row) {
       	        	return '<button type="button" class="btn btn-box-tool" data-toggle="tooltip" title="" data-widget="chat-pane-toggle" data-original-title="'+data+'">数量:'+row['sidsLength']+'</button>';
 	          	}
            },{
   	          	"targets" : 2,//开始时间
   	          	"data" : null,
   	          	"render" : function(data, type, row) {
   		          	return new Date(data).format('yyyy-MM-dd hh:mm:ss');
   	          	}
            },{
   	          	"targets" : 3,//结束时间
   	          	"data" : null,
   	          	"render" : function(data, type, row) {
   		          	return new Date(data).format('yyyy-MM-dd hh:mm:ss');
   	          	}
            },{
   	          	"targets" : 4,//间隔时间
   	          	"data" : null,
   	          	"render" : function(data, type, row) {
   		          	return data + "分钟";
   	          	}
            },{
	          	"targets" : 5,//内容
	          	"data" : null,
	          	"render" : function(data, type, row) {
		          	var shortMsg = data.substring(0, 10) + "...";
		          	html = '<div class="short-msg" style="cursor:pointer;">'+shortMsg+'</div>' + '<div class="full-msg" style="display:none;cursor:pointer;">'+data+'</div>';
	          		return html;
	          	}
          	},{
	          	"targets" : 6,//跑马灯状态
	          	"data" : null,
	          	"render" : function(data, type, row) {
	          		var html = "";
	          		var startTime = row['startTime'];
	          		var endTime = row['endTime'];
	          		var now = new Date();
	          		if(now.getTime() < startTime){
	          			html = '<span class="label label-warning">未开始</span>';
	          		}else if(now.getTime() > endTime){
	          			html = '<span class="label label-danger">已结束</span>';
	          		}else{
	          			html = '<span class="label label-success">轮播中</span>';
	          		}
		          	return html;
	          	}
          	},{
	          	"targets" : 8,//操作按钮目标列
	          	"data" : null,
	          	"render" : function(data, type, row) {
	          		var html = "";
		          	html += '<a style="cursor:pointer;" data-toggle="modal" data-target="#confirmModal" data-msg="您确认要编辑流水号为['+data.id+']的滚动公告吗？" data-href="javascript:ajaxLoadPage2Body(\'${pageContext.request.contextPath}/game/roll/notice/editUI/'+data.id+'/\');">编辑</a>';
		          	html += '&nbsp;<a style="cursor:pointer;" data-toggle="modal" data-target="#confirmModal" data-msg="您确认要删除流水号为['+data.id+']的滚动公告吗？" data-href="javascript:ajaxLoadPage2Body(\'${pageContext.request.contextPath}/game/roll/notice/delete/'+data.id+'/\');">删除</a>';
		          	return html;
	          	}
          	} ],
		});
		$('#noticesection').on('click', '.short-msg', function(){
			$(this).hide();
			$(this).siblings('.full-msg').show();
		});
		$('#noticesection').on('click', '.full-msg', function(){
			$(this).hide();
			$(this).siblings('.short-msg').show();
		});
	});
</script>
<script>
Date.prototype.format = function(format) {
    var date = {
           "M+": this.getMonth() + 1,
           "d+": this.getDate(),
           "h+": this.getHours(),
           "m+": this.getMinutes(),
           "s+": this.getSeconds(),
           "q+": Math.floor((this.getMonth() + 3) / 3),
           "S+": this.getMilliseconds()
    };
    if (/(y+)/i.test(format)) {
           format = format.replace(RegExp.$1, (this.getFullYear() + '').substr(4 - RegExp.$1.length));
    }
    for (var k in date) {
           if (new RegExp("(" + k + ")").test(format)) {
                  format = format.replace(RegExp.$1, RegExp.$1.length == 1
                         ? date[k] : ("00" + date[k]).substr(("" + date[k]).length));
           }
    }
    return format;
}
</script>