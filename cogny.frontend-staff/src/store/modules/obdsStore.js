import cogny from '../../api/obdsApi'

const state = {
  obdPage: {
    hasNext: false,
    hasPrevious: false,
    page: 1,
    scale: 15,
    total: 0,
    totalPages: 0,
    query: null,
    contents:[]
  },
  obdIsDup: {
    obdDeviceNo: 0,
    obdSerial: null,
    status: null,
    statusName: null,
    enabled: true
  }
}

const getters = {
  getObdsPage: state => state.obdPage,
  getObdVehicles: state => state.vehiclePage,
}
const actions = {
  loadObdsPage({ commit }, params) {
    cogny.fetchObds(page => commit('setObdsPage', page), {
      params: params
    })
  },
  loadObdEnums({ commit }, options) {
    cogny.fetchObdEnums(enums => {
      commit('setObdEnums', enums)
      if (options.callback) options.callback(enums)
    })
  },
  saveLinkVehicle({ commit }, options){
    cogny.postLinkVehicle(obd => {
      commit('setObd', obd)
      if (options.callback) options.callback(obd)
    }, options.data)
  },
  checkObdIsDup ({ commit }, options){
    cogny.postObdIsDup(obdIsDup => {
      commit('setObdIsDup')
      if (options.callback) options.callback(obdIsDup)
    }, options.data)
  },
  saveObdList({ commit }, options) {
    cogny.postObdList(obds => {
      commit('setObdList')
      if (options.callback) options.callback(obds)
    }, options.data)
  },
  deleteObd({ commit }, obdDeviceNo) {
    cogny.deleteObd( response => {
      if(response.status == 1000){
        commit('reloadObdList')
      } else {
        console.error('Error on deleting obd')
      }
    }, obdDeviceNo)
  },
}

const mutations = {
  setObdsPage(state, page) {
    state.obdPage = page
  },
  setObdEnums(state, enums) {
    state.enums = enums
  },
  setObd(state, obd) {
    let contents = state.obdPage.contents
    let item = contents.find(e => {
      if (e.obdNo === obd.obdNo) {
        return e
      }
    })
    let index = contents.indexOf(item)
    if (index > -1) {
      contents.splice(index, 1, obd)
    } else {
      this.dispatch('loadObdsPage')
    }
  },
  setObdIsDup() {},
  setObdList() {},
  reloadObdList() {
    this.dispatch('loadObdsPage')
  },
}

export default {
  state,
  getters,
  actions,
  mutations
}