<template>
  <div class="h-100">
    <img src="./assets/img/logo-fav.png" v-if="loadFavicon">
    <router-view/>
  </div>
</template>

<script>
export default {
  name: 'App',
  data: function() {
    return {
      loadFavicon: false
    }
  },
  created() {
    console.log('created App')
  }
}
</script>

