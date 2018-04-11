// 定义 popstate 的回调
window.onpopstate = function(event) { if (event.state) { _refreshDiv(event.state.div, event.state.url); }};

// 底层刷新DIV功能
var _refreshDiv = function(divId, url) {
	var div = $("#" + divId);
	// 给内容加一个加载的动画...
	var loading = $("<div class='overlay'><i class='fa fa-refresh fa-spin'></i></div>");
	div.find("div.box").append(loading);
	$.ajax({
		method : "GET",
		url : url,
		data : {
			"ajax" : true
		}
	}).done(function(result, status, XMLHttpRequest) {
		div.empty().html(result);
	}).fail(function(XmlHttpRequest, textStatus, errorThrown) {
		div.empty().html(XmlHttpRequest.responseText);// 404 500
	}).always(function() {// 不论成功与否都会执行
		loading.remove();
	});
}

// 异步加载页面到指定ID的DIV...
var ajaxLoadPage2Div = function(divId, url) {
	 window.history.pushState({"div" : divId, "url" : url }, "", window.location.href);
	 _refreshDiv(divId, url);
};
	
// 异步加载页到到内容区...
var ajaxLoadPage2Body = function(url) { ajaxLoadPage2Div("body", url); };

$(function() {
	// 拦截左边的导航中的链接，改为AJAX
	$("a.ajax").click(function() {
		var href = $(this).attr("href");
		if (href != "#") { ajaxLoadPage2Body(href); }
		return false;
	});
	
	// 弹出对话框
	$('#confirmModal').on('show.bs.modal', function(event) {
		var button = $(event.relatedTarget);
		var modal = $(this);
		modal.find('.modal-body p').text(button.data('msg'));//提示内容
		modal.find('.modal-footer button#ok').attr("onclick", button.data('href'));//OK事件
	});
});

// 注册Div里的Input获得焦点时清理掉错误提示
var registerFocusClearErrorMsg = function() {
	$('div.box-body input').focus(function() {
		var input = $(this);
		clearErrorMsg(input);
		input.parent().removeClass('has-error');
	});
}
// 清理掉下面一个元素（就是那个提示）
var clearErrorMsg = function(input) {
	input.next().remove();
}
// 添加一个错误提示
var addErrorMsg = function(input, msg) {
	clearErrorMsg(input);
	input.parent().addClass('has-error').append("<span class='help-block'>" + msg + "</span>").shake(2, 12, 500);
}

// 用于结果回调...
var resultCallback = function (msg, url){
	var modal = $("#ResultCallbackModal").modal({backdrop: 'static', keyboard: false});
	modal.find('.modal-body p').text(msg);//提示内容
	modal.find('.modal-footer button#ok').attr("onclick", url);//OK事件
}
//显示隐藏服务器列表
function toggleServerList() {
	$("#serverList").slideToggle(function() {/* 有空再来改那个按钮上的字 */});
}

// 添加道具到Table中...
var addItemTd = function(id, name) {
	$("#itemList").append(//
			"<tr id='tr:"+id+"'>" +
				"<td><input class='form-control' id='itemId' type='text' disabled value='" +id + "'></td>" +
				"<td><input class='form-control' id='itemName' type='text' disabled value='"+ name+ "'></td>" +
				"<td><input class='form-control' id='itemNum' type='text' value='1'></td>" +
				"<td onClick='javascript:$(this).parent().remove();'><a href='javascript:void(0);'>删除</a></td>" +
			"</tr>");
}

// 缩小100倍...
var doSomething = function(array, func) {
	 for(var i=0;i<array.length;i++){
		 array[i] = func(array[i]);
	 }
	 return array;
}

//Html编码获取Html转义实体  
function htmlEncode(value){
	if(value){
		value = value.replace(/\</g, "&lt;");//"<"转义   
		value = value.replace(/\>/g, "&gt;");//">"转义   
		value = value.replace(/\"/g, "&quot;");//"""转义   
		value = value.replace(/\'/g, "&acute;");//"'"转义
		value = value.replace(/\&/g, "&amp;");//"&"转义
		value = value.replace(/\ /g, "&nbsp;");//" "转义
		return value;  
	}
	return value;
}

//服务器列表 全选
var AllSelect = function(btn) {
	$(btn).nextAll("div").find("button[name=server]").each(function() {
		$(this).removeClass("btn-default").addClass("btn-primary").blur();
	});
	clearSelectServerErrorMsg();
}
// 服务器列表 反选
var ReverseSelection = function(btn) {
	$(btn).nextAll("div").find("button[name=server]").each(function() {
		if ($(this).hasClass("btn-default")) {
			$(this).removeClass("btn-default").addClass("btn-primary").blur();
		} else {
			$(this).removeClass("btn-primary").addClass("btn-default").blur();
		}
	});
	clearSelectServerErrorMsg();
}
//选择服务器列表
var selectServerId = function(btn, multiselect) {
	if ($(btn).hasClass("btn-default")) {
		// 取消别的选择...
		if (!multiselect){
			$("#server_list").find("button[name=server]").each(function() {
				$(this).removeClass("btn-primary").addClass("btn-default").blur();
			});
		}
		$(btn).removeClass("btn-default").addClass("btn-primary").blur();
	} else {
		$(btn).removeClass("btn-primary").addClass("btn-default").blur();
	}
	clearSelectServerErrorMsg();
}
// 取消选服的错误提示信息.
var clearSelectServerErrorMsg = function() {
	$("#server_list").next().remove();
}

function datetimeFormat(longTypeDate) {
	var datetimeType = "";
	var date = new Date();
	date.setTime(longTypeDate);
	datetimeType += date.getFullYear(); //年 
	datetimeType += "-" + getMonth(date); //月  
	datetimeType += "-" + getDay(date); //日 
	datetimeType += "  " + getHours(date); //时 
	datetimeType += ":" + getMinutes(date); //分
	datetimeType += ":" + getSeconds(date); //分
	return datetimeType;
}
//返回 01-12 的月份值  
function getMonth(date) {
	var month = "";
	month = date.getMonth() + 1; //getMonth()得到的月份是0-11 
	if (month < 10) {
		month = "0" + month;
	}
	return month;
}
//返回01-30的日期 
function getDay(date) {
	var day = "";
	day = date.getDate();
	if (day < 10) {
		day = "0" + day;
	}
	return day;
}
//返回小时
function getHours(date) {
	var hours = "";
	hours = date.getHours();
	if (hours < 10) {
		hours = "0" + hours;
	}
	return hours;
}
//返回分
function getMinutes(date) {
	var minute = "";
	minute = date.getMinutes();
	if (minute < 10) {
		minute = "0" + minute;
	}
	return minute;
}
//返回秒
function getSeconds(date) {
	var second = "";
	second = date.getSeconds();
	if (second < 10) {
		second = "0" + second;
	}
	return second;
}