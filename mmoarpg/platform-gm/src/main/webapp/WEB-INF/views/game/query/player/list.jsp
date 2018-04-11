<%@ page language="java" contentType="text/html;charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>

<section class="content-header">
	<h1>查询角色详情</h1>
	<ol class="breadcrumb">
		<li><a href="${pageContext.request.contextPath}/welcome/"><i class="fa fa-dashboard"></i> 首页</a></li>
		<li>游戏管理</li>
		<li class="active">查询角色详情</li>
	</ol>
</section>

<section class="content">

	<!-- 选服列表 -->
	<jsp:include page="../../../serverlist.jsp">
		<jsp:param name="multiselect" value="false" />
		<jsp:param name="dosomething" value="javascript:selectServerId(this, false);" />
	</jsp:include>

	<div class="box box-primary">
		<div class="box-header with-border">
			<h3 class="box-title">查询条件 :</h3>
		</div>
		
		<div class="box-body">
			<div class="checkbox">
            	<label>
					<input type="checkbox" id="vague" ${vague ? "checked" : ""}> 模糊查询
           		</label>
            </div>
                
			<div class="form-group">
				<label for="playername">角色名称 : <span class="text-red">*</span></label>
				<input id="playername" class="form-control" placeholder="请输入玩家的角色名称..."  value="${playername}" type="text">
			</div>
			<button id="btn_query_player" class="btn btn-primary btn-lg btn-block">查询</button>
		</div>
	</div>
	
	<jsp:include page="../../../error.jsp" />

	<c:if test="${not empty result}">
		<c:choose>
			<c:when test="${vague}">
				<div class="box">
					<div class="box-header with-border">
						<div class="box-header with-border">
							<h3 class="box-title">模糊查询的玩家列表</h3>
						</div>
						<div class="box-body">
							<table class="table table-bordered table-striped" id="player_list">
								<thead>
									<tr>
										<th>角色名称</th>
										<th>等级</th>
										<th>职业</th>
										<th>查看详情</th>
									</tr>
								</thead>
								<tbody>
								<c:forEach var="e" items="${result.rows}">
									<tr role="row" class="odd">
										<td>${e.name}</td>
										<td>${e.level}</td>
										<td>${e.pro}</td>
										<td><a href="javascript:ajaxLoadPage2Body('${pageContext.request.contextPath}/game/query/player/list/?serverId=${selectedServerId}&playername=${e.name}&vague=false');">查看</a></td>
									</tr>
								</c:forEach>
								</tbody>
							</table>
						</div>
					</div>
				</div>
			</c:when>
			<c:otherwise>
				<div class="box">
					<div class="box-header with-border">
						<div class="box-header with-border">
							<h3 class="box-title">基础信息</h3>
						</div>
						<div class="box-body">
							<table class="table table-bordered">
								<tbody>
									<tr>
										<th>服务器</th>
										<td></td>
										<th>用户名</th>
										<td>${result.username}</td>
										<th>角色名称</th>
										<td>${result.name}</td>
										<th>角色ID</th>
										<td>${result.id}</td>
									</tr>
									<tr>
										<th>渠道名</th>
										<td></td>
										<th>角色状态</th>
										<td>${result.roleState}</td>
										<th>是否在线</th>
										<td>${result.isOnline}</td>
										<th>战斗力</th>
										<td>${result.fightPower}</td>
									</tr>
									<tr>
										<th>职业</th>
										<td>${result.pro}</td>
										<th>银两</th>
										<td>${result.gold}</td>
										<th>VIP类型</th>
										<td>${result.vip}</td>
										<th>最后登录时间</th>
										<td>${result.loginTime}</td>
									</tr>
									<tr>
										<th>元宝</th>
										<td>${result.diamond}</td>
										<th>等级</th>
										<td>${result.level}</td>
										<th>经验</th>
										<td>${result.exp}</td>
										<th>下线时间</th>
										<td>${result.logoutTime}</td>
									</tr>
									<tr>
										<th>道友团队</th>
										<td>${result.daoyouName}</td>
										<th>所属仙盟</th>
										<td>${result.guildName}</td>
										<th>修为</th>
										<td>${result.classExp}</td>
										<th>绑定元宝</th>
										<td>${result.ticket}</td>
									</tr>
								</tbody>
							</table>
						</div>
					</div>
				</div>
				
				<!-- 穿戴信息，背包，坐骑，宠物什么的.... -->
				<div class="box box-solid">
					<div class="box-header with-border">
		      			<h3 class="box-title">附加信息</h3>
		            </div>
           			<div class="box-body">
		              <div class="box-group" id="accordion">
		                <div class="panel box box-primary">
		                  <div class="box-header with-border">
		               		<h4 class="box-title"><a data-toggle="collapse" data-parent="#accordion" href="#collapseOne">穿戴装备</a></h4>
		                  </div>
		                  <div id="collapseOne" class="panel-collapse collapse"><div class="box-body"></div></div>
		                </div>
		                <div class="panel box box-primary">
		                  <div class="box-header with-border">
		                    <h4 class="box-title"><a data-toggle="collapse" data-parent="#accordion" href="#collapseTwo">背包物品</a></h4>
		                  </div>
		                  <div id="collapseTwo" class="panel-collapse collapse"><div class="box-body"></div></div>
		                </div>
		                <div class="panel box box-primary">
		                  <div class="box-header with-border">
		                    <h4 class="box-title"><a data-toggle="collapse" data-parent="#accordion" href="#collapseThree">仓库物品</a></h4>
		                  </div>
		                  <div id="collapseThree" class="panel-collapse collapse"><div class="box-body"></div></div>
		                </div>
		                <div class="panel box box-primary">
		                  <div class="box-header with-border">
		                    <h4 class="box-title"><a data-toggle="collapse" data-parent="#accordion" href="#collapse_rank">个人排名</a></h4>
		                  </div>
		                  <div id="collapse_rank" class="panel-collapse collapse"><div class="box-body"></div></div>
		                </div>
		                <div class="panel box box-primary">
		                  <div class="box-header with-border">
		                    <h4 class="box-title"><a data-toggle="collapse" data-parent="#accordion" href="#collapse_skill">技能信息</a></h4>
		                  </div>
		                  <div id="collapse_skill" class="panel-collapse collapse"><div class="box-body"></div></div>
		                </div>
		                <div class="panel box box-primary">
		                  <div class="box-header with-border">
		                    <h4 class="box-title"><a data-toggle="collapse" data-parent="#accordion" href="#collapse_mount">坐骑信息</a></h4>
		                  </div>
		                  <div id="collapse_mount" class="panel-collapse collapse"><div class="box-body"></div></div>
		                </div>
		                <div class="panel box box-primary">
		                  <div class="box-header with-border">
		                    <h4 class="box-title"><a data-toggle="collapse" data-parent="#accordion" href="#collapse_pet">宠物信息</a></h4>
		                  </div>
		                  <div id="collapse_pet" class="panel-collapse collapse"><div class="box-body"></div></div>
		                </div>
		              </div>
		            </div>
		         </div>
			</c:otherwise>
		</c:choose>
	</c:if>
</section>

<script>
	$(function() {
		registerFocusClearErrorMsg();
		
		$("#btn_query_player").click(function(){
			var serverIds = [];
			$("button[type=button][name=server]").each(function() {
				if (!$(this).hasClass("btn-default")) {
					serverIds.push($(this).val());
				}
			});
			if (serverIds.length == 0) {
				addErrorMsg($("#server_list"), "请先选择目标服务器....");
				return false; // 阻止表单自动提交事件
			}
			
			var playername = $("#playername");
			if (playername.val().length == 0) {
				addErrorMsg(playername, "请输入玩家的角色名称...");
				return false;
			}
			
			var serverId = serverIds[0];
			ajaxLoadPage2Body('${pageContext.request.contextPath}/game/query/player/list/?serverId='+serverId+'&playername='+playername.val()+"&vague="+$("#vague").is(':checked'));
		});
	});
</script>

<c:choose>
	<c:when test="${vague}">
<script>
	$(function() {		
		$('#player_list').DataTable({
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
	            title: '模糊查询记录_${playername}'
	        },  {
	            extend: 'print',
	            text:'打印'
	        } ],
	      	//跟数组下标一样，第一列从0开始，这里表格初始化时，第四列默认降序
            "order": [[ 2, "desc" ]]
		});
	});
</script>
	</c:when>
	<c:otherwise>
<script>
	$(function() {		
		// 穿戴装备
		$('#collapseOne').on('show.bs.collapse', function () {
			var self = $(this).children("div");
			$.get("${pageContext.request.contextPath}/game/query/player/bag/item/?serverId=${selectedServerId}&playerId=${result.id}&type=1", function(result){
				self.html(result);
			});
		});
		// 背包
		$('#collapseTwo').on('show.bs.collapse', function () {
			var self = $(this).children("div");
			$.get("${pageContext.request.contextPath}/game/query/player/bag/item/?serverId=${selectedServerId}&playerId=${result.id}&type=0", function(result){
				self.html(result);
			});
		});
		// 仓库
		$('#collapseThree').on('show.bs.collapse', function () {
			var self = $(this).children("div");
			$.get("${pageContext.request.contextPath}/game/query/player/bag/item/?serverId=${selectedServerId}&playerId=${result.id}&type=2", function(result){
				self.html(result);
			});
		});
		// 个人排名
		$('#collapse_rank').on('show.bs.collapse', function () {
			var self = $(this).children("div");
			$.get("${pageContext.request.contextPath}/game/query/player/query/rank/?serverId=${selectedServerId}&playerId=${result.id}", function(result){
				self.html(result);
			});
		});
		// 技能
		$('#collapse_skill').on('show.bs.collapse', function () {
			var self = $(this).children("div");
			$.get("${pageContext.request.contextPath}/game/query/player/query/skill/?serverId=${selectedServerId}&playerId=${result.id}", function(result){
				self.html(result);
			});
		});
		// 坐骑
		$('#collapse_mount').on('show.bs.collapse', function () {
			var self = $(this).children("div");
			$.get("${pageContext.request.contextPath}/game/query/player/query/mount/?serverId=${selectedServerId}&playerId=${result.id}", function(result){
				self.html(result);
			});
		});
		// 宠物
		$('#collapse_pet').on('show.bs.collapse', function () {
			var self = $(this).children("div");
			$.get("${pageContext.request.contextPath}/game/query/player/query/pet/?serverId=${selectedServerId}&playerId=${result.id}", function(result){
				self.html(result);
			});
		});
	});
</script>	
	</c:otherwise>
</c:choose>
