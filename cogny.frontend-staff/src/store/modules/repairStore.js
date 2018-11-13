import cogny from '../../api/repairApi'
import cf from '../../utils/customFilters'

const state = {
  repairPage: {
    hasNext: false,
    hasPrevious: false,
    page: 1,
    scale: 15,
    total: 0,
    totalPages: 0,
    query: null,
    contents: []
  },
  repairDetail: {
    componentCate1List: [],
    componentCate2List: [],
    componentList: [],
    repairCategoryList: [],
  }
}

const getters = {
  getRepairPage: state => state.repairPage,
  getRepairDetail: state => state.repairDetail
}

const actions = {
  // 정비이력 관리 탭의 actions(Repair.vue)
  loadRepairPage({ commit }, params) {
    cogny.fetchRepairList(repairPage => 
      commit('setRepairPage', repairPage)
      ,{ params: params })
  },
  loadComponentCate1({commit}) {
    cogny.fetchComponentCate1(componentCate1List => 
      commit('setComponentCate1List', componentCate1List)
    )
  },
  loadComponentCate2({ commit }, options) {
    cogny.fetchComponentCate2(componentCate2List => {
      commit('setComponentCate2List', componentCate2List)
      if(options.callback) options.callback()
    }, { params: options.params })
  },
  loadComponentList({ commit }, options) {
    cogny.fetchComponentList(componentList => {
      commit('setComponentList', componentList)
      if(options.callback) options.callback()
    }, { params: options.params })
  },
  loadRepairEnums({ commit }) {
    cogny.fetchRepairEnums(repairEnums =>
      commit('setRepairEnums', repairEnums)
    )
  },
  saveRepair({ commit }, options) {
    cogny.postRepair(repair => {
      commit('setRepair', repair)
      if(options.callback) options.callback(repair)
    }, options.data)
  },
  deleteRepair({ commit }, options) {
    cogny.deleteRepair(response => {
      if(response.status == 1000){
        commit('setRepair')
        if(options.callback) options.callback(response)
      } else {
        console.error('Error on deleting repair')
      }
    }, options.repairNo)
  }
}

const mutations = {
  // 차량 정비이력 관리 탭의 mutations(SensorGraph.vue)
  setRepairPage(state, repairPage) {
    let contents = []
    let prevRepairNo
    repairPage.contents.forEach(function(repair) {
      let index = repairPage.contents.indexOf(repair)
      let newRepair = {}

      newRepair.repairNo = repair.repairNo
      newRepair.vehicleNo = repair.vehicleNo
      newRepair.odometer = repair.odometer
      newRepair.repairDate = repair.repairDate
      newRepair.memo = repair.memo
      newRepair.regDate = repair.regDate
      let componentList = []
      repair.repairComponentList.forEach(function(repairComponent) {
        let component = {
          repairComponentNo: repairComponent.repairComponentNo,
          category: {
            text: repairComponent.categoryName,
            value: repairComponent.category
          },
          component: {
            componentCate1: {
              componentCate1No: repairComponent.component.componentCate1.componentCate1No,
              name: repairComponent.component.componentCate1.name,
            },
            componentCate2: {
              componentCate2No: repairComponent.component.componentCate2.componentCate2No,
              name: repairComponent.component.componentCate2.name
            },
            componentCateNo: repairComponent.component.componentCateNo,
            componentNo: repairComponent.component.componentNo,
            manufacturerNo: repairComponent.component.manufacturerNo,
            name: repairComponent.component.name
          },
          componentCate1: {
            componentCate1No: repairComponent.componentCate1.componentCate1No,
            name: repairComponent.componentCate1.name
          },
          componentCate2: {
            componentCate1No: repairComponent.componentCate2.componentCate1No,
            componentCate2No: repairComponent.componentCate2.componentCate2No,
            name: repairComponent.componentCate2.name
          },
          cost: {
            formatted: !cf.formatNumber(repairComponent.cost) || cf.formatNumber(repairComponent.cost).length == 0 ? "" : "\\ " + cf.formatNumber(repairComponent.cost),
            value: repairComponent.cost
          },
          memo: {
            formatted: cf.shortenText(repairComponent.memo),
            value: repairComponent.memo
          }          
        }
        componentList.push(component)
      })
      newRepair.repairComponentList = componentList
      newRepair.repairComponent = {}
      newRepair._rowVariant = 'bg-gray',
      contents.push(newRepair)
      
      newRepair.repairComponentList.forEach(function(repairComponent) {
        newRepair = {}
        newRepair.repairComponent = repairComponent
        contents.push(newRepair)
      })
    })
    repairPage.contents = contents
    state.repairPage = repairPage
  },
  setComponentCate1List(state, componentCate1List) {
    state.repairDetail.componentCate1List = componentCate1List
  },
  setComponentCate2List(state, componentCate2List) {
    state.repairDetail.componentCate2List = componentCate2List
  },
  setComponentList(state, componentList) {
    state.repairDetail.componentList = componentList
  },
  setRepairEnums(state, repairEnums) {
    state.repairDetail.repairCategoryList = repairEnums.repairCategoryEnums
  }, 
  setRepair() {},
  reloadRepairList() {
    this.dispatch('loadRepairPage')
  }  
}

export default {
  state,
  getters,
  actions,
  mutations
}