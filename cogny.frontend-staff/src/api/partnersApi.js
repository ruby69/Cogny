import axios from 'axios'
import qs from 'qs'

export default{
  fetchPartners (callback, config) {
    axios.get('/api/partners?' + qs.stringify(config.params))
    .then(response => callback(response.data))
    .catch(error => console.error(error))
  },

  fetchPartnersEnums (callback){
    axios.get('/api/partners/enums')
    .then(response => callback(response.data))
    .catch(error => console.error(error))
  },

  postPartner (callback, data) {
    axios.post('/api/partners', data)
    .then(response => callback(response.data))
    .catch(error => console.error(error))
  },

  deletePartner (callback, partnerNo) {
    axios.delete('/api/partners/' + partnerNo)
    .then(response => callback(response.data))
    .catch(error => console.error(error))
  },
  
}