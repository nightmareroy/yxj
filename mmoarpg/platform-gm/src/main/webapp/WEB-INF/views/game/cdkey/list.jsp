<%@ page language="java" contentType="text/html;charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

<section class="content-header">
	<h1>礼包列表</h1>
	<ol class="breadcrumb">
		<li><a href="${pageContext.request.contextPath}/welcome/"><i class="fa fa-dashboard"></i> 首页</a></li>
		<li>游戏管理</li>
		<li>兑换码管理</li>
		<li class="active">礼包列表</li>
	</ol>
</section>
<section class="content">
	<div class="row">
		<div class="col-xs-12">
			<div class="box">
				<div class="box-header">
					<h3 class="box-title">
						<i class="fa fa-list"></i> 礼包列表
					</h3>
					<c:if test="${sessionScope.SESSION_USER.auth.have['MANAGE_CDKEY_CONFIG']}">
						<div class="box-tools input-group input-group-sm">
							<button type="button" class="btn btn-block btn-default btn-sm" onclick="javascript:ajaxLoadPage2Body('${pageContext.request.contextPath}/game/cdkey/add/ui/');">新增礼包</button>
						</div>
					</c:if>
				</div>
				<div class="box-body table-responsive no-padding">
					<table class="table table-hover">
						<tbody>
							<tr>
								<th>CDK编号</th>
								<th>CDK名称</th>
								<th>类型</th>
								<th>最小等级</th>
								<th>使用上限</th>
								<th>生成数量</th>
								<th>物品列表</th>
								<th>兑换开始时间</th>
								<th>兑换结束时间</th>
								<th>操作</th>
							</tr>
							<c:forEach var="e" items="${page.content}">
								<tr role="row" class="odd">
									<td>${e.id}</td>
									<td>${e.name}</td>
									<td>${e.type == 0 ? "普通" : "通用"}</td>
									<td>${e.minLevel}</td>
									<td>${e.useMax}</td>
									<td>${e.codeNum}</td>
									<td class="cdk-bonus" style="height:30px;display:block;overflow:hidden;">
										<div class="cdk-bonus-short"></div>
										<div class="cdk-bonus-info">${e.itemList}</div>
									</td>
									<td>${e.startTime}</td>
									<td>${e.endTime}</td>
									<td><a href="javascript:ajaxLoadPage2Body('${pageContext.request.contextPath}/game/cdkey/export/?id=${e.id}');">导出</a></td>
								</tr>
							</c:forEach>
						</tbody>
					</table>
				</div>
				<div class="box-footer clearfix">
					<jsp:include page="../../page.jsp">
						<jsp:param name="url" value="${pageContext.request.contextPath}/game/cdkey/list/" />
					</jsp:include>
				</div>
			</div>
		</div>
	</div>
</section>
<script>
	$('.cdk-bonus').each(function(i){
		var info = $(this).find('.cdk-bonus-info').html();
		var data = eval('('+info+')');
		var arr = new Array();
		for(var m=0;m<data.length;m++){
			arr.push(data[m].itemName+"("+data[m].itemId+")*" +data[m].itemNumber);
		}
		if(arr.length > 1){
			$(this).find('.cdk-bonus-short').html(arr[0] + "...<font color='#00a65a'>详细</font>");
		}else{
			$(this).find('.cdk-bonus-short').html(arr[0]);
		}
		var html = "";
		for(var m=0;m<arr.length;m++){
			html = html + "<p>" + arr[m] + "</p>";
		}
		$(this).find('.cdk-bonus-info').html(html);
	});
	$('.cdk-bonus').click(function(){
		if($(this).css("height") == "30px"){
			$(this).find('.cdk-bonus-short').hide();
			$(this).find('.cdk-bonus-info').show();
			$(this).css("height", "auto");
		}else{
			$(this).find('.cdk-bonus-short').show();
			$(this).find('.cdk-bonus-info').hide();
			$(this).css("height", "30px");
		}
	});
</script>