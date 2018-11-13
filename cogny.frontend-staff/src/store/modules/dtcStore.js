import cogny from '../../api/dtcApi'
import cf from '../../utils/customFilters'

const state = {
  dtcPage: {
    hasNext:false,
    hasPrevious:false,
    page: 1,
    scale: 15,
    total: 0,
    totalPages: 0,
    query: null,
    vehicleNo: null,
    contents: []
  }
}

const getters = {
  getDtcPage: state => state.dtcPage
}

const actions = {
  loadDtcPage({ commit }, params) {
    cogny.fetchDtcs(page => {
      commit('setDtcPage', page)
    }, { params: params })
  }
}

const mutations = {
  setDtcPage(state, page) {
    let contents = []
    page.contents.forEach(driveHistory => {
      let newDriveHistory = {}
      newDriveHistory.driveHistoryNo = driveHistory.driveHistoryNo
      newDriveHistory.companyName = driveHistory.vehicle.partner ? driveHistory.vehicle.partner.companyName : "-"
      newDriveHistory.licenseNo = driveHistory.vehicle.licenseNo
      newDriveHistory.modelName = driveHistory.vehicle.model.name
      newDriveHistory.obdSerial = driveHistory.obdDevice.obdSerial
      newDriveHistory.userName = driveHistory.user.name
      newDriveHistory.driveStartDateTime = driveHistory.startDate + " " + driveHistory.startTime
      newDriveHistory.driveEndDateTime = cf.formatDateTime(driveHistory.endTime)
      newDriveHistory.driveDistance = driveHistory.driveDistance

      contents.push(newDriveHistory)

      driveHistory.dtcRawList.forEach(function(dtcRaw) {
        newDriveHistory = {}
        newDriveHistory.dtcRaw = dtcRaw
        contents.push(newDriveHistory)
      })
    })
    page.contents = contents
    state.dtcPage = page
  }
}

export default {
  state,
  getters,
  actions,
  mutations
}