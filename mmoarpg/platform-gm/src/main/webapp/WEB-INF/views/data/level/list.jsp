<%@ page language="java" contentType="text/html;charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>

<section class="content-header">
	<h1>玩家等级分布</h1>
	<ol class="breadcrumb">
		<li><a href="${pageContext.request.contextPath}/welcome/"><i class="fa fa-dashboard"></i> 首页</a></li>
		<li>数据仓库</li>
		<li class="active">玩家等级分布</li>
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
			<form role="form" action="${pageContext.request.contextPath}/data/level/list/" method="post" id="queryData">
				<div class="form-group">
					<label>查询时间: </label>
					<div class="input-group">
						<div class="input-group-addon">
							<i class="fa fa-clock-o"></i>
						</div>
						<input type="text" class="form-control pull-right" id="reservationtime" value="${today}">
					</div>
				</div>
				<button id="btn_query_rank" type="submit" class="btn btn-primary btn-lg btn-block">查询</button>
			</form>
		</div>
	</div>
	<c:if test="${not empty result}">
		<div class="box">
			<div class="box-header with-border">
				<div class="box-header with-border">
					<h3 class="box-title">玩家等级分布</h3>
				</div>
				<div class="box-body">
					<div id="container"></div>
				</div>
			</div>
		</div>
		<div class="box">
			<div class="box-header with-border">
				<div class="box-header with-border">
					<h3 class="box-title">玩家等级分布数据</h3>
				</div>
				<div class="box-body">
					<table class="table table-bordered table-striped" id="example">
						<thead>
							<tr>
								<th>等级</th>
								<th>玩家数量</th>
							</tr>
						</thead>
						<tbody>
							<c:forEach var="e" items="${result}">
								<tr>
									<td>${e.level}</td>
									<td>${e.count}</td>
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
		ajaxLoadPage2Body($(this).attr("action")+"?serverIds="+serverIds.join(',')+"&reservationtime="+$("#reservationtime").val());
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
	            title: '玩家等级分布'
	        },  {
	            extend: 'print',
	            text:'打印'
	        } ],
	      	//跟数组下标一样，第一列从0开始，这里表格初始化时，第四列默认降序
            "order": [[ 0, "desc" ]]
		});
</script>
<script>
$(function() {
	$('#reservationtime').daterangepicker({
		"singleDatePicker": true,
		autoUpdateInput:false,
	    "maxDate": "${maxtime}",
	    locale: {
            format: 'YYYY-MM-DD'
        }
	}, function(start, end, label) {
		$('#reservationtime').val(start.format('YYYY-MM-DD'));
	});
});
</script>
<script>
var categories = [];
var xdata = [];

<c:forEach var="e" items="${result}">
categories.push(${e.level});
xdata.push(${e.count});
</c:forEach>

$(function () {
    $('#container').highcharts({
	credits : {
		enabled : false
	},
	chart : {
		zoomType : 'xy'
	},
	title : {
		text : '等级分布'
	},
	xAxis : [ {
		categories : categories
	} ],
	yAxis : [ { // Primary yAxis
		labels : {
			style : {
				color : '#89A54E'
			}
		},
		title : {
			text : '当前人数',
			style : {
				color : '#89A54E'
			}
		}
	}, { // Secondary yAxis
		title : {
			text : '',
			style : {
				color : '#4572A7'
			}
		},
		labels : {
			style : {
				color : '#4572A7'
			}
		},
		opposite : true
	} ],
	tooltip : {
		shared : true
	},
	legend : {
		layout : 'vertical',
		align : 'left',
		x : 120,
		verticalAlign : 'top',
		y : 50,
		floating : true,
		backgroundColor : '#FFFFFF'
	},
	series : [ {
		name : '人数',
		color : '#4572A7',
		type : 'column',
		yAxis : 1,
		data : xdata,
		tooltip : {
			valueSuffix : '人'
		}

	},]
	});
    
    $('#today').daterangepicker({
	    "singleDatePicker": true,
	    "startDate": "${empty today?maxtime:today}",
	    "maxDate": "${maxtime}",
	    locale: {
            format: 'YYYY-MM-DD'
        },
	}, function(start, end, label) {
		ajaxLoadPage2Body("${pageContext.request.contextPath}/survey/level/list/?today="+start.format('YYYY-MM-DD'));
	});
});
</script>