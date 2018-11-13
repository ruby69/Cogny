import cogny from '../../api/vehiclesApi'

const state = {
  vehiclePage: {
    hasNext: false,
    hasPrevious: false,
    page: 1,
    scale: 15,
    total: 0,
    totalPages: 0,
    query: null,
    vehicleStatus: null, // vehicle free or busy
    partnerNo: null,
    contents: []
  },

  fuels: null,
  manufacturers: null,
  modelGroups: null,
  models: null,
  modelSalesYear: null
}

const getters = {
  getVehiclesPage: state => state.vehiclePage,
  getFuels: state => state.fuels,
  getManufacturers: state => state.manufacturers,
  getModelGroups: state => state.modelGroups,
  getModels: state => state.models,
  getModelSalesYear: state => state.modelSalesYear
}

const actions = {
  loadVehiclesPage({ commit }, params) {
    cogny.fetchVehicles(page => 
      commit('setPage', page), {
        params: params
      })
  },
  loadFuels({ commit }) {
    cogny.fetchFuels(fuels => commit('setFuels', fuels))
  },
  loadManufacturers({ commit }) {
    cogny.fetchManufacturers(manufacturers =>
      commit('setManufacturers', manufacturers)
    )
  },
  loadModelGroupsByManufacturer({ commit }, options) {
    cogny.fetchModelGroupByManufacturer( modelGroups => {
      commit('setModelGroups', modelGroups)
      if (options.callback) options.callback()
    }, options.manufacturerNo)
  },
  loadModelsByModelGroup({ commit }, options) {
    cogny.fetchModelsByModelGorup(models => {
      commit('setModels', models)
      if (options.callback) options.callback()
    }, options.modelGroupNo)
  },
  loadModelSalesYear({ commit }, options) {
    cogny.fetchModelSalesYear(model => {
      commit('setModelSalesYear', model)
      if (options.callback) options.callback()
    }, options.modelNo)
  },
  saveVehicle({ commit }, options) {
    cogny.postVehicle(vehicle => {
      commit('setVehicle', vehicle)
      if (options.callback) options.callback(vehicle)
    }, options.data)
  },
  deleteVehicle({ commit }, vehicleNo) {
    cogny.deleteVehicle(response => {
      if(response.status == 1000){
        commit('reloadVehicleList')
      } else {
        console.error('Error on deleting vehicle')
      }
    }, vehicleNo)
  },
}

const mutations = {
  setPage(state, page) {
    state.vehiclePage = page
  },
  setFuels(state, fuels) {
    state.fuels = fuels
  },
  setManufacturers(state, manufacturers) {
    state.manufacturers = manufacturers
  },
  setModelGroups(state, modelGroups) {
    state.modelGroups = modelGroups
  },
  setModels(state, models) {
    state.models = models
  },
  setModelSalesYear(state, model) {
    if(!model || !model.beginYear) return
    let modelSalesYear = []
    let beginYear = model.beginYear
    let endYear = model.endYear ? model.endYear : (new Date()).getFullYear()
    for (let i = beginYear; i <= endYear; i++) {
      modelSalesYear.push({value: i, text: i + "년"})
    }
    state.modelSalesYear = modelSalesYear
  },
  setVehicle(state, vehicle) {
    let contents = state.vehiclePage.contents
    let item = contents.find(e => {
      if (e.vehicleNo === vehicle.vehicleNo) {
        return e
      }
    })
    let index = contents.indexOf(item)
    if (index > -1) {
      contents.splice(index, 1, vehicle)
    } else {
      this.dispatch('loadVehiclesPage')
    }
  },
  reloadVehicleList() {
    this.dispatch('loadVehiclesPage')
  },
}

export default {
  state,
  getters,
  actions,
  mutations
}