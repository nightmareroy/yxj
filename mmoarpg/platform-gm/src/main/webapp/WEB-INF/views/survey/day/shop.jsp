<%@ page language="java" contentType="text/html;charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<div class="box">
	<div class="box-header with-border">
		<h3 class="box-title">
			<i class="fa fa-bar-chart">商城销售额比例[<spring:message code='i18n.item.${type}' text='未知' />(${type})]</i>
		</h3>
		<div class="box-tools pull-right">
			<button type="button" class="btn btn-box-tool" data-widget="collapse">
				<i class="fa fa-minus"></i>
			</button>
			<div class="btn-group">
            	<button type="button" class="btn btn-box-tool dropdown-toggle" data-toggle="dropdown"><i class="fa fa-money"></i></button>
                <ul class="dropdown-menu" role="menu">
                    <c:forEach var="e" items="${moneyType}">
                    	<li><a href="javascript:ajaxLoadPage2Div('shop', '${pageContext.request.contextPath}/survey/day/shop/?today=${today}&type=${e.id}');"><spring:message code='i18n.item.${e.id}' text='未知' />(${e.id})</a></li>
					</c:forEach>
                </ul>
            </div>
			<button type="button" class="btn btn-box-tool" data-widget="remove">
				<i class="fa fa-times"></i>
			</button>
		</div>
	</div>
	<div class="box-body">
		<div id="shop_container" style="height: 300px"></div>
	</div>
</div>

<script>
	$(function () {
		var total = 0;
		<c:forEach var="e" items="${data}">
			total = total + ${e.value};
		</c:forEach>
		var array = [
		<c:forEach var="e" items="${data}">
			['<spring:message code="i18n.item.${e.key}" text="${e.key}"/>', ${e.value}],
		</c:forEach>
		];
		
	    $('#shop_container').highcharts({
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