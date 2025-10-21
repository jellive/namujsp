package com.jell.namujsp.controller;

import com.jell.namujsp.model.WikiDocument;
import com.jell.namujsp.service.WikiService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

/**
 * Wiki 컨트롤러
 */
@Controller
@RequestMapping("/wiki")
public class WikiController {

    private static final Logger logger = LoggerFactory.getLogger(WikiController.class);

    @Autowired
    private WikiService wikiService;

    /**
     * 메인 페이지 (문서 목록)
     */
    @GetMapping("")
    public String index(Model model) {
        List<WikiDocument> documents = wikiService.getAllDocuments();
        model.addAttribute("documents", documents);
        return "wiki/index";
    }

    /**
     * 문서 보기
     */
    @GetMapping("/view/{title}")
    public String viewDocument(@PathVariable("title") String title, Model model) {
        logger.info("문서 조회: {}", title);

        Map<String, Object> result = wikiService.getDocument(title);

        if ((Boolean) result.get("exists")) {
            model.addAttribute("document", result.get("document"));
            model.addAttribute("revision", result.get("revision"));
            model.addAttribute("htmlContent", result.get("htmlContent"));
            model.addAttribute("rawContent", result.get("rawContent"));
            return "wiki/view";
        } else {
            // 문서가 없으면 편집 페이지로 이동
            model.addAttribute("title", title);
            model.addAttribute("content", "");
            model.addAttribute("isNew", true);
            return "wiki/edit";
        }
    }

    /**
     * 문서 편집 페이지
     */
    @GetMapping("/edit/{title}")
    public String editDocument(@PathVariable("title") String title, Model model) {
        logger.info("문서 편집: {}", title);

        Map<String, Object> result = wikiService.getDocumentForEdit(title);

        model.addAttribute("title", title);
        model.addAttribute("content", result.get("content"));
        model.addAttribute("isNew", !(Boolean) result.get("exists"));

        return "wiki/edit";
    }

    /**
     * 문서 저장
     */
    @PostMapping("/save")
    public String saveDocument(
            @RequestParam("title") String title,
            @RequestParam("content") String content,
            @RequestParam(value = "editorName", required = false) String editorName,
            @RequestParam(value = "comment", required = false) String comment,
            HttpServletRequest request,
            Model model) {

        logger.info("문서 저장: {}", title);

        String editorIp = getClientIp(request);

        try {
            wikiService.saveDocument(title, content, editorName, editorIp, comment);
            return "redirect:/wiki/view/" + title;
        } catch (Exception e) {
            logger.error("문서 저장 실패: {}", title, e);
            model.addAttribute("error", "문서 저장 중 오류가 발생했습니다.");
            model.addAttribute("title", title);
            model.addAttribute("content", content);
            return "wiki/edit";
        }
    }

    /**
     * 문서 히스토리
     */
    @GetMapping("/history/{title}")
    public String viewHistory(@PathVariable("title") String title, Model model) {
        logger.info("문서 히스토리 조회: {}", title);

        Map<String, Object> result = wikiService.getHistory(title);

        if ((Boolean) result.get("exists")) {
            model.addAttribute("document", result.get("document"));
            model.addAttribute("revisions", result.get("revisions"));
            return "wiki/history";
        } else {
            model.addAttribute("error", "문서를 찾을 수 없습니다.");
            return "redirect:/wiki";
        }
    }

    /**
     * 클라이언트 IP 주소 가져오기
     */
    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }
}
