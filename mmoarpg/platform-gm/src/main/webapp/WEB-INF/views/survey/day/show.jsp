<%@ page language="java" contentType="text/html;charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<c:choose>
	<c:when test="${empty param.yesterday}">
		<c:set var="v" value="${0}"/>
	</c:when>
	<c:otherwise>
		<c:set var="v" value="${(param.today-param.yesterday)/param.yesterday}"/>
	</c:otherwise>
</c:choose>

<c:choose>
	<c:when test="${v == 0}">
		<span class="description-percentage text-yellow"><i class="fa fa-caret-left"></i>
	</c:when>
	<c:when test="${v > 0}">
		<span class="description-percentage text-green"><i class="fa fa-caret-up"></i>
	</c:when>
	<c:otherwise>
		<span class="description-percentage text-red"><i class="fa fa-caret-down"></i>
	</c:otherwise>
</c:choose>

<fmt:formatNumber value="${v}" type="percent" />

</span>
<h5 class="description-header">
<c:choose>
	<c:when test="${param.show == 'integer'}">
		${param.today}
	</c:when>
	<c:when test="${param.show == 'percentage'}">
		<fmt:formatNumber value="${param.today*100.0}" pattern="#0.00#"/>%
	</c:when>
	<c:otherwise>
		<fmt:formatNumber value="${param.today}" pattern="#0.00#"/>
	</c:otherwise>
</c:choose>
</h5>