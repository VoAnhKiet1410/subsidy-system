<template>
  <div class="min-h-screen bg-slate-50/60">

    <!-- ── STICKY HEADER ── -->
    <div class="bg-white border-b border-slate-200/80 px-8 py-4 sticky top-0 z-30 flex items-center gap-4">
      <button @click="$router.back()" class="w-9 h-9 rounded-xl flex items-center justify-center text-slate-500 hover:bg-slate-100 transition-colors flex-shrink-0">
        <span class="material-symbols-outlined">arrow_back</span>
      </button>
      <div class="flex-1 min-w-0">
        <div class="flex items-center gap-3 flex-wrap">
          <h2 class="text-lg font-black text-slate-800">#HS-{{ app.id }}</h2>
          <span :class="['px-3 py-1 rounded-full text-xs font-black', st.badge]">{{ st.label }}</span>
          <span v-if="app.diem_uu_tien >= 80" class="flex items-center gap-1 px-2.5 py-1 bg-amber-100 text-amber-700 rounded-full text-[10px] font-black">
            <span class="material-symbols-outlined text-sm" style="font-variation-settings:'FILL' 1;">star</span>Ưu tiên cao
          </span>
        </div>
        <p class="text-xs text-slate-400 mt-0.5">Nộp ngày {{ formatDate(app.ngay_nop_ho_so) }} · {{ app.chuong_trinh?.ten_chuong_trinh }}</p>
      </div>
      <!-- Quick actions header -->
      <div class="flex items-center gap-2 flex-shrink-0">
        <button @click="sendNotif" class="flex items-center gap-1.5 px-3 py-2 bg-slate-100 text-slate-600 text-xs font-bold rounded-xl hover:bg-slate-200">
          <span class="material-symbols-outlined text-sm">notifications</span>Thông báo
        </button>
        <button v-if="canReview && app.trang_thai==='PENDING'" @click="showRejectModal=true" class="flex items-center gap-1.5 px-3 py-2 bg-red-50 text-red-600 text-xs font-bold rounded-xl hover:bg-red-100">
          <span class="material-symbols-outlined text-sm">cancel</span>Từ chối
        </button>
        <button v-if="canReview && app.trang_thai==='PENDING'" @click="approveApp" class="flex items-center gap-1.5 px-4 py-2 bg-emerald-500 text-white text-xs font-bold rounded-xl hover:bg-emerald-600 shadow-md shadow-emerald-200">
          <span class="material-symbols-outlined text-sm">check_circle</span>Phê duyệt
        </button>
        <button v-if="canPay && app.trang_thai==='APPROVED'" @click="activeTab='payment';showPayModal=true" class="flex items-center gap-1.5 px-4 py-2 bg-primary text-white text-xs font-bold rounded-xl hover:opacity-90 shadow-md shadow-primary/20">
          <span class="material-symbols-outlined text-sm">payments</span>Tạo chi trả
        </button>
      </div>
    </div>

    <!-- ── LOADING ── -->
    <div v-if="loading" class="flex justify-center py-24">
      <span class="material-symbols-outlined animate-spin text-5xl text-primary">progress_activity</span>
    </div>

    <div v-else class="flex gap-6 p-8">

      <!-- ── MAIN CONTENT ── -->
      <div class="flex-1 min-w-0 space-y-5">

        <!-- Hero status card -->
        <div :class="['rounded-2xl p-6 flex items-center gap-5 border', st.heroBg, st.border]">
          <div :class="['w-14 h-14 rounded-2xl flex items-center justify-center flex-shrink-0', st.iconBg]">
            <span class="material-symbols-outlined text-2xl" style="font-variation-settings:'FILL' 1;" :class="st.iconColor">{{ st.sym }}</span>
          </div>
          <div class="flex-1">
            <p class="font-black text-slate-800 text-lg">{{ st.label }}</p>
            <p class="text-sm text-slate-500 mt-0.5">{{ st.desc }}</p>
            <div v-if="app.ly_do_tu_choi" class="mt-2 text-sm text-red-700 bg-red-100 rounded-lg px-3 py-2 font-medium">
              ❌ {{ app.ly_do_tu_choi }}
            </div>
          </div>
          <div class="text-right flex-shrink-0">
            <p class="text-xs text-slate-400 font-semibold">Ngày cập nhật</p>
            <p class="font-black text-slate-700">{{ formatDate(app.updated_at || app.ngay_nop_ho_so) }}</p>
          </div>
        </div>

        <!-- TABS -->
        <div class="flex gap-1 bg-slate-100 p-1 rounded-2xl w-fit">
          <button v-for="t in tabs" :key="t.key" @click="activeTab=t.key"
            :class="['flex items-center gap-2 px-4 py-2 rounded-xl text-sm font-semibold transition-all', activeTab===t.key ? 'bg-white text-primary shadow-sm' : 'text-slate-500 hover:text-slate-700']">
            <span class="material-symbols-outlined text-sm">{{ t.icon }}</span>{{ t.label }}
          </button>
        </div>

        <!-- TAB: Tổng quan -->
        <div v-if="activeTab==='overview'" class="space-y-5">
          <div class="grid grid-cols-1 md:grid-cols-2 gap-5">
            <!-- Người nộp -->
            <div class="bg-white rounded-2xl border border-slate-200/80 shadow-sm p-6">
              <h4 class="text-[10px] font-black uppercase tracking-widest text-slate-400 mb-4">Người nộp hồ sơ</h4>
              <div class="flex items-center gap-4 mb-4">
                <div class="w-12 h-12 rounded-2xl bg-primary/10 text-primary flex items-center justify-center text-lg font-black">
                  {{ (app.nguoi_dung?.ten_day_du||'ND').split(' ').slice(-2).map(w=>w[0]).join('') }}
                </div>
                <div>
                  <p class="font-black text-slate-800">{{ app.nguoi_dung?.ten_day_du || 'N/A' }}</p>
                  <p class="text-xs text-slate-400">{{ app.nguoi_dung?.username }}</p>
                </div>
              </div>
              <div class="space-y-2">
                <InfoRow icon="mail" :val="app.nguoi_dung?.email" />
                <InfoRow icon="phone" :val="app.nguoi_dung?.so_dien_thoai" />
                <InfoRow icon="location_on" :val="app.nguoi_dung?.dia_chi" />
              </div>
            </div>
            <!-- Chương trình -->
            <div class="bg-white rounded-2xl border border-slate-200/80 shadow-sm p-6">
              <h4 class="text-[10px] font-black uppercase tracking-widest text-slate-400 mb-4">Chương trình trợ cấp</h4>
              <p class="font-black text-slate-800 text-base mb-2">{{ app.chuong_trinh?.ten_chuong_trinh }}</p>
              <p class="text-sm text-slate-500 mb-4 leading-relaxed">{{ app.chuong_trinh?.mo_ta || 'Chưa có mô tả.' }}</p>
              <div class="space-y-2 pt-3 border-t border-slate-100">
                <div class="flex justify-between text-xs"><span class="text-slate-400">Trạng thái</span><span class="font-bold text-emerald-600">{{ app.chuong_trinh?.trang_thai || 'ACTIVE' }}</span></div>
                <div class="flex justify-between text-xs"><span class="text-slate-400">Bắt đầu</span><span class="font-semibold text-slate-700">{{ formatDate(app.chuong_trinh?.ngay_bat_dau) }}</span></div>
                <div class="flex justify-between text-xs"><span class="text-slate-400">Kết thúc</span><span class="font-semibold text-slate-700">{{ formatDate(app.chuong_trinh?.ngay_ket_thuc) }}</span></div>
              </div>
            </div>
          </div>
          <!-- Đối tượng -->
          <div class="bg-white rounded-2xl border border-slate-200/80 shadow-sm p-6">
            <h4 class="text-[10px] font-black uppercase tracking-widest text-slate-400 mb-3">Đối tượng hưởng trợ cấp</h4>
            <div class="flex items-center gap-4">
              <div class="w-11 h-11 rounded-xl bg-blue-100 text-blue-600 flex items-center justify-center">
                <span class="material-symbols-outlined" style="font-variation-settings:'FILL' 1;">groups</span>
              </div>
              <div>
                <p class="font-black text-slate-800">{{ app.doi_tuong?.ten_doi_tuong || '—' }}</p>
                <p class="text-sm text-slate-500">{{ app.doi_tuong?.mo_ta || 'Chưa có mô tả.' }}</p>
              </div>
            </div>
          </div>
        </div>

        <!-- TAB: Tài liệu -->
        <div v-if="activeTab==='docs'" class="bg-white rounded-2xl border border-slate-200/80 shadow-sm overflow-hidden">
          <div class="px-6 py-5 border-b border-slate-100 flex items-center justify-between">
            <h4 class="font-black text-slate-800">Tài liệu đính kèm</h4>
            <span class="px-2.5 py-1 bg-slate-100 text-slate-600 text-xs font-bold rounded-lg">{{ app.documents?.length || 0 }} tài liệu</span>
          </div>
          <div v-if="!app.documents?.length" class="py-16 text-center text-slate-400">
            <span class="material-symbols-outlined text-5xl text-slate-200 block mb-2">attach_file</span>Chưa có tài liệu
          </div>
          <div v-else class="divide-y divide-slate-50">
            <div v-for="doc in app.documents" :key="doc.id" class="flex items-center gap-4 px-6 py-4 hover:bg-slate-50 transition-colors">
              <div class="w-10 h-10 rounded-xl bg-slate-100 flex items-center justify-center flex-shrink-0">
                <span class="material-symbols-outlined text-slate-500" style="font-variation-settings:'FILL' 1;">{{ doc.loai_file?.includes('pdf') ? 'picture_as_pdf' : 'image' }}</span>
              </div>
              <div class="flex-1 min-w-0">
                <p class="font-semibold text-slate-800 text-sm truncate">{{ doc.ten_tai_lieu }}</p>
                <p class="text-xs text-slate-400">{{ doc.loai_tai_lieu }} · {{ doc.kich_thuoc || '—' }}</p>
              </div>
              <span :class="['px-2.5 py-1 rounded-full text-[10px] font-black flex-shrink-0', doc.trang_thai_ocr==='DONE' ? 'bg-emerald-100 text-emerald-700' : 'bg-amber-100 text-amber-700']">
                {{ doc.trang_thai_ocr==='DONE' ? '✓ OCR hoàn tất' : '⏳ Đang OCR' }}
              </span>
              <div class="flex gap-2 flex-shrink-0">
                <button class="p-2 rounded-lg bg-slate-100 text-slate-500 hover:bg-slate-200 transition-colors"><span class="material-symbols-outlined text-sm">visibility</span></button>
                <button class="p-2 rounded-lg bg-primary/10 text-primary hover:bg-primary/20 transition-colors"><span class="material-symbols-outlined text-sm">download</span></button>
              </div>
            </div>
          </div>
          <!-- OCR result -->
          <div v-if="app.documents?.some(d=>d.ocr_text)" class="border-t border-slate-100 p-6">
            <h5 class="text-xs font-black uppercase tracking-widest text-slate-400 mb-3">Kết quả OCR</h5>
            <div v-for="doc in app.documents.filter(d=>d.ocr_text)" :key="'ocr'+doc.id" class="bg-slate-50 rounded-xl p-4 mb-3 last:mb-0">
              <p class="text-[10px] font-bold text-slate-400 mb-2">{{ doc.ten_tai_lieu }}</p>
              <p class="text-sm text-slate-700 font-mono leading-relaxed">{{ doc.ocr_text }}</p>
            </div>
          </div>
        </div>

        <!-- TAB: Đánh giá AI -->
        <div v-if="activeTab==='ai'" class="space-y-5">
          <div v-if="!app.danh_gia_ai" class="bg-white rounded-2xl border border-slate-200/80 p-16 text-center text-slate-400">
            <span class="material-symbols-outlined text-5xl text-slate-200 block mb-2">psychology</span>Chưa có đánh giá AI
          </div>
          <template v-else>
            <!-- Score cards -->
            <div class="grid grid-cols-3 gap-5">
              <div class="bg-white rounded-2xl border border-slate-200/80 shadow-sm p-6 text-center">
                <p class="text-xs font-black uppercase tracking-widest text-slate-400 mb-3">Điểm ưu tiên</p>
                <div class="relative w-24 h-24 mx-auto mb-3">
                  <svg viewBox="0 0 100 100" class="-rotate-90 w-full h-full">
                    <circle cx="50" cy="50" r="40" fill="none" stroke="#f1f5f9" stroke-width="12"/>
                    <circle cx="50" cy="50" r="40" fill="none" :stroke="app.diem_uu_tien>=80?'#f59e0b':'#3b82f6'" stroke-width="12"
                      :stroke-dasharray="`${app.diem_uu_tien*2.51} 251`" stroke-linecap="round"/>
                  </svg>
                  <div class="absolute inset-0 flex items-center justify-center">
                    <span :class="['text-2xl font-black', app.diem_uu_tien>=80?'text-amber-600':'text-primary']">{{ app.diem_uu_tien }}</span>
                  </div>
                </div>
                <span :class="['px-3 py-1 rounded-full text-xs font-black', app.diem_uu_tien>=80?'bg-amber-100 text-amber-700':app.diem_uu_tien>=50?'bg-blue-100 text-blue-700':'bg-slate-100 text-slate-600']">
                  {{ app.diem_uu_tien>=80?'Ưu tiên cao':app.diem_uu_tien>=50?'Trung bình':'Thấp' }}
                </span>
              </div>
              <div class="bg-white rounded-2xl border border-slate-200/80 shadow-sm p-6 text-center">
                <p class="text-xs font-black uppercase tracking-widest text-slate-400 mb-3">Độ tin cậy AI</p>
                <div class="relative w-24 h-24 mx-auto mb-3">
                  <svg viewBox="0 0 100 100" class="-rotate-90 w-full h-full">
                    <circle cx="50" cy="50" r="40" fill="none" stroke="#f1f5f9" stroke-width="12"/>
                    <circle cx="50" cy="50" r="40" fill="none" :stroke="(app.danh_gia_ai.do_tin_cay||0)>=80?'#10b981':'#94a3b8'" stroke-width="12"
                      :stroke-dasharray="`${(app.danh_gia_ai.do_tin_cay||0)*2.51} 251`" stroke-linecap="round"/>
                  </svg>
                  <div class="absolute inset-0 flex items-center justify-center">
                    <span :class="['text-2xl font-black', (app.danh_gia_ai.do_tin_cay||0)>=80?'text-emerald-600':'text-slate-600']">{{ app.danh_gia_ai.do_tin_cay || 0 }}%</span>
                  </div>
                </div>
                <span class="px-3 py-1 rounded-full text-xs font-black bg-emerald-100 text-emerald-700">Tin cậy cao</span>
              </div>
              <div class="bg-white rounded-2xl border border-slate-200/80 shadow-sm p-6 flex flex-col justify-center">
                <p class="text-xs font-black uppercase tracking-widest text-slate-400 mb-3">Đề xuất AI</p>
                <div :class="['text-center py-4 rounded-xl font-black text-lg', app.danh_gia_ai.de_xuat==='APPROVE'?'bg-emerald-50 text-emerald-700':'bg-red-50 text-red-600']">
                  {{ app.danh_gia_ai.de_xuat==='APPROVE' ? '✓ Nên duyệt' : '✗ Cần xem lại' }}
                </div>
              </div>
            </div>
            <!-- Nhận xét chi tiết -->
            <div class="bg-white rounded-2xl border border-slate-200/80 shadow-sm p-6">
              <h4 class="text-[10px] font-black uppercase tracking-widest text-slate-400 mb-4">Nhận xét chi tiết</h4>
              <div class="bg-blue-50 border border-blue-100 rounded-xl p-5">
                <div class="flex items-start gap-3">
                  <span class="material-symbols-outlined text-blue-500 text-xl flex-shrink-0" style="font-variation-settings:'FILL' 1;">psychology</span>
                  <p class="text-sm text-blue-900 leading-relaxed">{{ app.danh_gia_ai.nhan_xet || 'Không có nhận xét.' }}</p>
                </div>
              </div>
              <div v-if="app.danh_gia_ai.cac_yeu_to" class="mt-4 space-y-2">
                <p class="text-xs font-bold text-slate-500">Các yếu tố đánh giá:</p>
                <div v-for="f in app.danh_gia_ai.cac_yeu_to" :key="f.name" class="flex items-center gap-3">
                  <span class="text-xs text-slate-600 w-40 flex-shrink-0">{{ f.name }}</span>
                  <div class="flex-1 h-2 bg-slate-100 rounded-full overflow-hidden">
                    <div class="h-full bg-primary rounded-full" :style="{ width: f.score+'%' }"></div>
                  </div>
                  <span class="text-xs font-bold text-slate-600 w-8 text-right">{{ f.score }}</span>
                </div>
              </div>
            </div>
          </template>
        </div>

        <!-- TAB: Chi trả -->
        <div v-if="activeTab==='payment'" class="space-y-5">
          <div v-if="!app.payments?.length" class="bg-white rounded-2xl border border-slate-200/80 p-16 text-center">
            <span class="material-symbols-outlined text-5xl text-slate-200 block mb-2">payments</span>
            <p class="text-slate-500 font-semibold">Chưa có thông tin chi trả</p>
            <button v-if="canPay && app.trang_thai==='APPROVED'" @click="showPayModal=true"
              class="mt-4 px-5 py-2.5 bg-primary text-white text-sm font-bold rounded-xl hover:opacity-90 shadow-md shadow-primary/20">
              <span class="material-symbols-outlined text-sm mr-1">add_circle</span>Tạo chi trả ngay
            </button>
          </div>
          <div v-else class="space-y-4">
            <div v-for="pay in app.payments" :key="pay.id" class="bg-white rounded-2xl border border-slate-200/80 shadow-sm p-6">
              <div class="flex items-center justify-between mb-4">
                <div class="flex items-center gap-3">
                  <div class="w-10 h-10 rounded-xl bg-purple-100 text-purple-600 flex items-center justify-center">
                    <span class="material-symbols-outlined" style="font-variation-settings:'FILL' 1;">payments</span>
                  </div>
                  <div>
                    <p class="font-black text-slate-800 text-lg">{{ formatVnd(pay.so_tien) }}</p>
                    <p class="text-xs text-slate-400">{{ formatDate(pay.ngay_chi_tra) }}</p>
                  </div>
                </div>
                <span :class="['px-3 py-1.5 rounded-full text-xs font-black', payStyle(pay.trang_thai).badge]">{{ payStyle(pay.trang_thai).label }}</span>
              </div>
              <div class="grid grid-cols-3 gap-4 pt-4 border-t border-slate-50 text-xs">
                <div><p class="text-slate-400 mb-0.5">Phương thức</p><p class="font-bold text-slate-700">{{ pay.phuong_thuc==='CHUYEN_KHOAN'?'Chuyển khoản':pay.phuong_thuc==='TIEN_MAT'?'Tiền mặt':'ATM' }}</p></div>
                <div><p class="text-slate-400 mb-0.5">Tài khoản</p><p class="font-bold text-slate-700">{{ pay.so_tai_khoan || '—' }}</p></div>
                <div><p class="text-slate-400 mb-0.5">Ghi chú</p><p class="font-bold text-slate-700">{{ pay.ghi_chu || '—' }}</p></div>
              </div>
            </div>
          </div>
        </div>

        <!-- TAB: Lịch sử xử lý -->
        <div v-if="activeTab==='history'" class="bg-white rounded-2xl border border-slate-200/80 shadow-sm p-6">
          <h4 class="text-[10px] font-black uppercase tracking-widest text-slate-400 mb-6">Lịch sử xử lý</h4>
          <div class="relative">
            <div class="absolute left-5 top-0 bottom-0 w-0.5 bg-slate-100"></div>
            <div class="space-y-6">
              <div v-for="(ev, i) in timeline" :key="i" class="relative flex gap-4 pl-12">
                <div :class="['absolute left-0 w-10 h-10 rounded-full flex items-center justify-center border-2 border-white shadow-sm flex-shrink-0', ev.done ? ev.color : 'bg-slate-100']">
                  <span class="material-symbols-outlined text-sm" :class="ev.done ? 'text-white' : 'text-slate-400'" style="font-variation-settings:'FILL' 1;">{{ ev.icon }}</span>
                </div>
                <div class="flex-1 pb-6">
                  <p :class="['font-bold text-sm', ev.done ? 'text-slate-800' : 'text-slate-400']">{{ ev.title }}</p>
                  <p class="text-xs text-slate-400 mt-0.5">{{ ev.time || 'Chưa thực hiện' }}</p>
                  <p v-if="ev.note" class="text-xs text-slate-500 mt-1.5 bg-slate-50 rounded-lg px-3 py-2">{{ ev.note }}</p>
                </div>
              </div>
            </div>
          </div>
        </div>

      </div><!-- end main -->

      <!-- ── RIGHT SIDEBAR ── -->
      <div class="w-72 flex-shrink-0 space-y-4">

        <!-- AI Score widget -->
        <div class="bg-white rounded-2xl border border-slate-200/80 shadow-sm p-5">
          <p class="text-[10px] font-black uppercase tracking-widest text-slate-400 mb-3">Điểm AI tổng hợp</p>
          <div class="flex items-center gap-3 mb-3">
            <div class="flex-1 h-3 bg-slate-100 rounded-full overflow-hidden">
              <div :class="['h-full rounded-full transition-all duration-700', app.diem_uu_tien>=80?'bg-amber-400':'bg-primary']"
                :style="{ width: (app.diem_uu_tien||0)+'%' }"></div>
            </div>
            <span :class="['font-black text-xl', app.diem_uu_tien>=80?'text-amber-600':'text-primary']">{{ app.diem_uu_tien || '—' }}</span>
          </div>
          <div class="flex items-center gap-2 text-xs text-slate-500">
            <span class="material-symbols-outlined text-sm text-emerald-500">check_circle</span>
            Độ tin cậy: <strong class="text-slate-700">{{ app.danh_gia_ai?.do_tin_cay || '—' }}%</strong>
          </div>
          <div class="flex items-center gap-2 text-xs text-slate-500 mt-1">
            <span class="material-symbols-outlined text-sm text-blue-500">psychology</span>
            Đề xuất: <strong :class="app.danh_gia_ai?.de_xuat==='APPROVE'?'text-emerald-600':'text-red-500'">{{ app.danh_gia_ai?.de_xuat==='APPROVE'?'Nên duyệt':'Xem lại' }}</strong>
          </div>
        </div>

        <!-- Quick Info -->
        <div class="bg-white rounded-2xl border border-slate-200/80 shadow-sm p-5 space-y-3">
          <p class="text-[10px] font-black uppercase tracking-widest text-slate-400">Thông tin nhanh</p>
          <div v-for="info in quickInfo" :key="info.label" class="flex justify-between items-center py-2 border-b border-slate-50 last:border-0">
            <span class="text-xs text-slate-400">{{ info.label }}</span>
            <span class="text-xs font-bold text-slate-700">{{ info.value }}</span>
          </div>
        </div>

        <!-- Action panel -->
        <div v-if="canReview" class="bg-white rounded-2xl border border-slate-200/80 shadow-sm p-5 space-y-2">
          <p class="text-[10px] font-black uppercase tracking-widest text-slate-400 mb-3">Hành động</p>
          <template v-if="app.trang_thai==='PENDING'">
            <button @click="approveApp" class="w-full py-2.5 bg-emerald-500 text-white text-sm font-bold rounded-xl hover:bg-emerald-600 flex items-center justify-center gap-2">
              <span class="material-symbols-outlined text-sm">check_circle</span>Phê duyệt hồ sơ
            </button>
            <button @click="showRejectModal=true" class="w-full py-2.5 bg-red-50 text-red-600 text-sm font-bold rounded-xl hover:bg-red-100 flex items-center justify-center gap-2 border border-red-200">
              <span class="material-symbols-outlined text-sm">cancel</span>Từ chối hồ sơ
            </button>
          </template>
          <button v-if="canPay && app.trang_thai==='APPROVED'" @click="showPayModal=true" class="w-full py-2.5 bg-primary text-white text-sm font-bold rounded-xl hover:opacity-90 flex items-center justify-center gap-2 shadow-md shadow-primary/20">
            <span class="material-symbols-outlined text-sm">payments</span>Tạo chi trả
          </button>
          <button @click="sendNotif" class="w-full py-2.5 bg-slate-50 text-slate-600 text-sm font-semibold rounded-xl hover:bg-slate-100 flex items-center justify-center gap-2 border border-slate-200">
            <span class="material-symbols-outlined text-sm">notifications</span>Gửi thông báo
          </button>
          <button @click="printApp" class="w-full py-2.5 bg-slate-50 text-slate-600 text-sm font-semibold rounded-xl hover:bg-slate-100 flex items-center justify-center gap-2 border border-slate-200">
            <span class="material-symbols-outlined text-sm">print</span>In hồ sơ
          </button>
        </div>

        <!-- Documents summary -->
        <div class="bg-white rounded-2xl border border-slate-200/80 shadow-sm p-5">
          <p class="text-[10px] font-black uppercase tracking-widest text-slate-400 mb-3">Tài liệu</p>
          <div class="flex items-center justify-between">
            <span class="text-2xl font-black text-slate-800">{{ app.documents?.length || 0 }}</span>
            <span class="text-xs text-slate-400">tài liệu</span>
          </div>
          <div class="mt-2 flex gap-2">
            <span class="px-2 py-1 bg-emerald-100 text-emerald-700 text-[10px] font-bold rounded-lg">
              {{ app.documents?.filter(d=>d.trang_thai_ocr==='DONE').length || 0 }} OCR xong
            </span>
            <span class="px-2 py-1 bg-amber-100 text-amber-700 text-[10px] font-bold rounded-lg">
              {{ app.documents?.filter(d=>d.trang_thai_ocr!=='DONE').length || 0 }} chờ
            </span>
          </div>
        </div>
      </div>
    </div>

    <!-- ── REJECT MODAL ── -->
    <Teleport to="body">
      <div v-if="showRejectModal" class="fixed inset-0 z-[200] flex items-center justify-center bg-black/40 backdrop-blur-sm p-4" @click.self="showRejectModal=false">
        <div class="bg-white rounded-3xl shadow-2xl w-full max-w-md overflow-hidden">
          <div class="h-1.5 bg-gradient-to-r from-red-400 to-red-600"></div>
          <div class="p-8 space-y-5">
            <div class="flex items-center gap-4">
              <div class="w-12 h-12 rounded-2xl bg-red-100 text-red-600 flex items-center justify-center">
                <span class="material-symbols-outlined text-2xl" style="font-variation-settings:'FILL' 1;">cancel</span>
              </div>
              <div><h3 class="text-xl font-black text-slate-800">Từ chối hồ sơ</h3><p class="text-xs text-slate-400">#HS-{{ app.id }}</p></div>
            </div>
            <textarea v-model="rejectReason" rows="4" placeholder="Nhập lý do từ chối chi tiết..."
              class="w-full px-4 py-3 bg-slate-50 border border-slate-200 rounded-xl text-sm focus:bg-white focus:outline-none focus:ring-2 focus:ring-red-200 resize-none"></textarea>
            <div class="flex justify-end gap-3">
              <button @click="showRejectModal=false" class="px-5 py-2.5 text-sm font-semibold text-slate-500 hover:bg-slate-100 rounded-xl">Hủy</button>
              <button @click="confirmReject" :disabled="!rejectReason.trim()" class="px-6 py-2.5 bg-red-500 text-white text-sm font-bold rounded-xl hover:bg-red-600 disabled:opacity-50">Xác nhận từ chối</button>
            </div>
          </div>
        </div>
      </div>
    </Teleport>

    <!-- ── PAY MODAL ── -->
    <Teleport to="body">
      <div v-if="showPayModal" class="fixed inset-0 z-[200] flex items-center justify-center bg-black/40 backdrop-blur-sm p-4" @click.self="showPayModal=false">
        <div class="bg-white rounded-3xl shadow-2xl w-full max-w-md overflow-hidden">
          <div class="h-1.5 bg-gradient-to-r from-primary to-blue-400"></div>
          <div class="p-8 space-y-5">
            <div class="flex items-center gap-4">
              <div class="w-12 h-12 rounded-2xl bg-primary/10 text-primary flex items-center justify-center">
                <span class="material-symbols-outlined text-2xl" style="font-variation-settings:'FILL' 1;">payments</span>
              </div>
              <div><h3 class="text-xl font-black text-slate-800">Tạo chi trả</h3><p class="text-xs text-slate-400">#HS-{{ app.id }}</p></div>
            </div>
            <div class="space-y-4">
              <div>
                <label class="block text-sm font-semibold text-slate-600 mb-1.5">Số tiền (VNĐ) <span class="text-red-500">*</span></label>
                <input v-model="payForm.so_tien" type="text" placeholder="VD: 2,000,000"
                  class="w-full px-4 py-3 bg-slate-50 border border-slate-200 rounded-xl text-sm focus:bg-white focus:outline-none focus:ring-2 focus:ring-primary/20" />
              </div>
              <div>
                <label class="block text-sm font-semibold text-slate-600 mb-1.5">Phương thức</label>
                <select v-model="payForm.phuong_thuc" class="w-full px-4 py-3 bg-slate-50 border border-slate-200 rounded-xl text-sm focus:outline-none focus:ring-2 focus:ring-primary/20">
                  <option value="CHUYEN_KHOAN">Chuyển khoản</option>
                  <option value="TIEN_MAT">Tiền mặt</option>
                  <option value="ATM">ATM</option>
                </select>
              </div>
              <div>
                <label class="block text-sm font-semibold text-slate-600 mb-1.5">Ngày chi trả</label>
                <input v-model="payForm.ngay_chi_tra" type="date" class="w-full px-4 py-3 bg-slate-50 border border-slate-200 rounded-xl text-sm focus:outline-none focus:ring-2 focus:ring-primary/20" />
              </div>
              <div>
                <label class="block text-sm font-semibold text-slate-600 mb-1.5">Ghi chú</label>
                <input v-model="payForm.ghi_chu" type="text" placeholder="Ghi chú thêm..." class="w-full px-4 py-3 bg-slate-50 border border-slate-200 rounded-xl text-sm focus:bg-white focus:outline-none focus:ring-2 focus:ring-primary/20" />
              </div>
            </div>
            <div class="flex justify-end gap-3">
              <button @click="showPayModal=false" class="px-5 py-2.5 text-sm font-semibold text-slate-500 hover:bg-slate-100 rounded-xl">Hủy</button>
              <button @click="confirmPay" class="px-6 py-2.5 bg-primary text-white text-sm font-bold rounded-xl hover:opacity-90 shadow-md shadow-primary/20">Xác nhận chi trả</button>
            </div>
          </div>
        </div>
      </div>
    </Teleport>

  </div>
</template>

<script setup>
import { ref, computed, onMounted, h } from 'vue'
import { useRoute } from 'vue-router'
import { authStore } from '../stores/auth'
import { useUI } from '../stores/ui'
import http from '../api/http'

const route = useRoute()
const ui    = useUI()
const loading = ref(true)
const activeTab = ref('overview')
const showRejectModal = ref(false)
const showPayModal = ref(false)
const rejectReason = ref('')
const payForm = ref({ so_tien: '', phuong_thuc: 'CHUYEN_KHOAN', ngay_chi_tra: new Date().toISOString().slice(0,10), ghi_chu: '' })

const InfoRow = {
  props: ['icon', 'val'],
  setup(p) {
    return () => h('div', { class: 'flex items-center gap-2 text-sm' }, [
      h('span', { class: 'material-symbols-outlined text-sm text-slate-400' }, p.icon),
      h('span', { class: 'text-slate-600' }, p.val || '—')
    ])
  }
}

const tabs = [
  { key: 'overview', label: 'Tong quan',  icon: 'info' },
  { key: 'docs',     label: 'Tai lieu',   icon: 'attach_file' },
  { key: 'ai',       label: 'Danh gia AI',icon: 'psychology' },
  { key: 'payment',  label: 'Chi tra',    icon: 'payments' },
  { key: 'history',  label: 'Lich su',    icon: 'history' },
]

const app = ref({
  id: route.params.id, trang_thai: 'DRAFT',
  ngay_nop_ho_so: null,
  nguoi_dung: {}, chuong_trinh: {}, doi_tuong: {},
  danh_gia_ai: null, documents: [], payments: [],
})

const canReview = computed(() => authStore.isAdmin || authStore.isReviewer)
const canPay    = computed(() => authStore.isAdmin || authStore.isFinance)

const STATUS_MAP = {
  DRAFT:        { badge:'bg-slate-100 text-slate-600',      iconBg:'bg-slate-100',    iconColor:'text-slate-500',   sym:'draft',         label:'Bản nháp',      desc:'Hồ sơ chưa được nộp',                heroBg:'bg-slate-50',   border:'border-slate-200' },
  SUBMITTED:    { badge:'bg-amber-100 text-amber-700',      iconBg:'bg-amber-100',    iconColor:'text-amber-600',   sym:'hourglass_top', label:'Chờ xét duyệt', desc:'Hồ sơ đã nộp, chờ cán bộ tiếp nhận', heroBg:'bg-amber-50',   border:'border-amber-200' },
  UNDER_REVIEW: { badge:'bg-blue-100 text-blue-700',        iconBg:'bg-blue-100',     iconColor:'text-blue-600',    sym:'manage_search', label:'Đang xét duyệt',desc:'Hồ sơ đang được xét duyệt',          heroBg:'bg-blue-50',    border:'border-blue-200' },
  APPROVED:     { badge:'bg-emerald-100 text-emerald-700',  iconBg:'bg-emerald-100',  iconColor:'text-emerald-600', sym:'check_circle',  label:'Đã phê duyệt',  desc:'Hồ sơ được chấp thuận',              heroBg:'bg-emerald-50', border:'border-emerald-200' },
  REJECTED:     { badge:'bg-red-100 text-red-700',          iconBg:'bg-red-100',      iconColor:'text-red-600',     sym:'cancel',        label:'Bị từ chối',    desc:'Hồ sơ không đạt điều kiện',          heroBg:'bg-red-50',     border:'border-red-200' },
  PAID:         { badge:'bg-teal-100 text-teal-700',        iconBg:'bg-teal-100',     iconColor:'text-teal-600',    sym:'payments',      label:'Đã chi trả',    desc:'Hồ sơ đã được chi trả tiền',         heroBg:'bg-teal-50',    border:'border-teal-200' },
  // Legacy aliases (phòng trường hợp dữ liệu cũ)
  PENDING:      { badge:'bg-amber-100 text-amber-700',      iconBg:'bg-amber-100',    iconColor:'text-amber-600',   sym:'hourglass_top', label:'Chờ xét duyệt', desc:'Hồ sơ chờ cán bộ tiếp nhận',        heroBg:'bg-amber-50',   border:'border-amber-200' },
  REVIEWING:    { badge:'bg-blue-100 text-blue-700',        iconBg:'bg-blue-100',     iconColor:'text-blue-600',    sym:'manage_search', label:'Đang xét duyệt',desc:'Hồ sơ đang được xét duyệt',          heroBg:'bg-blue-50',    border:'border-blue-200' },
}

const STATUS_FALLBACK = { badge:'bg-slate-100 text-slate-600', iconBg:'bg-slate-100', iconColor:'text-slate-500', sym:'help', label:'Không xác định', desc:'', heroBg:'bg-slate-50', border:'border-slate-200' }
const st = computed(() => STATUS_MAP[app.value.trang_thai] || STATUS_FALLBACK)

const quickInfo = computed(() => [
  { label: 'Mã hồ sơ',    value: `#HS-${app.value.id}` },
  { label: 'Ngày nộp',    value: formatDate(app.value.ngay_nop_ho_so) },
  { label: 'Đối tượng',   value: app.value.doi_tuong?.ten_doi_tuong || '—' },
  { label: 'Chương trình',value: app.value.chuong_trinh?.ten_chuong_trinh || '—' },
  { label: 'Tài liệu',    value: `${app.value.documents?.length || 0} file` },
])

const timeline = computed(() => {
  const a = app.value
  return [
    { title:'Nộp hồ sơ',          icon:'upload_file',  color:'bg-blue-500',   done: true, time: formatDate(a.ngay_nop_ho_so), note: 'Hồ sơ đã được tiếp nhận' },
    { title:'Xét duyệt',           icon:'manage_search',color:'bg-amber-500',  done: ['REVIEWING','APPROVED','REJECTED'].includes(a.trang_thai), time: null },
    { title:'Đánh giá AI',         icon:'psychology',   color:'bg-purple-500', done: !!a.danh_gia_ai, time: a.danh_gia_ai ? 'Đã hoàn tất' : null, note: a.danh_gia_ai ? `Điểm ưu tiên: ${a.danh_gia_ai.diem_uu_tien}` : null },
    { title:'Phê duyệt / Từ chối', icon:'gavel',        color: a.trang_thai === 'REJECTED' ? 'bg-red-500' : 'bg-emerald-500', done: ['APPROVED','REJECTED'].includes(a.trang_thai), time: null, note: a.ly_do_tu_choi },
    { title:'Chi trả',             icon:'payments',     color:'bg-teal-500',   done: a.payments?.some(p => p.trang_thai === 'COMPLETED'), time: null },
  ]
})

async function approveApp() {
  try {
    // Bước 1: Nếu đang SUBMITTED → chuyển UNDER_REVIEW trước
    if (app.value.trang_thai === 'SUBMITTED') {
      await http.patch(`/applications/${app.value.id}/under-review`)
      app.value.trang_thai = 'UNDER_REVIEW'
    }
    // Bước 2: UNDER_REVIEW → APPROVED
    await http.patch(`/applications/${app.value.id}/approve`)
    app.value.trang_thai = 'APPROVED'
    ui.showSuccess(`Đã phê duyệt hồ sơ #HS-${app.value.id}!`)
  } catch (e) {
    ui.showError('Phê duyệt thất bại: ' + (e.response?.data?.message || e.message))
  }
}
async function confirmReject() {
  if (!rejectReason.value.trim()) return
  try {
    // Bước 1: Nếu đang SUBMITTED → chuyển UNDER_REVIEW trước
    if (app.value.trang_thai === 'SUBMITTED') {
      await http.patch(`/applications/${app.value.id}/under-review`)
      app.value.trang_thai = 'UNDER_REVIEW'
    }
    // Bước 2: UNDER_REVIEW → REJECTED
    await http.patch(`/applications/${app.value.id}/reject`, { lyDoTuChoi: rejectReason.value })
    app.value.trang_thai = 'REJECTED'
    app.value.ly_do_tu_choi = rejectReason.value
    showRejectModal.value = false
    ui.showSuccess('Hồ sơ đã bị từ chối.')
  } catch (e) {
    ui.showError('Từ chối thất bại: ' + (e.response?.data?.message || e.message))
  }
}
function confirmPay() {
  if (!app.value.payments) app.value.payments = []
  app.value.payments.push({
    id: Date.now(),
    so_tien: Number(payForm.value.so_tien.replace(/\D/g, '')),
    phuong_thuc: payForm.value.phuong_thuc,
    ngay_chi_tra: payForm.value.ngay_chi_tra,
    ghi_chu: payForm.value.ghi_chu,
    trang_thai: 'PROCESSING',
  })
  showPayModal.value = false
  ui.showSuccess('Đã tạo chi trả!')
}
function sendNotif() { ui.showSuccess('Đã gửi thông báo đến người nộp!') }
function printApp()  { window.print() }

function payStyle(s) {
  const map = {
    PENDING:    { badge:'bg-slate-100 text-slate-500',    label:'Chưa chi' },
    PROCESSING: { badge:'bg-amber-100 text-amber-700',    label:'Đang xử lý' },
    COMPLETED:  { badge:'bg-emerald-100 text-emerald-700',label:'Đã chi' },
    FAILED:     { badge:'bg-red-100 text-red-600',        label:'Thất bại' },
  }
  return map[s] || { badge:'bg-slate-100 text-slate-400', label:'—' }
}
function formatDate(d) { if (!d) return '—'; return new Date(d).toLocaleDateString('vi-VN') }
function formatVnd(v)  {
  if (!v) return '0đ'
  if (v >= 1e9) return (v / 1e9).toFixed(1) + ' Tỷ'
  if (v >= 1e6) return (v / 1e6).toFixed(0) + ' Triệu'
  return v.toLocaleString('vi-VN') + 'đ'
}

onMounted(async () => {
  try {
    const [resApp, resUsers, resProgs, resCats] = await Promise.all([
      http.get(`/applications/${route.params.id}`),
      http.get('/users', { params: { size: 1000 } }).catch(()=>({data:[]})),
      http.get('/programs', { params: { size: 500 } }).catch(()=>({data:[]})),
      http.get('/beneficiary-groups', { params: { size: 1000 } }).catch(()=>({data:[]}))
    ])

    const a = resApp.data?.content || resApp.data || {};
    
    // Attempt mappings
    const usersList = resUsers.data?.content || resUsers.data || [];
    const progsList = resProgs.data?.content || resProgs.data || [];
    const catsList = resCats.data?.content || resCats.data || [];

    const user = usersList.find(u => u.id === a.nguoiDungId || u.username === a.nguoiDungId) || {};
    const prog = progsList.find(p => p.id === a.chuongTrinhId) || {};
    const cat = catsList.find(c => c.id === a.doiTuongId) || {};

    app.value = {
      ...a,
      id: a.id,
      trang_thai: a.trangThai || 'PENDING',
      ngay_nop_ho_so: a.ngayNopHoSo || a.createdAt,
      nguoi_dung: { 
        ten_day_du: user.fullName || user.username || ('Người nộp #' + (a.nguoiDungId||'').substring(0,4)),
        username: user.username || a.nguoiDungId,
        email: user.email,
        so_dien_thoai: user.phone || user.so_dien_thoai,
        dia_chi: user.address || user.dia_chi
      },
      chuong_trinh: {
        ten_chuong_trinh: prog.tenChuongTrinh || prog.name || '—',
        mo_ta: prog.moTa || prog.description,
        trang_thai: prog.status || prog.trangThai || 'ACTIVE',
        ngay_bat_dau: prog.startDate || prog.ngayBatDau,
        ngay_ket_thuc: prog.endDate || prog.ngayKetThuc
      },
      doi_tuong: {
        ten_doi_tuong: cat.fullName || cat.name || cat.tenDoiTuong || '—',
        mo_ta: cat.moTa || cat.description
      },
      diem_uu_tien: a.aiReview?.diemUuTien || a.diemUuTien || null,
      danh_gia_ai: a.aiReview ? {
        diem_uu_tien: a.aiReview.diemUuTien,
        do_tin_cay: a.aiReview.doTinCay,
        de_xuat: a.aiReview.deXuat,
        nhan_xet: a.aiReview.nhanXet || a.aiReview.nhanXetAi || '',
        cac_yeu_to: a.aiReview.cacYeuTo || [],
      } : null,
      documents: a.taiLieuDinhKem || a.documents || [],
      payments: a.chiTra || a.payments || [],
    }
  } catch (e) {
    console.error('Lỗi tải hồ sơ:', e)
    ui.showError('Không thể tải thông tin hồ sơ.')
  }
  loading.value = false
})
</script>

