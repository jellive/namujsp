package com.jell.namujsp.dao;

import com.jell.namujsp.model.WikiDocument;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.junit.jupiter.api.*;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.mapper.MapperFactoryBean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.Statement;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * WikiDocumentMapper Integration Test
 * TestContainers를 사용한 실제 PostgreSQL 데이터베이스 테스트
 */
@Testcontainers
@DisplayName("WikiDocumentMapper Integration 테스트")
class WikiDocumentMapperIntegrationTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine")
            .withDatabaseName("testdb")
            .withUsername("test")
            .withPassword("test");

    private static WikiDocumentMapper documentMapper;
    private static DataSource dataSource;

    @BeforeAll
    static void setUpDatabase() throws Exception {
        // HikariCP DataSource 설정
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(postgres.getJdbcUrl());
        config.setUsername(postgres.getUsername());
        config.setPassword(postgres.getPassword());
        config.setDriverClassName("org.postgresql.Driver");
        config.setMaximumPoolSize(5);

        dataSource = new HikariDataSource(config);

        // 스키마 초기화
        try (Connection conn = dataSource.getConnection();
             Statement stmt = conn.createStatement()) {
            // schema.sql 파일 실행
            String schemaSql = """
                CREATE TABLE IF NOT EXISTS wiki_document (
                    doc_id SERIAL PRIMARY KEY,
                    title VARCHAR(255) NOT NULL UNIQUE,
                    current_revision_id INTEGER,
                    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
                );

                CREATE INDEX IF NOT EXISTS idx_wiki_document_title ON wiki_document(title);
                """;
            stmt.execute(schemaSql);
        }

        // MyBatis SqlSessionFactory 설정
        SqlSessionFactoryBean sessionFactory = new SqlSessionFactoryBean();
        sessionFactory.setDataSource(dataSource);
        sessionFactory.setTypeAliasesPackage("com.jell.namujsp.model");

        // Mapper XML 파일 설정
        PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        sessionFactory.setMapperLocations(resolver.getResources("classpath:mappers/**/*.xml"));

        // MyBatis Configuration
        org.apache.ibatis.session.Configuration mybatisConfig = new org.apache.ibatis.session.Configuration();
        mybatisConfig.setMapUnderscoreToCamelCase(true);
        sessionFactory.setConfiguration(mybatisConfig);

        // Mapper 생성
        MapperFactoryBean<WikiDocumentMapper> mapperFactory = new MapperFactoryBean<>(WikiDocumentMapper.class);
        mapperFactory.setSqlSessionFactory(sessionFactory.getObject());
        mapperFactory.afterPropertiesSet();
        documentMapper = mapperFactory.getObject();
    }

    @BeforeEach
    void cleanDatabase() throws Exception {
        try (Connection conn = dataSource.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute("TRUNCATE TABLE wiki_document RESTART IDENTITY CASCADE");
        }
    }

    @Test
    @DisplayName("문서 삽입 및 조회")
    void testInsertAndFindById() {
        // Given
        WikiDocument document = new WikiDocument("테스트문서");

        // When
        int result = documentMapper.insert(document);

        // Then
        assertThat(result).isEqualTo(1);
        assertThat(document.getDocId()).isNotNull();

        WikiDocument found = documentMapper.findById(document.getDocId());
        assertThat(found).isNotNull();
        assertThat(found.getTitle()).isEqualTo("테스트문서");
    }

    @Test
    @DisplayName("제목으로 문서 조회")
    void testFindByTitle() {
        // Given
        WikiDocument document = new WikiDocument("메인페이지");
        documentMapper.insert(document);

        // When
        WikiDocument found = documentMapper.findByTitle("메인페이지");

        // Then
        assertThat(found).isNotNull();
        assertThat(found.getTitle()).isEqualTo("메인페이지");
        assertThat(found.getDocId()).isNotNull();
    }

    @Test
    @DisplayName("모든 문서 조회")
    void testFindAll() {
        // Given
        documentMapper.insert(new WikiDocument("문서1"));
        documentMapper.insert(new WikiDocument("문서2"));
        documentMapper.insert(new WikiDocument("문서3"));

        // When
        List<WikiDocument> documents = documentMapper.findAll();

        // Then
        assertThat(documents).hasSize(3);
        assertThat(documents)
            .extracting(WikiDocument::getTitle)
            .containsExactlyInAnyOrder("문서1", "문서2", "문서3");
    }

    @Test
    @DisplayName("문서 업데이트")
    void testUpdate() {
        // Given
        WikiDocument document = new WikiDocument("원본제목");
        documentMapper.insert(document);

        // When
        document.setTitle("수정된제목");
        int result = documentMapper.update(document);

        // Then
        assertThat(result).isEqualTo(1);

        WikiDocument updated = documentMapper.findById(document.getDocId());
        assertThat(updated.getTitle()).isEqualTo("수정된제목");
    }

    @Test
    @DisplayName("current_revision_id 업데이트")
    void testUpdateCurrentRevisionId() {
        // Given
        WikiDocument document = new WikiDocument("테스트");
        documentMapper.insert(document);

        // When
        int result = documentMapper.updateCurrentRevisionId(document.getDocId(), 99L);

        // Then
        assertThat(result).isEqualTo(1);

        WikiDocument updated = documentMapper.findById(document.getDocId());
        assertThat(updated.getCurrentRevisionId()).isEqualTo(99L);
    }

    @Test
    @DisplayName("문서 삭제")
    void testDelete() {
        // Given
        WikiDocument document = new WikiDocument("삭제할문서");
        documentMapper.insert(document);
        Long docId = document.getDocId();

        // When
        int result = documentMapper.delete(docId);

        // Then
        assertThat(result).isEqualTo(1);

        WikiDocument deleted = documentMapper.findById(docId);
        assertThat(deleted).isNull();
    }

    @Test
    @DisplayName("문서 존재 여부 확인 - 존재함")
    void testExistsByTitle_True() {
        // Given
        documentMapper.insert(new WikiDocument("존재하는문서"));

        // When
        boolean exists = documentMapper.existsByTitle("존재하는문서");

        // Then
        assertThat(exists).isTrue();
    }

    @Test
    @DisplayName("문서 존재 여부 확인 - 존재하지 않음")
    void testExistsByTitle_False() {
        // When
        boolean exists = documentMapper.existsByTitle("없는문서");

        // Then
        assertThat(exists).isFalse();
    }

    @Test
    @DisplayName("동일한 제목의 문서는 중복 삽입 불가")
    void testUniqueConstraint() {
        // Given
        documentMapper.insert(new WikiDocument("중복제목"));

        // When & Then
        try {
            documentMapper.insert(new WikiDocument("중복제목"));
            Assertions.fail("Should throw duplicate key exception");
        } catch (Exception e) {
            // 중복 키 예외 발생 예상
            assertThat(e.getMessage()).containsIgnoringCase("duplicate");
        }
    }
}
