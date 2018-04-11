<%@ page language="java" contentType="text/html;charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>

<section class="content-header">
	<h1>货币产出分布</h1>
	<ol class="breadcrumb">
		<li><a href="${pageContext.request.contextPath}/welcome/"><i class="fa fa-dashboard"></i> 首页</a></li>
		<li>数据仓库</li>
		<li class="active">货币产出分布</li>
	</ol>
</section>
<section class="content">

	<!-- 选服列表 -->
	<jsp:include page="../../serverlist.jsp">
		<jsp:param name="multiselect" value="true" />
		<jsp:param name="dosomething" value="javascript:selectServerId(this, true);" />
	</jsp:include>
	
	<div class="box box-primary">
		<div class="box-header with-border">
			<h3 class="box-title">查询条件</h3>
		</div>
		<div class="box-body">
			<form role="form" action="${pageContext.request.contextPath}/data/produce/list/" method="post" id="queryData">
				<div class="form-group">
					<label>查询时间: </label>
					<div class="input-group">
						<div class="input-group-addon">
							<i class="fa fa-clock-o"></i>
						</div>
						<input type="text" class="form-control pull-right" id="reservationtime" value="${reservationtime}">
					</div>
				</div>
				
				<div class="form-group">
					<label>货币类型: </label>
					<select class="form-control select2" style="width: 100%;" id="itemcode">
						<c:forEach var="t" items="${moneyType}">
							<option value="${t.id}" ${t.id==itemcode?'selected':''}>${t.des}</option>
						</c:forEach>
					</select>
				</div>
			
				<button id="btn_query_rank" type="submit" class="btn btn-primary btn-lg btn-block">查询</button>
			</form>
		</div>
	</div>
	<c:if test="${not empty data}">
		<div class="box">
			<div class="box-header with-border">
				<div class="box-header with-border">
					<h3 class="box-title">货币产出分布比</h3>
				</div>
				<div class="box-body">
					<div id="container"></div>
				</div>
			</div>
		</div>
		<div class="box">
			<div class="box-header with-border">
				<div class="box-header with-border">
					<h3 class="box-title">货币产出分布数据</h3>
				</div>
				<div class="box-body">
					<table class="table table-bordered table-striped" id="example">
						<thead>
							<tr>
								<th>来源</th>
								<th>产生额度(单位：元宝)</th>
							</tr>
						</thead>
						<tbody>
							<c:forEach var="e" items="${data}">
								<tr>
									<td><spring:message code="i18n.func.code.${e.key}" text="${e.key}"/></td>
									<td>${e.value}</td>
								</tr>
							</c:forEach>
						</tbody>
					</table>
				</div>
			</div>
		</div>
	</c:if>
</section>

<script>
	$('#queryData').on('submit', function() {
		var serverIds = [];
		$("button[type=button][name=server]").each(function() {
			if (!$(this).hasClass("btn-default")) {
				serverIds.push($(this).val());
			}
		});
		var $btn = $("#btn_query_rank").button('loading');
		ajaxLoadPage2Body($(this).attr("action")+"?serverIds="+serverIds.join(',')+"&reservationtime="+$("#reservationtime").val()+"&itemcode="+$("#itemcode").val()+"&operate=1");
		return false;
	});

		$('#example').DataTable({
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
	            title: '货币产出分布'
	        },  {
	            extend: 'print',
	            text:'打印'
	        } ],
	      	//跟数组下标一样，第一列从0开始，这里表格初始化时，第四列默认降序
            "order": [[ 1, "desc" ]]
		});
</script>
<script>
$(function() {
	$('#reservationtime').daterangepicker({
		"singleDatePicker": true,
	    "startDate": "${today}",
	    "maxDate": "${maxtime}",
	    locale: {
            format: 'YYYY-MM-DD'
        }
	});
});
</script>
<script>
	$(function() {
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
	            name: '今日总产出',
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