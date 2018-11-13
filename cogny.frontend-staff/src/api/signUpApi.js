import axios from 'axios'

export default {
  fetchUserInvitation (callback, invitationCode) {
    axios.get('/api/auth/invitation/' + invitationCode)
    .then(response => callback(response.data))
    .catch(error => console.error(error))
  },
  postAuthTokenSignUp (callback, data) {
    axios.post('/api/auth/token/singup/' + data.invitationCode, {}, {
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
    })
  },  
}