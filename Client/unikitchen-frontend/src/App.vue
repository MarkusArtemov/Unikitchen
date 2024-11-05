<!-- App.vue -->
<template>
  <div id="app">
    <HeaderComponent />
    <router-view />
  </div>
</template>

<script>
import HeaderComponent from '@/components/Header.vue';

export default {
  name: 'App',
  components: {
    HeaderComponent
  }
};
</script>

<style>
</style>
