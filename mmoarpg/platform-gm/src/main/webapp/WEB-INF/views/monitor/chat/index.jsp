<%@ page language="java" contentType="text/html;charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

<section class="content-header">
	<h1>聊天监控</h1>
	<ol class="breadcrumb">
		<li><a href="${pageContext.request.contextPath}/welcome/"><i class="fa fa-dashboard"></i> 首页</a></li>
		<li>运维工具</li>
		<li class="active">聊天监控</li>
	</ol>
</section>
<section class="content">
	<!-- 选服列表 -->
	<jsp:include page="../../serverlist.jsp">
		<jsp:param name="multiselect" value="false" />
		<jsp:param name="dosomething" value="javascript:selectServerByChat(this, true);" />
	</jsp:include>
	
	<div class="row" id="chat_monitor">
	</div>
</section>

<script type="text/javascript">
	var websocket;
	if(websocket != null){
		websocket.close();
	}
	if ('WebSocket' in window) {
		websocket = new WebSocket("ws://"+document.location.host+"${pageContext.request.contextPath}/chat/monitoring");
	} else if ('MozWebSocket' in window) {
		websocket = new MozWebSocket("ws://${pageContext.request.contextPath}/chat/monitoring");
	} else {
		websocket = new SockJS("http://"+document.location.host+"/${pageContext.request.contextPath}/sockjs/chat/monitoring");
	}
	websocket.onopen = function(evnt) {
		websocket.send('ChatMonitor');
	};
	
	var pauseArray = new Array(12);//
	websocket.onmessage = function(evnt) {
		var json = $.parseJSON(evnt.data);
		//服务器编号
		var sid = json.sid;
		if($("div[id='chat-div-" + sid + "']").length <= 0){
			return;
		}
		
		// 靠左边显示，还是右边...
		var left = ['', ' pull-left', ' pull-right'];
		if(!json.left){
			left = [' right', ' pull-right', ' pull-left'];
		}
		
		var nickname = htmlEncode(json.name);
		var content = htmlEncode(json.text);
		if(json.target){
			content = "对["+json.target+"]说："+content;
		}
		
		if(json.scope == 1) {
			content = "[世界]"+content;
		} else if(json.scope == 2){
			content = "[仙盟]"+content;
		} else if(json.scope == 3){
			content = "[队伍]"+content;
		} else if(json.scope == 4){
			content = "[私聊]"+content;
		} else if(json.scope == 5){
			content = "[跨服]"+content;
		} else if(json.scope == 6){
			content = "[道友]"+content;
		} else if(json.scope == 7){
			content = "[系统]"+content;
		} else if(json.scope == 8){
			content = "[喇叭]"+content;
		} else if(json.scope == 9){
			content = "[队伍喊话]"+content;
		}
		
		// 拼接聊天内容Div信息
		var div = '<div class="direct-chat-msg'+left[0]+'">'+//
					'<div class="direct-chat-info clearfix">'+//
						'<span class="direct-chat-name'+left[1]+'">'+nickname+' Lv:'+json.level+'(IP:'+json.ip+')</span>'+//
						'<span class="direct-chat-timestamp'+left[2]+'">'+datetimeFormat(json.date)+'</span>'+//
					'</div>'+//
					'<img class="direct-chat-img" src="${pageContext.request.contextPath}/resources/images/head/'+json.pro+'.png">'+//
					'<div class="direct-chat-text">'+content+' <a id="gag_'+json.id+'" data-toggle="modal" data-target="#confirmModal" data-msg="您确认要禁言['+nickname+'](Lv:'+json.level+')吗？" data-href="javascript:gag('+json.sid+',\''+json.id+'\',\''+json.name+'\',\''+content+'\');">禁言</a></div>'+//
				'</div>';//
		
		// 对目标频道的聊天DIV
		var ChatDiv = $('#chat-content-'+sid);
		ChatDiv.append(div).scrollTop(ChatDiv[0].scrollHeight);
		
		if(!json.left){//自动禁言也要把其他链接干掉
			$("a[id=gag_"+json.id+"]").each(function() {$(this).remove();});
		}
		
		// 删除过多的信息...
		var divCount = ChatDiv.find('.direct-chat-msg').length;
		var divMax = 32;
		if(divCount > divMax){
			ChatDiv.find('.direct-chat-msg:lt('+(divCount - divMax)+')').remove();
		}
	};
	websocket.onerror = function(evnt) {
	};
	websocket.onclose = function(evnt) {
	}
	$('.btn-pause').click(function(){
		if(pauseArray[$(this).attr("title")]){
			pauseArray[$(this).attr("title")] = false;
			$(this).html('暂停');
		}else{
			pauseArray[$(this).attr("title")] = true;
			$(this).html('恢复');
		}
	});
</script>
<script>
	var gag = function(serverId, playerId, playerName, reason){
		$.post('${pageContext.request.contextPath}/game/publish/mactive/', {"serverId":serverId,"playerId":playerId,"playerName":playerName,"reason":reason}, function(result){$("a[id=gag_"+playerId+"]").each(function() {$(this).remove();});});
	}
	
	var selectServerByChat = function(btn, multiselect) {
		selectServerId(btn, multiselect);

		// 如果存在目标服的监听，那就取消
		if ($("div[id='chat-div-" + $(btn).val() + "']").length > 0) {
			$("div[id='chat-div-" + $(btn).val() + "']").remove();
		}
		// 不存在那就监听新的服，添加一个框
		else {
			$("div[id='chat_monitor']").append(
					"<div class='col-md-4' id='chat-div-" + $(btn).val() + "'>"
					+"	<div class='box box-warning direct-chat direct-chat-warning'>"
					+"		<div class='box-header with-border'>"
					+"			<i class='fa fa-comments fa-fw'></i><h3 class='box-title'>" + $(btn).text() + "</h3>"
					+"			<div class='box-tools pull-right'>"
					+"				<button type='button' class='btn btn-box-tool' data-widget='collapse'>"
					+"					<i class='fa fa-minus'></i>"
					+"				</button>"
					+"				<button type='button' class='btn btn-box-tool' data-widget='remove'>"
					+"					<i class='fa fa-times'></i>"
					+"				</button>"
					+"			</div>"
					+"		</div>"
					+"		<div class='box-body'>"
					+"			<div id='chat-content-" + $(btn).val() + "' class='direct-chat-messages'></div>"
					+"		</div>"
					+"	</div>"
					+"</div>");
		}
	}
</script>