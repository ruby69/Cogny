import cogny from '../../api/invitationsApi'

const state = {
  invitationsPage: {
    hasNext: false,
    hasPrevious: false,
    page: 1,
    scale: 15,
    total: 0,
    totalPages: 0,
    query: null,
    contents:[]    
  }
}

const getters = {
  getInvitationsPage: state => state.invitationsPage
}

const actions = {
  loadInvitationsPage({ commit }, params) {
    cogny.fetchInvitations(page => commit('setInvitationsPage', page), {
      params: params
    })
  },
  saveInvitationList({ commit }, options) {
    cogny.postInvitationList(invitations => {
      commit('setInvitationList')
      if(options.callback) options.callback(invitations)
    }, options.data)
  },
  deleteInvitation({ commit }, userInvitationNo) {
    cogny.deleteInvitation( response => {
      if(response.status == 1000){
        commit('reloadInvitationList')
      } else {
        console.error('Error on deleting invitation')
      }
    }, userInvitationNo)
  },  
}

const mutations = {
  setInvitationsPage(state, page) {
    state.invitationsPage = page
  },
  setInvitationList() {},
  reloadInvitationList() {
    this.dispatch('loadInvitationsPage')
  }
}

export default {
  state,
  getters,
  actions,
  mutations
}