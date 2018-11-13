<template>
  <div class="be-wrapper be-color-header">
    <Navbar :pageTitle="pageTitle" />
    <SidebarLeft :active="null" />
    <div class="be-content">
      <div class="main-content container-fluid">
        <router-view/>
      </div>
    </div>    
  </div>
</template>
<script>
import Navbar from '../common/Navbar.vue'
import SidebarLeft from '../common/SidebarLeft.vue'

export default {
  name: 'ProfileMain',
  data: function() {
    return {
      pageTitle: '사용자 정보 수정'
    }
  },
  components: {
    Navbar,
    SidebarLeft
  },
  created() {
    console.log('created ProfileMain')
  }
}
</script>
