package com.trocap.selenium;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.*;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.*;

import java.io.*;
import java.nio.file.*;
import java.time.Duration;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Black Box Test Cases - JUnit 5 + Selenium WebDriver
 * =====================================================
 * TC04 - Để trống Username
 * TC05 - Để trống Password
 * TC06 - Để trống cả Username & Password
 * TC21 - Nộp hồ sơ thành công
 *
 * Yêu cầu trước khi chạy:
 *   - Frontend đang chạy tại http://localhost:5175
 *   - Backend  đang chạy tại http://localhost:8080
 *   - Tài khoản CITIZEN hợp lệ tồn tại trong DB
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class BlackBoxSeleniumTest {

    // ── Cấu hình ──────────────────────────────────────────────────────────
    private static final String BASE_URL         = "http://localhost:5175";
    private static final String LOGIN_URL        = BASE_URL + "/login";
    private static final String PORTAL_APPLY_URL = BASE_URL + "/portal/nop-ho-so";

    // Tài khoản CITIZEN hợp lệ (theo docs/test-data.md - U-04)
    private static final String CITIZEN_USER = "citizen1";
    private static final String CITIZEN_PASS = "123456";

    private static final Duration TIMEOUT = Duration.ofSeconds(10);

    // ── Tốc độ chạy — tăng để dễ quan sát ───────────────────────────────
    private static final int STEP_DELAY   = 1500;  // ms giữa các bước
    private static final int ACTION_DELAY = 800;   // ms sau mỗi thao tác nhỏ
    private static final int TYPE_DELAY   = 150;   // ms giữa các ký tự khi gõ

    /** Dừng giữa các bước để dễ quan sát */
    private void slow() throws InterruptedException {
        Thread.sleep(STEP_DELAY);
    }

    /** Gõ từng ký tự chậm để dễ thấy */
    private void slowType(WebElement el, String text) throws InterruptedException {
        el.clear();
        for (char c : text.toCharArray()) {
            el.sendKeys(String.valueOf(c));
            Thread.sleep(TYPE_DELAY);
        }
    }

    // ── Mỗi test dùng driver riêng ────────────────────────────────────────
    private WebDriver driver;
    private WebDriverWait wait;

    @BeforeEach
    void setUp() {
        WebDriverManager.chromedriver().setup(); // tự tải ChromeDriver đúng phiên bản

        ChromeOptions opts = new ChromeOptions();
        // opts.addArguments("--headless");      // bỏ comment nếu muốn chạy ẩn
        opts.addArguments("--no-sandbox");
        opts.addArguments("--disable-dev-shm-usage");
        opts.addArguments("--window-size=1280,900");

        driver = new ChromeDriver(opts);
        wait   = new WebDriverWait(driver, TIMEOUT);
    }

    @AfterEach
    void tearDown() {
        if (driver != null) driver.quit();
    }

    // ── Helper: mở trang login và chờ form sẵn sàng ──────────────────────
    private void openLoginPage() {
        driver.get(LOGIN_URL);
        wait.until(ExpectedConditions.presenceOfElementLocated(
                By.cssSelector("form.auth-form")));
    }

    // ── Helper: lấy input username / password ────────────────────────────
    private WebElement usernameInput() {
        return driver.findElement(
                By.cssSelector("input[placeholder='Nhập tên đăng nhập']"));
    }

    private WebElement passwordInput() {
        return driver.findElement(
                By.cssSelector("input[placeholder='Nhập mật khẩu']"));
    }

    private WebElement submitBtn() {
        return driver.findElement(By.cssSelector("button[type='submit']"));
    }

    // ── Helper: kiểm tra trường có HTML5 required validation lỗi không ───
    private boolean isValueMissing(WebElement el) {
        return (Boolean) ((JavascriptExecutor) driver)
                .executeScript("return arguments[0].validity.valueMissing;", el);
    }

    // ── Helper: kiểm tra .form-error có hiển thị không ───────────────────
    private boolean hasFormError() {
        List<WebElement> errors = driver.findElements(By.cssSelector(".form-error"));
        return !errors.isEmpty();
    }

    // ─────────────────────────────────────────────────────────────────────
    // TC04 - Để trống Username
    // ─────────────────────────────────────────────────────────────────────
    @Test
    @Order(1)
    @DisplayName("TC04 - Để trống Username, nhập Password hợp lệ")
    void TC04_blankUsername() throws InterruptedException {
        // Bước 1: Truy cập trang Đăng nhập
        openLoginPage();
        slow();

        // Bước 2: Bỏ trống ô Username
        usernameInput().clear();
        slow();

        // Bước 3: Nhập mật khẩu hợp lệ
        slowType(passwordInput(), "Valid@123");
        slow();

        // Bước 4: Nhấn nút "Đăng nhập"
        submitBtn().click();
        Thread.sleep(ACTION_DELAY);

        // Bước 5: Kiểm tra kết quả
        slow();
        assertTrue(
            driver.getCurrentUrl().contains("/login"),
            "TC04 FAIL: Hệ thống đã chuyển trang dù Username bị bỏ trống"
        );

        boolean blocked = isValueMissing(usernameInput()) || hasFormError();
        assertTrue(blocked,
            "TC04 FAIL: Không có thông báo lỗi khi để trống Username");

        slow();
        System.out.println("TC04 PASS: Hệ thống chặn submit khi Username trống");
    }

    // ─────────────────────────────────────────────────────────────────────
    // TC05 - Để trống Password
    // ─────────────────────────────────────────────────────────────────────
    @Test
    @Order(2)
    @DisplayName("TC05 - Nhập Username hợp lệ, để trống Password")
    void TC05_blankPassword() throws InterruptedException {
        // Bước 1: Truy cập trang Đăng nhập
        openLoginPage();
        slow();

        // Bước 2: Nhập tên đăng nhập hợp lệ
        slowType(usernameInput(), "valid_user");
        slow();

        // Bước 3: Bỏ trống ô Password
        passwordInput().clear();
        slow();

        // Bước 4: Nhấn nút "Đăng nhập"
        submitBtn().click();
        Thread.sleep(ACTION_DELAY);

        // Bước 5: Kiểm tra kết quả
        slow();
        assertTrue(
            driver.getCurrentUrl().contains("/login"),
            "TC05 FAIL: Hệ thống đã chuyển trang dù Password bị bỏ trống"
        );

        boolean blocked = isValueMissing(passwordInput()) || hasFormError();
        assertTrue(blocked,
            "TC05 FAIL: Không có thông báo lỗi khi để trống Password");

        slow();
        System.out.println("TC05 PASS: Hệ thống chặn submit khi Password trống");
    }

    // ─────────────────────────────────────────────────────────────────────
    // TC06 - Để trống cả Username & Password
    // ─────────────────────────────────────────────────────────────────────
    @Test
    @Order(3)
    @DisplayName("TC06 - Để trống cả Username lẫn Password")
    void TC06_bothBlank() throws InterruptedException {
        // Bước 1: Truy cập trang Đăng nhập
        openLoginPage();
        slow();

        // Bước 2 & 3: Bỏ trống cả hai ô
        usernameInput().clear();
        passwordInput().clear();
        slow();

        // Bước 4: Nhấn nút "Đăng nhập"
        submitBtn().click();
        Thread.sleep(ACTION_DELAY);

        // Bước 5: Kiểm tra kết quả
        slow();
        assertTrue(
            driver.getCurrentUrl().contains("/login"),
            "TC06 FAIL: Hệ thống đã chuyển trang dù cả hai trường đều trống"
        );

        boolean blocked = isValueMissing(usernameInput())
                       || isValueMissing(passwordInput())
                       || hasFormError();
        assertTrue(blocked,
            "TC06 FAIL: Không có thông báo lỗi khi cả hai trường đều trống");

        slow();
        System.out.println("TC06 PASS: Hệ thống chặn submit khi cả hai trường trống");
    }

    // ─────────────────────────────────────────────────────────────────────
    // TC21 - Nộp hồ sơ thành công
    // ─────────────────────────────────────────────────────────────────────
    @Test
    @Order(4)
    @DisplayName("TC21 - Nộp hồ sơ thành công với dữ liệu hợp lệ")
    void TC21_submitApplicationSuccess() throws Exception {
        // Bước 1: Đăng nhập bằng tài khoản CITIZEN hợp lệ
        openLoginPage();
        slow();
        slowType(usernameInput(), CITIZEN_USER);
        Thread.sleep(ACTION_DELAY);
        slowType(passwordInput(), CITIZEN_PASS);
        slow();
        submitBtn().click();
        wait.until(ExpectedConditions.urlContains("/portal"));
        slow();

        // Bước 2: Ở trang chủ portal, lấy href từ nút "Nộp hồ sơ" rồi navigate thẳng
        // (tránh vấn đề Vue Router không trigger khi Selenium click router-link)
        WebElement applyBtn = wait.until(ExpectedConditions.presenceOfElementLocated(
                By.xpath("//a[contains(@href,'/portal/nop-ho-so?program=')]")));

        String href = applyBtn.getAttribute("href");
        System.out.println("TC21: Navigate tới → " + href);

        // Navigate thẳng bằng driver.get() thay vì click
        driver.get(href);
        slow();

        // Bước 3: Đã vào /portal/nop-ho-so?program=<id> — chương trình đã được pre-select
        wait.until(ExpectedConditions.urlContains("/portal/nop-ho-so"));
        wait.until(ExpectedConditions.presenceOfElementLocated(
                By.xpath("//h1[contains(.,'h') and contains(.,'s')]"))); // "Nộp hồ sơ"

        // Chờ skeleton loading biến mất
        wait.until(ExpectedConditions.invisibilityOfElementLocated(
                By.cssSelector(".animate-pulse")));
        Thread.sleep(800);

        // ── STEP 0: Chương trình đã pre-select, chỉ cần chọn nhóm đối tượng ──
        // Chờ select nhóm đối tượng có options (API load xong)
        wait.until(driver -> {
            List<WebElement> selects = driver.findElements(By.cssSelector("select"));
            if (selects.isEmpty()) return false;
            Select s = new Select(selects.get(0));
            return s.getOptions().size() > 1;
        });

        Select groupSel = new Select(driver.findElement(By.cssSelector("select")));
        groupSel.selectByIndex(1); // bỏ placeholder, chọn nhóm đầu tiên
        slow();

        // Nhấn "Tiếp theo"
        clickNextButton();
        slow();

        // ── STEP 1: Thông tin cá nhân ─────────────────────────────────────
        wait.until(ExpectedConditions.presenceOfElementLocated(
                By.cssSelector("textarea")));
        slow();

        WebElement lyDo = driver.findElement(By.cssSelector("textarea"));
        slowType(lyDo, "Hoàn cảnh khó khăn, thu nhập thấp, cần hỗ trợ từ chương trình.");
        slow();

        clickNextButton();
        slow();

        // ── STEP 2: Tài liệu đính kèm ────────────────────────────────────
        wait.until(ExpectedConditions.presenceOfElementLocated(
                By.cssSelector("input[type='file']")));
        slow();

        // Tạo file PDF tạm để upload
        Path tmpFile = Files.createTempFile("test-doc-", ".pdf");
        Files.write(tmpFile, "%PDF-1.4 selenium test file".getBytes());

        WebElement fileInput = driver.findElement(By.cssSelector("input[type='file']"));
        ((JavascriptExecutor) driver).executeScript(
                "arguments[0].style.display='block';", fileInput);
        fileInput.sendKeys(tmpFile.toAbsolutePath().toString());
        Files.deleteIfExists(tmpFile);
        slow();

        // Gắn loại tài liệu cho file vừa upload
        List<WebElement> allSelects = driver.findElements(By.cssSelector("select"));
        if (!allSelects.isEmpty()) {
            Select docSel = new Select(allSelects.get(allSelects.size() - 1));
            if (docSel.getOptions().size() > 1) docSel.selectByIndex(1);
        }
        slow();

        clickNextButton();
        slow();

        // ── STEP 3: Xác nhận ──────────────────────────────────────────────
        wait.until(ExpectedConditions.presenceOfElementLocated(
                By.cssSelector("input[type='checkbox']")));
        slow();

        WebElement checkbox = driver.findElement(By.cssSelector("input[type='checkbox']"));
        if (!checkbox.isSelected()) checkbox.click();
        slow();

        // Nhấn "Nộp hồ sơ" (nút submit cuối cùng — text chứa "Nộp" hoặc "p h")
        // Dùng JavaScript click để tránh bị chặn bởi overlay
        WebElement submitHoSo = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//button[not(@disabled) and (contains(.,'p h') or contains(.,'Nộp'))]")));
        System.out.println("TC21: Đang nhấn nút Nộp hồ sơ... text=" + submitHoSo.getText());
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", submitHoSo);
        System.out.println("TC21: Đã click, chờ kết quả...");

        // ── Bước 5: Quan sát kết quả ──────────────────────────────────────
        // Tìm nút "Theo dõi hồ sơ" hoặc "Trang chủ" xuất hiện sau khi submit thành công
        // (tránh encoding issue với XPath tiếng Việt)
        WebDriverWait longWait = new WebDriverWait(driver, Duration.ofSeconds(20));
        WebElement successEl = longWait.until(ExpectedConditions.presenceOfElementLocated(
                By.xpath(
                    "//a[contains(@href,'/portal/ho-so-cua-toi')]" +   // nút "Theo dõi hồ sơ"
                    " | //h2[contains(@class,'font-black') and string-length(.) > 5]" // heading success
                )));

        assertNotNull(successEl,
                "TC21 FAIL: Không hiển thị màn hình nộp hồ sơ thành công");

        slow();
        System.out.println("TC21 PASS: Nộp hồ sơ thành công!");
    }

    /** Click nút "Tiếp theo" hiện tại trên trang */
    private void clickNextButton() {
        WebElement btn = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//button[contains(.,'Ti') and not(@disabled)]")));
        btn.click();
    }
}
