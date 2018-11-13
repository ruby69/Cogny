import Vue from 'vue'
import VueRouter from 'vue-router'

import LoginMain from '../components/signin/Main.vue'

import ProfileMain from '../components/profile/ProfileMain.vue'
import ProfileDetail from '../components/profile/ProfileDetail.vue'

import PartnerMain from '../components/partner/PartnerMain.vue'
import PartnerList from '../components/partner/PartnerList.vue'
import PartnerDetail from '../components/partner/PartnerDetail.vue'

import UserMain from '../components/user/UserMain.vue'
import UserList from '../components/user/UserList.vue'
import UserDetail from '../components/user/UserDetail.vue'

import InvitationMain from '../components/invitation/InvitationMain.vue'
import InvitationList from '../components/invitation/InvitationList.vue'
import InvitationInput from '../components/invitation/InvitationInput.vue'
import InvitationDetail from '../components/invitation/InvitationDetail.vue'

import VehicleMain from '../components/vehicle/VehicleMain.vue'
import VehicleList from '../components/vehicle/VehicleList.vue'
import VehicleDetail from '../components/vehicle/VehicleDetail.vue'
import VehicleMgt from '../components/vehicle/VehicleMgt.vue'
// import SensorGraph from '../components/vehicle/tabs/SensorGraph.vue'
// import RepairList from '../components/vehicle/tabs/RepairList.vue'
// import RepairDetail from '../components/vehicle/tabs/RepairDetail.vue'

import ObdMain from '../components/obd/ObdMain.vue'
import ObdList from '../components/obd/ObdList.vue'
import ObdInput from '../components/obd/ObdInput.vue'
import ObdDetail from '../components/obd/ObdDetail.vue'

import DtcMain from '../components/dtc/DtcMain.vue'
import DtcList from '../components/dtc/DtcList.vue'

import store from '../store'

Vue.use(VueRouter)

const router = new VueRouter({
  mode: 'history',
  routes: [
    {
      path: '/',
      name: 'loginMain',
      component: LoginMain,
      meta: {
        requiresAuth: false
      },
      beforeEnter: (to, from, next) => {
        store.dispatch('fetchProfile', {
          callback: response => {
            if(response && response.userNo && (response.role == 'ADMIN' || response.role == 'PARTNER_MECHANIC')) {
              sessionStorage.email = response.email
              sessionStorage.userNo = response.userNo
              sessionStorage.role = response.role              
              next({ name: 'vehicleList' })
            } else if(response.status == 9003) {
              next()
            } else {
              console.error("error occured on sign page: " + JSON.stringify(response))
              next()
            }
          }
        })
      }
    },
    {
      path: '/profile',
      name: 'profile',
      component: ProfileMain,
      meta: {
        requiresAuth: true,
        requiredPermissions: ['ADMIN', 'PARTNER_MECHANIC']
      },
      children: [
        {
          path: 'detail',
          name: 'profileDetail',
          component: ProfileDetail,
          props: (route) => ({
            pageDetailTitle: '계정 정보 수정',
            pageDetailDescription: '계정 정보를 변경하세요.',
            pageContents: route.params.currentUser,
            redirectRouter: 'vehicleList'
          }),
          meta: {
            requiresAuth: true,
            requiredPermissions: ['ADMIN', 'PARTNER_MECHANIC']
          },
        },
      ]
    },
    {
      path: '/partner',
      component: PartnerMain,
      meta: {
        requiresAuth: true,
        requiredPermissions: ['ADMIN']
      }, 
      children: [
        {
          path: '',
          name: 'partnerList',
          component: PartnerList,
          meta: {
            requiresAuth: true,
            requiredPermissions: ['ADMIN']
          }, 
        },
        {
          path: 'new',
          name: 'partnerNew',
          component: PartnerDetail,
          props: {
            pageDetailTitle: '제휴사 등록',
            pageDetailDescription: '신규 제휴사 정보를 입력하세요.'
          },
          meta: {
            requiresAuth: true,
            requiredPermissions: ['ADMIN']
          },
        },
        {
          path: 'detail',
          name: 'partnerDetail',
          component: PartnerDetail,
          props: (route) => ({
            pageDetailTitle: '제휴사 수정',
            pageDetailDescription: '제휴사 정보를 변경하세요.',
            pageContents: route.params.partner
          }),
          meta: {
            requiresAuth: true,
            requiredPermissions: ['ADMIN']
          },
        },
      ]
    },
    {
      path: '/user',
      component: UserMain,
      meta: {
        requiresAuth: true,
        requiredPermissions: ['ADMIN', 'PARTNER_MECHANIC']
      }, 
      children: [
        {
          path: '',
          name: 'userList',
          component: UserList,
          meta: {
            requiresAuth: true,
            requiredPermissions: ['ADMIN', 'PARTNER_MECHANIC']
          }, 
        },
        {
          path: 'new',
          name: 'userNew',
          component: UserDetail,
          props: {
            pageDetailTitle: '사용자 등록',
            pageDetailDescription: '신규 사용자 정보를 입력하세요.'
          },
          meta: {
            requiresAuth: true,
            requiredPermissions: ['ADMIN', 'PARTNER_MECHANIC']
          },
        },
        {
          path: 'detail',
          name: 'userDetail',
          component: UserDetail,
          props: (route) => ({
            pageDetailTitle: '사용자 수정',
            pageDetailDescription: '사용자 정보를 변경하세요.',
            pageContents: route.params.user
          }),
          meta: {
            requiresAuth: true,
            requiredPermissions: ['ADMIN', 'PARTNER_MECHANIC']
          }, 
        }
      ]
    },
    {
      path: '/invitation',
      component: InvitationMain,
      meta: {
        requiresAuth: true,
        requiredPermissions: ['ADMIN', 'PARTNER_MECHANIC']
      },
      children: [
        {
          path: '',
          name: 'invitationList',
          component: InvitationList,
          meta: {
            requiresAuth: true, 
            requiredPermissions: ['ADMIN', 'PARTNER_MECHANIC']
          }
        },
        {
          path: 'new',
          name: 'invitationNew',
          component: InvitationInput,
          props: {
            pageDetailTitle: '사용자 초대',
            pageDetailDescription: '초대할 사용자 정보를 입력하세요.'
          },
          meta: {
            requiresAuth: true,
            requiredPermissions: ['ADMIN', 'PARTNER_MECHANIC']
          }, 
        },
        {
          path: 'detail',
          name: 'invitationDetail',
          component: InvitationDetail,
          props: (route) => ({
            pageDetailTitle: '사용자 초대 정보 수정',
            pageDetailDescription: '초대할 사용자 정보를 수정하세요.',
            pageContents: route.params.userInvitation
          }),
          meta: {
            requiresAuth: true,
            requiredPermissions: ['ADMIN', 'PARTNER_MECHANIC']
          }, 
        },
      ]
    },
    {
      path: '/vehicle',
      component: VehicleMain,
      meta: {
        requiresAuth: true,
        requiredPermissions: ['ADMIN', 'PARTNER_MECHANIC']
      },      
      children: [
        {
          path: '',
          name: 'vehicleList',
          component: VehicleList,
          meta: {
            requiresAuth: true,
            requiredPermissions: ['ADMIN', 'PARTNER_MECHANIC']
          },      
        },
        {
          path: 'new',
          name: 'vehicleNew',
          component: VehicleDetail,
          props: {
            pageDetailTitle: '차량 등록',
            pageDetailDescription: '신규 차량 정보를 입력하세요.'
          },
          meta: {
            requiresAuth: true,
            requiredPermissions: ['ADMIN', 'PARTNER_MECHANIC']
          }, 
        },
        {
          path: 'detail',
          name: 'vehicleDetail',
          component: VehicleDetail,
          props: (route) => ({
            pageDetailTitle: '차량 정보 수정',
            pageDetailDescription: '차량 정보를 변경하세요.',
            pageContents: route.params.vehicle
          }),
          meta: {
            requiresAuth: true,
            requiredPermissions: ['ADMIN', 'PARTNER_MECHANIC']
          }, 
        },
        {
          path: 'management',
          name: 'vehicleMgt',
          component: VehicleMgt,
          props: (route) => ({
            pageContents: route.params.vehicle
          }),
          meta: {
            requiresAuth: true,
            requiredPermissions: ['ADMIN', 'PARTNER_MECHANIC']
          }, 
                // children: [
                //   {
                //     path: 'sensor',
                //     name: 'vehicleSensorGraph',
                //     components: {
                //       sensorGraph: SensorGraph,
                //       repair: null
                //     },
                //     props: {
                //       sensorGraph: (route) => ({
                //         vehicleNo: route.params.vehicle.vehicleNo,
                //         isMaximized: route.params.isMaximized
                //       }),
                //       repair: null
                //     },
                //   },
                //   {
                //     path: 'repair',
                //     name: 'vehicleRepairList',
                //     components: {
                //       sensorGraph: null,
                //       repair: RepairList
                //     },
                //     props: {
                //       sensorGraph: null,
                //       repair: (route) => ({
                //         vehicle: route.params.vehicle,
                //         isMaximized: route.params.isMaximized
                //       })
                //     }
                //   },
                //   {
                //     path: 'repair/detail',
                //     name: 'vehicleRepairDetail',
                //     components: {
                //       sensorGraph: null,
                //       repair: RepairDetail
                //     }
                //   }
                // ]
        }
      ]
    },
    {
      path: '/obd',
      component: ObdMain,
      meta: {
        requiresAuth: true,
        requiredPermissions: ['ADMIN', 'PARTNER_MECHANIC']
      }, 
      children: [
        {
          path: '',
          name: 'obdList',
          component: ObdList,
          meta: {
            requiresAuth: true,
            requiredPermissions: ['ADMIN', 'PARTNER_MECHANIC']
          }, 
        },
        {
          path: 'new',
          name: 'obdNew',
          component: ObdInput,
          props: {
            pageDetailTitle: 'OBD 단말기 등록',
            pageDetailDescription: '신규 OBD 단말기 정보를 입력하세요.'
          },
          meta: {
            requiresAuth: true,
            requiredPermissions: ['ADMIN', 'PARTNER_MECHANIC']
          }, 
        },
        {
          path: 'detail',
          name: 'obdDetail',
          component: ObdDetail,
          props: (route) => ({
            pageDetailTitle: 'OBD 단말기 수정',
            pageDetailDescription: 'OBD 단말기 정보를 변경하세요.',
            pageContents: route.params.obd
          }),
          meta: {
            requiresAuth: true,
            requiredPermissions: ['ADMIN', 'PARTNER_MECHANIC']
          }, 
        },
      ]
    },
    {
      path: '/dtc',
      component: DtcMain,
      meta: {
        requiresAuth: true,
        requiredPermissions: ['ADMIN']
      },
      children: [
        {
          path: '',
          name: 'dtcList',
          component: DtcList,
          meta: {
            requiresAuth: true,
        requiredPermissions: ['ADMIN']          }
        }
      ]
    }
  ]
})

router.beforeEach((to, from, next) => {
  if(to.meta.requiresAuth != undefined && to.meta.requiresAuth == false) {
    next()
    return
  } 

  if (to.meta.requiredPermissions != undefined && to.meta.requiredPermissions.length != 0 && to.meta.requiredPermissions.indexOf(sessionStorage.role) >= 0) {
    next()
    return
  } else if (to.meta.requiredPermissions == undefined || to.meta.requiredPermissions.length == 0 || to.meta.requiredPermissions.indexOf(sessionStorage.role) < 0) {
    
    store.dispatch('fetchProfile', {
      callback: response => {
        if(response && response.email) {
          sessionStorage.email = response.email
          sessionStorage.userNo = response.userNo
          sessionStorage.role = response.role              
          next({ name: 'vehicleList' })
        } else if(response.status == 9003) {
          alert("접근할 수 없는 페이지 입니다.")
          next({ name: 'loginMain' })
        } else {
          console.error("error occured on sign page: " + JSON.stringify(response))
        }
      }
    })

  } else {
    alert("로그인 후 사용가능합니다.")
    next({ name: 'loginMain' })
  }
})

export default router
