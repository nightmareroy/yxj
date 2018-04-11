<%@ page language="java" contentType="text/html;charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

<section class="content-header">
	<h1>反广告配置</h1>
	<ol class="breadcrumb">
		<li><a href="${pageContext.request.contextPath}/welcome/"><i class="fa fa-dashboard"></i> 首页</a></li>
		<li>运维工具</li>
		<li class="active">反广告配置</li>
	</ol>
</section>
<section class="content">
	<div class="row">
		<div class="col-xs-12">
			<div class="box box-primary">
				<div class="box-header">
					<h3 class="box-title">
						<i class="fa fa-list"></i> 反广告配置
					</h3>
				</div>
				<div class="box-body table-responsive">
					<div class="form-group">
						<label>当前反广告配置状态</label>
						<p>
						<label><input type="radio" name="status" class="minimal status-btn" <c:if test="${status == 1}"> checked</c:if>>启用中&nbsp;</label>
						<label><input type="radio" name="status" class="minimal status-btn" <c:if test="${status == 0}"> checked</c:if>>未启用</label>
		            </div>
		            <div class="form-group">
	                	<div class="input-group input-group-sm">
	                  		<input id="add-text" type="text" class="form-control" placeholder="添加一个新的屏蔽词">
						      <span class="input-group-btn">
						        <button id="add-btn" type="button" class="btn btn-info btn-flat">添加</button>
						      </span>
						</div>
	                </div>
	                <div id="add-error" style="display:none" class="alert alert-danger alert-dismissible">
		                <button type="button" class="close" data-dismiss="alert" aria-hidden="true">×</button>
		                <h4><i class="icon fa fa-ban"></i>警告</h4>
		               	 <div id="add-error-info">添加敏感词失败，请尝试重新添加或联系管理员</div>
		            </div>
	                <label>当前屏蔽词</label>
	                <table id="chat-forbid-table" class="table table-bordered">
	                	<thead><tr><td>屏蔽词</td><td>屏蔽词</td><td>屏蔽词</td></tr></thead>
	                	<tbody>
	                	</tbody>
	                </table>
				</div>
			</div>
		</div>
	</div>
</section>
<script>
	$.post("${pageContext.request.contextPath}/maintain/mchat/get/", function(result){
		var html = "<tr>";
		for(var i = 0;i<result.length;i++){
			var label = "<div class='chat-id' style='display:none'>"+result[i].id+"</div><font color='#D4D4D4'>"+new Date(result[i].createTime).format('yyyy-MM-dd')+"</font>" + result[i].value + "<font color='#D4D4D4'>("+result[i].name+")</font>" + '<i class="fa fa-fw fa-remove del-btn" style="cursor:pointer"></i>';
			html += "<td>" + label + "</td>";
			if(i != 0 && (i+1)%3 == 0 && (i+1) != result.length){
				html += "</tr><tr>";
			}
		}
		if(result.length % 3 == 1) html += "<td></td><td></td>";
		if(result.length % 3 == 2) html += "<td></td>";
		html += "</tr>";
		$('#chat-forbid-table tbody').html(html);
		var tables = $('#chat-forbid-table').DataTable({
			language : {//国际化文件
				url : "${pageContext.request.contextPath}/resources/plugins/datatables/i18n/zh_CN.json"
			}		
		});
	});
	$('#add-btn').click(function(){
		var text = $('#add-text').val();
		$.post("${pageContext.request.contextPath}/maintain/mchat/add/", {"content":text}, function(result){
			if(result == 1){
				ajaxLoadPage2Body("${pageContext.request.contextPath}/maintain/mchat/list/");
			}else{
				$('#add-error').show();
				$('#add-error-info').html("添加失败，请确认该敏感词是否已存在");
			}
		});
	});
	
	$('.status-btn').click(function(){
		var index = $('.status-btn').index($(this));
		$.post("${pageContext.request.contextPath}/maintain/mchat/statusup/", {"status":index==0?1:0});
	});
	$('table').on("click", '.del-btn', function(){
		var id = $(this).siblings('.chat-id').html();
		$.post("${pageContext.request.contextPath}/maintain/mchat/del/", {"id":id}, function(){
			ajaxLoadPage2Body("${pageContext.request.contextPath}/maintain/mchat/list/");
		});
	});
	
</script>
<script>
	Date.prototype.format = function(fmt) { 
	    var o = { 
	       "M+" : this.getMonth()+1,                 //月份 
	       "d+" : this.getDate(),                    //日 
	       "h+" : this.getHours(),                   //小时 
	       "m+" : this.getMinutes(),                 //分 
	       "s+" : this.getSeconds(),                 //秒 
	       "q+" : Math.floor((this.getMonth()+3)/3), //季度 
	       "S"  : this.getMilliseconds()             //毫秒 
	   }; 
	   if(/(y+)/.test(fmt)) {
	           fmt=fmt.replace(RegExp.$1, (this.getFullYear()+"").substr(4 - RegExp.$1.length)); 
	   }
	    for(var k in o) {
	       if(new RegExp("("+ k +")").test(fmt)){
	            fmt = fmt.replace(RegExp.$1, (RegExp.$1.length==1) ? (o[k]) : (("00"+ o[k]).substr((""+ o[k]).length)));
	        }
	    }
	   return fmt; 
	}  
</script>
