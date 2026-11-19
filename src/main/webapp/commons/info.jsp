<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/commons/taglib.jsp"%>
<c:if test="${not empty success}">
  <div class="alert alert-success"><c:out value="${success}"/></div>
</c:if>
<c:if test="${not empty alert}">
  <div class="alert alert-danger"><c:out value="${alert}"/></div>
</c:if>
<c:if test="${not empty message}">
  <div class="alert alert-info"><c:out value="${message}"/></div>
</c:if>
