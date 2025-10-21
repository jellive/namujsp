<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<jsp:include page="header.jsp">
    <jsp:param name="title" value="${title} - 편집" />
</jsp:include>

<div class="container">
    <div class="wiki-content">
        <h1>${isNew ? '새 문서 작성' : '문서 편집'}: ${title}</h1>

        <c:if test="${not empty error}">
            <div class="error">${error}</div>
        </c:if>

        <form action="<c:url value='/wiki/save' />" method="post">
            <input type="hidden" name="title" value="${title}" />

            <div class="form-group">
                <label for="content">문서 내용 (Seed 문법)</label>
                <textarea id="content" name="content" required>${content}</textarea>
            </div>

            <div class="form-group">
                <label for="editorName">작성자 이름 (선택)</label>
                <input type="text" id="editorName" name="editorName" placeholder="익명" />
            </div>

            <div class="form-group">
                <label for="comment">수정 코멘트 (선택)</label>
                <input type="text" id="comment" name="comment" placeholder="변경 사항을 간단히 설명해주세요" />
            </div>

            <div>
                <button type="submit" class="btn">저장</button>
                <a href="<c:url value='/wiki/view/${title}' />" class="btn btn-secondary">취소</a>
            </div>
        </form>

        <div style="margin-top: 2rem; padding: 1rem; background-color: #f8f9fa; border-radius: 4px;">
            <h3>Seed 문법 도움말</h3>
            <ul>
                <li><strong>굵게:</strong> **텍스트**</li>
                <li><strong>기울임:</strong> //텍스트//</li>
                <li><strong>취소선:</strong> ~~텍스트~~</li>
                <li><strong>밑줄:</strong> __텍스트__</li>
                <li><strong>링크:</strong> [[문서명]]</li>
                <li><strong>코드:</strong> {{{코드}}}</li>
                <li><strong>제목:</strong> = 큰 제목 =, == 중간 제목 ==, === 작은 제목 ===</li>
                <li><strong>목록:</strong> * 항목 또는 1. 순서 항목</li>
            </ul>
        </div>
    </div>
</div>

<jsp:include page="footer.jsp" />
