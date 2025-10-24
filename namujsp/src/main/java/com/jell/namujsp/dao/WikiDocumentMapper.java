package com.jell.namujsp.dao;

import com.jell.namujsp.model.WikiDocument;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * Wiki 문서 Mapper 인터페이스
 */
@Mapper
public interface WikiDocumentMapper {

    /**
     * 문서 조회 by ID
     */
    WikiDocument findById(@Param("docId") Long docId);

    /**
     * 문서 조회 by 제목
     */
    WikiDocument findByTitle(@Param("title") String title);

    /**
     * 모든 문서 목록 조회
     */
    List<WikiDocument> findAll();

    /**
     * 문서 생성
     */
    int insert(WikiDocument document);

    /**
     * 문서 업데이트
     */
    int update(WikiDocument document);

    /**
     * current_revision_id 업데이트
     */
    int updateCurrentRevisionId(@Param("docId") Long docId, @Param("revisionId") Long revisionId);

    /**
     * 문서 삭제
     */
    int delete(@Param("docId") Long docId);

    /**
     * 문서 존재 여부 확인
     */
    boolean existsByTitle(@Param("title") String title);
}
