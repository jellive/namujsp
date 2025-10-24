<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<jsp:include page="header.jsp">
    <jsp:param name="title" value="문서 목록" />
</jsp:include>

<div class="container">
    <div class="wiki-content">
        <h1>모든 문서 목록</h1>

        <table>
            <thead>
                <tr>
                    <th>문서 제목</th>
                    <th>생성일</th>
                    <th>최종 수정일</th>
                </tr>
            </thead>
            <tbody>
                <c:forEach items="${documents}" var="doc">
                    <tr>
                        <td>
                            <a href="<c:url value='/wiki/view/${doc.title}' />" class="wiki-link">
                                ${doc.title}
                            </a>
                        </td>
                        <td><fmt:formatDate value="${doc.createdAt}" pattern="yyyy-MM-dd HH:mm" /></td>
                        <td><fmt:formatDate value="${doc.updatedAt}" pattern="yyyy-MM-dd HH:mm" /></td>
                    </tr>
                </c:forEach>
            </tbody>
        </table>

        <c:if test="${empty documents}">
            <p>문서가 없습니다. 첫 번째 문서를 작성해보세요!</p>
        </c:if>
    </div>
</div>

<jsp:include page="footer.jsp" />
