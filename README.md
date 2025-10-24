# NamuJSP - Seed Engine Wiki

공부용 프로젝트입니다. This repository is only for study.

나무위키의 Seed 엔진을 모방한 위키 시스템입니다. Java Spring MVC와 PostgreSQL을 사용하여 구현되었습니다.

## 기술 스택

### Production
- **Java**: 21 (최신 LTS)
- **Framework**: Spring 6.1.14 (Spring MVC)
- **Jakarta EE**: Jakarta Servlet 6.0, Jakarta JSTL 3.0
- **Database**: PostgreSQL 16
- **ORM**: MyBatis 3.5.16
- **Connection Pool**: HikariCP 6.0
- **View**: JSP + JSTL
- **Build Tool**: Maven

### Testing
- **Unit Test**: JUnit 5.11, Mockito 5.14, AssertJ 3.26
- **Integration Test**: TestContainers 1.20 (PostgreSQL)
- **E2E Test**: Selenium WebDriver 4.26
- **Code Coverage**: JaCoCo 0.8.12

## 주요 기능

### 1. 위키 문서 관리
- 문서 생성, 조회, 편집
- 문서 히스토리 관리
- 버전별 수정 내역 추적

### 2. Seed 엔진 문법 지원
- **굵게**: `**텍스트**` → **텍스트**
- **기울임**: `//텍스트//` → *텍스트*
- **취소선**: `~~텍스트~~` → ~~텍스트~~
- **밑줄**: `__텍스트__` → <u>텍스트</u>
- **링크**: `[[문서명]]` → 다른 문서로 링크
- **코드**: `{{{코드}}}` → `코드`
- **제목**: `= 제목 =`, `== 제목 ==`, `=== 제목 ===`
- **목록**: `* 항목` (순서 없는 목록), `1. 항목` (순서 있는 목록)

### 3. 편집 기능
- 익명 편집 지원 (편집자 이름 선택 입력)
- IP 주소 자동 기록
- 수정 코멘트 작성

## 프로젝트 구조

```
namujsp/
├── src/main/java/com/jell/namujsp/
│   ├── controller/        # Spring MVC 컨트롤러
│   │   ├── HomeController.java
│   │   └── WikiController.java
│   ├── service/          # 비즈니스 로직
│   │   └── WikiService.java
│   ├── dao/              # MyBatis Mapper 인터페이스
│   │   ├── WikiDocumentMapper.java
│   │   └── WikiRevisionMapper.java
│   ├── model/            # 엔티티 클래스
│   │   ├── WikiDocument.java
│   │   └── WikiRevision.java
│   └── util/             # 유틸리티
│       └── SeedParser.java    # Seed 문법 파서
├── src/main/resources/
│   ├── mappers/          # MyBatis XML 매퍼
│   │   ├── WikiDocumentMapper.xml
│   │   └── WikiRevisionMapper.xml
│   ├── sql/
│   │   └── schema.sql    # 데이터베이스 스키마
│   └── database.properties
├── src/main/webapp/
│   └── WEB-INF/
│       ├── spring/       # Spring 설정
│       │   ├── root-context.xml
│       │   └── servlet-context.xml
│       ├── views/wiki/   # JSP 뷰
│       │   ├── header.jsp
│       │   ├── footer.jsp
│       │   ├── index.jsp
│       │   ├── view.jsp
│       │   ├── edit.jsp
│       │   └── history.jsp
│       └── web.xml
└── pom.xml
```

## 데이터베이스 스키마

### wiki_document
문서 정보를 저장하는 테이블
- `doc_id`: 문서 ID (Primary Key)
- `title`: 문서 제목 (Unique)
- `current_revision_id`: 현재 리비전 ID
- `created_at`: 생성 시간
- `updated_at`: 수정 시간

### wiki_revision
문서 수정 히스토리를 저장하는 테이블
- `revision_id`: 리비전 ID (Primary Key)
- `doc_id`: 문서 ID (Foreign Key)
- `content`: 문서 내용 (Seed 문법)
- `editor_name`: 편집자 이름
- `editor_ip`: 편집자 IP
- `revision_comment`: 수정 코멘트
- `created_at`: 생성 시간

## 설치 및 실행

### 1. 사전 요구사항
- Java 21 이상
- PostgreSQL 16 이상
- Maven 3.9 이상
- Tomcat 10.1 이상 (Jakarta EE 10 지원 필요)

### 2. 데이터베이스 설정

PostgreSQL에 데이터베이스를 생성합니다:
```sql
CREATE DATABASE namujsp;
```

`src/main/resources/sql/schema.sql` 파일을 실행하여 테이블을 생성합니다:
```bash
psql -U postgres -d namujsp -f src/main/resources/sql/schema.sql
```

### 3. 데이터베이스 연결 설정

`src/main/resources/database.properties` 파일을 수정합니다:
```properties
db.driver=org.postgresql.Driver
db.url=jdbc:postgresql://localhost:5432/namujsp
db.username=your_username
db.password=your_password
```

### 4. 빌드 및 배포

Maven으로 빌드합니다:
```bash
cd namujsp
mvn clean package
```

생성된 WAR 파일을 Tomcat에 배포합니다:
```bash
cp target/namujsp.war $TOMCAT_HOME/webapps/
```

### 5. 접속

브라우저에서 접속합니다:
```
http://localhost:8080/namujsp/
```

## 주요 URL

- `/` - 메인페이지
- `/wiki` - 모든 문서 목록
- `/wiki/view/{제목}` - 문서 보기
- `/wiki/edit/{제목}` - 문서 편집
- `/wiki/history/{제목}` - 문서 히스토리

## 테스트

### 테스트 실행

전체 테스트 실행:
```bash
cd namujsp
mvn clean test
```

Unit Test만 실행:
```bash
mvn test
```

Integration Test 실행 (TestContainers 사용):
```bash
mvn verify
```

Code Coverage 리포트 생성:
```bash
mvn clean test jacoco:report
# 리포트 확인: target/site/jacoco/index.html
```

### 테스트 구조

**1. Unit Tests** (TDD 방식)
- `SeedParserTest`: Seed 문법 파서 테스트 (20+ test cases)
- `WikiServiceTest`: 서비스 로직 테스트 (Mockito 사용)

**2. Integration Tests** (TestContainers)
- `WikiDocumentMapperIntegrationTest`: 실제 PostgreSQL 컨테이너와 DAO 레이어 통합 테스트

**3. E2E Tests** (Selenium WebDriver)
- `WikiE2ETest`: 브라우저 자동화 테스트
  - 문서 생성, 조회, 편집, 히스토리 전체 플로우 테스트
  - 주의: 애플리케이션 서버가 실행 중이어야 함

### 테스트 커버리지

JaCoCo를 사용하여 최소 50% 라인 커버리지를 유지합니다.

```bash
mvn clean test
# 커버리지 리포트: target/site/jacoco/index.html
```

### E2E 테스트 활성화

E2E 테스트는 기본적으로 `@Disabled` 상태입니다. 활성화하려면:

1. 애플리케이션을 Tomcat에 배포하고 시작
2. `WikiE2ETest.java`에서 `@Disabled` 어노테이션 제거
3. `BASE_URL`을 실제 서버 주소로 수정
4. 테스트 실행: `mvn test -Dtest=WikiE2ETest`

## 향후 확장 기능

- [ ] 사용자 인증 시스템
- [ ] 파일 업로드 (이미지 첨부)
- [ ] 더 많은 Seed 문법 지원 (테이블, 인용구, 각주 등)
- [ ] 문서 검색 기능
- [ ] 리비전 비교 (diff)
- [ ] 문서 삭제 및 복구
- [ ] 카테고리 시스템
- [ ] 최근 변경 내역
- [x] ~~완전한 테스트 커버리지 (Unit, Integration, E2E)~~

## 라이선스

MIT License - 학습용 프로젝트입니다.
