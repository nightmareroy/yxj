<%@ page language="java" contentType="text/html;charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<!-- 这个页面，只合适Spring返回的那个Page -->
<div class="row">
	<div class="col-sm-6">当前第 ${page.number+1} 页，共 ${page.totalPages} 页，每页 ${page.size} 条，共 ${page.totalElements} 条记录</div>
	<div class="col-sm-6">
		<ul class="pagination pagination-sm no-margin pull-right">
			<c:choose>
				<c:when test="${page.number == 0}">
					<li class="disabled"><a href="javascript:void(0);">上一页</a></li>
				</c:when>
				<c:otherwise>
					<li><a href="javascript:ajaxLoadPage2Body('${param.url}?page=${page.number - 1}&${param.args}');">上一页</a></li>
				</c:otherwise>
			</c:choose>

			<c:if test="${page.totalElements > 0}">
				<c:forEach begin="${page.number < 5 ? 0 :(page.number-4)}" end="${page.number + 5 > page.totalPages ? page.totalPages-1: page.number + 4 }" step="1" varStatus="i">
					<c:choose>
						<c:when test="${page.number == i.index}">
							<li class="active"><a href="javascript:void(0);">${i.index+1}</a></li>
						</c:when>
						<c:otherwise>
							<li><a href="javascript:ajaxLoadPage2Body('${param.url}?page=${i.index}&${param.args}');">${i.index+1}</a></li>
						</c:otherwise>
					</c:choose>
				</c:forEach>
			</c:if>

			<c:choose>
				<c:when test="${page.number >= page.totalPages-1}">
					<li class="disabled"><a href="javascript:void(0);">下一页</a></li>
				</c:when>
				<c:otherwise>
					<li><a href="javascript:ajaxLoadPage2Body('${param.url}?page=${page.number+ 1}&${param.args}');">下一页</a></li>
				</c:otherwise>
			</c:choose>
		</ul>
	</div>
</div>