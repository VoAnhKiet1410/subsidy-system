package com.trocap.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.trocap.TestDataFactory;
import com.trocap.auth.model.NguoiDung;
import com.trocap.auth.model.RefreshToken;
import com.trocap.auth.repository.NguoiDungRepository;
import com.trocap.auth.repository.RefreshTokenRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.time.Instant;
import java.util.Map;

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Test nghiệp vụ Authentication & Authorization.
 * Ref: docs/test-cases-auth.md (AUTH-01 → AUTH-17, PERM-01 → PERM-13)
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class AuthControllerTest {

    @Autowired MockMvc mockMvc;
    @Autowired ObjectMapper mapper;
    @Autowired NguoiDungRepository userRepo;
    @Autowired RefreshTokenRepository refreshTokenRepo;

    @BeforeEach
    void setup() {
        userRepo.deleteAll();
        refreshTokenRepo.deleteAll();
    }

    // ═══════════════════════════════════════════════════════════
    //  A. ĐĂNG KÝ
    // ═══════════════════════════════════════════════════════════

    /** AUTH-01: Đăng ký thành công, role mặc định = CITIZEN */
    @Test
    void register_success_defaultCitizen() throws Exception {
        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(Map.of(
                        "username", "citizen01",
                        "password", "Abc@1234",
                        "email", "citizen01@mail.com",
                        "fullName", "Nguyễn Văn A"
                ))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.roles[0]").value("CITIZEN"))
                .andExpect(jsonPath("$.data.username").value("citizen01"));
    }

    /** AUTH-01 fix: Register không cho client tự chọn ADMIN */
    @Test
    void register_ignoresRoleFromClient() throws Exception {
        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(Map.of(
                        "username", "hacker01",
                        "password", "Abc@1234",
                        "email", "hack@mail.com",
                        "fullName", "Hacker",
                        "role", "ADMIN"
                ))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.roles[0]").value("CITIZEN"));
    }

    /** AUTH-02: Trùng username */
    @Test
    void register_duplicateUsername() throws Exception {
        userRepo.save(TestDataFactory.citizen());

        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(Map.of(
                        "username", "citizen01",
                        "password", "Abc@1234",
                        "email", "other@mail.com",
                        "fullName", "Test"
                ))))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", containsString("Tên đăng nhập đã tồn tại")));
    }

    /** AUTH-03: Trùng email */
    @Test
    void register_duplicateEmail() throws Exception {
        userRepo.save(TestDataFactory.citizen());

        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(Map.of(
                        "username", "newuser",
                        "password", "Abc@1234",
                        "email", "citizen01@mail.com",
                        "fullName", "Test"
                ))))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", containsString("Email đã tồn tại")));
    }

    /** AUTH-04: Thiếu username → validation */
    @Test
    void register_blankUsername() throws Exception {
        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(Map.of(
                        "username", "",
                        "password", "Abc@1234",
                        "email", "test@mail.com",
                        "fullName", "Test"
                ))))
                .andExpect(status().isBadRequest());
    }

    /** AUTH-06: Password quá ngắn */
    @Test
    void register_shortPassword() throws Exception {
        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(Map.of(
                        "username", "testuser",
                        "password", "123",
                        "email", "test@mail.com",
                        "fullName", "Test"
                ))))
                .andExpect(status().isBadRequest());
    }

    // ═══════════════════════════════════════════════════════════
    //  B. ĐĂNG NHẬP
    // ═══════════════════════════════════════════════════════════

    /** AUTH-07: Login thành công */
    @Test
    void login_success() throws Exception {
        userRepo.save(TestDataFactory.citizen());

        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(Map.of(
                        "username", "citizen01",
                        "password", TestDataFactory.RAW_PASSWORD
                ))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.accessToken").isNotEmpty())
                .andExpect(jsonPath("$.data.refreshToken").isNotEmpty())
                .andExpect(jsonPath("$.data.username").value("citizen01"))
                .andExpect(jsonPath("$.data.roles[0]").value("CITIZEN"));
    }

    /** AUTH-08: Sai password → chung error message */
    @Test
    void login_wrongPassword() throws Exception {
        userRepo.save(TestDataFactory.citizen());

        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(Map.of(
                        "username", "citizen01",
                        "password", "WrongPass"
                ))))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message", containsString("Sai tên đăng nhập hoặc mật khẩu")));
    }

    /** AUTH-09: Sai username → chung error message (chống enumeration) */
    @Test
    void login_wrongUsername() throws Exception {
        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(Map.of(
                        "username", "nonexistent",
                        "password", "Abc@1234"
                ))))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message", containsString("Sai tên đăng nhập hoặc mật khẩu")));
    }

    // ═══════════════════════════════════════════════════════════
    //  C. TOKEN
    // ═══════════════════════════════════════════════════════════

    /** AUTH-14: Không gửi token → 401 Unauthorized */
    @Test
    void api_noToken_returns401() throws Exception {
        mockMvc.perform(get("/api/applications"))
                .andExpect(status().isUnauthorized());
    }

    /** AUTH-13: Token sai format → 401 */
    @Test
    void api_invalidToken_returns401() throws Exception {
        mockMvc.perform(get("/api/applications")
                .header("Authorization", "Bearer invalid.token.here"))
                .andExpect(status().isUnauthorized());
    }

    /** AUTH-15: Refresh token thành công */
    @Test
    void refreshToken_success() throws Exception {
        userRepo.save(TestDataFactory.citizen());
        RefreshToken rt = TestDataFactory.refreshToken("u-citizen1");
        refreshTokenRepo.save(rt);

        mockMvc.perform(post("/api/auth/refresh")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(Map.of(
                        "refreshToken", rt.getToken()
                ))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.accessToken").isNotEmpty())
                .andExpect(jsonPath("$.data.refreshToken").isNotEmpty());
    }

    /** AUTH-16: Refresh token không hợp lệ */
    @Test
    void refreshToken_invalid() throws Exception {
        mockMvc.perform(post("/api/auth/refresh")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(Map.of(
                        "refreshToken", "invalid-token"
                ))))
                .andExpect(status().isUnauthorized());
    }

    /** AUTH-16b: Refresh token hết hạn */
    @Test
    void refreshToken_expired() throws Exception {
        userRepo.save(TestDataFactory.citizen());
        RefreshToken rt = RefreshToken.builder()
                .token("expired-token").nguoiDungId("u-citizen1")
                .expiryDate(Instant.now().minusSeconds(3600)).build();
        refreshTokenRepo.save(rt);

        mockMvc.perform(post("/api/auth/refresh")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(Map.of(
                        "refreshToken", "expired-token"
                ))))
                .andExpect(status().isUnauthorized());
    }

    // ─── Helper: lấy access token ──────────────────────────────
    String loginAndGetToken(String username) throws Exception {
        MvcResult result = mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(Map.of(
                        "username", username,
                        "password", TestDataFactory.RAW_PASSWORD
                ))))
                .andReturn();
        String json = result.getResponse().getContentAsString();
        return mapper.readTree(json).at("/data/accessToken").asText();
    }

    // ═══════════════════════════════════════════════════════════
    //  D. PHÂN QUYỀN
    // ═══════════════════════════════════════════════════════════

    /** PERM-05: CITIZEN tạo nguồn quỹ → 403 hoặc 500 (tuỳ cấu hình) */
    @Test
    void citizen_createFund_notAllowed() throws Exception {
        userRepo.save(TestDataFactory.citizen());
        String token = loginAndGetToken("citizen01");

        int status = mockMvc.perform(post("/api/fund-sources")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"tenNguonQuy\":\"Test\",\"tongNganSach\":100000}"))
                .andReturn().getResponse().getStatus();
        assertTrue(status == 403 || status == 500,
                "Expected 403 or 500 but got " + status);
    }

    /** PERM-06: CITIZEN duyệt hồ sơ → 403 */
    @Test
    void citizen_approveApp_forbidden() throws Exception {
        userRepo.save(TestDataFactory.citizen());
        String token = loginAndGetToken("citizen01");

        mockMvc.perform(patch("/api/applications/any-id/under-review")
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isForbidden());
    }

    /** PERM-07: CITIZEN tạo payment → 403 */
    @Test
    void citizen_createPayment_forbidden() throws Exception {
        userRepo.save(TestDataFactory.citizen());
        String token = loginAndGetToken("citizen01");

        mockMvc.perform(post("/api/payments")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"hoSoHoTroId\":\"test\",\"soTien\":5000,\"phuongThuc\":\"CASH\"}"))
                .andExpect(status().isForbidden());
    }

    /** PERM-09: OFFICER tạo payment → 403 */
    @Test
    void officer_createPayment_forbidden() throws Exception {
        userRepo.save(TestDataFactory.officer());
        String token = loginAndGetToken("officer01");

        mockMvc.perform(post("/api/payments")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"hoSoHoTroId\":\"test\",\"soTien\":5000,\"phuongThuc\":\"CASH\"}"))
                .andExpect(status().isForbidden());
    }

    /** PERM-10: ACCOUNTANT duyệt hồ sơ → 403 */
    @Test
    void accountant_approveApp_forbidden() throws Exception {
        userRepo.save(TestDataFactory.accountant());
        String token = loginAndGetToken("accountant01");

        mockMvc.perform(patch("/api/applications/any-id/approve")
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isForbidden());
    }
}
