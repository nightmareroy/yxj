<%@ page language="java" contentType="text/html;charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

<section class="content-header">
	<h1>日志提取</h1>
	<ol class="breadcrumb">
		<li><a href="${pageContext.request.contextPath}/welcome/"><i class="fa fa-dashboard"></i> 首页</a></li>
		<li>运维工具</li>
		<li class="active">日志提取</li>
	</ol>
</section>
<section class="content">
	<div class="row">
		<div class="col-xs-12">
			<div class="box box-primary">
				<div class="box-header">
					<h3 class="box-title">
						<i class="fa fa-list"></i> 日志提取
					</h3>
				</div>
				<div class="box-body table-responsive">
					<div class="form-group">
	                  <label>请选择要提取的日志类型：</label>
	                  <select id="mlog_struct" class="form-control">
	                  </select>
	                </div>
	                <div class="form-group">
					  <label>查询日期:</label>
					  <div class="input-group">
					    <div class="input-group-addon">
					      <i class="fa fa-calendar"></i>
					    </div>
					    <input type="text" class="form-control pull-right" id="mlog_date">
					  </div>
					  <span class="help-block">时间跨度建议低于一个月</span>
					</div>
	                <div id="mlog_entry" class="form-group">
	                	<label>请选择要提取的日志元素：</label>
	                	<span class="help-block">选中的元素将出现在生成的文件中</span>
		            </div>
		            <div class="form-group">
	                	<label>备注：</label>
	                	<input id="file-note" type="text" class="form-control pull-right">
	                	<span class="help-block">区分所需日志是否已经生成过，防止反复提取相同日志，浪费资源</span>
		            </div>
					<button type="button" id="mlog_submit" class="btn btn-block btn-success">提交</button>
				</div>
			</div>
		</div>
	</div>
</section>
<section class="content">
	<div class="row">
		<div class="col-xs-12">
			<div class="box box-primary">
				<div class="box-header">
					<h3 class="box-title">
						<i class="fa fa-list"></i> 日志下载
					</h3>
				</div>
				<div class="box-body">
					<table class="table table-bordered table-striped" id="mlog_table">
						<thead>
							<tr>
								<th>唯一标识</th>
								<th>日志名称</th>
								<th>查询开始时间</th>
								<th>查询结束时间</th>
								<th>包含字段</th>
								<th>备注</th>
								<th>下载状态</th>
							</tr>
						</thead>
					</table>
					<div class="form-group">
						<button type="button" id="mlog_refresh" class="btn btn-block btn-success">刷新</button>
					</div>
				</div>
			</div>
		</div>
	</div>
</section>
<script>
	var mlog;
	$.post("${pageContext.request.contextPath}/maintain/log/mlog/",function(data){
		mlog = data;
		var html = "";
		for(var i in data) {
			html = html + "<option>"+i+" ("+data[i].desc+")"+"</option>";       	
		}
		$("#mlog_struct").html(html);
		onStructChange();
	});
	$("#mlog_struct").change(function(){
		onStructChange();
	});
	function onStructChange(){
		var html = "<label>请选择要提取的日志元素：</label><span class=\"help-block\">选中的元素将出现在生成的文件中</span>";
		var fields = mlog[$("#mlog_struct").val().split(' ')[0]].fields;
		for(var i=0;i<fields.length;i++){
			if(fields[i].name == "EventID"){
				html = html + '<div class="checkbox"><label><input type="checkbox" name="mlog_entry" value="'+fields[i].name+'">'+fields[i].name+'('+fields[i].desc+')'+'</label></div>';
			}else{
				html = html + '<div class="checkbox"><label><input type="checkbox" name="mlog_entry" checked value="'+fields[i].name+'">'+fields[i].name+'('+fields[i].desc+')'+'</label></div>';
			}
		}
		$("#mlog_entry").html(html);
	}
	$("#mlog_submit").click(function(){
		var entry = "";
		$("[name='mlog_entry']:checkbox:checked").each(function(i){
			if(i == 0){
				entry = $(this).val();
			}else{
				entry = entry + "::" + $(this).val();
			}
		});
		if(entry == ""){
			alert("至少选中一项日志元素");
			return false;
		}
		var struct = $("#mlog_struct").val().split(' ')[0];
		var date = $("#mlog_date").val().split(" - ");
		$.post("${pageContext.request.contextPath}/maintain/log/create/?struct="+struct+"&entry="+entry+"&start="+date[0]+"&end="+date[1]+"&note="+$('#file-note').val());
		ajaxLoadPage2Body("${pageContext.request.contextPath}/maintain/log/list/");
	});
	$('#mlog_date').daterangepicker({
		timePicker24Hour: true,
		maxDate: "${maxtime}",
        locale: {
            format: 'YYYY/MM/DD',
            applyLabel : '确定',  
            cancelLabel : '取消',  
            fromLabel : '起始时间',  
            toLabel : '结束时间',  
            customRangeLabel : '自定义起始日期',  
            daysOfWeek : [ '日', '一', '二', '三', '四', '五', '六' ],  
            monthNames : [ '一月', '二月', '三月', '四月', '五月', '六月', '七月', '八月', '九月', '十月', '十一月', '十二月' ]
        },
        ranges: {
            '今天': [moment(), moment()],
            '昨天': [moment().subtract(1, 'days'), moment().subtract(1, 'days')],
            '最近7天': [moment().subtract(6, 'days'), moment()],
            '最近30天': [moment().subtract(29, 'days'), moment()],
            '这个月': [moment().startOf('month'), moment().endOf('month')],
            '上个月': [moment().subtract(1, 'month').startOf('month'), moment().subtract(1, 'month').endOf('month')]
         }
	});
	
	var table = $('#mlog_table').DataTable({
		language : {//国际化文件
			url : "${pageContext.request.contextPath}/resources/plugins/datatables/i18n/zh_CN.json"
		},
        order: [[ 0, "desc" ]],
        ajax: "${pageContext.request.contextPath}/maintain/log/download/",
        dataSrc: '',
        columns:[ { data: 'id' },
                  { data: 'name' },
                  { data: 'start' },
                  { data: 'end' },
                  { data: 'entry' },
                  { data: 'note' },
                  { data: 'status' }
                  ],
		columnDefs: [{
			"targets" : 6,//操作按钮目标列
          	"data" : null,
          	"render" : function(data, type, row) {
          		var html = "";
	          	if(data == 0){
	          		html = '<a style="cursor:pointer;" data-toggle="modal" data-target="#confirmModal" data-msg="文件还在生成中，请耐心等待，如果长时间没有进展，请尝试重新生成，多次失败请联系管理员">文件生成中</a>';
	          	}else if(data == 1){
	          		html = '<a style="cursor:pointer;"data-toggle="modal" data-target="#confirmModal" data-msg="请再次确认是否要下载该日志文件" data-href="javascript:download(\'${pageContext.request.contextPath}/maintain/log/down/?id='+row.id+'&name='+row.name+'\');">下载</a>';
	          	}else{
	          		html = '<a style="cursor:pointer;" data-toggle="modal" data-target="#confirmModal" data-msg="文件生成失败，请删除重新提取，如果多次提取失败请联系管理员">生成失败</a>';
	          	}
	          	html = html + '&nbsp;<a style="cursor:pointer;" data-toggle="modal" data-target="#confirmModal" data-msg="请再次确认是否要删除日志文件" data-href="javascript:ajaxLoadPage2Body(\'${pageContext.request.contextPath}/maintain/log/del/?id='+row.id+'&name='+row.name+'\');">删除</a>';
	          	return html;
          	}
		}]
	});
	
	function download(url){
		window.location.href = url;
	}
	$("#mlog_refresh").click(function(){
		ajaxLoadPage2Body("${pageContext.request.contextPath}/maintain/log/list/");
	});
</script>
