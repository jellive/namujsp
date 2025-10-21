-- NamuJSP Database Schema for PostgreSQL
-- Seed Engine Wiki System

-- Wiki 문서 테이블
CREATE TABLE IF NOT EXISTS wiki_document (
    doc_id SERIAL PRIMARY KEY,
    title VARCHAR(255) NOT NULL UNIQUE,
    current_revision_id INTEGER,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Wiki 문서 수정 히스토리 테이블
CREATE TABLE IF NOT EXISTS wiki_revision (
    revision_id SERIAL PRIMARY KEY,
    doc_id INTEGER NOT NULL,
    content TEXT NOT NULL,
    editor_name VARCHAR(100),  -- 나중에 인증 추가시 user_id로 변경 가능
    editor_ip VARCHAR(45),     -- IPv6 지원
    revision_comment VARCHAR(500),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (doc_id) REFERENCES wiki_document(doc_id) ON DELETE CASCADE
);

-- 인덱스 생성
CREATE INDEX IF NOT EXISTS idx_wiki_document_title ON wiki_document(title);
CREATE INDEX IF NOT EXISTS idx_wiki_revision_doc_id ON wiki_revision(doc_id);
CREATE INDEX IF NOT EXISTS idx_wiki_revision_created_at ON wiki_revision(created_at DESC);

-- 샘플 데이터 삽입
INSERT INTO wiki_document (title, created_at, updated_at) VALUES
('메인페이지', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('나무위키:대문', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('샘플문서', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
ON CONFLICT (title) DO NOTHING;

INSERT INTO wiki_revision (doc_id, content, editor_name, editor_ip, revision_comment) VALUES
(1, '== 환영합니다 ==
'''나무JSP'''에 오신 것을 환영합니다!

이 위키는 나무위키의 seed 엔진을 모방한 학습용 프로젝트입니다.

=== 주요 기능 ===
* [[문서명]] - 문서 링크
* **굵은 글씨**
* //기울임//
* ~~취소선~~
* 제목과 소제목

[[샘플문서]]를 확인해보세요!', 'System', '127.0.0.1', '초기 문서 생성'),

(2, '== 나무위키 대문 ==
이것은 나무위키 스타일의 대문입니다.

=== 문법 테스트 ===
**굵은 글씨**와 //기울임//을 테스트합니다.
~~취소선도 가능합니다~~', 'System', '127.0.0.1', '대문 생성'),

(3, '== 샘플 문서 ==

=== Seed 엔진 문법 예제 ===

==== 텍스트 스타일 ====
* **굵은 글씨**
* //기울임체//
* ~~취소선~~
* **//굵은 기울임//**

==== 링크 ====
* [[메인페이지]]
* [[나무위키:대문]]

==== 제목 ====
= 큰 제목 =
== 중간 제목 ==
=== 작은 제목 ===

이 문서는 seed 엔진 문법의 예제를 보여줍니다.', 'Admin', '127.0.0.1', '샘플 문서 생성')
ON CONFLICT DO NOTHING;

-- wiki_document의 current_revision_id 업데이트
UPDATE wiki_document SET current_revision_id = 1 WHERE doc_id = 1;
UPDATE wiki_document SET current_revision_id = 2 WHERE doc_id = 2;
UPDATE wiki_document SET current_revision_id = 3 WHERE doc_id = 3;

-- 향후 확장을 위한 테이블 (현재는 주석 처리)
-- 사용자 인증 기능 추가시 사용
/*
CREATE TABLE IF NOT EXISTS wiki_user (
    user_id SERIAL PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    email VARCHAR(255),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    last_login TIMESTAMP
);
*/
