<template>
  <div ref="appWrapper" class="be-wrapper be-color-header">
    <Navbar :pageTitle="pageTitle"/>
    <SidebarLeft :active="'user'" />

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
  name: 'UserMain',

  data: function(){
    return {
      pageTitle: '사용자 관리',
    }
  },

  components: {
    Navbar,
    SidebarLeft,
  },

  created() {
    console.log('created user/userMain')
  }
}
</script>