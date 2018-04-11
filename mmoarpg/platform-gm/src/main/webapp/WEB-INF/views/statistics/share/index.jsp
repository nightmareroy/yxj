<%@ page language="java" contentType="text/html;charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<section class="content-header">
	<h1>
		各系统${action==0?"产出":"消耗"}份额  <small id="reservationtime"><i class="fa fa-calendar"> <u>${today}</u></i></small>
	</h1>
	<ol class="breadcrumb">
		<li><a href="${pageContext.request.contextPath}/welcome/"><i class="fa fa-dashboard"></i> 首页</a></li>
		<li>统计分析</li>
		<li class="active">各系统${action==0?"产出":"消耗"}份额</li>
	</ol>
</section>
<section class="content">
	<div class="row">
		<div class="col-xs-12">
			<div class="box">
				<div class="box-header">
					<h3 class="box-title">
						<i class="fa fa-list"></i> 各系统中[<spring:message code='i18n.item.${type}' text='未知' />(${type})]${action==0?"产出":"消耗"}份额比例
					</h3>
					<div class="box-tools pull-right">
						<div class="btn-group">
			            	<button type="button" class="btn btn-box-tool dropdown-toggle" data-toggle="dropdown"><i class="fa fa-money"></i></button>
			                <ul class="dropdown-menu" role="menu">
			                    <c:forEach var="e" items="${moneyType}">
			                    	<li><a href="javascript:ajaxLoadPage2Body('${pageContext.request.contextPath}/statistics/share/query/?today=${today}&type=${e.id}&action=${action}');"><spring:message code='i18n.item.${e.id}' text='未知' />(${e.id})</a></li>
								</c:forEach>
			                </ul>
			            </div>
					</div>
				</div>
				<div class="box-body">
					<div id="container"></div>
				</div>
			</div>
		</div>
	</div>
</section>
<script>
	$(function() {
		$('#reservationtime').daterangepicker({
		    "singleDatePicker": true,
		    "startDate": "${today}",
		    "endDate": "${today}",
		    "maxDate": "${maxtime}",
		    locale: {
	            format: 'YYYY-MM-DD'
	        },
		}, function(start, end, label) {
			$('#reservationtime').html("<i class='fa fa-calendar'> <u>"+start.format('YYYY-MM-DD')+"</u></i>");
			ajaxLoadPage2Body('${pageContext.request.contextPath}/statistics/share/query/?today='+start.format('YYYY-MM-DD')+'&type=${type}&action=${action}');
		});
		
		// 数据图表...
		var total = 0;
		<c:forEach var="e" items="${data}">
			total = total + ${e.value};
		</c:forEach>
		var array = [
		<c:forEach var="e" items="${data}">
			['<spring:message code="i18n.func.code.${e.key}" text="${e.key}"/>', ${e.value}],
		</c:forEach>
		];
		
	    $('#container').highcharts({
	    	credits: {
	            enabled:false
	  		},
	        chart: {
	            type: 'pie',
	            options3d: {
	                enabled: true,
	                alpha: 45
	            }
	        },
	        title: {
	            text: ''
	        },
	        plotOptions: {
	            pie: {
	                innerSize: 100,
	                depth: 45
	            }
	        },
	        series: [{
	            name: '今日总销售额',
	            data: array,
	            dataLabels: {
	                formatter: function () {
	                    // 大于1则显示
	                    return this.y > 1 ? '<b>' + this.point.name + ':</b> ' + Highcharts.numberFormat((this.y*100/total),2) + '%'  : null;
	                }
	            }
	        }]
	    });
	});
</script>