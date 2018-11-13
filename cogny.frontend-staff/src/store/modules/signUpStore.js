import cogny from '../../api/signUpApi'

const state = {
  userInvitation: null
}

const getters ={
  getUserInvitation: state => state.userInvitation
}

const actions = {
  loadUserInvitation({ commit }, options) {
    cogny.fetchUserInvitation(userInvitation => {
      commit('setUserInvitation', userInvitation)
      if(options.callback) options.callback(userInvitation)
    }, options.invitationCode)},
  saveNewUser({ commit }, options) {
    cogny.postAuthTokenSignUp(response => {
      commit('setNewUser')
      if(options.callback) options.callback(response)
    }, options.data)
  }
}

const mutations = {
  setUserInvitation(state, userInvitation) {
    state.userInvitation = userInvitation
  },
  setNewUser(){}
}

export default {
  state,
  getters,
  actions,
  mutations
}