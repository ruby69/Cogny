import Vue from 'vue'
import Vuex from 'vuex'
import SignUp from './modules/signUpStore'
import Auth from './modules/authStore'

Vue.use(Vuex)

export default new Vuex.Store({
  modules: {
    SignUp,
    Auth
  }
})