import axios from 'axios'
import qs from 'qs'

export default {
  // 정비 이력 조회 탭의 axios
  fetchRepairList(callback, config) {
    axios.get('/api/repairs?' + qs.stringify(config.params))
    .then(response => callback(response.data))
    .catch(error => console.error(error))
  },
  fetchComponentCate1(callback) {
    axios.get('/api/components/cate1')
    .then(response => callback(response.data))
    .catch(error => console.error(error))
  },
  fetchComponentCate2(callback, config) {
    axios.get('/api/components/cate2', config)
    .then(response => callback(response.data))
    .catch(error => console.error(error))
  },
  fetchComponentList(callback, config) {
    axios.get('/api/components', config)
    .then(response => callback(response.data))
    .catch(error => console.error(error))
  },
  fetchRepairEnums(callback) {
    axios.get('/api/repairs/enums')
    .then(response => callback(response.data))
    .catch(error => console.error(error))
  },
  postRepair(callback, data) {
    axios.post('/api/repairs', data)
    .then(response => callback(response.data))
    .catch(error => console.error(error))
  },
  deleteRepair (callback, repairNo) {
    axios.delete('/api/repairs/' + repairNo)
    .then(response => callback(response.data))
    .catch(error => console.error(error))
  }  
}