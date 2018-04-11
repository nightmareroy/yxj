<%@ page language="java" contentType="text/html;charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<section class="content-header">
	<h1>
		今日实时充值
	</h1>
	<ol class="breadcrumb">
		<li><a href="${pageContext.request.contextPath}/welcome/"><i class="fa fa-dashboard"></i> 首页</a></li>
		<li>监控系统</li>
		<li class="active">今日实时充值</li>
	</ol>
</section>
<section class="content">
	<div class="row">
		<div class="col-xs-12">
			<div class="panel-body" style="padding:0px; padding-bottom:20px;">
				<div style="font: bold 12px Arial, Helvetica, sans-serif; margin: 0; padding: 0; min-width: 600px; color: #bbbbbb;">
					<div style="background: #202020; margin: 0 auto; padding: 30px; border: 1px solid #333;">
						<div style="font-family: 'BebasNeueRegular', Arial, Helvetica, sans-serif; font-size: 36px; text-align: center; text-shadow: 0 0 5px #00c6ff;"></div>
						<ul style="margin: 0 auto; padding: 0px; list-style: none; text-align: center;">
							<li id="realtime-recharge-day" style="display: inline; font-size: 10em; text-align: center; font-family: 'BebasNeueRegular', Arial, Helvetica, sans-serif; text-shadow: 0 0 5px #00c6ff; color: #FCFCFC;">Loading...</li>
						</ul>
					</div>
				</div>
			</div>
		</div>
	</div>
</section>
<script>
	var request;//申明一个句柄
	if(request != null)
	  request.abort();
	
	function updateRecharge(cache) {
		request =$.get('${pageContext.request.contextPath}/monitor/charge/query/?cache='+cache, function(data) {
			if(data){
				$('#realtime-recharge-day').html('￥'+data);
			}
			updateRecharge(true);//每次请求完成,再发一次请求,避免客户端定时刷新来获取数据
		});
	}
	updateRecharge(false);
</script>