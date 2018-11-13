import cogny from '../../api/authApi'

const state = {
  currentUser: {
    userNo: null,
    name: null,
    partnerNo: null,
    email: null,
    tel: null,
    hpNo: null,
    role: null,
    regDate: null,
  }
}
const getters = {
  getCurrentUser: state => state.currentUser

}
const actions = {
  authToken({ commit }, options) {
    cogny.postAuthToken(responseData => {
      commit('setAuthToken', responseData.currentUser)
      if(options.callback) options.callback(responseData)
    }, options.data)
  },
  signOut({ commit }, options) {
    cogny.postSignOut(responseData => {
      commit('setSignOut')
      if(options.callback) options.callback(responseData)
    })
  },
  fetchProfile({ commit }, options) {
    cogny.getProfile(currentUser => {
      commit('setProfile', currentUser)
      if(options.callback) options.callback(currentUser)
    })
  }
}
const mutations = {
  setProfile(state, currentUser) {
    state.currentUser = currentUser
  },
  setAuthToken(state, currentUser) {
    state.currentUser = currentUser
  },
  setSignOut(state) {
    state.currentUser = {}
  }
}

export default {
  state,
  getters,
  actions,
  mutations
}