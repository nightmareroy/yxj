<%@ page language="java" contentType="text/html;charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>

<section class="content-header">
	<h1>商城销售额分布</h1>
	<ol class="breadcrumb">
		<li><a href="${pageContext.request.contextPath}/welcome/"><i class="fa fa-dashboard"></i> 首页</a></li>
		<li>数据仓库</li>
		<li class="active">商城销售额分布</li>
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
			<form role="form" action="${pageContext.request.contextPath}/data/shop/list/" method="post" id="queryData">
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
					<label>商城类型: </label>
					<select class="form-control select2" style="width: 100%;" id="shopType">
						<c:forEach var="t" items="${moneyType}">
							<option value="${t.shopType}" ${t.shopType==shopType?'selected':''}>${t.des}商城</option>
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
					<h3 class="box-title">商城销售分布</h3>
				</div>
				<div class="box-body">
					<div class="row">
						<div class="col-xs-6">
							<div id="shop_container_1"></div>
						</div>
						<div class="col-xs-6">
							<div id="shop_container_2"></div>
						</div>
					</div>
				</div>
			</div>
		</div>
		<div class="box">
			<div class="box-header with-border">
				<div class="box-header with-border">
					<h3 class="box-title">商城销售额分布数据</h3>
				</div>
				<div class="box-body">
					<table class="table table-bordered table-striped" id="example">
						<thead>
							<tr>
								<th>商品名称</th>
								<th>出售额度(单位：元宝)</th>
								<th>出售数量(单位：件)</th>
							</tr>
						</thead>
						<tbody>
							<c:forEach var="e" items="${data}">
								<tr role="row">
									<td><spring:message code="i18n.item.${e.itemcode}" text="${e.itemcode}"/></td>
									<td>${e.totalRmb}</td>
									<td>${e.totalNum }</td>
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
		ajaxLoadPage2Body($(this).attr("action")+"?serverIds="+serverIds.join(',')+"&reservationtime="+$("#reservationtime").val()+"&shopType="+$("#shopType").val());
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
	            title: '商城销售分布'
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
	$(".select2").select2();
	$('#reservationtime').daterangepicker({
		timePicker24Hour: true,
		timePicker: true,
        locale: {
            format: 'YYYY-MM-DD HH:mm:ss'
        },
        ranges: {
            '今天': [moment(), moment()],
            '昨天': [moment().subtract(1, 'days'), moment().subtract(1, 'days')],
            '最近7天': [moment().subtract(6, 'days'), moment()],
            '最近30开': [moment().subtract(29, 'days'), moment()],
            '这个月': [moment().startOf('month'), moment().endOf('month')],
            '上个月': [moment().subtract(1, 'month').startOf('month'), moment().subtract(1, 'month').endOf('month')]
         }
	});
});
</script>
<script>
	$(function () {
		var totalRmb = 0;
		var totalNum = 0;
		<c:forEach var="e" items="${data}">
			totalRmb = totalRmb + ${e.totalRmb};
			totalNum = totalNum + ${e.totalNum};
		</c:forEach>
		var arrayRmb = [
		<c:forEach var="e" items="${data}">
			['<spring:message code="i18n.item.${e.itemcode}" text="${e.itemcode}"/>', ${e.totalRmb}],
		</c:forEach>
		];
		var arrayNum = [
			<c:forEach var="e" items="${data}">
				['<spring:message code="i18n.item.${e.itemcode}" text="${e.itemcode}"/>', ${e.totalNum}],
			</c:forEach>
			];
		
	    $('#shop_container_1').highcharts({
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
	            text: '商城销售额分布'
	        },
	        plotOptions: {
	            pie: {
	                innerSize: 100,
	                depth: 45
	            }
	        },
	        series: [{
	            name: '今日总销售额',
	            data: arrayRmb,
	            dataLabels: {
	                formatter: function () {
	                    // 大于1则显示
	                    return this.y > 1 ? '<b>' + this.point.name + ':</b> ' + Highcharts.numberFormat((this.y*100/totalRmb),2) + '%'  : null;
	                }
	            }
	        }]
	    });
	    
	    $('#shop_container_2').highcharts({
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
	            text: '商城销售数量分布'
	        },
	        plotOptions: {
	            pie: {
	                innerSize: 100,
	                depth: 45
	            }
	        },
	        series: [{
	            name: '今日总销售数量',
	            data: arrayNum,
	            dataLabels: {
	                formatter: function () {
	                    // 大于1则显示
	                    return this.y > 1 ? '<b>' + this.point.name + ':</b> ' + Highcharts.numberFormat((this.y*100/totalNum),2) + '%'  : null;
	                }
	            }
	        }]
	    });
	});
</script>