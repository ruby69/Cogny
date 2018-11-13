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
                <div class="text-center" v-show="currentSignUpStep === 'SECOND'">
                  <div>
                    <span class="text-primary">{{ userInvitation.name }} </span>님, <br/>
                    코그니 리포트 회원으로 가입해주세요.
                  </div>
                  <div id="firebaseui-auth-container" class="pb-8 mt-4"></div>
                  <div id="loader" class="text-center align-items-center justify-content-center" v-if="hasLoaded">
                    <img src="../../assets/img/spinner-1s-110px.gif" width="110" height="110"/>
                  </div>
                </div>
                <div class="text-center" v-show="currentSignUpStep === 'THIRD'">
                  <div>
                    <span class="text-primary">{{ userInvitation.name }} </span>님, <br/>
                    <span class="text-primary">{{ userInvitation.partner.companyName }}</span> 회원가입을 감사드립니다.
                  </div>
                  <button class="btn btn-rounded btn-space btn-primary mt-5" @click="startCogny()">
                    <i class="icon mdi material-icons">verified_user</i> 코그니 사용하기
                  </button>
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
          that.currentSignUpStep = 'SECOND'
        }
      })
    },
    signIn: function() {
      let that = this
      firebase.auth().currentUser.getIdToken(true).then(function(idToken) {
        that.$store.dispatch('authToken', {
          data: {
            token: idToken
          },
          callback: data => {
            if(data.status == 1000 ) {
              that.currentSignUpStep = 'THIRD'
            } else {
              alert('시스템에 오류가 있습니다.')
              sessionStorage.clear()
              location.reload()
              return false
            }
          }
        })
      }).catch(function(error){
        alert('유효하지 않은 구글 계정입니다.')
        return false
      })      
    },

    startCogny: function() {
      location.href = '/'
    }
  },

  created() {
    console.log('created signup/auth')
    this.setUserInvitation()
  },
  mounted() {
    let that = this
    let uiConfig = {
      callbacks: {
        signInSuccessWithAuthResult: function(authResult, redirectUrl) {
          that.currentSignUpStep = 'SECOND'
          let user = authResult.user;
          let credential = authResult.credential;
          let isNewUser = authResult.additionalUserInfo.isNewUser;
          let providerId = authResult.additionalUserInfo.providerId;
          let operationType = authResult.operationType;

          firebase.auth().currentUser.getIdToken(true).then(function(idToken) {
            that.$store.dispatch('saveNewUser', {
              data: {
                token: idToken,
                invitationCode: that.invitationCode
              },
              callback: data => {
                if(data.status == 1000 ) {
                  that.signIn()
                  return false
                } else if(data.status == 2002) {
                  alert('이미 가입된 사용자 입니다.')
                  sessionStorage.clear()
                  location.reload()
                  return false
                }
              }
            })
          }).catch(function(error){
            alert('유효하지 않은 구글 계정입니다.')
            return false
          })
        },
        signInFailure: function(error) {
          alert("회원가입에 실패하였습니다.")
          return handleUIError(error);
        },
        uiShown: function() {
          that.hasLoaded = false
        }
      },
      signInSuccessUrl: '/',
      signInOptions: [
          {
            provider: firebase.auth.GoogleAuthProvider.PROVIDER_ID,
            languageCode: 'ko'
          },
        ],
      }
    let ui = new firebaseui.auth.AuthUI(firebase.auth())

    ui.start('#firebaseui-auth-container', uiConfig)
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
