<template>
  <nav class="navbar navbar-expand fixed-top be-top-header">
    <div class="container-fluid">
      <div class="be-navbar-header"><router-link to="/" class="navbar-brand"></router-link></div>

      <div class="be-right-navbar">
        <div class="page-title font-weight-bold"><span>{{ pageTitle }}</span></div>

        <ul class="nav navbar-nav float-right be-user-nav">
          <b-nav-item-dropdown no-caret right>
            <template slot="button-content">
              <img src="./../../assets/img/avatar.png" alt="Avatar">
              <span class="user-name">{{ currentUser.name }}</span>
            </template>

            <div class="user-info">
              <div class="user-name">{{ currentUser.name }}</div>
              <div class="user-position online">{{ currentUser.partner ? currentUser.partner.companyName : "-"  }}</div>
            </div>
            <b-dropdown-item-button @click="onEditProfile()"><span class="icon mdi mdi-face"></span> 사용자 정보 수정</b-dropdown-item-button>
            <b-dropdown-item-button @click="onChangeMyPassword()"><span class="icon mdi mdi-settings"></span> 비밀번호 변경</b-dropdown-item-button>
            <b-dropdown-item-button @click="onLogout()"><span class="icon mdi mdi-power"></span> 로그아웃</b-dropdown-item-button>
          </b-nav-item-dropdown>
        </ul>
      </div>
    </div>
  </nav>
</template>

<script>
import { mapGetters } from 'vuex'

export default {
  name: 'Navbar',

  props : {
    pageTitle: null
  },
  computed: mapGetters({
    currentUser: 'getCurrentUser'
  }),
  methods: {
    onSettings: () => {
      document.body.classList.toggle('open-right-sidebar')
    },
    onEditProfile: function() {
      this.$router.push({ name:'profileDetail', params: { currentUser : this.currentUser } })
    },
    onLogout: function() {
      this.$store.dispatch('signOut', {
        callback: responseData => {
          this.$router.push({ name:'loginMain' })
        }
      })
    }
  },

  created() {
    console.log('created common/Navbar')
  },
}
</script>
<style>
.font-weight-bold {
  color: #353F4B;
}
</style>
