import axios from 'axios'
import qs from 'qs'

export default {
  fetchVehicles (callback, config) {
    axios.get('/api/vehicles?' + qs.stringify(config.params))
    .then(response => callback(response.data))
    .catch(error => console.error(error))
  },
  postVehicle (callback, data) {
    axios.post('/api/vehicles', data)
    .then(response => callback(response.data))
    .catch(error => console.error(error))
  },
  deleteVehicle (callback, vehicleNo) {
    axios.delete('/api/vehicles/' + vehicleNo)
    .then(response => callback(response.data))
    .catch(error => console.error(error))
  },
  fetchFuels (callback) {
    axios.get('/api/fuels')
    .then(response => callback(response.data))
    .catch(error => console.error(error))
  },
  fetchManufacturers (callback) {
    axios.get('/api/manufacturers')
    .then(response => callback(response.data))
    .catch(error => console.error(error))
  },
  fetchModelGroupByManufacturer (callback, manufacturerNo) {
    axios.get('/api/modelgroups/' + manufacturerNo)
    .then(response => callback(response.data))
    .catch(error => console.error(error))
  },
  fetchModelsByModelGorup (callback, modelGroupNo) {
    axios.get('/api/models/' + modelGroupNo)
    .then(response => callback(response.data))
    .catch(error => console.error(error))
  },
  fetchModelSalesYear (callback, modelNo) {
    axios.get('/api/model/salesyear/' + modelNo)
    .then(response => callback(response.data))
    .catch(error => console.error(error))
  }
}