<template>
  <div class="be-wrapper be-color-header">
    <Navbar :pageTitle="pageTitle"/>
    <SidebarLeft :active="'obd'" />

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
  name: 'ObdMain',

  data: function(){
    return {
      pageTitle: 'OBD 관리',
    }
  },

  components: {
    Navbar,
    SidebarLeft,
  },

  created() {
    console.log('created obd/obdMain')
  }
}
</script>
<style>
.card-header .font-weight-bold{
  font-weight: normal!important;
}
.card-header>.font-weight-bold{
    margin: 0 1.538rem;
}
</style>
