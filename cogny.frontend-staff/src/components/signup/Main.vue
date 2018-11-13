<template>
  <div id="app" class="h-100">
    <div class="be-wrapper be-login login-bg">
      <div class="be-content mx-0">
        <div class="main-content container-fluid">
          <div class="splash-container">
            <div class="card pt-2 width-600">
              <div class="card-header pt-8">
                <img src="../../assets/img/logo-xx.png" alt="logo" width="202" height="24" class="logo-img" />
                <div class="page-title font-weight-bold "> 
                  <span> REPORT </span>
                </div>
                <div class="page-title mt-2"> 
                  <span> 회원가입 </span>
                </div>
              </div>
              <div class="card-body pt-8 pb-8 signup-body">
                <div class="text-center" v-show="currentSignUpStep === 'FIRST'">
                  <div>
                    반갑습니다. <span class="text-primary">{{ userInvitation.name }} </span>님, <br/>
                    <span class="text-primary">{{ userInvitation.partner.companyName }}</span> {{ userInvitation.roleName }}이신가요?
                  </div>
                  <div class="mt-4">
                    <button type="button" class="btn btn-space btn-secondary" @click="onCancel()">아니오</button>
                    <button type="button" class="btn btn-space btn-primary" @click="onInvitationOk()"> 예, 맞습니다</button>
                  </div>                  
                </div>
              </div>
            </div>
            <div class="bottom-copy">Copyright © Dymatics Inc. All Rights Reserved.</div>

          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'SignUp',

  data: function() {
    return {
      invitationCode: null,
      currentSignUpStep: null,
      userInvitation: {
        name: null,
        partner: { companyName: null},
        roleName: null
      },
      hasLoaded: false
    }
  },
  methods: {
    setUserInvitation: function() {
    let params = window.location.search
    let regex = new RegExp("[\\?&]" + "sn" + "=([^&#]*)")
    let results = regex.exec(params)
    this.invitationCode = results == null ? "" : decodeURIComponent(results[1].replace(/\+/g, " "))
    
    let that = this
    this.$store.dispatch('loadUserInvitation', {
      invitationCode: this.invitationCode,
      callback: userInvitation => {
        if(userInvitation == null || userInvitation == '') {
          alert('가입코드가 잘못되었습니다.')
          location.href = '/'
        }
        that.userInvitation = userInvitation
        that.currentSignUpStep = 'FIRST'
      }
    })
    },
    onInvitationOk: function() {
      location.href = '/signup/auth?sn=' + this.invitationCode
    },
  },

  created() {
    console.log('created signup/Main')
    this.setUserInvitation()
  },
  mounted() {
  }
}
</script>
<style>
.login-bg {
    background: linear-gradient(rgba(53, 63,75, 0.92), rgba(53, 63,75, 0.7)), url(../../assets/img/main_ban_1500.jpg) center;
    background-size: cover;
}
.bottom-copy {
    color: #fff;
    font-size: 12px;
    text-align: center;
    margin: 0 auto;
}
.signup-body{
  min-height: 222px;
}
.width-600 {
  width: 600px;
}
</style>
