"""
Black Box Test Cases - Selenium WebDriver
==========================================
TC04 - Để trống Username
TC05 - Để trống Password
TC06 - Để trống cả Username & Password
TC21 - Nộp hồ sơ thành công

Yêu cầu:
    pip install selenium
    ChromeDriver phải khớp phiên bản Chrome đang dùng
    Frontend chạy tại: http://localhost:5175
    Backend chạy tại:  http://localhost:8080
"""

import time
import unittest
from selenium import webdriver
from selenium.webdriver.common.by import By
from selenium.webdriver.support.ui import WebDriverWait, Select
from selenium.webdriver.support import expected_conditions as EC
from selenium.webdriver.chrome.options import Options
from selenium.webdriver.chrome.service import Service

# ─── Cấu hình ────────────────────────────────────────────────────────────────
BASE_URL        = "http://localhost:5175"
LOGIN_URL       = f"{BASE_URL}/login"
PORTAL_APPLY    = f"{BASE_URL}/portal/nop-ho-so"

# Tài khoản CITIZEN hợp lệ (đã tồn tại trong DB)
VALID_CITIZEN_USER = "citizen01"
VALID_CITIZEN_PASS = "123456"

WAIT_TIMEOUT = 10  # giây


def make_driver() -> webdriver.Chrome:
    """Khởi tạo Chrome WebDriver (headless tuỳ chọn)."""
    opts = Options()
    # opts.add_argument("--headless")          # bỏ comment nếu muốn chạy ẩn
    opts.add_argument("--no-sandbox")
    opts.add_argument("--disable-dev-shm-usage")
    opts.add_argument("--window-size=1280,900")
    return webdriver.Chrome(options=opts)


# ─── TC04 ─────────────────────────────────────────────────────────────────────
class TC04_BlankUsername(unittest.TestCase):
    """TC04 - Để trống Username, nhập Password hợp lệ → hệ thống không cho submit."""

    def setUp(self):
        self.driver = make_driver()
        self.wait   = WebDriverWait(self.driver, WAIT_TIMEOUT)

    def tearDown(self):
        self.driver.quit()

    def test_blank_username_shows_error(self):
        driver = self.driver
        wait   = self.wait

        # Bước 1: Truy cập trang Đăng nhập
        driver.get(LOGIN_URL)
        wait.until(EC.presence_of_element_located((By.CSS_SELECTOR, "form.auth-form")))

        # Bước 2: Bỏ trống ô Username (không nhập gì)
        username_input = driver.find_element(
            By.CSS_SELECTOR, "input[placeholder='Nhập tên đăng nhập']"
        )
        username_input.clear()

        # Bước 3: Nhập mật khẩu hợp lệ
        password_input = driver.find_element(
            By.CSS_SELECTOR, "input[placeholder='Nhập mật khẩu']"
        )
        password_input.clear()
        password_input.send_keys("Valid@123")

        # Bước 4: Nhấn nút "Đăng nhập"
        submit_btn = driver.find_element(By.CSS_SELECTOR, "button[type='submit']")
        submit_btn.click()

        # Bước 5: Quan sát kết quả
        # Trường hợp A: HTML5 required validation ngăn submit → input invalid
        # Trường hợp B: Server trả lỗi → .form-error hiển thị
        time.sleep(1)

        still_on_login = LOGIN_URL in driver.current_url or "/login" in driver.current_url
        self.assertTrue(
            still_on_login,
            "TC04 FAIL: Hệ thống đã chuyển trang dù Username bị bỏ trống."
        )

        # Kiểm tra trường username có trạng thái invalid (HTML5 required)
        is_invalid = driver.execute_script(
            "return arguments[0].validity.valueMissing;", username_input
        )
        # Hoặc có thể có .form-error hiển thị
        error_visible = len(driver.find_elements(By.CSS_SELECTOR, ".form-error")) > 0

        self.assertTrue(
            is_invalid or error_visible,
            "TC04 FAIL: Không có thông báo lỗi khi để trống Username."
        )
        print("TC04 PASS: Hệ thống chặn submit và hiển thị lỗi khi Username trống.")


# ─── TC05 ─────────────────────────────────────────────────────────────────────
class TC05_BlankPassword(unittest.TestCase):
    """TC05 - Nhập Username hợp lệ, để trống Password → hệ thống không cho submit."""

    def setUp(self):
        self.driver = make_driver()
        self.wait   = WebDriverWait(self.driver, WAIT_TIMEOUT)

    def tearDown(self):
        self.driver.quit()

    def test_blank_password_shows_error(self):
        driver = self.driver
        wait   = self.wait

        # Bước 1: Truy cập trang Đăng nhập
        driver.get(LOGIN_URL)
        wait.until(EC.presence_of_element_located((By.CSS_SELECTOR, "form.auth-form")))

        # Bước 2: Nhập tên đăng nhập hợp lệ
        username_input = driver.find_element(
            By.CSS_SELECTOR, "input[placeholder='Nhập tên đăng nhập']"
        )
        username_input.clear()
        username_input.send_keys("valid_user")

        # Bước 3: Bỏ trống ô Password
        password_input = driver.find_element(
            By.CSS_SELECTOR, "input[placeholder='Nhập mật khẩu']"
        )
        password_input.clear()

        # Bước 4: Nhấn nút "Đăng nhập"
        submit_btn = driver.find_element(By.CSS_SELECTOR, "button[type='submit']")
        submit_btn.click()

        # Bước 5: Quan sát kết quả
        time.sleep(1)

        still_on_login = LOGIN_URL in driver.current_url or "/login" in driver.current_url
        self.assertTrue(
            still_on_login,
            "TC05 FAIL: Hệ thống đã chuyển trang dù Password bị bỏ trống."
        )

        is_invalid = driver.execute_script(
            "return arguments[0].validity.valueMissing;", password_input
        )
        error_visible = len(driver.find_elements(By.CSS_SELECTOR, ".form-error")) > 0

        self.assertTrue(
            is_invalid or error_visible,
            "TC05 FAIL: Không có thông báo lỗi khi để trống Password."
        )
        print("TC05 PASS: Hệ thống chặn submit và hiển thị lỗi khi Password trống.")


# ─── TC06 ─────────────────────────────────────────────────────────────────────
class TC06_BothBlank(unittest.TestCase):
    """TC06 - Để trống cả Username lẫn Password → hệ thống không cho submit."""

    def setUp(self):
        self.driver = make_driver()
        self.wait   = WebDriverWait(self.driver, WAIT_TIMEOUT)

    def tearDown(self):
        self.driver.quit()

    def test_both_blank_shows_error(self):
        driver = self.driver
        wait   = self.wait

        # Bước 1: Truy cập trang Đăng nhập
        driver.get(LOGIN_URL)
        wait.until(EC.presence_of_element_located((By.CSS_SELECTOR, "form.auth-form")))

        # Bước 2 & 3: Bỏ trống cả hai ô
        username_input = driver.find_element(
            By.CSS_SELECTOR, "input[placeholder='Nhập tên đăng nhập']"
        )
        password_input = driver.find_element(
            By.CSS_SELECTOR, "input[placeholder='Nhập mật khẩu']"
        )
        username_input.clear()
        password_input.clear()

        # Bước 4: Nhấn nút "Đăng nhập"
        submit_btn = driver.find_element(By.CSS_SELECTOR, "button[type='submit']")
        submit_btn.click()

        # Bước 5: Quan sát kết quả
        time.sleep(1)

        still_on_login = LOGIN_URL in driver.current_url or "/login" in driver.current_url
        self.assertTrue(
            still_on_login,
            "TC06 FAIL: Hệ thống đã chuyển trang dù cả hai trường đều trống."
        )

        user_invalid = driver.execute_script(
            "return arguments[0].validity.valueMissing;", username_input
        )
        pass_invalid = driver.execute_script(
            "return arguments[0].validity.valueMissing;", password_input
        )
        error_visible = len(driver.find_elements(By.CSS_SELECTOR, ".form-error")) > 0

        self.assertTrue(
            (user_invalid or pass_invalid) or error_visible,
            "TC06 FAIL: Không có thông báo lỗi khi cả hai trường đều trống."
        )
        print("TC06 PASS: Hệ thống chặn submit và hiển thị lỗi khi cả hai trường trống.")


# ─── TC21 ─────────────────────────────────────────────────────────────────────
class TC21_SubmitApplicationSuccess(unittest.TestCase):
    """TC21 - Nộp hồ sơ thành công với dữ liệu hợp lệ (luồng 4 bước)."""

    def setUp(self):
        self.driver = make_driver()
        self.wait   = WebDriverWait(self.driver, WAIT_TIMEOUT)

    def tearDown(self):
        self.driver.quit()

    # ── Helper: đăng nhập ──────────────────────────────────────────────────
    def _login(self, username: str, password: str):
        driver = self.driver
        wait   = self.wait

        driver.get(LOGIN_URL)
        wait.until(EC.presence_of_element_located((By.CSS_SELECTOR, "form.auth-form")))

        driver.find_element(
            By.CSS_SELECTOR, "input[placeholder='Nhập tên đăng nhập']"
        ).send_keys(username)
        driver.find_element(
            By.CSS_SELECTOR, "input[placeholder='Nhập mật khẩu']"
        ).send_keys(password)
        driver.find_element(By.CSS_SELECTOR, "button[type='submit']").click()

        # Chờ redirect sang portal
        wait.until(EC.url_contains("/portal"))

    def test_submit_application_success(self):
        driver = self.driver
        wait   = self.wait

        # Bước 1: Đăng nhập bằng tài khoản CITIZEN hợp lệ
        self._login(VALID_CITIZEN_USER, VALID_CITIZEN_PASS)

        # Bước 2: Mở chức năng Nộp hồ sơ
        driver.get(PORTAL_APPLY)
        wait.until(EC.presence_of_element_located(
            (By.XPATH, "//h1[contains(text(),'Nộp hồ sơ hỗ trợ')]")
        ))

        # ── STEP 0: Chọn chương trình ──────────────────────────────────────
        # Chờ danh sách chương trình load xong (hết skeleton)
        wait.until(EC.presence_of_element_located(
            (By.CSS_SELECTOR, ".space-y-3 .bg-white.rounded-2xl.border-2")
        ))

        # Chọn chương trình đầu tiên có trạng thái OPEN
        programs = driver.find_elements(
            By.CSS_SELECTOR, ".space-y-3 .bg-white.rounded-2xl.border-2"
        )
        self.assertGreater(len(programs), 0, "TC21 FAIL: Không có chương trình nào để chọn.")
        programs[0].click()
        time.sleep(0.5)

        # Chọn nhóm đối tượng
        group_select = wait.until(EC.presence_of_element_located(
            (By.CSS_SELECTOR, "select")
        ))
        select_obj = Select(group_select)
        options = select_obj.options
        self.assertGreater(len(options), 1, "TC21 FAIL: Không có nhóm đối tượng nào.")
        select_obj.select_by_index(1)  # chọn option đầu tiên (bỏ qua placeholder)

        # Nhấn "Tiếp theo"
        next_btn = driver.find_element(
            By.XPATH, "//button[contains(text(),'Tiếp theo')]"
        )
        next_btn.click()

        # ── STEP 1: Thông tin cá nhân ──────────────────────────────────────
        wait.until(EC.presence_of_element_located(
            (By.XPATH, "//h2[contains(text(),'Thông tin cá nhân')]")
        ))

        # Nhập lý do xin trợ cấp (trường duy nhất có thể chỉnh sửa)
        ly_do = driver.find_element(By.CSS_SELECTOR, "textarea")
        ly_do.clear()
        ly_do.send_keys("Hoàn cảnh khó khăn, thu nhập thấp, cần hỗ trợ từ chương trình.")

        next_btn = driver.find_element(
            By.XPATH, "//button[contains(text(),'Tiếp theo')]"
        )
        next_btn.click()

        # ── STEP 2: Tài liệu đính kèm ──────────────────────────────────────
        wait.until(EC.presence_of_element_located(
            (By.XPATH, "//h2[contains(text(),'Tài liệu đính kèm')]")
        ))

        # Upload file test (tạo file tạm nếu cần)
        import os, tempfile
        tmp = tempfile.NamedTemporaryFile(suffix=".pdf", delete=False)
        tmp.write(b"%PDF-1.4 test file")
        tmp.close()

        file_input = driver.find_element(By.CSS_SELECTOR, "input[type='file']")
        driver.execute_script("arguments[0].style.display='block';", file_input)
        file_input.send_keys(tmp.name)
        os.unlink(tmp.name)

        time.sleep(1)

        # Gắn loại tài liệu cho file vừa upload
        doc_type_selects = driver.find_elements(
            By.CSS_SELECTOR, "select"
        )
        if doc_type_selects:
            doc_select = Select(doc_type_selects[-1])
            doc_options = doc_select.options
            if len(doc_options) > 1:
                doc_select.select_by_index(1)

        next_btn = driver.find_element(
            By.XPATH, "//button[contains(text(),'Tiếp theo')]"
        )
        next_btn.click()

        # ── STEP 3: Xác nhận ───────────────────────────────────────────────
        wait.until(EC.presence_of_element_located(
            (By.XPATH, "//h2[contains(text(),'Xác nhận và nộp hồ sơ')]")
        ))

        # Đánh dấu cam kết
        checkbox = driver.find_element(By.CSS_SELECTOR, "input[type='checkbox']")
        if not checkbox.is_selected():
            checkbox.click()

        # Nhấn "Nộp hồ sơ"
        submit_btn = driver.find_element(
            By.XPATH, "//button[contains(text(),'Nộp hồ sơ')]"
        )
        submit_btn.click()

        # ── Bước 5: Quan sát kết quả ───────────────────────────────────────
        # Chờ màn hình thành công xuất hiện
        success_heading = wait.until(EC.presence_of_element_located(
            (By.XPATH, "//h2[contains(text(),'Nộp hồ sơ thành công')]")
        ))

        self.assertIsNotNone(
            success_heading,
            "TC21 FAIL: Không hiển thị thông báo 'Nộp hồ sơ thành công'."
        )

        # Kiểm tra mã hồ sơ được sinh ra
        ma_ho_so_el = driver.find_elements(
            By.XPATH, "//*[contains(text(),'#HS-') or contains(@class,'text-primary')]"
        )
        self.assertGreater(
            len(ma_ho_so_el), 0,
            "TC21 FAIL: Không hiển thị mã hồ sơ sau khi nộp thành công."
        )

        print(f"TC21 PASS: Nộp hồ sơ thành công. Mã hồ sơ: {ma_ho_so_el[0].text}")


# ─── Entry point ──────────────────────────────────────────────────────────────
if __name__ == "__main__":
    unittest.main(verbosity=2)
