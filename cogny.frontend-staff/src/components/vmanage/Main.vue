<template>
  <div class="be-wrapper be-color-header">
  <!-- <div id="app" class="be-wrapper be-color-header"> -->
    <Navbar />

    <SidebarLeft :active="'vmanage'" />

    <div class="be-content">
      <div class="page-head">

        <!-- <component
          :is="currentView"
        /> -->
        <!-- <h2 class="page-head-title">Blank Page</h2>
        <nav aria-label="breadcrumb" role="navigation">
          <ol class="breadcrumb page-head-nav">
            <li class="breadcrumb-item"><a href="#">Home</a></li>
            <li class="breadcrumb-item"><a href="#">Pages</a></li>
            <li class="breadcrumb-item active">Blank Page Header</li>
          </ol>
        </nav> -->
      </div>
      <div class="main-content container-fluid">
        <div class="jumbotron"><h1>차량 운영관리</h1></div>
      </div>
    </div>

    <!-- <SidebarRight /> -->

  </div>
</template>

<script>
import Navbar from '../common/Navbar.vue'
// import SidebarRight from '../common/SidebarRight.vue'
import SidebarLeft from '../common/SidebarLeft.vue'


import { eventBus } from '../common/EventBus.vue'


export default {
  name: 'VmanageMain',


  components: {
    Navbar,
    // SidebarRight,
    SidebarLeft,

  },

}
</script>