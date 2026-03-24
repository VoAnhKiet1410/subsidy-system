// ============================================================
// MongoDB Migration Script: Flat → Module-based Architecture
// ============================================================
// Chạy script này trực tiếp trong MongoDB Shell (mongosh) hoặc
// thông qua Compass → Open Shell
//
// Kết nối: mongosh "mongodb://localhost:27017/trocap"
// Chạy:     load("migrate_data.js")
// ============================================================

// ──────────────────────────────────────────────────────────────
// 1. MIGRATE USERS: Chuyển đổi roles cũ → mới
// ──────────────────────────────────────────────────────────────
print("=== [1/6] Migrating Users ===")

// 1a. Thêm field 'roles' (array) nếu chưa có
db.users.find({ roles: { $exists: false } }).forEach(user => {
  let newRole = user.role
  // Mapping roles cũ → mới
  if (newRole === 'REVIEWER') newRole = 'OFFICER'
  else if (newRole === 'FINANCE') newRole = 'ACCOUNTANT'
  else if (newRole === 'VIEWER') newRole = 'CITIZEN'
  else if (!newRole) newRole = 'CITIZEN'

  db.users.updateOne(
    { _id: user._id },
    {
      $set: {
        roles: [newRole],
        status: user.status || 'ACTIVE'
      }
    }
  )
})

// 1b. Cập nhật roles trong array nếu đã có
db.users.updateMany(
  { roles: 'REVIEWER' },
  { $set: { 'roles.$': 'OFFICER' } }
)
db.users.updateMany(
  { roles: 'FINANCE' },
  { $set: { 'roles.$': 'ACCOUNTANT' } }
)
db.users.updateMany(
  { roles: 'VIEWER' },
  { $set: { 'roles.$': 'CITIZEN' } }
)

print(`  Updated ${db.users.countDocuments()} users`)

// ──────────────────────────────────────────────────────────────
// 2. MIGRATE BENEFICIARIES: category/status thành string
// ──────────────────────────────────────────────────────────────
print("=== [2/6] Migrating Beneficiaries ===")

// Beneficiaries collection đã dùng string trước → chỉ cần verify
const benCount = db.beneficiaries.countDocuments()
print(`  ${benCount} beneficiaries (no structural changes needed)`)

// ──────────────────────────────────────────────────────────────
// 3. MIGRATE PROGRAMS: status/type thành string
// ──────────────────────────────────────────────────────────────
print("=== [3/6] Migrating Programs ===")
const progCount = db.programs.countDocuments()
print(`  ${progCount} programs (no structural changes needed)`)

// ──────────────────────────────────────────────────────────────
// 4. MIGRATE APPLICATIONS → Tách documents + aiAnalysis
// ──────────────────────────────────────────────────────────────
print("=== [4/6] Migrating Applications (extract documents & AI analysis) ===")

let docsMigrated = 0
let aiMigrated = 0

db.applications.find().forEach(app => {
  // 4a. Tách embedded documents → collection 'documents'
  if (app.documents && Array.isArray(app.documents) && app.documents.length > 0) {
    app.documents.forEach(doc => {
      // Kiểm tra xem đã migrate chưa (tránh duplicate)
      const exists = db.documents.findOne({
        hoSoId: app._id.toString(),
        name: doc.name
      })
      if (!exists) {
        db.documents.insertOne({
          hoSoId: app._id.toString(),
          name: doc.name || 'Tài liệu',
          url: doc.url || '',
          type: doc.type || 'OTHER',
          verified: doc.verified || false,
          createdAt: app.createdAt || new Date()
        })
        docsMigrated++
      }
    })
  }

  // 4b. Tách embedded aiAnalysis → collection 'ai_evaluations'
  if (app.aiAnalysis && typeof app.aiAnalysis === 'object') {
    const exists = db.ai_evaluations.findOne({
      hoSoId: app._id.toString()
    })
    if (!exists) {
      db.ai_evaluations.insertOne({
        hoSoId: app._id.toString(),
        trustScore: app.aiAnalysis.trustScore || 0,
        riskLevel: app.aiAnalysis.riskLevel || 'MEDIUM',
        recommendation: app.aiAnalysis.recommendation || '',
        details: app.aiAnalysis,
        createdAt: app.createdAt || new Date()
      })
      aiMigrated++
    }
  }
})

print(`  Extracted ${docsMigrated} documents, ${aiMigrated} AI evaluations`)

// ──────────────────────────────────────────────────────────────
// 5. MIGRATE TRANSACTIONS: type/status/paymentMethod → string
// ──────────────────────────────────────────────────────────────
print("=== [5/6] Migrating Transactions ===")
const txCount = db.transactions.countDocuments()
print(`  ${txCount} transactions (no structural changes needed)`)

// ──────────────────────────────────────────────────────────────
// 6. MIGRATE AUDIT LOGS: userId→username, entity→entityType
// ──────────────────────────────────────────────────────────────
print("=== [6/6] Migrating Audit Logs ===")

db.audit_logs.find({ username: { $exists: false } }).forEach(log => {
  db.audit_logs.updateOne(
    { _id: log._id },
    {
      $set: {
        username: log.userId || 'system',
        entityType: log.entity || log.entityType || 'Unknown'
      },
      $unset: {
        userId: '',
        entity: '',
        timestamp: ''
      }
    }
  )
})

// Cập nhật reviewer references
db.audit_logs.updateMany(
  { username: 'reviewer1' },
  { $set: { username: 'officer1' } }
)
db.audit_logs.updateMany(
  { username: 'finance1' },
  { $set: { username: 'accountant1' } }
)
db.audit_logs.updateMany(
  { username: 'viewer1' },
  { $set: { username: 'citizen1' } }
)

print(`  Updated ${db.audit_logs.countDocuments()} audit logs`)

// ──────────────────────────────────────────────────────────────
// SUMMARY
// ──────────────────────────────────────────────────────────────
print("\n=== Migration Complete ===")
print(`  Users:         ${db.users.countDocuments()}`)
print(`  Beneficiaries: ${db.beneficiaries.countDocuments()}`)
print(`  Programs:      ${db.programs.countDocuments()}`)
print(`  Applications:  ${db.applications.countDocuments()}`)
print(`  Documents:     ${db.documents.countDocuments()}`)
print(`  AI Evaluations:${db.ai_evaluations.countDocuments()}`)
print(`  Transactions:  ${db.transactions.countDocuments()}`)
print(`  Audit Logs:    ${db.audit_logs.countDocuments()}`)
print(`  Notifications: ${db.notifications.countDocuments()}`)
print("\nDone! Restart backend to verify.")
