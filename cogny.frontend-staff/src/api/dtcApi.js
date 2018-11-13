import axios from 'axios'
import qs from 'qs'

export default {
  fetchDtcs(callback, config) {
    axios.get('/api/drivehistory/dtcs?' + qs.stringify(config.params))
    .then(response => callback(response.data))
    .catch(error => console.error(error))
  }
}