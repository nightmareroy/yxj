<%@ page language="java" contentType="text/html;charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<div class="box">
	<div class="box-header with-border">
		<h3 class="box-title">
			<i class="fa fa-bar-chart">转化率</i>
		</h3>

		<div class="box-tools pull-right">
			<button type="button" class="btn btn-box-tool" data-widget="collapse">
				<i class="fa fa-minus"></i>
			</button>
			<button type="button" class="btn btn-box-tool" data-widget="remove">
				<i class="fa fa-times"></i>
			</button>
		</div>
	</div>
	<div class="box-body">
		<div id="conversion_container" style="height: 300px"></div>
	</div>
</div>

<script>
$(function () {
	var max = 1;
	<c:forEach var="e" items="${data}">
		if(${e.value}>max){
			max = ${e.value};
		}
	</c:forEach>
	
    $('#conversion_container').highcharts({
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
				<c:forEach var="e" items="${data}">
					['<spring:message code="i18n.login.step.${e.key}" text="${e.key}"/>', ${e.value}],
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