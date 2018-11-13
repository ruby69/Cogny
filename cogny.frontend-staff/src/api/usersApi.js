import axios from 'axios'
import qs from 'qs'

export default {
  fetchUsers (callback, config){
    axios.get('/api/users?' + qs.stringify(config.params))
    .then(response => callback(response.data))
    .catch(error => console.error(error))
  },   
  fetchUsersEnums (callback){
    axios.get('/api/users/enums')
    .then(response => callback(response.data))
    .catch(error => console.error(error))
  },
  checkEmailDup (callback, config){
    axios.get('/api/users/isdupemail', config)
    .then(response => callback(response.data))
    .catch(error => colsole.error(error))
  },
  postUser (callback, data) {
    axios.post('/api/users', data)
    .then(response => callback(response.data))
    .catch(error => console.error(error))
  },

  deleteUser (callback, userNo) {
    axios.delete('/api/users/' + userNo)
    .then(response => callback(response.data))
    .catch(error => console.error(error))
  }
}