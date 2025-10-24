<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<jsp:include page="header.jsp">
    <jsp:param name="title" value="${document.title}" />
</jsp:include>

<div class="container">
    <div class="wiki-content">
        <h1>${document.title}</h1>

        <div style="margin-bottom: 1rem;">
            <a href="<c:url value='/wiki/edit/${document.title}' />" class="btn">편집</a>
            <a href="<c:url value='/wiki/history/${document.title}' />" class="btn btn-secondary">히스토리</a>
        </div>

        <div class="wiki-body">
            ${htmlContent}
        </div>

        <div class="wiki-meta">
            <p>
                최종 수정: <fmt:formatDate value="${revision.createdAt}" pattern="yyyy-MM-dd HH:mm:ss" />
                | 편집자: ${revision.editorName}
                <c:if test="${not empty revision.revisionComment}">
                    | 수정 내용: ${revision.revisionComment}
                </c:if>
            </p>
        </div>
    </div>
</div>

<jsp:include page="footer.jsp" />
