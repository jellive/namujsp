package com.jell.namujsp.service;

import com.jell.namujsp.dao.WikiDocumentMapper;
import com.jell.namujsp.dao.WikiRevisionMapper;
import com.jell.namujsp.model.WikiDocument;
import com.jell.namujsp.model.WikiRevision;
import com.jell.namujsp.util.SeedParser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * WikiService Unit Test
 * Mockito를 사용한 서비스 레이어 테스트
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("WikiService 테스트")
class WikiServiceTest {

    @Mock
    private WikiDocumentMapper documentMapper;

    @Mock
    private WikiRevisionMapper revisionMapper;

    @Mock
    private SeedParser seedParser;

    @InjectMocks
    private WikiService wikiService;

    private WikiDocument testDocument;
    private WikiRevision testRevision;

    @BeforeEach
    void setUp() {
        testDocument = new WikiDocument();
        testDocument.setDocId(1L);
        testDocument.setTitle("테스트문서");
        testDocument.setCurrentRevisionId(1L);
        testDocument.setCreatedAt(LocalDateTime.now());
        testDocument.setUpdatedAt(LocalDateTime.now());

        testRevision = new WikiRevision();
        testRevision.setRevisionId(1L);
        testRevision.setDocId(1L);
        testRevision.setContent("**테스트 내용**");
        testRevision.setEditorName("테스터");
        testRevision.setEditorIp("127.0.0.1");
        testRevision.setRevisionComment("테스트 수정");
        testRevision.setCreatedAt(LocalDateTime.now());
    }

    @Test
    @DisplayName("존재하는 문서 조회 성공")
    void testGetDocument_Success() {
        // Given
        when(documentMapper.findByTitle("테스트문서")).thenReturn(testDocument);
        when(revisionMapper.findLatestByDocId(1L)).thenReturn(testRevision);
        when(seedParser.parse("**테스트 내용**")).thenReturn("<strong>테스트 내용</strong>");

        // When
        Map<String, Object> result = wikiService.getDocument("테스트문서");

        // Then
        assertThat(result.get("exists")).isEqualTo(true);
        assertThat(result.get("document")).isEqualTo(testDocument);
        assertThat(result.get("revision")).isEqualTo(testRevision);
        assertThat(result.get("htmlContent")).isEqualTo("<strong>테스트 내용</strong>");
        assertThat(result.get("rawContent")).isEqualTo("**테스트 내용**");

        verify(documentMapper, times(1)).findByTitle("테스트문서");
        verify(revisionMapper, times(1)).findLatestByDocId(1L);
        verify(seedParser, times(1)).parse("**테스트 내용**");
    }

    @Test
    @DisplayName("존재하지 않는 문서 조회시 exists=false")
    void testGetDocument_NotFound() {
        // Given
        when(documentMapper.findByTitle("없는문서")).thenReturn(null);

        // When
        Map<String, Object> result = wikiService.getDocument("없는문서");

        // Then
        assertThat(result.get("exists")).isEqualTo(false);
        assertThat(result.get("title")).isEqualTo("없는문서");

        verify(documentMapper, times(1)).findByTitle("없는문서");
        verify(revisionMapper, never()).findLatestByDocId(anyLong());
    }

    @Test
    @DisplayName("새 문서 저장 성공")
    void testSaveDocument_NewDocument() {
        // Given
        when(documentMapper.findByTitle("새문서")).thenReturn(null);
        when(documentMapper.insert(any(WikiDocument.class))).thenAnswer(invocation -> {
            WikiDocument doc = invocation.getArgument(0);
            doc.setDocId(2L);
            return 1;
        });
        when(revisionMapper.insert(any(WikiRevision.class))).thenAnswer(invocation -> {
            WikiRevision rev = invocation.getArgument(0);
            rev.setRevisionId(2L);
            return 1;
        });

        // When
        wikiService.saveDocument("새문서", "새 내용", "작성자", "192.168.1.1", "새 문서 작성");

        // Then
        ArgumentCaptor<WikiDocument> docCaptor = ArgumentCaptor.forClass(WikiDocument.class);
        ArgumentCaptor<WikiRevision> revCaptor = ArgumentCaptor.forClass(WikiRevision.class);

        verify(documentMapper).insert(docCaptor.capture());
        verify(revisionMapper).insert(revCaptor.capture());
        verify(documentMapper).updateCurrentRevisionId(2L, 2L);

        WikiDocument capturedDoc = docCaptor.getValue();
        assertThat(capturedDoc.getTitle()).isEqualTo("새문서");

        WikiRevision capturedRev = revCaptor.getValue();
        assertThat(capturedRev.getContent()).isEqualTo("새 내용");
        assertThat(capturedRev.getEditorName()).isEqualTo("작성자");
        assertThat(capturedRev.getEditorIp()).isEqualTo("192.168.1.1");
        assertThat(capturedRev.getRevisionComment()).isEqualTo("새 문서 작성");
    }

    @Test
    @DisplayName("기존 문서 수정 성공")
    void testSaveDocument_UpdateExisting() {
        // Given
        when(documentMapper.findByTitle("테스트문서")).thenReturn(testDocument);
        when(revisionMapper.insert(any(WikiRevision.class))).thenAnswer(invocation -> {
            WikiRevision rev = invocation.getArgument(0);
            rev.setRevisionId(3L);
            return 1;
        });

        // When
        wikiService.saveDocument("테스트문서", "수정된 내용", "편집자", "10.0.0.1", "내용 수정");

        // Then
        ArgumentCaptor<WikiRevision> revCaptor = ArgumentCaptor.forClass(WikiRevision.class);

        verify(documentMapper, never()).insert(any(WikiDocument.class));
        verify(revisionMapper).insert(revCaptor.capture());
        verify(documentMapper).updateCurrentRevisionId(1L, 3L);

        WikiRevision capturedRev = revCaptor.getValue();
        assertThat(capturedRev.getDocId()).isEqualTo(1L);
        assertThat(capturedRev.getContent()).isEqualTo("수정된 내용");
    }

    @Test
    @DisplayName("편집자 이름이 없으면 '익명'으로 저장")
    void testSaveDocument_AnonymousEditor() {
        // Given
        when(documentMapper.findByTitle("테스트문서")).thenReturn(testDocument);
        when(revisionMapper.insert(any(WikiRevision.class))).thenReturn(1);

        // When
        wikiService.saveDocument("테스트문서", "내용", null, "127.0.0.1", null);

        // Then
        ArgumentCaptor<WikiRevision> revCaptor = ArgumentCaptor.forClass(WikiRevision.class);
        verify(revisionMapper).insert(revCaptor.capture());

        WikiRevision capturedRev = revCaptor.getValue();
        assertThat(capturedRev.getEditorName()).isEqualTo("익명");
    }

    @Test
    @DisplayName("편집자 이름이 빈 문자열이면 '익명'으로 저장")
    void testSaveDocument_EmptyEditorName() {
        // Given
        when(documentMapper.findByTitle("테스트문서")).thenReturn(testDocument);
        when(revisionMapper.insert(any(WikiRevision.class))).thenReturn(1);

        // When
        wikiService.saveDocument("테스트문서", "내용", "", "127.0.0.1", null);

        // Then
        ArgumentCaptor<WikiRevision> revCaptor = ArgumentCaptor.forClass(WikiRevision.class);
        verify(revisionMapper).insert(revCaptor.capture());

        WikiRevision capturedRev = revCaptor.getValue();
        assertThat(capturedRev.getEditorName()).isEqualTo("익명");
    }

    @Test
    @DisplayName("편집용 문서 조회 - 존재하는 문서")
    void testGetDocumentForEdit_Exists() {
        // Given
        when(documentMapper.findByTitle("테스트문서")).thenReturn(testDocument);
        when(revisionMapper.findLatestByDocId(1L)).thenReturn(testRevision);

        // When
        Map<String, Object> result = wikiService.getDocumentForEdit("테스트문서");

        // Then
        assertThat(result.get("exists")).isEqualTo(true);
        assertThat(result.get("document")).isEqualTo(testDocument);
        assertThat(result.get("content")).isEqualTo("**테스트 내용**");
    }

    @Test
    @DisplayName("편집용 문서 조회 - 존재하지 않는 문서")
    void testGetDocumentForEdit_NotExists() {
        // Given
        when(documentMapper.findByTitle("새문서")).thenReturn(null);

        // When
        Map<String, Object> result = wikiService.getDocumentForEdit("새문서");

        // Then
        assertThat(result.get("exists")).isEqualTo(false);
        assertThat(result.get("title")).isEqualTo("새문서");
        assertThat(result.get("content")).isEqualTo("");
    }

    @Test
    @DisplayName("문서 히스토리 조회 성공")
    void testGetHistory_Success() {
        // Given
        WikiRevision rev1 = new WikiRevision();
        rev1.setRevisionId(1L);
        WikiRevision rev2 = new WikiRevision();
        rev2.setRevisionId(2L);
        List<WikiRevision> revisions = Arrays.asList(rev1, rev2);

        when(documentMapper.findByTitle("테스트문서")).thenReturn(testDocument);
        when(revisionMapper.findByDocId(1L)).thenReturn(revisions);

        // When
        Map<String, Object> result = wikiService.getHistory("테스트문서");

        // Then
        assertThat(result.get("exists")).isEqualTo(true);
        assertThat(result.get("document")).isEqualTo(testDocument);
        assertThat(result.get("revisions")).isEqualTo(revisions);
    }

    @Test
    @DisplayName("문서 히스토리 조회 - 존재하지 않는 문서")
    void testGetHistory_NotFound() {
        // Given
        when(documentMapper.findByTitle("없는문서")).thenReturn(null);

        // When
        Map<String, Object> result = wikiService.getHistory("없는문서");

        // Then
        assertThat(result.get("exists")).isEqualTo(false);
        assertThat(result.get("title")).isEqualTo("없는문서");
    }

    @Test
    @DisplayName("모든 문서 목록 조회")
    void testGetAllDocuments() {
        // Given
        WikiDocument doc1 = new WikiDocument();
        doc1.setTitle("문서1");
        WikiDocument doc2 = new WikiDocument();
        doc2.setTitle("문서2");
        List<WikiDocument> documents = Arrays.asList(doc1, doc2);

        when(documentMapper.findAll()).thenReturn(documents);

        // When
        List<WikiDocument> result = wikiService.getAllDocuments();

        // Then
        assertThat(result).hasSize(2);
        assertThat(result).containsExactly(doc1, doc2);
        verify(documentMapper, times(1)).findAll();
    }

    @Test
    @DisplayName("문서 존재 여부 확인 - 존재함")
    void testDocumentExists_True() {
        // Given
        when(documentMapper.existsByTitle("테스트문서")).thenReturn(true);

        // When
        boolean exists = wikiService.documentExists("테스트문서");

        // Then
        assertThat(exists).isTrue();
        verify(documentMapper, times(1)).existsByTitle("테스트문서");
    }

    @Test
    @DisplayName("문서 존재 여부 확인 - 존재하지 않음")
    void testDocumentExists_False() {
        // Given
        when(documentMapper.existsByTitle("없는문서")).thenReturn(false);

        // When
        boolean exists = wikiService.documentExists("없는문서");

        // Then
        assertThat(exists).isFalse();
        verify(documentMapper, times(1)).existsByTitle("없는문서");
    }
}
