package com.jell.namujsp.e2e;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.*;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Wiki E2E Test
 * Selenium WebDriver를 사용한 엔드-투-엔드 테스트
 *
 * 주의: 이 테스트를 실행하려면 애플리케이션 서버가 실행 중이어야 합니다.
 * 예: http://localhost:8080/namujsp
 *
 * 실행 방법:
 * 1. PostgreSQL 데이터베이스 시작
 * 2. 애플리케이션을 Tomcat에 배포 및 시작
 * 3. 테스트 실행: mvn test -Dtest=WikiE2ETest
 *
 * @Disabled 어노테이션을 제거하고 BASE_URL을 환경에 맞게 수정하세요.
 */
@Disabled("E2E 테스트는 애플리케이션 서버가 실행 중일 때만 활성화하세요")
@DisplayName("Wiki E2E 테스트")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class WikiE2ETest {

    // 애플리케이션이 실행 중인 URL (환경에 맞게 수정)
    private static final String BASE_URL = "http://localhost:8080/namujsp";

    private static WebDriver driver;
    private static WebDriverWait wait;

    @BeforeAll
    static void setUpDriver() {
        // WebDriverManager로 ChromeDriver 자동 관리
        WebDriverManager.chromedriver().setup();

        // Headless 모드 설정 (GUI 없이 실행)
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless");
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");
        options.addArguments("--disable-gpu");
        options.addArguments("--window-size=1920,1080");

        driver = new ChromeDriver(options);
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    @AfterAll
    static void tearDownDriver() {
        if (driver != null) {
            driver.quit();
        }
    }

    @Test
    @Order(1)
    @DisplayName("메인 페이지 접속")
    void testAccessMainPage() {
        // When
        driver.get(BASE_URL);

        // Then
        assertThat(driver.getTitle()).contains("나무JSP");
    }

    @Test
    @Order(2)
    @DisplayName("새 문서 생성")
    void testCreateNewDocument() {
        // Given
        String newDocTitle = "E2E테스트문서_" + System.currentTimeMillis();
        driver.get(BASE_URL + "/wiki/view/" + newDocTitle);

        // When - 문서가 없으면 자동으로 편집 페이지로 이동
        wait.until(ExpectedConditions.presenceOfElementLocated(By.id("content")));

        WebElement contentArea = driver.findElement(By.id("content"));
        WebElement editorNameField = driver.findElement(By.id("editorName"));
        WebElement commentField = driver.findElement(By.id("comment"));

        contentArea.clear();
        contentArea.sendKeys("== E2E 테스트 ==\n\n**굵은 글씨**와 //기울임//을 테스트합니다.\n\n* 첫 번째 항목\n* 두 번째 항목");
        editorNameField.sendKeys("E2E Tester");
        commentField.sendKeys("E2E 테스트로 생성");

        driver.findElement(By.cssSelector("button[type='submit']")).click();

        // Then - 문서 보기 페이지로 리다이렉트
        wait.until(ExpectedConditions.urlContains("/wiki/view/"));

        String pageSource = driver.getPageSource();
        assertThat(pageSource).contains("<h2>E2E 테스트</h2>");
        assertThat(pageSource).contains("<strong>굵은 글씨</strong>");
        assertThat(pageSource).contains("<em>기울임</em>");
    }

    @Test
    @Order(3)
    @DisplayName("문서 조회")
    void testViewDocument() {
        // Given - 먼저 문서 생성
        String docTitle = "조회테스트문서_" + System.currentTimeMillis();
        driver.get(BASE_URL + "/wiki/view/" + docTitle);

        wait.until(ExpectedConditions.presenceOfElementLocated(By.id("content")));
        WebElement contentArea = driver.findElement(By.id("content"));
        contentArea.sendKeys("조회 테스트 내용입니다.");
        driver.findElement(By.cssSelector("button[type='submit']")).click();

        // When - 문서 조회
        wait.until(ExpectedConditions.urlContains("/wiki/view/"));

        // Then
        assertThat(driver.findElement(By.tagName("h1")).getText()).isEqualTo(docTitle);
        assertThat(driver.getPageSource()).contains("조회 테스트 내용입니다.");
    }

    @Test
    @Order(4)
    @DisplayName("문서 편집")
    void testEditDocument() {
        // Given - 먼저 문서 생성
        String docTitle = "편집테스트문서_" + System.currentTimeMillis();
        driver.get(BASE_URL + "/wiki/view/" + docTitle);

        wait.until(ExpectedConditions.presenceOfElementLocated(By.id("content")));
        driver.findElement(By.id("content")).sendKeys("원본 내용");
        driver.findElement(By.cssSelector("button[type='submit']")).click();

        wait.until(ExpectedConditions.urlContains("/wiki/view/"));

        // When - 문서 편집
        driver.findElement(By.linkText("편집")).click();

        wait.until(ExpectedConditions.presenceOfElementLocated(By.id("content")));
        WebElement contentArea = driver.findElement(By.id("content"));

        assertThat(contentArea.getAttribute("value")).contains("원본 내용");

        contentArea.clear();
        contentArea.sendKeys("수정된 내용");
        driver.findElement(By.id("comment")).sendKeys("내용 수정");
        driver.findElement(By.cssSelector("button[type='submit']")).click();

        // Then
        wait.until(ExpectedConditions.urlContains("/wiki/view/"));
        assertThat(driver.getPageSource()).contains("수정된 내용");
        assertThat(driver.getPageSource()).doesNotContain("원본 내용");
    }

    @Test
    @Order(5)
    @DisplayName("문서 히스토리 조회")
    void testViewHistory() {
        // Given - 문서 생성 및 수정
        String docTitle = "히스토리테스트_" + System.currentTimeMillis();

        // 첫 번째 버전 생성
        driver.get(BASE_URL + "/wiki/view/" + docTitle);
        wait.until(ExpectedConditions.presenceOfElementLocated(By.id("content")));
        driver.findElement(By.id("content")).sendKeys("첫 번째 버전");
        driver.findElement(By.id("comment")).sendKeys("첫 생성");
        driver.findElement(By.cssSelector("button[type='submit']")).click();

        wait.until(ExpectedConditions.urlContains("/wiki/view/"));

        // 두 번째 버전 생성
        driver.findElement(By.linkText("편집")).click();
        wait.until(ExpectedConditions.presenceOfElementLocated(By.id("content")));
        driver.findElement(By.id("content")).clear();
        driver.findElement(By.id("content")).sendKeys("두 번째 버전");
        driver.findElement(By.id("comment")).sendKeys("수정함");
        driver.findElement(By.cssSelector("button[type='submit']")).click();

        wait.until(ExpectedConditions.urlContains("/wiki/view/"));

        // When - 히스토리 보기
        driver.findElement(By.linkText("히스토리")).click();

        // Then
        wait.until(ExpectedConditions.presenceOfElementLocated(By.tagName("table")));

        String pageSource = driver.getPageSource();
        assertThat(pageSource).contains("첫 생성");
        assertThat(pageSource).contains("수정함");

        // 테이블에 2개의 리비전이 있어야 함
        int revisionCount = driver.findElements(By.cssSelector("table tbody tr")).size();
        assertThat(revisionCount).isEqualTo(2);
    }

    @Test
    @Order(6)
    @DisplayName("Seed 문법 파싱 - 링크")
    void testSeedSyntaxLink() {
        // Given
        String docTitle = "링크테스트_" + System.currentTimeMillis();
        driver.get(BASE_URL + "/wiki/view/" + docTitle);

        wait.until(ExpectedConditions.presenceOfElementLocated(By.id("content")));
        driver.findElement(By.id("content")).sendKeys("[[메인페이지]]로 이동하세요");
        driver.findElement(By.cssSelector("button[type='submit']")).click();

        // When
        wait.until(ExpectedConditions.urlContains("/wiki/view/"));

        // Then
        WebElement link = wait.until(ExpectedConditions.presenceOfElementLocated(
            By.cssSelector("a.wiki-link[href='/wiki/view/메인페이지']")
        ));

        assertThat(link.getText()).isEqualTo("메인페이지");
        assertThat(link.getAttribute("href")).endsWith("/wiki/view/메인페이지");
    }

    @Test
    @Order(7)
    @DisplayName("문서 목록 조회")
    void testViewDocumentList() {
        // When
        driver.get(BASE_URL + "/wiki");

        // Then
        wait.until(ExpectedConditions.presenceOfElementLocated(By.tagName("table")));

        String pageSource = driver.getPageSource();
        assertThat(pageSource).contains("모든 문서 목록");

        // 테이블이 존재하는지 확인
        assertThat(driver.findElements(By.cssSelector("table tbody tr")).size())
            .isGreaterThan(0);
    }

    @Test
    @Order(8)
    @DisplayName("익명 편집자 처리")
    void testAnonymousEditor() {
        // Given
        String docTitle = "익명테스트_" + System.currentTimeMillis();
        driver.get(BASE_URL + "/wiki/view/" + docTitle);

        wait.until(ExpectedConditions.presenceOfElementLocated(By.id("content")));

        // When - 편집자 이름을 입력하지 않고 저장
        driver.findElement(By.id("content")).sendKeys("익명 편집 테스트");
        // editorName 필드는 비워둠
        driver.findElement(By.cssSelector("button[type='submit']")).click();

        // Then - 문서 보기 페이지에서 "익명"으로 표시되어야 함
        wait.until(ExpectedConditions.urlContains("/wiki/view/"));
        String metaInfo = driver.findElement(By.className("wiki-meta")).getText();
        assertThat(metaInfo).contains("익명");
    }
}
