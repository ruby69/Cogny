<template>
  <div class="be-wrapper be-color-header">
    <Navbar />

    <SidebarLeft :active="'dashboard'" />

    <div class="be-content">
      <div class="page-head">
        <h2 class="page-head-title">Blank Page</h2>
        <nav aria-label="breadcrumb" role="navigation">
          <ol class="breadcrumb page-head-nav">
            <li class="breadcrumb-item"><a href="#">Home</a></li>
            <li class="breadcrumb-item"><a href="#">Pages</a></li>
            <li class="breadcrumb-item active">Blank Page Header</li>
          </ol>
        </nav>
      </div>
      <div class="main-content container-fluid">
        <div class="jumbotron"><h1>Dashboard</h1></div>
      </div>
    </div>
  </div>
</template>

<script>
import Navbar from '../common/Navbar.vue'
import SidebarLeft from '../common/SidebarLeft.vue'

export default {
  name: 'Dashboard',

  components: {
    Navbar,
    SidebarLeft
  },

  created() {
  }
}
</script>