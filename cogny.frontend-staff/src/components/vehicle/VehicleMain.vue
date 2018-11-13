<template>
  <div class="be-wrapper be-color-header">
    <Navbar :pageTitle="pageTitle"/>
    <SidebarLeft :active="'vehicle'" v-show="isVisible"/>
    <div class="be-content" :class="{'ml-0': !isVisible}">
      <div class="main-content container-fluid">
        <router-view/>
      </div>
    </div>

  </div>
</template>

<script>
import Navbar from '../common/Navbar.vue'
import SidebarLeft from '../common/SidebarLeft.vue'

import { eventBus } from '../common/EventBus.vue'

export default {
  name: 'VehicleMain',

  data: function(){
    return {
      isVisible: true,
      pageTitle: '차량 관리',
    }
  },

  components: {
    Navbar,
    SidebarLeft,
  },

  created() {
    console.log('created vehicle/VehicleMain')
    let that = this

    eventBus.$on('onMaximizeGraph', function(){
      that.isVisible = false
    })

    eventBus.$on('onRestoreGraph', function(){
      that.isVisible = true
    })
  }
}
</script>