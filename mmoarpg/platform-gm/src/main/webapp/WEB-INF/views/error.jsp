<%@ page language="java" contentType="text/html;charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<!-- 提示一个服务器正在维护中... -->
<c:if test="${not empty SERVER_NOT_FOUND}">
	<div class="alert alert-warning alert-dismissible">
		<button type="button" class="close" data-dismiss="alert" aria-hidden="true">×</button>
		<h4>
			<i class="icon fa fa-ban"></i> 温馨提示：
		</h4>
		很抱歉，目标服务器正在维护中，请稍后再次尝试...
	</div>
</c:if>

<!-- 提示一个查无此人的信息... -->
<c:if test="${not empty TARGET_NOT_FOUND}">
	<div class="alert alert-danger alert-dismissible">
		<button type="button" class="close" data-dismiss="alert" aria-hidden="true">×</button>
		<h4>
			<i class="icon fa fa-ban"></i> 温馨提示：
		</h4>
		很抱歉，查无结果，请确认查询条件...
	</div>
</c:if>