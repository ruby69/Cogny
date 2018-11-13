<template>
  <div class="be-wrapper be-color-header">
    <Navbar :pageTitle="pageTitle" />
    <SidebarLeft :active="'dtc'" />

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
  name: 'DtcMain',
  data: function() {
    return {
      pageTitle: 'DTC 조회'
    }
  },
  components: {
    Navbar,
    SidebarLeft
  },
  created() {
    console.log('created DtcMain')
  }
}
</script>

