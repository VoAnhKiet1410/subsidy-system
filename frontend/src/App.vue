<template>
  <!-- Trang login: full screen -->
  <router-view v-if="route.meta.public" />

  <!-- Cổng người dùng /portal: PortalLayout tự xử lý -->
  <router-view v-else-if="isPortal" />

  <!-- Admin panel: dùng AppLayout đầy đủ -->
  <AppLayout v-else />

  <!-- Global UI -->
  <ToastContainer />
  <ConfirmModal />
</template>

<script setup>
import { computed } from 'vue'
import { useRoute } from 'vue-router'
import AppLayout from './components/AppLayout.vue'
import ToastContainer from './components/ToastContainer.vue'
import ConfirmModal from './components/ConfirmModal.vue'

const route = useRoute()
const isPortal = computed(() => route.path.startsWith('/portal'))
</script>

