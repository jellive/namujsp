package com.jell.namujsp.model;

import java.time.LocalDateTime;

/**
 * Wiki 문서 수정 히스토리 엔티티
 */
public class WikiRevision {
    private Long revisionId;
    private Long docId;
    private String content;
    private String editorName;
    private String editorIp;
    private String revisionComment;
    private LocalDateTime createdAt;

    // 기본 생성자
    public WikiRevision() {
    }

    // 생성자
    public WikiRevision(Long docId, String content, String editorName, String editorIp, String revisionComment) {
        this.docId = docId;
        this.content = content;
        this.editorName = editorName;
        this.editorIp = editorIp;
        this.revisionComment = revisionComment;
    }

    // Getters and Setters
    public Long getRevisionId() {
        return revisionId;
    }

    public void setRevisionId(Long revisionId) {
        this.revisionId = revisionId;
    }

    public Long getDocId() {
        return docId;
    }

    public void setDocId(Long docId) {
        this.docId = docId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getEditorName() {
        return editorName;
    }

    public void setEditorName(String editorName) {
        this.editorName = editorName;
    }

    public String getEditorIp() {
        return editorIp;
    }

    public void setEditorIp(String editorIp) {
        this.editorIp = editorIp;
    }

    public String getRevisionComment() {
        return revisionComment;
    }

    public void setRevisionComment(String revisionComment) {
        this.revisionComment = revisionComment;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return "WikiRevision{" +
                "revisionId=" + revisionId +
                ", docId=" + docId +
                ", content='" + content + '\'' +
                ", editorName='" + editorName + '\'' +
                ", editorIp='" + editorIp + '\'' +
                ", revisionComment='" + revisionComment + '\'' +
                ", createdAt=" + createdAt +
                '}';
    }
}
