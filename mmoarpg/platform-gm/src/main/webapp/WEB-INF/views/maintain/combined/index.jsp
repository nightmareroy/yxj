<%@ page language="java" contentType="text/html;charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>

<section class="content-header">
	<h1>合服操作</h1>
	<ol class="breadcrumb">
		<li><a href="${pageContext.request.contextPath}/welcome/"><i class="fa fa-dashboard"></i> 首页</a></li>
		<li>运维工具</li>
		<li class="active">合服操作</li>
	</ol>
</section>
<section class="content">
	<div class="row">
		<div class="col-xs-12">
			<div class="box box-primary">
				<div class="box-header">
					<h3 class="box-title">合服配置</h3>
				</div>
				<div class="box-body">
					<form action="${pageContext.request.contextPath}/maintain/combined/locking/" method="post" id="lockingServerList">
						<div class="form-group">
							<label for="authgroup">被合并服: <span class="text-red">*</span></label>
							<select class="form-control select2" id="form" multiple="multiple" data-placeholder="请选择一个区服...">
								<c:forEach var="e" items="${server_list}">
									<option value="${e.id}">${e.serverName}(${e.id})</option>
								</c:forEach>
							</select>
							<p class="help-block">将要被合并掉的区服...</p>
						</div>
						<div class="form-group">
							<label for="authgroup">合并到: <span class="text-red">*</span></label> 
							<select class="form-control select2" id="to">
								<c:forEach var="e" items="${server_list}">
									<option value="${e.id}">${e.serverName}(${e.id})</option>
								</c:forEach>
							</select>
							<p class="help-block">合并到的目标区服...</p>
						</div>
						<button type="submit" id="btn_locking" class="btn btn-primary btn-lg btn-block">锁定区服</button>
					</form>
				</div>
			</div>
		</div>
	</div>
	
	<!-- 合服执行过程... -->
	<div id="combined_result" class="row" style="display: none;">
        <div class="col-md-6">
			<ul class="timeline">
				<li class="time-label"><span class="bg-red">合服准备</span></li>
            	<li>
              		<i class="fa bg-blue">1</i>
              		<div class="timeline-item">
                		<span class="time"><i class="fa fa-clock-o"></i> 大概需要10分钟</span>
                		<h3 class="timeline-header"><a>预处理</a></h3>
		                <div class="timeline-body">
		                	踢人停机存盘重启，确认数据已保存
		                </div>
                		<div class="timeline-footer">
		                  <a class="btn btn-primary btn-xs" data-toggle="modal" data-target="#confirmModal" data-msg="合并操作预处理-对内" data-href="javascript:setServerInternal();">对内</a>
		                  <a class="btn btn-primary btn-xs" data-toggle="modal" data-target="#confirmModal" data-msg="合并操作预处理-踢人" data-href="javascript:kickall();">踢人</a>
		                  <a class="btn btn-danger btn-xs" data-toggle="modal" data-target="#confirmModal" data-msg="合并操作预处理-重启" data-href="javascript:restart();">重启</a>
		                  <a class="btn btn-primary btn-xs" data-toggle="modal" data-target="#confirmModal" data-msg="合并操作预处理-备份" data-href="javascript:BackupRedis();">备份</a>
                		</div>
              		</div>
            	</li>
            	<li>
              		<i class="fa bg-blue">2</i>
              		<div class="timeline-item">
                		<span class="time"><i class="fa fa-clock-o"></i> 大概需要8分钟</span>
                		<h3 class="timeline-header"><a>删小号</a></h3>
		                <div class="timeline-body">
		                	删除一些小号，以便节约服务器资源
		                </div>
                		<div class="timeline-footer">
		                  <a class="btn btn-primary btn-xs" data-toggle="modal" data-target="#confirmModal" data-msg="合并操作预处理-删小号" data-href="javascript:deletePlayer();">开始删除</a>
                		</div>
              		</div>
            	</li>
            	<li>
              		<i class="fa bg-blue">3</i>
              		<div class="timeline-item">
                		<span class="time"><i class="fa fa-clock-o"></i> 大概需要5分钟</span>
                		<h3 class="timeline-header"><a>第二次备份</a></h3>
		                <div class="timeline-body">
		                	删除小号后的备份
		                </div>
                		<div class="timeline-footer">
		                  <a class="btn btn-primary btn-xs" data-toggle="modal" data-target="#confirmModal" data-msg="合并操作预处理-第二次备份" data-href="javascript:BackupRedis();">再次备份</a>
		                  <a class="btn btn-danger btn-xs" data-toggle="modal" data-target="#confirmModal" data-msg="合并操作预处理-停服" data-href="javascript:stop();">停服</a>
                		</div>
              		</div>
            	</li>
            	
            	<li class="time-label"><span class="bg-green">合服开始</span></li>
            	
            	<li>
              		<i class="fa bg-purple">4</i>
             		<div class="timeline-item">
                		<span class="time"><i class="fa fa-clock-o"></i> 大概需要5分钟</span>
                		<h3 class="timeline-header"><a>开始合并</a></h3>
		                <div class="timeline-body">
		                	迁移数据的过程...
		                </div>
                		<div class="timeline-footer">
		                  <a class="btn btn-primary btn-xs" data-toggle="modal" data-target="#confirmModal" data-msg="合并操作预处理-开始合并" data-href="javascript:merge();">开始</a>
                		</div>
              		</div>
            	</li>
	            <li>
	            	<i class="fa fa-clock-o bg-green"></i>
	            	<div class="timeline-item">
                		<h3 class="timeline-header"><a>合并完成</a></h3>
                		<div class="timeline-body">
		                	GG啦...
		                </div>
                		<div class="timeline-footer">
		                	<a class="btn btn-primary btn-xs" data-toggle="modal" data-target="#confirmModal" data-msg="修正区服配置" data-href="javascript:gameover();">修正区服配置</a>
		                	<a class="btn btn-danger btn-xs" data-toggle="modal" data-target="#confirmModal" data-msg="重启" data-href="javascript:restart();">重启</a>
                		</div>
              		</div>
	            </li>
      		</ul>
         </div>
         <div class="col-md-6">
         	<div class="box box-warning">
				<div class="box-header">
					<h3 class="box-title">合服控制台</h3>
				</div>
				<div class="box-body" id="exec_result" style="height: 750px;">
		        </div>
			</div>
         </div>
      </div>
</section>
<script type="text/javascript">
	$(".select2").select2();
	registerFocusClearErrorMsg();
	
	// 对内
	function setServerInternal(){
		$.post("${pageContext.request.contextPath}/maintain/combined/internal/?form="+$("#form").val()+"&to="+$("#to").val(),function(result){});
	}
	// kickall
	function kickall(){
		$.post("${pageContext.request.contextPath}/maintain/combined/kickall/?form="+$("#form").val()+"&to="+$("#to").val(),function(result){});
	}
	// restart
	function restart(){
		$.post("${pageContext.request.contextPath}/maintain/combined/restart/?form="+$("#form").val()+"&to="+$("#to").val(),function(result){});
	}
	// BackupRedis
	function BackupRedis(){
		$.post("${pageContext.request.contextPath}/maintain/combined/BackupRedis/?form="+$("#form").val()+"&to="+$("#to").val(),function(result){});
	}
	// 删小号
	function deletePlayer(){
		$.post("${pageContext.request.contextPath}/maintain/combined/deletePlayer/?form="+$("#form").val()+"&to="+$("#to").val(),function(result){});
	}
	// 合并
	function merge(){
		$.post("${pageContext.request.contextPath}/maintain/combined/merge/?form="+$("#form").val()+"&to="+$("#to").val(),function(result){});
	}
	// stop
	function stop(){
		$.post("${pageContext.request.contextPath}/maintain/combined/stop/?form="+$("#form").val()+"&to="+$("#to").val(),function(result){});
	}
	//gameover
	function gameover(){
		$.post("${pageContext.request.contextPath}/maintain/combined/gameover/?form="+$("#form").val()+"&to="+$("#to").val(),function(result){});
	}
	$("#lockingServerList").submit(function(evt) {
		evt.preventDefault();
		
		var form = $("#form");
		if (form.val().length == '') {
			addErrorMsg(form, "请选择将要被合并掉的区服...");
			return false;
		}
		
		var to = $("#to");
		if (to.val().length == '') {
			addErrorMsg(to, "请选择合并到的目标区服...");
			return false;
		}
		
		if(form.val() == to.val()){
			addErrorMsg(to, "两个区服一样哎,合并他干嘛...");
			return false;
		}
		
		var $btn = $("#btn_locking").button('loading');
		
		$.post($(this).attr("action"), {
			"form" : form.val(),
			"to" : to.val(),
		}, function(result) {
			if(result == 'OK'){
				form.attr("disabled","disabled"); 
				to.attr("disabled","disabled");
				$("#combined_result").show();
			}
		});
	});
</script>
<script type="text/javascript">
	var websocket;
	if(websocket != null){
		websocket.close();
	}
	if ('WebSocket' in window) {
		websocket = new WebSocket("ws://"+document.location.host+"${pageContext.request.contextPath}/package/updateing");
	} else if ('MozWebSocket' in window) {
		websocket = new MozWebSocket("ws://${pageContext.request.contextPath}/package/updateing");
	} else {
		websocket = new SockJS("http://"+document.location.host+"/${pageContext.request.contextPath}/sockjs/package/updateing");
	}
	
	websocket.onmessage = function(evnt) {
		var json = $.parseJSON(evnt.data);
		var log = json.log;
		if(json.level == 'error'){
			log ="<span class='text-red'>"+log+"</span>";
		}
		var ResultDiv = $('#exec_result');
		ResultDiv.append("<dev class='log-msg'>"+ log + "<br/></dev>").scrollTop(ResultDiv[0].scrollHeight);
		
		
		// 删除过多的信息...
		var divCount = ResultDiv.find('.log-msg').length;
		var divMax = 36;
		if(divCount > divMax){
			ResultDiv.find('.log-msg:lt('+(divCount - divMax)+')').remove();
		}
	};
	websocket.onerror = function(evnt) {
	};
	websocket.onclose = function(evnt) {
	}
</script>