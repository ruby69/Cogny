<template>
  <div class="h-100">
    <div class="be-wrapper be-login login-bg">
      <div class="be-content mx-0">
        <div class="main-content container-fluid">
          <div class="splash-container">
            
            <div class="card pt-2">
              <div class="card-header pt-8">
                <img src="../../assets/img/logo-xx.png" alt="logo" width="202" height="24" class="logo-img" />
                <span class="splash-description">Safe Driving with Your Car</span>
              </div>              
              <div class="card-body pt-8 pb-8">
                <div id="firebaseui-auth-container" class="pb-8"></div>
              </div>
            </div>
            <div class="bottom-copy">Copyright © Dymatics Inc. All Rights Reserved.</div>
          </div>
        </div>
      </div>
      <div>
        <b-modal ref="loginFail" centered ok-only>
          <p class="text-center font-weight-bold">{{ loginFailMessage }}</p>
        </b-modal>
      </div>
    </div>
  </div>
</template>

<script>

export default {
  name: 'SignIn',
  components: {
  },
  data: function () {
    return {
      loginFailMessage: null,
      hasLoaded: false
    }
  },
  created() {
    console.log('created signin')
  },
  mounted() {
    let that = this
    let uiConfig = {
      callbacks: {
        signInSuccessWithAuthResult: function(authResult, redirectUrl) {
          let user = authResult.user;
          let credential = authResult.credential;
          let isNewUser = authResult.additionalUserInfo.isNewUser;
          let providerId = authResult.additionalUserInfo.providerId;
          let operationType = authResult.operationType;
          firebase.auth().currentUser.getIdToken(true).then(function(idToken) {
            that.$store.dispatch('authToken', {
              data: {
                token: idToken
              },
              callback: data => {
                if(data.status == 1000 ) {
                  that.$router.push({ name: 'vehicleList' })
                  return true
                } else {
                  alert('가입되지 않은 사용자 입니다.')
                  sessionStorage.clear()
                  location.href = "/"                  
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
          alert("로그인에 실패하였습니다.")
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
            languageCode: 'ko',
            customParameters: {
              // Forces account selection even when one account
              // is available.
              prompt: 'select_account'
            }
          },
        ],
      }
    let ui = new firebaseui.auth.AuthUI(firebase.auth())
    ui.start('#firebaseui-auth-container', uiConfig)
  },
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
</style>
