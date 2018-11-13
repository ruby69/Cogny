import axios from 'axios'
import qs from 'qs'

export default{
  fetchObds (callback, config) {
     axios.get('/api/obds?' + qs.stringify(config.params))
    .then(response => callback(response.data))
    .catch(error => console.error(error))
  },
  postObdIsDup (callback, data) {
    axios.post('/api/obds/isdup', data)
    .then(response => callback(response.data))
    .catch(error => console.error(error))
  },
  postObdList (callback, data) {
    axios.post('/api/obds/list', data)
    .then(response => callback(response.data))
    .catch(error => console.error(error))
  },
  postLinkVehicle (callback, data) {
    axios.post('/api/linkvehicle', data)
    .then(response => callback(response.data))
    .catch(error => console.error(error))
  },
  deleteObd(callback, obdDeviceNo) {
    axios.delete('/api/obds/' + obdDeviceNo)
    .then(response => callback(response.data))
    .catch(error => console.error(error))
  }
}