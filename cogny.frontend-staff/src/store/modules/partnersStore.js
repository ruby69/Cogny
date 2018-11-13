import cogny from '../../api/partnersApi'

const state = {
  partnerPage: {
    hasNext: false,
    hasPrevious: false,
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
  getPartnersPage : state => state.partnerPage
}

const actions = {
  loadPartnersPage({ commit }, params) {
    cogny.fetchPartners(page => commit('setPartnersPage', page), {
      params: params
    })
  },

  loadPartnerEnums({ commit }, options) {
    cogny.fetchPartnersEnums(enums => {
      commit('setPartnerEnums', enums)
      if (options.callback) options.callback(enums)
    })
  },

  savePartner({ commit }, options){
    cogny.postPartner(partner => {
      commit('setPartner', partner)
      if (options.callback) options.callback(partner)
    }, options.data)
  },

  deletePartner({ commit }, partnerNo) {
    cogny.deletePartner( partner => {
      if(partner.enabled != false){
        console.error('Error on deleting partner')
      } else {
        commit('reloadPartnerList')
      }
    }, partnerNo)
  }
}

const mutations = {
  setPartnersPage(state, page) {
    state.partnerPage = page
  }, 

  setPartnerEnums(state, enums) {
    state.enums = enums
  },

  setPartner(state, partner) {
    let contents = state.partnerPage.contents
    let item = contents.find(e => {
      if (e.partnerNo === partner.partnerNo) {
        return e
      }
    })
    let index = contents.indexOf(item)
    if (index > -1) {
      contents.splice(index, 1, partner)
    } else {
      this.dispatch('loadPartnersPage')
    }
  },

  reloadPartnerList() {
    this.dispatch('loadPartnersPage')
  }
}

export default {
  state,
  getters,
  actions,
  mutations
}