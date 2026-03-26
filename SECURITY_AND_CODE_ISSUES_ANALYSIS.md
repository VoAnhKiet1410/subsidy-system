# Backend Source Code - Security & Code Quality Issues Analysis

**Project**: Subsidy System Backend  
**Target Directory**: `backend/src/main/java`  
**Analysis Date**: 2026-03-26  
**Files Analyzed**: 95 Java files

---

## CRITICAL ISSUES

### 1. NULL POINTER EXCEPTIONS

#### Issue 1.1: Potential NPE from `.orElse(null)` with null checks
**Severity**: HIGH  
**File**: [com/trocap/chitra/service/ChiTraService.java](com/trocap/chitra/service/ChiTraService.java#L125)  
**Line**: 125  
**Code Pattern**: 
```java
HoSoHoTro hoSo = hoSoRepository.findById(payment.getHoSoHoTroId()).orElse(null);
if (hoSo != null) {
    hoSo.setTrangThaiChiTra("FAILED");
    hoSoRepository.save(hoSo);
}
```
**Problem**: While there is a null check after, the pattern is risky. The setter could be called when `hoSo` is null if logic changes.  
**Recommendation**: Use `ifPresent()` or `orElseThrow()` instead.

#### Issue 1.2: Multiple NPE risks from `.orElse(null)`
**Severity**: MEDIUM  
**File**: [com/trocap/chitra/service/ChiTraService.java](com/trocap/chitra/service/ChiTraService.java#L144)  
**Line**: 144  
**Code Pattern**:
```java
ChuongTrinhTruCap chuongTrinh = chuongTrinhRepository.findById(hoSo.getChuongTrinhId()).orElse(null);
if (chuongTrinh != null && chuongTrinh.getNguonQuyId() != null) {
    deductBudget(chuongTrinh.getNguonQuyId(), chuongTrinh, payment.getSoTien());
}
```
**Problem**: If `chuongTrinh` is null, the logic skips budget deduction silently. No warning or error.  
**Recommendation**: Either throw exception or log warning.

#### Issue 1.3: Unsafe assume `roles` contains expected values
**Severity**: MEDIUM  
**File**: [com/trocap/hoso/controller/HoSoController.java](com/trocap/hoso/controller/HoSoController.java#L64)  
**Line**: 64  
**Code Pattern**:
```java
boolean isCitizen = currentUser.getRoles() != null
    && !currentUser.getRoles().contains("ADMIN")
    && !currentUser.getRoles().contains("OFFICER")
    && !currentUser.getRoles().contains("ACCOUNTANT");
```
**Problem**: If `roles` list is empty but not null, logic could behave unexpectedly. Also, roles could be null.  
**Recommendation**: Use safe collection checks and defensive defaults.

#### Issue 1.4: Null reference in condition checks
**Severity**: MEDIUM  
**File**: [com/trocap/hoso/service/HoSoService.java](com/trocap/hoso/service/HoSoService.java#L163)  
**Line**: 163-166  
**Code Pattern**:
```java
if (hoSo.getDoiTuongId() == null || hoSo.getDoiTuongId().isBlank()) {
    throw new BadRequestException("Thiếu thông tin đối tượng hưởng trợ cấp");
}
```
**Problem**: If `getDoiTuongId()` returns null, calling `.isBlank()` on null will throw NPE.  
**Recommendation**: Check null before calling instance methods.

---

### 2. UNCHECKED/BROAD EXCEPTION HANDLING

#### Issue 2.1: Too broad exception catching
**Severity**: HIGH  
**File**: [com/trocap/config/MongoIndexFixer.java](com/trocap/config/MongoIndexFixer.java#L43)  
**Line**: 43, 69  
**Code Pattern**:
```java
try {
    var indexOps = mongoTemplate.indexOps("beneficiaries");
    // ... operations
} catch (Exception e) {
    log.warn("Không thể sửa index code/beneficiaries: {}", e.getMessage());
}
```
**Problem**: Catching `Exception` is too broad. Will silently ignore critical errors like `NullPointerException`, `OutOfMemoryError`.  
**Recommendation**: Catch specific exceptions like `DataAccessException`, `MongoException`.

#### Issue 2.2: Broad exception in file operations
**Severity**: MEDIUM  
**File**: [com/trocap/tailieu/service/FileStorageService.java](com/trocap/tailieu/service/FileStorageService.java#L91)  
**Line**: 91  
**Code Pattern**:
```java
public void delete(String filename) {
    try {
        Path filePath = uploadDir.resolve(filename).normalize();
        Files.deleteIfExists(filePath);
    } catch (IOException e) {
        // Log warning but don't throw — metadata deleted is enough
    }
}
```
**Problem**: IOException silently ignored. File deletion failures are not reported to caller.  
**Recommendation**: Log warning or return boolean indicating success/failure.

---

### 3. RESOURCE LEAKS

#### Issue 3.1: XSSFWorkbook not properly closed in exception case
**Severity**: HIGH  
**File**: [com/trocap/export/ExcelExportService.java](com/trocap/export/ExcelExportService.java#L44)  
**Line**: 44  
**Code Pattern**:
```java
public byte[] exportBeneficiaries(List<DoiTuongHuongTruCap> list) throws IOException {
    try (XSSFWorkbook wb = new XSSFWorkbook()) {
        // ... code that may throw exception
    }
}
```
**Problem**: While using try-with-resources is good, if an exception occurs during large export operations, resources might be strained.  
**Recommendation**: Implement proper error handling with resource cleanup.

#### Issue 3.2: IOException declaration but may leak in edge cases
**Severity**: MEDIUM  
**File**: [com/trocap/export/ExcelExportService.java](com/trocap/export/ExcelExportService.java#L508)  
**Lines**: Multiple export methods (89, 136, 177, 508)  
**Problem**: All methods declare `throws IOException` but don't handle partial export scenarios.  
**Recommendation**: Add cleanup logic or use finally block.

---

### 4. TYPE MISMATCHES AND VALIDATION ISSUES

#### Issue 4.1: Inconsistent null check before `.isBlank()`
**Severity**: MEDIUM  
**File**: [com/trocap/chuongtrinh/service/ChuongTrinhService.java](com/trocap/chuongtrinh/service/ChuongTrinhService.java#L62)  
**Line**: 62  
**Code Pattern**:
```java
if (trangThai == null || trangThai.isBlank()) {
```
**Problem**: Unsafe - if `trangThai` is null, the first part short-circuits, so `.isBlank()` is safe. BUT pattern is error-prone.  
**Recommendation**: Use Apache Commons `StringUtils.isBlank()` instead.

#### Issue 4.2: Unsafe ternary operations with potential nulls
**Severity**: MEDIUM  
**File**: [com/trocap/chuongtrinh/service/ChuongTrinhService.java](com/trocap/chuongtrinh/service/ChuongTrinhService.java#L70)  
**Line**: 70  
**Code Pattern**:
```java
.nganSach(request.getNganSach() != null ? request.getNganSach() : 0.0)
```
**Problem**: Repeated pattern across codebase. Verbose and error-prone.  
**Recommendation**: Create utility method for safe null-coalescing.

---

## SECURITY VULNERABILITIES

### 5. SQL/QUERY INJECTION RISKS

#### Issue 5.1: User input concatenation in error messages
**Severity**: MEDIUM  
**File**: [com/trocap/chitra/service/ChiTraService.java](com/trocap/chitra/service/ChiTraService.java#L51)  
**Line**: 51  
**Code Pattern**:
```java
.orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy giao dịch: " + id));
```
**Problem**: User-controlled `id` directly concatenated in message. If this ID is displayed in UI, could be XSS vector.  
**Recommendation**: Use parameterized error messages or escape values.

#### Issue 5.2: Multiple concatenation points with IDs
**Severity**: MEDIUM  
**File**: Multiple files across codebase  
**Lines**: 
- [com/trocap/hoso/service/HoSoService.java](com/trocap/hoso/service/HoSoService.java#L51)#L51
- [com/trocap/chuongtrinh/service/ChuongTrinhService.java](com/trocap/chuongtrinh/service/ChuongTrinhService.java#L31)#L31
- [com/trocap/doituong/service/DoiTuongService.java](com/trocap/doituong/service/DoiTuongService.java#L26)#L26
- [com/trocap/tailieu/service/TaiLieuService.java](com/trocap/tailieu/service/TaiLieuService.java#L67)#L67

**Pattern**: Direct ID concatenation throughout.  
**Recommendation**: Implement sanitization layer or use parameterized messages.

#### Issue 5.3: MongoDB Query Injection potential
**Severity**: LOW (but present)  
**File**: [com/trocap/chuongtrinh/repository/ChuongTrinhRepository.java](com/trocap/chuongtrinh/repository/ChuongTrinhRepository.java#L18)  
**Line**: 18  
**Code Pattern**:
```java
@Query("{ 'ngayBatDau': { $gte: ?0 }, 'ngayKetThuc': { $lte: ?1 } }")
Page<ChuongTrinhTruCap> findByDateRange(LocalDate from, LocalDate to, Pageable pageable);
```
**Problem**: While using parameter substitution (?0, ?1), custom queries are risk vectors. If not properly parameterized, could be vulnerable.  
**Recommendation**: Consider using Spring Data criteria API or verify all @Query annotations.

---

### 6. AUTHENTICATION & AUTHORIZATION ISSUES

#### Issue 6.1: Insufficient authorization check in payment processing
**Severity**: HIGH  
**File**: [com/trocap/chitra/service/ChiTraService.java](com/trocap/chitra/service/ChiTraService.java#L59)  
**Lines**: 59-75  
**Problem**: `create()` method accepts `username` but doesn't verify user has appropriate role to create payments. Only checks if HoSo is APPROVED, not if user is authorized.  
**Recommendation**: Add @PreAuthorize annotation or role check in service.

#### Issue 6.2: Missing permission verification in notify operations
**Severity**: MEDIUM  
**File**: [com/trocap/thongbao/controller/ThongBaoController.java](com/trocap/thongbao/controller/ThongBaoController.java#L123)  
**Line**: 123  
**Code Pattern**:
```java
if (user.getRoles() != null && user.getRoles().contains("ADMIN")) {
```
**Problem**: Permission checking mixed inside controller. Should use @PreAuthorize at method level.  
**Recommendation**: Move to @PreAuthorize annotation.

#### Issue 6.3: Role-based access not enforced consistently
**Severity**: MEDIUM  
**File**: [com/trocap/hoso/controller/HoSoController.java](com/trocap/hoso/controller/HoSoController.java#L64-L70)  
**Lines**: 64-70  
**Problem**: CITIZEN role check is done manually in controller. Other controllers may not have same checks.  
**Recommendation**: Implement consistent @PreAuthorize across all controllers.

#### Issue 6.4: User permissions in file access not fully validated
**Severity**: MEDIUM  
**File**: [com/trocap/tailieu/service/TaiLieuService.java](com/trocap/tailieu/service/TaiLieuService.java#L71-L85)  
**Lines**: 71-85  
**Code Pattern**:
```java
public TaiLieuDinhKem findByIdWithAccess(String id, String username) {
    TaiLieuDinhKem taiLieu = findById(id);
    HoSoHoTro hoSo = hoSoRepository.findById(taiLieu.getHoSoHoTroId())
            .orElseThrow(() -> new ResourceNotFoundException(
                    "Không tìm thấy hồ sơ: " + taiLieu.getHoSoHoTroId()));
    
    if (user.getRoles() != null && user.getRoles().contains("ADMIN")) {
        return taiLieu;
    }
```
**Problem**: Incomplete role check. If user is OFFICER instead of ADMIN, access is denied even though officer should have access.  
**Recommendation**: Implement comprehensive role-based access control.

---

### 7. VALIDATION ISSUES

#### Issue 7.1: Missing input validation on file uploads
**Severity**: MEDIUM  
**File**: [com/trocap/tailieu/service/FileStorageService.java](com/trocap/tailieu/service/FileStorageService.java#L54-L68)  
**Lines**: 54-68  
**Problem**: File size limit not enforced. Only MIME type and extension checked.  
**Recommendation**: Add max file size check before storing.

#### Issue 7.2: No validation on filename extraction
**Severity**: MEDIUM  
**File**: [com/trocap/tailieu/service/FileStorageService.java](com/trocap/tailieu/service/FileStorageService.java#L105)  
**Line**: 105  
**Code Pattern**:
```java
private String getExtension(String filename) {
    if (filename == null || !filename.contains(".")) {
        return "";
    }
    return filename.substring(filename.lastIndexOf('.') + 1);
}
```
**Problem**: Returns empty string for invalid filenames. Could allow uploads with no extension.  
**Recommendation**: Throw exception instead.

#### Issue 7.3: Injectable DTO validation
**Severity**: LOW  
**File**: [com/trocap/chitra/dto/PaymentRequest.java](com/trocap/chitra/dto/PaymentRequest.java#L17)  
**Problem**: `@NotNull` validation on `soTien` but not on `phuongThuc`. Required fields may be missing from request.  
**Recommendation**: Add comprehensive validation annotations.

---

## LOGIC ERRORS & BUSINESS RULE VIOLATIONS

### 8. LOGIC ERRORS

#### Issue 8.1: Silent failure in budget deduction
**Severity**: MEDIUM  
**File**: [com/trocap/chitra/service/ChiTraService.java](com/trocap/chitra/service/ChiTraService.java#L125-128)  
**Lines**: 125-128  
**Code Pattern**:
```java
HoSoHoTro hoSo = hoSoRepository.findById(payment.getHoSoHoTroId()).orElse(null);
if (hoSo != null) {
    hoSo.setTrangThaiChiTra("FAILED");
    hoSoRepository.save(hoSo);
}
```
**Problem**: If `hoSo` is null (deleted between payment creation and status update), payment status changes but HoSo is not updated. Inconsistent state.  
**Recommendation**: Use orElseThrow() instead.

#### Issue 8.2: Incomplete optional field handling
**Severity**: MEDIUM  
**File**: [com/trocap/chuongtrinh/service/ChuongTrinhService.java](com/trocap/chuongtrinh/service/ChuongTrinhService.java#L100-105)  
**Lines**: 100-105  
**Code Pattern**:
```java
if (request.getTenChuongTrinh() != null) ct.setTenChuongTrinh(request.getTenChuongTrinh());
if (request.getMoTa() != null) ct.setMoTa(request.getMoTa());
```
**Problem**: Repeated null checks everywhere. If someone forgets a null check, old value remains.  
**Recommendation**: Use builder pattern or utility method for partial updates.

#### Issue 8.3: Missing payment amount validation
**Severity**: MEDIUM  
**File**: [com/trocap/chitra/service/ChiTraService.java](com/trocap/chitra/service/ChiTraService.java#L59-70)  
**Lines**: 59-70  
**Problem**: No validation that payment amount doesn't exceed proposed amount in HoSo.  
**Recommendation**: Add business logic validation.

---

### 9. UNUSED IMPORTS AND CODE

#### Issue 9.1: Wildcard imports
**Severity**: LOW  
**Files**: Multiple files use `import org.springframework.web.bind.annotation.*;`  
**Examples**:
- [com/trocap/auditlog/controller/AuditLogController.java](com/trocap/auditlog/controller/AuditLogController.java#L14)#L14
- [com/trocap/chitra/controller/ChiTraController.java](com/trocap/chitra/controller/ChiTraController.java#L17)#L17
- [com/trocap/hoso/controller/HoSoController.java](com/trocap/hoso/controller/HoSoController.java#L20)#L20

**Problem**: Wildcard imports reduce code clarity. Harder to track which classes are used.  
**Recommendation**: Use specific imports configured in IDE formatter.

#### Issue 9.2: Unused variable assignments
**Severity**: LOW  
**File**: [com/trocap/hoso/controller/HoSoController.java](com/trocap/hoso/controller/HoSoController.java#L107)  
**Line**: 107  
**Code Pattern**:
```java
hoSoService.search(user.getId(), null, null, null, null, null, pageable)
```
**Problem**: Multiple null parameters passed. Unclear what each null represents.  
**Recommendation**: Use named parameters or builder pattern.

---

## ADDITIONAL RECOMMENDATIONS

### 10. CODE QUALITY ISSUES

#### Issue 10.1: Tight coupling in services
**Severity**: LOW  
**Files**: Multiple service files  
**Problem**: Services directly depend on repositories and other services. Makes testing harder.  
**Recommendation**: Consider dependency injection patterns more explicitly.

#### Issue 10.2: No pagination limit enforcement
**Severity**: MEDIUM  
**File**: [com/trocap/hoso/controller/HoSoController.java](com/trocap/hoso/controller/HoSoController.java#L30-32)  
**Lines**: 30-32  
**Code Pattern**:
```java
@RequestParam(defaultValue = "10") int size
```
**Problem**: User can request any page size, including very large values (e.g., size=1000000), causing memory issues.  
**Recommendation**: Validate max page size.

#### Issue 10.3: Missing logging in critical operations
**Severity**: MEDIUM  
**File**: [com/trocap/chitra/service/ChiTraService.java](com/trocap/chitra/service/ChiTraService.java#L117-120)  
**Lines**: 117-120  
**Problem**: Payment status updates not logged for audit trail.  
**Recommendation**: Add @Sl4j logging and log all status changes.

#### Issue 10.4: Incomplete error messages
**Severity**: LOW  
**File**: [com/trocap/common/exception/GlobalExceptionHandler.java](com/trocap/common/exception/GlobalExceptionHandler.java#L73)  
**Line**: 73  
**Code Pattern**:
```java
.body(ApiResponse.error(ex.getMessage() != null ? ex.getMessage() : "Lỗi hệ thống"));
```
**Problem**: Generic error message "Lỗi hệ thống" doesn't help debugging.  
**Recommendation**: Log full stack trace server-side and return generic message to client.

---

## SUMMARY STATISTICS

| Issue Category | Count | Severity |
|---|---|---|
| Null Pointer Exceptions | 4 | HIGH/MEDIUM |
| Unchecked Exceptions | 2 | HIGH/MEDIUM |
| Resource Leaks | 2 | HIGH/MEDIUM |
| Type/Validation Issues | 3 | MEDIUM |
| SQL/Query Injection | 3 | MEDIUM/LOW |
| Auth/Authorization | 4 | HIGH/MEDIUM |
| Validation Issues | 3 | MEDIUM |
| Logic Errors | 3 | MEDIUM |
| Unused Code | 2 | LOW |
| Code Quality | 4 | MEDIUM/LOW |
| **TOTAL** | **30** | **MIXED** |

---

## PRIORITY FIXES

### 🔴 CRITICAL (Fix First)
1. Issue 6.1 - Missing role validation in payment creation
2. Issue 2.1 - Broad exception catching in MongoIndexFixer
3. Issue 1.1 - Potential NPE from `.orElse(null)` patterns
4. Issue 8.1 - Silent budget deduction failures

### 🟠 HIGH (Fix Next)
1. Issue 5.1-5.2 - Input concatenation in error messages
2. Issue 3.1 - Resource handling in Excel export
3. Issue 10.2 - Missing pagination limit enforcement

### 🟡 MEDIUM (Fix Later)
1. All validation and authorization inconsistencies
2. Exception handling improvements
3. Code quality and logging enhancements

---

## ACTIONABLE IMPROVEMENTS

### Immediate Actions (1-2 weeks)
- [ ] Replace all `.orElse(null)` with `.orElseThrow()` or `.ifPresent()`
- [ ] Fix auth/authorization on payment and file access operations
- [ ] Add file size validation
- [ ] Replace generic Exception catches with specific types

### Short-term (2-4 weeks)
- [ ] Implement consistent @PreAuthorize across all controllers
- [ ] Add pagination size limits
- [ ] Create utility methods for null-safe operations
- [ ] Add comprehensive logging to critical operations

### Medium-term (1-2 months)
- [ ] Refactor to remove tight coupling
- [ ] Add integration tests for security checks
- [ ] Create DTOs with comprehensive validation
- [ ] Implement proper error reporting and audit trails

