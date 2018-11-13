import axios from 'axios'

let sessionTimeOut = false

axios.interceptors.request.use(function (config) {
  if(config.url != '/api/auth/profile') {
    axios.interceptors.response.use(function (response) {
      if(response.data.status == 9002 || response.data.status == 9003) {
        sessionStorage.clear()
        location.href = "/"
      }  
      return response
    }, function (error) {
      if(!sessionTimeOut) {
        sessionTimeOut = true
        alert("로그아웃 되었습니다.")
      }
      sessionStorage.clear()
      location.href = "/"
      return Promise.reject(error);
    })
  }
  return config
}, function (error) {
  return Promise.reject(error)
})

export default {
  postAuthToken (callback, data) {
    axios.post('/api/auth/token/singin',{}, {
      headers: {'X-Firebase-Auth': data.token}
    })
    .then(response => {
      if(response.data.status == 1000) {
        sessionStorage.email = response.data.currentUser.email
        sessionStorage.userNo = response.data.currentUser.userNo
        sessionStorage.role = response.data.currentUser.role
      }
      callback(response.data)}
    )
    .catch(error => {
      console.error(error)
      alert("회원 가입 후 접속할 수 있습니다. 관리자에게 문의하세요.")
    })
  },
  postSignOut (callback) {
    axios.post('/api/auth/signout')
    .then(response => {
      sessionStorage.clear()
      callback(response)}
    )
    .catch(error => {
      console.error(error)
    })
  },
  getProfile (callback) {
    let config = {
      headers: {
        'Cache-Control' : 'no-cache'}
      }
    axios.get('/api/auth/profile', config)
    .then(response => callback(response.data))
    .catch(error => console.error(error))
  },  
}