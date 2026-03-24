<template>
  <div class="flex min-h-screen">
    <AppSidebar />
    <main class="flex-1 flex flex-col min-w-0 overflow-hidden">
      <AppTopbar />
      <div class="flex-1 overflow-y-auto">
        <router-view v-slot="{ Component, route }">
          <Transition name="page" mode="out-in">
            <component :is="Component" :key="route.path" />
          </Transition>
        </router-view>
      </div>
    </main>
  </div>
</template>

<script setup>
import AppSidebar from './AppSidebar.vue'
import AppTopbar from './AppTopbar.vue'
</script>
