package com.jell.namujsp.service;

import com.jell.namujsp.dao.WikiDocumentMapper;
import com.jell.namujsp.dao.WikiRevisionMapper;
import com.jell.namujsp.model.WikiDocument;
import com.jell.namujsp.model.WikiRevision;
import com.jell.namujsp.util.SeedParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Wiki 서비스
 */
@Service
public class WikiService {

    private static final Logger logger = LoggerFactory.getLogger(WikiService.class);

    @Autowired
    private WikiDocumentMapper documentMapper;

    @Autowired
    private WikiRevisionMapper revisionMapper;

    @Autowired
    private SeedParser seedParser;

    /**
     * 문서 조회 (제목으로)
     */
    public Map<String, Object> getDocument(String title) {
        Map<String, Object> result = new HashMap<>();

        WikiDocument document = documentMapper.findByTitle(title);

        if (document == null) {
            result.put("exists", false);
            result.put("title", title);
            return result;
        }

        // 최신 리비전 조회
        WikiRevision latestRevision = revisionMapper.findLatestByDocId(document.getDocId());

        if (latestRevision != null) {
            // Seed 문법을 HTML로 파싱
            String htmlContent = seedParser.parse(latestRevision.getContent());

            result.put("exists", true);
            result.put("document", document);
            result.put("revision", latestRevision);
            result.put("htmlContent", htmlContent);
            result.put("rawContent", latestRevision.getContent());
        } else {
            result.put("exists", false);
            result.put("title", title);
        }

        return result;
    }

    /**
     * 문서 생성 또는 수정
     */
    @Transactional
    public void saveDocument(String title, String content, String editorName, String editorIp, String comment) {
        WikiDocument document = documentMapper.findByTitle(title);

        // 문서가 없으면 새로 생성
        if (document == null) {
            document = new WikiDocument(title);
            documentMapper.insert(document);
            logger.info("새 문서 생성: {}", title);
        }

        // 새 리비전 생성
        WikiRevision revision = new WikiRevision(
                document.getDocId(),
                content,
                editorName != null && !editorName.isEmpty() ? editorName : "익명",
                editorIp,
                comment
        );

        revisionMapper.insert(revision);

        // 문서의 current_revision_id 업데이트
        documentMapper.updateCurrentRevisionId(document.getDocId(), revision.getRevisionId());

        logger.info("문서 저장 완료: {} (리비전 ID: {})", title, revision.getRevisionId());
    }

    /**
     * 문서 편집을 위한 원본 내용 조회
     */
    public Map<String, Object> getDocumentForEdit(String title) {
        Map<String, Object> result = new HashMap<>();

        WikiDocument document = documentMapper.findByTitle(title);

        if (document != null) {
            WikiRevision latestRevision = revisionMapper.findLatestByDocId(document.getDocId());
            result.put("exists", true);
            result.put("document", document);
            result.put("content", latestRevision != null ? latestRevision.getContent() : "");
        } else {
            result.put("exists", false);
            result.put("title", title);
            result.put("content", "");
        }

        return result;
    }

    /**
     * 문서 히스토리 조회
     */
    public Map<String, Object> getHistory(String title) {
        Map<String, Object> result = new HashMap<>();

        WikiDocument document = documentMapper.findByTitle(title);

        if (document == null) {
            result.put("exists", false);
            result.put("title", title);
            return result;
        }

        List<WikiRevision> revisions = revisionMapper.findByDocId(document.getDocId());

        result.put("exists", true);
        result.put("document", document);
        result.put("revisions", revisions);

        return result;
    }

    /**
     * 모든 문서 목록 조회
     */
    public List<WikiDocument> getAllDocuments() {
        return documentMapper.findAll();
    }

    /**
     * 문서 존재 여부 확인
     */
    public boolean documentExists(String title) {
        return documentMapper.existsByTitle(title);
    }
}
