package com.jell.namujsp.dao;

import com.jell.namujsp.model.WikiRevision;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * Wiki 문서 수정 히스토리 Mapper 인터페이스
 */
@Mapper
public interface WikiRevisionMapper {

    /**
     * 리비전 조회 by ID
     */
    WikiRevision findById(@Param("revisionId") Long revisionId);

    /**
     * 특정 문서의 모든 리비전 조회
     */
    List<WikiRevision> findByDocId(@Param("docId") Long docId);

    /**
     * 특정 문서의 최신 리비전 조회
     */
    WikiRevision findLatestByDocId(@Param("docId") Long docId);

    /**
     * 리비전 생성
     */
    int insert(WikiRevision revision);

    /**
     * 리비전 삭제
     */
    int delete(@Param("revisionId") Long revisionId);

    /**
     * 특정 문서의 리비전 개수
     */
    int countByDocId(@Param("docId") Long docId);
}
