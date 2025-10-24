package com.jell.namujsp.util;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * SeedParser Unit Test
 * TDD 방식으로 작성된 Seed 문법 파서 테스트
 */
@DisplayName("SeedParser 테스트")
class SeedParserTest {

    private SeedParser parser;

    @BeforeEach
    void setUp() {
        parser = new SeedParser();
    }

    @Test
    @DisplayName("null 입력시 빈 문자열 반환")
    void testParseNull() {
        String result = parser.parse(null);
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("빈 문자열 입력시 빈 문자열 반환")
    void testParseEmpty() {
        String result = parser.parse("");
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("굵게 문법: **텍스트** → <strong>텍스트</strong>")
    void testBoldSyntax() {
        String input = "**굵은 글씨**";
        String result = parser.parse(input);
        assertThat(result).contains("<strong>굵은 글씨</strong>");
    }

    @Test
    @DisplayName("기울임 문법: //텍스트// → <em>텍스트</em>")
    void testItalicSyntax() {
        String input = "//기울임 글씨//";
        String result = parser.parse(input);
        assertThat(result).contains("<em>기울임 글씨</em>");
    }

    @Test
    @DisplayName("취소선 문법: ~~텍스트~~ → <del>텍스트</del>")
    void testStrikethroughSyntax() {
        String input = "~~취소선~~";
        String result = parser.parse(input);
        assertThat(result).contains("<del>취소선</del>");
    }

    @Test
    @DisplayName("밑줄 문법: __텍스트__ → <u>텍스트</u>")
    void testUnderlineSyntax() {
        String input = "__밑줄__";
        String result = parser.parse(input);
        assertThat(result).contains("<u>밑줄</u>");
    }

    @Test
    @DisplayName("링크 문법: [[문서명]] → <a href='/wiki/view/문서명'>문서명</a>")
    void testLinkSyntax() {
        String input = "[[메인페이지]]";
        String result = parser.parse(input);
        assertThat(result).contains("<a href=\"/wiki/view/메인페이지\" class=\"wiki-link\">메인페이지</a>");
    }

    @Test
    @DisplayName("코드 문법: {{{코드}}} → <code>코드</code>")
    void testCodeSyntax() {
        String input = "{{{System.out.println(\"Hello\");}}}";
        String result = parser.parse(input);
        assertThat(result).contains("<code>System.out.println(\"Hello\");</code>");
    }

    @ParameterizedTest
    @CsvSource({
        "'= 큰 제목 =', '<h1>큰 제목</h1>'",
        "'== 중간 제목 ==', '<h2>중간 제목</h2>'",
        "'=== 작은 제목 ===', '<h3>작은 제목</h3>'",
        "'==== 더 작은 제목 ====', '<h4>더 작은 제목</h4>'"
    })
    @DisplayName("제목 문법 테스트")
    void testHeadingSyntax(String input, String expected) {
        String result = parser.parse(input);
        assertThat(result).contains(expected);
    }

    @Test
    @DisplayName("순서 없는 리스트: * 항목 → <li>항목</li>")
    void testUnorderedListSyntax() {
        String input = "* 첫 번째 항목";
        String result = parser.parse(input);
        assertThat(result).contains("<li>첫 번째 항목</li>");
    }

    @Test
    @DisplayName("순서 있는 리스트: 1. 항목 → <li>항목</li>")
    void testOrderedListSyntax() {
        String input = "1. 첫 번째 항목";
        String result = parser.parse(input);
        assertThat(result).contains("<li>첫 번째 항목</li>");
    }

    @Test
    @DisplayName("복합 문법: 굵게 + 기울임")
    void testCombinedBoldAndItalic() {
        String input = "**//굵은 기울임//**";
        String result = parser.parse(input);
        assertThat(result).contains("<strong><em>굵은 기울임</em></strong>");
    }

    @Test
    @DisplayName("리스트 내부에 링크")
    void testListWithLink() {
        String input = "* [[메인페이지]]로 이동";
        String result = parser.parse(input);
        assertThat(result)
            .contains("<li>")
            .contains("<a href=\"/wiki/view/메인페이지\" class=\"wiki-link\">메인페이지</a>")
            .contains("</li>");
    }

    @Test
    @DisplayName("여러 줄 파싱")
    void testMultiLineParsing() {
        String input = "**첫 줄**\n//두 번째 줄//\n~~세 번째 줄~~";
        String result = parser.parse(input);

        assertThat(result)
            .contains("<strong>첫 줄</strong>")
            .contains("<em>두 번째 줄</em>")
            .contains("<del>세 번째 줄</del>");
    }

    @Test
    @DisplayName("빈 줄은 <br/>로 변환")
    void testEmptyLineToBr() {
        String input = "첫 줄\n\n세 번째 줄";
        String result = parser.parse(input);
        assertThat(result).contains("<br/>");
    }

    @Test
    @DisplayName("복잡한 문서 파싱")
    void testComplexDocumentParsing() {
        String input = """
            == 나무위키 ==

            **나무위키**는 [[위키엔진]]을 사용하는 //위키 사이트//입니다.

            === 주요 기능 ===
            * **문서 편집**
            * //히스토리 관리//
            * ~~삭제된 기능~~

            {{{코드 예제}}}
            """;

        String result = parser.parse(input);

        assertThat(result)
            .contains("<h2>나무위키</h2>")
            .contains("<strong>나무위키</strong>")
            .contains("<a href=\"/wiki/view/위키엔진\" class=\"wiki-link\">위키엔진</a>")
            .contains("<em>위키 사이트</em>")
            .contains("<h3>주요 기능</h3>")
            .contains("<li><strong>문서 편집</strong></li>")
            .contains("<li><em>히스토리 관리</em></li>")
            .contains("<li><del>삭제된 기능</del></li>")
            .contains("<code>코드 예제</code>");
    }

    @Test
    @DisplayName("HTML 이스케이프 테스트")
    void testHtmlEscape() {
        String dangerous = "<script>alert('XSS')</script>";
        String result = parser.escapeHtml(dangerous);

        assertThat(result)
            .doesNotContain("<script>")
            .contains("&lt;script&gt;")
            .contains("&lt;/script&gt;");
    }

    @Test
    @DisplayName("특수문자 이스케이프 테스트")
    void testSpecialCharacterEscape() {
        String input = "a & b < c > d \"quoted\" 'single'";
        String result = parser.escapeHtml(input);

        assertThat(result)
            .contains("&amp;")
            .contains("&lt;")
            .contains("&gt;")
            .contains("&quot;")
            .contains("&#x27;");
    }

    @Test
    @DisplayName("null HTML 이스케이프시 빈 문자열 반환")
    void testEscapeHtmlNull() {
        String result = parser.escapeHtml(null);
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("일반 텍스트는 그대로 유지")
    void testPlainTextUnchanged() {
        String input = "평범한 텍스트입니다";
        String result = parser.parse(input);
        assertThat(result).contains("평범한 텍스트입니다");
    }

    @Test
    @DisplayName("중첩된 링크 테스트")
    void testNestedLinks() {
        String input = "[[문서A]]와 [[문서B]]를 참조하세요";
        String result = parser.parse(input);

        assertThat(result)
            .contains("<a href=\"/wiki/view/문서A\" class=\"wiki-link\">문서A</a>")
            .contains("<a href=\"/wiki/view/문서B\" class=\"wiki-link\">문서B</a>");
    }
}
