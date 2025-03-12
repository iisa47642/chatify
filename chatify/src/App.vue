<script>
import { RouterView } from 'vue-router';
import HeaderView from "../src/views/HeaderView.vue";
export default {
  name: 'App',
  components: {
    HeaderView,
  }
    
}
</script>
<template>
  <div class="wrapper">
    <RouterView/>
  </div>
</template>

<style>
.wrapper {
  background: url(../src/assets/pattern5.webp) 0 0 repeat;
  flex-direction: column; 
  font-family: "Noto Sans", sans-serif; 
  max-height: 100vh;
}
</style>