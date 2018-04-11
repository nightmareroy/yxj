<%@ page language="java" contentType="text/html;charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<section class="content-header">
	<h1>
		新手引导转化率
	</h1>
	<ol class="breadcrumb">
		<li><a href="${pageContext.request.contextPath}/welcome/"><i class="fa fa-dashboard"></i> 首页</a></li>
		<li>统计分析</li>
		<li class="active">新手引导转化率</li>
	</ol>
</section>
<section class="content">
	<div class="row">
		<div class="col-xs-12">
			<div class="box">
				<div class="box-header">
					<h3 class="box-title">
						<i class="fa fa-list"></i> 新手引导转化率
					</h3>
				</div>
				<div class="box-body">
					<div id="container" style="height: 600px"></div>
				</div>
			</div>
		</div>
	</div>
</section>
<script>
$(function () {
	var max = 1;
	<c:forEach var="e" items="${result}">
		if(${e.value}>max){
			max = ${e.value};
		}
	</c:forEach>
	
    $('#container').highcharts({
    	credits : {
			enabled : false
		},
        chart: {
            type: 'column'
        },
        title: {
            text: ''
        },
        xAxis: {
            type: 'category',
            labels: {
                rotation: -45,
                style: {
                    fontSize: '13px',
                    fontFamily: 'Verdana, sans-serif'
                }
            }
        },
        yAxis: {
            min: 0,
            title: {
                text: '打点计次 (次数)'
            }
        },
        legend: {
            enabled: false
        },
        tooltip: {
            pointFormat: '打点计次: <b>{point.y} 次数</b>'
        },
        series: [{
            name: '打点计次',
            data: [
				<c:forEach var="e" items="${result}">
					['<spring:message code="template.guide.${e.key}" text="${e.key}"/>', ${e.value}],
				</c:forEach>
            ],
            dataLabels: {
                enabled: true,
                rotation: -90,
                color: '#FFFFFF',
                align: 'right',
                y: 10, // 10 pixels down from the top
                style: {
                    fontSize: '13px',
                    fontFamily: 'Verdana, sans-serif'
                },
                formatter : function() {
					return this.y + '(' + Highcharts.numberFormat(this.y * 100 / max, 2) + '%)';
				}
            }
        }]
    });
});
</script>