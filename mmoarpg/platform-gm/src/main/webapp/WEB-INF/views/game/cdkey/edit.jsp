<%@ page language="java" contentType="text/html;charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

<section class="content-header">
	<h1>生成兑换码</h1>
	<ol class="breadcrumb">
		<li><a href="${pageContext.request.contextPath}/welcome/"><i class="fa fa-dashboard"></i> 首页</a></li>
		<li>游戏管理</li>
		<li>兑换码管理</li>
		<li><a href="javascript:ajaxLoadPage2Body('${pageContext.request.contextPath}/game/cdkey/list/');">礼包列表</a></li>
		<li class="active"> 生成兑换码</li>
	</ol>
</section>
<section class="content">
	<div class="row">
		<div class="col-xs-12">
			<div class="box box-primary">
				<div class="box-header">
					<h3 class="box-title">兑换码配置</h3>
				</div>
				<form role="form" action="${pageContext.request.contextPath}/game/cdkey/add/" method="post" id="edit_cdkey">
					<div class="box-body">
						<div class="form-group">
							<label>CDK类型：</label> 
							<label><input type="radio" name="type" value="0" checked> 普通CDK &nbsp;</label>
							<label><input type="radio" name="type" value="1"> 通用CDK&nbsp;</label>
						</div>
						<div class="form-group">
							<label for="name">礼包名称: <span class="text-red">*</span></label>
							<input id="name" class="form-control" placeholder="礼包名称" type="text" value="${cdkey.name}" maxlength="256">
							<span class="help-block">请输入一个有简短且有意义的礼包名称...</span>
						</div>
						<div class="form-group">
							<label>兑换时间 : <span class="text-red">*</span></label>
							<div class="input-group">
								<div class="input-group-addon">
									<i class="fa fa-clock-o"></i>
								</div>
								<input type="text" class="form-control pull-right" id="effectivetime">
							</div>
							<span class="help-block">这个礼包可兑换的起始时间和结果时间...</span>
						</div>
						<div class="form-group">
							<label for="minLevel">兑换最低等级: <span class="text-red">*</span></label>
							<input id="minLevel" class="form-control" type="text" value="1" maxlength="10">
							<span class="help-block">最低多少等级的角色可以使用此兑换码...</span>
						</div>
						<div class="form-group" id="useMax-div">
							<label for="useMax">同类型兑换上限: <span class="text-red">*</span></label>
							<input id="useMax" class="form-control" type="text" value="1" maxlength="10">
							<span class="help-block">一个角色最多使用此类型的兑换码多少个...</span>
						</div>
						<div class="form-group" id="codeNum-div">
							<label for="codeNum">生成数量: <span class="text-red">*</span></label>
							<input id="codeNum" class="form-control" type="text" value="1" maxlength="10">
							<span class="help-block">生成多少个兑换码...</span>
						</div>
						<div class="form-group">
							<label>礼包道具: <span class="text-red">*</span></label>
							<%@ include file="../../itemlist.jsp"%>
						</div>
						<button type="submit" id="btn_cdkey" class="btn btn-primary btn-lg btn-block">提交</button>
					</div>
				</form>
			</div>
		</div>
	</div>
</section>
<script>
	$(function() {
		$("input[name='type']").click(function() {
			// 普通
			if ($(this).val() == 0) {
				$("#codeNum-div").show();
				$("#useMax-div").show();
			}
			// 通用
			if ($(this).val() == 1) {
				$("#codeNum-div").hide();
				$("#useMax-div").hide();
			}
		});
		
		$('#effectivetime').daterangepicker({
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
	
	$('#edit_cdkey').on('submit', function() {
		var name = $("#name");
		if (name.val().length == '') {
			addErrorMsg(name, "请输入礼包名称...");
			return false;
		}
		
		var datepicker = $("#effectivetime");
		if (datepicker.val().length == '') {
			addErrorMsg(datepicker, "请选择兑换时间...");
			return false;
		}
		
		var type = $("input[name='type']:checked").val();
		var minLevel = $("#minLevel");
		var useMax = $("#useMax");
		// 普通CDK
		if (type == 0) {
			if (minLevel.val().length == '') {
				addErrorMsg(minLevel, "最低多少等级的角色可以使用此兑换码...");
				return false;
			}
			if (useMax.val().length == '') {
				addErrorMsg(useMax, "一个角色最多使用此类型的兑换码多少个...");
				return false;
			}
		}
		
		var codeNum = $("#codeNum");
		if (codeNum.val().length == '') {
			addErrorMsg(codeNum, "生成多少个兑换码...");
			return false;
		}
		
		var itemIdArray = [];
		$("input[id=itemId]").each(function() {
			itemIdArray.push($(this).val());
		});
		var itemNameArray = [];
		$("input[id=itemName]").each(function() {
			itemNameArray.push($(this).val());
		});
		var itemNumArray = [];
		$("input[id=itemNum]").each(function() {
			itemNumArray.push($(this).val());
		});
		
		var $btn = $("#btn_cdkey").button('loading');
		$.post($(this).attr("action"), {
			"name" : name.val(),
			"datepicker" : datepicker.val(),
			"type" : type,
			"minLevel" : minLevel.val(),
			"useMax" : useMax.val(),
			"codeNum" : codeNum.val(),
			"itemIdList" : itemIdArray,
			"itemNameList" : itemNameArray,
			"itemNumList" : itemNumArray
		}, function(result) {
			$btn.button('reset');
			resultCallback("操作成功", "javascript:ajaxLoadPage2Body('${pageContext.request.contextPath}/game/cdkey/list/');");
	    });
		return false;
	});
</script>