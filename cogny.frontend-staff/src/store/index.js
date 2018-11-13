import Vue from 'vue'
import Vuex from 'vuex'
import Auth from './modules/authStore'
import Dtc from './modules/dtcStore'
import Partners from './modules/partnersStore'
import Users from './modules/usersStore'
import Invitations from './modules/invitationsStore'
import Vehicles from './modules/vehiclesStore'
import VehicleManagements from './modules/vehicleManagementStore'
import Repairs from './modules/repairStore'
import Obds from './modules/obdsStore'

Vue.use(Vuex)

export default new Vuex.Store({
  modules: {
    Auth,
    Dtc,
    Partners,
    Users,
    Invitations,
    Vehicles,
    VehicleManagements,
    Repairs,
    Obds
  }
})