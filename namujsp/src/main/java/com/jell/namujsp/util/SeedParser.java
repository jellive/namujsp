package com.jell.namujsp.util;

import org.springframework.stereotype.Component;

/**
 * 나무위키 Seed 엔진 문법 파서
 * 기본적인 seed 문법을 HTML로 변환합니다.
 */
@Component
public class SeedParser {

    /**
     * Seed 문법을 HTML로 파싱
     * @param wikiText seed 문법으로 작성된 텍스트
     * @return HTML로 변환된 텍스트
     */
    public String parse(String wikiText) {
        if (wikiText == null || wikiText.isEmpty()) {
            return "";
        }

        String html = wikiText;

        // 줄 단위로 처리
        String[] lines = html.split("\n");
        StringBuilder result = new StringBuilder();

        for (String line : lines) {
            result.append(parseLine(line)).append("\n");
        }

        return result.toString();
    }

    /**
     * 한 줄을 파싱
     */
    private String parseLine(String line) {
        if (line.trim().isEmpty()) {
            return "<br/>";
        }

        String parsed = line;

        // 1. 제목 파싱 (= 제목 =)
        parsed = parseHeadings(parsed);

        // 2. 인라인 문법 파싱
        parsed = parseInline(parsed);

        // 3. 리스트 파싱
        parsed = parseList(parsed);

        return parsed;
    }

    /**
     * 제목 파싱
     * = 제목 = -> <h1>
     * == 제목 == -> <h2>
     * === 제목 === -> <h3>
     */
    private String parseHeadings(String line) {
        String trimmed = line.trim();

        // h1: = 제목 =
        if (trimmed.matches("^=\\s+(.+?)\\s+=$")) {
            String content = trimmed.replaceAll("^=\\s+(.+?)\\s+=$", "$1");
            return "<h1>" + content + "</h1>";
        }
        // h2: == 제목 ==
        else if (trimmed.matches("^==\\s+(.+?)\\s+==$")) {
            String content = trimmed.replaceAll("^==\\s+(.+?)\\s+==$", "$1");
            return "<h2>" + content + "</h2>";
        }
        // h3: === 제목 ===
        else if (trimmed.matches("^===\\s+(.+?)\\s+===$")) {
            String content = trimmed.replaceAll("^===\\s+(.+?)\\s+===$", "$1");
            return "<h3>" + content + "</h3>";
        }
        // h4: ==== 제목 ====
        else if (trimmed.matches("^====\\s+(.+?)\\s+====$")) {
            String content = trimmed.replaceAll("^====\\s+(.+?)\\s+====$", "$1");
            return "<h4>" + content + "</h4>";
        }

        return line;
    }

    /**
     * 인라인 문법 파싱
     */
    private String parseInline(String text) {
        String result = text;

        // 1. 굵게: **텍스트** -> <strong>텍스트</strong>
        result = result.replaceAll("\\*\\*(.+?)\\*\\*", "<strong>$1</strong>");

        // 2. 기울임: //텍스트// -> <em>텍스트</em>
        result = result.replaceAll("//(.+?)//", "<em>$1</em>");

        // 3. 취소선: ~~텍스트~~ -> <del>텍스트</del>
        result = result.replaceAll("~~(.+?)~~", "<del>$1</del>");

        // 4. 밑줄: __텍스트__ -> <u>텍스트</u>
        result = result.replaceAll("__(.+?)__", "<u>$1</u>");

        // 5. 문서 링크: [[문서명]] -> <a href="/wiki/문서명">문서명</a>
        result = result.replaceAll("\\[\\[(.+?)\\]\\]", "<a href=\"/wiki/view/$1\" class=\"wiki-link\">$1</a>");

        // 6. 코드: {{{텍스트}}} -> <code>텍스트</code>
        result = result.replaceAll("\\{\\{\\{(.+?)\\}\\}\\}", "<code>$1</code>");

        return result;
    }

    /**
     * 리스트 파싱
     */
    private String parseList(String line) {
        String trimmed = line.trim();

        // 순서 없는 리스트: * 항목
        if (trimmed.matches("^\\*\\s+(.+)$")) {
            String content = trimmed.replaceAll("^\\*\\s+(.+)$", "$1");
            return "<li>" + parseInline(content) + "</li>";
        }

        // 순서 있는 리스트: 1. 항목
        if (trimmed.matches("^\\d+\\.\\s+(.+)$")) {
            String content = trimmed.replaceAll("^\\d+\\.\\s+(.+)$", "$1");
            return "<li>" + parseInline(content) + "</li>";
        }

        return line;
    }

    /**
     * HTML 이스케이프 (XSS 방지)
     */
    public String escapeHtml(String text) {
        if (text == null) {
            return "";
        }
        return text
                .replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#x27;");
    }
}
