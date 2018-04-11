<%@page language="java" contentType="text/html;charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

<!DOCTYPE html>
<html>
	<head>
		<meta charset="utf-8">
		<meta name="renderer" content="webkit">
		<title>Game Manager - Powered By 清源科技</title>
		<link rel="shortcut icon" type="image/x-icon" href="${pageContext.request.contextPath}/resources/images/favicon.ico">
		<meta content="width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no" name="viewport">
		
		<!--[if lte IE 10]>
		<script type="text/javascript">
			location.href = '${pageContext.request.contextPath}/unsupport-browser/';
		</script>
		<![endif]-->
		
		<link rel="stylesheet" href="${pageContext.request.contextPath}/resources/bootstrap/css/bootstrap.min.css">
		<link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/font-awesome.min.css">
		<link rel="stylesheet" href="${pageContext.request.contextPath}/resources/plugins/daterangepicker/daterangepicker.css">
		<link rel="stylesheet" href="${pageContext.request.contextPath}/resources/plugins/datepicker/css/bootstrap-datepicker3.min.css">
		
		<!-- 表格 -->
		<link rel="stylesheet" href="${pageContext.request.contextPath}/resources/plugins/datatables/css/dataTables.bootstrap.min.css">
		<link rel="stylesheet" href="${pageContext.request.contextPath}/resources/plugins/datatables/buttons/css/buttons.dataTables.min.css">
		
		<!-- Select2 -->
		<link rel="stylesheet" href="${pageContext.request.contextPath}/resources/plugins/select2/select2.min.css">
		<link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/AdminLTE.min.css">
		<link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/skins/_all-skins.min.css">
		<link rel="stylesheet" href="${pageContext.request.contextPath}/resources/plugins/easyform/easyform.css">
	</head>

	<body class="hold-transition skin-blue sidebar-mini">
		<div class="wrapper">
			<%@ include file="header.jsp"%>
			<%@ include file="navbar.jsp"%>
			
			<div class="content-wrapper" id="body">
				<%@ include file="default.jsp"%>
			</div>
			
			<%@ include file="footer.jsp"%>
		</div>
		<%@ include file="confirm.jsp"%>
		<%@ include file="result.jsp"%>
		<script src="${pageContext.request.contextPath}/resources/js/jquery-3.2.0.min.js"></script>
		<script src="${pageContext.request.contextPath}/resources/js/jquery.form.min.js"></script>
		<script src="${pageContext.request.contextPath}/resources/bootstrap/js/bootstrap.min.js"></script>
		
		<script src="${pageContext.request.contextPath}/resources/plugins/select2/select2.full.min.js"></script>
		<!-- AdminLTE App -->
		<script src="${pageContext.request.contextPath}/resources/js/app.min.js"></script>
		
		<!-- date-range-picker -->
		<script src="${pageContext.request.contextPath}/resources/plugins/daterangepicker/moment.min.js"></script>
		<script src="${pageContext.request.contextPath}/resources/plugins/daterangepicker/daterangepicker.js"></script>
		
		<!-- InputMask -->
		<script src="${pageContext.request.contextPath}/resources/plugins/input-mask/jquery.inputmask.js"></script>
		<script src="${pageContext.request.contextPath}/resources/plugins/input-mask/jquery.inputmask.date.extensions.js"></script>
		<script src="${pageContext.request.contextPath}/resources/plugins/input-mask/jquery.inputmask.extensions.js"></script>
		<script src="${pageContext.request.contextPath}/resources/plugins/datepicker/js/bootstrap-datepicker.min.js"></script>

		<!-- Highcharts -->
		<script src="${pageContext.request.contextPath}/resources/plugins/highcharts/highcharts.js"></script>
		<script src="${pageContext.request.contextPath}/resources/plugins/highcharts/highcharts-3d.js"></script>
		<script src="${pageContext.request.contextPath}/resources/plugins/highcharts/highcharts-more.js"></script>
		<script src="${pageContext.request.contextPath}/resources/plugins/highcharts/solid-gauge.js"></script>
		
		<!-- 表格 -->
		<script src="${pageContext.request.contextPath}/resources/plugins/datatables/js/jquery.dataTables.min.js"></script>
		<script src="${pageContext.request.contextPath}/resources/plugins/datatables/js/dataTables.bootstrap.min.js"></script>
		<script src="${pageContext.request.contextPath}/resources/plugins/datatables/buttons/js/dataTables.buttons.min.js"></script>
		<script src="${pageContext.request.contextPath}/resources/plugins/datatables/buttons/js/jszip.min.js"></script><!-- Excel -->
		<script src="${pageContext.request.contextPath}/resources/plugins/datatables/buttons/js/buttons.html5.min.js"></script><!-- Excel+复制 -->
		<script src="${pageContext.request.contextPath}/resources/plugins/datatables/buttons/js/buttons.print.min.js"></script><!-- 打印 -->
		
		<script src="${pageContext.request.contextPath}/resources/plugins/ckeditor/ckeditor.js"></script>
		<script src="${pageContext.request.contextPath}/resources/js/ubb.js"></script>
		
		<script src="${pageContext.request.contextPath}/resources/js/shake.js"></script>
		<script src="${pageContext.request.contextPath}/resources/js/admin.js"></script>
		<script src="${pageContext.request.contextPath}/resources/js/sockjs.min.js"></script>
		
		<script src="${pageContext.request.contextPath}/resources/plugins/easyform/easyform.js"></script>
		<script src="${pageContext.request.contextPath}/resources/plugins/datepicker/locales/bootstrap-datepicker.zh-CN.min.js"></script>
	</body>
</html>