import cogny from '../../api/usersApi'

const state = {
  userPage: {
    hasNext:false,
    hasPrevious:false,
    page: 1,
    scale: 15,
    total: 0,
    totalPages: 0,
    query: null,
    contents: []
  },

  enums: {}
}

const getters = {
  getUserPage: state => state.userPage,
}

const actions = {
  loadUsersPage({ commit }, params) {
    cogny.fetchUsers(page => commit('setUserPage', page),{
      params: params
    })
  },
  loadUserEnums({ commit }, options) {
    cogny.fetchUsersEnums(enums => {
      commit('setUserEnums', enums)
      if (options.callback) options.callback(enums)
    })
  },
  checkEmailDup({ commit }, options){
    cogny.checkEmailDup(data => {
      commit('setIsEmailDup')
      if (options.callback) options.callback(data)
    }, {params: options.params})
  },
  saveUser({ commit }, options){
    cogny.postUser(user => {
      commit('setUser', user)
      if (options.callback) options.callback(user)
    }, options.data)
  },
  deleteUser({ commit }, userNo) {
    cogny.deleteUser( response => {
      if(response.status != 1000){
        console.error('Error on deleting user')
      } else {
        commit('reloadUserList')
      }
    }, userNo)
  },
}

const mutations = {
  setUserPage(state, page) {
    state.userPage = page
  },

  setUserEnums(state, enums) {
    state.enums = enums
  },
  setUser(state, user) {
    let contents = state.userPage.contents
    let item = contents.find(e => {
      if (e.userNo === user.userNo) {
        return e
      }
    })
    let index = contents.indexOf(item)
    if (index > -1) {
      contents.splice(index, 1, user)
    } else {
      this.dispatch('loadUsersPage')
    }
  },
  setIsEmailDup() {
  },
  reloadUserList() {
    this.dispatch('loadUsersPage')
  }
}

export default {
  state,
  getters,
  actions,
  mutations
}