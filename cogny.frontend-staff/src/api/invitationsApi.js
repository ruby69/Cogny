import axios from 'axios'
import qs from 'qs'

export default {
  fetchInvitations (callback, config) {
    axios.get('/api/invitation?' + qs.stringify(config.params))
   .then(response => callback(response.data))
   .catch(error => console.error(error))
  },
  postInvitationList (callback, data) {
    axios.post('/api/invitation/list', data)
    .then(response => callback(response.data))
    .catch(error => console.error(error))
  },
  deleteInvitation(callback, userInvitationNo) {
    axios.delete('/api/invitation/' + userInvitationNo)
    .then(response => callback(response.data))
    .catch(error => console.error(error))
  }
}