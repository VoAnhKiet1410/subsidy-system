# Backend Issues - Quick Reference Summary

## Critical Issues (Must Fix Immediately)

| # | Issue | File | Line | Severity | Impact |
|---|-------|------|------|----------|--------|
| 1 | Broad Exception catching silently ignores errors | MongoIndexFixer.java | 43, 69 | HIGH | Index creation failures not reported |
| 2 | Missing role validation on payment creation | ChiTraService.java | 59-75 | HIGH | Any authenticated user can create payments |
| 3 | Potential NPE from `.orElse(null)` with late checks | ChiTraService.java | 125 | MEDIUM-HIGH | Application crash if HoSo deleted |
| 4 | Direct ID concatenation in error messages | Multiple files | Various | MEDIUM-HIGH | Potential XSS if displayed in UI |
| 5 | Silent failure when HoSo not found in payment update | ChiTraService.java | 125-128 | MEDIUM-HIGH | Inconsistent database state |

## High-Risk Security Issues

| # | Vulnerability | File | Line | Risk | Recommendation |
|---|---|---|---|---|---|
| S1 | Insufficient auth in file access | TaiLieuService.java | 71-85 | HIGH | Enforce proper role-based access control |
| S2 | Incomplete permission checks | HoSoController.java | 64-70 | MEDIUM | Use @PreAuthorize consistently |
| S3 | MongoDB Query Injection potential | ChuongTrinhRepository.java | 18 | LOW | Review all @Query annotations |
| S4 | No file upload size limit | FileStorageService.java | 54 | MEDIUM | Add maxFileSize validation |
| S5 | File extension validation not enforced | FileStorageService.java | 105-110 | LOW | Throw exception for invalid extensions |

## Null Pointer Exception Risks (NPE)

| # | Location | Pattern | Line | Risk |
|---|---|---|---|---|
| N1 | ChiTraService.java | `.orElse(null)` then check | 125, 144 | HIGH - silent null checks |
| N2 | HoSoService.java | null before `.isBlank()` | 163, 166 | MEDIUM - NPE if null |
| N3 | HoSoController.java | Unsafe roles handling | 64 | MEDIUM - empty list not handled |
| N4 | JwtUtil.java | Raw cast without type check | 41 | MEDIUM - CCE possible |

## Exception Handling Issues

| # | File | Line | Problem | Recommended Fix |
|---|---|---|---|---|
| E1 | MongoIndexFixer.java | 43, 69 | `catch (Exception e)` | Catch `MongoException`, `DataAccessException` |
| E2 | FileStorageService.java | 91 | IOException ignored | Log or throw or return status |
| E3 | JwtAuthFilter.java | 48 | Broad JwtException catch | Add specific error response |
| E4 | ExcelExportService.java | All methods | `throws IOException` | Handle or cleanup resources |

## Resource Leak Risks

| # | File | Lines | Resource | Mitigation |
|---|---|---|---|---|
| RL1 | ExcelExportService.java | 44+ | XSSFWorkbook | Try-with-resources exists but error handling incomplete |
| RL2 | FileStorageService.java | 77+ | File streams | Implicit stream not closed on error |

## Validation Gaps

| # | File | Line | Missing Validation | Consequence |
|---|---|---|---|---|
| V1 | ExcelExportService.java | N/A | Large export memory limit | OOM on large datasets |
| V2 | PaymentRequest.java | 17 | Negative payment amounts | Negative payment could be created |
| V3 | HoSoController.java | 30-32 | Page size limit | User can request size=1000000 |
| V4 | FileStorageService.java | 105 | No exception on invalid filename | Silent failure with empty string |

## Authorization/Authentication Issues

| # | File | Line | Issue | Required Fix |
|---|---|---|---|---|
| A1 | ChiTraService.java | 59 | No role check on create() | Add @PreAuthorize("hasRole('OFFICER')") |
| A2 | TaiLieuService.java | 81 | Incomplete role check | Support OFFICER role properly |
| A3 | ThongBaoController.java | 123 | Runtime role check | Use @PreAuthorize instead |
| A4 | HoSoService.java | 76-77 | No audit logging | Add audit trail for access |

## Logic Errors

| # | File | Line | Logic Error | Impact |
|---|---|---|---|---|
| L1 | ChiTraService.java | 125-128 | Missing HoSo doesn't fail payment | Orphaned payment record |
| L2 | ChuongTrinhService.java | 86 | No check if budget sufficient | Over-budget spending possible |
| L3 | HoSoService.java | 156-167 | No max amount validation | Unbounded request amounts |
| L4 | ExcelExportService.java | 78+ | Null safety in streams | NPE if field is null |

## Code Quality Issues

| # | File | Type | Issue | Lines Affected |
|---|---|---|---|---|
| Q1 | Multiple | Wildcard imports | `import org.springframework.web.bind.annotation.*` | Various controllers |
| Q2 | HoSoController.java | Magic nulls | Multiple `null` parameters passed | 107 |
| Q3 | Multiple | Repeated patterns | `.getNganSach() != null ? ... : 0.0` | Many files |
| Q4 | All services | Missing audit logs | No logging on state changes | Throughout |
| Q5 | ChiTraService.java | Tight coupling | Direct repository access | 33-36 |

---

## Quick Status Check Checklist

- [ ] All API endpoints have @PreAuthorize annotations
- [ ] All Optional<T> uses orElseThrow() not orElse(null)
- [ ] No generic Exception catches remain
- [ ] All file uploads validated for size/type
- [ ] Pagination limits enforced
- [ ] All state-changing operations logged
- [ ] Role-based access consistent across services
- [ ] No direct ID concatenation in responses
- [ ] File resources properly closed
- [ ] Null checks before method calls

---

## Files Most Problematic (Needs Refactoring)

1. **ChiTraService.java** - Multiple NPE risks, auth issues, missing validations
2. **HoSoController.java** - Inconsistent auth, manual null checks
3. **ExcelExportService.java** - Resource handling, large datasets issue  
4. **MongoIndexFixer.java** - Broad exception handling
5. **FileStorageService.java** - Validation and resource issues

---

## Next Steps

1. Run static analysis with SonarQube for additional findings
2. Add integration tests for authorization scenarios
3. Implement input validation at DTO level
4. Create utility class for null-safe operations
5. Implement comprehensive audit logging
6. Add rate limiting on critical endpoints
7. Conduct security review with penetration testing

