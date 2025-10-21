package com.jell.namujsp.model;

import java.time.LocalDateTime;

/**
 * Wiki 문서 엔티티
 */
public class WikiDocument {
    private Long docId;
    private String title;
    private Long currentRevisionId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // 기본 생성자
    public WikiDocument() {
    }

    // 생성자
    public WikiDocument(String title) {
        this.title = title;
    }

    // Getters and Setters
    public Long getDocId() {
        return docId;
    }

    public void setDocId(Long docId) {
        this.docId = docId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Long getCurrentRevisionId() {
        return currentRevisionId;
    }

    public void setCurrentRevisionId(Long currentRevisionId) {
        this.currentRevisionId = currentRevisionId;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    @Override
    public String toString() {
        return "WikiDocument{" +
                "docId=" + docId +
                ", title='" + title + '\'' +
                ", currentRevisionId=" + currentRevisionId +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}
