import axios from 'axios'

export default {
  // 차량 센서데이터 조회 탭의 axios
  fetchDriveHistoryStartDates (callback, vehicleNo){
    axios.get('/api/drivehistory/startdates/' + vehicleNo)
    .then(response => callback(response.data))
    .catch(error => console.error(error))
  },
  fetchDriveHistoryIndexes (callback, config) {
    axios.get('/api/drivehistory/indexes', config)
    .then(response => callback(response.data))
    .catch(error => console.error(error))
  },
  fetchDriveHistory (callback, config) {
    axios.get('/api/drivehistory/detail', config)
    .then(response => callback(response.data))
    .catch(error => console.error(error))
  },
  fetchSensingRaw(callback, params) {
    axios.post('/api/sensordata/raw', params)
    .then(response => callback(response.data))
    .catch(error => console.error(error))
  },
  postDriveHistoryMemo(callback, data) {
    axios.post('/api/drivehistory/memo', data)
    .then(response => callback(response.data))
    .catch(error => console.error(error))
  }
}