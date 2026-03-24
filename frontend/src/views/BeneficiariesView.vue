<template>
  <div class="min-h-screen bg-slate-50/60">

    <!-- ══ CITIZEN: hồ sơ cá nhân ══ -->
    <template v-if="authStore.isViewer">
      <div class="p-8 space-y-6">
        <div class="flex items-end justify-between">
          <div>
            <p class="text-xs font-bold uppercase tracking-widest text-primary/60 mb-1">Cá nhân</p>
            <h2 class="text-3xl font-extrabold text-slate-800">Hồ sơ của tôi</h2>
          </div>
          <button @click="$router.push('/chuong-trinh')"
            class="flex items-center gap-2 px-5 py-2.5 bg-primary text-white text-sm font-bold rounded-xl hover:opacity-90 shadow-md shadow-primary/20">
            <span class="material-symbols-outlined text-sm">add_circle</span>Đăng ký mới
          </button>
        </div>
        <div class="grid grid-cols-3 gap-5">
          <div v-for="s in viewerStats" :key="s.label" :class="['rounded-2xl p-6 border', s.cls]">
            <p class="text-xs font-bold uppercase tracking-widest opacity-70">{{ s.label }}</p>
            <p class="text-4xl font-black mt-1">{{ s.value }}</p>
          </div>
        </div>
        <div class="bg-white rounded-2xl border border-slate-200/80 shadow-sm overflow-hidden">
          <div class="divide-y divide-slate-50">
            <div v-for="app in myApps" :key="app.id"
              class="px-6 py-5 flex items-center gap-4 hover:bg-slate-50 cursor-pointer transition-colors"
              @click="openQuickView(app)">
              <div :class="['w-11 h-11 rounded-2xl flex items-center justify-center flex-shrink-0', statusStyle(app.trang_thai).bg]">
                <span class="material-symbols-outlined text-lg" style="font-variation-settings:'FILL' 1;" :class="statusStyle(app.trang_thai).icon">{{ statusStyle(app.trang_thai).sym }}</span>
              </div>
              <div class="flex-1 min-w-0">
                <div class="flex items-center gap-2">
                  <p class="font-bold text-slate-800">{{ formatMaHoSo(app.id) }}</p>
                  <span :class="['px-2 py-0.5 rounded-full text-[10px] font-black', statusStyle(app.trang_thai).badge]">{{ statusLabel(app.trang_thai) }}</span>
                </div>
                <p class="text-xs text-slate-400 mt-0.5">{{ app.chuong_trinh?.ten_chuong_trinh || 'N/A' }} · Nộp {{ formatDate(app.ngay_nop_ho_so) }}</p>
              </div>
              <span class="material-symbols-outlined text-slate-300">chevron_right</span>
            </div>
          </div>
        </div>
      </div>
    </template>

    <!-- ══ ADMIN/OFFICER: Quản lý hồ sơ ══ -->
    <template v-else>
      <!-- Header -->
      <div class="bg-white border-b border-slate-200/80 px-8 py-5 flex items-center justify-between sticky top-0 z-30">
        <div>
          <p class="text-xs font-bold uppercase tracking-widest text-primary/60 mb-0.5">Quản lý hệ thống</p>
          <h2 class="text-xl font-black text-slate-800 tracking-tight">Quản lý hồ sơ hỗ trợ</h2>
        </div>
        <div class="flex items-center gap-3">
          <button @click="exportCsv"
            class="flex items-center gap-2 px-4 py-2 bg-slate-100 text-slate-600 text-sm font-semibold rounded-xl hover:bg-slate-200 transition-colors">
            <span class="material-symbols-outlined text-sm">download</span>Xuất CSV
          </button>
          <button v-if="authStore.isAdmin" @click="showCreateModal = true"
            class="flex items-center gap-2 px-4 py-2 bg-primary text-white text-sm font-bold rounded-xl hover:opacity-90 shadow-md shadow-primary/20">
            <span class="material-symbols-outlined text-sm">add_circle</span>Tạo hồ sơ
          </button>
        </div>
      </div>

      <div class="p-8 space-y-5">
        <!-- Summary bar -->
        <div class="grid grid-cols-5 gap-3">
          <div v-for="s in summaryStats" :key="s.label"
            :class="['bg-white border border-slate-200/80 rounded-2xl p-4 text-center cursor-pointer hover:border-primary/40 transition-colors', filterStatus === s.filter ? 'border-primary bg-primary/5' : '']"
            @click="filterStatus = filterStatus === s.filter ? '' : s.filter">
            <p :class="['text-2xl font-black', s.valueColor]">{{ s.value }}</p>
            <p class="text-[10px] font-bold uppercase tracking-wider text-slate-400 mt-0.5">{{ s.label }}</p>
          </div>
        </div>

        <!-- ─── ADVANCED FILTERS ─── -->
        <div class="bg-white rounded-2xl border border-slate-200/80 shadow-sm p-5">
          <div class="grid grid-cols-2 md:grid-cols-3 xl:grid-cols-6 gap-3">
            <!-- Search -->
            <div class="xl:col-span-2 relative">
              <span class="material-symbols-outlined absolute left-3 top-1/2 -translate-y-1/2 text-slate-400 text-sm">search</span>
              <input v-model="search" type="text" placeholder="Tìm mã HS, tên người nộp..."
                class="w-full pl-9 pr-3 py-2.5 bg-slate-50 border border-slate-200 rounded-xl text-sm focus:outline-none focus:ring-2 focus:ring-primary/20 focus:bg-white transition-all" />
            </div>
            <!-- Trạng thái -->
            <select v-model="filterStatus"
              class="px-3 py-2.5 bg-slate-50 border border-slate-200 rounded-xl text-sm text-slate-600 focus:outline-none focus:ring-2 focus:ring-primary/20">
              <option value="">Tất cả trạng thái</option>
              <option v-for="s in statusOptions" :key="s.val" :value="s.val">{{ s.label }}</option>
            </select>
            <!-- Chương trình -->
            <select v-model="filterProgram"
              class="px-3 py-2.5 bg-slate-50 border border-slate-200 rounded-xl text-sm text-slate-600 focus:outline-none focus:ring-2 focus:ring-primary/20">
              <option value="">Tất cả chương trình</option>
              <option v-for="p in programs" :key="p.id" :value="p.id">{{ p.ten_chuong_trinh }}</option>
            </select>
            <!-- Đối tượng -->
            <select v-model="filterCategory"
              class="px-3 py-2.5 bg-slate-50 border border-slate-200 rounded-xl text-sm text-slate-600 focus:outline-none focus:ring-2 focus:ring-primary/20">
              <option value="">Tất cả đối tượng</option>
              <option v-for="c in categories" :key="c.id" :value="c.id">{{ c.ten_doi_tuong }}</option>
            </select>
            <!-- Điểm AI -->
            <select v-model="filterAiScore"
              class="px-3 py-2.5 bg-slate-50 border border-slate-200 rounded-xl text-sm text-slate-600 focus:outline-none focus:ring-2 focus:ring-primary/20">
              <option value="">Tất cả điểm AI</option>
              <option value="high">Ưu tiên cao (≥ 80)</option>
              <option value="med">Trung bình (50-79)</option>
              <option value="low">Thấp (< 50)</option>
            </select>
          </div>

          <!-- Date range + clear -->
          <div class="flex items-center gap-3 mt-3 pt-3 border-t border-slate-100">
            <span class="text-xs font-bold text-slate-400 uppercase tracking-wider flex-shrink-0">Thời gian nộp:</span>
            <input v-model="filterDateFrom" type="date"
              class="px-3 py-2 bg-slate-50 border border-slate-200 rounded-xl text-sm focus:outline-none focus:ring-2 focus:ring-primary/20" />
            <span class="text-slate-400 text-sm">→</span>
            <input v-model="filterDateTo" type="date"
              class="px-3 py-2 bg-slate-50 border border-slate-200 rounded-xl text-sm focus:outline-none focus:ring-2 focus:ring-primary/20" />
            <button v-if="hasFilter" @click="clearFilters"
              class="ml-auto flex items-center gap-1.5 px-3 py-2 bg-red-50 text-red-600 text-xs font-bold rounded-xl hover:bg-red-100 transition-colors">
              <span class="material-symbols-outlined text-sm">filter_alt_off</span>Xóa bộ lọc
            </button>
            <span class="text-xs text-slate-400 ml-auto" v-else>{{ filteredApps.length }} kết quả</span>
          </div>
        </div>

        <!-- ─── DATA TABLE ─── -->
        <div class="bg-white rounded-2xl border border-slate-200/80 shadow-sm overflow-hidden">
          <div v-if="loading" class="flex justify-center py-16">
            <span class="material-symbols-outlined animate-spin text-4xl text-primary">progress_activity</span>
          </div>
          <div v-else-if="filteredApps.length === 0" class="py-20 text-center text-slate-400">
            <span class="material-symbols-outlined text-5xl text-slate-200 block mb-3">folder_open</span>
            <p class="font-semibold">Không tìm thấy hồ sơ nào</p>
          </div>
          <div v-else class="overflow-x-auto">
            <table class="w-full text-sm min-w-[1100px]">
              <thead>
                <tr class="border-b border-slate-100 bg-slate-50/80">
                  <th class="text-left px-4 py-3.5 text-[10px] font-black uppercase tracking-widest text-slate-400 w-28">Mã hồ sơ</th>
                  <th class="text-left px-4 py-3.5 text-[10px] font-black uppercase tracking-widest text-slate-400">Người nộp</th>
                  <th class="text-left px-4 py-3.5 text-[10px] font-black uppercase tracking-widest text-slate-400">Đối tượng</th>
                  <th class="text-left px-4 py-3.5 text-[10px] font-black uppercase tracking-widest text-slate-400">Chương trình</th>
                  <th class="text-left px-4 py-3.5 text-[10px] font-black uppercase tracking-widest text-slate-400 w-28">Ngày nộp</th>
                  <th class="text-left px-4 py-3.5 text-[10px] font-black uppercase tracking-widest text-slate-400 w-28">Trạng thái</th>
                  <th class="text-center px-4 py-3.5 text-[10px] font-black uppercase tracking-widest text-slate-400 w-24">Điểm AI</th>
                  <th class="text-center px-4 py-3.5 text-[10px] font-black uppercase tracking-widest text-slate-400 w-24">Tin cậy</th>
                  <th class="text-left px-4 py-3.5 text-[10px] font-black uppercase tracking-widest text-slate-400 w-28">Chi trả</th>
                  <th class="px-4 py-3.5 w-10"></th>
                </tr>
              </thead>
              <tbody class="divide-y divide-slate-50">
                <tr v-for="app in paginatedApps" :key="app.id"
                  :class="['group transition-colors', app.trang_thai === 'PENDING' ? 'bg-amber-50/60 hover:bg-amber-50' : 'hover:bg-slate-50/80']">
                  <!-- Mã hồ sơ -->
                  <td class="px-4 py-4">
                    <div class="flex items-center gap-2">
                      <span v-if="app.trang_thai === 'PENDING'" class="material-symbols-outlined text-amber-500 text-sm flex-shrink-0" style="font-variation-settings:'FILL' 1;" title="Chưa duyệt — Ưu tiên">star</span>
                      <div>
                        <p class="font-black text-slate-800">{{ formatMaHoSo(app.id) }}</p>
                      </div>
                    </div>
                  </td>
                  <!-- Người nộp -->
                  <td class="px-4 py-4">
                    <div class="flex items-center gap-2.5">
                      <div :class="['w-7 h-7 rounded-lg flex items-center justify-center text-[10px] font-black flex-shrink-0', app.avatarBg]">
                        {{ app.initials }}
                      </div>
                      <div>
                        <p class="font-semibold text-slate-800 text-xs">{{ app.ten_nguoi_nop }}</p>
                        <p class="text-[10px] text-slate-400">{{ app.nguoi_dung?.email || '—' }}</p>
                      </div>
                    </div>
                  </td>
                  <!-- Đối tượng -->
                  <td class="px-4 py-4">
                    <span class="px-2 py-1 bg-slate-100 text-slate-600 text-[11px] font-semibold rounded-lg">
                      {{ app.doi_tuong?.ten_doi_tuong || '—' }}
                    </span>
                  </td>
                  <!-- Chương trình -->
                  <td class="px-4 py-4">
                    <p class="text-xs font-semibold text-slate-700 max-w-[160px] truncate">{{ app.chuong_trinh?.ten_chuong_trinh || '—' }}</p>
                  </td>
                  <!-- Ngày nộp -->
                  <td class="px-4 py-4 text-xs text-slate-500">{{ formatDate(app.ngay_nop_ho_so) }}</td>
                  <!-- Trạng thái -->
                  <td class="px-4 py-4">
                    <span :class="['px-2.5 py-1 rounded-full text-[10px] font-black', statusStyle(app.trang_thai).badge]">
                      {{ statusLabel(app.trang_thai) }}
                    </span>
                  </td>
                  <!-- Điểm AI -->
                  <td class="px-4 py-4 text-center">
                    <div class="flex flex-col items-center gap-1">
                      <span :class="['text-sm font-black', app.diem_uu_tien >= 80 ? 'text-amber-600' : app.diem_uu_tien >= 50 ? 'text-blue-600' : 'text-slate-500']">
                        {{ app.diem_uu_tien ?? '—' }}
                      </span>
                      <div class="w-12 h-1.5 bg-slate-200 rounded-full overflow-hidden">
                        <div :class="['h-full rounded-full', app.diem_uu_tien >= 80 ? 'bg-amber-500' : 'bg-primary']"
                          :style="{ width: (app.diem_uu_tien || 0) + '%' }"></div>
                      </div>
                    </div>
                  </td>
                  <!-- Độ tin cậy -->
                  <td class="px-4 py-4 text-center">
                    <span :class="['text-xs font-bold', (app.do_tin_cay||0) >= 80 ? 'text-emerald-600' : 'text-slate-500']">
                      {{ app.do_tin_cay ? app.do_tin_cay + '%' : '—' }}
                    </span>
                  </td>
                  <!-- Trạng thái chi trả -->
                  <td class="px-4 py-4">
                    <span :class="['px-2 py-1 rounded-lg text-[10px] font-bold', payStatusStyle(app.trang_thai_chi_tra)]">
                      {{ payStatusLabel(app.trang_thai_chi_tra) }}
                    </span>
                  </td>
                  <!-- Actions -->
                  <td class="px-2 py-4">
                    <div class="relative" @click.stop>
                      <button @click="toggleMenu(app.id)"
                        class="w-8 h-8 rounded-xl flex items-center justify-center text-slate-400 hover:bg-slate-100 hover:text-slate-600 transition-colors opacity-0 group-hover:opacity-100">
                        <span class="material-symbols-outlined text-lg">more_vert</span>
                      </button>
                      <!-- Dropdown menu -->
                      <Teleport to="body">
                        <div v-if="openMenuId === app.id"
                          class="fixed z-[200] bg-white rounded-2xl shadow-2xl border border-slate-200/80 py-2 w-52"
                          :style="menuStyle"
                          @click.stop>
                          <button @click="openDetail(app)" class="w-full flex items-center gap-3 px-4 py-2.5 text-sm text-slate-700 hover:bg-slate-50 transition-colors font-medium">
                            <span class="material-symbols-outlined text-sm text-slate-400">open_in_new</span>Xem chi tiết
                          </button>
                          <button @click="openQuickView(app)" class="w-full flex items-center gap-3 px-4 py-2.5 text-sm text-slate-700 hover:bg-slate-50 transition-colors font-medium">
                            <span class="material-symbols-outlined text-sm text-slate-400">preview</span>Xem nhanh
                          </button>
                          <hr class="my-1 border-slate-100">
                          <button v-if="canReview" @click="approveApp(app)" class="w-full flex items-center gap-3 px-4 py-2.5 text-sm text-emerald-700 hover:bg-emerald-50 transition-colors font-semibold">
                            <span class="material-symbols-outlined text-sm">check_circle</span>Duyệt hồ sơ
                          </button>
                          <button v-if="canReview" @click="openRejectModal(app)" class="w-full flex items-center gap-3 px-4 py-2.5 text-sm text-red-600 hover:bg-red-50 transition-colors font-semibold">
                            <span class="material-symbols-outlined text-sm">cancel</span>Từ chối hồ sơ
                          </button>
                          <hr class="my-1 border-slate-100">
                          <button @click="viewDocs(app)" class="w-full flex items-center gap-3 px-4 py-2.5 text-sm text-slate-700 hover:bg-slate-50 transition-colors font-medium">
                            <span class="material-symbols-outlined text-sm text-slate-400">attach_file</span>Xem tài liệu
                          </button>
                          <button @click="viewAI(app)" class="w-full flex items-center gap-3 px-4 py-2.5 text-sm text-slate-700 hover:bg-slate-50 transition-colors font-medium">
                            <span class="material-symbols-outlined text-sm text-slate-400">psychology</span>Đánh giá AI
                          </button>
                          <button v-if="authStore.isAdmin || authStore.isFinance" @click="createPayment(app)" class="w-full flex items-center gap-3 px-4 py-2.5 text-sm text-purple-700 hover:bg-purple-50 transition-colors font-semibold">
                            <span class="material-symbols-outlined text-sm">payments</span>Tạo chi trả
                          </button>
                        </div>
                      </Teleport>
                    </div>
                  </td>
                </tr>
              </tbody>
            </table>
          </div>

          <!-- Pagination -->
          <div v-if="totalPages > 1" class="flex items-center justify-between px-6 py-4 border-t border-slate-100 bg-slate-50/50">
            <p class="text-xs text-slate-400 font-medium">
              Hiển thị {{ (currentPage-1)*pageSize+1 }}–{{ Math.min(currentPage*pageSize, filteredApps.length) }} / {{ filteredApps.length }} hồ sơ
            </p>
            <div class="flex gap-1.5">
              <button @click="currentPage--" :disabled="currentPage===1"
                class="w-8 h-8 rounded-lg flex items-center justify-center text-slate-500 hover:bg-white disabled:opacity-30 disabled:cursor-not-allowed border border-transparent hover:border-slate-200">
                <span class="material-symbols-outlined text-sm">chevron_left</span>
              </button>
              <button v-for="p in pageNumbers" :key="p" @click="currentPage = p"
                :class="['w-8 h-8 rounded-lg text-xs font-bold transition-colors', p === currentPage ? 'bg-primary text-white shadow-sm' : 'text-slate-500 hover:bg-white border border-transparent hover:border-slate-200']">
                {{ p }}
              </button>
              <button @click="currentPage++" :disabled="currentPage===totalPages"
                class="w-8 h-8 rounded-lg flex items-center justify-center text-slate-500 hover:bg-white disabled:opacity-30 disabled:cursor-not-allowed border border-transparent hover:border-slate-200">
                <span class="material-symbols-outlined text-sm">chevron_right</span>
              </button>
            </div>
          </div>
        </div>
      </div>
    </template>

    <!-- ══ QUICK VIEW DRAWER ══ -->
    <Teleport to="body">
      <div v-if="quickViewApp" class="fixed inset-0 z-[150] flex" @click.self="quickViewApp = null">
        <div class="flex-1 bg-black/40 backdrop-blur-sm" @click="quickViewApp = null"></div>
        <div class="w-full max-w-lg bg-white h-full overflow-y-auto shadow-2xl flex flex-col">
          <!-- Drawer header -->
          <div :class="['h-1.5 w-full', statusStyle(quickViewApp.trang_thai).gradient]"></div>
          <div class="flex items-center justify-between px-6 py-5 border-b border-slate-100">
            <div>
              <h3 class="font-black text-slate-800">Xem nhanh · {{ formatMaHoSo(quickViewApp.id) }}</h3>
              <p class="text-xs text-slate-400 mt-0.5">{{ formatDate(quickViewApp.ngay_nop_ho_so) }}</p>
            </div>
            <div class="flex items-center gap-2">
              <button @click="openDetail(quickViewApp)"
                class="flex items-center gap-1.5 px-3 py-1.5 bg-primary/10 text-primary text-xs font-bold rounded-lg hover:bg-primary/20">
                <span class="material-symbols-outlined text-sm">open_in_new</span>Chi tiết
              </button>
              <button @click="quickViewApp = null" class="w-8 h-8 rounded-lg flex items-center justify-center text-slate-400 hover:bg-slate-100">
                <span class="material-symbols-outlined text-lg">close</span>
              </button>
            </div>
          </div>

          <div class="flex-1 p-6 space-y-6">
            <!-- Status hero -->
            <div :class="['rounded-2xl p-5 flex items-center gap-4', statusStyle(quickViewApp.trang_thai).heroBg]">
              <div :class="['w-12 h-12 rounded-2xl flex items-center justify-center flex-shrink-0', statusStyle(quickViewApp.trang_thai).bg]">
                <span class="material-symbols-outlined text-xl" style="font-variation-settings:'FILL' 1;" :class="statusStyle(quickViewApp.trang_thai).icon">{{ statusStyle(quickViewApp.trang_thai).sym }}</span>
              </div>
              <div>
                <p class="font-black text-slate-800">{{ statusLabel(quickViewApp.trang_thai) }}</p>
                <p class="text-xs text-slate-500 mt-0.5">Hồ sơ đang ở trạng thái {{ statusLabel(quickViewApp.trang_thai).toLowerCase() }}</p>
              </div>
            </div>

            <!-- Thông tin cơ bản -->
            <div class="space-y-3">
              <h4 class="text-[10px] font-black uppercase tracking-widest text-slate-400">Thông tin cơ bản</h4>
              <div class="grid grid-cols-2 gap-3">
                <InfoRow label="Người nộp"    :value="quickViewApp.ten_nguoi_nop" />
                <InfoRow label="Ngày nộp"     :value="formatDate(quickViewApp.ngay_nop_ho_so)" />
                <InfoRow label="Chương trình" :value="quickViewApp.chuong_trinh?.ten_chuong_trinh || '—'" />
                <InfoRow label="Đối tượng"    :value="quickViewApp.doi_tuong?.ten_doi_tuong || '—'" />
              </div>
            </div>

            <!-- AI Scores -->
            <div class="space-y-3">
              <h4 class="text-[10px] font-black uppercase tracking-widest text-slate-400">Đánh giá AI</h4>
              <div class="grid grid-cols-2 gap-3">
                <div class="bg-slate-50 rounded-xl p-4 text-center">
                  <p class="text-xs text-slate-400 font-semibold mb-1">Điểm ưu tiên</p>
                  <p :class="['text-3xl font-black', quickViewApp.diem_uu_tien >= 80 ? 'text-amber-600' : 'text-primary']">{{ quickViewApp.diem_uu_tien ?? '—' }}</p>
                  <div class="w-full h-1.5 bg-slate-200 rounded-full mt-2 overflow-hidden">
                    <div class="h-full bg-amber-400 rounded-full" :style="{ width: (quickViewApp.diem_uu_tien||0)+'%' }"></div>
                  </div>
                </div>
                <div class="bg-slate-50 rounded-xl p-4 text-center">
                  <p class="text-xs text-slate-400 font-semibold mb-1">Độ tin cậy</p>
                  <p :class="['text-3xl font-black', (quickViewApp.do_tin_cay||0)>=80 ? 'text-emerald-600' : 'text-slate-600']">{{ quickViewApp.do_tin_cay ? quickViewApp.do_tin_cay + '%' : '—' }}</p>
                  <div class="w-full h-1.5 bg-slate-200 rounded-full mt-2 overflow-hidden">
                    <div class="h-full bg-emerald-400 rounded-full" :style="{ width: (quickViewApp.do_tin_cay||0)+'%' }"></div>
                  </div>
                </div>
              </div>
              <div v-if="quickViewApp.ai_nhan_xet" class="bg-blue-50 border border-blue-100 rounded-xl p-4">
                <p class="text-xs font-bold text-blue-700 mb-1">Nhận xét AI</p>
                <p class="text-sm text-blue-800">{{ quickViewApp.ai_nhan_xet }}</p>
              </div>
            </div>

            <!-- Tài liệu -->
            <div class="space-y-3">
              <h4 class="text-[10px] font-black uppercase tracking-widest text-slate-400">Tài liệu đính kèm ({{ quickViewApp.documents?.length || 0 }})</h4>
              <div v-if="!quickViewApp.documents?.length" class="text-xs text-slate-400 bg-slate-50 rounded-xl p-4 text-center">Chưa có tài liệu</div>
              <div v-for="doc in (quickViewApp.documents||[])" :key="doc.id"
                class="flex items-center gap-3 p-3 bg-slate-50 rounded-xl">
                <span class="material-symbols-outlined text-slate-400 text-lg">description</span>
                <div class="flex-1 min-w-0">
                  <p class="text-xs font-semibold text-slate-700 truncate">{{ doc.ten_tai_lieu }}</p>
                  <p class="text-[10px] text-slate-400">{{ doc.loai_tai_lieu }}</p>
                </div>
                <span :class="['text-[10px] font-bold px-2 py-0.5 rounded-full', doc.trang_thai_ocr === 'DONE' ? 'bg-emerald-100 text-emerald-700' : 'bg-amber-100 text-amber-700']">
                  {{ doc.trang_thai_ocr === 'DONE' ? 'OCR ✓' : 'Chờ OCR' }}
                </span>
              </div>
            </div>

            <!-- Lý do từ chối -->
            <div v-if="quickViewApp.ly_do_tu_choi" class="bg-red-50 border border-red-200 rounded-xl p-4">
              <p class="text-xs font-black text-red-600 uppercase tracking-wider mb-1">Lý do từ chối</p>
              <p class="text-sm text-red-800">{{ quickViewApp.ly_do_tu_choi }}</p>
            </div>
          </div>

          <!-- Drawer footer actions -->
          <div v-if="canReview && quickViewApp.trang_thai === 'PENDING'" class="p-5 border-t border-slate-100 flex gap-3">
            <button @click="approveApp(quickViewApp); quickViewApp = null"
              class="flex-1 py-2.5 bg-emerald-500 text-white text-sm font-bold rounded-xl hover:bg-emerald-600 flex items-center justify-center gap-2">
              <span class="material-symbols-outlined text-sm">check_circle</span>Duyệt hồ sơ
            </button>
            <button @click="openRejectModal(quickViewApp); quickViewApp = null"
              class="flex-1 py-2.5 bg-red-500 text-white text-sm font-bold rounded-xl hover:bg-red-600 flex items-center justify-center gap-2">
              <span class="material-symbols-outlined text-sm">cancel</span>Từ chối
            </button>
          </div>
        </div>
      </div>
    </Teleport>

    <!-- ══ REJECT MODAL ══ -->
    <Teleport to="body">
      <div v-if="rejectModal.show" class="fixed inset-0 z-[200] flex items-center justify-center bg-black/40 backdrop-blur-sm p-4" @click.self="rejectModal.show = false">
        <div class="bg-white rounded-3xl shadow-2xl w-full max-w-md overflow-hidden">
          <div class="h-1.5 bg-gradient-to-r from-red-400 to-red-600"></div>
          <div class="p-8 space-y-5">
            <div class="flex items-center gap-4">
              <div class="w-12 h-12 rounded-2xl bg-red-100 text-red-600 flex items-center justify-center">
                <span class="material-symbols-outlined text-2xl" style="font-variation-settings:'FILL' 1;">cancel</span>
              </div>
              <div>
                <h3 class="text-xl font-black text-slate-800">Từ chối hồ sơ</h3>
                <p class="text-xs text-slate-400">{{ formatMaHoSo(rejectModal.app?.id) }}</p>
              </div>
            </div>
            <div>
              <label class="block text-sm font-semibold text-slate-600 mb-1.5">Lý do từ chối <span class="text-red-500">*</span></label>
              <textarea v-model="rejectModal.reason" rows="4" placeholder="Nhập lý do từ chối chi tiết..."
                class="w-full px-4 py-3 bg-slate-50 border border-slate-200 rounded-xl text-sm focus:bg-white focus:outline-none focus:ring-2 focus:ring-red-200 resize-none"></textarea>
            </div>
            <div class="flex justify-end gap-3">
              <button @click="rejectModal.show = false" class="px-5 py-2.5 text-sm font-semibold text-slate-500 hover:bg-slate-100 rounded-xl">Hủy</button>
              <button @click="confirmReject" :disabled="!rejectModal.reason.trim()"
                class="px-6 py-2.5 bg-red-500 text-white text-sm font-bold rounded-xl hover:bg-red-600 disabled:opacity-50">Xác nhận từ chối</button>
            </div>
          </div>
        </div>
      </div>
    </Teleport>

  </div>
</template>

<script setup>
import { ref, computed, onMounted, onUnmounted, h } from 'vue'
import { formatMaHoSo } from '../utils/maHoSo'
import { useRouter } from 'vue-router'
import { authStore } from '../stores/auth'
import { useUI } from '../stores/ui'
import http from '../api/http'

const router = useRouter()
const ui = useUI()

// ─── inline InfoRow component ────────────────────────
const InfoRow = {
  props: ['label', 'value'],
  setup(p) { return () => h('div', { class: 'bg-slate-50 rounded-xl p-3' }, [
    h('p', { class: 'text-[10px] text-slate-400 font-bold uppercase tracking-wider mb-0.5' }, p.label),
    h('p', { class: 'text-sm font-semibold text-slate-700 truncate' }, p.value || '—'),
  ]) }
}

const loading = ref(true)
const apps = ref([])
const myApps = ref([])

// ─── Filters ─────────────────────────────────────────
const search       = ref('')
const filterStatus  = ref('')
const filterProgram = ref('')
const filterCategory= ref('')
const filterAiScore = ref('')
const filterDateFrom= ref('')
const filterDateTo  = ref('')
const hasFilter = computed(() => search.value||filterStatus.value||filterProgram.value||filterCategory.value||filterAiScore.value||filterDateFrom.value||filterDateTo.value)
function clearFilters() { search.value=''; filterStatus.value=''; filterProgram.value=''; filterCategory.value=''; filterAiScore.value=''; filterDateFrom.value=''; filterDateTo.value='' }

// ─── Pagination ───────────────────────────────────────
const currentPage = ref(1)
const pageSize = 10
const totalPages = computed(() => Math.max(1, Math.ceil(filteredApps.value.length / pageSize)))
const pageNumbers = computed(() => {
  const p = []; const t = totalPages.value; const c = currentPage.value
  for (let i=Math.max(1,c-2); i<=Math.min(t,c+2); i++) p.push(i)
  return p
})
const paginatedApps = computed(() => {
  const s = (currentPage.value-1)*pageSize
  return filteredApps.value.slice(s, s+pageSize)
})

// ─── Select options (mock) ───────────────────────────
const programs   = ref([{ id:1, ten_chuong_trinh:'Quỹ BTXH Tỉnh 2026' }, { id:2, ten_chuong_trinh:'Cứu Trợ Khẩn Cấp' }])
const categories = ref([{ id:1, ten_doi_tuong:'Người cao tuổi' }, { id:2, ten_doi_tuong:'Trẻ em nghèo' }, { id:3, ten_doi_tuong:'Người khuyết tật' }])

const statusOptions = [
  { val:'PENDING',   label:'Chờ duyệt' },
  { val:'REVIEWING', label:'Đang xét' },
  { val:'APPROVED',  label:'Phê duyệt' },
  { val:'REJECTED',  label:'Từ chối' },
]

// ─── Filtered & computed apps ─────────────────────────
const filteredApps = computed(() => {
  let list = [...apps.value]
  const q = search.value.toLowerCase()
  if (q) list = list.filter(a => `HS-${a.id} ${a.ten_nguoi_nop}`.toLowerCase().includes(q))
  if (filterStatus.value)   list = list.filter(a => a.trang_thai === filterStatus.value)
  if (filterProgram.value)  list = list.filter(a => a.chuong_trinh?.id === filterProgram.value)
  if (filterCategory.value) list = list.filter(a => a.doi_tuong?.id === filterCategory.value)
  if (filterAiScore.value === 'high') list = list.filter(a => (a.diem_uu_tien||0) >= 80)
  else if (filterAiScore.value === 'med') list = list.filter(a => { const d=a.diem_uu_tien||0; return d>=50&&d<80 })
  else if (filterAiScore.value === 'low') list = list.filter(a => (a.diem_uu_tien||0) < 50)
  if (filterDateFrom.value) list = list.filter(a => a.ngay_nop_ho_so >= filterDateFrom.value)
  if (filterDateTo.value)   list = list.filter(a => a.ngay_nop_ho_so <= filterDateTo.value)
  // ★ Ưu tiên PENDING (chưa duyệt) lên đầu, trong nhóm PENDING sắp theo điểm AI giảm dần
  list.sort((a, b) => {
    const aP = a.trang_thai === 'PENDING' ? 0 : 1
    const bP = b.trang_thai === 'PENDING' ? 0 : 1
    if (aP !== bP) return aP - bP
    // Cùng nhóm: sắp theo điểm AI giảm dần
    return (b.diem_uu_tien || 0) - (a.diem_uu_tien || 0)
  })
  currentPage.value = 1
  return list
})

// Summary stats (clickable filter)
const summaryStats = computed(() => [
  { label:'Tổng',       value: apps.value.length,                                filter:'',         valueColor:'text-slate-800' },
  { label:'Chờ duyệt', value: apps.value.filter(a=>a.trang_thai==='PENDING').length,   filter:'PENDING',  valueColor:'text-amber-600' },
  { label:'Đang xét',  value: apps.value.filter(a=>a.trang_thai==='REVIEWING').length, filter:'REVIEWING',valueColor:'text-blue-600' },
  { label:'Phê duyệt', value: apps.value.filter(a=>a.trang_thai==='APPROVED').length,  filter:'APPROVED', valueColor:'text-emerald-600' },
  { label:'Từ chối',   value: apps.value.filter(a=>a.trang_thai==='REJECTED').length,  filter:'REJECTED', valueColor:'text-red-500' },
])

const viewerStats = computed(() => [
  { label:'Tổng đơn',   value: myApps.value.length,                                             cls:'bg-gradient-to-br from-primary to-blue-600 text-white border-primary/20' },
  { label:'Đã duyệt',  value: myApps.value.filter(a=>a.trang_thai==='APPROVED').length,  cls:'bg-emerald-50 text-emerald-800 border-emerald-200' },
  { label:'Đang chờ',  value: myApps.value.filter(a=>!['APPROVED','REJECTED'].includes(a.trang_thai)).length, cls:'bg-amber-50 text-amber-800 border-amber-200' },
])

// ─── Quick View / Menus ──────────────────────────────
const quickViewApp = ref(null)
const openMenuId   = ref(null)
const menuStyle    = ref({})

function openQuickView(app) { quickViewApp.value = app; openMenuId.value = null }
function openDetail(app)    { router.push(`/ho-so/${app.id}`) }

function toggleMenu(id) {
  if (openMenuId.value === id) { openMenuId.value = null; return }
  openMenuId.value = id
  // position below the trigger button — simple approach
  setTimeout(() => {
    const btn = document.querySelector(`[data-menu="${id}"]`)
    const rect = btn?.getBoundingClientRect()
    menuStyle.value = rect ? { top: rect.bottom + 4 + 'px', right: (window.innerWidth - rect.right) + 'px' } : { top: '50%', right: '20px' }
  }, 0)
}

function closeMenu(e) {
  if (!e.target.closest('[data-menu]') && openMenuId.value) openMenuId.value = null
}
onMounted(() => document.addEventListener('click', closeMenu))
onUnmounted(() => document.removeEventListener('click', closeMenu))

// ─── Actions ─────────────────────────────────────────
const canReview = computed(() => authStore.isAdmin || authStore.isReviewer)
const showCreateModal = ref(false)

const rejectModal = ref({ show: false, app: null, reason: '' })
function openRejectModal(app) { rejectModal.value = { show: true, app, reason: '' }; openMenuId.value = null }
function confirmReject() {
  if (rejectModal.value.app) {
    rejectModal.value.app.trang_thai = 'REJECTED'
    rejectModal.value.app.ly_do_tu_choi = rejectModal.value.reason
    ui.showSuccess('Đã từ chối hồ sơ!')
  }
  rejectModal.value.show = false
}

function approveApp(app) {
  app.trang_thai = 'APPROVED'
  ui.showSuccess(`Đã phê duyệt hồ sơ ${formatMaHoSo(app.id)}!`)
  openMenuId.value = null
}
function viewDocs(app) { openDetail(app); openMenuId.value = null }
function viewAI(app)   { openDetail(app); openMenuId.value = null }
function createPayment(app) { router.push('/ngan-sach'); openMenuId.value = null }

async function exportCsv() {
  try {
    const { exportApi } = await import('../api/export')
    await exportApi.download('applications')
    const ui = useUI()
    ui.showSuccess('Đã xuất báo cáo hồ sơ thành công!')
  } catch (e) {
    alert('Xuất dữ liệu thất bại: ' + e.message)
  }
}

// ─── Styles / Helpers ─────────────────────────────────
const avatarBgs = ['bg-blue-100 text-blue-700','bg-emerald-100 text-emerald-700','bg-amber-100 text-amber-700','bg-purple-100 text-purple-700','bg-red-100 text-red-700','bg-teal-100 text-teal-700']

function statusStyle(s) {
  return {
    PENDING:   { badge:'bg-amber-100 text-amber-700',   bg:'bg-amber-100',   icon:'text-amber-600',   sym:'hourglass_top', gradient:'bg-gradient-to-r from-amber-400 to-amber-500',   heroBg:'bg-amber-50' },
    REVIEWING: { badge:'bg-blue-100 text-blue-700',     bg:'bg-blue-100',    icon:'text-blue-600',    sym:'manage_search', gradient:'bg-gradient-to-r from-blue-400 to-blue-500',     heroBg:'bg-blue-50' },
    APPROVED:  { badge:'bg-emerald-100 text-emerald-700',bg:'bg-emerald-100',icon:'text-emerald-600', sym:'check_circle',  gradient:'bg-gradient-to-r from-emerald-400 to-emerald-500',heroBg:'bg-emerald-50' },
    REJECTED:  { badge:'bg-red-100 text-red-700',       bg:'bg-red-100',     icon:'text-red-600',     sym:'cancel',        gradient:'bg-gradient-to-r from-red-400 to-red-500',       heroBg:'bg-red-50' },
  }[s] || { badge:'bg-slate-100 text-slate-600', bg:'bg-slate-100', icon:'text-slate-500', sym:'help', gradient:'bg-slate-300', heroBg:'bg-slate-50' }
}
function statusLabel(s) { return { PENDING:'Chờ duyệt', REVIEWING:'Đang xét', APPROVED:'Phê duyệt', REJECTED:'Từ chối' }[s] || s }
function payStatusStyle(s) { return { PENDING:'bg-slate-100 text-slate-500', PROCESSING:'bg-amber-100 text-amber-700', COMPLETED:'bg-emerald-100 text-emerald-700', FAILED:'bg-red-100 text-red-600' }[s] || 'bg-slate-100 text-slate-400' }
function payStatusLabel(s) { return { PENDING:'Chưa chi', PROCESSING:'Đang xử lý', COMPLETED:'Đã chi', FAILED:'Thất bại' }[s] || '—' }
function formatDate(d) { if (!d) return '—'; return new Date(d).toLocaleDateString('vi-VN') }

// ─── Load data ────────────────────────────────────────
const chTrinhMock  = [{ id:1, ten_chuong_trinh:'Quỹ BTXH Tỉnh 2026' }, { id:2, ten_chuong_trinh:'Cứu Trợ Khẩn Cấp' }, { id:3, ten_chuong_trinh:'Hỗ trợ Mùa Lũ' }]
const doiTuongMock = [{ id:1, ten_doi_tuong:'Người cao tuổi' }, { id:2, ten_doi_tuong:'Trẻ em nghèo' }, { id:3, ten_doi_tuong:'Người khuyết tật' }]
const namesMock    = ['Nguyễn Thị Hoa','Trần Văn Nam','Lê Thị Mai','Phạm Đình Khoa','Võ Minh Đức','Bùi Thị Lan','Đặng Văn Hùng','Hoàng Thị Thu','Phan Văn Tuấn','Lý Thị Bích']
const statuses     = ['PENDING','REVIEWING','APPROVED','REJECTED']
const payStatuses  = ['PENDING','PROCESSING','COMPLETED']

onMounted(async () => {
  try {
    const res = await http.get('/applications', { params: { page: 0, size: 200 } })
    apps.value = (res.data?.content || res.data || []).map((a, i) => ({
      ...a,
      ten_nguoi_nop: a.nguoi_dung?.ten_day_du || a.nguoi_dung?.username || 'N/A',
      initials: (a.nguoi_dung?.ten_day_du || 'ND').split(' ').slice(-2).map(w=>w[0]).join('').toUpperCase(),
      avatarBg: avatarBgs[i % avatarBgs.length],
      diem_uu_tien: a.danh_gia_ai?.diem_uu_tien,
      do_tin_cay: a.danh_gia_ai?.do_tin_cay,
      ai_nhan_xet: a.danh_gia_ai?.nhan_xet,
      trang_thai_chi_tra: a.chi_tra?.trang_thai || 'PENDING',
    }))
  } catch {
    // mock
    apps.value = Array.from({ length: 35 }, (_, i) => {
      const name = namesMock[i % namesMock.length]
      const aiScore = Math.floor(Math.random()*100)
      // Điểm tin cậy liên hệ với điểm AI: dao động ±8% quanh diem_uu_tien, clamp 50-99
      const confidence = Math.min(99, Math.max(50, aiScore + Math.floor((Math.random()-0.5)*16)))
      return {
        id: i+1, ten_nguoi_nop: name,
        initials: name.split(' ').slice(-2).map(w=>w[0]).join('').toUpperCase(),
        avatarBg: avatarBgs[i % avatarBgs.length],
        trang_thai: statuses[i % statuses.length],
        chuong_trinh: chTrinhMock[i % chTrinhMock.length],
        doi_tuong: doiTuongMock[i % doiTuongMock.length],
        ngay_nop_ho_so: new Date(Date.now()-Math.random()*86400000*30).toISOString().slice(0,10),
        diem_uu_tien: aiScore,
        do_tin_cay: confidence,
        ai_nhan_xet: i%3===0 ? 'Hồ sơ đầy đủ, tình trạng phù hợp với tiêu chí xét duyệt.' : null,
        trang_thai_chi_tra: payStatuses[i % payStatuses.length],
        documents: i%2===0 ? [{ id:1, ten_tai_lieu:'CMND/CCCD', loai_tai_lieu:'identity', trang_thai_ocr:'DONE' }, { id:2, ten_tai_lieu:'Hộ khẩu', loai_tai_lieu:'household', trang_thai_ocr:'PENDING' }] : [],
      }
    })
    if (authStore.isViewer) myApps.value = apps.value.slice(0, 3)
  }
  loading.value = false
})
</script>
