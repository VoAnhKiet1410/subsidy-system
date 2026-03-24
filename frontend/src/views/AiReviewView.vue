<template>
  <div class="min-h-screen bg-slate-50/60">

    <!-- ── TOPBAR ── -->
    <div class="bg-white border-b border-slate-200/80 px-8 py-4 flex items-center gap-4">
      <div class="flex-1">
        <p class="text-[11px] font-black uppercase tracking-widest text-primary/60 mb-0.5">AI Analysis Engine</p>
        <h2 class="text-xl font-black text-slate-800">Tài liệu &amp; Đánh giá AI</h2>
      </div>
      <!-- Queue indicator -->
      <div class="flex items-center gap-2 px-4 py-2 bg-amber-50 border border-amber-200 rounded-xl">
        <span class="w-2 h-2 rounded-full bg-amber-400 animate-pulse"></span>
        <span class="text-xs font-bold text-amber-700">{{ queue.length }} hồ sơ chờ AI</span>
      </div>
      <button @click="batchRun" class="flex items-center gap-2 px-4 py-2.5 bg-primary text-white text-sm font-bold rounded-xl hover:opacity-90 shadow-md shadow-primary/20">
        <span class="material-symbols-outlined text-sm">smart_toy</span>Chạy AI hàng loạt
      </button>
    </div>

    <div class="flex h-[calc(100vh-73px)]">

      <!-- ── LEFT: APPLICATION QUEUE ── -->
      <div class="w-72 bg-white border-r border-slate-200/80 flex flex-col flex-shrink-0 overflow-hidden">
        <div class="px-4 py-4 border-b border-slate-100">
          <div class="relative">
            <span class="material-symbols-outlined absolute left-3 top-2.5 text-slate-400 text-sm">search</span>
            <input v-model="qSearch" placeholder="Tìm hồ sơ..." class="w-full pl-9 pr-3 py-2 bg-slate-50 border border-slate-200 rounded-lg text-sm focus:outline-none focus:ring-2 focus:ring-primary/20 focus:bg-white" />
          </div>
        </div>
        <div class="flex-1 overflow-y-auto divide-y divide-slate-50">
          <div v-for="app in filteredQueue" :key="app.id"
            @click="selectApp(app)"
            :class="['px-4 py-3.5 cursor-pointer hover:bg-slate-50 transition-colors', selectedApp?.id === app.id ? 'bg-primary/5 border-l-2 border-primary' : '']">
            <div class="flex items-start gap-3">
              <div :class="['w-8 h-8 rounded-xl flex items-center justify-center flex-shrink-0 text-xs font-black', app.diem_uu_tien >= 80 ? 'bg-amber-100 text-amber-700' : 'bg-primary/10 text-primary']">
                {{ app.diem_uu_tien }}
              </div>
              <div class="flex-1 min-w-0">
                <p class="font-bold text-slate-800 text-sm truncate">{{ app.ten_nguoi_nop }}</p>
                <p class="text-xs text-slate-400">{{ formatMaHoSo(app.id) }}</p>
                <div class="flex items-center gap-1.5 mt-1.5">
                  <span v-for="tag in app.tags" :key="tag.label"
                    :class="['px-1.5 py-0.5 rounded text-[9px] font-black', tag.color]">{{ tag.label }}</span>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- ── CENTER: DOCUMENT PREVIEW ── -->
      <div class="flex-1 flex flex-col overflow-hidden min-w-0">
        <template v-if="!selectedApp">
          <div class="flex-1 flex flex-col items-center justify-center text-slate-400">
            <div class="w-20 h-20 rounded-3xl bg-slate-100 flex items-center justify-center mb-4">
              <span class="material-symbols-outlined text-4xl text-slate-300">psychology</span>
            </div>
            <p class="font-bold text-slate-500">Chọn hồ sơ để phân tích</p>
            <p class="text-sm">Nhấp vào hồ sơ bên trái để xem tài liệu và kết quả AI</p>
          </div>
        </template>
        <template v-else>
          <!-- Doc toolbar -->
          <div class="bg-white border-b border-slate-100 px-6 py-3 flex items-center gap-3">
            <div class="flex items-center gap-2 flex-1 min-w-0">
              <span class="material-symbols-outlined text-slate-400 text-sm">folder</span>
              <span class="font-bold text-slate-700 text-sm truncate">{{ selectedApp.ten_nguoi_nop }}</span>
              <span class="text-slate-300">·</span>
              <span class="text-xs text-slate-400">{{ formatMaHoSo(selectedApp.id) }}</span>
            </div>
            <div class="flex gap-1">
              <button v-for="doc in selectedApp.documents" :key="doc.id"
                @click="selectDoc(doc)"
                :class="['flex items-center gap-1.5 px-3 py-1.5 rounded-lg text-xs font-semibold transition-colors', selectedDoc?.id === doc.id ? 'bg-primary text-white' : 'bg-slate-100 text-slate-600 hover:bg-slate-200']">
                <span class="material-symbols-outlined text-xs" style="font-variation-settings:'FILL' 1;">{{ doc.loai_file.includes('pdf') ? 'picture_as_pdf' : 'image' }}</span>
                {{ doc.ten_tai_lieu }}
              </button>
            </div>
          </div>

          <!-- Preview area -->
          <div class="flex-1 overflow-auto bg-slate-100/60 flex items-center justify-center p-6">
            <div v-if="selectedDoc" class="w-full max-w-xl">
              <!-- PDF / Image Placeholder -->
              <div class="bg-white rounded-2xl shadow-lg overflow-hidden border border-slate-200">
                <div class="bg-slate-800 px-4 py-3 flex items-center gap-3">
                  <div class="flex gap-1.5">
                    <div class="w-3 h-3 rounded-full bg-red-500"></div>
                    <div class="w-3 h-3 rounded-full bg-amber-400"></div>
                    <div class="w-3 h-3 rounded-full bg-emerald-500"></div>
                  </div>
                  <span class="text-slate-400 text-xs flex-1 text-center truncate">{{ selectedDoc.ten_tai_lieu }} · {{ selectedDoc.loai_file }}</span>
                  <button class="text-slate-400 hover:text-white transition-colors"><span class="material-symbols-outlined text-sm">download</span></button>
                </div>
                <!-- File preview -->
                <div class="h-96 bg-slate-200 flex items-center justify-center relative">
                  <div class="text-center">
                    <div :class="['w-20 h-20 rounded-3xl flex items-center justify-center mx-auto mb-4', selectedDoc.loai_file.includes('pdf') ? 'bg-red-100' : 'bg-blue-100']">
                      <span class="material-symbols-outlined text-4xl" style="font-variation-settings:'FILL' 1;" :class="selectedDoc.loai_file.includes('pdf') ? 'text-red-500' : 'text-blue-500'">
                        {{ selectedDoc.loai_file.includes('pdf') ? 'picture_as_pdf' : 'image' }}
                      </span>
                    </div>
                    <p class="font-bold text-slate-600">{{ selectedDoc.ten_tai_lieu }}</p>
                    <p class="text-xs text-slate-400 mt-1">{{ selectedDoc.loai_file }} · {{ selectedDoc.kich_thuoc || '2.4 MB' }}</p>
                    <button class="mt-4 px-4 py-2 bg-slate-700 text-white text-xs font-bold rounded-xl hover:bg-slate-600">
                      <span class="material-symbols-outlined text-xs mr-1">visibility</span>Xem tài liệu
                    </button>
                  </div>
                  <!-- OCR badge -->
                  <div class="absolute top-3 right-3">
                    <span :class="['px-2.5 py-1 rounded-full text-[10px] font-black', selectedDoc.trang_thai_ocr === 'DONE' ? 'bg-emerald-500 text-white' : 'bg-amber-400 text-white']">
                      {{ selectedDoc.trang_thai_ocr === 'DONE' ? '✓ OCR' : '⏳ OCR' }}
                    </span>
                  </div>
                </div>
                <!-- Metadata bar -->
                <div class="px-5 py-3 bg-slate-50 border-t border-slate-100 flex items-center gap-5 text-xs text-slate-500">
                  <span class="flex items-center gap-1"><span class="material-symbols-outlined text-xs">folder</span>{{ selectedDoc.loai_tai_lieu }}</span>
                  <span class="flex items-center gap-1"><span class="material-symbols-outlined text-xs">schedule</span>{{ formatDate(selectedDoc.ngay_upload || new Date()) }}</span>
                  <span class="flex items-center gap-1"><span class="material-symbols-outlined text-xs">storage</span>{{ selectedDoc.kich_thuoc || '2.4 MB' }}</span>
                </div>
              </div>

              <!-- Thumbnail strip -->
              <div class="flex gap-3 mt-4 justify-center">
                <div v-for="doc in selectedApp.documents" :key="'thumb'+doc.id"
                  @click="selectDoc(doc)"
                  :class="['w-16 h-20 rounded-xl flex flex-col items-center justify-center gap-1 cursor-pointer transition-all border-2',
                    selectedDoc?.id === doc.id ? 'border-primary bg-primary/5' : 'border-transparent bg-white hover:border-slate-200']">
                  <span class="material-symbols-outlined text-xl" style="font-variation-settings:'FILL' 1;" :class="doc.loai_file.includes('pdf') ? 'text-red-400' : 'text-blue-400'">
                    {{ doc.loai_file.includes('pdf') ? 'picture_as_pdf' : 'image' }}
                  </span>
                  <p class="text-[8px] text-slate-500 text-center font-semibold leading-tight px-1 truncate w-full text-center">{{ doc.ten_tai_lieu }}</p>
                </div>
              </div>
            </div>
          </div>
        </template>
      </div>

      <!-- ── RIGHT: OCR + AI PANEL ── -->
      <div v-if="selectedApp" class="w-96 bg-white border-l border-slate-200/80 flex flex-col overflow-hidden flex-shrink-0">
        <!-- Tab bar -->
        <div class="flex border-b border-slate-100 flex-shrink-0">
          <button v-for="t in ['OCR', 'AI']" :key="t" @click="rightTab=t"
            :class="['flex-1 py-3.5 text-xs font-black uppercase tracking-widest transition-colors', rightTab===t ? 'text-primary border-b-2 border-primary' : 'text-slate-400 hover:text-slate-600']">
            <span class="material-symbols-outlined text-sm align-middle mr-1">{{ t==='OCR' ? 'document_scanner' : 'psychology' }}</span>{{ t }}
          </button>
        </div>

        <div class="flex-1 overflow-y-auto">

          <!-- OCR TAB -->
          <div v-if="rightTab==='OCR'" class="p-5 space-y-4">
            <template v-if="selectedDoc?.ocr_text">
              <div class="flex items-center justify-between">
                <p class="text-[10px] font-black uppercase tracking-widest text-slate-400">Văn bản trích xuất</p>
                <span class="px-2.5 py-1 bg-emerald-100 text-emerald-700 text-[10px] font-black rounded-full">✓ OCR hoàn tất</span>
              </div>
              <!-- OCR text with highlights -->
              <div class="bg-slate-50 rounded-xl p-4 font-mono text-xs text-slate-700 leading-6 whitespace-pre-wrap border border-slate-200" v-html="highlightOcr(selectedDoc.ocr_text)"></div>
              <!-- Key extracted fields -->
              <div class="bg-blue-50 border border-blue-100 rounded-xl p-4">
                <p class="text-[10px] font-black uppercase tracking-widest text-blue-400 mb-3">Trường dữ liệu trích xuất</p>
                <div class="space-y-2">
                  <div v-for="field in selectedDoc.extracted_fields || []" :key="field.key" class="flex justify-between text-xs">
                    <span class="text-slate-500">{{ field.key }}</span>
                    <span class="font-bold text-slate-800 text-right max-w-[160px] truncate">{{ field.value }}</span>
                  </div>
                </div>
              </div>
              <button @click="copyOcrText" class="w-full py-2.5 border border-slate-200 text-slate-600 text-xs font-semibold rounded-xl hover:bg-slate-50 flex items-center justify-center gap-2">
                <span class="material-symbols-outlined text-sm">content_copy</span>Sao chép văn bản
              </button>
            </template>
            <template v-else>
              <div class="py-12 text-center text-slate-400">
                <span class="material-symbols-outlined text-4xl text-slate-200 block mb-2">document_scanner</span>
                <p class="text-sm font-semibold">Chưa có kết quả OCR</p>
                <button @click="runOcr" :disabled="ocrRunning" class="mt-3 px-4 py-2 bg-primary text-white text-xs font-bold rounded-xl hover:opacity-90 disabled:opacity-50 flex items-center gap-2 mx-auto">
                  <span v-if="ocrRunning" class="material-symbols-outlined text-sm animate-spin">progress_activity</span>
                  {{ ocrRunning ? 'Đang xử lý...' : 'Chạy OCR ngay' }}
                </button>
              </div>
            </template>
          </div>

          <!-- AI TAB -->
          <div v-if="rightTab==='AI'" class="p-5 space-y-5">
            <template v-if="selectedApp.danh_gia_ai">

              <!-- AI Status chip -->
              <div :class="['flex items-center gap-3 p-4 rounded-2xl border', selectedApp.danh_gia_ai.de_xuat === 'APPROVE' ? 'bg-emerald-50 border-emerald-200' : 'bg-amber-50 border-amber-200']">
                <div :class="['w-10 h-10 rounded-xl flex items-center justify-center flex-shrink-0', selectedApp.danh_gia_ai.de_xuat === 'APPROVE' ? 'bg-emerald-100' : 'bg-amber-100']">
                  <span class="material-symbols-outlined" style="font-variation-settings:'FILL' 1;" :class="selectedApp.danh_gia_ai.de_xuat === 'APPROVE' ? 'text-emerald-600' : 'text-amber-600'">
                    {{ selectedApp.danh_gia_ai.de_xuat === 'APPROVE' ? 'check_circle' : 'warning' }}
                  </span>
                </div>
                <div>
                  <p class="font-black text-slate-800 text-sm">{{ selectedApp.danh_gia_ai.de_xuat === 'APPROVE' ? 'AI khuyến nghị phê duyệt' : 'AI cần xem xét thêm' }}</p>
                  <p class="text-xs text-slate-500 mt-0.5">Phân tích lúc {{ formatTime(selectedApp.danh_gia_ai.created_at) }}</p>
                </div>
              </div>

              <!-- Score circular -->
              <div class="grid grid-cols-2 gap-3">
                <div class="bg-slate-50 rounded-2xl p-4 text-center border border-slate-100">
                  <div class="relative w-16 h-16 mx-auto mb-2">
                    <svg viewBox="0 0 100 100" class="-rotate-90 w-full h-full">
                      <circle cx="50" cy="50" r="38" fill="none" stroke="#e2e8f0" stroke-width="14"/>
                      <circle cx="50" cy="50" r="38" fill="none"
                        :stroke="selectedApp.danh_gia_ai.diem_uu_tien >= 80 ? '#f59e0b' : '#6366f1'"
                        stroke-width="14"
                        :stroke-dasharray="`${selectedApp.danh_gia_ai.diem_uu_tien * 2.39} 239`"
                        stroke-linecap="round"/>
                    </svg>
                    <div class="absolute inset-0 flex items-center justify-center">
                      <span :class="['text-lg font-black', selectedApp.danh_gia_ai.diem_uu_tien >= 80 ? 'text-amber-600' : 'text-primary']">
                        {{ selectedApp.danh_gia_ai.diem_uu_tien }}
                      </span>
                    </div>
                  </div>
                  <p class="text-[10px] font-black uppercase tracking-widest text-slate-400">Ưu tiên</p>
                </div>
                <div class="bg-slate-50 rounded-2xl p-4 text-center border border-slate-100">
                  <div class="relative w-16 h-16 mx-auto mb-2">
                    <svg viewBox="0 0 100 100" class="-rotate-90 w-full h-full">
                      <circle cx="50" cy="50" r="38" fill="none" stroke="#e2e8f0" stroke-width="14"/>
                      <circle cx="50" cy="50" r="38" fill="none"
                        :stroke="(selectedApp.danh_gia_ai.do_tin_cay || 0) >= 80 ? '#10b981' : '#94a3b8'"
                        stroke-width="14"
                        :stroke-dasharray="`${(selectedApp.danh_gia_ai.do_tin_cay || 0) * 2.39} 239`"
                        stroke-linecap="round"/>
                    </svg>
                    <div class="absolute inset-0 flex items-center justify-center">
                      <span :class="['text-lg font-black', (selectedApp.danh_gia_ai.do_tin_cay || 0) >= 80 ? 'text-emerald-600' : 'text-slate-500']">
                        {{ selectedApp.danh_gia_ai.do_tin_cay }}%
                      </span>
                    </div>
                  </div>
                  <p class="text-[10px] font-black uppercase tracking-widest text-slate-400">Tin cậy</p>
                </div>
              </div>

              <!-- Tags -->
              <div v-if="selectedApp.tags?.length" class="flex flex-wrap gap-2">
                <span v-for="tag in selectedApp.tags" :key="tag.label"
                  :class="['flex items-center gap-1.5 px-3 py-1.5 rounded-xl text-xs font-bold border', tag.full]">
                  <span class="material-symbols-outlined text-xs" style="font-variation-settings:'FILL' 1;">{{ tag.icon }}</span>
                  {{ tag.label }}
                </span>
              </div>

              <!-- Factor bars -->
              <div class="bg-slate-50 rounded-2xl p-4 border border-slate-100">
                <p class="text-[10px] font-black uppercase tracking-widest text-slate-400 mb-3">Yếu tố đánh giá</p>
                <div class="space-y-3">
                  <div v-for="f in selectedApp.danh_gia_ai.cac_yeu_to || []" :key="f.name">
                    <div class="flex justify-between text-xs mb-1">
                      <span class="text-slate-600">{{ f.name }}</span>
                      <span class="font-bold" :class="f.score >= 80 ? 'text-emerald-600' : f.score >= 60 ? 'text-amber-600' : 'text-red-500'">{{ f.score }}</span>
                    </div>
                    <div class="h-1.5 bg-slate-200 rounded-full overflow-hidden">
                      <div :class="['h-full rounded-full', f.score >= 80 ? 'bg-emerald-500' : f.score >= 60 ? 'bg-amber-400' : 'bg-red-400']"
                        :style="{ width: f.score + '%' }"></div>
                    </div>
                  </div>
                </div>
              </div>

              <!-- AI Comment -->
              <div class="bg-gradient-to-br from-indigo-50 to-blue-50 rounded-2xl p-4 border border-indigo-100">
                <div class="flex items-center gap-2 mb-3">
                  <div class="w-7 h-7 rounded-lg bg-primary flex items-center justify-center flex-shrink-0">
                    <span class="material-symbols-outlined text-white text-sm" style="font-variation-settings:'FILL' 1;">smart_toy</span>
                  </div>
                  <span class="text-xs font-black text-primary">Nhận xét từ AI</span>
                </div>
                <p class="text-sm text-slate-700 leading-relaxed">{{ selectedApp.danh_gia_ai.nhan_xet }}</p>
              </div>

              <!-- Risk flags -->
              <div v-if="selectedApp.danh_gia_ai.rui_ro?.length" class="space-y-2">
                <p class="text-[10px] font-black uppercase tracking-widest text-slate-400">Cờ cảnh báo rủi ro</p>
                <div v-for="r in selectedApp.danh_gia_ai.rui_ro" :key="r"
                  class="flex items-start gap-2.5 p-3 bg-red-50 border border-red-100 rounded-xl text-sm text-red-700">
                  <span class="material-symbols-outlined text-red-500 text-sm flex-shrink-0 mt-0.5" style="font-variation-settings:'FILL' 1;">flag</span>
                  {{ r }}
                </div>
              </div>

            </template>
            <div v-else class="py-12 text-center text-slate-400">
              <span class="material-symbols-outlined text-4xl text-slate-200 block mb-2">psychology</span>
              <p class="text-sm font-semibold">Chưa có đánh giá AI</p>
              <button @click="runAi" class="mt-3 px-4 py-2 bg-primary text-white text-xs font-bold rounded-xl hover:opacity-90 flex items-center gap-2 mx-auto">
                <span class="material-symbols-outlined text-sm">smart_toy</span>Phân tích AI ngay
              </button>
            </div>
          </div>

        </div>

        <!-- ── ACTION FOOTER ── -->
        <div class="border-t border-slate-100 p-4 space-y-2.5 flex-shrink-0">
          <div class="grid grid-cols-2 gap-2">
            <button @click="confirmAI" class="py-2.5 bg-emerald-500 text-white text-xs font-bold rounded-xl hover:bg-emerald-600 flex items-center justify-center gap-1.5 shadow-sm">
              <span class="material-symbols-outlined text-sm">verified</span>Xác nhận AI
            </button>
            <button @click="manualReview" class="py-2.5 bg-slate-100 text-slate-600 text-xs font-bold rounded-xl hover:bg-slate-200 flex items-center justify-center gap-1.5">
              <span class="material-symbols-outlined text-sm">manage_accounts</span>Duyệt thủ công
            </button>
          </div>
          <button @click="requestDocs" class="w-full py-2.5 border border-amber-300 text-amber-700 bg-amber-50 text-xs font-bold rounded-xl hover:bg-amber-100 flex items-center justify-center gap-2">
            <span class="material-symbols-outlined text-sm">attach_file_add</span>Yêu cầu bổ sung tài liệu
          </button>
        </div>
      </div>

    </div>

    <!-- ══ MANUAL REVIEW MODAL ══ -->
    <Teleport to="body">
      <div v-if="manualModal.show" class="fixed inset-0 z-[200] flex items-center justify-center bg-black/40 backdrop-blur-sm p-4" @click.self="manualModal.show = false">
        <div class="bg-white rounded-3xl shadow-2xl w-full max-w-md overflow-hidden">
          <div :class="['h-1.5', manualModal.action === 'approve' ? 'bg-gradient-to-r from-emerald-400 to-emerald-600' : 'bg-gradient-to-r from-red-400 to-red-600']"></div>
          <div class="p-8 space-y-5">
            <div class="flex items-center gap-4">
              <div :class="['w-12 h-12 rounded-2xl flex items-center justify-center', manualModal.action === 'approve' ? 'bg-emerald-100 text-emerald-600' : 'bg-red-100 text-red-600']">
                <span class="material-symbols-outlined text-2xl" style="font-variation-settings:'FILL' 1;">{{ manualModal.action === 'approve' ? 'check_circle' : 'cancel' }}</span>
              </div>
              <div>
                <h3 class="text-xl font-black text-slate-800">{{ manualModal.action === 'approve' ? 'Phê duyệt thủ công' : 'Từ chối hồ sơ' }}</h3>
                <p class="text-xs text-slate-400">{{ formatMaHoSo(selectedApp?.id) }} · {{ selectedApp?.ten_nguoi_nop }}</p>
              </div>
            </div>

            <!-- Chọn hành động nếu chưa chọn -->
            <div v-if="!manualModal.action" class="grid grid-cols-2 gap-3">
              <button @click="manualModal.action = 'approve'" class="py-4 bg-emerald-50 border border-emerald-200 rounded-2xl text-center hover:bg-emerald-100 transition-colors">
                <span class="material-symbols-outlined text-2xl text-emerald-600 block mb-1" style="font-variation-settings:'FILL' 1;">check_circle</span>
                <p class="font-bold text-emerald-700 text-sm">Phê duyệt</p>
              </button>
              <button @click="manualModal.action = 'reject'" class="py-4 bg-red-50 border border-red-200 rounded-2xl text-center hover:bg-red-100 transition-colors">
                <span class="material-symbols-outlined text-2xl text-red-600 block mb-1" style="font-variation-settings:'FILL' 1;">cancel</span>
                <p class="font-bold text-red-700 text-sm">Từ chối</p>
              </button>
            </div>

            <!-- Ghi chú -->
            <div v-if="manualModal.action">
              <label class="block text-sm font-semibold text-slate-600 mb-1.5">
                {{ manualModal.action === 'approve' ? 'Ghi chú phê duyệt (tùy chọn)' : 'Lý do từ chối *' }}
              </label>
              <textarea v-model="manualModal.note" rows="4"
                :placeholder="manualModal.action === 'approve' ? 'Ghi chú thêm cho hồ sơ...' : 'Nhập lý do từ chối chi tiết...'"
                class="w-full px-4 py-3 bg-slate-50 border border-slate-200 rounded-xl text-sm focus:bg-white focus:outline-none focus:ring-2 focus:ring-primary/20 resize-none"></textarea>
            </div>

            <div v-if="manualModal.action" class="flex justify-end gap-3">
              <button @click="manualModal.show = false" class="px-5 py-2.5 text-sm font-semibold text-slate-500 hover:bg-slate-100 rounded-xl">Hủy</button>
              <button @click="confirmManual"
                :disabled="manualModal.action === 'reject' && !manualModal.note.trim()"
                :class="['px-6 py-2.5 text-sm font-bold rounded-xl disabled:opacity-50',
                  manualModal.action === 'approve' ? 'bg-emerald-500 text-white hover:bg-emerald-600' : 'bg-red-500 text-white hover:bg-red-600']">
                {{ manualModal.action === 'approve' ? 'Xác nhận duyệt' : 'Xác nhận từ chối' }}
              </button>
            </div>
          </div>
        </div>
      </div>
    </Teleport>

    <!-- ══ REQUEST DOCS MODAL ══ -->
    <Teleport to="body">
      <div v-if="requestDocsModal.show" class="fixed inset-0 z-[200] flex items-center justify-center bg-black/40 backdrop-blur-sm p-4" @click.self="requestDocsModal.show = false">
        <div class="bg-white rounded-3xl shadow-2xl w-full max-w-md overflow-hidden">
          <div class="h-1.5 bg-gradient-to-r from-amber-400 to-amber-600"></div>
          <div class="p-8 space-y-5">
            <div class="flex items-center gap-4">
              <div class="w-12 h-12 rounded-2xl bg-amber-100 text-amber-600 flex items-center justify-center">
                <span class="material-symbols-outlined text-2xl" style="font-variation-settings:'FILL' 1;">attach_file_add</span>
              </div>
              <div>
                <h3 class="text-xl font-black text-slate-800">Yêu cầu bổ sung</h3>
                <p class="text-xs text-slate-400">{{ formatMaHoSo(selectedApp?.id) }} · {{ selectedApp?.ten_nguoi_nop }}</p>
              </div>
            </div>

            <div>
              <p class="text-sm font-semibold text-slate-600 mb-3">Chọn tài liệu cần bổ sung:</p>
              <div class="space-y-2">
                <label v-for="dt in docTypes" :key="dt.value" class="flex items-center gap-3 p-3 bg-slate-50 rounded-xl cursor-pointer hover:bg-slate-100 transition-colors">
                  <input type="checkbox" v-model="requestDocsModal.selected" :value="dt.value"
                    class="w-4 h-4 text-primary rounded border-slate-300 focus:ring-primary/20" />
                  <div class="flex-1">
                    <p class="text-sm font-semibold text-slate-700">{{ dt.label }}</p>
                    <p class="text-[10px] text-slate-400">{{ dt.desc }}</p>
                  </div>
                </label>
              </div>
            </div>

            <div>
              <label class="block text-sm font-semibold text-slate-600 mb-1.5">Ghi chú cho người nộp (tùy chọn)</label>
              <textarea v-model="requestDocsModal.note" rows="2" placeholder="Hướng dẫn bổ sung..."
                class="w-full px-4 py-3 bg-slate-50 border border-slate-200 rounded-xl text-sm focus:bg-white focus:outline-none focus:ring-2 focus:ring-primary/20 resize-none"></textarea>
            </div>

            <div class="flex justify-end gap-3">
              <button @click="requestDocsModal.show = false" class="px-5 py-2.5 text-sm font-semibold text-slate-500 hover:bg-slate-100 rounded-xl">Hủy</button>
              <button @click="confirmRequestDocs" :disabled="!requestDocsModal.selected.length"
                class="px-6 py-2.5 bg-amber-500 text-white text-sm font-bold rounded-xl hover:bg-amber-600 disabled:opacity-50">
                Gửi yêu cầu ({{ requestDocsModal.selected.length }})
              </button>
            </div>
          </div>
        </div>
      </div>
    </Teleport>

    <!-- ══ BATCH AI MODAL ══ -->
    <Teleport to="body">
      <div v-if="batchModal.show" class="fixed inset-0 z-[200] flex items-center justify-center bg-black/40 backdrop-blur-sm p-4">
        <div class="bg-white rounded-3xl shadow-2xl w-full max-w-md overflow-hidden">
          <div class="h-1.5 bg-gradient-to-r from-primary to-indigo-600"></div>
          <div class="p-8 space-y-5">
            <div class="flex items-center gap-4">
              <div class="w-12 h-12 rounded-2xl bg-primary/10 text-primary flex items-center justify-center">
                <span class="material-symbols-outlined text-2xl" :class="batchModal.running ? 'animate-spin' : ''" style="font-variation-settings:'FILL' 1;">smart_toy</span>
              </div>
              <div>
                <h3 class="text-xl font-black text-slate-800">{{ batchModal.running ? 'AI đang phân tích...' : 'Phân tích hoàn tất!' }}</h3>
                <p class="text-xs text-slate-400">{{ batchModal.done }}/{{ batchModal.total }} hồ sơ</p>
              </div>
            </div>

            <!-- Progress bar -->
            <div class="space-y-2">
              <div class="h-3 bg-slate-100 rounded-full overflow-hidden">
                <div class="h-full bg-gradient-to-r from-primary to-indigo-500 rounded-full transition-all duration-500"
                  :style="{ width: (batchModal.total ? batchModal.done / batchModal.total * 100 : 0) + '%' }"></div>
              </div>
              <p class="text-xs text-slate-500 text-center">{{ batchModal.current || 'Đang khởi tạo...' }}</p>
            </div>

            <!-- Result list -->
            <div v-if="batchModal.results.length" class="max-h-48 overflow-y-auto space-y-1.5">
              <div v-for="r in batchModal.results" :key="r.id"
                class="flex items-center gap-3 p-2.5 bg-slate-50 rounded-xl text-xs">
                <span :class="['material-symbols-outlined text-sm', r.de_xuat === 'APPROVE' ? 'text-emerald-500' : 'text-amber-500']" style="font-variation-settings:'FILL' 1;">
                  {{ r.de_xuat === 'APPROVE' ? 'check_circle' : 'warning' }}
                </span>
                <span class="flex-1 font-semibold text-slate-700">{{ r.name }}</span>
                <span :class="['font-black', r.score >= 80 ? 'text-amber-600' : 'text-primary']">{{ r.score }}</span>
              </div>
            </div>

            <button v-if="!batchModal.running" @click="batchModal.show = false"
              class="w-full py-2.5 bg-primary text-white text-sm font-bold rounded-xl hover:opacity-90">
              Đóng
            </button>
          </div>
        </div>
      </div>
    </Teleport>

  </div>
</template>

<script setup>
import { formatMaHoSo } from '../utils/maHoSo'
import { ref, computed } from 'vue'
import { useUI } from '../stores/ui'

const ui = useUI()
const rightTab    = ref('AI')
const qSearch     = ref('')
const selectedApp = ref(null)
const selectedDoc = ref(null)

// ─── Modals ──────────────────────────────────────────
const manualModal = ref({ show: false, action: '', note: '' })
const requestDocsModal = ref({ show: false, selected: [], note: '' })
const batchModal = ref({ show: false, running: false, done: 0, total: 0, current: '', results: [] })

const docTypes = [
  { value: 'cccd',      label: 'CMND/CCCD',            desc: 'Ảnh chụp 2 mặt rõ nét' },
  { value: 'ho_khau',   label: 'Hộ khẩu',              desc: 'Trang chủ hộ + trang thành viên' },
  { value: 'thu_nhap',  label: 'Xác nhận thu nhập',     desc: 'Do UBND xã/phường cấp' },
  { value: 'y_te',      label: 'Giấy khám sức khỏe',    desc: 'Kết quả khám trong 6 tháng' },
  { value: 'khuyet_tat',label: 'Giấy xác nhận KT',      desc: 'Xác nhận mức độ khuyết tật' },
  { value: 'khac',      label: 'Tài liệu khác',         desc: 'Chứng minh hoàn cảnh' },
]

const MOCK_DOCS_1 = [
  { id:1, ten_tai_lieu:'CCCD', loai_tai_lieu:'Giấy tờ tùy thân', loai_file:'image/jpeg', trang_thai_ocr:'DONE', kich_thuoc:'1.2 MB',
    ocr_text:'HO VÀ TÊN: NGUYỄN THỊ HOA\nSố CCCD: 079123456789\nNgày sinh: 01/01/1960\nGiới tính: Nữ\nQuê quán: Đà Nẵng\nNơi TT: 123 Nguyễn Văn Linh, Đà Nẵng',
    extracted_fields: [{ key:'Họ tên', value:'Nguyễn Thị Hoa' }, { key:'Số CCCD', value:'079123456789' }, { key:'Ngày sinh', value:'01/01/1960' }, { key:'Nơi TT', value:'Đà Nẵng' }],
  },
  { id:2, ten_tai_lieu:'Hộ khẩu', loai_tai_lieu:'Hộ khẩu', loai_file:'application/pdf', trang_thai_ocr:'DONE', kich_thuoc:'3.1 MB', ocr_text:null, extracted_fields:[] },
  { id:3, ten_tai_lieu:'Xác nhận thu nhập', loai_tai_lieu:'Tài chính', loai_file:'application/pdf', trang_thai_ocr:'PENDING', kich_thuoc:'842 KB', ocr_text:null, extracted_fields:[] },
]

const queue = ref([
  { id:1001, ten_nguoi_nop:'Nguyễn Thị Hoa',  diem_uu_tien:87, documents: MOCK_DOCS_1,
    tags:[{ label:'Ưu tiên cao', color:'bg-amber-100 text-amber-700', full:'bg-amber-50 text-amber-700 border-amber-200', icon:'star' },
          { label:'Đủ tài liệu', color:'bg-emerald-100 text-emerald-700', full:'bg-emerald-50 text-emerald-700 border-emerald-200', icon:'check_circle' }],
    danh_gia_ai:{ diem_uu_tien:87, do_tin_cay:85, de_xuat:'APPROVE',
      nhan_xet:'Hồ sơ đầy đủ và hợp lệ. Người nộp đáp ứng đủ điều kiện theo tiêu chí của chương trình. Điểm ưu tiên cao do hoàn cảnh khó khăn và tuổi tác. Khuyến nghị phê duyệt.',
      cac_yeu_to:[{ name:'Tính đầy đủ hồ sơ', score:95 }, { name:'Điều kiện thu nhập', score:88 }, { name:'Điều kiện đối tượng', score:92 }, { name:'Xác thực giấy tờ', score:85 }],
      rui_ro:[], created_at: new Date().toISOString() }
  },
  { id:1002, ten_nguoi_nop:'Trần Văn Minh', diem_uu_tien:54, documents:[
      { id:4, ten_tai_lieu:'CCCD', loai_tai_lieu:'Giấy tờ tùy thân', loai_file:'image/jpeg', trang_thai_ocr:'DONE', kich_thuoc:'980 KB', ocr_text:null, extracted_fields:[] },
    ],
    tags:[{ label:'Thiếu tài liệu', color:'bg-red-100 text-red-600', full:'bg-red-50 text-red-600 border-red-200', icon:'warning' }],
    danh_gia_ai:{ diem_uu_tien:54, do_tin_cay:52, de_xuat:'REVIEW',
      nhan_xet:'Hồ sơ còn thiếu giấy xác nhận thu nhập và hộ khẩu. Không đủ cơ sở để đánh giá điều kiện tài chính. Cần bổ sung trước khi xét duyệt.',
      cac_yeu_to:[{ name:'Tính đầy đủ hồ sơ', score:45 }, { name:'Điều kiện thu nhập', score:60 }, { name:'Điều kiện đối tượng', score:70 }, { name:'Xác thực giấy tờ', score:55 }],
      rui_ro:['Thiếu giấy xác nhận thu nhập', 'Thiếu hộ khẩu thường trú'], created_at: new Date().toISOString() }
  },
  { id:1003, ten_nguoi_nop:'Lê Thị Bình',   diem_uu_tien:91, documents:[
      { id:5, ten_tai_lieu:'CCCD', loai_tai_lieu:'Giấy tờ tùy thân', loai_file:'image/jpeg', trang_thai_ocr:'DONE', kich_thuoc:'1.1 MB',
        ocr_text:'HO VÀ TÊN: LÊ THỊ BÌNH\nSố CCCD: 080987654321\nNgày sinh: 15/06/1955',
        extracted_fields:[{ key:'Họ tên', value:'Lê Thị Bình' }, { key:'Số CCCD', value:'080987654321' }, { key:'Ngày sinh', value:'15/06/1955' }]
      },
    ],
    tags:[{ label:'Ưu tiên cao', color:'bg-amber-100 text-amber-700', full:'bg-amber-50 text-amber-700 border-amber-200', icon:'star' }],
    danh_gia_ai:{ diem_uu_tien:91, do_tin_cay:88, de_xuat:'APPROVE',
      nhan_xet:'Hồ sơ tốt, người nộp là người cao tuổi sống một mình, không có thu nhập ổn định. Điểm ưu tiên rất cao.',
      cac_yeu_to:[{ name:'Tính đầy đủ hồ sơ', score:85 }, { name:'Điều kiện thu nhập', score:90 }, { name:'Điều kiện đối tượng', score:95 }, { name:'Xác thực giấy tờ', score:88 }],
      rui_ro:[], created_at: new Date().toISOString() }
  },
])

const filteredQueue = computed(() => {
  if (!qSearch.value) return queue.value
  return queue.value.filter(a => a.ten_nguoi_nop.toLowerCase().includes(qSearch.value.toLowerCase()) || String(a.id).includes(qSearch.value))
})

function selectApp(app) {
  selectedApp.value = app
  selectedDoc.value = app.documents?.[0] || null
  rightTab.value = 'OCR' // xem OCR trước
}
function selectDoc(doc) { selectedDoc.value = doc; rightTab.value = 'OCR' }

// ─── OCR Helpers ─────────────────────────────────────
function highlightOcr(text) {
  if (!text) return ''
  const keywords = ['HO VÀ TÊN','Số CCCD','Ngày sinh','Quê quán','Nơi TT','Giới tính']
  let out = text.replace(/&/g,'&amp;').replace(/</g,'&lt;').replace(/>/g,'&gt;')
  keywords.forEach(k => {
    out = out.replace(new RegExp(k, 'g'), `<mark class="bg-yellow-200 px-0.5 rounded">${k}</mark>`)
  })
  return out
}

function copyOcrText() {
  if (!selectedDoc.value?.ocr_text) return
  navigator.clipboard.writeText(selectedDoc.value.ocr_text)
    .then(() => ui.showSuccess('Đã sao chép văn bản OCR!'))
    .catch(() => ui.showError('Không thể sao chép'))
}

// ─── Chạy OCR cho 1 tài liệu ────────────────────────
const ocrRunning = ref(false)
async function runOcr() {
  if (!selectedDoc.value || ocrRunning.value) return
  ocrRunning.value = true
  ui.showToast({ message: 'Đang chạy OCR cho ' + selectedDoc.value.ten_tai_lieu + '...', type: 'info' })
  // Simulate OCR processing
  await sleep(2000)
  selectedDoc.value.trang_thai_ocr = 'DONE'
  selectedDoc.value.ocr_text = `HỌ VÀ TÊN: ${selectedApp.value.ten_nguoi_nop.toUpperCase()}\nSố giấy tờ: Đã xác minh\nNgày cấp: ${formatDate(new Date())}\nNội dung: Tài liệu hợp lệ`
  selectedDoc.value.extracted_fields = [
    { key: 'Họ tên', value: selectedApp.value.ten_nguoi_nop },
    { key: 'Trạng thái', value: 'Đã xác minh' },
    { key: 'Ngày xử lý', value: formatDate(new Date()) },
  ]
  ocrRunning.value = false
  ui.showSuccess('OCR hoàn tất cho ' + selectedDoc.value.ten_tai_lieu)
}

// ─── Xác nhận AI → duyệt + xóa khỏi queue ──────────
function confirmAI() {
  if (!selectedApp.value) return
  const app = selectedApp.value
  const isApprove = app.danh_gia_ai?.de_xuat === 'APPROVE'

  // Cập nhật trạng thái
  app.trang_thai = isApprove ? 'APPROVED' : 'REVIEWING'

  // Xóa khỏi queue
  const idx = queue.value.findIndex(a => a.id === app.id)
  if (idx !== -1) queue.value.splice(idx, 1)

  ui.showSuccess(`Đã xác nhận AI → ${isApprove ? 'Phê duyệt' : 'Chuyển xét duyệt'} formatMaHoSo(app.id)`)

  // Chuyển sang hồ sơ tiếp theo
  selectedApp.value = queue.value[0] || null
  selectedDoc.value = selectedApp.value?.documents?.[0] || null
}

// ─── Duyệt thủ công → mở modal ──────────────────────
function manualReview() {
  if (!selectedApp.value) return
  manualModal.value = { show: true, action: '', note: '' }
}

function confirmManual() {
  const app = selectedApp.value
  if (!app) return

  if (manualModal.value.action === 'approve') {
    app.trang_thai = 'APPROVED'
    app.ghi_chu_duyet = manualModal.value.note
    const idx = queue.value.findIndex(a => a.id === app.id)
    if (idx !== -1) queue.value.splice(idx, 1)
    ui.showSuccess(`Đã phê duyệt thủ công formatMaHoSo(app.id)!`)
  } else {
    app.trang_thai = 'REJECTED'
    app.ly_do_tu_choi = manualModal.value.note
    const idx = queue.value.findIndex(a => a.id === app.id)
    if (idx !== -1) queue.value.splice(idx, 1)
    ui.showError(`Đã từ chối formatMaHoSo(app.id): ${manualModal.value.note}`)
  }

  manualModal.value.show = false
  selectedApp.value = queue.value[0] || null
  selectedDoc.value = selectedApp.value?.documents?.[0] || null
}

// ─── Yêu cầu bổ sung tài liệu ──────────────────────
function requestDocs() {
  if (!selectedApp.value) return
  requestDocsModal.value = { show: true, selected: [], note: '' }
}

function confirmRequestDocs() {
  const app = selectedApp.value
  if (!app) return
  const labels = requestDocsModal.value.selected.map(v => docTypes.find(d => d.value === v)?.label).filter(Boolean)

  // Cập nhật tags → thêm "Chờ bổ sung"
  const hasTag = app.tags.some(t => t.label === 'Chờ bổ sung')
  if (!hasTag) {
    app.tags.push({ label: 'Chờ bổ sung', color: 'bg-amber-100 text-amber-700', full: 'bg-amber-50 text-amber-700 border-amber-200', icon: 'attach_file_add' })
  }
  // Xóa tag "Đủ tài liệu" nếu có
  app.tags = app.tags.filter(t => t.label !== 'Đủ tài liệu')

  requestDocsModal.value.show = false
  ui.showSuccess(`Đã gửi yêu cầu bổ sung: ${labels.join(', ')}`)
}

// ─── Chạy AI cho 1 hồ sơ ────────────────────────────
const aiRunning = ref(false)
async function runAi() {
  if (!selectedApp.value || aiRunning.value) return
  aiRunning.value = true
  rightTab.value = 'AI'

  // Simulate AI processing
  await sleep(2500)

  const score = Math.floor(60 + Math.random() * 40)
  const confidence = Math.min(99, Math.max(50, score + Math.floor((Math.random() - 0.5) * 16)))
  const isApprove = score >= 70

  selectedApp.value.diem_uu_tien = score
  selectedApp.value.danh_gia_ai = {
    diem_uu_tien: score,
    do_tin_cay: confidence,
    de_xuat: isApprove ? 'APPROVE' : 'REVIEW',
    nhan_xet: isApprove
      ? 'Hồ sơ đáp ứng các tiêu chí cơ bản. AI khuyến nghị phê duyệt với điểm ưu tiên ' + score + '.'
      : 'Hồ sơ chưa đủ điều kiện hoặc cần xem xét thêm. Điểm tin cậy ' + confidence + '% — cần cán bộ kiểm tra.',
    cac_yeu_to: [
      { name: 'Tính đầy đủ hồ sơ', score: Math.floor(50 + Math.random() * 50) },
      { name: 'Điều kiện thu nhập', score: Math.floor(40 + Math.random() * 60) },
      { name: 'Điều kiện đối tượng', score: Math.floor(60 + Math.random() * 40) },
      { name: 'Xác thực giấy tờ', score: Math.floor(50 + Math.random() * 50) },
    ],
    rui_ro: isApprove ? [] : ['Cần xác minh thêm thông tin thu nhập'],
    created_at: new Date().toISOString(),
  }

  aiRunning.value = false
  ui.showSuccess(`AI phân tích hoàn tất! Điểm: ${score} · Tin cậy: ${confidence}%`)
}

// ─── Chạy AI hàng loạt ──────────────────────────────
async function batchRun() {
  if (batchModal.value.running) return
  batchModal.value = { show: true, running: true, done: 0, total: queue.value.length, current: '', results: [] }

  for (let i = 0; i < queue.value.length; i++) {
    const app = queue.value[i]
    batchModal.value.current = `Đang phân tích: ${app.ten_nguoi_nop} (formatMaHoSo(app.id))...`

    await sleep(1200 + Math.random() * 800)

    const score = Math.floor(60 + Math.random() * 40)
    const confidence = Math.min(99, Math.max(50, score + Math.floor((Math.random() - 0.5) * 16)))
    const isApprove = score >= 70

    // Cập nhật data
    app.diem_uu_tien = score
    app.danh_gia_ai = {
      diem_uu_tien: score,
      do_tin_cay: confidence,
      de_xuat: isApprove ? 'APPROVE' : 'REVIEW',
      nhan_xet: isApprove ? 'AI khuyến nghị phê duyệt.' : 'Cần xem xét thêm.',
      cac_yeu_to: [
        { name: 'Tính đầy đủ hồ sơ', score: Math.floor(50 + Math.random() * 50) },
        { name: 'Điều kiện thu nhập', score: Math.floor(40 + Math.random() * 60) },
        { name: 'Điều kiện đối tượng', score: Math.floor(60 + Math.random() * 40) },
        { name: 'Xác thực giấy tờ', score: Math.floor(50 + Math.random() * 50) },
      ],
      rui_ro: isApprove ? [] : ['Cần xác minh thêm'],
      created_at: new Date().toISOString(),
    }

    batchModal.value.done = i + 1
    batchModal.value.results.push({ id: app.id, name: app.ten_nguoi_nop, score, de_xuat: isApprove ? 'APPROVE' : 'REVIEW' })
  }

  batchModal.value.running = false
  batchModal.value.current = 'Hoàn tất!'
}

// ─── Helpers ─────────────────────────────────────────
function formatDate(d) { return new Date(d).toLocaleDateString('vi-VN') }
function formatTime(d) { if (!d) return '—'; return new Date(d).toLocaleTimeString('vi-VN', { hour:'2-digit', minute:'2-digit' }) }
function sleep(ms) { return new Promise(r => setTimeout(r, ms)) }
</script>
