<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<jsp:include page="header.jsp">
    <jsp:param name="title" value="${document.title} - 히스토리" />
</jsp:include>

<div class="container">
    <div class="wiki-content">
        <h1>${document.title} - 수정 히스토리</h1>

        <div style="margin-bottom: 1rem;">
            <a href="<c:url value='/wiki/view/${document.title}' />" class="btn btn-secondary">문서로 돌아가기</a>
        </div>

        <table>
            <thead>
                <tr>
                    <th>리비전 ID</th>
                    <th>수정 시간</th>
                    <th>편집자</th>
                    <th>IP</th>
                    <th>코멘트</th>
                </tr>
            </thead>
            <tbody>
                <c:forEach items="${revisions}" var="rev">
                    <tr>
                        <td>${rev.revisionId}</td>
                        <td><fmt:formatDate value="${rev.createdAt}" pattern="yyyy-MM-dd HH:mm:ss" /></td>
                        <td>${rev.editorName}</td>
                        <td>${rev.editorIp}</td>
                        <td>${rev.revisionComment}</td>
                    </tr>
                </c:forEach>
            </tbody>
        </table>

        <c:if test="${empty revisions}">
            <p>히스토리가 없습니다.</p>
        </c:if>
    </div>
</div>

<jsp:include page="footer.jsp" />
